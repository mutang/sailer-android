//package com.jkys.sailerxwalkview.network;
//
//import android.content.Context;
//
//import com.jkys.jkysbase.JkysLog;
//import com.jkys.jkysbase.SpUtil;
////import com.jkys.jkysnetwork.CommonService;
//import com.jkys.jkysnetwork.model.RequestModel;
//import com.jkys.sailerxwalkview.action.SailerActionHandler;
//import com.jkys.sailerxwalkview.event.CopyAssetOrDownLoadStatus;
//import com.jkys.sailerxwalkview.event.UpdateDialogEvent;
//import com.jkys.sailerxwalkview.util.SailerFileUtils;
//import com.jkys.sailerxwalkview.util.SailerManagerHelper;
//import com.jkys.sailerxwalkview.util.SailerUpdateHelper;
//
//import org.greenrobot.eventbus.EventBus;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//
//import io.reactivex.Observer;
//import io.reactivex.disposables.Disposable;
//import okhttp3.ResponseBody;
////import rx.Subscriber;
//
////import com.android.volley.Request;
////import com.android.volley.RequestQueue;
////import com.android.volley.Response;
////import com.android.volley.VolleyError;
////import com.android.volley.toolbox.StringRequest;
////import com.android.volley.toolbox.Volley;
////import org.greenrobot.eventbus.EventBus;
//
///*
// * Created by on
// * Author: Zern
// * DATE: 16/12/20
// * Time: 14:31
// * Email:AndroidZern@163.com
//*/
//
//
//public class SailerNetManager {
////        implements Response.ErrorListener {
//
//    //    private RequestQueue mQueue;
//    private volatile static SailerNetManager sInstacne;
//
//    private SailerNetManager(Context context) {
////        if (mQueue == null) {
////            mQueue = Volley.newRequestQueue(context.getApplicationContext());
////        }
//    }
//
//    public static SailerNetManager getInstance(Context context) {
//        if (sInstacne == null) {
//            synchronized (SailerNetManager.class) {
//                if (sInstacne == null) {
//                    sInstacne = new SailerNetManager(context);
//                }
//            }
//        }
//        return sInstacne;
//    }
//
////    public static final String CHECK_H5_VERSION = BuildConfig.CHECK_H5_UPDATE_URL + "config-version.json";
////    public static final String GET_H5_UPDATEDATA = BuildConfig.CHECK_H5_UPDATE_URL + "config.json";
//
//    // H5文件网络请求的统一入口
//    public void requestH5FromNet(final String url, final Context context) {
//        int indexStart = url.indexOf("://");
//        int index = (indexStart + 3) + url.substring(indexStart + 3).indexOf("/");
//        String baseUrl = url.substring(0, index + 1);
//        String path = url.substring(index + 1);
//
//        Observer observer = new Observer<ResponseBody>() {
//
//            @Override
//            public void onError(Throwable e) {
//                JkysLog.d("ZernH5File", "没有网络，或者服务器挂了，网络获取Json出错!，等待用户重试。。。");
//                if (SailerActionHandler.CURRENT_H5FILES_VERSION == SailerActionHandler.ERROR_VCODE) { // 无网络。并且是第一次安装且版本号是比预打包的高需要下载新 但是无网络的状态 Copy
//                    // copy 资产目录的H5文件
//                    SailerUpdateHelper.getInstance().CopyAssetWithNotNet();
//                }
//                SailerActionHandler.isUpDating = false;
//                EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(ResponseBody responseBody) {
//                String response = null;
//                try {
//                    response = responseBody.string();
//                    if (SailerManagerHelper.getInstance().getSailerProxyHelper().checkH5Version() == null) {// 如果是该项目没有这个版本接口 那么逻辑走V2版本
//                        onProcessResultV2(response, url, context);
//                    } else {
//                        onProcessResultV1(response, url, context); // 如果是配置了 类似于血糖仪 那么走V1版本的逻辑
//                    }
//                } catch (IOException e) {
//                    onError(e);
//                }
//
//            }
//        };
//
//        RequestModel requestModel = new RequestModel();
//        requestModel.setRespProcessAsync(true);
//        requestModel.setBaseUrl(baseUrl);
//        requestModel.setPath(path);
//        requestModel.setObserver(observer);
//        SailerNetworkUtil.request(context, requestModel);
////        new CommonService(context, baseUrl).getRequestAsync(path, observer);
//    }
//
////    public RequestQueue getmQueue(Context context) {
////        if (mQueue == null) {
////            mQueue = Volley.newRequestQueue(context.getApplicationContext());
////        }
////        return mQueue;
////    }
//
//    // H5请求的后的成功的回调。目前逻辑是先请求config-version接口得到版本号, 进行逻辑判断时候 再继续请求下一个对应的模块的文件名字和MD5值。
//    private void onProcessResultV1(final String response, final String url, final Context context) {
//        if (SailerManagerHelper.getInstance().getSailerProxyHelper().checkH5Version().equals(url)) {
//            int remoteCode = 0;
//            try {
//                remoteCode = new JSONObject(response).getInt("version");
//            } catch (JSONException e) {
//                SailerActionHandler.isUpDating = false;
//                e.printStackTrace();
//            }
//            int locationVcode = (int) SpUtil.getSP(context, SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.ERROR_VCODE);
//            if (remoteCode > SailerManagerHelper.getInstance().getSailerProxyHelper().getConfigVersion()) { // 判断是否远程版本号比本地的预打包的高
//                if (remoteCode > locationVcode) { // 判断是否远程的版本号是否比本地的版本号高 高就去请求另外一个全部数据的接口 底就return不更新
////                            requestH5FromNet(SailerManagerHelper.getInstance().getSailerProxyHelper().checkH5UpDateData(), context);
//                    EventBus.getDefault().post(new UpdateDialogEvent(UpdateDialogEvent.repoHMCode));
//                } else {
//                    if (SailerActionHandler.isH5CallCheckUpdate) { // 院内一体机加的需求
//                        SailerActionHandler.isH5CallCheckUpdate = false;
//                        EventBus.getDefault().post(new UpdateDialogEvent(UpdateDialogEvent.repoHMCode_NoUpdate)); // 如果没有更新院内一体机需要提示H5版本没有更新。
//                    }
//                    SailerActionHandler.isUpDating = false;
//                }
//            } else { // 本地预打包的是最新的
//                if (SailerManagerHelper.getInstance().getSailerProxyHelper().getConfigVersion() > locationVcode) { // 在判断本地版本号是否比预打包的低 低就Copy
//                    try {
//                        SailerUpdateHelper.getInstance().CopyFilesNotNetWork(context);
//                        // 删除老版本文件。不保留! 院内的补充...
//                        String repo = SailerManagerHelper.getInstance().getSailerProxyHelper().getCurrentSailerRepo();
//                        String localDirPath = SailerFileUtils.getInstance().getH5FileDirPath(context) + "/" + repo + "/" + locationVcode;
//                        SailerFileUtils.getInstance().deleteDir(new File(localDirPath));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        SailerActionHandler.isUpDating = false;
//                        EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
//                    }
//                } else { // 本地预打包的是最新的 在判断本地版本号是否比预打包的低 低就Copy
//                    SailerActionHandler.isUpDating = false;
//                    if (SailerActionHandler.isH5CallCheckUpdate) { // 院内一体机加的需求
//                        SailerActionHandler.isH5CallCheckUpdate = false;
//                        EventBus.getDefault().post(new UpdateDialogEvent(UpdateDialogEvent.repoHMCode_NoUpdate)); // 如果没有更新院内一体机需要提示H5版本没有更新。
//                    }
//                }
//            }
//        } else if (SailerManagerHelper.getInstance().getSailerProxyHelper().checkH5UpDateData().equals(url)) {
//            int remoteCode = 0;
//            try {
//                remoteCode = new JSONObject(response).getInt("version");
//                int locationVcode = (int) SpUtil.getSP(context, SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.ERROR_VCODE);
//                if (remoteCode > locationVcode) {
//                    SailerUpdateHelper.getInstance().startUpdateH5Files(context, response);
//                } else {
//                    if (SailerActionHandler.isH5CallCheckUpdate) { // 院内一体机加的需求
//                        SailerActionHandler.isH5CallCheckUpdate = false;
//                        EventBus.getDefault().post(new UpdateDialogEvent(UpdateDialogEvent.repoHMCode_NoUpdate)); // 如果没有更新院内一体机需要提示H5版本没有更新。
//                    }
//                    SailerActionHandler.isUpDating = false;
//                }
//            } catch (JSONException e) {
//                SailerActionHandler.isUpDating = false;
//                e.printStackTrace();
//            }
//        }
////        ThreadPoolTools.getInstance().postWorkerTask(new Runnable() {
////            @Override
////            public void run() {
////
////            }
////        });
//    }
//
//    // H5请求的后的成功的回调。目前逻辑是先请求config-version接口得到版本号, 进行逻辑判断时候 再继续请求下一个对应的模块的文件名字和MD5值。
//    private void onProcessResultV2(final String response, final String url, final Context context) {
//        if (url.equals(SailerManagerHelper.getInstance().getSailerProxyHelper().checkH5UpDateData())) {
//            int remoteCode = 0;
//            try {
//                remoteCode = new JSONObject(response).getInt("version");
//            } catch (JSONException e) {
//                SailerActionHandler.isUpDating = false;
//                EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
//                e.printStackTrace();
//                return;
//            }
//            int locationVcode = (int) SpUtil.getSP(context, SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.ERROR_VCODE);
//            if (remoteCode > SailerManagerHelper.getInstance().getSailerProxyHelper().getConfigVersion()) { // 判断是否远程版本号比本地的预打包的高
//                if (remoteCode > locationVcode) { // 判断是否远程的版本号是否比本地的版本号高 是的话就下载更新
////                            requestH5FromNet(GET_H5_UPDATEDATA, context);
////                    EventBus.getDefault().post(new UpdateDialogEvent(UpdateDialogEvent.repoShopCode, response));
//                    SailerUpdateHelper.getInstance().startUpdateH5Files(context, response);
//                } else {
//                    SailerActionHandler.isUpDating = false;
//                }
//            } else { // 本地预打包的是最新的
//                if (SailerManagerHelper.getInstance().getSailerProxyHelper().getConfigVersion() > locationVcode) { // 在判断本地版本号是否比预打包的低 低就Copy
//                    try {
//                        SailerUpdateHelper.getInstance().CopyFilesNotNetWork(context);
//                        // 删除老版本文件。不保留! 院内的补充...
//                        String repo = SailerManagerHelper.getInstance().getSailerProxyHelper().getCurrentSailerRepo();
//                        String localDirPath = SailerFileUtils.getInstance().getH5FileDirPath(context) + "/" + repo + "/" + locationVcode;
//                        SailerFileUtils.getInstance().deleteDir(new File(localDirPath));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        SailerActionHandler.isUpDating = false;
//                        EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
//                    }
//                } else { // 本地预打包的是最新的 在判断本地版本号是否比预打包的低 低就Copy
//                    SailerActionHandler.isUpDating = false;
//                }
//            }
//        } else {
//            SailerActionHandler.isUpDating = false;
//        }
////        ThreadPoolTools.getInstance().postWorkerTask(new Runnable() {
////            @Override
////            public void run() {
////
////            }
////        });
//    }
//
////    @Override
////    public void onErrorResponse(VolleyError error) {
////        JkysLog.d("ZernH5File", "没有网络，或者服务器挂了，网络获取Json出错!，等待用户重试。。。");
////        if (SailerActionHandler.CURRENT_H5FILES_VERSION == SailerActionHandler.ERROR_VCODE) { // 无网络。并且是第一次安装且版本号是比预打包的高需要下载新 但是无网络的状态 Copy
////            // copy 资产目录的H5文件
////            SailerUpdateHelper.getInstance().CopyAssetWithNotNet();
////        }
////        SailerActionHandler.isUpDating = false;
////        EventBus.getDefault().post(new CopyAssetOrDownLoadStatus(CopyAssetOrDownLoadStatus.Fail));
////    }
//}
