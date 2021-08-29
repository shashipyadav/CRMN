package com.example.myapplication.user_interface.login;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.BottomSheetDropdownAdapter;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetServerList extends BottomSheetDialogFragment
        implements BottomSheetServerAdapter.ServerInterface {

    private String mobileNo = "";
    private BottomSheetServerAdapter adapter;
    private RecyclerView recyclerView;
    List<Server> serverList;
    private ServerSelectedListener listener;

    public interface ServerSelectedListener  {
        public void onServerSelected();
    }

    public BottomSheetServerList(ServerSelectedListener mListener) {
        // Required empty public constructor
        this.listener = mListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mobileNo = getArguments().getString(Constant.EXTRA_MOBILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_server_list, container, false);
        setSearchEditText(view);
        setUpRecyclerView(view);
        callGetServerListAPI();
        return view;
    }

    private void setUpRecyclerView(View root){
        serverList = new ArrayList<>();
        recyclerView  = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
    }

    private void callGetServerListAPI() {
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            getServerListAPI();
        } else {
            DialogUtil.showAlertDialog(getActivity(),
                    getResources().getString(R.string.no_internet_title),
                    getResources().getString(R.string.no_internet_message),
                    false,
                    false);
        }
    }

    private void setSearchEditText(View root) {
        EditText etSearch = root.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter != null){
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getServerListAPI() {
        serverList.clear();
        String URL = String.format(Constant.URL_SERVER_LIST, mobileNo);
        Log.e(getClass().getSimpleName(), "getServerListAPI() " + URL);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(getClass().getSimpleName(), "callAPI Response=" + response);
                        try {
                            Gson gson = new Gson();
                            serverList = gson.fromJson(response, new TypeToken<List<Server>>(){}.getType());
                            callAdapter();
                            if (response.isEmpty()) {
                                ToastUtil.showToastMessage(getResources().getString(R.string.mobile_not_found), getActivity());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    public void callAdapter(){
        adapter = new BottomSheetServerAdapter( getActivity(),serverList,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(String value) {
        listener.onServerSelected();
        Toast.makeText(getActivity(),"Selected Server: " + value ,Toast.LENGTH_LONG ).show();
        dismiss();
    }
}