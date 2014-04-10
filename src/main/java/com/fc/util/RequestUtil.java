package com.fc.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jun.bao
 * @since 2013年8月28日
 */
public class RequestUtil {

	private final static String SAOP_HEAD_NAME = "soapaction";

	/**
	 * 判断是否soap请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isSOAPRequest(HttpServletRequest request) {
		return request.getHeader(SAOP_HEAD_NAME) != null;
	}

	public static void getValue(HttpServletRequest request) {
	}

}
