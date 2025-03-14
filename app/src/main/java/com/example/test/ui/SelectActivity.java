package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.ui.home.HomeActivity;

public class SelectActivity extends AppCompatActivity {
    Button btnTest, btnNew;
    TextView btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnNew = findViewById(R.id.btnNextProgram);
        btnTest = findViewById(R.id.btnTest);
        btnBack= findViewById(R.id.btnBack);
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Log.d("DEBUG", "SelectActivity is opened. Updating lastActivity...");
//        // Lưu lại `lastActivity` khi mở SelectActivity
//        String currentActivity = SelectActivity.class.getName();
//        editor.putString("lastActivity", currentActivity);
//        editor.apply();
//
//        Log.d("DEBUG", "SelectActivity opened, lastActivity saved as: " + currentActivity);
//        editor.putString("lastActivity", SelectActivity.class.getName()); // Ghi đè giá trị cũ
//        editor.apply();

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, LoadingTestActivity.class);
                startActivity(intent);
                // Lưu trạng thái đã chọn
//                editor.putBoolean("hasSelectedOption", true);
//                editor.putString("lastActivity", HomeActivity.class.getName()); // Chuyển đến HomeActivity
//                editor.apply();

                finish(); // Đóng SelectActivity

            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, HomeActivity.class);
                startActivity(intent);
                // Lưu trạng thái đã chọn
//                editor.putBoolean("hasSelectedOption", true);
//                editor.putString("lastActivity", HomeActivity.class.getName()); // Chuyển đến HomeActivity
//                editor.apply();

                finish(); // Đóng SelectActivity
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, ChooseFieldsActivity.class);
                startActivity(intent);
            }
        });
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // Nếu chưa chọn gì, đánh dấu đang ở SelectActivity
//        boolean hasSelectedOption = sharedPreferences.getBoolean("hasSelectedOption", false);
//        if (!hasSelectedOption) {
//            editor.putString("lastActivity", this.getClass().getName());
//            editor.apply();
//        }
//    }
//@Override
//protected void onStop() {
//    super.onStop();
//    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
//    SharedPreferences.Editor editor = sharedPreferences.edit();
//
//    boolean hasSelectedOption = sharedPreferences.getBoolean("hasSelectedOption", false);
//
//    if (!hasSelectedOption) {
//        Log.d("DEBUG", "Saving lastActivity as SelectActivity");  // Kiểm tra log
//        editor.putString("lastActivity", SelectActivity.class.getName());
//        editor.apply();
//    }
//}


}