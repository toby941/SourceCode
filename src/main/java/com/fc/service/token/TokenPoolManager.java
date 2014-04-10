package com.fc.service.token;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.catalina.connector.Request;

import com.fc.container.FlowControlLifecycleListener;
import com.fc.domain.AlarmInfo;
import com.fc.domain.ContextConfig;
import com.fc.domain.ContextSnapshot;
import com.fc.domain.TokenPoolConfig;
import com.fc.domain.TokenPoolSnapshot;
import com.fc.domain.TokenQueueConfig;
import com.fc.service.token.priority.PriorityManager;
import com.fc.service.token.task.HttpCallable;
import com.fc.service.token.task.HttpRequestPoolExecutor;
import com.fc.service.token.task.ReloadConfigTimerTask;
import com.fc.service.token.task.SnapshotTimerTask;
import com.fc.util.Config;
import com.fc.util.Constants;
import com.fc.util.IOUtils;
import com.fc.util.JAXBUtil;
import com.fc.util.JDKHttpClient;

/**
 * 限流控制主入口
 * @author jun.bao
 * @since 2013年8月8日
 */
public class TokenPoolManager {

	/**
	 * 使用tomcat提供的log实现，通过配置 tomcat_home/conf/logging.properties文件，新增2limit.org.
	 * apache.juli.FileHandler.prefix = limit.
	 * 配置项，对于con.bill99.*包中所有日志统一输出到tomcat_home/logs/limit.log
	 */
	protected final static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory
			.getLog(TokenPoolManager.class);
	/**
	 * 通过本地线程实现token uuid的传递
	 */
	public static final ThreadLocal<String> tokenUUIDThreadLocal = new ThreadLocal<String>();

	/**
	 * tomcat_home/conf/server.xml中配置的 <Context/>节点的path属性，由 {@link FlowControlLifecycleListener}
	 * 负责读取并赋值，一个tomcat客户端只允许存在全局唯一的contextName
	 */
	private String contextName;
	/**
	 * 按 {@link TokenPoolConfig#getRequestUrl()} 为key，缓存令牌池
	 */
	private Map<String, TokenPool> tokenPoolMap;
	/**
	 * 与contextName对应的ContextConfig配置，包含 {@link TokenPoolConfig}与 {@link TokenQueueConfig}配置项
	 */
	private ContextConfig contextConfig;

	/**
	 * 静态化实例，确保客户端中 {@link TokenPoolManager}唯一
	 */
	private static TokenPoolManager manager;
	protected PriorityManager priorityManager;
	/**
	 * 警告信息上传通道(异步上传)，异步线程池实现，避免阻塞主流程
	 */
	private HttpRequestPoolExecutor alarmPoolExecutor;

	/**
	 * 快照信息上传通道(异步上传)，异步线程池实现，避免阻塞主流程
	 */
	private HttpRequestPoolExecutor snapshotPoolExecutor;

	/**
	 * 指定与服务端通讯的host，如http://limit.99bill.com
	 */
	private String tokenPoolServerHost;

	/**
	 * 上传快照定时器
	 */
	private Timer snapshotTimer;

	/**
	 * 请求配置更新定时器
	 */
	private Timer reloadTimer;

	/**
	 * 获取 {@link TokenPoolManager}唯一入口
	 * @return 单例的 {@link TokenPoolManager}
	 */
	public static TokenPoolManager getTokenPoolManager() {
		if (manager == null) {
			manager = new TokenPoolManager();
		}
		return manager;
	}

	/**
	 * 设置默认的请求token {@link Future} 任务超时时间，此处设定为5分钟，该超时时间不等同与 {@link TokenPoolConfig#getRequestTimeout()}时间 该配置项只是这个
	 * {@link Future} 任务的最大等待时间，实际令牌获取超时时间达到后即可返回
	 */
	private static Integer defaultRequestTimeout = 60 * 5;

	/**
	 * 缓存每次 {@link #reload()}的 {@link ContextConfig} hashcode,类似于 <a
	 * href="http://baike.baidu.com/view/3039264.htm">
	 * E-TAG</a>功能，当服务端配置没更新时，减少不必要的请求流量
	 */
	public Integer configXMLHashCode;

	/**
	 * 标记该 {@link TokenPoolManager}是否已经初始化过
	 */
	protected boolean initialized;

	/**
	 * 限流控制总开关
	 */
	private boolean isUsed = false;

	/**
	 * 获取token池
	 * @param name
	 *            {@link TokenPoolConfig#getTokenRequestUrl()}
	 * @return name对应的 {@link TokenPool};
	 */
	private TokenPool getPool(String name) {
		if (tokenPoolMap == null) {
			return null;
		}
		return tokenPoolMap.get(name);
	}

	/**
	 * 私有化构造方法,实现单例
	 */
	private TokenPoolManager() {
		super();
	}

	/**
	 * 通过http请求加载服务端最新令牌池配置
	 * @param contextName
	 *            以该参数为key，向服务端请求对应的令牌池配置
	 */
	private void loadConfig(String contextName) {

		try {
			if (log.isDebugEnabled()) {
				log.debug("begin load config contextName:" + contextName);
			}
			this.contextName = contextName;
			String getConfigUrl = Config.getConfigUrl(contextName, configXMLHashCode, tokenPoolServerHost);
			// System.out.println("url:" + getConfigUrl);
			String configXML = JDKHttpClient.doGet(getConfigUrl);
			// System.out.println("configXML:" + configXML);
			if (configXML != null && configXML.length() > 0) {
				this.configXMLHashCode = configXML.trim().hashCode();
				this.contextConfig = (ContextConfig) JAXBUtil.xml2Bean(ContextConfig.class, configXML);
			}
		} catch (Exception e) {
			this.configXMLHashCode = null;
			this.contextConfig = null;
			log.error("loadConfig with context :" + contextName, e);
		}

	}

	/**
	 * 销毁方法，由 {@link FlowControlLifecycleListener#lifecycleEvent(org.apache.catalina.LifecycleEvent)} 调用
	 */
	public void stop() {
		try {
			if (initialized) {
				snapshotTimer.cancel();
				reloadTimer.cancel();
				snapshotPoolExecutor.shutdownNow();
				alarmPoolExecutor.shutdownNow();
				priorityManager.stop();
				for (String key : tokenPoolMap.keySet()) {
					TokenPool p = tokenPoolMap.get(key);
					p.clear();
				}
				tokenPoolMap.clear();
				contextConfig = null;
				initialized = false;
			}
		} catch (Exception e) {
			log.error("stop error " + toString(), e);
		}
	}

	/**
	 * 读写锁确保并发时修改配置属性时，阻塞响应的读操作
	 */
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock readLock = rwl.readLock();
	private final Lock writeLock = rwl.writeLock();

	/**
	 * 通过 {@link #reloadTimer}定时重载令牌池配置
	 * @return true-当重载成功 false-当 {@link #loadConfig(String)}获取失败或服务端配置没更新
	 */
	public synchronized void reload() {
		// System.err.println("enter reload " + Thread.currentThread().getName());

		try {
			log.debug("begin reload with hashcode:" + configXMLHashCode);

			// System.out.println("begin loadConfig poolMap size:" + tokenPoolMap.size());
			loadConfig(contextName);
			// System.out.println(contextConfig);
			if (contextConfig == null) {
				log.debug("reload get contextConfig null,so do not reload");
				return;
			}
			if (!contextConfig.isUsed()) {
				log.debug("fc pause,pause all token pool");
				isUsed = false;
				return;
			}
			log.debug("server config change,begin reload");
			isUsed = true;
			List<TokenPoolConfig> tokenPoolConfigs = contextConfig.getTokenPoolConfigs();
			priorityManager = PriorityManager.getPriorityManager();
			if (tokenPoolMap == null) {
				tokenPoolMap = new ConcurrentHashMap<String, TokenPool>();
			}

			if (tokenPoolConfigs != null && tokenPoolConfigs.size() > 0) {
				// System.out.println("begin reload poolMap size:" + tokenPoolMap.size());
				Set<String> poolNames = new HashSet<String>();
				for (TokenPoolConfig tokenPoolConfig : tokenPoolConfigs) {
					String poolName = tokenPoolConfig.getRequestUrl();
					poolNames.add(poolName);
					TokenPool pool = tokenPoolMap.get(poolName);
					if (pool != null) {
						if (pool.getDeployType().equals(tokenPoolConfig.getDeployType())) {
							pool.reload(tokenPoolConfig);
						} else {
							// 热替换不支持不同类型的部署方式之间替换
							// pool.clear();
							// pool = null;
						}
					}
					if (pool == null) {
						if (Constants.DEPLOY_TYPE_CLIENT.equals(tokenPoolConfig.getDeployType())) {
							pool = new ClientDeployTokenPool(tokenPoolConfig);
						} else {
							pool = new ServerDeployTokenPool(contextName, tokenPoolConfig);
						}
					}
					tokenPoolMap.put(poolName, pool);
					// System.out.println("poolMap size: " + tokenPoolMap.size());
					// System.out.println("after remone poolNames poolMap size: " + tokenPoolMap.size());
				}
				// 将不在此次更新配置文件中的令牌池状态设置为unuse
				if (poolNames.size() > 0) {
					for (String key : tokenPoolMap.keySet()) {
						if (!poolNames.contains(key)) {
							TokenPool tokenPool = tokenPoolMap.get(key);
							if (tokenPool != null) {
								tokenPool.setUsed(false);
							}
						}
					}
				}
				priorityManager.load(tokenPoolConfigs);
			}
			// System.out.println("end reload poolMap size: " + tokenPoolMap.size());
			if (alarmPoolExecutor == null) {
				alarmPoolExecutor = new HttpRequestPoolExecutor();
			}

			if (snapshotTimer != null) {
				snapshotTimer.purge();
				snapshotTimer.cancel();
			}

			Integer snapshotSendInterval = contextConfig.getSnapshotSendInterval();
			if (snapshotSendInterval != null && snapshotSendInterval > 0) {
				if (snapshotPoolExecutor != null) {
					snapshotPoolExecutor.purge();
				} else {
					snapshotPoolExecutor = new HttpRequestPoolExecutor();
				}

				snapshotTimer = new Timer();
				SnapshotTimerTask task = new SnapshotTimerTask();
				snapshotTimer.schedule(task, 1000 * 5, snapshotSendInterval);
			}
			log.debug("end reload with hashcode:" + configXMLHashCode);
			this.contextConfig = null;
			return;
		} finally {
			// System.err.println("level reload " + Thread.currentThread().getName());
		}
	}

	/**
	 * 初始化方法
	 * @param contextName
	 * @return true-初始化成功 false-已经初始化过或初始化失败
	 */
	public boolean init(String contextName) {
		log.debug("begin init with contextName " + contextName);
		if (contextName == null || contextName.length() == 0) {
			log.warn("could not init TokenPoolManager with null contextName,plaese check you Tomcat conf/server.xml");
			return false;
		}

		reloadTimer = new Timer();
		ReloadConfigTimerTask reloadConfigTimerTask = new ReloadConfigTimerTask();
		reloadTimer.schedule(reloadConfigTimerTask, Config.getReloadDelay(), Config.getPeriod());

		loadConfig(contextName);

		if (contextConfig == null) {
			log.error(MessageFormat.format("could not loadConfig:{0} ", contextName));
			return false;
		}
		if (!contextConfig.isUsed()) {
			log.debug("fc pause,so do not init");
			isUsed = false;
			return false;
		}

		log.debug("init  tokenPool with contextName " + contextName);
		List<TokenPoolConfig> tokenPoolConfigs = contextConfig.getTokenPoolConfigs();
		tokenPoolMap = new ConcurrentHashMap<String, TokenPool>();
		priorityManager = PriorityManager.getPriorityManager();
		if (tokenPoolConfigs != null && tokenPoolConfigs.size() > 0) {
			for (TokenPoolConfig tokenPoolConfig : tokenPoolConfigs) {
				if (tokenPoolConfig.isClientDeploy()) {
					ClientDeployTokenPool pool = new ClientDeployTokenPool(tokenPoolConfig);
					tokenPoolMap.put(tokenPoolConfig.getRequestUrl(), pool);
				} else {
					log.debug("server deploy type won't finish until client deploy test ok,so any server deploy will ignore");
				}
			}
			priorityManager.load(tokenPoolConfigs);
		}
		alarmPoolExecutor = new HttpRequestPoolExecutor();
		Integer snapshotSendInterval = contextConfig.getSnapshotSendInterval();
		if (snapshotSendInterval != null && snapshotSendInterval > 0) {
			snapshotPoolExecutor = new HttpRequestPoolExecutor();
			snapshotTimer = new Timer();
			SnapshotTimerTask task = new SnapshotTimerTask();
			snapshotTimer.schedule(task, 1000 * 5, contextConfig.getSnapshotSendInterval());
		}
		initialized = true;
		this.contextConfig = null;
		isUsed = true;
		log.info(MessageFormat.format("finish init with contextName {0} ", contextName));
		return true;
	}

	/**
	 * 释放token请求入口
	 * @param name
	 * @return true-释放成功 false-对应 {@link Token}不存在
	 */
	public boolean releaseToken(String name) {
		try {

			if (!isUsed) {
				return true;
			}

			TokenPool pool = getPool(name);
			// 对于没有限流策略的请求，因为没有token，此处直接返回true
			if (pool != null) {
				String uuid = tokenUUIDThreadLocal.get();
				if (log.isDebugEnabled()) {
					log.debug(MessageFormat.format("release token poolname: {0}, token uuid:{1}, Thread:{2}", name,
							uuid, Thread.currentThread().getName()));
				}
				return pool.releaseToken(uuid);
			}
			return true;
		} catch (Exception e) {
			log.error("release token name:" + name, e);
			return true;
		}

	}

	/**
	 * 对于http get请求入口
	 * @param requestURI
	 * @param queryString
	 * @return
	 */
	public boolean getToken(String requestURI, String queryString) {
		InputStream in = null;
		try {
			if (queryString != null && queryString.length() > 0) {
				in = IOUtils.toInputStream(queryString);
			}
			boolean result = getToken(requestURI, in);
			return result;
		} catch (Exception e) {
			log.error("return true when unknow error happen", e);
			return true;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 根据 {@link TokenPoolConfig#getRequestUrl()}确定对应 {@link ClientDeployTokenPool} 与对应的优先级参数解析方式 ws请求：ContextPath:
	 * null,
	 * ContentType: text/xml; charset=utf-8, PathInfo: null,Protocol: HTTP/1.0,
	 * QueryString:null, RequestURI: /demo/apipay/services/BatchPayWS,
	 * ServletPath: null http请求：ContextPath: null, ContentType: null, PathInfo:
	 * null,Protocol: HTTP/1.1, QueryString:null, RequestURI: /demo/second.do,
	 * ServletPath: null
	 * @param requestURI
	 *            对应的 tokenPoolName
	 * @param in
	 *            {@link Request} 携带的请求流内容
	 * @return true-获取令牌成功可执行业务请求 false-令牌获取失败，业务请求执行中止 if priority==null-
	 *         true请求不限流
	 */
	public boolean getToken(String requestURI, InputStream in) {
		try {
			if (!isUsed) {
				log.debug("flow control is pause,always return true");
				return true;
			}
			TokenPool pool = getPool(requestURI);
			// requestURI无限流情况下，请求放行
			if (pool == null) {
				log.debug("no pool with " + requestURI + " always return true");
				return true;
			}

			Integer priority = null;
			if (priorityManager == null) {
				/** requestURI无优先级配置策略，请求按默认优先级处理 无请求参数 */
				log.debug("no priorityManager exist,client token request always use priority Integer.MAX_VALUE");
				priority = Integer.MAX_VALUE;
			} else {
				priority = priorityManager.getPriority(requestURI, in);
			}
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("begin getToken url: {0}, priority: {1},Thread: {2}", requestURI,
						priority, Thread.currentThread().getName()));
			}

			try {
				Future<String> future = pool.submit(priority);
				String result = future.get(defaultRequestTimeout, TimeUnit.SECONDS);

				if (result != null && result.length() > 0) {
					tokenUUIDThreadLocal.set(result);
					if (log.isDebugEnabled()) {
						log.debug(MessageFormat.format("get token success: {0}, priority: {1},Thread: {2}", result,
								priority, Thread.currentThread().getName()));
					}
					return true;
				}
				// 没有获取到token，资源紧张需通知服务器
				sendGetTokenTimeOutAlert(requestURI, priority, null);
				return false;
			} catch (RejectedExecutionException e) {
				sendGetTokenTimeOutAlert(requestURI, priority, e);
				log.warn("catch RejectedExecutionException", e);
				return false;
			} catch (TimeoutException e) {
				sendGetTokenTimeOutAlert(requestURI, priority, e);
				log.warn("get token time out", e);
				return false;
			}
		} catch (Exception e) {
			log.error("return true when unknow error happen", e);
			return true;
		}
	}

	/**
	 * 异步上传报警信息,报警分为
	 * TimeoutException 获取token超时
	 * RejectedExecutionException
	 * InterruptedException
	 * 没有获取到token，资源紧张需通知服务器
	 * @param requestURI
	 * @param priority
	 * @param exception
	 * @throws UnsupportedEncodingException
	 */
	public void sendGetTokenTimeOutAlert(String requestURI, int priority, Exception exception) {
		try {
			AlarmInfo alarmInfo = new AlarmInfo(requestURI, priority, exception.toString(), this.contextName);
			String info = JAXBUtil.bean2Xml(alarmInfo);
			info = URLEncoder.encode(info, "utf-8");
			HttpCallable callable = new HttpCallable(Config.getAlarmUrl(tokenPoolServerHost), info, true);
			alarmPoolExecutor.submit(callable);
		} catch (Exception e) {
			log.error("sendGetTokenTimeOutAlert error", e);
		}
	}

	public ContextSnapshot getSnapshot() {
		List<TokenPoolSnapshot> tokenPoolSnapshots = new ArrayList<TokenPoolSnapshot>();
		for (String key : tokenPoolMap.keySet()) {
			TokenPool pool = tokenPoolMap.get(key);
			TokenPoolSnapshot poolSnapshot = pool.getTokenPoolSnapshot();
			tokenPoolSnapshots.add(poolSnapshot);
		}
		ContextSnapshot snapshot = new ContextSnapshot(contextName);
		snapshot.setTokenPoolSnapshots(tokenPoolSnapshots);
		return snapshot;
	}

	/**
	 * 发送token池快照信息
	 */
	public void sendSnapshot() {
		try {
			ContextSnapshot snapshot = getSnapshot();
			String postStr = JAXBUtil.bean2Xml(snapshot);
			if (log.isDebugEnabled()) {
				log.debug("begin sendSnapshot: " + postStr);
			}
			postStr = URLEncoder.encode(postStr, "utf-8");
			HttpCallable callable = new HttpCallable(Config.getSnapshotUrl(tokenPoolServerHost), postStr, true);
			Future<String> future = snapshotPoolExecutor.submit(callable);
			// System.out.println(future.get());
		} catch (Exception e) {
			log.error("sendSnapshot error", e);
		}
	}

	@Override
	public String toString() {

		StringBuffer tokenPoolMapSb = new StringBuffer();
		if (tokenPoolMap != null) {
			for (String key : tokenPoolMap.keySet()) {
				tokenPoolMapSb.append("(" + key + ": " + tokenPoolMap.get(key));
			}
			tokenPoolMapSb.append(" )");
		}

		return "TokenPoolManager [tokenPoolMap=" + tokenPoolMapSb.toString() + ", contextConfig=" + contextConfig
				+ ", priorityManager=" + priorityManager + ", alarmPoolExecutor=" + alarmPoolExecutor
				+ ", snapshotPoolExecutor=" + snapshotPoolExecutor + "]";
	}

	public Map<String, TokenPool> getTokenPoolMap() {
		return tokenPoolMap;
	}

	public void setTokenPoolMap(Map<String, TokenPool> tokenPoolMap) {
		this.tokenPoolMap = tokenPoolMap;
	}

}
