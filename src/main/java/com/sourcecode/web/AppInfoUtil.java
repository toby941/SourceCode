/*
 * Copyright 2012 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.log4j.Logger;

/**
 * AppName.java
 * 
 * @author baojun
 */
public class AppInfoUtil {

    private static Logger logger = Logger.getLogger(AppInfoUtil.class);

    private static String cmdExec = "aapt dump badging {0}";

    public static List<String> getAppInfo(String path) throws Exception {
        Process p = Runtime.getRuntime().exec(MessageFormat.format(cmdExec, path));
        // Process p =
        // Runtime.getRuntime().exec("aapt dump badging C:\\k9mail.apk ");
        return IOUtils.readLines(p.getInputStream());
    }

    private static String packageNameRegex = "package:\\sname='(\\S*)'\\sversionCode='\\S*'\\sversionName='\\S*'";
    private static String versionCodeRegex = "package:\\sname='\\S*'\\sversionCode='(\\S*)'\\sversionName='\\S*'";
    private static String versionNameRegex = "package:\\sname='\\S*'\\sversionCode='\\S*'\\sversionName='(\\S*)'";
    private static String sdkVersionRegex = "sdkVersion:'(\\S*)'";
    private static String usesPermissionRegex = "uses-permission:'(\\S*)'";
    private static String usesFeatureRegex = "uses-feature:'(\\S*)'";
    private static String appNameRegex = "application:\\slabel='(.*)' icon='.*'";
    private static String iconRegex = "application:\\slabel='.*'\\sicon='(\\S*)'";
    private static String launchableActivityRegex = "launchable-activity:\\sname='(\\S*)'\\s.*";

    private static String packageNameKey = "package: name";
    private static String versionCodeKey = "versionCode";
    private static String versionNameKey = "versionName";
    private static String sdkVersionKey = "sdkVersion";
    private static String usesPermissionKey = "uses-permission";
    private static String usesFeatureKey = "uses-feature";
    private static String appNameKey = "application: label";
    private static String iconKey = "icon";
    private static String launchableActivityKey = "launchable-activity";

    private static List<AppAnalyzeParameter> appAnalyzeParameters = new ArrayList<AppAnalyzeParameter>();

    private static String usesPermissionName = "usesPermission";
    private static String usesFeatureName = "usesFeature";

    static {
        AppAnalyzeParameter packageNameParameter = new AppAnalyzeParameter("packageName", packageNameRegex, packageNameKey);
        appAnalyzeParameters.add(packageNameParameter);

        AppAnalyzeParameter versionCodeParameter = new AppAnalyzeParameter("versionCode", versionCodeRegex, versionCodeKey);
        appAnalyzeParameters.add(versionCodeParameter);

        AppAnalyzeParameter versionNameParameter = new AppAnalyzeParameter("versionName", versionNameRegex, versionNameKey);
        appAnalyzeParameters.add(versionNameParameter);

        AppAnalyzeParameter sdkVersionParameter = new AppAnalyzeParameter("sdkVersion", sdkVersionRegex, sdkVersionKey);
        appAnalyzeParameters.add(sdkVersionParameter);

        AppAnalyzeParameter usesPermissionParameter = new AppAnalyzeParameter("usesPermission", usesPermissionRegex, usesPermissionKey);
        appAnalyzeParameters.add(usesPermissionParameter);

        AppAnalyzeParameter usesFeatureParameter = new AppAnalyzeParameter("usesFeature", usesFeatureRegex, usesFeatureKey);
        appAnalyzeParameters.add(usesFeatureParameter);

        AppAnalyzeParameter appNameParameter = new AppAnalyzeParameter("appName", appNameRegex, appNameKey);
        appAnalyzeParameters.add(appNameParameter);

        AppAnalyzeParameter iconParameter = new AppAnalyzeParameter("icon", iconRegex, iconKey);
        appAnalyzeParameters.add(iconParameter);

        AppAnalyzeParameter launchActivityParameter = new AppAnalyzeParameter("launchActivity", launchableActivityRegex, launchableActivityKey);
        appAnalyzeParameters.add(launchActivityParameter);

    }

    public static AppInfo analyzeApkInfo(String appPath) throws Exception {
        List<String> infoList = getAppInfo(appPath);
        AppInfo appInfo = null;
        StringBuffer origina = new StringBuffer();
        Matcher apkPathMatcher = memberIdAndAppIdPattern.matcher(appPath);
        Integer memberId = null;
        Integer appId = null;
        if (apkPathMatcher.matches()) {
            appInfo = new AppInfo();
            memberId = Integer.valueOf(apkPathMatcher.group(1));
            appId = Integer.valueOf(apkPathMatcher.group(2));
            appInfo.setMemberId(memberId);
            appInfo.setAppId(appId);
            if (CollectionUtils.isNotEmpty(infoList)) {
                for (int i = 0; i < infoList.size(); i++) {
                    String info = infoList.get(i);
                    origina.append(info);
                    for (int j = 0; j < appAnalyzeParameters.size(); j++) {
                        if (info.contains(appAnalyzeParameters.get(j).beginStr)) {
                            setAppInfo(info, appInfo, appAnalyzeParameters.get(j));
                        }
                    }
                }
            }
            appInfo.setOriginalInfo(origina.toString());
        } else {
            logger.error("apk path error: " + appPath);
        }
        return appInfo;
    }

    public static void setAppInfo(String info, AppInfo appInfo, AppAnalyzeParameter parameter) throws IllegalAccessException {
        Pattern pattern = Pattern.compile(parameter.regexExpression);
        Matcher matcher = pattern.matcher(info);
        if (matcher.find()) {
            String result = matcher.group(1);
            if (StringUtils.isNotBlank(result)) {
                if (!(usesPermissionName.equals(parameter.name) || usesFeatureName.equals(parameter.name))) {
                    FieldUtils.writeDeclaredField(appInfo, parameter.name, result, true);
                } else {
                    Object obj = FieldUtils.readDeclaredField(appInfo, parameter.name, true);
                    List<String> usesPermission = null;
                    if (obj != null) {
                        usesPermission = (List<String>) obj;
                    } else {
                        usesPermission = new ArrayList<String>();
                    }
                    usesPermission.add(result);
                    FieldUtils.writeDeclaredField(appInfo, parameter.name, usesPermission, true);
                }
            }
        }
    }

    private static String INSERT_SQL =
            "insert into CORE_APP_INFO(APP_NAME, APP_ID, MEMBER_ID, PACKAGE_NAME,VERSION_CODE, VERSION_NAME, SDK_VERSION, ICON,LANUCH_ACTIVITY, ORIGINAL_INFO)values (\"{0}\",\"{1}\",\"{2}\",\"{3}\",\"{4}\",\"{5}\", \"{6}\",\"{7}\",\"{8}\", \"{9}\");";

    public static void main(String[] args) throws Exception {
        // String filePath = "D:\\apkall\\1234\\1244_23342_assd.apk";
        // List<String> infoList = getAppInfo(filePath);
        // AppInfo appInfo = analyzeApkInfo(filePath);
        Integer count = 0;
        List<String> fileList = new ArrayList<String>();
        fileList = getFile("L:\\audit_app", count, "apk", fileList);
        List<String> dealFileList = handleDuplicateApk(fileList);
        System.out.println(dealFileList.size());
        writeSqlFile(dealFileList);
    }

    private static void writeSqlFile(List<String> dealFileList) throws Exception {
        File sqlFile = new File("D:\\app.sql");
        List<String> sqlList = new ArrayList<String>();
        for (int i = 0; i < dealFileList.size(); i++) {
            AppInfo appInfo = analyzeApkInfo(dealFileList.get(i));
            String sql = getInsertSql(appInfo);
            sqlList.add(sql);
        }
        FileUtils.writeLines(sqlFile, sqlList);
    }

    private static Pattern memberIdAndAppIdPattern = Pattern.compile(".*\\\\(\\d+)\\\\(\\d+)_(\\d+).+\\.apk$");

    /**
     * 按时间戳删选最新的apk <br/>
     * L:\app_new\1755\4865_1325084335461_mini_v.apk <br/>
     * L:\app_new\1755\4875_1325250981454_renrenphoto_m.apk <br/>
     * 
     * @param filePathList
     * @return
     */
    public static List<String> handleDuplicateApk(List<String> filePathList) {
        Map<String, String> appIdMap = new HashMap<String, String>();
        for (String filePath : filePathList) {
            Matcher matcher = memberIdAndAppIdPattern.matcher(filePath);
            if (matcher.matches()) {
                String appId = matcher.group(2);
                Long time = Long.valueOf(matcher.group(3));
                String fileStored = appIdMap.get(appId);
                if (StringUtils.isNotBlank(fileStored)) {
                    Matcher matcherExistFilePath = memberIdAndAppIdPattern.matcher(fileStored);
                    if (matcherExistFilePath.matches()) {
                        Long timeLast = Long.valueOf(matcher.group(3));
                        if (timeLast > time) {
                            appIdMap.put(appId, filePath);
                        }
                    }
                } else {
                    appIdMap.put(appId, filePath);
                }
            }
        }
        List<String> returnList = new ArrayList<String>();
        for (String key : appIdMap.keySet()) {
            String path = appIdMap.get(key);
            returnList.add(path);
            System.out.println(path);
        }
        return returnList;
    }

    public static List<String> getFile(String path, Integer count, String fileEnd, List<String> result) {
        File f = new File(path);
        File[] files = f.listFiles();

        for (File s : files) {
            if (s.isDirectory()) {
                getFile(s.getAbsolutePath(), count, fileEnd, result);
            } else {
                if (s.getName().toLowerCase().endsWith(fileEnd.toLowerCase())) {
                    result.add(s.getAbsolutePath());
                }
            }
        }
        return result;
    }

    /**
     * 获取icon图片
     * 
     * @param apkPath
     * @param appInfo
     * @throws Exception
     */
    public void getIconFile(String apkPath, AppInfo appInfo) throws Exception {
        JarFile jarFile = new JarFile(apkPath);
        ZipEntry entry = jarFile.getEntry(appInfo.getIcon());
        OutputStream outputStream = new FileOutputStream(new File("d://1.png"));
        IOUtils.copy(jarFile.getInputStream(entry), outputStream);
    }

    public static String getInsertSql(AppInfo appInfo) {
        return MessageFormat.format(INSERT_SQL, appInfo.getAppName(), appInfo.getAppId().toString(), appInfo.getMemberId().toString(),
                appInfo.getPackageName(), appInfo.getVersionCode(), appInfo.getVersionName(), appInfo.getSdkVersion(), appInfo.getIcon(),
                appInfo.getLaunchActivity(), appInfo.getOriginalInfo());
    }
}
