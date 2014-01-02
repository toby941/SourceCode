package com.sourcecode.util;

import org.junit.Test;

/**
 * @author jun.bao
 * @since 2013年9月10日
 */
public class ExceptionUtilsTest {

	@Test
	public void testGetStackTrace() {
		try {
			int i = 0;
			throw new NullPointerException("i am an exception");
		} catch (Exception e) {
			String s = ExceptionUtils.getStackTrace(e);
			System.out.println(s);
		}
	}

}
