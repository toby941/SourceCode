package com.sourcecode.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class ApiHttpTest {

    public void doRequest(String url) throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        // 387e54d7-ae2f-4ce7-aff6-b166b16cd844 测试新街口ios
        // 3826ae3b-33af-4de8-9762-70be7a160aa4
        nameValuePairs.add(new BasicNameValuePair("mt_appcode", Des3.ens("387e54d7-ae2f-4ce7-aff6-b166b16cd844")));
        nameValuePairs.add(new BasicNameValuePair("mt_sid", Des3.ens("1123444")));
        nameValuePairs.add(new BasicNameValuePair("mt_mac", Des3.ens("84-2B-2B-91-AC-64")));
        nameValuePairs.add(new BasicNameValuePair("shop_user_id", "64421381"));
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

        HttpResponse response = httpclient.execute(httpPost);

        System.err.println(EntityUtils.toString(response.getEntity()));

    }

    public static String doRequest(String url, Map<String, String> params) throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (params != null) {
            for (String key : params.keySet()) {
                nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

        HttpResponse response = httpclient.execute(httpPost);

        return EntityUtils.toString(response.getEntity());
    }

    public static String doGetRequest(String url) throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        // 387e54d7-ae2f-4ce7-aff6-b166b16cd844 测试新街口ios
        // 3826ae3b-33af-4de8-9762-70be7a160aa4
        // 62618f99-0990-4adb-a25e-cfc0fb967ec2 only photo
        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("mt-appcode", "62618f99-0990-4adb-a25e-cfc0fb967ec2");

        HttpResponse response = httpclient.execute(httpGet);
        return EntityUtils.toString(response.getEntity());
        // FileUtils.writeByteArrayToFile(new File("D://log/1.png"), EntityUtils.toByteArray(response.getEntity()));
        // return new File("D://log/1.png").length() + "";
    }

    public static String HOST = "http://192.168.1.247:8820";
    public static String HOST_TOBY = "http://192.168.1.13:8888";

    @Test
    public void testLinks() throws Exception {
        String url = "/api.do?action=links";
        String getLinks = HOST_TOBY + url;
        doRequest(getLinks);
    }

    public static void main(String[] args) throws ClientProtocolException, IOException {
        // 德基广场 32.04317, 118.78509 11878 3204 971 449 118.77988115009305 32.04521164369418
        // String host = "http://192.168.1.13:9091/passapi/qrcode/ads";
        // String host = "http://emms.airad.com/passapi/mapoffset";
        // String host = "http://192.168.1.247:8860/api/passbook";
        // parking-lots bus-lines toilets
        // String host = "http://emms.airad.com/api/xinjiekou/parking-lots";
        // String host = "http://192.168.1.247:8860/api/passbook";
        String host = "http://emms.airad.com/api/about_us";
        // 1,5
        String response = doGetRequest(host);
        System.out.println(response);
    }

}
