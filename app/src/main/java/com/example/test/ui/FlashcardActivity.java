package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.NetworkChangeReceiver;
import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.adapter.FlashcardAdapter;
import com.example.test.api.AddFlashCardApiCallback;
import com.example.test.api.FlashcardApiCallback;
import com.example.test.api.FlashcardManager;
import com.example.test.model.Definition;
import com.example.test.model.Flashcard;
import com.example.test.model.FlashcardUtils;
import com.example.test.model.Meaning;
import com.example.test.model.Phonetic;
import com.example.test.model.WordData;
import com.example.test.response.ApiResponseFlashcard;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.ApiResponseOneFlashcard;
import com.example.test.response.FlashcardGroupResponse;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFlashcards;
    private FlashcardManager flashcardManager;
    NetworkChangeReceiver networkReceiver;
    TextView flBack;
    ImageView btnAddFlash, btnremove;
    private List<Flashcard> flashcards = new ArrayList<>();
    private EditText edtFlashName;
    private int currentPage = 1;
    private int totalPages = 1;
    private final int pageSize = 4; // Mỗi trang hiển thị 5 nhóm flashcard
    private ImageView btnNext, btnPrevious;
    private FlashcardAdapter flashcardAdapter;

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
        networkReceiver = new NetworkChangeReceiver();
        flBack = findViewById(R.id.flBack);
        btnAddFlash = findViewById(R.id.btnAddflash);
        btnremove = findViewById(R.id.iconRemove);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext.setAlpha(0.5f);
        btnNext.setEnabled(false);
        flashcardAdapter = new FlashcardAdapter(this, flashcards);
        @SuppressLint("CutPasteId") RecyclerView recyclerView = findViewById(R.id.recyclerViewFlashcards);
        recyclerView.setAdapter(flashcardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        btnPrevious.setAlpha(0.5f);
        btnPrevious.setEnabled(false);

        int groupId = getIntent().getIntExtra("GROUP_ID", -1);
        if (groupId != -1) {
            fetchFlashcards(groupId,currentPage);
        }
        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                fetchFlashcards(groupId,currentPage);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                fetchFlashcards(groupId,currentPage);
            }
        });
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(FlashcardActivity.this, GroupFlashcardActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
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
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    btnAdd.setEnabled(!charSequence.toString().trim().isEmpty());
                    btnAdd.setAlpha(btnAdd.isEnabled() ? 1.0f : 0.5f);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            // Xử lý sự kiện nút Cancel
            btnCancel.setOnClickListener(v -> dialog.dismiss());

            btnAdd.setOnClickListener(v -> {
                // Lấy từ đã nhập
                String word = edtFlashName.getText().toString().trim();

                // Đóng dialog_add_flash trước khi mở dialog_add_definition
                dialog.dismiss();

                // Gọi API để lấy thông tin từ
                showDefinitionDialog(word);
            });
        });

    }


    @SuppressLint("MissingInflatedId")
    private void showDefinitionDialog(String word) {
        flashcardManager.fetchWordDefinition(word, new AddFlashCardApiCallback<WordData>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(WordData wordData) {
                runOnUiThread(() -> {
                    // Gộp các nghĩa trước khi hiển thị
                    List<WordData> mergedData = FlashcardUtils.mergeWordData(Collections.singletonList(wordData));
                    Log.d("DEBUG", "Dữ liệu trước khi merge: " + new Gson().toJson(wordData));
                    Log.d("DEBUG", "Dữ liệu sau khi merge: " + new Gson().toJson(mergedData));

                    WordData mergedWordData = mergedData.get(0); // Chỉ lấy phần tử đầu tiên vì chỉ có 1 từ

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_add_definition, null);

                    LinearLayout phoneticContainer = dialogView.findViewById(R.id.phoneticContainer);
                    LinearLayout definitionContainer = dialogView.findViewById(R.id.definitionContainer);

                    LinearLayout partOfSpeechContainer = dialogView.findViewById(R.id.partOfSpeechContainer);
                    Button btnDone = dialogView.findViewById(R.id.btDone);
                    btnDone.setEnabled(false);
                    btnDone.setAlpha(0.5f);

                    List<AppCompatButton> phoneticButtons = new ArrayList<>();
                    List<AppCompatButton> speechButtons = new ArrayList<>();
                    List<AppCompatButton> definitionButtons = new ArrayList<>();
//                    List<AppCompatButton> meaningButtons = new ArrayList<>();

                    // Hiển thị phonetics
                    if (mergedWordData.getPhonetics() != null && !mergedWordData.getPhonetics().isEmpty()) {
                        phoneticButtons.clear();
                        for (Phonetic phonetic : mergedWordData.getPhonetics()) {
                            AppCompatButton btn = new AppCompatButton(FlashcardActivity.this);
                            btn.setText(phonetic.getText());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,  // Chiều rộng co giãn theo nội dung
                                    ViewGroup.LayoutParams.WRAP_CONTENT   // Chiều cao tự động co giãn
                            );
                            params.setMargins(8, 8, 8, 8); // (left, top, right, bottom)

                            btn.setLayoutParams(params);
                            btn.setBackgroundResource(R.drawable.btn_item_click);
                            btn.setTextColor(ContextCompat.getColor(FlashcardActivity.this, R.color.black));
                            btn.setTextSize(14);
                            btn.setAllCaps(false);
                            btn.setGravity(Gravity.CENTER); // Căn giữa văn bản
                            btn.setTag(false);
                            btn.setOnClickListener(v -> {
                                for (AppCompatButton otherBtn : phoneticButtons) {
                                    Log.d("DEBUG", "Số nút trong phoneticButtons: " + phoneticButtons.size());

                                    otherBtn.setSelected(false);
                                    otherBtn.setBackgroundResource(R.drawable.btn_item_click);
                                }
                                btn.setSelected(true);
                                btn.setBackgroundResource(R.drawable.btn_item_click);
                                checkEnableDone(phoneticButtons, definitionButtons, speechButtons, btnDone);
                            });

                            phoneticButtons.add(btn);
                            phoneticContainer.addView(btn);
                        }
                    } else {
                        phoneticContainer
                                .addView(new AppCompatTextView(FlashcardActivity.this) {
                                    {
                                        setText("No phonetics available");
                                    }
                                });
                    }

                    // Hiển thị Part of Speech
                    if (mergedWordData.getMeanings() != null && !mergedWordData.getMeanings().isEmpty()) {
                        speechButtons.clear();
                        Log.d("DEBUG", "Số meanings: " + mergedWordData.getMeanings().size());
                        for (int i = 0; i < mergedWordData.getMeanings().size(); i++) {
                            Meaning meaning = mergedWordData.getMeanings().get(i);
                            Log.d("DEBUG", "Thêm nút Part of Speech: " + meaning.getPartOfSpeech());
                            if (meaning.getPartOfSpeech() != null && !meaning.getPartOfSpeech().trim().isEmpty()) {

                                AppCompatButton btn = new AppCompatButton(FlashcardActivity.this);
                                btn.setText(meaning.getPartOfSpeech());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,  // Chiều rộng co giãn theo nội dung
                                        ViewGroup.LayoutParams.WRAP_CONTENT   // Chiều cao tự động co giãn
                                );
                                params.setMargins(8, 8, 8, 8); // (left, top, right, bottom)
                                btn.setLayoutParams(params);
                                btn.setBackgroundResource(R.drawable.btn_item_click);
                                btn.setTextColor(ContextCompat.getColor(FlashcardActivity.this, R.color.black));
                                btn.setTextSize(14);
                                btn.setAllCaps(false);
                                btn.setTag(false);
                                btn.setGravity(Gravity.CENTER); // Căn giữa văn bản

                                // Sự kiện click cho button Part of Speech
                                btn.setOnClickListener(v -> {
                                    for (AppCompatButton otherBtn : speechButtons) {
                                        Log.d("DEBUG", "Số nút trong phoneticButtons: " + speechButtons.size());

                                        otherBtn.setSelected(false);
                                        otherBtn.setBackgroundResource(R.drawable.btn_item_click);
                                    }
                                    btn.setSelected(true);
                                    btn.setBackgroundResource(R.drawable.btn_item_click);
                                    checkEnableDone(phoneticButtons, definitionButtons, speechButtons, btnDone);
                                    // Hiển thị definitions cho part of speech đã chọn
                                    updateDefinitions(definitionContainer, meaning, dialogView,
                                            phoneticButtons, definitionButtons, speechButtons, btnDone);
                                });
                                speechButtons.add(btn);
                                partOfSpeechContainer.addView(btn);
                            }
                        }

                        // Hiển thị definitions cho part of speech đầu tiên
                        if (!mergedWordData.getMeanings().isEmpty()) {
                            updateDefinitions(definitionContainer, mergedWordData.getMeanings().get(0), dialogView,
                                    phoneticButtons, definitionButtons, speechButtons, btnDone);
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(FlashcardActivity.this);
                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    btnDone.setOnClickListener(v -> {
                        String wordflash = word.trim();
                        int partOfSpeechIndex = getSelectedIndex(speechButtons);; // Chỉ mục loại từ được chọn
                        List<Integer> definitionIndices = getSelectedDefinitionIndices(definitionButtons);; // Danh sách các chỉ mục định nghĩa

                        if (wordflash.isEmpty()) {
                            Toast.makeText(FlashcardActivity.this, "Vui lòng nhập từ vựng!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int userId = Integer.parseInt(SharedPreferencesManager.getInstance(getApplicationContext()).getID());
                        Log.d("DEBUG","wordflash:"+ wordflash);
                        Log.d("DEBUG","speech:"+ partOfSpeechIndex);
                        Log.d("DEBUG","definition:"+ definitionIndices);
                        Log.d("DEBUG","userid:"+ userId);
                        flashcardManager.createFlashcard(getApplicationContext(),wordflash, definitionIndices, partOfSpeechIndex, userId, new AddFlashCardApiCallback<String>() {
                            @Override
                            public void onSuccess(String flashcardId) { // Lấy ID của flashcard vừa tạo
                                if (flashcardId == null) {
                                    runOnUiThread(() -> Toast.makeText(FlashcardActivity.this, "Lỗi tạo flashcard!", Toast.LENGTH_SHORT).show());
                                    return;
                                }

                                Log.d("DEBUG", "Flashcard created with ID: " + flashcardId);
                                //int groupID = Integer.parseInt(SharedPreferencesManager.getInstance(getApplicationContext()).getID());
                                int groupId = getIntent().getIntExtra("GROUP_ID", -1);
                                Log.d("GroupID:", "Group ID duoc goi :"+ groupId);
                                //  Gọi API để thêm flashcard vào nhóm
                                flashcardManager.addFlashcardToGroup(Integer.parseInt(flashcardId), groupId, new AddFlashCardApiCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        runOnUiThread(() -> {
                                            Log.d("DEBUG", "Flashcard added to group");
                                            // Cập nhật UI
                                            Flashcard newFlashcard = new Flashcard(Integer.parseInt(flashcardId), wordflash, definitionIndices, partOfSpeechIndex);
                                            flashcards.add(newFlashcard);
                                            flashcardAdapter.notifyDataSetChanged(); // Cập nhật toàn bộ danh sách
                                            recyclerViewFlashcards.scrollToPosition(flashcards.size() - 1);
                                            Toast.makeText(FlashcardActivity.this, "Thêm flashcard thành công!", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        });
                                    }

                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        runOnUiThread(() -> {
                                            Log.e("DEBUG", "API Error: " + errorMessage);
                                            Toast.makeText(FlashcardActivity.this, "Lỗi thêm vào nhóm: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                runOnUiThread(() -> {
                                    Log.e("DEBUG", "API Error: " + errorMessage);
                                    Toast.makeText(FlashcardActivity.this, "Lỗi tạo flashcard: " + errorMessage, Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    });
                    dialog.show();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(FlashcardActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Phương thức cập nhật definitions dựa trên part of speech đã chọn
    private void updateDefinitions(LinearLayout definitionContainer, Meaning meaning, View dialogView,
            List<AppCompatButton> phoneticButtons, List<AppCompatButton> definitionButtons,
            List<AppCompatButton> speechButtons,
            Button btnDone) {

        definitionContainer.removeAllViews();
        definitionButtons.clear();
        ScrollView definitionScrollView = dialogView.findViewById(R.id.definitionScrollView);

        int numberOfButtons = 0;
        if (meaning.getDefinitions() != null) {
            numberOfButtons += meaning.getDefinitions().size();
        }

        int buttonHeight = (int) getResources().getDimension(R.dimen.button_height);
        int scrollViewHeight = buttonHeight * Math.min(numberOfButtons, 3);

        definitionScrollView.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, scrollViewHeight));

        if (meaning.getDefinitions() != null) {
            Log.d("DEBUG", "Số lượng definitions: " + meaning.getDefinitions().size());
            for (Definition def : meaning.getDefinitions()) {
                Log.d("DEBUG", "Definition: " + def.getDefinition());
            }
        }
        // Hiển thị definitions cho part of speech đã chọn
        if (meaning.getDefinitions() != null && !meaning.getDefinitions().isEmpty()) {
            for (Definition definition : meaning.getDefinitions()) {
                AppCompatButton btn = new AppCompatButton(FlashcardActivity.this);
                btn.setText(definition.getDefinition());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,  // Chiều rộng co giãn theo nội dung
                        ViewGroup.LayoutParams.WRAP_CONTENT   // Chiều cao tự động co giãn
                );
                params.setMargins(8, 5, 0, 0); // (left, top, right, bottom)

                btn.setLayoutParams(params);
                btn.setBackgroundResource(R.drawable.btn_item_def_click);
                btn.setTextColor(ContextCompat.getColor(FlashcardActivity.this, R.color.black));
                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                btn.setPadding(40, 10, 40, 10);
                btn.setAllCaps(false);
                btn.setTag(false);
                btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

                btn.setOnClickListener(v -> {
                    Log.d("DEBUG", "Selected definition: " + definition.getDefinition());

                    // Gọi API để dịch nghĩa
                    try {
                        flashcardManager.translateDefinition(definition.getDefinition(),
                                new AddFlashCardApiCallback<String>() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onSuccess(String vietnameseMeaning) {
                                        // Cập nhật UI trong luồng chính
                                        runOnUiThread(() -> {
                                            // Hiển thị nghĩa tiếng Việt
                                            TextView vietnameseMeaningTextView = dialogView
                                                    .findViewById(R.id.vietnameseMeaningTextView);
                                            vietnameseMeaningTextView.setText(vietnameseMeaning);
                                        });
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(FlashcardActivity.this, "Error: " + errorMessage,
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        });
                                    }
                                });
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("DEBUG", "Số nút trong definitionButtons trước khi cập nhật: " + definitionButtons.size());
                    for (AppCompatButton otherBtn : definitionButtons) {
                        Log.d("DEBUG", "Số nút trong definitionButtons: " + definitionButtons.size());

                        otherBtn.setSelected(false);
                        otherBtn.setBackgroundResource(R.drawable.btn_item_def_click);
                    }
                    btn.setSelected(true);
                    btn.setBackgroundResource(R.drawable.btn_item_def_click);
                    checkEnableDone(phoneticButtons, definitionButtons, speechButtons, btnDone);
                });
                definitionButtons.add(btn);
                definitionContainer.addView(btn);
            }
            // Log kiểm tra sau khi thêm nút
            Log.d("DEBUG", "Tổng số definitionButtons sau khi thêm: " + definitionButtons.size());
            for (AppCompatButton btn : definitionButtons) {
                Log.d("DEBUG", "Button text: " + btn.getText().toString());
            }
        } else {
            definitionContainer.addView(new androidx.appcompat.widget.AppCompatTextView(FlashcardActivity.this) {
                {
                    setText("No definitions available");
                }
            });
        }
    }

    // Hàm kiểm tra và cập nhật trạng thái của nút Done
    private void checkEnableDone(List<AppCompatButton> phoneticButtons,
                                 List<AppCompatButton> speechButtons,
                                 List<AppCompatButton> definitionButtons,
                                 Button btnDone) {
        boolean isPhoneticSelected = false;
        boolean isSpeechSelected = false;
        boolean isDefinitionSelected = false;

        // Kiểm tra xem có ít nhất một nút được chọn trong mỗi nhóm không
        for (AppCompatButton btn : phoneticButtons) {
            if (btn.isSelected()) {
                isPhoneticSelected = true;
                break;
            }
        }
        for (AppCompatButton btn : speechButtons) {
            if (btn.isSelected()) {
                isSpeechSelected = true;
                break;
            }
        }
        for (AppCompatButton btn : definitionButtons) {
            if (btn.isSelected()) {
                isDefinitionSelected = true;
                break;
            }
        }

        // Nếu cả 3 nhóm đều có nút được chọn, kích hoạt nút Done
        if (isPhoneticSelected && isSpeechSelected && isDefinitionSelected) {
            btnDone.setEnabled(true);
            btnDone.setAlpha(1f); // Hiển thị rõ ràng khi được kích hoạt
        } else {
            btnDone.setEnabled(false);
            btnDone.setAlpha(0.5f); // Làm mờ nếu chưa đủ điều kiện
        }
    }


    private void fetchFlashcards(int groupId,int page) {
        flashcardManager.fetchFlashcardsInGroup(groupId, new FlashcardApiCallback() {
            @Override
            public void onSuccess(Object response) {

            }

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
                updateButtonState();
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

//    private void addFlashcard(String word, List<AppCompatButton> phoneticButtons,
//            List<AppCompatButton> definitionButtons, List<AppCompatButton> speechButtons,
//            AlertDialog dialog) {
//        String selectedPhonetic = null;
//        for (AppCompatButton btn : phoneticButtons) {
//            if ((boolean) btn.getTag()) {
//                selectedPhonetic = btn.getText().toString();
//                break;
//            }
//        }
//
//        String selectedDefinition = null;
//        for (AppCompatButton btn : definitionButtons) {
//            if ((boolean) btn.getTag()) {
//                selectedDefinition = btn.getText().toString();
//                break;
//            }
//        }
//        String selectedSpeech = null;
//        for (AppCompatButton btn : speechButtons) {
//            if ((boolean) btn.getTag()) {
//                selectedSpeech = btn.getText().toString();
//                break;
//            }
//        }
//        if (selectedPhonetic != null && selectedDefinition != null && selectedSpeech != null) {
//            Flashcard newFlashcard = new Flashcard();
//            newFlashcard.setWord(word);
//            newFlashcard.setPhoneticText(selectedPhonetic);
//            newFlashcard.setDefinitions(selectedDefinition);
//            newFlashcard.setVietNameseMeaning(selectedSpeech);
//            flashcards.add(newFlashcard);
//            updateRecyclerView(flashcards);
//            dialog.dismiss();
//        } else {
//            Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
//        }
//    }

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
    private void updateButtonState() {
        if (totalPages > 1) {
            btnPrevious.setEnabled(currentPage > 1);
            btnPrevious.setAlpha(currentPage > 1 ? 1.0f : 0.5f);

            btnNext.setEnabled(currentPage < totalPages);
            btnNext.setAlpha(currentPage < totalPages ? 1.0f : 0.5f);
        } else {
            // Nếu chỉ có 1 trang, làm mờ và vô hiệu hóa cả hai nút
            btnNext.setEnabled(false);
            btnNext.setAlpha(0.5f);

            btnPrevious.setEnabled(false);
            btnPrevious.setAlpha(0.5f);
        }
    }
    private int getSelectedIndex(List<AppCompatButton> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isSelected()) {
                return i;
            }
        }
        return -1; // Không có nút nào được chọn
    }
    private List<Integer> getSelectedDefinitionIndices(List<AppCompatButton> buttons) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isSelected()) {
                indices.add(i);
            }
        }
        return indices;
    }

}