package com.example.test.api;

import com.example.test.model.Answer;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponseAnswer {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data; // Change to Data class

    // Getters and Setters
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Inner class to represent the data structure
    public static class Data {
        @SerializedName("content")
        private List<Answer> content; // List of Answer objects

        public List<Answer> getContent() {
            return content;
        }

        public void setContent(List<Answer> content) {
            this.content = content;
        }
    }
}