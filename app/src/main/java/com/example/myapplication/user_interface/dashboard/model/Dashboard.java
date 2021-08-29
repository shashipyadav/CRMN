package com.example.myapplication.user_interface.dashboard.model;

public class Dashboard {

    private String title = "";
    private String chartId = "";
    private String onClick = "";
    private String drillDown;
    private String showName;
    private String chartType = "";
    private int id;
    private String drillDownId;


    public Dashboard(String title, String chartId, String onClick,String drillDown, String showName,String chartType, int id,String drillDownId) {
        this.title = title;
        this.chartId = chartId;
        this.onClick = onClick;
        this.drillDown = drillDown;
        this.showName = showName;
        this.chartType = chartType;
        this.id = id;
        this.drillDownId = drillDownId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChartId() {
        return chartId;
    }

    public void setChartId(String chartId) {
        this.chartId = chartId;
    }

    public String getOnClick() {
        return onClick;
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDrillDownId() {
        return drillDownId;
    }

    public void setDrillDownId(String drillDownId) {
        this.drillDownId = drillDownId;
    }
}
