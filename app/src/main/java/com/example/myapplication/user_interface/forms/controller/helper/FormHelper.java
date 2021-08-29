package com.example.myapplication.user_interface.forms.controller.helper;

import android.widget.LinearLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

public class FormHelper {

    private static final String DEBUG_TAG = FormHelper.class.getSimpleName();

    public String getChartId(String menuOnClick ){
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(menuOnClick);
        if (m.find()) {
            System.out.println(m.group(0));
            return m.group(0);
        }
        return "";
    }



    public static void disableFormAdd(LinearLayout root){
        root.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    }
}
