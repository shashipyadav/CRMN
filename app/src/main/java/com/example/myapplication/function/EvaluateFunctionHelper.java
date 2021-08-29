package com.example.myapplication.function;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.user_interface.forms.controller.helper.FieldHelper;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.Float.NaN;
import static java.lang.Float.isNaN;

public class EvaluateFunctionHelper {

    private final String DEBUG_TAG = EvaluateFunctionHelper.class.getSimpleName();
    private Context context;
    private List<Field> fieldsList;
    private int dlistArrayPosition;
    private List<Field> additionalFieldDataList;
    private FormRecylerAdapter adapter;

    public EvaluateFunctionHelper(Context context){
        this.context = context;
    }

    public void evaluateFunction(String fieldId, String functionlist,
                                 String extension, String jcodelist,
                                 String fieldValue) {
        Log.e(DEBUG_TAG, "evaluateFunction called");

        String url = "";
        String dlistspanids = "";
        List<Field> noOfDlist = new ArrayList<>(); //  dlist list
        List<DList> dlistfieldsList = new ArrayList<>();
        EncodeURIEngine uriEngine = new EncodeURIEngine();

        for (int i = 0; i < additionalFieldDataList.size(); i++) {
            Field fObj = additionalFieldDataList.get(i);
            if (fObj.getId().matches("dlistspanids")) {
                dlistspanids = fObj.getValue();
                break;
            }
        }

        boolean issFieldFound = false;
        for (int n = 0; n < fieldsList.size(); n++) {
            Field nObj = fieldsList.get(n);
            if (nObj.getId().equals("field" + fieldId + extension)) {
                fieldValue = nObj.getValue();
                issFieldFound = true;
                break;
            }
        }

        try {
            if ((!fieldValue.contains("<")) && (!fieldValue.contains(">"))) // (trim(fieldValue,"")!='') &&
            {
                int i = 0, j = 0, sum = 0;
                String sub = "", varid = "", varReplace = "";

                while (functionlist.contains("&gt;") //&gt; this stands for the greater-than sign ( > )
                        || functionlist.contains("&lt;") //&lt; this stands for the less-than sign ( < )
                        || functionlist.contains("&amp;"))  //&amp; this stands for the ampersand sign ( & )
                {
                    functionlist = functionlist.replace("&gt;", ">");
                    functionlist = functionlist.replace("&lt;", "<");
                    functionlist = functionlist.replace("&amp;", "&");
                }

                String[] arr = functionlist.split(";");
                functionlist = "";
                for (int m = 0; m < arr.length; m++) {
                    String func = arr[m];
                    if (extension.equals("")) {
                        String[] arr1 = func.split("@@");
                        boolean arr1FieldFound = false;
                        for (int d = 0; d < fieldsList.size(); d++) {
                            if (fieldsList.get(d).getId().matches("field" + arr1[0])) {
                                arr1FieldFound = true;
                                break;
                            }
                        }
                        if (arr1FieldFound) {
                            putfunctionvalues(func, extension, fieldValue);
                        } else {
                            //check field in some dlist
                            String[] darr = dlistspanids.split("/");
                            String dlistidin = "0";
                            String carrList = "";
                            boolean isindlist = false;

                            for (int p = 0; p < darr.length; p++) {
                                String dlistidlist = "";

                                //need to get few values from dlistarray
                                if(getDlistArrayPosition() != -1){
                                    Field kObj = fieldsList.get(getDlistArrayPosition());
                                    if (!kObj.getdListArray().isEmpty()) {
                                        noOfDlist = kObj.getdListArray();
                                        for (int h = 0; h < noOfDlist.size(); h++) {
                                            dlistfieldsList = noOfDlist.get(h).getdListsFields();
                                            for (int a = 0; a < dlistfieldsList.size(); a++) {
                                                DList aObj = dlistfieldsList.get(a);
                                                if (aObj.getId().matches("fieldids" + darr[p])) {
                                                    dlistidlist = aObj.getValue();
                                                    if (dlistidlist.indexOf("," + arr1[0] + ",") > -1) {
                                                        isindlist = true;
                                                        dlistidin = darr[p];
                                                        break;
                                                    }
                                                }
                                            } // end of dlistfieldsList for loop
                                        } // end of noOfDlist for loop
                                    } // end of getdListArray for loop
                                }

                                // end of  fieldsList for loop
                            } // end of p for loop

                            boolean isContentRowsFound = false;
                            for (int a = 0; a < dlistfieldsList.size(); a++) {
                                DList aObj = dlistfieldsList.get(a);
                                if (aObj.getId().toLowerCase().equals("contentrows" + dlistidin)) {
                                    carrList = aObj.getValue();
                                    isContentRowsFound = true;
                                    break;
                                }
                            }
                            if (isContentRowsFound) {
                                String[] carr = carrList.split(",");
                                for (int w = 0; w < carr.length; w++) {
                                    if (!carr[w].equals("")) {
                                        putfunctionvalues(func, "_" + carr[w], fieldValue);
                                    }
                                }
                            }
                            if (!isindlist) {
                                //it could be editpart field
                                if (func.contains("REQUIRED"))
                                    putfunctionvalues(func, extension, fieldValue);
                            }
                        }
                    } else if (extension.equals("_0")) {
                        String[] arr1 = func.split("@@");
                        String darr[] = dlistspanids.split("/");
                        String dlistidin = "0";
                        boolean isindlist = false;
                        for (int p = 0; p < darr.length; p++) {
                            String dlistidlist = "";

                            if(getDlistArrayPosition() != -1){
                                Field kObj = fieldsList.get(getDlistArrayPosition());
                                if (!kObj.getdListArray().isEmpty()) {
                                    noOfDlist = kObj.getdListArray();
                                    for (int h = 0; h < noOfDlist.size(); h++) {
                                        dlistfieldsList = noOfDlist.get(h).getdListsFields();
                                        for (int a = 0; a < dlistfieldsList.size(); a++) {
                                            DList aObj = dlistfieldsList.get(a);
                                            if (aObj.getId().matches("fieldids" + darr[p])) {
                                                dlistidlist = aObj.getValue();
                                                if (dlistidlist.indexOf("," + arr1[0] + ",") > -1) {
                                                    isindlist = true;
                                                    dlistidin = darr[p];
                                                    break;
                                                }
                                            }
                                        } // end of dlistfieldsList for loop
                                    } // end of noOfDlist for loop
                                } // end of getdListArray for loop
                            }

                        }
                        String carrList = "";
                        boolean isContentRowsFound = false;
                        if(getDlistArrayPosition() != -1){
                            Field kObj = fieldsList.get(getDlistArrayPosition());
                            if (!kObj.getdListArray().isEmpty()) {
                                noOfDlist = kObj.getdListArray();
                                for (int h = 0; h < noOfDlist.size(); h++) {
                                    dlistfieldsList = noOfDlist.get(h).getdListsFields();
                                    for (int a = 0; a < dlistfieldsList.size(); a++) {
                                        DList aObj = dlistfieldsList.get(a);
                                        if (aObj.getId().toLowerCase().equals("contentrows" + dlistidin)) {
                                            carrList = aObj.getValue();
                                            isContentRowsFound = true;
                                            break;
                                        }
                                    } // end of dlistfieldsList for loop
                                } // end of noOfDlist for loop
                            }
                            if (isContentRowsFound) {
                                String[] carr = carrList.split(",");
                                for (int w = 1; w < carr.length; w++) {
                                    if (!carr[w].equals("")) {
                                        putfunctionvalues(func, "_" + carr[w], fieldValue);
                                    }
                                }
                            }
                        }
                    } else {
                        putfunctionvalues(func, extension, fieldValue);
                    }
                } //end of m for loop

                String jidlist = "";
                if (!jcodelist.equals("")) {
                    String[] jarr = jcodelist.split(";");
                    jcodelist = "";

                    for (int ja = 0; ja < jarr.length; ja++) {
                        String func = jarr[ja];
                        String[] jarr2 = func.split("@@");
                        String jid = jarr2[0];

                        String[] jarr3 = jarr2[1].split("/");
                        String jvals = "";
                        for (int j3 = 0; j3 < jarr3.length; j3++) {
                            if (extension.equals("")) {
                                for (int a = 0; a < noOfDlist.size(); a++) {
                                    List<DListItem> dListArray = noOfDlist.get(a).getDListItemList();
                                    for (int b = 0; b < dListArray.size(); b++) {
                                        List<DList> dListFieldsList = dListArray.get(b).getDlistArray();
                                        for (int c = 0; c < dListFieldsList.size(); c++) {
                                            DList cObj = dListFieldsList.get(c);
                                            if (cObj.getId().matches("field" + jarr3[j3] + extension)) {
                                                if (!cObj.getValue().equals("")) {
                                                    jvals = jvals + uriEngine.encodeURIComponent(cObj.getValue() + "@j@");
                                                } else {
                                                    String[] arr1 = func.split("@@");
                                                    func = "";
                                                    //  window.setTimeout("clearField('field"+jarr3[j3]+extension+"');",2000);
                                                    jvals = "";
                                                }
                                                break;
                                            }
                                        }//end of 'c' for loop
                                    }// end of 'b' foor loop
                                }// end of 'a' for loop
                            } else {
                                for (int d = 0; d < fieldsList.size(); d++) {
                                    Field dObj = fieldsList.get(d);
                                    if (dObj.getId().matches("field" + jarr3[j3])) {
                                        if (!dObj.getValue().equals("")) {
                                            jvals = jvals + uriEngine.encodeURIComponent(dObj.getValue() + "@j@");
                                        } else {
                                            String[] arr1 = func.split("@@");
                                            func = "";
                                            jvals = "";
                                        }
                                        break;
                                    }
                                }//end of d for loop
                            }//end of else condition
                        }//end of 'j3' for loop

                        if (!jvals.equals("")) {
                            jcodelist = jcodelist + jvals + "@jj@";
                            jidlist = jidlist + func + ";";
                        }//end of !jvals.equals("")
                    } // end of 'ja' for loop
                } //end of if !jcodelist.equals("")

                if (!jcodelist.equals("")) {
                    //functionlist = functionlist.replace(/\+/g,"%2B");
                    jcodelist = jcodelist.replaceAll("\\+", "%2B");
                    jcodelist = jcodelist.replaceAll("\\&", "%26");
                    //alert("retrieve jidlist=="+jidlist+" jcodelist=="+jcodelist);
                    url = "getFunction.do?actn=evaluatefunction&functionlist=&extension=" + extension + "&jcodelist=" + jcodelist + "&jidlist=" + jidlist + "&ask=COMMAND_NAME_1";
                    FunctionHelper functionHelper = new FunctionHelper(context);
                    functionHelper.retrieveString3(url, "", "");

                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
    private void putfunctionvalues(String func, String extension,
                                   String fieldValue) {
        Log.e(DEBUG_TAG, "putfunctionvalues called");

        EvaluateEngine evaluateEngine = new EvaluateEngine();

        func = replacefunctionvalues(func, extension, fieldValue);
        Log.e(DEBUG_TAG, "putFunctionvalues, replacefunctionvalues returned "+ func);
        String sum = "0";
        if (!func.equals("")) {

            func = func.replaceAll("[|]", "\"");
            func = func.replaceAll("[\r\n]", " ");

            String[] arr1 = func.split("@@");
            if (!arr1[1].contains("{x}") && !arr1[1].contains("{i}") &&
                    !arr1[1].contains("&nbsp")) {

                Log.e(DEBUG_TAG, "line 306 arr1 = " + arr1[1]);
                try {
                    sum = evaluateEngine.eval(arr1[1]);
                    Log.e(DEBUG_TAG, " evaluateEngine.eval SUM = " +sum);
                } catch (Exception e) {
                    e.printStackTrace();
                    sum = arr1.length > 2 ? arr1[2] : "";
                }
            } else {
                sum = "0";
            }

            if (sum.toLowerCase().equals("continue")) {
                return;
            } else if ((sum + "").indexOf("LABEL=") == 0) {
                return;
            }

            for (int i = 0; i < fieldsList.size(); i++) {
                Field fObj = fieldsList.get(i);
                if (fObj.getId().matches("field" + arr1[0] + extension)) {
                    String oldValue = "";
                    oldValue = fObj.getValue();
                    if (fObj != null || ((sum + "").indexOf("REQUIRED") > -1))//it could be editpart field, then obj will be null
                    {
                        if (sum.toLowerCase().equals("readonly") || (sum + "").indexOf("READONLY") == 0) {
                            boolean doreadonly = true;
                            if (!fObj.getOptionsArrayList().isEmpty()) {
                                String ovalue = fObj.getValue();
                              //  fObj.getOptionsArrayList().clear();

//                                List<OptionModel> options = new ArrayList<>();
//                                options.add(new OptionModel(ovalue, ovalue));
//                                fObj.setOptionsArrayList(options);
                            } else {
                                //   obj.disabled = doreadonly;//true;
                                fObj.setSaveRequired("read");
                                fObj.setWebSaveRequired("read");
                             //   notifyAdapter(i);
                                notifyAdapterWithPayLoad(i, Constant.PAYLOAD_READ_EDIT);
                            }

                            return;
                        } else if (sum.toLowerCase().equals("editable") ||
                                (sum + "").indexOf("EDITABLE") == 0) {
                            boolean doreadonly = false;
                            if (!fObj.getOptionsArrayList().isEmpty()) {

                            } else {
                                if(fObj.getSaveRequired().toLowerCase().equals("read")){
                                    fObj.setSaveRequired("false");
                                    fObj.setWebSaveRequired("false");
                                }else if(fObj.getSaveRequired().toLowerCase().equals("true")) {
                                    fObj.setSaveRequired("true");
                                    fObj.setWebSaveRequired("true");
                                }

                               //  notifyAdapter(i);
                                 notifyAdapterWithPayLoad(i,Constant.PAYLOAD_READ_EDIT);
                            }
                            return;
                        } else if (sum.toLowerCase().equals("required") || (sum + "").indexOf("REQUIRED") == 0) {
                            boolean domandatory = true;
                            if (extension.equals("")) {
                                String fieldarr1 = arr1[0];
                                String editpartid = (sum + "").substring(8);
                                if (!editpartid.equals("")) {
                                    for (int b = 0; b < fieldsList.size(); b++) {
                                        Field bObj = fieldsList.get(b);
                                        if (bObj.getId().matches("field" + editpartid)) {
                                            fieldarr1 = "e" + editpartid;
                                            //alert("Required DLIST Field ID for NOTREQUIRED Function!");
                                            break;
                                        }
                                    }
                                }


                                // if(!obj) return true;
                                String midlist = "";
                                for (int m = 0; m < additionalFieldDataList.size(); m++) {
                                    Field addFieldObj = additionalFieldDataList.get(m);
                                    if (addFieldObj.getName().matches("mandatory")) {
                                        midlist = addFieldObj.getValue();
                                        if (midlist.indexOf(fieldarr1 + "/") != 0 && midlist.indexOf("/" + fieldarr1 + "/") < 0) {
                                            midlist += fieldarr1 + "/";
                                        }
                                        addFieldObj.setValue(midlist);
                                        fObj.setSaveRequired("true");
                                        break;
                                    }
                                }
                                // notifyAdapter(i);
                                   notifyAdapterWithPayLoad(i,Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                                return;
                            }//end of if no extenstion
                            else {
                                String dlistid = (sum + "").substring(8);
                                if (dlistid.equals("")) {
                                    // alert("Required DLIST Field ID in REQUIRED Function!");
                                } else {
                                    String midlist = "";
                                    for (int m = 0; m < additionalFieldDataList.size(); m++) {
                                        Field addFieldObj = additionalFieldDataList.get(m);
                                        if (addFieldObj.getName().matches("mandatory")) {
                                            midlist = addFieldObj.getValue();
                                            if (midlist.indexOf(dlistid + "_" + arr1[0] + "/") != 0 && midlist.indexOf("/" + dlistid + "_" + arr1[0] + "/") < 0) {
                                                if (extension != "" && midlist.indexOf(dlistid + "_" + arr1[0] + extension + "/") != 0 && midlist.indexOf("/" + dlistid + "_" + arr1[0] + extension + "/") < 0) {
                                                    midlist += dlistid + "_" + arr1[0] + extension + "/";//+extension
                                                } else {
                                                    midlist += dlistid + "_" + arr1[0] + "/";//+extension
                                                }
                                                addFieldObj.setValue(midlist);
                                                break;
                                            }
                                        }
                                    }
                                   // notifyAdapter(i);
                                    notifyAdapterWithPayLoad(i, Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                                    return;
                                }
                            }
                        }//end of else if required
                        else if (sum.toLowerCase().equals("notrequired") || (sum + "").indexOf("NOTREQUIRED") == 0) {
                            boolean domendetory = false;
                            if (extension.equals("")) {
                                String fieldarr1 = arr1[0];
                                String editpartid = (sum + "").substring(11);
                                if (!editpartid.equals("")) {
                                    Field nObj = null;
                                    for (int n = 0; n < fieldsList.size(); n++) {
                                        nObj = fieldsList.get(n);
                                        if (nObj.getId().equals("field" + editpartid)) {
                                            //  ToastUtil.showToastMessage("Please select the required options.", getActivity());
                                            //ToDo : adapter.notifyItemChanged();
                                            break;
                                        }
                                    }
                                }

                                    //if(nObj == null) return true;
                                    String midlist = "";
                                    for (int n = 0; n < additionalFieldDataList.size(); n++) {

                                        Field addFieldObj = additionalFieldDataList.get(n);
                                        if (addFieldObj.getName().matches("mandatory")) {
                                            midlist = addFieldObj.getValue();
                                            if (midlist.indexOf(fieldarr1 + "/") == 0)
                                                midlist = midlist.replace(fieldarr1 + "/", "");
                                            else if (midlist.indexOf("/" + fieldarr1 + "/") > -1)
                                                midlist = midlist.replace("/" + fieldarr1 + "/", "/");

                                            addFieldObj.setValue(midlist);

                                            fObj.setSaveRequired("false");
                                            fObj.setShowErrorMessage(false);
                                            break;
                                        }
                                    }
                                   // notifyAdapter(i);
                                    notifyAdapterWithPayLoad(i,Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                                    return;

                            } else {
                                String dlistid = (sum + "").substring(11);
                                if (dlistid.equals("")) {

                                } else {
                                    String midlist = "";
                                    for (int m = 0; m < additionalFieldDataList.size(); m++) {
                                        Field addFieldObj = additionalFieldDataList.get(m);
                                        if (addFieldObj.getName().matches("mandatory")) {
                                            midlist = addFieldObj.getValue();
                                            if (midlist.indexOf(dlistid + "_" + arr1[0] + extension + "/") == 0)
                                                midlist = midlist.replace(dlistid + "_" + arr1[0] + extension + "/", "");
                                            else if (midlist.indexOf("/" + dlistid + "_" + arr1[0] + extension + "/") > -1)
                                                midlist = midlist.replace("/" + dlistid + "_" + arr1[0] + extension + "/", "/");
                                            else if (midlist.indexOf(dlistid + "_" + arr1[0] + "/") == 0)
                                                midlist = midlist.replace(dlistid + "_" + arr1[0] + "/", "");
                                            else if (midlist.indexOf("/" + dlistid + "_" + arr1[0] + "/") > -1)
                                                midlist = midlist.replace("/" + dlistid + "_" + arr1[0] + "/", "/");

                                            addFieldObj.setValue(midlist);
                                            break;
                                        }
                                       //notifyAdapter(i);
                                        notifyAdapterWithPayLoad(i,Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                                        return;
                                    }
                                }
                            }
                            return;

                        }// end of not required
                        float CheckNum = NaN;
                        if ((sum != null && !sum.isEmpty()) && (arr1[arr1.length - 1] != null && !arr1[arr1.length - 1].isEmpty())) {

                            try {
                                if (arr1[arr1.length - 1].indexOf("0") > -1) {
                                    if(fObj.getDataType().toLowerCase().equals("double")){
                                        CheckNum = Float.parseFloat(sum);
                                    }else{
                                        CheckNum = NaN;
                                    }
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                CheckNum = NaN;
                            }
                        }
                        if (isNaN(CheckNum)) {
                            if (fObj.getType().toLowerCase().equals("checkbox") || fObj.getType().toLowerCase().equals("boolean") ) {
                                fObj.setValue(sum);
                            } else {

                                String defaultValue = fObj.getDefaultValue();
                                FieldHelper fieldHelper = new FieldHelper(context);
                                fieldValue = fieldHelper.setUserName(defaultValue);
                                if(!fieldValue.isEmpty()){
                                    fObj.setValue(fieldValue);
                                }else{
                                    fObj.setValue(sum);
                                }

                                //added this as this wasn't showing the result in the recyclerview field.
                                //  notifyAdapter(i);

                                if (fObj.getValue() != sum && !fObj.getOptionsArrayList().isEmpty()) {
//                                    if(obj.size < 2){
//                                        if(sum!='null'){
//                                            obj.options[0]=new Option(sum,sum);
//                                            obj.value=obj.options[0].value;
//                                        }
//                                    }
                                }
                            }
                        } else if ((sum + "").indexOf("/") > -1 || (sum + "").indexOf("-") > 0 || (sum + "").indexOf("_") > -1 || (sum + "").indexOf(":") > -1) {
                            fObj.setValue(sum);
                        } else if (arr1[2] != null && arr1[2] == "0.00") {//feb 05 changed arr1[2] to arr1[1] as it was crashing
                            fObj.setValue(Math.round(Math.round(Integer.parseInt(sum) * 100) / 100) + ".00");//for solving round of problem for 251.4984 in javascript
                            //  formatDouble(arr1[0]+extension,"");
                        } else if (arr1[2] != null && arr1[2].equals("0.000")) {
                            double s = Math.round((Double.parseDouble(sum) * 1000));
                            double d = (s / 1000);
                            sum = String.valueOf(d);
                            fObj.setValue(sum);
                        } else if (arr1[2] != null && arr1[2].equals("0.0000")) {
                            double s = Math.round((Double.parseDouble(sum)) * 10000000000L);
                            double d = (s / 10000000000L);
                            sum = String.valueOf(d);
                            fObj.setValue(sum);
                        } else {
                            //sum = parseInt((sum+0.000000000001)*100);
                            double s = Math.round((Double.parseDouble(sum)) * 100);//4.9350000000000001
                            double d = (s / 100);
                            sum = String.valueOf(d);
                            fObj.setValue(sum);
                            //   formatDouble(arr1[0]+extension);
                        }
                        if(fObj.getDataType().toLowerCase().equals("tag")){
                            notifyAdapter(i);
                        }else{
                            notifyAdapterWithPayLoad(i, Constant.PAYLOAD_TEXT_SQL);
                        }

                        break;
                    }
                }
            }
        }
    }

    private String replacefunctionvalues(String func, String extension,
                                         String fieldValue) {

        Log.e(DEBUG_TAG, "replacefunctionvalues called");
        int i = 0, j = 0, sum = 0;
        String sub = "", varid = "", varReplace = "";
        boolean isdate = false;
        int h = 0;

        while (func.indexOf("${") > -1 && h < 100) {
            h = h + 1;
            i = func.indexOf("${");
            sub = func.substring(i);
            j = sub.indexOf("}");
            varid = sub.substring(2, j);
            varReplace = "${" + sub.substring(2, j) + "}";

            if (varid.toLowerCase().equals("id")) varid = "id";

            if (varid.toLowerCase().equals("state")) {
                String vreplace = "";
                //   if( document.getElementById('statefield'))
                //       vreplace = document.getElementById('statefield').value; //not clear

                if (vreplace.equals("")) vreplace = "0";
                func = func.replace(varReplace, vreplace);
                h = 0;
            }//enf of if varid == state

            boolean isFieldFound = false;
            int fieldPosition = -1;
            for (int d = 0; d < fieldsList.size(); d++) {
                Field dObj = fieldsList.get(d);
                if (dObj.getId().matches("field" + varid + extension)) {
                    isFieldFound = true;
                    fieldPosition = d;
                    break;
                }
            }

            if (isFieldFound) {
                //TODO :_ pass dlistArray in arguments
                //this is for dlist

                if (fieldsList.get(fieldPosition).getType().toLowerCase().matches("checkbox|boolean")) {
                    func = func.replace(varReplace, fieldsList.get(fieldPosition).getValue());
                } else {
                    String vreplace = fieldsList.get(fieldPosition).getValue();
                    String datePat1 = "^(\\d{1,2})(-)(\\d{1,2})(-)(\\d{4})$";
                    String datePat2 = "^(\\d{4})(-)(\\d{1,2})(-)(\\d{1,2})$";
                    List<String> matchArray = new ArrayList();
                    Matcher matcher = Pattern.compile(datePat2).matcher(vreplace);
                    while (matcher.find()) {
                        // Do something with the matched text
                        matchArray.add(matcher.group(0));
                        matchArray.add(matcher.group(1));
                        matchArray.add(matcher.group(2));
                        matchArray.add(matcher.group(3));
                        matchArray.add(matcher.group(4));
                        matchArray.add(matcher.group(5));
                    }

                    if (!matchArray.isEmpty()) {
                        String d1 = matchArray.get(5);
                        String m1 = matchArray.get(3);
                        String y1 = matchArray.get(1);
                        vreplace = d1 + "/" + m1 + "/" + y1;
                    }

                    int vlength = vreplace.length();
                    if ((vlength == 10 || vlength == 16) && vreplace.indexOf('/') == 2 && vreplace.lastIndexOf('/') == 5 && func.indexOf('|') < 0) {
                        isdate = true;
                    }
                    if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
                        vreplace = vreplace.replace(",", "");
                    else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
                        vreplace = vreplace.replace(",", "");
                    else if (vlength > 15 && vreplace.lastIndexOf(',') == (vlength - 15) && vreplace.lastIndexOf('.') == (vlength - 11))
                        vreplace = vreplace.replace(",", "");
                    func = func.replace(varReplace, vreplace);
                }//end of else
                h = 0;
            } else {
                for (int a = 0; a < fieldsList.size(); a++) {
                    Field aObj = fieldsList.get(a);
                    String id = "field" + varid;
                    // String id = "field"+varid;

                    if (aObj.getId().equals(id)) {
                        if (aObj.getFieldType().equals("checkbox") || aObj.getType().equals("checkbox")) {
//                            func = func.replace(varReplace,
//                                    String.valueOf(((CheckBox) v).isChecked()));

                            func = func.replace(varReplace, aObj.getValue());
                        } else {

                            String vreplace = fieldsList.get(a).getValue();
                            int vlength = vreplace.length();

                            //alert(vlenght+" func=="+func+" index=="+func.indexOf('"'));
                            if ((vlength == 10 || vlength == 16) &&
                                    vreplace.indexOf("/") == 2 &&
                                    vreplace.lastIndexOf("/") == 5
                                    && func.indexOf("|") < 0) {
                                isdate = true;
                            }

                            if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7)
                                    && vreplace.lastIndexOf('.') == (vlength - 3)) {
                                vreplace = vreplace.replace(",", "");
                            } else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8)
                                    && vreplace.lastIndexOf('.') == (vlength - 4)) {
                                vreplace = vreplace.replace(",", "");
                            }
                            func = func.replace(varReplace, vreplace);
                        }
                        h = 0;
                        break;
                    } else {
                        //not found in the form
                        String[] arr1 = func.split("@@");
                        func = "";
                        // Log.e("line #1137", "Alert, please contact ITAakash No field *****" + id + "*****found in function ");
                    }
                }
            }

            if (isdate) {
                evaluatedate(func, extension);
                return "";
            }
        }
        return func;
    }

    private void evaluatedate(String func, String extension) {
        Log.e(DEBUG_TAG, "replacefunctionvalues called");
        int objPostion = -1;
        String[] arr1 = func.split("@@");
        String oldValue = "";
        boolean isFieldFound = false;
        for (int i = 0; i < fieldsList.size(); i++) {
            Field fObj = fieldsList.get(i);
            if (fObj.getId().matches("field" + arr1[0] + extension)) {
                objPostion = i;
                oldValue = fObj.getValue();
                isFieldFound = true;
                break;
            }
        }

        if (isFieldFound) {
            int sep = 1;
            if (arr1[1].indexOf("-") > -1) sep = -1;

            String[] varr;
            if (sep > 0) {
                varr = arr1[1].split("\\+");
            } else {
                varr = arr1[1].split("-");
            }

            String vreplace = varr[0];
            String days = varr[1];
            int adjust = 0;

            //if(arr1[1].split(sep>0?"+":"-")[2]) adjust=arr1[1].split(sep>0?"+":"-")[2];//+""
//           String[] jaar = arr1[1].split(sep>0?"+":"-");
//           if(jaar != null){
//               String [] jaar1 =arr1[1].split(sep>0?"+":"-");
//               adjust =  Integer.parseInt(jaar1[2]);
//           }

            if (days.length() == 0) {
                //TODO = pending
                //  obj.value = arr1[2];
                //  return true;
            }

            int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
            int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
            int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
            int hr1 = 0;
            int min1 = 0;

            if (vreplace.length() > 10) {
                hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
                min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
            }

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-yyyy");
//            Date d = new Date(yr1, mon1, dt1,hr1,min1,0);
//            Date d;
//            try {
//                d = sdf.parse(vreplace);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            if (isNaN(dt1)) {
                if (days.indexOf('/') > -1) fieldsList.get(objPostion).setValue("0.00");
                else fieldsList.get(objPostion).setValue("");
            } else if (days.indexOf('/') > -1) {
                vreplace = days;
                //pending
            } else {
                String fmDate = DateUtil.formatDate(vreplace, Constant.dd_MM_yyyy, Constant.yyyy_MM_dd);
                String dateString = DateUtil.setDate(fmDate, sep * Integer.parseInt(days));
                fieldsList.get(objPostion).setValue(dateString);

               // notifyAdapter(objPostion);
                notifyAdapterWithPayLoad(objPostion,Constant.PAYLOAD_TEXT_SQL);
            }
        }
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

    public FormRecylerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FormRecylerAdapter adapter) {
        this.adapter = adapter;
    }

    private void notifyAdapter(final int position){
        Log.e(DEBUG_TAG, "notifyAdapter called");

        if (context != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(DEBUG_TAG, "run called");
                    getAdapter().notifyItemChanged(position);
                }
            });
        }
    }

    private void notifyAdapterWithPayLoad(final int position, final String payload) {
        if(context != null){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            if(adapter != null){
                                Log.e(DEBUG_TAG, "notifyAdapterWithPayLoad "+ payload + "called");
                                adapter.notifyItemChanged(position,payload);
                            }
                        }
                    };
                    handler.post(r);
                }
            });
        }
    }
}
