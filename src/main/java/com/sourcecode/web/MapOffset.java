package com.sourcecode.web;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.text.ParseException;

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
 * 对google地图修正
 * 
 * @author toby
 */
public class MapOffset {
    private static Logger logger = org.apache.log4j.Logger.getLogger(MapOffset.class);

    public static String executeHttpGet(String requestUrl) throws ParseException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(requestUrl);
        HttpResponse response = httpclient.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        if (200 == statusLine.getStatusCode()) {
            String result = EntityUtils.toString(response.getEntity());
            return result;
        }
        else {
            logger.error("execute map abc error: " + ReflectionToStringBuilder.toString(statusLine));
        }
        return StringUtils.EMPTY;
    }

    public static boolean getOffset(Double lat, Double lng) {
        StringBuilder url = new StringBuilder();
        url.append("http://ditu.google.com/maps/vp?");
        url.append("spn=0.0,0.0&z=18&vp=");
        url.append(lat);
        url.append(",");
        url.append(lng);
        String urlStr = url.toString();

        try {
            String js = executeHttpGet(urlStr);
            System.out.println("urlStr: " + urlStr + " resault: " + js);
            int x = js.lastIndexOf("[");
            int y = js.lastIndexOf("]");
            if (x > 0 && y > 0) {
                String text = js.substring(x + 1, y);
                int b = text.lastIndexOf(",");
                int a = text.lastIndexOf(",", b - 1);
                if (a > 0 && b > 0) {
                    String offsetPixX = text.substring(a + 2, b);
                    String offsetPixY = text.substring(b + 2);
                    System.out.println("Offset_x: " + offsetPixX + "  Offset_y: " + offsetPixY);
                    return true;
                }
            }
            else {
                logger.error("error 1：  lat:" + lat + "\tlng:" + lng);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 沙县小吃 32.075147, 118.753981 11875 3207 973 448 118.74876142125703 32.07718338470381
    // 察哈尔车站 32.075541, 118.751902 118.74668242125699 32.07757737592766
    // 金城路 32.075786, 118.749008 11874 3207 972 449 118.74379378567505 32.077826915889425

    public static void main(String[] args) throws IOException {
        // x y 32.076566,118.747892
        // 11874:3207:972:449
        // fix1 118.75310621432493 32.07452505596113
        // fix2 118.74267778567503 32.078606898476124
        double lng = 118.749008;
        double lat = 32.075786;
        // double lngS = lngToPixel(lng, 18) + 972;
        // System.out.println("lngS:" + lngS);
        // double lngs = pixelToLng(lngS, 18);
        // System.out.println("lngs:" + lngs);
        // double lngX = latToPixel(lat, 18) + 449;
        // System.out.println("lngX:" + lngX);
        // double lngx = pixelToLat(lngX, 18);
        // System.out.println("lngx:" + lngx);
        // System.out.println(lngs + " " + lngx);

        double lngS = lngToPixel(lng, 18) - 972;
        System.out.println("lngS:" + lngS);
        double lngs = pixelToLng(lngS, 18);
        System.out.println("lngs:" + lngs);
        double lngX = latToPixel(lat, 18) - 449;
        System.out.println("lngX:" + lngX);
        double lngx = pixelToLat(lngX, 18);
        System.out.println("lngx:" + lngx);
        System.out.println(lngs + " " + lngx);
        // String path = "D:\\Dropbox\\doc\\mitian\\dev\\passbook\\google地图偏移精度5米.txt";
        // readToDB(path);

    }

    // 经度到像素X值
    public static double lngToPixel(double lng, int zoom) {
        return (lng + 180) * (256 << zoom) / 360;
    }

    // 像素X到经度
    public static double pixelToLng(double pixelX, int zoom) {
        return pixelX * 360 / (256 << zoom) - 180;
    }

    // 纬度到像素Y
    public static double latToPixel(double lat, int zoom) {
        double siny = Math.sin(lat * Math.PI / 180);
        double y = Math.log((1 + siny) / (1 - siny));
        return (128 << zoom) * (1 - y / (2 * Math.PI));
    }

    // 像素Y到纬度
    public static double pixelToLat(double pixelY, int zoom) {
        double y = 2 * Math.PI * (1 - pixelY / (128 << zoom));
        double z = Math.pow(Math.E, y);
        double siny = (z - 1) / (z + 1);
        return Math.asin(siny) * 180 / Math.PI;
    }

    public static String sql_templete =
            "insert into `EMMS_MAP_OFFSET`(`LNG`,`LAT`,`OFF_LNG`,`OFF_LAT`) values ({0},{1},{2},{3});\r\n";

    public static void readToDB(String filePath) throws IOException {
        RandomAccessFile read = new RandomAccessFile(filePath, "r");
        RandomAccessFile writer = new RandomAccessFile("D://tmp//offset.sql", "rw");
        int count = 0;
        while (true) {
            count++;
            String s = read.readLine();
            if (s == null) {
                break;
            }
            if (count % 500 == 0) {
                System.out.println("count:" + count);
            }
            else {
                String trimValue = StringUtils.trimToEmpty(s);
                if (StringUtils.isNotBlank(trimValue)) {
                    String[] values = trimValue.split(":");
                    String sql = MessageFormat.format(sql_templete, values);
                    writer.write(sql.getBytes());
                }
            }
        }
        read.close();
        writer.close();
    }
}
