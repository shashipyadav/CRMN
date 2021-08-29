package com.example.myapplication.user_interface.upcoming_meeting.model;

import java.util.ArrayList;
import java.util.List;

public class FilterListMainModel {
    private String value;
    private boolean isSelectedmain;
    public  List<FilterListModel> filterList = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<FilterListModel> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<FilterListModel> filterList) {
        this.filterList = filterList;
    }

    public boolean isSelectedmain() {
        return isSelectedmain;
    }

    public void setSelectedmain(boolean selectedmain) {
        isSelectedmain = selectedmain;
    }
}
