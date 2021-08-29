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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.Constant;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.KeyboardUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.util.ValidateUtil;
import com.example.myapplication.util.VolleyErrorUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private final String DEBUG_TAG = LoginActivity.class.getSimpleName();
    private SharedPrefManager prefManager;
    private EditText etMobile;
    private Button submit;
    private ProgressDialog mWaiting1;
    private ProgressDialog mWaiting;
    private CountryCodePicker ccp;
    private boolean iswithoutCountryCode = true;
    private int count = 0;
    private BottomSheetServerList.ServerSelectedListener mServerSelectedListener =
            new BottomSheetServerList.ServerSelectedListener() {
                @Override
                public void onServerSelected() {
                    // displayServerImage();
                    submit.setText("Get OTP");
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        submit.setText("Get Server List");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            ccp = (CountryCodePicker) findViewById(R.id.ccp);

            initEditText();
            initButton();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        ImageView imgLogo = findViewById(R.id.img_logo);
//        imgLogo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(validatedInputFields()){
//                    BottomSheetServerList bottomSheetFragment  = new BottomSheetServerList(mServerSelectedListener);
//                    Bundle args = new Bundle();
//                    args.putString(Constant.EXTRA_MOBILE , etMobile.getText().toString());
//                    bottomSheetFragment.setArguments(args);
//                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
//                }
//            }
//        });

    }

    public void initEditText() {
        etMobile = findViewById(R.id.et_mobile);
        etMobile.addTextChangedListener(textWatcher);
    }

    private void displayServerImage() {
        ImageView imageview = findViewById(R.id.img_logo);
        SharedPrefManager mPrefManager = new SharedPrefManager(this);
        String imageUrl = mPrefManager.getServerImageUrl();

        Picasso.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_server_icon)
                .resize(80, 80)
                .centerInside()
                .into(imageview);
    }

    public void initButton() {
        submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatedInputFields()) {
                    if (submit.getText().toString().toLowerCase().equals("get server list")) {
                        callGetServerListAPI();
                    } else {
                        callGetOtpApi();

                    }
                }
            }
        });
    }

    public boolean validatedInputFields() {
        if (ValidateUtil.isEmpty(etMobile)) {
            ToastUtil.showToastMessage("Please enter Mobile Number",
                    this);
            return false;
        }
        /*if (etMobile.getText().toString().length() < 10 || etMobile.getText().toString().length() > 10) {
            ToastUtil.showToastMessage("Enter a valid Mobile Number", LoginActivity.this);
            return false;
        }*/
        return true;
    }

    private void callGetServerListAPI() {
        if (NetworkUtil.isNetworkOnline(LoginActivity.this)) {
            getServerListAPI(iswithoutCountryCode);
        } else {
            DialogUtil.showAlertDialog(this,
                    getResources().getString(R.string.no_internet_title),
                    getResources().getString(R.string.no_internet_message),
                    false,
                    false);
        }
    }

    private void getServerListAPI(boolean b) {
        prefManager = new SharedPrefManager(LoginActivity.this);
        mWaiting1 = ProgressDialog.show(LoginActivity.this, "", getResources().getString(R.string.loading_message), false);

       /* if(iswithoutCountryCode)
            {
                mWaiting = ProgressDialog.show(LoginActivity.this, "", getResources().getString(R.string.loading_message), false);
            }else {

        }*/
        String URL = "";
        if (b) {
            if ("91".equalsIgnoreCase(ccp.getSelectedCountryCode())) {
                URL = String.format(Constant.URL_SERVER_LIST, ccp.getSelectedCountryCode() + etMobile.getText().toString());

            } else {
                URL = String.format(Constant.URL_SERVER_LIST, ccp.getSelectedCountryCode() + etMobile.getText().toString());

            }
        } else {
            URL = String.format(Constant.URL_SERVER_LIST, etMobile.getText().toString());

        }


        Log.e(DEBUG_TAG, "getServerListAPI() " + URL);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "callAPI Response=" + response);
                        try {
                            Gson gson = new Gson();

                            List<Server> serverList = gson.fromJson(response, new TypeToken<List<Server>>() {
                            }.getType());
                            if (!serverList.isEmpty()) {
                                if (serverList.size() == 1) {

                                    String serverImageUrl = String.format(Constant.SERVER_IMAGE_URL
                                            , serverList.get(0).getClientLogoForLoginScreen());

                                    prefManager.setServerDetails(serverList.get(0).getClientServerURL(),
                                            serverList.get(0).getClientServerCloudCode(),
                                            serverImageUrl);
                                    submit.setText("Getting OTP");
                                    callGetOtpApi();
                                } else {
                                    mWaiting1.dismiss();
                                    BottomSheetServerList bottomSheetFragment = new BottomSheetServerList(mServerSelectedListener);
                                    Bundle args = new Bundle();
                                    if (b) {
                                        args.putString(Constant.EXTRA_MOBILE, ccp.getSelectedCountryCode() + etMobile.getText().toString());

                                    } else {
                                        args.putString(Constant.EXTRA_MOBILE, etMobile.getText().toString());

                                    }

                                    bottomSheetFragment.setArguments(args);
                                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                                }
                            } else {
                                mWaiting1.dismiss();
                                if (count == 0) {
                                    count =1;
                                    iswithoutCountryCode = false;
                                    getServerListAPI(iswithoutCountryCode);

                                } else {
                                    ToastUtil.showToastMessage(getResources().getString(R.string.mobile_not_found), LoginActivity.this);

                                }


                            }


                            if (response.isEmpty()) {
                                ToastUtil.showToastMessage(getResources().getString(R.string.mobile_not_found), LoginActivity.this);
                                mWaiting1.dismiss();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mWaiting1 != null) {
                                mWaiting1.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    private void callGetOtpApi() {

        if (mWaiting == null) {
            mWaiting = ProgressDialog.show(LoginActivity.this, "", getResources().getString(R.string.loading_message), false);
        }

        String URL = "";

        if (iswithoutCountryCode) {
            URL = String.format(Constant.URL_GET_OTP,
                    prefManager.getClientServerUrl(),
                    ccp.getSelectedCountryCode() + etMobile.getText().toString(),
                    prefManager.getCloudCode());
        } else {
            URL = String.format(Constant.URL_GET_OTP,
                    prefManager.getClientServerUrl(),
                    etMobile.getText().toString(),
                    prefManager.getCloudCode());
        }

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        Log.e("callGetOtpApi url", URL);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "callGetOtpApi Response=" + response);
                        try {
                            if (response.contains("OTP sent by SMS to") || response.contains("OTP for this demo account is")) {
                                if (response.contains("OTP sent by SMS to")) {
                                    ToastUtil.showToastMessage(response, LoginActivity.this);
                                }
                                Intent intent = new Intent(LoginActivity.this, ValidateOtpActivity.class);
                                //intent.putExtra(Constant.EXTRA_MOBILE, ccp.getSelectedCountryCode() +etMobile.getText().toString());
                                if (iswithoutCountryCode) {
                                    intent.putExtra(Constant.EXTRA_MOBILE, ccp.getSelectedCountryCode() + etMobile.getText().toString());

                                } else {
                                    intent.putExtra(Constant.EXTRA_MOBILE, etMobile.getText().toString());

                                }
                                startActivity(intent);
                            } else {
                                ToastUtil.showToastMessage(response, LoginActivity.this);
                            }
                            mWaiting.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mWaiting != null) {
                                mWaiting.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorUtil.showVolleyError(LoginActivity.this, error);
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int size = 10;
            if (etMobile.getText().toString().length() == size) {
                KeyboardUtil.hideKeyboard(LoginActivity.this);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
