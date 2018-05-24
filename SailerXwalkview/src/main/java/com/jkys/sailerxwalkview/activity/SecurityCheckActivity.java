package com.jkys.sailerxwalkview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jkys.sailerxwalkview.event.SailerSecurityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/1/23
 * Time: 18:43
 * Email:AndroidZern@163.com
 */

public class SecurityCheckActivity extends SailerWebActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SailerSecurityEvent event) {
        switch (event.getCode()) {
            case SailerSecurityEvent.REFRESH:
                if (fragment != null) {
                    fragment.loadPageToUrl();
                }
                break;
            case SailerSecurityEvent.CLOSE:
                finish();
                break;
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
}
