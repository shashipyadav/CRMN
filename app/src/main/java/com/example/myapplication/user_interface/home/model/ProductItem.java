package com.example.myapplication.user_interface.home.model;

import android.util.Log;

import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListMainModel;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListModel;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

public class ProductItem implements Serializable {

    @SerializedName("itmGroup")
    private String itmGroup;

    @SerializedName("itemID")
    private String itemID;

    @SerializedName("itemName")
    @Expose
    private String itemName;

    @SerializedName("itmCategory")
    private String itmCategory;

    @SerializedName("itemDetails")
    private String itemDetails;

    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;

    private Product productInfo;

    public String getItmGroup() {
        return itmGroup;
    }

    public void setItmGroup(String itmGroup) {
        this.itmGroup = itmGroup;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItmCategory() {
        return itmCategory;
    }

    public void setItmCategory(String itmCategory) {
        this.itmCategory = itmCategory;
    }

    public String getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(String itemDetails) {
        this.itemDetails = itemDetails;
    }

    public Product getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(Product productInfo) {
        this.productInfo = productInfo;
    }

    /**
     * @param details is the String we get from api which we wish to map to Product
     */
    public void convertStringToObject(String details) {
        if (!details.equals("[]")) {
            Type productType = new TypeToken<List<Product>>() {
            }.getType();
            List<Product> list = new Gson().fromJson(details, productType);

            if (!list.isEmpty()) {
                productInfo = list.get(0);
                //  Log.e("ITEM DESC = ",productInfo.getItemDesc());
            }
        }
    }

    /**
     * Method to add the video to the imagefullview list so that we can show it in viewpager
     * in ProductDetailsActivity
     */
    public void addVideoLinkToList() {
        if (productInfo != null) {
            if (!productInfo.getVideoSd().equals("")) {
                getProductInfo().getImageFullView().add(productInfo.getVideoSd());
            }
        }
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }
}
