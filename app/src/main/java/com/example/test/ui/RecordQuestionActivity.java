//package com.example.test.api;
package com.example.test.ui;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.SpeechRecognitionCallback;
import com.example.test.SpeechRecognitionHelper;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;
import com.example.test.model.Result;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecordQuestionActivity extends AppCompatActivity implements SpeechRecognitionCallback {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private ImageView imgVoice, btnPlayAudio;
    private TextView tvTranscription;
    private SpeechRecognitionHelper speechRecognitionHelper;

    private List<String> userAnswers = new ArrayList<>();
    List<String> correctAnswers = new ArrayList<>();
    private List<Question> questions; // Danh sách câu hỏi
    private int currentQuestionIndex; // Vị trí câu hỏi hiện tại
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    private int answerIds;// Danh sách questionIds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_question);

        imgVoice = findViewById(R.id.imgVoice);
        tvTranscription = findViewById(R.id.tvTranscription);
        btnPlayAudio = findViewById(R.id.btn_play);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        seekBar = findViewById(R.id.seekBar);
        // Nhận dữ liệu từ Intent
        currentQuestionIndex = getIntent().getIntExtra("currentQuestionIndex", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");


        // Hiển thị câu hỏi hiện tại
        loadQuestion(currentQuestionIndex);
        fetchAudioUrl(questions.get(currentQuestionIndex).getId());


        // Kiểm tra quyền microphone
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            // Khởi tạo SpeechRecognitionHelper
            imgVoice.setOnClickListener(v ->initializeSpeechRecognition());
        }


        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar


        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = tvTranscription.getText().toString().trim();
            userAnswers.clear(); // Xóa các câu trả lời trước đó
            userAnswers.add(userAnswer); // Thêm câu trả lời mới vào danh sách
            Log.d("RecordQuestionActivity", "User Answers: " + userAnswers);

            if (userAnswers.isEmpty()) {
                Toast.makeText(RecordQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Lưu câu trả lời của người dùng
                quesManager.saveUserAnswer(questions.get(currentStep).getId(), userAnswer, new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.e("RecordQuestionActivity", "Câu trả lời đã được lưu: " + userAnswers.toString());
                        // Hiển thị kết quả sau khi lưu thành công
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswers, correctAnswers, () -> {
                                currentStep++; // Tăng currentStep
                                currentQuestionIndex++;
                                if (currentQuestionIndex < questions.size()) {
                                    updateProgressBar(progressBar, currentStep);
                                    fetchAudioUrl(questions.get(currentQuestionIndex).getId());
                                    loadQuestion(currentQuestionIndex);
                                } else {
                                    finishLesson();
                                }
                            });
                        });
                        resultManager.fetchAnswerPointsByQuesId(questions.get(currentStep).getId(), new ApiCallback<Answer>() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onSuccess(Answer answer) {
                                if (answer != null) {
                                    answerIds = answer.getId();
                                    Log.e("RecordQuestionActivity", "Answer ID từ API: " + answer.getId());
                                    if (answerIds != 0) {
                                        QuestionManager.gradeAnswer(answerIds, new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                            }
                                        });
                                    } else {
                                        Log.e("RecordQuestionActivity", "Bài học không có câu trl.");
                                    }
                                } else {
                                    Log.e("RecordQuestionActivity", "Không nhận được câu trả lời từ API.");
                                }
                            }


                            @Override
                            public void onFailure(String errorMessage) {

                            }

                        });
                    }

                    @Override
                    public void onSuccess(Object result) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("RecordQuestionActivity", errorMessage);

                    }
                });
            }
        });
    }

    private void fetchAudioUrl(int questionId) {

        // Gọi phương thức fetchAudioUrl từ ApiManager
        quesManager.fetchMediaByQuesId(questionId, new ApiCallback<MediaFile>() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(MediaFile mediaFile) {
                runOnUiThread(() -> { // Sử dụng runOnUiThread ở đây
                    if (mediaFile!= null) {
                        btnPlayAudio.setOnClickListener(v -> playAudio(mediaFile.getMaterLink()));
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hiển thị thông báo lỗi nếu có
                Log.e("media",errorMessage);
            }
        });
    }
    private void playAudio (String audioUrl){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(this, R.raw.uaifein);
            mediaPlayer.setOnCompletionListener(mp -> seekBar.setProgress(0));
            mediaPlayer.start();
            /*mediaPlayer.setDataSource(BaseApiManager.BASE_URL + "/" + audioUrl); // Sử dụng đường dẫn đầy đủ
            mediaPlayer.prepare();
            mediaPlayer.start();*/
        } catch (IllegalArgumentException e) {
            Log.e("MediaPlayerError", "IllegalArgumentException: " + e.getMessage()); // In ra lỗi chi tiết
        } /*catch (SecurityException e) {
            Log.e("MediaPlayerError", "SecurityException: " + e.getMessage()); // In ra lỗi chi tiết
        } catch (IllegalStateException e) {
            Log.e("MediaPlayerError", "IllegalStateException: " + e.getMessage()); // In ra lỗi chi tiết
        } catch (IOException e) {
            Log.e("MediaPlayerError", "IOException: " + e.getMessage()); // In ra lỗi chi tiết
        }*/
    }

    private void loadQuestion(int index) {
        if (index < questions.size()) {
            Question question = questions.get(index);
            quesManager.fetchQuestionContentFromApi(question.getId(), new ApiCallback<Question>() {
                @Override
                public void onSuccess(Question question) {
                    if (question != null) {
                        // Cập nhật giao diện người dùng với nội dung câu hỏi
                        runOnUiThread(() -> {
                            // Giả sử bạn có một TextView để hiển thị câu hỏi
                            TextView tvQuestion = findViewById(R.id.tvQuestion);
                            tvQuestion.setText(question.getQuesContent());

                            // Lưu trữ đáp án đúng để kiểm tra sau
                            List<QuestionChoice> choices = question.getQuestionChoices();
                            correctAnswers.clear();
                            for (QuestionChoice choice : choices) {
                                if (choice.isChoiceKey()) {
                                    correctAnswers.add(choice.getChoiceContent());
                                }
                            }
                        });
                    } else {
                        Log.e("RecordQuestionActivity", "Câu hỏi trả về là null.");
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("GrammarPick1QuestionActivity", errorMessage);
                }

                @Override
                public void onSuccess() {}
            });
        } else {
            finishLesson();
        }
    }

    private void finishLesson() {
        finish(); // Hoặc chuyển đến activity khác
    }


    private void initializeSpeechRecognition() {
        speechRecognitionHelper = new SpeechRecognitionHelper(this, this);
        speechRecognitionHelper.startListening();
    }

    private void updateProgressBar(LinearLayout progressBarSteps, int step) {
        if (step < progressBarSteps.getChildCount()) {
            final View currentStepView = progressBarSteps.getChildAt(step);

            // Animation thay đổi màu
            ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(
                    currentStepView,
                    "backgroundColor",
                    Color.parseColor("#E0E0E0"),  // Màu ban đầu
                    Color.parseColor("#C4865E")   // Màu đã hoàn thành
            );
            colorAnimator.setDuration(200); // Thời gian chuyển đổi màu
            colorAnimator.start();
        }
    }

    @Override
    public void onReadyForSpeech() {
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {
        // Gọi khi bắt đầu nhận diện giọng nói
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // Gọi khi thay đổi âm lượng
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // Gọi khi nhận được dữ liệu
    }

    @Override
    public void onEndOfSpeech() {
        Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int error) {
        Log.e("SpeechRecognizerError", "Error code: " + error);
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(ArrayList<String> matches) {
        if (matches != null && !matches.isEmpty()) {
            tvTranscription.setText(matches.get(0));
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // Gọi khi nhận được kết quả từng phần
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // Gọi khi có sự kiện tùy chỉnh
    }

    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        new Thread(() -> {
            while (mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (speechRecognitionHelper != null) {
            speechRecognitionHelper.destroy();
        }
        super.onDestroy();
    }

    // Xử lý yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeSpeechRecognition();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
