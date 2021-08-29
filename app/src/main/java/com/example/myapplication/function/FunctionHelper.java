package com.example.myapplication.function;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.controller.DListFieldHelper;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.example.myapplication.util.DialogUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.Float.isNaN;

public class FunctionHelper {

    private static final String DEBUG_TAG = FunctionHelper.class.getSimpleName();
    private static int dcombolive = 0;
    private static List<String> loadoArr;
    private static int loadfieldid;
    private static Boolean loadissql;
    private static String loadextension;
    private static Boolean loadmdcombo;
    private static String loadjcodelist;
    private static int dcombocounter = 0;
    private static int repeatcounter = 0;
    Context context;
    private SharedPrefManager mPrefManager;

    private int dlistArrayPosition;
    private List<Field> fieldsList;
    private List<Field> additionalFieldDataList;


    public FunctionHelper(Context mContext) {
        this.context = mContext;
        mPrefManager = new SharedPrefManager(context);
    }

    private static String retrieveDCombo(String s, String reportForm1, String oTxt) {
        Double randomnumber = Math.floor(Math.random() * 1111);
        s = s + "&random=" + randomnumber;

        Log.e("url", s);//alert(url);
        return s;
    }

    private static void loadfields(int fieldid, List<String> oArr,
                                   Boolean issql, String extension,
                                   String jcodelist, Boolean mdcombo) {
        loadfieldid = fieldid;
        loadoArr = oArr;
        loadissql = issql;
        loadextension = extension;
        loadjcodelist = jcodelist;
        loadmdcombo = mdcombo;
    }

    public  String load(int fieldid, List<String> oArr, Boolean issql, String extension,
                        String jcodelist, String fieldtype, Boolean mdcombo, boolean isVlist) {

        String oTxt = "field" + fieldid + extension;
        if (issql == true) {
            String jidlist = jcodelist;
            if (!jcodelist.equals("")) {
                jcodelist = replacesqllistappNew(String.valueOf(fieldid),"",extension,jcodelist,
                 true,isVlist);
                jcodelist = jcodelist.replace("/\"+/g", "%2B");
                jcodelist = jcodelist.replace("/\"&/g", "%26");
                dcombocounter = 1;
                String filtervalue = ("field" + fieldid + extension).replace("/\"%/g", "");

                List<Field> fieldList = getFieldsList();
                for(int i=0; i< fieldList.size(); i++){
                    Field fobj = fieldList.get(i);
                    if(fobj.getId().equals(filtervalue)){
                        filtervalue = fobj.getValue();
                        break;
                    }
                }

                EncodeURIEngine uriEngine = new EncodeURIEngine();
                filtervalue = uriEngine.encodeURIComponent(filtervalue);

                if (mdcombo) {
                    double randomnumber =  Math.floor(Math.random()*1111);
                    String s = mPrefManager.getClientServerUrl() +
                            "SaveFormField.do?actn=retrieveDComboList&cloudcode="+mPrefManager.getCloudCode()+"&token="+mPrefManager.getAuthToken()+"&extension="+extension
                            + "&jcodelist="+jcodelist
                            + "&jidlist=" +jidlist
                            + "&filtervalue=" + filtervalue
                            + "&ask=COMMAND_NAME_1"
                            + "random="+randomnumber;
                    return s;
                } else {
                    //      retrieveDCombo("https://best-erp.com:9000/strategicerp/"+
                    //      "SaveFormField.do?actn=retrieveDComboList&cloudcode="+window.cloudcode+"&token="+window.token+"&extension="+extension+"&jcodelist="+jcodelist+"&jidlist="+jidlist+"&filtervalue="+filtervalue+"&ask=COMMAND_NAME_1","reportForm1",oTxt);
                    String s = retrieveDCombo(mPrefManager.getClientServerUrl()
                            + "SaveFormField.do?actn=retrieveDComboList&extension=" + extension
                            + "&jcodelist=" + jcodelist
                            + "&jidlist=" + jidlist
                            + "&filtervalue="
                            + filtervalue + "&ask=COMMAND_NAME_1&cloudcode="+mPrefManager.getCloudCode()+"&token="+mPrefManager.getAuthToken(), "reportForm1", oTxt);
                    return s;
                }
            }

        } else {//if not sql
            //alert(oTxt+"=="+oArr);
           /* if(find(oTxt) && document.getElementById(oTxt)){
                String fvalue = document.getElementById(oTxt).value;
                    String[] arrs = fvalue.split(", ");
                    fvalue = arrs[arrs.length];
                }
                //alert(mdcombo+"=="+fvalue);
                String[] oArr1;
                int n = oArr.size();
                int j=0;
                for(int i=0;i<n;i++)
                {
                    if(oArr.get(i).toLowerCase().indexOf(fvalue.toLowerCase())>-1){
                        oArr1.push(oArr[i]);
                        j++;
                    }
                    if(j>20)break;
                }

                var oTextbox = new AutoSuggestControl(document.getElementById(oTxt), new StateSuggestions(oArr1));
                oTextbox.provider.requestSuggestionsonclick(oTextbox, true);*/
        }
        return oTxt;
    }

    public String evaluatesql(String fieldid, String extension,
                              String jcodelist, String fieldValue,boolean isVlist)
    {
        String finalURL = "";
        String jidlist = jcodelist;
        if ((fieldValue.trim().equals(""))) {
            List<Field> additionalFieldDataList = getAdditionalFieldDataList();
//            if(isVlist){
//                additionalFieldDataList = VlistFormActivity.vAdditionalFieldDataList;
//            }else {
//                additionalFieldDataList = FormFragment.additionalFieldDataList;
//            }


            String midlist = "";
            for(int i=0;i< additionalFieldDataList.size(); i++){
                Field fobj = additionalFieldDataList.get(i);
                if(fobj.getName().toLowerCase().equals("mandatory")){
                    midlist = fobj.getValue();
                    break;
                }
            }
            boolean dostop = midlist.indexOf(fieldid+"/")==0 || midlist.indexOf("/"+fieldid+"/")>-1 || midlist.indexOf("_"+fieldid+"/")>-1;
            if (dostop) return "";
        }
        if( (fieldValue.indexOf("<")<0) && (fieldValue.indexOf(">")<0) ){
            jcodelist = replacesqllistappNew(fieldid, fieldValue, extension, jcodelist, true,isVlist);
         //   Log.e("jcodelist", jcodelist);
            if (!jcodelist.equals("")) {
                jcodelist = jcodelist.replaceAll("\\+", "%2B");
                jcodelist = jcodelist.replaceAll("&", "%26");
                Log.e("jcodelist after replace", jcodelist);

                int randomnumber = (int) Math.floor(Math.random() * 1111);
                finalURL = mPrefManager.getClientServerUrl() + "getFunction.do?actn=evaluatesql&extension=" + extension + "&jcodelist=" + jcodelist + "&jidlist=" + jidlist + "&ask=COMMAND_NAME_1" + "&random=" + randomnumber + "&cloudcode=" + mPrefManager.getCloudCode() + "&token=" + mPrefManager.getAuthToken() +"&type=json";
                Log.e("evaluatesqlURL", finalURL);
            }
        }

        return finalURL;
    }

    public void onLoadChangeIdsInMainForm(String stateField){
        String checkstateid = "0";
        checkstateid = stateField;

        if(getFieldsList() != null) {

            for (int i = 0; i < getFieldsList().size(); i++) {
                Field fobj = getFieldsList().get(i);
                if ((fobj.getStates().indexOf("c") == -1 && fobj.getStates().indexOf("s" + checkstateid + "s") > -1)) {
                    if (fobj.getType().equals("hidden")) {
                        fobj.setType(fobj.getFieldType());
                    }
                }
            }

            String idlist = "";
            for (int i = 0; i < getAdditionalFieldDataList().size(); i++) {
                Field fobj = getAdditionalFieldDataList().get(i);
                if (fobj.getName().toLowerCase().equals("checkhideshowids")) {
                    idlist = fobj.getValue();
                    break;
                }
            }

            String[] arr = idlist.split("/");
            CheckHideShowHelper ch =  new CheckHideShowHelper();
            ch.setFieldsList(getFieldsList());
            ch.setAdditionalFieldDataList(getAdditionalFieldDataList());
            ch.setDlistArrayPosition(getDlistArrayPosition());

            for (int j = 0; j < arr.length; j++) {
              ch.checkHideShow(arr[j]);
            }
        }

    }

    public void onLoadChangeIds(String stateField) {
        Log.e(DEBUG_TAG, "statefield = " + stateField);

        String dlist = "";
        for (int i = 0; i < getAdditionalFieldDataList().size(); i++) {
            Field fobj = getAdditionalFieldDataList().get(i);
            if (fobj.getName().toLowerCase().equals("dlistspanids")) {
                dlist = fobj.getValue();
                break;
            }
        }
        if (!dlist.equals("")) {
            String checkstateid = "0";
            checkstateid = stateField;

            String[] arr_dlist = dlist.split("/");
            for (int i = 0; i < arr_dlist.length; i++) {
                String id1 = arr_dlist[i];
                // String state_list =document.getElementById('stateids'+id1).value;
                String stateList = "";
                String fieldIds = "";
                boolean isFieldIdsFound = false;
                boolean isStateIdsFound = false;
                List<DList> dlistFields = new ArrayList<>();



                if(getDlistArrayPosition() != -1){
                    Field mObj = getFieldsList().get(getDlistArrayPosition());

                    List<Field> dlistArray = mObj.getdListArray();
                    first:
                    for (int j = 0; j < dlistArray.size(); j++) { // will loop through the dlistbuttonArray

                        Field dlist1 = dlistArray.get(j); // sindle dlist Item
                        if (dlist1.getId().equals("field" + id1)) {
                            dlistFields = dlist1.getdListsFields();

                            for (int k = 0; k < dlistFields.size(); k++) {
                                DList dobj = dlistFields.get(k);
                                if (dobj.getId().equals("stateids" + id1)) {
                                    Log.e(DEBUG_TAG, "stateids" + id1);
                                    stateList = dobj.getValue();
                                    isStateIdsFound = true;
                                }

                                if (dobj.getId().equals("fieldids" + id1)) {
                                    Log.e(DEBUG_TAG, "fieldids" + id1);
                                    fieldIds = dobj.getValue();
                                    isFieldIdsFound = true;
                                }

                                if ((isStateIdsFound) && (isFieldIdsFound)) {
                                    break first;
                                }
                            }
                        }
                    }

                    String[] arr = stateList.split(",");

                    String fillids = fieldIds;
                    String[] arrf = fillids.split(",");

                    if (!id1.equals("")) {

                        for (int j = 0; j < arr.length; j++) {

                            //boolean bool = false;
                            for (int k = 0; k < dlistFields.size(); k++) {
                                DList dobj = dlistFields.get(k);
                                if (dobj.getId().equals("field" + arrf[j] + "_0")) {

                                    boolean doshowc = true;
                                    if (arr[j].indexOf("c") == 0) {
                                        String[] arrc = arr[j].split("-");
                                        String idc = arrc[0].substring(1);
                                    }

                                    if ((doshowc) && (arr[j].indexOf("c") == 0) || arr[j].indexOf("s" + checkstateid + "s") > -1 || arr[j].indexOf("s0s") > -1 || arr[j].equals("")) {
                                        //Show the field
                                        //when we get the dlist data in a certain state we want the fields to be shows
                                        if(dobj.getStates().indexOf("s" + checkstateid + "s") > -1){
                                            if (dobj.getType().equals("hidden")) {
                                                dobj.setType(dobj.getFieldType());

                                                Log.e(DEBUG_TAG, " onLoadChangeIds()  show dlist field --> " + dobj.getFieldName());
                                            }
                                        }
                                    } else {
                                        //initially Hide the field which has a states which starts with s for eg. s2978s2979
                                        dobj.setType("hidden");
                                        Log.e(DEBUG_TAG, " onLoadChangeIds()  hide dlist field --> " + dobj.getFieldName());
                                    }
                                }
                            }// end of for dlistFields.length
                        }//end of for loop arr.length
                    }
                }

            }
        }
    }

    public String replacesqllistappNew(String fieldid, String fieldValue,
                                    String extension, String jcodelist,
                                    boolean includeself, boolean isVlist)
    {
        try {
            String jidlist = jcodelist;

            if (!jcodelist.equals("")) {
                String[] jarr = jcodelist.split(";");
                jcodelist = "";

                for (int j = 0; j < jarr.length; j++) {
                    String func = jarr[j];
                    String[] jarr2 = func.split("@@");
                    String jid = jarr2[0];
                    String[] jarr3 = jarr2[1].split("/");
                    String jvals = "";

                    for (int j3 = 0; j3 < jarr3.length; j3++) {
                        if (jarr3[j3].equals("ERROR")) {
                            Log.e("replacesqllistapp", "There is a small ERROR in SQL query of this field.");
                            break;
                        }

                        List<Field> additionalFieldDataList = getAdditionalFieldDataList();
                        List<Field> fieldsList = getFieldsList();
//                        if(isVlist){
//                            additionalFieldDataList = VlistFormActivity.vAdditionalFieldDataList;
//                            fieldsList = VlistFormActivity.vFieldsList;
//                        }else {
//                            additionalFieldDataList = FormFragment.additionalFieldDataList;
//                            fieldsList = FormFragment.fieldsList;
//                        }

                        String midlist = "";
                        String idSelected = "";
                        for(int i=0;i< additionalFieldDataList.size(); i++){
                            Field fobj = additionalFieldDataList.get(i);
                            if(fobj.getName().toLowerCase().equals("mandatory")){
                                midlist = fobj.getValue();
                            }
                            if(fobj.getName().toLowerCase().equals("idselected")){
                                idSelected = fobj.getValue();
                            }

                            if(!midlist.isEmpty() && !idSelected.isEmpty()){
                             break;
                            }
                        }

                        boolean dostop = midlist.indexOf(jarr3[j3] + "/") == 0 ||
                                midlist.contains("/" + jarr3[j3] + "/") || midlist.contains("_" + jarr3[j3] + "/") ||
                                midlist.contains("_" + jarr3[j3] + extension + "/");

                        Field fieldObj1 = null;
                        boolean isObjFound = false;
                        for (int i = 0; i < fieldsList.size(); ++i) {
                            fieldObj1 = fieldsList.get(i);
                            String id = "field" + jarr3[j3]+extension;
                            if (id.equals(fieldObj1.getId())) // field found
                            {
                                isObjFound = true;
                                break;
                            }
                        }

                        if(!isObjFound){
                           if(jarr3[j3].trim().equalsIgnoreCase("id")){
                               fieldObj1 = new Field();
                               fieldObj1.setValue(idSelected);
                               isObjFound = true;
                           }
                        }

                        if (isObjFound) {
                            if ((!dostop || !fieldObj1.getValue().trim().equals(""))
                                    || jarr3[j3].equals(jid)) {

                                if (jarr3[j3].equals(jid) && !includeself) {
                                    jvals = jvals + "" + "@j@";
                                    Log.e("jvals", "331");
                                    Log.e("----", jvals);
                                } else {
                                    String valuee = fieldObj1.getValue();
                                    if (fieldObj1.getType().toLowerCase().equals("checkbox")) {
                                          valuee=fieldObj1.getValue();
                                    }

                                    if (valuee.contains("   ---")) {
                                        valuee = valuee.split("   ---")[0];
                                    }
                                 //   valuee = valuee.trim();

                                    EncodeURIEngine uriEngine = new EncodeURIEngine();
                                    valuee = uriEngine.encodeURIComponent(valuee);
                                    Log.e("value after encoding", "EncodeURIEngine =" + valuee);
                                    valuee = valuee.replaceAll("\\+", "%2B");
                                    valuee = valuee.replaceAll("&", "%26");

                                    jvals = jvals + valuee + "@j@";
                                    Log.e("jvals", "line #388");
                                    Log.e("----", jvals);

                                }

                            } else {
                                String[] arr1 = func.split("@@");
                                func = "";
                                jvals = "";
                                break;
                            }

                        } else if (jarr3[j3].equals("self")) {
                            jvals = jvals + "self" + "@j@";
                        } else {
                            jvals = "";
                            break;
                        }

                    }//end of for jarr3
                    jcodelist = jcodelist + jvals + "@jj@";
                }//end of for jarr
                Log.e("final jcodeList", jcodelist);
            }
        }catch (Exception e){
            e.printStackTrace();
            //Added this because list wasn't showing Product List
            String jvals = "";
            jcodelist = jcodelist + jvals + "@jj@";
        }
        return jcodelist;
    }

    private String replacesqllistapp(int fieldid,
                                     String extension,
                                     String jcodelist,
                                     boolean b,
                                     String midlist) {

        String jidlist = jcodelist;
        if (!jcodelist.equals("")) {

            String[] jarr = jcodelist.split(";");
            jcodelist = "";
            for (int j = 0; j < jarr.length; j++) {
                String func = jarr[j];
                String[] jarr2 = func.split("@@");
                String jid = jarr2[0];
                String[] jarr3 = jarr2[1].split("/");
                String jvals = "";
                for (int j3 = 0; j3 < jarr3.length; j3++) {

                    Boolean dostop = midlist.indexOf(jarr3[j3] + "/") == 0 || midlist.indexOf("/" + jarr3[j3] + "/") > -1 || midlist.indexOf("_" + jarr3[j3] + "/") > -1 || midlist.indexOf("_" + jarr3[j3] + extension + "/") > -1;

                    if (jarr3[j3].equals("self")) {
                        jvals = jvals + "self" + "@j@";
                    } else {
                        jvals = "";
                        break;
                    }//end of if obj
                }//end of j3=0
//if(jvals!=""){
                jcodelist = jcodelist + "@j@" + "@jj@";
                Log.e("replacesqlnew"," LINE 432");
                Log.e("jcodelist",jcodelist);
//}//end if jvals="";
            }//end of j=0

//alert('final jidlist=='+jidlist+' jcodelist =='+jcodelist);
        }//end of if jcodelist

        return jcodelist;

    }

    public  void formatDouble(String fieldid, String doround)
    {
        String values = "";

        int fieldPos = -1;
        for(int i=0; i < getFieldsList().size(); i++){
            Field obj = getFieldsList().get(i);
            if(obj.getId().equals("field"+fieldid)){
                values = obj.getValue();
                fieldPos = i;
            }
        }

     //   values = fieldid;
        values = trim(values, " ");
        if (values.equals("")) {

            if(fieldPos != -1){
                getFieldsList().get(fieldPos).setValue("0.00");
            }
        } else {
            if (values.indexOf("+") > -1 || values.indexOf("-") > -1 || values.indexOf("*") > -1 || values.indexOf("/") > -1 || values.indexOf("^") > -1 || values.indexOf("%") > -1) {
                try {

                    EvaluateEngine evaluateEngine = new EvaluateEngine();
                    values=evaluateEngine.eval((values + "").split(" ")[0].replace("," + "g", ""));

                //    values = (values + "").split(" ")[0].replace("," + "g", "");
                } catch (Exception e) {
                    values = values;
                }//end of catch

                int sum = (int) (Integer.parseInt((values + "").split(" ")[0].replace("," + "g", "")) * 1.0);
                if (isNaN(sum)) {
                    values = "0.00";
                } else if (doround.equals("0.00")) {
                    values = "" + Math.round(Math.round(sum * 100) / 100) + ".00";//Math.round((sum))+".00";
                } else if (doround.equals("0.000")) {
                    sum = Math.round((sum) * 1000);

                    sum = sum / 1000;
                    values = "" + sum;
                } else if (doround.equals("0.0000")) {
                    sum = Math.round((sum) * 1000000000);
                    sum = sum / 1000000000;
                    values = "" + sum;
                } else {
                    sum = Math.round((sum) * 100);
                    sum = sum / 100;
                    values = "" + sum;
                }
            }

            Boolean isminus = false;
            if (values.indexOf("-") == 0) {
                isminus = true;
                values = values.substring(1);
            }
            String issearch = "";
            if (values.indexOf("=") == 0 || values.indexOf(">") == 0 || values.indexOf("<") == 0) {
                issearch = values.substring(0, 1);
                values = values.substring(1);
            }

            String nvalue = "0.00";
            try {
                String fvalues = values.split(" ")[0].replace("," + "g", "");
                Double doublevalues = 0.0;
                if (doround.equals("0.0000")) {
                    BigDecimal bd = new BigDecimal(fvalues).setScale(10, RoundingMode.HALF_UP);
                    doublevalues = bd.doubleValue();
                } else if (doround.equals("0.000")) {
                    BigDecimal bd = new BigDecimal(fvalues).setScale(3, RoundingMode.HALF_UP);

                    doublevalues = bd.doubleValue();
                } else if (doround.equals("0.00")) {
                    BigDecimal bd = new BigDecimal(fvalues).setScale(0, RoundingMode.HALF_UP);
                    doublevalues = bd.doubleValue();
                    //doublevalues = doublevalues +".00";
                } else {
                    BigDecimal bd = new BigDecimal(fvalues).setScale(2, RoundingMode.HALF_UP);

                    doublevalues = bd.doubleValue();
                }
                String sdvalue = doublevalues + "";
                sdvalue = sdvalue.contains(".") ? sdvalue.split(".")[0] : sdvalue;
                String fstr = sdvalue.indexOf(".") > 1 ? sdvalue.split(".", 2)[1] : "00";
                String[] darr = sdvalue.split(" ");
                nvalue = "." + fstr;
                int count = 0;
                for (int i = (darr.length); i >= 0; i--) {
                    count++;
                    nvalue = darr[i] + (count == 4 || count == 6 || count == 8 || count == 10 || count == 12 || count == 14 || count == 16 || count == 18 || count == 20 ? "," : "") + nvalue;
                }
            } catch (Exception e) {
                nvalue = "0.00";//values;
            }

            if(fieldPos != -1){
               getFieldsList().get(fieldPos).setValue(issearch+(isminus?"-":"")+nvalue);
            }
        }
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

    public  void formatDate(String fieldid,List<Field> fieldList) {
        String values = "";
        for(int i =0; i <fieldList.size(); i++){
            Field fObj = fieldList.get(i);
            if(fObj.getId().matches(fieldid)){
                values = fObj.getValue().trim();
            }
        }
          Log.e("FormatDate #1034","ID "+fieldid+" VALUE "+values);

        if (values.equals("NULL") || values.equals("Null") || values.equals("null")) return;

        if (values.indexOf("/") > -1) {
            String id = "#" + fieldid;
            //    $(id).val(values);

        } else if (values.indexOf("-") > -1 && values.split("-").length == 3) {
            String id = "#" + fieldid;
            String[] st = values.split("-");
            String st1 = st[1] + "/" + st[2] + "/" + st[0];
            //  $(id).val(values);
        }
        repeatcounter = 0;

        //javascript code
        /*
        function formatDate(fieldid) {
  var values = document.getElementById(fieldid).value;
  values = trim(values," ");
  console.log("ID "+fieldid+" VALUE "+values);
  if(fieldid.includes("field4998") && values==""){
     var dt=new Date().toMMDDYYYYString();
     var d=dt.split("/");
     setTimeout(function(){ $("#"+fieldid).val(d[2]+"-"+d[1]+"-"+d[0]); }, 100);


  }
  else if(values=="Null" || values=="NULL" || values=="null") return true;

    if(values.indexOf("/")>-1){
     var id="#"+fieldid;
     $(id).val(values);

    }else if(values.indexOf("-")>-1 && values.split("-").length==3 ){
           var id="#"+fieldid;
      var st=values.split("-");
      var st1=st[1]+"/"+st[2]+"/"+st[0];
      $(id).val(values);
    }
repeatcounter=0;


}
         */

    }

    public  String checkadcombo(int position ,
                              int fieldid,
                              String extension,
                              String jcodelist,
                              List<Field> fieldList,
                              boolean ismdcombo,
                                String mdlist) {

        String result = "";
        String jidlist = jcodelist;
        Field obj = fieldList.get(position);

        if(obj != null){
            String fieldValue = obj.getValue().trim();
            if(fieldValue=="%") return "";
            String oldfieldValue = fieldValue;
            if(ismdcombo){
                //Todo : pending
                //fieldValue = fieldValue.substring(fieldValue.lastIndexOf(",")+1,fieldValue.length()).split(",")[0],"");
            }

        if ((trim(fieldValue, "") != "") && (trim(fieldValue, "") != "0") && (trim(fieldValue, "") != "0.00") && (fieldValue.indexOf("<") < 0) && (fieldValue.indexOf(">") < 0))//
        {
            jcodelist = replacesqllistapp(fieldid, extension, jcodelist, true,mdlist);
            //alert("end functionlist=="+functionlist);
            if (jcodelist != "") {
                jcodelist = jcodelist.replace("/\"+/g", "%2B");
                jcodelist = jcodelist.replace("/\"&/g", "%26");

               result =   retrieveString3(mPrefManager.getClientServerUrl() + "SaveFormField.do?actn=checkadcombo&extension=" + extension + "&jcodelist=" + jcodelist + "&jidlist=" + jidlist + "&ask=COMMAND_NAME_1&cloudcode="+mPrefManager.getCloudCode()+"&token"+mPrefManager.getAuthToken(), "reportForm1", "top.hideLoader();");//dcombocounter=0;
            }
        }
    }
        return result;
    }

    public static String retrieveString3(String surl, String reportForm1, String s1) {
        Double randomnumber = Math.floor(Math.random() * 1111);
        surl = surl + "&random=" + randomnumber;//+getFormAsString(nameOfFormToPost);
        return surl;
    }

    public void  vshowhide(String formid,String reportid, String fheight,
                           String namevalues,String updatefunction,String fieldid){

        EncodeURIEngine uriEngine = new EncodeURIEngine();
        String retrievejavascript = "fillfieldvalues(\\'"+uriEngine.encodeURIComponent(namevalues)+"\\');";
        retrievejavascript = retrievejavascript.replaceAll("\\+","%2B");
        retrievejavascript = retrievejavascript.replaceAll("&","%26");
        retrievejavascript = retrievejavascript.replace("\\","%5C");


        updatefunction = updatefunction.replace("/\\|","\"");

    }

    public  String fn(String formID,
                    String fetchformid,
                    String fieldId,
                    String matchingField,
                    String matchingFieldIds,
                      String fieldValue)
    {

        String url = "";
        EncodeURIEngine uriEngine = new EncodeURIEngine();

        String formid = formID;
        fieldValue = uriEngine.encodeURIComponent(fieldValue);

        List<Field> fieldList = getFieldsList();
        if(!fieldValue.equals("") && !matchingFieldIds.equals(""))
        {
            String[] arr=matchingFieldIds.split("/");
            String value="";
            String matchingFieldValues="";

            for(int i=0;i<arr.length;i++)
            {
                    for(int j =0; j < fieldList.size(); j++) {
                        Field fobj = fieldList.get(j);

                        if (fobj.getId().equals("field"+arr[i])) {
                            value = fobj.getValue();
                            matchingFieldValues = matchingFieldValues+value+"@@";
                            Log.e("fn", " matchingFieldValues "+matchingFieldValues+ "j = "+ j);
                            break;
                        }
                }
            }

            matchingFieldValues = uriEngine.encodeURIComponent(matchingFieldValues);
            matchingFieldValues = matchingFieldValues.replace("\\+","%2B");
            matchingFieldValues = matchingFieldValues.replace("\\&","%26");
            matchingField = uriEngine.encodeURIComponent(matchingField);

            String fetchstr = "fetchcounter.splice(fetchcounter.indexOf("+fieldId+"), 1);";
            url = retrieveString3(mPrefManager.getClientServerUrl()+"SaveFormField.do?actn=fetchFormField&formid="
                            +formid+"&fetchformid="+fetchformid+"&fieldid="+fieldId+"&fieldValue="+fieldValue
                            +"&matchingField="+matchingField+"&matchingFieldValues="+matchingFieldValues
                            +"&ask=COMMAND_NAME_1&cloudcode="+mPrefManager.getCloudCode()+"&token="+mPrefManager.getAuthToken()
                    ,"reportForm1",fetchstr) ;

            url = url +"&type=json";

            Log.e("FUNCTION HELPER fn= ",url);
        }

        return url;
    }

//    public int checkPattern(String fieldId,
//                            String regexPattern,
//                            String extension,
//                            String fieldName,
//                            boolean isDlist ) {
//
//        regexPattern = regexPattern.replace("\\\\","\\");
//
//        int position = -1;
//        try {
//
//            String field = "field" + fieldId + extension;
//            String fieldValue = "";
//            if (field.length() == 0) {
//                Log.e("checkPattern", "line#844 field value is empty - return");
//                return -1;
//            }
//
//            if (isDlist) {
//
//            } else {
//
//                try {
//                    boolean isFieldFound = false;
//                    for (int i = 0; i < FormFragment.fieldsList.size(); i++) {
//                        Field fieldObj = FormFragment.fieldsList.get(i);
//                        if (fieldObj.getId().equals(field)) {
//                            fieldValue = fieldObj.getValue();
//                            isFieldFound = true;
//                            position = i;
//                            break;
//                        }
//                    }
//
//                    if (!isFieldFound) {
//                        Log.e("checkPattern", "line#865 return");
//                        return -1;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (fieldName.toLowerCase().contains("e-mail") ||
//                    fieldName.toLowerCase().contains("email") ||
//                    regexPattern.toLowerCase().contains("email")) {
//
//                regexPattern = "([\\w\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
//            }else if (fieldName.toLowerCase().contains("mobile")
//                    || regexPattern.toLowerCase().contains("mobile")){
//
//                regexPattern = "^[0-9\\-\\+]{9,15}$";
//            }else if (regexPattern.toLowerCase().contains("aadhar")){
//                regexPattern =  "(?!0{12})^[0-9][0-9]{11}$";
//            }else if (regexPattern.toLowerCase().contains("url")){
//                regexPattern = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
//            }
//
//            if (!fieldValue.equals("")) {
//                Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
//
//                Matcher matcher = pattern.matcher(fieldValue);
//                boolean matchFound = matcher.find();
//
//                if (!matchFound) {
//                    //add error to field
//                    if(position != -1){
//                        FormFragment.fieldsList.get(position).setValue("");
//                        FormFragment.fieldsList.get(position).setShowErrorMessage(true);
//                        FormFragment.fieldsList.get(position).setErrorMessage("Enter a valid "+ fieldName);
//                        Log.e("Pattern doesn't match", "  -- Add Error");
//                    }
//                } else {
//                    if(position != -1){
//                        FormFragment.fieldsList.get(position).setShowErrorMessage(false);
//                        FormFragment.fieldsList.get(position).setErrorMessage("");
//                        //remove error from field
//                        Log.e("Pattern matches", "  -- Remove Error");
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        //if pattern doesn't match return the position of the field else return -1
//        Log.e("check pattern", "RESULT POSITION = "+position);
//        return position;
//    }

//    public boolean checkSave(ProgressDialog validationDialog){
//        boolean err = false;
//        String idlist = "";
//
//        if (!err){
//            idlist= FormFragment.form.getFormSaveCheck();
//            idlist = idlist.replace("&lt;","<");
//            idlist = idlist.replace("&gt;",">");
//
//            String idlist1 = FormFragment.form.getFormSaveCheckNames();
//            idlist1 = idlist1.replace("&lt;","<");
//            idlist1 = idlist1.replace("&gt;",">");
//
//            if(idlist.isEmpty() && idlist1.isEmpty()){
//                return false;
//            }
//
//            String[] arr = idlist.split(";");
//            String[] arr1 = idlist1.split(";");
//
//            String value = "";
//            String firstname = "";
//            String secondname = "";
//            boolean isfirstdate = false;
//            boolean isseconddate = false;
//            long firstvalue = 0;
//            long secondvalue = 0;
//            String firstValue = "";
//            String secondValue = "";
//
//            for(int i=0;i<arr.length;i++) {
//                boolean doalertonly = false;
//                if (arr[i].trim().length() > 3) {
//                    if (arr[i].indexOf("ALERT:") == 0) {
//                        doalertonly = true;
//                        arr[i] = arr[i].substring(6);
//                    }
//                    String[] carr = new String[6];
//                    carr[0] = "==";
//                    carr[1] = "!=";
//                    carr[2] = ">=";
//                    carr[3] = "<=";
//                    carr[4] = "<<";
//                    carr[5] = ">>";
//
//                    boolean notchecked = true;
//                    for (int x = 0; x < carr.length; x++) {
//                        if (arr[i] != null && (arr[i].indexOf(carr[x]) > 0) && notchecked) {
//                            firstname = trim(arr[i].split(carr[x])[0], "");
//                            secondname = trim(arr[i].split(carr[x])[1], "");
//
//                            if (firstname.indexOf("_") > -1) {
//                                String[] arr2 = firstname.split("_");
//                                firstname = arr2[1];
//                                String[] arr3 = secondname.split("_");
//                                secondname = arr3[1];
//
//                                String[] arr4 = new String[0];
//
//                                Field fieldDlist = FormFragment.fieldsList.get(FormFragment.dlistArrayPosition);
//                                List<Field> dlistArray = fieldDlist.getdListArray();
//                                first:
//                                for (int j = 0; j < dlistArray.size(); j++) {
//                                    Field fObj = dlistArray.get(j);
//                                    if(fObj.getId().equals("field"+arr2[0])){
//                                        List<DList> dlistFields = fObj.getdListsFields();
//                                        for (int k = 0; k < dlistFields.size(); k++) {
//                                            DList dobj = dlistFields.get(k);
//
//                                            if (dobj.getId().equals("contentrows" + arr2[0])) {
//                                                Log.e(DEBUG_TAG,"contentrows" + arr2[0] + " = " + dobj.getValue());
//                                                arr4 = dobj.getValue().split(",");
//                                                break first;
//                                            }
//                                        }
//                                    }
//                                }
//
//                                if (arr1 != null) {
//                                    for (int l = 0; l < arr4.length; l++) {
//                                        isfirstdate = false;
//                                        isseconddate = false;
//                                        if (!arr4[l].equals("")) {
//                                            //We check for dlistarray here
////                                          // and then loop dlistfield using the contentrows value ie. 1,2,3
//                                           String vreplace =  DListFieldHelper.getDlistFieldValue(arr2[0],firstname,arr4[l]);
////
//                                             int vlength = vreplace.length();
//                                            if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
//                                                vreplace = vreplace.replace(",", "");
//                                            else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
//                                                vreplace = vreplace.replace(",", "");
//
//                                            if ((vlength == 10 || vlength == 16) && vreplace.indexOf('/') == 2 && vreplace.lastIndexOf('/') == 5) {
//                                                int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
//                                                int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
//                                                int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
//                                                int hr1 = 0;
//                                                int min1 = 0;
//                                                if (vreplace.length() > 10) {
//                                                    hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
//                                                    min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
//                                                }
//                                                firstvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
//                                                isfirstdate = true;
//                                            } else if ((vlength == 10 || vlength == 16) && (vreplace.indexOf('-') == 4 && vreplace.lastIndexOf('-') == 7 || vreplace.indexOf("T") == 10)) {
//                                                // String arr2;String arr3;
//                                                if (vreplace.indexOf("T") == 10) {
//                                                    arr2 = vreplace.split("T");
//                                                    arr3 = vreplace.split("T")[0].split("-");
//                                                } else {
//                                                    arr3 = vreplace.split("-");
//                                                }
//                                                int dt1 = Integer.parseInt(arr3[2]);
//                                                int mon1 = Integer.parseInt(arr3[1]) - 1;
//                                                int yr1 = Integer.parseInt(arr3[0]);
//                                                int hr1 = 0;
//                                                int min1 = 0;
//                                                if (vreplace.length() > 10 && vreplace.indexOf("T") == 10) {
//                                                    hr1 = Integer.parseInt(vreplace.split("T")[1].split(":")[0]);
//                                                    min1 = Integer.parseInt(vreplace.split("T")[1].split(":")[1]);
//                                                }
//                                                firstvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
//                                                isfirstdate = true;
//                                            } else {
//                                                try{
//                                                float CheckNum = Float.parseFloat(vreplace);
//                                                if (isNaN(CheckNum)) {
//                                                    firstvalue = Integer.parseInt(vreplace);
//                                                } else {
//                                                    double m = (Float.parseFloat(vreplace) * 1.0);
//                                                    firstvalue = (long) m;
//
//                                                }}catch (NumberFormatException e){
//                                                    e.printStackTrace();
//                                                    firstvalue = 0;
//                                                    if(vreplace.matches(".+")){
//                                                            firstValue = vreplace;
//                                                    }else{
//                                                        //setting empty here as the control goes to else and we don't get the popup
//                                                        if(vreplace.isEmpty()){
//                                                            firstValue = "empty";
//                                                        }
//                                                    }
//                                                }
//
//                                            } // end of else
//                                            boolean isSecondNameFound = false;
//
//                                            vreplace =  DListFieldHelper.getDlistFieldValue(arr3[0],secondname,arr4[l]);
//                                            if(!vreplace.equals("")){
//                                                isSecondNameFound = true;
//                                            }
//
////                                            for (int p = 0; p < FormFragment.fieldsList.size(); p++) {
////                                                Field pObj = FormFragment.fieldsList.get(p);
////                                                if (pObj.getId().equals("field" + secondname + "_" + arr4[l])) {
////                                                    vreplace = pObj.getValue();
////                                                    isSecondNameFound = true;
////                                                    break;
////                                                }
////                                            }
//                                            if (isSecondNameFound) { //  if containing second name is found
//                                                vlength = vreplace.length();
//                                                if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
//                                                    vreplace = vreplace.replace(",", "");
//                                                else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
//                                                    vreplace = vreplace.replace(",", "");
//
//                                                if ((vlength == 10 || vlength == 16) && vreplace.indexOf('/') == 2 && vreplace.lastIndexOf('/') == 5) {
//                                                    int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
//                                                    int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
//                                                    int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
//                                                    int hr1 = 0;
//                                                    int min1 = 0;
//                                                    if (vreplace.length() > 10) {
//                                                        hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
//                                                        min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
//                                                    }
//                                                    secondvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
//                                                    isseconddate = true;
//                                                } else if ((vlength == 10 || vlength == 16) && (vreplace.indexOf('-') == 4 && vreplace.lastIndexOf('-') == 7 || vreplace.indexOf("T") == 10)) {
//                                                    if (vreplace.indexOf("T") == 10) {
//                                                        arr2 = vreplace.split("T");
//                                                        arr3 = vreplace.split("T")[0].split("-");
//                                                    } else {
//                                                        arr3 = vreplace.split("-");
//                                                    }
//                                                    int dt1 = Integer.parseInt(arr3[2]);
//                                                    int mon1 = Integer.parseInt(arr3[1]) - 1;
//                                                    int yr1 = Integer.parseInt(arr3[0]);
//                                                    int hr1 = 0;
//                                                    int min1 = 0;
//                                                    if (vreplace.length() > 10 && vreplace.indexOf("T") == 10) {
//                                                        hr1 = Integer.parseInt(vreplace.split("T")[1].split(":")[0]);
//                                                        min1 = Integer.parseInt(vreplace.split("T")[1].split(":")[1]);
//                                                    }
//                                                    secondvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
//                                                    isseconddate = true;
//                                                } else {
//                                                    float CheckNum = Float.parseFloat(vreplace);
//                                                    if (isNaN(CheckNum)) {
//                                                        secondvalue = Integer.parseInt(vreplace);
//                                                    } else {
//                                                        secondvalue = (int) (Double.parseDouble(vreplace) * 1.0);
//                                                    }
//                                                }
//                                            } // end of isSecondName found
//                                            else {
//                                                try{
//                                                secondvalue = (int) (Double.parseDouble(secondname) * 1.0);
//                                                if (isNaN(secondvalue)) {
//                                                    secondvalue = Integer.parseInt(secondname);
//                                                }
//                                                }catch (NumberFormatException e){
//                                                    secondvalue = 0;
//                                                    if(secondname.matches(".+")){
//                                                        if(secondname.isEmpty()){
//                                                            secondValue = "empty" ;
//                                                        }else{
//                                                            secondValue = secondname ;
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            boolean donotalert = false;
//                                            if(!firstValue.isEmpty() && !secondValue.isEmpty()){
//                                                if (carr[x].equals("=="))
//                                                    donotalert = (firstValue.equals(secondValue)) || (isfirstdate != isseconddate);
//                                                else if (carr[x].equals("!="))
//                                                    donotalert = (!firstValue.equals(secondValue)) || (isfirstdate != isseconddate);
//
//                                            }else{
//                                                if (carr[x].equals("=="))
//                                                    donotalert = (firstvalue == secondvalue) || (isfirstdate != isseconddate);
//                                                else if (carr[x].equals("!="))
//                                                    donotalert = (firstvalue != secondvalue) || (isfirstdate != isseconddate);
//                                                else if (carr[x].equals(">="))
//                                                    donotalert = (firstvalue >= secondvalue) || (isfirstdate != isseconddate);
//                                                else if (carr[x].equals("<="))
//                                                    donotalert = (firstvalue <= secondvalue) || (isfirstdate != isseconddate);
//                                                else if (carr[x].equals("<<"))
//                                                    donotalert = (firstvalue < secondvalue) || (isfirstdate != isseconddate);
//                                                else if (carr[x].equals(">>"))
//                                                    donotalert = (firstvalue > secondvalue) || (isfirstdate != isseconddate);
//                                            }
//
//                                            if (!donotalert) {
//                                                String condition = makeiteasy(arr1[i]);
//                                             //   dismissDialog(validationDialog);
//                                                DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
//                                                err = true;
//
//
//                                                return err;
//                                            }//end of doalertonly
//                                            firstValue = "";
//                                            secondValue = "";
//                                        }
//                                    }//end of for (arr4)
//                                }/*end of arr1 != null*/
//                            }/*end of firstname.indexOF */ else {
//                                isfirstdate = false;
//                                isseconddate = false;
//                                String vreplace = "";
//                                for (int n = 0; n < FormFragment.fieldsList.size(); n++) {
//                                    Field nObj = FormFragment.fieldsList.get(n);
//                                    if (nObj.getId().equals("field" + firstname)) {
//                                        vreplace = nObj.getValue();
//                                        break;
//                                    }
//                                }
//                                int vlength = vreplace.length();
//                                if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
//                                    vreplace = vreplace.replace(",", "");
//                                else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
//                                    vreplace = vreplace.replace(",", "");
//
//                                if ((vlength == 10 || vlength == 16) && vreplace.indexOf("/") == 2 && vreplace.lastIndexOf("/") == 5) {
//                                    int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
//                                    int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
//                                    int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
//                                    int hr1 = 0;
//                                    int min1 = 0;
//                                    if (vreplace.length() > 10) {
//                                        hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
//                                        min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
//                                    }
//                                    firstvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
//                                    isfirstdate = true;
//                                } else if ((vlength == 10 || vlength == 16) && (vreplace.indexOf('-') == 4 && vreplace.lastIndexOf('-') == 7 || vreplace.indexOf("T") == 10)) {
//                                    String[] arr2;
//                                    String[] arr3;
//                                    if (vreplace.indexOf("T") == 10) {
//                                        arr2 = vreplace.split("T");
//                                        arr3 = vreplace.split("T")[0].split("-");
//                                    } else {
//                                        arr3 = vreplace.split("-");
//                                    }
//                                    int dt1 = Integer.parseInt(arr3[2]);
//                                    int mon1 = Integer.parseInt(arr3[1]) - 1;
//                                    int yr1 = Integer.parseInt(arr3[0]);
//                                    int hr1 = 0;
//                                    int min1 = 0;
//                                    if (vreplace.length() > 10 && vreplace.indexOf("T") == 10) {
//                                        hr1 = Integer.parseInt(vreplace.split("T")[1].split(":")[0]);
//                                        min1 = Integer.parseInt(vreplace.split("T")[1].split(":")[1]);
//                                    }
//                                    firstvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
//                                    isfirstdate = true;
//                                } else {
//                                    try {
//                                        float CheckNum = Float.parseFloat(vreplace);
//                                        if (isNaN(CheckNum))
//                                            firstvalue = Integer.parseInt(vreplace);
//                                        else
//                                            firstvalue = (int) (Double.parseDouble(vreplace) * 1.0);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        firstvalue = 0;
//                                        if(vreplace.matches(".+")){
//                                            if(vreplace.isEmpty()){
//                                                firstValue = "empty";
//                                            }else{
//                                                firstValue = vreplace;
//                                            }
//                                        }
//                                    }
//                                }
//                                boolean isSecondNameFound = false;
//                                for (int p = 0; p < FormFragment.fieldsList.size(); p++) {
//                                    Field pObj = FormFragment.fieldsList.get(p);
//                                    if (pObj.getId().equals("field" + secondname)) {
//                                        vreplace = pObj.getValue();
//                                        isSecondNameFound = true;
//                                        break;
//                                    }
//                                }
//                                if (isSecondNameFound) {
//                                    vlength = vreplace.length();
//                                    if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7) && vreplace.lastIndexOf('.') == (vlength - 3))
//                                        vreplace = vreplace.replace(",", "");
//                                    else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8) && vreplace.lastIndexOf('.') == (vlength - 4))
//                                        vreplace = vreplace.replace(",", "");
//
//                                    if ((vlength == 10 || vlength == 16) && vreplace.indexOf('/') == 2 && vreplace.lastIndexOf('/') == 5) {
//                                        int dt1 = Integer.parseInt(vreplace.substring(0, 2), 10);
//                                        int mon1 = Integer.parseInt(vreplace.substring(3, 5), 10) - 1;
//                                        int yr1 = Integer.parseInt(vreplace.substring(6, 10), 10);
//                                        int hr1 = 0;
//                                        int min1 = 0;
//                                        if (vreplace.length() > 10) {
//                                            hr1 = Integer.parseInt(vreplace.substring(11, 13), 10);
//                                            min1 = Integer.parseInt(vreplace.substring(14, 16), 10);
//                                        }
//                                        secondvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
//                                        isseconddate = true;
//                                    } else if ((vlength == 10 || vlength == 16) && (vreplace.indexOf('-') == 4 && vreplace.lastIndexOf('-') == 7 || vreplace.indexOf("T") == 10)) {
//                                        String[] arr2;
//                                        String[] arr3;
//                                        if (vreplace.indexOf("T") == 10) {
//                                            arr2 = vreplace.split("T");
//                                            arr3 = vreplace.split("T")[0].split("-");
//
//                                        } else {
//                                            arr3 = vreplace.split("-");
//                                        }
//                                        int dt1 = Integer.parseInt(arr3[2]);
//                                        int mon1 = Integer.parseInt(arr3[1]) - 1;
//                                        int yr1 = Integer.parseInt(arr3[0]);
//                                        int hr1 = 0;
//                                        int min1 = 0;
//                                        if (vreplace.length() > 10 && vreplace.indexOf("T") == 10) {
//                                            hr1 = Integer.parseInt(vreplace.split("T")[1].split(":")[0]);
//                                            min1 = Integer.parseInt(vreplace.split("T")[1].split(":")[1]);
//                                        }
//                                        secondvalue = getDateInMillis(dt1, mon1, yr1, hr1, min1);
//                                        isseconddate = true;
//                                    } else {
//                                        try {
//                                            float CheckNum = Float.parseFloat(vreplace);
//                                            if (isNaN(CheckNum))
//                                                secondvalue = Integer.parseInt(vreplace);
//                                            else
//                                                secondvalue = (int) (Double.parseDouble(vreplace) * 1.0);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            secondvalue = 0;
//                                            if(vreplace.matches(".+")){
//                                                secondValue = vreplace;
//                                            }else{
//                                                secondValue = "empty";
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    if (secondname.indexOf("${today") > -1) {
//                                        if (secondname.equals("${today}")) {
//                                            secondvalue = Calendar.getInstance().getTimeInMillis();
//                                            isseconddate = true;
//                                        } else {
//                                            int goback = Integer.parseInt(secondname.substring(8, secondname.indexOf('}')), 10);
//                                            Calendar cal = Calendar.getInstance();
//                                            cal.set(Calendar.DAY_OF_MONTH, -goback);
//                                            secondvalue = cal.getTimeInMillis();
//                                            isseconddate = true;
//                                        }
//                                    } else {
//                                        try {
//                                            float CheckNum = Float.parseFloat(secondname);
//                                            if (isNaN(CheckNum))
//                                                secondvalue = Integer.parseInt(secondname);
//                                            else
//                                                secondvalue = (long) (Double.parseDouble(secondname) * 1.0);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            secondvalue = 0;
//                                            if(secondname.matches(".+")){
//                                                secondValue = secondname;
//                                            }else{
//                                                secondValue = "emtpy";
//                                            }
//                                        }
//                                    }
//                                }
//                                boolean donotalert = false;
//                                //  Log.e("firstValue = ", String.valueOf(firstvalue));
//                                //  Log.e("secondvalue = ", String.valueOf(secondvalue));
//                                if(!firstValue.isEmpty() && !secondValue.isEmpty()){
//                                    Log.e(DEBUG_TAG, "firstValue = "+ firstValue + " , secondValue = " + secondValue);
//                                    if (carr[x].equals("=="))
//                                        donotalert = (firstValue.equals(secondValue)) || (isfirstdate != isseconddate);
//                                    else if (carr[x].equals("!="))
//                                        donotalert = (!firstValue.equals(secondValue)) || (isfirstdate != isseconddate);
//
//                                }else{
//                                    Log.e(DEBUG_TAG, "firstvalue = "+ firstvalue + " , secondvalue = " + secondvalue);
//                                    if (carr[x].equals("=="))
//                                        donotalert = (firstvalue == secondvalue) || (isfirstdate != isseconddate);
//                                    else if (carr[x].equals("!="))
//                                        donotalert = (firstvalue != secondvalue) || (isfirstdate != isseconddate);
//                                    else if (carr[x].equals(">="))
//                                        donotalert = (firstvalue >= secondvalue) || (isfirstdate != isseconddate);
//                                    else if (carr[x].equals("<="))
//                                        donotalert = (firstvalue <= secondvalue) || (isfirstdate != isseconddate);
//                                    else if (carr[x].equals("<<"))
//                                        donotalert = (firstvalue < secondvalue) || (isfirstdate != isseconddate);
//                                    else if (carr[x].equals(">>"))
//                                        donotalert = (firstvalue > secondvalue) || (isfirstdate != isseconddate);
//                                }
//
//                                if (!donotalert) {
//                                    String condition = makeiteasy(arr1[i]);
//                                    if (!doalertonly) {
//                                     //   dismissDialog(validationDialog);
//                                        DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
//                                        err = true;
//                                        return err;
//                                    } else {
//                                        //There is notification here in hybrid app
//                                        //showing alert here as well in android
//                                     //   dismissDialog(validationDialog);
//                                        DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
//                                        err = true;
//                                        return err;
//                                    }
//                                }
//                            } //end of else
//                            notchecked = false;
//                        }
//                    }//end of for (carr)
//                    if (notchecked) {
//                        String condition = makeiteasy(arr1[i]);
//                       // dismissDialog(validationDialog);
//                        DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
//                    }
//                }/*end of  if (trim.length > 3) */ else {
//                    String condition = makeiteasy(arr1[i]);
//                   // dismissDialog(validationDialog);
//                    DialogUtil.showAlertDialog((Activity) context, "Please check condition", condition, false, false);
//                }
//            }
//    }
//
//       // dismissDialog(validationDialog);
//        return err;
//    }

    private void dismissDialog(ProgressDialog dialog) {
        if(dialog != null){
            dialog.dismiss();
        }
    }

//    public String makeiteasy(String condition){
//        //alert(condition);
//        condition = condition.replace("ALERT:","Warning: ");
//        condition = condition.replace("=="," should be ");// equal to
//        //alert(condition);
//        condition = condition.replace(">>"," should be greater than ");
//        condition = condition.replace("<<"," should be less than ");
//        condition = condition.replace(">="," should be greater than or equal to ");
//        condition = condition.replace("<="," should be less than or equal to ");
//        condition = condition.replaceAll("/  /"," ");
//        Log.e("condition = ", condition);
//        return condition;
//    }

//    private long getDateInMillis(int dt1, int mon1,int yr1, int hr1,int min1){
//
//        long timeInMillisec = 0;
//       try {
//           Calendar cal = Calendar.getInstance(); //current moment calendar
//           cal.set(Calendar.DAY_OF_MONTH, dt1);
//           cal.set(Calendar.MONTH,mon1);
//           cal.set(Calendar.YEAR,yr1);
//
//           cal.set(Calendar.HOUR_OF_DAY, hr1);
//           cal.set(Calendar.MINUTE, min1);
//           cal.set(Calendar.SECOND, 0); //if you don't care about seconds
//        //   final Date myDate = cal.getTime(); //assign the date object you need from calendar
//           timeInMillisec = cal.getTimeInMillis();
//
//           System.out.println(timeInMillisec);
//       }catch (Exception e){
//           e.printStackTrace();
//       }
//
//       return  timeInMillisec;
//
//    }

    public void onDestroy() {
        if(context != null) {
            context = null;
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

    public List<Field> getAdditionalFieldDataList() {
        return additionalFieldDataList;
    }

    public void setAdditionalFieldDataList(List<Field> additionalFieldDataList) {
        this.additionalFieldDataList = additionalFieldDataList;
    }
}
