package com.jkys.sailerxwalkview.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.jkys.sailerxwalkview.util.SailerManagerHelper;

import org.xwalk.core.XWalkView;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/10/27
 * Time: 15:41
 * Email:AndroidZern@163.com
 */

public class MyXwalkView extends XWalkView {
    public MyXwalkView(Context context) {
        super(context);
    }

    public MyXwalkView(Context context, Activity activity) {
        super(context, activity);
    }

    public MyXwalkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (SailerManagerHelper.getInstance().getSailerProxyHelper().isNeedHandlerBackKeyPress()
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // 是否客户端需要拦截物理返回键的监听。
            if (event.getAction() == KeyEvent.ACTION_UP) { // 在分享视图showing时 消费返回键弹起的事件。
                SailerManagerHelper.getInstance().getSailerProxyHelper().dismissSharePopup();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
}
