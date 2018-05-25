package com.jkys.sailerxwalkview.action;

import android.app.Activity;

import org.json.JSONException;
import org.xwalk.core.XWalkView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

//import com.android.volley.RequestQueue;

/**
 * 用来处理SailerAction的抽象类 所有的H5涉及到的常量和变量
 */
public abstract class SailerActionHandler {
    /**\
     * @param action H5传入的action
     * @param param H5传入的param
     * @param activity 和H5交互的activity
     * @param xWalkView 和H5交互的xWalkView
     * @param callId H5交互的action的callId 每一个action它的CallId都不样。
     * @return
     */
    public abstract boolean handlerUrl(String param, final Activity activity, final XWalkView xWalkView, String callId) throws JSONException;

    public final static String hybird = "hybird://";
    public final static String Native = "native";
    public final static String Http = "http";
    public final static String files = "file://";
    public final static String nativeCall = "native-call:";
    public final static String redirect = "redirect";
    public final static String callNativeBack = "callNativeBack";
    public final static String closeBrowser = "closeBrowser";
    public final static String open = "open";
    public final static String setActCode = "setActCode";
    public final static String openMall = "openMall";
    public final static String wxpay = "wxpay";
    public final static String yinLianPay = "yinLianPay";
    public final static String zhaoShangPay = "zhaoShangPay";
    public final static String aliPay = "aliPay";
    public final static String callLogin = "callLogin";
    public final static String callLogout = "callLogout";
    public final static String openShare = "openShare";
    public final static String copyWord = "copyWord";
    public final static String onlineFindDoctorPrescription = "onlineFindDoctorPrescription";
    public final static String asyncGetUserInfo = "asyncGetUserInfo";
    public final static String humanValidSuccess = "humanValidSuccess"; // 人机验证成功Sailer回调方法。
    public final static String humanValidFailed = "humanValidFailed"; // 人机验证失败Sailer回调方法。
    public final static String killProcess = "killProcess";
    public final static String systemSetting = "SystemSettingHandler";
    public final static String checkUpdate = "CheckUpdate";
    public final static String sendHealthPrescriptionPack = "sendHealthPrescriptionPack";
    public final static String test = "test";
    public final static String configNavBar = "configNavBar";
    public final static String showTabBar = "showTabBar";
    public final static String hideTabBar = "hideTabBar";
    public final static String showNavBar = "showNavBar";
    public final static String hideNavBar = "hideNavBar";
    public final static String getNativeData = "getNativeData"; // H5获取 Native数据
    public final static String setNativeData = "setNativeData"; // H5存 数据到Native。
    public final static String exec = "exec";  // 执行一段H5的脚本代码。
    public final static String setOrientation = "setOrientation"; // 配置Native的横竖屏
    public final static String getNetworkStatus = "getNetworkStatus"; // 获取Native网络状态
    public final static String UPDATE_APK = "UPDATE_APK"; // 客户端更新标志

    public static final int THUMBNAIL_ACTIVITY = 13284; // Sailer打开相册组件requestCode

    // 新增一个糖币任务
    public final static String callTangBiGift = "callTangBiGift";
    // 新增扫一扫功能
    public final static String ScanCode = "scanCode";
    // 咨询医生列表
    public final static String callDoctorConsult = "callDoctorConsult";
    // H5调用原生相册
    public static final String openCamera = "openCamera";

    // 跳转Native的原生界面的H5唯一标识
    public final static String pageMall = "page-mall";
    public final static String pageForumTopicMyspace = "page-page-forum-topic-myspace";
    public final static String subPageParam = "subPageParam";

    // 公司内部网络域名
    public final static String INTERNAL_URL = "91jkys.com";
    // Sailer内部文件协议
    public final static String APP_FILE_URL = "cn.dreamplus.wentang";

    // Sailer改成默认显示导航条, 除以下条件
    public final static String SAILER_ACTIVITY = "activity";    // 旧活动页
    public final static String SAILER_HIDENAV = "hidenav=true";    // 带参数的hidenav
    public final static String SAILER_NEWACTIVITY = "newactivity";  // 新活动页
    public final static String SAILER_QUICK_ANSWER = "quick-answer";    // 问答
    public final static String SAILER_DIABETES_KNOWLEDGE = "diabetes-knowledge";    // 百科
    public final static String SAILER_FOODMENU = "foodmenu";    // 食谱
    public final static String MALL_H5 = "mall-h5";    // 商城首页
    public final static String MALL_WECHAT = "mall-wechat";    // 商城首页
    public final static String SAILER_HYBIRD = SailerActionHandler.hybird;    // 商城的文件
    public final static String SAILER_EVENT = "event";    // event 活动
//    public final static String MALL_H5 = "MALL_H5";    // event 活动 TODO 合并代码疑问点
    public static String H5Sharecode = "";
    public static boolean isH5CallCheckUpdate = false; // 院内特有。标志是否是H5唤起Native的更新H5操作。

    public static boolean isH5CallLogin = false;

    // 记录当前回调的CallId 目前只用于登录时候失败 或者成功的回调时保存对应callId
    public static String currentLoginCallId = "";
    // 记录分享的CallId
    public static String currentShareCallId = "";
    // 记录支付的CallId
    public static String currentPayCallId = "";
    // 记录当前需要配置导航栏的id
    public static String currentNavBarCallId = "";
    // 分享参数
    public static String currentNavBarParam = "";
    // NavBar的title记录
//    public static String NavBarTitle = "";

    public static XWalkView currentXwalkViewNormal;

    // 单独给NavBar配置一个webView临时变量
    public static XWalkView currentNavBarXwalkView;

    // 当前正在交互的XWalkView
    public static XWalkView currentXWalkView;
    // 当前正在交互的每次的CallId
    public static String currentCallId;

    // 为了安全点。支付这块还是来个独立点的变量处理，怕有线程安全问题处理不好。
    public static XWalkView currentPayXwalkView;

    // 记录当前的支付PayAction
    public static String currentPayAction;

    // 内嵌Sailer逻辑的Activity所接受的传值字段
    public static String PageToUrl = "pageToUrl";

    public static final String NOT_FOUND = "NotFound";

    public static final String SHOP_ASSEST_FILE_PATH = "build/www/shop";

    // 商城加载的默认地址相对地址目录，会随着热更而改变。但是只会在APP启动的时候赋值一次。这边设置的值是没有用处的。最终还是被getUrlIndex覆盖
//    public static String IndexDir = "file:///android_asset/" + SHOP_ASSEST_FILE_PATH;

    // 是否正在更新。下载文件中。。。
    public static boolean isUpDating = false;

    public static int h5CODE = 17;

//    public static RequestQueue mQueue;

    // V4.6.3
    public static final String locationJSONStr = "{\n" +
            "\"version\": " + h5CODE + ",\n" +
            "\"requestPath\": \"http://static.91jkys.com/mall-h5/build/www/shop\",\n" +
            "\"forceUpdate\": true,\n" +
            "\"files\": {\n" +
            "\"03295a4d4e254b4ee502aaf4046af27c.png\": \"03295a4d4e254b4ee502aaf4046af27c\",\n" +
            "\"115702afd7e537619961f9e8c1904a90.png\": \"115702afd7e537619961f9e8c1904a90\",\n" +
            "\"1ee8fd39d070962dd5c9c8b52bc12519.png\": \"1ee8fd39d070962dd5c9c8b52bc12519\",\n" +
            "\"3fcbb88691945a4eca89e9fe53179021.png\": \"3fcbb88691945a4eca89e9fe53179021\",\n" +
            "\"64712e6f16955a583c1959525840544c.png\": \"64712e6f16955a583c1959525840544c\",\n" +
            "\"98affeb33aded26cafc412b64759b994.png\": \"98affeb33aded26cafc412b64759b994\",\n" +
            "\"Sailer.js\": \"d3862ca9fe50afabc540bfaaf5b2f33a\",\n" +
            "\"Telescope.js\": \"f05a580c9411ad9507cba22d38b3d9f0\",\n" +
            "\"app.css\": \"fc51324b497994f955d2990143477f93\",\n" +
            "\"app.js\": \"0bbbc5ef318081a4b4b93d4a0a8147cb\",\n" +
            "\"b01eea4b651d8f9eb243193d2da5856a.jpg\": \"b01eea4b651d8f9eb243193d2da5856a\",\n" +
            "\"b867c129388c1de33be6071c737cacd2.png\": \"b867c129388c1de33be6071c737cacd2\",\n" +
            "\"cb7b71f41c7de80626f79024d9f7faa5.png\": \"cb7b71f41c7de80626f79024d9f7faa5\",\n" +
            "\"index.html\": \"25cc1164cd8fb1b58a8bfa93d9428bbc\",\n" +
            "\"pingpp.js\": \"e3bb08912c76a8f132ac01f27bc1fa47\",\n" +
            "\"vendors.js\": \"1511386d047301a74f286c68d39215e2\"\n" +
            "}\n" +
            "}";

    // 非常重要的共享参数key，用来保存当前商城资源Index主界面的路径。
    public static final String KEY_SHOP_INDEX_URL = "Shop_IndexUrl_Zern";
    // 商城本地存储版本JSON
//    public static final String KEY_SHOP_VERSION_JSON = "Key_ShopVersionJson_Zern";
    // H5更新次数键
    public static final String KEY_UPDATE_TIMES = "Key_UpDateTimes_Zern";
//    // 本地的Repo版本号
    public static final String KEY_REPO_VERSION_CODE = "Key_RepoVersionCode_Zern";



    // 本地的商城版本号 ------------Sailer新的更新机制 v2版本

//    public static final String KEY_H5_VERSION_CODE = "Key_H5VersionCode_Zern";

    public static final int ERROR_VCODE = 0; // 错误的版本号

//    public static final int ConfigVerion = 230; // 预打包的版本号。

    public static int CURRENT_H5FILES_VERSION = ERROR_VCODE; // 当前热更前老版本的版本号

//    public static final String CONFIG_GLUCOMETER = "config/glucometer.json";

//    public static final String CONFIG_SHOP = "config/shop.json";

//    public static final String CONFIG_HM = "config/patient-manage.json"; // 院内管理

    public static int NEW_H5FILES_VERSION = ERROR_VCODE; // 当前热更后H5文件的版本号

    public static String CURRENT_H5FILES_REPO = null; // 当前热更的模块名 repo字段的值

    // repo 血糖仪
    public static final String glucometer = "glucometer";

    // repo 院内管理
    public static final String patient_manage = "patient-manage";

    public final static String shop = "shop";

    // H5那边需要存的共享参数的字段
    public static final String userInfoH5 = "userInfoH5";

    // --------------互联网医院 获取Native本地数据的key----------------------
    // 互联网医院 电子处方预览
    public static final String onlinePrescriptionPreview = "onlinePrescriptionPreview";
    // 互联网医院 用药指导预览
    public static final String onlineMedicationGuidance = "onlineMedicationGuidance";

    // 日志插入存储map 和其他变量。因为插入频繁。觉得每次new不合适。写一个统一变量。
    public static LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
    // 日志 格式 年月日 时分秒
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 日志日期
    public static Date date = new Date();
}



