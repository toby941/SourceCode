package com.fc.util;

import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * 定义默认配置信息
 * @author jun.bao
 * @since 2013年9月9日
 */
public class Config {

	/**
	 * 是否开发环境标志，默认为true
	 */
	public final static Boolean isDev = true;
	// public static String dev_host = "http://192.168.120.160:9090";
	/**
	 * 开发用联调 限流服务端访问host
	 */
	// public final static String DEV_HOST = "http://192.168.126.170:9083/fc-server";
	public final static String DEV_HOST = "http://127.0.0.1:8080/fc-server";
	/**
	 * 正式发布用 限流服务端访问host
	 */
	public final static String PRODUCT_HOST = "http://localhost:9090";

	/**
	 * 获取限流配置策略的服务端请求路径
	 */
	public final static String GET_CONFIG_URL = "/getconfig?name={0}&hashcode={1}";

	/**
	 * 告警信息上传路径
	 */
	public final static String POST_ALARM_URL = "/upload/alarm";

	/**
	 * 快照上传路径
	 */
	public final static String POST_SNAPSHOT_URL = "/upload/snapshot";

	/**
	 * 当限流策略由服务端维护时，向服务端请求token的路径
	 */
	public final static String GET_TOKEN_URL = "/token/get?c={0}&pn={1}&p={2}";

	/**
	 * 当限流策略由服务端维护时，向服务端请求token释放的路径
	 */
	public final static String RELEASE_TOKEN_URL = "/token/release?c={0}&pn={1}&t={2}";

	private final static Long DEV_DELAY = 1000 * 10L;
	private final static Long PRODUCT_DELAY = 1000 * 60 * 1L;

	private final static Long DEV_PERIOD = 1000 * 10L;
	private final static Long PRODUCT_PERIOD = 1000 * 30L;

	public static Long getReloadDelay() {
		if (isDev) {
			return DEV_DELAY;
		} else {
			return PRODUCT_DELAY;
		}
	}

	public static Long getPeriod() {
		if (isDev) {
			return DEV_PERIOD;
		} else {
			return PRODUCT_PERIOD;
		}
	}

	public static String getHost() {
		if (isDev) {
			return DEV_HOST;
		} else {
			return PRODUCT_HOST;
		}
	}

	public static String getSnapshotUrl(String givenHost) {
		if (givenHost != null) {
			return givenHost + POST_SNAPSHOT_URL;
		}
		return getHost() + POST_SNAPSHOT_URL;
	}

	public static String getAlarmUrl(String givenHost) {
		if (givenHost != null) {
			return givenHost + POST_ALARM_URL;
		}
		return getHost() + POST_ALARM_URL;
	}

	public static String getConfigUrl(String name, Integer hashCode, String givenHost) {
		String str = "";
		if (hashCode != null) {
			str = String.valueOf(hashCode);
		}
		name = URLEncoder.encode(name.trim());
		if (givenHost != null) {
			return MessageFormat.format(givenHost + GET_CONFIG_URL, name, str);
		}
		return MessageFormat.format(getHost() + GET_CONFIG_URL, name, str);
	}

	public static String getTokenUrl(String contextName, String poolName, Integer priority, String givenHost) {
		poolName = URLEncoder.encode(poolName);
		contextName = URLEncoder.encode(contextName);
		if (givenHost != null) {
			return MessageFormat.format(givenHost + GET_TOKEN_URL, contextName, poolName, priority);
		}
		return MessageFormat.format(getHost() + GET_TOKEN_URL, contextName, poolName, priority);
	}

	public static String getReleaseTokenUrl(String contextName, String poolName, String tokenCode, String givenHost) {
		poolName = URLEncoder.encode(poolName);
		contextName = URLEncoder.encode(contextName);
		if (givenHost != null) {
			return MessageFormat.format(givenHost + RELEASE_TOKEN_URL, contextName, poolName, tokenCode);
		}
		return MessageFormat.format(getHost() + RELEASE_TOKEN_URL, contextName, poolName, tokenCode);
	}

}
