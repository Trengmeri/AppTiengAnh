package com.example.test.api;

import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;

import java.util.List;

public interface ApiCallback {
    void onSuccess();
    void onSuccess(List<Question> questions);
    void onSuccess(Lesson lesson);
    void onSuccess(Course course);
    void onFailure(String errorMessage);
    void onSuccessWithOtpID(String otpID);
   // void onSuccess(String token);
/*    void onQuestionsSuccess(List<Question> questions);
    void onAnswersSuccess(List<Answer> answers);
    void onPointsSuccess(List<Point> points);*/
}