package com.example.myapplication.user_interface.dashboard.model;

import com.google.gson.annotations.SerializedName;

public class ChartDataItem {

    @SerializedName(value = "Count", alternate = {"Amount","Percentage","cnt"})
    private int Count;
    @SerializedName(value = "Source", alternate = "Status")
    private String Source;

    public ChartDataItem(){
        this(0,"");
    }

    public ChartDataItem(int Count, String Source) {
        this.Count = Count;
        this.Source = Source;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        this.Source = source;
    }
}
