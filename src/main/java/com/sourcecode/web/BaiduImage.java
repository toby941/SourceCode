package com.sourcecode.web;

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class BaiduImage {
	public final static Integer HTTP_RESPONSE_STATUS_SUCCESS_CODE = 200;
	static String requestUrl = "http://image.baidu.com/i?ct=201326592&lm=-1&tn=baiduimagenojs&pv=&word=MM&z=19&pn=0&rn=20&cl=2&width=480&height=800";
	public static String getImg() throws Exception {
		String urlString = requestUrl;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(urlString);
		HttpResponse response = httpclient.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		if (HTTP_RESPONSE_STATUS_SUCCESS_CODE
				.equals(statusLine.getStatusCode())) {
			String result = EntityUtils.toString(response.getEntity());
			return result;
		} else {
			return StringUtils.EMPTY;
		}
	}

	public static Object[] cleaner() throws Exception {
		CleanerProperties props = new CleanerProperties();

		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);

		// do parsing
		TagNode tagNode = new HtmlCleaner(props).clean(new URL(requestUrl));
		Object[] ns = tagNode.evaluateXPath("//td/a");
		// TagNode[] tagNodes = tagNode.getElementsByName("a", true);
		return ns;

	}
	public static void main(String[] args) throws Exception {
		String result = getImg();
		// System.out.println(result);
		Object[] nodes = cleaner();
		for (int i = 0; i < nodes.length; i++) {
			System.out.println(((TagNode) nodes[i]).getAttributeByName("href"));
		}
	}
}
