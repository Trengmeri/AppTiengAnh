package com.example.test.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LearningMaterialsManager extends BaseApiManager {
    private final Context context;

    public LearningMaterialsManager(Context context) {
        this.context = context;
    }

    public void fetchAndLoadImage(int questionId, ImageView imageView) {
        String url = BASE_URL + "/questions/" + questionId;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray dataArray = jsonResponse.getJSONArray("data");

                        if (dataArray.length() > 0) {  // Kiểm tra có dữ liệu không
                            JSONObject materialObject = dataArray.getJSONObject(0);
                            String imageUrl = materialObject.getString("materLink");

                            // Kiểm tra URL hợp lệ trước khi load ảnh
                            if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith("http")) {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Glide.with(context).load(imageUrl).into(imageView)
                                );
                            } else {
                                Log.e("API_ERROR", "URL ảnh không hợp lệ");
                            }
                        } else {
                            Log.e("API_ERROR", "Không có dữ liệu tài liệu");
                        }

                    } catch (Exception e) {
                        Log.e("API_ERROR", "Lỗi JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("API_ERROR", "Không tìm thấy ảnh. Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Lỗi kết nối: " + e.getMessage());
            }
        });
    }
}
