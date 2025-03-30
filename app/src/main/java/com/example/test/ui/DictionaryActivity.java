package com.example.test.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.AddFlashCardApiCallback;
import com.example.test.api.FlashcardApiCallback;
import com.example.test.api.FlashcardManager;
import com.example.test.model.Definition;
import com.example.test.model.Meaning;
import com.example.test.model.Phonetic;
import com.example.test.model.WordData;
import com.example.test.response.ApiResponseFlashcard;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.ApiResponseOneFlashcard;
import com.example.test.response.FlashcardGroupResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    EditText edtWord;
    ImageView btnFind;
    LinearLayout wordContainer;
    TextView dicBacktoExplore;
    Button btnAdd;
    private FlashcardManager flashcardManager;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dictionary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtWord=findViewById(R.id.edtWord);
        btnFind=findViewById(R.id.btnFind);
        wordContainer=findViewById(R.id.WordContainer);
        dicBacktoExplore= findViewById(R.id.dicBacktoExplore);
        btnAdd= findViewById(R.id.btnAdd);
        flashcardManager = new FlashcardManager();
        btnAdd.setOnClickListener(v -> {
            List<String> groupNames = getGroupsFromSharedPreferences(this);
            showGroupSelectionDialog(groupNames); // Nếu có dữ liệu, hiển thị luôn
        });

        btnFind.setOnClickListener(view -> {
            String word = edtWord.getText().toString().trim();

            if (isVietnamese(word)) {
                flashcardManager.translateToEnglish(word, new AddFlashCardApiCallback<String>() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(String translatedWord) {
                        runOnUiThread(() -> {
                            showDefinition(translatedWord); // Tìm kiếm nghĩa tiếng Anh
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() ->
                                Toast.makeText(DictionaryActivity.this, "Dịch lỗi: " + errorMessage, Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            } else {
                showDefinition(word);
            }
        });


        dicBacktoExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void showDefinition(String word) {
        btnAdd.setVisibility(View.GONE);
        flashcardManager.fetchWordDefinition(word, new AddFlashCardApiCallback<WordData>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(WordData wordData) {
                runOnUiThread(() -> {
                    wordContainer.removeAllViews(); // Xóa dữ liệu cũ

                    LayoutInflater inflater = getLayoutInflater();
                    View contentView = inflater.inflate(R.layout.item_find_dictionary, wordContainer, false);

                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout phoneticContainer = contentView.findViewById(R.id.phoneticContainer);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout definitionContainer = contentView.findViewById(R.id.definitionContainer);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout partOfSpeechContainer = contentView.findViewById(R.id.partOfSpeechContainer);
                    TextView wordLabel= contentView.findViewById(R.id.wordLabel);
                    List<AppCompatButton> phoneticButtons = new ArrayList<>();
                    List<AppCompatButton> speechButtons = new ArrayList<>();
                    List<AppCompatButton> definitionButtons = new ArrayList<>();
                    wordLabel.setVisibility(View.VISIBLE);
                    wordLabel.setText("Word: " + wordData.getWord());
                    // Hiển thị phonetics
                    if (wordData.getPhonetics() != null && !wordData.getPhonetics().isEmpty()) {
                        for (Phonetic phonetic : wordData.getPhonetics()) {
                            AppCompatButton btn = new AppCompatButton(DictionaryActivity.this);
                            btn.setText(phonetic.getText());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,  // Chiều rộng co giãn theo nội dung
                                    ViewGroup.LayoutParams.WRAP_CONTENT   // Chiều cao tự động co giãn
                            );
                            params.setMargins(8, 8, 8, 8); // (left, top, right, bottom)

                            btn.setLayoutParams(params);
                            btn.setBackgroundResource(R.drawable.btn_item_click);
                            btn.setTextColor(ContextCompat.getColor(DictionaryActivity.this, R.color.black));
                            btn.setTextSize(14);
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
                                checkEnableAdd(phoneticButtons, definitionButtons, speechButtons, btnAdd,true);
                            });

                            phoneticButtons.add(btn);
                            phoneticContainer.addView(btn);
                        }
                    } else {
                        phoneticContainer
                                .addView(new AppCompatTextView(DictionaryActivity.this) {
                                    {
                                        setText("No phonetics available");
                                    }
                                });
                    }

                    // Hiển thị Part of Speech
                    if (wordData.getMeanings() != null) {
                        for (int i = 0; i < wordData.getMeanings().size(); i++) {
                            Meaning meaning = wordData.getMeanings().get(i);
                            if (meaning.getPartOfSpeech() != null) {
                                AppCompatButton btn = new AppCompatButton(DictionaryActivity.this);
                                btn.setText(meaning.getPartOfSpeech());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,  // Chiều rộng co giãn theo nội dung
                                        ViewGroup.LayoutParams.WRAP_CONTENT   // Chiều cao tự động co giãn
                                );
                                params.setMargins(8, 8, 8, 8); // (left, top, right, bottom)
                                btn.setLayoutParams(params);
                                btn.setBackgroundResource(R.drawable.btn_item_click);
                                btn.setTextColor(ContextCompat.getColor(DictionaryActivity.this, R.color.black));
                                btn.setTextSize(14);
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
                                    checkEnableAdd(phoneticButtons, definitionButtons, speechButtons, btnAdd,true);
                                    // Hiển thị definitions cho part of speech đã chọn
                                    updateDefinitions(definitionContainer, meaning, contentView,
                                            phoneticButtons, definitionButtons, speechButtons);
                                });
                                speechButtons.add(btn);
                                partOfSpeechContainer.addView(btn);
                            }
                        }

                        // Hiển thị definitions cho part of speech đầu tiên
                        if (!wordData.getMeanings().isEmpty()) {
                            updateDefinitions(definitionContainer, wordData.getMeanings().get(0), contentView,
                                    phoneticButtons, definitionButtons, speechButtons);
                        }
                    }

                    wordContainer.addView(contentView); // Hiển thị toàn bộ layout thay vì dialog
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(DictionaryActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateDefinitions(LinearLayout definitionContainer, Meaning meaning, View dialogView,
                                   List<AppCompatButton> phoneticButtons, List<AppCompatButton> definitionButtons,
                                   List<AppCompatButton> speechButtons) {

        definitionContainer.removeAllViews();
        NestedScrollView definitionScrollView = dialogView.findViewById(R.id.definitionScrollView);

        int numberOfButtons = 0;
        if (meaning.getDefinitions() != null) {
            numberOfButtons += meaning.getDefinitions().size();
        }

        int buttonHeight = (int) getResources().getDimension(R.dimen.button_height);
        int scrollViewHeight = buttonHeight * Math.min(numberOfButtons, 3);

        definitionScrollView.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, scrollViewHeight));
        // Hiển thị definitions cho part of speech đã chọn
        if (meaning.getDefinitions() != null && !meaning.getDefinitions().isEmpty()) {
            for (Definition definition : meaning.getDefinitions()) {
                AppCompatButton btn = new AppCompatButton(DictionaryActivity.this);
                btn.setText(definition.getDefinition());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,  // Chiều rộng co giãn theo nội dung
                        ViewGroup.LayoutParams.WRAP_CONTENT   // Chiều cao tự động co giãn
                );
                params.setMargins(8, 5, 0, 0); // (left, top, right, bottom)

                btn.setLayoutParams(params);
                btn.setBackgroundResource(R.drawable.btn_item_def_click);
                btn.setTextColor(ContextCompat.getColor(DictionaryActivity.this, R.color.black));
                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                btn.setPadding(40, 10, 40, 10);
                btn.setTag(false);
                btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

                btn.setOnClickListener(v -> {
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
                                            Toast.makeText(DictionaryActivity.this, "Error: " + errorMessage,
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                        });
                                    }
                                });
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    for (AppCompatButton otherBtn : definitionButtons) {
                        Log.d("DEBUG", "Số nút trong definitionButtons: " + definitionButtons.size());

                        otherBtn.setSelected(false);
                        otherBtn.setBackgroundResource(R.drawable.btn_item_def_click);
                    }
                    btn.setSelected(true);
                    btn.setBackgroundResource(R.drawable.btn_item_def_click);
                    checkEnableAdd(phoneticButtons, definitionButtons, speechButtons, btnAdd, true);
                });
                definitionButtons.add(btn);
                definitionContainer.addView(btn);
            }
        } else {
            definitionContainer.addView(new androidx.appcompat.widget.AppCompatTextView(DictionaryActivity.this) {
                {
                    setText("No definitions available");
                }
            });
        }
    }
    private void checkEnableAdd(List<AppCompatButton> phoneticButtons,
                                 List<AppCompatButton> speechButtons,
                                 List<AppCompatButton> definitionButtons,
                                 Button btnAdd,boolean hasPhonetics) {
        boolean isPhoneticSelected = false;
        boolean isSpeechSelected = false;
        boolean isDefinitionSelected = false;

        // Kiểm tra xem có ít nhất một nút được chọn trong mỗi nhóm không
        if (hasPhonetics) {
            for (AppCompatButton btn : phoneticButtons) {
                if (btn.isSelected()) {
                    isPhoneticSelected = true;
                    break;
                }
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
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.GONE);
        }
    }
    private boolean isVietnamese(String text) {
        return text.matches(".*[aàáảãạăắằẳẵặâấầẩẫậeèéẻẽẹêếềểễệiìíỉĩịoòóỏõọôốồổỗộơớờởỡợuùúủũụưứừửữựyỳýỷỹỵđ].*");
    }
    private List<String> getGroupsFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FlashcardPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("group_list", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type); // Chuyển JSON thành danh sách
        }
        return new ArrayList<>(); // Trả về danh sách rỗng nếu không có dữ liệu
    }
    private void showGroupSelectionDialog(List<String> groupNames) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn nhóm flashcard: ");

        String[] items = groupNames.toArray(new String[0]);
        builder.setItems(items, (dialog, which) -> {
            String selectedGroup = groupNames.get(which);
            Toast.makeText(this, "Đã chọn nhóm: " + selectedGroup, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}