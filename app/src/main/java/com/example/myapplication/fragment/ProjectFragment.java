package com.example.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ProjectAdapter;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.model.ProjectModel;
import com.example.myapplication.network.VolleyResponseListener;
import com.example.myapplication.network.VolleyUtils;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.example.myapplication.util.JsonUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
import com.facebook.shimmer.ShimmerFrameLayout;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.function.CheckHideShowHelper.DEBUG_TAG;

/**
 * Created by shashikant on 8/12/19.
 */


public class ProjectFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<ProjectModel> projectModelList;
    private EditText searchView;
    private ProjectAdapter projectAdapter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private SharedPrefManager prefManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = (EditText) view.findViewById(R.id.searchView);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        prefManager = new SharedPrefManager(getActivity());


        projectModelList = new ArrayList<>();
        setList();
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.clearFocus();
               // Constants.hideKeyboard(MyTeamDirectoryActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });*/
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 &&  getActivity().findViewById(R.id.ll_search).getVisibility()==View.VISIBLE)

                {
                    //getActivity().findViewById(R.id.ll_search).setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    //getActivity().findViewById(R.id.ll_search).setVisibility(View.VISIBLE);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return view;
    }

    private void filter(String newText) {
        List<ProjectModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ProjectModel s : projectModelList) {
            //if the existing elements contains the search input
            if (s.getPROJECT().toLowerCase().contains(newText.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        projectAdapter.filterList(filterdNames);
    }
    private void setList() {
        if (NetworkUtil.isNetworkOnline(getActivity())) {

            String URL_GET= prefManager.getClientServerUrl()+"/pages/sqls/customerappsqls.jsp?action=projectlist&token="+  prefManager.getAuthToken()+"&cloudcode="+ prefManager.getCloudCode();
           /* String URL_GET = String.format(Constant.URL_SINGLE_CHART_ID,
                    prefManager.getClientServerUrl(),
                    VFORM_ID,
                    prefManager.getAuthToken(),
                    prefManager.getCloudCode());*/

            Log.e(DEBUG_TAG, "callGetChartIdApi url = " + URL_GET);

            VolleyUtils.GET_METHOD(getActivity(), URL_GET, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    System.out.println("Error" + message);
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(Object response) {
                    Log.e("SUCCESS" , (String) response);
                    try {
                        JSONArray jsonArray = new JSONArray((String) response);


                        for(int y =0; y<jsonArray.length(); y++){
                            ProjectModel projectModel = new ProjectModel();
                            JSONObject jObj = jsonArray.getJSONObject(y);
                            if(jObj.has("PROJECT_CODE")){
                                projectModel.setPROJECT_CODE(jObj.getString("PROJECT_CODE"));
                            }
                            if(jObj.has("PROJECT")){
                                projectModel.setPROJECT(jObj.getString("PROJECT"));
                            }
                            if(jObj.has("STATUS")){
                                projectModel.setSTATUS(jObj.getString("STATUS"));
                            }
                            if(jObj.has("STATUS")){
                                projectModel.setSTATUS(jObj.getString("STATUS"));
                            }
                            if(jObj.has("EXPECTED_DATE")){
                                projectModel.setEXPECTED_DATE(jObj.getString("EXPECTED_DATE"));
                            } if(jObj.has("IMAGE")){
                                projectModel.setIMAGE(jObj.getString("IMAGE"));
                            }
                            if(jObj.has("B_IMAGE")){
                                projectModel.setB_IMAGE(jObj.getString("B_IMAGE"));
                            }
                            if(jObj.has("CATEGORY")){
                                projectModel.setCATEGORY(jObj.getString("CATEGORY"));
                            }
                            if(jObj.has("LOCATION")){
                                projectModel.setLOCATION(jObj.getString("LOCATION"));
                            }if(jObj.has("RERA")){
                                projectModel.setRERA(jObj.getString("RERA"));
                            }if(jObj.has("OTPENABLE")){
                                projectModel.setOTPENABLE(jObj.getString("OTPENABLE"));
                            }


                            projectModelList.add(projectModel);
                        }
                        if (projectModelList != null && projectModelList.size() > 0) {
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            projectAdapter = new ProjectAdapter(getActivity(), projectModelList,prefManager);
                            recyclerView.setAdapter(projectAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(linearLayoutManager);
                        }


                    }catch (Exception e ){
                        e.printStackTrace();
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        new AlertDialog.Builder(getActivity())

                                .setMessage("Project not available")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {
                                    }
                                }).create().show();
                    }
                }
            });
        }else{
            ToastUtil.showToastMessage("Please check your internet connection and try again",getActivity());
        }
    }
/*
    private void setList() {

        final SharedPreferences sharedPref = getActivity().getSharedPreferences("customerpp", Context.MODE_PRIVATE);

        Api service = RetrofitClientInstance.getRetrofitInstance(sharedPref.getString("Client_Server_URL", "")).create(Api.class);
        Call<List<ProjectModel>> call = service.getProjectList(sharedPref.getString("auth_token", ""), sharedPref.getString("cloudCode", ""));
        */
/*final KProgressHUD hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100)
                .show();
        hud.setProgress(90);*//*

        call.enqueue(new Callback<List<ProjectModel>>() {
            @Override
            public void onResponse(Call<List<ProjectModel>> call, Response<List<ProjectModel>> response) {
                if (response.body() != null && !response.body().isEmpty()) {

                    projectModelList = response.body();
                    //Constants.projectModelList = response.body();
                    if (projectModelList != null && projectModelList.size() > 0) {
                         projectAdapter = new ProjectAdapter(getActivity(), projectModelList);
                        recyclerView.setAdapter(projectAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                    }

                } else {
                    new AlertDialog.Builder(getActivity())

                            .setMessage("Project not available")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            }).create().show();

                }
                //hud.dismiss();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<ProjectModel>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

               //hud.dismiss();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);


            }
        });

    }
*/


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
         getActivity().setTitle("Project");
        //((YourActivity) getActivity()).getSupportActionBar().setTitle("Your Title");

    }
    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

}
