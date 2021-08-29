package com.example.myapplication.user_interface.menu.controller;

import com.example.myapplication.user_interface.menu.model.DrawerItem;

public interface OnMenuClickListener {

    public void onMenuClicked(int position,DrawerItem item, int visibility);
}
