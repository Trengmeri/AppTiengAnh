package com.example.test;

public interface ApiCallback {
    void onSuccess();
    void onFailure(String errorMessage);
/*    void onQuestionsSuccess(List<Question> questions);
    void onAnswersSuccess(List<Answer> answers);
    void onPointsSuccess(List<Point> points);*/
}