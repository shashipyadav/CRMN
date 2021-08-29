package com.example.myapplication.user_interface.upcoming_meeting.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.user_interface.upcoming_meeting.controller.MapListAdapter;
import com.example.myapplication.user_interface.upcoming_meeting.model.MapModel;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class UpcomingMeetingFragment extends Fragment {
    private RecyclerView recylerview_map;
    private ProgressDialog mWaiting;
    private List<MapModel> mapModelList = new ArrayList<>();
    private FloatingActionButton material_map, material_type;
    private MapListAdapter adapter;
    LinearLayout llNoItemsView;
    String title = "";
    private TextView txtNoItemsText;
    private ImageView noItemsImg;

    public UpcomingMeetingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            Bundle bundle = getArguments();
            title = bundle.getString(Constant.EXTRA_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_upcoming_meeting, container, false);
        init(root);
        setTitle();
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            callMapAPI();
        } else {
            DialogUtil.showAlertDialog(getActivity(),
                    getResources().getString(R.string.no_internet_title),
                    getResources().getString(R.string.no_internet_message),
                    false,
                    false);
        }

        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callMapAPI(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        return root;
    }

    private void callMapAPI() {
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            mapListAPI();
        } else {
            DialogUtil.showAlertDialog(getActivity(), "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }
    }

    private void mapListAPI() {
        if(mapModelList != null){
            mapModelList.clear();
        }
        showProgressDialog();
        SharedPrefManager mPrefManager = new SharedPrefManager(getActivity());
        String url  = String.format(Constant.MAP_LIST_URL,mPrefManager.getClientServerUrl(), mPrefManager.getUserName(),mPrefManager.getCloudCode(),mPrefManager.getAuthToken());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            Log.e(getClass().getSimpleName(), "callAPI Response=" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                MapModel taskObj = new MapModel();

                                JSONObject jobj = jsonArray.getJSONObject(i);
                                taskObj.setName(jobj.getString("company_name"));
                                taskObj.setAddress(jobj.getString("address"));
                                taskObj.setLatitude(jobj.getString("latitude"));
                                taskObj.setLongitude(jobj.getString("longitude"));
                                taskObj.setCountry(jobj.getString("country"));
                                taskObj.setState(jobj.getString("state"));
                                taskObj.setPhone(jobj.getString("mobile"));
                                taskObj.setEmail(jobj.getString("email"));
                                taskObj.setWebSite(jobj.getString("website"));
                                taskObj.setDistict(jobj.getString("city"));
                                taskObj.setDealer(jobj.getString("busi_type"));
                                taskObj.setType(jobj.getString("busi_class"));
                                taskObj.setPincode(jobj.getString("pincode"));
                                taskObj.setCust_code(jobj.getString("cust_code"));
                                taskObj.setContact_person(jobj.getString("contact_person"));
                                mapModelList.add(taskObj);

                            }
                            callAdapter(mapModelList);
                            hideProgressDialog();
                        } catch (Exception e) {
                            callAdapter(mapModelList);
                            e.printStackTrace();

                            hideProgressDialog();
                            //   Log.e(TAG, "callAPI Error Response= "+  response);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callAdapter(mapModelList);
                Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        });
        queue.add(request);
    }

    private void callAdapter(List<MapModel> mapModelList) {
        if(mapModelList.isEmpty()){
            adapter = new MapListAdapter(mapModelList, getActivity());
            recylerview_map.setAdapter(adapter);
            llNoItemsView.setVisibility(View.VISIBLE);
        }else{
            llNoItemsView.setVisibility(View.GONE);
            adapter = new MapListAdapter(mapModelList, getActivity());
          //  recylerview_map.setHasFixedSize(true);
            recylerview_map.setLayoutManager(new LinearLayoutManager(getActivity()));
            recylerview_map.setAdapter(adapter);
        }
    }

    private void init(View root) {
        llNoItemsView = root.findViewById(R.id.no_items_view);
        txtNoItemsText = root.findViewById(R.id.txt_msg);
        txtNoItemsText.setText("No data to display");
        noItemsImg = root.findViewById(R.id.no_items_img);
        noItemsImg.setImageResource(R.drawable.ic_list);
        recylerview_map = root.findViewById(R.id.recylerview_map);
        recylerview_map.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        EditText searchField = root.findViewById(R.id.iv_filter);
        material_map =  root.findViewById(R.id.material_map);
        material_type =  root.findViewById(R.id.material_type);

        material_map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                List<MapModel> stList = ((MapListAdapter) adapter).getListdata();

                Intent intent = new Intent(getActivity(), MapViewPlacesActivity.class);
                intent.putExtra("mapList", JsonUtil.listObjectToJsonArray(stList));
                startActivity(intent);
            }
        });


        material_type.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapFilterActivity.class);
                intent.putExtra("filterData", "busi_class");
                startActivityForResult(intent, 200);

            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

    }

    private void setTitle() {
        getActivity().setTitle(title);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {

            try {
                String dataFilter = data.getStringExtra("filterData");
                Log.e("#296",dataFilter);
                List<MapModel> mapModelList2 = new ArrayList<MapModel>();
                List<String> mapModelList1 = (List<String>) JsonUtil.jsonArrayToListObject(dataFilter, String.class);

                for (int j = 0; j < mapModelList.size(); j++) {
                    boolean isExist = false;
                    for (int i = 0; i < mapModelList1.size(); i++) {
                        String datafi = mapModelList1.get(i);
                        if (mapModelList.get(j).getDealer().contains(mapModelList1.get(i)) || datafi.equalsIgnoreCase(mapModelList.get(j).getType()) || datafi.equalsIgnoreCase(mapModelList.get(j).getDistict()) || datafi.equalsIgnoreCase(mapModelList.get(j).getCountry()) || datafi.equalsIgnoreCase(mapModelList.get(j).getState())) {
                            isExist = true;
                        }
                    }
                    if (isExist) {
                        mapModelList2.add(mapModelList.get(j));
                    }
                }
                callAdapter(mapModelList2);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void filter(String text) {
        List<MapModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (MapModel s : mapModelList) {
            //if the existing elements contains the search input
            if (s.getCountry().toLowerCase().contains(text.toLowerCase())
                    || s.getState().toLowerCase().contains(text.toLowerCase())
                    || s.getDistict().toLowerCase().contains(text.toLowerCase())
                    || s.getDealer().toLowerCase().contains(text.toLowerCase())
                    || s.getType().toLowerCase().contains(text.toLowerCase())
            || s.getName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        mapModelList = null;
        super.onDestroyView();
    }
}


