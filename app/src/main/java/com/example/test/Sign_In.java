package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sign_In extends AppCompatActivity {

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
                if (!apiManager.isInternetAvailable(Sign_In.this)) {
                    Toast.makeText(Sign_In.this, "Không có kết nối Internet!", Toast.LENGTH_SHORT).show();
                    return;
                }
                apiManager.sendLoginRequest(email, pass, new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(Sign_In.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Sign_In.this, Home.class);
                        startActivity(intent); // Chuyển hướng đến Home Activity
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(Sign_In.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(Sign_In.this, ForgotPassWord.class);
                startActivity(intent);
            }
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sign_In.this, Sign_up.class);
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