package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Intro2Activity extends AppCompatActivity {

    ImageView logoImage;
    Button btnNext, btnSkip;
    TextView txtN1,txtN;
    int clickCount =0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AnhXa();
        String[] text1 = {"Get ready!!", "Encybara!"};
        String[] text2 = {"Let's learn English with Capy >v<", "The English learning app from the future!"};

        int[] currentIndex1 = {0};
        int[] currentIndex2 = {0};


        // Button để chuyển tiếp nội dung
        btnNext.setOnClickListener(v -> {
            clickCount++;
            // 1. Bắt đầu hiệu ứng trượt ra
            txtN1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
            txtN.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));

            // 2. Sau khi trượt ra, thay đổi nội dung và trượt vào
            txtN1.postDelayed(() -> {
                currentIndex1[0] = (currentIndex1[0] + 1) % text1.length; // Tăng chỉ số vòng lặp
                txtN1.setText(text1[currentIndex1[0]]); // Đặt nội dung mới
                txtN1.setText(Html.fromHtml(text1[currentIndex1[0]], Html.FROM_HTML_MODE_COMPACT));
                txtN1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
            }, 300);
            txtN.postDelayed(() -> {
                currentIndex2[0] = (currentIndex2[0] + 1) % text2.length; // Tăng chỉ số vòng lặp
                txtN.setText(text2[currentIndex2[0]]); // Đặt nội dung mới
                txtN.setText(Html.fromHtml(text2[currentIndex2[0]], Html.FROM_HTML_MODE_COMPACT));
                txtN.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
            }, 300); // Đợi 300ms trước khi cập nhật nội dung

            if(clickCount ==3){
                Intent intent = new Intent(Intro2Activity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }

        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intro2Activity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
    private void AnhXa() {
        logoImage = (ImageView) findViewById(R.id.logoimageView);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        txtN = (TextView) findViewById(R.id.txtNguyen);
        txtN1 = (TextView) findViewById(R.id.txtNguyen1);
    }
}