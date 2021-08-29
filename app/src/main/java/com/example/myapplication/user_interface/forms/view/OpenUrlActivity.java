package com.example.myapplication.user_interface.forms.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.myapplication.R;
import com.example.myapplication.Constant;

public class OpenUrlActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            url = bundle.getString(Constant.EXTRA_URL);
        }
        setWebView();


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

    }

    private void setWebView(){
        webView = findViewById(R.id.web_view_faq);
        webView.setBackgroundColor(0x00000000);
        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new WebViewClient());
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}