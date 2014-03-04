package com.bill99.golden.inf.hbase.util;

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
import org.apache.commons.lang.StringUtils;

/**
 * @author jun.bao
 * @since 2013年10月31日
 */
public class AccessLogTest {
	// private static Pattern p = Pattern.compile(".*\\[.{2}/.{3}/.{4}:04:(.{2}):.{2}.*");\
	// 下载时间统计 按小时
	private static Pattern RegexHour = Pattern.compile(".*\\[.{2}/.{3}/.{4}:(.{2}).*");

	// 下载时间统计 按天
	private static Pattern p = Pattern.compile(".*\\[(.{2})/.{3}/.{4}.*");

	// 下载时间统计 按天
	private static Pattern RegexDay = Pattern.compile(".*\\[(.{2})/.{3}/.{4}.*");

	// 文件大小 字节单位
	private static Pattern fileSize = Pattern.compile(".*\\[.{2}/.{3}/.*HTTP/1.1 200\\s{1}(\\d+).*");
	// ip
	private static Pattern RegexIp = Pattern.compile("(.*)\\s- -.*");

	public static String getTime(String line) {
		Matcher m = p.matcher(line);
		while (m.find()) {
			return m.group(1);
		}
		return null;
	}

	public static String getFirstGroup(String line, Pattern p) {
		Matcher m = p.matcher(line);
		while (m.find()) {
			return m.group(1);
		}
		return null;

	}

	public static void readFileSize(List<String> filesString) {
		Map<String, Integer> countMap = new TreeMap<String, Integer>();
		for (String s : filesString) {
			if (s.indexOf("GET") > 0 && s.indexOf("common&fileId=1.txt") <= 0) {
				String sizeStr = getFirstGroup(s, fileSize);
				if (sizeStr != null) {
					Integer size = Integer.valueOf(sizeStr);
					String key = " ";
					Integer fileSizeKb = size / 1024;
					if (fileSizeKb <= 5) {
						key = "0-5k";
					} else if (fileSizeKb > 5 && fileSizeKb <= 100) {
						key = "5-100k";
					} else if (fileSizeKb > 100 && fileSizeKb <= 500) {
						key = "100-500k";
					} else {
						key = "500k+";
					}
					Integer count = countMap.get(key);
					if (count == null) {
						countMap.put(key, 1);
					} else {
						count = count + 1;
						countMap.put(key, count);
					}
				} else {
					System.out.println(s);
				}
			}
		}

		for (String key : countMap.keySet()) {
			System.out.println(key + "	" + countMap.get(key));
		}
	}

	public static void readByRegex(List<String> filesString, Pattern regex, String include, String except) {
		Map<String, Integer> countMap = new TreeMap<String, Integer>(new Comparator<String>() {

			@Override
			public int compare(String k1, String k2) {
				return k1.compareTo(k2);
			}
		});
		for (String s : filesString) {
			if (StringUtils.isNotBlank(include)) {
				if (s.indexOf(include) <= 0) {
					continue;
				}
			}
			if (StringUtils.isNotBlank(except)) {
				if (s.indexOf(except) > 0) {
					continue;
				}
			}
			String ip = getFirstGroup(s, regex);
			if (ip != null) {
				Integer count = countMap.get(ip);
				if (count == null) {
					countMap.put(ip, 1);
				} else {
					count = count + 1;
					countMap.put(ip, count);
				}
			}
		}
		for (String key : countMap.keySet()) {
			System.out.println(key + "	" + countMap.get(key));
		}
	}

	public static void readLine(List<String> filesString) {
		Map<String, Integer> countMap = new TreeMap<String, Integer>(new Comparator<String>() {

			@Override
			public int compare(String k1, String k2) {
				return k1.compareTo(k2);
			}
		});
		for (String s : filesString) {
			if (s.indexOf("POST") > 0) {
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
		}
		for (String key : countMap.keySet()) {
			System.out.println(key + "	" + countMap.get(key));
		}
	}

	// String line =
	// "172.16.50.176 - - [28/Oct/2013:07:04:02 +0800] GET /fss-server/FileDownload?appCode=common&fileId=1.txt HTTP/1.1 200 6";
	public static void main(String[] args) throws IOException {
		// String file = "D:\\tmp\\log\\access.2013-10-27.log";
		String file = "D:\\tmp\\fss-20131121\\2014\\4";
		File[] files = new File(file).listFiles();
		List<String> filesString = new ArrayList<String>();
		for (File f : files) {
			readByRegex(FileUtils.readLines(f), RegexHour, "GET", "fileId=1.txt");
			// filesString.addAll(FileUtils.readLines(f));
		}
		// readLine(filesString);
		// readByRegex(filesString, RegexIp);
		// readByRegex(filesString, RegexHour, null, "fileId=1.txt");

	}
}