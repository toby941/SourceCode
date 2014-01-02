package com.bill99.golden.inf.hbase.util;

public class StringUtil {

	public static String replaceSlashNToBr(String source) {
		if (null != source && source.length() > 0)
			return source.replaceAll("\n", "<br/>");
		else
			return source;
	}

	public static void main(String[] args) {
		String str = "@124new\nmanhao";
		System.out.println(replaceSlashNToBr(str));
	}
}
