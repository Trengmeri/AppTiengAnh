package com.example.test.ui.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.test.ui.home.HomeFragment;
import com.example.test.ui.explore.ExploreFragment;

public class MainAdapter extends FragmentStateAdapter {
    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else {
            return new ExploreFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Có 2 Fragment
    }
}

