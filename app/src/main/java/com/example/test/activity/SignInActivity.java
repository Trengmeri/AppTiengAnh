package com.example.test.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.NetworkChangeReceiver;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiManager;
import com.example.test.api.ApiResponseAnswer;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.Result;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    EditText edtEmail, edtMKhau;
    CheckBox cbCheck;
    Button btnIn, btnForgot, btnUp;
    NetworkChangeReceiver networkReceiver;
    ApiManager apiManager;
    private boolean isPasswordVisible = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        AnhXa();
        setupPasswordField();

        // Tạo đối tượng NetworkChangeReceiver
        networkReceiver = new NetworkChangeReceiver();
        apiManager = new ApiManager();

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String pass = edtMKhau.getText().toString();
                if (!apiManager.isInternetAvailable(SignInActivity.this)) {
                    Toast.makeText(SignInActivity.this, "Không có kết nối Internet!", Toast.LENGTH_SHORT).show();
                    return;
                }
                apiManager.sendLoginRequest(email, pass, new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(SignInActivity.this, ChooseFieldsActivity.class);
                        startActivity(intent); // Chuyển hướng đến Home Activity
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccessWithOtpID(String otpID) {}

                    @Override
                    public void onSuccess(Result result) {}

                    @Override
                    public void onSuccess(Answer answer) {}

                });
                // if (email.isEmpty() || pass.isEmpty()) {
                // Toast.makeText(Sign_In.this, "Vui lòng điền đầy đủ thông tin!",
                // Toast.LENGTH_LONG).show();
                // } else if (!isValidEmail(email)) {
                // Toast.makeText(Sign_In.this, "Email không đúng định dạng!",
                // Toast.LENGTH_LONG).show();
                // } else if (!isValidPassword(pass)) {
                // Toast.makeText(Sign_In.this, "Mật khẩu ít nhất 8 ký tự gồm chữ hoa, chữ
                // thường, số và ký tự đặc biệt", Toast.LENGTH_LONG).show();
                // }else {
                // sendLoginRequest(email, pass);
                // }
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("SignInActivity","onclicked");
                Intent intent = new Intent(SignInActivity.this, ForgotPassWordActivity.class);
                startActivity(intent);
            }
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordField() {
        edtMKhau.setOnTouchListener((v, event) -> {
            // Kiểm tra xem người dùng có nhấn vào drawableEnd không
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtMKhau.getRight() - edtMKhau.getCompoundDrawables()[2].getBounds().width())) {
                    // Đổi trạng thái hiển thị mật khẩu
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        edtMKhau.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        edtMKhau.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_pass, 0, R.drawable.icon_visibility_off, 0);
                    } else {
                        // Hiện mật khẩu
                        edtMKhau.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        edtMKhau.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_pass, 0, R.drawable.icon_visibility, 0);
                    }
                    isPasswordVisible = !isPasswordVisible;

                    // Đặt con trỏ ở cuối văn bản
                    edtMKhau.setSelection(edtMKhau.getText().length());
                    return true;
                }
            }
            return false;
        });

        edtMKhau.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý trước khi văn bản thay đổi

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi văn bản thay đổi, tự động ẩn mật khẩu nếu đang hiển thị
                if (isPasswordVisible) {
                    // Chuyển về chế độ ẩn mật khẩu
                    edtMKhau.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtMKhau.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.icon_pass, 0, R.drawable.icon_visibility_off, 0);
                    isPasswordVisible = false;

                    // Đặt con trỏ ở cuối văn bản
                    edtMKhau.setSelection(edtMKhau.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý sau khi văn bản thay đổi
            }
        });
    }

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


    private void AnhXa() {
        edtEmail = findViewById(R.id.edtPass);
        edtMKhau = findViewById(R.id.edtMKhau);
        cbCheck = findViewById(R.id.cbCheck);
        btnIn = findViewById(R.id.btnIn);
        btnUp = findViewById(R.id.btnUp);
        btnForgot =(Button) findViewById(R.id.btnForgot);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!_&*]).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}