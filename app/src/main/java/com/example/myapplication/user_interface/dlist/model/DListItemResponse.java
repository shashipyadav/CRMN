package com.example.myapplication.user_interface.dlist.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DListItemResponse {
    @SerializedName("dlistArray")
    private List<DListItem> dListItemList;

    public List<DListItem> getdListItemList() {
        return dListItemList;
    }

    public void setdListItemList(List<DListItem> dListItemList) {
        this.dListItemList = dListItemList;
    }
}
