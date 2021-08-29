package com.example.myapplication.function;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.Constant;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.dlist.view.DListFormActivity;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.vdlist.VlistDlistFormActivity;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DlistFunctionHelper {

    private static int dcombocounter = 0;
    Context context;
    private SharedPrefManager mPrefManager;
    private DatabaseManager dbManager;
    String fieldPositions = "";
    private List<Field> fieldsList;
    private List<Field> additionalFieldDataList;
    private List<DList> dlistFieldValues;


    public DlistFunctionHelper(Context mContext) {
        this.context = mContext;
        mPrefManager = new SharedPrefManager(context.getApplicationContext());
        dbManager = new DatabaseManager(context.getApplicationContext());
        dbManager.open();
    }


    public  String load(int fieldid, List<String> oArr, Boolean issql, String extension,
                        String jcodelist, String fieldtype, Boolean mdcombo,boolean isVlist)
    {

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

                for(int i=0; i< getDlistFieldValues().size(); i++){
                    DList dobj = getDlistFieldValues().get(i);
                    if(dobj.getId().equals(filtervalue)){
                        filtervalue = dobj.getValue();
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
                            + "&filtervalue="   + filtervalue
                            + "&ask=COMMAND_NAME_1"
                            + "random="+randomnumber;
                    return s;
                } else {

                   //changing extension from _1,_2 to _0
                    extension = "_0";
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


    private static String retrieveDCombo(String s, String reportForm1, String oTxt) {
        Double randomnumber = Math.floor(Math.random() * 1111);
        s = s + "&random=" + randomnumber;//+getFormAsString(nameOfFormToPost);
        Log.e("url", s);//alert(url);
        return s;
    }

    public String replacesqllistappNew(String fieldid, String fieldValue,
                                       String extension, String jcodelist,
                                       boolean includeself,boolean isVlist)
    {
        try {
//            List<Field> additionalFieldDataList = null;
//            List<Field> fieldsList = null;
//            List<DList> dlistFieldValues = null;
//            if(isVlist){
//                additionalFieldDataList =  VlistFormActivity.vAdditionalFieldDataList;
//                fieldsList = VlistFormActivity.vFieldsList;
//                dlistFieldValues = VlistDlistFormActivity.dlistFieldValues;
//            }else{
//                additionalFieldDataList =  FormFragment.additionalFieldDataList;
//                fieldsList = FormFragment.fieldsList;
//                dlistFieldValues = DListFormActivity.dlistFieldValues;
//            }


            String jidlist = jcodelist;
            String midlist = "";
            for(int i=0;i< getAdditionalFieldDataList().size(); i++){
                Field fobj = getAdditionalFieldDataList().get(i);
                if(fobj.getName().toLowerCase().equals("mandatory")){
                    midlist = fobj.getValue();
                    break;
                }
            }

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


                        boolean dostop = midlist.indexOf(jarr3[j3] + "/") == 0 ||
                                midlist.contains("/" + jarr3[j3] + "/") ||
                                midlist.contains("_" + jarr3[j3] + "/") ||
                                midlist.contains("_" + jarr3[j3] + extension + "/");

                        //CHECK IN DLIST
                        DList fieldObj1 = null;
                        boolean isObjFound = false;
                        for (int i = 0; i < getDlistFieldValues().size(); ++i) {
                            fieldObj1 = getDlistFieldValues().get(i);
                            String id = "field" + jarr3[j3]+extension;
                            if (id.equals(fieldObj1.getId())) // field found
                            {
                                isObjFound = true;
                                break;
                            }
                        }

                        //MAIN FORM
                        Field fieldObj2 = null;
                        if(!isObjFound){
                            fieldObj1 = null;
                            for (int i = 0; i < getFieldsList().size(); ++i) {
                                fieldObj2 = getFieldsList().get(i);
                                String id = "field" + jarr3[j3];
                                if (id.equals(fieldObj2.getId())) // field found
                                {
                                    isObjFound = true;
                                    break;
                                }
                            }
                        }

                        if(!isObjFound){
                            if(jarr3[j3].trim().equalsIgnoreCase("id")){
                                fieldObj1 = new DList();
                                fieldObj1.setValue("0");
                                if(fieldObj1 == null){
                                    fieldObj2 = new Field();
                                    fieldObj2.setValue("0");
                                }
                                isObjFound = true;
                            }
                        }

                        if (isObjFound) {
                            String fValue ="";
                            String fType = "";

                            if(fieldObj1 != null){
                                fValue = fieldObj1.getValue();
                                fType = fieldObj1.getType();
                            }else{
                                fValue = fieldObj2.getValue();
                                fType = fieldObj2.getDataType();
                            }

                            if ((!dostop) || (!fValue.equals(""))
                                    || (jarr3[j3].equals(jid))) {

                                if (jarr3[j3].equals(jid) && !includeself) {
                                    jvals = jvals + "" + "@j@";
                                } else {

                                    String valuee = fValue;
                                    if (fType.toLowerCase().equals("checkbox")) {
                                        //  valuee=$(obj).is(':checked');
                                        valuee=fValue;
                                        //  valuee = fieldObj.getInputValueByUser();
                                    }
                                    if (valuee.contains("   ---")) {
                                        valuee = valuee.split("   ---")[0];
                                        valuee = valuee.trim();
                                    }

                                    EncodeURIEngine uriEngine = new EncodeURIEngine();
                                    valuee = uriEngine.encodeURIComponent(valuee);
                                   // Log.e("value after encoding", "EncodeURIEngine =" + valuee);
                                    valuee = valuee.replaceAll("\\+", "%2B");
                                    valuee = valuee.replaceAll("&", "%26");
                                    jvals = jvals + valuee + "@j@";

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
             //   Log.e("final jcodeList", jcodelist);
            }
        }catch (Exception e){
            e.printStackTrace();
            //Added this because list wasn't showing Product List
            String jvals = "";
            jcodelist = jcodelist + jvals + "@jj@";
        }
        return jcodelist;
    }


    public String evaluatesql(String fieldid, String extension,
                              String jcodelist, String fieldValue, List<DList> dlist,boolean isVlist)
    {



        String finalURL = "";
        String jidlist = jcodelist;

        if ((fieldValue.equals(""))) {
            //get #mandatory from additionalfields
            String midlist = "";
            for(int i=0;i< getAdditionalFieldDataList().size(); i++){
                Field fobj = getAdditionalFieldDataList().get(i);
                if(fobj.getId().toLowerCase().equals("mandatory")){
                    midlist = fobj.getValue();
                    break;
                }
            }
            boolean dostop = midlist.indexOf(fieldid + "/") == 0 || midlist.contains("/" + fieldid + "/") || midlist.contains("_" + fieldid + "/");
            if (dostop) return "";
        }
        if ((!fieldValue.contains("<")) && (!fieldValue.contains(">"))) {

            jcodelist = replacesqllistappNew(fieldid, fieldValue, extension, jcodelist, true,isVlist);

            if (!jcodelist.equals("")) {
                jcodelist = jcodelist.replaceAll("\\+", "%2B");
                jcodelist = jcodelist.replaceAll("&", "%26");

                int randomnumber = (int) Math.floor(Math.random() * 1111);
                finalURL = mPrefManager.getClientServerUrl() + "getFunction.do?actn=evaluatesql&extension=" + extension + "&jcodelist=" + jcodelist + "&jidlist=" + jidlist + "&ask=COMMAND_NAME_1" + "&random=" + randomnumber + "&cloudcode=" + mPrefManager.getCloudCode() + "&token=" + mPrefManager.getAuthToken() +"&type=json";
                Log.e("evaluatesqlURL", finalURL);
            }
        }

        return finalURL;// call api with the final URL
    }

    public String fn_dlist(String formid, String fieldid, String matchingField,
                          String matchingFieldIds,String dlistid, String dlistformid, boolean isVlist){

        List<DList> dlist = null;
        List<Field> fieldList = null;
        if(isVlist){
            dlist = VlistDlistFormActivity.dlistFieldValues;
            fieldList = VlistFormActivity.vFieldsList;
        }else {
            dlist = DListFormActivity.dlistFieldValues;
            fieldList = FormFragment.fieldsList;
        }

        String url = "";

        EncodeURIEngine encodeURIEngine = new EncodeURIEngine();
        matchingField= encodeURIEngine.encodeURIComponent(matchingField);

        String fieldId = "field"+fieldid+dlistid;
        String fieldValue = "";
        for(int i =0; i < dlist.size(); i++){
            DList obj = dlist.get(i);
            if(obj.getId().equals(fieldId)){
                if(obj.getFieldType().toLowerCase().equals("checkbox")){
                    fieldValue = obj.getValue();
                }else{
                    fieldValue = encodeURIEngine.encodeURIComponent(obj.getValue());
                }
                break;
            }
        }

        if (!fieldValue.equals(""))
        {
            String[] arr = matchingFieldIds.split("/");
            String value="";
            String matchingFieldValues="";

            for(int i=0;i<arr.length; i++) {
                if ("".equals(arr[i])) continue;
                boolean isDListFieldFound = false;
                for (int j = 0; j < dlist.size(); j++) {
                    DList jObj = dlist.get(j);
                    if (jObj.getId().equals("field" + arr[i] + dlistid)) {
                        value = jObj.getValue();
                        matchingFieldValues = matchingFieldValues + value + "@@";
                        isDListFieldFound = true;
                        break;
                    }
                }//end of j for loop

                //check in parent
                if (!isDListFieldFound) {
                    for (int k = 0; k < fieldList.size(); k++) {
                        Field kObj = fieldList.get(k);
                        if (kObj.getId().equals("field" + arr[i])) {
                            value = kObj.getValue();
                            matchingFieldValues = matchingFieldValues + value + "@@";
                            break;
                        }
                    }//end of k for loop
                }
            }//end of i forloop

            matchingFieldValues = encodeURIEngine.encodeURIComponent(matchingFieldValues);
            matchingFieldValues = matchingFieldValues.replace("\\+","%2B");
            matchingFieldValues = matchingFieldValues.replace("\\&","%26");
            url =  retrieveString3(mPrefManager.getClientServerUrl()+"SaveFormField.do?actn=fetchDlistField&dlistformid="+dlistformid+"&&dlistid="+dlistid+"&formid="+formid+"&fieldid="+fieldid+"&fieldValue="+fieldValue+"&matchingField="+matchingField+"&matchingFieldValues="+matchingFieldValues+"&ask=COMMAND_NAME_1&cloudcode="+mPrefManager.getCloudCode()+"&token="+mPrefManager.getAuthToken()+"&type=json","reportForm1","fetchstr");
            Log.e("DlistFunctionHelper","fndlist URL = "+url);
        }
        return url;
    }

    public static String retrieveString3(String surl, String reportForm1, String s1) {
        Double randomnumber = Math.floor(Math.random() * 1111);
        surl = surl + "&random=" + randomnumber;//+getFormAsString(nameOfFormToPost);
        return surl;
    }

    public void totaldlist(String id,String fieldid,String dlistid,String doround,boolean isVlist){

        List<Field> fieldsList = null;
        if(isVlist){
            fieldsList = VlistFormActivity.vFieldsList;
        }else {
            fieldsList = FormFragment.fieldsList;
        }


        double sum =0;
        double value=0;
        String contentrowscount = "";
        dlistid = "field"+dlistid;


        int dlistFieldValuesCount = dbManager.getDlistRowsCount(dlistid);
        for(int j = 1; j<=dlistFieldValuesCount; j++){
            contentrowscount= contentrowscount+j+",";
        }

        String[] contentarr= contentrowscount.split(",");

        for(int i=0;i<contentarr.length;i++){
            String vreplace = "";
            String dlistRowsJson =  dbManager.fetchFormJsonBySrNo(dlistid,Integer.parseInt(contentarr[i]));
            try{
                JSONObject jsonObject = new JSONObject(dlistRowsJson);
                JSONArray jsonArray = jsonObject.getJSONArray("dlistArray");
                for(int x =0; x<jsonArray.length(); x++){
                    JSONObject jObj = jsonArray.getJSONObject(x);
                    if(jObj.getString("mId").equals("field"+fieldid+"_"+contentarr[i])){
                        vreplace = jObj.getString("mValue");
                        break;
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            int vlength = vreplace.length();
            if(vlength > 7 && vreplace.lastIndexOf(",")==(vlength-7) && vreplace.lastIndexOf(".")==(vlength-3)) vreplace = vreplace.replace(",", "");
            else if(vlength > 8 && vreplace.lastIndexOf(",")==(vlength-8) && vreplace.lastIndexOf(".")==(vlength-4)) vreplace = vreplace.replace(",", "");

            try{
                if (!vreplace.equals(""))
                {
                    value= (Double.parseDouble(vreplace)*1);
                    sum = sum + value;
                }else{
                    return;
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
            }

        }//end of for contentarr

        try{
            for(int i=0; i< fieldsList.size(); i++){
                Field field = fieldsList.get(i);
                if(field.getId().matches("field"+id)){
                    if(!field.getValue().equals(sum)){
                        if(!doround.isEmpty() && doround.equals("0.00")){
                            //  Todo
//                            document.getElementById('field'+id).value = Math.round(Math.round(sum*100)/100)+".00";//Math.round((sum))+".00";
//                            formatDouble(id);
                            sum = Math.round(Math.round(sum*100)/100);
                            field.setValue(String.valueOf(sum));
                            Log.e("totaldlist line# 643", String.valueOf(sum));
                            //formatDouble(id);
                        }else if(!doround.isEmpty()&&doround.equals("0.000")){

                            double s = Math.round((sum)*1000);
                            sum = s/1000;
                            field.setValue(String.valueOf(sum));
                            Log.e("totaldlist line# 648", String.valueOf(sum));

                        }else if(!doround.isEmpty()&& doround.equals("0.0000")){
                            double s = Math.round((sum)*10000000000L);
                            sum = s/10000000000L;
                            field.setValue(String.valueOf(sum));
                            Log.e("totaldlist line# 653", String.valueOf(sum));
                        } else{
                            double s = Math.round((sum)*100);
                            sum = s/100;
                            field.setValue(String.valueOf(sum));
                            Log.e("totaldlist line# 657 ", String.valueOf(sum));
                            //   formatdouble(id);
                        }
                        if(!fieldPositions.contains(String.valueOf(i))){
                            fieldPositions = fieldPositions+","+i;
                        }

                      //  FormFragment.adapter.notifyAdapterWithPayLoad(i,Constant.PAYLOAD_TEXT_SQL);
                        Log.e("totaldlist final sum","field"+id +" "+  sum);
                        break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(isVlist){
            mPrefManager.setVFieldPositionsToNotify(fieldPositions);
        }else{
            mPrefManager.setFieldPositionsToNotify(fieldPositions);
        }

    }

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

    public List<Field> getAdditionalFieldDataList() {
        return additionalFieldDataList;
    }

    public void setAdditionalFieldDataList(List<Field> additionalFieldDataList) {
        this.additionalFieldDataList = additionalFieldDataList;
    }

    public List<DList> getDlistFieldValues() {
        return dlistFieldValues;
    }

    public void setDlistFieldValues(List<DList> dlistFieldValues) {
        this.dlistFieldValues = dlistFieldValues;
    }
}


