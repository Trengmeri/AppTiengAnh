package com.example.test.api;

import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Enrollment;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.Result;

import java.util.List;

import java.util.List;

public interface ApiCallback {
    void onSuccess();
    void onSuccess(Question questions);
    void onSuccess(Lesson lesson);
    void onSuccess(Course course);
    void onSuccess(Result result);
    void onSuccess(Answer answer);
    void onSuccess(Enrollment enrollment);
    void onSuccess(MediaFile mediaFile);
    void onFailure(String errorMessage);
    void onSuccessWithOtpID(String otpID);
    void onSuccessWithToken(String token);

}