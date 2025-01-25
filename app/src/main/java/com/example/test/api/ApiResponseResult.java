package com.example.test.api;

import com.example.test.model.Course;
import com.example.test.model.Result;
import com.google.gson.annotations.SerializedName;

public class ApiResponseResult {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Result data;

    // Getters và Setters
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result getData() {
        return data;
    }

    public void setData(Result data) {
        this.data = data;
    }
}
