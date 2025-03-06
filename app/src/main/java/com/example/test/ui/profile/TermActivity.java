package com.example.test.ui.profile;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

public class TermActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_term);
        // Find the WebView by its unique ID
        WebView webView = findViewById(R.id.web);
        ImageView back = findViewById(R.id.btnBack);

        back.setOnClickListener(view -> {
            finish();
        });

        webView.loadUrl("https://trengmeri.github.io/WebView/webview.html");

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
    }
}