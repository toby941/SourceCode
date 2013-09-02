package com.sourcecode.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author jun.bao
 * @since 2013年8月29日
 */
public class JDKHttpClient {
	public static final String GET_URL = "http://localhost:8080/welcome1";

	public static final String POST_URL = "http://localhost:8080/welcome1";

	public static final Integer connectTimeout = 1000 * 6;
	public static final Integer readTimeout = 1000 * 10;

	/**
	 * 
	 * @param getURL
	 * @return
	 * @throws IOException
	 */
	public static String doGet(String getURL) {
		// 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		try {
			URL getUrl = new URL(getURL);
			// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
			// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
			connection = (HttpURLConnection) getUrl.openConnection();
			connection.setConnectTimeout(connectTimeout);
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
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				// 断开连接
				connection = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static String doPost(String postURL, Map<String, String> paramaters) {
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
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			StringBuffer contentBuffer = new StringBuffer();
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
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				// 断开连接
				connection = null;
			} catch (final IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
}
