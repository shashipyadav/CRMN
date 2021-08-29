package com.example.myapplication.user_interface.home.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListMainModel;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListModel;
import com.example.myapplication.user_interface.upcoming_meeting.model.MapModel;
import com.example.myapplication.user_interface.upcoming_meeting.controller.FilterAdapter;
import com.example.myapplication.user_interface.upcoming_meeting.controller.MultipleTypeImageAdaptern;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProductFilterActivity extends AppCompatActivity {

    public static List<FilterListMainModel> filterLstList = new ArrayList<>();
    public static String json = "";
    private static List<FilterListMainModel> list = new ArrayList<>();
    RecyclerView filterRV;
    RecyclerView filterValuesRV;
    private Button btn_apply;
    private ProgressDialog mWaiting;
    private List<MapModel> mapModelList = new ArrayList<>();
    private String filterData;
    private FilterAdapter adapter;
    private MultipleTypeImageAdaptern multipleTypeImageAdaptern;
    private EditText et_filter;
    String filterValueString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_filter);

        if(getIntent() != null){
            Intent intent = getIntent();
            filterData = intent.getStringExtra("filterData");
            filterValueString = intent.getStringExtra(Constant.EXTRA_FILTER_STRING);
        }
        json = "";
        init();
        setTitleM();
    }

    private void setTitleM() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Filter");
    }

    private void init() {
        et_filter = findViewById(R.id.et_filter);
        filterRV = findViewById(R.id.filterRV);
        filterValuesRV = findViewById(R.id.filterValuesRV);
        filterRV.setLayoutManager(new LinearLayoutManager(this));
        filterValuesRV.setLayoutManager(new LinearLayoutManager(this));
        btn_apply = findViewById(R.id.applyB);

        if (json.isEmpty()) {
            ApiCall();
        } else {
            list = (List<FilterListMainModel>) JsonUtil.jsonArrayToListObject(json, FilterListMainModel.class);
            setAdapterData();

        }

        // list = (List<FilterListMainModel>) JsonUtil.jsonArrayToListObject(json, FilterListMainModel.class);
        findViewById(R.id.clearB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterLstList = new ArrayList<>();
                list = (List<FilterListMainModel>) JsonUtil.jsonArrayToListObject(json, FilterListMainModel.class);
                multipleTypeImageAdaptern = new MultipleTypeImageAdaptern(ProductFilterActivity.this, list, filterValuesRV,et_filter);
                filterRV.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(ProductFilterActivity.this, LinearLayoutManager.VERTICAL, false);
                filterRV.setLayoutManager(layoutManager);
                filterRV.setAdapter(multipleTypeImageAdaptern);
            }
        });


        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterLstList = ((MultipleTypeImageAdaptern) multipleTypeImageAdaptern).getlistMainModelList();

                for (int i = 0; i < filterLstList.size(); i++) {

                    for (int k = 0; k < filterLstList.get(i).getFilterList().size(); k++) {
                        if (filterLstList.get(i).getFilterList().get(k).isSelected()) {
                            filterLstList.get(i).setSelectedmain(true);
                            break;
                        } else {
                            filterLstList.get(i).setSelectedmain(false);
                        }
                    }
                }


                List<String> stringList = new ArrayList<>();
                for (int i = 0; i < filterLstList.size(); i++) {

                    for (int k = 0; k < filterLstList.get(i).getFilterList().size(); k++) {
                        if (filterLstList.get(i).getFilterList().get(k).isSelected()) {
                            stringList.add(filterLstList.get(i).getFilterList().get(k).getTitle());
                        }
                    }
                }

                Intent i = new Intent();
                i.putExtra("filterData", JsonUtil.listObjectToJsonArray(stringList));
                setResult(200, i);
                finish();

            }
        });


    }

    private void ApiCall() {
        if (NetworkUtil.isNetworkOnline(ProductFilterActivity.this)) {
            dataListAPI();
        } else {
            DialogUtil.showAlertDialog(ProductFilterActivity.this, "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }

    }

    private void dataListAPI() {
        showProgressDialog();

        SharedPrefManager mPrefManager = new SharedPrefManager(ProductFilterActivity.this);
        String filterValue = "Product Filter@j@"+filterValueString;

        String url = String.format(Constant.PRODUCT_FILTER_LIST_URL, mPrefManager.getClientServerUrl(),filterValue,mPrefManager.getCloudCode(),mPrefManager.getAuthToken());
        Log.e("Filter Url",url);

        RequestQueue queue = Volley.newRequestQueue(ProductFilterActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //    Log.e(TAG, "callAPI Response=" + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                MapModel taskObj = new MapModel();
                                JSONObject jobj = jsonArray.getJSONObject(i);

                                if(jobj.has("FilterValue")){
                                    json = (String) jobj.get("FilterValue");
                                    list = (List<FilterListMainModel>) JsonUtil.jsonArrayToListObject(json, FilterListMainModel.class);
                                }
                            }

                            setAdapterData();
                            //callAdapter(mapModelList);
                            hideProgressDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgressDialog();
                            //   Log.e(TAG, "callAPI Error Response= "+  response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                hideProgressDialog();
            }
        });
        queue.add(request);
    }

    private void setAdapterData() {
        if (filterLstList.isEmpty()) {
            multipleTypeImageAdaptern = new MultipleTypeImageAdaptern(ProductFilterActivity.this, list, filterValuesRV,et_filter);
            filterRV.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ProductFilterActivity.this, LinearLayoutManager.VERTICAL, false);
            filterRV.setLayoutManager(layoutManager);
            filterRV.setAdapter(multipleTypeImageAdaptern);
        } else {
            List<FilterListModel> stringListchecked = new ArrayList<>();
            List<FilterListModel> stringListUncheckedchecked = new ArrayList<>();
            List<FilterListModel> mainFilter = new ArrayList<>();
            for (int i = 0; i < filterLstList.size(); i++) {
                stringListchecked = new ArrayList<>();
                stringListUncheckedchecked = new ArrayList<>();
                mainFilter = new ArrayList<>();
                for (int k = 0; k < filterLstList.get(i).getFilterList().size(); k++) {
                    if (filterLstList.get(i).getFilterList().get(k).isSelected()) {
                        stringListchecked.add(filterLstList.get(i).getFilterList().get(k));
                    } else {
                        stringListUncheckedchecked.add(filterLstList.get(i).getFilterList().get(k));

                    }
                }
                // filterLstList = new ArrayList<>();
                mainFilter.addAll(stringListchecked);
                mainFilter.addAll(stringListUncheckedchecked);

                filterLstList.get(i).setFilterList(mainFilter);
            }

            multipleTypeImageAdaptern = new MultipleTypeImageAdaptern(ProductFilterActivity.this, filterLstList, filterValuesRV,et_filter);
            filterRV.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ProductFilterActivity.this, LinearLayoutManager.VERTICAL, false);
            filterRV.setLayoutManager(layoutManager);
            filterRV.setAdapter(multipleTypeImageAdaptern);
        }
    }

    private void showProgressDialog() {
        mWaiting = ProgressDialog.show(ProductFilterActivity.this, "",
                "Loading...", false);
    }

    private void hideProgressDialog() {
        if (mWaiting != null) {
            mWaiting.dismiss();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}