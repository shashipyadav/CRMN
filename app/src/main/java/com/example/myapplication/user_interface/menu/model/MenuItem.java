package com.example.myapplication.user_interface.menu.model;

import java.util.HashMap;
import java.util.List;

public class MenuItem {

    String id;
    String text;
    String parentId;
    HashMap<MenuItem, List<MenuItem>> listDataChild;





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }


    public HashMap<MenuItem, List<MenuItem>> getListDataChild() {
        return listDataChild;
    }

    public void setListDataChild(HashMap<MenuItem, List<MenuItem>> listDataChild) {
        this.listDataChild = listDataChild;
    }
}
