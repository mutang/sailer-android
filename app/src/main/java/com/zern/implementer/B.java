package com.zern.implementer;

import android.app.Activity;

import com.jkys.sailerxwalkview.action.SailerActionHandler;

import org.json.JSONException;
import org.xwalk.core.XWalkView;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/5/24
 * Time: 16:44
 * Email:AndroidZern@163.com
 */

public class B extends SailerActionHandler {
    @Override
    public boolean handlerUrl(String action, String param, Activity activity, XWalkView xWalkView, String callId) throws JSONException {
        return false;
    }
}
