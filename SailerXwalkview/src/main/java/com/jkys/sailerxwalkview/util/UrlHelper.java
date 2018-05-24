package com.jkys.sailerxwalkview.util;

import android.webkit.MimeTypeMap;
import java.util.HashMap;

/**
 * 自定义url缓存匹配规则检查工具类。
 * <p/>
 * 根据一组自定义的匹配规则来确定某个特定的url是否符合。
 */
public class UrlHelper {

    // Utility Class instantiate prohibited.
    private UrlHelper() {
    }

    public static String getSuffix(String url) {
        String suffix = "unknown";
        try {
            suffix = MimeTypeMap.getFileExtensionFromUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (suffix != null && !suffix.equals("") && !suffix.equals("unknown")) {
            return suffix;
        }
        suffix = "unknown";
        int suffixIndex = -1;
        int markIndex = url.indexOf("?");// 整个url中问号索引
        if (markIndex != -1) {
            // 有问号，解决问号后的参数中有.的bug
            suffixIndex = url.substring(0, markIndex).lastIndexOf(".");
        } else {
            suffixIndex = url.lastIndexOf(".");
        }
        if (suffixIndex != -1 && suffixIndex < url.length() - 1) {
            suffix = url.substring(suffixIndex + 1).toLowerCase();
            markIndex = suffix.indexOf("?");// 后缀之后问号索引
            if (markIndex != -1) {
                suffix = suffix.substring(0, markIndex);
            }
            // 判断包含fragment的情形
            int fragmentIndex = suffix.indexOf("#");
            if (fragmentIndex != -1) {
                suffix = suffix.substring(0, fragmentIndex);
            }
        }
        return suffix;
    }


    /**
     * 根据url获取mimeType,这里简单的根据后缀来判断
     *
     * @param url
     * @return
     */
    public static String getMimeType(String url) {
        String mimeType = null;
        String fileExtension = null;
        try {
            fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mimeType == null) {
            if (fileExtension != null
                    && (fileExtension.equalsIgnoreCase("php") || fileExtension.equalsIgnoreCase("htm")
                    || fileExtension.equalsIgnoreCase("html") || fileExtension.equalsIgnoreCase("hts"))) {
                mimeType = "text/html";
            } else if (fileExtension != null && fileExtension.equalsIgnoreCase("js")) {
                mimeType = "application/x-javascript";
            } else if (fileExtension != null && fileExtension.equalsIgnoreCase("css")) {
                mimeType = "text/css";
            } else if (fileExtension != null
                    && (fileExtension.equalsIgnoreCase("java") || fileExtension.equalsIgnoreCase("xml")
                    || fileExtension.equalsIgnoreCase("txt"))) {
                mimeType = "text/plain";
            }
//            else if (fileExtension != null && fileExtension.equalsIgnoreCase("ico")) {
//                mimeType = "application/octet-stream";
//            }
            else if (fileExtension != null
                    && (fileExtension.equalsIgnoreCase("jpe") || fileExtension.equalsIgnoreCase("jpeg")
                    || fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpz"))) {
                mimeType = "image/jpeg";
            } else if (fileExtension != null
                    && (fileExtension.equalsIgnoreCase("png") || fileExtension.equalsIgnoreCase("pnz"))) {
                mimeType = "image/png";
            } else if (fileExtension != null
                    && (fileExtension.equalsIgnoreCase("gif") || fileExtension.equalsIgnoreCase("ifm"))) {
                mimeType = "image/gif";
            } else if (fileExtension != null && fileExtension.equalsIgnoreCase("css")) {
                mimeType = "text/css";
            }
        }
        return mimeType;
    }

    /**
     * 获取参数
     *
     * @return
     */
    public static HashMap<String, String> getQuery(String url) {
        HashMap<String, String> queryMap = new HashMap<>();
        if (url == null) {
            return queryMap;
        }
        int index = url.lastIndexOf("?");
        String queryStr = null;
        if (index != -1) {
            queryStr = url.substring(index + 1, url.length());
            String[] queryArr = queryStr.split("&");
            if (queryArr != null && queryArr.length > 0) {
                for (int i = 0; i < queryArr.length; i++) {
                    String[] keyValue = queryArr[i].split("=");
                    if (keyValue != null && keyValue.length == 2) {
                        queryMap.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }
        return queryMap;
    }

    /**
     * 是否包含该参数
     * @param url
     * @param key
     * @return
     */
    public static boolean isMatchQuery(String url,String key){
        HashMap<String,String> queryMap = getQuery(url);
        if(queryMap.containsKey(key))
            return true;
        else
            return false;
    }

    /**
     * 获取参数值
     * @param url
     * @param key
     * @return
     */
    public static String getQueryValue(String url,String key){
        HashMap<String,String> queryMap = getQuery(url);
        return queryMap.get(key);
    }
}
