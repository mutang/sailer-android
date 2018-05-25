package com.jkys.sailerxwalkview.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zern on 17/9/5.
 */

public class NetworkCheckUtil {
    /**
     * 网络类型
     */
    public static int networkType;
    public static long lastCheckNetworkTime;
    private static final long CHECK_NETWORK_INTERVAL = 1 * 60 * 1000L;

    public static boolean checkNetwork(Context ctx, boolean isForce) {
        boolean isChangeOff = false;
        if (NetworkCheckUtil.networkType == 0) {
            Log.e("zern", "默认网络状态,不需要处理");
            return isChangeOff;
        }
        long curTime = System.currentTimeMillis();
        if (!isForce && lastCheckNetworkTime > 0 && curTime - lastCheckNetworkTime <= CHECK_NETWORK_INTERVAL) {
            Log.e("zern", "上一次check在指定的时间内");
            return isChangeOff;
        }
        lastCheckNetworkTime = curTime;
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        Log.e("zern", "networkType=" + NetworkCheckUtil.networkType);
        if (NetworkCheckUtil.networkType == 1) {
            //wifi,打开wifi
            Log.e("zern", "打开WIFI");
            if (!wm.isWifiEnabled()) {
                Log.e("zern", "当前WIFI未打开");
                wm.setWifiEnabled(true);
                wm.reconnect();
                boolean isOpen = getMobileDataStatus(ctx);
                if (isOpen) {
                    setMobileDataStatus(ctx, false);
                }
                isChangeOff = true;
            }
//            closeAPN(ctx);
        } else if (NetworkCheckUtil.networkType == 2) {
            //数据网络,关闭wifi
            Log.e("zern", "关闭WIFI");
            if (wm.isWifiEnabled()) {
                Log.e("zern", "当前WIFI未关闭");
                wm.disconnect();
                wm.setWifiEnabled(false);
                isChangeOff = true;
            }
//            boolean isOpen = getMobileDataStatus(ctx);
//            if (!isOpen) {
//                setMobileDataStatus(ctx, true);
//            }
//            openAPN(ctx);
        }
        return isChangeOff;
    }

    public static boolean waitNetworkConnected(Context ctx, ResendRequestCallback callback) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni;
        boolean isNetworkConnected = false;
        int i = 0;
        while (i < 30) {
            ni = cm.getActiveNetworkInfo();
            if (NetworkCheckUtil.networkType == 1) {
                //wifi
                if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI && ni.isConnected()) {
                    //send your http request
                    if (callback != null)
                        callback.resendRequest();
                    isNetworkConnected = true;
                    break;
                }
            } else if (NetworkCheckUtil.networkType == 2) {
                //移动网络
                if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE && ni.isConnected()) {
                    //send your http request
                    if (callback != null)
                        callback.resendRequest();
                    isNetworkConnected = true;
                    break;
                }
            } else {
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }

        return isNetworkConnected;
    }

    // 开启APN
    public static void openAPN(Context ctx) {
        Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        List<APN> list = getAPNList(ctx);
        for (APN apn : list) {
            ContentValues cv = new ContentValues();

            // 获取及保存移动或联通手机卡的APN网络匹配
            cv.put("apn", APNMatchTools.matchAPN(apn.apn));
            cv.put("type", APNMatchTools.matchAPN(apn.type));
            // 更新系统数据库，改变移动网络状态
            ctx.getContentResolver().update(uri, cv, "_id=?", new String[]{
                    apn.id
            });
        }

    }

    // 关闭APN
    public static void closeAPN(Context ctx) {
        Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        List<APN> list = getAPNList(ctx);
        for (APN apn : list) {
            // 创建ContentValues保存数据
            ContentValues cv = new ContentValues();
            // 添加"close"匹配一个错误的APN，关闭网络
            cv.put("apn", APNMatchTools.matchAPN(apn.apn) + "close");
            cv.put("type", APNMatchTools.matchAPN(apn.type) + "close");

            // 更新系统数据库，改变移动网络状态
            ctx.getContentResolver().update(uri, cv, "_id=?", new String[]{
                    apn.id
            });
        }
    }

    public static class APN {
        String id;
        String apn;
        String type;
    }

    private static List<APN> getAPNList(Context ctx) {
        Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        // current不为空表示可以使用的APN
        String projection[] =
                {
                        "_id, apn, type, current"
                };
        // 查询获取系统数据库的内容
        Cursor cr = ctx.getContentResolver().query(uri, projection, null, null, null);

        // 创建一个List集合
        List<APN> list = new ArrayList<APN>();

        while (cr != null && cr.moveToNext()) {

            Log.d("ApnSwitch", "id" + cr.getString(cr.getColumnIndex("_id")) + " \n" + "apn"
                    + cr.getString(cr.getColumnIndex("apn")) + "\n" + "type"
                    + cr.getString(cr.getColumnIndex("type")) + "\n" + "current"
                    + cr.getString(cr.getColumnIndex("current")));

            APN a = new APN();

            a.id = cr.getString(cr.getColumnIndex("_id"));
            a.apn = cr.getString(cr.getColumnIndex("apn"));
            a.type = cr.getString(cr.getColumnIndex("type"));
            list.add(a);
        }

        if (cr != null)
            cr.close();

        return list;
    }

    /**
     * 移动数据开启和关闭
     *
     * @param context
     * @param enabled
     */
    public static void setMobileDataStatus(Context context, boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //ConnectivityManager类

        Class<?> conMgrClass = null;

        //ConnectivityManager类中的字段
        Field iConMgrField = null;
        //IConnectivityManager类的引用
        Object iConMgr = null;
        //IConnectivityManager类
        Class<?> iConMgrClass = null;
        //setMobileDataEnabled方法
        Method setMobileDataEnabledMethod = null;
        try {

            //取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            //取得ConnectivityManager类中的对象Mservice
            iConMgrField = conMgrClass.getDeclaredField("mService");
            //设置mService可访问
            iConMgrField.setAccessible(true);
            //取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            //取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());

            //取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

            //设置setMobileDataEnabled方法是否可访问
            setMobileDataEnabledMethod.setAccessible(true);
            //调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {

            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取移动数据开关状态
     *
     * @param ctx
     * @return
     */
    public static boolean getMobileDataStatus(Context ctx) {
        ConnectivityManager cm;
        cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = cm.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;
        Boolean isOpen = false;
        try {
            Method method = cmClass.getMethod("getMobileDataEnabled", argClasses);
            isOpen = (Boolean) method.invoke(cm, argObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOpen;

    }


    public interface ResendRequestCallback {
        void resendRequest();
    }

}
