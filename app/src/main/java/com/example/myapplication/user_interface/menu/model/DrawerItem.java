package com.example.myapplication.user_interface.menu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawerItem implements Serializable, Parcelable {


    @SerializedName("drilldown")
    private String drillDown;
    @SerializedName("showname")
    private String showName;
    @SerializedName("onclick")
    private String onClick = "";
    @SerializedName("charttype")
    private String chartType = "";
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String mTitle;
    @Expose(deserialize = false)
    private boolean isSelected = false;
    @Expose(deserialize = false)
    private int parentId;
    @Expose(deserialize = false)
    private HashMap<DrawerItem, List<DrawerItem>> listDataChild;
    @Expose(deserialize = false)
    private ArrayList<DrawerItem> listHeader;


    public DrawerItem() {
        listHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
    }

    public DrawerItem(Parcel in) {
        super();
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public HashMap<DrawerItem, List<DrawerItem>> getListDataChild() {
        return listDataChild;
    }

    public void setListDataChild(HashMap<DrawerItem, List<DrawerItem>> listDataChild) {
        this.listDataChild = listDataChild;
    }

    public String getOnClick() {
        return onClick;
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ArrayList<DrawerItem> getListHeader() {
        return listHeader;
    }

    public void setListHeader(ArrayList<DrawerItem> listHeader) {
        this.listHeader = listHeader;
    }

    public String getDrillDown() {
        return drillDown;
    }

    public void setDrillDown(String drillDown) {
        this.drillDown = drillDown;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public void readFromParcel(Parcel in) {
        isSelected = in.readByte() != 0;
        drillDown = in.readString();
        showName = in.readString();
        chartType = in.readString();
        id = in.readInt();
        mTitle = in.readString();
        onClick = in.readString();
        parentId = in.readInt();
        listHeader = in.createTypedArrayList(DrawerItem.CREATOR);
        listDataChild = (HashMap<DrawerItem, List<DrawerItem>>) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(drillDown);
        dest.writeString(showName);
        dest.writeString(chartType);
        dest.writeInt(id);
        dest.writeString(mTitle);
        dest.writeString(onClick);
        dest.writeInt(parentId);
        dest.writeTypedList(listHeader);
        dest.writeSerializable(listDataChild);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }


    public static final Parcelable.Creator<DrawerItem> CREATOR = new Parcelable.Creator<DrawerItem>() {
        public DrawerItem createFromParcel(Parcel in) {
            return new DrawerItem(in);
        }

        public DrawerItem[] newArray(int size) {

            return new DrawerItem[size];
        }
    };
}
