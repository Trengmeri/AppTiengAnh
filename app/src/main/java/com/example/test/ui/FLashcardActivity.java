package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class FLashcardActivity extends AppCompatActivity {

    TextView flBack;
    LinearLayout flcid;
    ImageView btnAddflash,btnRemove;
    LinearLayout flashContainer;

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
        flBack = findViewById(R.id.flBack);
        flcid = findViewById(R.id.flcid);
        btnAddflash = findViewById(R.id.btnAddflash);
        btnRemove = findViewById(R.id.btnRemove);
        flashContainer = findViewById(R.id.flashContainer);
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FLashcardActivity.this, GroupFlashcardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        flcid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FLashcardActivity.this, FlashcardInfomationActivity.class);
                startActivity(intent);
            }
        });

        btnAddflash.setOnClickListener(view -> showAddFlashDialog());
        btnRemove.setOnClickListener(view -> showRemoveDialog(flashContainer));

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
                //addFlashcardButton(word); // Hàm thêm nhóm vào danh sách
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

}