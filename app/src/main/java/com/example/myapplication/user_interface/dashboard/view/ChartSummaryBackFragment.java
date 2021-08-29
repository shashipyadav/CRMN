package com.example.myapplication.user_interface.dashboard.view;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.dashboard.model.Chart;
import com.example.myapplication.user_interface.dashboard.model.ChartDataItem;
import com.example.myapplication.helper.SharedPrefManager;
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
public class ChartSummaryBackFragment extends Fragment implements FilterBottomSheetFragment.FilterBottomSheetClickListener, View.OnClickListener {

    private Chart mChartObj;
    private static final String DEBUG_TAG = ChartSummaryBackFragment.class.getSimpleName();
    private String mFormURL = "";
    private String mTitle = "";
    private String mChartId = "";
    private SharedPrefManager mPrefManager;
    private DatabaseManager mDbManager;
    private LinearLayout mLinearLayout;
    private Typeface typefaceSemiBold;
    private Typeface typefaceRegular;
    private ProgressBar progressBar;
    private FloatingActionButton fabFilter;

    public ChartSummaryBackFragment() {
    }

    public static ChartSummaryBackFragment newInstance(String chartId,
                                                       String title,
                                                       String url) {

        ChartSummaryBackFragment fragment = new ChartSummaryBackFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.EXTRA_CHART_ID, chartId);
        bundle.putString(Constant.EXTRA_TITLE, title);
        bundle.putString(Constant.EXTRA_DATA_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mTitle = bundle.getString(Constant.EXTRA_TITLE);
            mChartId = bundle.getString(Constant.EXTRA_CHART_ID);
            mFormURL = bundle.getString(Constant.EXTRA_DATA_URL);
            Log.e(DEBUG_TAG, "FORM URL = " + mFormURL);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.chart_summary, container,
                false);
        initView(root);
        init();
        return root;
    }

    private void init(){
        initDatabase();
        showHeader();
        callDashboardFormAPI();

    }

    private void initView(View root) {
        fabFilter = getActivity().findViewById(R.id.fabFilter);
        fabFilter.setOnClickListener(this);
        progressBar = root.findViewById(R.id.progressBar);
        typefaceSemiBold = ResourcesCompat.getFont(getActivity(), R.font.titillium_web_semibold);
        typefaceRegular = ResourcesCompat.getFont(getActivity(), R.font.titillium_web);
        mPrefManager = new SharedPrefManager(getActivity());
        mLinearLayout = root.findViewById(R.id.linearLayout);

    }

    private void initDatabase(){
        mDbManager = new DatabaseManager(getActivity());
        mDbManager.open();
    }


    private void showTable() {

        for (int i = 0; i < mChartObj.getChartDataItemList().size(); i++) {
            ChartDataItem obj = mChartObj.getChartDataItemList().get(i);

            TableRow tr = new TableRow(getActivity());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView txtSource = new TextView(getActivity());
            txtSource.setPadding(16, 16, 16, 16);
            txtSource.setTextSize(16);
            txtSource.setTypeface(typefaceRegular);
            txtSource.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            txtSource.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtSource.setText(obj.getSource().isEmpty() ? "NA" : obj.getSource());
            txtSource.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tr.addView(txtSource);


            TextView txtCount = new TextView(getActivity());
            txtCount.setPadding(16, 16, 16, 16);
            txtCount.setTextSize(16);
            txtCount.setTypeface(typefaceRegular);
            txtCount.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            txtCount.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtCount.setText(String.valueOf(obj.getCount()));
            txtCount.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tr.addView(txtCount);
            mLinearLayout.addView(tr);

        }
    }

    private void callDashboardFormAPI() {
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            callFormFieldApi(mFormURL, mChartId, mTitle);
        } else {
            progressBar.setVisibility(View.GONE);
            buildChartUI(mChartId);
        }
    }

    private void callFormFieldApi(String dashboardFormUrl, final String chartId,
                                  final String title) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, dashboardFormUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            boolean formAvailable = mDbManager.checkIfFormExists(Integer.parseInt(chartId));
                            if (formAvailable) {
                                mDbManager.updateForm(Integer.parseInt(chartId), response);
                            } else {
                                mDbManager.insertForm(Integer.parseInt(chartId), title, response);
                            }
                        }
                        buildChartUI(chartId);
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

    private void buildChartUI(String chartId) {
        String response = mDbManager.getFormJson(Integer.parseInt(chartId));

        try {
            mChartObj = new Chart();
            JSONObject jsonObject = new JSONObject(response);
            mChartObj.setDataUrl(jsonObject.getString("dataurl"));
            mChartObj.setFilter(jsonObject.getString("filter"));
            mChartObj.setExtraParams(jsonObject.getString("extraparams"));
            mChartObj.setBlocks(jsonObject.getInt("blocks"));
            mChartObj.setChartType(jsonObject.getString("charttype"));
            mChartObj.setMobileBlocks(jsonObject.getInt("mobileblocks"));
            mChartObj.setChartTitle(jsonObject.getString("charttitle"));
            mChartObj.setJson(jsonObject.getString("json"));
            mChartObj.setId(jsonObject.getString("id"));

            callChartDataAPI("", false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callChartDataAPI(String filterValue, boolean filter) {

        String url = mChartObj.getDataUrl();
        url = url.replaceAll("['\\s+]", "");
        url = url.replace("window.baseurl", mPrefManager.getClientServerUrl());
        url = url.replace("window.cloudcode", mPrefManager.getCloudCode());
        url = url.replace("window.token", mPrefManager.getAuthToken());

        if (filter) {
            String extraParams = mChartObj.getExtraParams();
            String result = extraParams.split("&valuestring")[0];

            extraParams = result + "&valuestring=" + filterValue;
            url = url + "&" + extraParams;
         //   Log.e(DEBUG_TAG, "url with filtervalues = " + url);
        }

//        Log.e(DEBUG_TAG, "CHART DATA API FINAL URL = " + url);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "LeadByStatusAPI Response=" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<ChartDataItem> chartDataItemList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ChartDataItem obj = new Gson().fromJson(jsonObject.toString(), ChartDataItem.class);
                                chartDataItemList.add(obj);
                            }
                            mChartObj.setChartDataItemList(chartDataItemList);
                            showTable();
                            progressBar.setVisibility(View.GONE);
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
    }

    private void showHeader() {

        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView txtSource = new TextView(getActivity());
        txtSource.setPadding(16, 16, 16, 16);
        txtSource.setTextSize(16);
        txtSource.setTypeface(typefaceSemiBold);
        txtSource.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        txtSource.setTextColor(getResources().getColor(R.color.colorPrimary));
        txtSource.setText("Source");
        txtSource.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(txtSource);

        TextView txtCount = new TextView(getActivity());
        txtCount.setPadding(16, 16, 16, 16);
        txtCount.setTextSize(16);
        txtCount.setTypeface(typefaceSemiBold);
        txtCount.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        txtCount.setTextColor(getResources().getColor(R.color.colorPrimary));
        txtCount.setText("Count");
        txtCount.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(txtCount);
        mLinearLayout.addView(tr);
    }

    @Override
    public void onClick(View v) {
        if (v == fabFilter) {
            FilterBottomSheetFragment bottomSheetFragment = new FilterBottomSheetFragment(this);
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        }
    }

    @Override
    public void sendFilterValues(String title, String value) {
       // Log.e(DEBUG_TAG, "sendFilterValues called");
       // Log.e(DEBUG_TAG, "Title - " + title + " Value = " + value);

        if (NetworkUtil.isNetworkOnline(getActivity())) {
            callChartDataAPI(value, true);
        } else {
            DialogUtil.showAlertDialog(getActivity(), "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }
    }
}
