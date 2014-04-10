package com.fc.util;

import java.util.List;

/**
 * @author jun.bao
 * @since 2013年9月10日
 */
public class CollectionUtils {

	/**
	 * 将list对象元素转化为一个stirng对象
	 * 
	 * @param list
	 * @return o.toString() + " "+o.toString() + " "..格式的字符串
	 */
	public static String listToString(List list) {
		StringBuffer sb = new StringBuffer();
		for (Object o : list) {
			sb.append(o.toString() + " ");
		}
		return sb.toString();
	}
}
