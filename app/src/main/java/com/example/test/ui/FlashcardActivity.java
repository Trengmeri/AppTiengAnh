package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FlashcardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFlashcards;
    private FlashcardManager flashcardManager;
    TextView flBack;
    ImageView btnAddFlash, btnremove;
    private List<Flashcard> flashcards = new ArrayList<>();
    private EditText edtFlashName;

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
        flBack= findViewById(R.id.flBack);
        btnAddFlash= findViewById(R.id.btnAddflash);
        btnremove= findViewById(R.id.iconRemove);

        int groupId = getIntent().getIntExtra("GROUP_ID", -1);
        if (groupId != -1) {
            fetchFlashcards(groupId);
        }

        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(FlashcardActivity.this, GroupFlashcardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnAddFlash.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_flash, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.show();

            // Ánh xạ các view trong dialog
            edtFlashName = dialogView.findViewById(R.id.edtFlashName);
            Button btnAdd = dialogView.findViewById(R.id.btnAdd);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            // Ban đầu disable nút Add
            btnAdd.setEnabled(false);
            btnAdd.setAlpha(0.5f); // Làm mờ nút Add
            edtFlashName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    btnAdd.setEnabled(!charSequence.toString().trim().isEmpty());
                    btnAdd.setAlpha(btnAdd.isEnabled() ? 1.0f : 0.5f);
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            // Xử lý sự kiện nút Cancel
            btnCancel.setOnClickListener(v -> dialog.dismiss());

            // Xử lý sự kiện nhấn nút "Add"
            btnAdd.setOnClickListener(v -> {
                // Lấy từ đã nhập
                String word = edtFlashName.getText().toString().trim();
                // Đóng dialog_add_flash trước khi mở dialog_add_definition
                dialog.dismiss();
                Intent intent = new Intent(this, FlashcardActivity.class);
                intent.putExtra("FLASHCARD_WORD", word);
                //startActivity(intent);


                // Dữ liệu giả định (sau này có thể thay bằng API)
                List<String> phonetics = Arrays.asList("phonetic1", "phonetic2", "phonetic3");
                List<String> definitions = Arrays.asList("definition1", "definition2", "definition3");
                List<String> meanings = Arrays.asList("meaning1", "meaning2");


//                // Hiển thị dialog chọn phonetic & definition
                showDefinitionDialog(word,phonetics, definitions, meanings);

            });

        });
    }
    @SuppressLint("MissingInflatedId")
    private void showDefinitionDialog(String word,List<String> phoneticList, List<String> definitionList, List<String> meaningList) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_definition, null);

        LinearLayout phoneticContainer = dialogView.findViewById(R.id.phoneticContainer);
        LinearLayout definitionContainer = dialogView.findViewById(R.id.definitionContainer);
        LinearLayout meaningContainer = dialogView.findViewById(R.id.meaningContainer);

        TextView btnDone = dialogView.findViewById(R.id.btnDone);

        btnDone.setEnabled(false);
        btnDone.setAlpha(0.5f);

        List<AppCompatButton> phoneticButtons = new ArrayList<>();
        List<AppCompatButton> definitionButtons = new ArrayList<>();
        List<AppCompatButton> meaningButtons = new ArrayList<>();

        // Tạo nút phonetic (Kéo ngang)
        for (String phonetic : phoneticList) {
            AppCompatButton btn = new AppCompatButton(this);
            btn.setText(phonetic);
            btn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            btn.setBackgroundResource(R.drawable.item_phonetic);
            btn.setTextColor(ContextCompat.getColor(this, R.color.black));
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            btn.setPadding(20, 10, 20, 10);
            btn.setTag(false);

            btn.setOnClickListener(v -> {
                for (AppCompatButton otherBtn : phoneticButtons) {
                    otherBtn.setBackgroundResource(R.drawable.item_phonetic);
                    otherBtn.setTag(false);
                }
                btn.setBackgroundResource(R.drawable.item_phonetic_selected);
                btn.setTag(true);
                checkEnableDone(phoneticButtons, definitionButtons,meaningButtons, btnDone);
            });

            phoneticButtons.add(btn);
            phoneticContainer.addView(btn);
        }

        // Tạo nút definition
        for (String definition : definitionList) {
            AppCompatButton btn = new AppCompatButton(this);
            btn.setText(definition);
            btn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            btn.setBackgroundResource(R.drawable.item_definition);
            btn.setTextColor(ContextCompat.getColor(this, R.color.black));
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            btn.setPadding(20, 10, 20, 10);
            btn.setTag(false);

            btn.setOnClickListener(v -> {
                for (AppCompatButton otherBtn : definitionButtons) {
                    otherBtn.setBackgroundResource(R.drawable.item_definition);
                    otherBtn.setTag(false);
                }
                btn.setBackgroundResource(R.drawable.item_definition_selected);
                btn.setTag(true);
                checkEnableDone(phoneticButtons, definitionButtons,meaningButtons, btnDone);
            });

            definitionButtons.add(btn);
            definitionContainer.addView(btn);
        }

        //Tạo nút meaning
        for (String meaning : meaningList) {
            AppCompatButton btn = new AppCompatButton(this);
            btn.setText(meaning);
            btn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            btn.setBackgroundResource(R.drawable.item_definition);
            btn.setTextColor(ContextCompat.getColor(this, R.color.black));
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            btn.setPadding(20, 10, 20, 10);
            btn.setTag(false);

            btn.setOnClickListener(v -> {
                for (AppCompatButton otherBtn : meaningButtons) {
                    otherBtn.setBackgroundResource(R.drawable.item_definition);
                    otherBtn.setTag(false);
                }
                btn.setBackgroundResource(R.drawable.item_definition_selected);
                btn.setTag(true);
                checkEnableDone(phoneticButtons, definitionButtons,meaningButtons, btnDone);
            });
            meaningButtons.add(btn);
            meaningContainer.addView(btn);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

//        btnDone.setOnClickListener(v -> dialog.dismiss());
        btnDone.setOnClickListener(v -> {
//             String flashword = getIntent().getStringExtra("FLASHCARD_WORD");
            addFlashcard(word, phoneticButtons, definitionButtons, meaningButtons, dialog);
        });
        dialog.show();
    }

    private void checkEnableDone(List<AppCompatButton> phoneticButtons, List<AppCompatButton> definitionButtons, List<AppCompatButton> meaningButtons,TextView btnDone) {
        boolean phoneticSelected = false;
        boolean definitionSelected = false;
        boolean meaningSelected = false;


        for (AppCompatButton btn : phoneticButtons) {
            if ((boolean) btn.getTag()) { // Kiểm tra tag
                phoneticSelected = true;
                break;
            }
        }

        for (AppCompatButton btn : definitionButtons) {
            if ((boolean) btn.getTag()) { // Kiểm tra tag
                definitionSelected = true;
                break;
            }
        }
        for (AppCompatButton btn : meaningButtons) {
            if ((boolean) btn.getTag()) { // Kiểm tra tag
                meaningSelected = true;
                break;
            }
        }

        // Cập nhật trạng thái nút btnDone
        btnDone.setEnabled(phoneticSelected && definitionSelected && meaningSelected);
        btnDone.setAlpha(phoneticSelected && definitionSelected && meaningSelected ? 1.0f : 0.5f);

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
                    runOnUiThread(() -> {
                        updateRecyclerView(flashcards);
                    });
                } else {
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

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView(List<Flashcard> flashcards) {
        if (recyclerViewFlashcards != null) {
            FlashcardAdapter adapter = new FlashcardAdapter(this, flashcards);
            recyclerViewFlashcards.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewFlashcards.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.e("FlashcardActivity", "RecyclerView is null");
        }
    }

    private void addFlashcard(String word, List<AppCompatButton> phoneticButtons,
                              List<AppCompatButton> definitionButtons, List<AppCompatButton> meaningButtons,
                              AlertDialog dialog) {
        String selectedPhonetic = null;
        for (AppCompatButton btn : phoneticButtons) {
            if ((boolean) btn.getTag()) {
                selectedPhonetic = btn.getText().toString();
                break;
            }
        }

        String selectedDefinition = null;
        for (AppCompatButton btn : definitionButtons) {
            if ((boolean) btn.getTag()) {
                selectedDefinition = btn.getText().toString();
                break;
            }
        }
        String selectedMeaning = null;
        for (AppCompatButton btn : meaningButtons) {
            if ((boolean) btn.getTag()) {
                selectedMeaning = btn.getText().toString();
                break;
            }
        }
        if (selectedPhonetic != null && selectedDefinition != null && selectedMeaning !=null) {
            Flashcard newFlashcard = new Flashcard();
            newFlashcard.setWord(word);
            newFlashcard.setPhoneticText(selectedPhonetic);
            newFlashcard.setDefinitions(selectedDefinition);
            newFlashcard.setVietNameseMeaning(selectedMeaning);
            flashcards.add(newFlashcard);
            updateRecyclerView(flashcards);
            dialog.dismiss();
        } else {
            Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        }
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