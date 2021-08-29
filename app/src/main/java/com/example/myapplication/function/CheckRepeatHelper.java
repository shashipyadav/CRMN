package com.example.myapplication.function;

import android.content.Context;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.model.Field;

import java.util.List;

public class CheckRepeatHelper {

    Context context;
    String formId;
    SharedPrefManager mPrefManager;
    private List<Field> fieldsList;
    private List<Field> additionalFieldDataList;

    public CheckRepeatHelper(Context context,String formId ){
        this.context = context;
        this.formId = formId;
        mPrefManager = new SharedPrefManager(context);
    }

    public String splitCheckRepeats(String funcString){
        String[] arr = funcString.split("checkrepeats");
        String[] checkRep = arr[1].split(",");

        final String formid = checkRep[0].replaceAll("['\\(\\)]", "");
        final String divid = checkRep[1].replaceAll("\'", "");
        final String fieldidlist = checkRep[2].replaceAll("['\\(\\)]", "");
        String fieldtype = checkRep[3].replaceAll("['\\(\\);]", "");

        String url = checkRepeats(formid, divid, fieldidlist,
                fieldtype,formId);

        return url;
    }

    private String checkRepeats(String formid,
                               String divid,
                               String fieldidlist,
                               String fieldtype,
                               String showformid) {

        String value = "";
        String[] arr= fieldidlist.split("/");

        String idlist= "";
        for(int i = 0; i< getAdditionalFieldDataList().size(); i++){
            Field fobj = getAdditionalFieldDataList().get(i);
            if(fobj.getName().toLowerCase().equals("mandatory")){
                idlist = fobj.getValue();
                break;
            }
        }

        String fvalue="";

        for(int i=0; i<arr.length; i++) {

            for (int j = 0; j < getFieldsList().size(); j++) {
                Field fObj = getFieldsList().get(j);

                if (fObj.getId().equals("field" + arr[i])) {

                    if (!fObj.getValue().equals("") || idlist.indexOf(arr[i] + '/') < 0) {
                        fvalue = fObj.getValue();
                        if (fvalue.indexOf("-->") > -1 && fvalue.indexOf("-->") == (fvalue.length() - 3))
                            fvalue = fvalue.substring(0, fvalue.length() - 3);

                        fObj.setValue(fvalue);
                        String datePat = "^(\\d{4})(/|-)(\\d{1,2})(/|-)(\\d{1,2})$";
                        if (formid.equals("547")) {
                            if (fvalue.indexOf("/") > -1 && fvalue.split("/").length >= 3) {
                                String[] st = fvalue.split("/");
                                String st1 = st[2] + "-" + st[1] + "-" + st[0];
                                fvalue = st1;
                            }
                        } else if (fvalue.matches(datePat)) {
                            if (fvalue.indexOf("-") > -1 && !(fvalue.indexOf("/") > -1)) {
                                String[] st = fvalue.split("-");
                                String st1 = st[2] + "/" + st[1] + "/" + st[0];
                                fvalue = st1;
                            }
                        }

                        value = value + fvalue+"@@";
                    } else {
                        return "";
                    }

                    break;
                }
            }
        }

        if(value.equals("@@")) return "";


        EncodeURIEngine uriEngine = new EncodeURIEngine();
        value = uriEngine.encodeURIComponent(value);
        value = value.replaceAll("\\+","%2B");
        value = value.replaceAll("&","%26");


        Double randomnumber = Math.floor(Math.random() * 1111);

        String url = mPrefManager.getClientServerUrl() +
                "SaveFormField.do?&actn=getRepeats&divid=" + divid +
                "&fieldtype="+fieldtype +
                "&fieldidlist="+fieldidlist +
                "&idvalues="+value +
                "&formid=" + formid +
                "&showformid=" + showformid+
                "&ask=COMMAND_NAME_1&cloudcode=" + mPrefManager.getCloudCode() +
                "&token=" + mPrefManager.getAuthToken() +
                "&random="+randomnumber +
                "&crossDomain=true&AjaxRequestUniqueId=161598970048120&type=json";

        return url;
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
}
