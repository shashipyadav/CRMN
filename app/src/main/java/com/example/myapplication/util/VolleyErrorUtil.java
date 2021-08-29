package com.example.myapplication.util;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.myapplication.R;

public class VolleyErrorUtil {

    public static void showVolleyError(Context context, VolleyError error){
        if(context != null){
            if (error instanceof TimeoutError) {
                ToastUtil.showToastMessage(context.getResources().
                                getString(R.string.error_network_timeout),
                        context);
            }else if(error instanceof NoConnectionError){
                ToastUtil.showToastMessage(context.getResources().
                                getString(R.string.error_no_connection),
                        context);
            } else if (error instanceof AuthFailureError) {
                ToastUtil.showToastMessage(context.getResources().
                                getString(R.string.error_auth_failure),
                        context);
            } else if (error instanceof ServerError) {
                ToastUtil.showToastMessage(context.getResources().
                                getString(R.string.error_servererror),
                        context);
            } else if (error instanceof NetworkError) {
                ToastUtil.showToastMessage(context.getResources().
                                getString(R.string.error_network_error),
                        context);
            } else if (error instanceof ParseError) {
                ToastUtil.showToastMessage(context.getResources().
                                getString(R.string.error_parse_error),
                        context);
            }
        }
    }
}
