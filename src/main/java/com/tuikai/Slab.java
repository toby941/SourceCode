package com.tuikai;

public class Slab {

	static int size = 100000 * 85;
	/**
	 * 缓存数组
	 */
	private static Object[] cache = new Object[size];

	public static void main(String[] args) throws InterruptedException {
		GCMonitoring.init();
		//缓存填充
		for (int i = 0; i < cache.length; i++) {
			logGCInfo(i);
			cache[i] = new Long(i);
		}
		//隔行删除
		for (int i = 0; i < cache.length; i++) {
			if (i % 2 == 0) {
				cache[i] = null;
			}
			logGCInfo(i);
		}
		//主动gc
		System.gc();
		Thread.sleep(1000L * 2);
		logGCInfo(0);
		for (int i = 0; i < cache.length; i++) {
			if (i % 2 == 0) {
				cache[i] = new BigLong();  //胖子对象
				 //cache[i]=new Long(i);  //普通对象
			}
			logGCInfo(i);
		}
	}

	public static Long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public static Long getfree() {
		return Runtime.getRuntime().freeMemory();
	}

	public static void logGCInfo(int i) throws InterruptedException {
		if (i % 100000 == 0) {
			Thread.sleep(100L);
			System.out.println("index:" + i / 100000 + " total:"
					+ getTotalMemory() / 1024 / 1024 + " free:" + getfree()
					/ 1024 / 1024);
		}
	}
}

class BigLong {
	private Long[] big;

	public BigLong() {
		super();
		this.big = new Long[9999];
		for (int i = 0; i < big.length; i++) {
			big[i] = new Long(i);
		}

	}
}
