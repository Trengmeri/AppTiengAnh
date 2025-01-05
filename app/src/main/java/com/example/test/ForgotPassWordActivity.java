package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassWordActivity extends AppCompatActivity {

    EditText edtEmail;
    Button btnNext;
    ImageView icback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass_word);
        AnhXa();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();

                if (!isValidEmail(email)) {
                    Toast.makeText(ForgotPassWordActivity.this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(ForgotPassWordActivity.this, ConfirmCodeActivity.class);
                    startActivity(intent);
                }
            }
        });

        icback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassWordActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AnhXa() {
        edtEmail = (EditText) findViewById(R.id.edtPass);
        btnNext = findViewById(R.id.btnNext);
        icback = findViewById(R.id.icback);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}