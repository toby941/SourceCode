package com.sourcecode.web;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.sourcecode.util.JsonBuildUtils;

public class BusLine {
    private final String name;
    private final List<String> station;

    public BusLine(String response) {
        super();
        int begin = response.indexOf("{");
        int end = response.lastIndexOf(";");
        String json = response.substring(begin, end);
        JSONObject j = JSONObject.fromObject(json);
        JSONArray dataJson = j.getJSONArray("c");
        if (dataJson.size() > 0) {
            JSONObject metadataJson = dataJson.getJSONObject(0);
            name = Hack51ditu.w(metadataJson.getJSONObject("a").getString("name"));
            JSONArray stationJson = metadataJson.getJSONArray("c");
            station = new ArrayList<String>();
            for (int i = 0; i < stationJson.size(); i++) {
                station.add(Hack51ditu.w(stationJson.getJSONObject(i).getJSONObject("a").getString("name")));
            }
        }
        else {
            name = StringUtils.EMPTY;
            station = new ArrayList<String>();
        }
    }

    public JSONObject toJson() {
        JSONArray stations = JsonBuildUtils.buildJsonTrimEmpty(station);
        JSONObject resultJson = new JSONObject();
        resultJson.accumulate("name", name);
        resultJson.accumulate("station", stations);
        return resultJson;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : station) {
            sb.append(s + " ");
        }
        return "BusLine [name=" + name + ", station=" + sb.toString() + "]";
    }
}
