package com.example.test.api;

import com.example.test.response.ApiResponseFlashcard;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.FlashcardGroupResponse;

public interface FlashcardApiCallback {
    void onSuccess(ApiResponseFlashcardGroup response);

    void onSuccess(FlashcardGroupResponse response);

    void onSuccess(ApiResponseFlashcard response);
    
    void onFailure(String errorMessage);
}