package com.example.test.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public abstract class BaseApiManager {

    public static final String BASE_URL = "http://192.168.56.1:8080"; // Thay đổi URL của bạn nếu cần
    protected static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

}