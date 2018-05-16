package com.zern.ioc;

import android.app.Activity;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/5/11
 * Time: 17:40
 * Email:AndroidZern@163.com
 */

public class ViewBinder {
    private static final String SUFFIX = "$$ViewInjector";

    // Activity 调用的方法
    public static void bind(Activity activity) {
        bind(activity, activity);
    }

    /**
     * 1. 寻找对应的代理类
     * 2. 调用接口提供的绑定方法
     * @param host
     * @param root
     */
    private static void bind(Activity host, Activity root) {
        if (host == null || root == null) {
            return;
        }

        Class<? extends Activity> aClass = host.getClass(); // 反射获取到Activity类
        String proxyClassFullName = aClass.getName() + SUFFIX; // 拿到Activity的类名并且拼接生成类的名称
        try {
            Class<?> proxyClass = Class.forName(proxyClassFullName);
            ViewInjector viewInjector = (ViewInjector) proxyClass.newInstance();
            if (viewInjector != null) {
                viewInjector.inject(host, root);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }
}
