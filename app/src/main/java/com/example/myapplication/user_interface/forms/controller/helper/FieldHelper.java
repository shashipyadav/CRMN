package com.example.myapplication.user_interface.forms.controller.helper;

import android.content.Context;

import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.view.FormFragment;

import java.util.List;

public class FieldHelper {
    private static final String DEBUG_TAG = FieldHelper.class.getSimpleName();
    public SharedPrefManager mPrefManager;

    public FieldHelper(Context context) {
        mPrefManager = new SharedPrefManager(context.
                getApplicationContext());
    }

    /**
     *
     * @param condition is the default value of that particular field.
     * @return if the condition has self,username  or login returns user's login name,  otherwise returns the condition.
     */
    public String getValue(String condition) {
        if (condition.toLowerCase().
                matches("\\$\\{self\\}|\\$\\{username\\}|\\$\\{login\\}|\\$username|\\$self|self")) {
            return mPrefManager.getUserName();
        }else if(condition.toLowerCase().matches("auto|sql")){ //location\$location|location|
            return "";
        } else {
            return condition;
        }
    }

    public String setUserName(String condition){
        if (condition.toLowerCase().
                matches("\\$\\{self\\}|\\$\\{username\\}|\\$\\{login\\}|\\$username|\\$self|self")) {
            return mPrefManager.getUserName();
        }

        return "";
    }

    public static String replaceUpdatetview(String onChange) {
        //. = non word characters i.e ',
        String regex = "updatetview\\((\\'[0-9]+)\\'\\,\\'(([0-9]{1,}\\/)+\\')\\,\\'(?:[A-Z]{1,}\\s[A-Z]{1,}|[A-Z]{1,}|[A-Z]{1,}\\W)\\'\\,\\'[A-Za-z\\s]+...[A-Za-z].+\\)\\;";
        onChange = onChange.replaceAll(regex, "");
        return onChange;
    }

    public static void hideShowMoreOptionsTab() {
        int dlistButtonPosition = FormFragment.dlistArrayPosition;

        boolean allDlistFieldsAreHidden = false;
        if(dlistButtonPosition != -1) {
            List<Field> dlistFieldArray = FormFragment.fieldsList.get(dlistButtonPosition).getdListArray();

            for(int i=0; i < dlistFieldArray.size(); i++) {

                Field dobj = dlistFieldArray.get(i);

                if(dobj.isHidden()){
                    allDlistFieldsAreHidden = true;
                }else{
                    allDlistFieldsAreHidden = true;
                }
            }
        }

        if(FormFragment.fieldsList != null) {
            Field moreOptionTabField = FormFragment.fieldsList.get(dlistButtonPosition);

            if(allDlistFieldsAreHidden){
                moreOptionTabField.setFieldType(moreOptionTabField.getFieldType());
                moreOptionTabField.setType("hidden");
            }else{
                moreOptionTabField.setType(moreOptionTabField.getFieldType());
            }
        }
    }
}
