package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.ui.explore.ExploreFragment;

public class ScheduleActivity extends AppCompatActivity {

    private TextView textViewReminderTimeHour, textViewReminderTimeMins,btnBacktoEx;
    private ImageView up, down; // Single ImageView for Up/Down
    ImageView Mon,Tue,Wed,Thu,Fri,Sat,Sun,check_icon2,check_icon3,check_icon4,check_icon5,check_icon6,check_icon7,check_icon0;
    ImageView Basic, Advance,LevelUp, check_basic, check_advance,check_levelup;

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
        btnBacktoEx= findViewById(R.id.btnBacktoEx);

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

//    private void createSchedule() {
//        for (int i = 0; i < dayButtons.length; i++) {
//            if (dayButtons[i].isChecked()) {
//                String selectedDay = convertDayOfWeekToDate(i);
//                String scheduleTime = selectedDay + "T" + hourStr + ":" + minuteStr + ":00Z";
//
//        Schedule request = new Schedule();
//        request.setScheduleTime(scheduleTime);
//        request.setDaily(isDaily);
//        request.setCourseId(courseId);
//
//        ScheduleManager scheduleManager = new ScheduleManager(this);
//        scheduleManager.createSchedule(request, new ApiCallback() {
//            @Override
//            public void onSuccess() {
//                // Xử lý thành công
//                Log.d("ScheduleActivity", "Tạo lịch học thành công");
//            }
//
//            @Override
//            public void onSuccess(Question questions) {
//
//            }
//
//            @Override
//            public void onSuccess(Lesson lesson) {
//
//            }
//
//            @Override
//            public void onSuccess(Course course) {
//
//            }
//
//            @Override
//            public void onSuccess(Result result) {
//
//            }
//
//            @Override
//            public void onSuccess(Answer answer) {
//
//            }
//
//            @Override
//            public void onSuccess(MediaFile mediaFile) {
//
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                // Xử lý lỗi
//                Log.e("ScheduleActivity", "Lỗi tạo lịch học: " + errorMessage);
//            }
//
//            @Override
//            public void onSuccessWithOtpID(String otpID) {
//
//            }
//
//            @Override
//            public void onSuccessWithToken(String token) {
//
//            }
//
//            //... (Các phương thức onSuccess và onFailure khác)
//        });
//    }
//
//    private String convertDayOfWeekToDate(int dayOfWeek) {
//        // Lấy ngày hiện tại
//        LocalDate currentDate = LocalDate.now();
//
//        // Lấy thứ trong tuần tương ứng với số nguyên đầu vào (0 = Chủ nhật, 1 = Thứ hai,...)
//        DayOfWeek selectedDayOfWeek = DayOfWeek.of(dayOfWeek % 7 + 1);
//
//        // Tính toán số ngày cần cộng thêm để đến ngày được chọn
//        int daysToAdd = (selectedDayOfWeek.getValue() - currentDate.getDayOfWeek().getValue() + 7) % 7;
//
//        // Cộng thêm số ngày vào ngày hiện tại để có được ngày tháng của thứ được chọn
//        LocalDate selectedDate = currentDate.plusDays(daysToAdd);
//
//        // Định dạng ngày tháng theo chuẩn ISO 8601 (yyyy-MM-dd)
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());
//        return selectedDate.format(formatter);
//    }
}