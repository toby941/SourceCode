package com.sourcecode.android;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

public class FileCopy {

    static String fileName[] = new String[] { "AndroidManifest.xml", "proguard.cfg", "project.properties" };
    static String folderNames[] = new String[] { "src", "libs", "assets", "res" };

    public static void copyFile(String fromFolder, String toFolder) throws IOException {
        File f = new File(fromFolder);
        File newF = new File(toFolder);
        File[] list = f.listFiles();
        for (File file : list) {
            if (file.isDirectory() && ArrayUtils.contains(folderNames, file.getName())) {
                File destDir = new File(toFolder + "/" + file.getName());
                FileUtils.copyDirectory(file, destDir);
            } else if (file.isFile() && ArrayUtils.contains(fileName, file.getName())) {
                File destFile = new File(toFolder + "/" + file.getName());
                FileUtils.copyFile(file, destFile);
            }
        }
    }
    static String projectName[] = new String[] { "HanzoApp Freeora", "HanzoApp Freeblu", "HanzoApp Cowboy", "HanzoApp China", "HanzoApp Mail",
            "HanzoApp Cowboy2", "HanzoApp Art", "HanzoApp KawaiiBlue", "HanzoApp KawaiiPink", "HanzoApp KawaiiYellow" };

    static String copyProjectName[] = new String[] { "1", "2", "3", "4", "7", "8", "12", "14", "15", "16" };
    static String path = "D:\\workspace\\";
    static String toPath = "D:\\release\\mincow\\";
    static String oldLibFileName = "hazon_20120706.jar";
    static String workspacePath = "D:\\workspace\\";

    /**
     * 新旧lib替换
     * 
     * @param newLibPath
     * @throws IOException
     */
    public static void changeLibs(String newLibPath) throws IOException {
        File newLibsFile = new File(newLibPath);
        for (int i = 0; i < copyProjectName.length; i++) {
            File f = new File(workspacePath + projectName[i] + "\\libs");
            if (f != null && f.exists()) {
                File[] files = f.listFiles();
                for (File inFile : files) {
                    if (inFile.getName().toLowerCase().contains("hazon")) {
                        inFile.delete();
                        break;
                    }
                }
                FileUtils.copyFileToDirectory(newLibsFile, f);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < projectName.length; i++) {
            File f = new File(toPath + projectName[i]);
            if (f != null && f.exists()) {
                FileUtils.deleteDirectory(f);
            }
            copyFile(path + projectName[i], toPath + copyProjectName[i]);
        }
        // changeLibs("D://tmp//hazon_20120725.jar");

    }
}
