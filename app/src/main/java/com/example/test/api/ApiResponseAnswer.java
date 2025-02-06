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
    private Data data; // Sử dụng lớp Data để chứa danh sách Answer

    // Thêm thông tin phân trang
    @SerializedName("page")
    private int page;

    @SerializedName("totalPages")
    private int totalPages;

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

    // Getters và Setters cho page và totalPages
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Lớp Data để chứa danh sách Answer
    public static class Data {
        @SerializedName("content")
        private List<Answer> content; // Danh sách các Answer

        public List<Answer> getContent() {
            return content;
        }

        public void setContent(List<Answer> content) {
            this.content = content;
        }
    }
}