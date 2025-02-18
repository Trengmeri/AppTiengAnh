package com.example.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.model.Flashcard;
import com.example.test.ui.FlashcardInformationActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private Context context;
    private List<Flashcard> flashcards;

    public FlashcardAdapter(Context context, List<Flashcard> flashcards) {
        this.context = context;
        this.flashcards = flashcards;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flashcard, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        Flashcard flashcard = flashcards.get(position);
        holder.wordTextView.setText(flashcard.getWord());
        holder.meaningTextView.setText(flashcard.getVietNameseMeaning());

        // Thêm sự kiện click cho item flashcard
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlashcardInformationActivity.class);
            intent.putExtra("FLASHCARD_ID", flashcard.getId()); // Gửi ID flashcard đến FlashcardInformationActivity
            context.startActivity(intent); // Khởi động activity thông tin flashcard
        });
        // Sự kiện click vào iconRemove để hiển thị dialog xác nhận
        holder.iconRemove.setOnClickListener(v -> showRemoveDialog(position));

    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    public static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;
        TextView meaningTextView;
        ImageView iconRemove;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.textViewWord);
            meaningTextView = itemView.findViewById(R.id.textViewMeaning);
            iconRemove = itemView.findViewById(R.id.iconRemove);
        }
    }
    private void showRemoveDialog(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_remove_flash); // Tạo file XML cho dialog

        Button btnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);
        Button btnRemove = bottomSheetDialog.findViewById(R.id.btnRemove);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnRemove.setOnClickListener(v -> {
            // Xóa flashcard khỏi màn hình
//            LinearLayout layoutFlashcards = findViewById(R.id.flashContainer);
//            layoutFlashcards.removeView(flashcardView);
//            bottomSheetDialog.dismiss(); // Đóng hộp thoại sau khi xóa
            flashcards.remove(position);  // Xóa khỏi danh sách
            notifyItemRemoved(position); // Cập nhật RecyclerView
            bottomSheetDialog.dismiss(); // Đóng dialog
        });

        bottomSheetDialog.show();
    }
}