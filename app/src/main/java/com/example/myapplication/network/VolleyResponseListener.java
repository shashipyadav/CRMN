package com.example.myapplication.network;

public interface VolleyResponseListener {

    void onError(String message);

    void onResponse(Object response);
}
