package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewPassActivity extends AppCompatActivity {

    EditText edtPass, edtRePass;
    Button btnNext;
    ImageView icback;
    private boolean isPasswordVisible = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_pass);

        AnhXa();
        setupPasswordField();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = edtPass.getText().toString();
                String repass = edtRePass.getText().toString();

                if (!isValidPassword(pass)) {
                    Toast.makeText(NewPassActivity.this, "Mật khẩu ít nhất 8 ký tự gồm chữ hoa, chữ thường, số và ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                } else if (!pass.equals(repass)) {
                    Toast.makeText(NewPassActivity.this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(NewPassActivity.this, LoadPassActivity.class);
                    startActivity(intent);
                }
            }
        });

        icback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewPassActivity.this, ForgotPassWordActivity.class);
                startActivity(intent);
            }
        });
    }
//    Ẩn hiện mk
    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordField() {
        edtPass.setOnTouchListener((v, event) -> {
            // Kiểm tra xem người dùng có nhấn vào drawableEnd không
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtPass.getRight() - edtPass.getCompoundDrawables()[2].getBounds().width())) {
                    // Thay đổi trạng thái hiển thị mật khẩu
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        edtPass.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_pass, 0, R.drawable.icon_visibility_off, 0);
                    } else {
                        // Hiện mật khẩu
                        edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        edtPass.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_pass, 0, R.drawable.icon_visibility, 0);
                    }
                    isPasswordVisible = !isPasswordVisible;

                    // Đặt con trỏ ở cuối văn bản
                    edtPass.setSelection(edtPass.getText().length());
                    return true;
                }
            }
            return false;
        });
        edtRePass.setOnTouchListener((v, event) -> {
            // Kiểm tra xem người dùng có nhấn vào drawableEnd không
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtRePass.getRight() - edtRePass.getCompoundDrawables()[2].getBounds().width())) {
                    // Thay đổi trạng thái hiển thị mật khẩu
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        edtRePass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        edtRePass.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_pass, 0, R.drawable.icon_visibility_off, 0);
                    } else {
                        // Hiện mật khẩu
                        edtRePass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        edtRePass.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_pass, 0, R.drawable.icon_visibility, 0);
                    }
                    isPasswordVisible = !isPasswordVisible;

                    // Đặt con trỏ ở cuối văn bản
                    edtRePass.setSelection(edtRePass.getText().length());
                    return true;
                }
            }
            return false;
        });
        // Theo dõi thay đổi văn bản
        edtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra nếu mật khẩu đang hiển thị và người dùng nhập thêm ký tự
                if (isPasswordVisible) {
                    // Tự động chuyển về chế độ ẩn mật khẩu
                    edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtPass.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.icon_pass, 0, R.drawable.icon_visibility_off, 0);
                    isPasswordVisible = false;

                    // Đặt con trỏ ở cuối văn bản
                    edtPass.setSelection(edtPass.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });
        edtRePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra nếu mật khẩu đang hiển thị và người dùng nhập thêm ký tự
                if (isPasswordVisible) {
                    // Tự động chuyển về chế độ ẩn mật khẩu
                    edtRePass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtRePass.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.icon_pass, 0, R.drawable.icon_visibility_off, 0);
                    isPasswordVisible = false;

                    // Đặt con trỏ ở cuối văn bản
                    edtRePass.setSelection(edtRePass.getText().length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });
    }
    private void AnhXa() {
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtRePass = (EditText) findViewById(R.id.edtRePass);
        btnNext = findViewById(R.id.btnNext);
        icback = findViewById(R.id.icback);
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!_&*]).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}