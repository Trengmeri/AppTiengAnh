package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sign_up extends AppCompatActivity {

    EditText edtName, edtPhone, edtEmail, edtMKhau;
    CheckBox cbCheck;
    Button btnUp, btnIn;
    NetworkChangeReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        AnhXa();

        // Lắng nghe trạng thái checkbox
        cbCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Bật hoặc tắt nút Sign Up dựa trên trạng thái checkbox
            btnUp.setEnabled(isChecked);
            btnUp.setBackgroundColor(ContextCompat.getColor(this, R.color.btncolor));

        });

        // Tạo đối tượng NetworkChangeReceiver
        networkReceiver = new NetworkChangeReceiver();

        btnUp.setOnClickListener(view -> {
            if (!isInternetAvailable()) {
                Toast.makeText(Sign_up.this, "Vui lòng kiểm tra kết nối Internet của bạn.", Toast.LENGTH_LONG).show();
            } else {
                // Thực hiện yêu cầu nếu có Internet
                String hoten = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String soDT = edtPhone.getText().toString();
                String pass = edtMKhau.getText().toString();

                if (hoten.isEmpty() || email.isEmpty() || soDT.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(Sign_up.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(email) || !isValidPhoneNumber(soDT)) {
                    Toast.makeText(Sign_up.this, "Email hoặc số điện thoại không đúng định dạng", Toast.LENGTH_LONG).show();
                } else if (!isValidPassword(pass)) {
                    Toast.makeText(Sign_up.this, "Mật khẩu ít nhất 8 ký tự gồm chữ hoa, chữ thường, số và ký tự đặc biệt", Toast.LENGTH_LONG).show();
                } else {
                    sendSignUpRequest(hoten, soDT, email, pass);
                }
            }
        });

        btnIn.setOnClickListener(view -> {
            Intent intent = new Intent(Sign_up.this, Sign_In.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Đăng ký BroadcastReceiver
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Hủy đăng ký BroadcastReceiver
        unregisterReceiver(networkReceiver);
    }

    private void AnhXa() {
        edtEmail = findViewById(R.id.edtPass);
        edtName = findViewById(R.id.edtTen);
        edtPhone = findViewById(R.id.edtSdt);
        edtMKhau = findViewById(R.id.edtMKhau);
        cbCheck = findViewById(R.id.cbCheck);
        btnUp = findViewById(R.id.btnUp);
        btnIn = findViewById(R.id.btnIn);
        // Vô hiệu hóa button ban đầu
        btnUp.setEnabled(false);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phonePattern = "^[0-9]{10,11}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!_&*]).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void sendSignUpRequest(String name, String phone, String email, String password) {
        OkHttpClient client = new OkHttpClient();

        // Tạo JSON chứa dữ liệu đăng ký
        String json = "{ \"name\": \"" + name + "\", \"phone\": \"" + phone + "\", \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        // Tạo Request gửi đến máy chủ
        Request request = new Request.Builder()
                .url("http://192.168.109.2:8080/users") // Thay URL máy chủ thực tế
                .post(body)
                .build();

        // Thực thi yêu cầu
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Sign_up", "Kết nối thất bại: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(Sign_up.this, "Kết nối thất bại! Không thể kết nối tới API.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("Sign_up", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Sign_up.this, "Đăng ký thành công! Vui lòng kiểm tra email của bạn.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Sign_up.this, ConfirmCode2.class);
                        startActivity(intent);
                        finish(); // Đảm bảo màn hình đăng ký bị hủy
                    });
                } else {
                    Log.e("Sign_up", "Lỗi từ server: Mã lỗi " + response.code() + ", Nội dung: " + responseBody);
                    runOnUiThread(() -> Toast.makeText(Sign_up.this, "Đăng ký thất bại! Vui lòng kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        }
        return false;
    }

    // BroadcastReceiver để lắng nghe thay đổi trạng thái mạng
    public static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo == null || !networkInfo.isConnected()) {
                Toast.makeText(context, "Không có Internet. Vui lòng kiểm tra kết nối.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
