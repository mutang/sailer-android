package com.jkys.sailerxwalkview.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jkys.sailerxwalkview.activity.BaseTopActivity;
import com.jkys.sailerxwalkview.activity.SailerWebActivity;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/7/12
 * Time: 11:22
 * Email:AndroidZern@163.com
 */
public class SailerResourceClient extends XWalkResourceClient {

//    /**
//     * 网络类型
//     */
//    public static int networkType;

    public SailerResourceClient(XWalkView view) {
        super(view);
    }

    ProgressBar hProgressBar;
    LinearLayout errorPagell;
    RelativeLayout mProcessRl;

    public SailerResourceClient(XWalkView view, ProgressBar hProgressBar, LinearLayout errorPagell, RelativeLayout mProcessRl) {
        super(view);
        this.hProgressBar = hProgressBar;
        this.errorPagell = errorPagell;
        this.mProcessRl = mProcessRl;
    }

    @Override
    public XWalkWebResourceResponse createXWalkWebResourceResponse(String mimeType, String encoding, InputStream data) {
        return super.createXWalkWebResourceResponse(mimeType, encoding, data);
    }

    @Override
    public XWalkWebResourceResponse createXWalkWebResourceResponse(String mimeType, String encoding, InputStream data, int statusCode, String reasonPhrase, Map<String, String> responseHeaders) {
        return super.createXWalkWebResourceResponse(mimeType, encoding, data, statusCode, reasonPhrase, responseHeaders);
    }

    @Override
    public XWalkWebResourceResponse shouldInterceptLoadRequest(XWalkView view, XWalkWebResourceRequest request) {
        String method = request.getMethod();
        Uri uri = request.getUrl();
        String url = uri.toString();
        if (!url.startsWith("http"))
            return super.shouldInterceptLoadRequest(view, request);
        Log.e("wuweixiang", method + " = " + url);
        if (method.equalsIgnoreCase("GET")) {
            // 异步模式
//            lastCheckNetworkTime = System.currentTimeMillis();
            if (NetworkCheckUtil.networkType > 0) {
                Activity activity = BaseTopActivity.getTopActivity();
                NetworkCheckUtil.checkNetwork(activity.getApplicationContext(), false);
            }
            //暂时不拦截
//            final WebResInputStream cris = new WebResInputStream(url);
//            final XWalkWebResourceResponse response = createXWalkWebResourceResponse(UrlHelper.getMimeType(url), "utf-8", cris);
//            requestNetwork(view, response, cris, uri, request.getRequestHeaders(), true);
//            return response;
        } else {
            if (NetworkCheckUtil.networkType > 0) {
                Activity activity = BaseTopActivity.getTopActivity();
                NetworkCheckUtil.checkNetwork(activity.getApplicationContext(), false);
            }
        }
        return super.shouldInterceptLoadRequest(view, request);
    }

    /**
     * 失败处理,依赖打开或者关闭开关
     *
     * @return
     */
    private boolean checkNetwork(final XWalkView view) {
        return checkNetworkAndReSendReqIfNeed(view, null, null, null, null);
    }

    private boolean checkNetworkAndReSendReqIfNeed(final XWalkView view, final XWalkWebResourceResponse response,
                                                   final WebResInputStream cris, final Uri uri,
                                                   final Map<String, String> requestHeaders) {
//        Activity activity = BaseTopActivity.getTopActivity();
//        if (!NetworkUtil.isNetworkAvailable()) {
//            //直接设置成获取失败
//            return true;
//        }
        if (NetworkCheckUtil.networkType == 0)
            return true;

        final Context ctx = BaseTopActivity.getTopActivity().getApplicationContext();
        boolean isChangeOff = NetworkCheckUtil.checkNetwork(ctx, false);
        if (!isChangeOff)
            return true;
        boolean isNetworkConnected = NetworkCheckUtil.waitNetworkConnected(ctx, new NetworkCheckUtil.ResendRequestCallback() {
//            @Override
            public void resendRequest() {
//                if (cris != null && response != null && uri != null)
//                    requestNetwork(view, response, cris, uri, requestHeaders, false);
            }
        });
        if (!isNetworkConnected && NetworkCheckUtil.networkType == 1) {
            Log.e("wuweixiang", "网络木有连接上");
            if (view != null) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
//                        ZernToast.showToast(ctx, "请到“设置-WLAN”手动连接网络");
                    }
                });
            }

//            Toast.makeText(ctx, "请到“设置-WLAN”手动连接网络", Toast.LENGTH_SHORT).show();
            //网络没有连接上,直接返回失败
//            Intent data = new Intent(Settings.ACTION_WIFI_SETTINGS);
//            data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ctx.startActivity(data); //直接进入手机中的wifi网络设置界面
            return true;
        }
        //等候资源重试完成
        return false;
    }

//    private boolean requestFailProcess(final XWalkView view, final XWalkWebResourceResponse response,
//                                       final WebResInputStream cris, final Uri uri,
//                                       final Map<String, String> requestHeaders) {
//        Log.e("wuweixiang", "requestFailProcess");
//
//        if (Build.VERSION.SDK_INT >= 21) {
//            final ConnectivityManager connectivityManager = (ConnectivityManager) BaseTopActivity.getTopActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkRequest.Builder builder = new NetworkRequest.Builder();
//            // 设置感兴趣的网络功能
//            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
//
//            // 设置指定的网络传输类型(蜂窝传输) 等于手机网络
//            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
//
//            // 设置感兴趣的网络：计费网络
////             builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
//
//            NetworkRequest request = builder.build();
//            ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
//                /**
//                 * Called when the framework connects and has declared a new network ready for use.
//                 * This callback may be called more than once if the {@link Network} that is
//                 * satisfying the request changes.
//                 */
////                @TargetApi(Build.VERSION_CODES.M)
//                @Override
//                public void onAvailable(Network network) {
//                    super.onAvailable(network);
//                    Log.e("wuweixiang", "已根据功能和传输类型找到合适的网络");
//
//                    // 可以通过下面代码将app接下来的请求都绑定到这个网络下请求
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        connectivityManager.bindProcessToNetwork(network);
//                    } else {
//                        // 23后这个方法舍弃了
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            ConnectivityManager.setProcessDefaultNetwork(network);
//                        }
//                    }
//                    requestNetwork(view, response, cris, uri, requestHeaders, false);
//
//                    // 也可以在将来某个时间取消这个绑定网络的设置
//                    // if (Build.VERSION.SDK_INT >= 23) {
//                    //      onnectivityManager.bindProcessToNetwork(null);
//                    //} else {
//                    //     ConnectivityManager.setProcessDefaultNetwork(null);
//                    //}
//
//                    // 只要一找到符合条件的网络就注销本callback
//                    // 你也可以自己进行定义注销的条件
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        connectivityManager.unregisterNetworkCallback(this);
//                    }
//
//
//                }
//            };
////            connectivityManager.registerNetworkCallback(request, callback);
//            connectivityManager.requestNetwork(request, callback);
//            return false;
//        }
//
//        return true;
//    }

//    public void checkNetwork(final Context ctx) {


//        new CommonService(ctx, "http://www.baidu.com").getRequestAsync("", null, null,
//                new Subscriber<ResponseBody>() {
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("wuweixiang", "http://www.baidu.com onError");
//                        requestFailProcessByOnOff();
////                        ZernToast.showToast(ctx, "网络异常");
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//
//                    }
//                });

//    }

//    private void requestNetwork(final XWalkView view, final XWalkWebResourceResponse response,
//                                final WebResInputStream cris, final Uri uri,
//                                final Map<String, String> requestHeaders, final boolean isRetry) {
//        Log.e("wuweixiang", "requestNetwork" + " = " + uri.toString());
//        String baseUrl = uri.getScheme() + "://" + uri.getHost();
//        int port = uri.getPort();
//        if (port > 0) {
//            baseUrl = baseUrl + ":" + port;
//        }
//        String path = uri.getPath();
//        Map<String, String> queryMap = new HashMap<>();
//        Set<String> set = uri.getQueryParameterNames();
//        Iterator<String> iterator = set.iterator();
//        while (iterator.hasNext()) {
//            String paramName = iterator.next();
//            queryMap.put(paramName, uri.getQueryParameter(paramName));
//        }
//        final Context ctx = BaseTopActivity.getTopActivity().getApplicationContext();
//        new CommonService(ctx, baseUrl).getRequestAsync(path, requestHeaders, queryMap,
//                new Observer<ResponseBody>() {
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("wuweixiang", "onError");
//                        if (NetworkCheckUtil.networkType > 0) {
//                            if (isRetry) {
//                                boolean result = checkNetworkAndReSendReqIfNeed(view, response, cris, uri, requestHeaders);
//                                if (result) {
//                                    //失败了,设置流为null
//                                    cris.setInputStream(null);
//                                    try {
//                                        response.setStatusCodeAndReasonPhrase(404, "network error");
//                                    } catch (Exception e1) {
//                                        e1.printStackTrace();
//                                    }
////                                ZernToast.showToast(ctx, "网络异常");
//                                }
//                            } else {
//                                cris.setInputStream(null);
//                                try {
//                                    response.setStatusCodeAndReasonPhrase(404, "network error");
//                                } catch (Exception e1) {
//                                    e1.printStackTrace();
//                                }
////                            ZernToast.showToast(ctx, "网络异常");
//                            }
//                        } else {
//                            cris.setInputStream(null);
//                            try {
//                                response.setStatusCodeAndReasonPhrase(404, "network error");
//                            } catch (Exception e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        MediaType mediaType = responseBody.contentType();
//                        cris.setInputStream(responseBody.byteStream());
//                        response.setMimeType(mediaType.type());
//                        Charset charset = mediaType.charset(Charset.forName("utf-8"));
//                        response.setEncoding(charset.name());
//                    }
//                });
//
//    }


    @Override
    public void onLoadStarted(XWalkView view, String url) {
        Log.d("ResourceClientstart", " " + url);
        super.onLoadStarted(view, url);
    }

    @Override
    public void onLoadFinished(XWalkView view, String url) {
        Log.d("ZernClientResourceStop", "onLoadFinished");
        super.onLoadFinished(view, url);
    }

    /**
     * 打开新页面处理
     *
     * @return
     */
    private void openNewPageProcess(XWalkView view, String url) {
        // 当新开页面的时候，除了拨打电话号码的，其他都需要setUserInfo一把
        Activity topActivity = BaseTopActivity.getTopActivity();
        if (topActivity != null) {
            if (SailerManagerHelper.getInstance().getSailerProxyHelper().isHideNavBar(url)) {
                if (BaseTopActivity.getTopActivity() instanceof SailerWebActivity) {
                    SailerWebActivity activity = (SailerWebActivity) topActivity;
                    activity.setFragmentNavigation(View.GONE);
                }
            } else {
                if (topActivity instanceof SailerWebActivity) {
                    SailerWebActivity activity = (SailerWebActivity) topActivity;
                    activity.setFragmentNavigation(View.VISIBLE);
                }
            }
        }

        if (!url.startsWith("tel:")) {
            view.setTag(true);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(XWalkView view, String url) {

//        // 当新开页面的时候，除了拨打电话号码的，其他都需要setUserInfo一把
//        Activity topActivity = BaseTopActivity.getTopActivity();
//        if (topActivity != null) {
//            if (SailerManagerHelper.getInstance().getSailerProxyHelper().isHideNavBar(url)) {
//                if (BaseTopActivity.getTopActivity() instanceof SailerWebActivity) {
//                    SailerWebActivity activity = (SailerWebActivity) topActivity;
//                    activity.setFragmentNavigation(View.GONE);
//                }
//            } else {
//                if (topActivity instanceof SailerWebActivity) {
//                    SailerWebActivity activity = (SailerWebActivity) topActivity;
//                    activity.setFragmentNavigation(View.VISIBLE);
//                }
//            }
//        }
//
//        if (!url.startsWith("tel:")) {
//            view.setTag(true);
//        }
        Log.d("Zern-onPageFinish-wcccc", "" + url);
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
//        super.onReceivedSslError(view, callback, error);
        final String packageName = BaseTopActivity.getTopActivity().getPackageName();
        final PackageManager pm = BaseTopActivity.getTopActivity().getPackageManager();

        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                // debug = true
                callback.onReceiveValue(true);
                return;
            } else {
                // debug = false
                callback.onReceiveValue(true);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // When it doubt, lock it out!
            callback.onReceiveValue(true);
        }
    }

    @Override
    public void onProgressChanged(XWalkView view, int newProgress) {
        if (mProcessRl != null) { // 针对APP一打开 APP初始化资源不足。导致加载H5url慢,有1.5的白屏时间。所以需要显示loading的标志。不然以为是白屏。
            mProcessRl.setVisibility(View.VISIBLE);
            if (100 == newProgress && mProcessRl != null) {
                mProcessRl.setVisibility(View.GONE);
            }
        }
        if (hProgressBar != null) { // 非首页H5不需要显示进度条。
            hProgressBar.setVisibility(View.VISIBLE);
            hProgressBar.setProgress(newProgress);
            if (100 == newProgress) {
                hProgressBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (hProgressBar != null) {
                            hProgressBar.setVisibility(View.GONE);

                        }
                    }
                }, 100);
            }
        }
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
//                    super.onReceivedLoadError(view, errorCode, description, failingUrl);
        Log.e("wuweixiang", "onReceivedLoadError = " + failingUrl + " errorCode= " + errorCode);
        errorPagell.setVisibility(View.VISIBLE);
    }


}
