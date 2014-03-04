package com.sourcecode.bill99.fss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @author jun.bao
 * @since 2014年1月17日
 */
public class FileHandle {

	private final static String tmp_path = "D:/tmp/file/";

	/**
	 * 只创建文件，对文件无写入修改操作 不占用文件句柄
	 * 文件修改后，资源不释放会占用句柄，windows句柄数极限测试是80000左右，超过此值系统报错
	 * 系统资源不足，无法完成请求的服务。进程强制退出
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		FileHandle fileHandle = new FileHandle();
		fileHandle.open(1000 * 1000 * 10000);
	}

	public void open(int count) throws IOException {
		List<File> files = new ArrayList<File>();
		List<OutputStream> oss = new ArrayList<OutputStream>();
		for (int i = 0; i < count; i++) {
			File f = new File(tmp_path + i + ".txt");
			OutputStream os = new FileOutputStream(f, true);
			oss.add(os);
			IOUtils.write("s", os);
			files.add(f);
			if (i % 500000 == 0 && i > 10) {
				System.out.println(files.get(i - 4000).exists());
			}
		}
	}
}
