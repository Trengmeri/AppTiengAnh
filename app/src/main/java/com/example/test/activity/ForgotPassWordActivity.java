package com.example.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

public class ForgotPassWordActivity extends AppCompatActivity {

    EditText edtEmail;
    Button btnContinue;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass_word);
        setUpView();

        btnContinue.setOnClickListener(view -> {
            String email = edtEmail.getText().toString();

            if (!isValidEmail(email)) {
                Toast.makeText(ForgotPassWordActivity.this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(ForgotPassWordActivity.this, ConfirmCodeActivity.class);
                startActivity(intent);
            }
        });

        imgBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassWordActivity.this, SignInActivity.class);
            startActivity(intent);
        });
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
