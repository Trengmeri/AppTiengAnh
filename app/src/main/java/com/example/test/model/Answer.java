package com.example.test.model;

public class Answer {
    private int id;
    private boolean isCorrect;
    private int questionId;
    private int sessionId;
    private int pointAchieved;

    // Constructor
    public Answer(int id,  boolean isCorrect, int questionId, int sessionId, int pointAchieved) {
        this.id = id;
        this.isCorrect = isCorrect;
        this.questionId = questionId;
        this.sessionId = sessionId;
        this.pointAchieved = pointAchieved;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getPointAchieved() {
        return pointAchieved;
    }

    public void setPointAchieved(int pointAchieved) {
        this.pointAchieved = pointAchieved;
    }
}