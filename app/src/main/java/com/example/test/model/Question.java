package com.example.test.model;

public class Question {
    private int id;
    private String content;

    // Constructor
    public Question(int id, String content) {
        this.id = id;
        this.content = content;
    }

    // Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
