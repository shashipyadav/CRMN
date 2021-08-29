package com.example.myapplication.model;

public class ProjectModel {

    private String LOCATION;

    private String PROJECT;

    private String STATUS;

    private String IMAGE;

    private String RERA;

    private String CATEGORY;

    private String EXPECTED_DATE;

    private String PROJECT_CODE;

    private String B_IMAGE;
    private String OTPENABLE;

    public String getOTPENABLE() {
        return OTPENABLE;
    }

    public void setOTPENABLE(String OTPENABLE) {
        this.OTPENABLE = OTPENABLE;
    }

    public String getLOCATION ()
    {
        return LOCATION;
    }

    public void setLOCATION (String LOCATION)
    {
        this.LOCATION = LOCATION;
    }

    public String getPROJECT ()
    {
        return PROJECT;
    }

    public void setPROJECT (String PROJECT)
    {
        this.PROJECT = PROJECT;
    }

    public String getSTATUS ()
    {
        return STATUS;
    }

    public void setSTATUS (String STATUS)
    {
        this.STATUS = STATUS;
    }

    public String getIMAGE ()
    {
        return IMAGE;
    }

    public void setIMAGE (String IMAGE)
    {
        this.IMAGE = IMAGE;
    }

    public String getRERA ()
    {
        return RERA;
    }

    public void setRERA (String RERA)
    {
        this.RERA = RERA;
    }

    public String getCATEGORY ()
    {
        return CATEGORY;
    }

    public void setCATEGORY (String CATEGORY)
    {
        this.CATEGORY = CATEGORY;
    }

    public String getEXPECTED_DATE ()
    {
        return EXPECTED_DATE;
    }

    public void setEXPECTED_DATE (String EXPECTED_DATE)
    {
        this.EXPECTED_DATE = EXPECTED_DATE;
    }

    public String getPROJECT_CODE ()
    {
        return PROJECT_CODE;
    }

    public void setPROJECT_CODE (String PROJECT_CODE)
    {
        this.PROJECT_CODE = PROJECT_CODE;
    }

    public String getB_IMAGE ()
    {
        return B_IMAGE;
    }

    public void setB_IMAGE (String B_IMAGE)
    {
        this.B_IMAGE = B_IMAGE;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [LOCATION = "+LOCATION+", PROJECT = "+PROJECT+", STATUS = "+STATUS+", IMAGE = "+IMAGE+", RERA = "+RERA+", CATEGORY = "+CATEGORY+", EXPECTED_DATE = "+EXPECTED_DATE+", PROJECT_CODE = "+PROJECT_CODE+", B_IMAGE = "+B_IMAGE+"]";
    }
}
