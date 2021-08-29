package com.example.myapplication.user_interface.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Product implements Serializable {



    @SerializedName("itm_basic_info")
    private List<LinkedHashMap<String,String>>  itmBasicInfo = null;

    @SerializedName("item_add_info")
    private List<LinkedHashMap<String,String>>  itemAddInfo = null;


    @SerializedName("Image_Thumbnail")
    private String imageThumbnail;

    @SerializedName("Image_FullView")
    private List<String> imageFullView;

    @SerializedName("Document_Brochure")
    private String documentBrochure;

    @SerializedName("Document_TechnicalDocument")
    private String technicalDocument;

    @SerializedName("Video_SD")
    private String videoSd;

    @SerializedName("Video_HD")
    private String videoHd;

    public Product(){
        this("");
    }

    public Product(String productImage){
       this( "",
              new ArrayList<String>(), "",
               "", "", "");
    }

    public Product( String imageThumbnail,
                   List<String> imageFullView, String documentBrochure,
                   String technicalDocument, String videoSd, String videoHd) {

        this.imageThumbnail = imageThumbnail;
        this.imageFullView = imageFullView;
        this.documentBrochure = documentBrochure;
        this.technicalDocument = technicalDocument;
        this.videoSd = videoSd;
        this.videoHd = videoHd;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public List<String> getImageFullView() {
        return imageFullView;
    }

    public void setImageFullView(List<String> imageFullView) {
        this.imageFullView = imageFullView;
    }


    public String getDocumentBrochure() {
        return documentBrochure;
    }

    public void setDocumentBrochure(String documentBrochure) {
        this.documentBrochure = documentBrochure;
    }

    public String getTechnicalDocument() {
        return technicalDocument;
    }

    public void setTechnicalDocument(String technicalDocument) {
        this.technicalDocument = technicalDocument;
    }

    public String getVideoSd() {
        return videoSd;
    }

    public void setVideoSd(String videoSd) {
        this.videoSd = videoSd;
    }

    public String getVideoHd() {
        return videoHd;
    }

    public void setVideoHd(String videoHd) {
        this.videoHd = videoHd;
    }

    public List<LinkedHashMap<String,String>>  getItmBasicInfo() {
        return itmBasicInfo;
    }

    public void setItmBasicInfo(List<LinkedHashMap<String,String>>  itmBasicInfo) {
        this.itmBasicInfo = itmBasicInfo;
    }

    public List<LinkedHashMap<String,String>>  getItemAddInfo() {
        return itemAddInfo;
    }

    public void setItemAddInfo(List<LinkedHashMap<String,String>>  itemAddInfo) {
        this.itemAddInfo = itemAddInfo;
    }
}
