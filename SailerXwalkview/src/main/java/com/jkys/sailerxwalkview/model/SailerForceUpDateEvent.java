package com.jkys.sailerxwalkview.model;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/8/2
 * Time: 15:03
 * Email:AndroidZern@163.com
 */
public class SailerForceUpDateEvent {

    // 100 表示 开始了， 200 表示更新完成。 300 表示其他，暂时未定
    private int ForceStatus;

    public static final int Start = 100;
    public static final int Finish = 200;
    public static final int Fail = 300;

    public SailerForceUpDateEvent(int forceStatus) {
        ForceStatus = forceStatus;
    }

    public int getForceStatus() {
        return ForceStatus;
    }
}
