package com.yogapppackage;

import java.net.MalformedURLException;

import net.sf.json.JSONObject;

import com.caucho.hessian.client.HessianProxyFactory;

public class HessianClient {
    public static void OnClick11() {
        // String url = "http://192.168.1.16:9091/api.do";
        // String url = "http://weimp.sinaapp.com/api.do";
        // String url = "  http://42.121.112.185/XuanR_YogAppProject/InfoSelectServer";
        String url = "http://180.96.38.219/XuanR_YogAppProject/InfoSelectServer";

        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            factory.setDebug(true);
            factory.setReadTimeout(5000);
            factory.setHessian2Reply(false);
            factory.setChunkedPost(false);
            BasicAPI basic = factory.create(BasicAPI.class, url);
            // JSONObject s = basic.SelectTheOneCepActive("1", "200970", "CHI");
            // JSONObject s = basic.ConfirmCepActive("1", "200970");
            // JSONObject s = basic.getLocation("CHI");
            // JSONObject s = basic.getLocationDetail("CTF", "CHI");
            // JSONObject s = basic.GetGameDetailInfo("WL", "20130817", "CHI");
            // JSONObject s = basic.GetGameDetailInfoPlace("IEF", "20130817", "CHI");
            JSONObject s = basic.SelectConfirmCepActive("200970", "CHI");
            // JSONObject s = basic.SelectAllCepActive("200970", "CHI");
            // JSONObject s = basic.GetGameInfo("200970", "CHI");
            // JSONObject s = basic.GetGameDetailInfo("00001", "20130812", "CHI");
            // JSONObject s = basic.UserLogin("200970", "032747", "CHI");
            // JSONObject s = basic.ChangePassword("20001", "032747", "111111", "111111", "CHI");
            // JSONObject s = basic.SignInCepActive("abcefg", "200030", "199.88", "46.8897", "CHI");
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
