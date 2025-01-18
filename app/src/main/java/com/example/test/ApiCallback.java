package com.example.test;

import com.example.test.model.Question;

public interface ApiCallback {
    void onSuccess();
    void onSuccess(Question question);
    void onFailure(String errorMessage);
    void onSuccessWithOtpID(String otpID);
   // void onSuccess(String token);
/*    void onQuestionsSuccess(List<Question> questions);
    void onAnswersSuccess(List<Answer> answers);
    void onPointsSuccess(List<Point> points);*/
}