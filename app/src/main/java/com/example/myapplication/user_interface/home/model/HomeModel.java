package com.example.myapplication.user_interface.home.model;

import java.util.ArrayList;
import java.util.List;

public class HomeModel {
    private String Order;
    private List<HomeSectionModel> homeSectionModelList = new ArrayList<>();


    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public List<HomeSectionModel> getProductList() {
        return homeSectionModelList;
    }

    public void setProductList(List<HomeSectionModel> homeSectionModelList) {
        this.homeSectionModelList = homeSectionModelList;
    }
}


