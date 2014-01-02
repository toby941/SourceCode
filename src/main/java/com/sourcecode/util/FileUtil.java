package com.sourcecode.util;

import java.io.File;

/**
 * @author jun.bao
 * @since 2013年9月5日
 */
public class FileUtil {

	public static void delFile(String name, File f) {
		if (f.isFile() && f.getName().equals(name)) {
			System.out.println("delete file: " + f.getAbsolutePath());
			f.delete();
		}
		if (f.isDirectory()) {
			File[] subFile = f.listFiles();
			for (File sf : subFile) {
				if (sf.isDirectory()) {
					delFile(name, sf);
				} else if (sf.getName().equals(name)) {
					System.out.println("delete file: " + f.getAbsolutePath());
					sf.delete();
				}
			}
		}

	}

	public static void main(String[] args) {
		delFile("package.html", new File("D:/dev/tomcat6/apache-tomcat-6.0.37-src/java/"));
	}
}
