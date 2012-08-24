package com.sourcecode.web;

import org.jsoup.Jsoup;

/**
 * http://jsoup.org/apidocs/
 * 
 * @author toby
 */
public class HTMLClean {

    public static String easy_html =
            "&lt;p&gt;本店主营：山羊绒&nbsp;&nbsp;&nbsp; 驼绒&nbsp;&nbsp;&nbsp; 牛绒&nbsp;&nbsp;&nbsp; 貂绒 兔绒制品欢迎新老用户惠顾。电话：13931955156 &nbsp;&nbsp;QQ:627132027&nbsp; 鹿王羊绒衫 鹿王羊绒裤&nbsp; 鹿王针织衫&nbsp; 鹿王打底衫&lt;/p&gt;";

    public static String easy2_html =
            "<p>本店主营：山羊绒    驼绒    牛绒    貂绒 兔绒制品欢迎新老用户惠顾。电话：13931955156   QQ:627132027  鹿王羊绒衫 鹿王羊绒裤  鹿王针织衫  鹿王打底衫</p>";

    public static String easy3_html =
            "&lt;p align=&quot;left&quot; style=&quot;list-style-type: square&quot;&gt;&lt;font color=&quot;#000000&quot;&gt;&lt;span style=&quot;font-family: 微软雅黑, sans-serif&quot;&gt;&lt;/span&gt;&lt;/font&gt;&lt;/p&gt;&lt;p align=&quot;left&quot; style=&quot;text-align: center; list-style-type: square&quot;&gt;&lt;font color=&quot;#000000&quot;&gt;&lt;span style=&quot;font-family: 微软雅黑, sans-serif&quot;&gt;&lt;img src=&quot;http://img02.taobaocdn.com/imgextra/i2/676406584/T2CdBHXj0MXXXXXXXX_!!676406584.jpg&quot; /&gt;&lt;/span&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&nbsp;&lt;/p&gt;";

    public static void clean() {
        String text = Jsoup.parse(easy3_html).text();
        text = Jsoup.parse(text).text();
        System.out.println(text);
    }

    public static void main(String[] args) {
        clean();
    }
}
