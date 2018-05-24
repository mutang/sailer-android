package com.jkys.sailerxwalkview.event;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/6/16
 * Time: 13:18
 * Email:AndroidZern@163.com
 */

public class UpdateDialogEvent {
    private int repoCode; // 仓库类型,用来区别不同的模块业务逻辑
    private String param; // 后续或许还需要传值之类的需求.

    public static final int repoShopCode = 100; // PT的商城
    public static final int repoHMCode = 200; // 院内
    public static final int repoHMCode_NoUpdate = 300; // 院内 H5唤起更新之无更新后的回调

    public UpdateDialogEvent(int repoCode) {
        this.repoCode = repoCode;
    }

    public UpdateDialogEvent(int repoCode, String param) {
        this.repoCode = repoCode;
        this.param = param;
    }

    public int getRepoCode() {
        return repoCode;
    }

    public String getParam() {
        return param;
    }
}
