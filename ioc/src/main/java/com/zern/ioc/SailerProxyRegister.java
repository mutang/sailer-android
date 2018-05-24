package com.zern.ioc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/5/11
 * Time: 17:40
 * Email:AndroidZern@163.com
 */

public class SailerProxyRegister {
    private static final String proxyPackName = "com.sailer";
    private static final String proxyClassName = "SailerActionRegister$$Zern";
    private static final String proxyClassMethod = "initSailerActionsMap";

    /**
     * 反射执行编译自动生成的代码。
     */
    public static void initSailer() {
        try {
            Class<?> proxyClass = Class.forName(proxyPackName + "." + proxyClassName);
            Object o = proxyClass.newInstance();
            Method initSailerActionsMap = proxyClass.getDeclaredMethod(proxyClassMethod);
            initSailerActionsMap.setAccessible(true);
            initSailerActionsMap.invoke(o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
