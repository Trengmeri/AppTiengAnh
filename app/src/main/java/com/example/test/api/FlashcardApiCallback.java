package com.example.test.api;

import com.example.test.response.ApiResponseFlashcardGroup;

public interface FlashcardApiCallback {
    void onSuccess(ApiResponseFlashcardGroup response);

    void onFailure(String errorMessage);
}