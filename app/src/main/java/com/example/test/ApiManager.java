package com.example.test;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
/*
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;*/

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiManager {
    private OkHttpClient client;

    public ApiManager() {
        client = new OkHttpClient();
    }

    public void sendLoginRequest(String email, String password, ApiCallback callback) {
        String json = "{ \"username\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.109.2:8080/login") // Thay bằng URL máy chủ của bạn
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

    public void sendSignUpRequest(String name, String phone, String email, String password, ApiCallback callback) {
        String json = "{ \"name\": \"" + name + "\", \"phone\": \"" + phone + "\", \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.109.2:8080/register") // Thay bằng URL máy chủ của bạn
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
                    callback.onFailure("Đăng ký thất bại! Vui lòng kiểm tra lại thông tin.");
                }
            }
        });
    }

    /*// Phương thức để lấy danh sách câu hỏi từ API
    public void fetchQuestionsFromApi(ApiCallback callback) {
        Request request = new Request.Builder()
                .url("http://192.168.109.2:8080/get_questions") // Địa chỉ API để lấy câu hỏi
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
                    Gson gson = new Gson();
                    Type questionListType = new TypeToken<List<Question>>(){}.getType();
                    List<Question> questions = gson.fromJson(responseBody, questionListType);
                    callback.onQuestionsSuccess(questions);
                } else {
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }
        });
    }

    // Phương thức để lấy danh sách câu trả lời từ API
    public void fetchAnswersFromApi(int questionId, ApiCallback callback) {
        Request request = new Request.Builder()
                .url("http://192.168.109.2:8080/get_answers?question_id=" + questionId) // Địa chỉ API để lấy câu trả lời của một câu hỏi
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
                    Gson gson = new Gson();
                    Type answerListType = new TypeToken<List<Answer>>(){}.getType();
                    List<Answer> answers = gson.fromJson(responseBody, answerListType);
                    callback.onAnswersSuccess(answers);
                } else {
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }
        });
    }

    // Phương thức để lấy điểm từ API
    public void fetchPointsFromApi(ApiCallback callback) {
        Request request = new Request.Builder()
                .url("http://192.168.109.2:8080/get_points") // Địa chỉ API để lấy điểm
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
                    Gson gson = new Gson();
                    Type pointListType = new TypeToken<List<Point>>(){}.getType();
                    List<Point> points = gson.fromJson(responseBody, pointListType);
                    callback.onPointsSuccess(points);
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
