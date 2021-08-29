package com.example.myapplication.user_interface.forms.controller.helper;

import android.util.Log;

import com.example.myapplication.user_interface.forms.model.OptionModel;

import java.util.List;

public class SpinnerHelper {
    private static final String DEBUG_TAG = SpinnerHelper.class.getSimpleName();

    public static int getIndex(List<OptionModel> optionsArrayList,String selectedValue){
        if(optionsArrayList != null){
            for(int i=0; i<optionsArrayList.size(); i++) {
                if(optionsArrayList.get(i).getId().equals(selectedValue)){
                    Log.e(DEBUG_TAG, "selectedValue = " + optionsArrayList.get(i));
                    return i;
                }
            }
        }
        return 0;
    }
}
