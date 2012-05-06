/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.android;

/**
 * CodeBuild.java
 * 
 * @author baojun 2012-4-19
 */
public class CodeBuild {

    static String code = "    @SmallTest\r\n" + "    public void test##() {\r\n" + "        solo.sleep(getSecond() * 1000);\r\n"
            + "        solo.clickOnView(getView());\r\n" + "    }";

    public static String buildJava(int times) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < times; i++) {
            String newCode = code.replace("##", i + "a");
            sb.append(newCode + "\r\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(buildJava(1000));
    }
}
