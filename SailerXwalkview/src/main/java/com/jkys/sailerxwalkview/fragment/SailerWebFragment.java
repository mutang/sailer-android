package com.jkys.sailerxwalkview.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkys.sailerxwalkview.R;
import com.jkys.sailerxwalkview.action.SailerActionHandler;
import com.jkys.sailerxwalkview.event.CopyAssetOrDownLoadStatus;
import com.jkys.sailerxwalkview.model.SailerForceUpDateEvent;
import com.jkys.sailerxwalkview.model.SailerParamEvent;
import com.jkys.sailerxwalkview.util.SailerManagerHelper;
import com.jkys.sailerxwalkview.util.SailerUpdateHelper;
import com.jkys.sailerxwalkview.widget.MyXwalkView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

//import android.util.DisplayMetrics;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/6/21
 * Time: 14:03
 * Email:AndroidZern@163.com
 */
@SuppressLint("JavascriptInterface")
public class SailerWebFragment extends Fragment implements View.OnClickListener {

    public MyXwalkView xWalkView;
    public RelativeLayout mProcessRl;
    public ProgressBar hProgressBar;
    // 导航栏配置 一般不怎么会用到
    public RelativeLayout titleBarll;

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    public TextView titleTv;
    public TextView shareTv;
    public TextView backTv;
    public LinearLayout llqatest;
    public boolean isSwitch = false;
    public LinearLayout errorPagell;
    public TextView updateLogo;
    public TextView errorPagebackTv, errorPageReloadTv;
    public TextView updateErrorTv; // 文件下载出错的异常提示.
    Activity mActivity;

    public LinearLayout getErrorPagell() { // 无网络默认显示的视图
        return errorPagell;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_sailer, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new View(getContext());
        }
        initView(view);
        SailerManagerHelper.getInstance().addxWalkview(xWalkView);
//        xWalkView.clearCache(true);
        initTitleBar(view);
        initMyXwalkview();
        return view;
    }

    public void initMyXwalkview() {
        xWalkView.clearCache(true); // 默认是去掉缓存的。
        SailerManagerHelper.getInstance().initXWalkView(xWalkView, mActivity, hProgressBar, errorPagell, mProcessRl);
    }

    private void initView(View view) {
        updateLogo = (TextView) view.findViewById(R.id.updateLogo);
        mProcessRl = (RelativeLayout) view.findViewById(R.id.custom_rl);
        hProgressBar = (ProgressBar) view.findViewById(R.id.hProgressBar);
        xWalkView = (MyXwalkView) view.findViewById(R.id.xwalk_webview);
        errorPagell = (LinearLayout) view.findViewById(R.id.error_pagell);
        errorPagebackTv = (TextView) view.findViewById(R.id.error_page_backTv);
        errorPageReloadTv = (TextView) view.findViewById(R.id.error_page_load_againTv);
        updateErrorTv = (TextView) view.findViewById(R.id.update_errorTv);
        errorPagebackTv.setOnClickListener(this);
        errorPageReloadTv.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            loadPageToUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String needPageUrl;

    // 加载具体的业务代码。 解析外部传入的url 协议。控制显示隐藏导航条
    public void loadPageToUrl() {
        Bundle bundle = getArguments();
        if (bundle != null && !TextUtils.isEmpty(bundle.getString(SailerActionHandler.PageToUrl))) {
            String pageToUrl = bundle.getString(SailerActionHandler.PageToUrl).trim();
            if (!TextUtils.isEmpty(pageToUrl)) {
                if (SailerManagerHelper.getInstance().getSailerProxyHelper().isHideNavBar(pageToUrl)) {
                    setNavigation(View.GONE);
                } else {
                    setNavigation(View.VISIBLE);
                }
                SailerManagerHelper.getInstance().getSailerProxyHelper().handleRedirect(pageToUrl, mActivity, xWalkView);
            }
        }
    }

    private void initTitleBar(View view) {
        titleBarll = (RelativeLayout) view.findViewById(R.id.lltitleBar);
        backTv = (TextView) view.findViewById(R.id.backTv);
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SailerManagerHelper.getInstance().getSailerProxyHelper().callNativeBack(mActivity, xWalkView);
            }
        });
        titleTv = (TextView) view.findViewById(R.id.titleTv);
        shareTv = (TextView) view.findViewById(R.id.shareTv);
    }

    public void setNavigation(int visible) {
        titleBarll.setVisibility(visible);
        titleTv.setVisibility(visible);
        backTv.setVisibility(visible);
    }

    public void setTitleText(String title) {
        titleTv.setText(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (xWalkView != null) {
            xWalkView.resumeTimers();
            xWalkView.onShow();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (xWalkView != null) {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
    }

    private void configNavBar(String params) throws JSONException {
        final JSONObject subParams = new JSONObject(params);
        String tt = subParams.getString("text");
        shareTv.setText(tt + "");
        final String action = subParams.getString("action");
        final String param = subParams.getString("param");
//        titleTv.setText(HandlerH5Utils.NavBarTitle + "");
        shareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SailerManagerHelper.getInstance().getSailerProxyHelper().handlerActionParam(action, param, mActivity, xWalkView, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 当前Tab栏中的商城重定向到一个地址
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SailerParamEvent event) {
        SailerManagerHelper.getInstance().getSailerProxyHelper().loadUrl(event.getParam(), mActivity, xWalkView);
    }

    /**
     * Sailer的强制更新的事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SailerForceUpDateEvent event) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (xWalkView != null) {
            xWalkView.onDestroy();
            // 从管理列表中移除相对应的xwalkview
            try {
                SailerManagerHelper.getInstance().removexWalkview(xWalkView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            xWalkView = null;
        }
        EventBus.getDefault().unregister(this);
        SailerActionHandler.currentNavBarXwalkView = null;
        SailerActionHandler.currentNavBarCallId = null;
        SailerActionHandler.currentNavBarParam = null;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.error_page_backTv) {
            if (mActivity != null) {
                SailerManagerHelper.getInstance().getSailerProxyHelper().callNativeBack(mActivity, xWalkView);
                if (errorPagell != null) {
                    errorPagell.setVisibility(View.GONE);
                }
            }

        } else if (i == R.id.error_page_load_againTv) {
            if (errorPagell != null) {
                errorPagell.setVisibility(View.GONE);
            }
            loadPageToUrl();

        }
    }

    public void showLoading() {
        if (mProcessRl != null) {
            mProcessRl.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (mProcessRl != null) {
            mProcessRl.setVisibility(View.GONE);
        }
    }

    // Assetscopy的事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CopyAssetOrDownLoadStatus event) {
        switch (event.getCopyStatus()) {
            case CopyAssetOrDownLoadStatus.Start:
                break;
            case CopyAssetOrDownLoadStatus.Finish:
                if (needPageUrl != null) {
                    SailerManagerHelper.getInstance().getSailerProxyHelper().handleRedirect(needPageUrl, mActivity, xWalkView);
//                    HandleH5Utils.getInstance().handleRedirect(needPageUrl, (BaseTopActivity) getActivity(), BGWebView);
//                    needPageUrl = null;
                }
                if (mProcessRl != null && updateLogo != null) {
                    Log.d("ZernTestkadun", "--------BBBBBBBBBB-----------");
                    updateLogo.setText("正在努力加载中...");
                    mProcessRl.setVisibility(View.GONE);
                    updateLogo.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("ZernTestkadun", "--------CCCCCCCCCC-----------");
                            updateLogo.setVisibility(View.GONE);
                        }
                    }, 2000);
                }
                break;
            case CopyAssetOrDownLoadStatus.DownFileError: // 院内特有。
                if (updateErrorTv != null) {
                    updateErrorTv.setVisibility(View.VISIBLE);
                    updateErrorTv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateErrorTv.setVisibility(View.GONE);
                        }
                    }, 1500);
                }
                if (mProcessRl != null) {
                    mProcessRl.setVisibility(View.GONE);
                }
                if (updateLogo != null) {
                    updateLogo.setVisibility(View.GONE);
                }
                // 如果是第一次安装就遇到了H5的更新。而且在下载文件更新时候遇到了错误将会带来白屏。此时应该copy
                // 判断是否第一次打开.间接判断是否有本地版本号
                if (SailerActionHandler.CURRENT_H5FILES_VERSION == SailerActionHandler.ERROR_VCODE) { // 无网络。并且是第一次安装且版本号是比预打包的高需要下载新 但是无网络的状态 Copy
                    // copy 资产目录的H5文件
                    SailerUpdateHelper.getInstance().CopyAssetWithNotNet();
                }
                break;
            case CopyAssetOrDownLoadStatus.Fail:
                if (mProcessRl != null) {
                    mProcessRl.setVisibility(View.GONE);
                }
                if (updateLogo != null) {
                    updateLogo.setText("网络异常。请去掉代理再连接网络");
                    updateLogo.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("ZernTestkadun", "--------CCCCCCCCCC-----------");
                            updateLogo.setVisibility(View.GONE);
                        }
                    }, 2000);
                }
                break;
        }
    }


    public MyXwalkView getxWalkView() {
        return xWalkView;
    }
}
