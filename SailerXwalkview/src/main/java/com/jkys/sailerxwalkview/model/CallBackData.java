package com.jkys.sailerxwalkview.model;

import java.io.Serializable;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/7/11
 * Time: 11:34
 * Email:AndroidZern@163.com
 */
public class CallBackData <T> implements Serializable{

    /**
     * returnCode : 0000
     * returnMsg : 调用成功
     * data : {"uid":"","sn":"","appver":"","chr":"","token":""}
     */

    private String returnCode;
    private String returnMsg;
    /**
     * uid :
     * sn :
     * appver :
     * chr :
     * token :
     */

    private T data;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
