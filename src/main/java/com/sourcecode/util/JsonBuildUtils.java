package com.sourcecode.util;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.lang3.StringUtils;

public class JsonBuildUtils {

    /**
     * 过滤java对象中的空或null值 输出json
     * 
     * @param collection
     * @return
     */
    public static JSONArray buildJsonTrimEmpty(List collection) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            @Override
            public boolean apply(Object source/* 属性的拥有者 */, String name /* 属性名字 */, Object value/* 属性值 */) {
                return value == null || StringUtils.isEmpty(value.toString());
            }
        });
        JSONArray jsonArray = JSONArray.fromObject(collection, jsonConfig);
        return jsonArray;
    }

}
