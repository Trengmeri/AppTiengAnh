package com.example.test;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.widget.Toast;
/*
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;*/

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiManager {
    private OkHttpClient client;
    private String email;
    public ApiManager() {}

    public void sendLoginRequest(String email, String password, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Tăng thời gian kết nối
                .readTimeout(20, TimeUnit.SECONDS)    // Tăng thời gian chờ phản hồi
                .writeTimeout(10, TimeUnit.SECONDS)   // Tăng thời gian ghi dữ liệu
                .build();
        String json = "{ \"username\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.56.1:8080/login") // Thay bằng URL máy chủ của bạn
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiManager", "Kết nối thất bại: " + e.getMessage());
                callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("ApiManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    Log.e("ApiManager", "Lỗi từ server: Mã lỗi " + response.code() + ", Nội dung: " + responseBody);
                    callback.onFailure("Đăng nhập thất bại! Vui lòng kiểm tra lại thông tin.");
                }
            }
        });
    }
//{ \"name\": \"" + name + "\", \"phone\": \"" + phone + "\", \"email\": \"" + email + "\", \"password\": \"" + password + "\" }
    public void sendSignUpRequest(Context context,String name, String email, String password, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Tăng thời gian kết nối
                .readTimeout(20, TimeUnit.SECONDS)    // Tăng thời gian chờ phản hồi
                .writeTimeout(10, TimeUnit.SECONDS)   // Tăng thời gian ghi dữ liệu
                .build();

        String json = "{ \"name\": \"" + name + "\", \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.56.1:8080/register") // Thay bằng URL máy chủ của bạn
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiManager", "Kết nối thất bại: " + e.getMessage());
                callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
//                if (e instanceof java.net.SocketTimeoutException) {
//                    Log.e("ApiManager", "Kết nối timeout: " + e.getMessage());
//                    callback.onFailure("Thời gian kết nối đã hết. Vui lòng thử lại.");
//                } else if (e instanceof java.net.UnknownHostException) {
//                    Log.e("ApiManager", "Không tìm thấy máy chủ: " + e.getMessage());
//                    callback.onFailure("Không thể tìm thấy máy chủ. Kiểm tra lại URL hoặc kết nối mạng.");
//                } else if (e instanceof java.net.ConnectException) {
//                    Log.e("ApiManager", "Không kết nối được tới server: " + e.getMessage());
//                    callback.onFailure("Không thể kết nối tới server. Kiểm tra xem server có hoạt động không.");
//                } else {
//                    Log.e("ApiManager", "Lỗi không xác định: " + e.getMessage(), e);
//                    callback.onFailure("Lỗi không xác định: " + e.getMessage());
//                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("ApiManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email); // Lưu email
                    editor.apply();

                } else {
                    Log.e("ApiManager", "Lỗi từ server: Mã lỗi " + response.code() + ", Nội dung: " + responseBody);
                    callback.onFailure("Đăng ký thất bại! Vui lòng kiểm tra lại thông tin.");
                }
            }
        });
    }
    //email = getIntent().getStringExtra("email");
    public void sendConfirmCodeRequest(String email,String code, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Tăng thời gian kết nối
                .readTimeout(20, TimeUnit.SECONDS)    // Tăng thời gian chờ phản hồi
                .writeTimeout(10, TimeUnit.SECONDS)   // Tăng thời gian ghi dữ liệu
                .build();

        String json = "{ \"email\": \"" + email + "\", \"otp\": \"" + code + "\" }";

        Log.d("ConfirmCode", "Email: " + email + ", OTP: " + code);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.56.1:8080/verify-otp") // Thay bằng URL máy chủ của bạn
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.e("ApiManager", "Kết nối thất bại: " + e.getMessage());
//                callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
                if (e instanceof SocketTimeoutException) {
                    Log.e("ApiManager", "Kết nối timeout: " + e.getMessage());
                    callback.onFailure("Thời gian kết nối đã hết. Vui lòng thử lại.");
                } else if (e instanceof UnknownHostException) {
                    Log.e("ApiManager", "Không tìm thấy máy chủ: " + e.getMessage());
                    callback.onFailure("Không thể tìm thấy máy chủ. Kiểm tra lại URL hoặc kết nối mạng.");
                } else if (e instanceof ConnectException) {
                    Log.e("ApiManager", "Không kết nối được tới server: " + e.getMessage());
                    callback.onFailure("Không thể kết nối tới server. Kiểm tra xem server có hoạt động không.");
                } else {
                    Log.e("ApiManager", "Lỗi không xác định: " + e.getMessage(), e);
                    callback.onFailure("Lỗi không xác định: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("ApiManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess(); // Gọi callback thành công
                } else {
//                    Log.e("ApiManager", "Lỗi từ server: Mã lỗi " + response.code() + ", Nội dung: " + responseBody);
//                    callback.onFailure("Mã OTP sai! Vui lòng kiểm tra lại.");
                    if (!response.isSuccessful()) {
                        JSONObject errorJson = null; // Parse nội dung phản hồi
                        try {
                            errorJson = new JSONObject(responseBody);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        String errorMessage = errorJson.optString("message", "Mã OTP sai! Vui lòng kiểm tra lại.");
                        callback.onFailure(errorMessage);
                    }
                }
            }
        });
    }

    /*// Phương thức để lấy nội dung câu hỏi từ API
    public void fetchQuestionContentFromApi(ApiCallback callback) {
        Request request = new Request.Builder()
                .url("http://192.168.109.2:8080/get_question_content") // Địa chỉ API để lấy nội dung câu hỏi
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiManager", "Lỗi kết nối: " + e.getMessage());
                callback.onFailure("Không thể kết nối tới API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("ApiManager", "Phản hồi từ server: " + responseBody);

                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody); // Gọi callback với nội dung câu hỏi
                } else {
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }
        });
    }*/

    // Kiểm tra kết nối Internet
    public boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
