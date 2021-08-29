package com.example.myapplication.user_interface.menu.controller;

import android.content.Context;

import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.menu.model.DrawerItem;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuHelper {

    private final String DEBUG_TAG = MenuHelper.class.getSimpleName();
    private Context mContext;

    public MenuHelper(Context context) {
        this.mContext = context;
    }

    ArrayList<DrawerItem> menuList = new ArrayList<>();
    ArrayList<DrawerItem> dashboardList = new ArrayList<>();

    public ArrayList<DrawerItem> setMenu(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String title = jsonObject1.getString("title");
                //To make a separate menulist for dashboard  which will be shown under Dashboard in bottomnavigation
                String[] menuwe = title.split("-->");
                String firstItemww = menuwe[0];
                if (firstItemww.toLowerCase().equalsIgnoreCase("Pre Sales") /*||
                        firstItemww.toLowerCase().equalsIgnoreCase("Post Sales") || firstItemww.toLowerCase().equalsIgnoreCase("Post Sales MIS")*/) {



                    if (title.toLowerCase().contains("dashboard")) {

                    DrawerItem item = new Gson().fromJson(jsonObject1.toString(), DrawerItem.class);
                    dashboardList.add(item);
                }

                // check the count of title present in a particular string
                int count = title.length() - title.replaceAll("-->", "@").length();

                if (count == 0) {

                    DrawerItem firstItem = new Gson().fromJson(jsonObject1.toString(), DrawerItem.class);
                    if (!title.toLowerCase().matches("pending tasks|dashboard|ess")) {
                        menuList.add(firstItem);
                    }

                } else if (count == 2) {
                    String[] menu = title.split("-->");
                    String firstItem = menu[0];
                    String secondItem = menu[1];


                    int index = getMenuIndex(firstItem);

                    if (index != -1) {
                        DrawerItem drawerItem = new Gson().fromJson(jsonObject1.toString(), DrawerItem.class);
                        drawerItem.setTitle(secondItem);
                        ArrayList<DrawerItem> mHeaderList = menuList.get(index).getListHeader();
                        mHeaderList.add(drawerItem);
                        menuList.get(index).setListHeader(mHeaderList);
                    } else {

                        DrawerItem parentItem = new DrawerItem();
                        parentItem.setTitle(firstItem);

                        DrawerItem childItem = new Gson().fromJson(jsonObject1.toString(), DrawerItem.class);
                        childItem.setTitle(secondItem);
                        ArrayList<DrawerItem> headerList = new ArrayList<>();
                        headerList.add(childItem);
                        parentItem.setListHeader(headerList);
                        if (!firstItem.toLowerCase().matches("pending tasks|dashboard|ess")) {
                            menuList.add(parentItem);
                        }

                    }

                } else if (count >= 3) {
                    //handle 3 level here
                    String[] menu = title.split("-->");
                    String menu0 = menu[0];
                    String menu1 = menu[1];
                    String menu2 = menu[2];

                    int index = getMenuIndex(menu0);
                    if (index != -1) {
                        DrawerItem secondItem = new DrawerItem();
                        secondItem.setTitle(menu1);

                        ArrayList<DrawerItem> mHeaderList = menuList.get(index).getListHeader();
                        int sIndex = getSecondItemIndex(menu1, mHeaderList);
                        if (sIndex != -1) {

                            DrawerItem sItem = menuList.get(index).getListHeader().get(sIndex);
                            List<DrawerItem> listChildArray = new ArrayList<>();
                            HashMap<DrawerItem, List<DrawerItem>> listHashMap = menuList.get(index).getListDataChild();

                            for (DrawerItem key : listHashMap.keySet()) {
                                List<DrawerItem> chilArrayList = listHashMap.get(key);
                                if (chilArrayList != null) {

                                    DrawerItem lastItem = new Gson().fromJson(jsonObject1.toString(), DrawerItem.class);
                                    lastItem.setTitle(menu2);
                                    chilArrayList.add(lastItem);
                                } else {
                                    chilArrayList = new ArrayList<>();
                                    DrawerItem lastItem = new Gson().fromJson(jsonObject1.toString(), DrawerItem.class);
                                    lastItem.setTitle(menu2);

                                    chilArrayList.add(lastItem);
                                    listHashMap.put(sItem, listChildArray);
                                }
                            }
                            menuList.get(index).setListDataChild(listHashMap);
                        } else {

                            mHeaderList.add(secondItem);
                            menuList.get(index).setListHeader(mHeaderList);

                            List<DrawerItem> listChildArray = new ArrayList<>();
                            HashMap<DrawerItem, List<DrawerItem>> listHashMap = new HashMap<>();
                            DrawerItem childItem = new Gson().fromJson(jsonObject1.toString(), DrawerItem.class);
                            childItem.setTitle(menu2);

                            listChildArray.add(childItem);
                            listHashMap.put(secondItem, listChildArray);
                            menuList.get(index).setListDataChild(listHashMap);
                        }
                    } else {
                        DrawerItem firstItem = new DrawerItem();
                        firstItem.setTitle(menu0);

                        DrawerItem secondItem = new DrawerItem();
                        secondItem.setTitle(menu1);

                        ArrayList<DrawerItem> mHeaderList = new ArrayList<>();
                        mHeaderList.add(secondItem);
                        firstItem.setListHeader(mHeaderList);

                        List<DrawerItem> listChildArray = new ArrayList<>();
                        HashMap<DrawerItem, List<DrawerItem>> listHashMap = new HashMap<>();

                        DrawerItem lastChild = new Gson().fromJson(jsonObject1.toString(), DrawerItem.class);
                        lastChild.setTitle(menu2);
                        listChildArray.add(lastChild);

                        listHashMap.put(secondItem, listChildArray);
                        firstItem.setListDataChild(listHashMap);

                        if (!menu0.toLowerCase().matches("pending tasks|dashboard|ess")) {
                            menuList.add(firstItem);
                        }
                    }
                }
            }
                /////
            }

           /* DrawerItem item = new DrawerItem();
            item.setTitle("Ess");
            item.setOnClick("");
            menuList.add(item);*/

            saveDashboardMenu();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return menuList;
    }


    private int getMenuIndex(String menuTitle) {
        for (int i = 0; i < menuList.size(); i++) {
            DrawerItem drawerItem = menuList.get(i);

            if (drawerItem.getTitle().equalsIgnoreCase(menuTitle)) {
                return i;
            }
        }
        return -1;
    }

    private int getSecondItemIndex(String menuTitle, List<DrawerItem> headerList) {
        for (int i = 0; i < headerList.size(); i++) {
            DrawerItem drawerItem = headerList.get(i);

            if (drawerItem.getTitle().equalsIgnoreCase(menuTitle)) {
                return i;
            }
        }
        return -1;
    }

    private void saveDashboardMenu() {
        SharedPrefManager prefManager = new SharedPrefManager(mContext);
        Gson gson = new Gson();
        String jsonT = gson.toJson(dashboardList);
        prefManager.setDashboardMenu(jsonT);
    }
}
