package com.example.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

public class LoadingTestActivity extends AppCompatActivity {

    TextView ldText;
    private int dotCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        startLoadingAnimation();
        ldText= findViewById(R.id.loadingText);
        new Handler().postDelayed(() -> {
            // Intent để chuyển màn hình
            Intent intent = new Intent(LoadingTestActivity.this, GrammarPick1QuestionActivity.class);
            startActivity(intent);
            finish(); // Kết thúc màn hình hiện tại
        }, 3000);

    }
    private void startLoadingAnimation() {
        final Handler handler = new Handler();
        Runnable updateDots = new Runnable() {
            @Override
            public void run() {
                // Chỉ thay đổi dấu chấm
                String dots = "";
                for (int i = 0; i < dotCount; i++) {
                    dots += ".";
                }
                ldText.setText("Loading" + dots); // "Loading" luôn đứng yên

                dotCount = (dotCount + 1) % 6;  // Tối đa 5 dấu chấm

                handler.postDelayed(this, 500);  // Cập nhật mỗi 500ms
            }
        };

        handler.post(updateDots);  // Bắt đầu animation
    }
}