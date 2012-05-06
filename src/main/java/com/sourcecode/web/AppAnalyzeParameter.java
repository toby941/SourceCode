/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

/**
 * AppAnalyzeParameter.java
 * 
 * @author baojun
 */
public class AppAnalyzeParameter {
    protected String name;
    protected String regexExpression;
    protected String beginStr;

    public AppAnalyzeParameter(String gName, String gRegexExpression, String gBeginStr) {
        super();
        name = gName;
        regexExpression = gRegexExpression;
        beginStr = gBeginStr;
    }

    /**
     * 
     */
    public AppAnalyzeParameter() {
        super();
    }
}
