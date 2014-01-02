package com.sourcecode.util;

import java.io.IOException;

import org.apache.log4j.AsyncAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;

/**
 * @author jun.bao
 * @since 2013年9月25日
 */
public class LogUtils {

	class ThreadTest extends Thread {

		ThreadTest() {
		}

		public void run() {
			try {
				getLog();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		LogUtils logUtils = new LogUtils();
		ThreadTest t1 = logUtils.new ThreadTest();
		ThreadTest t2 = logUtils.new ThreadTest();
		t1.start();
		t2.start();
		Thread.sleep(50000);
	}

	private static boolean initLog4j = false;

	public static void getLog() throws Exception {
		if (initLog4j) {
			System.out.println("has init");
		} else {

			System.out.println("not init");
			init();
		}
	}

	public static synchronized void init() throws IOException, InterruptedException {

		if (!initLog4j) {

			PatternLayout layout = new PatternLayout();
			layout.setConversionPattern("%d{HH:mm:ss sss} [%t] %p [%c:%L)] - %m%n");

			DailyRollingFileAppender appender = new DailyRollingFileAppender(layout, "/opt/log/sql-parameter.log", "'_'yyyy-MM-dd");
			appender.setAppend(true);
			appender.setThreshold(Level.INFO);
			appender.setName("inf.dbpool.sql.parameter");
			Thread.sleep(5000);
			System.out.println("enter init");
			AsyncAppender asyncAppender = new AsyncAppender();
			asyncAppender.addAppender(appender);
			asyncAppender.setName("inf.dbpool.sql.parameter");
			asyncAppender.setThreshold(Level.INFO);
			asyncAppender.setLayout(layout);

			asyncAppender.setBufferSize(256);
			asyncAppender.activateOptions();

			org.apache.log4j.Logger logger = LogManager.getLogger("com.bill99.ext");
			logger.setAdditivity(false);
			logger.addAppender(appender);
			logger.setLevel(Level.INFO);
			initLog4j = true;
		}
	}
}
