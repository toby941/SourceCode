package com.bill99.golden.inf.hbase.service;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jun.bao
 * @since 2013年12月25日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context/*.xml")
public class ConfigurationServiceTest {

	@Autowired
	private ConfigurationService configurationService;

	@Test
	public void testLoadConfiguration() {
		Map<String, String[]> map = configurationService.loadConfiguration();
		for (String key : map.keySet()) {
			System.out.println(key + " : " + map.get(key)[0]);
		}
	}
}
