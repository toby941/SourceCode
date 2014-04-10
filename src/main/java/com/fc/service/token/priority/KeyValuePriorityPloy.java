package com.fc.service.token.priority;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.fc.domain.TokenPoolConfig;
import com.fc.domain.TokenQueueConfig;
import com.fc.util.Constants;
import com.fc.util.StringParse;
import com.fc.util.XmlParse;

/**
 * 按预定义的key解析请求中携带的value，并组合成key 获取优先级
 * @author jun.bao
 * @since 2013年9月10日
 */
public class KeyValuePriorityPloy implements PriorityPloy {
	protected final static Log log = LogFactory.getLog(KeyValuePriorityPloy.class);

	/**
	 * 缓存优先级的map
	 */
	private Map<String, Integer> priorityMap;
	private String requestURL;
	/**
	 * 需解析提取的目标key列表
	 */
	private List<String> filterKeys;
	/**
	 * 请求类型 text/xml
	 */
	private String contentType;

	public KeyValuePriorityPloy() {
		super();
	}

	public KeyValuePriorityPloy(TokenPoolConfig config) {
		super();
		requestURL = config.getRequestUrl();
		contentType = config.getContentType();
		/*
		 * 此处的key按照 key:key:key 形式组合，一个或多个
		 */
		String key = config.getRequestKey();
		StringTokenizer st = new StringTokenizer(key, Constants.PRIORITY_VALUE_DELIMITER);
		filterKeys = new ArrayList<String>();

		while (st.hasMoreTokens()) {
			filterKeys.add(st.nextToken());
		}
		priorityMap = new HashMap<String, Integer>();
		if (config.getTokenQueueConfigs() != null && config.getTokenQueueConfigs().size() > 0) {

			for (TokenQueueConfig queueConfig : config.getTokenQueueConfigs()) {
				/*
				 * 此处的value+? 多个之间用#作为分隔符号
				 */
				String value = queueConfig.getValue();
				Integer priority = queueConfig.getPriority();
				StringTokenizer sTokenizer = new StringTokenizer(value, Constants.PRIORITY_VALUE_GROUP_DELIMITER);
				while (sTokenizer.hasMoreTokens()) {
					priorityMap.put(sTokenizer.nextToken(), priority);
				}
			}
		}
	}

	/**
	 * 解析请求流，按配置的key进行关键内容提取 将关键value组装成value1-value2-value3形式作为key查找对应优先级
	 */
	@Override
	public Integer getPriority(InputStream in) {
		try {
			Map<String, String> kv = null;

			if (contentTypeXML.equals(contentType)) {
				kv = XmlParse.sax(in, filterKeys);
			} else {
				kv = StringParse.parse(in, filterKeys);
			}
			// 请求参数中没有关键key，则按最低优先级处理
			if (kv == null || kv.size() == 0) {
				log.debug("there is no key in InputStream return Integer.MAX_VALUE");
				return Integer.MAX_VALUE;
			}
			StringBuffer sbKey = new StringBuffer();
			/**
			 * 将关键value组装成value1-value2-value3形式作为key查找对应优先级
			 */
			for (String value : kv.values()) {
				sbKey.append(value + Constants.PRIORITY_VALUE_DELIMITER);
			}
			String key = sbKey.substring(0, sbKey.length() - 1);

			Integer priority = priorityMap.get(key);
			if (priority == null) {
				priority = Integer.MAX_VALUE;
			}
			if (log.isDebugEnabled()) {
				log.info(MessageFormat.format("get priority:{0},with key:{1},Thread:{2}", priority, key, Thread
						.currentThread().getName()));
			}
			return priority;
		} catch (Exception e) {
			log.error("getPriority error", e);
			return Integer.MAX_VALUE;
		}
	}
}
