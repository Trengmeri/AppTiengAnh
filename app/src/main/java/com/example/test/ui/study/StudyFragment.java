package com.example.test.ui.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import com.example.test.R;
import com.example.test.model.Course;

import java.util.List;

public class StudyFragment extends Fragment {
    private Button btnAbout, btnLesson;
    private ViewPager2 viewPager;

    public StudyFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAbout = view.findViewById(R.id.btnAbout);
        btnLesson = view.findViewById(R.id.btnLesson);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setSaveEnabled(false);


        StudyPagerAdapter adapter = new StudyPagerAdapter(getChildFragmentManager(), getLifecycle());


        adapter.addFragment(new MyCourseFragment());
        adapter.addFragment(new AllCourseFragment());
        viewPager.setAdapter(adapter);

        // Set sự kiện click cho button
        btnAbout.setOnClickListener(v -> viewPager.setCurrentItem(0)); // Chuyển về MyCourseFragment
        btnLesson.setOnClickListener(v -> viewPager.setCurrentItem(1)); // Chuyển về AllCourseFragment

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                FragmentManager fragmentManager = getChildFragmentManager();

                if (position == 0) { // Khi chuyển đến MyCourseFragment
                    MyCourseFragment myCourseFragment = (MyCourseFragment) fragmentManager.findFragmentByTag("f0");
                    if (myCourseFragment != null) {
                        myCourseFragment.onResume(); // Gọi lại onResume() để làm mới dữ liệu
                    }
                } else if (position == 1) { // Khi chuyển đến AllCourseFragment
                    AllCourseFragment allCourseFragment = (AllCourseFragment) fragmentManager.findFragmentByTag("f1");
                    if (allCourseFragment != null) {
                        allCourseFragment.onResume(); // Gọi lại onResume() để làm mới dữ liệu
                    }
                }
            }
        });

    }



}
