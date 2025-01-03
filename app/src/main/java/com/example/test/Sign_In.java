package com.example.test;

import android.content.Intent;
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

public class Sign_In extends AppCompatActivity {

    EditText edtName, edtPhone, edtEmail, edtMKhau;
    CheckBox cbCheck;
    Button btnIn, btnForgot, btnUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        AnhXa();

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hoten = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String soDT = edtPhone.getText().toString();
                String pass = edtMKhau.getText().toString();

                if (!isValidEmail(email)) {
                    Toast.makeText(Sign_In.this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                }
                if (!isValidPassword(pass)) {
                    Toast.makeText(Sign_In.this, "Mật khẩu ít nhất 8 ký tự gồm chữ hoa, chữ thường, số và ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                }
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

    private void AnhXa() {
        edtEmail = (EditText) findViewById(R.id.edtPass);
        edtName = (EditText) findViewById(R.id.edtTen);
        edtPhone = (EditText) findViewById(R.id.edtSdt);
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