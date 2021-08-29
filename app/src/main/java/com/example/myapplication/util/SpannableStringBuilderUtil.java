package com.example.myapplication.util;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class SpannableStringBuilderUtil {

    public static SpannableStringBuilder appendString(String[] textList, int[] colors)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        try {
            for (int i = 0; i < textList.length; i++) {
                SpannableString spannableString = new SpannableString(textList[i]);
                spannableString.setSpan(new ForegroundColorSpan(colors[i]), 0, textList[i].length(), 0);
                builder.append(spannableString);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return builder;
    }


    public static String[] getErrorTextList(String fieldName, boolean isInvalid) {
        if(isInvalid){
            return new String[]{fieldName, " * ","[INVALID DATA]"};
        }else{
            return new String[]{fieldName, " * ", "[REQUIRED]"};
        }
    }

    public static String[] getTextList(String fieldName) {
        return new String[]{fieldName, " *"};
    }




}
