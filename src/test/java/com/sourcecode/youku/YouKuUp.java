/*
 * Copyright 2011 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.youku;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.junit.Test;

/**
 * YouKuUp.java
 * 
 * @author baojun
 */
public class YouKuUp {

    /**
     * @throws ClientProtocolException
     * @throws IOException
     */
    @Test
    public void testPostUp() throws ClientProtocolException, IOException {

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost("http://v.youku.com/QVideo/~ajax/updown?__rt=1&__ro=");
            System.out.println("executing request " + httppost.getURI());
            HttpParams params = new BasicHttpParams();
            params.setParameter("__rt", "1");
            params.setParameter("__ro", "");
            params.setParameter("videoId", "XMzIyODAwNDI0");
            params.setParameter("type", "up");
            params.setParameter("__ap", "{\"videoId\": \"XMzIyODAwNDI0\", \"type\": \"up\"}");
            httppost.setHeader("Host", "v.youku.com");
            httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
            httppost.setHeader("Referer", "http://v.youku.com/v_show/id_XMzIyODAwNDI0.html");

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httppost, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");

        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

    /**
     * http://v.youku.com/v_show/id_XMzIyODAwNDI0.html setInterval(function(){location.reload();},2000);
     */
    public void testGet() {

    }

}
