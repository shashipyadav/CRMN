package com.example.myapplication.user_interface.dlist.model;

import org.json.JSONArray;
import org.json.JSONObject;


public class DlistRowItem {

    private JSONArray dlistArray;
    private boolean mIsSelected;

    public DlistRowItem(JSONArray dlistArray, boolean isSelected) {
        this.dlistArray = dlistArray;
        this.mIsSelected = isSelected;
    }

    public JSONArray getDlistArray() {
        return dlistArray;
    }

    public void setDlistArray(JSONArray dlistArray) {
        this.dlistArray = dlistArray;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }
}
