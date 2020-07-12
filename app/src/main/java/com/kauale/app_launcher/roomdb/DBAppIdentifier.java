package com.kauale.app_launcher.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kauale.app_launcher.home.AppCountryModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = {AppCountryModel.class}, version = 1)
public abstract class DBAppIdentifier extends RoomDatabase {
    private static DBAppIdentifier instance;
    public abstract PackageInfoDao packageInfoDao();

    public static  synchronized DBAppIdentifier getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, DBAppIdentifier.class, "app_identifer")
                    .allowMainThreadQueries()   //Allows room to do operation on main thread
                    .fallbackToDestructiveMigration()
                    .build();
        return instance;
    }


    public void deleteDatabase(){
        Executor myExecutor = Executors.newFixedThreadPool(1);
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                instance.clearAllTables();
            }
        });
    }
}
