package com.sourcecode.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpUtils {

    private final static Logger log = Logger.getLogger(HttpUtils.class);

    /**
     * 以流形式获取web资源，图片内容二进制获取
     * 
     * @param httpUrl
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static byte[] getResponse(String httpUrl) throws ClientProtocolException, IOException {
        try {
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            HttpGet httpGet = new HttpGet(httpUrl);
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            HttpResponse response = httpclient.execute(httpGet);
            byte[] content = EntityUtils.toByteArray(response.getEntity());
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpclient);
            return content;
        }
        catch (Exception e) {
            log.error("getResponse error", e);
            return null;
        }
    }

    public static String doGetRequest(String url) throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpGet);
        return EntityUtils.toString(response.getEntity());
    }

    public static String doPostRequest(String url) throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity =
                new StringEntity(
                        "<xml><ToUserName><![CDATA[toUser]]></ToUserName><FromUserName><![CDATA[fromUser]]></FromUserName> <CreateTime>1348831860</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[this is a test]]></Content><MsgId>1234567890123456</MsgId></xml>");
        httpPost.setEntity(entity);
        HttpResponse response = httpclient.execute(httpPost);
        return EntityUtils.toString(response.getEntity());
    }

    public static void main(String[] args) throws IOException {
        // String url = "http://img6.cache.netease.com/cnews/2012/11/6/20121106100038534e0.jpg";
        String url = "http://127.0.0.1:9091/api/wx";
        // byte[] content = getResponse(url);
        // FileUtils.writeByteArrayToFile(new File("D:\\tmp\\xxx.png"), content);
        System.out.println(doPostRequest(url));
    }
}
