//package com.jkys.sailerxwalkview.util;
//
//import android.Manifest;
//import android.app.Dialog;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Message;
//import android.view.Gravity;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//import com.jkys.jkysbase.DeviceUtil;
//import com.jkys.jkysbase.ZernToast;
//import com.jkys.jkysbase.baseclass.BaseTopActivity;
//import com.jkys.sailerxwalkview.R;
//
//import java.lang.ref.WeakReference;
//
///**
// * Created by on
// * Author: Zern
// * DATE: 18/1/31
// * Time: 16:11
// * Email:AndroidZern@163.com
// */
//
//public class UpdateApkUtils {
//
//    private WeakReference<BaseTopActivity> activityWR;
//
//    public UpdateApkUtils(BaseTopActivity activity) {
//        activityWR = new WeakReference<BaseTopActivity>(activity);
//    }
//
//    public void startDownLoadApk() {
//        if (activityWR == null || activityWR.get() == null) return;
//        BaseTopActivity activity = activityWR.get();
//        if (activity.checkWriteStoragePermission()) {
//            downloadApk(false);
//        } else {
//            activity.setRequestPermissionsResultProxy(new BaseTopActivity.RequestPermissionsResultProxy() {
//                @Override
//                public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//                    if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                            // Permission Granted
//                            downloadApk(false);
//                        } else {
//                            // Permission Denied
//                        }
//                    }
//                    return false;
//                }
//            });
//        }
//    }
//
//    private void downloadApk(boolean isforce) {
//        initDownload();
//        showLoadingDialog(isforce);
//        apkUtil.downLoadStart();
////        myDialog.dismiss();
//    }
//
//    private LoadingDialog mLoadingDialog;//下载进度对话框
//    private int totalLengths;//应用包总大小
//    private int length;//当前总进度
//
//    //显示下载对话框
//    private void showLoadingDialog(final boolean isforce) {
//        try {
//            if (activityWR == null || activityWR.get() == null)
//                return;
//            BaseTopActivity activity = activityWR.get();
//            mLoadingDialog = new LoadingDialog.Builder()
//                    .setCloseListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (apkUtil.getLoadingState()) {
//                                showCancelDialog(isforce);
//                            } else {
//                                mLoadingDialog.closeDialog();
//                                if (isforce) {
//                                    BaseTopActivity.finishAll();
//                                }
//                            }
//                        }
//                    })
//                    .build(activity);
//            mLoadingDialog.setMsginfo("下载进度  " + 0 + "%");
//            mLoadingDialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Dialog updateDialog;
//
//    //取消下载对话框
//    private void showCancelDialog(final boolean isforce) {
//        if (activityWR == null || activityWR.get() == null)
//            return;
//        BaseTopActivity activity = activityWR.get();
//
//        if (updateDialog == null) {
//            updateDialog = new Dialog(activity, R.style.ImageloadingDialogStyle);
//        }
//        Window window = updateDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        updateDialog.setContentView(R.layout.update_dialog);
//        updateDialog.setCancelable(false);
//        TextView leftTv = (TextView) window.findViewById(R.id.tv_btnleft);
//        TextView rightTv = (TextView) window.findViewById(R.id.tv_btnright);
//        TextView title = (TextView) window.findViewById(R.id.title);
//        leftTv.setText("取消");
//        rightTv.setText("确定");
//        title.setText("确定要中断下载吗？");
//        leftTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateDialog.dismiss();
//            }
//        });
//        rightTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateDialog.dismiss();
//                apkUtil.cancelDownLoad();
//                mLoadingDialog.closeDialog();
//                if (isforce) {
//                    BaseTopActivity.finishAll();
//                }
//            }
//        });
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = (int) (DeviceUtil.getScreenW() * 0.75);
//        lp.gravity = Gravity.CENTER;
//        window.setAttributes(lp);
//        updateDialog.show();
//    }
//
//    private DownLoadApkUtil apkUtil;
//    private LoadHandler mHander;
//
//    private void initDownload() {
//        if (activityWR == null || activityWR.get() == null) return;
//        BaseTopActivity activity = activityWR.get();
//        apkUtil = new DownLoadApkUtil(activity, new LoadingListener(activity),
//                DownLoadApkUtil.downLoadUrl);
//        mHander = new LoadHandler(activity);
//    }
//
//
//    class LoadingListener implements DownLoadApkUtil.DownLoadListener {
//        WeakReference<BaseTopActivity> weakReference;
//
//        LoadingListener(BaseTopActivity mContext) {
//            weakReference = new WeakReference<BaseTopActivity>(mContext);
//        }
//
//        @Override
//        public void loadProgress(int len) {
//            if (weakReference == null || weakReference.get() == null) return;
//            length = len;
//            Message message = new Message();
//            message.arg1 = 1;
//            mHander.sendMessage(message);
//        }
//
//        @Override
//        public void totalLength(int totalLen) {
//            if (weakReference == null || weakReference.get() == null) return;
//
//            totalLengths = totalLen;
//        }
//
//        @Override
//        public void loadError() {
//            if (weakReference == null || weakReference.get() == null) return;
//            Message message = new Message();
//            message.arg1 = 2;
//            mHander.sendMessage(message);
//        }
//
//        @Override
//        public void loadSuccess() {
//            if (weakReference == null || weakReference.get() == null) return;
//            Message message = new Message();
//            message.arg1 = 3;
//            mHander.sendMessage(message);
//        }
//
//        @Override
//        public void loadErrorNoSpare() {
//            if (weakReference == null || weakReference.get() == null) return;
//            Message message = new Message();
//            message.arg1 = 4;
//            mHander.sendMessage(message);
//        }
//
//        @Override
//        public void fileNotFound() {
//            if (weakReference == null || weakReference.get() == null) return;
//            Message message = new Message();
//            message.arg1 = 5;
//            mHander.sendMessage(message);
//        }
//    }
//
//    class LoadHandler extends Handler {
//        WeakReference<BaseTopActivity> weakReference;
//
//        LoadHandler(BaseTopActivity activity) {
//            weakReference = new WeakReference<BaseTopActivity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            try {
//                if (weakReference == null || weakReference.get() == null) return;
//                BaseTopActivity activity = weakReference.get();
//                switch (msg.arg1) {
//                    case 1:
//                        int total = (int) ((length * 1.0 / totalLengths) * 100);
//                        mLoadingDialog.setMsginfo("下载进度  " + total + "%");
//                        break;
//                    case 2:
//                        ZernToast.showToastForTime(activity, "系统异常", Gravity.CENTER, 0, 0, 1500);
//                        mLoadingDialog.stopAnimation();
//                        break;
//                    case 3:
//                        mLoadingDialog.stopAnimation();
//                        mLoadingDialog.closeDialog();
//                        apkUtil.installApk(activity);
//                        break;
//                    case 4:
//                        ZernToast.showToastForTime(activity, "存储空间不足", Gravity.CENTER, 0, 0, 1500);
//                        mLoadingDialog.stopAnimation();
//                        break;
//
//                    case 5:
//                        try {
//                            if (Build.VERSION.SDK_INT >= 23) {
//                                activity.showSpecialPermissionsJumpDialog("存储");
//                            } else {
//                                ZernToast.showToastForTime(activity, "apk下载失败，本地文件不存在", Gravity.CENTER, 0, 0, 1500);
//                            }
//                            mLoadingDialog.stopAnimation();
////                            activity.mLoadingDialog.closeDialog();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        break;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
