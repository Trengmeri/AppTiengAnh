package com.example.test.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {

    @SerializedName("id")
    private int id;

    @SerializedName("quesContent")
    private String quesContent;

    @SerializedName("quesType")
    private String quesType;

    @SerializedName("point")
    private int point;

    @SerializedName("questionChoices")
    private List<Choice> questionChoices;

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuesContent() {
        return quesContent;
    }

    public void setQuesContent(String quesContent) {
        this.quesContent = quesContent;
    }

    public String getQuesType() {
        return quesType;
    }

    public void setQuesType(String quesType) {
        this.quesType = quesType;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public List<Choice> getQuestionChoices() {
        return questionChoices;
    }

    public void setQuestionChoices(List<Choice> questionChoices) {
        this.questionChoices = questionChoices;
    }
}
