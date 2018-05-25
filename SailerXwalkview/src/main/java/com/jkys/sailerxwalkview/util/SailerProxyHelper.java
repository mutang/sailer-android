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
import android.content.Intent;
import android.text.TextUtils;

import com.jkys.sailerxwalkview.action.ActionManager;
import com.jkys.sailerxwalkview.action.SailerActionHandler;
import com.jkys.sailerxwalkview.model.CallBackData;

import org.json.JSONException;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkView;

import java.util.HashMap;

/**
 * Sailer的抽象的代理类, 涉及到: 万能跳、以及业务层相关功能拓展的接口等功能抽象。调用层如需实现功能那么去实现它的一些方法即可 都是空方法。
 */
public abstract class SailerProxyHelper {

    private HashMap<String, Object> dataMap = new HashMap<>();

    public void putNativeData(String key, Object value) {
        dataMap.put(key, value);
    }

    public Object getNativeData(String key) {
        return dataMap.get(key);
    }

    /**
     * Sailer的跳转功能方法
     */
    public abstract void startIntent(String target, Activity activity, String param);

    /**
     * 处理Open的方法。
     *
     * @param activity
     * @param ptrim
     */
    public final void handleOpen(Activity activity, String ptrim) {
        if (ptrim.startsWith(SailerActionHandler.Native)) {
            String tParam = ptrim.replaceFirst("native://", "");
            //做兼容处理
            if (tParam.contains("native//")) {
                tParam = ptrim.replaceFirst("native//", "");
            }
            String[] splits = tParam.split(":");
            if (splits.length > 0) {
                String subParam = tParam.replaceFirst(splits[0] + ":", "");
                startIntent(splits[0], activity, subParam);
            }
        } else {
            startIntentBllActivity(activity, ptrim);
        }
    }

    /**
     * 开启是一个业务层的Sailer功能的Activity
     */
    public abstract void startIntentBllActivity(Activity activity, String param);

    public String getCurrentPatientManage(){
        return SailerActionHandler.patient_manage;
    }

    public void handleRedirect(String param, Activity activity, XWalkView xWalkView) {
        try {
            if (!TextUtils.isEmpty(param)) {
                String ptrim = param.trim();
                if (ptrim.startsWith(SailerActionHandler.hybird)) {
                    // 加载本地静态的html文件
//                    String tParam = param.replaceFirst("hybird://", "");
                    String tParam = ptrim.replaceFirst(SailerActionHandler.hybird, "");
                    // shop字段是用来区分shop、medical、social等H5文件协议
                    String redirUrl = "";
                    if (tParam.startsWith(SailerActionHandler.shop)) {
                        String ttParam = tParam.replaceFirst(SailerActionHandler.shop, "");
                        redirUrl = getUrlIndex(activity.getApplicationContext(), SailerActionHandler.shop) + ttParam;
                    } else if (tParam.startsWith(SailerActionHandler.glucometer)) {
                        String ttParam = tParam.replaceFirst(SailerActionHandler.glucometer, "");
                        redirUrl = getUrlIndex(activity.getApplicationContext(), SailerActionHandler.glucometer) + ttParam;
                    } else if (tParam.startsWith(getCurrentPatientManage())) {
                        String ttParam = tParam.replaceFirst(getCurrentPatientManage(), "");
                        redirUrl = getUrlIndex(activity.getApplicationContext(), getCurrentPatientManage()) + ttParam;
                    }
                    loadUrl(redirUrl, activity, xWalkView);
                } else if (ptrim.startsWith(SailerActionHandler.Native)) { // TODO 这边实现需要跳转的原生界面 使用新重载的方法
                    String tParam = ptrim.replaceFirst("native://", "");
                    String[] splits = tParam.split(":");
                    if (splits.length > 0) {
                        String subParam = tParam.replaceFirst(splits[0] + ":", "");
                        SailerManagerHelper.getInstance().getSailerProxyHelper().startIntent(splits[0], activity, subParam);
                    }
                } else if (ptrim.startsWith(SailerActionHandler.Http)) { //这边一般都是http开头的
                    loadUrl(ptrim, activity, xWalkView);
                } else if (ptrim.startsWith(SailerActionHandler.files)) {
                    loadUrl(ptrim, activity, xWalkView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // H5 调用Natice方法之后的成功回调Json数据
    public final <T> void jsOnSuccessCallBackLogin(final String callId, Activity activity, final XWalkView xWalkView, T data) {
        try {
            CallBackData<T> callBackData = new CallBackData<>();
            callBackData.setReturnMsg("调用成功");
            callBackData.setReturnCode("0000");
            callBackData.setData(data);
            final String dataJson = GsonUtil.getCommonGson().toJson(callBackData);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String jsCodeFun = "javascript:Sailer.onSuccess(" + "'" + callId + "'" + "," + dataJson + ")";
                    xWalkView.load(jsCodeFun, "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // H5 调用Natice方法之后的成功回调 不传参数
    public final void jsOnSuccessCallBack(final String callId, Activity activity, final XWalkView xWalkView) {
        try {
            jsOnSuccessCallBack(callId, activity, xWalkView, new Object());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final <T> void jsOnSuccessCallBack(final String callId, Activity activity, final XWalkView xWalkView, T data) {
        try {
            CallBackData<T> callBackData = new CallBackData<>();
            callBackData.setReturnMsg("调用成功");
            callBackData.setReturnCode("0000");
            callBackData.setData(data);
            final String dataJson = GsonUtil.getCommonGson().toJson(callBackData);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String jsCodeFun = "javascript:Sailer.onSuccess(" + "'" + callId + "'" + "," + "'" + dataJson + "'" + ")";
                    String escapeStr = EscapeCharacterUtils.changeCharacter(jsCodeFun);
                    xWalkView.load(escapeStr, "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // H5 调用Natice方法之后的失败回调 不传参数
    public void jsOnFailCallBack(final String callId, Activity activity, final XWalkView xWalkView) {
        try {
            jsOnFailCallBack(callId, activity, xWalkView, new Object());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // H5 调用Natice方法之后的失败回调Json数据
    public final <T> void jsOnFailCallBack(final String callId, Activity activity, final XWalkView xWalkView, T data) {
        try {
            CallBackData<T> callBackData = new CallBackData<>();
            callBackData.setReturnMsg("调用失败");
            callBackData.setReturnCode("1111");
            callBackData.setData(data);
            final String dataJson = GsonUtil.getCommonGson().toJson(callBackData);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String jsCodeFun = "javascript:Sailer.onFail(" + "'" + callId + "'" + "," + "'" + dataJson + "'" + ")";
                    xWalkView.load(jsCodeFun, "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void callNativeBack(final Activity activity, final XWalkView xWalkView) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    XWalkNavigationHistory history = xWalkView.getNavigationHistory();
                    if (history != null) {
                        if (history.canGoBack()) {
                            history.navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);
                        } else {
                            activity.finish();
                        }
                    } else {
                        activity.finish();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 显示和隐藏Main中的tab栏
    public abstract boolean showOrHideTabBar(final Activity activity, XWalkView xWalkView, String callId, final int visible);

    // H5分享功能
    public abstract void handleShare(final String param, final Activity activity, final XWalkView xWalkView);

    // js交互协议解析
    public final void handlerSalierUrl(final String data, final Activity activity, final XWalkView xWalkView) {
        try {
            String dataTrim = data.trim();
            if (dataTrim.indexOf(SailerActionHandler.nativeCall) == 0) {
                String[] calls = dataTrim.split("\\$");
                String callId = "", action = "", param = "";
                // Sailer callId获取
                if (calls.length > 0) {
                    callId = calls[0].replaceFirst("native-call:", "");
                }
                // action 获取
                if (calls.length > 2)
                    action = calls[2];
                // param 获取
                if (calls.length > 3)
                    param = calls[3];
                handlerActionParam(action, param, activity, xWalkView, callId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public final void handlerActionParam(String action, String param, final Activity activity, final XWalkView xWalkView, String callId) throws JSONException {
        if (!TextUtils.isEmpty(action)) {
//            for (SailerActionHandler sailerActionHandler : ActionManager.getActions()) {
//                if (sailerActionHandler.handlerUrl(action, param, activity, xWalkView, callId)) {
//                    break;
//                }
//            }
            SailerActionHandler sailerActionHandler = ActionManager.getActionsMap().get(action);
            if (sailerActionHandler != null) {
                sailerActionHandler.handlerUrl(param, activity, xWalkView, callId);
            }
        }
    }

    public final void hideOutLinkUrl(Activity activity, String protocol) {
        String[] calls = protocol.split("\\$");
        if (calls.length > 3) {
            String action = calls[2];
            String params = calls[3];
            switch (action) {
                case SailerActionHandler.open:
                    handleOpen(activity, params);
                    break;
                case SailerActionHandler.redirect: // FIXME: 17/3/3 这边暂时不考虑处理
                    handleOpen(activity, params);
                    break;
            }
        }
    }

//    public abstract String checkUpdateH5Url();

    // 判断是否是要求隐藏导航条
    public abstract boolean isHideNavBar(String pageToUrl);

    // 处理支付工具类Main
    public abstract boolean HandlerPayResult(int requestCode, int resultCode, Intent data, Activity activity);

    /**
     * 很重要的方法 用来通知H5同学Native端已经准备完毕
     * @param activity
     * @param xWalkView
     */
    public final void fireReady(Activity activity, XWalkView xWalkView) {
        try {
            callJsFunc("Sailer.fire('ready')", activity, xWalkView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户信息
     * @param activity
     * @return
     */
    public abstract String getUserInfo(Activity activity);

    /**
     * 触发H5同学需要登录
     * @param activity
     * @param xWalkView
     */
    public final void fireLoginOut(Activity activity, XWalkView xWalkView) {
        try {
            String userInfoJson = getUserInfo(activity);
            String fireLogoutFun = "Sailer.fire('logout'" + "," + "'" + userInfoJson + "'" + ")";
            callJsFunc(fireLogoutFun, activity, xWalkView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 触发H5同学需要登录
     * @param activity
     * @param xWalkView
     */
    public final void fireLogin(Activity activity, XWalkView xWalkView) {
        try {
            String userInfoJson = getUserInfo(activity);
            String fireLogoutFun = "Sailer.fire('login'" + "," + "'" + userInfoJson + "'" + ")";
            callJsFunc(fireLogoutFun, activity, xWalkView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 触发H5的方法。主动塞给H5用户信息
     */
    public final void fireSetUserInfo(Activity activity, XWalkView xWalkView) {
        try {
            String userInfoJson = getUserInfo(activity);
            String fireUserInfoFun = "Sailer.fire('setUserInfo'" + "," + "'" + userInfoJson + "'" + ")";
            callJsFunc(fireUserInfoFun, activity, xWalkView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void callJsFunc(final String func, final Activity activity, final XWalkView xWalkView) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    xWalkView.load("javascript:" + func, "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载url
     * @param param 网址 url
     * @param activity 加载的activity
     * @param xWalkView 加载的xwalkview
     */
    public final void loadUrl(final String param, final Activity activity, final XWalkView xWalkView) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    xWalkView.load(param, "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取商城当前需要加载的本地文件路径Url
//    public final String getUrlIndex(Context context) {
//        String url = "";
//        url = (String) SpUtil.getSP(context, SailerActionHandler.KEY_SHOP_INDEX_URL, SailerActionHandler.NOT_FOUND);
//        if (SailerActionHandler.NOT_FOUND.equals(url)) {
//            url = "file:///android_asset/" + SailerActionHandler.SHOP_ASSEST_FILE_PATH;
//        }
//        return url;
//    }

    // H5 特殊的地方。。。
    public final void asyUserCallBack(final String callId, Activity activity, final XWalkView xWalkView, final String asyUserInfo) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String jsCodeFun = "javascript:Sailer.onSuccess(" + "'" + callId + "'" + "," + "'" + asyUserInfo + "'" + ")";
                xWalkView.load(jsCodeFun, "");
            }
        });
    }

    private String userAgent = "tangyi";

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * @return H5资源更新的版本号
     */
    public abstract String checkH5Version ();

    /**
     * @return H5资源更新的信息数据
     */
    public abstract String checkH5UpDateData ();

    // 获取H5文件当前需要加载的本地文件路径Url repo表示H5模块名称 商城就是shop 目前是glucometer
    public String getUrlIndex(Context context, String repo) {
        Context mContext = context.getApplicationContext();
        String path = SailerFileUtils.getInstance().getH5FileDirPath(mContext);
        int locationVcode = SailerActionHandler.CURRENT_H5FILES_VERSION;
        if (locationVcode == 0) {
            locationVcode = (int) SpUtil.getSP(mContext, SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.ERROR_VCODE);
        }
        if (locationVcode == 0) {
            return null;
        }
//        int locationVcode = (int) SpUtil.getSP(context, KEY_H5_VERSION_CODE, 1);
        String url = "file://" + path + "/" + repo + "/" + locationVcode;
        return url;
    }

    // 该方法是和具体项目有关系 这层并不是必须由上层用户实现的。 所以父类是空方法体 TODO 下次再优化吧 因需求紧
    public void sailerFire(String eventName, Object paramObject, final Activity activity, final XWalkView webview) {}

    // 上层用户使用的配置版本号。
    public abstract int getConfigVersion();

    // 预打包的对应模块的json文件相对路径
    public abstract String getRepoConfigJsonRelpath();

    public String currentSailerRepo = "shop"; // 当前热更的模块名 repo字段的值

    // 目前Sailer加载的当前 仓库 repo
    public String getCurrentSailerRepo() {
        return currentSailerRepo;
    }

    public void setCurrentSailerRepo(String currentSailerRepo) {
        this.currentSailerRepo = currentSailerRepo;
    }


    /**
     * 是否需要处理接手处理XwalkView的物理返回按钮事件。默认是不处理。
     * @return
     */
    public boolean isNeedHandlerBackKeyPress () {
        return false;
    }

    public void dismissSharePopup() {
    }

    public String getDownloadApkUrl () {
        return "http://files.qa.91jkys.com/apks/hospital/yhjz/hospital_Qatest.apk"; // apk下载地址。不同项目可以重写该方法替换地址。
    }

}
