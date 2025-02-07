package com.example.test.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.NetworkChangeReceiver;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.AuthenticationManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.Result;

public class ForgotPassWordActivity extends AppCompatActivity {

    EditText edtEmail;
    Button btnContinue;
    ImageView imgBack;
    NetworkChangeReceiver networkReceiver;
    AuthenticationManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass_word);
        setUpView();

        networkReceiver = new NetworkChangeReceiver();
        apiManager = new AuthenticationManager();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPassWordActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPassWordActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!apiManager.isInternetAvailable(ForgotPassWordActivity.this)) {
                    Toast.makeText(ForgotPassWordActivity.this, "Không có kết nối Internet!", Toast.LENGTH_SHORT).show();
                    return;
                }
                apiManager.sendForgotPasswordRequest(email, new ApiCallback() {
                    @Override
                    public void onSuccess() {
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
                                Toast.makeText(ForgotPassWordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccessWithOtpID(String otpID) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ForgotPassWordActivity.this, "Vui lòng kiểm tra email của bạn.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        saveOtpId(otpID); // Lưu otpID vào SharedPreferences
                        Intent intent = new Intent(ForgotPassWordActivity.this, ConfirmCodeActivity.class);
//                      intent.putExtra("email", email);
                        startActivity(intent);
                    }

                    @Override
                    public void onSuccessWithToken(String token) {

                    }

                    @Override
                    public void onSuccess(Result result) {}

                    @Override
                    public void onSuccess(Answer answer) {

                    }

                });
            }
        });

        imgBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassWordActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }
    private void saveOtpId(String otpID) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("otpID", otpID);
        editor.apply();
    }
    private void setUpView() {
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnContinue = findViewById(R.id.btn_continue);
        imgBack = (ImageView) findViewById(R.id.imgBack);
    }
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
}
}
