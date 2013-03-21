package com.sourcecode.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class FinancialUtils {

    private static Logger log = Logger.getLogger(FinancialUtils.class);

    private final static String URL =
            "http://webservice.webxml.com.cn/WebServices/ForexRmbRateWebService.asmx/getForexRmbRate";

    private final static String URL_EXCHANGERATEWEBSERVICE =
            "http://webservice.webxml.com.cn/WebServices/ExchangeRateWebService.asmx/getExchangeRate?theType=A";

    private static JSONArray catchResult = null;
    private static Integer requestTimes = 0;

    public static JSONArray getFullRate() throws ClientProtocolException, IOException {
        requestTimes++;
        if (catchResult == null || requestTimes % 60 == 0) {
            JSONArray rmb = getForexRmbRate();
            JSONArray other = getExchangeRate();
            rmb.addAll(other);
            catchResult = rmb;
        }

        return catchResult;
    }

    public static JSONArray getForexRmbRate() throws ClientProtocolException, IOException {

        String result = HttpUtils.doGetRequest(URL);
        JSONArray jsonArray = new JSONArray();
        try {
            SAXBuilder sb = new SAXBuilder();
            Document doc;
            doc = sb.build(IOUtils.toInputStream(result));
            Element root = (Element) doc.getRootElement().getChildren().get(1); // 得到根元素
            List focs = root.getChild("getForexRmbRate").getChildren("ForexRmbRate"); // 得到根元素所有子元素的集合
            Element foc = null;

            for (int i = 0; i < focs.size(); i++) {
                foc = (Element) focs.get(i);
                String name = foc.getChildText("Name");
                String rateStr = StringUtils.trimToEmpty(foc.getChildText("fBuyPrice"));
                float rate = 0;
                if (rateStr.length() > 0) {
                    rate = Float.valueOf(rateStr) / 100;
                }
                String symbol = foc.getChildText("Symbol");
                Map<String, String> jsonMap = new HashMap<String, String>();
                jsonMap.put("name", name + "人民币");
                jsonMap.put("buyPrice", String.valueOf(rate));
                jsonMap.put("code", symbol);
                JSONObject jsonObject = JSONObject.fromObject(jsonMap);
                jsonArray.add(jsonObject);
            }
        }
        catch (JDOMException e) {
            log.error("getForexRmbRate error", e);
        }
        catchResult = jsonArray;
        return jsonArray;
    }

    public static JSONArray getExchangeRate() throws ClientProtocolException, IOException {
        String result = HttpUtils.doGetRequest(URL_EXCHANGERATEWEBSERVICE);
        JSONArray jsonArray = new JSONArray();
        try {
            SAXBuilder sb = new SAXBuilder();
            Document doc;
            doc = sb.build(IOUtils.toInputStream(result));
            Element root = (Element) doc.getRootElement().getChildren().get(1); // 得到根元素
            List focs = root.getChild("getExchangeRate").getChildren("ExchangeRate"); // 得到根元素所有子元素的集合
            Element foc = null;

            for (int i = 0; i < focs.size(); i++) {
                foc = (Element) focs.get(i);
                String name = foc.getChildText("Currency");
                String rate = foc.getChildText("BuyPrice");
                String symbol = foc.getChildText("Code");
                Map<String, String> jsonMap = new HashMap<String, String>();
                jsonMap.put("name", name);
                jsonMap.put("buyPrice", rate);
                jsonMap.put("code", StringUtils.trim(symbol));
                JSONObject jsonObject = JSONObject.fromObject(jsonMap);
                jsonArray.add(jsonObject);
            }
        }
        catch (JDOMException e) {
            log.error("getForexRmbRate error", e);
        }
        catchResult = jsonArray;
        return jsonArray;
    }

    public static void main(String[] args) throws ClientProtocolException, IOException {
        System.out.println(getFullRate());
    }
}
