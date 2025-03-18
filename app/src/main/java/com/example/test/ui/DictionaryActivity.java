package com.example.test.ui;

import android.annotation.SuppressLint;
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

import com.example.test.R;
import com.example.test.api.AddFlashCardApiCallback;
import com.example.test.api.FlashcardManager;
import com.example.test.model.Definition;
import com.example.test.model.Meaning;
import com.example.test.model.Phonetic;
import com.example.test.model.WordData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    EditText edtWord;
    ImageView btnFind;
    LinearLayout wordContainer;
    TextView dicBacktoExplore;
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
        flashcardManager = new FlashcardManager();

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = edtWord.getText().toString().trim();
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

                    List<AppCompatButton> phoneticButtons = new ArrayList<>();
                    List<AppCompatButton> speechButtons = new ArrayList<>();
                    List<AppCompatButton> definitionButtons = new ArrayList<>();

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
        ScrollView definitionScrollView = dialogView.findViewById(R.id.definitionScrollView);

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
}