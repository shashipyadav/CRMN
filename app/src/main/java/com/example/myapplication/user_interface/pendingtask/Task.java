package com.example.myapplication.user_interface.pendingtask;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("Form_ID")
    private int formId;

    @SerializedName("Record_ID")
    private int recordId;

    @SerializedName("State_ID")
    private int stateId;

    @SerializedName("Priority")
    private int priority;

    @SerializedName("Date")
    private int date;

    @SerializedName("Ownerid")
    private int ownerId;

    @SerializedName("ID")
    private int id;

    @SerializedName("User_ID")
    private int userId;

    @SerializedName("Unread")
    private String unRead;

    @SerializedName("User_Name")
    private String userName;

    @SerializedName("Task_Details")
    private String taskDetails;

    @SerializedName("Name_of_Company")
    private String nameOfCompany;

    @SerializedName("Project_Name")
    private String projectName;

    @Expose(deserialize = false)
    private String stateColor;

    @SerializedName("Remarks")
    private String remarks;

    @SerializedName("State_Name")
    private String stateName;

    @SerializedName("Show_Date")
    private String showDate;

    @SerializedName("Form_Name")
    private String formName;

    @SerializedName("Reschedule_Fields")
    private String rescheduleFields;

    @SerializedName("Module_Name")
    private String moduleName;

    @Expose(deserialize = false)
    private boolean isSelected          = false;


    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUnRead() {
        return unRead;
    }

    public void setUnRead(String unRead) {
        this.unRead = unRead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(String taskDetails) {
        this.taskDetails = taskDetails;
    }

    public String getNameOfCompany() {
        return nameOfCompany;
    }

    public void setNameOfCompany(String nameOfCompany) {
        this.nameOfCompany = nameOfCompany;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStateColor() {
        return stateColor;
    }

    public void setStateColor(String stateColor) {
        this.stateColor = stateColor;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getRescheduleFields() {
        return rescheduleFields;
    }

    public void setRescheduleFields(String rescheduleFields) {
        this.rescheduleFields = rescheduleFields;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
