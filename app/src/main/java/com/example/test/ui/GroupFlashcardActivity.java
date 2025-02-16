package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.test.response.ApiResponseOneFlashcard;
import com.example.test.ui.explore.ExploreActivity;
import com.example.test.ui.explore.ExploreFragment;
import com.example.test.api.FlashcardApiCallback;
import com.example.test.api.FlashcardManager;
import com.example.test.model.FlashcardGroup;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.FlashcardGroupResponse;
import java.util.List;

import java.util.ArrayList;

public class GroupFlashcardActivity extends AppCompatActivity {

    AppCompatButton groupFlcid;
    TextView backtoExplore;
    ImageView btnaddgroup;
    LinearLayout groupContainer;
    private FlashcardManager flashcardManager;
    private final ArrayList<AppCompatButton> groupButtons = new ArrayList<>();

    @SuppressLint({ "MissingInflatedId", "ClickableViewAccessibility" })
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

        groupFlcid = findViewById(R.id.groupFlcid);
        backtoExplore = findViewById(R.id.flBacktoExplore);
        btnaddgroup = findViewById(R.id.btnAddGroup);
        groupContainer = findViewById(R.id.groupContainer);
        flashcardManager = new FlashcardManager();

        btnaddgroup.setOnClickListener(view -> showAddGroupDialog());

        groupFlcid.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable[] drawables = groupFlcid.getCompoundDrawablesRelative();
                if (drawables[2] != null) { // Kiểm tra drawableEnd có tồn tại không
                    int drawableWidth = drawables[2].getBounds().width();
                    int buttonWidth = groupFlcid.getWidth();
                    int touchX = (int) event.getX();

                    // Kiểm tra xem người dùng có chạm vào drawableEnd không
                    if (touchX >= (buttonWidth - drawableWidth - groupFlcid.getPaddingEnd())) {
                        int groupId = (int) groupFlcid.getTag(); // Retrieve the groupId from the button's tag
                        showEditGroupDialog(groupFlcid, groupId); // Pass the groupId
                        return true;
                    }
                }
            }
            return false;
        });

        groupFlcid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupFlashcardActivity.this, FlashcardActivity.class);
                startActivity(intent);
            }
        });
        backtoExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent= new Intent(GroupFlashcardActivity.this,
                // ExploreFragment.class);
                // startActivity(intent);
            }
        });

        fetchFlashcardGroups();
    }

    private void showEditGroupDialog(Button groupButton, int groupId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_groupflash, null);

        EditText edtEditGroupName = dialogView.findViewById(R.id.edtEditGroupName);
        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        edtEditGroupName.setText(groupButton.getText().toString().trim());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Bật/tắt nút "Edit" nếu có thay đổi trong EditText
        edtEditGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnEdit.setEnabled(
                        !s.toString().trim().isEmpty() && !s.toString().equals(groupButton.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Sự kiện khi nhấn "Edit" (Cập nhật tên nhóm)
        btnEdit.setOnClickListener(v -> {
            String newName = edtEditGroupName.getText().toString().trim();

            // Gọi API để cập nhật tên nhóm
            flashcardManager.updateFlashcardGroup(groupId, newName, new FlashcardApiCallback() {
                @Override
                public void onSuccess(ApiResponseFlashcardGroup response) {
                    runOnUiThread(() -> {
                        groupButton.setText(newName);
                        dialog.dismiss();
                        Toast.makeText(GroupFlashcardActivity.this, "Group updated successfully", Toast.LENGTH_SHORT)
                                .show();
                    });
                }

                @Override
                public void onSuccess(FlashcardGroupResponse response) {

                }

                @Override
                public void onSuccess(ApiResponseFlashcard response) {

                }

                @Override
                public void onSuccess(ApiResponseOneFlashcard response) {

                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(GroupFlashcardActivity.this, "Error updating group: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        // Sự kiện khi nhấn "Delete" (Xóa nhóm)
        btnDelete.setOnClickListener(v -> {
            // Gọi API để xóa nhóm
            flashcardManager.deleteFlashcardGroup(groupId, new FlashcardApiCallback() {
                @Override
                public void onSuccess(ApiResponseFlashcardGroup response) {
                    runOnUiThread(() -> {
                        groupContainer.removeView(groupButton);
                        dialog.dismiss();
                        Toast.makeText(GroupFlashcardActivity.this, "Group deleted successfully", Toast.LENGTH_SHORT)
                                .show();
                        fetchFlashcardGroups();
                    });
                }

                @Override
                public void onSuccess(FlashcardGroupResponse response) {

                }

                @Override
                public void onSuccess(ApiResponseFlashcard response) {

                }

                @Override
                public void onSuccess(ApiResponseOneFlashcard response) {

                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(GroupFlashcardActivity.this, "Error deleting group: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
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

        // Ban đầu disable nút Add
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
                    public void onSuccess(ApiResponseOneFlashcard response) {

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
            public void onSuccess(ApiResponseOneFlashcard response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("GroupFlashcardActivity", "Error fetching groups: " + errorMessage);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addGroupButton(String groupName, int groupId) {
        LinearLayout layoutFlashcards = findViewById(R.id.groupContainer);

        // Tạo một LinearLayout cho mỗi nhóm
        LinearLayout groupLayout = new LinearLayout(this);
        groupLayout.setOrientation(LinearLayout.HORIZONTAL); // Đặt orientation là horizontal
        groupLayout.setGravity(Gravity.CENTER_VERTICAL); // Căn giữa theo chiều dọc
        groupLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Nút nhóm
        AppCompatButton newGroup = new AppCompatButton(this);
        newGroup.setText(groupName);
        newGroup.setTextSize(22);
        newGroup.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_flash));
        newGroup.setLayoutParams(new LinearLayout.LayoutParams(
                0, // Chiều rộng sẽ được điều chỉnh
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1)); // Tỉ lệ trọng số
        newGroup.setPaddingRelative(16, 16, 16, 16);
        newGroup.setPadding(16, 16, 16, 16);

        // Tạo ImageView cho biểu tượng chỉnh sửa
        ImageView editIcon = new ImageView(this);
        editIcon.setImageResource(R.drawable.icon_edit); // Thay đổi drawable theo nhu cầu
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(48, 48); // Kích thước biểu tượng
        params.setMargins(16, 0, 0, 0); // Thêm khoảng cách bên trái
        editIcon.setLayoutParams(params);

        // Lưu ID nhóm vào tag của nút
        newGroup.setTag(groupId);

        // Sự kiện khi nhấn vào nút nhóm
        newGroup.setOnClickListener(v -> {
            Intent intent = new Intent(GroupFlashcardActivity.this, FlashcardActivity.class);
            intent.putExtra("GROUP_ID", groupId);
            startActivity(intent);
        });

        // Sự kiện khi nhấn vào biểu tượng chỉnh sửa
        editIcon.setOnClickListener(v -> {
            showEditGroupDialog(newGroup, groupId); // Mở cửa sổ chỉnh sửa
        });

        // Thêm nút nhóm và biểu tượng chỉnh sửa vào layout
        groupLayout.addView(newGroup);
        groupLayout.addView(editIcon);
        layoutFlashcards.addView(groupLayout);
    }
}