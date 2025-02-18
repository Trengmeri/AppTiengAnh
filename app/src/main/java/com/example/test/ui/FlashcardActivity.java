package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class FlashcardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFlashcards;
    private FlashcardManager flashcardManager;
    TextView flBack;
    ImageView btnAddFlash, btnremove;
    private List<Flashcard> flashcards = new ArrayList<>();

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
            EditText edtFlashName = dialogView.findViewById(R.id.edtFlashName);
            ImageView btnSearch = dialogView.findViewById(R.id.btnSearch);
            Spinner spinnerMeaning = dialogView.findViewById(R.id.spinner_meaning);
            Spinner spinnerPhonetic = dialogView.findViewById(R.id.spinner_phonetic);
            Spinner spinnerDefinition = dialogView.findViewById(R.id.spinner_def);
            Button btnAdd = dialogView.findViewById(R.id.btnAdd);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            // Ban đầu disable nút Add
            btnAdd.setEnabled(false);
            btnAdd.setAlpha(0.5f); // Làm mờ nút Add
            edtFlashName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    enableAddButton(edtFlashName, spinnerMeaning, spinnerPhonetic, spinnerDefinition, btnAdd);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            // Lắng nghe sự thay đổi của Spinner
            AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    enableAddButton(edtFlashName, spinnerMeaning, spinnerPhonetic, spinnerDefinition, btnAdd);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

            spinnerMeaning.setOnItemSelectedListener(spinnerListener);
            spinnerPhonetic.setOnItemSelectedListener(spinnerListener);
            spinnerDefinition.setOnItemSelectedListener(spinnerListener);

            // Xử lý sự kiện nút Cancel
            btnCancel.setOnClickListener(v -> dialog.dismiss());

            // Bắt sự kiện khi nhấn nút tìm kiếm
            btnSearch.setOnClickListener(v -> {
                String word = edtFlashName.getText().toString().trim();
                if (!word.isEmpty()) {
                    fetchData(word, spinnerMeaning, spinnerPhonetic, spinnerDefinition);
                } else {
                    Toast.makeText(this, "Vui lòng nhập từ cần tìm!", Toast.LENGTH_SHORT).show();
                }
            });
            btnAdd.setOnClickListener(v -> {
                String word = edtFlashName.getText().toString().trim();
                String meaning = spinnerMeaning.getSelectedItem().toString();  // Lấy nghĩa đã chọn
                String phonetic = spinnerPhonetic.getSelectedItem().toString();  // Lấy phát âm đã chọn
                String definition = spinnerDefinition.getSelectedItem().toString();  // Lấy định nghĩa đã chọn

                // Kiểm tra nếu từ không rỗng và các spinner có dữ liệu
                if (!word.isEmpty() && !meaning.isEmpty() && !phonetic.isEmpty() && !definition.isEmpty()) {
                    // Tạo đối tượng flashcard mới
                    Flashcard newFlashcard = new Flashcard();
                    newFlashcard.setWord(word);
                    newFlashcard.setVietNameseMeaning(meaning);
                    newFlashcard.setPhoneticText(phonetic);
                    newFlashcard.setDefinitions(definition);

                    // Thêm flashcard mới vào danh sách flashcards (giả sử flashcards là List<Flashcard>)
                    flashcards.add(newFlashcard);

                    // Cập nhật RecyclerView với danh sách flashcards mới
                    updateRecyclerView(flashcards);

                    // Đóng dialog sau khi thêm flashcard
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            });


        });


    }
    private void enableAddButton(EditText edtFlashName, Spinner spinnerMeaning, Spinner spinnerPhonetic, Spinner spinnerDefinition, Button btnAdd) {
        boolean isFlashNameNotEmpty = !edtFlashName.getText().toString().trim().isEmpty();
        boolean isMeaningSelected = spinnerMeaning.getSelectedItemPosition() != AdapterView.INVALID_POSITION;
        boolean isPhoneticSelected = spinnerPhonetic.getSelectedItemPosition() != AdapterView.INVALID_POSITION;
        boolean isDefinitionSelected = spinnerDefinition.getSelectedItemPosition() != AdapterView.INVALID_POSITION;

        if (isFlashNameNotEmpty && isMeaningSelected && isPhoneticSelected && isDefinitionSelected) {
            btnAdd.setEnabled(true);
            btnAdd.setAlpha(1.0f); // Hiển thị rõ nút Add
        } else {
            btnAdd.setEnabled(false);
            btnAdd.setAlpha(0.5f); // Làm mờ nút Add
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

    @SuppressLint("ClickableViewAccessibility")
    private void fetchData(String word, Spinner spinnerMeaning, Spinner spinnerPhonetic, Spinner spinnerDefinition) {
        List<String> meanings = Arrays.asList("Nghia 1 ", "Nghĩa 2", "Nghĩa 3");
        List<String> phonetics = Arrays.asList("/fəˈnetɪk/", "/fəˈnɛtɪk/");
        List<String> definitions = Arrays.asList("Định nghĩa 1 rat dai khong the hien thi het trong spinner sdkhfdhjdf",
                "Định nghĩa 2 rat dai khong the hien thi het trong spinnerj dhfjdhfjskfsf",
                "Định nghĩa 3 rat dai khong the hien thi het trong spinner jhsdjhfisuhfcjdhfuie");

        // Gán dữ liệu vào Spinner
        setSpinnerData(spinnerMeaning, meanings);
        setSpinnerData(spinnerPhonetic, phonetics);
        setSpinnerData(spinnerDefinition, definitions);

        // Biến cờ để ngăn chặn gọi onItemSelected ngay khi spinner khởi tạo
        final boolean[] isUserInteracted = {false};

        spinnerDefinition.setOnTouchListener((v, event) -> {
            isUserInteracted[0] = true;
            return false;
        });

        spinnerDefinition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteracted[0]) { // Chỉ hiển thị khi người dùng chọn, không phải khi spinner load dữ liệu
                    String fullDefinition = definitions.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(FlashcardActivity.this);
                    builder.setTitle("Definition");
                    builder.setMessage(fullDefinition);
                    builder.setPositiveButton("OK", null);
                    builder.show();

                    // Reset lại cờ để tránh gọi lại onItemSelected không mong muốn
                    isUserInteracted[0] = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Hàm cập nhật dữ liệu vào Spinner
    private void setSpinnerData(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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