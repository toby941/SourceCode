package com.sourcecode.web;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class GoogleImage {

	public static String getHTML(String url) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.106 Safari/535.2");
		httpGet.addHeader(
				"Referer",
				"http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&biw=1399&bih=725&tbs=isz%3Aex%2Ciszw%3A480%2Ciszh%3A800&tbm=isch&sa=1&q=MM+%E5%A3%81%E7%BA%B8&oq=MM+%E5%A3%81%E7%BA%B8&aq=f&aqi=&aql=&gs_l=img.3...680499l683087l0l683551l5l5l0l0l0l0l0l0ll0l0.frgbld.");
		HttpResponse response = httpclient.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		if (200 == statusLine.getStatusCode()) {
			String result = EntityUtils.toString(response.getEntity());
			return result;
		} else {
			return "";
		}
	}

	public static void main(String[] args) {
		List<String> imgList1 = getImgListByAjax("美女", 800, 480, 1, 22);
		List<String> imgList2 = getImgListByAjax("美女", 800, 480, 2, 76);

		for (String s : imgList2) {
			System.out.println(s);
		}
	}

	private static String GOOGLE_AJAX_URL_TEMPLATE = "http://www.google.com.hk/search?q={0}&hl=zh-CN&safe=strict&sa=X&gbv=2&tbs=isz:ex,iszw:{1},iszh:{2}&biw=1399&bih=347&tbm=isch&ijn=1&sprg={3}&page={4}&start={5}&ijn={6}";
	private static String SS = "http://www.google.com.hk/search?q={0}&hl=zh-CN&newwindow=1&safe=strict&sa=X&gbv=2&tbs=isz:ex,iszw:{1},iszh:{2}&biw=1869&bih=485&tbm=isch&ei=BNelT4HnGuuXiQft9pmqAw&sprg={3}&page={4}&start={5}&ijn={6}";
	private static String a = "http://www.google.com.hk/search?q=MM&hl=zh-CN&newwindow=1&safe=strict&sa=X&gbv=2&tbs=isz:ex,iszw:960,iszh:800&biw=1869&bih=485&tbm=isch&ijn=1&ei=BNelT4HnGuuXiQft9pmqAw&sprg=1&page=1&start=22";
	private static String b = "http://www.google.com.hk/search?q=MM&hl=zh-CN&newwindow=1&safe=strict&sa=X&gbv=2&tbs=isz:ex,iszw:960,iszh:800&biw=1869&bih=485&tbm=isch&ijn=2&ei=BNelT4HnGuuXiQft9pmqAw&sprg=3&page=4&start=76";
	private static String c = "http://www.google.com.hk/search?q=MM&hl=zh-CN&newwindow=1&safe=strict&sa=X&gbv=2&tbs=isz:ex,iszw:800,iszh:480&biw=1399&bih=347&tbm=isch&ijn=1&sprg=1&page=2&start=0";

	private static Pattern googleScriptImgRegex = Pattern
			.compile(".*imgurl=(.*.[png|jpg|jpge])&amp.*data-src=\"(.*)\" height.*");

	public static Map<String, String> getImgByAjaxUrl(String keyword,
			Integer width, Integer height, Integer page, Integer start) {
		String requestUrl = MessageFormat.format(GOOGLE_AJAX_URL_TEMPLATE,
				keyword, width, height, page, page + 1, start, page);
		System.out.println(requestUrl);
		Map<String, String> imageMap = new HashMap<String, String>();
		try {
			String response = getHTML(requestUrl).trim();
			String[] imageDivs = response.split("a href");
			for (String imageDiv : imageDivs) {
				try {
					Matcher m = googleScriptImgRegex.matcher(imageDiv);
					if (m.matches() && m.groupCount() == 2) {
						imageMap.put(m.group(1).trim(), m.group(2).trim());
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			return imageMap;
		}
		return imageMap;
	}
	public static List<String> getImgListByAjax(String keyword, Integer width,
			Integer height, Integer page, Integer start) {
		Map<String, String> imgMaps = getImgByAjaxUrl(keyword, width, height,
				page, start);
		List<String> imgList = new ArrayList<String>();
		if (imgMaps != null && imgMaps.size() > 0) {
			for (String key : imgMaps.keySet()) {
				String urlString = imgMaps.get(key);
				imgList.add(urlString);
			}
		}
		System.out.println(imgList.get(0));
		System.out.println(imgList.size());
		return imgList;
	}
}
