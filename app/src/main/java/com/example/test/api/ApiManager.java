package com.example.test.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.text.TextWatcher;
import android.util.Log;
/*
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;*/

import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.QuestionChoice;
import com.example.test.model.Question;
import com.example.test.model.Result;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiManager {
    public ApiManager() {
    }

    private static final String BASE_URL = "http://192.168.56.1:8080"; // Thay đổi URL của bạn
    private static final OkHttpClient client = new OkHttpClient();

    public static void gradeAnswer(int answerId, Callback callback) {
        String url = BASE_URL + "/api/v1/answers/grade/" + answerId;

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/json"))) // Nếu không có body, có thể để trống
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void sendLoginRequest(String email, String password, ApiCallback callback) {

        String json = "{ \"username\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/auth/login") // Thay bằng URL máy chủ của bạn
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

    // { \"name\": \"" + name + "\", \"phone\": \"" + phone + "\", \"email\": \"" +
    // email + "\", \"password\": \"" + password + "\" }
    public void sendSignUpRequest(Context context, String name, String email, String password, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .build();

        String json = "{ \"name\": \"" + name + "\", \"email\": \"" + email + "\", \"password\": \"" + password
                + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/auth/register") // Thay bằng URL máy chủ của bạn
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
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONObject data = responseJson.getJSONObject("data");
                        String otpID = data.optString("otpID"); // Trích xuất otpID từ phản hồi

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

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .build();
        String json = "{ \"otpID\": \"" + otpID + "\", \"otp\": \"" + code + "\" }";

        Log.d("ConfirmCode", "OTPID: " + otpID + ", OTP: " + code);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/otp/verify-otp") // Thay bằng URL máy chủ của bạn
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

    //Resend OTP
    public void resendCodeRequest(String otpID, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .build();
        String json = "{ \"otpID\": \"" + otpID + "\" }";

        Log.d("ConfirmCode", "OTPID: " + otpID );

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/otp/resend-otp") // Thay bằng URL máy chủ của bạn
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

    public void sendForgotRequest(String email, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .build();

        String json = "{ \"email\": \"" + email + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/forgot-password/send-otp") // Thay bằng URL máy chủ của bạn
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
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONObject data = responseJson.getJSONObject("data");
                        String otpID = data.optString("otpID"); // Trích xuất otpID từ phản hồi
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

    public void sendConfirmCodeForgotRequest(String otpID, String code, ApiCallback callback) {

        String json = "{ \"otpID\": \"" + otpID + "\", \"otp\": \"" + code + "\" }";

        Log.d("ConfirmCode", "OTPID: " + otpID + ", OTP: " + code);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/otp/verify-otp") // Thay bằng URL máy chủ của bạn
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
//                if (response.isSuccessful()) {
//                    callback.onSuccess(); // Gọi callback thành công
//                } else {
//                    if (!response.isSuccessful()) {
//                        JSONObject errorJson = null; // Parse nội dung phản hồi
//                        try {
//                            errorJson = new JSONObject(responseBody);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                        String errorMessage = errorJson.optString("message", "Mã OTP sai! Vui lòng kiểm tra lại.");
//                        callback.onFailure(errorMessage);
//                    }
//                }
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
//                        JSONObject data = responseJson.getJSONObject("data");
                        String token = responseJson.optString("token"); // Trích xuất otpID từ phản hồi
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

    public void updatePasswordRequest(String newPassword, String confirmPassword,String token, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        String json = "{ \"newPassword\": \"" + newPassword + "\", \"confirmPassword\": \"" + confirmPassword + "\" }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/forgot-password/update-password") // Thay bằng URL API của bạn
                .post(body)
                .addHeader("Authorization", "Bearer " + token) // Thêm header Authorization với token
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
                    callback.onSuccess(); // Gọi callback thành công
                } else {
                    callback.onFailure("Cập nhật mật khẩu thất bại! " + response.message());
                }
            }
        });
    }


    // Phương thức để lấy dữ liệu câu hỏi từ API (GET request)
    public void fetchQuestionContentFromApi(int questionId, ApiCallback callback) {
        // String access_token =
        // "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2YW5raWV0bWFzdGVyY3Y0QGdtYWlsLmNvbSIsImV4cCI6MTc0NTMwNzEyMCwiaWF0IjoxNzM2NjY3MTIwLCJ1c2VyIjp7ImlkIjo1LCJlbWFpbCI6InZhbmtpZXRtYXN0ZXJjdjRAZ21haWwuY29tIiwibmFtZSI6IlZhbiBLaWV0In19.K_906ifZ2fQEMxkPEPERaLY7Gh-VTyjvoUae6CEjnLkmR-vyleeraJuAzfEvzMfgsMwwniTBntAIBQP_p9HgFA";
        // // Token mà bạn đã nhận được sau khi đăng nhập thành công
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/questions/" + questionId)// Địa chỉ API lấy câu hỏi
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
                            Gson gson = new Gson();
                            // Chuyển đổi JSON thành đối tượng ApiResponse
                            ApiResponseQuestion apiResponse = gson.fromJson(responseBody, ApiResponseQuestion.class);
                            // Lấy đối tượng Question từ thuộc tính data của ApiResponse
                            Question question = apiResponse.getData();

                            if (question != null && question.getQuestionChoices() != null) {
                                Log.d("ApiManager", "Câu hỏi: " + question.getQuesContent());
                                Log.d("ApiManager", "Số câu trả lời: " + question.getQuestionChoices().size());

                                // Hiển thị các lựa chọn câu trả lời
                                for (QuestionChoice choice : question.getQuestionChoices()) {
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

    public void fetchLessonById(int lessonId, ApiCallback callback) {

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/lessons/" + lessonId) // Thay bằng URL máy chủ của bạn
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ApiManager", "Phản hồi từ server: " + responseBody);

                    // Phân tích cú pháp JSON để lấy thông tin bài học
                    Gson gson = new Gson();
                    ApiResponseLesson apiResponse = gson.fromJson(responseBody, ApiResponseLesson.class);

                    // Kiểm tra mã trạng thái
                    if (apiResponse.getStatusCode() == 200) {
                        Lesson lesson = apiResponse.getData(); // Lấy dữ liệu bài học
                        callback.onSuccess(lesson); // Gọi callback onSuccess với bài học
                    } else {
                        callback.onFailure("Lỗi từ server: " + apiResponse.getMessage());
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

    public void fetchCourseById(ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/courses/1") // Thay bằng URL máy chủ của bạn
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ApiManager", "Phản hồi từ server: " + responseBody);

                    // Phân tích cú pháp JSON để lấy thông tin khóa học
                    Gson gson = new Gson();
                    ApiResponseCourse apiResponse = gson.fromJson(responseBody, ApiResponseCourse.class);

                    // Kiểm tra mã trạng thái
                    if (apiResponse.getStatusCode() == 200) {
                        Course course = apiResponse.getData(); // Lấy dữ liệu khóa học
                        callback.onSuccess(course); // Gọi callback onSuccess với khóa học
                    } else {
                        callback.onFailure("Lỗi từ server: " + apiResponse.getMessage());
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

    public void saveUserAnswer(int questionId, String answerContent, ApiCallback callback) {

        // Tạo JSON từ dữ liệu
        String json = "{"
                + "\"questionId\":" + questionId + ","
                + "\"answerContent\":\"" + answerContent + "\""
                + "}";

        // Tạo RequestBody
        RequestBody body = RequestBody.create(
                json, MediaType.get("application/json; charset=utf-8"));

        // Tạo Request
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/answers/user/1") // Thay bằng URL máy chủ của bạn
                .post(body)
                .build();

        // Thực hiện Request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Không thể lưu câu trả lời: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Lỗi: " + response.code());
                }
            }
        });
    }

    public void fetchAnswerPointsByQuesId(int questionId, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/answers/question/" + questionId) // Thay bằng URL máy chủ của bạn
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ApiManager", "Phản hồi từ server: " + responseBody);

                    // Tạo đối tượng từ phản hồi JSON
                    Gson gson = new Gson();
                    ApiResponseAnswer apiResponse = gson.fromJson(responseBody, ApiResponseAnswer.class);

                    // Kiểm tra mã trạng thái
                    if (apiResponse.getStatusCode() == 200) {
                        List<Answer> answers = apiResponse.getData().getContent(); // Lấy dữ liệu khóa học

                        // Tạo JSON từ danh sách answers
                        String jsonAnswers = gson.toJson(answers);
                        Log.d("ApiManager", "Dữ liệu JSON: " + jsonAnswers);

                        callback.onSuccess(answers); // Gọi callback onSuccess với khóa học
                    } else {
                        callback.onFailure("Lỗi từ server: " + apiResponse.getMessage());
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

    public void fetchResultByLesson(int lessonId, ApiCallback callback) {

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/lesson-results/lesson/" + lessonId) // Thay bằng URL máy chủ của bạn
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ApiManager", "Phản hồi từ server: " + responseBody);

                    // Tạo đối tượng từ phản hồi JSON
                    Gson gson = new Gson();
                    ApiResponseResult apiResponse = gson.fromJson(responseBody, ApiResponseResult.class);

                    // Kiểm tra mã trạng thái
                    if (apiResponse.getStatusCode() == 200) {
                        Result result = apiResponse.getData(); // Lấy dữ liệu khóa học

                        // Tạo JSON từ đối tượng result
                        String jsonResult = gson.toJson(result);
                        Log.d("ApiManager", "Dữ liệu JSON: " + jsonResult);

                        callback.onSuccess(result); // Gọi callback onSuccess với khóa học
                    } else {
                        callback.onFailure("Lỗi từ server: " + apiResponse.getMessage());
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
