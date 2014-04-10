package com.fc.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * @author jun.bao
 * @since 2013年9月9日
 */
public class JAXBUtil {

	protected final static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory.getLog(JAXBUtil.class);

	/**
	 * 将生成的xml转换为对象
	 * @param zClass
	 *            转换为实例的对象类类型
	 * @param xmlPath
	 *            需要转换的xml路径
	 */
	public static Object xml2Bean(Class<?> zClass, String xml) {
		Object obj = null;
		JAXBContext context = null;
		InputStream iStream = null;
		if (null == xml || "".equals(xml) || "null".equalsIgnoreCase(xml) || xml.length() < 1)
			return obj;
		try {
			context = JAXBContext.newInstance(zClass);
			// if without "utf-8", Invalid byte 2 of 2-byte UTF-8 sequence.
			iStream = new ByteArrayInputStream(xml.getBytes("utf-8"));
			Unmarshaller um = context.createUnmarshaller();
			obj = um.unmarshal(iStream);
			return obj;
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (iStream != null) {
				try {
					iStream.close();
				} catch (IOException e) {
					log.error("close iStream error", e);
				}
			}
		}
		return obj;
	}

	public static String bean2Xml(Object bean) {
		String xmlString = null;
		JAXBContext context;
		StringWriter writer;
		if (null == bean)
			return xmlString;
		try {
			// 下面代码将对象转变为xml
			context = JAXBContext.newInstance(bean.getClass());
			Marshaller m = context.createMarshaller();
			writer = new StringWriter();
			m.marshal(bean, writer);
			xmlString = writer.toString();
			return xmlString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlString;
	}

}
