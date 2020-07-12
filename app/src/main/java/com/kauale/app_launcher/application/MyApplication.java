package com.kauale.app_launcher.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.kauale.app_launcher.roomdb.DBAppIdentifier;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    private static MyApplication instance = null;
    private String TAG = MyApplication.class.getName();
    private static FirebaseDatabase firebaseDatabase;
    private static DBAppIdentifier dbAppIdentifier;
    public static final BlockingDeque<Runnable> poolWorkQueue = new LinkedBlockingDeque<>(10);
    public static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 3, 1, TimeUnit.SECONDS, poolWorkQueue);

    public static MyApplication getInstance() {
        return instance;
    }
    public static DBAppIdentifier getDbAppIdentifier(){
        if(dbAppIdentifier == null)
            dbAppIdentifier = DBAppIdentifier.getInstance(getInstance());
        return dbAppIdentifier;
    }

    public static FirebaseDatabase getFirebaseDatabase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return firebaseDatabase;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "AttachBaseContext");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        instance = this;
    }
}
