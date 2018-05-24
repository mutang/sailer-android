package com.jkys.sailerxwalkview.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/5/23
 * Time: 12:03
 * Email:AndroidZern@163.com
 */

public class SailerFileUtils {

    private static class SingletonHolder {
        private static final SailerFileUtils INSTANCE = new SailerFileUtils();
    }

    private SailerFileUtils() {
    }

    public static final SailerFileUtils getInstance() {
        return SailerFileUtils.SingletonHolder.INSTANCE;
    }

    // 为什么往外抛? 那是因为外面有同样的异常。而我需要不同处理。 方便出错管理。还是外部通过非空来判断吧。
    public String readFileToString(InputStream in) {
        String content = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = in.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            bos.close();
            in.close();
            content = new String(bos.toByteArray(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private File H5FileDir;

    // 创建离线的H5文件的路径
    private File getH5FileDir(Context context) {
        if (H5FileDir == null) {
            H5FileDir = context.getFilesDir();
        }
//        if (H5FileDir == null) {
//            H5FileDir = FileUtil.getExternalStorageDir(null);
//        }
//        H5FileDir.mkdirs();
//        if (H5FileDir == null) {
//            H5FileDir = FileUtil.getFilesDir(context);
//        }
        if (H5FileDir != null)
            H5FileDir.mkdirs();
        return H5FileDir;
    }

    // 获取离线的H5文件的路径
    public String getH5FileDirPath(Context context) {
        if (H5FileDir == null) {
            getH5FileDir(context);
        }
        return H5FileDir.getAbsolutePath();
    }

    /**
     * @param requestPath   需要下载的文件的url地址前缀
     * @param newDirPath    新的文件的目录 不一定是根目录,有可能是相对目录
     * @param differEntries 需要下载的文件的Map集合
     * @return true 表示是否全部下载并且都是完好的 false 表示其中有一个文件出错了 目前是一个出错全部都是错。
     * @throws IOException
     */
    public boolean DownFileWithRMap(String requestPath, String newDirPath, Set<Map.Entry<String, String>> differEntries) throws IOException {
        boolean flag = true; // 标志Map中所有文件是否全部下载并且都是完好的
        for (Map.Entry<String, String> entry : differEntries) {
            String fileName = entry.getKey();
            String fileMd5 = entry.getValue();
            String url = requestPath + "/" + fileName;
            String locationFilePath = newDirPath + "/" + fileName;
            makeFilewithSplit(locationFilePath);
            File needDownFile = new File(locationFilePath);
            if (!needDownFile.exists()) { // 是否没有下载过了 无则去网络下载
                if (!downFileAndCheckMD5(url, locationFilePath, needDownFile, fileMd5)) { // 如果下载中出错了 不再继续下载 终止循环
                    flag = false;
                    break;
                }
            } else { // 如果文件已经下载过了 那么判断下载的文件是否是对的。
                InputStream in = new FileInputStream(needDownFile);
                String fileMD5Now = digest(in, "MD5");
                if (!TextUtils.isEmpty(fileMD5Now) && fileMD5Now.equals(fileMd5)) {
                    Log.d("ZernDownLoad", "上次下载已成功了 这次检查到了 不进行操作");
                    in.close();
                } else {
                    Log.d("ZernDownLoad", "上次下载过 但是不完整 这次检查到了 删除之并下载");
                    needDownFile.delete();
                    if (!downFileAndCheckMD5(url, locationFilePath, needDownFile, fileMd5)) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    private int fileCount = 0;

    /**
     * @param url              需要下载的文件的url
     * @param locationFilePath 该文件的本地路径
     * @param needDownFile     该文件
     * @param fileMd5Rel       该文件的正确的MD5值
     * @return
     * @throws IOException
     */
    private boolean downFileAndCheckMD5(String url, String locationFilePath, File needDownFile, String fileMd5Rel) throws IOException {
        if (downLoadFile(url, locationFilePath)) {
            InputStream is = new FileInputStream(needDownFile); // 比对MD5值
            String downFileMD5 = digest(is, "MD5");
            if (!TextUtils.isEmpty(downFileMD5) && fileMd5Rel.equals(downFileMD5)) { // 下载正确
                Log.d("ZernH5File", "下载完毕。。。    " + ++fileCount);
                is.close();
                return true;
            } else {
                Log.d("ZernH5File", "" + "文件下载后MD不匹配。。。");
                // 那就删除那个出错的文件
                needDownFile.delete();
                return false;
            }
        } else {
            Log.d("ZernH5File", "" + "文件下载中出错了。。。");
            if (needDownFile.exists()) needDownFile.delete();
            return false;
        }
    }

    // 前者是服务器网址，后者是本地文件地址和文件名
    public boolean downLoadFile(String httpUrl, String locationFilePath) {
        boolean successFlag = false;
        InputStream is = null;
        OutputStream os = null;
        try {
            URL url = new URL(httpUrl);
            URLConnection con = url.openConnection();
            is = con.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            os = new FileOutputStream(locationFilePath);
            // 开始下载了
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
                Log.d("ZernH5Filedown", "bs -- " + bs + "---len---" + len);
            }
            // 下载完毕
            os.flush();
            successFlag = true;
        } catch (MalformedURLException e) {
            successFlag = false;
            e.printStackTrace();
        } catch (IOException e) {
            successFlag = false;
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return successFlag;
        }
    }

    // 获取到文件的MD5值
    public String digest(InputStream in, String algorithm) {
        String digestStr;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] buf = new byte[1024 * 2];
            int c;
            while ((c = in.read(buf)) != -1) {
                if (c > 0) {
                    md.update(buf, 0, c);
                }
            }
            byte[] digest = md.digest();
            digestStr = hexString(digest);
            return digestStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String hexString(byte[] bs) {
        StringBuilder sb = new StringBuilder(bs.length * 2);
        int b;
        for (byte b1 : bs) {
            b = b1;
            if (b < 0) {
                b += 256;
            }
            if (b < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    // file:///aaa/aa/aa 创建 aaa/aa的目录。 得到aa的文件
    public String makeFilewithSplit(String fileDirPath) {
        String fileDir = ""; // 最终获取到的文件目录根目录
        String fileName = "";
        if (fileDirPath.contains("/")) {
            String[] h5fileArr = fileDirPath.split("\\/");
            fileName = h5fileArr[h5fileArr.length - 1];
            // 取除了最后一个/ 后的字符串
            if (h5fileArr.length > 1) {
                // 这边怕以后会有同样的名字的多级目录 如 aa/bb/cc/aa/ee 这样。就会导致replace 不了最后一个匹配的
//                fileDir = fileDirPath.replace();
                for (int i = 0; i < h5fileArr.length - 1; i++) {
                    if (i == h5fileArr.length - 2) {
                        fileDir = fileDir + h5fileArr[i];
                    } else {
                        fileDir = fileDir + h5fileArr[i] + "/";
                    }
                }
                File file = new File(fileDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        } else {
            fileName = fileDirPath; // 如果没有/ 认为就是文件名字
        }
        return fileName;
    }

    /**
     * @param sameMap    需要Copy的文件map集合 都是绝对路径
     * @param newDirPath Copy到指定的目录
     * @param context
     * @throws IOException
     */
    public boolean copyFileFromAsset(Map<String, String> sameMap, String newDirPath, String repo, Context context) throws IOException {
        boolean flag = true;
        for (Map.Entry<String, String> entry : sameMap.entrySet()) {
            String fileName = entry.getKey();
            String fileMd5 = entry.getValue();
            String destFilePath = newDirPath + "/" + fileName;
            makeFilewithSplit(destFilePath);
            File file = new File(destFilePath);
            if (!file.exists()) { // 如果文件不存在就 copy 。
                if (!justCopyFile(repo, context, file, true, fileName, null)) { //如果copy不成功 就停止copy 下次再来copy 一般都是成功的
                    flag = false;
                    break;
                }
            } else { // 文件存在了 校验文件的完整性
                InputStream is = new FileInputStream(file);
                String fileMd5Now = digest(is, "MD5"); // 现已存在的文件的MD5值
                if (!TextUtils.isEmpty(fileMd5Now) && fileMd5Now.equals(fileMd5)) { // 确认了该文件缺失是上次copy成功了
                    Log.d("ZernCopy", "上次copy成功过 这次检查到了 不进行操作");
                    is.close();
                } else { // 上次copy过但是不完整 那么删除该继续copy文件
                    Log.d("ZernCopy", "上次copy失败了, 这次检查到了 再次进行copy");
                    file.delete(); // 那就删除那个出错的文件
                    if (!justCopyFile(repo, context, file, true, fileName, null)) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * copy文件。返回true表示copy成功。 false 表示copy失败
     *
     * @param repo                 H5的模块名称
     * @param context              普通的上下文
     * @param targetFile           copy目标文件
     * @param isFromAsset          标志是否从assets目录下copy
     * @param assetsSourceFilePath 当assets为true时 该值有效 指需要copy源文件的全路径
     * @param ExSourceFilePath     当assets为false时 指的是copy源文件的全路径
     * @throws IOException
     */
    public boolean justCopyFile(String repo, Context context, File targetFile, boolean isFromAsset, String assetsSourceFilePath, String ExSourceFilePath) throws IOException {
        InputStream is;
        long sizeBefore, sizeAfter;
        if (isFromAsset) {
            is = context.getAssets().open(repo + "/" + SailerManagerHelper.getInstance().getSailerProxyHelper().getConfigVersion() + "/" + assetsSourceFilePath);
        } else {
            is = new FileInputStream(ExSourceFilePath);
        }
        sizeBefore = is.available();
        FileOutputStream fos = new FileOutputStream(targetFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.flush();
        fos.close();
        is.close();
        sizeAfter = targetFile.length();
        Log.d("ZernSizeYes", sizeBefore + "---" + sizeAfter);
        if (sizeBefore == sizeAfter && sizeAfter != 0) {
            return true;
        } else {
            return false;
        }
    }

    // 从外部目录中copy文件
    public boolean copyFileFromExDir(String repo, Map<String, String> sameMap, String newDirPath, String localDirPath, Context context) throws IOException {
        boolean flag = true;
        for (Map.Entry<String, String> entry : sameMap.entrySet()) {
            String fileName = entry.getKey(); // 文件名
            String fileMd5 = entry.getValue(); // 该文件的MD5值
            String copyFilePath = localDirPath + "/" + fileName;
            String destFilePath = newDirPath + "/" + fileName;
            makeFilewithSplit(destFilePath);
            File file = new File(destFilePath);
            if (!file.exists()) { // 如果文件不存在
                if (!justCopyFile(repo, context, file, false, null, copyFilePath)) {
                    flag = false;
                    break;
                }
            } else { // 如果文件存在 验证该文件的完整性
                InputStream is = new FileInputStream(file);
                String fileMd5Now = digest(is, "MD5"); // 现已存在的文件的MD5值
                if (!TextUtils.isEmpty(fileMd5Now) && fileMd5Now.equals(fileMd5)) { // 确认了该文件缺失是上次copy成功了
                    Log.d("ZernDownCopy", "上次copy成功过 这次检查到了 不进行操作");
                    is.close();
                } else { // 上次copy过但是不完整 那么删除该继续copy文件
                    Log.d("ZernDownCopy", "上次copy失败了, 这次检查到了 再次进行copy");
                    file.delete(); // 那就删除那个出错的文件
                    if (!justCopyFile(repo, context, file, false, null, copyFilePath)) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 递归删除指定文件目录下面的所有的文件
     *
     * @param root 指定文件夹或者文件
     */
    public void deleteDir(File root) {
        Log.d("ZernDEL", "开始删除老文件");
        try {
            File files[] = root.listFiles();
            if (files != null && files.length != 0) { // 如果文件夹中有文件
                for (File f : files) {
                    if (f.isDirectory()) { // 是文件夹 继续删除里面的文件
                        Log.d("ZernDEL递归文件夹", "---" + f.getName());
                        deleteDir(f);
                    } else { // 是文件
                        if (f.exists()) {
                            f.delete();
                            Log.d("ZernDEL文件删除", "---" + f.getName());
                        }
                    }
                }
            } else { // 如果文件夹中没有文件了 直接删除
                Log.d("ZernDEL空文件夹", "--" + files + "---" + root.getName());
                root.delete();
            }
            if (root.exists()) {
                Log.d("ZernDEL空文件夹-父目录", "---" + files + "---" + root.getName());
                root.delete();
            }
        } catch (Exception ex) {
            Log.d("ZernDELEx", "---" + ex.getMessage() + "--");
            ex.printStackTrace();
        }
    }
}
