package com.bill99.golden.inf.hbase.service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.catalog.CatalogTracker;
import org.apache.hadoop.hbase.catalog.MetaReader;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * @author jun.bao
 * @since 2013年12月20日
 */

@ContextConfiguration(locations = "classpath:context/*.xml")
public class SplitTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private Configuration conf;

	// @Before
	public void init() {
		conf = HBaseConfiguration.create();
		conf.set("fs.default.name", "hdfs://namenode01:9000");
		conf.set("hbase.zookeeper.quorum", "zk01,zk02,datanode08");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.regionserver.lease.period", "9000");

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

	public static void main(String[] args) throws IOException {
		String put = "put ''test'', ''toby2323{0}'', ''tc:{1}'',''1''";
		File f = new File("D://tmp//test.hbase");
		List<String> content = new ArrayList<String>();
		for (int i = 0; i < 1000 * 1000; i++) {
			Long rowid = RandomUtils.nextLong();
			Long value = RandomUtils.nextLong();
			String sql = MessageFormat.format(put, String.valueOf(rowid), String.valueOf(value));
			content.add(sql);
		}
		FileUtils.writeLines(f, content);
	}

	// @Test
	public void tableUpdateTest() throws IOException {

		// 更新现有表的split策略
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTable hTable = new HTable(conf, "test");
		HTableDescriptor htd = hTable.getTableDescriptor();
		HTableDescriptor newHtd = new HTableDescriptor(htd);
		newHtd.setValue(HTableDescriptor.SPLIT_POLICY, KeyPrefixRegionSplitPolicy.class.getName());// 指定策略
		newHtd.setValue("prefix_split_key_policy.prefix_length", "2");
		newHtd.setValue("MEMSTORE_FLUSHSIZE", "5242880"); // 5M
		admin.disableTable("test");
		admin.modifyTable(Bytes.toBytes("test"), newHtd);
		admin.enableTable("test");

		System.out.println(hTable.getTableDescriptor().getRegionSplitPolicyClassName());
		for (HTableDescriptor h : admin.listTables()) {
			System.out.println(h.getNameAsString() + "  " + h.getRegionSplitPolicyClassName());
		}
	}

	// @Test
	public void testTableSplitPolicy() throws IOException {
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTable hTable = new HTable(conf, "test");
		System.out.println(HTableDescriptor.DEFAULT_MEMSTORE_FLUSH_SIZE);
		HTableDescriptor htd = hTable.getTableDescriptor();
		System.out.println("desiredMaxFileSize: " + htd.getMaxFileSize());
		System.out.println("flushSize: " + htd.getMemStoreFlushSize());
		System.out.println("conf flushSize: "
				+ conf.getLong(HConstants.HREGION_MEMSTORE_FLUSH_SIZE, HTableDescriptor.DEFAULT_MEMSTORE_FLUSH_SIZE));
		System.out.println("conf desiredMaxFileSize: "
				+ conf.getLong(HConstants.HREGION_MAX_FILESIZE, HConstants.DEFAULT_MAX_FILE_SIZE));
	}

	@Test
	public void testInsert() throws IOException {
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTable hTable = new HTable(conf, "test");

		List<Put> list = new ArrayList<Put>();
		String value = RandomStringUtils.random(500);
		hTable.flushCommits();
		hTable.setAutoFlush(false);
		for (int i = 0; i < 1000000; i++) {
			if (i % 2000 == 0) {
				System.out.println(i);
				hTable.flushCommits();
			}
			Put p = getPut(RandomUtils.nextInt(999) + "toby" + RandomUtils.nextLong(), "tc", null,
					"test" + RandomUtils.nextInt() + value);
			hTable.put(p);
		}
		hTable.flushCommits();
		hTable.close();
	}

	// @Test
	public void testSplit() throws IOException, InterruptedException {
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTable hTable = new HTable(conf, "test");
		Map<HRegionInfo, ServerName> map = hTable.getRegionLocations();
		System.out.println(map.size());
		for (HRegionInfo info : map.keySet()) {

			System.out.println(info.getRegionNameAsString());
			admin.split(info.getRegionName());
			admin.flush(info.getRegionName());
		}
		Thread.sleep(1000L * 100);
	}

	// @Test
	public void doSplit() throws IOException, InterruptedException {
		HBaseAdmin admin = new HBaseAdmin(conf);
		byte[] tableName = Bytes.toBytes("if_fss_files,anna,1387763667252.7248c72b07e811fdc6e9dcf18ed2a447.");
		byte[] splitPoint = Bytes.toBytes("anna ss");
		admin.split(tableName, splitPoint);
		Thread.sleep(1000L * 10000);
	}

	// @Test
	public void testPair() throws ZooKeeperConnectionException, IOException, InterruptedException {
		Pair<HRegionInfo, ServerName> pair = MetaReader.getRegion(getCatalogTracker(),
				Bytes.toBytes("if_fss_files,if_fss_files, toby,1387532921658.126d2f02f4033caa092ff0e03382119d."));
		System.out.println(pair.getSecond());
		ServerName sn = pair.getSecond();
		HConnection connection = HConnectionManager.getConnection(this.conf);
		// HRegionInterface rs = connection.getHRegionConnection(sn.getHostname(), sn.getPort());
		HRegionServer regionServer = new HRegionServer(conf);
		HTable hTable = new HTable(conf, "if_fss_files");
		Map<HRegionInfo, ServerName> map = hTable.getRegionLocations();
		int i = 0;
		for (HRegionInfo info : map.keySet()) {
			if (i > 10) {
				break;
			}
			i++;
			byte[] b = info.getRegionName();
			String name = Bytes.toStringBinary(info.getRegionName());
			HRegion in = regionServer.getOnlineRegion(Bytes.toBytes(name));
			if (in == null) {
				System.out.println(name + "is not online");
			} else {
				System.out.println(in.getRegionNameAsString());
			}
		}
	}

	private synchronized CatalogTracker getCatalogTracker() throws ZooKeeperConnectionException, IOException {
		CatalogTracker ct = null;
		try {
			ct = new CatalogTracker(conf);
			ct.start();
		} catch (InterruptedException e) {
			// Let it out as an IOE for now until we redo all so tolerate IEs
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted", e);
		}
		return ct;
	}

}
