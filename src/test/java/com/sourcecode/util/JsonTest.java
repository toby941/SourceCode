package com.sourcecode.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 * @author jun.bao
 * @since 2013年12月19日
 */
public class JsonTest {

	private static JsonDomain jsonDomain = new JsonDomain();

	@Test
	public void fastJson() {
		String s = null;
		Date d1 = new Date();
		for (int i = 0; i < 100000; i++) {
			s = JSON.toJSONString(jsonDomain);
		}
		Date d2 = new Date();
		System.out.println("fast to json :" + (d2.getTime() - d1.getTime()));
	}

	@Test
	public void jacksonTest() throws IOException {

		String s = null;
		Date d1 = new Date();
		for (int i = 0; i < 100000; i++) {
			s = objectMapper.writeValueAsString(jsonDomain);
		}
		Date d2 = new Date();
		System.out.println("jackson to json: " + (d2.getTime() - d1.getTime()));
	}

	private static String json;
	private ObjectMapper objectMapper;

	@Before
	public void init() throws IOException {

		StdSerializerProvider sp = new StdSerializerProvider();
		objectMapper = new ObjectMapper(null, sp, null);

		json = FileUtils.readFileToString(new File(
				"E:\\git\\SourceCode\\src\\test\\java\\com\\sourcecode\\util\\domaain.json"));
	}

	@Test
	public void fastToObj() throws IOException {

		Date d1 = new Date();
		for (int i = 0; i < 100000; i++) {
			JsonDomain domain = JSON.parseObject(json, JsonDomain.class);
		}
		Date d2 = new Date();
		System.out.println("fast to obj: " + (d2.getTime() - d1.getTime()));
	}

	@Test
	public void jacksonToObj() throws IOException {

		Date d1 = new Date();
		for (int i = 0; i < 100000; i++) {
			JsonDomain domain = objectMapper.readValue(json, JsonDomain.class);
		}
		Date d2 = new Date();
		System.out.println("jackson to obj: " + (d2.getTime() - d1.getTime()));

	}

}
