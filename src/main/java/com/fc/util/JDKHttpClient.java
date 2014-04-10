package com.fc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * 封装get post请求工具类
 * @author jun.bao
 * @since 2013年8月29日
 */
public class JDKHttpClient {

	public static final Integer connectTimeout = 10000 * 5;
	public static final Integer readTimeout = 10000 * 8;
	protected final static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory
			.getLog(JDKHttpClient.class);

	/**
	 * @param requestURL
	 * @param timeout
	 *            调用者指定请求超时时间
	 * @return 请求内容字符串
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
			connection.setDoOutput(false);
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(readTimeout);
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
			connection.setRequestProperty("Accept", "*/*");
			// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
			// 服务器
			connection.connect();
			int statusCode = connection.getResponseCode();
			if (HttpServletResponse.SC_OK == statusCode) {
				// 取得输入流，并使用Reader读取
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String lines;
				while ((lines = reader.readLine()) != null) {
					sb.append(lines);
				}
				return sb.toString();
			} else {
				log.debug("doGet with url: " + requestURL + " error code:" + statusCode);
				return null;
			}

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
	 * @return 请求内容字符串
	 * @throws IOException
	 */
	public static String doGet(String requestURL) {
		return doGet(requestURL, connectTimeout);
	}

	public static String doPost(String postURL, String content, Integer timeout) {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		HttpURLConnection connection = null;
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
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(readTimeout);
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
			out.writeBytes(content);

			out.flush();
			out.close(); // flush and close
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			log.error("doPost with url: " + postURL + " content: " + content, e);
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

	public static String doPost(String postURL, String content) {
		return doPost(postURL, content, connectTimeout);
	}

	public static String doPost(String postURL, Map<String, String> paramaters) {

		StringBuffer contentBuffer = new StringBuffer();
		try {
			for (String key : paramaters.keySet()) {
				contentBuffer.append(key + "=" + URLEncoder.encode(paramaters.get(key), "utf-8") + "&");
			}
			return doPost(postURL, contentBuffer.toString(), connectTimeout);
		} catch (UnsupportedEncodingException e) {
			log.error("doPost with url: " + postURL + " content: " + contentBuffer.toString(), e);
			return null;
		}
	}

	// /**
	// * @param args
	// */
	public static void main(String[] args) {
		String s = doGet("http://192.168.126.170:9083/fc-server/getconfig?name=/limitdemo");
		System.out.println(s);
	}
}
