package com.zern.sailerandroidxwalkview;

import android.app.Application;

import com.zern.ioc.SailerProxyRegister;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/5/24
 * Time: 11:27
 * Email:AndroidZern@163.com
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SailerProxyRegister.initSailer();
    }
}
