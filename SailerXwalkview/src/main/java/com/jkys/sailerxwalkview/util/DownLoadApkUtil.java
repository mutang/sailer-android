//package com.jkys.sailerxwalkview.util;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.support.v4.content.FileProvider;
//import android.text.TextUtils;
//
//import com.jkys.jkysbase.BaseCommonUtil;
//import com.jkys.jkysbase.FileUtil;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
///**
// * by xiaoke on 16/12/14.
// */
//
//public class DownLoadApkUtil {
//    public static String downLoadUrl = SailerManagerHelper.getInstance().getSailerProxyHelper().getDownloadApkUrl();
////    public static String downLoadUrl = BuildConfig.APK_URL + "files/apps/apk/zsty.apk";
//
//
//    public static void setDownLoadUrl(String downLoadUrl) {
//        DownLoadApkUtil.downLoadUrl = downLoadUrl;
//    }
//
//
//    DownLoadListener loadListener;
//    String urlstr;
//    File apkFile = null;
//    boolean isLoading;//是否还在下载
//    private Context ctx;
//
//    public DownLoadApkUtil(Context ctx, DownLoadListener loadListener, String urlstr) {
//        this.loadListener = loadListener;
//        this.urlstr = urlstr;
//        int index = downLoadUrl.lastIndexOf("/");
//        String fileName = downLoadUrl.substring(index + 1);
//        apkFile = createApkFileDir("apk", fileName);
////        apkFile = createApkFileDir("apk", "patient.apk");
//        this.ctx = ctx;
//    }
//
////    public DownLoadApkUtil() {
////        apkFile = createApkFileDir("apk", "patient.apk");
////    }
//
//
//    //启动下载任务
//    public File downLoadStart() {
//        File file = null;
//        synchronized (this) {
//            if (isLoading) return null;
//            isLoading = true;
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                downFile(urlstr);
//            }
//        }).start();
//        return file;
//    }
//
//    public String getApkFilePath() {
//        return apkFile.getPath();
//    }
//
//
//    public void cancelDownLoad() {
//        isLoading = false;
//
//    }
//
//    public boolean getLoadingState() {
//        return isLoading;
//    }
//
//    //下载过程
//    private File downFile(String urlstr) {
//        boolean isLoadSucess = false;
//        int totalLenth = 0;// the size of package
//        int apkLoadLength = 0;//the size of current loading package
//        InputStream inputStream = null;
//        FileOutputStream outputStream = null;
//        try {
//            if (apkFile == null) {
//                apkFile = createApkFileDir("apk", "patient.apk");
//            }
//            URL url = new URL(urlstr);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setConnectTimeout(5000);
//            inputStream = connection.getInputStream();
//            if (inputStream != null) {
//                loadListener.totalLength(connection.getContentLength());
//                totalLenth = connection.getContentLength();
//                if (!apkFile.exists()) {
//                    createApkFileDir("apk", "patient.apk");
//                }
//                outputStream = new FileOutputStream(apkFile);
//                byte[] buffer = new byte[1024];
//                int length = 0;
//                while ((length = inputStream.read(buffer)) != -1 && isLoading) {
//                    outputStream.write(buffer, 0, length);
//                    apkLoadLength += length;
//                    loadListener.loadProgress(apkLoadLength);
//                }
//
//                if (apkLoadLength == connection.getContentLength()) {
//                    isLoading = false;
//                    isLoadSucess = true;
//
//                }
//            }
//
//        } catch (Exception e) {
//            isLoading = false;
//            if (apkFile != null && !apkFile.exists()) {
//                loadListener.fileNotFound();
//            } else if (totalLenth > FileUtil.getExternalStorageAvailableSize()) {
//                loadListener.loadErrorNoSpare();
//            } else {
//                loadListener.loadError();
//            }
//
//        } finally {
//
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                    outputStream.flush();
//                } catch (IOException e) {
//                }
//            }
//
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                }
//            }
//
//            if (isLoadSucess) {
//                loadListener.loadSuccess();
//            }
//
//        }
//
//        return apkFile;
//    }
//
//
//    //创建apk目录在sd卡中
//    private File createApkFileDir(String apkPath, String fileName) {
//        String sdPath = null;
//        File fileRoot = FileUtil.getExternalStorageDir(ctx);
//        if (fileRoot != null)
//            sdPath = fileRoot.getAbsolutePath();
//        if (!TextUtils.isEmpty(sdPath)) {
//            if (apkPath.startsWith(File.separator)) {
//                sdPath = sdPath + apkPath;
//            } else {
//                sdPath = sdPath + File.separator + apkPath;
//            }
//        }
//        if (!TextUtils.isEmpty(sdPath)) {
//            File file = new File(sdPath);
//            if (!file.exists()) {
//                boolean iscreate = file.mkdirs();
//            }
//        }
//
//        File apkFile = null;
//        if (!TextUtils.isEmpty(sdPath)) {
//            apkFile = new File(sdPath, fileName);
//            if (!apkFile.exists()) {
//                try {
//                    apkFile.createNewFile();
//                } catch (Exception ex) {
//                }
//            }
//        }
//
//        return apkFile;
//    }
//
////    public static String getApkPath(String apkPath) {
////        String sdPath = null;
////        if (UuidUtils.checkExternalStorage()) {
////            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
////            if (!TextUtils.isEmpty(sdPath)) {
////                if (apkPath.startsWith(File.separator)) {
////                    sdPath = sdPath + apkPath;
////                } else {
////                    sdPath = sdPath + File.separator + apkPath;
////                }
////            }
////        }
////
////        return sdPath;
////    }
//
//
//    /**
//     * 安装APK
//     */
//    public void installApk(Context context) {
//        //获取系統版本
//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri apkUri;
//        if (currentapiVersion < 24) {
//            // 从文件中创建uri
//            apkUri = Uri.fromFile(apkFile);
//        } else {
//            //兼容android7.0 使用共享文件的形式
//            apkUri = FileProvider.getUriForFile(context, BaseCommonUtil.getFileAuthority(),
//                    apkFile);//这里进行替换uri的获得方式
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//这里加入flag
//        }
//
//        if (!SailerManagerHelper.getInstance().getSailerProxyHelper().isPhone()) { // 如果它是一体机
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        context.startActivity(intent);
//
////        if (SailerManagerHelper.getInstance().getSailerProxyHelper().isPhone()) { // 如果它是一体机
////            BaseTopActivity.finishAll();
////        }
//    }
//
//
//    /**
//     * 打开已经安装好的apk
//     */
//    public void openApk(Context context) {
//        PackageManager manager = context.getPackageManager();
//        // 这里的是你下载好的文件路径
//        PackageInfo info = manager.getPackageArchiveInfo(apkFile.getPath(), PackageManager.GET_ACTIVITIES);
//        if (info != null) {
//            Intent intent = manager.getLaunchIntentForPackage(info.applicationInfo.packageName);
//            context.startActivity(intent);
//        }
//    }
//
//    public interface DownLoadListener {
//        public void loadProgress(int length);
//
//        public void totalLength(int totalLenght);
//
//        public void loadError();
//
//        public void loadSuccess();
//
//        public void loadErrorNoSpare();
//
//        public void fileNotFound();
//    }
//
//}
