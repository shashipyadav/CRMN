package com.example.myapplication.helper;

import android.os.Build;

public class DeviceInfoHelper {

    public static String getSystemVersion(){
        return System.getProperty("os.version");
    }

    public static String getDeviceAPILevel(){
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    public static String getDeviceName(){
       return android.os.Build.DEVICE;
    }

    public static String getDeviceModel(){
        return android.os.Build.MODEL;
    }

    public static String getProduct(){
        return android.os.Build.PRODUCT;
    }
}
