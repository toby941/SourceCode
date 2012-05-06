package com.sourcecode.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.ParseException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * HttpClient.java 封装mapabc请求获取省市区方法
 * 
 * @author baojun
 */
public class MapABCUtil {

    private static Logger logger = org.apache.log4j.Logger.getLogger(MapABCUtil.class);

    public final static Integer HTTP_RESPONSE_STATUS_SUCCESS_CODE = 200;

    private final static String XML_URL =
            "<?xml version=\"1.0\" encoding=\"gb2312\"?>\r\n"
                    + "<spatial_request method=\"searchPoint\"><x>{0}</x><y>{1}</y><xs/><ys/><poiNumber>10</poiNumber><range>NaN</range><pattern>1</pattern><roadLevel>\r\n"
                    + "0</roadLevel><exkey/></spatial_request>\r\n";

    public final static String[] API_KEY_ARRAY = new String[] { "b0a7db0b3a30f944a21c3682064dc70ef5b738b062f6479a5eca39725798b1ee300bd8d5de3a4ae3" };

    private static String MAPABC_URL = "http://search1.mapabc.com/sisserver?config=SPAS&ver=2.0&resType=json&enc=utf-8&spatialXml={0}&a_k={1}";

    public static String executeHttpGet(String requestUrl) throws ParseException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(requestUrl);
        HttpResponse response = httpclient.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        if (HTTP_RESPONSE_STATUS_SUCCESS_CODE.equals(statusLine.getStatusCode())) {
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } else {
            logger.error("execute map abc error: " + ReflectionToStringBuilder.toString(statusLine));
        }
        return StringUtils.EMPTY;
    }

    public static boolean isValidLongitudeAndLatitude(String longitude, String latitude) {
        return StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude) && !"0".equals(longitude) && !"0".equals(latitude);
    }

    public static LocationInfo getLocationInfo(String longitude, String latitude) throws ParseException, IOException {
        if (isValidLongitudeAndLatitude(longitude, latitude)) {
            String xmlParameter = MessageFormat.format(XML_URL, longitude, latitude);
            String apiKey = API_KEY_ARRAY[0];
            String requestUrl = getMapABCRequestURL(xmlParameter, apiKey);
            String result = executeHttpGet(requestUrl);

            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.fromObject(result);
                LocationInfo locationInfo = new LocationInfo(jsonObject);
                locationInfo.setLongitude(longitude);
                locationInfo.setLatitude(latitude);
                return locationInfo;
            }
        }

        return LocationInfo.getEmptyLocationInfo();
    }

    public static String getMapABCRequestURL(String xml, String apiKey) throws UnsupportedEncodingException {
        return MessageFormat.format(MAPABC_URL, URLEncoder.encode(xml, "utf-8"), apiKey);
    }

    public static void main(String[] args) throws Exception {
        LocationInfo locationInfo = getLocationInfo("118.735063", "32.042041");
        System.out.println(locationInfo);
    }
}
