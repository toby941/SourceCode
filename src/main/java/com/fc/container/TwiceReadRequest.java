package com.fc.container;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;

import org.apache.catalina.connector.CoyoteAdapter;
import org.apache.catalina.connector.Request;

/**
 * {@link Request}包装类，用于实现对于 {@link Request#getInputStream()}的两次读取功能 在
 * {@link CoyoteAdapter#service(org.apache.coyote.Request, org.apache.coyote.Response)} 方法中需提前获取
 * {@link Request#getInputStream()}解析请求参数用于判断限流优先级
 * @author jun.bao
 * @since 2013年8月22日
 */
public class TwiceReadRequest extends Request {

	private String body;
	protected final static org.apache.juli.logging.Log logger = org.apache.juli.logging.LogFactory
			.getLog(TwiceReadRequest.class);

	public TwiceReadRequest(Request request) {
		super();
		this.coyoteRequest = request.getCoyoteRequest();
		this.connector = request.getConnector();
		inputBuffer.setRequest(coyoteRequest);
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			logger.error("Error reading the request body...");
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					logger.error("Error closing bufferedReader...");
				}
			}
		}
		// 用body字符串存储inputStream
		body = stringBuilder.toString();
	}

	/**
	 * 将body字符串转化为inputStream返回,此处body的内容可延迟到第一次执行getInputStream方法时在赋值，延迟赋值，提高性能
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream inputStream = new ServletInputStream() {
			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
		return inputStream;
	}

	public TwiceReadRequest() {
		super();
		body = "";
	}

	@Override
	public InputStream getStream() {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream inputStream = new ServletInputStream() {
			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
		return inputStream;
	}

	/**
	 * request请求重用时，用新的inputStream填充body
	 */
	public void resetBody() {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			// InputStream inputStream = new CoyoteInputStream(inputBuffer);
			// CoyoteInputStream构造方法不可见 需改造
			InputStream inputStream = null;
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

				char[] charBuffer = new char[128];
				int bytesRead = -1;

				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			logger.error("Error reading the request body...");
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					logger.error("Error closing bufferedReader...");
				}
			}
		}

		body = stringBuilder.toString();
	}
}
