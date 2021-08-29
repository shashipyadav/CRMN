package com.example.myapplication.user_interface.vlist;

public class Vlist {

    String columnField;
    String fieldValue;
    String columnTitle;

    public Vlist(String fieldName, String fieldValue,String title) {
        this.columnField = fieldName;
        this.fieldValue = fieldValue;
        this.columnTitle = title;
    }

    public String getColumnField() {
        return columnField;
    }

    public void setColumnField(String columnField) {
        this.columnField = columnField;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }
}
