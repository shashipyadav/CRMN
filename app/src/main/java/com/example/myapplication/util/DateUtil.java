package com.example.myapplication.util;

import android.util.Log;

import com.example.myapplication.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static final String DEBUG_TAG = DateUtil.class.getSimpleName();

    public static String formatDateTo_yyyyMMdd(String date) {
        if (date != null && !date.equals("")) {
            SimpleDateFormat spf;
            if (date.contains("T")) {
                spf = new SimpleDateFormat(Constant.yyyy_MM_dd_HH_mm);
            } else {
                spf = new SimpleDateFormat("dd/MM/yyyy");
            }
            // old date format
            Date newDate = null;
            try {
                newDate = spf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // new date format
            spf = new SimpleDateFormat("yyyy-MM-dd");
            date = spf.format(newDate);
            return date;
        }
        return date;
    }

    public static String formatDate(String date, String inputFormat, String outputFormat) {
        Log.e(DEBUG_TAG,"line#38 date = " + date);
        try {
            if (date != null && !date.equals("")) {

                if(date.matches("^(\\d{2})/(\\d{1,2})/\\d{4}$|^(\\d{2})/(\\d{1,2})/\\d{4} (\\d{2}:\\d{2})$")){
                    return date;
                }
                // old date format
                SimpleDateFormat spf = new SimpleDateFormat(inputFormat);
                Log.e(DEBUG_TAG, "spf = "+inputFormat);

                if(date.contains("T")){
                    spf = new SimpleDateFormat(Constant.yyyy_MM_dd_HH_mm);
                    Log.e(DEBUG_TAG, "spf = "+Constant.yyyy_MM_dd_HH_mm);
                }
                Date newDate = null;
                try {
                    newDate = spf.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // new date format
                spf = new SimpleDateFormat(outputFormat);
                date = spf.format(newDate);
                Log.e(DEBUG_TAG, "returned date = " + date);
                return date;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //added this because when we fetch a record the date is already formatted. so we want to return the same date
            Log.e(DEBUG_TAG, "exception returned date = " + date);
            return date;
        }
        Log.e(DEBUG_TAG, "line#69 returned date = ");
        return "";
    }

    public static String setDate(String dt, int amount) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.yyyy_MM_dd);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, amount);
        String dateString = sdf.format(c.getTime());

        return dateString;
    }


    public static String getFormatedDate(String gvar) {
        String dtStr = "";
        if (gvar.toLowerCase().matches("field\\$\\{today\\}|\\$\\{today\\}|today")) {
            dtStr = getCurrentDate(Constant.dd_MM_yyyy);
        } else if (gvar.toLowerCase().matches("field\\$\\{now\\}|\\$\\{now\\}|now")) {
            dtStr = getCurrentDate(Constant.dd_MM_yyyy_HH_mm);
        } else if (gvar.toLowerCase().matches("field\\$\\{mstart\\}|\\$\\{mstart\\}")) {
            dtStr = getStartDateOfTheMonth();
        } else if (gvar.toLowerCase().matches("field\\$\\{mend\\}|\\$\\{mend\\}")) {
            dtStr = getLastDateOfTheMonth();
        } else if (gvar.toLowerCase().matches("field\\$\\{fystart\\}|\\$\\{fystart\\}")) {
            dtStr = getFYStartDate();
        } else if (gvar.toLowerCase().matches("field\\$\\{fyend\\}|\\$\\{fyend\\}")) {
            dtStr = getFYEndDate();
        }else if (gvar.toLowerCase().contains("T")){
            dtStr = formatDate(gvar,Constant.yyyy_MM_dd_HH_mm,Constant.dd_MM_yyyy);
        }

        return dtStr;
    }

    public static String getCurrentDate(String format) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(format);
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getStartDateOfTheMonth() {
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println(c.getTime());
        Date dt = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constant.dd_MM_yyyy);
        return df.format(dt);
    }

    public static String getLastDateOfTheMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        System.out.println(c.getTime());
        Date dt = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constant.dd_MM_yyyy);
        return df.format(dt);
    }

    public static String getFYStartDate() {
        Calendar c = Calendar.getInstance();   // this takes current date
        int month = c.get(Calendar.MONTH);
        if ((month + 1) <= 3) {
            c.add(Calendar.YEAR, -1);
        }

        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, 3);
        System.out.println(c.getTime());
        Date dt = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constant.dd_MM_yyyy);
        return df.format(dt);
    }

    public static String getFYEndDate() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        if ((month + 1) <= 3) {
        }else{
            c.add(Calendar.YEAR, 1);
        }

        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.MONTH, 2);
        //add a year to the date

        System.out.println(c.getTime());
        Date dt = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constant.dd_MM_yyyy);
        return df.format(dt);
    }
}
