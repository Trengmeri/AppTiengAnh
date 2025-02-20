package com.example.test.ui.home.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.test.ui.home.HomeFragment;
import com.example.test.ui.explore.ExploreFragment;
import com.example.test.ui.profile.ProfileFragment;

public class MainAdapter extends FragmentStateAdapter {
    ImageView btnstudy,btnexplore,btnprofile,btnHome;
    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else if (position ==1) {
            return new ExploreFragment();
        } else {
            return new ProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Có 3 Fragment
    }
}

