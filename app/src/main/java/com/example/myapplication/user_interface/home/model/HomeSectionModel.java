package com.example.myapplication.user_interface.home.model;

import com.google.gson.annotations.SerializedName;

public class HomeSectionModel {

    private int mImageURL;
    private String mTitle = "";
    private String mDesc = "";
    private String mAmount = "";

    @SerializedName("API_ID")
    private String API_ID;

    @SerializedName("Section_Name")
    private String Section_Name;

    @SerializedName("Image_Path")
    private String Image_Path;

    @SerializedName("Seq")
    private String Seq;

    @SerializedName("Elongated")
    private int elongdated; // 1 means show elongated images 0 means show square image

    @SerializedName("GridCode")
    private int gridCode; // 1 = 1x1, 2= 2x2

    public HomeSectionModel() {
    }

    public HomeSectionModel(int imageURL, String title, String desc, String amount) {
        this.mImageURL = imageURL;
        this.mTitle = title;
        this.mDesc = desc;
        this.mAmount = amount;
    }

    public int getImageURL() {
        return mImageURL;
    }

    public void setImageURL(int imageURL) {
        this.mImageURL = imageURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        this.mAmount = amount;
    }

    public String getAPI_ID() {
        return API_ID;
    }

    public void setAPI_ID(String API_ID) {
        this.API_ID = API_ID;
    }

    public String getSection_Name() {
        return Section_Name;
    }

    public void setSection_Name(String section_Name) {
        Section_Name = section_Name;
    }

    public String getImage_Path() {
        return Image_Path;
    }

    public void setImage_Path(String image_Path) {
        Image_Path = image_Path;
    }

    public String getSeq() {
        return Seq;
    }

    public void setSeq(String seq) {
        Seq = seq;
    }

    public int getElongdated() {
        return elongdated;
    }

    public void setElongdated(int elongdated) {
        this.elongdated = elongdated;
    }

    public int getGridCode() {
        return gridCode;
    }

    public void setGridCode(int gridCode) {
        this.gridCode = gridCode;
    }
}
