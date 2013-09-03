package com.sourcecode.groovy;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.net.URL;

/**
 * @author jun.bao
 * @since 2013年9月2日
 */
public class InvokeGroovy {
	public static void main(String[] args) {
		ClassLoader cl = new InvokeGroovy().getClass().getClassLoader();
		GroovyClassLoader groovyCl = new GroovyClassLoader(cl);
		try {
			// 从文件中读取
			URL url = Thread.currentThread().getContextClassLoader().getResource("com/sourcecode/groovy/Test.groovy");
			File f = new File(url.getFile());
			Class groovyClass = groovyCl.parseClass(f);
			// 直接使用Groovy字符串,也可以获得正确结果
			// Class groovyClass =
			// groovyCl.parseClass("package org.openjweb.groovy; \r\n import org.openjweb.core.groovy.test.IFoo;\r\n class Foo implements IFoo {public Object run(Object foo) {return 23}}");
			// Class groovyClass = groovyCl
			// .parseClass("package groovy; \r\n import groovy.IFoo;\r\n class Foo implements IFoo {public Object run(Object foo) {return 22222}}");//
			// 这个返回true

			IFoo foo = (IFoo) groovyClass.newInstance();
			System.out.println(foo.run(new Integer(2)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
