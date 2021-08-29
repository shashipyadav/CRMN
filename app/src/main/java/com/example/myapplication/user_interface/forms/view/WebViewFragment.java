package com.example.myapplication.user_interface.forms.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.Constant;
import com.example.myapplication.util.VolleyErrorUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    private String DEBUG_TAG = WebViewFragment.class.getSimpleName();
    private String title = "";
    private WebView webViewFaq;
    private ProgressBar progressBar;
    private String chartId = "";

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if(getArguments() != null){
            Bundle bundle = getArguments();
            title = bundle.getString(Constant.EXTRA_TITLE);
            chartId = getArguments().getString(Constant.EXTRA_CHART_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.webview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setTitle();
    }

    private void initView(View root){
        progressBar = root.findViewById(R.id.progressBar);
        webViewFaq = root.findViewById(R.id.web_view_faq);
    }

    @Override
    public void onStart() {
        super.onStart();
        webViewLoad();
    }

    private void setTitle(){
        getActivity().setTitle(title);
    }

    private void webViewLoad() {
        webViewFaq.setBackgroundColor(0x00000000);
        WebSettings webSettings = webViewFaq.getSettings();
        webViewFaq.setWebViewClient(new WebViewClient());
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);

        if(chartId.equals("")) {
            Log.e("WEBVIEWFRAGMENT", "webViewLoad Called");
            try {
                // read the HTML from the file
                InputStream fin = getActivity().getAssets().open("simpolo1.html");
                byte[] buffer = new byte[fin.available()];
                fin.read(buffer);
                fin.close();

                // load the HTML
                webViewFaq.loadData(new String(buffer), "text/html", "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            callGetHtmlUrlAPI();
        }
    }

    private void callGetHtmlUrlAPI(){
        SharedPrefManager mPrefManager = new SharedPrefManager(getActivity());
                String apiUrl = String.format(Constant.FORM_URL,
                        mPrefManager.getClientServerUrl(),
                        chartId,
                        Constant.SERVER_DATE_TIME,
                        mPrefManager.getCloudCode(),
                        mPrefManager.getAuthToken());

                Log.e(DEBUG_TAG, "Form url = " + apiUrl);


        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "Response=" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String htmlUrl = jsonObject.getString("extraparams");
                            htmlUrl = htmlUrl.replaceAll("^path=","");
                            webViewFaq.loadUrl(htmlUrl);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(getActivity() != null){
                    VolleyErrorUtil.showVolleyError(getActivity(), error);
                }
            }
        });
        queue.add(request);
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }
}
