package com.kauale.app_launcher.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kauale.app_launcher.application.MyApplication;
import com.kauale.app_launcher.logs.AppIndentifierLogs;
import com.kauale.app_launcher.MainActivity;
import com.kauale.app_launcher.R;


public class SuperActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getDbAppIdentifier();
    }

    public void gotoActivity(Class activityClass, Bundle bundle) {
        try {
            Intent intent = new Intent(this, activityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
    }

    public void gotoFragment(Fragment fragment, Bundle bundle, String backStackTag, Context activity) {
        try {
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(0, 0);
            if (activity instanceof MainActivity) {
                fragmentTransaction.replace(R.id.frame_main, fragment);
            }
            if (CommonUtil.isValidString(backStackTag)) {
                fragmentTransaction.addToBackStack(backStackTag);
            }
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
    }

    public void goToActivityWithoutFinish(Context context, Bundle bundle, Class<?> aClass){
        Intent intentLogin = new Intent(context, aClass);
        if(bundle != null)
            intentLogin.putExtras(bundle);
        startActivity(intentLogin);
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        }catch (Exception e){
            AppIndentifierLogs.printStackTrace(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}