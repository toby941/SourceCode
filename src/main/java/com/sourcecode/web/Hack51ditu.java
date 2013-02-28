package com.sourcecode.web;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

/**
 * 51地图破解
 * 
 * @author toby
 */
public class Hack51ditu {

    /**
     * 百分百高仿 http://api.51ditu.com/js/maps.js的核心解密方法 <br/>
     * var w=function(p){var a=0,s=0;var d=p.length;var f=new String();var g=-1;var h=0;for(var j=0;j<d;j++){var
     * k=p.charCodeAt(j);k=(k==95)?63:((k==44)?62:((k>=97)?(k-61):((k>=
     * 65)?(k-55):(k-48))));s=(s<<6)+k;a+=6;while(a>=8){var
     * l=s>>(a-8);if(h>0){g=(g<<6)+(l&(0x3f));h--;if(h==0){f+=String
     * .fromCharCode(g);};}else{if(l>=224){g=l&(0xf);h=2;}else
     * if(l>=128){g=l&(0x1f);h=1;}else{f+=String.fromCharCode(l);};};s=s-(l<<(a-8));a-=8;};};return f;};<br/>
     * 连变量命名都一致
     * 
     * @param p 加密的字符串
     * @return 解密后正常显示的字符串
     */
    public static String w(String p) {
        int a = 0;
        int s = 0;
        int d = p.length();
        String f = "";
        int g = -1;
        int h = 0;
        for (int j = 0; j < d; j++) {
            char tmp = p.charAt(j);
            int k = tmp;
            k = (k == 95) ? 63 : ((k == 44) ? 62 : ((k >= 97) ? (k - 61) : ((k >= 65) ? (k - 55) : (k - 48))));
            s = (s << 6) + k;
            a += 6;
            while (a >= 8) {
                int l = s >> (a - 8);
                if (h > 0) {
                    g = (g << 6) + (l & (0x3f));
                    h--;
                    if (h == 0) {
                        f += getString(g);
                    };
                }
                else {
                    if (l >= 224) {
                        g = l & (0xf);
                        h = 2;
                    }
                    else if (l >= 128) {
                        g = l & (0x1f);
                        h = 1;
                    }
                    else {
                        f += getString(l);
                    };
                };
                s = s - (l << (a - 8));
                a -= 8;
            };
        };
        return f;
    }

    public static void main(String[] args) throws ClientProtocolException, IOException {
        System.out.println(w("CZaevBgKvhIowAE5wQMmvP,EBUQTgEMwX2a"));
        System.out.println(w("vBgKvhIowAE5wQMmvP,E"));
        System.out.println(w("2W2W"));
        getBusLine("", "");
    }

    /**
     * http://srvfree.api.51ditu.com/apisrv/bus?tp=bl&cy=beijing&ln=919&mn=10&qt=2&fg=0<br/>
     * cy city 城市全拼 <br/>
     * qt querytype 公交查询的精确/模糊模式设置 0：精确模式 1：模糊模式 2：先精确后模糊
     * 
     * @param busNo 公交线路名称 例如查询2路 就是2
     * @param city 城市拼音 beijing nanjing
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static void getBusLine(String busNo, String city) throws ClientProtocolException, IOException {
        String url = "http://srvfree.api.51ditu.com/apisrv/bus?tp=bl&cy=nanjing&ln=游4   &mn=1&qt=0&fg=0";
        String result = ApiHttpTest.doGetRequest(url);
        BusLine line = new BusLine(result);
        System.out.println(line.toJson());
    }

    public static String getString(int g) {
        char s = (char) g;
        return String.valueOf(s);
    }

    /**
     * 将字符串转成unicode
     * 
     * @param str 待转字符串
     * @return unicode字符串
     */
    public static String convert(String str) {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>> 8); // 取出高8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); // 取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

    /**
     * 将unicode 字符串
     * 
     * @param str 待转字符串
     * @return 普通字符串
     */
    public static String revert(String str) {
        str = (str == null ? "" : str);
        if (str.indexOf("\\u") == -1) {
            return str;
        }// 如果不是unicode码则原样返回

        StringBuffer sb = new StringBuffer(1000);

        for (int i = 0; i < str.length() - 6;) {
            String strTemp = str.substring(i, i + 6);
            String value = strTemp.substring(2);
            int c = 0;
            for (int j = 0; j < value.length(); j++) {
                char tempChar = value.charAt(j);
                int t = 0;
                switch (tempChar) {
                    case 'a' :
                        t = 10;
                        break;
                    case 'b' :
                        t = 11;
                        break;
                    case 'c' :
                        t = 12;
                        break;
                    case 'd' :
                        t = 13;
                        break;
                    case 'e' :
                        t = 14;
                        break;
                    case 'f' :
                        t = 15;
                        break;
                    default :
                        t = tempChar - 48;
                        break;
                }

                c += t * ((int) Math.pow(16, (value.length() - j - 1)));
            }
            sb.append((char) c);
            i = i + 6;
        }
        return sb.toString();
    }

}
