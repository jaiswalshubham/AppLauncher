package com.kauale.app_identifer.database;

public interface CustomCallbackRetrofit<T> {
    void onSuccess(T object);
    void onFailure(T object);
}
