package com.example.test.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.test.R;

import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.ResultManager;
import com.example.test.api.LearningProgressManager;
import com.example.test.api.UserManager;
import com.example.test.model.Enrollment;
import com.example.test.model.Lesson;
import com.example.test.model.Result;
import com.example.test.ui.NotificationActivity;
import com.example.test.ui.profile.ProfileFragment;
import com.example.test.ui.study.MyCourseFragment;
import com.example.test.ui.study.StudyFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {
    private Button continueButton;
    private Spinner courseSpinner;
    private TextView courseTitle, courseNumber;
    private TextView totalPoints, readingPoints, listeningPoints, speakingPoints, writingpoint;
    private ImageView btnNoti, btnProfile;
    private UserManager userManager;
    private LearningProgressManager learningProgressManager;
    private List<CourseInfo> activeCourses;
    private ArrayAdapter<CourseInfo> spinnerAdapter;
    private int selectedCourseId = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeManagers();
        setupViews();
        loadData();
    }

    private void initializeViews(View view) {
        btnProfile = view.findViewById(R.id.imgAvatar);
        continueButton = view.findViewById(R.id.btn_continue);
        courseTitle = view.findViewById(R.id.courseTitle);

        btnNoti = view.findViewById(R.id.img_notification);
        totalPoints = view.findViewById(R.id.totalPoints);
        readingPoints = view.findViewById(R.id.readingpoint);
        listeningPoints = view.findViewById(R.id.listeningpoint);
        speakingPoints = view.findViewById(R.id.speakingpoint);
        writingpoint = view.findViewById(R.id.writingpoint);
        courseSpinner = view.findViewById(R.id.courseSpinner);
    }

    private void initializeManagers() {
        learningProgressManager = new LearningProgressManager(requireContext());
        userManager = new UserManager(requireContext());
        activeCourses = new ArrayList<>();
    }

    private void setupViews() {
        setupSpinner();
        setupClickListeners();
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<CourseInfo>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                activeCourses
        )
        {
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            TextView textView = (TextView) view;
            textView.setTextColor(Color.BLACK);
            textView.setPadding(16, 16, 16, 16);
            return view;
        }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(spinnerAdapter);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < activeCourses.size()) {
                    CourseInfo selectedCourse = activeCourses.get(position);
                    if (selectedCourse != null) {
                        selectedCourseId = selectedCourse.getCourseId(); // Lưu ID
                        updateSelectedCourse(selectedCourse);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupClickListeners() {
        continueButton.setOnClickListener(v -> {
            if (!isAdded() || getActivity() == null) {
                return;
            }

            if (selectedCourseId != -1) {
                Log.d("HomeFragment", "Continue button clicked for course ID: " + selectedCourseId);

                // Switch to Study tab
                ViewPager2 viewPager = requireActivity().findViewById(R.id.vpg_main);
                viewPager.setCurrentItem(1, true);

                // Pass course ID to StudyFragment
                StudyFragment studyFragment = (StudyFragment) requireActivity()
                        .getSupportFragmentManager()
                        .findFragmentByTag("f1");

                if (studyFragment != null) {
                    Log.d("HomeFragment", "Found StudyFragment, selecting course");
                    studyFragment.selectCourse(selectedCourseId);
                } else {
                    Log.e("HomeFragment", "StudyFragment not found");
                }
            }
        });
    }


    private void navigateToNotifications() {
        startActivity(new Intent(getActivity(), NotificationActivity.class));
    }

    private void loadData() {
        loadEnrollments();
        loadLearningResults();
        loadUserProfile();
    }

    private void loadEnrollments() {
        learningProgressManager.fetchLatestEnrollment(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject enrollment) {
                try {
                    JsonArray content = enrollment.getAsJsonObject("data")
                            .getAsJsonArray("content");
                    processEnrollments(content);
                } catch (Exception e) {
                    handleError("Error parsing enrollment", e);
                }
            }

            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(String error) {
                handleError("Failed to load enrollments", error);
            }
        });
    }

    private void processEnrollments(JsonArray enrollments) {
        activeCourses.clear();
        boolean foundActiveCourse = false;

        for (int i = 0; i < enrollments.size(); i++) {
            JsonObject course = enrollments.get(i).getAsJsonObject();
            if (isActiveCourse(course)) {
                foundActiveCourse = true;
                fetchAndAddCourseToSpinner(course.get("courseId").getAsInt());
            }
        }

        if (!foundActiveCourse) {
            requireActivity().runOnUiThread(this::showNoCourseMessage);
        }
    }

    private boolean isActiveCourse(JsonObject course) {
        return course.get("proStatus").getAsBoolean()
                && course.get("comLevel").getAsDouble() == 0
                && course.get("totalPoints").getAsDouble() == 0;
    }

    private void fetchAndAddCourseToSpinner(int courseId) {
        learningProgressManager.fetchCourseDetails(courseId, new ApiCallback<String>() {
            @Override
            public void onSuccess(String courseName) {
                if (!isAdded() || getActivity() == null) return;

                CourseInfo courseInfo = new CourseInfo(courseId, courseName);
                requireActivity().runOnUiThread(() -> {
                    activeCourses.add(courseInfo);
                    spinnerAdapter.notifyDataSetChanged();

                    if (activeCourses.size() == 1 && courseSpinner != null) {
                        courseSpinner.setSelection(0);
                        updateSelectedCourse(courseInfo);
                    }
                });
            }

            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(String error) {
                handleError("Failed to fetch course details", error);
            }
        });
    }
    private void updateSelectedCourse(CourseInfo course) {
        if (!isAdded() || getActivity() == null) return;

        requireActivity().runOnUiThread(() -> {

            if (courseTitle != null) {
                courseTitle.setText(course.getCourseName());
            }
        });
    }
    private void loadLearningResults() {
        learningProgressManager.fetchLearningResults(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                if (!isAdded()) return;
                updateScores(result);
            }

            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(String error) {
                handleError("Failed to load learning results", error);
            }
        });
    }

    private void updateScores(JsonObject result) {
        try {
            double listeningScore = result.get("listeningScore").getAsDouble();
            double speakingScore = result.get("speakingScore").getAsDouble();
            double readingScore = result.get("readingScore").getAsDouble();
            double writingScore = result.get("writingScore").getAsDouble();
            double overallScore = result.get("overallScore").getAsDouble();

            requireActivity().runOnUiThread(() -> {
                totalPoints.setText(String.format("%.1f", overallScore));
                listeningPoints.setText(String.format("%.1f", listeningScore));
                speakingPoints.setText(String.format("%.1f", speakingScore));
                readingPoints.setText(String.format("%.1f", readingScore));
                writingpoint.setText(String.format("%.1f", writingScore));
            });
        } catch (Exception e) {
            handleError("Error parsing scores", e);
        }
    }

    private void handleError(String message, String error) {
        Log.e("HomeFragment", message + ": " + error);
        if (isAdded()) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void handleError(String message, Exception e) {
        handleError(message, e.getMessage());
    }

    private void showNoCourseMessage() {
        courseNumber.setText("Thông báo");
        courseTitle.setText("Bạn chưa tham gia khóa học nào");
        continueButton.setVisibility(View.GONE);
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
        loadUserProfile();
    }
}
// Helper class để lưu thông tin khóa học
class CourseInfo {
    private int courseId;
    private String courseName;

    public CourseInfo(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public int getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }

    @Override
    public String toString() {
        return courseName;
    }
}
