package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.test.NetworkChangeReceiver;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.AuthenticationManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtMKhau1;
    CheckBox cbCheck;
    Button btnUp, btnIn;
    NetworkChangeReceiver networkReceiver;
    AuthenticationManager apiManager;
    private long lastClickTime = 0;
    //private boolean isPasswordVisible = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        AnhXa();
        //setupPasswordField();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtName.addTextChangedListener(textWatcher);
        edtEmail.addTextChangedListener(textWatcher);
        edtMKhau1.addTextChangedListener(textWatcher);

        cbCheck.setOnCheckedChangeListener((buttonView, isChecked) -> checkInputFields());


        // Tạo đối tượng NetworkChangeReceiver
        networkReceiver = new NetworkChangeReceiver();
        apiManager = new AuthenticationManager(this);

        btnUp.setOnClickListener(view -> {
            // Chặn multi-click
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < 5000) { // Chặn bấm liên tục trong 2 giây
                return;
            }
            lastClickTime = currentTime;

            if (!apiManager.isInternetAvailable(SignUpActivity.this)) {
                Toast.makeText(SignUpActivity.this, "Vui lòng kiểm tra kết nối Internet của bạn.", Toast.LENGTH_LONG).show();
            } else {
                // Thực hiện yêu cầu nếu có Internet
                String hoten = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String pass = edtMKhau1.getText().toString();
                if (!isValidEmail(email)) {
                    Toast.makeText(SignUpActivity.this, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidPassword(pass)) {
                    Toast.makeText(SignUpActivity.this, "Mật khẩu không đúng định dạng. Vui lòng thử lại", Toast.LENGTH_LONG).show();
                    return;
                }
                // Tạm thời vô hiệu hóa nút để tránh spam
                btnUp.setEnabled(false);
                btnUp.setAlpha(0.5f);

                apiManager.sendSignUpRequest(this,hoten, email, pass, new ApiCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onSuccess(Question question) {}

                    @Override
                    public void onSuccess(Result result) {}

                    @Override
                    public void onSuccess(Answer answer) {}

                    @Override
                    public void onSuccess(MediaFile mediaFile) {

                    }

                    @Override
                    public void onSuccess(Lesson lesson) {}
                    @Override
                    public void onSuccess(Course course) {}

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccessWithOtpID(String otpID) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công! Vui lòng kiểm tra email của bạn.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        saveOtpId(otpID); // Lưu otpID vào SharedPreferences
                        Log.d("ConfirmCode", "otpID được lưu: " + otpID);

                        Intent intent = new Intent(SignUpActivity.this, ConfirmCode2Activity.class);
                        intent.putExtra("source", "register");
                        startActivity(intent);

                    }

                    @Override
                    public void onSuccessWithToken(String token) {

                    }
                });
                new Handler().postDelayed(() -> {
                    btnUp.setEnabled(true);
                    btnUp.setAlpha(1.0f);
                }, 5000);
            }
        });


        btnIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }
    private void saveOtpId(String otpID) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("otpID", otpID);
        editor.apply();
        Log.d("ConfirmCode", "Otp ID đã được lưu: " + otpID);
    }
    @SuppressLint("ClickableViewAccessibility")
//    private void setupPasswordField() {
//        edtMKhau.setOnTouchListener((v, event) -> {
//        edtMKhau.setOnTouchListener((v, event) -> {
//            // Kiểm tra xem người dùng có nhấn vào drawableEnd không
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (event.getRawX() >= (edtMKhau.getRight() - edtMKhau.getCompoundDrawables()[2].getBounds().width())) {
//                    // Thay đổi trạng thái hiển thị mật khẩu
//                    if (isPasswordVisible) {
//                        // Ẩn mật khẩu
//                        edtMKhau.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                        edtMKhau.setCompoundDrawablesWithIntrinsicBounds(
//                                R.drawable.icon_pass, 0, R.drawable.icon_visibility_off, 0);
//                    } else {
//                        // Hiện mật khẩu
//                        edtMKhau.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                        edtMKhau.setCompoundDrawablesWithIntrinsicBounds(
//                                R.drawable.icon_pass, 0, R.drawable.icon_visibility, 0);
//                    }
//                    isPasswordVisible = !isPasswordVisible;
//
//                    // Đặt con trỏ ở cuối văn bản
//                    edtMKhau.setSelection(edtMKhau.getText().length());
//                    return true;
//                }
//            }
//            return false;
//        });
//
//        // Theo dõi thay đổi văn bản
//        edtMKhau.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // Không cần xử lý
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // Kiểm tra nếu mật khẩu đang hiển thị và người dùng nhập thêm ký tự
//                if (isPasswordVisible) {
//                    // Tự động chuyển về chế độ ẩn mật khẩu
//                    edtMKhau.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    edtMKhau.setCompoundDrawablesWithIntrinsicBounds(
//                            R.drawable.icon_pass, 0, R.drawable.icon_visibility_off, 0);
//                    isPasswordVisible = false;
//
//                    // Đặt con trỏ ở cuối văn bản
//                    edtMKhau.setSelection(edtMKhau.getText().length());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // Không cần xử lý
//            }
//        });
//    }
    @Override
    protected void onStart() {
        super.onStart();
        // Đăng ký BroadcastReceiver
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Hủy đăng ký BroadcastReceiver
        unregisterReceiver(networkReceiver);
    }

    private void checkInputFields() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtMKhau1.getText().toString().trim();
        boolean isChecked = cbCheck.isChecked();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && isChecked) {
            btnUp.setEnabled(true);
            btnUp.setAlpha(1.0f); // Làm sáng nút
        } else {
            btnUp.setEnabled(false);
            btnUp.setAlpha(0.5f); // Làm mờ nút
        }
    }
    private void AnhXa() {
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtTen);
        edtMKhau1= findViewById(R.id.edtMKhau);
        cbCheck = findViewById(R.id.cbCheck);
        btnUp = findViewById(R.id.btnUp);
        btnIn = findViewById(R.id.btnIn);
        // Vô hiệu hóa button ban đầu
        btnUp.setEnabled(false);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

//    private boolean isValidPhoneNumber(String phoneNumber) {
//        String phonePattern = "^[0-9]{10,11}$";
//        Pattern pattern = Pattern.compile(phonePattern);
//        Matcher matcher = pattern.matcher(phoneNumber);
//        return matcher.matches();
//    }

    private boolean isValidPassword(String password) {
//        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!_&*]).{8,}$";
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
