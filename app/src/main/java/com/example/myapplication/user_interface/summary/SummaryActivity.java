package com.example.myapplication.user_interface.summary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import com.example.myapplication.R;
import com.example.myapplication.user_interface.forms.view.OpenUrlActivity;
import com.example.myapplication.function.EncodeURIEngine;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.helper.FileHelper;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.util.ExtensionUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = SummaryActivity.class.getName();

    private String entry    = "";
    private String title    = "";
    private String vList    = "";
    private Field fieldObj;

    private TableLayout tableLayout;
    private ProgressDialog progressDialog;
    private SharedPrefManager mPrefManager;
    private LinearLayout mllNoItemsView;

    //Volley
    private RequestQueue queue;
    private StringRequest request;



    //need title, vlist,
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dff_popup);

        //added for performance
        getWindow().setBackgroundDrawable(null);
        initViews();

        if(vList.equals("")){
            //this means its coming from FormFragment
            load();
        }else{
            //this means its coming from PendingTaskDetailsActivity
            vList = vList.replace("VLIST:","");

            if (NetworkUtil.isNetworkOnline(this)) {
                callAPI(vList);
            }else{
                ToastUtil.showToastMessage("Please check your internet connection and try again",this);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void initViews()
    {
        mPrefManager = new SharedPrefManager(this);
        getArguments();
        setActionBar();
        initTableLayout();
    }

    @Override
    public void onBackPressed() {
        if(request != null){
            if( progressDialog != null) {
                progressDialog.dismiss();
            }

            request.cancel();
        }

        super.onBackPressed();
    }

    private void getArguments()
    {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            entry =  bundle.getString(Constant.EXTRA_ENTRY);
            if(entry.equals("PendingTask")){
                title = bundle.getString(Constant.EXTRA_TITLE);
                vList = bundle.getString(Constant.EXTRA_VLIST);
            }else{

                String json=  bundle.getString(Constant.EXTRA_DATA);
                fieldObj = (Field) JsonUtil.jsonToObject(json, Field.class);
                if(fieldObj != null){
                    if (fieldObj.getShowfieldname().isEmpty()) {
                         title = fieldObj.getField_name();
                    } else {
                        title = fieldObj.getShowfieldname();
                    }
                }

            }
        }
    }

    private void setActionBar()
    {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initTableLayout(){
        tableLayout = findViewById(R.id.tablelayout);
    }

    private void load(){
        String vList = "";
        if(fieldObj.getType().equals("VLIST")){
            vList = fieldObj.getOnClickVList();
        }else{
            vList = fieldObj.getOnclicksummary();
        }
        callGetVList(vList);
    }

    private void callGetVList(String vlist){

     //   vlist = "get_vlist('results_48198','getFunction.do?actn=getsqljsondata&sqlfieldid=summary&fieldid=48198&ids=45936/45938/48197/45932/47529/47694/&valuestring='+replacesqllist('45936/45938/48197/45932/47529/47694/'));";
        EncodeURIEngine uriEngine = new EncodeURIEngine();
        String[] arr = vlist.split("get_vlist");
        String[] varr = arr[1].replaceAll("[\\(\\)]", "").split(",");
        String divid = varr[0];
        String func = varr[1].replace("'","");
        String[] arr1 = func.split("\\+");

        String function = arr1[0];
        String replaceSqlList =  arr1[1].replace("replacesqllist","").replace(";","");

        String valueString = "";
        String[] jarr = replaceSqlList.split("/");
        for(int i=0; i < jarr.length; i++){
            for(int j =0; j < FormFragment.fieldsList.size(); j++){
                Field fObj = FormFragment.fieldsList.get(j);
                if(fObj.getId().equals("field"+jarr[i])){
                   String  value = "";

                  /*  if(fObj.getFieldType().toLowerCase().matches("mdcombo|adcombo|dcombo")){
                        value = fObj.getValue().replace("%","%25");
                    }else{
                        value = fObj.getValue();
                    } */

                    value = fObj.getValue();


                    value = uriEngine.encodeURIComponent(value);
                    value = value.replaceAll("\\+", "%2B");
                    value = value.replaceAll("&", "%26");

                    Log.e("callGetVList",fObj.getId()+ " == " + value );
                     valueString = valueString + value+ "@j@";
                }
            }
        }

        Log.e(DEBUG_TAG, "valueString = "+ valueString);
        Log.e(DEBUG_TAG, "final Function="+function+valueString);

        if (NetworkUtil.isNetworkOnline(this)) {
            callAPI(function+valueString);
        }else{
            ToastUtil.showToastMessage("Please check your internet connection and try again",this);

        }
    }

    private void callAPI(String function) {
        progressDialog = new ProgressDialog(SummaryActivity.this);
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
                                showTable(response);
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

    public void showTable(final String response){
        List<String> hiddenFieldsArray = new ArrayList<>();

                try{
                    String[] arr = response.split("@L@");
                    String data = arr[0];
                    String title = arr[1];

                    Log.e("Data",data);
                    Log.e("Title",title);

                    TableRow row= new TableRow(SummaryActivity.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);
                    TextView tv = null;

                    Typeface typefaceBold = ResourcesCompat.getFont(SummaryActivity.this, R.font.montserrat_bold);
                    Typeface typefaceNormal =  ResourcesCompat.getFont(SummaryActivity.this, R.font.montserrat);


                    JSONArray jsonArray = new JSONArray(title);
                    for(int i=0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String columnTitle = jsonObject.getString("title");
                        String columnField = jsonObject.getString("field");

                        if(!columnTitle.matches("^-.*")){
                            tv = new TextView(SummaryActivity.this);
                            // tv.setText(key.toString());
                            tv.setText(columnTitle);
                            tv.setPadding(20,20,20,20);
                            tv.setTextSize(18);
                            tv.setTypeface(typefaceBold);
                            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                            tv.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                            tv.setGravity(Gravity.CENTER);
                            tv.setLayoutParams(lp);
                            row.addView(tv);
                            tv = null;
                        }else{
                            hiddenFieldsArray.add(columnField);
                        }

                    }
                    tableLayout.addView(row);

                    JSONArray dataJsonArray = new JSONArray(data);
                    for(int k=0; k<dataJsonArray.length(); k++){
                      //  TableRow cRow = new TableRow(SummaryActivity.this);
                        row = new TableRow(SummaryActivity.this);
                        row.setLayoutParams(lp);

                        JSONObject cObj = dataJsonArray.getJSONObject(k);
                        String value = "";
                        for(int n=0; n < jsonArray.length(); n++){
                            JSONObject titleJSONObj = jsonArray.getJSONObject(n);

                            String field = titleJSONObj.getString("field");

                            if(!hiddenFieldsArray.isEmpty()){
                                for(int p = 0; p < hiddenFieldsArray.size(); p++) {
                                    if(!hiddenFieldsArray.get(p).equals(field)) {
                                        value = cObj.getString(titleJSONObj.getString("field"));
                                        tv = new TextView(SummaryActivity.this);
                                        tv.setText(value);
                                        tv.setPadding(20, 20, 20, 20);
                                        tv.setBackground(getResources().getDrawable(R.drawable.cell_shape_light_bg));
                                        tv.setTextSize(16);
                                        tv.setTextIsSelectable(true);
                                        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        tv.setGravity(Gravity.CENTER);
                                        tv.setTypeface(typefaceNormal);

                                        if(value.startsWith("http") || value.startsWith("https") ){
                                            tv.setText(Html.fromHtml("<font color=#000000>  <u>" + value + "</u>  </font>"));
                                            final String finalValue1 = value;
                                            tv.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(SummaryActivity.this, OpenUrlActivity.class);
                                                    intent.putExtra(Constant.EXTRA_URL, finalValue1);
                                                    startActivity(intent);
                                                }
                                            });

                                        }else{
                                            if(!value.matches("^(.+)@(.+)$")){
                                                String extension = ExtensionUtil.getExtension(value);
                                                    if(!extension.isEmpty()) {
                                                        tv.setText(Html.fromHtml("<font color=#000000>  <u>" + value + "</u>  </font>"));

                                                        final String finalValue = value;
                                                        tv.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                FileHelper fileHelper = new FileHelper(SummaryActivity.this);
                                                                fileHelper.viewFile(finalValue,true);
                                                            }
                                                        });
                                                    }
                                            }
                                        }
                                        row.addView(tv);
                                        tv = null;
                                    }
                                }
                            }else{

                                value = cObj.getString(titleJSONObj.getString("field"));
                                tv = new TextView(SummaryActivity.this);
                                tv.setText(value);
                                tv.setPadding(20, 20, 20, 20);
                                tv.setBackground(getResources().getDrawable(R.drawable.cell_shape_light_bg));
                                tv.setTextSize(16);
                                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                                tv.setGravity(Gravity.CENTER);
                                tv.setTypeface(typefaceNormal);

                                if(value.startsWith("http") || value.startsWith("https") ){
                                    tv.setText(Html.fromHtml("<font color=#000000>  <u>" + value + "</u>  </font>"));
                                    final String finalValue1 = value;
                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(SummaryActivity.this, OpenUrlActivity.class);
                                            intent.putExtra(Constant.EXTRA_URL, finalValue1);
                                            startActivity(intent);
                                        }
                                    });

                                }else{
                                    if(!value.matches("^(.+)@(.+)$")){
                                        String extension = ExtensionUtil.getExtension(value);
                                        if(!extension.isEmpty()){
                                            tv.setText(Html.fromHtml("<font color=#000000>  <u>" + value + "</u>  </font>"));

                                            final String finalValue = value;
                                            tv.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    FileHelper fileHelper = new FileHelper(SummaryActivity.this);
                                                    fileHelper.viewFile(finalValue,true);
                                                }
                                            });
                                        }
                                    }
                                }
                                row.addView(tv);
                                tv = null;
                            }
                        }
                        tableLayout.addView(row);
                    }

                    if(jsonArray.length() == 0){
                        mllNoItemsView = findViewById(R.id.no_items_view);
                        mllNoItemsView.setVisibility(View.VISIBLE);
                    }

            }catch (Exception e){
                e.printStackTrace();
                }
            }
 //       });
 //   }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
    
    private void addTextView(TableRow row, TextView tv, String value){
        tv = new TextView(SummaryActivity.this);
        tv.setText(value);
        tv.setPadding(20, 20, 20, 20);
        tv.setBackground(getResources().getDrawable(R.drawable.cell_shape_light_bg));
        tv.setTextSize(16);
        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        tv.setGravity(Gravity.CENTER);
      //  tv.setTypeface(typefaceNormal);

        if(value.startsWith("http") || value.startsWith("https") ){
            tv.setText(Html.fromHtml("<font color=#000000>  <u>" + value + "</u>  </font>"));
            final String finalValue1 = value;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SummaryActivity.this, OpenUrlActivity.class);
                    intent.putExtra(Constant.EXTRA_URL, finalValue1);
                    startActivity(intent);
                }
            });

        }else{
            String extension = ExtensionUtil.getExtension(value);
            if(!extension.isEmpty()){
                tv.setText(Html.fromHtml("<font color=#000000>  <u>" + value + "</u>  </font>"));

                final String finalValue = value;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileHelper fileHelper = new FileHelper(SummaryActivity.this);
                        fileHelper.viewFile(finalValue,true);
                    }
                });
            }
        }
        row.addView(tv);
        tv = null;
    }
}
