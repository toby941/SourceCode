package com.sourcecode.util;

import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author jun.bao
 * @since 2014年2月19日
 */
public class LogTest {

	public static void main(String[] args) {
		URL config = LogTest.class.getClassLoader().getResource("log4j.properties");
		String filePath = config.toExternalForm().substring("file:/".length());
		new PropertyConfigurator().doConfigure(filePath, LogManager.getLoggerRepository());
		System.out.println("parse done");
		return;
	}
}
