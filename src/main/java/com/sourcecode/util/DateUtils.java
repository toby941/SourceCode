package com.sourcecode.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jun.bao
 * @since 2013年9月24日
 */
public abstract class DateUtils {

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");

	/**
	 * @param dateStr 接受 YYYYMMddHH:mm:ss输入
	 * @return
	 */
	public static Date parseDate(String dateStr) {
		try {
			return simpleDateFormat.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String format(Date date) {
		return simpleDateFormat.format(date);
	}

	public static void main(String[] args) throws IOException {
		System.out.println(new Date(1384324966618L));
		// File f = new File("D:/tmp/fss/hbase-oracle-master-hadoop001.99bill.com.log");
		// List<String> list = FileUtils.readLines(f);
		// for (String s : list) {
		// if (s.indexOf("1383911196885") > 0) {
		// System.out.println(s);
		// }
		// }

	}
}
