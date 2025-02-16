package com.example.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.model.Flashcard;
import com.example.test.ui.FlashcardInformationActivity;

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
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    public static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;
        TextView meaningTextView;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.textViewWord);
            meaningTextView = itemView.findViewById(R.id.textViewMeaning);
        }
    }
}