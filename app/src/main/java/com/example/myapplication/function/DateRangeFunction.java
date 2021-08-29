package com.example.myapplication.function;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.DialogUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateRangeFunction {

    private Context context;
    private List<Field> fieldsList;
    private List<DList> dlistFieldValues;
    private FormRecylerAdapter adapter;

    public DateRangeFunction(Context context) {
        this.context = context;
    }

    public void dateRange(String fieldId,
                          String dateRangeDates1,
                          String dateRangeDates2,
                          String selectedDate,
                          int position,
                          boolean isDlist) {

        try {

            String fromDate = "";
            String toDate = "";
            SimpleDateFormat df = new SimpleDateFormat(Constant.dd_MM_yyyy);


            if (dateRangeDates1.indexOf("fystart") == 0) {
                fromDate = DateUtil.getFYStartDate();
            }

            if (dateRangeDates2.indexOf("fyend") == 0) {
                toDate = DateUtil.getFYEndDate();
            }

            if (dateRangeDates1.indexOf("mstart") == 0) {
                fromDate = DateUtil.getStartDateOfTheMonth();
            }

            if (dateRangeDates2.indexOf("mstart") == 0) {
                toDate = DateUtil.getStartDateOfTheMonth();
            }

            if (dateRangeDates1.indexOf("mend") == 0) {
                fromDate = DateUtil.getStartDateOfTheMonth();
            }

            if (dateRangeDates2.indexOf("mend") == 0) {
                toDate = DateUtil.getLastDateOfTheMonth();
            }

            if (dateRangeDates1.indexOf("today") == 0) {
                if (dateRangeDates1.contains("today+")) {
                    String days = dateRangeDates1.split("today\\+")[1];
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, Integer.parseInt(days));
                    Date dt = cal.getTime();

                    fromDate = df.format(dt);
                } else if (dateRangeDates1.contains("today-")) {
                    String days = dateRangeDates1.split("today-")[1];
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -Integer.parseInt(days));
                    Date dt = cal.getTime();
                    fromDate = df.format(dt);
                } else {
                    Calendar cal = Calendar.getInstance();
                    Date dt = cal.getTime();
                    fromDate = df.format(dt);
                }
            }

            if (dateRangeDates2.indexOf("today") == 0) {

                if (dateRangeDates2.contains("today+")) {
                    String days = dateRangeDates2.split("today\\+")[1];
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, Integer.parseInt(days));
                    Date dt = cal.getTime();

                    toDate = df.format(dt);
                } else if (dateRangeDates2.contains("today-")) {
                    String days = dateRangeDates2.split("today-")[1];
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -Integer.parseInt(days));
                    Date dt = cal.getTime();
                    toDate = df.format(dt);
                } else {
                    Calendar cal = Calendar.getInstance();
                    Date dt = cal.getTime();
                    toDate = df.format(dt);
                }
            }

            if ("".equals(fromDate))
                fromDate = ("".equals(dateRangeDates1) ? "01/01/1500" : dateRangeDates1);
            if ("".equals(toDate))
                toDate = ("".equals(dateRangeDates2) ? "01/01/9990" : dateRangeDates2);

            if (selectedDate.indexOf(":") > -1) {
                if (!selectedDate.isEmpty()) {
                    selectedDate = selectedDate.split(" ")[0];
                }

            }

            String dateFrom = fromDate;
            String dateTo = toDate;

            String dateCheck = selectedDate;
            String dd = "";
            String mm = "";
            String yyyy = "";

            String[] d1 = dateFrom.split("/");
            String[] d2 = dateTo.split("/");
            String[] c = dateCheck.split("/");

            long from = getDateInMillis(Integer.parseInt(d1[0]), Integer.parseInt(d1[1]) - 1, Integer.parseInt(d1[2]));  // -1 because months are from 0 to 11
            long to = getDateInMillis(Integer.parseInt(d2[0]), Integer.parseInt(d2[1]) - 1, Integer.parseInt(d2[2]));
            long check = getDateInMillis(Integer.parseInt(c[0]), Integer.parseInt(c[1]) - 1, Integer.parseInt(c[2]));


            if (check >= from && check <= to) {
                // this returns the value in javascript - > console.log($("#field"+fieldId).val());
                Log.e("dateRange line#", "124");
            } else {
                if (isDlist) {
                    getDlistFieldValues().get(position).setValue("");

                } else {
                    getFieldsList().get(position).setValue("");
                    getAdapter().notifyAdapterWithPayLoad(position, Constant.PAYLOAD_TEXT);
                }

                String msg = "Selected Date is not in Range. Please select date between " + dateFrom + " - " + dateTo + ".";
                DialogUtil.showAlertDialog((Activity) context,
                        "Please check condition", msg,
                        false,
                        false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private long getDateInMillis(int dt1, int mon1, int yr1) {
        long timeInMillisec = 0;
        try {
            Calendar cal = Calendar.getInstance(); //current moment calendar
            cal.set(Calendar.DAY_OF_MONTH, dt1);
            cal.set(Calendar.MONTH, mon1);
            cal.set(Calendar.YEAR, yr1);

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            //if you don't care about seconds
            //   final Date myDate = cal.getTime(); //assign the date object you need from calendar
            timeInMillisec = cal.getTimeInMillis();

            System.out.println(timeInMillisec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeInMillisec;

    }


    public List<Field> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<Field> fieldsList) {
        this.fieldsList = fieldsList;
    }

    public List<DList> getDlistFieldValues() {
        return dlistFieldValues;
    }

    public void setDlistFieldValues(List<DList> dlistFieldValues) {
        this.dlistFieldValues = dlistFieldValues;
    }

    public FormRecylerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FormRecylerAdapter adapter) {
        this.adapter = adapter;
    }
}
