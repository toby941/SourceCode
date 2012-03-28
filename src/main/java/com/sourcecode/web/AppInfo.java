/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

import java.util.List;

/**
 * AppInfo.java
 * 
 * @author baojun
 */
public class AppInfo {

    private Integer appId;
    private Integer memberId;

    private String packageName;
    private String versionCode;
    private String versionName;
    private String sdkVersion;
    private List<String> usesPermission;
    private List<String> usesFeature;
    private String appName;
    private String icon;
    private byte[] iconbyte;
    private String launchActivity;
    private String[] densities;
    /**
     * aapt dump badging apk的原始信息
     */
    private String originalInfo;

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the versionCode
     */
    public String getVersionCode() {
        return versionCode;
    }

    /**
     * @param versionCode the versionCode to set
     */
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * @return the versionName
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * @param versionName the versionName to set
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * @return the sdkVersion
     */
    public String getSdkVersion() {
        return sdkVersion;
    }

    /**
     * @param sdkVersion the sdkVersion to set
     */
    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    /**
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the iconbyte
     */
    public byte[] getIconbyte() {
        return iconbyte;
    }

    /**
     * @param iconbyte the iconbyte to set
     */
    public void setIconbyte(byte[] iconbyte) {
        this.iconbyte = iconbyte;
    }

    /**
     * @return the launchActivity
     */
    public String getLaunchActivity() {
        return launchActivity;
    }

    /**
     * @param launchActivity the launchActivity to set
     */
    public void setLaunchActivity(String launchActivity) {
        this.launchActivity = launchActivity;
    }

    /**
     * @return the densities
     */
    public String[] getDensities() {
        return densities;
    }

    /**
     * @param densities the densities to set
     */
    public void setDensities(String[] densities) {
        this.densities = densities;
    }

    /**
     * @return the originalInfo
     */
    public String getOriginalInfo() {
        return originalInfo;
    }

    /**
     * @param originalInfo the originalInfo to set
     */
    public void setOriginalInfo(String originalInfo) {
        this.originalInfo = originalInfo;
    }

    /**
     * @return the usesPermission
     */
    public List<String> getUsesPermission() {
        return usesPermission;
    }

    /**
     * @param usesPermission the usesPermission to set
     */
    public void setUsesPermission(List<String> usesPermission) {
        this.usesPermission = usesPermission;
    }

    /**
     * @return the usesFeature
     */
    public List<String> getUsesFeature() {
        return usesFeature;
    }

    /**
     * @param usesFeature the usesFeature to set
     */
    public void setUsesFeature(List<String> usesFeature) {
        this.usesFeature = usesFeature;
    }

    /**
     * @return the appId
     */
    public Integer getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    /**
     * @return the memberId
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * @param memberId the memberId to set
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
