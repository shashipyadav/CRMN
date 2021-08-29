package com.example.myapplication.user_interface.dashboard.view;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.customviews.DatePickerDialogFragment;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dashboard.controller.FilterMoreAdapter;
import com.example.myapplication.user_interface.dashboard.controller.FilterMoreInterface;
import com.example.myapplication.user_interface.dashboard.model.FilterMoreModel;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.VolleyErrorUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, FilterMoreInterface {

    private static final String DEBUG_TAG = FilterBottomSheetFragment.class.getSimpleName();
    private TextView txtTitle;
    private EditText etStartDate, etEndDate;
    boolean isStartDateClicked = false;
    private Button btnCancel,btnFilter,btnMore;
    private SharedPrefManager mPrefManager;
    private List<String> nameOfCompanyList = new ArrayList<>();
    private List<String> projectList = new ArrayList<>();
    private LinearLayout llDefaultFilter, llMoreFilter;
    private Spinner spinnerNameOfComp,spinnerProjects;
    private String title = "";
    private FilterBottomSheetClickListener mListener;
    private String nameOfCompany = "%",project = "%",startDate = "",endDate = "";
    private String mMoreFilter = "";
    private String mExtraParams = "";
    FilterMoreAdapter adapter;
    RecyclerView recyclerView;
    int fetchCounter = 0;
    List<FilterMoreModel> filterMoreList = new ArrayList<>();
    ProgressDialog progressDialog1;

    @Override
    public void dateClickListener(int position) {
        displayDatePicker(position);
    }

    @Override
    public void loadSpinnerData(int position) {

        if (NetworkUtil.isNetworkOnline(getActivity())) {
            loadDropdownData(position);
        } else {
            DialogUtil.showAlertDialog(getActivity(), "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }

    }

    @Override
    public void onValueChanged(int position, String value) {
        filterMoreList.get(position).setValue(value);
      //  adapter.notifyItemChanged(position);

        Log.e(DEBUG_TAG,"updated spinner value"+value);
    }

    public interface FilterBottomSheetClickListener{
         void sendFilterValues(String title,String value);
    }

    public FilterBottomSheetFragment(FilterBottomSheetClickListener listener){
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            title = args.getString(Constant.EXTRA_TITLE);
            mMoreFilter = args.getString(Constant.EXTRA_MORE);
            mExtraParams = args.getString(Constant.EXTRA_PARAMS);
        }

    }


    private void displayDatePicker(final int position) {
        DialogFragment dialogFragment = new DatePickerDialogFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat(Constant.yyyy_MM_dd);
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                String currentDateandTime = sdf.format(cal.getTime());
                filterMoreList.get(position).setValue(currentDateandTime);
                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    });
                }
                //Calendar.getInstance()
            }
        }, null, null, getActivity());

        if (dialogFragment.isAdded()) {
            return;
        } else {
            dialogFragment.show(getChildFragmentManager(), "Date");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            callNameCompanyAPI();
            callProjectsAPI();
        } else {
            DialogUtil.showAlertDialog(getActivity(), "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View root){
        mPrefManager = new SharedPrefManager(getActivity());
        txtTitle = root.findViewById(R.id.title);
        if(title != null){
            StringBuilder builder = new StringBuilder(title);
            builder.append(" Filter Results");
            txtTitle.setText(builder);
        }

        etStartDate = root.findViewById(R.id.edit_start_date);
        etStartDate.setText(DateUtil.getFYStartDate());
        etStartDate.setOnClickListener(this);

        etEndDate = root.findViewById(R.id.edit_end_date);
        etEndDate.setText(DateUtil.getFYEndDate());
        etEndDate.setOnClickListener(this);
        btnCancel = root.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnFilter = root.findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(this);
        btnMore = root.findViewById(R.id.btn_more);
        btnMore.setOnClickListener(this);
        if(!mMoreFilter.equals("")){
            btnMore.setVisibility(View.VISIBLE);
        }
        spinnerProjects = root.findViewById(R.id.spinner_projects);
        spinnerProjects.setOnItemSelectedListener(this);
        spinnerNameOfComp = root.findViewById(R.id.spinner_company);
        spinnerNameOfComp.setOnItemSelectedListener(this);
        llDefaultFilter = root.findViewById(R.id.default_filter);
        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(!mMoreFilter.equals("")){
            createDynamicMoreFilter(root);
        }
    }

    private void createDynamicMoreFilter(View root){
        String[] jarr = mMoreFilter.split("@c@");
        for(int i=0; i < jarr.length; i++){
            //45938#End Date::$enddate
            String[] fieldArr= jarr[i].split("#");
            String fieldId = fieldArr[0];
            String[] tarr = fieldArr[1].split("::");
            String fieldName = tarr[0];
            String dataType = tarr[1];

            if(dataType.toLowerCase().contains("date")){
                dataType = "date";
            }

            FilterMoreModel moreFilterField = new FilterMoreModel(fieldId,fieldName,dataType);
            filterMoreList.add(moreFilterField);
        }
        adapter = new FilterMoreAdapter(getActivity(), filterMoreList,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v == etStartDate){
            isStartDateClicked = true;
            displayDatePicker();
        }

        if(v == etEndDate){
            isStartDateClicked = false;
            displayDatePicker();
        }

        if(v == btnCancel){
            dismiss();
        }

        if(v == btnFilter){
            //call interface
            filter();
            dismiss();
        }

        if(v == btnMore){
            String title = btnMore.getText().equals("More")? "Back" : "More";
            btnMore.setText(title);
            if(title.equals("Back")){
                llDefaultFilter.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }else{
                llDefaultFilter.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void filter(){
        //More Filter id
        String moreFieldIds = "";
        //more filter value string
        String moreFieldValues = "";

        //loop through to get more field ids  and values
        for(int i =0; i<filterMoreList.size(); i++){
            FilterMoreModel obj = filterMoreList.get(i);
            moreFieldIds = moreFieldIds+obj.getFieldId()+"/";
            moreFieldValues = moreFieldValues+obj.getValue()+"@j@";
        }
        String ids = "";
        if(!mExtraParams.equals("")){
            String[] arr = mExtraParams.split("&");

            for(int i=0; i<arr.length; i++){
                //get the normal filter idstring
                if(arr[i].toLowerCase().contains("idstring")){
                    ids = arr[i];
                }
            }
        }
        //valuestring for the normal filter fields
        String value = nameOfCompany+"@j@"+project+"@j@"+startDate+"@j@"+endDate;

        //append normal and more filter ids
        String idString = ids +moreFieldIds;
        //append normal value and more filter values
        String valueString = value + "@j@" + moreFieldValues;

        Log.e(DEBUG_TAG,"idString = " + idString);
        Log.e(DEBUG_TAG,"valueString = "+valueString);

        //append idstring and valuestring so that we could pass it to the filter api
        String extraparams=idString+"&"+"valuestring="+valueString;

        Log.e(DEBUG_TAG,"extra params = "+ extraparams);

        mListener.sendFilterValues(title,extraparams);
    }

    private void displayDatePicker() {
        DialogFragment dialogFragment = new DatePickerDialogFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat(Constant.dd_MM_yyyy);
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                String currentDateandTime = sdf.format(cal.getTime());
                String formattedDate = DateUtil.formatDateTo_yyyyMMdd(currentDateandTime);

                if(isStartDateClicked){
                    etStartDate.setText(currentDateandTime);
                    startDate =  formattedDate;

                }else{
                    etEndDate.setText(currentDateandTime);
                    endDate = formattedDate;

                }
                //Calendar.getInstance()
            }
        }, null, null, getActivity());

        if (dialogFragment.isAdded()) {
            return;
        } else {
            dialogFragment.show(getChildFragmentManager(), "Date");
        }
    }

    private void callNameCompanyAPI(){
       //https://best-erp.com:9000/strategicerp/getFunction.do?actn=getsqljsondata&sqlfieldid=38675&token=DNVEP9576C544LQG&cloudcode=b2b&type=json
        String URL = String.format(Constant.URL_NAME_OF_COMPANY, mPrefManager.getClientServerUrl(),
                mPrefManager.getCloudCode(), mPrefManager.getAuthToken());

        Log.e("NAME OF COMPANY URL" , URL);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "NameCompanyAPI Response=" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i< jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                nameOfCompanyList.add(jsonObject.getString("Name_of_Company"));
                            }
                            nameOfCompanyList.add(0,"Select");
                            ArrayAdapter<String> companyAdapter = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_item, nameOfCompanyList);
                            companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            spinnerNameOfComp.setAdapter(companyAdapter);
                           // mWaiting.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
//                            if (mWaiting != null) {
//                                mWaiting.dismiss();
//                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorUtil.showVolleyError(getActivity(), error);
//                if (mWaiting != null) {
//                    mWaiting.dismiss();
//                }
            }
        });
        queue.add(request);
    }

    private void callProjectsAPI(){
        //https://best-erp.com:9000/strategicerp/getFunction.do?actn=getsqljsondata&sqlfieldid=35767&ids=nameofcompany&valuestring=%&token=DNVEP9576C544LQG&cloudcode=b2b&type=json
        String URL = String.format(Constant.URL_PROJECTS, mPrefManager.getClientServerUrl(),
        mPrefManager.getCloudCode(), mPrefManager.getAuthToken());
        Log.e("NAME OF PROJECT URL" , URL);
      //  String URL ="https://best-erp.com:9000/strategicerp/getFunction.do?actn=getsqljsondata&sqlfieldid=35767&ids=nameofcompany&valuestring=%&token=DNVEP9576C544LQG&cloudcode=b2b&type=json";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "ProjectAPI Response=" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i< jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                projectList.add(jsonObject.getString("Project_Name"));
                            }
                            projectList.add(0,"Select");
                            ArrayAdapter<String> projectAdapter = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_item, projectList);
                            projectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            spinnerProjects.setAdapter(projectAdapter);
                            // mWaiting.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
//                            if (mWaiting != null) {
//                                mWaiting.dismiss();
//                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorUtil.showVolleyError(getActivity(), error);
//                if (mWaiting != null) {
//                    mWaiting.dismiss();
//                }
            }
        });
        queue.add(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId() == R.id.spinner_company){
            if(!nameOfCompanyList.get(position).toString().equals("Select")){
            nameOfCompany = nameOfCompanyList.get(position).toString();
            }
        }
        if(parent.getId() == R.id.spinner_projects){

            if(!projectList.get(position).toString().equals("Select")){
                project = projectList.get(position).toString();
            }

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadDropdownData(final int position) {
        String fieldId = filterMoreList.get(position).getDataType().toLowerCase().replace("sql","");
        boolean isValidURL = false;
        SharedPrefManager prefManager = new SharedPrefManager(getActivity());
        String url = String.format(Constant.FILTER_MORE_DROPDOWN_URL,prefManager.getClientServerUrl(),fieldId,prefManager.getCloudCode(),prefManager.getAuthToken());

        if (url != null && !url.isEmpty()) {
            isValidURL = android.util.Patterns.WEB_URL.matcher(url).matches();
        }
        if(!isValidURL) {
            Log.e(DEBUG_TAG, "#line 402 , Bad URL = "+ url);
            return;
        }
        if(fetchCounter == 0){
            progressDialog1 = ProgressDialog.show(getActivity(), "", "Fetching Details ...", false);
        }
        fetchCounter++;
        Log.e(DEBUG_TAG, "More Filter Dropdown URL=" + url);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(DEBUG_TAG, "Drop down response=" + response);
                        String s = response.toString();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<String> list = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Iterator<String> keys = jsonObject.keys();

                                while (keys.hasNext()) {
                                    String keyValue = (String) keys.next();
                                    String valueString = jsonObject.getString(keyValue);
                                    list.add(valueString);
                                }

                            }
                            list.add(0,"Select");
                             refreshList(list,position);

                            fetchCounter--;
                            if(fetchCounter == 0){
                                if(progressDialog1 != null){
                                    progressDialog1.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            fetchCounter--;

                            if(fetchCounter == 0){
                                if(progressDialog1 != null){
                                    progressDialog1.dismiss();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                fetchCounter--;
                if(fetchCounter == 0){
                    if(progressDialog1 != null){
                        progressDialog1.dismiss();
                    }
                }
            }
        });
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT,
                Constant.NO_OF_TRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void refreshList(final List<String> list, final int position){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filterMoreList.get(position).setDropdownList(list);
                Log.e(DEBUG_TAG, "LINE #494 ADAPTER NOTIFYDATAITEMCHANGED");
                adapter.notifyItemChanged(position);
            }
        });
    }

}

