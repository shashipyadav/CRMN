package com.example.myapplication.user_interface.quicklink;

public class QuickLink {

    private String chartId;
    private String mTitle;

    public QuickLink(String chartId, String title) {
        this.chartId = chartId;
        this.mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getChartId() {
        return chartId;
    }

    public void setChartId(String chartId) {
        this.chartId = chartId;
    }
}
