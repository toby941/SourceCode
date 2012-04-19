/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

/**
 * CommonUtils.java
 * 
 * @author baojun 2012-4-18
 */
public class CommonUtils {

    public static Integer getLength(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        int count = 0;
        char[] chs = str.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            count += (chs[i] > 0xff) ? 2 : 1;
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println(getLength("我的s。s%"));
    }
}
