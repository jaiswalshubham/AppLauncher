package com.kauale.app_identifer.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kauale.app_identifer.home.AppCountryModel;

import java.util.List;


@Dao
public interface PackageInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppCountryModel... appCountryModels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AppCountryModel> appCountryModels);

    @Update
    void update(AppCountryModel... appCountryModels);

    @Delete
    void delete(AppCountryModel appCountryModel);

    @Query("SELECT * FROM app_country")
    List<AppCountryModel> getAllPackageList();

    @Query("SELECT * FROM app_country WHERE packageName = :packageName")
    AppCountryModel getPackageInfo(String packageName);

    @Query("DELETE FROM app_country")
    void deleteAll();

    @Query("DELETE FROM app_country WHERE appName =:entityId")
    void deleteByAppName(String entityId);
}
