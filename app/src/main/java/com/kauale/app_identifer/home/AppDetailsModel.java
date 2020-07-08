package com.kauale.app_identifer.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.kauale.app_identifer.utils.CommonUtil;

import java.io.Serializable;

public class AppDetailsModel implements Serializable {

    String packageName;
    String directory;
//    private  Intent launchIntentForPackage;
    String appName;
    Drawable appIcon;
    String country;

    public AppDetailsModel() {
    }

    public AppDetailsModel(String packageName, String directory, Intent launchIntentForPackage, String appName, Drawable appIcon) {
        this.packageName = packageName;
        this.directory = directory;
//        this.launchIntentForPackage = launchIntentForPackage;
        this.appName = appName;
        this.appIcon = appIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDirectory() {
        return directory;
    }

//    public Intent getLaunchIntentForPackage() {
//        return launchIntentForPackage;
//    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getCountry() {
        if(CommonUtil.isValidString(country))
            return country;
        else return "";
    }
    public void setCountry(String country) {
        this.country = country;
    }

}
