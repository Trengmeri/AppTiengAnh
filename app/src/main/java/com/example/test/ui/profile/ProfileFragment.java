package com.example.test.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.NetworkChangeReceiver;
import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.AuthenticationManager;
import com.example.test.model.User;
import com.example.test.ui.EditProfileActivity;
import com.example.test.ui.SignInActivity;

public class ProfileFragment extends Fragment {
    TextView userName, userEmail;
    LinearLayout btnLogout, term , language;
    NetworkChangeReceiver networkReceiver;
    AuthenticationManager apiManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);

        User user = SharedPreferencesManager.getInstance(getContext()).getUser();
        if (user != null) {
            userName.setText(user.getName());
            userEmail.setText(user.getEmail());
        }

        btnLogout= view.findViewById(R.id.btnLogout);
        btnedit= view.findViewById(R.id.btnEdit);

        term = view.findViewById(R.id.term);
        language = view.findViewById(R.id.language);

        language.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),LanguageActivity.class);
            startActivity(intent);
        });

        term.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),TermActivity.class);
            startActivity(intent);
        });
        // Tạo đối tượng NetworkChangeReceiver
        networkReceiver = new NetworkChangeReceiver();
        apiManager = new AuthenticationManager(requireContext());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        Log.d("ProfileFragment", "After Logout - Remember Me: " + isRemembered);
        Log.d("ProfileFragment", "After Logout - Saved Email: " + savedEmail);
        Log.d("ProfileFragment", "After Logout - Saved Password: " + savedPassword);

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

    }
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            // Gọi API logout
//            Intent intent= new Intent(getActivity(), SignInActivity.class);
//            startActivity(intent);
            apiManager.sendLogoutRequest(new ApiCallback() {
                @Override
                public void onSuccess() {
                    // Chuyển về màn hình đăng nhập
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();  // Xóa tất cả dữ liệu
                    editor.commit(); // Lưu thay đổi ngay lập tức

                    Log.d("Logout", "Đã xóa SharedPreferences");
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finishAffinity(); // Đóng toàn bộ activity
                }

                @Override
                public void onSuccess(Object result) {

                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}