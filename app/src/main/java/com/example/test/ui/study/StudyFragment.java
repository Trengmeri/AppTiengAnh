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
import androidx.fragment.app.FragmentTransaction;
import com.example.test.R;

public class StudyFragment extends Fragment {
    private Button btnAbout, btnLesson;

    public StudyFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        replaceFragment(new MyCourseFragment());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAbout = view.findViewById(R.id.btnAbout);
        btnLesson = view.findViewById(R.id.btnLesson);

        // Load AboutFragment mặc định
        replaceFragment(new MyCourseFragment());

        btnAbout.setOnClickListener(v -> replaceFragment(new MyCourseFragment()));
        btnLesson.setOnClickListener(v -> replaceFragment(new AllCourseFragment()));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();

        // Xóa tất cả fragment con bên trong fragment_container
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Thay thế bằng fragment mới (MyCourseFragment hoặc AllCourseFragment)
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}
