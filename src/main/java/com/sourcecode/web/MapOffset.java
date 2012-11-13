package com.sourcecode.web;

import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * 对google地图修正
 * 
 * @author toby
 */
public class MapOffset {
    private static Logger logger = org.apache.log4j.Logger.getLogger(MapOffset.class);

    public static String executeHttpGet(String requestUrl) throws ParseException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(requestUrl);
        HttpResponse response = httpclient.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        if (200 == statusLine.getStatusCode()) {
            String result = EntityUtils.toString(response.getEntity());
            return result;
        }
        else {
            logger.error("execute map abc error: " + ReflectionToStringBuilder.toString(statusLine));
        }
        return StringUtils.EMPTY;
    }

    public static boolean getOffset(Double lat, Double lng) {
        StringBuilder url = new StringBuilder();
        url.append("http://ditu.google.com/maps/vp?");
        url.append("spn=0.0,0.0&z=18&vp=");
        url.append(lat);
        url.append(",");
        url.append(lng);
        String urlStr = url.toString();

        try {
            String js = executeHttpGet(urlStr);
            System.out.println("urlStr: " + urlStr + " resault: " + js);
            int x = js.lastIndexOf("[");
            int y = js.lastIndexOf("]");
            if (x > 0 && y > 0) {
                String text = js.substring(x + 1, y);
                int b = text.lastIndexOf(",");
                int a = text.lastIndexOf(",", b - 1);
                if (a > 0 && b > 0) {
                    String offsetPixX = text.substring(a + 2, b);
                    String offsetPixY = text.substring(b + 2);
                    System.out.println("Offset_x: " + offsetPixX + "  Offset_y: " + offsetPixY);
                    return true;
                }
            }
            else {
                logger.error("error 1：  lat:" + lat + "\tlng:" + lng);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        getOffset(39.111195, 117.148067);
    }
}
