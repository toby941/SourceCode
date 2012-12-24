package com.sourcecode.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.io.IOUtils;

public class JsonUtils {

    /**
     * id address name lng lat<br/>
     * 0 中山路19号鼓楼邮政大厦底层 邮政大楼 118.791148 32.064482 <br/>
     * 1 丹凤街唱经楼西街 唱经楼西街 118.79333 32.059688
     * 
     * @param txtPath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getJson(String txtPath) throws FileNotFoundException, IOException {
        List<String> strList = IOUtils.readLines(new FileInputStream(new File(txtPath)), "GBK");
        String[] subArray = strList.get(0).split("\\s");
        List<Map<String, String>> collection = new ArrayList<Map<String, String>>();
        for (int i = 1; i < strList.size(); i++) {
            String[] contentArreay = strList.get(i).split("\\s");
            Map<String, String> map = new HashMap<String, String>();
            for (int j = 0; j < subArray.length; j++) {
                map.put(subArray[j], contentArreay[j]);
            }
            collection.add(map);
        }
        JSONArray jsonArray = JSONArray.fromObject(collection);
        System.out.println(jsonArray.toString());
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        getJson("C:\\Users\\toby\\Desktop\\WC.txt");
    }
}
