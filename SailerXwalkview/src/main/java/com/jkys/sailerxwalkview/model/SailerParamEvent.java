package com.jkys.sailerxwalkview.model;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/7/13
 * Time: 13:12
 * Email:AndroidZern@163.com
 */
// 用来传递通知商城首页中重新加载html页面
public class SailerParamEvent {

    private String param;

    public SailerParamEvent(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
