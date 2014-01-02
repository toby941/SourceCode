package com.sourcecode.bill99;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.mina.util.ConcurrentHashSet;
import org.junit.Test;

import com.sourcecode.util.HttpUtils;

/**
 * @author jun.bao
 * @since 2013年11月19日
 */
public class FssFileDownloadTest {

	private static String upload_url = "http://192.168.65.60:8085/fss-server/FileUpload";
	// private static String upload_url = "http://192.168.51.100:8083/if-fss-server/FileUpload";
	// private static String download_url =
	// "http://192.168.65.60:8085/fss-server/FileDownload?appCode=common&fileId=00006448-2013-1112-1800-1001800000728";

	private static String download_url = "http://192.168.51.100:8083/if-fss-server/FileDownload?appCode=common&fileId=44446448-2013-1112-1800-1001800000728";
	private static AtomicInteger i = new AtomicInteger(0);
	private static final String UID = UUID.randomUUID().toString().replace('-', '_');

	private static ConcurrentHashSet<String> set = new ConcurrentHashSet<String>();

	public static void addUUID() {
		// String tempFileName = String.format("upload_%s_%s.tmp", new Object[] { UID, getUniqueId() });
		// System.out.println(getUniqueId());
		String uuid = getUniqueId();
		// if (set.contains(uuid)) {
		// System.out.println(uuid);
		// }
		set.add(uuid);
	}

	private static final AtomicInteger COUNTER = new AtomicInteger(0);

	private static String getUniqueId() {
		final int limit = 100000000;
		int current = COUNTER.getAndIncrement();
		String id = Integer.toString(current);
		// If you manage to get more than 100 million of ids, you'll
		// start getting ids longer than 8 characters.
		if (current < limit) {
			id = ("00000000" + id).substring(id.length());
		}
		return id;
	}

	// @Test
	public void testUUID() {
		TestRunnable runner = new TestRunnable() {
			@Override
			public void runTest() throws Throwable {
				while (true) {
					addUUID();
					if (i.getAndIncrement() % 10000 == 0) {
						System.out.println("count: " + i.get() + "size: " + set.size());
					}
				}
			}
		};

		int runnerCount = 20;
		// Rnner数组，相当于并发多少个。
		TestRunnable[] trs = new TestRunnable[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			trs[i] = runner;
		}
		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		try {
			// 开发并发执行数组里定义的内容
			mttr.runTestRunnables();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testUpload() throws Exception {
		final String appCode = "common";
		final String filePath = "D:/tmp/xls/1.txt";

		TestRunnable runner = new TestRunnable() {
			@Override
			public void runTest() throws Throwable {
				while (true) {
					if (i.get() > 50000) {
						return;
					}

					int s = RandomUtils.nextInt(10) + 1;
					Thread.sleep(s * 100L);
					String fileId = fssUploadFile(appCode, filePath);
					if (i.get() % 100 == 0) {
						System.out.println(i);
						System.out.println(fileId);
					}
					i.addAndGet(1);
				}
			}
		};

		int runnerCount = 10;
		// Rnner数组，相当于并发多少个。
		TestRunnable[] trs = new TestRunnable[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			trs[i] = runner;
		}
		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		try {
			// 开发并发执行数组里定义的内容
			mttr.runTestRunnables();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	/**
	 * 把本地文件上传到FSS存储上
	 * @param appCode
	 *            调用者的应用的名字
	 * @param localFilePath
	 *            本地文件路径
	 * @return fileId
	 *         文件的Id，客户端需要存储这个id，以后下载该文件的时候需要这个id
	 * @throws Exception
	 */

	public static String fssUploadFile(String appCode, String localFilePath) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		String fileId = UUID.randomUUID().toString();
		String url = null;
		try {
			url = upload_url;
			HttpPost httppost = new HttpPost(url);
			httppost.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			httppost.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 50000);
			FileBody bin = new FileBody(new File(localFilePath));
			StringBody strBodyAppCode = new StringBody(appCode);
			StringBody strBodyFileId = new StringBody(fileId);
			StringBody strBodyFileName = new StringBody(localFilePath.substring(localFilePath.replaceAll("\\\\", "/")
					.lastIndexOf("/") + 1), Charset.forName("UTF-8"));
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("bin", bin);
			reqEntity.addPart("appCode", strBodyAppCode);
			reqEntity.addPart("fileId", strBodyFileId);
			reqEntity.addPart("fileName", strBodyFileName);
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			StatusLine status = response.getStatusLine();
			EntityUtils.consume(resEntity);
			if (status != null && status.getStatusCode() == HttpStatus.SC_OK) {
				// System.out.println("FSSClient upload file successed,appCode:" + appCode + ",fileId:" + fileId);
			} else {
				System.out.println("FSS upload error" + status.getReasonPhrase() + " " + status.getStatusCode());
				fileId = null;
				throw new Exception("FSS upload error");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return fileId;
	}

	// @Test
	public void test() throws ClientProtocolException, IOException {

		TestRunnable runner = new TestRunnable() {
			@Override
			public void runTest() throws Throwable {
				while (true) {
					if (i.get() > 50000) {
						return;
					}
					if (i.get() % 100 == 0) {
						System.out.println(i);
					}
					i.addAndGet(1);
					int s = RandomUtils.nextInt(10) + 1;
					Thread.sleep(s * 10L);
					File f = new File("D:/tmp/xls/" + UUID.randomUUID().toString() + ".txt");
					try {
						FileUtils.writeByteArrayToFile(f, HttpUtils.getResponseUsePost(download_url));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		int runnerCount = 20;
		// Rnner数组，相当于并发多少个。
		TestRunnable[] trs = new TestRunnable[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			trs[i] = runner;
		}
		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		try {
			// 开发并发执行数组里定义的内容
			mttr.runTestRunnables();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
}
