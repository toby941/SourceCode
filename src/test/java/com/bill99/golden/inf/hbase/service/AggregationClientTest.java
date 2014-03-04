package com.bill99.golden.inf.hbase.service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jun.bao
 * @since 2014年1月21日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context/*.xml")
public class AggregationClientTest {
	@Autowired
	private Configuration conf;

	public <R, S> long get() {
		return 0;

	}

	// @Test
	public void changeTable() throws Exception {
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTable hTable = new HTable(conf, "t1");
		HTableDescriptor htd = hTable.getTableDescriptor();
		HTableDescriptor newHtd = new HTableDescriptor(htd);

		newHtd.addCoprocessor("org.apache.hadoop.hbase.coprocessor.AggregateImplementation");

		newHtd.setDeferredLogFlush(true);
		admin.disableTable("t1");
		admin.modifyTable(Bytes.toBytes("t1"), newHtd);
		admin.enableTable("t1");
	}

	/**
	 * test: 379135 %128% 9706
	 * if_fss_files: 3035224 %BIZ% 2848
	 * @throws Throwable
	 */
	@Test
	public void count() throws Throwable {
		conf.setLong("hbase.rpc.timeout", 600000);
		AggregationClient aggregationClient = new AggregationClient(conf);
		Scan scan = new Scan();
		// 指定扫描列族，唯一值
		scan.addFamily(Bytes.toBytes("cf"));
		// Filter f = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("BIZ.*"));
		// scan.setFilter(f);
		long rowCount = aggregationClient.rowCount(Bytes.toBytes("t1"), null, scan);
		System.out.println("row count is " + rowCount);
	}
}
