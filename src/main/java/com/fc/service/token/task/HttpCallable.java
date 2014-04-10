package com.fc.service.token.task;

import java.util.Map;
import java.util.concurrent.Callable;

import com.fc.util.JDKHttpClient;

/**
 * 用于异步提交http请求 的task
 * @author jun.bao
 * @since 2013年8月29日
 */
public class HttpCallable implements Callable<String> {

	private String requstURL;
	private Map<String, String> postMap;
	private String postString;
	/**
	 * 是否post请求,默认为false
	 */
	private boolean postMethod = false;

	public HttpCallable(String url, String info, boolean isPost) {
		super();
		this.postString = info;
		this.requstURL = url;
		postMethod = isPost;
	}

	public HttpCallable(String url, Map<String, String> info, boolean isPost) {
		super();
		this.postMap = info;
		this.requstURL = url;
		postMethod = isPost;
	}

	public String getRequstURL() {
		return requstURL;
	}

	public void setRequstURL(String requstURL) {
		this.requstURL = requstURL;
	}

	public Map<String, String> getPostMap() {
		return postMap;
	}

	public void setPostMap(Map<String, String> postMap) {
		this.postMap = postMap;
	}

	@Override
	public String call() throws Exception {
		if (postMethod) {
			if (postMap != null && !postMap.isEmpty()) {
				return JDKHttpClient.doPost(requstURL, postMap);
			} else {
				return JDKHttpClient.doPost(requstURL, postString);
			}
		} else {
			return JDKHttpClient.doGet(requstURL);
		}
	}

	public boolean isPostMethod() {
		return postMethod;
	}

	public void setPostMethod(boolean postMethod) {
		this.postMethod = postMethod;
	}

}
