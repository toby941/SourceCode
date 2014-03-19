package com.sourcecode.jvm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @author jun.bao
 * @since 2014年3月5日
 */
public class ConcurrentHashMapTest {
	public static void main(String[] args) {
		String a = "test";
		String b = "test";
		String c = b.intern();
		System.err.println(a == b);
		System.err.println(a == c);

	}

	@Test
	public void test() {
		String regrex = "((1\\d{10})|((0\\d{2,3}){1}([1-9]\\d{6,7}){1}))";
		Pattern p = Pattern.compile(regrex);
		Matcher m = p.matcher("0001111111a");
		System.out.println(m.matches());

		String pattern = "((1\\d{10})|((0\\d{2,3}){1}([1-9]\\d{6,7}){1}))";
		System.out.println(Pattern.matches(pattern, "0001111111a"));

	}

}
