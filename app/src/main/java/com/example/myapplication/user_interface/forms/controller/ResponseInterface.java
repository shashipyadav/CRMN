package com.example.myapplication.user_interface.forms.controller;

public interface ResponseInterface {

    void onSuccessResponse(String recordId,boolean isFieldTypeTag);
    void onChangeState(String recordId);
}
