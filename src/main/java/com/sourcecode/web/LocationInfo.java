package com.sourcecode.web;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

/**
 * LocationInfo.java
 * 
 * @author baojun
 */
public class LocationInfo {

    public LocationInfo() {
        super();
    }

    public static LocationInfo getEmptyLocationInfo() {
        LocationInfo locationInfo = new LocationInfo();
        return locationInfo;
    }

    public Boolean isEmpty() {
        return StringUtils.isBlank(area) && StringUtils.isBlank(city) && StringUtils.isBlank(province);
    }

    public static final String PROVINCE_JSON_KEY = "Province";
    public static final String CITY_JSON_KEY = "City";
    public static final String DISTRICT_JSON_KEY = "District";

    public static final String JSON_NAME_KEY = "name";
    public static final String JSON_CODE_KEY = "code";
    public static final String JSON_LONGITUDE_KEY = "x";
    public static final String JSON_LATITUDE_KEY = "y";

    public static final String JSON_BEAN_KEY = "SpatialBean";

    public LocationInfo(JSONObject mapABCJson) {
        super();
        JSONObject locationBean = mapABCJson.getJSONObject(JSON_BEAN_KEY);
        JSONObject provinceObject = locationBean.getJSONObject(PROVINCE_JSON_KEY);
        JSONObject cityObject = locationBean.getJSONObject(CITY_JSON_KEY);
        JSONObject districtObject = locationBean.getJSONObject(DISTRICT_JSON_KEY);
        if (provinceObject != null) {
            province = provinceObject.getString(JSON_NAME_KEY);
            provinceCode = provinceObject.getInt(JSON_CODE_KEY);

        }

        if (cityObject != null) {
            city = cityObject.getString(JSON_NAME_KEY);
            cityCode = cityObject.getInt(JSON_CODE_KEY);
        }
        if (districtObject != null) {
            area = districtObject.getString(JSON_NAME_KEY);
            areaCode = districtObject.getInt(JSON_CODE_KEY);
            latitude = districtObject.getString(JSON_LATITUDE_KEY);
            longitude = districtObject.getString(JSON_LONGITUDE_KEY);
        }
    }
    private Integer recId;

    private String longitude;

    private String latitude;

    private String province;

    private String city;

    private String area;

    private Date addTime;
    private Integer provinceCode;
    private Integer cityCode;
    private Integer areaCode;

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getProvince() {
        if (StringUtils.isBlank(province)) {
            return StringUtils.EMPTY;
        }
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        if (StringUtils.isBlank(city)) {
            return StringUtils.EMPTY;
        }
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        if (StringUtils.isBlank(area)) {
            return StringUtils.EMPTY;
        }
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    /**
     * @return the provinceCode
     */
    public Integer getProvinceCode() {
        if (provinceCode == null) {
            return 0;
        }
        return provinceCode;
    }

    /**
     * @param provinceCode the provinceCode to set
     */
    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    /**
     * @return the cityCode
     */
    public Integer getCityCode() {
        if (cityCode == null) {
            return 0;
        }
        return cityCode;
    }

    /**
     * @param cityCode the cityCode to set
     */
    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    /**
     * @return the areaCode
     */
    public Integer getAreaCode() {
        if (areaCode == null) {
            return 0;
        }
        return areaCode;
    }

    /**
     * @param areaCode the areaCode to set
     */
    public void setAreaCode(Integer areaCode) {
        this.areaCode = areaCode;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LocationInfo [addTime=" + addTime + ", area=" + area + ", areaCode=" + areaCode + ", city=" + city + ", cityCode=" + cityCode + ", latitude="
                + latitude + ", longitude=" + longitude + ", province=" + province + ", provinceCode=" + provinceCode + ", recId=" + recId + "]";
    }
}
