package com.example.myapplication.user_interface.dlist.controller;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.helper.FieldHelper;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DListFieldHelper {

    private static final String DEBUG_TAG = FieldHelper.class.getSimpleName();
    public SharedPrefManager mPrefManager;
    private static  DatabaseManager dbManager;
    private static Context context;

    public DListFieldHelper(Context context) {
        DListFieldHelper.context = context;
        mPrefManager = new SharedPrefManager(context.
                getApplicationContext());

    }

    private static void initDatabase() {
        dbManager = new DatabaseManager(DListFieldHelper.context.getApplicationContext());
        dbManager.open();
    }

    /**
     * Method to update the no of rows a particular dlist has.
     * contentrows is needed for checkSave() function and for validating dlist
     * @param fieldId id of the field which has a dlist
     * @param rows    no of rows in the dlist
     */
    public void updateContentRows(String fieldId, int rows) {
    //    for (int i = 0; i < FormFragment.fieldsList.size(); i++) {
        if(fieldId.contains("field")){
            fieldId = fieldId.split("field")[1];
        }

        if(FormFragment.dlistArrayPosition != - 1) {
            Field mObj = FormFragment.fieldsList.get(FormFragment.dlistArrayPosition);
            Log.e(DEBUG_TAG, "updateContentRows = " + fieldId);
            if (!mObj.getdListArray().isEmpty()) {

                List<Field> dlistArray = mObj.getdListArray();
                for (int j = 0; j < dlistArray.size(); j++) { // will loop through the dlistbuttonArray

                    Field dlist = dlistArray.get(j); // sindle dlist Item
                    if(dlist.getId().equals("field"+fieldId)){

                        List<DList> dlistFields = dlist.getdListsFields();

                        //loop through dlist title - zeroth row
                        for (int k = 0; k < dlistFields.size(); k++) {
                            DList dobj = dlistFields.get(k);

                            if (dobj.getId().equals("contentrows" + fieldId)) {
                                StringBuilder value = new StringBuilder(",");
                                for (int x = 1; x <= rows; x++) {
                                    value.append(x).append(",");
                                }
                                Log.e(DEBUG_TAG, "updateContentRows   value = " + String.valueOf(value));
                                dobj.setValue(String.valueOf(value));

                                break;
                            }
                        }
                    }
                }
            }
        }

       // }
    }


    public static String getContentRows(String fieldId){

        String[] arr = fieldId.split("field");

        String contentRows = "";
        try{
            int position = FormFragment.dlistArrayPosition;
            Field mObj = FormFragment.fieldsList.get(position);
            if (!mObj.getdListArray().isEmpty()) {
                List<Field> dlistArray = mObj.getdListArray();

                for (int j = 0; j < dlistArray.size(); j++) { // will loop through the dlistbuttonArray
                    Field dlist = dlistArray.get(j); // sindle dlist Item
                    List<DList> dlistFields = dlist.getdListsFields();

                    //loop through dlist title - zeroth row
                    for (int k = 0; k < dlistFields.size(); k++) {
                        DList dobj = dlistFields.get(k);

                        if (dobj.getId().equals("contentrows" + arr[1])) {
                            Log.e(DEBUG_TAG, "getContentRows value = " + dobj.getValue());
                            contentRows =  dobj.getValue();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return contentRows;
    }

    /**
     * Method to get the value of a certain dlist field
     * @param fieldId  is the fieldId of the field which has dlistArray,
     * @param dlistFieldId is the id of the field inside the dlistarray for eg. field12345_0
     * @param extension is the position of the dlistfield object inside the list. it always start with 1
     * @return the value of dlistfield
     */
    public static String getDlistFieldValue(String fieldId, String dlistFieldId,String extension,boolean isVlist){
        initDatabase();
        Gson gson = new Gson();
        String value = "";

        int dlistFieldPosition = -1;
        List<Field> fieldsList = null;
        if(isVlist) {
            dlistFieldPosition = VlistFormActivity.vDlistArrayPosition;
            fieldsList = VlistFormActivity.vFieldsList;
        }else{
            dlistFieldPosition = FormFragment.dlistArrayPosition;
            fieldsList = FormFragment.fieldsList;
        }

        first:
        // will loop through the dlistbuttonArray
        for (int m = 0; m < fieldsList.get(dlistFieldPosition).getdListArray().size(); m++) {
            Field mObj = fieldsList.get(dlistFieldPosition).getdListArray().get(m);
            if(mObj.getId().equals("field"+fieldId)){

                // sindle dlist Item
                String dlistSingleRowJson = dbManager.fetchFormJsonBySrNo("field"+fieldId,Integer.parseInt(extension));
                try{
                    JSONObject jsonObject = new JSONObject(dlistSingleRowJson);
                    JSONArray jsonArray = jsonObject.getJSONArray("dlistArray");
                    for(int x =0; x<jsonArray.length(); x++){
                        JSONObject jObj = jsonArray.getJSONObject(x);
                        if (jObj.getString("mId").equals("field" + dlistFieldId + "_" + extension)) {
                            value = jObj.getString("mValue");
                            Log.e(DEBUG_TAG, "getDlistFieldValue  " + "dlist field = " +jObj.getString("mId") + " value = " + value);
                            break first;
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        return  value;
    }
}
