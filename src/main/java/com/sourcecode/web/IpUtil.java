/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * IpUtil.java
 * 
 * @author baojun 2012-4-25
 */
public class IpUtil {
    static String requestTemplete = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=text&ip={0}";

    public static void getLocation(List<String> ipList) throws Exception {
        MultiValueMap result = new MultiValueMap();
        for (int i = 0; i < ipList.size(); i++) {
            if (StringUtils.isNotBlank(ipList.get(i))) {
                String requestUrl = MessageFormat.format(requestTemplete, ipList.get(i).trim());
                String response = executeHttpGet(requestUrl);
                if (response.split("\\s").length > 5) {
                    String province = response.split("\\s")[4];
                    result.put(province, response);
                } else {
                    System.out.println("ip:" + ipList.get(i) + " locatioin:" + response);
                }
            }
        }
        for (Object key : result.keySet()) {
            Collection<Object> c = result.getCollection(key);
            System.out.println("location:" + key + " size:" + c.size());
        }
    }

    public static String executeHttpGet(String requestUrl) throws ParseException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(requestUrl);
        httpGet.setHeader("x-forwarded-for", "222.39.237.99");
        httpGet.setHeader("x-forwarded-for", "222.39.237.99");
        httpGet.setHeader("Proxy-Client-IP", "222.39.237.99");
        httpGet.setHeader("WL-Proxy-Client-IP", "222.39.237.99");
        HttpResponse response = httpclient.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        if (new Integer(200).equals(statusLine.getStatusCode())) {
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            return result;
        }
        return StringUtils.EMPTY;
    }

    public static void main(String[] args) throws Exception {
        // String response =
        // executeHttpGet("http://city.ip138.com/ip2city.asp");
        String response = executeHttpGet("http://www.ip.cn/getip.php?action=getip&ip_url=&from=web");
        System.out.println(response);
    }
}
