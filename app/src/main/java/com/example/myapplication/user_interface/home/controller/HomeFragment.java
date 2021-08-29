package com.example.myapplication.user_interface.home.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.home.model.HomeModel;
import com.example.myapplication.user_interface.home.model.HomeSectionModel;
import com.example.myapplication.user_interface.home.view.MultipleTypeImageAdapter;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.customviews.NotScrollingToFocusedChildrenLinearLayoutManager;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String DEBUG_TAG = HomeFragment.class.getSimpleName();
    private List<HomeModel> homemainModelList = new ArrayList<>();
    private List<HomeModel> list;
    private ProgressDialog  mWaiting;
    private RecyclerView  recyclerView;
    private LinearLayout llNoItemsView;
    private ImageView noItemImage;
    private TextView noItemTextView;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initView(root);
        callHomeAPI();
        return root;
    }

    private void setTitle() {
        getActivity().setTitle(getResources().getString(R.string.menu_home));
    }

    private void initView(View root) {
        setTitle();
        initRecyclerview(root);
        llNoItemsView = root.findViewById(R.id.no_items_view);
        noItemImage = root.findViewById(R.id.no_items_img);
        noItemTextView = root.findViewById(R.id.txt_msg);
    }

    private void initRecyclerview(View root){
        recyclerView = root.findViewById(R.id.rv_new_product);
        recyclerView.setHasFixedSize(true);
        //for smooth scroll when we have images in recyclerview
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        //rv_new_product.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    private void callHomeAPI(){
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            apiCall();
        }else{
            showNoInternetImage();
//            DialogUtil.showAlertDialog(getActivity(), getString(R.string.no_internet_title),
//                    getString(R.string.no_internet_message), false, false);
        }
    }

    private void apiCall() {
        showProgressDialog();
        SharedPrefManager mPrefManager = new SharedPrefManager(getActivity());
         String url  = String.format(Constant.HOME_URL, mPrefManager.getClientServerUrl(),mPrefManager.getCloudCode(),mPrefManager.getAuthToken());
            Log.e(DEBUG_TAG, "Home URL = " + url);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Log.e(DEBUG_TAG, "Home Response = " + response);
                            list = new ArrayList<>();
                            HomeModel obj =null;
                            List<HomeSectionModel> homeSectionModelList = new ArrayList<>();

                            JSONArray jsonArray = new JSONArray(response);
                            int  prevOrder = -1;
                            for (int i = 0; i < jsonArray.length(); i++) {

                                HomeModel homeModel = new HomeModel();
                                JSONObject jobj = jsonArray.getJSONObject(i);
                                homeModel.setOrder(jobj.getString("Order"));

                                int currentOrder = Integer.parseInt(jobj.getString("Order"));
                                if(prevOrder != currentOrder){
                                    //New order
                                    obj = new HomeModel();
                                    obj.setOrder(jobj.getString("Order"));

                                    homeSectionModelList = new ArrayList<>();
                                    HomeSectionModel homeSectionModel =  new Gson().fromJson(jobj.toString(), HomeSectionModel.class);
                                    homeSectionModelList.add(homeSectionModel);
                                    obj.setProductList(homeSectionModelList);
                                    list.add(obj);
                                    prevOrder = currentOrder;
                                }else{

                                    HomeSectionModel homeSectionModel =  new Gson().fromJson(jobj.toString(), HomeSectionModel.class);
                                    homeSectionModelList.add(homeSectionModel);
                                    obj.setProductList(homeSectionModelList);
                                    prevOrder = currentOrder;
                                }
                                homemainModelList.add(homeModel);
                            }
                            hideProgressDialog();
                            callAdapternewProductModelList();

                        } catch (Exception e) {
                            callAdapternewProductModelList();
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callAdapternewProductModelList();
                hideProgressDialog();
            }
        });
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT,
                Constant.NO_OF_TRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void callAdapternewProductModelList() {
        try {

            if (list == null) {
                llNoItemsView.setVisibility(View.VISIBLE);
                noItemTextView.setText(R.string.no_result_found);
                noItemImage.setImageResource(R.drawable.ic_list);
            } else {
                llNoItemsView.setVisibility(View.GONE);
                MultipleTypeImageAdapter adapter = new MultipleTypeImageAdapter(getActivity(), list);
                recyclerView.setHasFixedSize(true);
                NotScrollingToFocusedChildrenLinearLayoutManager layoutManager = new NotScrollingToFocusedChildrenLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        mWaiting = ProgressDialog.show(getActivity(), "",
                "Loading...", false);
    }

    private void hideProgressDialog() {
        if (mWaiting != null) {
            mWaiting.dismiss();
        }
    }

    private void showNoInternetImage() {
        llNoItemsView.setVisibility(View.VISIBLE);
        noItemTextView.setText(R.string.no_internet_title);
        noItemImage.setImageResource(R.drawable.ic_no_internet);
    }
}
