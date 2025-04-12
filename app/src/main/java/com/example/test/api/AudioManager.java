package com.example.test.api;


import static com.example.test.api.BaseApiManager.BASE_URL;
import static com.example.test.api.BaseApiManager.client;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.example.test.SharedPreferencesManager;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioManager extends BaseApiManager{
    private final Context context;

    public AudioManager(Context context) {
        this.context = context;
    }

    public void uploadfileM4A(File file, ApiCallback callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("audio/m4a")))
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/audio/convert-m4a-to-wav") // Cập nhật BASE_URL của bạn
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] wavData = response.body().bytes();
                    String base64Wav = Base64.encodeToString(wavData, Base64.DEFAULT);
                    callback.onSuccess(base64Wav);  // hoặc callback.onSuccess(wavData) nếu bạn dùng byte[]
                } else {
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("Không thể kết nối tới API: " + e.getMessage());
            }
        });
    }
//    public void convert(File file, ApiCallback callback){
//        String token = SharedPreferencesManager.getInstance(context).getAccessToken();
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", file.getName(),
//                        RequestBody.create(file, MediaType.parse("audio/m4a")))
//                .build();
//        Request request = new Request.Builder()
//                .url(BASE_URL + "/api/v1/speech/conert") // Cập nhật BASE_URL của bạn
//                .addHeader("Authorization", "Bearer " + token)
//                .post(requestBody)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//
//        });
//    }


}
