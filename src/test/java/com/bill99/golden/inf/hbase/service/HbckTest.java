package com.bill99.golden.inf.hbase.service;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.util.HBaseFsck;
import org.apache.zookeeper.KeeperException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jun.bao
 * @since 2013年12月24日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context/*.xml")
public class HbckTest {
	@Autowired
	private Configuration conf;

	@Test
	public void errorTest() throws MasterNotRunningException, ZooKeeperConnectionException, IOException,
			InterruptedException, KeeperException, ClassNotFoundException {
		conf.set("hbase.rootdir", "hdfs://namenode01:9000/hbase");
		HBaseFsck baseFsck = new HBaseFsck(conf);
		baseFsck.connect();
		System.out.println("root:" + conf.get("hbase.rootdir"));
		System.out.println("hbase.hregion.max.filesize:" + conf.get("hbase.hregion.max.filesize"));
		System.out.println("hbase.hregion.memstore.flush.size:" + conf.get("hbase.hregion.memstore.flush.size"));
		System.out.println("regionsplitlimit:" + conf.get("hbase.regionserver.regionSplitLimit"));

		// baseFsck.onlineHbck();
		// baseFsck.loadHdfsRegionDirs();
		// HBaseFsck.ErrorReporter error = baseFsck.getErrors();
		// String s = "";
		// error.detail(s);
		// System.out.println(s);
	}

}
