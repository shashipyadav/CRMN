package com.example.myapplication.function;

import android.util.Log;

import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.view.DListFormActivity;
import com.example.myapplication.user_interface.forms.model.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckPatternFunction {

    private List<Field> fieldsList;
    private int dlistArrayPosition;
    private List<Field> additionalFieldDataList;
    private  List<DList> dlistFieldValues;

    public  int checkPattern(String fieldId,
                                   String regexPattern,
                                   String extension,
                                   String fieldName,
                                   boolean isDlist) {

        regexPattern = regexPattern.replace("\\\\", "\\");

        int position = -1;
        try {

            String field = "field" + fieldId + extension;
            String fieldValue = "";
            boolean isFieldFound = false;
            if (field.length() == 0) {
                Log.e("checkPattern", "line#844 field value is empty - return");
                return -1;
            }

            if (isDlist) {

             //   List<DList> dlist = DListFormActivity.dlistFieldValues;
                String field_id = "field" + fieldId + extension;
                for (int j = 0; j < getDlistFieldValues().size(); j++) {
                    DList obj = getDlistFieldValues().get(j);
                    if (obj.getId().equals(field_id)) {

                        fieldValue = obj.getValue();
                        position = j;
                        isFieldFound = true;
                        break;
                    }
                }


                if (!isFieldFound) {
                    Log.e("checkPattern", "line#865 return");
                    return -1;
                }

            } else {
                try {

                    for (int i = 0; i < getFieldsList().size(); i++) {
                        Field fieldObj = getFieldsList().get(i);
                        if (fieldObj.getId().equals(field)) {
                            fieldValue = fieldObj.getValue();
                            isFieldFound = true;
                            position = i;
                            break;
                        }
                    }

                    if (!isFieldFound) {
                        Log.e("checkPattern", "line#865 return");
                        return -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (fieldName.toLowerCase().contains("e-mail") ||
                    fieldName.toLowerCase().contains("email") ||
                    regexPattern.toLowerCase().contains("email")) {

                regexPattern = "([\\w\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
            } else if (fieldName.toLowerCase().contains("mobile")
                    || regexPattern.toLowerCase().contains("mobile")) {

                regexPattern = "^[0-9\\-\\+]{9,15}$";
            } else if (regexPattern.toLowerCase().contains("aadhar")) {
                regexPattern = "(?!0{12})^[0-9][0-9]{11}$";
            } else if (regexPattern.toLowerCase().contains("url")) {
                regexPattern = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
            }

            if (!fieldValue.equals("")) {
                Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);

                Matcher matcher = pattern.matcher(fieldValue);
                boolean matchFound = matcher.find();

                if (!matchFound) {
                    //add error to field
                    if (position != -1) {

                        if (isDlist) {

                            getDlistFieldValues().get(position).setShowErrorMessage(true);
                            getDlistFieldValues().get(position).setValue("");
                            getDlistFieldValues().get(position).setErrorMessage("Enter a valid " + fieldName);

                        } else {
                            getFieldsList().get(position).setShowErrorMessage(true);
                            getFieldsList().get(position).setValue("");
                            getFieldsList().get(position).setErrorMessage("Enter a valid " + fieldName);
                        }
                    }
                    Log.e("Pattern doesn't match", "  -- Add Error");

                } else {
                    if (position != -1) {
                        if (isDlist) {
                            getDlistFieldValues().get(position).setShowErrorMessage(false);
                            getDlistFieldValues().get(position).setErrorMessage("");
                        } else {
                            getFieldsList().get(position).setShowErrorMessage(false);
                            getFieldsList().get(position).setErrorMessage("");
                        }
                    }
                    Log.e("Pattern matches", "  -- Remove Error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if pattern doesn't match return the position of the field else return -1
        Log.e("check pattern", "RESULT POSITION = " + position);
        return position;
    }

    public List<Field> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<Field> fieldsList) {
        this.fieldsList = fieldsList;
    }

    public int getDlistArrayPosition() {
        return dlistArrayPosition;
    }

    public void setDlistArrayPosition(int dlistArrayPosition) {
        this.dlistArrayPosition = dlistArrayPosition;
    }

    public List<Field> getAdditionalFieldDataList() {
        return additionalFieldDataList;
    }

    public void setAdditionalFieldDataList(List<Field> additionalFieldDataList) {
        this.additionalFieldDataList = additionalFieldDataList;
    }

    public List<DList> getDlistFieldValues() {
        return dlistFieldValues;
    }

    public void setDlistFieldValues(List<DList> dlistFieldValues) {
        this.dlistFieldValues = dlistFieldValues;
    }
}
