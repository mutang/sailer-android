package com.jkys.sailerxwalkview.model;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/7/18
 * Time: 16:57
 * Email:AndroidZern@163.com
 */
public class SailerNavBarEvent {

    // 10000 是消失， 11111 是显示
    public static final int hideNavBar = 10000;
    public static final int showNavBar = 11111;
    public static final int configNavBar = 22222;

    private int status;

    public SailerNavBarEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
