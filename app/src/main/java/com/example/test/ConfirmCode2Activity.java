package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmCode2Activity extends AppCompatActivity {

    private EditText[] codeInputs; // Mảng chứa các ô nhập mã
    private int currentInputIndex = 0; // Vị trí hiện tại của con trỏ nhập liệu
    private ImageView icback;
    private Button btnRe;
    private TextView tvCountdown; // TextView hiển thị thời gian đếm ngược
    private static final long COUNTDOWN_TIME = 60000; // 60 giây
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_code2);

        // Áp dụng padding cho giao diện
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các ô nhập mã
        codeInputs = new EditText[]{
                findViewById(R.id.editText1),
                findViewById(R.id.editText2),
                findViewById(R.id.editText3),
                findViewById(R.id.editText4),
                findViewById(R.id.editText5),
                findViewById(R.id.editText6)
        };

        icback = findViewById(R.id.icback);
        btnRe = findViewById(R.id.btnRe);
        tvCountdown = findViewById(R.id.tv_countdown); // Ánh xạ TextView đếm ngược

        // Thiết lập sự kiện cho các nút trên bàn phím
        setupKeyboardListeners();

        // Nút quay lại
        icback.setOnClickListener(view -> {
            Intent intent = new Intent(ConfirmCode2Activity.this, ForgotPassWordActivity.class);
            startActivity(intent);
        });

        // Bắt đầu đếm ngược thời gian
        startCountdown();
        btnRe.setOnClickListener(view -> {
            resetCountdown();  // Gọi phương thức reset lại bộ đếm
        });
    }

    private void resetCountdown() {
        // Hủy bộ đếm hiện tại (nếu có)
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // Bắt đầu lại bộ đếm từ đầu
        startCountdown();
    }


    private void setupKeyboardListeners() {
        // Danh sách các nút số từ 0 đến 9
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9
        };

        // Gắn sự kiện cho từng nút
        for (int i = 0; i < buttonIds.length; i++) {
            int finalI = i;
            findViewById(buttonIds[i]).setOnClickListener(v -> {
                if (currentInputIndex < codeInputs.length) {
                    codeInputs[currentInputIndex].setText(String.valueOf(finalI)); // Hiển thị số vào ô nhập
                    currentInputIndex++; // Chuyển sang ô tiếp theo
                }
            });
        }

        // Nút xóa
        findViewById(R.id.btnDel).setOnClickListener(v -> {
            if (currentInputIndex > 0) {
                currentInputIndex--; // Quay lại ô trước
                codeInputs[currentInputIndex].setText(""); // Xóa nội dung
            }
        });

        // Lắng nghe sự kiện nhập mã vào ô cuối cùng
        codeInputs[5].addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() == 1) { // Khi người dùng nhập vào ô cuối cùng
                    String code = getCode(); // Lấy mã đã nhập
                    // Gọi API xác nhận mã OTP
                    ApiManager apiManager = new ApiManager();
                    apiManager.sendConfirmCodeRequest(code, new ApiCallback() {
                        @Override
                        public void onSuccess() {
                            // Chuyển đến Activity tiếp theo nếu mã đúng
                            Intent intent = new Intent(ConfirmCode2Activity.this, SetUpAccountActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // Hiển thị thông báo lỗi nếu mã sai
                            Toast.makeText(ConfirmCode2Activity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }


    // Phương thức để lấy mã đã nhập
    private String getCode() {
        StringBuilder code = new StringBuilder();
        for (EditText input : codeInputs) {
            code.append(input.getText().toString());
        }
        return code.toString();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) { // Cập nhật mỗi giây
            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật thời gian còn lại
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                tvCountdown.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // Khi đếm ngược kết thúc
                tvCountdown.setText("00:00");
                onCountdownFinished();
            }
        }.start();
    }

    private void onCountdownFinished() {
        // Hành động khi đếm ngược kết thúc
        Toast.makeText(ConfirmCode2Activity.this, "Vui lòng nhấn gửi lại mã!", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo hoặc xử lý logic khác
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy bộ đếm khi Activity bị hủy
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
