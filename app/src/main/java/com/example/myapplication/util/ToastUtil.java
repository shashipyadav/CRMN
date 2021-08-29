package com.example.myapplication.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showToastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}

