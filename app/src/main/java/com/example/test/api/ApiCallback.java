package com.example.test.api;

import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.Result;

import java.util.List;

public interface ApiCallback {
    void onSuccess();
    void onSuccess(Question question);
    void onSuccess(Lesson lesson);
    void onSuccess(Course course);
    void onSuccess(Result result);
    void onSuccess(List<Answer> answer);

    void onSuccess(ApiResponseAnswer response);

    void onFailure(String errorMessage);
    void onSuccessWithOtpID(String otpID);
   // void onSuccess(String token);
/*    void onQuestionsSuccess(List<Question> questions);
    void onAnswersSuccess(List<Answer> answers);
    void onPointsSuccess(List<Point> points);*/
}