package com.example.myapplication.util;

import android.widget.EditText;

public class ValidateUtil {

    public static boolean isEmpty(EditText editText){
        if(editText.getText().length() == 0)
            return true;
        else
            return false;
    }
}
