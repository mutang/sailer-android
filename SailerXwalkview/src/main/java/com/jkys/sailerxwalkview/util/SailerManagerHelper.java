package com.jkys.sailerxwalkview.util;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/4/27
 * Time: 15:35
 * Email:AndroidZern@163.com
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jkys.sailerxwalkview.action.SailerActionHandler;
import com.jkys.sailerxwalkview.activity.SailerWebActivity;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.util.LinkedList;
import java.util.List;

/**
 * Sailer的全局配置类 和一些H5需要在运行时保存的变量。
 */
public class SailerManagerHelper {

    private static class SingletonHolder {
        private static final SailerManagerHelper INSTANCE = new SailerManagerHelper();
    }

    private SailerManagerHelper() {
    }

    public static final SailerManagerHelper getInstance() {
        return SailerManagerHelper.SingletonHolder.INSTANCE;
    }

    /**
     * 全局配置XWalkView的参数 初始化Sailer的配置
     */
    public void initXWalkViewConfig(Context context) {
        initXWalkViewConfig(context, new DefaultSailerProxyHelper());
    }

    private SailerProxyHelper sailerProxyHelper;

    public SailerProxyHelper getSailerProxyHelper() {
        return sailerProxyHelper;
    }

    public void setSailerProxyHelper(SailerProxyHelper sailerProxyHelper) {
        this.sailerProxyHelper = sailerProxyHelper;
    }

    /**
     * 实现代理功能的初始化。配置业务层需要的扩展功能
     *
     * @param context
     */
    public void initXWalkViewConfig(Context context, SailerProxyHelper sailerProxyHelper) {
        try {
            this.sailerProxyHelper = sailerProxyHelper;
            XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
//            String locationVJson = (String) SpUtil.getSP(context, SailerActionHandler.KEY_SHOP_VERSION_JSON, SailerActionHandler.NOT_FOUND);
            // 应用第一次启动商城
//            if (SailerActionHandler.NOT_FOUND.equals(locationVJson)) {
//                SpUtil.inputSP(context, SailerActionHandler.KEY_SHOP_VERSION_JSON, SailerActionHandler.locationJSONStr);
//                SpUtil.inputSP(context, SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.h5CODE);
//            }
            SailerActionHandler.CURRENT_H5FILES_VERSION = (int) SpUtil.getSP(context, SailerActionHandler.KEY_REPO_VERSION_CODE, SailerActionHandler.ERROR_VCODE);
//            SailerActionHandler.IndexDir = SailerManagerHelper.getInstance().getSailerProxyHelper().getUrlIndex(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化XWalkview
     */
    public void initXWalkView(final XWalkView xWalkView, final Activity activity, final ProgressBar hProgressBar, final LinearLayout errorPagell, final RelativeLayout mProcessRl) {
        SailerResourceClient xWalkResourceClient = new SailerResourceClient(xWalkView, hProgressBar, errorPagell, mProcessRl);
        SailerUIClient xWalkUIClient = new SailerUIClient(xWalkView, activity);
        initXWalkView(xWalkView, activity, xWalkResourceClient, xWalkUIClient);
//        try {
//            xWalkView.addJavascriptInterface(new JavaScriptInterface(new JavaScriptInterfaceInterface() {
//                @Override
//                public String jsCallAppMethod(String data) {
//                    Log.d("Zern-jsCallAppMethod: ", " " + data);
//                    sailerProxyHelper.handlerSalierUrl(data, activity, xWalkView);
//                    return "";
//                }
//            }), "NativeInterface");
//            xWalkView.setResourceClient(new SailerResourceClient(xWalkView) {
//                @Override
//                public void onProgressChanged(XWalkView view, int newProgress) {
//                    if (mProcessRl != null) { // 针对APP一打开 APP初始化资源不足。导致加载H5url慢,有1.5的白屏时间。所以需要显示loading的标志。不然以为是白屏。
//                        mProcessRl.setVisibility(View.VISIBLE);
//                        if (100 == newProgress && mProcessRl != null) {
//                            mProcessRl.setVisibility(View.GONE);
//                        }
//                    }
//                    if (hProgressBar != null) { // 非首页H5不需要显示进度条。
//                        hProgressBar.setVisibility(View.VISIBLE);
//                        hProgressBar.setProgress(newProgress);
//                        if (100 == newProgress) {
//                            hProgressBar.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (hProgressBar != null) {
//                                        hProgressBar.setVisibility(View.GONE);
//
//                                    }
//                                }
//                            }, 100);
//                        }
//                    }
//                    super.onProgressChanged(view, newProgress);
//                }
//
//                @Override
//                public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
//                    errorPagell.setVisibility(View.VISIBLE);
////                    super.onReceivedLoadError(view, errorCode, description, failingUrl);
//                }
//            });
//            xWalkView.setUIClient(new SailerUIClient(xWalkView, activity));
//            // UA 的配置。因为Xalkview无直接获取UA的方法,所以这边是UA打印出来后我照着之前的自定义赋值了一遍 开发者可以修改这边的代码重新赋值。
//            String userAgent = getSailerProxyHelper().getUserAgent()
//                    + "; Mozilla/5.0 (Linux; Android "
//                    + Build.VERSION.RELEASE + "; "
//                    + Build.MODEL
//                    + " Build/" + Build.BRAND + " "
//                    + Build.MODEL + ") "
//                    + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Crosswalk/14.43.343.25 Mobile Safari/537.36";
//            xWalkView.setUserAgentString(userAgent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 初始化XWalkview 上层用户自定义SailerUIClient和SailerResourceClient
     */
    public void initXWalkView(final XWalkView xWalkView, final Activity activity, XWalkResourceClient xWalkResourceClient, XWalkUIClient xWalkUIClient) {
        try {
            xWalkView.addJavascriptInterface(new JavaScriptInterface(new JavaScriptInterfaceInterface() {
                @Override
                public String jsCallAppMethod(String data) {
                     Log.d("Zern-jsCallAppMethod: ", " " + data);
                    sailerProxyHelper.handlerSalierUrl(data, activity, xWalkView);
                    return "";
                }
            }), "NativeInterface");
            xWalkView.setResourceClient(xWalkResourceClient);
            xWalkView.setUIClient(xWalkUIClient);
            // UA 的配置。因为Xalkview无直接获取UA的方法,所以这边是UA打印出来后我照着之前的自定义赋值了一遍 开发者可以修改这边的代码重新赋值。
            String userAgent = getSailerProxyHelper().getUserAgent()
                    + "; Mozilla/5.0 (Linux; Android "
                    + Build.VERSION.RELEASE + "; "
                    + Build.MODEL
                    + " Build/" + Build.BRAND + " "
                    + Build.MODEL + ") "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Crosswalk/14.43.343.25 Mobile Safari/537.36";
            xWalkView.setUserAgentString(userAgent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 所有的Sailer的xwalkview的引用管理列表
    private List<XWalkView> xWalkViews = new LinkedList<XWalkView>();

    public List<XWalkView> getxWalkViews() {
        return xWalkViews;
    }

    public void addxWalkview(XWalkView xWalkView) {
        try {
            xWalkViews.add(xWalkView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removexWalkview(XWalkView xWalkView) {
        try {
            xWalkViews.remove(xWalkView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DefaultSailerProxyHelper extends SailerProxyHelper {

        @Override
        public void startIntent(String target, Activity activity, String param) {

        }

        @Override
        public void startIntentBllActivity(Activity activity, String param) {
            Intent intent = new Intent(activity, SailerWebActivity.class);
            intent.putExtra(SailerActionHandler.PageToUrl, param);
            activity.startActivity(intent);
        }

        @Override
        public boolean showOrHideTabBar(Activity activity, XWalkView xWalkView, String callId, int visible) {
            return false;
        }

        @Override
        public void handleShare(String param, Activity activity, XWalkView xWalkView) {

        }

//        @Override
//        public String checkUpdateH5Url() {
//            return null;
//        }

        @Override
        public boolean isHideNavBar(String pageToUrl) {
            return false;
        }

        @Override
        public boolean HandlerPayResult(int requestCode, int resultCode, Intent data, Activity activity) {
            return false;
        }

        @Override
        public String getUserInfo(Activity activity) {
            return null;
        }

        @Override
        public String checkH5Version() {
            return null;
        }

        @Override
        public String checkH5UpDateData() {
            return null;
        }

        @Override
        public int getConfigVersion() {
            return 42;
        } //TODO 发到github需要注意修改处

        @Override
        public String getRepoConfigJsonRelpath() {
            return "config/patient-manage.json"; // TODO 发到github需要注意修改处
        }
    }
}
