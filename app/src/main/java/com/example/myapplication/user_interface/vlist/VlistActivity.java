package com.example.myapplication.user_interface.vlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.function.EncodeURIEngine;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.helper.FileHelper;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.view.OpenUrlActivity;
import com.example.myapplication.user_interface.summary.SummaryActivity;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.util.ExtensionUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.PixelUtil;
import com.example.myapplication.util.ToastUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VlistActivity extends AppCompatActivity {

    private String entry = "";
    private String title = "";
    private String vList = "";
    private String vlistRelationIds = "";
    private Field fieldObj;
    private LinearLayout linearLayoutHeader;
    private LinearLayout mllNoItemsView;
    private ProgressDialog progressDialog;
    private SharedPrefManager mPrefManager;
    private String json = "";

    //Volley
    private RequestQueue queue;
    private StringRequest request;

    private RecyclerView recyclerview;
    private VlistAdapter adapter;
    List<List<Vlist>> listOfdataFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlist);
        getWindow().setBackgroundDrawable(null);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void init()
    {
        getArguments();
        initViews();
        listOfdataFields = new ArrayList<>();
        mPrefManager = new SharedPrefManager(this);
        setActionBar();
    }


    private void initViews () {
        linearLayoutHeader = findViewById(R.id.linearlayout_header);
        mllNoItemsView = findViewById(R.id.no_items_view);
        initRecyclerview();
        initFab();
    }

    private void initRecyclerview(){
        recyclerview = findViewById(R.id.recylerview_vform);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initFab(){
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.EXTRA_DATA, json);
                bundle.putString(Constant.VLIST_FORM_ID, mPrefManager.getVFormId());
                bundle.putString(Constant.EXTRA_MODE,"new"); // new = new Form or update = form which already exists

                Intent intent = new Intent(VlistActivity.this,
                        VlistFormActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void setActionBar()
    {
       // getSupportActionBar().setTitle(title);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
    }

    private void getArguments()
    {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            entry =  bundle.getString(Constant.EXTRA_ENTRY);

            if(entry.equals("PendingTask")) {
                title = bundle.getString(Constant.EXTRA_TITLE);
                vList = bundle.getString(Constant.EXTRA_VLIST);
            }else {
                json=  bundle.getString(Constant.EXTRA_DATA);
                fieldObj = (Field) JsonUtil.jsonToObject(json, Field.class);
                if(fieldObj != null){
                    title = fieldObj.getFieldName();
                    vlistRelationIds = fieldObj.getVlistRelationIds();
                }
            }
        }
    }

    private void loadAPI() {
        vList = fieldObj.getOnClickVList();
        callGetVList(vList);
    }

    private void callGetVList(String vlist) {
        if(FormFragment.fieldsList != null) {
            EncodeURIEngine uriEngine = new EncodeURIEngine();
            String[] arr = vlist.split("get_vlist");
            String[] varr = arr[1].replaceAll("[\\(\\)]", "").split(",");
            String divid = varr[0];
            String func = varr[1].replace("'", "");
            String[] arr1 = func.split("\\+");

            String function = arr1[0];
            String replaceSqlList = arr1[1].replace("replacesqllist", "")
                    .replace(";", "");

            String valueString = "";
            String[] jarr = replaceSqlList.split("/");
            for (int i = 0; i < jarr.length; i++) {
                for (int j = 0; j < FormFragment.fieldsList.size(); j++) {
                    Field fObj = FormFragment.fieldsList.get(j);
                    if (fObj.getId().equals("field" + jarr[i])) {
                        String value = "";

                  /*  if(fObj.getFieldType().toLowerCase().matches("mdcombo|adcombo|dcombo")){
                        value = fObj.getValue().replace("%","%25");
                    }else{
                        value = fObj.getValue();
                    } */

                        value = fObj.getValue();
                        value = uriEngine.encodeURIComponent(value);
                        value = value.replaceAll("\\+", "%2B");
                        value = value.replaceAll("&", "%26");

                        Log.e("callGetVList", fObj.getId() + " == " + value);
                        valueString = valueString + value + "@j@";
                        break;
                    }
                }
            }

            Log.e("VlistActivity", "valueString = " + valueString);
            Log.e("VlistActivity", "final Function=" + function + valueString);

            if (NetworkUtil.isNetworkOnline(this)) {
                callAPI(function + valueString);
            } else {
                ToastUtil.showToastMessage("Please check your internet connection and try again", this);

            }
        }

    }
    private void callAPI(String function) {
        progressDialog = new ProgressDialog(VlistActivity.this);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();

        String url = mPrefManager.getClientServerUrl() + function+"&cloudcode="+mPrefManager.getCloudCode()+"&token="+mPrefManager.getAuthToken();

        Log.e("callAPI url", url);
        queue = Volley.newRequestQueue(this);
        request = new StringRequest(Request.Method.GET, url + "&type=json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            initVlistHeader(response);
                            if(progressDialog != null){
                                progressDialog.dismiss();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if(progressDialog != null){
                                progressDialog.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        queue.add(request);

    }


    private void initVlistHeader(String response) {
        try {
            if (linearLayoutHeader != null) {
                linearLayoutHeader.removeAllViews();
            }

            String[] arr = response.split("@L@");
            String data = arr[0];
            String title = arr[1];

            Log.e("Data",data);
            Log.e("Title",title);

            TextView tv = null;
            Typeface typefaceBold = ResourcesCompat.getFont(VlistActivity.this, R.font.montserrat_bold);
            Typeface typefaceNormal =  ResourcesCompat.getFont(VlistActivity.this, R.font.montserrat);


            JSONArray jsonArray = new JSONArray(title);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String columnTitle = jsonObject.getString("title");
                String columnField = jsonObject.getString("field");

                if(!columnTitle.matches("^-.*|Record ID")){
                    tv = new TextView(VlistActivity.this);
                    // tv.setText(key.toString());
                    tv.setText(columnTitle);
                    tv.setPadding(20,20,20,20);
                    tv.setTextSize(18);
                    tv.setTypeface(typefaceBold);
                    tv.setWidth(PixelUtil.convertDpToPixel(this, 150f));
                    tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tv.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    tv.setGravity(Gravity.CENTER);
                    tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    linearLayoutHeader.addView(tv);
                    tv = null;
                }
            }

            setUpDataForRows(data,jsonArray);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAPI();
    }


    private void setUpDataForRows(String data, JSONArray jsonArray) {
        try {
            if(listOfdataFields != null){
                listOfdataFields.clear();
            }

        //data fields
        JSONArray dataJsonArray = new JSONArray(data);
        for(int k=0; k<dataJsonArray.length(); k++) {
            JSONObject cObj = dataJsonArray.getJSONObject(k);
            String value = "";
            List<Vlist> dataFieldList = new ArrayList<>();
            for(int n=0; n < jsonArray.length(); n++){
                JSONObject titleJSONObj = jsonArray.getJSONObject(n);

                String cfield = titleJSONObj.getString("field");
                String cTitle = titleJSONObj.getString("title");

                value = cObj.getString(titleJSONObj.getString("field"));
                dataFieldList.add(new Vlist(cfield,value,cTitle));
            }
            listOfdataFields.add(dataFieldList);
        }

        if(jsonArray.length() == 0){
            mllNoItemsView.setVisibility(View.VISIBLE);
        }
        callAdapter();
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    private void callAdapter() {

        if(listOfdataFields.isEmpty()){
            mllNoItemsView.setVisibility(View.VISIBLE);
        }else{
            mllNoItemsView.setVisibility(View.GONE);
            if(adapter == null) {
                adapter = new VlistAdapter(this,listOfdataFields);
                recyclerview.setAdapter(adapter);

            }else{
                adapter.notifyDataSetChanged();
            }
        }
    }
}