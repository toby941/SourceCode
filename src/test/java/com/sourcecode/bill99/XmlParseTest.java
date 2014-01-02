package com.sourcecode.bill99;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 测试各种xml解析效率
 * @author jun.bao
 * @since 2013年8月27日
 */
public class XmlParseTest {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException,
			DocumentException, JDOMException {
		// ClassLoader.getSystemResourceAsStream("soap.xml");
		URL url = Thread.currentThread().getContextClassLoader().getResource("com/sourcecode/bill99/soap.xml");
		// System.out.println(url.getFile());
		int times = 10000 * 5;
		Long time1 = Calendar.getInstance().getTimeInMillis();
		for (int i = 0; i < times; i++) {
			// domTest(url);
			// dom4jTest(url);
			// saxTest(url);
			jdomTest(url);
		}
		Long time2 = Calendar.getInstance().getTimeInMillis();
		System.out.println(time2 - time1);
	}

	public static void jdomTest(URL url) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		org.jdom2.Document doc = builder.build(new File(url.getFile()));
		org.jdom2.Element foo = doc.getRootElement();
		org.jdom2.Element body = foo.getChildren().get(0);
		// body.getChildren().get(0).getName()
		org.jdom2.Element username = body.getChildren().get(0).getChildren().get(1);
		org.jdom2.Element ip = body.getChildren().get(0).getChildren().get(2);
		org.jdom2.Element mac = body.getChildren().get(1).getChildren().get(6);

		// System.out.println(username.getText());
		// System.out.println(ip.getText());
		// System.out.println(mac.getText());

		Map<String, String> kv = new HashMap<String, String>();
		kv.put("username", username.getText());
		kv.put("ip", ip.getText());
		kv.put("mac", mac.getText());

	}

	public static void dom4jTest(URL url) throws DocumentException {
		File f = new File(url.getFile());
		SAXReader reader = new SAXReader();
		org.dom4j.Document doc = reader.read(f);
		Element root = doc.getRootElement();
		Element body = root.element("Body");
		Element simplePay = body.element("simplePay");
		Element name = simplePay.element("username");
		Element ip = simplePay.element("ip");
		Element mac = body.element("multiRef").element("mac");

		Map<String, String> kv = new HashMap<String, String>();
		kv.put("username", name.getText());
		kv.put("ip", ip.getText());
		kv.put("mac", mac.getText());
	}

	public static void domTest(URL url) throws ParserConfigurationException, SAXException, IOException {
		File f = new File(url.getFile());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(f);
		NodeList names = doc.getElementsByTagName("username");
		NodeList ips = doc.getElementsByTagName("ip");
		NodeList macs = doc.getElementsByTagName("mac");

		Map<String, String> kv = new HashMap<String, String>();
		kv.put("username", names.item(0).getNodeValue());
		kv.put("ip", ips.item(0).getNodeValue());
		kv.put("mac", macs.item(0).getNodeValue());
		// System.out.println(names.item(0).getTextContent());
		// System.out.println(ips.item(0).getTextContent());
		// System.out.println(macs.item(0).getTextContent());
	}

	public static void saxTest(URL url) throws ParserConfigurationException, SAXException, FileNotFoundException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// factory.setValidating(true);// 开启验证XML功能
		SAXParser parser = factory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		List<String> list = new ArrayList<String>();
		list.add("ip");
		list.add("username");
		list.add("mac");
		XMLFilter myFilter = new MyFilter(reader, list);

		DefaultHandler defaultHandler = new MyDefaultHandler();
		myFilter.setContentHandler(defaultHandler);
		myFilter.parse(new InputSource(new FileInputStream(new File(url.getFile()))));

		// reader.setContentHandler(defaultHandler);
		// reader.parse(new InputSource(new FileInputStream(new
		// File(url.getFile()))));
		// String ip = (String) reader.getProperty("ip");
		// System.out.println(ip);
		Map<String, String> map = ((MyDefaultHandler) defaultHandler).getKv();
		// System.out.println(map.keySet());
	}

}
