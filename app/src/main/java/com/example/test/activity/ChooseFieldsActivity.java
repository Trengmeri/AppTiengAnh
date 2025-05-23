package com.example.test.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

public class ChooseFieldsActivity extends AppCompatActivity {
    Button btnEco,btnConstruct,btnIT, btnMechan, btnOther;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_fields);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnEco= findViewById(R.id.btnEconomic);
        btnConstruct= findViewById(R.id.btnConstruct);
        btnIT= findViewById(R.id.btnIT);
        btnMechan= findViewById(R.id.btnMechanic);
        btnOther= findViewById(R.id.btnOthers);

        btnIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseFieldsActivity.this, SelectActivity.class);
                startActivity(intent);
            }
        });

    }
}