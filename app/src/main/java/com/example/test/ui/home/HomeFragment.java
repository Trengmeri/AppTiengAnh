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

import com.bumptech.glide.Glide;
import com.example.test.R;

import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.ResultManager;
import com.example.test.api.LearningProgressManager;
import com.example.test.api.UserManager;
import com.example.test.model.Lesson;
import com.example.test.model.Result;
import com.example.test.ui.NotificationActivity;
import com.example.test.ui.profile.ProfileFragment;
import com.example.test.ui.study.StudyFragment;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {
    private Button continueButton;
    private TextView courseTitle,courseNumber;
    private TextView totalPoints, readingPoints, listeningPoints, speakingPoints, writingpoint;
    private ImageView btnNoti, btnStudy, btnExplore, btnProfile;
    private UserManager userManager;
    private LearningProgressManager learningProgressManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo biến sau khi view đã tạo

        learningProgressManager = new LearningProgressManager(requireContext());
        userManager = new UserManager(requireContext());
        // Initialize views...
        btnProfile = view.findViewById(R.id.imgAvatar);
        continueButton = view.findViewById(R.id.btn_continue);
        courseTitle = view.findViewById(R.id.courseTitle);
//        lessonTitle1 = view.findViewById(R.id.lessonTitle);
        courseNumber = view.findViewById(R.id.courseNumber);
        btnNoti = view.findViewById(R.id.img_notification);
        totalPoints = view.findViewById(R.id.totalPoints);
        readingPoints = view.findViewById(R.id.readingpoint);
        listeningPoints = view.findViewById(R.id.listeningpoint);
        speakingPoints = view.findViewById(R.id.speakingpoint);
        writingpoint = view.findViewById(R.id.writingpoint);

        loadData();
        loadUserProfile();
        setupClickListeners();
        btnNoti.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        // Chạy tính điểm sau khi UI đã sẵn sàng

    }
    private void setupClickListeners() {
        Button btnContinue = requireView().findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(v -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.vpg_main);
            // Giả sử StudyFragment ở vị trí 1 trong adapter
            viewPager.setCurrentItem(1, true);
        });
    }
    private void loadData() {
        learningProgressManager.fetchLatestEnrollment(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess() {
                // Empty implementation required
            }

            @Override
            public void onSuccess(JsonObject enrollment) {
                if (getActivity() == null || !isAdded()) return;

                try {
                    int courseId = enrollment.get("courseId").getAsInt();
                    learningProgressManager.fetchCourseDetails(courseId, new ApiCallback<String>() {
                        @Override
                        public void onSuccess() {
                            // Empty implementation required
                        }

                        @Override
                        public void onSuccess(String courseName) {
                            requireActivity().runOnUiThread(() -> {
                                    courseNumber.setText("Course " + courseId);
                                    courseTitle.setText(courseName);
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e("HomeFragment", "Error fetching course: " + error);
                        }
                    });
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing enrollment: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Error fetching enrollment: " + error);
            }
        });

        learningProgressManager.fetchLearningResults(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess() {
                // Empty implementation required
            }

            @Override
            public void onSuccess(JsonObject results) {
                if (getActivity() == null || !isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    try {
                        if (totalPoints != null) totalPoints.setText(results.get("overallScore").getAsString());
                        if (readingPoints != null) readingPoints.setText(results.get("readingScore").getAsString());
                        if (listeningPoints != null) listeningPoints.setText(results.get("listeningScore").getAsString());
                        if (speakingPoints != null) speakingPoints.setText(results.get("speakingScore").getAsString());
                        if (writingpoint != null) writingpoint.setText(results.get("writingScore").getAsString());
                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error updating UI: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Error fetching results: " + error);
            }
        });
    }
//    private void calculateTotalPoints() {
//        if (totalPoints == null) {
//            Log.e("HomeFragment", "❌ Không thể cập nhật điểm: `totalPoints` là null!");
//            return;
//        }
//        lessonManager.fetchAllLessonIds(new ApiCallback<List<Integer>>() { // Lấy danh sách ID bài học
//            @Override
//            public void onSuccess() {}
//            @Override
//            public void onSuccess(List<Integer> lessonIds) {
//                Log.d("LessonManager", "📌 Tổng số bài học: " + lessonIds.size());
//
//                AtomicInteger total = new AtomicInteger(0);
//                AtomicInteger reading = new AtomicInteger(0);
//                AtomicInteger listening = new AtomicInteger(0);
//                AtomicInteger speaking = new AtomicInteger(0);
//                AtomicInteger writing = new AtomicInteger(0);
//
//                AtomicInteger completedRequests = new AtomicInteger(0);
//                int totalLessons = lessonIds.size();
//
//                for (int lessonId : lessonIds) {
//                    lessonManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() { // Lấy chi tiết từng bài học
//                        @Override
//                        public void onSuccess() {}
//                        @Override
//                        public void onSuccess(Lesson lesson) {
//                            String skillType = lesson.getSkillType(); // Lấy skill từ lesson
//
//                            resultManager.fetchResultByLesson(lessonId, new ApiCallback<Result>() { // Lấy điểm
//                                @Override
//                                public void onSuccess() {}
//
//                                @Override
//                                public void onSuccess(Result result) {
//                                    int points = result.getTotalPoints();
//                                    total.addAndGet(points);
//
//                                    // Cộng điểm vào skill tương ứng
//                                    switch (skillType) {
//                                        case "READING": reading.addAndGet(points); break;
//                                        case "LISTENING": listening.addAndGet(points); break;
//                                        case "SPEAKING": speaking.addAndGet(points); break;
//                                        case "WRITING": writing.addAndGet(points); break;
//                                    }
//
//                                    // Tăng completedRequests và kiểm tra nếu đủ request thì cập nhật UI
//                                    if (completedRequests.incrementAndGet() == totalLessons) {
//                                        updateUI(total.get(), reading.get(), listening.get(), speaking.get(), writing.get());
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(String errorMessage) {
//                                    Log.e("ResultManager", "⚠️ Lỗi lấy điểm từ lesson " + lessonId + ": " + errorMessage);
//                                    if (completedRequests.incrementAndGet() == totalLessons) {
//                                        updateUI(total.get(), reading.get(), listening.get(), speaking.get(), writing.get());
//                                    }
//                                }
//
//                            });
//                        }
//
//                        @Override
//                        public void onFailure(String errorMessage) {
//                            Log.e("LessonManager", "⚠️ Không thể lấy chi tiết bài học " + lessonId + ": " + errorMessage);
//                            completedRequests.incrementAndGet();
//                            Log.d("Home", String.valueOf(completedRequests));
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Log.e("LessonManager", "❌ Lỗi khi lấy danh sách lessonId: " + errorMessage);
//            }
//        });
//    }

    private void loadUserProfile() {
        String userId = SharedPreferencesManager.getInstance(requireContext()).getID();
        if (userId == null) return;

        userManager.fetchUserProfile(Integer.parseInt(userId), new ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                requireActivity().runOnUiThread(() -> {
                    String avatarUrl = result.optString("avatar");
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        avatarUrl = avatarUrl.replace("0.0.0.0", "14.225.198.3");

                        Glide.with(HomeFragment.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.img_avt_profile)
                                .error(R.drawable.img_avt_profile)
                                .circleCrop()
                                .into(btnProfile);
                    }
                });
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onFailure(String errorMessage) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(),
                                "Failed to load profile: " + errorMessage,
                                Toast.LENGTH_SHORT).show()
                );
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
