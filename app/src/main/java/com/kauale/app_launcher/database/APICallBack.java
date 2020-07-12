package com.kauale.app_launcher.database;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kauale.app_launcher.MainActivity;
import com.kauale.app_launcher.home.AppCountryModel;
import com.kauale.app_launcher.logs.AppIndentifierLogs;
import com.kauale.app_launcher.roomdb.CallBackInterface;
import com.kauale.app_launcher.roomdb.DBAppIdentifier;
import com.kauale.app_launcher.roomdb.PackageInfoDao;
import com.kauale.app_launcher.utils.CommonUtil;
import com.kauale.app_launcher.utils.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class APICallBack {
    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
    private OkHttpClient client;
    private static APICallBack instance;

    public APICallBack(String s) {
       this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .dispatcher(new Dispatcher())
                .build();
    }

    public APICallBack() {

    }

    private APICallBack getInstance() {
        if(instance != null)
            return instance;
        else {
            instance = new APICallBack(null);
            return instance;
        }
    }

    public void getData(Context context, CallBackInterface callBack){
        String url = ApiClient.baseUrl  + "fetchAppData.json";
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        HttpUrl httpUrl = httpUrlBuilder.build();
        Request  request = new Request.Builder()
                .url(httpUrl)
                .build();
        getInstance().client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                System.out.println("Error okhttp3 in fetching data");

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                ResponseBody responseBody = response.body();
                try {
                    if(response.isSuccessful()){
                        String responseStr = responseBody.string();
                        JSONObject jsonObject = new JSONObject(responseStr);
                        System.out.println(responseStr);
                        if(response != null && response.isSuccessful()){
                            String [] array = jsonObject.toString().split("\\{*\\}");
                            String responsestring = "[";
                            int i = 0;
                            for (String k:array) {
                                if(i == array.length -1){
                                    responsestring += k.substring(k.indexOf(":")).substring(1) + "}]";
                                }else  responsestring += k.substring(k.indexOf(":")).substring(1) + "},";
                                i++;
                            }

                        List<AppCountryModel> appCountryModels = new Gson().fromJson(responsestring, TypeToken.getParameterized(ArrayList.class, AppCountryModel.class).getType());
                            System.out.println(new Gson().toJson(appCountryModels));
                            List<AppCountryModel> defaultAppCountryModel = new ArrayList<>();
                            for (AppCountryModel appCountryModel: appCountryModels){
                                if(CommonUtil.isValidString(appCountryModel.getA())){
                                    appCountryModel.setPackageName(appCountryModel.getA());
                                    appCountryModel.setAppName(appCountryModel.getB());
                                    appCountryModel.setCountryName(appCountryModel.getC());
                                }else if (!CommonUtil.isValidString(appCountryModel.getPackageName()))
                                                defaultAppCountryModel.add(appCountryModel);
                            }
                            appCountryModels.removeAll(defaultAppCountryModel);
                            DBAppIdentifier dbAppIdentifier = DBAppIdentifier.getInstance(context);
                            PackageInfoDao packageInfoDao = dbAppIdentifier.packageInfoDao();
                            for (AppCountryModel appCountryModel : appCountryModels) {
                                try {
                                    packageInfoDao.insert(appCountryModel);
                                }catch (Exception e){
                                    AppIndentifierLogs.printStackTrace(e);
                                }
                            }
                            packageInfoDao.insertAll(appCountryModels);
                            if(callBack != null){
                                if(callBack instanceof MainActivity)
                                    callBack.onSuccessResponse(appCountryModels, Constants.MAIN);
                                else callBack.onSuccessResponse(appCountryModels,Constants.FRAG);
                            }
                        }
                    }
                }catch (Exception e){
                    AppIndentifierLogs.printStackTrace(e);
                }

            }
        });

    }


}
