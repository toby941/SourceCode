/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

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
        System.out.println(getAppName(null));
    }
}
