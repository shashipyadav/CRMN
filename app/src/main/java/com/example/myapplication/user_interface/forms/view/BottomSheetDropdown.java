package com.example.myapplication.user_interface.forms.view;

import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.function.DlistFunctionHelper;
import com.example.myapplication.function.FunctionHelper;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.view.DListFormActivity;
import com.example.myapplication.user_interface.forms.controller.BottomSheetDropdownAdapter;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.model.Form;
import com.example.myapplication.user_interface.forms.model.OptionModel;
import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.vdlist.VlistDlistFormActivity;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.example.myapplication.util.KeyboardUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BottomSheetDropdown extends BottomSheetDialogFragment
        implements BottomSheetDropdownAdapter.BottomSheetDropdownClickListener
        ,View.OnClickListener, BottomSheetDropdownAdapter.OnMdcomboItemSelectedListener {

    private static final String DEBUG_TAG = BottomSheetDropdown.class.getSimpleName();
    private BottomSheetDropdownAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<OptionModel> dropdownList = new ArrayList<>();
    private String type = "";
    private int position = -1;
    String onClickRightBtnFunc = "";
    private String fieldName = "";
    BottomSheetClickListener listener;
    private Button btnOK, btnCancel;
    private boolean isCalledFromDlist = false;
    private int curdListItemPosition = -1;
    private boolean isVlist;

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.btn_ok){
            List<OptionModel> list = adapter.getSelected();
            String selectedItems = "";
            for (int i = 0; i < list.size(); i++) {

                if (list.get(i).isSelected()) {
                    String itemName = list.get(i).getValue().trim();
                    if (itemName.contains(" --- ")) {
                        String[] arr = itemName.split(" --- ");
                        arr[0] = arr[0].trim();
                        selectedItems = selectedItems.concat(arr[0] + ", ");
                    } else {
                        selectedItems = selectedItems.concat(itemName + ", ");
                    }
                }
            }
            listener.getSelectValues(selectedItems,position);
            dismiss();

        }else if(v.getId() == R.id.btn_cancel){
            dismiss();
        }
    }

    @Override
    public void disableDropDownItems(int position) {
        String value = dropdownList.get(position).getValue();
            if(dropdownList.get(position).isSelected()){
                adapter.onSelected(value);
            }else{
                adapter.onDeselected();
            }
    }

    public interface BottomSheetClickListener{
        public void getSelectValues(String selectedValue, int fieldPosition);
    }

    public BottomSheetDropdown(BottomSheetClickListener listener,boolean isVlist){
        this.listener = listener;
        this.isVlist = isVlist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(Constant.EXTRA_TYPE);
            position = getArguments().getInt(Constant.EXTRA_POSITION);
            onClickRightBtnFunc = getArguments().getString(Constant.EXTRA_ONCLICK_RIGHT);
            fieldName = getArguments().getString(Constant.EXTRA_FIELD_NAME);
            isCalledFromDlist = getArguments().getBoolean(Constant.EXTRA_IS_DLIST);
        }

        if(isCalledFromDlist){
            curdListItemPosition = getArguments().getInt(Constant.EXTRA_DLIST_ROW_POSITION);
            Log.e(DEBUG_TAG,"curddListItemPosition ="+ curdListItemPosition);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = null;
        if(type.equalsIgnoreCase("mdcombo")){
            view = inflater.inflate(R.layout.fragment_bottom_sheet_mdcombo, container, false);
            initButton(view);

        }else {
             view = inflater.inflate(R.layout.fragment_bottom_sheet_adcombo, container, false);
        }
        setFieldName(view);
        setUpRecyclerView(view);
        setSearchEditText(view);


        if(isCalledFromDlist){
            if (onClickRightBtnFunc.contains("load")) {
              //  if (type.toLowerCase().matches("mdcombo|adcombo|dcombo|tag")) {
                    loadWhenClickedInDlist(position, onClickRightBtnFunc);
               // }
            }
        }else{
            if (onClickRightBtnFunc.contains("load")) {
              //  if (type.toLowerCase().matches("mdcombo|adcombo|dcombo|tag")) {
                    loadWhenClickedInForm(position, onClickRightBtnFunc);
              //  }
            }
        }

        return view;
    }

    private void initButton(View root){
         btnOK = root.findViewById(R.id.btn_cancel);
         btnOK.setOnClickListener(this);
         btnCancel = root.findViewById(R.id.btn_ok);
         btnCancel.setOnClickListener(this);
    }

    private void setSearchEditText(View root) {
         EditText  etSearch = root.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter != null){
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setFieldName(View root){
        TextView txtFieldName = root.findViewById(R.id.txt_field_name);
        txtFieldName.setText(fieldName);
    }

    private void loadSpinnerData(final String field, String url,
                                 final int position, final boolean mdcombo, final String type) {
        KeyboardUtil.hideKeyboard(getActivity());
        boolean isValidURL = false;
        if (url != null && !url.isEmpty()) {
            isValidURL = android.util.Patterns.WEB_URL.matcher(url).matches();
        }
        if (isValidURL) {
            if (NetworkUtil.isNetworkOnline(getActivity())) {

                Log.e(DEBUG_TAG, "loadSpinnerData URL=" + url);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest request = new StringRequest(Request.Method.GET, url + "&type=json",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e(DEBUG_TAG, "LoadSpinner Response=" + response);

                                try {
                                    Object json = new JSONTokener(response).nextValue();
                                    if (json instanceof JSONObject) {

                                        JSONObject jsonObject = new JSONObject(response);
                                        String value = jsonObject.getString("response");
                                        if(value.contains("---   img@#@#")){
                                            String[] arr = value.split("---   img@#@#");
                                            value=arr[0];
                                        }

                                        if (mdcombo) {
                                            //replacing % as it was showing %% in Stock Report - Design Code field.
                                            dropdownList.add(0, new OptionModel("%25", value.replace("%%", "%")));
                                            callAdapter();
//
                                        } else if (type.equalsIgnoreCase("adcombo")) {
                                            dropdownList.add(new OptionModel(value, value));
                                            callAdapter();
                                        }
                                    } else if (json instanceof JSONArray) {
                                        JSONArray jsonArray = new JSONArray(response);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            String value = jsonArray.getString(i);
                                            if(value.contains("---   img@#@#")){
                                                String[] arr = value.split("---   img@#@#");
                                                value=arr[0];
                                            }

                                            dropdownList.add(new OptionModel(value, value));
                                        }

                                        if (mdcombo) {
                                            dropdownList.add(0, new OptionModel("%25", "%"));
                                        }

                                        callAdapter();
                                    }

                                } catch (Exception e) {
                                    if (e instanceof JSONException) {
                                        if (mdcombo) {
                                            //adding this so that we can get % in multispinner even when we don't get any data from the api
                                            //basically added for Item Code in Stock Report
                                            dropdownList.add(0, new OptionModel("%25", "%"));
                                            callAdapter();
                                        }
                                    }

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(request);

            } else {
                ToastUtil.showToastMessage("Please check your internet connection and try again", getActivity());
                //    DialogUtil.showAlertDialog(getActivity(),
                //    "No Internet Connection!", "Please check your internet connection and try again", false, false);
            }
        } else {
            Log.e(DEBUG_TAG, "#line 402 , Bad URL = " + url);
        }

    }

    public void callAdapter(){
        adapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView(View root){
        adapter = new BottomSheetDropdownAdapter( getActivity(),dropdownList,type,this);
        adapter.setMdcomboItemSelectedListener(this);
        recyclerView  = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    private void loadWhenClickedInForm(int position, String onClickRightBtnFunc) {
        Log.e(DEBUG_TAG, "loadSpinner = " + onClickRightBtnFunc);
        String type = "";

        List<Field> fieldList = null;
        List<Field> additionalFieldDataList = null;

        if(isVlist){
            type = VlistFormActivity.vFieldsList.get(position).getDataType();
            fieldList = VlistFormActivity.vFieldsList;
            additionalFieldDataList = VlistFormActivity.vAdditionalFieldDataList;
        }else{
           type =  FormFragment.fieldsList.get(position).getDataType();
            fieldList = FormFragment.fieldsList;
            additionalFieldDataList = FormFragment.additionalFieldDataList;
        }


        String[] h = onClickRightBtnFunc.split("\\(");
        String[] f = h[1].replaceAll("['\\(\\)]", "").split(",");
        int fieldId = Integer.parseInt(f[0]);
        List<String> oArr = f[1].isEmpty() ? Collections.EMPTY_LIST : Collections.EMPTY_LIST; // TODO: replace the else condition later
        Boolean issql = Boolean.valueOf(f[2]);
        String extension = f[3];
        String jcodelist = f[4];
        String fieldtype = f[5];
        boolean mdcombo = Boolean.parseBoolean(f[6].replace(";", ""));

        FunctionHelper funcHelper = new FunctionHelper(getActivity());
        funcHelper.setFieldsList(fieldList);
        funcHelper.setAdditionalFieldDataList(additionalFieldDataList);

        String url = funcHelper.load(fieldId, oArr, issql, extension, jcodelist, fieldtype,
                mdcombo,isVlist);
        String[] arr = jcodelist.split("@@");
        String fieldWhenWeSetValue = "field" + arr[0];

        loadSpinnerData(fieldWhenWeSetValue, url, position, mdcombo, type);
    }


    private void loadWhenClickedInDlist(int position, String dropDownClickFunc) {
        try {
            Log.e(DEBUG_TAG, "loadSpinner = " + dropDownClickFunc);

            String[] h = dropDownClickFunc.split("\\(");
            String[] f = h[1].replaceAll("['\\(\\)]", "").split(",");
            int fieldId = Integer.parseInt(f[0]);
            List<String> oArr = f[1].isEmpty() ? Collections.EMPTY_LIST : Collections.EMPTY_LIST; // TODO: replace the else condition later
            Boolean issql = Boolean.valueOf(f[2]);
            String extension = f[3];
            int curDlistItem = curdListItemPosition;
            extension = "_" + curDlistItem;
            String jcodelist = f[4];
            String fieldtype = f[5];
            Boolean mdcombo = Boolean.valueOf(f[6].replace(";", ""));

            List<DList> dlistFieldValues = null;
            List<Field> additionalFieldDataList = null;

            if (isVlist) {
                type = VlistDlistFormActivity.dlistFieldValues.get(position).getFieldType();
                dlistFieldValues = VlistDlistFormActivity.dlistFieldValues;
                additionalFieldDataList = VlistFormActivity.vAdditionalFieldDataList;
            } else {
                type = DListFormActivity.dlistFieldValues.get(position).getFieldType();
                dlistFieldValues = DListFormActivity.dlistFieldValues;
                additionalFieldDataList = FormFragment.additionalFieldDataList;
            }

            DlistFunctionHelper mFunctionHelper = new DlistFunctionHelper(getActivity());
            mFunctionHelper.setDlistFieldValues(dlistFieldValues);
            mFunctionHelper.setAdditionalFieldDataList(additionalFieldDataList);
            mFunctionHelper.setFieldsList(FormFragment.fieldsList);

            String url = mFunctionHelper.load(fieldId, oArr, issql, extension, jcodelist, fieldtype,
                    mdcombo, isVlist);

            String[] arr = jcodelist.split("@@");
            //   String fieldWhenWeSetValue = "field"+arr[0];
            String fieldWhenWeSetValue = arr[0];
            loadSpinnerDataInDlist(fieldWhenWeSetValue, url, position);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(String selectedValue) {
        dismiss();
        listener.getSelectValues(selectedValue,position);
    }

    private void loadSpinnerDataInDlist(final String field, String url, final int position) {
        boolean isValidURL = false;
        if (url != null && !url.isEmpty()) {
            isValidURL = android.util.Patterns.WEB_URL.matcher(url).matches();
        }
        if(isValidURL) {
            if(NetworkUtil.isNetworkOnline(getActivity())) {
                Log.e(DEBUG_TAG, "dlist loadSpinnerData URL=" + url);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest request = new StringRequest(Request.Method.GET, url + "&type=json",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e(DEBUG_TAG, "LoadSpinner Response=" + response);
                                try {
                                    Object json = new JSONTokener(response).nextValue();
                                    if (json instanceof JSONObject) {

                                        JSONObject jsonObject = new JSONObject(response);
                                        String value = jsonObject.getString("response").trim();

                                        dropdownList.add(new OptionModel(value, value));

                                    }else if (json instanceof JSONArray) {

                                        JSONArray jsonArray = new JSONArray(response);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            dropdownList.add(new OptionModel(jsonArray.getString(i), jsonArray.getString(i)));
                                        }
                                    }
                                    callAdapter();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(request);
            }else{
                ToastUtil.showToastMessage("Please check your internet connection and try again",getActivity());
                //    DialogUtil.showAlertDialog(getActivity(),
                //            "No Internet Connection!", "Please check your internet connection and try again", false, false);
            }
        }
    }

}