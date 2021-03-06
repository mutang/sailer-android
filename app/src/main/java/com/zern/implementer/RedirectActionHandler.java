package com.zern.implementer;

import android.app.Activity;
import android.util.Log;

import com.jkys.sailerxwalkview.action.SailerActionHandler;
import com.zern.ZernToast;
import com.zern.ioc_annotation.SailerRegisterAnnotation;

import org.json.JSONException;
import org.xwalk.core.XWalkView;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/5/24
 * Time: 16:44
 * Email:AndroidZern@163.com
 */
@SailerRegisterAnnotation(SailerActionHandler.redirect)
public class RedirectActionHandler extends SailerActionHandler {
    @Override
    public boolean handlerUrl(String param, Activity activity, XWalkView xWalkView, String callId) throws JSONException {
        Log.d("SailerHandlerUrl", SailerActionHandler.redirect + "#" + param);
        ZernToast.showToast(activity, "SailerHandlerUrl" + SailerActionHandler.redirect + "#" + param);
        return true;
    }
}
