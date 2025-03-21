package com.example.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.NevigateQuestion;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;
    private LessonManager lessonManager = new LessonManager();
    private ResultManager resultManager = new ResultManager(context);

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        courseList.sort(Comparator.comparingInt(Course::getId));
        Course course = courseList.get(position);
        holder.tvCourseTitle.setText(course.getName());
        holder.tvCourseDescription.setText(course.getIntro());

        // Xóa tất cả lesson trước khi thêm mới
        holder.lessonContainer.removeAllViews(); // Sắp xếp lesson theo thứ tự tăng dần


        // Thêm từng Lesson ID vào layout
        for (Integer lessonId : course.getLessonIds()) {
            TextView textView = new TextView(context);
            textView.setText(String.valueOf(lessonId));
            textView.setTextSize(16);
            textView.setTypeface(null, Typeface.BOLD); // Chữ in đậm
            textView.setGravity(Gravity.CENTER);

            int size = 100;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(20, 0, 20, 0); // Điều chỉnh khoảng cách giữa các lesson
            textView.setLayoutParams(params);

            resultManager.fetchResultByLesson(lessonId, new ApiCallback<Result>() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onSuccess(Result result) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        textView.setBackgroundResource(R.drawable.bg_lesson_cricle);
                        textView.setBackgroundTintList(null);// Nếu có kết quả thì đặt bg_lesson
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        textView.setBackgroundResource(R.drawable.bg_lesson_cricle); // Giữ nền mặc định
                        textView.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY)); // Áp màu xám
                    });
                }
            });

            // Xử lý sự kiện click để fetch dữ liệu và điều hướng
            textView.setOnClickListener(v -> {
                lessonManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
                    @Override
                    public void onSuccess(Lesson lesson) {
                        Intent intent = new Intent(context, NevigateQuestion.class);
                        intent.putExtra("skill", lesson.getSkillType());
                        intent.putExtra("courseId", course.getId());
                        intent.putExtra("lessonId", lessonId);
                        intent.putExtra("questionIds", new ArrayList<>(lesson.getQuestionIds())); // Truyền danh sách câu hỏi
                        context.startActivity(intent);
                    }

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(context, "Lỗi tải dữ liệu: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            });

            holder.lessonContainer.addView(textView);
        }
    }



    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseTitle, tvCourseDescription;
        LinearLayout lessonContainer;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvCourseDescription = itemView.findViewById(R.id.tvCourseDescription);
            lessonContainer = itemView.findViewById(R.id.lessonContainer);
        }
    }
}

