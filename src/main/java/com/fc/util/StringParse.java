package com.fc.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * @author jun.bao
 * @since 2013年9月11日
 */
public class StringParse {
	protected final static Log log = LogFactory.getLog(StringParse.class);

	/**
	 * 将形如key=abc&key2=bdc&keyc=anna的字符串按k,v组合成map返回
	 * @param in
	 * @param keys
	 * @return
	 */
	public static Map<String, String> parse(InputStream in, List<String> keys) {

		Map<String, String> map = new HashMap<String, String>();

		try {
			StringTokenizer tokenizer = new StringTokenizer(IOUtils.convertStreamToString(in), "&");
			while (tokenizer.hasMoreTokens()) {
				String[] str = tokenizer.nextToken().split("=");
				String key = str[0];
				String value = str[1];
				if (keys.contains(key)) {
					map.put(key, value);
				}
			}
			return map;

		} catch (Exception e) {
			log.error("parse error", e);
		}

		return null;
	}

}
