package com.example.test.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;

public class HomeFragment extends Fragment {
//    Button continueButton;
//    LinearLayout lessonsContainer; // LinearLayout để chứa các bài học
//    TextView courseTitle,lessonTitle1,lessonNumber; // TextView để hiển thị tên khóa học
//    ImageView btnNoti,btnstudy,btnexplore,btnprofile, icHome, icExplore;
//    ViewPager2 vpgMain;
//    GridLayout bottomBar;
//    QuestionManager quesManager = new QuestionManager(this);
//    LessonManager lesManager = new LessonManager();
//    ResultManager resultManager = new ResultManager(this);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
