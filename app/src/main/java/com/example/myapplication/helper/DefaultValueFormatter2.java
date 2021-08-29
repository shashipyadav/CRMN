package com.example.myapplication.helper;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.forms.controller.helper.FieldHelper;
import com.example.myapplication.util.DateUtil;

import java.util.Arrays;
import java.util.List;

public class DefaultValueFormatter2 {

    private static final String DEBUG_TAG = DefaultValueFormatter.class.getSimpleName();
    private Context context;
    private static SharedPrefManager prefManager;
    private String fieldValue;
    private String defaultValue;

    public DefaultValueFormatter2(Context context) {
        this.context = context;
        prefManager = new SharedPrefManager(context);
    }

    //based on the formated default value we need to show certain values.

    public  String getFormattedValue() {
        String finalValue = "";
        String formatedDefaultValue = "";
        if (!defaultValue.isEmpty()) {
            formatedDefaultValue = containsAny(defaultValue);
        }

        if (getFieldValue().equals("0")) {
            if (formatedDefaultValue.toLowerCase().matches("\\$\\{self\\}|\\$\\{username\\}|\\$\\{login\\}|\\$username|\\$self")) {
                finalValue = prefManager.getUserName();
            } else {
                if (formatedDefaultValue.matches("EMAIL|CAPS|auto|AUTO|LOCATION|LOCATION\\$location")) {
                    if (formatedDefaultValue.matches("LOCATION|LOCATION\\$location")) {
                        //  locationListener.displayLocation(position, "address");
                        finalValue = "address";
                    } else if (formatedDefaultValue.matches("\\$location")) {
                        finalValue = "location";
                        // locationListener.displayLocation(position, "location");
                    } else {
                        finalValue  = getFieldValue();
                    }
                } else {
                    FieldHelper fieldHelper = new FieldHelper(context);
                    finalValue = fieldHelper.getValue(formatedDefaultValue);
                }
            }
        } else {
            //check if field value contains a word similar to default value and format it accordingly
            FieldHelper fieldHelper = new FieldHelper(context);
            finalValue = fieldHelper.getValue(getFieldValue());

            if (formatedDefaultValue.toLowerCase().matches("\\$\\{self\\}|\\$\\{username\\}|\\$\\{login\\}|\\$username|\\$self")) {
                finalValue = prefManager.getUserName();
            } else {
                if (finalValue.matches("LOCATION|LOCATION\\$location")) {
                    finalValue = "address";
                } else if (finalValue.matches("\\$location")) {
                    finalValue = "location";
                } else {
                    finalValue = getFieldValue();
                }
            }
        }

        return finalValue;
    }

    //replace any words which we don't want to show in the field first.
    public static String containsAny(String defaultValue)
    {
        Log.i(DEBUG_TAG, "containsAny default value = "+ defaultValue);
        //  boolean bResult=false; // will be set, if any of the words are found
        String[] words = {"EDFFPOPUP","SQLTRUE", "VLIST", "SCROLL", "NEXT","FUNCTIONDFF","DFFPOPUP","FUNCTION","SQL","DFF","SQLFUNCTION","NOPOPUP","NOALL"};

        List<String> list = Arrays.asList(words);
        for (String word: list ) {
            boolean bFound = defaultValue.contains(word);
            if (bFound) {
                defaultValue = defaultValue.replace(word, "");
                defaultValue = defaultValue.replaceAll("%%","%");
                defaultValue = defaultValue.replace("SQL",""); // todo : solve this sql remains after removing
                //   Log.e(DEBUG_TAG, "DEFAULT STRING = "+ str +" WORD =" +word);
                return  defaultValue;

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
        return defaultValue;
    }


    public String formatDateDefaultValue(){
        String  dateString = fieldValue;
        String finalValue = "";
        if (!dateString.isEmpty()) {
            if( !dateString.equals("0")){
                if (defaultValue.matches("\\$\\{now\\}|field\\$\\{now\\}")) {
                    finalValue = DateUtil.formatDate(dateString, Constant.yyyy_MM_dd_now, Constant.dd_MM_yyyy_HH_mm);
                } else if (defaultValue.matches("\\$\\{today\\}|field\\$\\{today\\}")) {
                    finalValue = DateUtil.formatDate(dateString, Constant.yyyy_MM_dd, Constant.dd_MM_yyyy);
                } else {
                    finalValue = DateUtil.formatDate(dateString, Constant.yyyy_MM_dd, Constant.dd_MM_yyyy);
                }
            }else{
                finalValue = "";
            }
        } else {
            //if value is empty, set default value
            if (!defaultValue.equals("")) {
                String formatedDefaultValue = "";
                if (!defaultValue.isEmpty()) {
                    formatedDefaultValue = containsAny(defaultValue);
                }
                Log.e(DEBUG_TAG, "defaultValue = " + formatedDefaultValue);
                finalValue = DateUtil.getFormatedDate(formatedDefaultValue);
            }
        }

        return finalValue;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}

