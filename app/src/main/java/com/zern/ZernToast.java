package com.zern;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by on
 * Author: Zern
 * DATE: 18/5/25
 * Time: 14:35
 * Email:AndroidZern@163.com
 */

public class ZernToast {
    private static Toast mToast = null;

    public static void showToast(Context context, String msgInfo) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msgInfo);
        mToast.show();
    }

    // 设置Toast显示的位置
    public static void showToast(Context context, String msgInfo, int gravity, int xOffset, int yOffset) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msgInfo);
        mToast.setGravity(gravity, xOffset, yOffset);
        mToast.show();
    }


    // 设置Toast显示的时间
    public static void showToastForTime(Context context, String msgInfo, int gravity, int xOffset, int yOffset, int time) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), "", time);
        }
        mToast.setText(msgInfo);
        mToast.setGravity(gravity, xOffset, yOffset);
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
