package com.fc.service.token;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.fc.domain.TokenPoolConfig;
import com.fc.domain.TokenPoolSnapshot;
import com.fc.domain.TokenQueueConfig;
import com.fc.domain.TokenQueueSnapshot;
import com.fc.service.token.task.TokenCallable;
import com.fc.service.token.task.TokenPoolExecutor;
import com.fc.util.Constants;

/**
 * {@link TokenPool} 客户端令牌获取维护实现
 * @author jun.bao
 * @since 2013年7月29日
 */
public class ClientDeployTokenPool implements TokenDispatch, TokenPool {
	/**
	 * 使用tomcat提供的log实现，通过配置 tomcat_home/conf/logging.properties文件，新增2limit.org.
	 * apache.juli.FileHandler.prefix = limit.
	 * 配置项，对于con.*包中所有日志统一输出到tomcat_home/logs/limit.log
	 */
	protected final static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory
			.getLog(ClientDeployTokenPool.class);

	/**
	 * 读写锁确保并发时修改配置属性时，阻塞响应的读操作
	 */
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock readLock = rwl.readLock();
	private final Lock writeLock = rwl.writeLock();

	/**
	 * 排队超过2000的任务直接丢弃，避免资源过度消耗
	 */
	private static Integer maxCapacity = 2000;
	private static Integer defaultkeepAliveTime = 5 * 60;
	/**
	 * 上一次更新的TokenQueueConfig配置条目
	 */
	private List<TokenQueueConfig> lastQueueConfigs;
	/**
	 * 进行令牌分配的任务掉度集合，按优先级分类,同一优先级FIFO排队
	 */
	private Map<Integer, TokenPoolExecutor> executorMap;

	private Integer holdTimeout;

	private String poolName;
	private Integer id;

	private Integer[] priorityArray;

	/**
	 * 空闲token集合，按优先级分类
	 */
	private Map<Integer, FlowControlArrayBlockingQueue<Token>> queueMap;

	private Integer requestTimeout;

	/**
	 * token队列元素进行调度的，元素poll时间，100毫秒，若在dispatch时刻无空余token，快速失败
	 */
	private long borrowTimeout = 100L;

	private Timer cleanTimer;

	private boolean isUsed;
	/**
	 * 存放所有正在使用的token
	 */
	private final Map<String, Token> tokenMap = new ConcurrentHashMap<String, Token>();

	private AtomicInteger tokenTotalCount = new AtomicInteger(0);

	public ClientDeployTokenPool() {
		super();
		cleanTimer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				purge();

			}
		};
		cleanTimer.schedule(task, 1000 * 5, 1000 * 10 * 1);

	}

	public void purge() {
		try {
			if (executorMap != null) {
				for (Integer key : executorMap.keySet()) {
					TokenPoolExecutor executor = executorMap.get(key);
					if (log.isDebugEnabled()) {
						log.debug(MessageFormat.format("priority:{0},ActiveCount:{1},prestartAllCoreThreads:{2}", key,
								executor.getActiveCount(), executor.prestartAllCoreThreads()));
					}
					executor.purge();
				}
			}
			resetHoldTimeOutToken();
		} catch (Exception e) {
			log.error("purge error", e);
		}
	}

	@Override
	public void clear() {

		writeLock.lock();
		try {
			tokenMap.clear();
			queueMap.clear();
			for (Integer key : executorMap.keySet()) {
				TokenPoolExecutor executor = executorMap.get(key);
				executor.shutdown();
			}
			executorMap.clear();
			cleanTimer.cancel();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void dispatch(Integer priority) {
		if (queueMap == null || queueMap.isEmpty()) {
			return;
		}

		FlowControlArrayBlockingQueue<Token> needTokenQueue = queueMap.get(priority);
		for (Integer pri : priorityArray) {
			if (pri.equals(priority)) {
				continue;
			}
			FlowControlArrayBlockingQueue<Token> otherTokenQueue = queueMap.get(pri);
			// queue队列空闲token大于总size的50%视为可borrow
			if (otherTokenQueue.isCanborrow()) {
				Token token = null;
				try {
					token = otherTokenQueue.pollNotDispatch(borrowTimeout, TimeUnit.MILLISECONDS);
					// System.out.println(MessageFormat.format("dispatch priority {0} to {1} succ", pri, priority));

				} catch (InterruptedException e) {
					// ignore this borrow token exception
					token = null;
					// System.err.println(MessageFormat.format(
					// "dispatch priority {0} to {1} failure cause get token timeout", pri, priority));
					if (log.isDebugEnabled()) {
						log.debug(MessageFormat.format("dispatch priority {0} to {1} failure cause get token timeout",
								pri, priority));
					}
				}
				if (token != null) {
					if (log.isDebugEnabled()) {
						log.debug(MessageFormat.format("dispatch priority {0} to {1} success", pri, priority));
					}
					needTokenQueue.offer(token);
					return;
				}
			} else {
				// System.err.println(MessageFormat.format("dispatch priority {0} to {1} failure cause no free token",
				// pri, priority));
				if (log.isDebugEnabled()) {
					log.debug(MessageFormat.format("dispatch priority {0} to {1} failure cause no free token", pri,
							priority));
				}
			}
		}
	}

	public TokenPoolExecutor getExecutor(Integer priority) {
		return executorMap.get(priority);
	}

	public Map<Integer, TokenPoolExecutor> getExecutorMap() {
		return executorMap;
	}

	public Integer getHoldTimeout() {
		return holdTimeout;
	}

	public String getPoolName() {
		return poolName;
	}

	public Integer[] getPriorityArray() {
		return priorityArray;
	}

	public BlockingQueue<Token> getQueue(Integer priority) {
		return queueMap.get(priority);
	}

	public Map<Integer, FlowControlArrayBlockingQueue<Token>> getQueueMap() {
		return queueMap;
	}

	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	@Override
	public String getToken(Integer priority) {
		readLock.lock();
		try {

			if (!isUsed) {
				return Constants.TOKEN_RESULT_CODE;
			}

			BlockingQueue<Token> queue = getQueue(priority);
			// 防止由于重载配置导致某一优先级删除，返回最低优先级队列
			if (queue == null) {
				queue = getQueue(priorityArray[0]);
			}

			Token token = queue.poll(requestTimeout, TimeUnit.MILLISECONDS);
			if (token != null) {
				if (log.isDebugEnabled()) {
					log.debug("get token success use priority " + priority);
				}
				token.setLastAssessTime(Calendar.getInstance().getTime());
				tokenMap.put(token.getName(), token);
				return token.getName();
			} else {
				if (log.isDebugEnabled()) {
					log.debug("get token failure null use priority " + priority);
				}
				return null;
			}

		} catch (InterruptedException e) {
			if (log.isInfoEnabled()) {
				log.info("get token failure InterruptedException use priority " + priority);
			}
			return null;
		} finally {
			readLock.unlock();
		}

	}

	/**
	 * 释放超时持有的
	 */
	public void resetHoldTimeOutToken() {
		for (String uuid : tokenMap.keySet()) {
			Token token = tokenMap.get(uuid);
			if (token != null && token.isHoldTimeOut(holdTimeout)) {
				token.reset();
				if (log.isDebugEnabled()) {
					log.debug("release hold timeout token " + uuid);
				}
				Integer priority = token.getPriority();
				tokenMap.remove(uuid);
				// 重置uuid，避免超时线程2次释放
				token.setName(UUID.randomUUID().toString());
				BlockingQueue<Token> queue = getQueue(priority);
				if (queue != null) {
					queue.offer(token);
				}
			}
		}
	}

	@Override
	public boolean releaseToken(String uuid) {
		if (uuid != null) {
			Token token = tokenMap.remove(uuid);
			if (token != null) {
				token.reset();
				Integer priority = token.getPriority();
				BlockingQueue<Token> queue = getQueue(priority);
				if (queue != null) {
					if (log.isDebugEnabled()) {
						log.debug(MessageFormat.format("return token to priority: {0}, uuid: {1},Thread:{2}", priority,
								uuid, Thread.currentThread().getName()));
					}
					return queue.offer(token);
				}
			}
			if (Constants.TOKEN_RESULT_CODE.equals(uuid)) {
				return true;
			}
		}
		return false;
	}

	public void setExecutorMap(Map<Integer, TokenPoolExecutor> executorMap) {
		this.executorMap = executorMap;
	}

	public void setHoldTimeout(Integer holdTimeout) {
		this.holdTimeout = holdTimeout;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public void setPriorityArray(Integer[] priorityArray) {
		this.priorityArray = priorityArray;
	}

	public void setQueueMap(Map<Integer, FlowControlArrayBlockingQueue<Token>> queueMap) {
		this.queueMap = queueMap;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	/**
	 * 提交请求token任务
	 * @param priority
	 * @return 一个支持异步结果的Future对象
	 */
	@Override
	public Future<String> submit(Integer priority) {
		TokenCallable callable = new TokenCallable();
		// 对于优先级为null或者Integer.MAX_VALUE情况，按该队列的最低优先级处理
		if (priority == null || Integer.MAX_VALUE == priority.intValue()) {
			priority = priorityArray[0];
		}
		callable.setPriority(priority);
		callable.setTokenPool(this);
		return executorMap.get(priority).submit(callable);
	}

	/**
	 * 获取tokenpool的运行快照,快照元素：各个优先级队列token使用占比，排队数量，各优先级token平均请求时间，持有时间
	 * TokenPoolExecutor相关指标： <br/>
	 * {@link ThreadPoolExecutor#getActiveCount()} 返回主动执行任务的近似线程数。<br/>
	 * {@link ThreadPoolExecutor#getCompletedTaskCount()} 返回已完成执行的近似任务总数。<br/>
	 * {@link ThreadPoolExecutor#getTaskCount()} 返回曾计划执行的近似任务总数。<br/>
	 * {@link ThreadPoolExecutor#getPoolSize()} 返回池中的当前线程数。<br/>
	 * {@link ThreadPoolExecutor#getQueue()} 返回此执行程序使用的任务队列。<br/>
	 * @return name:/app-apipay/services/BatchPayWS,summary:free token summary
	 *         [priority: 1 count: 20;],executor summary
	 *         [priority:0,activecount:
	 *         0,completedtaskcount:0,taskcount:0,poolsizeÃ¯Â¼Â0,queuesize:{5};
	 *         ] ,in use token [0]
	 */
	@Override
	public String getSnapshot() {

		StringBuffer freeTokenSummarySB = new StringBuffer();
		for (Integer priority : queueMap.keySet()) {
			freeTokenSummarySB.append("priority: " + priority + " count: " + queueMap.get(priority).size() + ";");
		}

		StringBuffer executorSummarySB = new StringBuffer();
		for (Integer priority : executorMap.keySet()) {
			TokenPoolExecutor executor = executorMap.get(priority);
			String info = MessageFormat.format(
					"priority:{0},activecount:{1},completedtaskcount:{2},taskcount:{3},poolsize:{4},queuesize:{5}; ",
					priority, executor.getActiveCount(), executor.getCompletedTaskCount(), executor.getTaskCount(),
					executor.getPoolSize(), executor.getQueue().size());
			executorSummarySB.append(info);
		}
		Integer inUseToken = tokenMap.size();
		return MessageFormat.format("free token summary [{0}],executor summary [{1}] ,in use token [{2}]",
				freeTokenSummarySB.toString(), executorSummarySB.toString(), inUseToken);
	}

	@Override
	public TokenPoolSnapshot getTokenPoolSnapshot() {
		List<TokenQueueSnapshot> tokenQueueSnapshots = getTokenQueueSnapshot();
		TokenPoolSnapshot poolSnapshot = new TokenPoolSnapshot(id, tokenQueueSnapshots);
		poolSnapshot.setUseToken(this.tokenMap.size());
		poolSnapshot.setSumToken(tokenTotalCount.get());
		return poolSnapshot;
	}

	private List<TokenQueueSnapshot> getTokenQueueSnapshot() {
		if (queueMap == null) {
			return null;
		}
		List<TokenQueueSnapshot> queueSnapshots = new ArrayList<TokenQueueSnapshot>();
		for (Integer priority : queueMap.keySet()) {
			FlowControlArrayBlockingQueue<Token> queue = queueMap.get(priority);
			TokenPoolExecutor executor = executorMap.get(priority);
			TokenQueueSnapshot snapshot = new TokenQueueSnapshot(queue, executor, id, priority);
			queueSnapshots.add(snapshot);
		}
		return queueSnapshots;
	}

	/**
	 * @param queueConfigs
	 */
	public ClientDeployTokenPool(TokenPoolConfig tokenPoolConfig) {
		this();
		List<TokenQueueConfig> queueConfigs = tokenPoolConfig.getTokenQueueConfigs();
		Integer reqTo = tokenPoolConfig.getRequestTimeout();
		Integer holdTo = tokenPoolConfig.getHoldTimeout();
		id = tokenPoolConfig.getId();
		poolName = tokenPoolConfig.getRequestUrl();
		tokenTotalCount.set(tokenPoolConfig.getTotalTokenCount());
		// 对于没有优先级队列统一按一个队列处理
		if (queueConfigs == null || queueConfigs.size() == 0) {
			queueConfigs = new ArrayList<TokenQueueConfig>();
			TokenQueueConfig config = new TokenQueueConfig();
			config.setPriority(Integer.MAX_VALUE);
			config.setThreshold(Constants.DEFAULT_TOKEN_BORROW_THRESHOLD);
			config.setMaxPoolSize(Constants.DEFAULT_MAX_TOKEN_TASK_POOL_SIZE_NO_PRIORITY);
			config.setTokenCount(tokenPoolConfig.getTotalTokenCount());
			queueConfigs.add(config);
		}
		writeLock.lock();
		try {
			queueMap = new ConcurrentHashMap<Integer, FlowControlArrayBlockingQueue<Token>>();
			executorMap = new ConcurrentHashMap<Integer, TokenPoolExecutor>();
			priorityArray = new Integer[queueConfigs.size()];
			requestTimeout = reqTo;
			holdTimeout = holdTo;
			isUsed = true;
			lastQueueConfigs = queueConfigs;
			for (int i = 0; i < queueConfigs.size(); i++) {
				TokenQueueConfig config = queueConfigs.get(i);
				Integer tokenCount = config.getTokenCount();
				Integer priority = config.getPriority();
				Float threshold = config.getThreshold();
				FlowControlArrayBlockingQueue<Token> queue = new FlowControlArrayBlockingQueue<Token>(tokenCount,
						false, threshold);
				for (int j = 0; j < tokenCount; j++) {
					Token token = new Token(UUID.randomUUID().toString(), priority);
					queue.add(token);
				}
				queue.setDispatch(this);
				queue.setPriority(priority);
				queueMap.put(priority, queue);
				TokenPoolExecutor executor = new TokenPoolExecutor(defaultkeepAliveTime, TimeUnit.SECONDS,
						new ArrayBlockingQueue<Runnable>(maxCapacity), config.getMaxPoolSize());
				executorMap.put(priority, executor);
				priorityArray[i] = priority;
			}

			// 对优先级按从低到高排序，token调度从低优先级开始(priority越小优先级越高)
			Arrays.sort(priorityArray, new Comparator<Integer>() {
				@Override
				public int compare(Integer a, Integer b) {
					return -(a.compareTo(b));
				}
			});
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 重新热加载配置
	 * @param newQueueConfigs
	 * @param reqTo
	 * @param holdTo
	 */
	@Override
	public void reload(TokenPoolConfig tokenPoolConfig) {
		List<TokenQueueConfig> newQueueConfigs = tokenPoolConfig.getTokenQueueConfigs();
		Integer reqTo = tokenPoolConfig.getRequestTimeout();
		Integer holdTo = tokenPoolConfig.getHoldTimeout();

		// 对于没有优先级队列统一按一个队列处理
		if (newQueueConfigs == null || newQueueConfigs.size() == 0) {
			newQueueConfigs = new ArrayList<TokenQueueConfig>();
			TokenQueueConfig config = new TokenQueueConfig();
			config.setPriority(Integer.MAX_VALUE);
			config.setThreshold(Constants.DEFAULT_TOKEN_BORROW_THRESHOLD);
			config.setMaxPoolSize(Constants.DEFAULT_MAX_TOKEN_TASK_POOL_SIZE_NO_PRIORITY);
			config.setTokenCount(tokenPoolConfig.getTotalTokenCount());
			newQueueConfigs.add(config);
		}

		writeLock.lock();
		try {
			priorityArray = new Integer[newQueueConfigs.size()];
			requestTimeout = reqTo;
			holdTimeout = holdTo;
			isUsed = true;
			tokenTotalCount.set(tokenPoolConfig.getTotalTokenCount());
			for (int i = 0; i < newQueueConfigs.size(); i++) {
				TokenQueueConfig config = newQueueConfigs.get(i);
				Integer tokenCount = config.getTokenCount();
				Integer priority = config.getPriority();
				priorityArray[i] = priority;

				TokenQueueConfig oldTokenQueueConfig = null;
				for (TokenQueueConfig tokenQueueConfig : lastQueueConfigs) {
					if (tokenQueueConfig.getPriority().equals(priority)) {
						oldTokenQueueConfig = tokenQueueConfig;
					}
				}

				if (oldTokenQueueConfig == null) {// 新优先级配置加入
					FlowControlArrayBlockingQueue<Token> queue = new FlowControlArrayBlockingQueue<Token>(tokenCount,
							false, config.getThreshold());
					for (int j = 0; j < tokenCount; j++) {
						Token token = new Token(UUID.randomUUID().toString(), priority);
						queue.add(token);
					}
					queue.setDispatch(this);
					queue.setPriority(priority);
					queueMap.put(priority, queue);

					TokenPoolExecutor executor = new TokenPoolExecutor(defaultkeepAliveTime, TimeUnit.SECONDS,
							new ArrayBlockingQueue<Runnable>(maxCapacity), config.getMaxPoolSize());
					executorMap.put(priority, executor);
					continue;
				}

				if (!config.isChange(oldTokenQueueConfig)) {// 优先级配置对应token数量 外借阈值，最大排队数无改动
					continue;
				}

				// 即有优先级配置更新
				FlowControlArrayBlockingQueue<Token> queue = queueMap.get(priority);
				int oldCapacity = queue.getCapacity();
				if (oldCapacity != tokenCount) {

					// token令牌被拿出后，归还前 代表使用中，因此此时queue的空余size即为在使用中的token数
					int inUseTokenCount = queue.remainingCapacity();
					// 新容量大于旧容量
					if (tokenCount > oldCapacity) {
						queue = new FlowControlArrayBlockingQueue<Token>(tokenCount, false);
						for (int j = 0; j < tokenCount - inUseTokenCount; j++) {
							Token token = new Token(UUID.randomUUID().toString(), priority);
							queue.add(token);
						}
					}
					// 新容量小于旧容量
					else if (tokenCount < oldCapacity) {
						queue = new FlowControlArrayBlockingQueue<Token>(tokenCount, false);
						if (inUseTokenCount < tokenCount) {
							for (int j = 0; j < tokenCount - inUseTokenCount; j++) {
								Token token = new Token(UUID.randomUUID().toString(), priority);
								queue.add(token);
							}
						}
					}
					queue.setThreshold(config.getThreshold());
					queue.setDispatch(this);
					queue.setPriority(priority);
					queueMap.put(priority, queue);
				}
				TokenPoolExecutor executor = executorMap.get(priority);
				executor.setKeepAliveTime(requestTimeout, TimeUnit.MILLISECONDS);
				executor.setMaximumPoolSize(config.getMaxPoolSize());

			}

			// 对优先级按从低到高排序，token调度从低优先级开始(priority越小优先级越高)
			Arrays.sort(priorityArray, new Comparator<Integer>() {
				@Override
				public int compare(Integer a, Integer b) {
					return -(a.compareTo(b));
				}
			});

			// 清理不存在的Priority对应queue
			for (TokenQueueConfig tokenQueueConfig : lastQueueConfigs) {
				Integer lastPriority = tokenQueueConfig.getPriority();
				boolean isExist = false;
				for (TokenQueueConfig newTokenQueueConfig : newQueueConfigs) {
					if (newTokenQueueConfig.getPriority().equals(lastPriority)) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					queueMap.remove(lastPriority);
				}
			}

			// reload完成后将新的配置信息缓存
			lastQueueConfigs = newQueueConfigs;
		} catch (Exception e) {

		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public String toString() {
		return "TokenPool [executorMap=" + executorMap + ", holdTimeout=" + holdTimeout + ", poolName=" + poolName
				+ ", priorityArry=" + Arrays.toString(priorityArray) + ", queueMap=" + queueMap + ", requestTimeout="
				+ requestTimeout + ", tokenMap=" + tokenMap + "]";
	}

	@Override
	public Integer getDeployType() {
		return Constants.DEPLOY_TYPE_CLIENT;
	}

	@Override
	public void setUsed(boolean used) {
		isUsed = used;
	}

}
