package com.example.myapplication.user_interface.login;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Server implements Parcelable
{

    @SerializedName("Client_Logo_For_Home_Screen")
    @Expose
    private String clientLogoForHomeScreen;
    @SerializedName("Mobile_License_Code")
    @Expose
    private String mobileLicenseCode;
    @SerializedName("Mobile_Client_Name")
    @Expose
    private String mobileClientName;
    @SerializedName("Client_Server_Cloud_Code")
    @Expose
    private String clientServerCloudCode;
    @SerializedName("Client_Logo_For_Login_Screen")
    @Expose
    private String clientLogoForLoginScreen;
    @SerializedName("Client_Server_URL")
    @Expose
    private String clientServerURL;
    public final static Creator<Server> CREATOR = new Creator<Server>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Server createFromParcel(android.os.Parcel in) {
            return new Server(in);
        }

        public Server[] newArray(int size) {
            return (new Server[size]);
        }

    }
            ;

    protected Server(android.os.Parcel in) {
        this.clientLogoForHomeScreen = ((String) in.readValue((String.class.getClassLoader())));
        this.mobileLicenseCode = ((String) in.readValue((String.class.getClassLoader())));
        this.mobileClientName = ((String) in.readValue((String.class.getClassLoader())));
        this.clientServerCloudCode = ((String) in.readValue((String.class.getClassLoader())));
        this.clientLogoForLoginScreen = ((String) in.readValue((String.class.getClassLoader())));
        this.clientServerURL = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Server() {
    }

    public String getClientLogoForHomeScreen() {
        return clientLogoForHomeScreen;
    }

    public void setClientLogoForHomeScreen(String clientLogoForHomeScreen) {
        this.clientLogoForHomeScreen = clientLogoForHomeScreen;
    }

    public String getMobileLicenseCode() {
        return mobileLicenseCode;
    }

    public void setMobileLicenseCode(String mobileLicenseCode) {
        this.mobileLicenseCode = mobileLicenseCode;
    }

    public String getMobileClientName() {
        return mobileClientName;
    }

    public void setMobileClientName(String mobileClientName) {
        this.mobileClientName = mobileClientName;
    }

    public String getClientServerCloudCode() {
        return clientServerCloudCode;
    }

    public void setClientServerCloudCode(String clientServerCloudCode) {
        this.clientServerCloudCode = clientServerCloudCode;
    }

    public String getClientLogoForLoginScreen() {
        return clientLogoForLoginScreen;
    }

    public void setClientLogoForLoginScreen(String clientLogoForLoginScreen) {
        this.clientLogoForLoginScreen = clientLogoForLoginScreen;
    }

    public String getClientServerURL() {
        return clientServerURL;
    }

    public void setClientServerURL(String clientServerURL) {
        this.clientServerURL = clientServerURL;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(clientLogoForHomeScreen);
        dest.writeValue(mobileLicenseCode);
        dest.writeValue(mobileClientName);
        dest.writeValue(clientServerCloudCode);
        dest.writeValue(clientLogoForLoginScreen);
        dest.writeValue(clientServerURL);
    }

    public int describeContents() {
        return 0;
    }

}


