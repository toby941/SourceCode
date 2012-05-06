/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * AppName.java
 * 
 * @author baojun
 */
public class AppName {

    public static String getAppName(String path) throws Exception {
        String result = null;
        Process p = Runtime.getRuntime().exec("aapt dump badging D:\\workspace\\sourcecode\\src\\main\\java\\1.apk ");
        result = IOUtils.toString(p.getInputStream());
        return result;
    }

    public static void main(String[] args) throws Exception {
        String info = getAppName(null);
        // System.out.println(info);
        String[] arr = info.split("\r\n");
        Pattern p = Pattern.compile("application-label:'(.*)'");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith("application-label")) {
                Matcher matcher = p.matcher("application-label:'Dungeon Raid'");
                while (matcher.find()) {
                    System.out.println(matcher.group(1));
                }
            }
        }

    }
}
