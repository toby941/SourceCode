package com.sourcecode.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class ApiHttpTest {

    public void doRequest(String url) throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mt_appcode", Des3.ens("3826ae3b-33af-4de8-9762-70be7a160aa4")));
        nameValuePairs.add(new BasicNameValuePair("mt_sid", Des3.ens("1123444")));
        nameValuePairs.add(new BasicNameValuePair("mt_mac", Des3.ens("84-2B-2B-91-AC-64")));
        nameValuePairs.add(new BasicNameValuePair("shop_user_id", "676406584"));
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

        HttpResponse response = httpclient.execute(httpPost);

        System.err.println(EntityUtils.toString(response.getEntity()));

    }
    public static String HOST = "http://192.168.1.247:8820";
    public static String HOST_TOBY = "http://192.168.1.13:8888";

    @Test
    public void testLinks() throws Exception {
        String url = "/api.do?action=links";
        String getLinks = HOST_TOBY + url;
        doRequest(getLinks);
    }
}
