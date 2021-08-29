package com.example.myapplication.user_interface.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class FilterMoreModel {
    private String fieldName = "";
    private String dataType="";
    private String fieldId="";
    private String value="";
    private List<String> dropdownList = new ArrayList<>();

    public FilterMoreModel( String fieldId,String fieldName, String dataType) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.dataType = dataType;

    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getDropdownList() {
        return dropdownList;
    }

    public void setDropdownList(List<String> dropdownList) {
        this.dropdownList = dropdownList;
    }
}
