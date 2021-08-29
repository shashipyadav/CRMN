package com.example.myapplication.user_interface.forms.controller.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.model.Form;
import com.example.myapplication.user_interface.forms.model.OptionModel;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.util.VolleyErrorUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseForm {

    private final String DEBUG_TAG = getClass().getSimpleName();
    private DatabaseManager dbManager;
    private int chartId;
    private String title;
    private Context context;
    private SharedPrefManager mPrefManager;
    private  ProgressDialog progressDialog;
    private List<Field> fieldList;
    private List<Field> additionalFieldDataList;

    public ParseForm(Context context) {
        this.context = context;
    }


    private void loadForm() {
        try {
            boolean formAvailable = getDbManager().checkIfFormExists(getChartId());
            if (NetworkUtil.isNetworkOnline(context)) {
                String dateTime = "";
                if (!formAvailable) {
                    dateTime = Constant.SERVER_DATE_TIME;
                } else {
                    //1991-10-03%2011:20:00
                    dateTime = DateUtil.getCurrentDate("yyyy-MM-dd hh:mm:ss");
                }

                String formURL = String.format(Constant.FORM_URL,
                        mPrefManager.getClientServerUrl(),
                        chartId, dateTime,
                        mPrefManager.getCloudCode(),
                        mPrefManager.getAuthToken());

                Log.e("FORM_URL", formURL);
                callFormFieldApi(formURL);
            } else {
                if (formAvailable) {
                    buildFormUI(String.valueOf(chartId));
                } else {
                    ToastUtil.showToastMessage(context.getResources().getString(R.string.no_internet_message), context);
                    // DialogUtil.showAlertDialog(getActivity(),
                    // "No Internet Connection!",
                    // "Please check your internet connection and try again", false, false);
                }
            }
        }catch (Exception  e){
            e.printStackTrace();
        }
    }

    public SharedPrefManager getmPrefManager() {
        return mPrefManager;
    }

    public void setmPrefManager(SharedPrefManager mPrefManager) {
        this.mPrefManager = mPrefManager;
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    public void setDbManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }



    private void callFormFieldApi(String formURL) {
        showProgressDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET,
                formURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(DEBUG_TAG, "callFormFieldAPI Response Received");

                        if (!response.isEmpty()) {
                            if (getDbManager() != null) {
                                boolean formAvailable = getDbManager().checkIfFormExists(chartId);
                                if (formAvailable) {
                                    getDbManager().updateForm(getChartId(), response);
                                } else {
                                    getDbManager().insertForm(getChartId(), title, response);
                                }
                            }
                            buildFormUI(String.valueOf(chartId));
                        } else if (response.equals("")) {
                            buildFormUI(String.valueOf(chartId));
                        }

                        dismissProgressDialog();

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyErrorUtil.showVolleyError(context, error);
                        dismissProgressDialog();
                        buildFormUI(String.valueOf(chartId));
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        requestQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }




    private void buildFormUI(String chartId) {
      /*  try {
            String response = "";
            if(getDbManager() != null){
                response = getDbManager().getFormJson(Integer.parseInt(chartId));
            }

            JSONObject jsonObject = new JSONObject(response);
            JSONObject json = jsonObject.getJSONObject("json");

            String formSaveCheck = "";
            if (json.has("formsavecheck")) {
                formSaveCheck = json.getString("formsavecheck");
            }
            String formSaveCheckNames = "";
            if (json.has("formsavechecknames")) {
                formSaveCheckNames = json.getString("formsavechecknames");
            }

            form = new Form(json.getString("enctype"),
                    json.getString("data-title"),
                    json.getString("form-method"),
                    json.getString("form-id"),
                    json.getString("id"),
                    json.getString("form-save"),
                    formSaveCheck,
                    formSaveCheckNames);

            JSONArray jsonArray = json.getJSONArray("fields");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.length() > 5) {
                    String showFieldName = "";
                    if (obj.has("showfieldname")) {
                        showFieldName = obj.getString("showfieldname");
                    }

                    String waterMark = "";
                    if (obj.has("watermark")) {
                        waterMark = obj.getString("watermark");
                    }

                    String saveRequired = "";
                    if (obj.has("save_required")) {
                        saveRequired = obj.getString("save_required");
                    }

                    String webSaveRequired = "";
                    if (obj.has("websave_required")) {
                        webSaveRequired = obj.getString("websave_required");
                    }

                    String jFunction = "";
                    if (obj.has("jfunction")) {
                        jFunction = obj.getString("jfunction");
                    }

                    String onChange = "";
                    if (obj.has("onchange")) {
                        onChange = obj.getString("onchange");
                        onChange = FieldHelper.replaceUpdatetview(onChange);
                    }

                    String onKeyDown = "";
                    if (obj.has("onkeydown")) {
                        onKeyDown = obj.getString("onkeydown");
                    }

                    String default_value = "";
                    if (obj.has("default_value")) {
                        default_value = obj.getString("default_value");
                    }

                    String sqlValue = "";
                    if (obj.has("sql_value")) {
                        sqlValue = obj.getString("sql_value");
                    }

                    String fieldName = "";
                    if (obj.has("field_name")) {
                        fieldName = obj.getString("field_name");
                    }

                    String relation = "";
                    if (obj.has("relation")) {
                        relation = obj.getString("relation");
                    }

                    String type = "";
                    if (obj.has("type")) {
                        type = obj.getString("type");
                    }

                    String fieldType = "";
                    String field_type = "";
                    if (obj.has("field_type")) {
                        fieldType = obj.getString("field_type");
                        field_type = obj.getString("field_type");
                    }

                    String states = "";
                    if (obj.has("states")) {
                        states = obj.getString("states");
                        //If states is not empty
                        //set fieldType = type
                        //set type = hidden
                        //by doing so the extra fields will be hidden initially
                        if (!states.isEmpty()) {
                            // Jan 16 2020 - commented this condition as Responsible person wasn't hidden when the form loads initially
                            //  if (!states.startsWith("s")) { // checking this because getting s2089s2598s as states sometimes
                            fieldType = type;
                            type = "hidden";
                            //  }
                        }
                    }

                    String relationField = "";
                    if (obj.has("relation_field")) {
                        relationField = obj.getString("relation_field");
                    }

                    String jIdList = "";
                    if (obj.has("jidlist")) {
                        jIdList = obj.getString("jidlist");
                    }

                    String name = "";
                    if (obj.has("name")) {
                        name = obj.getString("name");
                    }

                    String width = "";
                    if (obj.has("width")) {
                        width = obj.getString("width");
                    }

                    String options = "";
                    List<OptionModel> optionsArrayList = new ArrayList<>();
                    if (obj.has("options")) {
                        options = obj.getString("options");

                        if (!options.isEmpty() && !options.equals("")) {
                            String str[] = options.split(",");

                            if(str.length > 0){
                                for (int a = 0; a < str.length; a++) {
                                    String[] op = str[a].split(":");
                                    // Log.e("OPTIONS STRING ",  "str[a] =" + str[a].toString());

                                    String mOp0 = op[0].trim().replaceAll("[\"\\{\\}]", "");
                                    String mOp1 = op[1].trim().replaceAll("[\"\\{\\}]", "");
                                    optionsArrayList.add(new OptionModel(mOp0, mOp1));
                                    // Log.e("OPTIONS STRING ", op[0].toString() + "  op1=" + op[1].toString());

                                }
                                optionsArrayList.add(0, new OptionModel("",""));
                            }
                        }
                    }

                    if (default_value.toLowerCase().matches("sql\\$\\{self\\}|\\$username")) {
                        optionsArrayList.add(new OptionModel(mPrefManager.getUserName(), mPrefManager.getUserName()));
                    }

                    String searchRequired = "";
                    if (obj.has("search_required")) {
                        searchRequired = obj.getString("search_required");
                    }

                    String vlistRelationIds = "";
                    if(obj.has("vlistrelationids")){
                        vlistRelationIds = obj.getString("vlistrelationids");
                    }

                    String vlistDefaultIds = "";
                    if(obj.has("vlistdefaultids")){
                        vlistDefaultIds = obj.getString("vlistdefaultids");
                    }

                    String id = "";
                    if (obj.has("id")) {
                        id = obj.getString("id");
                    }

                    String placeHolder = "";
                    if (obj.has("placeholder")) {
                        placeHolder = obj.getString("placeholder");
                    }

                    String value = "";
                    if (obj.has("value")) {
                        if (default_value.matches("\\$\\{today\\}|field\\$\\{today\\}")) {
                            value = DateUtil.getCurrentDate(Constant.yyyy_MM_dd);
                        }else if(default_value.matches("\\$\\{now\\}|field\\$\\{now\\}")){
                            value = DateUtil.getCurrentDate(Constant.yyyy_MM_dd_now);
                        } else {
                            value = obj.getString("value");
                        }
                    }

                    String onClickSummary = "";
                    if (obj.has("onclicksummary")) {
                        onClickSummary = obj.getString("onclicksummary");
                    }

                    String onClickVList = "";
                    if (obj.has("onclickvlist")) {
                        onClickVList = obj.getString("onclickvlist");
                    }

                    String newRecord = "";
                    if (obj.has("newrecord")) {
                        newRecord = obj.getString("newrecord");
                    }

                    String onClickRightButton = "";
                    if (obj.has("onclickrightbutton")) {
                        onClickRightButton = obj.getString("onclickrightbutton");
                    }

                    List<Field> DlistArray = new ArrayList<>();
                    List<DList> dlistField = new ArrayList<>();
                    if (obj.has("dlistfields")) {
                        JSONArray dListArray = obj.getJSONArray("dlistfields");

                        for (int k = 0; k < dListArray.length(); k++) {
                            JSONObject kObj = dListArray.getJSONObject(k);
                            DList dlistObject = null;
                            if(k == 0){
                                //code for setting fieldid_0 eg. fieldid50412_0
                                dlistObject = new DList();
                                String fieldid = id.replace("field","fieldid");
                                fieldid += "_0";
                                dlistObject.setName(fieldid);
                                dlistObject.setFieldName(fieldid);
                                dlistObject.setSrNo(0);
                                dlistObject.setId(fieldid);
                                dlistObject.setType("hidden");
                                dlistObject.setValue("0");
                                dlistObject.setFieldName(fieldid);
                                dlistField.add(dlistObject);
                            }

                            dlistObject = new DList();
                            if (kObj.has("dropdownclick")) {
                                dlistObject.setDropDownClick(kObj.getString("dropdownclick"));
                            }

                            String dOptions = "";
                            List<OptionModel> dOptionsArrayList = new ArrayList<>();
                            if (kObj.has("options")) {
                                dOptions = kObj.getString("options");

                                if (!dOptions.isEmpty()) {
                                    Log.e("dOptions = ", dOptions);
                                    String str[] = dOptions.split(",");

                                    if(str.length > 0){
                                        for (int a = 0; a < str.length; a++) {
                                            String[] op = str[a].split(":");
                                            // Log.e("OPTIONS STRING ",  "str[a] =" + str[a].toString());

                                            String mOp0 = op[0].trim().replaceAll("[\"\\{\\}]", "");
                                            String mOp1 = op[1].trim().replaceAll("[\"\\{\\}]", "");
                                            dOptionsArrayList.add(new OptionModel(mOp0, mOp1));
                                            // Log.e("OPTIONS STRING ", op[0].toString() + "  op1=" + op[1].toString());

                                        }
                                        dOptionsArrayList.add(0, new OptionModel("",""));
                                    }
                                }
                                dlistObject.setOptionsArrayList(dOptionsArrayList);
                            }

                            if(kObj.has("defaultvalue")) {
                                dlistObject.setDefaultValue(kObj.getString("defaultvalue"));
                            }

                            if (kObj.has("readonly")) {
                                dlistObject.setReadOnly(kObj.getString("readonly"));
                            }

                            if (kObj.has("save_required")) {
                                dlistObject.setSaveRequired(kObj.getString("save_required"));
                            }

                            if (kObj.has("search_required")) {
                                dlistObject.setSearchRequired(kObj.getString("search_required"));
                            }

                            if (kObj.has("srno")) {
                                dlistObject.setSrNo(kObj.getInt("srno"));
                            }

                            if (kObj.has("name")) {
                                dlistObject.setName(kObj.getString("name"));
                            }

                            if (kObj.has("id")) {
                                dlistObject.setId(kObj.getString("id"));
                            }

                            if (kObj.has("addfunction")) {
                                dlistObject.setAddFunction(kObj.getString("addfunction"));
                            }
                            if (kObj.has("onkeydown")) {
                                dlistObject.setOnKeyDown(kObj.getString("onkeydown"));
                            }

                            if (kObj.has("type")) {
                                dlistObject.setType(kObj.getString("type"));
                            }

                            if (kObj.has("value")) {
                                dlistObject.setValue(kObj.getString("value"));
                            }

                            if (kObj.has("field_type")) {
                                dlistObject.setFieldType(kObj.getString("field_type"));
                            }

                            if (kObj.has("field_name")) {
                                dlistObject.setFieldName(kObj.getString("field_name"));
                            }
                            if (kObj.has("states")) {
                                String mStates = kObj.getString("states");
                                if(mStates.startsWith("s")){
                                    dlistObject.setType("hidden");
                                }
                                dlistObject.setStates(kObj.getString("states"));
                            }
                            dlistField.add(dlistObject);
                        }
                    }

                    if (fieldType.equalsIgnoreCase("DLIST")) {
                        if (obj.has("dlistfields")) {

                            //dLIst form Fields which will be shown when we click on the button in recyclerview
                            Field dlistF = new Field(waterMark, showFieldName, saveRequired, jFunction,
                                    onChange, onKeyDown, default_value, type, sqlValue,
                                    fieldName, relation, states, relationField,
                                    onClickVList, jIdList, name, width, searchRequired, id, placeHolder,
                                    value, fieldType, onClickSummary, options, newRecord, dlistField, optionsArrayList, "", "",field_type,"","");
                            DlistArray.add(dlistF);

                            //DList which we well show as a button
                            Field field = new Field(waterMark, showFieldName, saveRequired, jFunction,
                                    "", onKeyDown, default_value, "DLIST", sqlValue,
                                    fieldName, relation, "", relationField,
                                    onClickVList, jIdList, "", searchRequired, "", placeHolder,
                                    value, fieldType, onClickSummary, newRecord, DlistArray, optionsArrayList,
                                    onClickRightButton, webSaveRequired,field_type);


                            //add it to the form
                            int dListIndex = getDListArray();
                            if (dListIndex != -1) {
                                List<Field> dListArray = getFieldList().get(dListIndex).getdListArray();
                                dListArray.add(dlistF);
                            } else {
                                getFieldList().add(field);
                            }
                        }
                    } else {
                        Field field = new Field(waterMark, showFieldName, saveRequired, jFunction,
                                onChange, onKeyDown, default_value, type, sqlValue,
                                fieldName, relation, states, relationField,
                                onClickVList, jIdList, name, width, searchRequired, id, placeHolder,
                                value, fieldType, onClickSummary, newRecord, options, dlistField, optionsArrayList,
                                onClickRightButton, webSaveRequired,field_type,vlistRelationIds,vlistDefaultIds);

                        getFieldList().add(field);
                    }
                } else {
                    String showFieldName = "";
                    if (obj.has("showfieldname")) {
                        showFieldName = obj.getString("showfieldname");
                    }

                    String name = "";
                    if (obj.has("name")) {
                        name = obj.getString("name");
                    }

                    String id = "";
                    if (obj.has("id")) {
                        id = obj.getString("id");
                    }

                    String type = "";
                    if (obj.has("type")) {
                        type = obj.getString("type");
                    }

                    String value = "";
                    if (obj.has("value")) {
                        if (id.toLowerCase().equals("formid")) {
                            FORM_ID = obj.getString("value");
                            mPrefManager.setFormId(FORM_ID);
                        }
                        value = obj.getString("value");
                    }
                    getAdditionalFieldDataList().add(new Field(showFieldName, name, id, type, value));
                }
                dlistArrayPosition = getDListArray();
                callAdapter();
            }
            showDynamicButtons(RECORD_ID,false);
        } catch (JSONException e) {
            e.printStackTrace();
            callAdapter();
        }*/
    }

    private int getDListArray() {
        for (int i = 0; i < getFieldList().size(); i++) {
            if (!getFieldList().get(i).getdListArray().isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Field> fieldList) {
        this.fieldList = fieldList;
    }

    public List<Field> getAdditionalFieldDataList() {
        return additionalFieldDataList;
    }

    public void setAdditionalFieldDataList(List<Field> additionalFieldDataList) {
        this.additionalFieldDataList = additionalFieldDataList;
    }
}
