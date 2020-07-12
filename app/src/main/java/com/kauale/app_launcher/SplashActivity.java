package com.kauale.app_launcher;

import android.os.Bundle;

import com.kauale.app_launcher.database.APICallBack;
import com.kauale.app_launcher.utils.SuperActivity;

public class SplashActivity extends SuperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new APICallBack().getData(this,null);
        gotoActivity(MainActivity.class, null);

    }

}