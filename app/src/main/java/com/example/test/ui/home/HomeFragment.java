package com.example.test.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Lesson;
import com.example.test.model.Result;
import com.example.test.ui.NotificationActivity;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {
    private Button continueButton;
    private TextView courseTitle, lessonTitle1, lessonNumber;
    private TextView totalPoints, readingPoints, listeningPoints, speakingPoints, writingpoint;
    private ImageView btnNoti, btnStudy, btnExplore, btnProfile;
    private ResultManager resultManager;
    private LessonManager lessonManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo biến sau khi view đã tạo
        resultManager = new ResultManager(requireContext());
        lessonManager = new LessonManager();

        continueButton = view.findViewById(R.id.btn_continue);
        courseTitle = view.findViewById(R.id.courseTitle);
        lessonTitle1 = view.findViewById(R.id.lessonTitle);
        lessonNumber = view.findViewById(R.id.lessonNumber);
        btnNoti = view.findViewById(R.id.img_notification);
        totalPoints = view.findViewById(R.id.totalPoints);
        readingPoints = view.findViewById(R.id.readingpoint);
        listeningPoints = view.findViewById(R.id.listeningpoint);
        speakingPoints = view.findViewById(R.id.speakingpoint);
        writingpoint = view.findViewById(R.id.writingpoint);


        continueButton.setOnClickListener(v -> Toast.makeText(getActivity(), "Continue studying clicked!", Toast.LENGTH_SHORT).show());

        btnNoti.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        // Chạy tính điểm sau khi UI đã sẵn sàng
//        view.post(() -> calculateTotalPoints());
    }

    private void calculateTotalPoints() {
        if (totalPoints == null) {
            Log.e("HomeFragment", "❌ Không thể cập nhật điểm: `totalPoints` là null!");
            return;
        }

        lessonManager.fetchAllLessonIds(new ApiCallback<List<Integer>>() { // Lấy danh sách ID bài học
            @Override
            public void onSuccess() {}
            @Override
            public void onSuccess(List<Integer> lessonIds) {
                Log.d("LessonManager", "📌 Tổng số bài học: " + lessonIds.size());

                AtomicInteger total = new AtomicInteger(0);
                AtomicInteger reading = new AtomicInteger(0);
                AtomicInteger listening = new AtomicInteger(0);
                AtomicInteger speaking = new AtomicInteger(0);
                AtomicInteger writing = new AtomicInteger(0);

                AtomicInteger completedRequests = new AtomicInteger(0);
                int totalLessons = lessonIds.size();

                for (int lessonId : lessonIds) {
                    lessonManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() { // Lấy chi tiết từng bài học
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onSuccess(Lesson lesson) {
                            String skillType = lesson.getSkillType(); // Lấy skill từ lesson

                            resultManager.fetchResultByLesson(lessonId, new ApiCallback<Result>() { // Lấy điểm
                                @Override
                                public void onSuccess() {}

                                @Override
                                public void onSuccess(Result result) {
                                    int points = result.getTotalPoints();
                                    total.addAndGet(points);

                                    // Cộng điểm vào skill tương ứng
                                    switch (skillType) {
                                        case "READING": reading.addAndGet(points); break;
                                        case "LISTENING": listening.addAndGet(points); break;
                                        case "SPEAKING": speaking.addAndGet(points); break;
                                        case "WRITING": writing.addAndGet(points); break;
                                    }

                                    // Tăng completedRequests và kiểm tra nếu đủ request thì cập nhật UI
                                    if (completedRequests.incrementAndGet() == totalLessons) {
                                        updateUI(total.get(), reading.get(), listening.get(), speaking.get(), writing.get());
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Log.e("ResultManager", "⚠️ Lỗi lấy điểm từ lesson " + lessonId + ": " + errorMessage);
                                    if (completedRequests.incrementAndGet() == totalLessons) {
                                        updateUI(total.get(), reading.get(), listening.get(), speaking.get(), writing.get());
                                    }
                                }

                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("LessonManager", "⚠️ Không thể lấy chi tiết bài học " + lessonId + ": " + errorMessage);
                            completedRequests.incrementAndGet();
                            Log.d("Home", String.valueOf(completedRequests));
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("LessonManager", "❌ Lỗi khi lấy danh sách lessonId: " + errorMessage);
            }
        });
    }
    private void updateUI(int total, int reading, int listening, int speaking, int writing) {
        requireActivity().runOnUiThread(() -> {
            totalPoints.setText(total + "đ");
            readingPoints.setText(reading + "đ");
            listeningPoints.setText(listening + "đ");
            speakingPoints.setText(speaking + "đ");
            writingpoint.setText(writing + "đ");
        });
    }

}
