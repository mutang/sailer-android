//package com.jkys.sailerxwalkview.util;
//
//import android.content.Context;
//import android.os.Environment;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//
///**
// * Created by on
// * Author: Zern
// * DATE: 16/7/25
// * Time: 10:29
// * Email:AndroidZern@163.com
// */
//public class DownFileUtils {
//
//    // 前者是服务器网址，后者是本地文件地址和文件名
//    public static boolean downLoadFile(String httpUrl, String locationFilePath) {
//        boolean successFlag = false;
//        InputStream is = null;
//        OutputStream os = null;
//        try {
//            URL url = new URL(httpUrl);
//            URLConnection con = url.openConnection();
////            int contentLength = con.getContentLength();
////            JkysLog.d("DownLoadZern", contentLength + "byte");
//            is = con.getInputStream();
//            byte[] bs = new byte[1024];
//            int len;
//            os = new FileOutputStream(locationFilePath);
//            // 开始下载了
//            while ((len = is.read(bs)) != -1) {
//                os.write(bs, 0, len);
//            }
//            // 下载完毕
//            os.flush();
//            successFlag = true;
//        } catch (MalformedURLException e) {
//            successFlag = false;
//            e.printStackTrace();
//        } catch (IOException e) {
//            successFlag = false;
//            e.printStackTrace();
//        } finally {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//                if (is != null) {
//                    is.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return successFlag;
//        }
//    }
//
//    private static File mShopDir;
//
//    public static File getmShopDir(Context context) {
//        if (mShopDir == null) {
//            mShopDir = context.getFilesDir();
//        }
//
//        if (mShopDir == null) {
//            mShopDir = Environment.getExternalStorageDirectory();
//        }
//        mShopDir.mkdirs();
//        return mShopDir;
//    }
//
//    public static String getShopDirPath(Context context) {
//        if (mShopDir == null) {
//            getmShopDir(context);
//        }
//        return mShopDir.getAbsolutePath() + "/build/www/shop";
//    }
//
//}
//
