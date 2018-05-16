package com.zern.sailerandroidxwalkview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zern.ioc.ViewBinder;
import com.zern.ioc_annotation.BindView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv1)
    public TextView tv1;

    @BindView(R.id.tv2)
    public TextView tv2;

    @BindView(R.id.tv3)
    public TextView tv3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
        tv1.setText("hello world!!!");
    }

}
