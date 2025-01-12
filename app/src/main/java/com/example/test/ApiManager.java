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

import com.example.test.model.ApiResponse;
import com.example.test.model.Choice;
import com.example.test.model.Question;
import com.example.test.model.Choice;
import com.example.test.model.Choice;
import com.example.test.model.ResponseWrapper;
import com.example.test.model.TokenResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
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

    public ApiManager() {
    }

    public void sendLoginRequest(String email, String password, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS) // Tăng thời gian kết nối
                .readTimeout(20, TimeUnit.SECONDS) // Tăng thời gian chờ phản hồi
                .writeTimeout(10, TimeUnit.SECONDS) // Tăng thời gian ghi dữ liệu
                .build();
        String json = "{ \"username\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.56.1:8080/api/v1/auth/login") // Thay bằng URL máy chủ của bạn
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
                    // Gson gson = new Gson();
                    // TokenResponse tokenResponse = gson.fromJson(responseBody,
                    // TokenResponse.class);
                    // String access_token= tokenResponse.getAccess_token();

                    // callback.onSuccess(access_token);
                    callback.onSuccess();
                } else {
                    Log.e("ApiManager", "Lỗi từ server: Mã lỗi " + response.code() + ", Nội dung: " + responseBody);
                    callback.onFailure("Đăng nhập thất bại! Vui lòng kiểm tra lại thông tin.");
                }
                // if (response.isSuccessful()) {
                // try {
                // // Phân tích JSON từ phản hồi của server
                // Gson gson = new Gson();
                // TokenResponse tokenResponse = gson.fromJson(responseBody,
                // TokenResponse.class);
                // String access_token = tokenResponse.getAccess_token(); // Lấy access_token
                // thay vì token
                //
                // // Kiểm tra nếu access_token hợp lệ
                // if (access_token != null && !access_token.isEmpty()) {
                // Log.d("ApiManager", "Access Token hợp lệ: " + access_token);
                // callback.onSuccess(access_token); // Trả về access_token cho callback
                // } else {
                // Log.e("ApiManager", "Access token không có trong phản hồi.");
                // callback.onFailure("Access token không hợp lệ.");
                // }
                // } catch (JsonSyntaxException e) {
                // Log.e("ApiManager", "Lỗi khi phân tích JSON: " + e.getMessage());
                // callback.onFailure("Lỗi khi xử lý dữ liệu từ server.");
                // }
                // } else {
                // Log.e("ApiManager", "Lỗi từ server: Mã lỗi " + response.code() + ", Nội dung:
                // " + responseBody);
                // callback.onFailure("Đăng nhập thất bại! Vui lòng kiểm tra lại thông tin.");
                // }
            }
        });
    }

    // { \"name\": \"" + name + "\", \"phone\": \"" + phone + "\", \"email\": \"" +
    // email + "\", \"password\": \"" + password + "\" }
    public void sendSignUpRequest(Context context, String name, String email, String password, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Tăng thời gian kết nối
                .readTimeout(20, TimeUnit.SECONDS) // Tăng thời gian chờ phản hồi
                .writeTimeout(10, TimeUnit.SECONDS) // Tăng thời gian ghi dữ liệu
                .build();

        String json = "{ \"name\": \"" + name + "\", \"email\": \"" + email + "\", \"password\": \"" + password
                + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.56.1:8080/api/v1/auth/register") // Thay bằng URL máy chủ của bạn
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiManager", "Kết nối thất bại: " + e.getMessage());
                callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
                // if (e instanceof java.net.SocketTimeoutException) {
                // Log.e("ApiManager", "Kết nối timeout: " + e.getMessage());
                // callback.onFailure("Thời gian kết nối đã hết. Vui lòng thử lại.");
                // } else if (e instanceof java.net.UnknownHostException) {
                // Log.e("ApiManager", "Không tìm thấy máy chủ: " + e.getMessage());
                // callback.onFailure("Không thể tìm thấy máy chủ. Kiểm tra lại URL hoặc kết nối
                // mạng.");
                // } else if (e instanceof java.net.ConnectException) {
                // Log.e("ApiManager", "Không kết nối được tới server: " + e.getMessage());
                // callback.onFailure("Không thể kết nối tới server. Kiểm tra xem server có hoạt
                // động không.");
                // } else {
                // Log.e("ApiManager", "Lỗi không xác định: " + e.getMessage(), e);
                // callback.onFailure("Lỗi không xác định: " + e.getMessage());
                // }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("ApiManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs",
                            Context.MODE_PRIVATE);
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

    // email = getIntent().getStringExtra("email");
    public void sendConfirmCodeRequest(String email, String code, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Tăng thời gian kết nối
                .readTimeout(20, TimeUnit.SECONDS) // Tăng thời gian chờ phản hồi
                .writeTimeout(10, TimeUnit.SECONDS) // Tăng thời gian ghi dữ liệu
                .build();

        String json = "{ \"email\": \"" + email + "\", \"otp\": \"" + code + "\" }";

        Log.d("ConfirmCode", "Email: " + email + ", OTP: " + code);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.56.1:8080/api/v1/auth/verify-otp") // Thay bằng URL máy chủ của bạn
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log.e("ApiManager", "Kết nối thất bại: " + e.getMessage());
                // callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
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
                    // Log.e("ApiManager", "Lỗi từ server: Mã lỗi " + response.code() + ", Nội dung:
                    // " + responseBody);
                    // callback.onFailure("Mã OTP sai! Vui lòng kiểm tra lại.");
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

    //
    // public void sendQuestionDataToApi(String quesContent, String quesType, int
    // point, String[] choices, String answer, ApiCallback callback) {
    // // Xây dựng JSON từ các tham số
    // String json = "{ \"quesContent\": \"" + quesContent + "\", " +
    // "\"quesType\": \"" + quesType + "\", " +
    // "\"point\": " + point + ", " +
    // "\"choiceContent\": \"" + String.join(",", choices) + "\", " +
    // "\"choiceKey\": \"" + answer + "\" }";
    // RequestBody requestBody =
    // RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
    //
    // Request request = new Request.Builder()
    // .url("http://192.168.109.2:8080/your_endpoint") // Địa chỉ API
    // .post(requestBody)
    // .build();
    //
    // // Thực hiện yêu cầu
    // client.newCall(request).enqueue(new Callback() {
    // @Override
    // public void onFailure(Call call, IOException e) {
    // Log.e("ApiManager", "Lỗi kết nối: " + e.getMessage());
    // callback.onFailure("Không thể kết nối tới API.");
    // }
    //
    // @Override
    // public void onResponse(Call call, Response response) throws IOException {
    // String responseBody = response.body().string();
    // Log.d("ApiManager", "Phản hồi từ server: " + responseBody);
    // // Xử lý phản hồi từ server (parse JSON)
    // if (response.isSuccessful()) {
    // callback.onSuccess(); // Gọi callback thành công
    // } else {
    // callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
    // }
    // }
    // });
    // }

    // Phương thức để lấy dữ liệu câu hỏi từ API (GET request)
    public void fetchQuestionContentFromApi(ApiCallback callback) {
        // String access_token =
        // "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2YW5raWV0bWFzdGVyY3Y0QGdtYWlsLmNvbSIsImV4cCI6MTc0NTMwNzEyMCwiaWF0IjoxNzM2NjY3MTIwLCJ1c2VyIjp7ImlkIjo1LCJlbWFpbCI6InZhbmtpZXRtYXN0ZXJjdjRAZ21haWwuY29tIiwibmFtZSI6IlZhbiBLaWV0In19.K_906ifZ2fQEMxkPEPERaLY7Gh-VTyjvoUae6CEjnLkmR-vyleeraJuAzfEvzMfgsMwwniTBntAIBQP_p9HgFA";
        // // Token mà bạn đã nhận được sau khi đăng nhập thành công
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS) // Tăng thời gian kết nối
                .readTimeout(30, TimeUnit.SECONDS) // Tăng thời gian chờ phản hồi
                .writeTimeout(20, TimeUnit.SECONDS) // Tăng thời gian ghi dữ liệu
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.56.1:8080/api/v1/questions/5")// Địa chỉ API lấy câu hỏi
                // .addHeader("Authorization", "Bearer " + access_token)
                .build();

        // Thực hiện yêu cầu GET
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Kiểm tra body trả về
                    String responseBody = response.body() != null ? response.body().string() : null;

                    if (responseBody != null && !responseBody.isEmpty()) {
                        Log.d("ApiManager", "JSON trả về: " + responseBody);

                        try {
                            // Parse JSON thành đối tượng Question
                            // Gson gson = new Gson();
                            // Question question = gson.fromJson(responseBody, Question.class);
                            Gson gson = new Gson();
                            ApiResponse apiResponse = gson.fromJson(responseBody, ApiResponse.class);
                            Question question = apiResponse.getData(); // Lấy đối tượng Question từ ApiResponse

                            if (question != null && question.getQuestionChoices() != null) {
                                Log.d("ApiManager", "Câu hỏi: " + question.getQuesContent());
                                Log.d("ApiManager", "Số câu trả lời: " + question.getQuestionChoices().size());

                                // Hiển thị các lựa chọn câu trả lời
                                for (Choice choice : question.getQuestionChoices()) {
                                    Log.d("ApiManager", "Lựa chọn: " + choice.getChoiceContent() + ", Đúng: "
                                            + choice.isChoiceKey());
                                }

                                // Gọi callback với đối tượng question
                                callback.onSuccess(question); // Gọi callback thành công với đối tượng Question
                            } else {
                                Log.e("ApiManager", "Câu hỏi hoặc câu trả lời không hợp lệ.");
                                callback.onFailure("Dữ liệu không hợp lệ từ server.");
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("ApiManager", "Lỗi khi parse JSON: " + e.getMessage());
                            callback.onFailure("Lỗi khi parse JSON.");
                        }
                    } else {
                        Log.e("ApiManager", "Body trả về rỗng hoặc không hợp lệ.");
                        callback.onFailure("Dữ liệu không hợp lệ từ server.");
                    }
                } else {
                    Log.e("ApiManager", "Lỗi từ server: Mã lỗi " + response.code());
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiManager", "Lỗi kết nối: " + e.getMessage());
                callback.onFailure("Không thể kết nối tới API.");
            }
        });
    }

    // public void fetchAnswerContentFromApi(ApiCallback callback) {
    // Request request = new Request.Builder()
    // .url("http://192.168.109.2:8080/get_answer_content") // Địa chỉ API để lấy
    // nội dung câu trả lời
    // .build();
    //
    // client.newCall(request).enqueue(new Callback() {
    // @Override
    // public void onFailure(Call call, IOException e) {
    // Log.e("ApiManager", "Lỗi kết nối: " + e.getMessage());
    // callback.onFailure("Không thể kết nối tới API.");
    // }
    //
    // @Override
    // public void onResponse(Call call, Response response) throws IOException {
    // String responseBody = response.body().string();
    // Log.d("ApiManager", "Phản hồi từ server: " + responseBody);
    //
    // if (response.isSuccessful()) {
    // callback.onSuccess(responseBody); // Gọi callback với nội dung câu trả lời
    // } else {
    // callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
    // }
    // }
    // });
    // }

    // // Phương thức gọi API lấy URL của file âm thanh
    // public void fetchAudioUrl(String url, ApiCallback callback) {
    // Request request = new Request.Builder()
    // .url("url")
    // .build();
    //
    // client.newCall(request).enqueue(new Callback() {
    // @Override
    // public void onFailure(Call call, IOException e) {
    // // Gọi callback khi có lỗi
    // callback.onFailure(e.getMessage());
    // }
    //
    // @Override
    // public void onResponse(Call call, Response response) throws IOException {
    // if (response.isSuccessful()) {
    // // Giả sử API trả về URL của file âm thanh dưới dạng string
    // String audioUrl = response.body().string(); // Lấy URL từ phản hồi API
    // runOnUiThread(() -> playAudio(audioUrl)); // Chạy playAudio trên UI thread
    // } else {
    // runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Lỗi tải âm
    // thanh", Toast.LENGTH_SHORT).show());
    // }
    // }
    // });
    // }

    // Kiểm tra kết nối Internet
    public boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
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
