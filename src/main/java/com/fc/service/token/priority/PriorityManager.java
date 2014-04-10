package com.fc.service.token.priority;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.fc.domain.TokenPoolConfig;

/**
 * @author jun.bao
 * @since  2013年9月4日 
 */
/**
 * 限流优先级获取统一入口
 * @author jun.bao
 * @since 2013年8月29日
 */
public class PriorityManager {
	protected final static Log log = LogFactory.getLog(PriorityManager.class);
	private static PriorityManager manager;

	public Map<String, PriorityPloy> priorityMap;

	public static PriorityManager getPriorityManager() {
		if (manager == null) {
			manager = new PriorityManager();
		}
		return manager;
	}

	public void stop() {
		if (priorityMap != null) {
			priorityMap.clear();
			priorityMap = null;
			manager = null;
		}
	}

	public void load(List<TokenPoolConfig> configs) {
		for (TokenPoolConfig config : configs) {
			String key = config.getRequestKey();
			if (key != null && key.length() > 0) {
				PriorityPloy priorityPloy = new KeyValuePriorityPloy(config);
				priorityMap.put(config.getRequestUrl(), priorityPloy);
			}
		}
	}

	/**
	 * 单例实现
	 */
	private PriorityManager() {
		super();
		priorityMap = new ConcurrentHashMap<String, PriorityPloy>();
	}

	/**
	 * 获取优先级入口
	 * @param requestUri
	 * @param in
	 * @return 若根据key找到对应priority则直接返回 否则返回 {@link Integer#MAX_VALUE};
	 */
	public Integer getPriority(String requestUri, InputStream in) {

		if (in == null) {
			log.debug("no input stream witu uri: " + requestUri + " return Integer.MAX_VALUE");
			return Integer.MAX_VALUE;
		}

		PriorityPloy priorityPloy = priorityMap.get(requestUri);
		if (priorityPloy != null) {
			Integer priority = priorityPloy.getPriority(in);
			return priority;
		}
		if (log.isInfoEnabled()) {
			log.info("no priorityPloy found witu uri: " + requestUri + " return Integer.MAX_VALUE");
		}
		return Integer.MAX_VALUE;
	}

}