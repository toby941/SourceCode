package com.fc.service.token.task;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步提交http请求的线程调度类
 * @author jun.bao
 * @since 2013年8月29日
 */
public class HttpRequestPoolExecutor extends ThreadPoolExecutor {

	/**
	 * 初始处理线程数
	 */
	private static final Integer CORE_SIZE = 2;
	/**
	 * 最大处理线程数
	 */
	private static final Integer MAX_SIZE = 20;
	/**
	 * task允许执行时间 单位 秒
	 */
	private static Long keepTime = 10L;
	/**
	 * 最大任务排队数
	 */
	private static Integer workQueueSize = 1000;

	public HttpRequestPoolExecutor() {
		super(CORE_SIZE, MAX_SIZE, keepTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(workQueueSize),
				new LogAlarmPolicy());
	}

	/**
	 * 当task无法执行时将信息记录本地日志
	 * @author jun.bao
	 */
	public static class LogAlarmPolicy implements RejectedExecutionHandler {
		protected final static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory
				.getLog(LogAlarmPolicy.class);

		public LogAlarmPolicy() {
			super();
		}

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
			FutureTask<HttpCallable> task = (FutureTask<HttpCallable>) r;
			HttpCallable alertCallable;
			try {
				alertCallable = task.get();

				Map<String, String> alarmInfo = alertCallable.getPostMap();
				StringBuffer sb = new StringBuffer();
				for (String key : alarmInfo.keySet()) {
					sb.append(key + ": " + alarmInfo.get(key) + "&");
				}
				if (log.isInfoEnabled()) {
					log.info("http request task rejected,info:" + sb.toString() + " Thread: "
							+ Thread.currentThread().getName());
				}
			} catch (Exception e1) {
				log.error("rejectedExecution handle error" + e1);
			}
		}
	}
}
