package com.bill99.golden.inf.hbase.service;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bill99.golden.inf.hbase.util.HBaseUtils;

/**
 * @author jun.bao
 * @since 2013年12月23日
 */
@Service
public class SplitService {

	@Autowired
	private Configuration configuration;

	private final static Logger log = Logger.getLogger(SplitService.class);

	public void split(String tableNameOrRegionName, String splitPoint) {
		HBaseAdmin admin = null;
		try {
			admin = new HBaseAdmin(configuration);
			if (StringUtils.isNotBlank(splitPoint)) {
				admin.split(tableNameOrRegionName, splitPoint);
			} else {
				admin.split(tableNameOrRegionName);
			}
		} catch (Exception e) {
			log.error(String.format("tableNameOrRegionName: %s splitpoint:%s", tableNameOrRegionName, splitPoint), e);
		} finally {
			HBaseUtils.closeQuietly(admin);
		}
	}
}
