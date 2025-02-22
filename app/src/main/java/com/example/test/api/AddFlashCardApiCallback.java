package com.example.test.api;

public interface AddFlashCardApiCallback<T> {
    void onSuccess(T response);
    void onFailure(String errorMessage);
}
