package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.FlashcardAdapter;
import com.example.test.api.FlashcardApiCallback;
import com.example.test.api.FlashcardManager;
import com.example.test.model.Flashcard;
import com.example.test.response.ApiResponseFlashcard;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.ApiResponseOneFlashcard;
import com.example.test.response.FlashcardGroupResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFlashcards;
    private FlashcardManager flashcardManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LinearLayout flashContainer = findViewById(R.id.flashContainer);
        recyclerViewFlashcards = findViewById(R.id.recyclerViewFlashcards);
        flashcardManager = new FlashcardManager();

        int groupId = getIntent().getIntExtra("GROUP_ID", -1);
        if (groupId != -1) {
            fetchFlashcards(groupId);
        }
    }

    private void fetchFlashcards(int groupId) {
        flashcardManager.fetchFlashcardsInGroup(groupId, new FlashcardApiCallback() {
            @Override
            public void onSuccess(ApiResponseFlashcardGroup response) {

            }

            @Override
            public void onSuccess(FlashcardGroupResponse response) {

            }

            @Override
            public void onSuccess(ApiResponseFlashcard response) {
                List<Flashcard> flashcards = response.getData().getContent();
                if (flashcards != null && !flashcards.isEmpty()) {
                    updateRecyclerView(flashcards);
                } else {
                    // Xử lý trường hợp không có flashcard nào
                    Log.w("FlashcardActivity", "No flashcards found for group ID: " + groupId);
                }
            }

            @Override
            public void onSuccess(ApiResponseOneFlashcard response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("FlashcardActivity", "Error fetching flashcards: " + errorMessage);
                runOnUiThread(() -> {
                    Toast.makeText(FlashcardActivity.this, "Error fetching flashcards: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void updateRecyclerView(List<Flashcard> flashcards) {
        FlashcardAdapter adapter = new FlashcardAdapter(this, flashcards);
        recyclerViewFlashcards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFlashcards.setAdapter(adapter);
    }

    private void showAddFlashDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_flash, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText edtFlashName = dialogView.findViewById(R.id.edtFlashName);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Ban đầu disable nút Add
        btnAdd.setEnabled(false);
        btnAdd.setAlpha(0.5f); // Làm mờ nút Add

        // Lắng nghe sự thay đổi của EditText
        edtFlashName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    btnAdd.setEnabled(false);
                    btnAdd.setAlpha(0.5f); // Làm mờ nút Add
                } else {
                    btnAdd.setEnabled(true);
                    btnAdd.setAlpha(1.0f); // Hiển thị rõ nút Add
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Xử lý sự kiện nút Cancel
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Xử lý sự kiện nút Add
        btnAdd.setOnClickListener(v -> {
            String word = edtFlashName.getText().toString().trim();
            if (!word.isEmpty()) {
                // addFlashcardButton(word); // Hàm thêm nhóm vào danh sách
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showRemoveDialog(View flashcardView) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_remove_flash); // Tạo file XML cho dialog

        Button btnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);
        Button btnRemove = bottomSheetDialog.findViewById(R.id.btnRemove);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnRemove.setOnClickListener(v -> {
            // Xóa flashcard khỏi màn hình
            LinearLayout layoutFlashcards = findViewById(R.id.flashContainer);
            layoutFlashcards.removeView(flashcardView);
            bottomSheetDialog.dismiss(); // Đóng hộp thoại sau khi xóa
        });

        bottomSheetDialog.show();
    }

    private void addFlashcardButton(Flashcard flashcard) {
        MaterialButton flashcardButton = new MaterialButton(this);
        flashcardButton.setText(flashcard.getWord());
        flashcardButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Sự kiện khi nhấn vào nút flashcard
        flashcardButton.setOnClickListener(v -> {
            Intent intent = new Intent(FlashcardActivity.this, FlashcardInformationActivity.class);
            intent.putExtra("FLASHCARD_ID", flashcard.getId()); // Gửi ID flashcard đến FlashcardInfomationActivity
            startActivity(intent); // Chuyển hướng đến FlashcardInfomationActivity
        });

        // Thêm nút vào layout
        LinearLayout flashcardContainer = findViewById(R.id.flashContainer);
        flashcardContainer.addView(flashcardButton);
    }
}