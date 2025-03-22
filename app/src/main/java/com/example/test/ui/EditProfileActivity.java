package com.example.test.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EditProfileActivity extends AppCompatActivity {

    TextView backtoProfile;
     static final int PICK_IMAGE = 1;
     ImageView imgAvatar;
     String currentAvatarUrl;
     EditText edtName, edtSdt;
     Spinner spnField;
     FrameLayout btnUpdate;
     UserManager userManager;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupSpinner();

        userManager = new UserManager(this);
        backtoProfile= findViewById(R.id.backtoProfile);

        backtoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        imgAvatar = findViewById(R.id.imgAvatar);
//        imgAvatar.setOnClickListener(v -> selectImage());
        loadUserProfile();
        btnUpdate.setOnClickListener(view -> updateProfile());
    }
    private void initViews() {
        backtoProfile = findViewById(R.id.backtoProfile);
        edtName = findViewById(R.id.edtName);
        edtSdt = findViewById(R.id.edtSdt);
        spnField = findViewById(R.id.spnField);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fields_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnField.setAdapter(adapter);
    }
    private void loadUserProfile() {
        String userId = SharedPreferencesManager.getInstance(this).getID();

        userManager.fetchUserProfile(Integer.parseInt(userId), new ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                runOnUiThread(() -> {
                    try {
                        edtName.setText(result.getString("name"));
                        String phoneNumber = "";
                        if (result.has("phone")) {
                            phoneNumber = result.getString("phone");
                        } else if (result.has("phoneNumber")) {
                            phoneNumber = result.getString("phoneNumber");
                        } else if (result.has("sdt")) {
                            phoneNumber = result.getString("sdt");
                        }
                        edtSdt.setText(phoneNumber);

                        String speciField = result.getString("speciField");
                        ArrayAdapter adapter = (ArrayAdapter) spnField.getAdapter();
                        int position = adapter.getPosition(speciField);
                        if (position >= 0) {
                            spnField.setSelection(position);
                        }

                        // Replace IP address in avatar URL
                        currentAvatarUrl = result.optString("avatar");
                        if (currentAvatarUrl != null && !currentAvatarUrl.isEmpty()) {
                            currentAvatarUrl = currentAvatarUrl.replace("0.0.0.0", "14.225.198.3");
                            Log.d("EditProfile", "Modified avatar URL: " + currentAvatarUrl);

                            imgAvatar = findViewById(R.id.imgAvatar); // Find the ImageView
                            imgAvatar.setOnClickListener(v -> selectImage()); // Set click listener

                            Glide.with(EditProfileActivity.this)
                                    .load(currentAvatarUrl)
                                    .placeholder(R.drawable.img_avt_profile)
                                    .error(R.drawable.img_avt_profile)
                                    .circleCrop()
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.e("EditProfile", "Failed to load avatar: " + e.getMessage());
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d("EditProfile", "Avatar loaded successfully");
                                            return false;
                                        }
                                    })
                                    .into(imgAvatar);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(EditProfileActivity.this,
                                "Error loading profile data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(EditProfileActivity.this,
                            "Failed to load profile: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadAvatar(imageUri);
        }
    }
    private String getRealPathFromURI(Uri uri) {
        // For API 19 and above (MediaStore documents)
        if (uri.getAuthority().equals("com.android.providers.media.documents")) {
            final String docId = uri.getLastPathSegment().split(":")[1];
            final String selection = MediaStore.Images.Media._ID + "=" + docId;

            try (Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA},
                    selection,
                    null,
                    null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    return cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                Log.e("EditProfile", "Error getting path: " + e.getMessage());
            }
        }
        // Fallback to old method for other URIs
        else {
            try {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    cursor.close();
                    return path;
                }
            } catch (Exception e) {
                Log.e("EditProfile", "Error getting path: " + e.getMessage());
            }
        }
        return null;
    }
    private void uploadAvatar(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading avatar...");
        progressDialog.show();

        try {
            // Create temp file
            File tempFile = new File(getCacheDir(), "temp_avatar.jpg");

            // Copy image data to temp file
            try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
                 OutputStream outputStream = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Upload the temp file
            String userId = SharedPreferencesManager.getInstance(this).getID();
            userManager.uploadAvatar(Integer.parseInt(userId), tempFile, new ApiCallback<String>() {
                @Override
                public void onSuccess(String newAvatarUrl) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        if (newAvatarUrl != null && !newAvatarUrl.isEmpty()) {
                            String modifiedUrl = newAvatarUrl.replace("0.0.0.0", "14.225.198.3");
                            Glide.with(EditProfileActivity.this)
                                    .load(modifiedUrl)
                                    .placeholder(R.drawable.img_avt_profile)
                                    .error(R.drawable.img_avt_profile)
                                    .circleCrop()
                                    .into(imgAvatar);

                            Toast.makeText(EditProfileActivity.this,
                                    "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        tempFile.delete(); // Clean up temp file
                    });
                }

                @Override
                public void onSuccess() {
                    // Not used
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this,
                                "Failed to update avatar: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                        tempFile.delete(); // Clean up temp file
                    });
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void updateProfile() {
        String userId = SharedPreferencesManager.getInstance(this).getID();
        String name = edtName.getText().toString().trim();
        String phone = edtSdt.getText().toString().trim();
        String specialField = spnField.getSelectedItem().toString();

        if (name.isEmpty()) {
            edtName.setError("Name is required");
            return;
        }

        btnUpdate.setEnabled(false);
        Toast.makeText(this, "Updating profile...", Toast.LENGTH_SHORT).show();

        userManager.updateProfile(Integer.parseInt(userId), name, phone, specialField, new ApiCallback<Object>() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(EditProfileActivity.this,
                            "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    btnUpdate.setEnabled(true);

                    finish();
                });
            }

            @Override
            public void onSuccess(Object result) {
                // Not used in this case
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(EditProfileActivity.this,
                            "Update failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    btnUpdate.setEnabled(true);
                });
            }
        });
    }
}