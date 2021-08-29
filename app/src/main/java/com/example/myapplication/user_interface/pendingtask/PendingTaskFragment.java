package com.example.myapplication.user_interface.pendingtask;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingTaskFragment extends Fragment {

    private static final String TAG = PendingTaskFragment.class.getName();
    //Widgets
    private LinearLayout llNoItemsView;
    private TextView txtTaskCount;
    private EditText etSearch;
    private ProgressDialog mWaiting;
    private SharedPrefManager mPrefManager;
    private boolean isLoading =false;
    private ImageView noItemImage;
    private TextView noItemTextView;

    //RecyclerView
    private RecyclerView recyclerView;
    private PendingTaskAdapter mAdapter;
    private List<Task> pendingTaskList;

    int pageNumber = 1;
    private Boolean loading = true;

    int pastVisibleItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;

    public PendingTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pending_task, container, false);
        initViews(root);

        BottomNavigationView bottomNavigationView= getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.b_nav_pending_task).setChecked(true);

        if (NetworkUtil.isNetworkOnline(getActivity())) {
            callGetChartIdsAPI();
        } else {
//            DialogUtil.showAlertDialog(getActivity(), getResources().getString(R.string.no_internet_title),
//                    getResources().getString(R.string.no_internet_message),
//                    false,
//                    false);
            showNoInternetImage();
        }
        return root;
    }

    private void initViews(View root) {
        mPrefManager = new SharedPrefManager(getActivity());
        setTitle();
        initText(root);
        setSearchEditText(root);
       // setRecyclerView(root);
        pendingTaskList = new ArrayList<>();


        recyclerView = root.findViewById(R.id.recyclerview);
        mAdapter = new PendingTaskAdapter(getActivity(), recyclerView,pendingTaskList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.v("scrollll","state changed");

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                if (!loading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true;
                        pageNumber = pageNumber + 1;
                        callPendingTasksAPI(pageNumber,false,false);
                        Log.d("page", "onScrolled: " + pageNumber);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
               /* if (dy > 0) {
                    int totalCount = mLayoutManager.getItemCount();
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalCount) {
                            Log.v("scroll","scrolled"+pastVisibleItems+pageNumber);
                        }
                    }

                }*/
            }
        });
      /*  recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = true;
                            pageNumber = pageNumber + 1;
                            callPendingTasksAPI(pageNumber,false,false);
                            Log.d("page", "onScrolled: " + pageNumber);
                        }
                    }
                }
            }
        });*/


    }

    private void setTitle(){
        getActivity().setTitle(getResources().getString(R.string.pending_task));
    }

    @Override
    public void onResume() {
        super.onResume();
        callPendingTasksAPI(1,true,true);
    }

    private void initText(View root) {
        txtTaskCount = root.findViewById(R.id.txt_task_count);
        llNoItemsView = root.findViewById(R.id.no_items_view);
        noItemImage = root.findViewById(R.id.no_items_img);
        noItemTextView = root.findViewById(R.id.txt_msg);
    }

    private void setSearchEditText(View root) {
        etSearch = root.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mAdapter != null){
                    mAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void setRecyclerView(View root) {
        pendingTaskList = new ArrayList<>();
       /* recyclerView = root.findViewById(R.id.recyclerview);
         mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PendingTaskAdapter(getActivity(), recyclerView,pendingTaskList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));*/


        recyclerView = root.findViewById(R.id.recyclerview);
        mAdapter = new PendingTaskAdapter(getActivity(), recyclerView,pendingTaskList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
       // mAdapter.setOnLoadMoreListener(onLoadMoreListener);




    }

    OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //Here adding null object to last position,check the condition in getItemViewType() method,if object is null then display progress
                    if(!pendingTaskList.isEmpty()){
                        pendingTaskList.add(null);
                        mAdapter.notifyItemInserted(pendingTaskList.size() - 1);
                    }

                    // Call you API, then update the result into dataModels, then call adapter.notifyDataSetChanged().
                    //Update the new data into list object
                    pageNumber++;
                    callPendingTasksAPI(pageNumber,false,false);

                }
            });
        }
    };

    private void callGetChartIdsAPI() {
        String url = String.format(Constant.URL_CHART_ID,mPrefManager.getClientServerUrl(),mPrefManager.getCloudCode(),mPrefManager.getAuthToken());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        Log.d("chart ids api url", url);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Iterator<String> iterator = jsonObject.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    mPrefManager.putString(key, jsonObject.optString(key));
                                }
                            }

                        } catch (Exception e) {
                            callAdapter();
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callAdapter();
                hideProgressDialog();
            }
        });
        queue.add(request);
    }

    private void callAdapter() {
        if (pendingTaskList == null) {
            llNoItemsView.setVisibility(View.VISIBLE);
            noItemTextView.setText(R.string.no_result_found);
            noItemImage.setImageResource(R.drawable.ic_list);

        } else {
            llNoItemsView.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
            loading = false;

        }
    }

    private void setTaskCount(String count) {
        txtTaskCount.setText(count);
    }

    private void callPendingTasksAPI(int pgNumber,boolean showDialog,boolean clearTasks) {
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            pendingTasksAPI(pgNumber,showDialog,clearTasks);
        } else {
            showNoInternetImage();
//            DialogUtil.showAlertDialog(getActivity(), "No Internet Connection!",
//                    "Please check your internet connection and try again", false, false);
        }
    }

    private void showNoInternetImage() {
        llNoItemsView.setVisibility(View.VISIBLE);
        noItemTextView.setText(R.string.no_internet_title);
        noItemImage.setImageResource(R.drawable.ic_no_internet);
    }

    private void pendingTasksAPI(int pgNumber, final boolean showDialog,boolean clearTasks) {
        if(showDialog){
            pendingTaskList.clear();
            showProgressDialog();
        }

        String url = String.format(Constant.PENDING_TASKS_URL,
                mPrefManager.getClientServerUrl(),
                mPrefManager.getCloudCode(),
                String.valueOf(pgNumber),
                mPrefManager.getAuthToken());

        Log.e(TAG,"PENDING TASK URL = " +url);
        //https://9.strategicerpcloud.com/strategicerp/getFunction.do?actn=getsqljsondata&sqlfieldid=57245&cloudcode=realestatemedium&ids=recordsize/pagenumber&valuestring=20@j@1&token=U3ZXBYN7JFQSPK35&type=json

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       //Log.e(TAG, "callAPI Response=" + response);
                        try {
                            if(!showDialog){
                                pendingTaskList.remove(pendingTaskList.size() - 1);
                                mAdapter.notifyItemRemoved(pendingTaskList.size());
                            }

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jobj = jsonArray.getJSONObject(i);
                                Task taskObj = new Gson().fromJson(jobj.toString(), Task.class);
                                pendingTaskList.add(taskObj);
                            }
                            setTaskCount(String.valueOf(pendingTaskList.size()));
                            callAdapter();

                            hideProgressDialog();
                            if(!showDialog){
                                mAdapter.setLoading();
                                if(response.contains("[]")){
                                 //   Toast.makeText(getActivity(), "No More Tasks To Load", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            callAdapter();
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callAdapter();
               hideProgressDialog();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    private void showProgressDialog(){
        mWaiting = ProgressDialog.show(getActivity(), "",
                "Loading...", false);
    }

    private void hideProgressDialog(){
        if (mWaiting != null) {
            mWaiting.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        mAdapter = null;
        pendingTaskList = null;
        mPrefManager = null;
        super.onDestroyView();

    }
}
