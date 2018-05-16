package com.zern.ioc;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/5/11
 * Time: 18:08
 * Email:AndroidZern@163.com
 */

public interface ViewInjector<T> {
    void inject(T t, Object source);
}
