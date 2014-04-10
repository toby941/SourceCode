package com.sourcecode.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * @author jun.bao
 * @since 2014年3月27日
 */
public class RandomReadFileUtils {

	public static void read(String path, String newPath) throws IOException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(new File(path), "r");
		RandomAccessFile writeFile = new RandomAccessFile(new File(newPath), "rw");
		// 重新把文件指针定位到开始处
		randomAccessFile.seek(0);

		// 使用readLine读取文件内容,每个字节的值被转换为字符的低8位,而字符的高8位被赋予0.因此这个方法不支持unicode字符集.
		String content = randomAccessFile.readLine();
		while (content != null) {
			String s = readCardNo(content);
			if (s != null) {
				writeFile.writeBytes(s + "\r\n");
			}
			content = randomAccessFile.readLine();
		}
		randomAccessFile.close();
		writeFile.close();
	}

	public static String readCardNo(String line) {
		Matcher m = p.matcher(line);
		if (m.matches()) {
			return m.group(1) + "," + m.group(2) + "," + m.group(3);
		}
		return null;
	}

	private static Pattern p = Pattern
			.compile(".*<cardNo>(.*)</cardNo.*expiredDate>(.*)</expiredDate.*cvv2>(.*)</cvv2.*");

	public static void trim(String from, String to) throws IOException {

		File f = new File(from);
		File t = new File(to);

		List<String> list = FileUtils.readLines(f, "UTF-8");
		for (String s : list) {
			String trim = s.trim() + "\r\n";
			FileUtils.writeStringToFile(t, trim, true);
		}

	}

	public static void distinct(String from, String to) throws IOException {
		File f = new File(from);
		File t = new File(to);

		List<String> list = FileUtils.readLines(f, "UTF-8");
		Set<String> set = new HashSet<String>();
		for (String s : list) {
			String trim = s.trim();
			set.add(trim);
		}
		for (String s : set) {
			FileUtils.writeStringToFile(t, s + "\r\n", true);
		}

	}

	public static void main(String[] args) throws IOException {
		String s = "D:\\chromedown\\1395898315_3545.txt";
		String s1 = "D:\\tmp\\card_2.txt";
		String distinctS = "D:\\tmp\\card_3.txt";
		// read(s, s1);
		distinct(s1, distinctS);
		// String s = "D:\\tmp\\card_trim.txt";
		// String s1 = "D:\\tmp\\card_distinct.txt";
		// distinct(s, s1);
		// File f = new File(s1);
		// List<String> list = FileUtils.readLines(f);
		// FileSerializable serializable = new FileSerializable();
		// serializable.setList(list);
		// ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("D:\\tmp\\card_distinct.obj"));
		// out.writeObject(serializable);
		// out.close();
	}
}
