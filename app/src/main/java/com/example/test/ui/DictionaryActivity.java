package com.example.test.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
                showDefinitionDialog(word);
            }
        });

        dicBacktoExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void showDefinitionDialog(String word) {
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
                    List<AppCompatButton> definitionButtons = new ArrayList<>();
                    List<AppCompatButton> meaningButtons = new ArrayList<>();

                    // Hiển thị phonetics
                    if (wordData.getPhonetics() != null && !wordData.getPhonetics().isEmpty()) {
                        for (Phonetic phonetic : wordData.getPhonetics()) {
                            AppCompatButton btn = new AppCompatButton(DictionaryActivity.this);
                            btn.setText(phonetic.getText());
                            btn.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            btn.setBackgroundResource(R.drawable.btn_item_def_click);
                            btn.setTextColor(ContextCompat.getColor(DictionaryActivity.this, R.color.black));
                            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            btn.setPadding(40, 10, 20, 10);
                            btn.setTag(false);
                            btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

                            btn.setOnClickListener(v -> {
                                for (AppCompatButton otherBtn : phoneticButtons) {
                                    otherBtn.setBackgroundResource(R.drawable.btn_item_def_click);
                                    otherBtn.setTag(false);
                                }
                                btn.setBackgroundResource(R.drawable.item_phonetic_selected);
                                btn.setTag(true);
                            });

                            phoneticButtons.add(btn);
                            phoneticContainer.addView(btn);
                        }
                    } else {
                        phoneticContainer.addView(new androidx.appcompat.widget.AppCompatTextView(DictionaryActivity.this) {{
                            setText("No phonetics available");
                        }});
                    }

                    // Hiển thị Part of Speech
                    if (wordData.getMeanings() != null) {
                        for (Meaning meaning : wordData.getMeanings()) {
                            if (meaning.getPartOfSpeech() != null) {
                                AppCompatButton btn = new AppCompatButton(DictionaryActivity.this);
                                btn.setText(meaning.getPartOfSpeech());
                                btn.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));

                                btn.setTextColor(ContextCompat.getColor(DictionaryActivity.this, R.color.black));
                                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                btn.setPadding(20, 10, 20, 10);
                                btn.setTag(false);
                                btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

                                btn.setOnClickListener(v -> {
                                    updateDefinitions(definitionContainer, meaning, contentView,
                                            phoneticButtons, definitionButtons, meaningButtons);
                                });

                                partOfSpeechContainer.addView(btn);
                            }
                        }

                        if (!wordData.getMeanings().isEmpty()) {
                            updateDefinitions(definitionContainer, wordData.getMeanings().get(0), contentView,
                                    phoneticButtons, definitionButtons, meaningButtons);
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
                                   List<AppCompatButton> meaningButtons) {

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
                btn.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                btn.setBackgroundResource(R.drawable.item_definition);
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
                        otherBtn.setBackgroundResource(R.drawable.item_definition);
                        otherBtn.setTag(false);
                    }
                    btn.setBackgroundResource(R.drawable.item_definition_selected);
                    btn.setTag(true);
                    //checkEnableDone(phoneticButtons, definitionButtons, meaningButtons, btnDone);
                });

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