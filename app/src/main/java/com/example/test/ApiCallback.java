package com.example.test;

public interface ApiCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}