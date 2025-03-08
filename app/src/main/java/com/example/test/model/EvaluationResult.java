package com.example.test.model;

public class EvaluationResult {
    private String improvements;
    private String evaluation;
    private double score;

    public EvaluationResult(String improvements, String evaluation, double score) {
        this.improvements = improvements;
        this.evaluation = evaluation;
        this.score = score;
    }

    public String getimprovements() {
        return improvements;
    }

    public void setimprovements(String improvements) {
        this.improvements = improvements;
    }

    public String getevaluation() {
        return evaluation;
    }

    public void setevaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public double getPoint() {
        return score;
    }

    public void setPoint(double score) {
        this.score = score;
    }
}
