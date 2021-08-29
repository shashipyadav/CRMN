package com.example.myapplication.user_interface.upcoming_meeting.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.upcoming_meeting.controller.FilterAdapter;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.user_interface.upcoming_meeting.controller.MultipleTypeImageAdaptern;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListMainModel;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListModel;
import com.example.myapplication.user_interface.upcoming_meeting.model.MapModel;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapFilterActivity extends AppCompatActivity {
    public static List<FilterListMainModel> filterLstList = new ArrayList<>();
    public static String json = "";
    private static List<FilterListMainModel> list = new ArrayList<>();
    RecyclerView filterRV;
    RecyclerView filterValuesRV;
    private RecyclerView recylerview_map;
    private Button btn_apply;
    private ProgressDialog mWaiting;
    private List<MapModel> mapModelList = new ArrayList<>();
    private String filterData;
    private FilterAdapter adapter;
    private MultipleTypeImageAdaptern multipleTypeImageAdaptern;
    private EditText et_filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if(getIntent() != null){
            Intent intent = getIntent();
            filterData = intent.getStringExtra("filterData");
        }
        init();
        setTitleM();
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
                multipleTypeImageAdaptern = new MultipleTypeImageAdaptern(MapFilterActivity.this, list, filterValuesRV,et_filter);
                filterRV.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(MapFilterActivity.this, LinearLayoutManager.VERTICAL, false);
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
        if (NetworkUtil.isNetworkOnline(MapFilterActivity.this)) {
            dataListAPI();
        } else {
            DialogUtil.showAlertDialog(MapFilterActivity.this, "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }

    }

    private void dataListAPI() {
        showProgressDialog();
        // String url  = Constant.MAP_LIST_URL;
       //   String url = "https://best-erp.com:9000/strategicerp/getFunction.do?actn=getsqljsondata&sqlfieldid=54973&ids=54972&valuestring=Map%20Filter@j@&cloudcode=b2b&token=DNVEP9576C544LQG&type=json";

        SharedPrefManager mPrefManager = new SharedPrefManager(MapFilterActivity.this);
        String url = String.format(Constant.MAP_FILTER_LIST_URL, mPrefManager.getClientServerUrl(),mPrefManager.getCloudCode(),mPrefManager.getAuthToken());
        RequestQueue queue = Volley.newRequestQueue(MapFilterActivity.this);
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
                                if(jobj.has("APIValue")){
                                    json = (String) jobj.get("APIValue");
                                    list = (List<FilterListMainModel>) JsonUtil.jsonArrayToListObject(json, FilterListMainModel.class);
                                }

//                                if(jobj.has("FilterValue")){
//                                    json = (String) jobj.get("FilterValue");
//                                    list = (List<FilterListMainModel>) JsonUtil.jsonArrayToListObject(json, FilterListMainModel.class);
//                                }
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
            multipleTypeImageAdaptern = new MultipleTypeImageAdaptern(MapFilterActivity.this, list, filterValuesRV,et_filter);
            filterRV.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(MapFilterActivity.this, LinearLayoutManager.VERTICAL, false);
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

            multipleTypeImageAdaptern = new MultipleTypeImageAdaptern(MapFilterActivity.this, filterLstList, filterValuesRV,et_filter);
            filterRV.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(MapFilterActivity.this, LinearLayoutManager.VERTICAL, false);
            filterRV.setLayoutManager(layoutManager);
            filterRV.setAdapter(multipleTypeImageAdaptern);
        }
    }

    private void setTitleM() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Filter");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog() {
        mWaiting = ProgressDialog.show(MapFilterActivity.this, "",
                "Loading...", false);
    }

    private void hideProgressDialog() {
        if (mWaiting != null) {
            mWaiting.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
