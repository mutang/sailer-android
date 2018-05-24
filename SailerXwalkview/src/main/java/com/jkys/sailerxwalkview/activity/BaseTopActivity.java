package com.jkys.sailerxwalkview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wuweixiang on 16/8/24.
 * 最顶层activity，放置所有activity的公用处理
 */
public abstract class BaseTopActivity extends FragmentActivity {
    public static List<Activity> sActivities = new LinkedList<Activity>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        super.onCreate(savedInstanceState);
        sActivities.add(this);
    }

    @Override
    protected void onDestroy() {
        sActivities.remove(this);
        super.onDestroy();
    }

    public static Activity getTopActivity() {
        if (sActivities.size() > 0) {
            return sActivities.get(sActivities.size() - 1);
        } else {
            return null;
        }
    }
}
