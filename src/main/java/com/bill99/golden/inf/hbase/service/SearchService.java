package com.bill99.golden.inf.hbase.service;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bill99.golden.inf.hbase.util.HBaseUtils;

/**
 * @author jun.bao
 * @since 2013年12月27日
 */
@Service
public class SearchService {
	@Autowired
	private Configuration configuration;

	private final static Logger log = Logger.getLogger(SearchService.class);

	public Result get(String tableName, String rowkey) throws IOException {
		HTable hTable = null;
		try {
			if (StringUtils.isNotBlank(tableName) || StringUtils.isNotBlank(rowkey)) {
				hTable = new HTable(configuration, tableName);
				Get get = new Get(Bytes.toBytes(rowkey));
				Result r = hTable.get(get);

				return r;
			}
			return null;
		} finally {
			HBaseUtils.closeQuietly(hTable);
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
