package com.sourcecode.ios;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KeyRename {

    public static String packageName = "com.q.airad.";
    public static String keyPath = "D://download//Keys";
    public static String keyStorePath = "D://driver//key//store//";
    public static String newKeyName = keyStorePath + packageName;

    public static List<File> changeKeyName() {
        File f = new File(keyPath);
        File[] files = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
        List<File> lisFiles = new ArrayList<File>();
        for (File file : files) {
            lisFiles.add(file);
        }
        Collections.sort(lisFiles, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return (int) ((f1.lastModified() - f2.lastModified()));
            }
        });

        return lisFiles;
    }

    public static void main(String[] args) {
        List<File> list = changeKeyName();
        System.out.println(list.size());
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).getName();
            list.get(i).renameTo(new File(newKeyName + name));
            System.out.println(list.get(i).getName());
        }
    }
}
