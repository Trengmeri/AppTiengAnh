package com.example.test.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import com.example.test.NotificationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthenticationManager extends BaseApiManager {

    public void sendLoginRequest(String email, String password, ApiCallback callback) {
        String json = "{ \"username\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/auth/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AuthenticationManager", "Kết nối thất bại: " + e.getMessage());
                callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("AuthenticationManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    Log.e("AuthenticationManager", "Lỗi từ server: Mã lỗi " + response.code() + ", Nội dung: " + responseBody);
                    callback.onFailure("Đăng nhập thất bại! Vui lòng kiểm tra lại thông tin.");
                }
            }
        });
    }

    public void sendSignUpRequest(Context context, String name, String email, String password, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Thời gian chờ kết nối
                .readTimeout(30, TimeUnit.SECONDS)    // Thời gian chờ đọc dữ liệu
                .writeTimeout(20, TimeUnit.SECONDS)   // Thời gian chờ ghi dữ liệu
                .build();

        String json = "{ \"name\": \"" + name + "\", \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/auth/register")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AuthenticationManager", "Kết nối thất bại: " + e.getMessage());
                callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("AuthenticationManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONObject data = responseJson.getJSONObject("data");
                        String otpID = data.optString("otpID");
                        callback.onSuccessWithOtpID(otpID);
                    } catch (JSONException e) {
                        callback.onFailure("Lỗi phân tích phản hồi JSON: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Đăng ký thất bại!" + response.message());
                }
            }
        });
    }

    public void sendConfirmCodeRequest(String otpID, String code, ApiCallback callback) {
        String json = "{ \"otpID\": \"" + otpID + "\", \"otp\": \"" + code + "\" }";
        Log.d("AuthenticationManager", "OTPID: " + otpID + ", OTP: " + code);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/otp/verify-otp")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleNetworkError(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("AuthenticationManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess();
                    //them
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        String message = responseJson.optString("message", "Tài khoản của bạn đã được tạo.");
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        NotificationManager.getInstance().addNotification(
                                "Đăng ký thành công",
                                message,
                                currentDate
                        );
                    } catch (JSONException e) {
                        callback.onFailure("Lỗi phân tích phản hồi JSON: " + e.getMessage());
                    }
                } else {
                    handleError(response, responseBody, callback, "Mã OTP sai! Vui lòng kiểm tra lại.");
                }
            }
        });
    }

    public void resendConfirmCodeRequest(String otpID, ApiCallback callback) {
        String json = "{ \"otpID\": \"" + otpID + "\" }";
        Log.d("AuthenticationManager", "OTPID: " + otpID);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/otp/resend-otp")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleNetworkError(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("AuthenticationManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    handleError(response, responseBody, callback, "Gửi lại mã OTP thất bại!");
                }
            }
        });
    }

    public void sendForgotPasswordRequest(String email, ApiCallback callback) {
        String json = "{ \"email\": \"" + email + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/forgot-password/send-otp")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AuthenticationManager", "Kết nối thất bại: " + e.getMessage());
                callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("AuthenticationManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONObject data = responseJson.getJSONObject("data");
                        String otpID = data.optString("otpID");
                        callback.onSuccessWithOtpID(otpID);
                    } catch (JSONException e) {
                        callback.onFailure("Lỗi phân tích phản hồi JSON: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Thất bại: " + response.message());
                }
            }
        });
    }

    public void sendConfirmForgotPasswordRequest(String otpID, String code, ApiCallback callback) {
        String json = "{ \"otpID\": \"" + otpID + "\", \"otp\": \"" + code + "\" }";
        Log.d("AuthenticationManager", "OTPID: " + otpID + ", OTP: " + code);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/otp/verify-otp")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleNetworkError(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("AuthenticationManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        String token = responseJson.optString("token");
                        callback.onSuccessWithToken(token);
                    } catch (JSONException e) {
                        callback.onFailure("Lỗi phân tích phản hồi JSON: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Thất bại: " + response.message());
                }
            }
        });
    }

    public void updatePassword(String newPassword, String confirmPassword, String token, ApiCallback callback) {
        String json = "{ \"newPassword\": \"" + newPassword + "\", \"confirmPassword\": \"" + confirmPassword + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/forgot-password/update-password")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AuthenticationManager", "Kết nối thất bại: " + e.getMessage());
                callback.onFailure("Kết nối thất bại! Không thể kết nối tới API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("AuthenticationManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Cập nhật mật khẩu thất bại! " + response.message());
                }
            }
        });
    }

    private void handleNetworkError(IOException e, ApiCallback callback) {
        if (e instanceof SocketTimeoutException) {
            Log.e("AuthenticationManager", "Kết nối timeout: " + e.getMessage());
            callback.onFailure("Thời gian kết nối đã hết. Vui lòng thử lại.");
        } else if (e instanceof UnknownHostException) {
            Log.e("AuthenticationManager", "Không tìm thấy máy chủ: " + e.getMessage());
            callback.onFailure("Không thể tìm thấy máy chủ. Kiểm tra lại URL hoặc kết nối mạng.");
        } else if (e instanceof ConnectException) {
            Log.e("AuthenticationManager", "Không kết nối được tới server: " + e.getMessage());
            callback.onFailure("Không thể kết nối tới server. Kiểm tra xem server có hoạt động không.");
        } else {
            Log.e("AuthenticationManager", "Lỗi không xác định: " + e.getMessage(), e);
            callback.onFailure("Lỗi không xác định: " + e.getMessage());
        }
    }

    private void handleError(Response response, String responseBody, ApiCallback callback, String defaultMessage) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            String errorMessage = errorJson.optString("message", defaultMessage);
            callback.onFailure(errorMessage);
        } catch (JSONException e) {
            callback.onFailure(defaultMessage);
        }
    }

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