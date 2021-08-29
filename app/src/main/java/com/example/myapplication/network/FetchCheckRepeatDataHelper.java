package com.example.myapplication.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.controller.ResponseInterface;
import com.example.myapplication.user_interface.forms.model.Field;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class FetchCheckRepeatDataHelper {

    private final String DEBUG_TAG = FetchCheckRepeatDataHelper.class.getSimpleName();
    private Context context;
    private ProgressDialog mWaiting;
    private ResponseInterface listener;
    private List<Field> fieldsList;
    private List<Field> additionalFieldDataList;
    private int dlistArrayPosition;

    public  FetchCheckRepeatDataHelper(Context context,ResponseInterface listener) {
        this.context = context;
        this.listener = listener;
    }


    public void callFetchCheckRepeatData(String url) {
        mWaiting = ProgressDialog.show(context, "", "Fetching details ...", false);
        Log.i(DEBUG_TAG,"callFetchCheckRepeatData URl === "+ url);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            getResponse(response);
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
        queue.add(request);
    }

    private void getResponse(String response) {
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject fieldValueJsonObj = jsonObject.getJSONObject("fetchFieldValues");
            String formId        = fieldValueJsonObj.getString("formid");
            String state         = fieldValueJsonObj.getString("state");
            String mRecordId     = fieldValueJsonObj.getString("id");
            String dlistFieldIds = fieldValueJsonObj.getString("dlistfieldid");

            JSONArray jsonArray  = fieldValueJsonObj.getJSONArray("nameValues");
            setFieldValues(jsonArray,state);

            if(context != null){
                //When we are fetching a record, onChange shouldn't be called when the form loads
                //  FormFragment.adapter.setFlag(false);
               // FormFragment.adapter.notifyDataSetChanged();
            }

            if(listener != null){
                listener.onSuccessResponse(mRecordId,true);
            }
        }catch (Exception e) {

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

                    if (fieldsList != null) {
                        for (int j = 0; j < fieldsList.size(); j++) {
                            Field fieldObj = fieldsList.get(j);

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
                                }
                            }
                             FormFragment.adapter.notifyAdapter(j);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            dismissDialog();
        }
    }


    private void dismissDialog() {
        if (mWaiting != null) {
            mWaiting.dismiss();
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
}
