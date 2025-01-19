package com.example.test.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.test.NetworkChangeReceiver;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiManager;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;

public class ConfirmCode2Activity extends AppCompatActivity {

    private EditText[] codeInputs; // Mảng chứa các ô nhập mã
    private int currentInputIndex = 0; // Vị trí hiện tại của con trỏ nhập liệu
    private ImageView icback;
    private Button btnRe;
    private TextView tvCountdown; // TextView hiển thị thời gian đếm ngược
    private static final long COUNTDOWN_TIME = 60000; // 60 giây
    private CountDownTimer countDownTimer;
    private String otpID;
    NetworkChangeReceiver networkReceiver;
    ApiManager apiManager;
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

        icback = findViewById(R.id.iconback);
        btnRe = findViewById(R.id.btnRe);
        tvCountdown = findViewById(R.id.tv_countdown); // Ánh xạ TextView đếm ngược

        // Thiết lập sự kiện cho các nút trên bàn phím
        setupKeyboardListeners();

        // Nút quay lại
        icback.setOnClickListener(view -> {
            Intent intent = new Intent(ConfirmCode2Activity.this, SignUpActivity.class);
            startActivity(intent);
        });
        networkReceiver = new NetworkChangeReceiver();
        apiManager = new ApiManager();
        // Bắt đầu đếm ngược thời gian
        startCountdown();
        btnRe.setOnClickListener(view -> {
            btnRe.setEnabled(false); // Ngăn người dùng nhấn liên tục
            btnRe.setAlpha(0.5f);
            resetCountdown();// Gọi phương thức reset lại bộ đếm

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
                if (charSequence.length() == 1) {
                    String otpID = getOtpIdFromPreferences();// Khi người dùng nhập vào ô cuối cùng
                    String code = getCode(); // Lấy mã đã nhập
                    // Gọi API xác nhận mã OTP
                    ApiManager apiManager = new ApiManager();
//                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//                    String email = sharedPreferences.getString("email", null);
//                    if (email != null) {
//                        Log.d("ConfirmCode", "Email lấy từ SharedPreferences: " + email);
//                    } else {
//                        Log.e("ConfirmCode", "Email không tồn tại trong SharedPreferences");
//                    }
                    apiManager.sendConfirmCodeRequest(otpID,code, new ApiCallback() {
                        @Override
                        public void onSuccess() {
                            // Chuyển đến Activity tiếp theo nếu mã đúng
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ConfirmCode2Activity.this, "OTP correct!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            clearOtpId();
                            Intent intent = new Intent(ConfirmCode2Activity.this, SetUpAccountActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onSuccess(Question question) {

                        }

                        @Override
                        public void onSuccess(Lesson lesson) {}

                        @Override
                        public void onSuccess(Course course) {}


                        @Override
                        public void onFailure(String errorMessage) {
                            // Hiển thị thông báo lỗi nếu mã sai
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ConfirmCode2Activity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onSuccessWithOtpID(String otpID) {

                        }

                    });
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }
    // Lấy otpID từ SharedPreferences
    private String getOtpIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("otpID", null); // Trả về giá trị otpID hoặc null nếu không tồn tại
    }

    // Xóa otpID khỏi SharedPreferences
    private void clearOtpId() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("otpID");
        editor.apply();
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
                btnRe.setEnabled(true);
                btnRe.setAlpha(1.0f);
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
