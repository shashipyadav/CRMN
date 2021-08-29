package com.example.myapplication.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }
}
