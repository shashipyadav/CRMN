package com.example.myapplication.user_interface.dlist.model;

import java.io.Serializable;
import java.util.List;

public class DListItem implements Serializable {

    private List<DList> dlistArray;
    private boolean mIsSelected;

    public DListItem(List<DList> dlistArray, boolean isSelected) {
        this.dlistArray = dlistArray;
        this.mIsSelected = isSelected;
    }

    public DListItem(List<DList> dlistArray) {
        this.dlistArray = dlistArray;
    }

    public List<DList> getDlistArray() {
        return dlistArray;
    }

    public void setDlistArray(List<DList> dlistArray) {
        this.dlistArray = dlistArray;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }
}
