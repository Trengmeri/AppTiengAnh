package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.api.ApiCallback;
import com.example.test.api.QuestionManager;
import com.example.test.model.Question;
import com.example.test.ui.GrammarPick1QuestionActivity;
import com.example.test.ui.GrammarPickManyActivity;
import com.example.test.ui.ListeningQuestionActivity;
import com.example.test.ui.RecordQuestionActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NevigateQuestion extends AppCompatActivity {
    private String skill;
    private int currentQuestionIndex; // Vị trí câu hỏi hiện tại
    private List<Integer> questionIds = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private QuestionManager quesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            skill = intent.getStringExtra("skill");
            questionIds = (List<Integer>) intent.getSerializableExtra("questionIds");
        }

        if (questionIds == null || questionIds.isEmpty() || skill == null) {
            finish(); // Nếu dữ liệu không hợp lệ, đóng Activity ngay lập tức
            return;
        }

        quesManager = new QuestionManager(this);
        currentQuestionIndex = 0; // Bắt đầu từ câu hỏi đầu tiên

        // Gọi API để lấy danh sách câu hỏi
        fetchQuestionsFromAPI();
    }

    private void fetchQuestionsFromAPI() {
        for (Integer id : questionIds) {
            quesManager.fetchQuestionContentFromApi(id, new ApiCallback<Question>() {
                @Override
                public void onSuccess(Question question) {
                    if (question != null) {
                        questions.add(question);

                        // Khi đã lấy đủ tất cả câu hỏi, chuyển sang Activity tiếp theo
                        if (questions.size() == questionIds.size()) {
                            navigateToActivity(questions.get(currentQuestionIndex));
                        }
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    finish(); // Nếu có lỗi, đóng Activity
                }

                @Override
                public void onSuccess() {}
            });
        }
    }

    private void navigateToActivity(Question question) {
        Intent intent = null;

        if ("READING".equals(skill)) {
            intent = new Intent(this, question.getQuesType().equals("CHOICE")
                    ? GrammarPick1QuestionActivity.class
                    : GrammarPickManyActivity.class);
        } else if ("LISTENING".equals(skill)) {
            intent = new Intent(this, ListeningQuestionActivity.class);
        } else if ("SPEAKING".equals(skill)) {
            intent = new Intent(this, RecordQuestionActivity.class);
        }

        if (intent != null) {
            intent.putExtra("currentQuestionIndex", currentQuestionIndex);
            if (questions == null || questions.isEmpty()) {
                Log.e("NevigateQuestion", "Danh sách câu hỏi bị null hoặc rỗng trước khi gửi!");
            } else {
                Log.d("NevigateQuestion", "Danh sách câu hỏi trước khi gửi: " + questions);
            }
            Collections.sort(questions, Comparator.comparingInt(Question::getId));
            intent.putExtra("questions", (Serializable) questions);
            startActivity(intent);
        }
        finish(); // Đóng Activity ngay sau khi chuyển hướng
    }
}
