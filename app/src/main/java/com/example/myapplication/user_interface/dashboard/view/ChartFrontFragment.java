package com.example.myapplication.user_interface.dashboard.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.dashboard.controller.ChartHelper;
import com.example.myapplication.user_interface.dashboard.controller.DrilldownBottomSheetFragment;
import com.example.myapplication.user_interface.dashboard.model.Chart;
import com.example.myapplication.user_interface.dashboard.model.ChartDataItem;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dashboard.model.Dashboard;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.VolleyErrorUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFrontFragment extends Fragment implements FilterBottomSheetFragment.FilterBottomSheetClickListener, View.OnClickListener {

    private static final String DEBUG_TAG = ChartFrontFragment.class.getSimpleName();

    private SharedPrefManager mPrefManager;
    private DatabaseManager mDbManager;
    private ChartHelper mChartHelper;
    private CardView cardChart;
    private LinearLayout llParentLayout;
    private FloatingActionButton fabFilter;
    private ProgressBar progressBar;
    private Dashboard dashboardObj;
    private LinearLayout llChartContainer;

    private String jsonMyObject = "";
    private String mTitle = "";
    private String mChartId = "";
    private String formURL = "";
    int count = 0;

    private Chart mChartObj = new Chart();

    public ChartFrontFragment() {
    }

    public static ChartFrontFragment newInstance(String object,String url,String title){
        ChartFrontFragment fragment = new ChartFrontFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.EXTRA_OBJECT, object );
        bundle.putString(Constant.EXTRA_TITLE, title);
        bundle.putString(Constant.EXTRA_DATA_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        if(bundle != null){
            jsonMyObject = bundle.getString(Constant.EXTRA_OBJECT);
            dashboardObj = new Gson().fromJson(jsonMyObject, Dashboard.class);
            mChartId = dashboardObj.getChartId();
            mTitle = bundle.getString(Constant.EXTRA_TITLE);
            formURL = bundle.getString(Constant.EXTRA_DATA_URL);
        }

        if(getActivity() != null){
            getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Constant.EXTRA_GET_DRILL_DOWN));
        }

        String str = dashboardObj.getOnClick();
        int startIndex = str.indexOf(0, str.indexOf(",") + 1);
        int endIndex = str.indexOf(startIndex, str.indexOf(","));

//        Log.e(DEBUG_TAG,"startIndex =" +startIndex);
//        Log.e(DEBUG_TAG, "endIndex = "+endIndex);
//        Log.e(DEBUG_TAG, "Next Chart Id" + str.substring(startIndex, endIndex));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String title = intent.getStringExtra(Constant.EXTRA_TITLE);
            String value = intent.getStringExtra(Constant.EXTRA_VALUE);
            String key = intent.getStringExtra(Constant.EXTRA_KEY);
            Log.e(DEBUG_TAG, "title = " + title);
            Log.e(DEBUG_TAG, "value = "+ value);
            Log.e(DEBUG_TAG, "x="+key);

            if(mChartObj.getExtraParams().toLowerCase().contains("drilldown")){
                DrilldownBottomSheetFragment bottomSheetFragment  = new DrilldownBottomSheetFragment();
                Bundle args = new Bundle();
                args.putString(Constant.EXTRA_DRILL_DOWN_TITLE , dashboardObj.getDrillDown());
                args.putString(Constant.EXTRA_KEY,key);
                args.putString(Constant.EXTRA_URL,mChartObj.getDataUrl());
                bottomSheetFragment.setArguments(args);
                bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getActivity() != null){
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.diff_chart_types, container,
                false);
        init();
        initViews(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        callDashboardFormAPI();
    }

    private void callDashboardFormAPI(){
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            callFormFieldApi(formURL, mChartId, mTitle);
        }else{
            buildChartUI(mChartId);
        }
    }

    private void init(){
        setTitle();
        initDatabase();
    }

    private void initViews(View root){
        progressBar = root.findViewById(R.id.progressBar);
        llChartContainer = root.findViewById(R.id.linear_chart);
        fabFilter = getActivity().findViewById(R.id.fabFilter);
        fabFilter.setOnClickListener(this);
        llParentLayout = root.findViewById(R.id.parent_layout);
        cardChart = root.findViewById(R.id.cardview);
        mPrefManager = new SharedPrefManager(getActivity());
        mChartHelper = new ChartHelper(getActivity());
    }

    private void initDatabase(){
        mDbManager = new DatabaseManager(getActivity());
        mDbManager.open();
    }

    private void setTitle(){
        getActivity().setTitle(getActivity().getResources().getString(R.string.chart_summary));
    }

    private void callFormFieldApi(String dashboardFormUrl, final String chartId,
                                  final String title) {
       // Log.e(DEBUG_TAG, "callFormFieldApi URL = " + dashboardFormUrl);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, dashboardFormUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     //   Log.e(DEBUG_TAG, "Response=" + response);
                        if(!response.isEmpty()){
                            boolean formAvailable = mDbManager.checkIfFormExists(Integer.parseInt(chartId));
                            if(formAvailable){
                                mDbManager.updateForm(Integer.parseInt(chartId),response);
                            }else{
                                mDbManager.insertForm(Integer.parseInt(chartId), title,response);
                            }
                        }
                        buildChartUI(chartId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(getActivity() != null){
                    VolleyErrorUtil.showVolleyError(getActivity(), error);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        queue.add(request);
    }

    private void buildChartUI(String chartId){
        String response = mDbManager.getFormJson(Integer.parseInt(chartId));

        try{
            mChartObj = new Chart();
            JSONObject jsonObject = new JSONObject(response);
            mChartObj.setDataUrl(jsonObject.getString("dataurl"));
            mChartObj.setFilter(jsonObject.getString("filter"));
            mChartObj.setExtraParams(jsonObject.getString("extraparams"));
            mChartObj.setBlocks(jsonObject.getInt("blocks"));
            mChartObj.setChartType(jsonObject.getString("charttype"));

            mChartObj.setMobileBlocks(jsonObject.getInt("mobileblocks"));
            mChartObj.setChartTitle(jsonObject.getString("charttitle"));
            if(jsonObject.has("morefilter")){
                mChartObj.setMoreFilter(jsonObject.getString("morefilter"));
            }

            mChartObj.setJson(jsonObject.getString("json"));
            mChartObj.setId(jsonObject.getString("id"));

            callChartDataAPI("",false);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callChartDataAPI( String filterValue,boolean filter){

        String url = mChartObj.getDataUrl();
        url = url.replaceAll("['\\s+]","");
        url = url.replace("window.baseurl",mPrefManager.getClientServerUrl());
        url = url.replace("window.cloudcode",mPrefManager.getCloudCode());
        url = url.replace("window.token",mPrefManager.getAuthToken());

        if(filter){
            url = url +"&"+filterValue;
        }

        Log.e(DEBUG_TAG, "CHART DATA API FINAL URL = "+url);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "Chart Data Response=" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<ChartDataItem> chartDataItemList = new ArrayList<>();
                            for(int i=0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ChartDataItem obj = new Gson().fromJson(jsonObject.toString(), ChartDataItem.class);
                                chartDataItemList.add(obj);
                            }
                            mChartObj.setChartDataItemList(chartDataItemList);

                            populateChart();

                        } catch (Exception e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorUtil.showVolleyError(getActivity(), error);
                progressBar.setVisibility(View.GONE);
            }
        });
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT,
                Constant.NO_OF_TRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * based on chart type populate different types of chart
     */
    private void populateChart(){
        Log.e(DEBUG_TAG,"populateChart" +count++);
        llChartContainer.removeAllViews();

        AnyChartView mAnyChartView = new AnyChartView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(10,10,10,10);
        params.weight=1;
        mAnyChartView.setLayoutParams(params);

        String chartType = mChartObj.getChartType();
        final List<DataEntry> data = new ArrayList<>();

        for(int i = 0; i < mChartObj.getChartDataItemList().size(); i++){
            ChartDataItem charObj = mChartObj.getChartDataItemList().get(i);
            data.add(new ValueDataEntry(charObj.getSource(),charObj.getCount()));
        }

        if(chartType.equalsIgnoreCase("donut")){
            mChartHelper.setDonutChart(mAnyChartView, data,mTitle);
        }else if(chartType.equalsIgnoreCase("funnel")){
            mChartHelper.setFunnelChart(mAnyChartView,data,mTitle);

        }else if(chartType.equalsIgnoreCase("bar")){
            mChartHelper.setBarChart(mAnyChartView,data,mTitle);

        }else if(chartType.equalsIgnoreCase("hbar")){
            mChartHelper.setHorizontalBarChart(mAnyChartView,data,mTitle);

        }else if(chartType.equalsIgnoreCase("stack-bar")){
            Cartesian stackedBarChart = mChartHelper.setStackedBarChart(data,mTitle);
            mAnyChartView.setChart(stackedBarChart);

        }else if(chartType.equalsIgnoreCase("sparkline")){

        }else if(chartType.equalsIgnoreCase("guage")) {
            mChartHelper.setGuageChart(mAnyChartView,data,mTitle);

        }else if(chartType.equalsIgnoreCase("line")){
            mChartHelper.setLineChart(mAnyChartView,data,mTitle);

        }else if(chartType.equalsIgnoreCase("area")){
            mChartHelper.setAreaChart(mAnyChartView,data,mTitle);

        }else if(chartType.equalsIgnoreCase("table")){

        }else{

            Pie pie = mChartHelper.setPieChart(data,mTitle);
            mAnyChartView.setChart(pie);
        }

        llChartContainer.addView(mAnyChartView);
        progressBar.setVisibility(View.GONE);
        if(data.isEmpty()){
            //show No Data to display message
        }
    }

    @Override
    public void sendFilterValues(String title, String value) {

        Log.e(DEBUG_TAG, "sendFilterValues called");
        Log.e(DEBUG_TAG, "Title - " +title + " Value = " + value);

        if (NetworkUtil.isNetworkOnline(getActivity())) {
               callChartDataAPI(value,true);
        } else {
            DialogUtil.showAlertDialog(getActivity(), "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == fabFilter){
            FilterBottomSheetFragment  bottomSheetFragment  = new FilterBottomSheetFragment(this);
            Bundle args = new Bundle();
            args.putString(Constant.EXTRA_MORE,mChartObj.getMoreFilter());
            args.putString(Constant.EXTRA_PARAMS, mChartObj.getExtraParams());
            bottomSheetFragment.setArguments(args);
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        }
    }

    @Override
    public void onDestroyView() {
        Log.e(DEBUG_TAG, "onDestroyView() called");
        super.onDestroyView();
    }
}
