package com.sourcecode.web;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class BaiduImage {
    public final static Integer HTTP_RESPONSE_STATUS_SUCCESS_CODE = 200;
    /**
     * 百度图片抓取url word-搜索关键词<br/>
     * pn-起始页 <br/>
     * rn-分页大小 <br/>
     * width-指定宽<br/>
     * height-指定高<br/>
     */
    static String REQUEST_URL_TEMPLETE =
            "http://image.baidu.com/i?ct=201326592&lm=-1&tn=baiduimagenojs&pv=&word={0}&z=19&pn={1}&rn={2}&cl=2&width=480&height=800";
    static String BAIUD_IMG_URL_PREFIX = "http://image.baidu.com";

    static String SCRIPT_IMG =
            "http://image.baidu.com/i?tn=baiduimage&ct=201326592&cl=2&lm=-1&st=-1&fm=index&fr=&sf=1&fmq=1333707568520_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&word=MM&s=1#z=&width=480&height=800&pn=0";

    /**
     * 抓取给定url转化为string
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static String getHTML(String url) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        if (HTTP_RESPONSE_STATUS_SUCCESS_CODE.equals(statusLine.getStatusCode())) {
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 加载指定URL，通过指定xpath获取对应元素
     * 
     * @param url
     * @param xpath
     * @return 包含对应元素的结点集合，可能为null
     * @throws Exception
     */
    public static Object[] getNode(String url, String xpath) throws Exception {
        CleanerProperties props = new CleanerProperties();

        // set some properties to non-default values
        props.setTranslateSpecialEntities(true);
        props.setTransResCharsToNCR(true);
        props.setOmitComments(true);
        // do parsing
        TagNode tagNode = new HtmlCleaner(props).clean(new URL(url));
        // getHTML方法比较慢,带测试
        // TagNode tagNode = new HtmlCleaner(props).clean(getHTML(url));
        Object[] ns = tagNode.evaluateXPath(xpath);
        return ns;

    }

    public static String getImgUrlFromScript() throws Exception {
        Object[] nodes = getNode(SCRIPT_IMG, "//script");
        for (int i = 0; i < nodes.length; i++) {
            TagNode node = (TagNode) nodes[i];
            String json = node.getText().toString();
            if (json.startsWith("var imgdata =")) {
                return json;
            }
        }
        return null;
    }

    /**
     * 获取img url
     * 
     * @param keyword 搜索关键词
     * @param pageNumber 起始页
     * @param size 分页大小
     * @return img 绝对路径
     * @throws Exception
     */
    public static List<String> getImgUrl(String keyword, int pageNumber, int size) throws Exception {
        List<String> imgUrlList = new ArrayList<String>();
        String requestUrl = MessageFormat.format(REQUEST_URL_TEMPLETE, keyword, pageNumber, size);
        Object[] nodes = getNode(requestUrl, "//td/a");
        if (nodes != null && nodes.length > 0) {
            for (int i = 0; i < nodes.length; i++) {
                TagNode node = (TagNode) nodes[i];
                String detailInfoURL = BAIUD_IMG_URL_PREFIX + node.getAttributeByName("href");
                Object[] detailNodes = getNode(detailInfoURL, "//table//img");
                if (detailNodes != null && detailNodes.length > 0) {
                    TagNode detailInfo = (TagNode) detailNodes[0];
                    String imgUrl = detailInfo.getAttributeByName("src");
                    imgUrlList.add(imgUrl);
                }
            }
        }
        return imgUrlList;
    }

    private static Pattern pattern = Pattern.compile(".*\"objURL\":\"(.*)\",\"fromURL\".*");

    public static void main(String[] args) throws Exception {
        String json = getImgUrlFromScript();
        // System.out.println(json);
        String imgs[] = json.split("currentIndex");
        for (int i = 0; i < imgs.length; i++) {
            String info = imgs[i];
            Matcher m = pattern.matcher(info);
            if (m.matches()) {
                System.out.println(m.group(1).trim());
            }
        }

    }
}
