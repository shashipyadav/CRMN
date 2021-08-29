package com.example.myapplication.user_interface.pendingtask;

import com.google.gson.annotations.SerializedName;

public class TaskInfoItem {

    @SerializedName("Field_Value")
    private String fieldValue;

    @SerializedName("Field_Name")
    private String fieldName;

    @SerializedName("Field_Type")
    private String fieldType;

    @SerializedName("Field_ID")
    private String fieldId;

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

}
