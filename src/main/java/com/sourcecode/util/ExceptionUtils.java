package com.sourcecode.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author jun.bao
 * @since 2013年9月10日
 */
public class ExceptionUtils {

	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

}