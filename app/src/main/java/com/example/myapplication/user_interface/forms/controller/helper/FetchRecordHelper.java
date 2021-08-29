package com.example.myapplication.user_interface.forms.controller.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.controller.DListFieldHelper;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.function.FunctionHelper;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.CommunicatorInterface;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.controller.ResponseInterface;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FetchRecordHelper {

    private final String DEBUG_TAG = FetchRecordHelper.class.getSimpleName();
    private Context context;
    private String formId;
    private String recordId;
    private String uniqueFieldId;
    private ProgressDialog mWaiting;
    private ResponseInterface listener;
    private LinearLayout rootLayout;
    private boolean isInputInTagField = false;
    private CommunicatorInterface communicatorInterface;
    private DatabaseManager dbManager;
    private boolean isVlist;
    private static List<Field> additionalFieldDataList = null;
    private static List<Field> fieldsList = null;
    private static int dlistArrayPosition = -1;
    private FormRecylerAdapter adapter;

    public FetchRecordHelper(Context context,ResponseInterface listener) {
        this.context = context;
        this.listener = listener;
        dbManager = new DatabaseManager(context);
        dbManager.open();
    }

    public void setCommunicatorInterface(CommunicatorInterface mCommunicatorInterface){
        this.communicatorInterface = mCommunicatorInterface;
    }

    public void callGetFieldViewValuesAPI(final FunctionHelper functionHelper,
                                          final String state) {

        SharedPrefManager prefManager = new SharedPrefManager(context);

        String url = String.format(Constant.URL_FIELD_VIEW_VALUES,
                prefManager.getClientServerUrl(),
                formId,
                recordId,
                prefManager.getCloudCode(),
                prefManager.getAuthToken());

        Log.i(DEBUG_TAG,"callGetFieldViewValuesAPI === "+ url);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           //set main form field values
                            //pass
                        //    Log.i(DEBUG_TAG,"callGetFieldViewValuesAPI RESPONSE=== "+ response);

                            JSONObject jsonData = new JSONObject(response);
                            if(jsonData != null){
                                for (int i = 0; i < jsonData.names().length(); i++) {

                                    for (int j = 0; j < getFieldsList().size(); j++) {
                                        Field fieldObj =getFieldsList().get(j);

                                        if (fieldObj.getId().equals(jsonData.names().getString(i))) {
                                            String value = (String) jsonData.get(jsonData.names().getString(i));
                                            fieldObj.setValue(value);
                                            break;
                                        }
                                    }
                                }
                            }
                            //Added this as few fields should be hidden or shown as per its state.
                            functionHelper.onLoadChangeIdsInMainForm(state);
                            notifyUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //dismissDialog();

                            //Added this as few fields should be hidden or shown as per its state.
                            functionHelper.onLoadChangeIdsInMainForm(state);
                            notifyUI();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // dismissDialog();
                //Added this as few fields should be hidden or shown as per its state.
                functionHelper.onLoadChangeIdsInMainForm(state);
                notifyUI();
            }
        });
        queue.add(request);
    }

    private void notifyUI() {
        if(context != null){
            if(getAdapter() != null){
                //When we are fetching a record, onChange shouldn't be called when the form loads
                getAdapter().setFlag(false);
                getAdapter().notifyDataSetChanged();
            }

            if(listener != null) {
                if(isInputInTagField){
                    listener.onSuccessResponse(getRecordId(),isInputInTagField());
                }
            }
        }
    }

    public void callFetchRecordAPI() {
        SharedPrefManager prefManager = new SharedPrefManager(context);
        mWaiting = ProgressDialog.show(context, "", "Fetching record...", false);
       String url = "";
       if(isInputInTagField()){
           String uniqueValue = getRecordId();
           url = String.format(Constant.URL_FETCH_USING_TAG,
                   prefManager.getClientServerUrl(),
                   getUniqueFieldId(),
                   formId,
                   uniqueValue,
                   prefManager.getCloudCode(),
                   prefManager.getAuthToken());
       }else{
            url = String.format(Constant.URL_FETCH_RECORD,
                    prefManager.getClientServerUrl(),
                    formId,
                    recordId,
                    prefManager.getCloudCode(),
                    prefManager.getAuthToken());
       }

       Log.e("Fetch Record URL ", url);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i(DEBUG_TAG,"Fetch Record  RESPONSE ---> "+ response);

                            if(response.contains("NO_ACCESS")){
                                dismissDialog();
                                if(communicatorInterface != null){
                                    if(!isInputInTagField){
                                        communicatorInterface.respond();
                                    }
                                }
                            }else{
                                setValue(response);
                            }

                            if (isVlist()) {
                                if(communicatorInterface != null){
                                    if(!isInputInTagField){
                                        communicatorInterface.respond();
                                    }
                                }
                            }


                            dismissDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                            dismissDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    /**
     * Sets the values in the main form
     * @param response is the api response
     */
    private void setValue(String response) {
        try{
            JSONObject jsonData             = new JSONObject(response);
            JSONObject fieldValueJsonObj    = jsonData.getJSONObject("fetchFieldValues");
            String formId                   = fieldValueJsonObj.getString("formid");
            String state                    = fieldValueJsonObj.getString("state");
            String stateName                = fieldValueJsonObj.getString("stateName");
            String mRecordId                = fieldValueJsonObj.getString("id");

            String dlistFieldIds            = fieldValueJsonObj.getString("dlistfieldid");
            String dlistNoOfRows            = fieldValueJsonObj.getString("dlistnoofrows");
            JSONArray namesValuesArray      = fieldValueJsonObj.getJSONArray("nameValues");
            JSONArray dlistFieldValuesArray = fieldValueJsonObj.getJSONArray("dlistfieldvalues");

            setIdSelectedInAdditionalData(mRecordId);

            setRecordId(mRecordId);

            FunctionHelper functionHelper = new FunctionHelper(context);
            functionHelper.setFieldsList(getFieldsList());
            functionHelper.setAdditionalFieldDataList(getAdditionalFieldDataList());
            functionHelper.setDlistArrayPosition(getDlistArrayPosition());
            //keep this before we set values as we are duplicating the dlistfield from a single object.
            functionHelper.onLoadChangeIds(state);

            //set main form field values
            setFieldValues(namesValuesArray,state);

            //set dlistfield values
            setDlistFieldValues(dlistFieldValuesArray,dlistFieldIds,dlistNoOfRows,isVlist);

            callGetFieldViewValuesAPI(functionHelper,state);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sets the values in fields in the main Form
     * @param jsonArray is the response from server
     */
    private void setFieldValues(JSONArray jsonArray,String state){
        try{

            for(int x =0 ;x<jsonArray.length();x++){

                JSONObject jsonData = jsonArray.getJSONObject(x);

                for (int i = 0; i < jsonData.names().length(); i++) {
                    Log.v(DEBUG_TAG, "key = " + jsonData.names().getString(i) + " value = " + jsonData.get(jsonData.names().getString(i)));

                    if (getFieldsList() != null) {
                        for (int j = 0; j <getFieldsList().size(); j++) {
                        Field fieldObj = getFieldsList().get(j);

                        if (fieldObj.getId().equals(jsonData.names().getString(i))) {

                            String value = (String) jsonData.get(jsonData.names().getString(i));

                            if (value.contains("< select >")) {
                                value = value.replaceAll("select|\\<|\\:|\\#|\\>", "").trim();
                                int stringlength = value.length();
                                int d = stringlength / 2;
                                String firstString = value.substring(0, d);
                                String secondString = value.substring(d, stringlength);
                                Log.e(DEBUG_TAG, "firstString =" + firstString);
                                Log.e(DEBUG_TAG, "secongString = " + secondString);
                                if (firstString.equals(secondString)) {
                                    value = firstString;
                                }
                            }
                            Log.e(DEBUG_TAG, "FIELD === " + jsonData.names().getString(i) + "\n Value === " + value);
                            fieldObj.setValue(value);
                        }

                        if (!state.equals("0")) {
                            if (fieldObj.getId().equals("statefield")) {
                                fieldObj.setValue(state);
                                Log.e("FETCH RECORD","statefield state = " + state);
                            }
                        }
                        // FormFragment.adapter.notifyAdapter(j);
                    }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setIdSelectedInAdditionalData(String recordId){
        if(getAdditionalFieldDataList() != null){
            for (int j = 0; j < getAdditionalFieldDataList().size(); j++) {
                Field fobj = getAdditionalFieldDataList().get(j);
                if(fobj.getName().toLowerCase().equals("idselected")){
                    fobj.setValue(recordId);
                    break;
                }
            }
        }
    }

    /**
     * sets the values in the dlist fields
     * @param jsonArray
     * @param dlistFieldIds
     * @param dlistNoOfRows
     */
    private void setDlistFieldValues(JSONArray jsonArray, String dlistFieldIds, String dlistNoOfRows,boolean isVlist){
        try{

            if(getFieldsList()!= null) {

                String[] arrDlistFieldIds = dlistFieldIds.split("@@");
                String[] arrDlistNoOfRows = dlistNoOfRows.split("@@");

                //looping various dlist field ids
                for (int i = 0; i < arrDlistFieldIds.length; i++) {
                    for (int k = 0; k < jsonArray.length(); k++) {
                        String fieldId = "field" + arrDlistFieldIds[i];

                        JSONObject valuesObj = jsonArray.getJSONObject(k);
                        //  List<DListItem> dlistItemFieldValues = new ArrayList<>();

                        int rows = 0;
                        try {
                            rows = Integer.parseInt(arrDlistNoOfRows[i]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            rows = 0;
                        }

                        if (rows != 0) {
                            if (dbManager != null) {
                                dbManager.deleteDListWithFieldId(fieldId);
                            }


                            Gson gson = new Gson();
                            for (int j = 1; j <= rows; j++) {

                                List<DList> dlistValuesArray = getDListFieldObjectArray(fieldId, isVlist);
                                List<DList> dlistField = new ArrayList<>();

                                if (dlistValuesArray != null) {
                                    for (int m = 0; m < dlistValuesArray.size(); m++) {
                                        DList dlist = dlistValuesArray.get(m);
                                        DList object = new DList();
                                        object.setDropDownClick(dlist.getDropDownClick());
                                        object.setSearchRequired(dlist.getSearchRequired());
                                        object.setSaveRequired(dlist.getSaveRequired());
                                        object.setReadOnly(dlist.getReadOnly());
                                        object.setSrNo(dlist.getSrNo());
                                        object.setName(dlist.getName());
                                        object.setAddFunction(dlist.getAddFunction());
                                        object.setOptionsArrayList(dlist.getOptionsArrayList());
                                        object.setOnKeyDown(dlist.getOnKeyDown());
                                        object.setType(dlist.getType());
                                        object.setValue(dlist.getValue());
                                        object.setFieldType(dlist.getFieldType());
                                        object.setFieldName(dlist.getFieldName());
                                        object.setId(dlist.getId());

                                        dlistField.add(object);
                                    }
                                }
                                Iterator<String> iterator = valuesObj.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    String index = "_" + j;

                                    if (key.contains(index)) {
                                        Log.i("TAG", "key:" + key + "--Value::" + valuesObj.optString(key));
                                        if (dlistField != null) {
                                            for (int m = 0; m < dlistField.size(); m++) {
                                                DList dlist = dlistField.get(m);
                                                String[] arr = key.split("_");
                                                String[] jarr = dlist.getId().split("_");

                                                if (arr[0].equals(jarr[0])) {
                                                    dlist.setId(arr[0] + index);

                                                    if (dlist.getFieldName().toLowerCase().matches("sr no|sr")) {
                                                        dlist.setName(key);
                                                        dlist.setValue(String.valueOf(j));
                                                    } else if (dlist.getFieldName().toLowerCase().matches("fieldid([0-9]{5,})_[0-9]")) {
                                                        String[] fieldarr = dlist.getFieldName().split("_");
                                                        String fieldid = fieldarr[0] + index;
                                                        dlist.setName(fieldid);
                                                        dlist.setId(fieldid);
                                                        dlist.setName(fieldid);
                                                        dlist.setValue(valuesObj.optString(key));

                                                    } else {
                                                        dlist.setName(key);
                                                        dlist.setValue(valuesObj.optString(key));

                                                    }
                                                } else {
                                                    //adding this check to maintain sr no we don't get sr no from api
                                                    if (dlist.getFieldName().toLowerCase().matches("sr no|sr")) {
                                                        dlist.setId(jarr[0] + index);
                                                        dlist.setName(key);
                                                        dlist.setValue(String.valueOf(j));

                                                    } else {
                                                        //set the index of the fields whose values are not fetched from the api
                                                        //for eg ID -
                                                        dlist.setId(jarr[0] + index);
                                                    }

                                                }  //end of if arr[0]
                                            }//end of for loop m
                                        }//end of dlistField != null
                                    }//end of key contains
                                }//end of while

                                if (j != 0) {
                                    DListItem dListItem = new DListItem(dlistField);
                                    String dlistJson = gson.toJson(dListItem);
                                    Log.e("call api", "Converting dlist to json ----->" + dlistJson);
                                    dbManager.insertDListRow(Integer.parseInt(getFormId()), "", fieldId, "", dlistJson, j);
                                }
                            }//end of rows for loop

                        }// end of rows != 0

                        if (rows != 0) {
                            DListFieldHelper fieldHelper = new DListFieldHelper(context);
                            fieldHelper.updateContentRows(fieldId, rows);
                        }

                    }// end of for loop k

                }//end of arrDlistFieldIds
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  List<DList> getDListFieldObjectArray(String id,boolean isVlist) {
        List<DList> vArray = null; // dlist values

        try {
//            List<Field> fieldsList = null;
//
//            if(isVlist) {
//                fieldsList =  VlistFormActivity.vFieldsList;
//            }else {
//                fieldsList =  FormFragment.fieldsList;
//            }

//
//            for (int h = 0; h < getFieldsList().size(); h++) {
                Field hobj = getFieldsList().get(getDlistArrayPosition());
                //find the buttons dlistArray
                if (!hobj.getdListArray().isEmpty()) {
                    List<Field> dlistArray = hobj.getdListArray();
                    for (int x = 0; x < dlistArray.size(); x++) {
                        Field xobj = dlistArray.get(x);

                        //find a particular dlist with the help the id
                        if (id.equals(xobj.getId())) {
                            //   Log.e("DLIST_FIELD_ID", "FOUND ID---" + xobj.getId() + "---");
                            //   Log.e("FOUND DLIST FIELD", "FOR TITLE SIZE =  " + xobj.getdListsFields().size());
                            //find the dlistfields header array
                            // assigning dListFieldArray to dListValuesArray so that values array and field array are consistent
//                          //when we get dlistfieldvalues from api we don't get it as a field object so have to keep values array and field array consistent

                            List<DList> list = xobj.getdListsFields();
                            vArray = new ArrayList<>();

                            for (int y = 0; y < list.size(); y++) {
                                DList ydlist = list.get(y);
                                //    Log.e("yDLIST OBJECT", ydlist.toString());
                                vArray.add(ydlist); // doing this so that the changes don't reflect in the original arraylist
                            }
                            //  Log.e("AFTER COPYING", "dlistValuesArray size =" + vArray.size());
                        }
                    }
                }
      //      }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vArray;
    }

    private void dismissDialog(){
        if (mWaiting != null) {
            mWaiting.dismiss();
        }
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getUniqueFieldId() {
        return uniqueFieldId.replace("field","");
    }

    public void setUniqueFieldId(String uniqueFieldId) {
        this.uniqueFieldId = uniqueFieldId;
    }

    public LinearLayout getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(LinearLayout rootLayout) {
        this.rootLayout = rootLayout;
    }

    public boolean isInputInTagField() {
        return isInputInTagField;
    }

    public void setInputInTagField(boolean fieldTypeTag) {
        isInputInTagField = fieldTypeTag;
    }

    public boolean isVlist() {
        return isVlist;
    }

    public void setVlist(boolean vlist) {
        isVlist = vlist;
    }

    public List<Field> getAdditionalFieldDataList() {
        return additionalFieldDataList;
    }

    public void setAdditionalFieldDataList(List<Field> additionalFieldDataList) {
        this.additionalFieldDataList = additionalFieldDataList;
    }

    private static  List<Field> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<Field> fieldsList) {
        this.fieldsList = fieldsList;
    }

    private static  int getDlistArrayPosition() {
        return dlistArrayPosition;
    }

    public void setDlistArrayPosition(int dlistArrayPosition) {
        this.dlistArrayPosition = dlistArrayPosition;
    }

    public FormRecylerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FormRecylerAdapter adapter) {
        this.adapter = adapter;
    }
}
