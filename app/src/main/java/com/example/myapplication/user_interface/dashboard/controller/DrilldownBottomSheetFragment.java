package com.example.myapplication.user_interface.dashboard.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dashboard.model.Drilldown;
import com.example.myapplication.user_interface.dashboard.view.DrilldownAdapter;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.VolleyErrorUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrilldownBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrilldownBottomSheetFragment extends BottomSheetDialogFragment {

    private List<Drilldown> mDrilldownList = new ArrayList<>();
    private String DEBUG_TAG = DrilldownBottomSheetFragment.class.getSimpleName();
    private  DrilldownAdapter adapter;
    private RecyclerView mRecyclerView;

    private String mKey = "";
    private String mChartUrl = "";
    private String mDrillDownTitle = "";

    public DrilldownBottomSheetFragment() {
        // Required empty public constructor
    }

    public static DrilldownBottomSheetFragment newInstance(String drillDownTitle,String key, String dataUrl) {
        DrilldownBottomSheetFragment fragment = new DrilldownBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(Constant.EXTRA_DRILL_DOWN_TITLE,drillDownTitle);
        args.putString(Constant.EXTRA_KEY, key);
        args.putString(Constant.EXTRA_URL, dataUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDrillDownTitle = getArguments().getString(Constant.EXTRA_DRILL_DOWN_TITLE);

            mKey = getArguments().getString(Constant.EXTRA_KEY);
            mChartUrl = getArguments().getString(Constant.EXTRA_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_drilldown_bottom_sheet, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));

        if (NetworkUtil.isNetworkOnline(getActivity())) {

            drillDownAPI();
        }else{
            DialogUtil.showAlertDialog(getActivity(), "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }

        return root;
    }

    /**
     * API to get drill down data when clicked on a particular area of a chart
     *
     */
    private void drillDownAPI(){
        mDrilldownList.clear();
       // here mKey is the value we get from chart e.g India Mart
        mKey = mKey.replace("NA","");

        SharedPrefManager mPrefManager = new SharedPrefManager(getActivity());
     //   String chartURl = mChartObj.getDataUrl();
        mChartUrl = mChartUrl.replaceAll("['\\s+]","");
        mChartUrl = mChartUrl.replace("window.baseurl",mPrefManager.getClientServerUrl());
        mChartUrl = mChartUrl.replace("window.cloudcode",mPrefManager.getCloudCode());
        mChartUrl = mChartUrl.replace("window.token",mPrefManager.getAuthToken());

       // String clickValue = "Lead_Details_by_Source"+"@@"+ mKey+"@j@";
        Log.e("mDrillDownTitle",mDrillDownTitle);

        String clickValue = mDrillDownTitle.replace("_j_","") +"@@"+ mKey+"@j@";

        String url = mPrefManager.getClientServerUrl() + "getFunction.do?actn=getsqljsondata&sqlfieldid=45289&chartcolumns=&link=Lead%20No@37007&cloudcode=b2b&token=DNVEP9576C544LQG&clickvalue="+ clickValue;

     //   String url = mChartUrl +  clickValue;
        Log.e(DEBUG_TAG, "drillDownAPI URL = " + url);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "Response=" + response);

                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Drilldown obj = new Drilldown();
                                obj.setLeadBy(jsonObject.getString("Lead_By"));
                                obj.setOrganisationName(jsonObject.getString("Organization_Name"));
                                obj.setLeadEstimate(jsonObject.getString("Lead_Estimate"));
                                obj.setProduct(jsonObject.getString("Product"));
                                obj.setLeadNo(jsonObject.getString("Lead_No"));
                                obj.setResponsibleUser(jsonObject.getString("Responsible_User"));
                                obj.setLocation(jsonObject.getString("Location"));

                                mDrilldownList.add(obj);
                            }
                            callAdapter();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(getActivity() != null){
                    VolleyErrorUtil.showVolleyError(getActivity(), error);
                }
            }
        });
        queue.add(request);
    }

    private void callAdapter(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }else{
            adapter = new DrilldownAdapter(getActivity(),mDrilldownList);
            mRecyclerView.setAdapter(adapter);
        }
    }
}