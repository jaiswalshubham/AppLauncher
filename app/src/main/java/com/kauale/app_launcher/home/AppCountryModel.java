package com.kauale.app_launcher.home;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "app_country",primaryKeys = {"packageName"})
public class AppCountryModel {

    @NonNull
    String packageName;
    String appName;
    String countryName;

    String a;
    String b;
    String c;

    public AppCountryModel(String appName, String countryName, String packageName) {
        this.appName = appName;
        this.countryName = countryName;
        this.packageName = packageName;
    }

    public AppCountryModel() {
    }

    public AppCountryModel(String appName, String packageName) {

        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }



}
