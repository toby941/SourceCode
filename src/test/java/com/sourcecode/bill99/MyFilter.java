package com.sourcecode.bill99;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * @author jun.bao
 * @since 2013年8月27日
 */
public class MyFilter extends XMLFilterImpl {
	private String currentElement;

	private List<String> mapperkeys;

	public MyFilter(XMLReader parent) {
		super(parent);
	}

	public MyFilter(XMLReader parent, List keys) {
		super(parent);
		this.mapperkeys = keys;
	}

	/**
	 * 过滤掉不在mapperkeys 中的元素
	 **/
	public void startElement(String namespaceURI, String localName, String fullName, Attributes attributes) throws SAXException {

		currentElement = fullName;
		// System.out.println("** " + fullName);
		if (mapperkeys.contains(fullName)) {
			super.startElement(namespaceURI, localName, fullName, attributes);
		}
	}

	/**
	 * 過濾掉元素<技術書籍>的結束事件
	 **/
	public void endElement(String namespaceURI, String localName, String fullName) throws SAXException {
		if (mapperkeys.contains(fullName)) {
			super.endElement(namespaceURI, localName, fullName);
		}
	}

	/**
	 * 過濾掉元素<技術書籍>中的內容
	 **/
	public void characters(char[] buffer, int start, int length) throws SAXException {
		if (mapperkeys.contains(currentElement)) {
			super.characters(buffer, start, length);
		}
	}
}
