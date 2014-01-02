package com.bill99.golden.inf.hbase.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HServerLoad;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bill99.golden.inf.hbase.domain.Region;
import com.bill99.golden.inf.hbase.domain.Table;

/**
 * @author jun.bao
 * @since 2013年12月13日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context/*.xml")
public class RegionServiceTest {

	@Autowired
	private RegionService regionService;

	@Test
	public void getSotefile() throws IOException {
		HBaseAdmin admin = regionService.getHBaseAdmin();
		ClusterStatus status = admin.getClusterStatus();
		Collection<ServerName> serverNames = status.getServers();
		for (ServerName sn : serverNames) {
			String serverNameStr = sn.getHostname();
			if (serverNameStr.contains("datanode09")) {
				HServerLoad serverLoad = status.getLoad(sn);
				for (Map.Entry<byte[], HServerLoad.RegionLoad> entry : serverLoad.getRegionsLoad().entrySet()) {
					final HServerLoad.RegionLoad regionLoad = entry.getValue();
					String name = regionLoad.getNameAsString();
					if (name.equals("if_fss_files,anna ss,1387791557050.3978a41e71058bec136235cec744a318.")) {
					}
					System.out.println(name);
				}
			}
		}
	}

	// @Test
	public void testGetServerNames() throws IOException {
		for (int i = 0; i < 5; i++) {
			Long begin = new Date().getTime();
			List<String> names = regionService.getServerNames();
			Long end = new Date().getTime();
			System.out.println(end - begin);
		}
		// for (String s : names) {
		// System.out.println(s);
		// }
	}

	// @Test
	public void testTable() throws IOException {
		Configuration configuration = regionService.getConfiguration();
		HBaseAdmin admin = new HBaseAdmin(configuration);
		HTable hTable = new HTable(configuration, "if_fss_files");
		Map<HRegionInfo, ServerName> map = hTable.getRegionLocations();
		System.out.println(map.size());
		int i = 0;
		for (HRegionInfo info : map.keySet()) {
			if (i > 10) {
				break;
			}
			i++;
			byte[] b = info.getRegionName();
			String name = Bytes.toStringBinary(info.getRegionName());
			// System.out.println(info.getRegionNameAsString());
		}

	}

	// @Test
	public void getTableNamses() throws IOException {
		Date d1 = new Date();
		List<String> list = regionService.getTableNames();
		for (String t : list) {
			System.out.println(t);
		}
		Date d2 = new Date();
		System.out.println(d2.getTime() - d1.getTime());
	}

	// @Test
	public void regionTestAll() throws IOException {
		Date d1 = new Date();
		Map<String, List<Table>> map = regionService.getTableInfo();
		for (String key : map.keySet()) {
			List<Table> list = (List<Table>) map.get(key);
			for (Table t : list) {
				System.out.println(t);
			}
		}
		Date d2 = new Date();
		System.out.println(d2.getTime() - d1.getTime());
	}

	// @Test
	public void testRegionList() throws Exception {
		Date d1 = new Date();
		List<Region> list = regionService.getRegionInfo("if_fss_files", "datanode09");
		for (Region r : list) {
			System.out.println(r);
		}
		Date d2 = new Date();
		System.out.println(d2.getTime() - d1.getTime());
	}

}
