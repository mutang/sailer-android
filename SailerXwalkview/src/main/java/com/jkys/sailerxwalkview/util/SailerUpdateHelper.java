package com.jkys.sailerxwalkview.util;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/4/27
 * Time: 15:12
 * Email:AndroidZern@163.com
 */

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.jkys.sailerxwalkview.action.SailerActionHandler;
import com.jkys.sailerxwalkview.activity.BaseTopActivity;
import com.jkys.sailerxwalkview.dbservice.SailerDBService;
import com.jkys.sailerxwalkview.dbservice.SailerSQLData;
import com.jkys.sailerxwalkview.event.CopyAssetOrDownLoadStatus;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SailerH5离线文件更新和替换类
 */
public class SailerUpdateHelper {
    private static class SingletonHolder {
        private static final SailerUpdateHelper INSTANCE = new SailerUpdateHelper();
    }

    private SailerUpdateHelper() {
    }

    public static final SailerUpdateHelper getInstance() {
        return SailerUpdateHelper.SingletonHolder.INSTANCE;
    }

    // 获取Asset目录下面的所有的文件名称和对应的MD5的值 保存为Map
    private boolean getAssetFileMap(Map<String, String> locationFileMap, String[] fileNames, Context context) {
        if (fileNames.length > 0) { //说明是目录
            InputStream is;
            try {
                for (String fileName : fileNames) {
                    is = context.getAssets().open(SailerActionHandler.SHOP_ASSEST_FILE_PATH + "/" + fileName);
                    String fileMD5 = FileMD5Utils.digest(is, "MD5");
                    if (!TextUtils.isEmpty(fileMD5)) {
                        locationFileMap.put(fileName, fileMD5);
                        is.close();
                    } else {
                        // MD5获取错误
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else { // 说明下面没有文件 全部下载
            return true;
        }
        return true;
    }

    // 获取H5外部文件存储目录下面的所有的文件名称和对应的MD5的值 保存为Map
    private boolean getExternalFileMap(Map<String, String> locationFileMap, String localDirPath, String[] fileNames) {
        InputStream is;
        if (fileNames.length > 0) { //说明是目录
            try {
                for (String fileName : fileNames) {
                    File file = new File(localDirPath + "/" + fileName);
                    if (file.exists()) { // 如果文件存在
                        is = new FileInputStream(file);
                        String fileMD5 = FileMD5Utils.digest(is, "MD5");
                        if (!TextUtils.isEmpty(fileMD5)) {
                            locationFileMap.put(fileName, fileMD5);
                            is.close();
                        } else {
                            // MD5获取错误
                            return false;
                        }
                    } else {
                        // 文件不存在
                        return false;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else { // 说明是文件夹里面没有文件了 那就全部下载
            return true;
        }
        return true;
    }

    public void checkUpdate(final Context context) {
        final Context aContext = context.getApplicationContext();
        if (!isNetworkAvailable(aContext)) { // 首先判断网络是否通  如果不可用
            // 判断本地存储的最新版本,copy一下assets资源到外部文件夹
            SailerUpdateHelper.getInstance().CopyAssetWithNotNet();
            if (SailerActionHandler.isH5CallCheckUpdate) { // 院内一体机加的需求 无网络连接提示
                SailerActionHandler.isH5CallCheckUpdate = false;
                EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
            }
        } else {
            // 正在判断是否需要更新
            if (!SailerActionHandler.isUpDating) {
                SailerActionHandler.isUpDating = true;
                if (SailerManagerHelper.getInstance().getSailerProxyHelper().checkH5Version() == null) {// 如果是该项目没有配置这个版本接口 那就直接访问数据Detail接口
//                    SailerNetManager.getInstance(aContext).requestH5FromNet(SailerManagerHelper.getInstance().getSailerProxyHelper().checkH5UpDateData(), context.getApplicationContext());
                } else {
//                    SailerNetManager.getInstance(aContext).requestH5FromNet(SailerManagerHelper.getInstance().getSailerProxyHelper().checkH5Version(), context.getApplicationContext());
                }
            }
        }
    }

    /**
     * 是否网络可用
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    public void startUpdateH5Files(final Context context, final String response) {
        ThreadPoolTools.getInstance().postWorkerTask(new Runnable() {
            @Override
            public void run() {
                boolean forceUpdate = false;
                try {
                    JSONObject remoteJsonOb = new JSONObject(response);
                    // 服务器版本号
                    int remoteVcode = remoteJsonOb.getInt("version");
                    // 服务器的repo 本次更新模块目录
//                    String repo = remoteJsonOb.getString("repo");
//                    String repo = "shop";
                    String repo = SailerManagerHelper.getInstance().getSailerProxyHelper().getCurrentSailerRepo();
                    // 本地数据库版本号
                    int locationVcode = (int) SpUtil.getSP(context, SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.ERROR_VCODE);
                    // 新增的强更新代码
                    forceUpdate = remoteJsonOb.optBoolean("forceUpdate", false);
                    // 如果是强更新让H5页面停止交互。
                    EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Start));
//                        if (forceUpdate) {
//                            EventBus.getDefault().post(new ShopForceUpDateEvent(ShopForceUpDateEvent.Start));
//                        }
                    String requestPath = remoteJsonOb.getString("requestPath");
                    String filesJsonStr = remoteJsonOb.getString("files");

                    Map<String, String> locationFileMap = SailerDBService.getInstance().findAll();
                    if (locationFileMap.size() == 0) { // 如果数据库中没有数据 说明第一次更新。 那么从config.json中去拿
//                        InputStream inputStream = context.getAssets().open(SailerActionHandler.CONFIG_SHOP);
                        InputStream inputStream = context.getAssets().open(SailerManagerHelper.getInstance().getSailerProxyHelper().getRepoConfigJsonRelpath());
                        String gluconfig_json = SailerFileUtils.getInstance().readFileToString(inputStream);// 读取asset目录下面的config.json文件
                        JSONObject jsonObject = new JSONObject(gluconfig_json);
                        String files = jsonObject.getString("files");
                        Type type = new TypeToken<Map<String, String>>() {
                        }.getType();
                        locationFileMap = GsonUtil.getCommonGson().fromJson(files, type);
                    }

                    //----代码到这边为止已经拿到本地H5的所有文件的name和MD5的Map集合---------------
                    Type type = new TypeToken<Map<String, String>>() {
                    }.getType();
                    Map<String, String> remoteMap = GsonUtil.getCommonGson().fromJson(filesJsonStr, type);
                    Set<Map.Entry<String, String>> remoteEntries = remoteMap.entrySet();

                    // 远程和本地Map的差集 download
                    Map<String, String> differMap = new HashMap<>();
                    // 远程和本地Map的交集 copy
                    Map<String, String> sameMap = new HashMap<>();

                    for (Map.Entry<String, String> remoteEntry : remoteEntries) {
                        // 先找key相同的, 也就是说文件名字一样的比对
                        if (locationFileMap.containsKey(remoteEntry.getKey())) {
                            String localV = locationFileMap.get(remoteEntry.getKey());
                            String remoteV = remoteEntry.getValue();
                            if (localV.equals(remoteV)) { // 如果MD5 匹配
                                sameMap.put(remoteEntry.getKey(), remoteV);
                            } else {
                                differMap.put(remoteEntry.getKey(), remoteV);
                            }
                        } else {  // 本地没有这个文件，说明也是需要下载的。
                            differMap.put(remoteEntry.getKey(), remoteEntry.getValue());
                        }
                    }

                    // 本地更新后的文件目录路径
                    String newDirPath = SailerFileUtils.getInstance().getH5FileDirPath(context) + "/" + repo + "/" + remoteVcode;
                    Set<Map.Entry<String, String>> differEntries = differMap.entrySet();

                    // 下载需要更新的文件的Map 如果出错了 那就不继续了
                    if (!SailerFileUtils.getInstance().DownFileWithRMap(requestPath, newDirPath, differEntries)) {
                        SailerActionHandler.isUpDating = false;
                        EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.DownFileError));
//                            if (forceUpdate)
//                                EventBus.getDefault().post(new ShopForceUpDateEvent(ShopForceUpDateEvent.Fail));
                        return;
                    }

                    // 全部文件下载完毕 开始Copy文件 同样还是区别下
                    String localDirPath = SailerFileUtils.getInstance().getH5FileDirPath(context) + "/" + repo + "/" + locationVcode;
                    if (locationVcode == SailerActionHandler.ERROR_VCODE) { // 如果本地版本是0 那说明要从资源目录中去copy 复现情况是 第一次安装。遇到H5版本更新。 下载没有问题但是copy就会有问题。 因为没有0版本代码
                        if (!SailerFileUtils.getInstance().copyFileFromAsset(sameMap, newDirPath, repo, context)) { // 从asset目录中copy文件 不成功就结束本次更新
                            SailerActionHandler.isUpDating = false;
                            EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
                            return;
                        }
                    } else {
                        if (!SailerFileUtils.getInstance().copyFileFromExDir(repo, sameMap, newDirPath, localDirPath, context)) { // 如果copy过程中有一个文件copy失败了 整个本次更新作废。下次再来。
                            SailerActionHandler.isUpDating = false;
                            EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
                            return;
                        }
                    }
                    // 终于所有的文件都copy和下载完毕了 接下来就是修改替换之前的读取的文件目录  修改本地版本versionJSON
                    List<SailerSQLData> datas = new LinkedList<>();
                    for (Map.Entry<String, String> stringStringEntry : remoteMap.entrySet()) {
                        datas.add(new SailerSQLData(stringStringEntry.getKey(), stringStringEntry.getValue()));
                    }
                    SailerDBService.getInstance().updateList(datas);
                    SailerActionHandler.NEW_H5FILES_VERSION = remoteVcode;
//                    HandleH5Utils.CURRENT_H5FILES_VERSION = locationVcode;//当前使用的旧版本
                    SailerActionHandler.CURRENT_H5FILES_REPO = repo;
//                    if (SailerActionHandler.CURRENT_H5FILES_VERSION == SailerActionHandler.ERROR_VCODE) {// 如果是之前老版本没有存过 那么直接替换成最新的版本号
//                        SailerActionHandler.CURRENT_H5FILES_VERSION = SailerActionHandler.NEW_H5FILES_VERSION;
//                    }
                    SailerActionHandler.CURRENT_H5FILES_VERSION = SailerActionHandler.NEW_H5FILES_VERSION;
                    SpUtil.inputSP(context.getApplicationContext(), SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.NEW_H5FILES_VERSION);
                    EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.DownFileFinish));
                    if (locationVcode < SailerActionHandler.CURRENT_H5FILES_VERSION) { // 如果老版本比当前版本底 我才去删除老版本。这里防止当前和老版本一样大小时误删除当前版本的文件。
                        SailerFileUtils.getInstance().deleteDir(new File(localDirPath)); // 删除老版本文件。不保留!
                    }
                    SailerActionHandler.isUpDating = false;
//                        if (forceUpdate) {
//                            EventBus.getDefault().post(new ShopForceUpDateEvent(ShopForceUpDateEvent.Finish));
//                        }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    SailerActionHandler.isUpDating = false;
                    EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
//                    if (forceUpdate)
//                        EventBus.getDefault().post(new ShopForceUpDateEvent(ShopForceUpDateEvent.Fail));
                } catch (IOException e) {
                    e.printStackTrace();
                    SailerActionHandler.isUpDating = false;
                    EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
//                    if (forceUpdate)
//                        EventBus.getDefault().post(new ShopForceUpDateEvent(ShopForceUpDateEvent.Fail));
                } catch (JSONException e) {
                    e.printStackTrace();
                    SailerActionHandler.isUpDating = false;
                    EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
//                    if (forceUpdate)
//                        EventBus.getDefault().post(new ShopForceUpDateEvent(ShopForceUpDateEvent.Fail));
                }
            }
        });
    }

//    private void startUpdateShop(final Context context, final String response, final String locationVJson) {
//        ThreadPoolTools.getInstance().postWorkerTask(new Runnable() {
//            @Override
//            public void run() {
//                boolean forceUpdate = false;
//                try {
//                    JSONObject remoteJsonOb = new JSONObject(response);
//                    int remoteVcode = remoteJsonOb.getInt("version");
//                    // 新增的强更新代码
//                    forceUpdate = remoteJsonOb.optBoolean("forceUpdate", false);
//
//                    JSONObject locationJsonOb = new JSONObject(locationVJson);
//                    int locationVcode = locationJsonOb.getInt("version");
//                    // 判断是否要更新
//                    if (locationVcode < remoteVcode) {
//                        // 如果是强更新让商城界面停止交互。
//                        if (forceUpdate) {
//                            EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Start));
//                        }
//                        String requestPath = remoteJsonOb.getString("requestPath");
//                        String filesJsonStr = remoteJsonOb.getString("files");
//                        int fileCount = 0;
//                        // 首先要拿到本地所有H5的文件名字和MD5值 后面要差量更新
//                        Map<String, String> locationFileMap = new HashMap<>();
//                        // 获取H5热更新次数 默认为0
//                        int updateTimes = (int) SpUtil.getSP(context, SailerActionHandler.KEY_UPDATE_TIMES, 0);
//                        if (updateTimes == 0) { // 如果没有更新过，或者更新失败了则去拿assets资产目录下面的H5所有文件
//                            String[] fileNames = context.getAssets().list(SailerActionHandler.SHOP_ASSEST_FILE_PATH);
//                            // 给本地H5文件map集合赋值，如果出错，就直接return方法。
//                            if (!getAssetFileMap(locationFileMap, fileNames, context)) {
//                                SailerActionHandler.isUpDating = false;
//                                if (forceUpdate)
//                                    EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Fail));
//                                return;
//                            }
//                        } else if (updateTimes > 0) { // 如果有更新过，那么去拿外存文件目录下面的H5的所有文件
//                            String localDirPath = DownFileUtils.getShopDirPath(context) + "/" + locationVcode;
//                            File file = new File(localDirPath);
//                            if (file.isDirectory()) {
//                                String[] fileNames = file.list();
//                                if (!getExternalFileMap(locationFileMap, localDirPath, fileNames)) {
//                                    SailerActionHandler.isUpDating = false;
//                                    if (forceUpdate)
//                                        EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Fail));
//                                    return;
//                                }
//                            }
//                        }
//
//                        //----代码到这边为止已经拿到本地H5的所有文件的name和MD5的Map集合---------------
//                        Type type = new TypeToken<Map<String, String>>() {
//                        }.getType();
//                        Map<String, String> remoteMap = Constant.GSON.fromJson(filesJsonStr, type);
//
//                        Set<Map.Entry<String, String>> remoteEntries = remoteMap.entrySet();
//                        Set<Map.Entry<String, String>> localEntries = locationFileMap.entrySet();
//
//                        // 远程和本地Map的差集 download
//                        Map<String, String> differMap = new HashMap<>();
//                        // 远程和本地Map的交集 copy
//                        Map<String, String> sameMap = new HashMap<>();
//
//                        for (Map.Entry<String, String> remoteEntry : remoteEntries) {
//                            // 先找key相同的, 也就是说文件名字一样的比对
//                            if (locationFileMap.containsKey(remoteEntry.getKey())) {
//                                String localV = locationFileMap.get(remoteEntry.getKey());
//                                String remoteV = remoteEntry.getValue();
//                                if (localV.equals(remoteV)) { // 如果MD5 匹配
//                                    sameMap.put(remoteEntry.getKey(), remoteV);
//                                } else {
//                                    differMap.put(remoteEntry.getKey(), remoteV);
//                                }
////                                for (Map.Entry<String, String> localEntry : localEntries) {
////                                    // 如果文件名字一样
////                                    if (remoteEntry.getKey().equals(localEntry.getKey())) {
////                                        // 文件MD5一样，说明不需要下载，但是需要Copy
////                                        if (remoteEntry.getValue().equals(localEntry.getValue()))
////                                            sameMap.put(remoteEntry.getKey(), remoteEntry.getValue());
////                                        else // 文件名字一样，但是MD5不一样，需要下载
////                                            differMap.put(remoteEntry.getKey(), remoteEntry.getValue());
////                                    }
////                                }
//                            } else {  // 本地没有这个文件，说明也是需要下载的。
//                                differMap.put(remoteEntry.getKey(), remoteEntry.getValue());
//                            }
//                        }
//
//                        // 本地更新后的文件目录路径
//                        String newDirPath = DownFileUtils.getShopDirPath(context) + "/" + remoteVcode;
//                        File fileDir = new File(newDirPath);
//                        if (!fileDir.exists()) {
//                            fileDir.mkdirs();
//                        }
//                        Set<Map.Entry<String, String>> differEntries = differMap.entrySet();
//                        // 下载需要更新的文件的Map
//                        for (Map.Entry<String, String> entry : differEntries) {
//
//                            String fileName = entry.getKey();
//                            String fileMd5 = entry.getValue();
//
//                            String url = requestPath + "/" + fileName;
//                            String locationFilePath = newDirPath + "/" + fileName;
//
//                            // 如果下载完毕了
//                            if (DownFileUtils.downLoadFile(url, locationFilePath)) {
//                                // 比对MD5值
//                                File file = new File(locationFilePath);
//                                InputStream is = new FileInputStream(file);
//                                if (file.exists()) {
//                                    String downFileMD5 = FileMD5Utils.digest(is, "MD5");
//                                    // 下载成功了，而且MD也匹配 哇塞。
//                                    if (!TextUtils.isEmpty(downFileMD5) && fileMd5.equals(downFileMD5)) {
//                                        Log.d("DownLoadZern", "下载完毕。。。    " + ++fileCount);
//                                        is.close();
//                                    } else {
//                                        Log.d("DownLoadZern", "" + "文件下载出错。。。");
//                                        // 那就删除那个出错的文件
//                                        file.delete();
//                                        SailerActionHandler.isUpDating = false;
//                                        if (forceUpdate)
//                                            EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Fail));
//                                        return;
//                                    }
//                                }
//                            } else {
//                                Log.d("DownLoadZern", "" + "文件下载出错。。。");
//                                File file = new File(locationFilePath);
//                                if (file.exists()) file.delete();
//                                SailerActionHandler.isUpDating = false;
//                                if (forceUpdate)
//                                    EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Fail));
//                                return;
//                            }
//                        }
//
//                        // 全部文件下载完毕 开始Copy文件 同样还是区别下
//                        File file = new File(newDirPath);
//                        if (file.exists() && file.isDirectory()) {
//                            // 如果是第一次热更 则去asset目录下copy相同的文件
//                            if (updateTimes == 0) {
//                                for (String sameFileName : sameMap.keySet()) {
//                                    String copyFilePath = SailerActionHandler.SHOP_ASSEST_FILE_PATH + "/" + sameFileName;
//                                    String destFilePath = newDirPath + "/" + sameFileName;
//                                    InputStream is = context.getAssets().open(copyFilePath);
//                                    File newFile = new File(destFilePath);
//                                    FileOutputStream fos = new FileOutputStream(newFile);
//                                    byte[] buffer = new byte[1024];
//                                    int len;
//                                    while ((len = is.read(buffer)) != -1) {
//                                        fos.write(buffer, 0, len);
//                                    }
//                                    fos.flush();
//                                    fos.close();
//                                    is.close();
//                                }
//                            } else if (updateTimes > 0) {
//                                String localDirPath = DownFileUtils.getShopDirPath(context) + "/" + locationVcode;
//                                for (String sameFileName : sameMap.keySet()) {
//                                    String copyFilePath = localDirPath + "/" + sameFileName;
//                                    String destFilePath = newDirPath + "/" + sameFileName;
//                                    FileInputStream fis = new FileInputStream(copyFilePath);
//                                    File newFile = new File(destFilePath);
//                                    FileOutputStream fos = new FileOutputStream(newFile);
//                                    byte[] buffer = new byte[1024];
//                                    int len;
//                                    while ((len = fis.read(buffer)) != -1) {
//                                        fos.write(buffer, 0, len);
//                                    }
//                                    fos.flush();
//                                    fos.close();
//                                    fis.close();
//                                }
//
//                            }
//                        }
//
//                        // 终于所有的文件都copy和下载完毕了 接下来就是修改替换之前的读取的文件目录  修改本地版本versionJSON
//                        SpUtil.inputSP(context, SailerActionHandler.KEY_SHOP_VERSION_JSON, response);
//                        SpUtil.inputSP(context, SailerActionHandler.KEY_UPDATE_TIMES, ++updateTimes);
//                        SpUtil.inputSP(context, SailerActionHandler.KEY_SHOP_INDEX_URL, "file://" + newDirPath);
//                        SpUtil.inputSP(context, SailerActionHandler.KEY_REPO_VERSION_CODE, remoteVcode);
//                        SailerActionHandler.isUpDating = false;
//                        Log.d("DownLoadZern", "恭喜您，热更新成功!!!");
//                        if (forceUpdate) {
//                            EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Finish));
//                            // 因为强更新。直接修改所有的URl路径 再次初始化Url
//                            SailerActionHandler.IndexDir = SailerManagerHelper.getInstance().getSailerProxyHelper().getUrlIndex(context);
//                        }
//                    } else {
//                        SailerActionHandler.isUpDating = false;
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    SailerActionHandler.isUpDating = false;
//                    if (forceUpdate)
//                        EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Fail));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    SailerActionHandler.isUpDating = false;
//                    if (forceUpdate)
//                        EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Fail));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.d("DownLoadZern", "服务器返回的JSON解析错误");
//                    SailerActionHandler.isUpDating = false;
//                    if (forceUpdate)
//                        EventBus.getDefault().post(new SailerForceUpDateEvent(SailerForceUpDateEvent.Fail));
//                }
//            }
//        });
//    }

    /**
     * CopyAsset中的资源文件到外部文件夹 如果是没有网络的话。
     */
    public void CopyAssetWithNotNet() {
        Activity topActivity = BaseTopActivity.getTopActivity();
        if (topActivity != null) {
            final Context applicationContext = topActivity.getApplicationContext();
            final int locationVcode = (int) SpUtil.getSP(applicationContext, SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.ERROR_VCODE);
            Log.d("ZernH5File", "--checkUpdateWithNoNet-" + locationVcode);
            if (locationVcode < SailerManagerHelper.getInstance().getSailerProxyHelper().getConfigVersion()) { // 判断本地版本号是否比预打包的小, 小就copy
                SailerActionHandler.isUpDating = true;
                ThreadPoolTools.getInstance().postWorkerTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            H5FileUtils.getInstance().CopyFilesNotNetWork(context, HandleH5Utils.ConfigVerion);
                            CopyFilesNotNetWork(applicationContext);
                            // 删除老版本文件。不保留! 院内的补充...
                            String repo = SailerManagerHelper.getInstance().getSailerProxyHelper().getCurrentSailerRepo();
                            String localDirPath = SailerFileUtils.getInstance().getH5FileDirPath(applicationContext) + "/" + repo + "/" + locationVcode;
                            SailerFileUtils.getInstance().deleteDir(new File(localDirPath));
                            Log.d("ZernH5File", "--checkUpdateWithNoNet-" + "CopyFilesNotNetWork");
                        } catch (Exception e) {
                            e.printStackTrace();
                            SailerActionHandler.isUpDating = false;
                            EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
                        }
                    }
                });
            }
        }
    }

    // 断网的情况下 copyAsset目录文件。
    public void CopyFilesNotNetWork(Context context) throws IOException, JSONException {
        EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Start));
//        InputStream inputStream = context.getAssets().open(SailerActionHandler.CONFIG_SHOP);
        InputStream inputStream = context.getAssets().open(SailerManagerHelper.getInstance().getSailerProxyHelper().getRepoConfigJsonRelpath());
        String gluconfig_json = SailerFileUtils.getInstance().readFileToString(inputStream);// 读取asset目录下面的config.json文件
        JSONObject jsonObject = new JSONObject(gluconfig_json);
//        String repo = jsonObject.getString("repo");
//        String repo = "shop";
        String repo = SailerManagerHelper.getInstance().getSailerProxyHelper().getCurrentSailerRepo();
        String files = jsonObject.getString("files");
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> locationMap = GsonUtil.getCommonGson().fromJson(files, type);
        String newDirPath = SailerFileUtils.getInstance().getH5FileDirPath(context) + "/" + repo + "/" + SailerManagerHelper.getInstance().getSailerProxyHelper().getConfigVersion();
        if (!SailerFileUtils.getInstance().copyFileFromAsset(locationMap, newDirPath, repo, context)) { // 从asset目录中copy文件 不成功就结束本次更新
            Log.d("ZernAssetCopy", "第一次安装copy失败结束本次Copy");
            SailerActionHandler.isUpDating = false;
            EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
            return;
        }
        // copy成功 更新本地数据库
        List<SailerSQLData> datas = new LinkedList<>();
        for (Map.Entry<String, String> stringStringEntry : locationMap.entrySet()) {
            datas.add(new SailerSQLData(stringStringEntry.getKey(), stringStringEntry.getValue()));
        }
        SailerDBService.getInstance().updateList(datas);
        SailerActionHandler.isUpDating = false;
        SailerActionHandler.NEW_H5FILES_VERSION = SailerManagerHelper.getInstance().getSailerProxyHelper().getConfigVersion();
        SailerActionHandler.CURRENT_H5FILES_REPO = repo;
        Log.d("ZernH5File", "--CopyFilesNotNetWork-" + SailerActionHandler.CURRENT_H5FILES_VERSION);
        if (SailerActionHandler.CURRENT_H5FILES_VERSION == SailerActionHandler.ERROR_VCODE) {// 如果是当前版本号还是零。那么直接就替换最新的版本号了。
            SailerActionHandler.CURRENT_H5FILES_VERSION = SailerActionHandler.NEW_H5FILES_VERSION;
        }
        SpUtil.inputSP(context.getApplicationContext(), SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.NEW_H5FILES_VERSION); // 更新本地版本号。
        EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Finish));
        Log.d("ZernH5File", "--CopyFilesNotNetWork-" + SailerActionHandler.CURRENT_H5FILES_VERSION);
    }
}
