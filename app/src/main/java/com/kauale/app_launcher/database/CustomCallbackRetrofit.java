package com.kauale.app_launcher.database;

public interface CustomCallbackRetrofit<T> {
    void onSuccess(T object);
    void onFailure(T object);
}
