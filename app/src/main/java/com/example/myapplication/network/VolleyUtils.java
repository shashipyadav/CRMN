package com.example.myapplication.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Constant;

import java.util.HashMap;
import java.util.Map;

public class VolleyUtils {

    public static void GET_METHOD(Context context, String url,
                                  final VolleyResponseListener listener)
    {

        // Initialize a new StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());

                    }
                })
        {
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Access the RequestQueue through singleton class.
        NetworkManager.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void POST_METHOD(Context context, String url,
                                   final Map<String,String> getParams,
                                   final VolleyResponseListener listener)
    {

        // Initialize a new StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());

                    }
                })

        {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                getParams.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        // Access the RequestQueue through singleton class.
        NetworkManager.getInstance(context).addToRequestQueue(stringRequest);
    }
}
