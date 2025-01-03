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

public class Sign_up extends AppCompatActivity {

    EditText edtName, edtPhone, edtEmail, edtMKhau;
    CheckBox cbCheck;
    Button btnUp, btnIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        AnhXa();

        // Lắng nghe trạng thái checkbox
        cbCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Bật hoặc tắt nút Sign Up dựa trên trạng thái checkbox
            btnUp.setEnabled(isChecked);
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hoten = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String soDT = edtPhone.getText().toString();
                String pass = edtMKhau.getText().toString();

                if (!isValidEmail(email) || !isValidPhoneNumber(soDT)) {
                    Toast.makeText(Sign_up.this, "Email hoặc số điện thoại không đúng định dạng", Toast.LENGTH_SHORT).show();
                } else if (!isValidPassword(pass)) {
                    Toast.makeText(Sign_up.this, "Mật khẩu ít nhất 8 ký tự gồm chữ hoa, chữ thường, số và ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(Sign_up.this, ConfirmCode2.class);
                    startActivity(intent);
                }
            }
        });

        btnIn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sign_up.this, Sign_In.class);
                startActivity(intent);
            }
        }));

    }

    private void AnhXa() {
        edtEmail = (EditText) findViewById(R.id.edtPass);
        edtName = (EditText) findViewById(R.id.edtTen);
        edtPhone = (EditText) findViewById(R.id.edtSdt);
        edtMKhau = (EditText) findViewById(R.id.edtMKhau);
        cbCheck = findViewById(R.id.cbCheck);
        btnUp = findViewById(R.id.btnUp);
        btnIn = findViewById(R.id.btnIn);
        // Vô hiệu hóa button ban đầu
        btnUp.setEnabled(false);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phonePattern = "^[0-9]{10,11}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!_&*]).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}