package com.niubicloud.fileservice;

import java.util.HashMap;

public class FileUtil {
    private final static HashMap<String,String> mimeTable = new HashMap<String,String>();

    static {
        mimeTable.put("html","text/html");
        mimeTable.put("htm","text/html");
        mimeTable.put("css","text/css");
        mimeTable.put("xhtml","application/xhtml+xml");
        mimeTable.put("xml","application/xml");
        mimeTable.put("txt","text/plain");
        mimeTable.put("js","text/x-javascript");
        mimeTable.put("mjs","text/javascript");
        mimeTable.put("jsonld","application/ld+json");
        mimeTable.put("json","application/json");
        mimeTable.put("jar","application/java-archive");
        mimeTable.put("svg","image/svg+xml");

        mimeTable.put("png","image/png");
        mimeTable.put("webp","image/webp");
        mimeTable.put("jpg","image/jpeg");
        mimeTable.put("jpeg","image/jpeg");
        mimeTable.put("bmp","image/bmp");
    }

    public static String getSuffixName(String path) {
        String fileName = path.substring(path.lastIndexOf("\\")+1);
        String[] strArray = fileName.split("\\.");
        int suffixIndex = strArray.length -1;
        return strArray[suffixIndex];
    }

    public static String getMIME(String suffix){
        String s = mimeTable.get(suffix);
        return (s == null) ? "" : s;
    }

}
