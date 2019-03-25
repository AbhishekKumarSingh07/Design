package com.example.design;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {
    private WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        view=findViewById(R.id.wb);
        WebSettings webSettings=view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String val=getIntent().getStringExtra("url");
        view.setWebViewClient(new WebViewClient());
        view.loadUrl(val);
    }
}
