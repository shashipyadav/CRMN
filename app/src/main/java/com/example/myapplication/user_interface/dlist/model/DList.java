package com.example.myapplication.user_interface.dlist.model;

import android.util.Log;

import com.example.myapplication.user_interface.forms.model.OptionModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DList implements Serializable {

    private String mDropDownClick;
    private String mReadOnly;
    private int mSrNo;
    private String mName;
    private String mId;
    private String mAddFunction;
    private String mOnKeyDown;
    private String mType;
    private String mValue;
    private String mFieldType;
    private String mFieldName;
    private String mSaveRequired;
    private String mSearchRequired;
    private List<String> mDropDownList;
    private List<DList> mFormFieldList;
    private boolean mIsSelected;
    private String mStates;
    private String fieldid;
    private List<OptionModel> mOptionsArrayList;
    private boolean mShowErrorMessage   = false;
    private String errorMessage = "";
    private String mDefaultValue;


    public DList() {
        this("", "", 0,
                "", "", "",
                "", "", "",
                "", "", "",
                "","","");
    }

    public DList(String dropDownClick,
                 String readOnly,
                 int srNo,
                 String name,
                 String id,
                 String addFunction,
                 String onKeyDown,
                 String type,
                 String value,
                 String fieldType,
                 String fieldName,
                 String saveRequired,
                 String searchRequired,
                 String states,
                 String defaultValue) {

        this(dropDownClick, readOnly, srNo, name,
                id, addFunction, onKeyDown, type,
                value, fieldType, fieldName, saveRequired,
                searchRequired, new ArrayList<String>(),
                new ArrayList<DList>(),
                false,states,defaultValue);
    }

    public DList(String dropDownClick,
                 String readOnly,
                 int srNo,
                 String name,
                 String id,
                 String addFunction,
                 String onKeyDown,
                 String type,
                 String value,
                 String fieldType,
                 String fieldName,
                 String saveRequired,
                 String searchRequired,
                 List<String> dropDownList,
                 List<DList> formFieldList,
                 boolean isSelected,
                 String states,
                 String defaultValue) {

        this.mDropDownClick = dropDownClick;
        this.mReadOnly = readOnly;
        this.mSrNo = srNo;
        this.mName = name;
        this.mId = id;
        this.mAddFunction = addFunction;
        this.mOnKeyDown = onKeyDown;
        this.mType = type;
        this.mValue = value;
        this.mFieldType = fieldType;
        this.mFieldName = fieldName;
        this.mSaveRequired = saveRequired;
        this.mSearchRequired = searchRequired;
        this.mDropDownList = dropDownList;
        this.mFormFieldList = formFieldList;
        this.mIsSelected = isSelected;
        this.mStates = states;
        this.mDefaultValue = defaultValue;
    }

    public String getDropDownClick() {
        return mDropDownClick;
    }

    public void setDropDownClick(String dropDownClick) {
        this.mDropDownClick = dropDownClick;
    }

    public String getReadOnly() {
        return mReadOnly;
    }

    public void setReadOnly(String readOnly) {
        this.mReadOnly = readOnly;
    }

    public int getSrNo() {
        return mSrNo;
    }

    public void setSrNo(int srNo) {
        this.mSrNo = srNo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getAddFunction() {
        return mAddFunction;
    }

    public void setAddFunction(String addFunction) {
        this.mAddFunction = addFunction;
    }

    public String getOnKeyDown() {
        return mOnKeyDown;
    }

    public void setOnKeyDown(String onKeyDown) {
        this.mOnKeyDown = onKeyDown;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getValue() {
//        String value = mValue;
//        try{
//            if(getFieldType().toLowerCase().equals("double")){
//                if(!value.isEmpty()){
//                    double d = Double.parseDouble(mValue);
//                  value = new DecimalFormat("0.000").format(d);
//                }
//            }else{
//                value =  mValue;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.e("getValue = ","value = " + mValue);
//            value = mValue;
//        }
//        return value;



        if(getFieldType().toLowerCase().matches("checkbox|boolean")){
            if(mValue.isEmpty()){
                mValue = "false";
            }
        }
        return mValue;
    }

    public void setHiddenOrReadOnly() {
        //applicable only for view and vfunction
        if(getFieldType().toLowerCase().matches("view|vfunction")) {
            if(!getFieldType().toLowerCase().matches("hidden")){
                setSaveRequired("read");
            }
        }
    }

    public void setValue(String value) {
        this.mValue = value;
    }

    public String getFieldType() {
      //  setHiddenOrReadOnly();
        return mFieldType;
    }

    public void setFieldType(String fieldType) {
        this.mFieldType = fieldType;
        setHiddenOrReadOnly();
    }

    public String getFieldName() {
        return mFieldName;
    }

    public void setFieldName(String fieldName) {
        this.mFieldName = fieldName;
    }

    public List<DList> getFormFieldList() {
        return mFormFieldList;
    }

    public void setFormFieldList(List<DList> formFieldList) {
        this.mFormFieldList = formFieldList;
    }

    public List<String> getDropDownList() {
        return mDropDownList;
    }

    public void setDropDownList(List<String> dropDownList) {
        this.mDropDownList = dropDownList;
    }

    public String getSaveRequired() {
        return mSaveRequired;
    }

    public void setSaveRequired(String saveRequired) {
        this.mSaveRequired = saveRequired;
    }

    public String getSearchRequired() {
        return mSearchRequired;
    }

    public void setSearchRequired(String searchRequired) {
        this.mSearchRequired = searchRequired;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public boolean isHidden(){
        boolean isHidden = false;
        if(getType().toLowerCase().equals("hidden")){
            isHidden = true;
        }if(getFieldType().toLowerCase().equals("hidden")){
            isHidden = true;
        }if(getSearchRequired().toLowerCase().equals("false")){
            isHidden = true;
        }
        return isHidden;
    }

    public String getStates() {
        return mStates;
    }

    public void setStates(String states) {
        this.mStates = states;
    }

    public String getFieldid() {
        return fieldid;
    }

    public void setFieldid(String fieldid) {
        this.fieldid = fieldid;
        Log.e("Dlist Model", "fieldid = " + this.fieldid);
    }

    public List<OptionModel> getOptionsArrayList() {
        return mOptionsArrayList;
    }

    public void setOptionsArrayList(List<OptionModel> optionsArrayList) {
        this.mOptionsArrayList = optionsArrayList;
    }

    public boolean isReadOnly(){
        if (getSaveRequired().equalsIgnoreCase("read")) {
            return true;
        }
        return false;
    }

    public boolean showErrorMessage() {
        return mShowErrorMessage;
    }

    public void setShowErrorMessage(boolean showErrorMessage) {
        this.mShowErrorMessage = showErrorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDefaultValue() {
        return mDefaultValue;
    }

    public void setDefaultValue(String mDefaultValue) {
        this.mDefaultValue = mDefaultValue;
    }
}
