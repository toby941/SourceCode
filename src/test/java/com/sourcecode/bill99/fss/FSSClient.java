package com.sourcecode.bill99.fss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

/**
 * @author jun.bao
 * @since 2014年1月16日
 */
public class FSSClient {
	/**
	 * FSS download file URL,configured in nfs:fss-client.properties
	 */
	private String getURL;
	/**
	 * FSS upload file URL,configured in nfs:fss-client.properties
	 */
	private String uploadURL;

	private String listURL;

	private String fssServerSites;

	private List<String> fssServerSitesList;

	private List<String> badFssServerSitesList = Collections.synchronizedList(new ArrayList<String>());

	private int badFssServerSitesCheckInterval = 30; // minutes

	private int httpServerTimeOut = 1000;// secs

	public int getHttpServerTimeOut() {
		return httpServerTimeOut;
	}

	public void setHttpServerTimeOut(int httpServerTimeOut) {
		this.httpServerTimeOut = httpServerTimeOut;
	}

	public int getBadFssServerSitesCheckInterval() {
		return badFssServerSitesCheckInterval;
	}

	public void setBadFssServerSitesCheckInterval(int badFssServerSitesCheckInterval) {
		this.badFssServerSitesCheckInterval = badFssServerSitesCheckInterval;
	}

	public String getFssServerSites() {
		return fssServerSites;
	}

	public void setFssServerSites(String fssServerSites) {
		this.fssServerSites = fssServerSites;
		if (StringUtils.isBlank(fssServerSites))
			return;
		this.fssServerSitesList = new ArrayList<String>();
		String[] sites = fssServerSites.split(",");
		for (String site : sites) {
			this.fssServerSitesList.add(site.trim());
		}
	}

	public String getListURL() {
		return listURL;
	}

	public void setListURL(String listURL) {
		this.listURL = listURL;
	}

	public String getGetURL() {
		return getURL;
	}

	public void setGetURL(String getURL) {
		this.getURL = getURL;
	}

	public String getUploadURL() {
		return uploadURL;
	}

	public void setUploadURL(String uploadURL) {
		this.uploadURL = uploadURL;
	}

	private static Log log = LogFactory.getLog(FSSClient.class);

	public static void main(String[] args) throws Exception {
		FSSClient client = new FSSClient();
		client.setFssServerSites("http://192.168.120.160:8080/if-fss-server/");
		client.setGetURL("FileDownload");
		String appCode = "BIZ";
		String fileId = "large";
		for (int i = 0; i < 100000; i++) {
			String path = "D:/tmp/fss/jpg/1.jpg";
			if (i % 50 == 0) {
				System.out.println(i);
			}
			try {
				client.fssGetFile(appCode, fileId, path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从FSS下载文件，并且存储到本地路径
	 * @param appCode
	 *            调用者的应用的名字
	 * @param fileId
	 *            文件的id，在上传的时候生成，客户端需要在上传成功后保存这个id
	 * @param localFilePath
	 *            本地文件路径
	 * @throws Exception,如果文件找不到，会throw Exception
	 */
	public void fssGetFile(String appCode, String fileId, String localFilePath) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String url = null;
		try {
			url = this.getGetURL2();
			HttpGet httpGet = new HttpGet(url + "?appCode=" + appCode + "&fileId=" + fileId);
			BasicHttpParams params = new BasicHttpParams();

			httpGet.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, httpServerTimeOut);
			httpGet.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpServerTimeOut);
			HttpResponse response = httpClient.execute(httpGet);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					log.debug("get file type:" + entity.getContentType());
					log.debug("isStream:" + entity.isStreaming());
					File storeFile = new File(localFilePath);
					FileOutputStream output = null;
					try {
						output = new FileOutputStream(storeFile);
						InputStream input = entity.getContent();
						byte b[] = new byte[1024 * 10];
						int j = 0;
						while ((j = input.read(b)) != -1) {
							output.write(b, 0, j);
							Thread.sleep(1000L * 10);
						}
					} catch (IOException e1) {
						log.error("file not found for appCode:" + appCode + ",fileId:" + fileId, e1);
						throw new Exception("error get file->appCode:" + appCode + ",fileId:" + fileId, e1);
					} catch (Exception e2) {
						log.error("file not found for appCode:" + appCode + ",fileId:" + fileId, e2);
						throw new Exception("error get file->appCode:" + appCode + ",fileId:" + fileId, e2);
					} finally {
						if (output != null) {
							output.flush();
							output.close();
						}
					}
				}
				if (entity != null) {
					EntityUtils.consume(entity);
				}
			} else {
				log.info("file not found for appCode:" + appCode + ",fileId:" + fileId);
				throw new FileNotFoundException();
			}
		} catch (IOException e3) {
			this.processIOException(url, e3);
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * 从FSS下载文件，并且返回给web下载
	 * @param appCode
	 *            调用者的应用的名字
	 * @param fileId
	 *            文件的id，在上传的时候生成，客户端需要在上传成功后保存这个id
	 * @param resp
	 *            web请求的响应
	 * @throws Exception,如果文件找不到，会throw Exception
	 */
	public void fssGetFile(String appCode, String fileId, HttpServletResponse resp) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String url = null;
		try {
			url = this.getGetURL2();
			HttpGet httpGet = new HttpGet(url + "?appCode=" + appCode + "&fileId=" + fileId);
			httpGet.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, httpServerTimeOut);
			httpGet.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpServerTimeOut);
			HttpResponse response = httpClient.execute(httpGet);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					ServletOutputStream output = null;
					try {
						if (response.getAllHeaders() != null && response.getAllHeaders().length > 0) {
							Header[] hs = response.getAllHeaders();
							for (int i = 0; i < hs.length; i++) {
								resp.addHeader(hs[i].getName(), hs[i].getValue());
							}
							resp.setCharacterEncoding("utf-8");
						}
						output = resp.getOutputStream();
						InputStream input = entity.getContent();
						byte b[] = new byte[1024];
						int j = 0;
						while ((j = input.read(b)) != -1) {
							output.write(b, 0, j);
						}
					} catch (IOException e) {
						log.error("file not found for appCode:" + appCode + ",fileId:" + fileId, e);
						throw new Exception("error get file->appCode:" + appCode + ",fileId:" + fileId);
					} finally {
						if (output != null) {
							output.flush();
							output.close();
						}
					}
				}
				if (entity != null) {
					EntityUtils.consume(entity);
				}
			} else {
				log.error("file not found for appCode:" + appCode + ",fileId:" + fileId);
				throw new FileNotFoundException();
			}
		} catch (IOException e) {
			log.error(e);
			this.processIOException(url, e);
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
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
	public String fssUploadFile(String appCode, String localFilePath) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String fileId = UUID.randomUUID().toString();
		String url = null;
		try {
			url = this.getUploadURL2();
			HttpPost httppost = new HttpPost(url);
			httppost.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, httpServerTimeOut);
			httppost.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpServerTimeOut);
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
				log.info("FSSClient upload file successed,appCode:" + appCode + ",fileId:" + fileId);
			} else {
				log.error("FSS upload error");
				fileId = null;
				throw new Exception("FSS upload error");
			}
		} catch (IOException e) {
			this.processIOException(url, e);
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return fileId;
	}

	/**
	 * 把本地文件上传到FSS存储上
	 * @param appCode
	 *            调用者的应用的名字
	 * @param input
	 *            需要上传的文件流
	 * @param fileName
	 *            需要上传的文件名
	 * @return fileId
	 *         文件的Id，客户端需要存储这个id，以后下载该文件的时候需要这个id
	 * @throws Exception
	 */
	public String fssUploadFile(String appCode, InputStream input, String fileName) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String fileId = UUID.randomUUID().toString();
		String url = null;
		try {
			url = this.getUploadURL2();
			HttpPost httppost = new HttpPost(url);
			httppost.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, httpServerTimeOut);
			httppost.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpServerTimeOut);
			InputStreamBody bin = new InputStreamBody(input, fileId);
			StringBody strBodyAppCode = new StringBody(appCode);
			StringBody strBodyFileId = new StringBody(fileId);
			StringBody strBodyFileName = new StringBody(fileName, Charset.forName("UTF-8"));
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
				log.info("FSSClient upload file successed,appCode:" + appCode + ",fileId:" + fileId);
			} else {
				log.error("FSS upload error");
				fileId = null;
				throw new Exception("FSS upload error");
			}
		} catch (IOException e) {
			this.processIOException(url, e);
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return fileId;
	}

	/**
	 * 从多个FSS server中选择一个
	 * @return
	 * @throws IOException
	 */
	public String getFssServerSite() throws IOException {
		if (this.fssServerSitesList == null || this.fssServerSitesList.size() <= 0) {
			throw new IOException("Error get fss server site,please check the property file.");
		}
		/*
		 * do not check badserver by Yulong
		 * List<String> fssServerSitesListCopy = new ArrayList<String>(this.fssServerSitesList);
		 * if (this.badFssServerSitesList.size() > 0) {
		 * Iterator<String> i = fssServerSitesListCopy.iterator();
		 * String site = null;
		 * while (i.hasNext()) {
		 * site = i.next();
		 * if (this.badFssServerSitesList.contains(site)) {
		 * i.remove();
		 * }
		 * }
		 * }
		 * int n = fssServerSitesListCopy.size();
		 */
		int n = fssServerSitesList.size();
		if (n <= 0) {
			throw new IOException("Error get fss server site, no sites is available,please try later.");
		} else if (n == 1) {
			return fssServerSitesList.get(0);
		} else {
			int index = RandomUtils.nextInt(n);
			return fssServerSitesList.get(index);
		}
	}

	public String getGetURL2() throws Exception {
		String fssSite = this.getFssServerSite();
		return fssSite + this.getURL;
	}

	public String getUploadURL2() throws Exception {
		String fssSite = this.getFssServerSite();
		return fssSite + this.uploadURL;
	}

	public String getListURL2() throws Exception {
		String fssSite = this.getFssServerSite();
		return fssSite + this.listURL;
	}

	private void processIOException(String url, IOException ex) throws IOException {
		if (ex.getClass().getName().indexOf("ClientAbortException") >= 0) {// 取消下载时tomcat会报此错误
			// log.info("error:ClientAbortException",ex);
			return;
		}
		if (url != null) {
			this.badFssServerSitesList.add(url.substring(0, url.indexOf("/File")));
			throw ex;
		} else {
			throw ex;
		}
	}

	/**
	 * 定时检测FSS Server是否可用,no need do that now
	 */
	public void init() {
		/*
		 * Timer timer = new Timer();
		 * timer.schedule(new TimerTask() {
		 * @Override
		 * public void run() {
		 * String nowDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		 * log.info("start badFssServerSitesCheck(" + nowDateTime + ")...");
		 * HttpClient httpClient = new DefaultHttpClient();
		 * synchronized (badFssServerSitesList) {
		 * Iterator<String> i = badFssServerSitesList.iterator();
		 * String site = null;
		 * while (i.hasNext()) {
		 * site = i.next();
		 * HttpGet httpGet = new HttpGet(site + "/index.jsp");
		 * try {
		 * HttpResponse response = httpClient.execute(httpGet);
		 * if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
		 * i.remove();
		 * }
		 * } catch (Exception e) {
		 * log.warn("error check bad fssServer site:" + site);
		 * }
		 * }
		 * }
		 * log.info("end badFssServerSitesCheck(" + nowDateTime + ").");
		 * }
		 * }, 60 * 1000l, this.badFssServerSitesCheckInterval * 60 * 1000l);
		 */
	}
}