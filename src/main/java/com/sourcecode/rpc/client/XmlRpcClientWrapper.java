package com.sourcecode.rpc.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcLiteHttpTransportFactory;

/**
 * XmlRpcClientWrapperImpl.java
 * 
 * @author baojun 2012-4-5
 */
public class XmlRpcClientWrapper {
    protected Log log = LogFactory.getLog(XmlRpcClientWrapper.class);
    /** 远程服务的URL地址，默认http://localhost/xmlrpc */
    private String url = "http://localhost/xmlrpc";
    /** 最多的连接尝试次数，默认1次 */
    private int maxTry = 1;
    /** 服务连接超时的时长（单位：毫秒），默认10000ms */
    private int connectionTimeout = 10000;
    private XmlRpcClient client;
    /** 是否支持扩展，默认：true */
    private boolean enabledForExtensions = true;
    /** 默认：false */
    private boolean enabledForExceptions = false;
    /** 默认：false */
    private boolean contentLengthOptional = false;
    /** 字符编码，默认：UTF-8 */
    private String basicEncoding = "UTF-8";
    /** 连接密码 */
    private String basicPassword;
    /** 连接密码用户名 */
    private String basicUserName;

    public void init() {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL(url));
            config.setEnabledForExtensions(enabledForExtensions);
            config.setEnabledForExceptions(enabledForExceptions);
            config.setConnectionTimeout(connectionTimeout);
            config.setContentLengthOptional(contentLengthOptional);
            config.setBasicEncoding(basicEncoding);
            config.setBasicPassword(basicPassword);
            config.setBasicUserName(basicUserName);
            client = new XmlRpcClient();
            client.setConfig(config);
            client.setTransportFactory(new XmlRpcLiteHttpTransportFactory(client));
        } catch (MalformedURLException e) {
            log.error("init rpc client error", e);
            throw new IllegalArgumentException("the url:" + url + " could not determine the server!");
        }
    }

    public static void main(String[] args) {
        XmlRpcClientWrapper wrapper = new XmlRpcClientWrapper();
        wrapper.init();
        wrapper.execute("rpcTestService.getProduct", new Object[] { "ss" });
    }

    /**
     * 执行服务
     * 
     * @param serviceName 服务名称，例如："Calculator.add"
     * @param parameters 参数
     * @return 返回执行得到的结果
     */
    public Object execute(String serviceName, Object[] parameters) {
        Object mOutput = null;
        if (client == null) {
            init();
        }
        try {
            boolean run = false;
            Throwable runE = null;
            for (int i = 0; i < maxTry; i++) {
                try {
                    mOutput = client.execute(serviceName, parameters);
                    run = true;
                    break;
                } catch (Throwable e) {
                    runE = e;
                }
                Thread.sleep(1000);
            }
            if (run == false) {
                throw new Exception("重复请求“" + maxTry + "”次后没有成功。", runE);
            }
        } catch (Exception e) {
            String param = "";
            for (int i = 0; (null != parameters) && (i < parameters.length); i++) {
                param += parameters[i].toString();
            }
            log.error("service:" + serviceName + " parameters:" + param + " error!", e);
            throw new IllegalArgumentException("service:" + serviceName + " error!", e);
        }
        return mOutput;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnabledForExtensions() {
        return enabledForExtensions;
    }

    public void setEnabledForExtensions(boolean enabledForExtensions) {
        this.enabledForExtensions = enabledForExtensions;
    }

    public boolean isEnabledForExceptions() {
        return enabledForExceptions;
    }

    public void setEnabledForExceptions(boolean enabledForExceptions) {
        this.enabledForExceptions = enabledForExceptions;
    }

    public int getMaxTry() {
        return maxTry;
    }

    public void setMaxTry(int maxTry) {
        this.maxTry = maxTry;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isContentLengthOptional() {
        return contentLengthOptional;
    }

    public void setContentLengthOptional(boolean contentLengthOptional) {
        this.contentLengthOptional = contentLengthOptional;
    }

    public String getBasicEncoding() {
        return basicEncoding;
    }

    public void setBasicEncoding(String basicEncoding) {
        this.basicEncoding = basicEncoding;
    }

    public String getBasicPassword() {
        return basicPassword;
    }

    public void setBasicPassword(String basicPassword) {
        this.basicPassword = basicPassword;
    }

    public String getBasicUserName() {
        return basicUserName;
    }

    public void setBasicUserName(String basicUserName) {
        this.basicUserName = basicUserName;
    }
}
