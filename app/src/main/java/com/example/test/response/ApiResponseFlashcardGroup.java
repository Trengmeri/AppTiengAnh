package com.example.test.response;

import com.example.test.model.FlashcardGroup;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponseFlashcardGroup {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private FlashcardGroupData data;

    public static class FlashcardGroupData {
        @SerializedName("content")
        private List<FlashcardGroup> content;

        public List<FlashcardGroup> getContent() {
            return content;
        }

        public void setContent(List<FlashcardGroup> content) {
            this.content = content;
        }
    }

    // Getters và Setters cho statusCode và message
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FlashcardGroupData getData() {
        return data;
    }

    public void setData(FlashcardGroupData data) {
        this.data = data;
    }
}