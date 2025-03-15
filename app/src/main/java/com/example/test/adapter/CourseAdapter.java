package com.example.test.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.model.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

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
        Course course = courseList.get(position);
        holder.tvCourseTitle.setText(course.getName());

        // Xóa các lesson cũ
        holder.lessonContainer.removeAllViews();

        // Số lượng bài học
        int lessonCount = course.getLessonIds().size();
        int totalSpacing = 40 * (lessonCount - 1); // Tổng khoảng cách giữa các hình tròn
        int itemWidth = (holder.lessonContainer.getWidth() - totalSpacing) / lessonCount; // Tính toán kích thước hình tròn

        for (Integer lessonId : course.getLessonIds()) {
            TextView textView = new TextView(context);
            textView.setText(String.valueOf(lessonId));
            textView.setTextSize(16);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.bg_lesson); // Hình tròn

            // Điều chỉnh kích thước hợp lý
            int size = Math.min(100, itemWidth); // Đảm bảo không quá to
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(20, 0, 20, 0); // Căn chỉnh khoảng cách
            textView.setLayoutParams(params);

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
            lessonContainer = itemView.findViewById(R.id.lessonContainer);
        }
    }
}

