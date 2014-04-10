package com.fc.util;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * 用于解析xml格式文件，提取关键key对应value的工具类
 * 
 * @author jun.bao
 * @since 2013年8月27日
 */
public class KeyFilter extends XMLFilterImpl {
	private String currentElement;

	private List<String> mapperkeys;

	public KeyFilter(XMLReader parent) {
		super(parent);
	}

	public KeyFilter(XMLReader parent, List keys) {
		super(parent);
		this.mapperkeys = keys;
	}

	/**
	 * 只提取mapperkeys中的元素
	 **/
	@Override
	public void startElement(String namespaceURI, String localName,
			String fullName, Attributes attributes) throws SAXException {

		currentElement = fullName;
		if (mapperkeys.contains(fullName)) {
			super.startElement(namespaceURI, localName, fullName, attributes);
		}
	}

	/**
	 * 只提取mapperkeys中的元素
	 **/
	@Override
	public void endElement(String namespaceURI, String localName,
			String fullName) throws SAXException {
		if (mapperkeys.contains(fullName)) {
			super.endElement(namespaceURI, localName, fullName);
		}
	}

	/**
	 * 读取元素内容
	 **/
	@Override
	public void characters(char[] buffer, int start, int length)
			throws SAXException {
		if (mapperkeys.contains(currentElement)) {
			super.characters(buffer, start, length);
		}
	}
}
