package com.example.test.model;

public class Choice {
    private int id;
    private String choiceContent;
    private boolean choiceKey;

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChoiceContent() {
        return choiceContent;
    }

    public void setChoiceContent(String choiceContent) {
        this.choiceContent = choiceContent;
    }

    public boolean isChoiceKey() {
        return choiceKey;
    }

    public void setChoiceKey(boolean choiceKey) {
        this.choiceKey = choiceKey;
    }
}

