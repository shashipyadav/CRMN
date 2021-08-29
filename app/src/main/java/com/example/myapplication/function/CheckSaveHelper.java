package com.example.myapplication.function;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import com.example.myapplication.user_interface.dlist.controller.DListFieldHelper;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.util.DialogUtil;
import java.util.Calendar;
import java.util.List;
import static java.lang.Float.isNaN;

public class CheckSaveHelper {

    private String DEBUG_TAG = getClass().getSimpleName();
    private Context context;
    private boolean isVlist;
    private int dlistArrayPosition;
    private List<Field> fieldsList;
    private List<Field> additionalFieldDataList;
    private String formSaveCheck;
    private String formSaveCheckNames;


    public CheckSaveHelper(Context context) {
        this.context = context;
    }

    public boolean checkSave(ProgressDialog validationDialog){
        boolean err = false;
        String idlist = "";
        String idlist1 = "";

        if (!err){
            Field fieldDlist  = getFieldsList().get(dlistArrayPosition);

            idlist= getFormSaveCheck();
            idlist = idlist.replace("&lt;","<");
            idlist = idlist.replace("&gt;",">");

            idlist1 = getFormSaveCheckNames();
            idlist1 = idlist1.replace("&lt;","<");
            idlist1 = idlist1.replace("&gt;",">");

            if(idlist.isEmpty() && idlist1.isEmpty()){
                return false;
            }

            String[] arr = idlist.split(";");
            String[] arr1 = idlist1.split(";");

            String value = "";
            String firstname = "";
            String secondname = "";
            boolean isfirstdate = false;
            boolean isseconddate = false;
            long firstvalue = 0;
            long secondvalue = 0;
            String firstValue = "";
            String secondValue = "";

            for(int i=0;i<arr.length;i++) {
                boolean doalertonly = false;
                if (arr[i].trim().length() > 3) {
                    if (arr[i].indexOf("ALERT:") == 0) {
                        doalertonly = true;
                        arr[i] = arr[i].substring(6);
                    }
                    String[] carr = new String[6];
                    carr[0] = "==";
                    carr[1] = "!=";
                    carr[2] = ">=";
                    carr[3] = "<=";
                    carr[4] = "<<";
                    carr[5] = ">>";

                    boolean notchecked = true;
                    for (int x = 0; x < carr.length; x++) {
                        if (arr[i] != null && (arr[i].indexOf(carr[x]) > 0) && notchecked) {
                            firstname = trim(arr[i].split(carr[x])[0], "");
                            secondname = trim(arr[i].split(carr[x])[1], "");

                            if (firstname.indexOf("_") > -1) {
                                String[] arr2 = firstname.split("_");
                                firstname = arr2[1];
                                String[] arr3 = secondname.split("_");
                                secondname = arr3[1];

                                String[] arr4 = new String[0];


                                List<Field> dlistArray = fieldDlist.getdListArray();
                                first:
                                for (int j = 0; j < dlistArray.size(); j++) {
                                    Field fObj = dlistArray.get(j);
                                    if(fObj.getId().equals("field"+arr2[0])){
                                        List<DList> dlistFields = fObj.getdListsFields();
                                        for (int k = 0; k < dlistFields.size(); k++) {
                                            DList dobj = dlistFields.get(k);

                                            if (dobj.getId().equals("contentrows" + arr2[0])) {
                                                Log.e(DEBUG_TAG,"contentrows" + arr2[0] + " = " + dobj.getValue());
                                                arr4 = dobj.getValue().split(",");
                                                break first;
                                            }
                                        }
                                    }
                                }

                                if (arr1 != null) {
                                    for (int l = 0; l < arr4.length; l++) {
                                        isfirstdate = false;
                                        isseconddate = false;
                                        if (!arr4[l].equals("")) {
                                            //We check for dlistarray here
//                                          // and then loop dlistfield using the contentrows value ie. 1,2,3
                                            String vreplace =  DListFieldHelper.getDlistFieldValue(arr2[0],firstname,arr4[l],isVlist);
//
                                            int vlength = vreplace.length();
                                            if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
                                                vreplace = vreplace.replace(",", "");
                                            else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
                                                vreplace = vreplace.replace(",", "");

                                            if ((vlength == 10 || vlength == 16) && vreplace.indexOf('/') == 2 && vreplace.lastIndexOf('/') == 5) {
                                                int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
                                                int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
                                                int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
                                                int hr1 = 0;
                                                int min1 = 0;
                                                if (vreplace.length() > 10) {
                                                    hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
                                                    min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
                                                }
                                                firstvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
                                                isfirstdate = true;
                                            } else if ((vlength == 10 || vlength == 16) && (vreplace.indexOf('-') == 4 && vreplace.lastIndexOf('-') == 7 || vreplace.indexOf("T") == 10)) {
                                                // String arr2;String arr3;
                                                if (vreplace.indexOf("T") == 10) {
                                                    arr2 = vreplace.split("T");
                                                    arr3 = vreplace.split("T")[0].split("-");
                                                } else {
                                                    arr3 = vreplace.split("-");
                                                }
                                                int dt1 = Integer.parseInt(arr3[2]);
                                                int mon1 = Integer.parseInt(arr3[1]) - 1;
                                                int yr1 = Integer.parseInt(arr3[0]);
                                                int hr1 = 0;
                                                int min1 = 0;
                                                if (vreplace.length() > 10 && vreplace.indexOf("T") == 10) {
                                                    hr1 = Integer.parseInt(vreplace.split("T")[1].split(":")[0]);
                                                    min1 = Integer.parseInt(vreplace.split("T")[1].split(":")[1]);
                                                }
                                                firstvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
                                                isfirstdate = true;
                                            } else {
                                                try{
                                                    float CheckNum = Float.parseFloat(vreplace);
                                                    if (isNaN(CheckNum)) {
                                                        firstvalue = Integer.parseInt(vreplace);
                                                    } else {
                                                        double m = (Float.parseFloat(vreplace) * 1.0);
                                                        firstvalue = (long) m;

                                                    }}catch (NumberFormatException e){
                                                    e.printStackTrace();
                                                    firstvalue = 0;
                                                    if(vreplace.matches(".+")){
                                                        firstValue = vreplace;
                                                    }else{
                                                        //setting empty here as the control goes to else and we don't get the popup
                                                        if(vreplace.isEmpty()){
                                                            firstValue = "empty";
                                                        }
                                                    }
                                                }

                                            } // end of else
                                            boolean isSecondNameFound = false;

                                            vreplace =  DListFieldHelper.getDlistFieldValue(arr3[0],secondname,arr4[l],isVlist);
                                            if(!vreplace.equals("")){
                                                isSecondNameFound = true;
                                            }

//                                            for (int p = 0; p < FormFragment.fieldsList.size(); p++) {
//                                                Field pObj = FormFragment.fieldsList.get(p);
//                                                if (pObj.getId().equals("field" + secondname + "_" + arr4[l])) {
//                                                    vreplace = pObj.getValue();
//                                                    isSecondNameFound = true;
//                                                    break;
//                                                }
//                                            }
                                            if (isSecondNameFound) { //  if containing second name is found
                                                vlength = vreplace.length();
                                                if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
                                                    vreplace = vreplace.replace(",", "");
                                                else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
                                                    vreplace = vreplace.replace(",", "");

                                                if ((vlength == 10 || vlength == 16) && vreplace.indexOf('/') == 2 && vreplace.lastIndexOf('/') == 5) {
                                                    int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
                                                    int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
                                                    int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
                                                    int hr1 = 0;
                                                    int min1 = 0;
                                                    if (vreplace.length() > 10) {
                                                        hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
                                                        min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
                                                    }
                                                    secondvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
                                                    isseconddate = true;
                                                } else if ((vlength == 10 || vlength == 16) && (vreplace.indexOf('-') == 4 && vreplace.lastIndexOf('-') == 7 || vreplace.indexOf("T") == 10)) {
                                                    if (vreplace.indexOf("T") == 10) {
                                                        arr2 = vreplace.split("T");
                                                        arr3 = vreplace.split("T")[0].split("-");
                                                    } else {
                                                        arr3 = vreplace.split("-");
                                                    }
                                                    int dt1 = Integer.parseInt(arr3[2]);
                                                    int mon1 = Integer.parseInt(arr3[1]) - 1;
                                                    int yr1 = Integer.parseInt(arr3[0]);
                                                    int hr1 = 0;
                                                    int min1 = 0;
                                                    if (vreplace.length() > 10 && vreplace.indexOf("T") == 10) {
                                                        hr1 = Integer.parseInt(vreplace.split("T")[1].split(":")[0]);
                                                        min1 = Integer.parseInt(vreplace.split("T")[1].split(":")[1]);
                                                    }
                                                    secondvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
                                                    isseconddate = true;
                                                } else {
                                                    float CheckNum = Float.parseFloat(vreplace);
                                                    if (isNaN(CheckNum)) {
                                                        secondvalue = Integer.parseInt(vreplace);
                                                    } else {
                                                        secondvalue = (int) (Double.parseDouble(vreplace) * 1.0);
                                                    }
                                                }
                                            } // end of isSecondName found
                                            else {
                                                try{
                                                    secondvalue = (int) (Double.parseDouble(secondname) * 1.0);
                                                    if (isNaN(secondvalue)) {
                                                        secondvalue = Integer.parseInt(secondname);
                                                    }
                                                }catch (NumberFormatException e){
                                                    secondvalue = 0;
                                                    if(secondname.matches(".+")){
                                                        if(secondname.isEmpty()){
                                                            secondValue = "empty" ;
                                                        }else{
                                                            secondValue = secondname ;
                                                        }
                                                    }
                                                }
                                            }
                                            boolean donotalert = false;
                                            if(!firstValue.isEmpty() && !secondValue.isEmpty()){
                                                if (carr[x].equals("=="))
                                                    donotalert = (firstValue.equals(secondValue)) || (isfirstdate != isseconddate);
                                                else if (carr[x].equals("!="))
                                                    donotalert = (!firstValue.equals(secondValue)) || (isfirstdate != isseconddate);

                                            }else{
                                                if (carr[x].equals("=="))
                                                    donotalert = (firstvalue == secondvalue) || (isfirstdate != isseconddate);
                                                else if (carr[x].equals("!="))
                                                    donotalert = (firstvalue != secondvalue) || (isfirstdate != isseconddate);
                                                else if (carr[x].equals(">="))
                                                    donotalert = (firstvalue >= secondvalue) || (isfirstdate != isseconddate);
                                                else if (carr[x].equals("<="))
                                                    donotalert = (firstvalue <= secondvalue) || (isfirstdate != isseconddate);
                                                else if (carr[x].equals("<<"))
                                                    donotalert = (firstvalue < secondvalue) || (isfirstdate != isseconddate);
                                                else if (carr[x].equals(">>"))
                                                    donotalert = (firstvalue > secondvalue) || (isfirstdate != isseconddate);
                                            }

                                            if (!donotalert) {
                                                String condition = makeiteasy(arr1[i]);
                                                //   dismissDialog(validationDialog);
                                                DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
                                                err = true;


                                                return err;
                                            }//end of doalertonly
                                            firstValue = "";
                                            secondValue = "";
                                        }
                                    }//end of for (arr4)
                                }/*end of arr1 != null*/
                            }/*end of firstname.indexOF */ else {
                                isfirstdate = false;
                                isseconddate = false;
                                String vreplace = "";
                                for (int n = 0; n < getFieldsList().size(); n++) {
                                    Field nObj = getFieldsList().get(n);
                                    if (nObj.getId().equals("field" + firstname)) {
                                        vreplace = nObj.getValue();
                                        break;
                                    }
                                }
                                int vlength = vreplace.length();
                                if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
                                    vreplace = vreplace.replace(",", "");
                                else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
                                    vreplace = vreplace.replace(",", "");

                                if ((vlength == 10 || vlength == 16) && vreplace.indexOf("/") == 2 && vreplace.lastIndexOf("/") == 5) {
                                    int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
                                    int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
                                    int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
                                    int hr1 = 0;
                                    int min1 = 0;
                                    if (vreplace.length() > 10) {
                                        hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
                                        min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
                                    }
                                    firstvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
                                    isfirstdate = true;
                                } else if ((vlength == 10 || vlength == 16) && (vreplace.indexOf('-') == 4 && vreplace.lastIndexOf('-') == 7 || vreplace.indexOf("T") == 10)) {
                                    String[] arr2;
                                    String[] arr3;
                                    if (vreplace.indexOf("T") == 10) {
                                        arr2 = vreplace.split("T");
                                        arr3 = vreplace.split("T")[0].split("-");
                                    } else {
                                        arr3 = vreplace.split("-");
                                    }
                                    int dt1 = Integer.parseInt(arr3[2]);
                                    int mon1 = Integer.parseInt(arr3[1]) - 1;
                                    int yr1 = Integer.parseInt(arr3[0]);
                                    int hr1 = 0;
                                    int min1 = 0;
                                    if (vreplace.length() > 10 && vreplace.indexOf("T") == 10) {
                                        hr1 = Integer.parseInt(vreplace.split("T")[1].split(":")[0]);
                                        min1 = Integer.parseInt(vreplace.split("T")[1].split(":")[1]);
                                    }
                                    firstvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
                                    isfirstdate = true;
                                } else {
                                    try {
                                        float CheckNum = Float.parseFloat(vreplace);
                                        if (isNaN(CheckNum))
                                            firstvalue = Integer.parseInt(vreplace);
                                        else
                                            firstvalue = (int) (Double.parseDouble(vreplace) * 1.0);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        firstvalue = 0;
                                        if(vreplace.matches(".+")){
                                            if(vreplace.isEmpty()){
                                                firstValue = "empty";
                                            }else{
                                                firstValue = vreplace;
                                            }
                                        }
                                    }
                                }
                                boolean isSecondNameFound = false;
                                for (int p = 0; p <getFieldsList().size(); p++) {
                                    Field pObj = getFieldsList().get(p);
                                    if (pObj.getId().equals("field" + secondname)) {
                                        vreplace = pObj.getValue();
                                        isSecondNameFound = true;
                                        break;
                                    }
                                }
                                if (isSecondNameFound) {
                                    vlength = vreplace.length();
                                    if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
                                        vreplace = vreplace.replace(",", "");
                                    else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
                                        vreplace = vreplace.replace(",", "");

                                    if ((vlength == 10 || vlength == 16) && vreplace.indexOf('/') == 2 && vreplace.lastIndexOf('/') == 5) {
                                        int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
                                        int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
                                        int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
                                        int hr1 = 0;
                                        int min1 = 0;
                                        if (vreplace.length() > 10) {
                                            hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
                                            min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
                                        }
                                        secondvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
                                        isseconddate = true;
                                    } else if ((vlength == 10 || vlength == 16) && (vreplace.indexOf('-') == 4 && vreplace.lastIndexOf('-') == 7 || vreplace.indexOf("T") == 10)) {
                                        String[] arr2;
                                        String[] arr3;
                                        if (vreplace.indexOf("T") == 10) {
                                            arr2 = vreplace.split("T");
                                            arr3 = vreplace.split("T")[0].split("-");

                                        } else {
                                            arr3 = vreplace.split("-");
                                        }
                                       // int dt1 = Integer.parseInt(arr3[2]);
                                        int dt1 = Integer.parseInt(arr3[2].split(" ")[0]);
                                        int mon1 = Integer.parseInt(arr3[1]) - 1;
                                        int yr1 = Integer.parseInt(arr3[0]);
                                        int hr1 = 0;
                                        int min1 = 0;
                                        if (vreplace.length() > 10 && vreplace.indexOf("T") == 10) {
                                            hr1 = Integer.parseInt(vreplace.split("T")[1].split(":")[0]);
                                            min1 = Integer.parseInt(vreplace.split("T")[1].split(":")[1]);
                                        }
                                        secondvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
                                        isseconddate = true;
                                    } else {
                                        try {
                                            float CheckNum = Float.parseFloat(vreplace);
                                            if (isNaN(CheckNum))
                                                secondvalue = Integer.parseInt(vreplace);
                                            else
                                                secondvalue = (int) (Double.parseDouble(vreplace) * 1.0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            secondvalue = 0;
                                            if(vreplace.matches(".+")){
                                                secondValue = vreplace;
                                            }else{
                                                secondValue = "empty";
                                            }
                                        }
                                    }
                                } else {
                                    if (secondname.indexOf("${today") > -1) {
                                        if (secondname.equals("${today}")) {
                                            secondvalue = Calendar.getInstance().getTimeInMillis();
                                            isseconddate = true;
                                        } else {
                                            int goback = Integer.parseInt(secondname.substring(8, secondname.indexOf('}')), 10);
                                            Calendar cal = Calendar.getInstance();
                                            cal.set(Calendar.DAY_OF_MONTH, -goback);
                                            secondvalue = cal.getTimeInMillis();
                                            isseconddate = true;
                                        }
                                    } else {
                                        try {
                                            float CheckNum = Float.parseFloat(secondname);
                                            if (isNaN(CheckNum))
                                                secondvalue = Integer.parseInt(secondname);
                                            else
                                                secondvalue = (long) (Double.parseDouble(secondname) * 1.0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            secondvalue = 0;
                                            if(secondname.matches(".+")){
                                                secondValue = secondname;
                                            }else{
                                                secondValue = "emtpy";
                                            }
                                        }
                                    }
                                }
                                boolean donotalert = false;
                                //  Log.e("firstValue = ", String.valueOf(firstvalue));
                                //  Log.e("secondvalue = ", String.valueOf(secondvalue));
                                if(!firstValue.isEmpty() && !secondValue.isEmpty()){
                                    Log.e(DEBUG_TAG, "firstValue = "+ firstValue + " , secondValue = " + secondValue);
                                    if (carr[x].equals("=="))
                                        donotalert = (firstValue.equals(secondValue)) || (isfirstdate != isseconddate);
                                    else if (carr[x].equals("!="))
                                        donotalert = (!firstValue.equals(secondValue)) || (isfirstdate != isseconddate);

                                }else{
                                    Log.e(DEBUG_TAG, "firstvalue = "+ firstvalue + " , secondvalue = " + secondvalue);
                                    if (carr[x].equals("=="))
                                        donotalert = (firstvalue == secondvalue) || (isfirstdate != isseconddate);
                                    else if (carr[x].equals("!="))
                                        donotalert = (firstvalue != secondvalue) || (isfirstdate != isseconddate);
                                    else if (carr[x].equals(">="))
                                        donotalert = (firstvalue >= secondvalue) || (isfirstdate != isseconddate);
                                    else if (carr[x].equals("<="))
                                        donotalert = (firstvalue <= secondvalue) || (isfirstdate != isseconddate);
                                    else if (carr[x].equals("<<"))
                                        donotalert = (firstvalue < secondvalue) || (isfirstdate != isseconddate);
                                    else if (carr[x].equals(">>"))
                                        donotalert = (firstvalue > secondvalue) || (isfirstdate != isseconddate);
                                }

                                if (!donotalert) {
                                    Log.d("test", makeiteasy(arr1[i]));
                                    String condition = makeiteasy(arr1[i]);
                                    if (!doalertonly) {
                                        //   dismissDialog(validationDialog);
                                        DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
                                        err = true;
                                        return err;
                                    } else {
                                        //There is notification here in hybrid app
                                        //showing alert here as well in android
                                        //   dismissDialog(validationDialog);
                                        DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
                                        err = true;
                                        return err;
                                    }
                                }
                            } //end of else
                            notchecked = false;
                        }
                    }//end of for (carr)
                    if (notchecked) {
                        String condition = makeiteasy(arr1[i]);
                        // dismissDialog(validationDialog);
                        DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
                    }
                }/*end of  if (trim.length > 3) */ else {
                    String condition = makeiteasy(arr1[i]);
                    // dismissDialog(validationDialog);
                    DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
                }
            }
        }

        // dismissDialog(validationDialog);
        return err;
    }


    private long getDateInMillis(int dt1, int mon1,int yr1, int hr1,int min1){

        long timeInMillisec = 0;
        try {
            Calendar cal = Calendar.getInstance(); //current moment calendar
            cal.set(Calendar.DAY_OF_MONTH, dt1);
            cal.set(Calendar.MONTH,mon1);
            cal.set(Calendar.YEAR,yr1);

            cal.set(Calendar.HOUR_OF_DAY, hr1);
            cal.set(Calendar.MINUTE, min1);
            cal.set(Calendar.SECOND, 0); //if you don't care about seconds
            cal.set(Calendar.MILLISECOND,0);
            //   final Date myDate = cal.getTime(); //assign the date object you need from calendar
            timeInMillisec = cal.getTimeInMillis();

            System.out.println(timeInMillisec);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  timeInMillisec;

    }

    public String makeiteasy(String condition){
        //alert(condition);
        condition = condition.replace("ALERT:","Warning: ");
        condition = condition.replace("=="," should be ");// equal to
        //alert(condition);
        condition = condition.replace(">>"," should be greater than ");
        condition = condition.replace("<<"," should be less than ");
        condition = condition.replace(">="," should be greater than or equal to ");
        condition = condition.replace("<="," should be less than or equal to ");
        condition = condition.replaceAll("/  /"," ");
        Log.e("condition = ", condition);
        return condition;
    }


    private static String trim(String values, String s) {
        return ltrim(rtrim(values + "", s), s);

    }

    private static String ltrim(String rtrim, String chars) {
        if (!chars.equals("")) {
            chars = "\\s";
        } else chars = chars;
        return rtrim.replace("^[" + chars + "]+" + "g", "");
    }

    private static String rtrim(String s, String chars) {
        if (!chars.equals("")) {
            chars = "\\s";
        } else chars = chars;
        return s.replace("[" + chars + "]+$" + "g", "");
    }

//    private void showValidationProgress() {
//        if(validateProgressDialog == null){
//            validateProgressDialog = new ProgressDialog(getActivity());
//            validateProgressDialog.setMessage("Please Wait Validating Data ...");
//            validateProgressDialog.show();
//        }
//    }
//
//    private void dismissValidationProgress() {
//        if(validateProgressDialog != null){
//            validateProgressDialog.dismiss();
//        }
//    }

    public boolean isVlist() {
        return isVlist;
    }

    public void setVlist(boolean vlist) {
        isVlist = vlist;
    }


    public List<Field> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<Field> fieldsList) {
        this.fieldsList = fieldsList;
    }

    public int getDlistArrayPosition() {
        return dlistArrayPosition;
    }

    public void setDlistArrayPosition(int dlistArrayPosition) {
        this.dlistArrayPosition = dlistArrayPosition;
    }

    public void setAdditionalFieldDataList(List<Field> additionalFieldDataList) {
        this.additionalFieldDataList = additionalFieldDataList;
    }

    public String getFormSaveCheck() {
        return formSaveCheck;
    }

    public void setFormSaveCheck(String formSaveCheck) {
        this.formSaveCheck = formSaveCheck;
    }

    public String getFormSaveCheckNames() {
        return formSaveCheckNames;
    }

    public void setFormSaveCheckNames(String formSaveCheckNames) {
        this.formSaveCheckNames = formSaveCheckNames;
    }
}
