package com.example.myapplication.user_interface.vlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.customviews.DatePickerDialogFragment;
import com.example.myapplication.customviews.NotScrollingToFocusedChildrenLinearLayoutManager;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.function.CheckPatternFunction;
import com.example.myapplication.function.CheckRepeatHelper;
import com.example.myapplication.function.CheckSaveHelper;
import com.example.myapplication.function.DateRangeFunction;
import com.example.myapplication.function.EvaluateFunctionHelper;
import com.example.myapplication.function.FunctionHelper;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.location.CurrentLocation;
import com.example.myapplication.network.FetchCheckRepeatDataHelper;
import com.example.myapplication.network.VolleyResponseListener;
import com.example.myapplication.network.VolleyUtils;
import com.example.myapplication.user_interface.dlist.controller.DListFieldHelper;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.user_interface.dynamicbutton.DynamicButton;
import com.example.myapplication.user_interface.dynamicbutton.DynamicButtonAdapter;
import com.example.myapplication.user_interface.dynamicbutton.DynamicButtonHelper;
import com.example.myapplication.user_interface.forms.controller.CommunicatorInterface;
import com.example.myapplication.user_interface.forms.controller.CustomDateTimePickerInterface;
import com.example.myapplication.user_interface.forms.controller.FormFieldInterface;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.user_interface.forms.controller.LocationInterface;
import com.example.myapplication.user_interface.forms.controller.OnBottomReachedListener;
import com.example.myapplication.user_interface.forms.controller.ResponseInterface;
import com.example.myapplication.user_interface.forms.controller.WorkFlowMandatory;
import com.example.myapplication.user_interface.forms.controller.helper.AttachFileHelpler;
import com.example.myapplication.user_interface.forms.controller.helper.FetchRecordHelper;
import com.example.myapplication.user_interface.forms.controller.helper.FieldHelper;
import com.example.myapplication.user_interface.forms.controller.helper.PrintHelper;
import com.example.myapplication.user_interface.forms.controller.helper.SaveRecordHelper;
import com.example.myapplication.user_interface.forms.controller.helper.ValidateFormHelper;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.model.Form;
import com.example.myapplication.user_interface.forms.model.OptionModel;
import com.example.myapplication.user_interface.forms.view.BottomSheetDropdown;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.pendingtask.BottomSheetStatusDialogFragment;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.DownloadReportUtil;
import com.example.myapplication.util.FileUtil;
import com.example.myapplication.util.KeyboardUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
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
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.myapplication.Constant.EXTRA_IS_DLIST;
import static com.example.myapplication.Constant.EXTRA_POSITION;
import static com.example.myapplication.Constant.PAYLOAD_TEXT;


public class VlistFormActivity extends AppCompatActivity implements
        FormFieldInterface,
        CustomDateTimePickerInterface {

    private static final int PERMISSION_REQUEST_CODE = 202;
    private final String DEBUG_TAG = getClass().getSimpleName();
    private SharedPrefManager prefManager;
    private String VFORM_ID = "";
    private String VRECORD_ID = "0";
    private String vList = "";
    public static String VRECORD_ID_REF = "0";
    private String entryFrom = "";
    private String vlistRelationIds = "";
    private String chartId = "";
    public static int vDlistArrayPosition = -1;
    private DatabaseManager dbManager;
    public static Form vForm;
    public static List<Field> vFieldsList;
    public static List<Field> vAdditionalFieldDataList;
    private DynamicButtonHelper dynamicButtonHelper;
    public static FormRecylerAdapter adapter;
    private RecyclerView recyclerviewForm;
    private RecyclerView recyclerViewDynButton;
    private LinearLayout llNoItemsView;
    private LinearLayout llRecyclerViewRoot;
    private FunctionHelper functionHelper;
    private AttachFileHelpler attachFileHelpler;
    private ValidateFormHelper validateHelper;
    private CheckSaveHelper checkSaveHelper;
    private Gson gson;
    private List<DynamicButton> buttonList;
    private DynamicButtonAdapter buttonAdapter;
    private ProgressDialog progressDialog, fetchProgressDialog;
    private String mode = "new";
    private String title = "";
    private int fetchCounter = 0;
    private int vFileFieldPosition = -1;
    private boolean isVInputInTagField = false;
    private String printUrl = "";
    private ProgressDialog validateProgressDialog;
    private Field mainFormFieldObj;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ImageView noItemImage;
    private TextView noItemTextView;

    CommunicatorInterface communicatorInterface = new CommunicatorInterface() {
        @Override
        public void respond() {
            onBackPressed();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_form);
        initViews();
        callGetChartIdApi();
    }

    private void initShimmer() {
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        deleteVlistDlist();
        super.onBackPressed();
    }

    private void deleteVlistDlist() {
        try{
        Log.e("deleteVlistDlist","CALLED");
        if(vFieldsList != null) {
            if(vDlistArrayPosition != -1) {
                List<Field> dlistButtonArray = vFieldsList.get(vDlistArrayPosition)
                        .getdListArray();

                for(int i=0; i < dlistButtonArray.size(); i++) {
                    Field dObj = dlistButtonArray.get(i);
                    if(dbManager != null) {
                        dbManager.deleteDListWithFieldId(dObj.getId());
                    }
                }
            }
        }
        }catch (Exception e){
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
    protected void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }

    private void notifyFieldPositions() {
        Log.e("notifyFieldPositions"," CALLED");
        if (prefManager != null) {
            if (vFieldsList != null) {
                String fieldPositions = prefManager.geVFieldPositions();
                if (!fieldPositions.isEmpty()) {
                    String[] posArr = fieldPositions.split(",");
                    for (int i = 0; i < posArr.length; i++) {
                        if(!posArr[i].isEmpty()){
                            notifyAdapterWithPayLoad(Integer.parseInt(posArr[i].trim()),Constant.PAYLOAD_TEXT_SQL);
                        }
                    }
                }
                prefManager.setVFieldPositionsToNotify("");
            }
        }
    }

    private void getArguments()
    {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            mode = bundle.getString(Constant.EXTRA_MODE);
            String json=  bundle.getString(Constant.EXTRA_DATA);
            mainFormFieldObj = (Field) JsonUtil.jsonToObject(json, Field.class);
            if(mainFormFieldObj != null){
                title = mainFormFieldObj.getFieldName();
                vList = mainFormFieldObj.getOnClickVList();
            }
            if(mode.equals("update")) {
                VRECORD_ID = bundle.getString(Constant.EXTRA_RECORD_ID);
            }
        }
    }

    private void initViews() {
        init();
        initShimmer();
        llNoItemsView = findViewById(R.id.no_items_view);
        llRecyclerViewRoot  = findViewById(R.id.ll_recylerview_form);
        noItemImage = findViewById(R.id.no_items_img);
        noItemTextView = findViewById(R.id.txt_msg);
        initRecyclerView();
        initButtonAdapter();
    }

    private void init() {
        initSharedPref();
        getArguments();
        initDatabase();
        setActionBar();
        initList();
        initClasses();
    }

    private void initList() {
        vFieldsList = new ArrayList<>();
        vAdditionalFieldDataList = new ArrayList<>();
    }

    private void initClasses() {
        initHelperClasses();
        initDynamicHelper();
        initCheckSaveHelper();
        gson = new Gson();
    }

    private void initCheckSaveHelper() {
        checkSaveHelper = new CheckSaveHelper(VlistFormActivity.this);
        checkSaveHelper.setVlist(true);
        checkSaveHelper.setFieldsList(vFieldsList);
        checkSaveHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
    }

    private void initDynamicHelper() {
        dynamicButtonHelper = new DynamicButtonHelper(VlistFormActivity.this,
                mResponseInterface);
        dynamicButtonHelper.setVlist(true);
    }

    private void initHelperClasses() {
        functionHelper = new FunctionHelper(VlistFormActivity.this);
        attachFileHelpler = new AttachFileHelpler(VlistFormActivity.this);
        validateHelper = new ValidateFormHelper(VlistFormActivity.this,true);
    }

    private void setActionBar() {
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");*/
    }

    private void initSharedPref() {
        prefManager = new SharedPrefManager(this);
    }

    private void initDatabase() {
        dbManager = new DatabaseManager(this);
        dbManager.open();
    }

    public void initRecyclerView() {
        recyclerviewForm = findViewById(R.id.recylerview_form);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // recyclerviewForm.setHasFixedSize(true);
        // Stops items doing a default flashing animation on individual refresh
        RecyclerView.ItemAnimator animator = recyclerviewForm.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        NotScrollingToFocusedChildrenLinearLayoutManager llm = new NotScrollingToFocusedChildrenLinearLayoutManager(VlistFormActivity.this);
        llm.setAutoMeasureEnabled(false);
        recyclerviewForm.setLayoutManager(llm);
    }

    private void callGetChartIdApi() {
        VFORM_ID = prefManager.getVFormId();
        if (NetworkUtil.isNetworkOnline(this)) {
            String URL_GET = String.format(Constant.URL_SINGLE_CHART_ID,
                    prefManager.getClientServerUrl(),
                    VFORM_ID,
                    prefManager.getAuthToken(),
                    prefManager.getCloudCode());

            Log.e(DEBUG_TAG, "callGetChartIdApi url = " + URL_GET);

            VolleyUtils.GET_METHOD(VlistFormActivity.this, URL_GET, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    System.out.println("Error" + message);

                }

                @Override
                public void onResponse(Object response) {
                    Log.e("SUCCESS" , (String) response);
                    try {
                        JSONArray jsonArray = new JSONArray((String) response);
                        for(int i=0; i< jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            chartId = jsonObject.getString("Chart_ID");
                        }
                        if(!chartId.isEmpty() && !chartId.equals("0")){
                            llNoItemsView.setVisibility(View.GONE);
                            loadFormApi(chartId);
                        }else{
                                llNoItemsView.setVisibility(View.VISIBLE);
                        }

                    }catch (Exception e ){
                        e.printStackTrace();
                    }
                }
            });
        }else{
            ToastUtil.showToastMessage("Please check your internet connection and try again",this);
        }
    }

    private void loadFormApi(String chartId) {
        if (NetworkUtil.isNetworkOnline(this)) {
            generateFormUrl(chartId);
        }else {
            ToastUtil.showToastMessage("Please check your internet connection and try again",this);
        }
    }

    private void generateFormUrl(String chartId) {
        try {
            boolean formAvailable = dbManager.checkIfFormExists(Integer.parseInt(chartId));
            if (NetworkUtil.isNetworkOnline(this)) {
                String dateTime = "";
                if (!formAvailable) {
                    dateTime = Constant.SERVER_DATE_TIME;
                } else {
                    dateTime = DateUtil.getCurrentDate("yyyy-MM-dd hh:mm:ss");
                }

                String formURL = String.format(Constant.FORM_URL,
                        prefManager.getClientServerUrl(),
                        chartId, dateTime, prefManager.getCloudCode(), prefManager.getAuthToken());
                Log.e("FORM_URL", formURL);
                callFormApi(formURL);
            } else {
                if (formAvailable) {
                    buildFormUI(chartId);
                } else {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    showNoInternetImage();
//                    ToastUtil.showToastMessage(getResources()
//                                    .getString(R.string.no_internet_message),
//                            this);
                }
            }
        }catch (Exception  e){
            e.printStackTrace();
        }
    }

    private void showNoInternetImage() {
        shimmerFrameLayout.setVisibility(View.GONE);
        llNoItemsView.setVisibility(View.VISIBLE);
        noItemTextView.setText(R.string.no_internet_title);
        noItemImage.setImageResource(R.drawable.ic_no_internet);
    }

    private void callFormApi(String formUrl) {
        Log.i("TAG","FORM URL - > " + formUrl);

        showProgressDialog();
        VolleyUtils.GET_METHOD(VlistFormActivity.this, formUrl, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                System.out.println("Error" + message);
                dismissProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                Log.e("SUCCESS" , (String) response);
                Log.i(getClass().getSimpleName(), "formFieldAPI Response Received");

                String mResponse = (String)response;
                if (!mResponse.isEmpty()) {
                    if (dbManager != null) {
                        boolean formAvailable = dbManager.checkIfFormExists(Integer.parseInt(chartId));
                        if (formAvailable) {
                            dbManager.updateForm(Integer.parseInt(chartId), mResponse);
                        } else {
                            dbManager.insertForm(Integer.parseInt(chartId), "no title", mResponse);
                        }
                    }
                    buildFormUI(chartId);
                } else if (response.equals("")) {
                    buildFormUI(chartId);
                }

                dismissProgressDialog();

            }
        });
    }

    private void showFetchProgressDialog(){
        if (fetchCounter == 0) {
            fetchProgressDialog = ProgressDialog.show(this,
                    "", "Fetching Details ...",
                    false);
        }
        fetchCounter++;
    }

    private void dismissFetchProgressDialog(){
        fetchCounter--;
        if (fetchCounter == 0) {
            if ((fetchProgressDialog != null) && fetchProgressDialog.isShowing()) {
                fetchProgressDialog.dismiss();
            }
        }
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void buildFormUI(String chartId) {
        String response = dbManager.getFormJson(Integer.parseInt(chartId));

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject json = jsonObject.getJSONObject("json");
            formObject(json);

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

                            if(str.length > 0){
                                for (int a = 0; a < str.length; a++) {
                                    String[] op = str[a].split(":");
                                    // Log.e("OPTIONS STRING ",  "str[a] =" + str[a].toString());

                                    String mOp0 = op[0].trim().replaceAll("[\"\\{\\}]", "");
                                    String mOp1 = op[1].trim().replaceAll("[\"\\{\\}]", "");
                                    optionsArrayList.add(new OptionModel(mOp0, mOp1));
                                    // Log.e("OPTIONS STRING ", op[0].toString() + "  op1=" + op[1].toString());

                                }
                                optionsArrayList.add(0, new OptionModel("",""));
                            }
                        }
                    }

                    if (default_value.toLowerCase().matches("sql\\$\\{self\\}|\\$username")) {
                        optionsArrayList.add(new OptionModel(prefManager.getUserName(), prefManager.getUserName()));
                    }

                    String searchRequired = "";
                    if (obj.has("search_required")) {
                        searchRequired = obj.getString("search_required");
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
                        }else if(default_value.matches("\\$\\{now\\}|field\\$\\{now\\}")){
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

                    String vlistRelationIds = "";
                    if(obj.has("vlistrelationids")){
                        vlistRelationIds = obj.getString("vlistRelatioIds");
                    }

                    String vlistDefaultIds = "";
                    if(obj.has("vlistdefaultids")){
                        vlistDefaultIds = obj.getString("vlistdefaultids");
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
                            if(k == 0){
                                //code for setting fieldid_0 eg. fieldid50412_0
                                dlistObject = new DList();
                                String fieldid = id.replace("field","fieldid");
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

                                    if(str.length > 0){
                                        for (int a = 0; a < str.length; a++) {
                                            String[] op = str[a].split(":");
                                            // Log.e("OPTIONS STRING ",  "str[a] =" + str[a].toString());

                                            String mOp0 = op[0].trim().replaceAll("[\"\\{\\}]", "");
                                            String mOp1 = op[1].trim().replaceAll("[\"\\{\\}]", "");
                                            dOptionsArrayList.add(new OptionModel(mOp0, mOp1));
                                            // Log.e("OPTIONS STRING ", op[0].toString() + "  op1=" + op[1].toString());

                                        }
                                        dOptionsArrayList.add(0, new OptionModel("",""));
                                    }
                                }
                                dlistObject.setOptionsArrayList(dOptionsArrayList);
                            }

                            if(kObj.has("defaultvalue")) {
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
                                if(mStates.startsWith("s")){
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
                                    value, fieldType, onClickSummary, options, newRecord, dlistField, optionsArrayList, "", "",field_type,"","");
                            DlistArray.add(dlistF);

                            //DList which we well show as a button
                            Field field = new Field(waterMark, showFieldName, saveRequired, jFunction,
                                    "", onKeyDown, default_value, "DLIST", sqlValue,
                                    fieldName, relation, "", relationField,
                                    onClickVList, jIdList, "", searchRequired, "", placeHolder,
                                    value, fieldType, onClickSummary, newRecord, DlistArray, optionsArrayList,
                                    onClickRightButton, webSaveRequired,field_type);


                            //add it to the form
                            int dListIndex = getDListArray();
                            if (dListIndex != -1) {
                                List<Field> dListArray = vFieldsList.get(dListIndex).getdListArray();
                                dListArray.add(dlistF);
                            } else {
                                vFieldsList.add(field);
                            }
                        }
                    } else {
                        Field field = new Field(waterMark, showFieldName, saveRequired, jFunction,
                                onChange, onKeyDown, default_value, type, sqlValue,
                                fieldName, relation, states, relationField,
                                onClickVList, jIdList, name, width, searchRequired, id, placeHolder,
                                value, fieldType, onClickSummary, newRecord, options, dlistField, optionsArrayList,
                                onClickRightButton, webSaveRequired,field_type,vlistRelationIds,vlistDefaultIds);

                        vFieldsList.add(field);
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
                            VFORM_ID = obj.getString("value");
                        }
                        value = obj.getString("value");
                    }
                    vAdditionalFieldDataList.add(new Field(showFieldName, name, id, type, value));
                }
                vDlistArrayPosition = getDListArray();
                callAdapter();
            }
            showDynamicButtons(VRECORD_ID,false);

            if(mode.equals("new")) {
                callVlistRelationHelper();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            callAdapter();
        }
    }

    private void callVlistRelationHelper() {
        VlistRelationIdsHelper vH = new VlistRelationIdsHelper();
        vH.setvList(mainFormFieldObj.getOnClickVList());
        vH.setVlistRelationIds(mainFormFieldObj.getVlistRelationIds());
        vH.setVlistDefaultIds(mainFormFieldObj.getVlistDefaultIds());
        vH.setFieldsList(FormFragment.fieldsList);
        vH.setvFieldsList(vFieldsList);
        vH.setAdapter(adapter);
        vH.setDlistArrayPosition(FormFragment.dlistArrayPosition);
        vH.setValueForRelationIds();
    }
    private int getDListArray() {
        for (int i = 0; i < vFieldsList.size(); i++) {
            if (!vFieldsList.get(i).getdListArray().isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private void callAdapter() {
        if (vFieldsList.isEmpty()) {
            shimmerFrameLayout.setVisibility(View.GONE);
            llNoItemsView.setVisibility(View.VISIBLE);
            noItemTextView.setText(R.string.no_result_found);
            noItemImage.setImageResource(R.drawable.ic_list);
        } else {
            functionHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
            functionHelper.setFieldsList(vFieldsList);
            functionHelper.setDlistArrayPosition(vDlistArrayPosition);

            llNoItemsView.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
            adapter = new FormRecylerAdapter(VlistFormActivity.this, vFieldsList, this,"vform",true);
            //added this as hidden fields were getting called when i was trying to type in edit text in recyclerview - Pre Registration Form
            recyclerviewForm.setItemViewCacheSize(vFieldsList.size());
            recyclerviewForm.setAdapter(adapter);
            adapter.setRecordId(VRECORD_ID);
            adapter.setFormId(VFORM_ID);
            adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                @Override
                public void onBottomReached(final int position) {
//                    if(entryFrom != null && entryFrom.equals("pending_task") || isInputInTagField ){
//                        adapter.setFlag(false);
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                // fetchingAfterSaving = false;
//                                if(adapter != null){
//                                    adapter.setFlag(true);
//                                }
//                            }
//                        },getDelay());
//                    }
                }
            });
            adapter.setCustomDateTimePickerListener(this);
            adapter.setLocationListener(new LocationInterface() {
                @Override
                public void displayLocation(int position, String type ) {
                    CurrentLocation currentLocation = new CurrentLocation(VlistFormActivity.this);
                    currentLocation.setPosition(position);
                    currentLocation.setType(type);
                    currentLocation.getLocation();
                }
            });
        }

    }

    private void notifyAdapter(final int position) {
        if(this != null){
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            if(adapter != null){
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
        if(this != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            if(adapter != null){
                                Log.e(getClass().getSimpleName(), "notifyAdapterWithPayLoad "+payload+ " called");
                                adapter.notifyItemChanged(position,payload);
                            }
                        }
                    };
                    handler.post(r);
                }
            });
        }
    }

    private void callAPI(String url, final boolean isPayLoad) {
        KeyboardUtil.hideKeyboard(VlistFormActivity.this);
        boolean isValidURL = false;
        if (url != null && !url.isEmpty()) {
            isValidURL = android.util.Patterns.WEB_URL.matcher(url).matches();
        }
        if (isValidURL) {
            if (NetworkUtil.isNetworkOnline(VlistFormActivity.this)) {

                showFetchProgressDialog();

                Log.i(getClass().getSimpleName(),"callAPI URL === " + url);

                RequestQueue queue = Volley.newRequestQueue(VlistFormActivity.this);
                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.v(getClass().getSimpleName(),"callAPI RESPONSE === " + response);
                                    JSONObject jsonData = new JSONObject(response);
                                    if(jsonData != null) {
                                        if(vFieldsList != null){
                                            for (int i = 0; i < jsonData.names().length(); i++) {
                                                for (int j = 0; j < vFieldsList.size(); j++) {
                                                    Field fieldObj = vFieldsList.get(j);
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
                                                        if(isPayLoad){
                                                            notifyAdapterWithPayLoad(j,Constant.PAYLOAD_TEXT);
                                                        }else{
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
                                                        if(rows > 50){
                                                            rows = 50;
                                                            //  ToastUtil.showToastMessage("You can select a max of 50 rows only", getActivity());
                                                        }
                                                        if(dbManager != null){
                                                            dbManager.deleteDListWithFieldId(id);
                                                        }

                                                        FetchRecordHelper fh = new FetchRecordHelper(VlistFormActivity.this, null);
                                                        fh.setFieldsList(vFieldsList);
                                                        fh.setDlistArrayPosition(vDlistArrayPosition);
                                                        fh.setAdditionalFieldDataList(vAdditionalFieldDataList);

                                                        for (int j = 1; j <= rows; j++) {
                                                            dlistValuesArray = fh.getDListFieldObjectArray(id,true);
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

                                                            if(j != 0){
                                                                DListItem dListItem = new DListItem(dlistField);
                                                                String dlistJson = gson.toJson(dListItem);
                                                                Log.e("call api", "Converting dlist to json ----->" +  dlistJson);
                                                                dbManager.insertDListRow(Integer.parseInt(VFORM_ID),title,id,"",dlistJson,j);
                                                            }
                                                        }//end of row for loop
                                                    }// end of if
                                                }//end of jsonArray for loop

                                                if (rows != 0) {
                                                    DListFieldHelper fieldHelper = new DListFieldHelper(VlistFormActivity.this);
                                                    fieldHelper.updateContentRows(fieldId, rows);
                                                }

                                            }// end of if
                                        }
                                    }

                                    dismissFetchProgressDialog();
                                } catch (Exception e) {
                                    dismissFetchProgressDialog();
                                    e.printStackTrace();

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissFetchProgressDialog();
                    }
                });
                queue.add(request);
            } else {
                ToastUtil.showToastMessage("Please check your internet connection and try again", VlistFormActivity.this);
            }
        } else {
            Log.e(getClass().getSimpleName(), "#line553-- BAD URL =" + url);
        }
    }

    private void formObject(JSONObject json) {

        try {

            String formSaveCheck = "";
            if (json.has("formsavecheck")) {
                formSaveCheck = json.getString("formsavecheck");
            }
            String formSaveCheckNames = "";
            if (json.has("formsavechecknames")) {
                formSaveCheckNames = json.getString("formsavechecknames");
            }

            vForm = new Form(json.getString("enctype"),
                    json.getString("data-title"),
                    json.getString("form-method"),
                    json.getString("form-id"),
                    json.getString("id"),
                    json.getString("form-save"),
                    formSaveCheck,
                    formSaveCheckNames);

            String mtitle = vForm.getDataTitle();
            title = mtitle.contains("-->") ? mtitle.split("-->")[1] : mtitle;
            getSupportActionBar().setTitle(title);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initButtonAdapter() {
        buttonList = new ArrayList<>();
        recyclerViewDynButton = findViewById(R.id.recyclerview_buttons);
        recyclerViewDynButton.setLayoutManager(new GridLayoutManager(this,
                3));
        buttonAdapter = new DynamicButtonAdapter(this, buttonList,
                mDynamicButtonClickListener);
        recyclerViewDynButton.setAdapter(buttonAdapter);
    }

    private void showDynamicButtons(String recordId, boolean isFieldTypeTag){
        if(dynamicButtonHelper != null){
            dynamicButtonHelper.setFormId(VFORM_ID);
            dynamicButtonHelper.setCommunicatorInterface(communicatorInterface);
            dynamicButtonHelper.setRecordId(recordId);
            dynamicButtonHelper.setButtonList(buttonList);
            dynamicButtonHelper.setRecyclerView(recyclerViewDynButton);
            dynamicButtonHelper.setButtonAdapter(buttonAdapter);
            dynamicButtonHelper.setInputInTagField(isFieldTypeTag);
            dynamicButtonHelper.setFormTitle(title);
            dynamicButtonHelper.callGetDynamicButtonsAPI();
        }

    }

    @Override
    public void loadCustomDateTimePicker(int position, String onChange) {

    }

    private BottomSheetDropdown.BottomSheetClickListener bottomSheetClickListener =
            new BottomSheetDropdown.BottomSheetClickListener() {
                @Override
                public void getSelectValues(final String valueString, final int fieldPosition) {
                    Log.e(getClass().getSimpleName(), "getSelectValues VALUE ----> " + valueString);

                    Log.e(DEBUG_TAG, "getSelectValues VALUE ----> " + valueString);

                    String fieldValue = "";
                    if(vFieldsList != null) {
                        fieldValue = vFieldsList.get(fieldPosition).getValue();
                        if(!fieldValue.isEmpty()){

                            if(!fieldValue.endsWith(", ")) {

                                int index = fieldValue.lastIndexOf(", ");
                                if(index > -1) {
                                    String searchKeyword = fieldValue.substring(index + 1);
                                    Log.e("SEARCH KEYWORD = " ,searchKeyword );

                                    fieldValue = fieldValue.replace(searchKeyword, " ");
                                    fieldValue +=  valueString;
                                }else{
                                    fieldValue = valueString;
                                }
                            }
                        }else{
                            fieldValue = valueString;
                        }
                    }


                    onValueChanged(fieldPosition, fieldValue,"");
                    //do not change this
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(fieldPosition,PAYLOAD_TEXT);
                        }
                    });

                    final String onChange = vFieldsList.get(fieldPosition).getOnChange();
                    final String fieldType = vFieldsList.get(fieldPosition).getDataType();

                    if(vFieldsList != null){
                        onChange(fieldPosition, onChange, fieldValue,
                                fieldType);

                        checkHideShow(onChange);
                    }
                }
            };

    private DynamicButtonAdapter.DynamicButtonClickListener mDynamicButtonClickListener =
            new DynamicButtonAdapter.DynamicButtonClickListener() {
                @Override
                public void onDynamicButtonClicked(DynamicButton dynamicButton) {
                   if (dynamicButton.getValue().toLowerCase().equals("clear")) {
                        clearFields();
                    } else if (dynamicButton.getValue().toLowerCase().matches("print \\(1\\)|print")) {
                        PrintHelper printHelper = new PrintHelper(VlistFormActivity.this,
                                VFORM_ID, VRECORD_ID);
                        printHelper.setMandatoryFields(getMandatory());
                        printHelper.setTitle(title);

                        if (VRECORD_ID.equals("0")) {
                            DialogUtil.showAlertDialog(VlistFormActivity.this, "", "Please save a record for printing", false, false);
                        } else {
                            printUrl = printHelper.print();
                            if (!checkPermission()) {
                                requestPermission();
                            } else {
                                DownloadReportUtil.getInstance(VlistFormActivity.this).downloadReport(printUrl,title);
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
                        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                    }
                }
            };

    private String getMandatory() {
        String mandatory = "";
        for (int i = 0; i < vAdditionalFieldDataList.size(); i++) {
            Field fObj = vAdditionalFieldDataList.get(i);
            if (fObj.getName().toLowerCase().matches("mandatory")) {
                mandatory = fObj.getValue();
                break;
            }
        }
        return mandatory;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(VlistFormActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(VlistFormActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    private void onAttachPressed(){
        if(VRECORD_ID.equals("0")){
            DialogUtil.showAlertDialog(VlistFormActivity.this, "",   "You must select a record before uploading attachment", false, false);
        }else{
            attachFileHelpler.setAttachFileClicked(true);
            chooseFile();
        }
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

            if (NetworkUtil.isNetworkOnline(VlistFormActivity.this)) {
                if(title.toLowerCase().matches("save")){
                    callSaveDlistAPI(nextState);
                }else{
                    String mandatory = separateMandatory(nextState);
                    WorkFlowMandatory workFlowMandatory = new WorkFlowMandatory(VlistFormActivity.this,
                            vFieldsList,
                            adapter);
                    workFlowMandatory.showMandatoryFields(mandatory,vDlistArrayPosition);

                    validateHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
                    validateHelper.setFieldsList(vFieldsList);
                    validateHelper.setDlistArrayPosition(vDlistArrayPosition);

                    if (validateHelper.checkMandatory(mandatory)) {
                        checkSaveHelper.setFormSaveCheck(vForm.getFormSaveCheck());
                        checkSaveHelper.setFormSaveCheckNames(vForm.getFormSaveCheckNames());
                        if(!checkSaveHelper.checkSave(validateProgressDialog)){
                            final SaveRecordHelper saveRecordHelper = new SaveRecordHelper(VlistFormActivity.this,mResponseInterface);
                            saveRecordHelper.setVlist(true);
                            saveRecordHelper.setFormId(VFORM_ID);
                            saveRecordHelper.setRecordId(VRECORD_ID);
                            saveRecordHelper.setFlag(false);
                            saveRecordHelper.setEditRecord(true);
                            saveRecordHelper.setChangeState(true);
                            saveRecordHelper.setNextState(nextState);
                            saveRecordHelper.setFormSave(vForm.getFormSave());
                            saveRecordHelper.setButtonTitle(title);

                            new Thread(new Runnable() {
                                public void run() {
                                    saveRecordHelper.callSaveFormRecordAPI();
                                }
                            }).start();
                        }
                    }
                }
            }else{
                DialogUtil.showAlertDialog(VlistFormActivity.this,
                        "No Internet Connection!",
                        "Please check yours internet connection and try again",
                        false,
                        false);
            }
        }
    };

    private String separateMandatory(String value){
        String state = value;

        if(value.contains("SaveNextState")){
            String[] m = state.split("SaveNextState");
            String n = m[1].replaceAll("[\\(\\)]","");
            String[] j = n.split(",");
            String nextStateId = j[0].replace("\'","");
            String mandatory = j[1].replace("\'","");
            String nextStateName = j[2].replace("\'","");
            return mandatory;
        }else{
            return value;
        }
    }

    private void callSaveDlistAPI(String nextState) {

        if (NetworkUtil.isNetworkOnline(VlistFormActivity.this)) {

            validateHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
            validateHelper.setFieldsList(vFieldsList);
            validateHelper.setDlistArrayPosition(vDlistArrayPosition);

            if(validateHelper.areSaveRequiredFieldsValidated()) {

                if (validateHelper.checkMandatory("")) {

                    checkSaveHelper.setFormSaveCheck(vForm.getFormSaveCheck());
                    checkSaveHelper.setFormSaveCheckNames(vForm.getFormSaveCheckNames());
                    if (!checkSaveHelper.checkSave(validateProgressDialog)) {
                        final SaveRecordHelper saveRecordHelper = new SaveRecordHelper(VlistFormActivity.this,
                                mResponseInterface);
                        saveRecordHelper.setFormSave(vForm.getFormSave());
                        saveRecordHelper.setVlist(true);
                        saveRecordHelper.setFieldsList(vFieldsList);
                        saveRecordHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
                        saveRecordHelper.setDlistArrayPosition(vDlistArrayPosition);

                        if(!VRECORD_ID.equals("0")){
                            saveRecordHelper.setRecordId(VRECORD_ID);
                            saveRecordHelper.setFlag(false);
                            saveRecordHelper.setChangeState(true);
                            saveRecordHelper.setEditRecord(true);
                            saveRecordHelper.setFormTitle(title);
                            saveRecordHelper.setNextState(nextState);
                            saveRecordHelper.setVlist(true);
                            saveRecordHelper.setFieldsList(vFieldsList);
                            saveRecordHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
                            saveRecordHelper.setDlistArrayPosition(vDlistArrayPosition);
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
            DialogUtil.showAlertDialog(VlistFormActivity.this,
                    "No Internet Connection!",
                    "Please check your internet connection and try again",
                    false,
                    false);
        }
    }


    private ResponseInterface mResponseInterface = new ResponseInterface() {
        @Override
        public void onSuccessResponse(String recordId, boolean isFieldTypeTag) {
            Log.e("mResponseInterface","RECORD_ID = " + recordId);
            if(entryFrom != null && entryFrom.equals("pending_task")) {
                //setting record id here for dynamic button when in Pending Task Screen
                recordId = VRECORD_ID;
            }

          //  RECORD_ID_REF = recordId;
            VRECORD_ID = recordId;

            if(adapter != null){
                adapter.setRecordId(VRECORD_ID);
            }

            showDynamicButtons(VRECORD_ID, isFieldTypeTag);
        }

        @Override
        public void onChangeState(String recordId) {
            // fetchingAfterSaving = true;
            Log.e(getClass().getSimpleName(), "onChangeState Called");
            clearFields();
            FetchRecordHelper fetchRecordHelper = new FetchRecordHelper(VlistFormActivity.this,null);
            fetchRecordHelper.setFormId(VFORM_ID);
            fetchRecordHelper.setRecordId(VRECORD_ID);
            fetchRecordHelper.setInputInTagField(false);
            fetchRecordHelper.setCommunicatorInterface(communicatorInterface);
            fetchRecordHelper.setFieldsList(vFieldsList);
            fetchRecordHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
            fetchRecordHelper.setDlistArrayPosition(vDlistArrayPosition);
            fetchRecordHelper.setAdapter(adapter);
            fetchRecordHelper.callFetchRecordAPI();
        }
    };

    private void clearFields() {
        for (int i = 0; i < vFieldsList.size(); i++) {
            vFieldsList.get(i).setValue("");
            if (i == vDlistArrayPosition) {
                List<Field> dlistArray = vFieldsList.get(i).getdListArray();
                for (int j = 0; j < dlistArray.size(); j++) {
                    Field fieldObj = dlistArray.get(j);
                    fieldObj.getDListItemList().clear();
                }
            }
        }
        dbManager.deleteDList();
        // adapter.notifyDataSetChanged();
    }

    @Override
    public void onKeyDown(int position, Field obj) {
        if(vFieldsList != null){
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
    }

    @Override
    public void onChange(int position, String onChange, String value, String fieldType) {
        try {
            if (!onChange.isEmpty()) {
                if (vFieldsList != null) {
                    String[] jarr = onChange.split("\\);");
                    for (int i = 0; i < jarr.length; i++) {

                        if (jarr[i].contains("evaluatefunction")) {
                            evaluateFunction(jarr[i], value);
                        } else if (jarr[i].contains("clearfieldids")) {
                            clearFieldIds(jarr[i]);
                        } else if (jarr[i].contains("fn")) {
                            fn(jarr[i], value, position);

                        } else if (jarr[i].contains("checkpattern")) {
                            boolean isReadOnly = vFieldsList.get(position).isReadOnly();
                            boolean hidden = vFieldsList.get(position).isHidden();

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
                        }else if(jarr[i].contains("daterange")){
                            splitDateRange(position,jarr[i],value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void splitDateRange(int position, String funcString,String selectedDate) {
        if (!funcString.isEmpty()) {
            String[] arr = funcString.split("daterange");
            String[] darr = arr[1].split(",");
            final String fieldId = darr[0].replaceAll("['\\(\\)]", "");
            final String dateRangeDates1 = darr[1].replaceAll("\'", "");
            final String dateRangeDates2 = darr[2].replaceAll("['\\(\\)]", "");

            DateRangeFunction dF = new DateRangeFunction(VlistFormActivity.this);
            dF.setAdapter(adapter);
            dF.setFieldsList(vFieldsList);
            dF.dateRange(fieldId,dateRangeDates1,dateRangeDates2,selectedDate,position,false);

        }
    }

    @Override
    public void evaluateSqlWithPayload(int position, String onChange) {
        if (!onChange.isEmpty()) {
            if (vFieldsList != null) {
                String funcString = "";
                Pattern pattern = Pattern.compile("evaluatesql.*?\\);");
                Matcher matcher = pattern.matcher(onChange);
                while (matcher.find()) {
                    funcString = matcher.group(0);
                    break;
                }

                if (funcString.contains("evaluatesql")) {
                    evaluateSQL(funcString, vFieldsList.get(position).getValue(), position, true);
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

    private void load(int position, String onClickRightButton, boolean isKeyDown) {
        if (vFieldsList != null) {
        String type = vFieldsList.get(position).getDataType();
        if (!onClickRightButton.isEmpty()) {
            String[] h = onClickRightButton.split("\\(");
            String[] f = h[1].replaceAll("['\\(\\)]", "").split(",");
            int fieldId = Integer.parseInt(f[0]);
            List<String> oArr = f[1].isEmpty() ? Collections.EMPTY_LIST : Collections.EMPTY_LIST; // TODO: replace the else condition later
            Boolean issql = Boolean.valueOf(f[2]);
            String extension = f[3];
            String jcodelist = f[4];
          //  FunctionHelper funcHelper = new FunctionHelper(VlistFormActivity.this);
            if (isKeyDown) {
                boolean mdcombo = Boolean.parseBoolean(f[5].replace(";", ""));
                String url = functionHelper.load(fieldId, oArr, issql, extension, jcodelist, "", mdcombo,true);
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
                    String url = functionHelper.load(fieldId, oArr, issql, extension, jcodelist, fieldtype, mdcombo,false);
                    if (fetchCounter >= 0) {
                        loadSpinnerData("", url, position, mdcombo, type);
                    }
                }
            }
        }
        }
    }

    private void loadSpinnerData(final String field,
                                 String url,
                                 final int position,
                                 final boolean mdcombo,
                                 final String type) {
        KeyboardUtil.hideKeyboard(VlistFormActivity.this);
        boolean isValidURL = false;
        if (url != null && !url.isEmpty()) {
            isValidURL = android.util.Patterns.WEB_URL.matcher(url).matches();
        }
        if (isValidURL) {
            if (NetworkUtil.isNetworkOnline(VlistFormActivity.this)) {
                showFetchProgressDialog();

                Log.e("FETCH_COUNTER", "loadSpinnerData" + String.valueOf(fetchCounter));
                Log.e(DEBUG_TAG, "loadSpinnerData URL=" + url);
                RequestQueue queue = Volley.newRequestQueue(VlistFormActivity.this);
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


                                            vFieldsList.get(position).setOptionsArrayList(list);
                                            if (this != null) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                });
                                            }
                                        } else if (type.equalsIgnoreCase("adcombo")) {
                                            final List<OptionModel> list = new ArrayList<>();
                                            list.add(new OptionModel(value, value));
                                            list.add(0,new OptionModel("",""));
                                            vFieldsList.get(position).setOptionsArrayList(list);
                                            if (this != null) {
                                             return;
                                            }


                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                });

                                        } else {

                                            for (int i = 0; i < vFieldsList.size(); ++i) {
                                                Field fieldObj = vFieldsList.get(i);
                                                if (field.equals(fieldObj.getId())) // field found
                                                {
                                                    vFieldsList.get(position).setValue(value.trim());
                                                    runOnUiThread(new Runnable() {
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

                                        vFieldsList.get(position).setOptionsArrayList(list);
                                        if (this != null) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyItemChanged(position);
                                                }
                                            });
                                        }
                                    }

                                    dismissFetchProgressDialog();
//
                                } catch (Exception e) {
                                    if (e instanceof JSONException) {
                                        if (mdcombo) {
                                            //adding this so that we can get % in multispinner even when we don't get any data from the api
                                            //basically added for Item Code in Stock Report
                                            ArrayList<OptionModel> list = new ArrayList<>();
                                            list.add(0, new OptionModel("%25", "%"));
                                            vFieldsList.get(position).setOptionsArrayList(list);
                                            if (this != null) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    e.printStackTrace();
                                    dismissFetchProgressDialog();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dismissFetchProgressDialog();
                    }
                });
                queue.add(request);

            } else {
                ToastUtil.showToastMessage("Please check your internet connection and try again", VlistFormActivity.this);
                //    DialogUtil.showAlertDialog(getActivity(),
                //    "No Internet Connection!", "Please check your internet connection and try again", false, false);
            }
        } else {
            Log.e(DEBUG_TAG, "#line 402 , Bad URL = " + url);
        }
    }

    @Override
    public void loadDatePicker(int position, String onChange) {
        displayDatePicker(position, onChange);
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
                vFieldsList.get(position).setValue(currentDateandTime);

                if (this != null) {
                   runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    });
                }
                if(!onChange.isEmpty()){
                    onChange(position, onChange,
                            currentDateandTime, "");

                    checkHideShow(onChange);
                }
            }
        }, null, null, VlistFormActivity.this);

        if (dialogFragment.isAdded()) {
            return;
        } else {
            dialogFragment.show(getSupportFragmentManager(), "Date");
        }
    }

    @Override
    public void onClickRightButton(int position, String onKeyDown, String onClickRightBtnFunc, String fieldType, String fieldName) {
        if (!onKeyDown.isEmpty()) {
            Bundle args = new Bundle();
            args.putString(Constant.EXTRA_ONKEY_DOWN, onKeyDown);
            args.putString(Constant.EXTRA_ONCLICK_RIGHT, onClickRightBtnFunc);
            args.putString(Constant.EXTRA_TYPE, fieldType);
            args.putString(Constant.EXTRA_FIELD_NAME, fieldName);
            args.putInt(EXTRA_POSITION, position);
            args.putBoolean(EXTRA_IS_DLIST, false);

            BottomSheetDropdown bottomSheetFragment = new BottomSheetDropdown(
                    bottomSheetClickListener,true);
            bottomSheetFragment.setArguments(args);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        }
    }

    @Override
    public void onValueChanged(int position, String value, String onChange) {
        try {
            if (!vFieldsList.isEmpty()) {
                vFieldsList.get(position).setValue(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fetchRecord(int position, String recordId, String uniqueFieldId) {

        Log.e(DEBUG_TAG, "fetchRecord Called");
        isVInputInTagField = true;
        //this gets called when you  type record id in the autocomplete text view and its fieldtype is tag
        FetchRecordHelper fetchRecordHelper = new FetchRecordHelper(VlistFormActivity.this,
                mResponseInterface);
        fetchRecordHelper.setFormId(VFORM_ID);
        fetchRecordHelper.setRecordId(recordId);
        fetchRecordHelper.setRootLayout(llRecyclerViewRoot);
        fetchRecordHelper.setUniqueFieldId(uniqueFieldId);
        fetchRecordHelper.setInputInTagField(true);
        fetchRecordHelper.setFieldsList(vFieldsList);
        fetchRecordHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
        fetchRecordHelper.setDlistArrayPosition(vDlistArrayPosition);
        fetchRecordHelper.setAdapter(adapter);
        fetchRecordHelper.callFetchRecordAPI();
    }

    @Override
    public void pickFile(int position) {
        vFileFieldPosition = position;
        if (hasPermission()) {
            chooseFile();
        }
    }

    private boolean hasPermission() {
        if ((ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(VlistFormActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(this,
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
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), Constant.PICK_FILE_REQUEST_CODE);
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

                if (vFieldsList != null) {
                    for (int i = 0; i < vFieldsList.size(); i++) {
                        Field fieldObj = vFieldsList.get(i);
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

    private void divhideshow(String id, String value) {
        try {
            String idlist = "";
            for (int i = 0; i < vAdditionalFieldDataList.size(); i++) {
                Field field = vAdditionalFieldDataList.get(i);
                if (field.getName().equals("stateids")) {
                    idlist = field.getValue();
                }
            }

            String checkstateid = "0";
            for(int i=0; i < vFieldsList.size(); i++){
                Field fobj = vFieldsList.get(i);
                if(fobj.getId().equals("statefield")){
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

                                if (checkedids.indexOf("/"+arr3[0]+"/") < 0) {
                                    for (int b = 0; b < vFieldsList.size(); b++) {
                                        Field bObj = vFieldsList.get(b);
                                        if (bObj.getId().matches("field" + arr3[0])) {

                                            if (bObj.getType().toLowerCase().matches("checkbox")) {

                                                if (bObj.getValue().matches("false") || bObj.getValue().isEmpty()) {
                                                    if (checkedidshide.indexOf("/" + arr3[0] + "/") < 0) checkedidshide = checkedidshide.concat(arr3[0] + "/") ;
                                                }
                                            } else if (!bObj.getType().toLowerCase().matches("checkbox") && !bObj.getValue().matches(arr3[1])) {
                                                if(checkedidshide.indexOf("/"+arr3[0]+"/") < 0 ) checkedidshide += arr3[0]+"/";
                                            } else {
                                                checkedids =  checkedids.concat(arr3[0] + "/");
                                                checkedidshide = checkedidshide.replace("/" + arr3[0] + "/", "/");
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }// end of j for loop

                        if (!checkedidshide.equals("/")) doshow = false;
                        if(doshow && checkstateid!="0" && (arr[i].indexOf('s')==0||arr[i].indexOf("-c-s")>-1)) if(arr[i].indexOf('s'+checkstateid+'s') < 0 && arr[i].indexOf("s0s") < 0) doshow = false;

                        if (doshow) {
                            for (int j = 0; j < vFieldsList.size(); j++) {
                                Field fObj = vFieldsList.get(j);

                                if(!fObj.getdListArray().isEmpty()){

                                    for(int k =0; k < fObj.getdListArray().size(); k++ ){
                                        Field dlistObj = fObj.getdListArray().get(k);
                                        if (dlistObj.getStates().matches(arr[i]) && dlistObj.getType().equals("hidden")) {
                                            dlistObj.setType(dlistObj.getFieldType());
                                            Log.e(DEBUG_TAG, "field unhidden -> " + dlistObj.getFieldName()) ;
                                        }
                                    }
                                }else{
                                    if (fObj.getStates().matches(arr[i]) && fObj.getType().equals("hidden")) {
                                        fObj.setType(fObj.getFieldType());
                                        // notifyAdapter(j);
                                    }
                                }
                                notifyAdapterWithPayLoad(j,Constant.PAYLOAD_HIDE_SHOW);
                            }
                            showHideDlistFields(arr[i], "c" + id + "-" + value + "-c");
                        } else{
                            for (int j = 0; j < vFieldsList.size(); j++) {
                                Field fObj = vFieldsList.get(j);

                                if(!fObj.getdListArray().isEmpty()) {

                                    for(int k =0; k < fObj.getdListArray().size(); k++ ) {
                                        Field dlistObj = fObj.getdListArray().get(k);

                                        if (dlistObj.getStates().matches(arr[i])
                                                && !dlistObj.getType().equals("hidden")) {
                                            dlistObj.setFieldType(fObj.getType());
                                            dlistObj.setType("hidden");
                                            Log.e(DEBUG_TAG, "field hidden -> " + dlistObj.getFieldName()) ;
                                        }

                                    }
                                }else {
                                    if (fObj.getStates().matches(arr[i])
                                            && !fObj.getType().equals("hidden")) {
                                        fObj.setFieldType(fObj.getType());
                                        fObj.setType("hidden");

                                    }
                                }
                                notifyAdapterWithPayLoad(j,Constant.PAYLOAD_HIDE_SHOW);
                            }
                        }
                    } else {
                        for (int j = 0; j < vFieldsList.size(); j++) {
                            Field fObj = vFieldsList.get(j);

                            if(!fObj.getdListArray().isEmpty()) {

                                for(int k =0; k < fObj.getdListArray().size(); k++ ) {
                                    Field dlistObj = fObj.getdListArray().get(k);

                                    if (dlistObj.getStates().matches(arr[i])
                                            && dlistObj.getType().equals("hidden")) {
                                        dlistObj.setType(fObj.getFieldType());
                                        Log.e(DEBUG_TAG, "field unhidden -> " + dlistObj.getFieldName()) ;
                                    }

                                }
                            }else {
                                if (fObj.getStates().matches(arr[i]) && fObj.getType().equals("hidden")) {
                                    fObj.setType(fObj.getFieldType());
                                }
                            }
                            notifyAdapterWithPayLoad(j,Constant.PAYLOAD_HIDE_SHOW);
                        }
                    }
                } else if (arr[i].indexOf("c" + id + "-") > -1) {
                    for (int j = 0; j < vFieldsList.size(); j++) {
                        Field fObj = vFieldsList.get(j);

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

    private void showHideDlistFields(String arr, String value) {

        if (vDlistArrayPosition != -1) {
            List<Field> dlistArray = vFieldsList.get(vDlistArrayPosition).getdListArray();


            for (int j = 0; j < dlistArray.size(); j++) {
                Field fObj = dlistArray.get(j);

                //zeroth row
                List<DList> dlistField = fObj.getdListsFields();
                for (int k = 0; k < dlistField.size(); k++) {
                    DList dobj = dlistField.get(k);

                    if (!dobj.getStates().isEmpty()) {
                        Log.e("SHOWHIDEDLISTFIELDS", "ARR = " + arr);
                        Log.e("SHOWHIDEDLISTFIELDS", "states = "+ dobj.getStates());

                        if (dobj.getStates().indexOf(value) > -1) {
                            // if(dobj.getStates().matches(arr) && dobj.getType().equals("hidden")) {
                            dobj.setType(dobj.getFieldType());
                            Log.e("CHECKHIDESHOW", "DLISTFIELD SHOW ->"  + dobj.getFieldName());
                        } else {
                            dobj.setType("hidden");
                            Log.e("CHECKHIDESHOW", "DLISTFIELD Hide ->"  + dobj.getFieldName());
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

    public void evaluateFunction(String funcString, final String value) {
        String[] arr = funcString.split("evaluatefunction");
        String[] evalFunc = arr[1].split(",");
        final String fieldId = evalFunc[0].replaceAll("['\\(\\)]", "");
        final String functionList = evalFunc[1].replaceAll("\'", "");
        final String extension = evalFunc[2].replaceAll("['\\(\\)]", "");
        final String jcodeList = evalFunc[3].replaceAll("['\\(\\)]", "");

        Runnable backGroundRunnable = new Runnable() {
            public void run() {
                EvaluateFunctionHelper evaluateFunctionHelper = new EvaluateFunctionHelper(VlistFormActivity.this);
                evaluateFunctionHelper.setDlistArrayPosition(vDlistArrayPosition);
                evaluateFunctionHelper.setFieldsList(vFieldsList);
                evaluateFunctionHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
                evaluateFunctionHelper.setAdapter(adapter);
                evaluateFunctionHelper.evaluateFunction(fieldId, functionList, extension, jcodeList,
                        value);
            }
        };
        Thread thread = new Thread(backGroundRunnable);
        thread.start();
    }

    private void clearFieldIds(String funcString) {
        String[] arr = funcString.split("clearfieldids");
        String[] f = arr[1].split(",");
        String fieldId = f[0].replaceAll("['\\(\\);]", "");
        clearfieldids(fieldId);
    }

    private void clearfieldids(String fieldId) {
        for (int i = 0; i < vFieldsList.size(); i++) {
            if (fieldId.equals(vFieldsList.get(i).getId())) {
                vFieldsList.get(i).setValue("");
            }
        }
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
        cp.setFieldsList(vFieldsList);
        cp.setAdditionalFieldDataList(vAdditionalFieldDataList);

        int resultPosition = cp.checkPattern(fieldId,
                regexPattern,
                extension,
                fieldName,
                false);

        if (resultPosition != -1) {
            //scrollToFieldPostion(resultPosition);
            //notifyAdapter(resultPosition);
            notifyAdapter(resultPosition);
        }
    }

    private void fn(String funcString, String value, final int position){
        String[] arr = funcString.split("fn");
        String[] fnString = arr[1].split(",");

        final String fetchFormId = fnString[0].replaceAll("['\\(\\)]", "");
        final String fieldId = fnString[1].replaceAll("\'", "");
        final String matchingField = fnString[2].replaceAll("['\\(\\)]", "");
        final String matchingFieldIds = fnString[3].replaceAll("['\\(\\)]","");

        String fnURL = functionHelper.fn(VFORM_ID,fetchFormId, fieldId, matchingField, matchingFieldIds,value );
        //call api with this url
        if (fetchCounter >= 0) {
            callAPI(fnURL,false);
        }
    }

    private void checkRepeats(String funcString) {

        CheckRepeatHelper checkRepeatHelper = new CheckRepeatHelper(VlistFormActivity.this,VFORM_ID);
        checkRepeatHelper.setFieldsList(vFieldsList);
        checkRepeatHelper.setAdditionalFieldDataList(vAdditionalFieldDataList);
        String url = checkRepeatHelper.splitCheckRepeats(funcString);

        if (fetchCounter >= 0) {
            if(!url.equals("")){
                FetchCheckRepeatDataHelper ch = new FetchCheckRepeatDataHelper(VlistFormActivity.this,mResponseInterface);
                ch.setFieldsList(vFieldsList);
                ch.setAdditionalFieldDataList(vAdditionalFieldDataList);
                ch.callFetchCheckRepeatData(url);
            }
        }

    }

    public void evaluateSQL(String funcString, String value, final int position,boolean isPayLoad) {
        String[] arr = funcString.split("evaluatesql");
        String[] evalSql = arr[1].split(",");
        final String fieldId = evalSql[0].replaceAll("['\\(\\)]", "");
        final String extension = evalSql[1].replaceAll("\'", "");
        final String jcodeList = evalSql[2].replaceAll("['\\(\\)]", "");
        if(vFieldsList != null){
            value = vFieldsList.get(position).getValue();
        }
        String evSqlURL = functionHelper.evaluatesql(fieldId, extension, jcodeList,
                value,true);
        //call api with this url
        if (fetchCounter >= 0) {
            callAPI(evSqlURL,isPayLoad);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == Constant.PICK_FILE_REQUEST_CODE){
                if(data != null) {
                    Uri uri = data.getData();
                    FileUtil fileUtil = new FileUtil(VlistFormActivity.this);
                    String  filePath = fileUtil.getPath(uri);
                    Log.e("FILE_PATH", filePath);

                    if(attachFileHelpler.isAttachFileClicked()){
                        attachFileHelpler.setFileName(filePath);
                        attachFileHelpler.setFormId(VFORM_ID);
                        attachFileHelpler.setRecordId(VRECORD_ID);

                        new AlertDialog.Builder(VlistFormActivity.this)
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
                    }else{
                        if(vFieldsList != null){
                            vFieldsList.get(vFileFieldPosition).setValue(filePath);
                            adapter.notifyItemChanged(vFileFieldPosition);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}