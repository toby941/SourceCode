package com.fc.container;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.util.StringManager;

import com.fc.service.token.TokenPoolManager;

/**
 * 限流功能初始化以及销毁清理监听器，本着对业务应用最小改动原则 此监听器不在server.xml中配置注册，而是通过硬编码在 {@link StandardContext#init() }
 * 中注册,获取context的上下文信息后，对每个context(即应用)进行限流功能初始化
 * @author jun.bao
 * @since 2013年8月21日
 */
public class FlowControlLifecycleListener implements LifecycleListener {
	/**
	 * The Context we are associated with.
	 */
	protected Context context = null;

	/**
	 * 使用tomcat提供的log实现，通过配置 tomcat_home/conf/logging.properties文件，新增2limit.org.
	 * apache.juli.FileHandler.prefix = limit.
	 * 配置项，对于con.bill99.*包中所有日志统一输出到tomcat_home/logs/limit.log
	 */
	protected final static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory
			.getLog(FlowControlLifecycleListener.class);
	/**
	 * The string resources for this package.
	 */
	protected static final StringManager sm = StringManager.getManager(Constants.Package);
	/**
	 * Initialized flag.
	 */
	protected boolean initialized = false;

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		// Identify the context we are associated with
		try {
			context = (Context) event.getLifecycle();
		} catch (ClassCastException e) {
			log.error(sm.getString("contextConfig.cce", event.getLifecycle()), e);
			return;
		}
		if (event.getType().equals(Lifecycle.INIT_EVENT)) {
			log.info("FlowControlLifecycleListener begin init ");
			init(context.getName());
			log.info("FlowControlLifecycleListener finish init ");
		} else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
			stop(context.getName());
		}
	}

	/**
	 * {@link TokenPoolManager} 初始化调用方法入口
	 * @param contextName
	 */
	protected synchronized void init(String contextName) {
		if (initialized) {
			log.warn("lifecycleEvent already init with context: " + context.getPath());
			return;
		}
		if (log.isInfoEnabled()) {
			log.info(this.getClass().getName() + " init TokenPoolManager with context: " + contextName);
		}
		if (contextName == null || contextName.length() == 0 || contextName.equals("/")) {
			log.warn("contextName is null, TokenPoolManager do not need init!");
			return;
		}
		TokenPoolManager.getTokenPoolManager().init(contextName);
		initialized = true;
	}

	/**
	 * {@link TokenPoolManager} 销毁方法入口
	 * @param contextName
	 */
	protected synchronized void stop(String contextName) {
		if (!initialized) {
			log.warn(contextName + " have not been init!");
			return;
		}
		if (contextName == null || contextName.length() == 0) {
			log.warn("contextName is null, TokenPoolManager do not need stop!");
			return;
		}
		TokenPoolManager.getTokenPoolManager().stop();
		context = null;
		initialized = false;
	}
}
