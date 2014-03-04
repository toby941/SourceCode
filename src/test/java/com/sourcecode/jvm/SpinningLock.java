package com.sourcecode.jvm;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jun.bao
 * @since 2014年1月17日
 */
public class SpinningLock implements Runnable {

	private static AtomicLong count;
	public final static int NUM_THREADS = 1; // change
	public final static long max_long = 500L * 1000L * 600L;

	public static void main(final String[] args) throws Exception {
		final long start = System.nanoTime();
		count = new AtomicLong();
		runTest();
		System.out.println("duration = " + (System.nanoTime() - start));
	}

	private static void runTest() throws InterruptedException {
		Thread[] threads = new Thread[NUM_THREADS];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new SpinningLock());
		}

		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}
	}

	@Override
	public void run() {
		for (;;) {
			long result = count.addAndGet(1);
			if (result > max_long) {
				return;
			}
		}
	}

}
