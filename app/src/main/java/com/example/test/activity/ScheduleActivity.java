package com.example.test.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

public class ScheduleActivity extends AppCompatActivity {

    private TextView textViewReminderTimeHour, textViewReminderTimeMins;
    private ImageView up, down; // Single ImageView for Up/Down

    private int currentHour = 0;
    private int currentMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        textViewReminderTimeHour = findViewById(R.id.textViewReminderTimeHour);
        textViewReminderTimeMins = findViewById(R.id.textViewReminderTimeMins);
        up = findViewById(R.id.up);   // Initialize the ImageView
        down = findViewById(R.id.down); // Initialize the ImageView

        updateTime(); // Set initial time display

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Determine whether to increment hour or minute based on focus
                if (textViewReminderTimeHour.isFocused()) {
                    incrementHour();
                } else if (textViewReminderTimeMins.isFocused()) {
                    incrementMinute();
                }
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Determine whether to decrement hour or minute based on focus
                if (textViewReminderTimeHour.isFocused()) {
                    decrementHour();
                } else if (textViewReminderTimeMins.isFocused()) {
                    decrementMinute();
                }
            }
        });


        // Handle focus changes (optional - for better UX)
        textViewReminderTimeHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // You can add visual feedback here (e.g., change background color)
            }
        });

        textViewReminderTimeMins.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // You can add visual feedback here
            }
        });
    }

    private void incrementHour() {
        currentHour++;
        if (currentHour > 23) {
            currentHour = 0;
        }
        updateTime();
    }

    private void decrementHour() {
        currentHour--;
        if (currentHour < 0) {
            currentHour = 23;
        }
        updateTime();
    }

    private void incrementMinute() {
        currentMinute += 15;
        if (currentMinute >= 60) {
            currentMinute = 0;
            incrementHour(); // Increment hour when minutes roll over
        }
        updateTime();
    }

    private void decrementMinute() {
        currentMinute -= 15;
        if (currentMinute < 0) {
            currentMinute = 45;
            decrementHour(); // Decrement hour when minutes roll under
        }
        updateTime();
    }

    private void updateTime() {
        String hourStr = String.format("%02d", currentHour);
        String minuteStr = String.format("%02d", currentMinute);
        textViewReminderTimeHour.setText(hourStr);
        textViewReminderTimeMins.setText(minuteStr);
    }
}