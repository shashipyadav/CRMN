package com.example.myapplication.user_interface.home.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Constant;
import com.example.myapplication.util.ToastUtil;

public class PdfViewActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = PdfViewActivity.class.getSimpleName();
    private WebView mWebView;
    boolean didLoadPDF = false;
    private String pdfUrl = "";
    private String title = "";
    private String htmlCode = "";
    private ProgressDialog prDialog;
    int retrycount=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        getArguments();
        setActionBar();
        loadPdf();
    }
    private void setActionBar(){
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadPdf(){
        mWebView = findViewById(R.id.webView);
        mWebView.invalidate();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.setWebViewClient(new CallWebViewClient());

    //    pdfUrl ="https://drive.google.com/file/d/1iiSw8wafQXXZtge8Q9yRfDCFqe-Xe6gE/view?usp=sharing";
        Log.e(DEBUG_TAG,"PDF URL = "+ pdfUrl);

        if(pdfUrl.isEmpty()){
            ToastUtil.showToastMessage("Not a valid pdf url",this);
            //SharedPrefManager prefManager = new SharedPrefManager(this);
            //mWebView.loadDataWithBaseURL(prefManager.getClientServerUrl()+"sendMailReport.do?actn=PrintData&idlist=@&fieldid=101&fieldId=101&fieldValue=&formid=3336&matchingFields=&reportid=101&moduleconditions=&reportname=Outstanding%20Bills%20Report","","text/html","utf-8",null);
        }else{
            if(pdfUrl.contains("drive.google.com")){
                mWebView.loadUrl(pdfUrl);

            }else {
                mWebView.loadUrl(Constant.GOOGLE_DOC_VIEWER_URL + pdfUrl);
                Log.e(DEBUG_TAG, "FINAL GOOLE DOC + PDF URL = " + Constant.GOOGLE_DOC_VIEWER_URL + pdfUrl);
            }
        }


    //    mWebView.loadUrl("https://7.strategicerpcloud.com/strategicerp/sendMailReport.do?actn=PrintData&idlist=@&fieldid=93&fieldId=93&fieldValue=&formid=3336&matchingFields=&reportid=93&moduleconditions=&reportname=Outstanding%20Bills%20Report&cloudcode=simpoloceramics&token=DTZ9BWT91CO3GTUQ");


    }

    private void getArguments(){
        if(getIntent().getExtras() !=null){
            title = getIntent().getExtras().getString(Constant.EXTRA_TITLE);
            pdfUrl =  getIntent().getExtras().getString(Constant.EXTRA_URL);
        }
    }

    private void dismissProgressDialog() {


        if(prDialog !=null && prDialog.isShowing()){
            prDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.clearCache(true);
        mWebView.getSettings().setAppCacheEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

        public class CallWebViewClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
                if (prDialog == null) {
                    prDialog = new ProgressDialog(PdfViewActivity.this);
                    prDialog.setMessage("Please wait ...");
                    Log.e(DEBUG_TAG,"onPageStarted called" + "URL = " + pdfUrl);
                    prDialog.show();
                }
            didLoadPDF = true;
        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                                    WebResourceError error) {
            super.onReceivedError(view, request, error);

            if(retrycount<5)
            {
                mWebView.reload();
                retrycount=retrycount+1;
                Toast.makeText(
                        PdfViewActivity.this,
                        "Unable to load PDF, retying " + retrycount,
                        Toast.LENGTH_LONG
                ).show();
                dismissProgressDialog();
            }
            else
            {
                Toast.makeText(
                        PdfViewActivity.this,
                        "Unable to load PDF. " + error,
                        Toast.LENGTH_LONG
                 ).show();
                dismissProgressDialog();
            }


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);


             if (!didLoadPDF) {
                mWebView.reload();
            } else {
                if (mWebView.getTitle().equals("")) {
                    //  Log.e(DEBUG_TAG, "Title = " + mWebView.getTitle());
                    if (retrycount < 5) {
                        mWebView.reload();
                        retrycount = retrycount + 1;
                    }
                }
            }

            dismissProgressDialog();
        }
    }
}