package com.example.test.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.QuestionManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.Result;

public class QuestionActivity extends AppCompatActivity {

    private QuestionManager quesManager = new QuestionManager(this);
    private TextView tvQuestionContent;
//    private ViewStub choiceViewStub;
//    private ViewStub recordingViewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

//        tvQuestionContent = findViewById(R.id.tvQuestionContent);
//        choiceViewStub = findViewById(R.id.choice_viewstub);
//        recordingViewStub = findViewById(R.id.recording_viewstub);

        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
            int questionId = bundle.getInt("questionId");

            quesManager.fetchQuestionContentFromApi(questionId, new ApiCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onSuccess(Question question) {
                    if (question!= null) {
                        runOnUiThread(() -> {
                            String quesType = question.getQuesType();

//                            if ("CHOICE".equals(quesType)) {
//                                View choiceView = choiceViewStub.inflate();
//                                // Lấy các View con trong choiceView và xử lý logic chọn đáp án
//                                //...
//                            } else if ("RECORDING".equals(quesType)) {
//                                View recordingView = recordingViewStub.inflate();
//                                // Lấy các View con trong recordingView và xử lý logic ghi âm
//                                //...
//                            }

                            // Hiển thị nội dung câu hỏi
                            tvQuestionContent.setText(question.getQuesContent());

                            // Hiển thị các thành phần khác (nếu có)
                            //...
                        });
                    } else {
                        Log.e("QuestionActivity", "Câu hỏi trả về là null.");
                    }
                }

                @Override
                public void onSuccess(Lesson lesson) {

                }

                @Override
                public void onSuccess(Course course) {

                }

                @Override
                public void onSuccess(Result result) {

                }

                @Override
                public void onSuccess(Answer answer) {

                }

                @Override
                public void onSuccess(MediaFile mediaFile) {

                }

                //... Các phương thức callback khác...

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("QuestionActivity", errorMessage);
                }

                @Override
                public void onSuccessWithOtpID(String otpID) {

                }

                @Override
                public void onSuccessWithToken(String token) {

                }
            });
        } else {
            Log.e("QuestionActivity", "Không nhận được questionId từ Intent.");
        }
    }
}