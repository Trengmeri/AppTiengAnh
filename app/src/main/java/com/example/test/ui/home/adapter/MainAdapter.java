package com.example.test.ui.home.adapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.test.ui.home.HomeFragment;
import com.example.test.ui.explore.ExploreFragment;
import com.example.test.ui.profile.ProfileFragment;
import com.example.test.ui.study.StudyFragment;
import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();

    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        // Khởi tạo danh sách các Fragment
        fragmentList.add(new HomeFragment());  // Trang 0
        fragmentList.add(new StudyFragment()); // Trang 1
        fragmentList.add(new ExploreFragment());// Trang 2
        fragmentList.add(new ProfileFragment());// Trang 3
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    // Hàm này giúp lấy Fragment theo vị trí
    public Fragment getFragment(int position) {
        return fragmentList.get(position);
    }
}
