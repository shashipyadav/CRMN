package com.example.myapplication.helper;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.user_interface.forms.controller.helper.FieldHelper;

import java.util.Arrays;
import java.util.List;

public class DefaultValueFormatter {

    private static final String DEBUG_TAG = DefaultValueFormatter.class.getSimpleName();
    private Context context;
    private SharedPrefManager prefManager;

    public DefaultValueFormatter(Context context) {
        this.context = context;
        prefManager = new SharedPrefManager(context);
    }

    public static String containsAny(String str)
    {
        Log.i(DEBUG_TAG, "containsAny default value = "+ str);
        //  boolean bResult=false; // will be set, if any of the words are found
        String[] words = {"EDFFPOPUP","SQLTRUE", "VLIST", "SCROLL", "NEXT","FUNCTIONDFF","DFFPOPUP","FUNCTION","SQL","DFF","SQLFUNCTION","NOPOPUP","NOALL"};

        List<String> list = Arrays.asList(words);
        for (String word: list ) {
            boolean bFound = str.contains(word);
            if (bFound) {
                str = str.replace(word, "");
                str = str.replaceAll("%%","%");
                str = str.replace("SQL",""); // todo : solve this sql remains after removing
                //   Log.e(DEBUG_TAG, "DEFAULT STRING = "+ str +" WORD =" +word);
                return  str;

            }
        }

//        //LOCATION, EMAIL, EXT, SIZEINMB, AUTO, Auto, CAPS,
//        String[] words1 = {"LOCATION","EMAIL", "EXT", "SIZEINMB", "AUTO","Auto","CAPS"};
//        List<String> list1 = Arrays.asList(words1);
//        for (String word: list1 ) {
//            boolean bFound = formatedValue.contains(word);
//            if (bFound) {
//                formatedValue = "";
//                break;
//            }
//        }

     //   Log.i(DEBUG_TAG, "containsAny formatted value = "+ str);
        return str;
    }
}
