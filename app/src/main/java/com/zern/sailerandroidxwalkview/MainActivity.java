package com.zern.sailerandroidxwalkview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void OpenSailerDemo(View view) {
        Intent intent = new Intent(this, SailerWebActivity.class);
        intent.putExtra(SailerActionHandler.PageToUrl, "http://static.91jkys.com/sailer/build/demo.html");
        startActivity(intent);
    }
}
