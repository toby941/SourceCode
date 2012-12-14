package com.sourcecode.mincow;

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

import com.sourcecode.web.Des3;

public class ApiTest {

    public void doRequest(String url) throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String eappcode = Des3.ens("3826ae3b-33af-4de8-9762-70be7a160aa4");
        String esid = Des3.ens("1123444");
        String emac = Des3.ens("84-2B-2B-91-AC-64");
        System.out.println(eappcode + "\r\n" + esid + "\r\n" + emac + "\r\n");
        nameValuePairs.add(new BasicNameValuePair("mt_appcode", eappcode));

        nameValuePairs.add(new BasicNameValuePair("mt_sid", esid));
        nameValuePairs.add(new BasicNameValuePair("mt_mac", emac));
        nameValuePairs.add(new BasicNameValuePair("shop_user_id", "476449520"));
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

        HttpResponse response = httpclient.execute(httpPost);

        System.err.println(EntityUtils.toString(response.getEntity()));

    }

    public static String HOST = "http://www.mincow.com/api.do";

    @Test
    public void testLinks() throws Exception {
        String url = "?action=banners";
        String getLinks = HOST + url;
        doRequest(getLinks);
    }

}
