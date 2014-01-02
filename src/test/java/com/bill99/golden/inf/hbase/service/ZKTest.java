package com.bill99.golden.inf.hbase.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZooKeeperWatcher;
import org.apache.zookeeper.KeeperException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

import com.bill99.golden.inf.hbase.domain.zookeeper.Base;

/**
 * @author jun.bao
 * @since 2013年12月18日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context/*.xml")
public class ZKTest {

	static {
		try {
			Log4jConfigurer.initLogging("classpath:log4j.properties");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.err.println("Cannot Initialize log4j");
		}
	}

	private ZooKeeperWatcher watcher;
	private HMaster master;
	@Autowired
	private Configuration conf;

	@Autowired
	private ZooKeeperService zkService;

	// @Before
	public void init() throws IOException, KeeperException, InterruptedException {
		master = new HMaster(conf);
		watcher = master.getZooKeeperWatcher();
	}

	// @Test
	public void testZk() throws IOException, KeeperException {
		RecoverableZooKeeper rz = ZKUtil.connect(conf, watcher);
		List<String> node = ZKUtil.listChildrenNoWatch(watcher, "/");
		for (String s : node) {
			System.out.println(s);
		}
	}

	@Test
	public void dumpTest() {
		Base base = zkService.dumpZookeeperInfo();
		System.out.println(base);
	}

}
