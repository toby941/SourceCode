package com.fc.util;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.bill99.fc.util.ChildNodeKeyFilter;

/**
 * 将xml中经过 {@link ChildNodeKeyFilter}过滤的待提取属性以key-value方式返回一个map集合
 * @author jun.bao
 * @since 2013年8月27日
 */
public class KeyValueHandler extends DefaultHandler {
	private StringBuffer buf;

	private Map<String, String> kv;

	public KeyValueHandler() {
		super();
		kv = new HashMap<String, String>();
	}

	@Override
	public void startDocument() throws SAXException {
		buf = new StringBuffer();
		// System.out.println("*******開始剖析文件*******");
	}

	@Override
	public void endDocument() throws SAXException {
		// System.out.println("*******剖析文件結束*******");
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) {
		// System.out.println("\n字首對應: " + prefix + " 開始!" + "  它的URI是:" + uri);
	}

	@Override
	public void endPrefixMapping(String prefix) {
		// System.out.println("\n字首對應: " + prefix + " 結束!");
	}

	@Override
	public void startElement(String namespaceURI, String localName, String fullName, Attributes attributes)
			throws SAXException {

		// System.out.println("\n元素: " + "[" + fullName + "]" + " 開始剖析!");

		// 列印出屬性訊息
		for (int i = 0; i < attributes.getLength(); i++) {
			// System.out.println("\t屬性名稱:" + attributes.getLocalName(i) +
			// " 屬性值:" + attributes.getValue(i));
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String fullName) throws SAXException {
		// 列印出非空的元素內容並將StringBuffer清空
		String nullStr = "";
		if (!buf.toString().trim().equals(nullStr)) {
			// System.out.println("\t內容是: " + buf.toString().trim());
			kv.put(fullName, buf.toString().trim());
		}
		buf.setLength(0);
		// 列印元素剖析結束訊息
		// System.out.println("元素: " + "[" + fullName + "]" + " 剖析結束!");
	}

	public void reNew() {
		kv.clear();
	}

	public Map<String, String> getKv() {
		return kv;
	}

	public void setKv(Map<String, String> kv) {
		this.kv = kv;
	}

	@Override
	public void characters(char[] chars, int start, int length) throws SAXException {
		// 將元素內容累加到StringBuffer中
		buf.append(chars, start, length);
	}

	@Override
	public void warning(SAXParseException exception) {
		System.out.println("*******WARNING******");
		System.out.println("\t行:\t" + exception.getLineNumber());
		System.out.println("\t列:\t" + exception.getColumnNumber());
		System.out.println("\t錯誤訊息:\t" + exception.getMessage());
		System.out.println("********************");
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		System.out.println("******* ERROR ******");
		System.out.println("\t行:\t" + exception.getLineNumber());
		System.out.println("\t列:\t" + exception.getColumnNumber());
		System.out.println("\t錯誤訊息:\t" + exception.getMessage());
		System.out.println("********************");
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		System.out.println("******** FATAL ERROR ********");
		System.out.println("\t行:\t" + exception.getLineNumber());
		System.out.println("\t列:\t" + exception.getColumnNumber());
		System.out.println("\t錯誤訊息:\t" + exception.getMessage());
		System.out.println("*****************************");
	}
}
