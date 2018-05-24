package com.jkys.sailerxwalkview.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;

import com.jkys.sailerxwalkview.action.SailerActionHandler;
import com.jkys.sailerxwalkview.activity.BaseTopActivity;
import com.jkys.sailerxwalkview.activity.SailerWebActivity;

import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/7/12
 * Time: 10:43
 * Email:AndroidZern@163.com
 */
public class SailerUIClient extends XWalkUIClient {

    private int count = 0;
    //    private RelativeLayout mProcessRl;
    private Activity activity;
    public static ValueCallback mUploadMessage;
    public static final int FILE_SELECTED = 10000;

    public SailerUIClient(XWalkView view, Activity activity) {
        super(view);
//        this.mProcessRl = mProcessRl;
        this.activity = activity;
    }

    /**
     * 打开新页面处理
     *
     * @return
     */
    private void openNewPageProcess(XWalkView view, String url) {
        // 当新开页面的时候，除了拨打电话号码的，其他都需要setUserInfo一把
        Activity topActivity = BaseTopActivity.getTopActivity();
        if (topActivity != null) {
            if (SailerManagerHelper.getInstance().getSailerProxyHelper().isHideNavBar(url)) {
                if (BaseTopActivity.getTopActivity() instanceof SailerWebActivity) {
                    SailerWebActivity activity = (SailerWebActivity) topActivity;
                    activity.setFragmentNavigation(View.GONE);
                }
            } else {
                if (topActivity instanceof SailerWebActivity) {
                    SailerWebActivity activity = (SailerWebActivity) topActivity;
                    activity.setFragmentNavigation(View.VISIBLE);
                }
            }
        }

        if (!url.startsWith("tel:")) {
            view.setTag(true);
        }
    }


    @Override
    public void onPageLoadStarted(XWalkView view, String url) {
//        boolean isChangeOff = NetworkCheckUtil.checkNetwork(view.getContext(), false);
//        if (isChangeOff) {
//
//        }
        count = count + 1;
        openNewPageProcess(view, url);
//        if (HandlerH5Utils.isHideNavBar(url)) {
//            if (activity instanceof ShopActivityNew) {
//                ShopActivityNew activity = (ShopActivityNew) this.activity;
//                activity.setFragmentNavigation(View.GONE);
//            }
//        } else {
//            if (activity instanceof ShopActivityNew) {
//                ShopActivityNew activity = (ShopActivityNew) this.activity;
//                activity.setFragmentNavigation(View.VISIBLE);
//            }
//        }
//        mProcessRl.setVisibility(View.VISIBLE);
        super.onPageLoadStarted(view, url);
    }

    @Override
    public void onPageLoadStopped(XWalkView xWalkView, String url, XWalkUIClient.LoadStatus status) {
//        mProcessRl.setVisibility(View.GONE);
        boolean flag = false;
        Object tag = xWalkView.getTag();
        if (tag != null && tag instanceof Boolean) {
            flag = (boolean) tag;
        }
        if (status == LoadStatus.FINISHED && flag) {
//            Pattern compile = Pattern.compile(HandlerH5Utils.INTERNAL_URL);
            // 判断当前加载的是否是本公司domains或者是本APP内部文件。
            if (url.contains(SailerActionHandler.INTERNAL_URL) || url.contains(SailerActionHandler.APP_FILE_URL) || url.contains(SailerActionHandler.SHOP_ASSEST_FILE_PATH)) {
                SailerManagerHelper.getInstance().getSailerProxyHelper().fireSetUserInfo(activity, xWalkView);
                SailerManagerHelper.getInstance().getSailerProxyHelper().fireReady(activity, xWalkView);
            }
//            else {
//                if (activity instanceof ShopActivityNew){
//                    ShopActivityNew activity = (ShopActivityNew) this.activity;
//                    activity.setFragmentNavigation(View.VISIBLE);
//                }
//            }
        }
        xWalkView.setTag(false);
        if (activity instanceof SailerWebActivity) {
            SailerWebActivity activity = (SailerWebActivity) this.activity;
            activity.setFragmentTitleText(xWalkView.getTitle() == null ? "" : xWalkView.getTitle());
        }
        super.onPageLoadStopped(xWalkView, url, status);
    }

    @Override
    public boolean onConsoleMessage(XWalkView view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        return super.onConsoleMessage(view, message, lineNumber, sourceId, messageType);
    }

    @Override
    public void onReceivedTitle(XWalkView view, String title) {
//        HandlerH5Utils.NavBarTitle = title;
//        if (activity instanceof ShopActivityNew) {
//            ShopActivityNew activity = (ShopActivityNew) this.activity;
//            activity.setFragmentTitleText(title);
//        }
        super.onReceivedTitle(view, title);
    }

    @Override
    public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        super.openFileChooser(view, uploadFile, acceptType, capture);
        if (mUploadMessage != null) return;
        mUploadMessage = uploadFile;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        activity.startActivityForResult(Intent.createChooser(i, "选择图片"), FILE_SELECTED);
    }

}
