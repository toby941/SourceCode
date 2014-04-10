package com.fc.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author jun.bao
 * @since 2013年8月22日
 */
public class IOUtils {

	public static String convertByteToString(byte[] b) {
		return new String(b);
	}

	/**
	 * 将string字符串转化为InputStream
	 * @param input
	 * @return
	 */
	public static InputStream toInputStream(String input) {
		byte[] bytes = input.getBytes();
		return new ByteArrayInputStream(bytes);
	}

	/**
	 * 将 {@link InputStream}转化为 {@link String}对象输出
	 * @param is
	 * @return {@link String}字符串
	 * @throws IOException
	 */
	public static String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				// is.close();

			}
			return writer.toString();
		} else {
			return "";
		}
	}

}
