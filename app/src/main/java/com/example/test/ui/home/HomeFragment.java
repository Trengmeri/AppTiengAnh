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
import com.google.gson.JsonArray;
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
                try {
                    Log.d("HomeFragment", "Raw enrollment response: " + enrollment.toString());
                    JsonObject data = enrollment.getAsJsonObject("data");
                    JsonArray content = data.getAsJsonArray("content");
                    Log.d("HomeFragment", "Content array size: " + content.size());

                    if (content.size() == 0) {
                        getActivity().runOnUiThread(() -> showNoCourseMessage());
                        return;
                    }

                    JsonObject activeEnrollment = null;
                    for (int i = content.size() - 1; i >= 0; i--) {
                        JsonObject current = content.get(i).getAsJsonObject();
                        if (current.get("proStatus").getAsBoolean()) {
                            activeEnrollment = current;
                            break;
                        }
                    }

                    if (activeEnrollment == null) {
                        Log.d("HomeFragment", "No active enrollment found");
                        getActivity().runOnUiThread(() -> showNoCourseMessage());
                        return;
                    }

                    double totalPoints = activeEnrollment.get("totalPoints").getAsDouble();
                    double comLevel = activeEnrollment.get("comLevel").getAsDouble();
                    int courseId = activeEnrollment.get("courseId").getAsInt();

                    final JsonObject finalEnrollment = activeEnrollment;
                    getActivity().runOnUiThread(() -> {
                        if (totalPoints == 0 && comLevel == 0) {
                            fetchAndShowCourseDetails(courseId);
                            if (continueButton != null) {
                                continueButton.setVisibility(View.VISIBLE);
                            }
                        } else {
                            showCompletedMessage();
                        }
                    });

                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing enrollment", e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> showNoCourseMessage());
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "API call failed: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> showNoCourseMessage());
                }
            }
        });

        learningProgressManager.fetchLearningResults(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                if (getActivity() == null) return;

                try {
                    double listeningScore = result.get("listeningScore").getAsDouble();
                    double speakingScore = result.get("speakingScore").getAsDouble();
                    double readingScore = result.get("readingScore").getAsDouble();
                    double writingScore = result.get("writingScore").getAsDouble();
                    double overallScore = result.get("overallScore").getAsDouble();

                    getActivity().runOnUiThread(() -> {
                        // Update UI with scores
                        totalPoints.setText(String.format("%.1f", overallScore));
                        listeningPoints.setText(String.format("%.1f", listeningScore));
                        speakingPoints.setText(String.format("%.1f", speakingScore));
                        readingPoints.setText(String.format("%.1f", readingScore));
                        writingpoint.setText(String.format("%.1f", writingScore));
                    });

                    Log.d("HomeFragment", String.format(
                            "Scores loaded - overall: %.1f, listening: %.1f, speaking: %.1f, reading: %.1f, writing: %.1f",
                            overallScore, listeningScore, speakingScore, readingScore, writingScore
                    ));

                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing learning results", e);
                }
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Failed to load learning results: " + error);
            }
        });
    }

    private void showCompletedMessage() {
        if (courseNumber != null) courseNumber.setText("Thông báo");
        if (courseTitle != null) courseTitle.setText("Bạn đã hoàn thành các khóa học trước");
        if (continueButton != null) continueButton.setVisibility(View.GONE);
    }

    public void showNoCourseMessage() {
        if (courseNumber != null) courseNumber.setText("Thông báo");
        if (courseTitle != null) courseTitle.setText("Bạn chưa tham gia khóa học nào");
        if (continueButton != null) continueButton.setVisibility(View.GONE);
    }



    private void fetchAndShowCourseDetails(int courseId) {
        learningProgressManager.fetchCourseDetails(courseId, new ApiCallback<String>() {
            @Override
            public void onSuccess() {
                // Empty implementation required
            }

            @Override
            public void onSuccess(String courseName) {
                if (getActivity() == null) return; // Ngăn lỗi khi Fragment đã bị tách khỏi Activity
                getActivity().runOnUiThread(() -> {
                    courseNumber.setText("Course " + courseId);
                    courseTitle.setText(courseName);
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Error fetching course: " + error);
            }
        });
    }

    private void loadUserProfile() {
        String userId = SharedPreferencesManager.getInstance(requireContext()).getID();
        if (userId == null) return;

        userManager.fetchUserProfile(Integer.parseInt(userId), new ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                if (getActivity() == null || !isAdded()) return;

                getActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;

                    String avatarUrl = result.optString("avatar");
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        avatarUrl = avatarUrl.replace("0.0.0.0", "14.225.198.3");

                        if (isAdded()) { // Check before using Glide
                            Glide.with(requireActivity())
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.img_avt_profile)
                                    .error(R.drawable.img_avt_profile)
                                    .circleCrop()
                                    .into(btnProfile);
                        }
                    }
                });
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onFailure(String errorMessage) {
                if (getActivity() == null || !isAdded()) return;

                getActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "Failed to load profile: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload profile when returning from EditProfile
        loadUserProfile();
    }
}
