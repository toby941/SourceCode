package com.bill99.golden.inf.hbase.service.jmx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bill99.golden.inf.hbase.service.RegionService;
import com.bill99.golden.inf.hbase.service.jmx.beans.stat.RPCStats;

@Service
public class JMXRPCStatService {
	private static Logger logger = LoggerFactory.getLogger(JMXRPCStatService.class);

	public static String url;

	@Autowired
	private RegionService regionService;

	@PostConstruct
	public void init() {
		url = regionService.getBaseUrl() + "jmx?qry=hadoop:service=HBase,name=RPCStatistics-60000";
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
			logger.error("rpc request: " + urlString, ex);
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

	private RPCStats parseBean(String jsonString) {
		RPCStats bean = null;
		if (null != jsonString && jsonString.trim().length() > 0)
			bean = JSON.parseObject(jsonString, RPCStats.class);
		return bean;
	}

	public RPCStats genBeans() {
		RPCStats beans = null;
		if (null != url) {
			String urlString = request(url);
			beans = parseBean(urlString);
		}
		return beans;
	}

}
