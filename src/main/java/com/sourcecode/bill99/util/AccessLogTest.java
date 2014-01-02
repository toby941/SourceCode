package com.sourcecode.bill99.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * @author jun.bao
 * @since 2013年10月31日
 */
public class AccessLogTest {
	// private static Pattern p = Pattern.compile(".*\\[(.{2}/.{3}/.{4}:.{2}:.{2}:.{2}).*");
	// private static Pattern p = Pattern.compile(".*\\[.{2}/.{3}/.{4}:(.{2}):.{2}:.{2}.*");
	private static Pattern p = Pattern.compile(".*\\[.{2}/.{3}/.{4}:04:(.{2}):.{2}.*");

	public static String getTime(String line) {
		Matcher m = p.matcher(line);
		while (m.find()) {
			return m.group(1);
		}
		return null;
	}

	// String line =
	// "172.16.50.176 - - [28/Oct/2013:07:04:02 +0800] GET /fss-server/FileDownload?appCode=common&fileId=1.txt HTTP/1.1 200 6";
	public static void main(String[] args) throws IOException {
		// String file = "D:\\tmp\\log\\access.2013-10-27.log";
		String file = "D:\\tmp\\log\\";
		File[] files = new File(file).listFiles();
		List<String> filesString = new ArrayList<String>();
		for (File f : files) {
			filesString.addAll(FileUtils.readLines(f));
		}
		Map<String, Integer> countMap = new TreeMap<String, Integer>(new Comparator<String>() {

			@Override
			public int compare(String k1, String k2) {
				return k1.compareTo(k2);
			}
		});
		for (String s : filesString) {
			String hour = getTime(s);
			if (hour != null) {
				Integer count = countMap.get(hour);
				if (count == null) {
					countMap.put(hour, 1);
				} else {
					count = count + 1;
					countMap.put(hour, count);
				}
			}
		}
		for (String key : countMap.keySet()) {
			System.out.println(key + ": " + countMap.get(key));
		}
	}
}
