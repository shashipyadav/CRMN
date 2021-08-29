package com.example.myapplication.user_interface.dynamicbutton;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.CommunicatorInterface;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.controller.ResponseInterface;
import com.example.myapplication.Constant;
import com.example.myapplication.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class DynamicButtonHelper {
    private static final String DEBUG_TAG = DynamicButtonHelper.class.getSimpleName();
    private Context context;
    private SharedPrefManager prefManager;
    private String recordId;
    private String formId;
    private String module;
    private ProgressDialog mWaiting;
    private List<DynamicButton> buttonList;
    private DynamicButtonAdapter buttonAdapter;
    private RecyclerView recyclerView;
    private LinearLayout rootLayout;
    private ResponseInterface listener;
    private String buttonTitle;
    private boolean isInputInTagField = false;
    private String formTitle;
    private CommunicatorInterface communicatorInterface;
    private boolean isVlist;

    public DynamicButtonHelper(Context context,ResponseInterface listener){
        this.context = context;
        this.listener = listener;
        prefManager = new SharedPrefManager(this.context);
    }

    public void setCommunicatorInterface(CommunicatorInterface mCommunicatorInterface){
        this.communicatorInterface = mCommunicatorInterface;
    }

    public void callGetDynamicButtonsAPI() {

        if(getButtonList() != null ){
            getButtonList().clear();
        }

        String url = String.format(Constant.URL_DYNAMIC_BUTTONS,
                prefManager.getClientServerUrl(),
                getRecordId(),
                getFormId(),
                prefManager.getCloudCode(),
                prefManager.getAuthToken());

        Log.e("DYNAMIC BUTTON URL", url);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "callGetDynamicButtonsAPI Response=" + response);
                        try {
                            JSONObject jsonobj = new JSONObject(response);
                            JSONObject btnObj = jsonobj.getJSONObject("buttons");
                            JSONArray permissionBtnArray = btnObj.getJSONArray("permissionbuttons");
                            JSONArray dynBtnArray = null;
                            if (btnObj.has("dynamicbuttons")) {
                                 dynBtnArray = btnObj.getJSONArray("dynamicbuttons");

                                for (int i = 0; i < dynBtnArray.length(); i++) {
                                    JSONObject jobj = dynBtnArray.getJSONObject(i);
                                    String value = jobj.getString("value");

                                    //Hide Search, Import, Export button from all forms
                                    if(!value.toLowerCase().matches("search|import|export|email")){
                                        DynamicButton dynamicButton = new DynamicButton();
                                        dynamicButton.setValue(value);
                                        dynamicButton.setOnClick(jobj.getString("onclick"));
                                        getButtonList().add(dynamicButton);
                                    }


                                }
                            }
                            for (int i = 0; i < permissionBtnArray.length(); i++) {
                                JSONObject jobj = permissionBtnArray.getJSONObject(i);
                                  String value = jobj.getString("value");

                                 DynamicButton permissionButton = new DynamicButton();

                                 //Hide Search, Import, Export button from all forms
                                 if(!value.toLowerCase().matches("search|import|export|email")){
                                     if (value.toLowerCase().matches("print \\(1\\)|print")) {
                                         if(getFormId().trim().matches("1931|3360|3334|3481|3408|3336")){
                                             permissionButton.setValue(jobj.getString("value"));
                                             permissionButton.setOnClick(jobj.getString("onclick"));
                                             getButtonList().add(permissionButton);
                                         }
                                     }else{
                                         permissionButton.setValue(jobj.getString("value"));
                                         permissionButton.setOnClick(jobj.getString("onclick"));
                                         getButtonList().add(permissionButton);
                                     }
                                 }
                            }

                            getButtonAdapter().notifyDataSetChanged();
                            getRecyclerView().setVisibility(View.VISIBLE);

                            if(isVlist()){
                                if(!isInputInTagField){
                                    Log.e(DEBUG_TAG, "callOnChangeState");
                                    if(!getRecordId().equals("0")){
                                        listener.onChangeState(getRecordId());
                                    }

                                }
                            }else{

                                if(dynBtnArray != null && dynBtnArray.length() > 0) {
                                    if(!isInputInTagField){
                                        Log.e(DEBUG_TAG, "callOnChangeState");
                                        listener.onChangeState(getRecordId());
                                    }

                                }else{
                                    if(!getRecordId().equals("0")){
                                        if(dynBtnArray == null || dynBtnArray.length() == 0){
                                            // means there is no next state and make it go to home page
                                            //isInputInTagField - we are checking if there is input in TAG Field by the user
                                            if(!isInputInTagField){
                                                if(communicatorInterface != null){
                                                    communicatorInterface.respond();
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  VolleyErrorUtil.showVolleyError(context, error);
            }
        });
        queue.add(request);
    }

    public void callActionAPI(final String value){
        try{
        showProgressDialog();
        String state = value;
        String[] m = state.split("SaveNextState");
        String n = m[1].replaceAll("[\\(\\)]","");
        String[] j = n.split(",");
        String nextStateId = j[0].replace("\'","");
        String nextStateName = j[2].replace("\'","");

        int userId = 0;
        String jvals = nextStateId+"@j@"+userId+"@j@";
        //for delete ,close pr pass report Id as selectedId
        String URL = String.format(Constant.URL_ACTION_TASK,
                prefManager.getClientServerUrl(),
                formId,
                recordId,
                jvals,
                prefManager.getCloudCode(),
                prefManager.getAuthToken());

        Log.e("ACTION_TASK", URL);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "Response=" + response);
                        if(response.toLowerCase().contains("retrievefieldvalues")){
                            ToastUtil.showToastMessage("Success", context);
                            listener.onSuccessResponse(recordId,isInputInTagField);
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // VolleyErrorUtil.showVolleyError(context, error);
                hideProgressDialog();
            }
        });
        queue.add(request);
        }catch (Exception e){
            e.printStackTrace();
            hideProgressDialog();

        }
    }

    private void showProgressDialog() {
        mWaiting = ProgressDialog.show(context, "",
                "Loading...", false);
    }

    private void hideProgressDialog() {
        if (mWaiting != null) {
            mWaiting.dismiss();
        }
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormId() {
        return formId;
    }

    public List<DynamicButton> getButtonList() {
        return buttonList;
    }

    public void setButtonList(List<DynamicButton> buttonList) {
        this.buttonList = buttonList;
    }

    public DynamicButtonAdapter getButtonAdapter() {
        return buttonAdapter;
    }

    public void setButtonAdapter(DynamicButtonAdapter buttonAdapter) {
        this.buttonAdapter = buttonAdapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public LinearLayout getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(LinearLayout rootLayout) {
        this.rootLayout = rootLayout;
    }

    public void setButtonTitle(String value){
        buttonTitle = value;
    }

    private String getButtonTitle(){
        return buttonTitle;
    }

    public boolean isInputInTagField() {
        return isInputInTagField;
    }

    public void setInputInTagField(boolean inputInTagField) {
        isInputInTagField = inputInTagField;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public boolean isVlist() {
        return isVlist;
    }

    public void setVlist(boolean vlist) {
        isVlist = vlist;
    }
}
