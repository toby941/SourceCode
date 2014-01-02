package com.sourcecode.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 封装get post请求工具类
 * @author jun.bao
 * @since 2013年8月29日
 */
public class JDKHttpClient {

	public static final Integer connectTimeout = 1000 * 5;
	public static final Integer readTimeout = 1000 * 10;
	protected static Log log = LogFactory.getLog(JDKHttpClient.class);

	/**
	 * @param requestURL
	 * @param timeout
	 *            调用者指定请求超时时间
	 * @return
	 */
	public static String doGet(String requestURL, Integer timeout) {
		// 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		try {
			URL getUrl = new URL(requestURL);
			// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
			// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
			connection = (HttpURLConnection) getUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(readTimeout);
			// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
			// 服务器
			connection.connect();
			// 取得输入流，并使用Reader读取
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			while ((lines = reader.readLine()) != null) {
				sb.append(lines);
			}
			return sb.toString();
		} catch (IOException e) {
			log.error("doGet with url: " + requestURL, e);
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				connection = null;
			} catch (IOException e) {
				log.error("finally close reader error", e);
			}
		}
	}

	/**
	 * @param getURL
	 * @return
	 * @throws IOException
	 */
	public static String doGet(String requestURL) {
		return doGet(requestURL, connectTimeout);
	}

	public static String doPost(String postURL, Map<String, String> paramaters, Integer timeout) {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		StringBuffer contentBuffer = new StringBuffer();
		try {
			URL postUrl = new URL(postURL);
			connection = (HttpURLConnection) postUrl.openConnection();
			// 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// Set the post method. Default is GET
			connection.setRequestMethod("POST");
			// Post 请求不能使用缓存
			connection.setUseCaches(false);
			/*
			 * application/x-www-form-urlencoded：窗体数据被编码为名称/值对。这是标准的编码格式。abv=124234&test=hello& <br/>
			 * multipart/form-data： 窗体数据被编码为一条消息，页上的每个控件对应消息中的一个部分。<br/>
			 * text/plain： 窗体数据以纯文本形式进行编码，其中不含任何控件或格式字符。
			 */
			connection.setRequestProperty("Content-Type", "multipart/form-data");
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(readTimeout);
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			for (String key : paramaters.keySet()) {
				contentBuffer.append(key + "=" + URLEncoder.encode(paramaters.get(key), "utf-8") + "&");
			}
			// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
			out.writeBytes(contentBuffer.toString());

			out.flush();
			out.close(); // flush and close
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			log.error("doPost with url: " + postURL + " content: " + contentBuffer.toString(), e);
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				connection = null;
			} catch (IOException e) {
				log.error("finally close reader error", e);
			}

		}
	}

	public static String doPost(String postURL, Map<String, String> paramaters) {
		return doPost(postURL, paramaters, connectTimeout);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, String> p = new HashMap<String, String>();
		p.put("test", "hello");
		p.put("abv", "124234");

		String s = doPost("http://localhost:8080/demo/", p);
		System.out.println(s);
	}
}
