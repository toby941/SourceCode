/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.rpc.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * XmlRpcServer.java
 * 
 * @author baojun 2012-4-5
 */
public class XmlRpcServer extends HttpServlet {
    private XmlRpcServletServer server;

    @Override
    public void init(ServletConfig pConfig) throws ServletException {
        super.init(pConfig);
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        server = (XmlRpcServletServer) context.getBean("rpcServer");
    }

    @Override
    public void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException, ServletException {
        server.execute(pRequest, pResponse);
    }

}
