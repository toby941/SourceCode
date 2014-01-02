package com.bill99.golden.inf.hbase.service.jmx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bill99.golden.inf.hbase.service.jmx.beans.stat.RPCStats;

/**
 * @author jun.bao
 * @since 2013年12月19日
 */
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = "classpath:context/*.xml")
public class JMXRPCStatServiceTest {

	@Autowired
	private JMXRPCStatService service;

	// @Test
	public void test() {
		RPCStats stats = service.genBeans();
		System.out.println(stats);
	}

	private String request(String urlString) {
		URL url = null;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(urlString);
			in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			String str = null;
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore
			}
		}
		return sb.toString();
	}

	@Test
	public void testJson() {
		String s = request("http://namenode01:60010/jmx?qry=hadoop:service=Master,name=Master");
		JSONArray jsonArray = JSON.parseObject(s).getJSONArray("beans").getJSONObject(0).getJSONArray("RegionServers");

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONArray regionsLoads = jsonObject.getJSONObject("value").getJSONArray("regionsLoad");
			for (int j = 0; j < regionsLoads.size(); j++) {
				JSONObject regionsLoad = regionsLoads.getJSONObject(j);
				JSONObject json = regionsLoad.getJSONObject("value");
				String name = json.getString("nameAsString");
				if (name.startsWith("hbase") || name.startsWith("if")) {

				} else {
					System.out.println(name);
					System.out.println(json.getIntValue("memStoreSizeMB"));
				}
			}
		}

	}

}
