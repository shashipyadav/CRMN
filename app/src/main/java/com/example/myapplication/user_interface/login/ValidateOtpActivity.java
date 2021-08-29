package com.example.myapplication.user_interface.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.launcher.NavigationActivity;
import com.example.myapplication.helper.DeviceInfoHelper;
import com.example.myapplication.Constant;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.KeyboardUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.util.ValidateUtil;
import com.example.myapplication.util.VolleyErrorUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ValidateOtpActivity extends AppCompatActivity {
    private final String DEBUG_TAG = ValidateOtpActivity.class.getSimpleName();

    private SharedPrefManager prefManager;
    private ProgressDialog    mWaiting;
    private String            mobileNo = "";
    private Button            btnVerify;
    private EditText          et1;
    private EditText          et2;
    private EditText          et3;
    private EditText          et4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_otp);
        try {
            getExtras();
            initViews();
           // displayServerImage();
            setVerifyLabel();
            initButton();
            initBackButton();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void displayServerImage(){
        ImageView imageview = findViewById(R.id.image_logo);
        SharedPrefManager mPrefManager = new SharedPrefManager(this);
        String imageUrl = mPrefManager.getServerImageUrl();

        Picasso.with(this).load(imageUrl)
                .placeholder(R.drawable.default_server_icon)
                .resize(80,80)
                .centerInside()
                .into(imageview);
    }

    private void getExtras(){
        if (getIntent().getExtras() != null) {
            mobileNo = getIntent().getExtras().getString(Constant.EXTRA_MOBILE);
        }
    }

    private void initViews() {
        btnVerify = findViewById(R.id.btn_verify);
        et1 = findViewById(R.id.text1);
        et2 = findViewById(R.id.text2);
        et3 = findViewById(R.id.text3);
        et4 = findViewById(R.id.text4);

        prefManager = new SharedPrefManager(ValidateOtpActivity.this);
        et1.addTextChangedListener(textWatcher);
        et2.addTextChangedListener(textWatcher);
        et3.addTextChangedListener(textWatcher);
        et4.addTextChangedListener(textWatcher);
    }

    private void setVerifyLabel(){
        TextView txtInfo = findViewById(R.id.txt_info);
        txtInfo.setText(String.format("Please type the verification code sent to %1$s",
                mobileNo));
    }

    private void initButton(){
        Button verify = findViewById(R.id.btn_verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatedInputFields()) {
                    callValidateOtpAPI();
                }
            }
        });
    }

    private void initBackButton(){
        ImageView backbtn = findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public boolean validatedInputFields() {
        if (ValidateUtil.isEmpty(et1) || ValidateUtil.isEmpty(et2) ||
                ValidateUtil.isEmpty(et3) || ValidateUtil.isEmpty(et4)) {
            ToastUtil.showToastMessage(getString(R.string.please_enter_otp),
                    this);
            return false;
        }
        return true;
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int size = 1;
            if(et1.getText().toString().length() == size){
                et2.requestFocus();
            }if(et2.getText().toString().length()== size){
                et3.requestFocus();
            }if(et3.getText().toString().length() == size){
                et4.requestFocus();
            }if(et4.getText().toString().length() == size){
                KeyboardUtil.hideKeyboard(ValidateOtpActivity.this);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void callValidateOtpAPI(){
        if (NetworkUtil.isNetworkOnline(ValidateOtpActivity.this)) {
            validateOtpAPI();
        }else {
            DialogUtil.showAlertDialog(ValidateOtpActivity.this,
                    getString(R.string.no_internet_title), getString(R.string.no_internet_message), false, false);
        }
    }

    private void validateOtpAPI(){
        String otp = et1.getText().toString() + et2.getText().toString() +
                et3.getText().toString() + et4.getText().toString();

        mWaiting = ProgressDialog.show(ValidateOtpActivity.this, "", getString(R.string.loading_title), false);
        String URL = String.format(Constant.URL_VALIDATE_OTP,
                prefManager.getClientServerUrl(),
                mobileNo, otp, prefManager.getCloudCode());

        RequestQueue queue = Volley.newRequestQueue(ValidateOtpActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "validateOTP Response=" + response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            String token = jsonObject.getString("Auth_token");
                            String userName = jsonObject.getString("username");
                            String loginname = jsonObject.getString("loginname");
                            String userid = jsonObject.getString("userid");


                            if(result.toLowerCase().equals("success")){
                                prefManager.saveUserDetails(true,token,mobileNo,userName,userid,loginname);
                                ToastUtil.showToastMessage("Success",ValidateOtpActivity.this);
                                if(mWaiting != null){
                                    mWaiting.dismiss();
                                }

                                callUpdateDeviceInfoAPI();
                            }else{
                                ToastUtil.showToastMessage(getString(R.string.check_mobile),
                                        ValidateOtpActivity.this);
                            }
                            if(mWaiting != null){
                                mWaiting.dismiss();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            if(response.contains("OTP does not match!")){
                                ToastUtil.showToastMessage(response,
                                        ValidateOtpActivity.this);
                            }
                            if(mWaiting != null){
                                mWaiting.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorUtil.showVolleyError(ValidateOtpActivity.this, error);
                if (mWaiting != null) {
                    mWaiting.dismiss();
                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void callUpdateDeviceInfoAPI(){
        // let detail = "\(DEVICE_NAME ?? "")@j@_@j@\(DEVICE_MODEL ?? "")@j@\(SYSTEM_VERSION ?? "")@j@_@j@_"

        String deviceInfo = DeviceInfoHelper.getDeviceName() + "@j@_@j@"+DeviceInfoHelper.getDeviceModel()+"@j@_@j@"+DeviceInfoHelper.getSystemVersion()+"@j@_@j@_";

        mWaiting = ProgressDialog.show(ValidateOtpActivity.this, "", "Please wait...", false);
        String URL = String.format(Constant.URL_UPDATE_DEVICE_INFO,
                prefManager.getClientServerUrl(),prefManager.getAuthToken(),prefManager.getCloudCode(),prefManager.getFcmToken(),deviceInfo);

        Log.e("UPDATE DEVICE URL",URL);

        RequestQueue queue = Volley.newRequestQueue(ValidateOtpActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "UPDATE DEVICE INFO Response=" + response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("action");
                            ToastUtil.showToastMessage(result,ValidateOtpActivity.this);

                            Intent intent = new Intent(ValidateOtpActivity.this,
                                    NavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                            if(mWaiting != null){
                                mWaiting.dismiss();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            if(mWaiting != null){
                                mWaiting.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorUtil.showVolleyError(ValidateOtpActivity.this, error);
                if (mWaiting != null) {
                    mWaiting.dismiss();
                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
