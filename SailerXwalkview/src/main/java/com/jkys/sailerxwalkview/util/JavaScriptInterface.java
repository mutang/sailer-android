package com.jkys.sailerxwalkview.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkView;

/**
 * Created by tom on 16/1/5.
 */



public class JavaScriptInterface {
    private Context mContxt;
    private String uid;
    private String newToken;
    private XWalkView mXWalkView;

    public  JavaScriptInterfaceInterface javaScriptInterfaceInterface;

    public JavaScriptInterface(Context mContxt, String uid, String newToken, XWalkView mXWalkView) {
        this.mContxt = mContxt;
        this.uid = uid;
        this.newToken = newToken;
        this.mXWalkView = mXWalkView;
    }

    public  JavaScriptInterface(JavaScriptInterfaceInterface javaScriptInterfaceInterface){
        this.javaScriptInterfaceInterface=javaScriptInterfaceInterface;
    }

    public void fun1FromAndroid(String name) {
        Toast.makeText(mContxt, name, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public String jsCallAndroidMethod(String data){
        Log.i("jsCallAndroidMethod",data);
        return this.javaScriptInterfaceInterface.jsCallAppMethod(data);
    }

}
