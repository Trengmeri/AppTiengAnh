package com.example.test.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.ScheduleManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Enrollment;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.Result;
import com.example.test.model.Schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private TextView textViewReminderTimeHour, textViewReminderTimeMins, btnBacktoEx;
    private ImageView up, down;
    ImageView Mon, Tue, Wed, Thu, Fri, Sat, Sun, check_icon2, check_icon3, check_icon4, check_icon5, check_icon6, check_icon7, check_icon0;
    ImageView Basic, Advance, LevelUp, check_basic, check_advance, check_levelup;

    private int currentHour = 0;
    private int currentMinute = 0;
    private int selectedGoal = 0; // 0: None, 1: Basic, 2: Advance, 3: LevelUp

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        textViewReminderTimeHour = findViewById(R.id.textViewReminderTimeHour);
        textViewReminderTimeMins = findViewById(R.id.textViewReminderTimeMins);
        btnBacktoEx = findViewById(R.id.btnBacktoEx);

        Sun = findViewById(R.id.sun);
        Mon = findViewById(R.id.mon);
        Tue = findViewById(R.id.tue);
        Wed = findViewById(R.id.wed);
        Thu = findViewById(R.id.thu);
        Fri = findViewById(R.id.fri);
        Sat = findViewById(R.id.sat);
        Basic = findViewById(R.id.basic);
        Advance = findViewById(R.id.advance);
        LevelUp = findViewById(R.id.levelup);

        check_icon2 = findViewById(R.id.check_icon2);
        check_icon3 = findViewById(R.id.check_icon3);
        check_icon4 = findViewById(R.id.check_icon4);
        check_icon5 = findViewById(R.id.check_icon5);
        check_icon6 = findViewById(R.id.check_icon6);
        check_icon7 = findViewById(R.id.check_icon7);
        check_icon0 = findViewById(R.id.check_icon0);
        check_basic = findViewById(R.id.check_icon_basic);
        check_advance = findViewById(R.id.check_icon_advance);
        check_levelup = findViewById(R.id.check_icon_levelup);
        up = findViewById(R.id.up);   // Initialize the ImageView
        down = findViewById(R.id.down); // Initialize the ImageView

        updateTime(); // Set initial time display

        Basic.setOnClickListener(v -> {
            selectedGoal = 1; // Chọn Basic
            updateGoalSelectionUI();
        });

        Advance.setOnClickListener(v -> {
            selectedGoal = 2; // Chọn Advance
            updateGoalSelectionUI();
        });

        LevelUp.setOnClickListener(v -> {
            selectedGoal = 3; // Chọn LevelUp
            updateGoalSelectionUI();
        });

        Mon.setOnClickListener(v -> {
            Mon.setSelected(!Mon.isSelected());

            // Cập nhật UI
            if (Mon.isSelected()) {
                // Hiển thị icon check
                check_icon2.setVisibility(View.VISIBLE);
            } else {
                // Ẩn icon check
                check_icon2.setVisibility(View.GONE);
            }
        });

        Tue.setOnClickListener(v -> {
            Tue.setSelected(!Tue.isSelected());

            // Cập nhật UI
            if (Tue.isSelected()) {
                // Hiển thị icon check
                check_icon3.setVisibility(View.VISIBLE);
            } else {
                // Ẩn icon check
                check_icon3.setVisibility(View.GONE);
            }
        });

        Wed.setOnClickListener(v -> {
            Wed.setSelected(!Wed.isSelected());

            // Cập nhật UI
            if (Wed.isSelected()) {
                // Hiển thị icon check
                check_icon4.setVisibility(View.VISIBLE);
            } else {
                // Ẩn icon check
                check_icon4.setVisibility(View.GONE);
            }
        });

        Thu.setOnClickListener(v -> {
            Thu.setSelected(!Thu.isSelected());

            // Cập nhật UI
            if (Thu.isSelected()) {
                // Hiển thị icon check
                check_icon5.setVisibility(View.VISIBLE);
            } else {
                // Ẩn icon check
                check_icon5.setVisibility(View.GONE);
            }
        });

        Fri.setOnClickListener(v -> {
            Fri.setSelected(!Fri.isSelected());

            // Cập nhật UI
            if (Fri.isSelected()) {
                // Hiển thị icon check
                check_icon6.setVisibility(View.VISIBLE);
            } else {
                // Ẩn icon check
                check_icon6.setVisibility(View.GONE);
            }
        });

        Sat.setOnClickListener(v -> {
            Sat.setSelected(!Sat.isSelected());

            // Cập nhật UI
            if (Sat.isSelected()) {
                // Hiển thị icon check
                check_icon7.setVisibility(View.VISIBLE);
            } else {
                // Ẩn icon check
                check_icon7.setVisibility(View.GONE);
            }
        });

        Sun.setOnClickListener(v -> {
            Sun.setSelected(!Sun.isSelected());

            // Cập nhật UI
            if (Sun.isSelected()) {
                // Hiển thị icon check
                check_icon0.setVisibility(View.VISIBLE);
            } else {
                // Ẩn icon check
                check_icon0.setVisibility(View.GONE);
            }
        });


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

        btnBacktoEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent= new Intent(ScheduleActivity.this, ExploreFragment.class);
//                startActivity(intent);
                finish();
            }
        });

        // Add an onClick listener to a button to trigger scheduling
        findViewById(R.id.textViewDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSchedule();
                finish();
            }
        });
    }

    private void updateGoalSelectionUI() {
        check_basic.setVisibility(selectedGoal == 1? View.VISIBLE: View.GONE);
        check_advance.setVisibility(selectedGoal == 2? View.VISIBLE: View.GONE);
        check_levelup.setVisibility(selectedGoal == 3? View.VISIBLE: View.GONE);
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

    private void createSchedule() {
        List<Schedule> schedules = gatherScheduleData();

        if (schedules.isEmpty()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
            return;
        }

        ScheduleManager scheduleManager = new ScheduleManager(this);
        for (Schedule schedule: schedules) {
            scheduleManager.createSchedule(schedule, new ApiCallback() {
                @Override
                public void onSuccess() {
                    // Xử lý thành công
                    Log.d("ScheduleActivity", "Tạo lịch học thành công: " + schedule.toString());
                }

                @Override
                public void onSuccess(Question questions) {

                }
                @Override
                public void onSuccess(Enrollment enrollment) {}

                @Override
                public void onSuccess(Lesson lesson) {

                }

                @Override
                public void onSuccess(Course course) {

                }

                @Override
                public void onSuccess(Result result) {

                }

                @Override
                public void onSuccess(Answer answer) {

                }

                @Override
                public void onSuccess(MediaFile mediaFile) {

                }

                @Override
                public void onFailure(String errorMessage) {
                    // Xử lý lỗi
                    Log.e("ScheduleActivity", "Lỗi tạo lịch học: " + errorMessage);
                }

                @Override
                public void onSuccessWithOtpID(String otpID) {

                }

                @Override
                public void onSuccessWithToken(String token) {

                }
            });
        }
    }

    private List<Schedule> gatherScheduleData() {
        String hourStr = textViewReminderTimeHour.getText().toString();
        String minuteStr = textViewReminderTimeMins.getText().toString();

        List<Schedule> schedules = new ArrayList<>();
        String userId = SharedPreferencesManager.getInstance(this).getID(); // Replace with your actual method

        // Check each day and add a schedule if selected
        if (Mon.isSelected()) {
            String mondayDate = convertDayOfWeekToDate(DayOfWeek.MONDAY);
            String scheduleTime = mondayDate + "T" + hourStr + ":" + minuteStr + ":00Z";
            schedules.add(new Schedule(userId, scheduleTime, false, null));
        }
        if (Tue.isSelected()) {
            String tuesdayDate = convertDayOfWeekToDate(DayOfWeek.TUESDAY);
            String scheduleTime = tuesdayDate + "T" + hourStr + ":" + minuteStr + ":00Z";
            schedules.add(new Schedule(userId, scheduleTime, false, null));
        }
        if (Wed.isSelected()) {
            String wednesdayDate = convertDayOfWeekToDate(DayOfWeek.WEDNESDAY);
            String scheduleTime = wednesdayDate + "T" + hourStr + ":" + minuteStr + ":00Z";
            schedules.add(new Schedule(userId, scheduleTime, false, null));
        }
        if (Thu.isSelected()) {
            String thursdayDate = convertDayOfWeekToDate(DayOfWeek.THURSDAY);
            String scheduleTime = thursdayDate + "T" + hourStr + ":" + minuteStr + ":00Z";
            schedules.add(new Schedule(userId, scheduleTime, false, null));
        }
        if (Fri.isSelected()) {
            String fridayDate = convertDayOfWeekToDate(DayOfWeek.FRIDAY);
            String scheduleTime = fridayDate + "T" + hourStr + ":" + minuteStr + ":00Z";
            schedules.add(new Schedule(userId, scheduleTime, false, null));
        }
        if (Sat.isSelected()) {
            String saturdayDate = convertDayOfWeekToDate(DayOfWeek.SATURDAY);
            String scheduleTime = saturdayDate + "T" + hourStr + ":" + minuteStr + ":00Z";
            schedules.add(new Schedule(userId, scheduleTime, false, null));
        }
        if (Sun.isSelected()) {
            String sundayDate = convertDayOfWeekToDate(DayOfWeek.SUNDAY);
            String scheduleTime = sundayDate + "T" + hourStr + ":" + minuteStr + ":00Z";
            schedules.add(new Schedule(userId, scheduleTime, false, null));
        }

        return schedules;
    }

    private String convertDayOfWeekToDate(DayOfWeek dayOfWeek) {
        // Get today's date
        LocalDate currentDate = LocalDate.now();

        // Calculate the number of days to add to reach the selected day
        int daysToAdd = (dayOfWeek.getValue() - currentDate.getDayOfWeek().getValue() + 7) % 7;

        // Add the days to the current date to get the date of the selected day
        LocalDate selectedDate = currentDate.plusDays(daysToAdd);

        // Format the date according to ISO 8601 (yyyy-MM-dd)
        return selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}