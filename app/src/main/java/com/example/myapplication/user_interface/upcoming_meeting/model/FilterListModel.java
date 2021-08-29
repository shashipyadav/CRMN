package com.example.myapplication.user_interface.upcoming_meeting.model;

import java.util.logging.Filter;

public class FilterListModel {
    private String title;
    private boolean isSelected;

    public FilterListModel(){

    }

    public FilterListModel(String title){
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [title = "+title+"]";
    }
}
