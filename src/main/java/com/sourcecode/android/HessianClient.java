package com.sourcecode.android;

import java.net.MalformedURLException;

import net.sf.json.JSONObject;

import com.caucho.hessian.client.HessianProxyFactory;
import com.yogapppackage.BasicAPI;

public class HessianClient {
    public static void OnClick11() {
        String url = "http://192.168.1.16:9091/hello.do";
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            factory.setDebug(true);
            factory.setReadTimeout(5000);
            factory.setHessian2Reply(false);
            BasicAPI basic = factory.create(BasicAPI.class, url);
            // JSONObject s = basic.SelectAllCepActive();
            JSONObject s = basic.SelectTheOneCepActive("1");
            System.out.println(s.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        OnClick11();
    }
}
