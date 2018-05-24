package com.jkys.sailerxwalkview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.jkys.sailerxwalkview.R;
import com.jkys.sailerxwalkview.action.SailerActionHandler;
import com.jkys.sailerxwalkview.event.OnlineFishEvent;
import com.jkys.sailerxwalkview.fragment.SailerWebFragment;
import com.jkys.sailerxwalkview.util.SailerManagerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SailerWebActivity extends BaseTopActivity {
    public SailerWebFragment fragment; // 改Fragment拥有的是最基本的Sailer的功能。但是远远不够业务复杂的需求。
    public boolean isNeedRefresh;//是否是需要在H5交互后在该页面的onResume中刷新

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        JkysLog.e("wuweixiang", "onRequestPermissionsResult");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sailer);
        EventBus.getDefault().register(this);
        String pageToUrl = getIntent().getStringExtra(SailerActionHandler.PageToUrl);
        // 初始化你要装载的Fragment 这边子类可以重写 填充一个SailerWebFragment子类 业务层的Fragment更具有业务功能。
        initFragment();
        // 如果传入指令
        if (!TextUtils.isEmpty(pageToUrl)) {
            String pageToUrltrim = pageToUrl.trim();
            Bundle bundle = new Bundle();
            bundle.putString(SailerActionHandler.PageToUrl, pageToUrltrim);
            fragment.setArguments(bundle);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.frame_WebFragment, fragment).show(fragment).commitAllowingStateLoss();
    }

    public void initFragment() {
        fragment = new SailerWebFragment();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedRefresh) {
            if (SailerActionHandler.currentCallId != null && SailerActionHandler.currentXWalkView != null) {
                SailerManagerHelper.getInstance().getSailerProxyHelper().jsOnSuccessCallBack(SailerActionHandler.currentCallId, this, SailerActionHandler.currentXWalkView, "");
            }
            SailerActionHandler.currentCallId = null;
            SailerActionHandler.currentXWalkView = null;
            isNeedRefresh = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                String result = data.getExtras().getString("pay_result");
//                /* 处理返回值
//                 * "success" - payment succeed
//                 * "fail"    - payment failed
//                 * "cancel"  - user canceld
//                 * "invalid" - payment plugin not installed
//                 */
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
////                showMsg(result, errorMsg, extraMsg);
//                if(fragment != null){
//                    if (!StringUtil.isBlank(HandlerH5Utils.currentPayAction)){
//                        fragment.setPay(result, HandlerH5Utils.currentPayAction);
//                        HandlerH5Utils.currentPayAction = null;
//                    }
//                }
//            }
//        }
        //  让H5工具类统一处理支付的回调
        try {
            if (SailerManagerHelper.getInstance().getSailerProxyHelper().HandlerPayResult(requestCode, resultCode, data, this))
                return;
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (requestCode == SailerUIClient.FILE_SELECTED) { // H5处理相册
//            fragment.onActivityResult(requestCode, resultCode, data);
//        } else {
        super.onActivityResult(requestCode, resultCode, data);
//        }
    }

    public void setFragmentNavigation(int visible) {
        if (fragment != null) {
            fragment.setNavigation(visible);
        }
    }

    public void setFragmentTitleText(String title) {
        if (fragment != null) {
            fragment.setTitleText(title);
        }
    }

    public SailerWebFragment getFragment() {
        return fragment;
    }

    public void showLoading() {
        if (fragment != null) {
            fragment.showLoading();
        }
    }

    public void hideLoading() {
        if (fragment != null) {
            fragment.hideLoading();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OnlineFishEvent event) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public SailerWebFragment getSailerFragment() {
        return fragment;
    }
}
