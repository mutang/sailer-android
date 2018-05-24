package com.jkys.sailerxwalkview.event;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/1/23
 * Time: 18:46
 * Email:AndroidZern@163.com
 */

public class SailerSecurityEvent {
    public static final int REFRESH = 1024;
    public static final int CLOSE = 1025;

    private int code;

    public SailerSecurityEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
