/*
 * Copyright 2011 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

import java.net.URLDecoder;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/**
 * MapAbc.java
 * 
 * @author baojun
 */
public class MapAbc {

    public void search() throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet("http://search1.mapabc.com/sisserver");
            HttpParams params = new BasicHttpParams();
            params.setParameter("config", "SPAS");
            params.setParameter("ver", "2.0");
            params.setParameter("resType", "xml");
            params.setParameter("method", "searchPoint");
            params.setParameter("__ap", "{\"videoId\": \"XMzIyODAwNDI0\", \"type\": \"up\"}");
            httpget.setHeader("Host", "search1.mapabc.com");
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
            httpget.setHeader("Referer", "http://v.youku.com/v_show/id_XMzIyODAwNDI0.html");
            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

    public static void main(String[] args) throws Exception {
        MapAbc abc = new MapAbc();
        abc.search();
        String s =
                URLDecoder
                        .decode("search1.mapabc.com/sisserver?highLight=false&config=SPAS&ver=2.0\r\n"
                                + "&resType=json&enc=utf-8&spatialXml=%3C?xml%20version=%221.0%22%20encoding=%22gb2312%22?%3E%0D%0A%3Cspatial_request%20\r\n"
                                + "method=%22searchPoint%22%3E%3Cx%3E116.334%3C/x%3E%3Cy%3E39.9872%3C/y%3E%3Cxs/%3E%3Cys/%3E%3CpoiNumber%3E10%3C/poiNumber\r\n"
                                + "%3E%3Crange%3ENaN%3C/range%3E%3Cpattern%3E1%3C/pattern%3E%3CroadLevel%3E0%3C/roadLevel%3E%3Cexkey/%3E%3C/\r\n"
                                + "spatial_request%3E%0D%0A&a_k=b0a7db0b3a30f944a21c3682064dc70ef5b738b062f6479a5eca39725798b1ee300bd8d5de3a4ae3");
        System.out.println(s);
    }
    // search1.mapabc.com/sisserver?highLight=false&config=SPAS&ver=2.0
    // &resType=json&enc=utf-8&spatialXml=%3C?xml%20version=%221.0%22%20encoding=%22gb2312%22?%3E%0D%0A%3Cspatial_request%20
    // method=%22searchPoint%22%3E%3Cx%3E116.334%3C/x%3E%3Cy%3E39.9872%3C/y%3E%3Cxs/%3E%3Cys/%3E%3CpoiNumber%3E10%3C/poiNumber
    // %3E%3Crange%3ENaN%3C/range%3E%3Cpattern%3E1%3C/pattern%3E%3CroadLevel%3E0%3C/roadLevel%3E%3Cexkey/%3E%3C/
    // spatial_request%3E%0D%0A&a_k=b0a7db0b3a30f944a21c3682064dc70ef5b738b062f6479a5eca39725798b1ee300bd8d5de3a4ae3
    // 其中spatialXml为　XML请求包，转成标准格式如下
    // <spatial_request method=”searchPoint>
    // <x>116.334</x>
    // <y>39.9872</y>
    // <xs/>
    // <ys/>
    // <poiNumber>10</poiNumber>
    // <range>NaN</range>
    // <pattern>1</pattern>
    // <roadLevel>0</roadLevel>
    // <exKey/>
    // </spatial_request>
}
