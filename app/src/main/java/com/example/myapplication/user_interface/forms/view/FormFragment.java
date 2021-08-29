package com.example.myapplication.user_interface.forms.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.function.CheckPatternFunction;
import com.example.myapplication.function.CheckSaveHelper;
import com.example.myapplication.function.DateRangeFunction;
import com.example.myapplication.user_interface.dlist.controller.DListFieldHelper;
import com.example.myapplication.user_interface.dlist.controller.DlistFieldRecyclerAdapter;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.user_interface.dynamicbutton.DynamicButton;
import com.example.myapplication.user_interface.dynamicbutton.DynamicButtonAdapter;
import com.example.myapplication.function.CheckRepeatHelper;
import com.example.myapplication.function.EvaluateFunctionHelper;
import com.example.myapplication.location.CurrentLocation;
import com.example.myapplication.network.FetchCheckRepeatDataHelper;
import com.example.myapplication.user_interface.forms.controller.CommunicatorInterface;
import com.example.myapplication.user_interface.forms.controller.CustomDateTimePickerInterface;
import com.example.myapplication.user_interface.forms.controller.FormFieldInterface;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.user_interface.forms.controller.LocationInterface;
import com.example.myapplication.user_interface.forms.controller.OnBottomReachedListener;
import com.example.myapplication.user_interface.forms.controller.ResponseInterface;
import com.example.myapplication.user_interface.forms.controller.WorkFlowMandatory;
import com.example.myapplication.user_interface.forms.controller.helper.AttachFileHelpler;
import com.example.myapplication.user_interface.dynamicbutton.DynamicButtonHelper;
import com.example.myapplication.user_interface.forms.controller.helper.FetchRecordHelper;
import com.example.myapplication.user_interface.forms.controller.helper.FieldHelper;
import com.example.myapplication.user_interface.forms.controller.helper.ParseForm;
import com.example.myapplication.user_interface.forms.controller.helper.PrintHelper;
import com.example.myapplication.user_interface.forms.controller.helper.SaveRecordHelper;
import com.example.myapplication.user_interface.forms.controller.helper.ValidateFormHelper;
import com.example.myapplication.user_interface.forms.model.OptionModel;
import com.example.myapplication.function.FunctionHelper;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.pendingtask.BottomSheetStatusDialogFragment;
import com.example.myapplication.user_interface.quicklink.QuickLinkHelper;
import com.example.myapplication.Constant;
import com.example.myapplication.customviews.DatePickerDialogFragment;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.model.Form;
import com.example.myapplication.customviews.CustomDateTimePicker;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.FileUtil;
import com.example.myapplication.util.KeyboardUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.customviews.NotScrollingToFocusedChildrenLinearLayoutManager;
import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.util.VolleyErrorUtil;
//import com.facebook.shimmer.ShimmerFrameLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.example.myapplication.Constant.EXTRA_IS_DLIST;
import static com.example.myapplication.Constant.EXTRA_POSITION;
import static com.example.myapplication.Constant.PAYLOAD_TEXT;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormFragment extends Fragment implements FormFieldInterface,
        CustomDateTimePickerInterface {

    private final String DEBUG_TAG = FormFragment.class.getName();
    private static final int PERMISSION_REQUEST_CODE = 201;
    public static List<Field> fieldsList;
    public static List<Field> additionalFieldDataList; //using this list to store data such as mandatory, stateids etc
    private List<DynamicButton> buttonList;
    public static Form form = new Form();
    public static int dlistArrayPosition = -1; //using this to get the position of the field object which has the dlist array(dlist array consists of all the different dlist)

    private CustomDateTimePicker custom;
    private DynamicButtonHelper dynamicButtonHelper;

    private int fileFieldPosition = -1;
    private int fetchCounter = 0;

    public static String RECORD_ID_REF = "0";
    private String FORM_ID = "";
    private String RECORD_ID = "0";
    private String chartId = "";
    private String entryFrom = "";
    private String title = "";
    private String printUrl = "";

    private ProgressDialog validateProgressDialog;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;

    private RecyclerView recyclerviewForm;
    private RecyclerView recyclerViewDynButton;
    private DynamicButtonAdapter buttonAdapter;
    public static FormRecylerAdapter adapter;
    private LinearLayout llNoItemsView;
    private LinearLayout llRecyclerViewRoot;
    private ScrollView mScrollView;
    private ShimmerFrameLayout shimmerFrameLayout;

    private SharedPrefManager mPrefManager;
    private DatabaseManager dbManager;

    private FunctionHelper functionHelper;
    private QuickLinkHelper quickLinkHelper;
    private AttachFileHelpler attachFileHelpler;
    private ValidateFormHelper validateHelper;
    private CheckSaveHelper checkSaveHelper;
    private CommunicatorInterface communicatorListener;
    private boolean isInputInTagField = false;
    private RelativeLayout relBackgroundLayout;
    private ImageView noItemImage;
    private TextView noItemTextView;
    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgument();
        if (entryFrom == null) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_quick_link, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            if (quickLinkHelper.saveQuickLink()) {
                ToastUtil.showToastMessage(getResources()
                        .getString(R.string.quicklink_removed_message), getActivity());
            } else {
                ToastUtil.showToastMessage(getResources()
                                .getString(R.string.quicklink_saved_message),
                        getActivity());
            }
            getActivity().invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);
        init(view);
        initDatabase();
        initRecyclerView(view);
        loadForm();


        return view;
    }

    private void getArgument() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            title = bundle.getString(Constant.EXTRA_TITLE);
            chartId = bundle.getString(Constant.EXTRA_CHART_ID);
            entryFrom = bundle.getString(Constant.EXTRA_ENTRY);

            if (entryFrom != null && entryFrom.equals("pending_task")) {
                RECORD_ID = bundle.getString(Constant.EXTRA_RECORD_ID);
                FORM_ID = bundle.getString(Constant.EXTRA_FORM_ID);
            }
        }
        setQuickLinkHelper();
    }

    private void setQuickLinkHelper() {
        quickLinkHelper = new QuickLinkHelper(getActivity());
        quickLinkHelper.setTitle(title);
        quickLinkHelper.setChartId(chartId);
    }

    private void init(View root) {
        initShimmer(root);
        initViews(root);
        setTitle();
        initList();
        initSharedPref();
        initButtonAdapter(root);
        initDynamicHelper();
        initHelperClasses();
        initCheckSaveHelper();
        gson = new Gson();
    }

    private void initShimmer(View root) {
        shimmerFrameLayout = root.findViewById(R.id.shimmerFrameLayout);
    }

    private void initViews(View root) {
        relBackgroundLayout = root.findViewById(R.id.rel_bg_layout);
        mScrollView = root.findViewById(R.id.scrollview);
        llNoItemsView = root.findViewById(R.id.no_items_view);
        llRecyclerViewRoot = root.findViewById(R.id.ll_recylerview_form);
        noItemImage = root.findViewById(R.id.no_items_img);
        noItemTextView = root.findViewById(R.id.txt_msg);
    }

    private void initCheckSaveHelper() {
        checkSaveHelper = new CheckSaveHelper(getActivity());
        checkSaveHelper.setVlist(false);
        checkSaveHelper.setFieldsList(fieldsList);
        checkSaveHelper.setAdditionalFieldDataList(additionalFieldDataList);
    }

    private void initDynamicHelper() {
        dynamicButtonHelper = new DynamicButtonHelper(getActivity(), mResponseInterface);
        dynamicButtonHelper.setVlist(false);
    }

    private void initSharedPref() {
        mPrefManager = new SharedPrefManager(getActivity());
    }

    private void initList() {
        fieldsList = new ArrayList<>();
        additionalFieldDataList = new ArrayList<>();
    }

    private void setTitle() {
        if (getActivity() != null) {
            getActivity().setTitle(title);
        }
    }

    private void initHelperClasses() {
        functionHelper = new FunctionHelper(getActivity());
        attachFileHelpler = new AttachFileHelpler(getActivity());
        validateHelper = new ValidateFormHelper(getActivity(), false);
    }

    private void initButtonAdapter(View root) {
        buttonList = new ArrayList<>();
        recyclerViewDynButton = root.findViewById(R.id.recyclerview_buttons);
        recyclerViewDynButton.setLayoutManager(new GridLayoutManager(getActivity(),
                3));
        buttonAdapter = new DynamicButtonAdapter(getActivity(), buttonList,
                mDynamicButtonClickListener);
        recyclerViewDynButton.setAdapter(buttonAdapter);
    }

    private void initDatabase() {
        dbManager = new DatabaseManager(getActivity());
        dbManager.open();
    }

    public void initRecyclerView(View root) {
        recyclerviewForm = root.findViewById(R.id.recylerview_form);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // recyclerviewForm.setHasFixedSize(true);
        // Stops items doing a default flashing animation on individual refresh
        RecyclerView.ItemAnimator animator = recyclerviewForm.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        NotScrollingToFocusedChildrenLinearLayoutManager llm = new NotScrollingToFocusedChildrenLinearLayoutManager(getActivity());
        llm.setAutoMeasureEnabled(false);
        recyclerviewForm.setLayoutManager(llm);

    }

    private void displayDatePicker(final int position, final String onChange) {
        DialogFragment dialogFragment = new DatePickerDialogFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //changed format from yyyy_MM_dd to yyyy_MM_dd_HH_mm 07 dec 2020
                //changed format from yyyy_MM_dd_HH_mm to yyyy_MM_dd 12 Feb  20211
                SimpleDateFormat sdf = new SimpleDateFormat(Constant.yyyy_MM_dd);
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                String currentDateandTime = sdf.format(cal.getTime());
                //   DateUtil.formatDateTo_yyyyMMdd(currentDateandTime);
                fieldsList.get(position).setValue(currentDateandTime);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    });
                }
                if (!onChange.isEmpty()) {
                    onChange(position, onChange,
                            currentDateandTime, "");

                    checkHideShow(onChange);
                }
            }
        }, null, null, getActivity());

        if (dialogFragment.isAdded()) {
            return;
        } else {
            dialogFragment.show(getChildFragmentManager(), "Date");
        }
    }

    private void displayCustomDateTimePicker(final int position) {
        custom = new CustomDateTimePicker(getActivity(),
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM, long _timeInMillies) {

                        String strDayOfMonth = CustomDateTimePicker.pad(calendarSelected
                                .get(Calendar.DAY_OF_MONTH));
                        String strMonth = CustomDateTimePicker.pad((monthNumber + 1));
                        String strHour24 = CustomDateTimePicker.pad(hour24);
                        String strMin = CustomDateTimePicker.pad(min);


                        String selectedDateTime = strDayOfMonth
                                + "/" + strMonth + "/" + year
                                + " " + strHour24 + ":" + strMin;

                        long timeInMillies = _timeInMillies;

                        fieldsList.get(position).setValue(selectedDateTime);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemChanged(position);
                                }
                            });
                        }

                        String onchange = fieldsList.get(position).getOnChange();

                        if (!onchange.isEmpty()) {
                            onChange(position, onchange,
                                    selectedDateTime, "");

                            checkHideShow(onchange);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */
        custom.set24HourFormat(true);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        custom.setDate(Calendar.getInstance());
        custom.showDialog();
    }

    @Override
    public void loadCustomDateTimePicker(int position, String onChange) {
        displayCustomDateTimePicker(position);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (quickLinkHelper.checkIfSavedInQuickLinks()) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_filled));
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_outline));
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void loadForm() {
//        ParseForm pf = new ParseForm(getActivity());
//        pf.setChartId(Integer.parseInt(chartId));
//        pf.setDbManager(dbManager);
//        pf.setmPrefManager(mPrefManager);

        try {
            boolean formAvailable = dbManager.checkIfFormExists(Integer.parseInt(chartId));
            if (NetworkUtil.isNetworkOnline(getActivity())) {
                String dateTime = "";
                if (!formAvailable) {
                    dateTime = Constant.SERVER_DATE_TIME;
                } else {
                    //1991-10-03%2011:20:00
                    dateTime = DateUtil.getCurrentDate("yyyy-MM-dd hh:mm:ss");
                }

                String formURL = String.format(Constant.FORM_URL,
                        mPrefManager.getClientServerUrl(),
                        chartId, dateTime, mPrefManager.getCloudCode(), mPrefManager.getAuthToken());
                Log.e("FORM_URL", formURL);
                callFormFieldApi(formURL);
            } else {
                if (formAvailable) {
                    buildFormUI(chartId);
                } else {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    //  ToastUtil.showToastMessage(getResources().getString(R.string.no_internet_message), getActivity());
                    showNoInternetImage();
                    // DialogUtil.showAlertDialog(getActivity(),
                    // "No Internet Connection!",
                    // "Please check your internet connection and try again", false, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
        notifyFieldPositions();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }

    private void notifyFieldPositions() {
        Log.e("notifyFieldPositions", " CALLED");
        if (mPrefManager != null) {
            if (fieldsList != null) {
                String fieldPositions = mPrefManager.getFieldPositions();
                if (!fieldPositions.isEmpty()) {
                    String[] posArr = fieldPositions.split(",");
                    for (int i = 0; i < posArr.length; i++) {
                        if (!posArr[i].isEmpty()) {
                            notifyAdapterWithPayLoad(Integer.parseInt(posArr[i].trim()), Constant.PAYLOAD_TEXT_SQL);
                        }
                    }
                }
                mPrefManager.setFieldPositionsToNotify("");
            }
        }
    }

    private void loadSpinnerData(final String field,
                                 String url,
                                 final int position,
                                 final boolean mdcombo,
                                 final String type) {
        KeyboardUtil.hideKeyboard(getActivity());
        boolean isValidURL = false;
        if (url != null && !url.isEmpty()) {
            isValidURL = android.util.Patterns.WEB_URL.matcher(url).matches();
        }
        if (isValidURL) {
            if (NetworkUtil.isNetworkOnline(getActivity())) {
                showProgressDialog();

                Log.e("FETCH_COUNTER", "loadSpinnerData" + String.valueOf(fetchCounter));
                Log.e(DEBUG_TAG, "loadSpinnerData URL=" + url);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest request = new StringRequest(Request.Method.GET, url + "&type=json",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e(DEBUG_TAG, "LoadSpinner Response=" + response);

                                try {
                                    Object json = new JSONTokener(response).nextValue();
                                    if (json instanceof JSONObject) {

                                        JSONObject jsonObject = new JSONObject(response);
                                        String value = jsonObject.getString("response").trim();

                                        if (mdcombo) {
                                            List<OptionModel> list = new ArrayList<>();
                                            //replacing % as it was showing %% in Stock Report - Design Code field.
                                            list.add(0, new OptionModel("%25", value.replace("%%", "%")));

                                            fieldsList.get(position).setOptionsArrayList(list);
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                });
                                            }
                                        } else if (type.equalsIgnoreCase("adcombo")) {
                                            final List<OptionModel> list = new ArrayList<>();
                                            list.add(new OptionModel(value, value));
                                            list.add(0, new OptionModel("", ""));
                                            fieldsList.get(position).setOptionsArrayList(list);
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                });
                                            }
                                        } else {

                                            for (int i = 0; i < fieldsList.size(); ++i) {
                                                Field fieldObj = fieldsList.get(i);
                                                if (field.equals(fieldObj.getId())) // field found
                                                {
                                                    fieldsList.get(position).setValue(value.trim());
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            adapter.notifyItemChanged(position);
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (json instanceof JSONArray) {
                                        JSONArray jsonArray = new JSONArray(response);
                                        List<OptionModel> list = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            list.add(new OptionModel(jsonArray.getString(i), jsonArray.getString(i)));
                                        }

                                        if (mdcombo) {
                                            list.add(0, new OptionModel("%25", "%"));
                                        }

                                        fieldsList.get(position).setOptionsArrayList(list);
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyItemChanged(position);
                                                }
                                            });
                                        }
                                    }

                                    dismissProgressDialog();
//
                                } catch (Exception e) {
                                    if (e instanceof JSONException) {
                                        if (mdcombo) {
                                            //adding this so that we can get % in multispinner even when we don't get any data from the api
                                            //basically added for Item Code in Stock Report
                                            ArrayList<OptionModel> list = new ArrayList<>();
                                            list.add(0, new OptionModel("%25", "%"));
                                            fieldsList.get(position).setOptionsArrayList(list);
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dismissProgressDialog();
                    }
                });
                queue.add(request);

            } else {
                ToastUtil.showToastMessage("Please check your internet connection and try again", getActivity());
                //    DialogUtil.showAlertDialog(getActivity(),
                //    "No Internet Connection!", "Please check your internet connection and try again", false, false);
            }
        } else {
            Log.e(DEBUG_TAG, "#line 402 , Bad URL = " + url);
        }
    }

    private void callAPI(String url, final boolean isPayLoad) {
        KeyboardUtil.hideKeyboard(getActivity());
        boolean isValidURL = false;
        if (url != null && !url.isEmpty()) {
            isValidURL = android.util.Patterns.WEB_URL.matcher(url).matches();
        }
        if (isValidURL) {
            if (NetworkUtil.isNetworkOnline(getActivity())) {

                showProgressDialog();

                Log.i(DEBUG_TAG, "callAPI URL === " + url);

                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.v(DEBUG_TAG, "callAPI RESPONSE === " + response);
                                    JSONObject jsonData = new JSONObject(response);
                                    if (jsonData != null) {
                                        if (fieldsList != null) {
                                            for (int i = 0; i < jsonData.names().length(); i++) {
                                                for (int j = 0; j < fieldsList.size(); j++) {
                                                    Field fieldObj = fieldsList.get(j);
                                                    if (fieldObj.getId().equals(jsonData.names().getString(i))) {
                                                        String value = (String) jsonData.get(jsonData.names().getString(i).trim());
                                                        if (value.contains("< select >")) {
                                                            value = value.replaceAll("select|\\<|\\:|\\#|\\>", "").trim();
                                                            int stringlength = value.length();
                                                            int d = stringlength / 2;
                                                            String firstString = value.substring(0, d);
                                                            String secondString = value.substring(d, stringlength);
                                                            if (firstString.equals(secondString)) {
                                                                value = firstString;
                                                            }
                                                        }
                                                        fieldObj.setValue(value);
                                                        if (isPayLoad) {
                                                            notifyAdapterWithPayLoad(j, Constant.PAYLOAD_TEXT);
                                                        } else {
                                                            notifyAdapter(j);
                                                        }
                                                    }
                                                }
                                            }

                                            List<DList> dlistValuesArray = null;
                                            //fetch dList Values
                                            if (jsonData.has("filldlistvalues")) {
                                                JSONArray jsonArray = jsonData.getJSONArray("filldlistvalues");
                                                String fieldId = "";
                                                int rows = 0;

                                                //looping various dlist api values
                                                for (int k = 0; k < jsonArray.length(); k++) {

                                                    JSONObject kObj = jsonArray.getJSONObject(k);
                                                    fieldId = kObj.getString("id");
                                                    String id = "field" + fieldId;
                                                    try {
                                                        rows = Integer.parseInt(kObj.getString("rows"));
                                                    } catch (NumberFormatException e) {
                                                        e.printStackTrace();
                                                    }

                                                    //dlistvalues object
                                                    JSONObject valuesObj = kObj.getJSONObject("values");
                                                    functionHelper.onLoadChangeIds("");
                                                    if (rows != 0) {
                                                        if (rows > 50) {
                                                            rows = 50;
                                                            //  ToastUtil.showToastMessage("You can select a max of 50 rows only", getActivity());
                                                        }
                                                        if (dbManager != null) {
                                                            dbManager.deleteDListWithFieldId(id);
                                                        }

                                                        FetchRecordHelper fh = new FetchRecordHelper(getActivity(), null);
                                                        fh.setFieldsList(fieldsList);
                                                        fh.setDlistArrayPosition(dlistArrayPosition);
                                                        fh.setAdditionalFieldDataList(additionalFieldDataList);

                                                        for (int j = 1; j <= rows; j++) {

                                                            dlistValuesArray = fh.getDListFieldObjectArray(id, false);
                                                            List<DList> dlistField = new ArrayList<>();

                                                            if (dlistValuesArray != null) {
                                                                for (int m = 0; m < dlistValuesArray.size(); m++) {
                                                                    DList dlist = dlistValuesArray.get(m);
                                                                    DList object = new DList();
                                                                    object.setDropDownClick(dlist.getDropDownClick());
                                                                    object.setSearchRequired(dlist.getSearchRequired());
                                                                    object.setSaveRequired(dlist.getSaveRequired());
                                                                    object.setReadOnly(dlist.getReadOnly());
                                                                    object.setSrNo(dlist.getSrNo());
                                                                    object.setName(dlist.getName());
                                                                    object.setId(dlist.getId());
                                                                    object.setOptionsArrayList(dlist.getOptionsArrayList());
                                                                    object.setAddFunction(dlist.getAddFunction());
                                                                    object.setOnKeyDown(dlist.getOnKeyDown());
                                                                    object.setType(dlist.getType());
                                                                    object.setValue(dlist.getValue());
                                                                    object.setFieldType(dlist.getFieldType());
                                                                    object.setFieldName(dlist.getFieldName());
                                                                    object.setDefaultValue(dlist.getDefaultValue());
                                                                    dlistField.add(object);
                                                                }
                                                            }
                                                            Iterator<String> iterator = valuesObj.keys();
                                                            while (iterator.hasNext()) {
                                                                String key = iterator.next();
                                                                String[] keyArr = key.split("_");
                                                                String keyIndex = "_" + keyArr[1];
                                                                String index = "_" + j;
                                                                if (keyIndex.matches(index)) {
                                                                    Log.e("TAG", "key:" + key + "--Value::" + valuesObj.optString(key) + " Index = " + index);
                                                                    if (dlistField != null) {
                                                                        for (int m = 0; m < dlistField.size(); m++) {
                                                                            DList dlist = dlistField.get(m);
                                                                            String[] arr = key.split("_");
                                                                            String[] jarr = dlist.getId().split("_");

                                                                            if (arr[0].equals(jarr[0])) {
                                                                                dlist.setId(arr[0] + index);
                                                                                if (dlist.getFieldName().toLowerCase().matches("sr no|sr")) {
                                                                                    dlist.setName(key);
                                                                                    dlist.setValue(String.valueOf(j));
                                                                                } else {
                                                                                    dlist.setName(key);
                                                                                    dlist.setValue(valuesObj.optString(key).trim());
                                                                                }
                                                                            } else {
                                                                                //added this check for SR NO ..we don't get sr no from api so we have to manually maintain sr no
                                                                                if (dlist.getFieldName().toLowerCase().matches("sr no|sr")) { //sr. no|sr
                                                                                    dlist.setId(jarr[0] + index);
                                                                                    dlist.setName(key);
                                                                                    dlist.setValue(String.valueOf(j));
                                                                                } else {
                                                                                    //added this so that we can change the index of the field for eg from field1234_0 to field1234_1 etc
                                                                                    dlist.setId(jarr[0] + index);
                                                                                }

                                                                                //added this so that we can change the index of the field for eg from field1234_0 to field1234_1 etc
                                                                                // dlist.setId(jarr[0] + index);
                                                                            }
                                                                            //end of if arr[0]

                                                                        }//end of m for loop
                                                                    }//end if !=null
                                                                }//end of if key.contains
                                                            }//end of while

                                                            if (j != 0) {
                                                                DListItem dListItem = new DListItem(dlistField);
                                                                String dlistJson = gson.toJson(dListItem);
                                                                Log.e("call api", "Converting dlist to json ----->" + dlistJson);
                                                                dbManager.insertDListRow(Integer.parseInt(FORM_ID), title, id, "", dlistJson, j);
                                                            }
                                                        }//end of row for loop
                                                    }// end of if
                                                }//end of jsonArray for loop

                                                if (rows != 0) {
                                                    DListFieldHelper fieldHelper = new DListFieldHelper(getActivity());
                                                    fieldHelper.updateContentRows(fieldId, rows);
                                                }

                                            }// end of if
                                        }
                                    }

                                    dismissProgressDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                    }
                });
                queue.add(request);
            } else {
                ToastUtil.showToastMessage("Please check your internet connection and try again", getActivity());
            }
        } else {
            Log.e(DEBUG_TAG, "#line553-- BAD URL =" + url);
        }
    }

    private void showProgressDialog() {
        if (fetchCounter == 0) {
            progressDialog1 = ProgressDialog.show(getActivity(),
                    "", "Fetching Details ...",
                    false);
        }
        fetchCounter++;
    }

    private void dismissProgressDialog() {
        fetchCounter--;
        if (fetchCounter == 0) {
            if ((progressDialog1 != null) && progressDialog1.isShowing()) {
                progressDialog1.dismiss();
            }
        }
    }

    private void callSaveDlistAPI(String nextState) {

        if (NetworkUtil.isNetworkOnline(getActivity())) {

            validateHelper.setAdditionalFieldDataList(additionalFieldDataList);
            validateHelper.setFieldsList(fieldsList);
            validateHelper.setDlistArrayPosition(dlistArrayPosition);

            if (validateHelper.areSaveRequiredFieldsValidated()) {
                if (validateHelper.checkMandatory("")) {

                    checkSaveHelper.setFormSaveCheck(form.getFormSaveCheck());
                    checkSaveHelper.setFormSaveCheckNames(form.getFormSaveCheckNames());
                    if (!checkSaveHelper.checkSave(validateProgressDialog)) {

                        final SaveRecordHelper saveRecordHelper = new SaveRecordHelper(getActivity(), mResponseInterface);
                        saveRecordHelper.setFormSave(form.getFormSave());
                        saveRecordHelper.setVlist(false);
                        saveRecordHelper.setFieldsList(fieldsList);
                        saveRecordHelper.setAdditionalFieldDataList(additionalFieldDataList);
                        saveRecordHelper.setDlistArrayPosition(dlistArrayPosition);

                        if (!RECORD_ID.equals("0")) {
                            saveRecordHelper.setRecordId(RECORD_ID);
                            saveRecordHelper.setFlag(false);
                            saveRecordHelper.setChangeState(true);
                            saveRecordHelper.setEditRecord(true);
                            saveRecordHelper.setFormTitle(title);
                            saveRecordHelper.setNextState(nextState);
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                saveRecordHelper.callSaveFormRecordAPI();
                            }
                        }).start();

                    }
                }
            }
        } else {
            DialogUtil.showAlertDialog(getActivity(),
                    "No Internet Connection!", "Please check your internet connection and try again", false, false);
        }
    }

    private String getMandatory() {
        String mandatory = "";
        for (int i = 0; i < additionalFieldDataList.size(); i++) {
            Field fObj = additionalFieldDataList.get(i);
            if (fObj.getName().toLowerCase().matches("mandatory")) {
                mandatory = fObj.getValue();
                break;
            }
        }
        return mandatory;
    }

    private void showAlert(String mandatory) {
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matchPattern = pattern.matcher(mandatory);

        while (matchPattern.find()) {
            mandatory = matchPattern.group(1);
        }
        DialogUtil.showAlertDialog((Activity) getActivity(), "", mandatory, false, false);
    }

//    private boolean checkMandatory(String compulsory) {
//     //  showValidationProgress();
//
//        final String[] result = {""};
//        String mandatory = "";
//        if(!compulsory.isEmpty()){
//            mandatory = compulsory;
//        }  else{
//            mandatory = getMandatory();
//        }
//
//        //this condition is to just show the alert we get sometimes from dynamic buttons.
//        //no further action should be taken
//        if(mandatory.contains("alert")){
//            showAlert(mandatory);
//            return false;
//        }
//
//        int selectedItem = -1;
//        boolean isFieldHidden = false;
//        String[] arr = mandatory.split("/");
//
//        List<Field> dlistArray = new ArrayList<>();
//        first:
//        for (int j = 0; j < arr.length; j++) {
//            String fieldId = "field" + arr[j];
//            //main Form validation
//            if (!fieldId.contains("_")) {
//                for (int k = 0; k < fieldsList.size(); k++) {
//                    Field kobj = fieldsList.get(k);
//                    if (fieldId.matches(kobj.getId())) {
//
//                        if (kobj.getdListArray().isEmpty()) {
//                            if(kobj.getDataType().toLowerCase().equals("file")){
//                                String ext = getExtension(kobj.getValue());
//                                //Checks if there is any extension after the last . in your input
//                                if (ext.isEmpty()) {
//                                    selectedItem = k;
//                                   // dismissValidationProgress();
//                                    showErrorDialog(kobj.getFieldName());
//                                    isFieldHidden = false;
//                                }
//                            }else{
//                                if (kobj.getValue().isEmpty()) {
//                                    if (!kobj.getType().matches("hidden")) {
//                                        // ToastUtil.showToastMessage(kobj.getField_name() + "Can't be empty", getActivity());
//                                        // isFieldHidden = true;
//                                        // } else {
//                                        selectedItem = k;
//                                        fieldsList.get(k).setShowErrorMessage(true);
//                                      //  dismissValidationProgress();
//                                        showErrorDialog(kobj.getFieldName());
//                                        final int finalK = k;
//
////                                        if (getActivity() != null) {
////                                            getActivity().runOnUiThread(new Runnable() {
////                                                @Override
////                                                public void run() {
////                                                    adapter.notifyItemChanged(finalK);
////                                                }
////                                            });
////                                        }
//                                        isFieldHidden = false;
//                                    }
//                                } else {
//                                    fieldsList.get(k).setShowErrorMessage(false);
//                                    final int finalK1 = k;
////                                    if (getActivity() != null) {
////                                        getActivity().runOnUiThread(new Runnable() {
////                                            @Override
////                                            public void run() {
////                                                adapter.notifyItemChanged(finalK1);
////                                            }
////                                        });
////                                    }
//                                }
//                            }
//                            break ;
//                        } else {
//                            //dlist Found
//                            //ToDo = search for save required in DList pending
//                            dlistArray = kobj.getdListArray();
//                            for (int m = 0; m < dlistArray.size(); m++) { // will loop through the dlistbuttonArray
//                                Field dlist = dlistArray.get(m);
//                                if (fieldId.matches(dlist.getId())) {
//                                    //Display a toast saying this dlist can't be empty
//                                    // For.eg Configuration Parameter is Empty
//                                    ToastUtil.showToastMessage(dlist.getField_name() + "Can't be empty", getActivity());
//                                    selectedItem = m;
//                                    isFieldHidden = true;
//                                    break;
//                                }
//                            }
//                            Log.e("CHECKMANADATORY", "dlistEmpty");
//                        }
//                    }
//                }
//                if (selectedItem != -1) {
//                    break;
//                }
//            } else {
//                boolean r = validateDlist( arr[j],j);
//                if(!r){
//                    result[0] = "false";
//                    break ;
//                }
//            }
//        }
//
//        if (!isFieldHidden) {
//            if (selectedItem != -1) {
//                Log.e(DEBUG_TAG, "------ #line 715");
//                Log.e("isValidated", "Position = " + selectedItem + " is Emtpy ------");
//             //   scrollToFieldPostion(selectedItem);
//                result[0] = "false";
//            }
//        }
//
//        if (result[0].equals("false")) {
//            Log.e("isValidated", String.valueOf(false));
//            return false;
//        }
//        Log.e("isValidated", String.valueOf(true));
//        return true;
//    }

    private void showValidationProgress() {
        if (validateProgressDialog == null) {
            validateProgressDialog = new ProgressDialog(getActivity());
            validateProgressDialog.setMessage("Please Wait Validating Data ...");
            validateProgressDialog.show();
        }
    }

    private void dismissValidationProgress() {
        if (validateProgressDialog != null) {
            validateProgressDialog.dismiss();
        }
    }


//    public boolean validateDlist(String arr,int positionOfMandatoryField){
//        boolean isValidated = true;
//        //dlistfields validation
//        String[] jaar = arr.split("_");
//        String dlistID = jaar[0];
//        String dlistField = jaar[1];
//        //validate dlist form fields
//        isValidated = validateDlistFormFields(dlistID,dlistField,positionOfMandatoryField);
//        return isValidated;
//    }

//    public boolean validateDlistFormFields(String dlistField,
//                                           String dlistChildField,
//                                           int positionOfMandatoryField) {
//        boolean isValidated = true;
//                String[] arr4 = new String[0];
//                boolean isContentRowFound = false;
//
//                List<Field> dlistArray = FormFragment.fieldsList.get(dlistArrayPosition).getdListArray();
//                //get the value in content rows for a partcular dlist
//
//                for (int j = 0; j < dlistArray.size(); j++) {
//                    Field dlist = dlistArray.get(j); // sindle dlist Item
//                  if(dlist.getId().equals("field"+dlistField)){
//                    List<DList> dlistFields = dlist.getdListsFields();
//                    //loop through dlist title - zeroth row
//                    for (int k = 0; k < dlistFields.size(); k++) {
//                        DList dobj = dlistFields.get(k);
//                        if (dobj.getId().equals("contentrows" + dlistField.replace("field",""))) {
//                        //    Log.e(DEBUG_TAG,"contentrows" + dlistField.replace("field","") + "value = " + dobj.getValue());
//                            arr4 = dobj.getValue().split(",");
//                            isContentRowFound= true;
//                            break;
//                        }
//                    }//end of for loop
//                  }
//                 }//end of for loop
//
//                if(!isContentRowFound){
//                   return true;
//                }
//
//                //loop through the content array
//                for (int l = 0; l < arr4.length; l++) {
//                    if (!arr4[l].equals("")) {
//                        isValidated =  checkIfDlistFormFieldEmpty(dlistField,dlistChildField,arr4[l],positionOfMandatoryField);
//                        if(!isValidated){
//                            break;
//                        }
//                    }
//                }
//                return isValidated;
//    }

    public void showErrorDialog(String fieldName) {
        DialogUtil.showAlertDialog((Activity) getActivity(), "", fieldName + " can't be empty.", false, false);
    }

//    public boolean checkIfDlistFormFieldEmpty(String fieldId,
//                                              String dlistFieldId,
//                                              String extension,
//                                              int positionOfMandatoryField) {
//
//        boolean isValidated = true;
//
//        List<Field> dlistButtonArray =  FormFragment.fieldsList.get(dlistArrayPosition).getdListArray();
//
//        first:
//        for (int m = 0; m <dlistButtonArray.size(); m++) {
//            Field dlist = dlistButtonArray.get(m);
//            if(dlist.getId().equals("field"+fieldId)){
//
//                List<String> dlistRowsJson =  dbManager.fetchDlistJson("field"+fieldId);
//              for(int x=0; x<dlistRowsJson.size(); x++) {
//                  try {
//                      JSONObject jsonObject = new JSONObject(dlistRowsJson.get(x));
//                      JSONArray jsonArray = jsonObject.getJSONArray("dlistArray");
//                      for(int y =0; y<jsonArray.length(); y++){
//                          JSONObject jObj = jsonArray.getJSONObject(y);
//                          if (jObj.getString("mId").equals("field" + dlistFieldId + "_" + extension)) {
//
//                              if(jObj.getString("mFieldType").toLowerCase().equals("file")){
//                                  String ext = getExtension(jObj.getString("mValue"));
//                                  //Checks if there is any extension after the last . in your input
//                                  if (ext.isEmpty()) {
//                                    //  dismissValidationProgress();
//                                      showErrorDialog(jObj.getString("mFieldName"));
//                                      isValidated = false;
//                                      break first;
//                                  }
//                              }else{
//
//                                  if(jObj.getString("mFieldType").toLowerCase().equals("double")){
//
//                                      if(jObj.getString("mValue").isEmpty() || jObj.getString("mValue").matches("0|0.0|0.00|0.000")){
//                                          showErrorDialog(jObj.getString("mFieldName"));
//                                          isValidated = false;
//                                          break first;
//                                      }
//                                  }else{
//                                      if(jObj.getString("mValue").isEmpty()) {
//                                        //  dismissValidationProgress();
//                                          showErrorDialog(jObj.getString("mFieldName"));
//                                          isValidated = false;
//                                          break first;
//                                          //  return false;
//                                      }
//                                  }
//                              }
//                              Log.e(DEBUG_TAG, "getDlistFieldValue  " + "dlist field = " + jObj.getString("mId") + " value = " + jObj.getString("mValue"));
//                          }
//                      }
//                  }catch (Exception e) {
//                      e.printStackTrace();
//                  }
//              }
//
//                if(dlistRowsJson.size() == 0){
//
//                    String mandatoryFieldName = validateHelper.getMandatoryName(positionOfMandatoryField);
//                  //  dismissValidationProgress();
//                    showErrorDialog(mandatoryFieldName);
//                    isValidated = false;
//                    break first;
//                }
//            }
//        }
//        return isValidated;
//    }

//    static String getExtension(String str){
//        int begin = str.lastIndexOf(".");
//        if(begin == -1)
//            return "";
//        String ext = str.substring(begin + 1);
//        return ext;
//    }

    public void scrollToFieldPostion(int selectedItem) {
        try {
            if (recyclerviewForm != null) {
                float childY = recyclerviewForm.getChildAt(selectedItem).getY();
                final float y = recyclerviewForm.getY() + childY;

                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fling(0);
                        mScrollView.smoothScrollTo(0, (int) y);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        for (int i = 0; i < fieldsList.size(); i++) {
            fieldsList.get(i).setValue("");
            if (i == dlistArrayPosition) {
                List<Field> dlistArray = fieldsList.get(i).getdListArray();
                for (int j = 0; j < dlistArray.size(); j++) {
                    Field fieldObj = dlistArray.get(j);
                    fieldObj.getDListItemList().clear();
                }
            }
        }
        if (dbManager != null) {
            dbManager.deleteDList();
        }
        if (adapter != null) {

            adapter.notifyDataSetChanged();
        }
    }

    private boolean hasPermission() {
        if ((checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constant.REQUEST_PERMISSIONS);
            }
            return false;
        } else {
            Log.e("Else", "Else");
            return true;
        }
    }

    public void chooseFile() {
        String[] mimeTypes =
                {"image/*",
                        "application/pdf",
                        "application/msword",
                        "application/vnd.ms-powerpoint",
                        "application/vnd.ms-excel",
                        "text/plain"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), Constant.PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Constant.PICK_FILE_REQUEST_CODE) {
                if (data != null) {
                    Uri uri = data.getData();
                    FileUtil fileUtil = new FileUtil(getActivity());
                    String filePath = fileUtil.getPath(uri);
                    Log.e("FILE_PATH", filePath);

                    if (attachFileHelpler.isAttachFileClicked()) {
                        attachFileHelpler.setFileName(filePath);
                        attachFileHelpler.setFormId(FORM_ID);
                        attachFileHelpler.setRecordId(RECORD_ID);

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Attach")
                                .setMessage("Are you sure you want to continue ?")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(new Runnable() {
                                            public void run() {
                                                attachFileHelpler.uploadFile();
                                            }
                                        }).start();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        if (fieldsList != null) {
                            fieldsList.get(fileFieldPosition).setValue(filePath);
                            adapter.notifyItemChanged(fileFieldPosition);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callFormFieldApi(String formURL) {
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading....");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET,
                formURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(DEBUG_TAG, "callFormFieldAPI Response Received");

                        if (!response.isEmpty()) {
                            if (dbManager != null) {
                                boolean formAvailable = dbManager.checkIfFormExists(Integer.parseInt(chartId));
                                if (formAvailable) {
                                    dbManager.updateForm(Integer.parseInt(chartId), response);
                                } else {
                                    dbManager.insertForm(Integer.parseInt(chartId), title, response);
                                }
                            }
                            buildFormUI(chartId);
                        } else if (response.equals("")) {
                            buildFormUI(chartId);
                        }

//                        if(progressDialog != null){
//                            progressDialog.dismiss();
//                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyErrorUtil.showVolleyError(getActivity(), error);
//                        if(progressDialog != null){
//                            progressDialog.dismiss();
//                        }
                        buildFormUI(chartId);
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        requestQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void buildFormUI(String chartId) {
        try {
            String response = "";
            if (dbManager != null) {
                response = dbManager.getFormJson(Integer.parseInt(chartId));
            }

            JSONObject jsonObject = new JSONObject(response);
            JSONObject json = jsonObject.getJSONObject("json");

            String formSaveCheck = "";
            if (json.has("formsavecheck")) {
                formSaveCheck = json.getString("formsavecheck");
            }
            String formSaveCheckNames = "";
            if (json.has("formsavechecknames")) {
                formSaveCheckNames = json.getString("formsavechecknames");
            }

            form = new Form(json.getString("enctype"),
                    json.getString("data-title"),
                    json.getString("form-method"),
                    json.getString("form-id"),
                    json.getString("id"),
                    json.getString("form-save"),
                    formSaveCheck,
                    formSaveCheckNames);

            JSONArray jsonArray = json.getJSONArray("fields");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.length() > 5) {
                    String showFieldName = "";
                    if (obj.has("showfieldname")) {
                        showFieldName = obj.getString("showfieldname");
                    }

                    String waterMark = "";
                    if (obj.has("watermark")) {
                        waterMark = obj.getString("watermark");
                    }

                    String saveRequired = "";
                    if (obj.has("save_required")) {
                        saveRequired = obj.getString("save_required");
                    }

                    String webSaveRequired = "";
                    if (obj.has("websave_required")) {
                        webSaveRequired = obj.getString("websave_required");
                    }

                    String jFunction = "";
                    if (obj.has("jfunction")) {
                        jFunction = obj.getString("jfunction");
                    }

                    String onChange = "";
                    if (obj.has("onchange")) {
                        onChange = obj.getString("onchange");
                        onChange = FieldHelper.replaceUpdatetview(onChange);
                    }

                    String onKeyDown = "";
                    if (obj.has("onkeydown")) {
                        onKeyDown = obj.getString("onkeydown");
                    }

                    String default_value = "";
                    if (obj.has("default_value")) {
                        default_value = obj.getString("default_value");
                    }

                    String sqlValue = "";
                    if (obj.has("sql_value")) {
                        sqlValue = obj.getString("sql_value");
                    }

                    String fieldName = "";
                    if (obj.has("field_name")) {
                        fieldName = obj.getString("field_name");
                    }

                    String relation = "";
                    if (obj.has("relation")) {
                        relation = obj.getString("relation");
                    }

                    String type = "";
                    if (obj.has("type")) {
                        type = obj.getString("type");
                    }

                    String fieldType = "";
                    String field_type = "";
                    if (obj.has("field_type")) {
                        fieldType = obj.getString("field_type");
                        field_type = obj.getString("field_type");
                    }

                    String states = "";
                    if (obj.has("states")) {
                        states = obj.getString("states");
                        //If states is not empty
                        //set fieldType = type
                        //set type = hidden
                        //by doing so the extra fields will be hidden initially
                        if (!states.isEmpty()) {
                            // Jan 16 2020 - commented this condition as Responsible person wasn't hidden when the form loads initially
                            //  if (!states.startsWith("s")) { // checking this because getting s2089s2598s as states sometimes
                            fieldType = type;
                            type = "hidden";
                            //  }
                        }
                    }

                    String relationField = "";
                    if (obj.has("relation_field")) {
                        relationField = obj.getString("relation_field");
                    }

                    String jIdList = "";
                    if (obj.has("jidlist")) {
                        jIdList = obj.getString("jidlist");
                    }

                    String name = "";
                    if (obj.has("name")) {
                        name = obj.getString("name");
                    }

                    String width = "";
                    if (obj.has("width")) {
                        width = obj.getString("width");
                    }

                    String options = "";
                    List<OptionModel> optionsArrayList = new ArrayList<>();
                    if (obj.has("options")) {
                        options = obj.getString("options");

                        if (!options.isEmpty() && !options.equals("")) {
                            String str[] = options.split(",");

                            if (str.length > 0) {
                                for (int a = 0; a < str.length; a++) {
                                    String[] op = str[a].split(":");
                                    // Log.e("OPTIONS STRING ",  "str[a] =" + str[a].toString());

                                    String mOp0 = op[0].trim().replaceAll("[\"\\{\\}]", "");
                                    String mOp1 = op[1].trim().replaceAll("[\"\\{\\}]", "");
                                    optionsArrayList.add(new OptionModel(mOp0, mOp1));
                                    // Log.e("OPTIONS STRING ", op[0].toString() + "  op1=" + op[1].toString());

                                }
                                optionsArrayList.add(0, new OptionModel("", ""));
                            }
                        }
                    }

                    if (default_value.toLowerCase().matches("sql\\$\\{self\\}|\\$username")) {
                        optionsArrayList.add(new OptionModel(mPrefManager.getUserName(), mPrefManager.getUserName()));
                    }

                    String searchRequired = "";
                    if (obj.has("search_required")) {
                        searchRequired = obj.getString("search_required");
                    }

                    String vlistRelationIds = "";
                    if (obj.has("vlistrelationids")) {
                        vlistRelationIds = obj.getString("vlistrelationids");
                    }

                    String vlistDefaultIds = "";
                    if (obj.has("vlistdefaultids")) {
                        vlistDefaultIds = obj.getString("vlistdefaultids");
                    }

                    String id = "";
                    if (obj.has("id")) {
                        id = obj.getString("id");
                    }

                    String placeHolder = "";
                    if (obj.has("placeholder")) {
                        placeHolder = obj.getString("placeholder");
                    }

                    String value = "";
                    if (obj.has("value")) {
                        if (default_value.matches("\\$\\{today\\}|field\\$\\{today\\}")) {
                            value = DateUtil.getCurrentDate(Constant.yyyy_MM_dd);
                        } else if (default_value.matches("\\$\\{now\\}|field\\$\\{now\\}")) {
                            value = DateUtil.getCurrentDate(Constant.yyyy_MM_dd_now);
                        } else {
                            value = obj.getString("value");
                        }
                    }

                    String onClickSummary = "";
                    if (obj.has("onclicksummary")) {
                        onClickSummary = obj.getString("onclicksummary");
                    }

                    String onClickVList = "";
                    if (obj.has("onclickvlist")) {
                        onClickVList = obj.getString("onclickvlist");
                    }

                    String newRecord = "";
                    if (obj.has("newrecord")) {
                        newRecord = obj.getString("newrecord");
                    }

                    String onClickRightButton = "";
                    if (obj.has("onclickrightbutton")) {
                        onClickRightButton = obj.getString("onclickrightbutton");
                    }

                    List<Field> DlistArray = new ArrayList<>();
                    List<DList> dlistField = new ArrayList<>();
                    if (obj.has("dlistfields")) {
                        JSONArray dListArray = obj.getJSONArray("dlistfields");

                        for (int k = 0; k < dListArray.length(); k++) {
                            JSONObject kObj = dListArray.getJSONObject(k);
                            DList dlistObject = null;
                            if (k == 0) {
                                //code for setting fieldid_0 eg. fieldid50412_0
                                dlistObject = new DList();
                                String fieldid = id.replace("field", "fieldid");
                                fieldid += "_0";
                                dlistObject.setName(fieldid);
                                dlistObject.setFieldName(fieldid);
                                dlistObject.setSrNo(0);
                                dlistObject.setId(fieldid);
                                dlistObject.setType("hidden");
                                dlistObject.setValue("0");
                                dlistObject.setFieldName(fieldid);
                                dlistField.add(dlistObject);
                            }

                            dlistObject = new DList();
                            if (kObj.has("dropdownclick")) {
                                dlistObject.setDropDownClick(kObj.getString("dropdownclick"));
                            }

                            String dOptions = "";
                            List<OptionModel> dOptionsArrayList = new ArrayList<>();
                            if (kObj.has("options")) {
                                dOptions = kObj.getString("options");

                                if (!dOptions.isEmpty()) {
                                    Log.e("dOptions = ", dOptions);
                                    String str[] = dOptions.split(",");

                                    if (str.length > 0) {
                                        for (int a = 0; a < str.length; a++) {
                                            String[] op = str[a].split(":");
                                            // Log.e("OPTIONS STRING ",  "str[a] =" + str[a].toString());

                                            String mOp0 = op[0].trim().replaceAll("[\"\\{\\}]", "");
                                            String mOp1 = op[1].trim().replaceAll("[\"\\{\\}]", "");
                                            dOptionsArrayList.add(new OptionModel(mOp0, mOp1));
                                            // Log.e("OPTIONS STRING ", op[0].toString() + "  op1=" + op[1].toString());

                                        }
                                        dOptionsArrayList.add(0, new OptionModel("", ""));
                                    }
                                }
                                dlistObject.setOptionsArrayList(dOptionsArrayList);
                            }

                            if (kObj.has("defaultvalue")) {
                                dlistObject.setDefaultValue(kObj.getString("defaultvalue"));
                            }

                            if (kObj.has("readonly")) {
                                dlistObject.setReadOnly(kObj.getString("readonly"));
                            }

                            if (kObj.has("save_required")) {
                                dlistObject.setSaveRequired(kObj.getString("save_required"));
                            }

                            if (kObj.has("search_required")) {
                                dlistObject.setSearchRequired(kObj.getString("search_required"));
                            }

                            if (kObj.has("srno")) {
                                dlistObject.setSrNo(kObj.getInt("srno"));
                            }

                            if (kObj.has("name")) {
                                dlistObject.setName(kObj.getString("name"));
                            }

                            if (kObj.has("id")) {
                                dlistObject.setId(kObj.getString("id"));
                            }

                            if (kObj.has("addfunction")) {
                                dlistObject.setAddFunction(kObj.getString("addfunction"));
                            }
                            if (kObj.has("onkeydown")) {
                                dlistObject.setOnKeyDown(kObj.getString("onkeydown"));
                            }

                            if (kObj.has("type")) {
                                dlistObject.setType(kObj.getString("type"));
                            }

                            if (kObj.has("value")) {
                                dlistObject.setValue(kObj.getString("value"));
                            }

                            if (kObj.has("field_type")) {
                                dlistObject.setFieldType(kObj.getString("field_type"));
                            }

                            if (kObj.has("field_name")) {
                                dlistObject.setFieldName(kObj.getString("field_name"));
                            }
                            if (kObj.has("states")) {
                                String mStates = kObj.getString("states");
                                if (mStates.startsWith("s")) {
                                    dlistObject.setType("hidden");
                                }
                                dlistObject.setStates(kObj.getString("states"));
                            }
                            dlistField.add(dlistObject);
                        }
                    }

                    if (fieldType.equalsIgnoreCase("DLIST")) {
                        if (obj.has("dlistfields")) {

                            //dLIst form Fields which will be shown when we click on the button in recyclerview
                            Field dlistF = new Field(waterMark, showFieldName, saveRequired, jFunction,
                                    onChange, onKeyDown, default_value, type, sqlValue,
                                    fieldName, relation, states, relationField,
                                    onClickVList, jIdList, name, width, searchRequired, id, placeHolder,
                                    value, fieldType, onClickSummary, options, newRecord, dlistField, optionsArrayList, "", "", field_type, "", "");
                            DlistArray.add(dlistF);

                            //DList which we well show as a button
                            Field field = new Field(waterMark, showFieldName, saveRequired, jFunction,
                                    "", onKeyDown, default_value, "DLIST", sqlValue,
                                    fieldName, relation, "", relationField,
                                    onClickVList, jIdList, "", searchRequired, "", placeHolder,
                                    value, fieldType, onClickSummary, newRecord, DlistArray, optionsArrayList,
                                    onClickRightButton, webSaveRequired, field_type);


                            //add it to the form
                            int dListIndex = getDListArray();
                            if (dListIndex != -1) {
                                List<Field> dListArray = fieldsList.get(dListIndex).getdListArray();
                                dListArray.add(dlistF);
                            } else {
                                fieldsList.add(field);
                            }
                        }
                    } else {
                        Field field = new Field(waterMark, showFieldName, saveRequired, jFunction,
                                onChange, onKeyDown, default_value, type, sqlValue,
                                fieldName, relation, states, relationField,
                                onClickVList, jIdList, name, width, searchRequired, id, placeHolder,
                                value, fieldType, onClickSummary, newRecord, options, dlistField, optionsArrayList,
                                onClickRightButton, webSaveRequired, field_type, vlistRelationIds, vlistDefaultIds);

                        fieldsList.add(field);
                    }
                } else {
                    String showFieldName = "";
                    if (obj.has("showfieldname")) {
                        showFieldName = obj.getString("showfieldname");
                    }

                    String name = "";
                    if (obj.has("name")) {
                        name = obj.getString("name");
                    }

                    String id = "";
                    if (obj.has("id")) {
                        id = obj.getString("id");
                    }

                    String type = "";
                    if (obj.has("type")) {
                        type = obj.getString("type");
                    }

                    String value = "";
                    if (obj.has("value")) {
                        if (id.toLowerCase().equals("formid")) {
                            FORM_ID = obj.getString("value");
                            mPrefManager.setFormId(FORM_ID);
                        }
                        value = obj.getString("value");
                    }
                    if(additionalFieldDataList==null){

                        additionalFieldDataList = new ArrayList<>();


                    }
                    additionalFieldDataList.add(new Field(showFieldName, name, id, type, value));
                }
                dlistArrayPosition = getDListArray();
                callAdapter();
            }
            showDynamicButtons(RECORD_ID, false);
        } catch (JSONException e) {
            e.printStackTrace();
            callAdapter();
        }
    }

    private int getDListArray() {
        for (int i = 0; i < fieldsList.size(); i++) {
            if (!fieldsList.get(i).getdListArray().isEmpty()) {
                return i;
            }
        }
        return -1;
    }

//    private void initFunctionHelper() {
//        functionHelper.setAdditionalFieldDataList(additionalFieldDataList);
//        functionHelper.setFieldsList(fieldsList);
//        functionHelper.setDlistArrayPosition(dlistArrayPosition);
//    }

    private void callAdapter() {
        if (fieldsList.isEmpty()) {
            shimmerFrameLayout.setVisibility(View.GONE);
            //relBackgroundLayout.setBackgroundResource(R.drawable.background_bg);
            llNoItemsView.setVisibility(View.VISIBLE);
            noItemTextView.setText(R.string.no_result_found);
            noItemImage.setImageResource(R.drawable.ic_list);
        } else {
            functionHelper.setAdditionalFieldDataList(additionalFieldDataList);
            functionHelper.setFieldsList(fieldsList);
            functionHelper.setDlistArrayPosition(dlistArrayPosition);

            llNoItemsView.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
            // relBackgroundLayout.setBackgroundResource(R.drawable.background_bg);
            adapter = new FormRecylerAdapter(getActivity(), fieldsList, this, entryFrom, false);
            //added this as hidden fields were getting called when i was trying to type in edit text in recyclerview - Pre Registration Form
            recyclerviewForm.setItemViewCacheSize(fieldsList.size());
            recyclerviewForm.setAdapter(adapter);
            adapter.setRecordId(RECORD_ID);
            adapter.setFormId(FORM_ID);
            adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                @Override
                public void onBottomReached(final int position) {
                    if (entryFrom != null && entryFrom.equals("pending_task") || isInputInTagField) {
                        adapter.setFlag(false);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // fetchingAfterSaving = false;
                                if (adapter != null) {
                                    adapter.setFlag(true);
                                }
                            }
                        }, getDelay());
                    }
                }
            });
            adapter.setCustomDateTimePickerListener(this);
            adapter.setLocationListener(new LocationInterface() {
                @Override
                public void displayLocation(int position, String type) {
                    CurrentLocation currentLocation = new CurrentLocation(getActivity());
                    currentLocation.setPosition(position);
                    currentLocation.setType(type);
                    currentLocation.getLocation();
                }
            });
        }
    }

    public int getDelay() {
        if ((entryFrom != null && entryFrom.equals("pending_task")) || isInputInTagField) {
            return 2500;
        } else {
            if (fieldsList.size() > 30) {
                return 900;
            } else {
                return 100;
            }
        }
    }

    public void evaluateFunction(String funcString, final String value) {
        String[] arr = funcString.split("evaluatefunction");
        String[] evalFunc = arr[1].split(",");
        final String fieldId = evalFunc[0].replaceAll("['\\(\\)]", "");
        final String functionList = evalFunc[1].replaceAll("\'", "");
        final String extension = evalFunc[2].replaceAll("['\\(\\)]", "");
        final String jcodeList = evalFunc[3].replaceAll("['\\(\\)]", "");

        Runnable backGroundRunnable = new Runnable() {
            public void run() {
                EvaluateFunctionHelper evaluateFunctionHelper = new EvaluateFunctionHelper(getActivity());
                evaluateFunctionHelper.setDlistArrayPosition(dlistArrayPosition);
                evaluateFunctionHelper.setFieldsList(fieldsList);
                evaluateFunctionHelper.setAdditionalFieldDataList(additionalFieldDataList);
                evaluateFunctionHelper.setAdapter(adapter);
                evaluateFunctionHelper.evaluateFunction(fieldId, functionList, extension, jcodeList,
                        value);
            }
        };
        Thread thread = new Thread(backGroundRunnable);
        thread.start();
    }

    public void checkPattern(String funcString, final String value, final int position) {
        String[] arr = funcString.split("checkpattern");
        String[] checkPat = arr[1].split("\',\'");
        final String fieldId = checkPat[0].replaceAll("['\\(\\)]", "");
        final String regexPattern = checkPat[1].replaceAll("\'", "");
        final String extension = checkPat[2].replaceAll("\'", "");
        final String fieldName = checkPat[3].replaceAll("\'", "");

        Log.e("CHECKPATTERN", fieldId + "@J@" + regexPattern + "@J@" + extension + "@J@" + fieldName);

        //  int resultPosition = functionHelper.checkPattern(fieldId, regexPattern, extension, fieldName, false);
        CheckPatternFunction cp = new CheckPatternFunction();
        cp.setFieldsList(fieldsList);
        cp.setAdditionalFieldDataList(additionalFieldDataList);


        int resultPosition = cp.checkPattern(fieldId,
                regexPattern,
                extension,
                fieldName,
                false);

        if (resultPosition != -1) {
            //scrollToFieldPostion(resultPosition);
            //  notifyAdapter(resultPosition);
            notifyAdapter(resultPosition);
        }
    }

    private void checkRepeats(String funcString) {

        CheckRepeatHelper checkRepeatHelper = new CheckRepeatHelper(getActivity(), FORM_ID);
        checkRepeatHelper.setFieldsList(fieldsList);
        checkRepeatHelper.setAdditionalFieldDataList(additionalFieldDataList);
        String url = checkRepeatHelper.splitCheckRepeats(funcString);

        if (fetchCounter >= 0) {
            if (!url.equals("")) {
                FetchCheckRepeatDataHelper ch = new FetchCheckRepeatDataHelper(getActivity(), mResponseInterface);
                ch.setAdditionalFieldDataList(additionalFieldDataList);
                ch.setFieldsList(fieldsList);
                ch.callFetchCheckRepeatData(url);
            }
        }

    }

    public void evaluateSQL(String funcString, String value, final int position, boolean isPayLoad) {
        String[] arr = funcString.split("evaluatesql");
        String[] evalSql = arr[1].split(",");
        final String fieldId = evalSql[0].replaceAll("['\\(\\)]", "");
        final String extension = evalSql[1].replaceAll("\'", "");
        final String jcodeList = evalSql[2].replaceAll("['\\(\\)]", "");
        if (fieldsList != null) {
            value = fieldsList.get(position).getValue();
        }
        String evSqlURL = functionHelper.evaluatesql(fieldId, extension, jcodeList,
                value, false);
        //call api with this url
        if (fetchCounter >= 0) {
            callAPI(evSqlURL, isPayLoad);
        }
    }

    private void fn(String funcString, String value, final int position) {
        String[] arr = funcString.split("fn");
        String[] fnString = arr[1].split(",");

        final String fetchFormId = fnString[0].replaceAll("['\\(\\)]", "");
        final String fieldId = fnString[1].replaceAll("\'", "");
        final String matchingField = fnString[2].replaceAll("['\\(\\)]", "");
        final String matchingFieldIds = fnString[3].replaceAll("['\\(\\)]", "");

        String fnURL = functionHelper.fn(FORM_ID, fetchFormId, fieldId, matchingField, matchingFieldIds, value);
        //call api with this url
        if (fetchCounter >= 0) {
            callAPI(fnURL, false);
        }
    }

    private void clearFieldIds(String funcString) {
        String[] arr = funcString.split("clearfieldids");
        String[] f = arr[1].split(",");
        String fieldId = f[0].replaceAll("['\\(\\);]", "");
        clearfieldids(fieldId);
    }

    private void clearfieldids(String fieldId) {
        for (int i = 0; i < fieldsList.size(); i++) {
            if (fieldId.equals(fieldsList.get(i).getId())) {
                fieldsList.get(i).setValue("");
            }
        }
    }

    @Override
    public void checkHideShow(String funcString) {

        if (!funcString.isEmpty()) {
            Pattern pattern = Pattern.compile("checkhideshow.*?\\);");
            Matcher matcher = pattern.matcher(funcString);

            while (matcher.find()) {
                funcString = matcher.group(0);
                break;
            }

            if (funcString.contains("checkhideshow")) {
                String[] arr = funcString.split("checkhideshow");
                String[] f = arr[1].split(",");
                String fieldId = f[0].replaceAll("['\\(\\);]", "");

                String value = "";
                String fID = "field" + fieldId;
                String inputValue = "";

                if (fieldsList != null) {
                    for (int i = 0; i < fieldsList.size(); i++) {
                        Field fieldObj = fieldsList.get(i);
                        if (fID.equals(fieldObj.getId())) {
                            if (fieldObj.getType().toLowerCase().equals("checkbox")) {
                                value = fieldObj.getValue();
                                break;
                            } else {
                                inputValue = fieldObj.getValue();

                                List<OptionModel> options = fieldObj.getOptionsArrayList();
                                if (options.isEmpty()) {
                                    value = inputValue;
                                    break;
                                } else {
                                    for (int j = 0; j < options.size(); j++) {
                                        String val = options.get(j).getId();
                                        if (val.contains(inputValue)) {
                                            String[] op = val.split(":");
                                            value = op[0].replaceAll("['\\{\")]", "");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (value.contains(" --- ")) {
                        String[] arr1 = value.split(" --- ");
                        value = arr1[0].trim();
                    }
                    divhideshow(fieldId, value);
                }
            }
        }
    }

    //working code for checkbox hide show
    private void divhideshow(String id, String value) {
        try {
            String idlist = "";
            for (int i = 0; i < additionalFieldDataList.size(); i++) {
                Field field = additionalFieldDataList.get(i);
                if (field.getName().equals("stateids")) {
                    idlist = field.getValue();
                }
            }

            String checkstateid = "0";
            for (int i = 0; i < fieldsList.size(); i++) {
                Field fobj = fieldsList.get(i);
                if (fobj.getId().equals("statefield")) {
                    checkstateid = fobj.getValue();
                    break;
                }
            }

            String[] arr = idlist.split("@");
            for (int i = 0; i < arr.length; i++) {
                //e.g.c45689-true-c
                if (arr[i].indexOf("c" + id + "-" + value + "-c") > -1) {

                    if ((arr[i].length() - 3) > ("c" + id + "-" + value + "-c").length()) {
                        String checkedids = "/";
                        String checkedidshide = "/";
                        checkedids += id + "/";
                        String[] arr2 = arr[i].split("-c");
                        boolean doshow = true;

                        for (int j = 0; j < arr2.length; j++) {
                            String tmp = arr2[j].trim();
                            if (!tmp.equals("") && tmp.indexOf("-") > 0) {
                                if (tmp.indexOf('c') == 0) tmp = tmp.substring(1);
                                String[] arr3 = tmp.split("-");

                                if (checkedids.indexOf("/" + arr3[0] + "/") < 0) {
                                    for (int b = 0; b < fieldsList.size(); b++) {
                                        Field bObj = fieldsList.get(b);
                                        if (bObj.getId().matches("field" + arr3[0])) {

                                            if (bObj.getType().toLowerCase().matches("checkbox")) {

                                                if (bObj.getValue().matches("false") || bObj.getValue().isEmpty()) {
                                                    if (checkedidshide.indexOf("/" + arr3[0] + "/") < 0)
                                                        checkedidshide = checkedidshide.concat(arr3[0] + "/");
                                                }
                                            } else if (!bObj.getType().toLowerCase().matches("checkbox") && !bObj.getValue().matches(arr3[1])) {
                                                if (checkedidshide.indexOf("/" + arr3[0] + "/") < 0)
                                                    checkedidshide += arr3[0] + "/";
                                            } else {
                                                checkedids = checkedids.concat(arr3[0] + "/");
                                                checkedidshide = checkedidshide.replace("/" + arr3[0] + "/", "/");
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }// end of j for loop

                        if (!checkedidshide.equals("/")) doshow = false;
                        if (doshow && checkstateid != "0" && (arr[i].indexOf('s') == 0 || arr[i].indexOf("-c-s") > -1))
                            if (arr[i].indexOf('s' + checkstateid + 's') < 0 && arr[i].indexOf("s0s") < 0)
                                doshow = false;

                        if (doshow) {
                            for (int j = 0; j < fieldsList.size(); j++) {
                                Field fObj = fieldsList.get(j);

                                if (!fObj.getdListArray().isEmpty()) {

                                    for (int k = 0; k < fObj.getdListArray().size(); k++) {
                                        Field dlistObj = fObj.getdListArray().get(k);
                                        if (dlistObj.getStates().matches(arr[i]) && dlistObj.getType().equals("hidden")) {
                                            dlistObj.setType(dlistObj.getFieldType());
                                            Log.e(DEBUG_TAG, "field unhidden -> " + dlistObj.getFieldName());
                                        }
                                    }
                                } else {
                                    if (fObj.getStates().matches(arr[i]) && fObj.getType().equals("hidden")) {
                                        fObj.setType(fObj.getFieldType());
                                        // notifyAdapter(j);
                                    }
                                }
                                notifyAdapterWithPayLoad(j, Constant.PAYLOAD_HIDE_SHOW);
                            }
                            showHideDlistFields(arr[i], "c" + id + "-" + value + "-c");
                        } else {
                            for (int j = 0; j < fieldsList.size(); j++) {
                                Field fObj = fieldsList.get(j);

                                if (!fObj.getdListArray().isEmpty()) {

                                    for (int k = 0; k < fObj.getdListArray().size(); k++) {
                                        Field dlistObj = fObj.getdListArray().get(k);

                                        if (dlistObj.getStates().matches(arr[i])
                                                && !dlistObj.getType().equals("hidden")) {
                                            dlistObj.setFieldType(fObj.getType());
                                            dlistObj.setType("hidden");
                                            Log.e(DEBUG_TAG, "field hidden -> " + dlistObj.getFieldName());
                                        }

                                    }
                                } else {
                                    if (fObj.getStates().matches(arr[i])
                                            && !fObj.getType().equals("hidden")) {
                                        fObj.setFieldType(fObj.getType());
                                        fObj.setType("hidden");
                                        Log.e(DEBUG_TAG, "field hidden -> " + fObj.getFieldName());

                                    }
                                }
                                notifyAdapterWithPayLoad(j, Constant.PAYLOAD_HIDE_SHOW);
                            }
                        }
                    } else {
                        for (int j = 0; j < fieldsList.size(); j++) {
                            Field fObj = fieldsList.get(j);

                            if (!fObj.getdListArray().isEmpty()) {

                                for (int k = 0; k < fObj.getdListArray().size(); k++) {
                                    Field dlistObj = fObj.getdListArray().get(k);

                                    if (dlistObj.getStates().matches(arr[i])
                                            && dlistObj.getType().equals("hidden")) {
                                        dlistObj.setType(fObj.getFieldType());
                                        Log.e(DEBUG_TAG, "field unhidden -> " + dlistObj.getFieldName());
                                    }

                                }
                            } else {
                                if (fObj.getStates().matches(arr[i]) && fObj.getType().equals("hidden")) {
                                    fObj.setType(fObj.getFieldType());
                                    Log.e(DEBUG_TAG, "field unhidden -> " + fObj.getFieldName());
                                }
                            }
                            notifyAdapterWithPayLoad(j, Constant.PAYLOAD_HIDE_SHOW);
                        }
                    }
                } else if (arr[i].indexOf("c" + id + "-") > -1) {
                    for (int j = 0; j < fieldsList.size(); j++) {
                        Field fObj = fieldsList.get(j);

                        if (!fObj.getdListArray().isEmpty()) {

                            for (int k = 0; k < fObj.getdListArray().size(); k++) {
                                Field dlistObj = fObj.getdListArray().get(k);

                                if (dlistObj.getStates().matches(arr[i])
                                        && !dlistObj.getType().equals("hidden")) {
                                    dlistObj.setFieldType(fObj.getType());
                                    dlistObj.setType("hidden");
                                    Log.e(DEBUG_TAG, "field hidden -> " + dlistObj.getFieldName());
                                }
                            }
                        } else {

                            if (fObj.getStates().matches(arr[i]) && !fObj.getType().equals("hidden")) {
                                fObj.setFieldType(fObj.getType());
                                fObj.setType("hidden");
                                Log.e(DEBUG_TAG, "field hidden -> " + fObj.getFieldName());
                            }
                        }
                        notifyAdapterWithPayLoad(j, Constant.PAYLOAD_HIDE_SHOW);
                    }
                }
            }
            //   FieldHelper.hideShowMoreOptionsTab();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void notifyAdapter(final int position) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            if (adapter != null) {
                                adapter.notifyItemChanged(position);
                            }
                        }
                    };
                    handler.post(r);
                }
            });
        }
    }

    private void notifyAdapterWithPayLoad(final int position, final String payload) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            if (adapter != null) {
                                Log.e(DEBUG_TAG, "notifyAdapterWithPayLoad " + payload + " called");
                                adapter.notifyItemChanged(position, payload);
                            }
                        }
                    };
                    handler.post(r);
                }
            });
        }
    }

    private void showHideDlistFields(String arr, String value) {

        if (dlistArrayPosition != -1) {
            List<Field> dlistArray = fieldsList.get(dlistArrayPosition).getdListArray();


            for (int j = 0; j < dlistArray.size(); j++) {
                Field fObj = dlistArray.get(j);

                //zeroth row
                List<DList> dlistField = fObj.getdListsFields();
                for (int k = 0; k < dlistField.size(); k++) {
                    DList dobj = dlistField.get(k);

                    if (!dobj.getStates().isEmpty()) {
                        Log.e("SHOWHIDEDLISTFIELDS", "ARR = " + arr);
                        Log.e("SHOWHIDEDLISTFIELDS", "states = " + dobj.getStates());

                        if (dobj.getStates().indexOf(value) > -1) {
                            // if(dobj.getStates().matches(arr) && dobj.getType().equals("hidden")) {
                            dobj.setType(dobj.getFieldType());
                            Log.e("CHECKHIDESHOW", "DLISTFIELD SHOW ->" + dobj.getFieldName());
                        } else {
                            dobj.setType("hidden");
                            Log.e("CHECKHIDESHOW", "DLISTFIELD Hide ->" + dobj.getFieldName());
                        }


//                        if((dobj.getStates().matches(arr) && dobj.getType().equals("hidden")) ){
//
//                        }else{
//
//                        }

                    }
                }
            }
        }
    }

    private void load(int position, String onClickRightButton, boolean isKeyDown) {
        String type = fieldsList.get(position).getDataType();
        if (!onClickRightButton.isEmpty()) {
            String[] h = onClickRightButton.split("\\(");
            String[] f = h[1].replaceAll("['\\(\\)]", "").split(",");
            int fieldId = Integer.parseInt(f[0]);
            List<String> oArr = f[1].isEmpty() ? Collections.EMPTY_LIST : Collections.EMPTY_LIST; // TODO: replace the else condition later
            Boolean issql = Boolean.valueOf(f[2]);
            String extension = f[3];
            String jcodelist = f[4];

            if (isKeyDown) {
                boolean mdcombo = Boolean.parseBoolean(f[5].replace(";", ""));
                String url = functionHelper.load(fieldId, oArr, issql, extension, jcodelist, "", mdcombo, false);
                if (fetchCounter >= 0) {
                    loadSpinnerData("", url, position, mdcombo, type);
                }
            } else {
                String fieldtype = f[5];
                boolean mdcombo = Boolean.parseBoolean(f[6].replace(";", ""));
                //TODO: need to check this condition
                Pattern pattern = Pattern.compile("([0-9]{5,})(\\W{2})\\W");
                Matcher matcher = pattern.matcher(jcodelist);

                if (!matcher.matches()) {
                    String url = functionHelper.load(fieldId, oArr, issql, extension, jcodelist, fieldtype, mdcombo, false);
                    if (fetchCounter >= 0) {
                        loadSpinnerData("", url, position, mdcombo, type);
                    }
                }
            }
        }
    }

    private ResponseInterface mResponseInterface = new ResponseInterface() {
        @Override
        public void onSuccessResponse(String recordId, boolean isFieldTypeTag) {
            Log.e("mResponseInterface", "RECORD_ID = " + recordId);
            if (entryFrom != null && entryFrom.equals("pending_task")) {
                //setting record id here for dynamic button when in Pending Task Screen
                recordId = RECORD_ID;
            }

            RECORD_ID_REF = recordId;
            RECORD_ID = recordId;

            if (adapter != null) {
                adapter.setRecordId(RECORD_ID);
            }

            showDynamicButtons(RECORD_ID, isFieldTypeTag);
        }

        @Override
        public void onChangeState(String recordId) {
            // fetchingAfterSaving = true;
            Log.e(DEBUG_TAG, "onChangeState Called");
            clearFields();
            FetchRecordHelper fetchRecordHelper = new FetchRecordHelper(getActivity(), null);
            fetchRecordHelper.setFormId(FORM_ID);
            fetchRecordHelper.setRecordId(RECORD_ID);
            fetchRecordHelper.setInputInTagField(false);
            fetchRecordHelper.setCommunicatorInterface(communicatorListener);
            fetchRecordHelper.setFieldsList(fieldsList);
            fetchRecordHelper.setAdditionalFieldDataList(additionalFieldDataList);
            fetchRecordHelper.setDlistArrayPosition(dlistArrayPosition);
            fetchRecordHelper.setAdapter(adapter);
            fetchRecordHelper.callFetchRecordAPI();
        }
    };


    private void showDynamicButtons(String recordId, boolean isFieldTypeTag) {
        if (dynamicButtonHelper != null) {
            dynamicButtonHelper.setFormId(FORM_ID);
            dynamicButtonHelper.setCommunicatorInterface(communicatorListener);
            dynamicButtonHelper.setRecordId(recordId);
            dynamicButtonHelper.setButtonList(buttonList);
            dynamicButtonHelper.setRecyclerView(recyclerViewDynButton);
            dynamicButtonHelper.setButtonAdapter(buttonAdapter);
            dynamicButtonHelper.setInputInTagField(isFieldTypeTag);
            dynamicButtonHelper.setFormTitle(title);
            dynamicButtonHelper.callGetDynamicButtonsAPI();
        }

    }

    private DynamicButtonAdapter.DynamicButtonClickListener mDynamicButtonClickListener =
            new DynamicButtonAdapter.DynamicButtonClickListener() {
                @Override
                public void onDynamicButtonClicked(DynamicButton dynamicButton) {
                    if (dynamicButton.getValue().toLowerCase().equals("clear")) {
                        clearFields();
                    } else if (dynamicButton.getValue().toLowerCase().matches("print \\(1\\)|print")) {
                        PrintHelper printHelper = new PrintHelper((AppCompatActivity) getActivity(),
                                FORM_ID, RECORD_ID);
                        printHelper.setMandatoryFields(getMandatory());
                        printHelper.setTitle(title);

                        if (RECORD_ID.equals("0")) {
                            DialogUtil.showAlertDialog(getActivity(), "", "Please save a record for printing", false, false);
                        } else {
                            printUrl = printHelper.print();
                            if (!checkPermission()) {
                                requestPermission();
                            } else {
                                downloadReport(printUrl);
                            }
                        }

                    } else if (dynamicButton.getValue().toLowerCase().matches("attach")) {
                        onAttachPressed();
                    } else {
                        Bundle args = new Bundle();
                        args.putString(Constant.EXTRA_TITLE, dynamicButton.getValue());
                        args.putString(Constant.EXTRA_CLICK, dynamicButton.getOnClick());

                        //show a dialog when we click an actionable dynamic button
                        BottomSheetStatusDialogFragment bottomSheetFragment = new BottomSheetStatusDialogFragment(mStatusBottomSheetClickListener);
                        bottomSheetFragment.setArguments(args);
                        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
                    }
                }
            };

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    private void downloadReport(String printUrl) {
        Toast.makeText(getActivity(), title + " Download Started", Toast.LENGTH_SHORT).show();
        Log.e("PRINT URL", printUrl);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(printUrl));
        request.setDescription("Some Description");
        request.setTitle(title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length == 0) {
                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeAccepted) {
                        downloadReport(printUrl);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private BottomSheetStatusDialogFragment.StatusBottomSheetClickListener
            mStatusBottomSheetClickListener = new BottomSheetStatusDialogFragment.
            StatusBottomSheetClickListener() {
        @Override
        public void changeStatus(String title, String nextState) {
            // 1. on submit, call  bottom sheet dialog fragment for confirmation
            // 2. validation
            // 3. checksave
            // 4. call save dlist api
            // 5. call change status api

            if (NetworkUtil.isNetworkOnline(getActivity())) {
                if (title.toLowerCase().matches("save")) {
                    callSaveDlistAPI(nextState);
                } else {
                    String mandatory = separateMandatory(nextState);
                    WorkFlowMandatory workFlowMandatory = new WorkFlowMandatory(getActivity(), fieldsList, adapter);
                    workFlowMandatory.showMandatoryFields(mandatory, dlistArrayPosition);

                    validateHelper.setAdditionalFieldDataList(additionalFieldDataList);
                    validateHelper.setFieldsList(fieldsList);
                    validateHelper.setDlistArrayPosition(dlistArrayPosition);

                    if (validateHelper.checkMandatory(mandatory)) {

                        checkSaveHelper.setFormSaveCheck(form.getFormSaveCheck());
                        checkSaveHelper.setFormSaveCheckNames(form.getFormSaveCheckNames());
                        if (!checkSaveHelper.checkSave(validateProgressDialog)) {

                            final SaveRecordHelper saveRecordHelper = new SaveRecordHelper(getActivity(), mResponseInterface);
                            saveRecordHelper.setVlist(false);
                            saveRecordHelper.setFormId(FORM_ID);
                            saveRecordHelper.setRecordId(RECORD_ID);
                            saveRecordHelper.setFlag(false);
                            saveRecordHelper.setEditRecord(true);
                            saveRecordHelper.setChangeState(true);
                            saveRecordHelper.setNextState(nextState);
                            saveRecordHelper.setFormSave(form.getFormSave());
                            saveRecordHelper.setButtonTitle(title);
                            saveRecordHelper.setFieldsList(fieldsList);
                            saveRecordHelper.setAdditionalFieldDataList(additionalFieldDataList);
                            saveRecordHelper.setDlistArrayPosition(dlistArrayPosition);

                            new Thread(new Runnable() {
                                public void run() {
                                    saveRecordHelper.callSaveFormRecordAPI();
                                }
                            }).start();
                        }
                    }
                }
            } else {
                DialogUtil.showAlertDialog(getActivity(), "No Internet Connection!",
                        "Please check yours internet connection and try again", false, false);
            }
        }
    };

    private void onAttachPressed() {
        if (RECORD_ID.equals("0")) {
            DialogUtil.showAlertDialog(getActivity(), "", "You must select a record before uploading attachment", false, false);
        } else {
            attachFileHelpler.setAttachFileClicked(true);
            chooseFile();
        }
    }

    private String separateMandatory(String value) {
        String state = value;

        if (value.contains("SaveNextState")) {
            String[] m = state.split("SaveNextState");
            String n = m[1].replaceAll("[\\(\\)]", "");
            String[] j = n.split(",");
            String nextStateId = j[0].replace("\'", "");
            String mandatory = j[1].replace("\'", "");
            String nextStateName = j[2].replace("\'", "");
            return mandatory;
        } else {
            return value;
        }
    }

    private BottomSheetDropdown.BottomSheetClickListener bottomSheetClickListener =
            new BottomSheetDropdown.BottomSheetClickListener() {
                @Override
                public void getSelectValues(final String valueString, final int fieldPosition) {
                    Log.e(DEBUG_TAG, "getSelectValues VALUE ----> " + valueString);

                    String fieldValue = "";
                    if (fieldsList != null) {
                        fieldValue = fieldsList.get(fieldPosition).getValue();
                        if (!fieldValue.isEmpty()) {

                            if (!fieldValue.endsWith(", ")) {

                                int index = fieldValue.lastIndexOf(", ");
                                if (index > -1) {
                                    String searchKeyword = fieldValue.substring(index + 1);
                                    Log.e("SEARCH KEYWORD = ", searchKeyword);

                                    fieldValue = fieldValue.replace(searchKeyword, " ");
                                    fieldValue += valueString;
                                } else {
                                    fieldValue = valueString;
                                }
                            }
                        } else {
                            fieldValue = valueString;
                        }
                    }

                    onValueChanged(fieldPosition, fieldValue, "");
                    //do not change this
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(fieldPosition, PAYLOAD_TEXT);
                        }
                    });

                    final String onChange = fieldsList.get(fieldPosition).getOnChange();
                    final String fieldType = fieldsList.get(fieldPosition).getDataType();

                    if (fieldsList != null) {
                        onChange(fieldPosition, onChange, fieldValue,
                                fieldType);

                        checkHideShow(onChange);
                    }
                }
            };

    @Override
    public void onDestroyView() {
        ////clearMemory();
        super.onDestroyView();
    }

    private void clearMemory() {
        mPrefManager.setFieldPositionsToNotify("");
        deleteDlistRowsFromDb();

        additionalFieldDataList = null;
        fieldsList = null;
        buttonList = null;
        if (functionHelper != null) {
            functionHelper = null;
        }
        attachFileHelpler = null;
        dynamicButtonHelper = null;
        form = null;
        mPrefManager = null;
        buttonAdapter = null;
        adapter = null;
        dbManager = null;

        if (progressDialog1 != null) {
            progressDialog1.dismiss();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void deleteDlistRowsFromDb() {
        dbManager.deleteDList();
    }

    public FormFragment(CommunicatorInterface listener) {
        communicatorListener = listener;
    }

    public FormFragment() {

    }

    @Override
    public void onKeyDown(int position, Field obj) {
        if (!obj.getOnKeyDown().isEmpty()) {
            if (obj.getOnKeyDown().contains("load")) {
                //(24 Oct 2020 )made this change because,
                // read only field was not populating when we selected a value
                // from multiselect spinner
                // load(position, obj.getOnclickrightbutton());
                load(position, obj.getOnKeyDown(), true);
            }
        }
    }

    @Override
    public void onChange(final int position, final String onChange,
                         final String value, String fieldType) {
        try {
            if (!onChange.isEmpty()) {
                if (fieldsList != null) {
                    String[] jarr = onChange.split("\\);");
                    for (int i = 0; i < jarr.length; i++) {

                        if (jarr[i].contains("evaluatefunction")) {
                            evaluateFunction(jarr[i], value);
                        } else if (jarr[i].contains("clearfieldids")) {
                            clearFieldIds(jarr[i]);
                        } else if (jarr[i].contains("fn")) {
                            fn(jarr[i], value, position);

                        } else if (jarr[i].contains("checkpattern")) {
                            boolean isReadOnly = fieldsList.get(position).isReadOnly();
                            boolean hidden = fieldsList.get(position).isHidden();

                            if (!hidden) {
                                if ((!isReadOnly)) {
                                    if (!value.isEmpty()) {
                                        checkPattern(jarr[i], value, position);
                                    }
                                }
                            }
                        } else if (jarr[i].contains("checkrepeats")) {
                            checkRepeats(jarr[i]);
                        } else if (jarr[i].contains("evaluatesql")) {
                            evaluateSQL(jarr[i], value, position, false);
                        } else if (jarr[i].contains("daterange")) {
                            splitDateRange(position, jarr[i], value);
                        } else if (jarr[i].contains("checkunique")) {
                            splitCheckUnique(position, jarr[i], value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void splitCheckUnique(int position, String funcString, String value) {
        if (!funcString.isEmpty()) {
            String[] arr = funcString.split("checkunique");
            String[] darr = arr[1].split(",");
            final String formId = darr[0].replaceAll("['\\(\\)]", "");
            final String uniqueFieldId = darr[1].replaceAll("\'", "");
            fetchRecord(position, value, uniqueFieldId);
        }
    }

    private void splitDateRange(int position, String funcString, String selectedDate) {
        if (!funcString.isEmpty()) {
            String[] arr = funcString.split("daterange");
            String[] darr = arr[1].split(",");
            final String fieldId = darr[0].replaceAll("['\\(\\)]", "");
            final String dateRangeDates1 = darr[1].replaceAll("\'", "");
            final String dateRangeDates2 = darr[2].replaceAll("['\\(\\)]", "");

            DateRangeFunction dF = new DateRangeFunction(getActivity());
            dF.setAdapter(adapter);
            dF.setFieldsList(fieldsList);
            dF.dateRange(fieldId, dateRangeDates1, dateRangeDates2, selectedDate, position, false);
        }
    }

    @Override
    public void evaluateSqlWithPayload(int position, String onChange) {
        if (!onChange.isEmpty()) {
            if (fieldsList != null) {
                String funcString = "";
                Pattern pattern = Pattern.compile("evaluatesql.*?\\);");
                Matcher matcher = pattern.matcher(onChange);

                while (matcher.find()) {
                    funcString = matcher.group(0);
                    break;
                }

                if (funcString.contains("evaluatesql")) {
                    evaluateSQL(funcString, fieldsList.get(position).getValue(), position, true);
                }

            }
        }
    }


    @Override
    public void loadSpinner(int position, String onClickRightButton) {
        if (onClickRightButton.contains("load")) {
            load(position, onClickRightButton, false);
        }
    }

    @Override
    public void loadDatePicker(int position, String onChange) {
        displayDatePicker(position, onChange);
    }

    @Override
    public void onClickRightButton(int position, String onKeyDown,
                                   String onClickRightBtnFunc,
                                   String fieldType, String fieldName) {
        if (!onKeyDown.isEmpty()) {
            Bundle args = new Bundle();
            args.putString(Constant.EXTRA_ONKEY_DOWN, onKeyDown);
            args.putString(Constant.EXTRA_ONCLICK_RIGHT, onClickRightBtnFunc);
            args.putString(Constant.EXTRA_TYPE, fieldType);
            args.putString(Constant.EXTRA_FIELD_NAME, fieldName);
            args.putInt(EXTRA_POSITION, position);
            args.putBoolean(EXTRA_IS_DLIST, false);

            BottomSheetDropdown bottomSheetFragment = new BottomSheetDropdown(
                    bottomSheetClickListener, false);
            bottomSheetFragment.setArguments(args);
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        }
    }

    @Override
    public void onValueChanged(int position, String value, String onChange) {
        try {
            if (!fieldsList.isEmpty()) {
                fieldsList.get(position).setValue(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fetchRecord(int position, String recordId, String uniqueFieldId) {

        Log.e(DEBUG_TAG, "fetchRecord Called");
        isInputInTagField = true;
        //this gets called when you  type number in the autocomplete text view and its fieldtype is tag
        FetchRecordHelper fetchRecordHelper = new FetchRecordHelper(getActivity(),
                mResponseInterface);
        fetchRecordHelper.setFormId(FORM_ID);
        fetchRecordHelper.setRecordId(recordId);
        fetchRecordHelper.setRootLayout(llRecyclerViewRoot);
        fetchRecordHelper.setUniqueFieldId(uniqueFieldId);
        fetchRecordHelper.setInputInTagField(true);
        fetchRecordHelper.setFieldsList(fieldsList);
        fetchRecordHelper.setAdditionalFieldDataList(additionalFieldDataList);
        fetchRecordHelper.setDlistArrayPosition(dlistArrayPosition);
        fetchRecordHelper.setAdapter(adapter);
        fetchRecordHelper.callFetchRecordAPI();
    }

    @Override
    public void pickFile(int position) {
        fileFieldPosition = position;
        if (hasPermission()) {
            chooseFile();
        }
    }

    private void showNoInternetImage() {
        shimmerFrameLayout.setVisibility(View.GONE);
        llNoItemsView.setVisibility(View.VISIBLE);
        noItemTextView.setText(R.string.no_internet_title);
        noItemImage.setImageResource(R.drawable.ic_no_internet);
    }
}