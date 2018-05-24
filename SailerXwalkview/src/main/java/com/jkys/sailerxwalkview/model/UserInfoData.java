package com.jkys.sailerxwalkview.model;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/8/17
 * Time: 10:55
 * Email:AndroidZern@163.com
 */
public class UserInfoData {

    /**
     * uid : 1525349
     * uuid : 810EBLT2D3CJ
     * envType : 1
     * sn : m2 note
     * appver : 4.4.0
     * chr : clt
     * token : 3AB85D4984BF48F68D7F8C6FFC66CAB1
     * activity : BannerActivity
     * inApp : true
     */

    private int uid;
    private String uuid;
    private String envType;
    private String sn;
    private String appver;
    private String chr;
    private String token;
    private String activity;
    private String inApp;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEnvType() {
        return envType;
    }

    public void setEnvType(String envType) {
        this.envType = envType;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAppver() {
        return appver;
    }

    public void setAppver(String appver) {
        this.appver = appver;
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getInApp() {
        return inApp;
    }

    public void setInApp(String inApp) {
        this.inApp = inApp;
    }
}
