package com.kauale.app_identifer;

import android.os.Bundle;
import android.os.Handler;

import com.kauale.app_identifer.database.APICallBack;
import com.kauale.app_identifer.utils.SuperActivity;

public class SplashActivity extends SuperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new APICallBack().getData(this,null);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoActivity(MainActivity.class, null);
            }
        },0);

    }

}