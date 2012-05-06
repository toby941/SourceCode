package com.sourcecode.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

/**
 * ImageSearch.java
 * 
 * @author toby941
 */
public class ImageSearch {

    private static String baiduImageSearchUrl =
            "http://image.baidu.com/i?ct=201326592&cl=2&lm=-1&st=-1&tn=baiduimage&istype=2&fm=index&pv=&z=19&word=MM&s=1#z=&width=480&height=800&pn=0";

    private static String baiduImageSearchUrlTemplete =
            "http://image.baidu.com/i?ct=201326592&cl=2&lm=-1&st=-1&tn=baiduimage&istype=2&fm=index&pv=&z=19&word={0}&s=1#z=&width={1}&height={2}&pn=0";

    public static String requestImage(String word, String width, String height) {
        String searchUrl = MessageFormat.format(baiduImageSearchUrlTemplete, word, width, height);
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse response = httpclient.execute(new HttpGet(searchUrl));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                return responseString;
            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    public static void main(String[] args) throws XPatherException {
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);

        String respones = ImageSearch.requestImage("MM", "480", "800");
        TagNode node = cleaner.clean(respones);
        System.out.println(node.getText().toString());
        String pathName = "div";
        Object[] info_nodes = node.evaluateXPath(pathName);
        if (info_nodes.length > 0) {
            TagNode info_node = (TagNode) info_nodes[0];
            System.out.println(info_node.toString());
        }

    }
}
