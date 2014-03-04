package com.bill99.golden.inf.hbase.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * @author jun.bao
 * @since 2014年2月14日
 */
@ContextConfiguration(locations = "classpath:context/*.xml")
public class WriteTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private Configuration conf;

	// @Test
	public void changeTable() throws Exception {
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTable hTable = new HTable(conf, "t1");
		HTableDescriptor htd = hTable.getTableDescriptor();
		HTableDescriptor newHtd = new HTableDescriptor(htd);
		newHtd.setValue(HTableDescriptor.SPLIT_POLICY, KeyPrefixRegionSplitPolicy.class.getName());// 指定策略
		newHtd.setValue("hbase.hregion.max.filesize", "1073741824");// 1G
		newHtd.setValue("prefix_split_key_policy.prefix_length", "2");
		newHtd.setValue("MEMSTORE_FLUSHSIZE", "5242880"); // 5M
		newHtd.setValue("COMPRESSION", "SNAPPY");
		newHtd.setValue("hbase.master.loadbalance.bytable", "true");
		newHtd.setDeferredLogFlush(true);
		admin.disableTable("t1");
		admin.modifyTable(Bytes.toBytes("t1"), newHtd);
		admin.enableTable("t1");
	}

	public Put getPut(String row, String columnFamily, String qualifier, String value) throws IOException {
		Put put = new Put(row.getBytes());
		if (qualifier == null || "".equals(qualifier)) {
			put.add(columnFamily.getBytes(), null, value.getBytes());
		} else {
			put.add(columnFamily.getBytes(), qualifier.getBytes(), value.getBytes());
		}
		return put;
	}

	// @Test
	public void multiTest() {
		TestRunnable runner = new TestRunnable() {
			@Override
			public void runTest() throws Throwable {
				testT1Input();
			}
		};
		int runnerCount = 5;
		// Rnner数组，相当于并发多少个。
		TestRunnable[] trs = new TestRunnable[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			trs[i] = runner;
		}
		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		try {
			// 开发并发执行数组里定义的内容
			mttr.runTestRunnables();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	// @Test
	public void testT1Input() throws IOException {
		HTable htable = new HTable(conf, "t1");
		htable.setWriteBufferSize(10 * 1024 * 1024);
		htable.setAutoFlush(false);
		Long begin = Calendar.getInstance().getTimeInMillis();
		String value = RandomStringUtils.random(50);
		String name = (System.nanoTime() % 1000) + Thread.currentThread().getName();
		List<Put> putList = new ArrayList<Put>();
		for (int i = 0; i < 1000000; i++) {
			if (i % 20000 == 0) {
				System.out.println(i);
				// htable.flushCommits();
				// htable.put(putList);
				// putList.clear();
				System.out.println(i + "rpc done");
			}
			Put p = getPut(i + name, "cf", null, "test" + i + value);
			// htable.put(p);
			putList.add(p);
			htable.put(p);
			htable.flushCommits();
		}
		// htable.flushCommits();
		htable.close();
		Long end = Calendar.getInstance().getTimeInMillis();
		System.err.println(name + "  cost: " + (end - begin));
	}

	@Test
	public void testT2Input() throws IOException {

		HTable htable = new HTable(conf, "t1");
		Long begin = Calendar.getInstance().getTimeInMillis();
		String value = RandomStringUtils.random(50);
		String name = (System.nanoTime() % 1000) + Thread.currentThread().getName();
		for (int i = 0; i < 10000; i++) {
			if (i % 200 == 0) {
				System.out.println(i);
				// htable.flushCommits();
				// htable.put(putList);
				// putList.clear();
				System.out.println(i + "rpc done");
			}
			Put p = getPut(i + name, "cf", null, "test" + i + value);
			htable.put(p);
			htable.flushCommits();
		}

		htable.close();
		Long end = Calendar.getInstance().getTimeInMillis();
		System.err.println(name + "  cost: " + (end - begin));
	}

	// @Test
	public void testApplogInput() throws IOException {
		HTable htable = new HTable(conf, "hbase_app_log_model_2014-02-13");
		htable.setWriteBufferSize(10 * 1024 * 1024);
		htable.setAutoFlush(false);
		Long begin = Calendar.getInstance().getTimeInMillis();
		String value = RandomStringUtils.random(500);
		for (int i = 0; i < 100000; i++) {
			if (i % 2000 == 0) {
				System.out.println(i);
				htable.flushCommits();
			}
			Put p = getPut(i + "toby3", "data", null, "test" + i + value + value);
			htable.put(p);
		}
		htable.flushCommits();
		htable.close();
		Long end = Calendar.getInstance().getTimeInMillis();
		System.err.println("cost: " + (end - begin));
	}

}
