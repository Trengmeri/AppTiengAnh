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

                if (!isAdded()) { // Kiểm tra tránh lỗi "Fragment has not been attached yet"
                    return;
                }

                FragmentManager fragmentManager = getChildFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag("f" + position);

                if (fragment instanceof MyCourseFragment && position == 0) {
                    ((MyCourseFragment) fragment).onResume(); // Gọi phương thức cập nhật
                } else if (fragment instanceof AllCourseFragment && position == 1) {
                    ((AllCourseFragment) fragment).onResume(); // Gọi phương thức cập nhật
                }
            }
        });


    }



}
