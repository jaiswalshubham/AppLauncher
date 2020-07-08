package com.kauale.app_identifer.database;

import retrofit2.Call;
import retrofit2.http.GET;

interface ApiInterface {

    @GET("allAppData.json?")
    Call<String> getPackageDataFromDB();

}
