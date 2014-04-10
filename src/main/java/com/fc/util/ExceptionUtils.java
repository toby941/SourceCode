package com.fc.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author jun.bao
 * @since 2013年9月10日
 */
public class ExceptionUtils {

	/**
	 * 将异常堆栈合并为一个字符串
	 * 
	 * @param throwable
	 * @return
	 */
	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	/**
	 * 限制异常堆栈输出最大长度
	 * 
	 * @param throwable
	 * @param size
	 * @return 按 size 截取的 异常堆栈
	 */
	public static String getStackTrace(Throwable throwable, int size) {
		String str = getStackTrace(throwable);
		int sub = str.length() > size ? size : str.length();
		return str.substring(0, sub);
	}

}