package com.fc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 基于SAX的xml解析器
 * @author jun.bao
 * @since 2013年8月27日
 */
public class XmlParse {
	protected final static Log log = LogFactory.getLog(XmlParse.class);

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		// ClassLoader.getSystemResourceAsStream("soap.xml");
		List<String> list = new ArrayList<String>();
		list.add("ip");
		list.add("username");
		list.add("multiRef");
		String file = "D://tmp//soap.xml";
		InputStream in = new FileInputStream(new File(file));
		// System.out.println(url.getFile());
		int times = 1;
		Long time1 = Calendar.getInstance().getTimeInMillis();
		for (int i = 0; i < times; i++) {
			Map<String, String> result = sax(in, list);
			for (String key : result.keySet()) {
				System.out.println(key + "  " + result.get(key));
			}
		}
		Long time2 = Calendar.getInstance().getTimeInMillis();
		System.out.println(time2 - time1);
	}

	private static XMLReader getReader() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			return parser.getXMLReader();
		} catch (Exception e) {
			log.error("create reader error", e);
			return null;
		}
	}

	/**
	 * 解析xml
	 * @param in
	 * @param keys
	 * @return 提取 xml中 所有 keys中属性，以key-value形式map返回
	 */
	public static Map<String, String> sax(InputStream in, List<String> keys) {
		Map<String, String> map = null;
		try {
			XMLReader reader = getReader();
			if (reader != null) {
				XMLFilter myFilter = new ChildNodeKeyFilter(reader, keys);
				DefaultHandler defaultHandler = new KeyValueHandler();
				myFilter.setContentHandler(defaultHandler);
				myFilter.parse(new InputSource(in));
				map = ((KeyValueHandler) defaultHandler).getKv();
			}
		} catch (Exception e) {
			log.error("parser error", e);
		}
		return map;
	}

}
