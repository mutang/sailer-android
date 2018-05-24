//package com.jkys.sailerxwalkview.network;
//
//import android.content.Context;
//
//import com.jkys.jkysbase.BaseCommonUtil;
//import com.jkys.jkysnetwork.model.GWRequestModel;
//import com.jkys.jkysnetwork.model.InitNetworkModel;
//import com.jkys.jkysnetwork.model.RequestModel;
//import com.jkys.jkysnetwork.model.UploadSingleFileModel;
//import com.spinytech.macore.MaApplication;
//import com.spinytech.macore.router.LocalRouter;
//import com.spinytech.macore.router.MaActionResult;
//import com.spinytech.macore.router.RouterRequestUtil;
//
///**
// * Created by wuweixiang on 18/2/1.
// */
//
//public class SailerNetworkUtil {
////    private static final String DEMAIN = "cn.dreamplus.wentangdoctor";
//    private static final String PROVIDER = "network";
//
//
//    public static void gwRequest(Context context, GWRequestModel gwRequestModel) {
//        try {
//            LocalRouter.getInstance(MaApplication.getMaApplication())
//                    .route(context, RouterRequestUtil.obtain(context)
//                            .domain(BaseCommonUtil.demain)
//                            .provider(PROVIDER)
//                            .action("gwRequest")
//                            .reqeustObject(gwRequestModel)
//                    );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void request(Context context, RequestModel requestModel) {
//        try {
//            LocalRouter.getInstance(MaApplication.getMaApplication())
//                    .route(context, RouterRequestUtil.obtain(context)
//                            .domain(BaseCommonUtil.demain)
//                            .provider(PROVIDER)
//                            .action("request")
//                            .reqeustObject(requestModel)
//                    );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
