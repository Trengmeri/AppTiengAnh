package com.example.test;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    EditText edtEmail, edtMKhau;
    CheckBox cbCheck;
    Button btnIn, btnForgot, btnUp;
    NetworkChangeReceiver networkReceiver;
    ApiManager apiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        AnhXa();

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignInActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        startActivity(intent); // Chuyển hướng đến Home Activity
                    }


                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
        edtEmail = (EditText) findViewById(R.id.edtPass);
        edtMKhau = (EditText) findViewById(R.id.edtMKhau);
        cbCheck = findViewById(R.id.cbCheck);
        btnIn = findViewById(R.id.btnIn);
        btnUp = findViewById(R.id.btnUp);
        btnForgot = findViewById(R.id.btnForgot);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!_&*]).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}