package com.example.myapplication.user_interface.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class Chart {

    private String mDataUrl = "";
    private String mFilter = "";
    private String mExtraParams = "";
    private int  mBlocks = 0;
    private String mChartType = "";
    private int mobileBlocks = 0;
    private String chartId = "";
    private String mChartTitle = "";
    private String mId = "";
    private String mJson = "";
    private String mMoreFilter = "";
    private List<ChartDataItem> mChartDataItemList = new ArrayList<>();

    public Chart(){

    }

    public String getChartType() {
        return mChartType;
    }

    public void setChartType(String chartType) {
        mChartType = chartType;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getChartId() {
        return chartId;
    }

    public void setChartId(String chartId) {
        this.chartId = chartId;
    }

    public List<ChartDataItem> getChartDataItemList() {
        return mChartDataItemList;
    }

    public void setChartDataItemList(List<ChartDataItem> chartDataItemList) {
        mChartDataItemList = chartDataItemList;
    }

    public String getDataUrl() {
        return mDataUrl;
    }

    public void setDataUrl(String dataUrl) {
        mDataUrl = dataUrl;
    }

    public String getFilter() {
        return mFilter;
    }

    public void setFilter(String filter) {
        mFilter = filter;
    }

    public String getExtraParams() {
        return mExtraParams;
    }

    public void setExtraParams(String extraParams) {
        mExtraParams = extraParams;
    }

    public int getBlocks() {
        return mBlocks;
    }

    public void setBlocks(int blocks) {
        mBlocks = blocks;
    }

    public int getMobileBlocks() {
        return mobileBlocks;
    }

    public void setMobileBlocks(int mobileBlocks) {
        this.mobileBlocks = mobileBlocks;
    }

    public String getChartTitle() {
        return mChartTitle;
    }

    public void setChartTitle(String chartTitle) {
        mChartTitle = chartTitle;
    }

    public String getJson() {
        return mJson;
    }

    public void setJson(String json) {
        mJson = json;
    }

    public String getMoreFilter() {
        return mMoreFilter;
    }

    public void setMoreFilter(String mMoreFilter) {
        this.mMoreFilter = mMoreFilter;
    }
}
