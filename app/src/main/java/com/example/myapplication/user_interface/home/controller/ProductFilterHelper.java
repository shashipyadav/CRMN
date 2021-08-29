package com.example.myapplication.user_interface.home.controller;

import com.example.myapplication.user_interface.home.model.ProductItem;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListMainModel;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListModel;

import java.util.ArrayList;
import java.util.List;

public class ProductFilterHelper {

    public static List<FilterListMainModel> getProductFilterList(List<ProductItem> productItems){

        List<FilterListModel> designList = new ArrayList<>();
        List<FilterListModel> verticalList = new ArrayList<>();
        List<FilterListModel> categoryList = new ArrayList<>();
        List<FilterListModel> surfaceList = new ArrayList<>();
        List<FilterListModel> qualityList = new ArrayList<>();
        List<FilterListModel> sizeList = new ArrayList<>();

        if(productItems != null){

            for (ProductItem item : productItems) {
                if (item.getProductInfo() != null) {
                    //fix product filter

                 /*   if (!item.getProductInfo().getDesign().equals("")) {
                        designList.add(new FilterListModel(item.getProductInfo().getDesign()));
                    }

                    if (!item.getProductInfo().getVertical().equals("")) {
                        verticalList.add(new FilterListModel(item.getProductInfo().getVertical()));
                    }

                    if (!item.getProductInfo().getCategory().equals("")) {
                        categoryList.add(new FilterListModel(item.getProductInfo().getCategory()));
                    }

                    if (!item.getProductInfo().getSurface().equals("")) {
                        surfaceList.add(new FilterListModel(item.getProductInfo().getSurface()));
                    }
                    if (!item.getProductInfo().getQualityCode().equals("")) {
                        qualityList.add(new FilterListModel(item.getProductInfo().getQualityCode()));
                    }

                    if (!item.getProductInfo().getSize().equals("")) {
                        sizeList.add(new FilterListModel(item.getProductInfo().getSize()));
                    } */
                }
            }
        }

        List<FilterListMainModel> filterList = new ArrayList<>();
        FilterListMainModel filterObj = new FilterListMainModel();
        filterObj.setValue("Design");
        filterObj.setFilterList(designList);
        filterList.add(filterObj);

        filterObj = new FilterListMainModel();
        filterObj.setValue("Vertical");
        filterObj.setFilterList(verticalList);
        filterList.add(filterObj);

        filterObj = new FilterListMainModel();
        filterObj.setValue("Category");
        filterObj.setFilterList(categoryList);
        filterList.add(filterObj);

        filterObj = new FilterListMainModel();
        filterObj.setValue("Surface");
        filterObj.setFilterList(surfaceList);
        filterList.add(filterObj);

        filterObj = new FilterListMainModel();
        filterObj.setValue("Quality");
        filterObj.setFilterList(qualityList);
        filterList.add(filterObj);

        filterObj = new FilterListMainModel();
        filterObj.setValue("Size");
        filterObj.setFilterList(sizeList);
        filterList.add(filterObj);

        return filterList;
    }
}
