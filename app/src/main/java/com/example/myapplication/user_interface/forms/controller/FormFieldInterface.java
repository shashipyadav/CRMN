package com.example.myapplication.user_interface.forms.controller;

import com.example.myapplication.user_interface.forms.model.Field;

public interface FormFieldInterface {

    public void onKeyDown(int position, Field obj);

    public void onChange(int position, String onChange, String value, String fieldType);

    public void evaluateSqlWithPayload(int position,String onChange);

    public void loadSpinner(int position, String onClickRightButton);

    public void loadDatePicker(int position,String onChange);

    public void onClickRightButton(int position, String onKeyDown, String onClickRightBtnFunc, String fieldType,String fieldName);

    public void onValueChanged(int position, String value, String onChange);

    public void fetchRecord(int position, String recordId, String uniqueValue);

    public void pickFile(int position);

    public void checkHideShow(String funcString);
}


