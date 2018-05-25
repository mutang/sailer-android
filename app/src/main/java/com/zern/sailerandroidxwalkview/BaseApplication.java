package com.zern.sailerandroidxwalkview;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.jkys.sailerxwalkview.util.SailerManagerHelper;
import com.zern.ioc.SailerProxyRegister;

import java.util.List;

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
        initMyAppOnCreate();
    }

    private void initMyAppOnCreate() {
        String curProcessName = getCurProcessName(getApplicationContext());
        if (BuildConfig.APPLICATION_ID.equals(curProcessName)) { // 就是我自己的APP进程。防止个推等其他Service服务导致再次初始化Sailer
            SailerInit();
        }
    }

    private void SailerInit() {
        // 1. 注册所有的ActionHandler
        SailerProxyRegister.registerAllAction();
        // 2. 初始配置Xwalkview 使用默认的代理类。建议公司用的话 还是自己去实现代理类。
        SailerManagerHelper.getInstance().initXWalkViewConfig(this);
        // 2.  自己定义实现代理 处理自己公司业务有关的逻辑。
        // SailerManagerHelper.getInstance().initXWalkViewConfig(this, PTSailerProxyHelper.getInstance());
    }

    public String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();
        if (appProcessList == null || appProcessList.size() == 0) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            if (appProcess != null && appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
