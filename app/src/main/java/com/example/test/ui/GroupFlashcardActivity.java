package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.response.ApiResponseFlashcard;
import com.example.test.ui.explore.ExploreActivity;
import com.example.test.ui.explore.ExploreFragment;
import com.example.test.api.FlashcardApiCallback;
import com.example.test.api.FlashcardManager;
import com.example.test.model.FlashcardGroup;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.FlashcardGroupResponse;
import java.util.List;

public class GroupFlashcardActivity extends AppCompatActivity {

    // AppCompatButton groupFlcid;
    TextView backtoExplore;
    ImageView btnaddgroup;
    LinearLayout groupContainer;
    private FlashcardManager flashcardManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_flashcard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // groupFlcid = findViewById(R.id.groupFlcid);
        backtoExplore = findViewById(R.id.flBacktoExplore);
        btnaddgroup = findViewById(R.id.btnAddGroup);
        groupContainer = findViewById(R.id.groupContainer);
        flashcardManager = new FlashcardManager();

        btnaddgroup.setOnClickListener(view -> showAddGroupDialog());

        // groupFlcid.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View view) {
        // Intent intent = new Intent(GroupFlashcardActivity.this,
        // FLashcardActivity.class);
        // startActivity(intent);
        // finish();
        // }
        // });
        backtoExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent= new Intent(GroupFlashcardActivity.this,
                // ExploreFragment.class);
                // startActivity(intent);
                finish();

            }
        });

        fetchFlashcardGroups();
    }

    private void showAddGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_group, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText edtGroupName = dialogView.findViewById(R.id.edtGroupName);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnAdd.setEnabled(false);
        btnAdd.setAlpha(0.5f); // Làm mờ nút Add

        // Lắng nghe sự thay đổi của EditText
        edtGroupName.addTextChangedListener(new TextWatcher() {
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
            String groupName = edtGroupName.getText().toString().trim();
            if (!groupName.isEmpty()) {
                int userId = 1; // Thay đổi ID người dùng nếu cần
                flashcardManager.createFlashcardGroup(groupName, userId, new FlashcardApiCallback() {
                    @Override
                    public void onSuccess(ApiResponseFlashcardGroup response) {
                        runOnUiThread(() -> { // Đảm bảo cập nhật UI trên UI thread
                            FlashcardGroup newGroup = response.getData(); // Đảm bảo rằng getData() trả về
                                                                          // FlashcardGroup
                            addGroupButton(newGroup.getName(), newGroup.getId()); // Sử dụng tên nhóm từ phản hồi
                            fetchFlashcardGroups(); // Cập nhật danh sách nhóm
                            dialog.dismiss(); // Đóng hộp thoại
                        });
                    }

                    @Override
                    public void onSuccess(FlashcardGroupResponse response) {
                        // Không làm gì ở đây
                    }

                    @Override
                    public void onSuccess(ApiResponseFlashcard response) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("GroupFlashcardActivity", "Error creating group: " + errorMessage);
                        runOnUiThread(() -> {
                            Toast.makeText(GroupFlashcardActivity.this, "Error creating group: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        });

        dialog.show();
    }

    private void fetchFlashcardGroups() {
        flashcardManager.fetchFlashcardGroups(1, 1, new FlashcardApiCallback() {
            @Override
            public void onSuccess(ApiResponseFlashcardGroup response) {
                // Không làm gì ở đây
            }

            @Override
            public void onSuccess(FlashcardGroupResponse response) {
                runOnUiThread(() -> { // Đảm bảo cập nhật UI trên UI thread
                    // Xóa các nút cũ trước khi cập nhật danh sách
                    groupContainer.removeAllViews();

                    // Kiểm tra xem có dữ liệu không
                    if (response.getData() != null && response.getData().getContent() != null) {
                        List<FlashcardGroup> groups = response.getData().getContent();
                        for (FlashcardGroup group : groups) {
                            addGroupButton(group.getName(), group.getId());
                        }
                    }
                });
            }

            @Override
            public void onSuccess(ApiResponseFlashcard response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("GroupFlashcardActivity", "Error fetching groups: " + errorMessage);
            }
        });
    }

    private void addGroupButton(String groupName, int groupId) {
        LinearLayout layoutFlashcards = findViewById(R.id.groupContainer);

        AppCompatButton newGroup = new AppCompatButton(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        newGroup.setText(groupName);
        newGroup.setTextSize(22);
        newGroup.setPaddingRelative(16, 16, 16, 16);
        newGroup.setPadding(16, 16, 16, 16);

        newGroup.setGravity(Gravity.CENTER);
        newGroup.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_flash));

        newGroup.setLayoutParams(params);
        newGroup.setOnClickListener(v -> {
            Intent intent = new Intent(GroupFlashcardActivity.this, FlashcardActivity.class);
            intent.putExtra("GROUP_ID", groupId); // Gửi ID nhóm
            startActivity(intent);
        });

        layoutFlashcards.addView(newGroup);
    }
}