package com.fc.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * 支持多层级路径属性获取的XMLFilterImpl实现,对形如 a.b.c的查询路径，会依次遍历a节点下的b节点下的c节点的value值
 * @author jun.bao
 * @since 2013年10月22日
 */
public class ChildNodeKeyFilter extends XMLFilterImpl {

	private String currentElement;

	private List<KeyNode> mapperkeys;

	private KeyNode currentKeyNode;
	private List<String> nameLinkNode;
	private String startName;
	private String endName;

	public ChildNodeKeyFilter(XMLReader parent) {
		super(parent);
	}

	public ChildNodeKeyFilter(XMLReader parent, List<String> keys) {
		super(parent);
		mapperkeys = new ArrayList<ChildNodeKeyFilter.KeyNode>();
		for (String s : keys) {
			KeyNode node = new KeyNode(s);
			mapperkeys.add(node);
		}

	}

	private KeyNode getCurrentKeyNode(String fullName) {

		for (KeyNode kn : mapperkeys) {
			if (kn.isFirst(fullName)) {
				return kn;
			}
		}
		return null;
	}

	/**
	 * 只提取mapperkeys中的元素
	 **/
	@Override
	public void startElement(String namespaceURI, String localName, String fullName, Attributes attributes)
			throws SAXException {

		currentElement = fullName;

		if (currentKeyNode == null) {
			currentKeyNode = getCurrentKeyNode(fullName);
		}
		if (currentKeyNode != null && currentKeyNode.isMatch(fullName)) {
			super.startElement(namespaceURI, localName, fullName, attributes);
		}
	}

	/**
	 * 只提取mapperkeys中的元素
	 **/
	@Override
	public void endElement(String namespaceURI, String localName, String fullName) throws SAXException {

		currentElement = fullName;
		if (currentKeyNode != null && currentKeyNode.isEnd(fullName)) {
			super.endElement(namespaceURI, localName, fullName);
		}

	}

	/**
	 * 读取元素内容
	 **/
	@Override
	public void characters(char[] buffer, int start, int length) throws SAXException {
		if (currentKeyNode != null && currentKeyNode.getEnd().equals(currentElement)) {
			super.characters(buffer, start, length);
		}
	}

	public class KeyNode {
		private String keys[];

		public boolean isEnd(String matchKey) {
			return keys[keys.length - 1].equals(matchKey);
		}

		public boolean isFirst(String matchKey) {
			return keys[0].equals(matchKey);
		}

		public String getEnd() {
			return keys[keys.length - 1];
		}

		public boolean isMatch(String matchKey) {
			for (String key : keys) {
				if (key.equals(matchKey)) {
					return true;
				}
			}
			return false;
		}

		public KeyNode() {
			super();
		}

		public KeyNode(String keyStr) {
			super();
			keys = keyStr.split("\\.");
		}
	}

}
