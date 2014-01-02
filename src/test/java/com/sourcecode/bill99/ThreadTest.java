package com.sourcecode.bill99;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.junit.Test;

/**
 * @author jun.bao
 * @since 2013年9月27日
 */
public class ThreadTest {
	private final Set<String> runningJobs = Collections.synchronizedSet(new HashSet<String>());

	@Test
	public void MultiRequestsTest() {
		System.out.println(Calendar.getInstance().getTime());
		// 构造一个Runner
		TestRunnable runner = new TestRunnable() {
			@Override
			public void runTest() throws Throwable {
				while (true) {
					runningJobs.add("1");
				}
			}
		};

		TestRunnable runner2 = new TestRunnable() {
			@Override
			public void runTest() throws Throwable {
				while (true) {
					String[] arr = runningJobs.toArray(new String[0]);
					if (arr == null || arr.length == 0) {
						System.out.println("arr is null");
					}
				}
			}
		};
		int runnerCount = 500;
		runningJobs.add("1");
		// Rnner数组，相当于并发多少个。
		TestRunnable[] trs = new TestRunnable[runnerCount];
		TestRunnable[] trs2 = new TestRunnable[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			trs[i] = runner;
			trs2[i] = runner2;
		}
		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		MultiThreadedTestRunner mttr2 = new MultiThreadedTestRunner(trs2);
		try {
			// 开发并发执行数组里定义的内容
			mttr.runTestRunnables();
			mttr2.runTestRunnables();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println(Calendar.getInstance().getTime());
	}
}
