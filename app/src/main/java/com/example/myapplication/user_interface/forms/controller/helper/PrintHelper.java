package com.example.myapplication.user_interface.forms.controller.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.home.controller.PdfViewActivity;
import com.example.myapplication.Constant;
import com.example.myapplication.util.VolleyErrorUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrintHelper {

    private static final String DEBUG_TAG = PrintHelper.class.getSimpleName();
    private Activity activity;
    private String title;
    private String formId;
    private String recordId;
    private String mandatoryFields;
    private SharedPrefManager prefManager;


    public PrintHelper(AppCompatActivity activity,String formId, String recordId){
        this.activity = activity;
        this.formId = formId;
        this.recordId =recordId;
        prefManager = new SharedPrefManager(this.activity);
    }

    public String print() {
        String mandatory = getMandatory();
        String[] arr = mandatory.split("/");
        List<Field> dlistArray = new ArrayList<>();
        String fieldValuesList = "";

        for (int j = 0; j < arr.length; j++) {
            String fieldId = "field" + arr[j];
            if (!fieldId.contains("_")) {
                for (int k = 0; k < FormFragment.fieldsList.size(); k++) {
                    Field kobj = FormFragment.fieldsList.get(k);
                    if (fieldId.matches(kobj.getId())) {
                        if (kobj.getdListArray().isEmpty()) {
                            //main Form

                            String value = kobj.getValue().isEmpty() ? "%40" : kobj.getValue();
                            fieldValuesList = fieldValuesList + value;
                            continue;
                        } else {
                            //dlist Found
                            //ToDo =search for save required in DList pending
                            dlistArray = kobj.getdListArray();
                            for (int m = 0; m < dlistArray.size(); m++) { // will loop through the dlistbuttonArray
                                Field dlist = dlistArray.get(m);
                                if (fieldId.matches(dlist.getId())) {

                                    String value = kobj.getValue().isEmpty() ? "%40" : kobj.getValue();
                                    fieldValuesList = fieldValuesList + value;
                                    continue;
                                }
                            }
                        }
                    }
                }
            } else {
                String[] jaar = arr[j].split("_");
                String dlistID = "field" + jaar[0];
                String dlistField = jaar[1];

                for (int k = 0; k < FormFragment.fieldsList.size(); k++) {
                    Field kFieldObj = FormFragment.fieldsList.get(k);
                    if (!kFieldObj.getdListArray().isEmpty()) {
                        dlistArray = kFieldObj.getdListArray();
                        for (int y = 0; y < dlistArray.size(); y++) {
                            Field yDlistObj = dlistArray.get(y);
                            if (dlistID.matches(yDlistObj.getId())) {
                                List<DListItem> dlistFieldValues = yDlistObj.getDListItemList();
                                for (int x = 0; x < dlistFieldValues.size(); x++) { // looping through dlist rows
                                    List<DList> dlistRow = dlistFieldValues.get(x).getDlistArray();
                                    if (dlistField.toLowerCase().contains("_dlist")) {

                                        String value = yDlistObj.getValue().isEmpty() ? "%40" : yDlistObj.getValue();
                                        fieldValuesList = fieldValuesList + value;
                                        continue;
                                    } else {
                                        for (int w = 0; w < dlistRow.size(); w++) {
                                            DList dobj = dlistRow.get(w);
                                            String[] idarr = dobj.getId().split("_");
                                            String dId = idarr[0];

                                            if (dlistField.matches(idarr[0])) {
                                                if (dobj.getValue().isEmpty()) {

                                                    String value = dobj.getValue().isEmpty() ? "%40" : dobj.getValue();
                                                    fieldValuesList = fieldValuesList + value;
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        fieldValuesList = fieldValuesList.trim();
        String url = "";
        String REPORT_NAME = "";


    /*    if (getRecordId().equals("0")) {
            REPORT_NAME = "RESULTS";
             url= prefManager.getClientServerUrl() + "sendMailReport.do?actn=PrintData&idlist=@&fieldid=0&fieldidlist="+mandatory+"&fieldvaluelist="+ fieldValuesList +"&fieldId=0&fieldValue=SQL&formid="+getFormId()+
                    "&matchingFields=&reportid="+getRecordId()+"&moduleconditions=&reportname="+REPORT_NAME+"&cloudcode=" + prefManager.getCloudCode() + "&token=" + prefManager.getAuthToken();

        } else {

            REPORT_NAME = getTitle();
            if(REPORT_NAME.toLowerCase().contains("report")){
                url = String.format(Constant.URL_PRINT_REPORT,
                        prefManager.getClientServerUrl(),
                        getRecordId(),
                        getRecordId(),
                        getFormId(),
                        getRecordId(),
                        REPORT_NAME,
                        prefManager.getCloudCode(),
                        prefManager.getAuthToken());

            }else{

                REPORT_NAME = "FORM";
                //https://7.strategicerpcloud.com/strategicerp/sendMailReport.do?actn=PrintData&idlist=&fieldid=340&fieldId=340&fieldValue=&formid=2364&matchingFields=&reportid=340&moduleconditions=&reportname=FORM&cloudcode=simpoloceramics&token=DTZ9BWT91CO3GTUQ

                url= prefManager.getClientServerUrl() + "sendMailReport.do?actn=PrintData&idlist=@&fieldid=" + getRecordId() + "&fieldidlist="+mandatory+"&fieldvaluelist="+ fieldValuesList +"&fieldId="+ getRecordId()+"&fieldValue=SQL&formid="+getFormId()+
                        "&matchingFields=&reportid="+getRecordId()+"&moduleconditions=&reportname="+REPORT_NAME+"&cloudcode=" + prefManager.getCloudCode() + "&token=" + prefManager.getAuthToken();


              //  https://7.strategicerpcloud.com/strategicerp/sendMailReport.do?actn=PrintData&id=2364&formid=2364&fieldid=0&reportid=0&idlist=@&fieldidlist=47944/44142/44143/50266/45164/50259/50261/53657_53720/53657_53721/53657_53655/53657_53656/45168/44146/&fieldvaluelist=%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40%40&fieldValue=SQL&moduleconditions=&reportname=RESULTS&cloudcode=simpoloceramics&token=DTZ9BWT91CO3GTUQ
            }

            url = prefManager.getClientServerUrl() + "sendMailReport.do?actn=PrintData&id=" + getFormId() + "&formid=" + getFormId() + "&fieldid=0&reportid=" + getRecordId() + "&idlist=@&" +
                    "fieldidlist=" + mandatory + "&fieldvaluelist=" + fieldValuesList + "&fieldValue=SQL&moduleconditions=&reportname=" + REPORT_NAME + "&cloudcode=" + prefManager.getCloudCode() + "&token=" + prefManager.getAuthToken();

        } */
        url =  prefManager.getClientServerUrl() + "sendMailReport.do?actn=PrintData&idlist=@&fieldid="+getRecordId()+"&fieldId="+getRecordId()+"&fieldValue=&formid="+getFormId()+"&matchingFields=&reportid="+getRecordId()+"&moduleconditions=&reportname="+getTitle()+"&cloudcode="+prefManager.getCloudCode()+"&token="+ prefManager.getAuthToken();

       return url;

//        if (NetworkUtil.isNetworkOnline(activity.getApplicationContext())) {
//           //openInBrowser(url);
//
//
//         //   Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
//         //   activity.startActivity(browserIntent);
//
//            callPrintRecordAPI(url);
//        } else {
//            ToastUtil.showToastMessage(activity.getResources().getString(R.string.no_internet_message), activity.getApplicationContext());
//        }
    }


    private void openInBrowser(String url ) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null);
                activity.startActivity(intent);
            }
    }

    private void callPrintRecordAPI(final String url) {
        Log.e(DEBUG_TAG, "PRINT URL = " + url);
        final ProgressDialog mWaiting = ProgressDialog.show(activity, "", "Please wait ...", false);
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "Response=" + response);
                        String fileName = "";
                        String fileUrl = "";
                        try {
//                           if (!response.isEmpty()) {
//                                String fileName = response.replace("<span id=\"retrievejavascript\">openfilelinkwindow('", "").replace("');</span>", "");
//
//                                Log.e("FINAL FILE NAME = ", fileName);
//                                fileName = prefManager.getClientServerUrl() + fileName;
//
//                                Intent intent = new Intent(activity, PdfViewActivity.class);
//                                intent.putExtra(Constant.EXTRA_TITLE, getTitle());
//                                intent.putExtra(Constant.EXTRA_URL, fileName);
//                                activity.startActivity(intent);
//                            }


                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                fileName = jsonObject.getString("filename");
                                fileUrl = jsonObject.getString("fileurl");
                            }

                            Intent intent = new Intent(activity, PdfViewActivity.class);
                            intent.putExtra(Constant.EXTRA_TITLE, getTitle());
                            intent.putExtra(Constant.EXTRA_URL, fileUrl);
                            if(fileUrl.isEmpty()){
                                intent.putExtra(Constant.EXTRA_HTML, response);
                            }
                            activity.startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
//
//                            Intent intent = new Intent(activity, PdfViewActivity.class);
//                            intent.putExtra(Constant.EXTRA_TITLE, getTitle());
//                            intent.putExtra(Constant.EXTRA_URL, "");
//                            intent.putExtra(Constant.EXTRA_HTML, response);
//                            activity.startActivity(intent);

                            if(fileUrl.isEmpty()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setPackage("com.android.chrome");
                                try {
                                    activity.startActivity(intent);
                                } catch (ActivityNotFoundException ex) {
                                    // Chrome browser presumably not installed so allow user to choose instead
                                    intent.setPackage(null);
                                    activity.startActivity(intent);
                                }
                            }
                        }
                        if (mWaiting != null) {
                            mWaiting.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorUtil.showVolleyError(activity, error);
                if (mWaiting != null) {
                    mWaiting.dismiss();
                }
            }
        });
        queue.add(request);
    }

    public String getFormId(){
        return formId;
    }

    public String getRecordId(){
        return recordId;
    }

    public void setMandatoryFields(String mandatoryFields){
        this.mandatoryFields = mandatoryFields;
    }

    private String getMandatory(){
        return mandatoryFields;
    }

    public void setTitle(String title){
        this.title = title;
    }

    private String getTitle(){
       return title;
    }


}
