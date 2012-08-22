package com.sourcecode.io;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NumberUtils;

public class FileReadUtils {

    private static String sql =
            "insert into TAOBAO_EMAIL (ID,CATEGORY,TAOBAO_ID,TAOBAO_URL,GRADE,PHONE,M_PHONE ,QQ,EMAIL ,AREA,EST_DATE,CREDIS,ITEM_COUNTS,COMPANY_NAME) VALUES ({0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11},{12},''name'');";

    public static List<String> readFile(String path) throws Exception {
        File f = new File(path);
        List<String> lists = IOUtils.readLines(new FileInputStream(f), "GBK");
        List<String> sqlList = new ArrayList<String>();
        for (int i = 0; i < lists.size(); i++) {
            String[] info = lists.get(i).replaceAll(",", " ").split(";");

            try {
                info[5] = handlePhone(info[5]);
                info[6] = handlePhone(info[6]);
                info[7] = handleQQ(info[7]);
                info[8] = handlEmail(info[8]);
                handleStringForSql(info);
                String s = MessageFormat.format(sql, info);
                sqlList.add(s);
            } catch (Exception e) {
                System.out.println("info:" + lists.get(i));
                e.printStackTrace();
            }

        }
        return sqlList;
    }

    private static void handleStringForSql(String[] info) {
        for (int i = 0; i < info.length; i++) {
            if (!NumberUtils.isNumber(info[i])) {
                info[i] = "'" + info[i].trim() + "'";
            }
        }
    }

    private static String handlePhone(String input) {

        input = input.trim();
        if (input.equals("phone") || input.equals("mphone")) {
            return input;
        }
        String handlePhone = "";
        String[] phone = input.split("\\|\\|");
        if (phone.length > 1) {
            handlePhone = phone[1];
        } else {
            handlePhone = input;
        }
        return handlePhone;
    }

    private static String handleQQ(String input) {
        String handleQQ = "";
        String[] qq = input.split("\\|\\|QQ:");
        if (qq.length > 1) {
            handleQQ = qq[1];
        } else {
            handleQQ = "120857336";
        }
        return handleQQ;
    }

    private static String handlEmail(String input) {
        String handleEmail = "";
        if (input.trim().equals("email")) {
            return input;
        }
        String[] email = input.split("\\|\\|");
        if (email.length > 1) {
            handleEmail = email[1];
        } else {
            handleEmail = "email";
        }
        return handleEmail;

    }

    public static void main(String[] args) throws Exception {
        System.out.println(URLEncoder.encode("http://fuwu.taobao.com/ser/detail.htm?service_id=25120"));
        // List<String> sqlList = readFile("d://2.txt");
        // File insertSql = new File("d://3.txt");
        // FileUtils.writeLines(insertSql, sqlList);
        // for (int i = 0; i < sqlList.size(); i++) {
        // System.out.println(sqlList.get(i));
        // }
    }
}
