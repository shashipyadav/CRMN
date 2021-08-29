package com.example.myapplication.user_interface.dlist.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.function.CheckPatternFunction;
import com.example.myapplication.function.EvaluateFunctionHelper;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.user_interface.dlist.controller.DListFieldHelper;
import com.example.myapplication.user_interface.dlist.controller.DlistFieldInterface;
import com.example.myapplication.user_interface.dlist.controller.DlistFieldRecyclerAdapter;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.forms.controller.CustomDateTimePickerInterface;
import com.example.myapplication.user_interface.forms.model.Form;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.function.DlistFunctionHelper;
import com.example.myapplication.function.EncodeURIEngine;
import com.example.myapplication.function.EvaluateEngine;
import com.example.myapplication.customviews.DatePickerDialogFragment;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.view.BottomSheetDropdown;
import com.example.myapplication.Constant;
import com.example.myapplication.customviews.CustomDateTimePicker;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.FileUtil;
import com.example.myapplication.util.KeyboardUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.Float.NaN;
import static java.lang.Float.isNaN;

public class DListFormActivity extends AppCompatActivity implements
        View.OnClickListener, DlistFieldInterface, CustomDateTimePickerInterface {

    private static final String DEBUG_TAG = DListFormActivity.class.getName();
    private String title = "";
    private int mDlistButtonPosition = -1;
    private int curdListItemPosition = 0;
    private List<DListItem> dlistFieldList = new ArrayList<>();
    public static List<DList> dlistFieldValues = new ArrayList<>();
    private DlistFunctionHelper mFunctionHelper;
    private RecyclerView recyclerviewForm;
    private DlistFieldRecyclerAdapter adapter;
    private Button btnPrevious, btnNext;
    private int fieldPosition = -1;
    private String mode = "";
    private String FIELD_ID = "";
    private int fileFieldPosition = -1;
    private DatabaseManager dbManager;
    private Gson gson;
    int dlistRowsCount = -1;
    private int fetchCounter = 0;
    private ProgressDialog progressDialog1;

    private BottomSheetDropdown.BottomSheetClickListener bottomSheetClickListener =
            new BottomSheetDropdown.BottomSheetClickListener() {
        @Override
        public void getSelectValues(String valueString, final int fieldPosition) {

            String fieldValue = "";
            if(dlistFieldValues != null) {
                fieldValue = dlistFieldValues.get(fieldPosition).getValue();
                if(!fieldValue.isEmpty()){
                    if(!fieldValue.endsWith(", ")) {
                        int index = fieldValue.lastIndexOf(", ");
                        if(index > -1) {
                            String searchKeyword = fieldValue.substring(index + 1);
                            Log.e("SEARCH KEYWORD = " ,searchKeyword );

                            fieldValue = fieldValue.replace(searchKeyword, " ");
                            fieldValue +=  valueString;
                        }else {
                            fieldValue = valueString;
                        }
                    }

                }else{
                    fieldValue = valueString;
                }
            }

            onValueChanged(fieldPosition,fieldValue,true);

            String addFunction = dlistFieldValues.get(fieldPosition).getAddFunction();
            if(!addFunction.isEmpty()) {
                onAddFunction(fieldPosition,addFunction,fieldValue);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //added for performance
        try{
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_dlist_form);
        initView();

        if(getIntent().getExtras() != null) {
         //   title = bundle.getString(Constant.EXTRA_TITLE);
            FIELD_ID = getIntent().getStringExtra(Constant.EXTRA_FIELD_ID);
            mDlistButtonPosition = getIntent().getIntExtra(Constant.EXTRA_DLIST_BUTTON_POSITION,-1);
        //  mDlistItemPosition = getIntent().getIntExtra(Constant.EXTRA_DLIST_ITEM_POSITION,-1);
            curdListItemPosition = getIntent().getIntExtra(Constant.EXTRA_DLIST_ROW_POSITION,-1);//position of
            mode = getIntent().getStringExtra(Constant.EXTRA_MODE);
        }
        fieldPosition = FormFragment.dlistArrayPosition;
        //curdListItemPosition += 1;
        Log.e(DEBUG_TAG,"curdListItemPosition = "+curdListItemPosition);

        //USING THIS FOR FUNCTION
        initDatabase();

        String dlistItemString = dbManager.fetchFormJsonBySrNo(FIELD_ID,curdListItemPosition);
        gson = new Gson();
        DListItem ditem = gson.fromJson(dlistItemString, DListItem.class);
        dlistFieldValues = ditem.getDlistArray();

        init();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        dbManager = new DatabaseManager(DListFormActivity.this);
        dbManager.open();
    }

    private void init(){
        setActionBar();
        initForm();
    }

    private void initView(){
       // mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerviewForm = findViewById(R.id.recyclerdlist);
        recyclerviewForm.setItemAnimator(null);
        LinearLayoutManager llm =
                new LinearLayoutManager(this);
        llm.setItemPrefetchEnabled(false);
        recyclerviewForm.setLayoutManager(llm);
        btnPrevious = findViewById(R.id.button_prev);
        btnPrevious.setOnClickListener(this);
        btnNext = findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
    }

    private void setActionBar() {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initForm() {
        dlistRowsCount = dbManager.getDlistRowsCount(FIELD_ID);
        mFunctionHelper = new DlistFunctionHelper(DListFormActivity.this);
        mFunctionHelper.setFieldsList(FormFragment.fieldsList);
        mFunctionHelper.setAdditionalFieldDataList(FormFragment.additionalFieldDataList);
        mFunctionHelper.setDlistFieldValues(dlistFieldValues);
        setButtonEnabled(curdListItemPosition, dlistRowsCount);
        bindDlistFields(dlistFieldValues,mDlistButtonPosition,curdListItemPosition);
    }

    public void setButtonEnabled(int curdListItemPosition, int dlistFieldSize) {
        if (curdListItemPosition == 0) {
            btnPrevious.setEnabled(false);
        }
    }

    private int getTotalNoOfRows(){
      return dbManager.getDlistRowsCount(FIELD_ID);
    }

    public void bindDlistFields(List<DList> fields,
                                int dlistButtonArrayPosition,
            /* int dlistItemPosition, */int listObjPosition) {

        if(adapter != null){
            adapter = null;
        }

        recyclerviewForm.setItemViewCacheSize(fields.size());

        adapter = new DlistFieldRecyclerAdapter(this,
                fields, this, dlistButtonArrayPosition, listObjPosition);

        recyclerviewForm.setAdapter(adapter);


//        adapter.setOnBottomReachedListener(new DlistFieldRecyclerAdapter.OnBottomReachedListener() {
//            @Override
//            public void onBottomReached(int position) {
//                Log.e("DLIST","BOTTOM REACHED FALSE");
//                adapter.setFlag(false);
//                Handler handler = new Handler();
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.setFlag(true);
//                        Log.e("DLIST","BOTTOM REACHED TRUE");
//                    }
//                },1500);
//
//            }
//        });

        adapter.setCustomDateTimePickerListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        if(item.getItemId() == R.id.action_settings){

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        updateDListInDb();
        super.onBackPressed();
    }

    private void callAPI(String url, final int position, final boolean isPayLoad) {
        boolean isValidURL = false;
        if (url != null && !url.isEmpty()) {
            isValidURL = android.util.Patterns.WEB_URL.matcher(url).matches();
        }
        if(isValidURL) {

            if (NetworkUtil.isNetworkOnline(DListFormActivity.this)) {
                showProgressDialog();

                RequestQueue queue = Volley.newRequestQueue(DListFormActivity.this);
                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e(DEBUG_TAG, "callAPI Response=" + response);
                                try {
                                    Object json = new JSONTokener(response).nextValue();
                                    if (json instanceof JSONObject) {
                                    JSONObject jsonData = new JSONObject(response);

                                    for (int i = 0; i < jsonData.names().length(); i++) {
                                        // Log.v(TAG, "key = " + jsonData.names().getString(i) + " value = " + jsonData.get(jsonData.names().getString(i)));

                                        for (int j = 0; j < dlistFieldValues.size(); j++) {
                                            DList dListObj = dlistFieldValues.get(j);

                                            if (dListObj.getId().equals(jsonData.names().getString(i))) {
                                                dListObj.setValue((String) jsonData.get(jsonData.names().getString(i)));
                                                notifyAdapterWithPayload(j,Constant.PAYLOAD_EVALUATE_FUNCTION);
                                              //  notifyAdapter(j);
                                                break;
                                            }
                                        }
                                    }
                                    }
                                    if(json instanceof JSONArray){
                                        JSONArray jsonArray = new JSONArray(response);
                                        for(int i =0; i < jsonArray.length(); i++){
                                            JSONObject jsonData2 = jsonArray.getJSONObject(i);
                                            for (int j = 0; j < jsonData2.names().length(); j++) {
                                                for (int k = 0; k < dlistFieldValues.size(); k++) {
                                                    DList dListObj = dlistFieldValues.get(k);

                                                    if (dListObj.getId().equals(jsonData2.names().getString(j))) {
                                                        if(jsonData2.get(jsonData2.names().getString(j)) instanceof  String) {
                                                            dListObj.setValue((String) jsonData2.get(jsonData2.names().getString(j)));
                                                        }else {
                                                            dListObj.setValue(String.valueOf(jsonData2.get(String.valueOf(jsonData2.names().getString(j)))));
                                                        }
                                                        notifyAdapterWithPayload(k,Constant.PAYLOAD_EVALUATE_FUNCTION);
                                                        //notifyAdapter(k);
                                                        break;
                                                    }
                                                }
                                            }
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
                ToastUtil.showToastMessage("Please check your internet connection and try again", DListFormActivity.this);
                // DialogUtil.showAlertDialog(getActivity(),
                //         "No Internet Connection!", "Please check your internet connection and try again", false, false);
            }
        }
    }

    @Override
    protected void onPause() {
        updateDListInDb();
        super.onPause();
    }

    private void showProgressDialog() {
        if (fetchCounter == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog1 = ProgressDialog.show(DListFormActivity.this, "", "Fetching Details ...", false);
                }
            });
        }
        fetchCounter++;
    }

    private void dismissProgressDialog(){
        fetchCounter--;
        if (fetchCounter == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ((progressDialog1 != null) && progressDialog1.isShowing()) {
                        progressDialog1.dismiss();
                    }
                }
            });
        }
    }

    private void displayDatePicker(final int position) {
        DialogFragment dialogFragment = new DatePickerDialogFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                String currentDateandTime = sdf.format(cal.getTime());

                dlistFieldValues.get(position).setValue(currentDateandTime);
              //  notifyAdapter(position);
                notifyAdapterWithPayload(position,Constant.PAYLOAD_TEXT);
                String addFunction = dlistFieldValues.get(position).getAddFunction();
                if(!addFunction.isEmpty()){
                    onAddFunction(position, addFunction,
                            currentDateandTime);

                }
                //Calendar.getInstance()
            }
        }, null, null, DListFormActivity.this);

        if (dialogFragment.isAdded()) {
            return;
        } else {
            dialogFragment.show(getSupportFragmentManager(), "Date");
        }
    }

    private void notifyAdapter(final int position){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // update the ui from here
                if(adapter != null){
                    adapter.notifyItemChanged(position);
                }
            }
        });

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Handler handler = new Handler();
//
//                final Runnable r = new Runnable() {
//                    public void run() {
//                        if(adapter != null){
//                            adapter.notifyItemChanged(position);
//                        }
//                    }
//                };
//                handler.post(r);
//            }
//        });
    }


    private void notifyAdapterWithPayload (final int position, final String payload) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // update the ui from here
                if(adapter != null){
                    Log.e(DEBUG_TAG, "notifyAdapterWithPayLoad "+payload+ " called");
                    adapter.notifyItemChanged(position,payload);
                }
            }
        });


//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Handler handler = new Handler();
//
//                final Runnable r = new Runnable() {
//                    public void run() {
//                        if(adapter != null){
//                            Log.e(DEBUG_TAG, "notifyAdapterWithPayLoad "+payload+ " called");
//                            adapter.notifyItemChanged(position,payload);
//                        }
//                    }
//                };
//                handler.post(r);
//            }
//        });

    }

    @Override
    public void onAddFunction(final int position,
                              final String function,
                              final String value) {

        if (!function.isEmpty()) {
            String funcString = function;
                String[] jarr = funcString.split("\\);");
                for (int i = 0; i < jarr.length; i++) {

                    if(jarr[i].contains("clearfieldids")) {
                        clearFieldIds(jarr[i]);
                    }else if (jarr[i].contains("evaluatesql")) {

                        String[] arr = jarr[i].split("evaluatesql");
                        String[] evalSql = arr[1].split(",");
                        final String fieldId = evalSql[0].replaceAll("['\\(\\)]", "");
                        //previous dlist code april 13 2021
                      ///  int dlistPos  = curdListItemPosition+1;
                        int dlistPos  = curdListItemPosition;
                        final String extension = "_"+dlistPos;
                        final String jcodeList = evalSql[2].replaceAll("['\\(\\)]", "");

                        String evSqlURL = mFunctionHelper.evaluatesql(fieldId, extension, jcodeList, value, dlistFieldValues,false);
                        callAPI(evSqlURL, position,false);

                    }else if(jarr[i].contains("evaluatefunction")) {
                        splitEvaluationFunction(jarr[i],value);
                    }else if(jarr[i].contains("fn_dlist")) {
                        String[] arr = jarr[i].split("fn_dlist");
                        String[] fndlistFunc = arr[1].replaceAll("\'","").split(",");
                        final String formid =  fndlistFunc[0].replaceAll("['\\(\\)]", "");
                        final String fieldid =  fndlistFunc[1];
                        final String matchingField = fndlistFunc[2];
                        final String matchingFieldIds = fndlistFunc[3];
                        String dlistid =  fndlistFunc[4];
                        final String dlistformid =  fndlistFunc[5];
                        int dlistPos = curdListItemPosition;
                        dlistid = "_"+dlistPos;

                        final String finalDlistid = dlistid;
                        String fndlistURL = mFunctionHelper.fn_dlist(formid, fieldid, matchingField, matchingFieldIds, finalDlistid,dlistformid,false);
                        callAPI(fndlistURL, position,false);

                    }else if(jarr[i].contains("totaldlist")){
                        String[] arr = jarr[i].split("totaldlist");
                        String[] totaldlistFunc = arr[1].replaceAll("\'","").split(",");
                        final String id = totaldlistFunc[0].replaceAll("['\\(\\)]", "");
                        final String fieldid = totaldlistFunc[1];
                        final String dlistid = totaldlistFunc[2];
                        final String doround = totaldlistFunc[3];

                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mFunctionHelper.totaldlist(id,fieldid,dlistid,doround,false);
                            }
                        });
                        t.start();

                    }else if(jarr[i].contains("checkpattern")) {
                        int dlistPos  = curdListItemPosition;
                        boolean isReadOnly = dlistFieldValues.get(dlistPos).isReadOnly();
                        boolean hidden = dlistFieldValues.get(dlistPos).isHidden();
                                if(!value.isEmpty()){
                                    checkPattern(jarr[i],value,position);
                                }
                    }
                }
        }
    }


    private void splitEvaluationFunction(String funcString,final String value) {

        String[] arr = funcString.split("evaluatefunction");
        String[] evalFunc = arr[1].split(",");
        final String fieldId = evalFunc[0].replaceAll("['\\(\\)]", "");
        final String functionList = evalFunc[1].replaceAll("\'", "");
        //    int curDlistItem = curdListItemPosition +1;
        final String extension = evalFunc[2].replaceAll("['\\(\\)]", ""); //curDlistItem;
        final String jcodeList = evalFunc[3];

        Thread d = new Thread(new Runnable() {
            @Override
            public void run() {

                evaluateFunction(fieldId,functionList, extension,jcodeList,value);
            }
        });
        d.start();
        d.setName("My Thread - "+ System.currentTimeMillis());

    }

    public void checkPattern(String funcString, final String value, final int position) {
        String[] arr = funcString.split("checkpattern");
        String[] checkPat = arr[1].split("\',\'");
        final String fieldId = checkPat[0].replaceAll("['\\(\\)]", "");
        final String regexPattern = checkPat[1].replaceAll("\'", "");

        //previous dlist code
       // int dlistPos  = curdListItemPosition+1;
        int dlistPos  = curdListItemPosition;
        final String extension = "_"+dlistPos;
        Log.e("CHECKPATTERN", fieldId + "@J@" + regexPattern + "@J@" + extension + "@J@" );

        CheckPatternFunction cp = new CheckPatternFunction();
        cp.setFieldsList(null);
        cp.setAdditionalFieldDataList(null);
        cp.setDlistFieldValues(dlistFieldValues);

        int resultPosition = cp.checkPattern(fieldId,
                regexPattern,
                extension,
                dlistFieldValues.get(position).getFieldName(),
                true);

        if (resultPosition != -1) {
            //scrollToFieldPostion(resultPosition);
            notifyAdapter(resultPosition);
        }
    }

    private void clearFieldIds(String funcString) {
        String[] arr = funcString.split("clearfieldids");
        String[] f = arr[1].split(",");
        String fieldId = f[0].replaceAll("['\\(\\);]", "");
        clearfieldids(fieldId);
    }

    private void clearfieldids(String fieldId) {
        fieldId = fieldId.replace("/","");
        //previous dlist code april 13 2021
       // int extension = curdListItemPosition+1;
        int extension = curdListItemPosition;
        fieldId = "field"+fieldId +"_"+ extension;

        for (int i = 0; i < dlistFieldValues.size(); i++) {

            if (fieldId.equals(dlistFieldValues.get(i).getId())) {
//                FormFragment.fieldsList.get(fieldPosition).getdListArray()
//                        .get(mDlistButtonPosition).getDListItemList()
//                        .get(curdListItemPosition).getDlistArray().get(i).setValue("");
                dlistFieldValues.get(i).setValue("");
                notifyAdapterWithPayload(i,Constant.PAYLOAD_TEXT);
                break;
            }
        }
    }

    @Override
    public void pickFile(int position) {
        if(hasPermission()){
            fileFieldPosition = position;
            chooseFile();
        }
    }

    private boolean hasPermission(){
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constant.REQUEST_PERMISSIONS);
            }
            return false;
        } else {
            return true;
        }
    }

    public void chooseFile(){
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == Constant.PICK_FILE_REQUEST_CODE) {
                if (data != null) {
                    Uri uri = data.getData();
                    FileUtil fileUtil = new FileUtil(this);
                    String filePath = fileUtil.getPath(uri);
                    Log.e("FILE_PATH", filePath);

                //    FormFragment.fieldsList.get(fieldPosition).getdListArray().get(mDlistButtonPosition).getDListItemList().get(curdListItemPosition).getDlistArray().get(fileFieldPosition).setValue(filePath);
                    dlistFieldValues.get(fileFieldPosition).setValue(filePath);
                    notifyAdapter(fileFieldPosition);

                    onAddFunction(fieldPosition,
                            dlistFieldValues.get(fileFieldPosition).getAddFunction(),
                            dlistFieldValues.get(fileFieldPosition).getValue());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void loadDatePicker(int position) {
        displayDatePicker(position);
    }

    @Override
    public void onValueChanged(int position, String value,boolean doUpdateRecyclerView) {
        dlistFieldValues.get(position).setValue(value);
        if(doUpdateRecyclerView){
            notifyAdapterWithPayload(position,Constant.PAYLOAD_TEXT);
        }

        updateDListInDb();
        Log.e("ONVALUECHANGE", "SETVALUE = " + value + "dlistfield = " + dlistFieldValues.get(position).getFieldName());

    }

    @Override
    public void onClickRightButton(int position, String dropDownClickFunc,String fieldType) {
        if (!dropDownClickFunc.isEmpty()) {

            if (dropDownClickFunc.contains("load")) {
                if (fieldType.toLowerCase().equals("adcombo") ||
                        fieldType.toLowerCase().equals("mdcombo") ||
                        fieldType.toLowerCase().equals("dcombo"))
                {
                    Bundle args = new Bundle();
                    args.putString(Constant.EXTRA_ONKEY_DOWN,dlistFieldValues.get(position).getOnKeyDown());
                    args.putString(Constant.EXTRA_ONCLICK_RIGHT,dropDownClickFunc);
                    args.putString(Constant.EXTRA_TYPE,fieldType);
                    args.putString(Constant.EXTRA_FIELD_NAME,dlistFieldValues.get(position).getFieldName());
                    args.putInt(Constant.EXTRA_POSITION,position);
                    args.putBoolean(Constant.EXTRA_IS_DLIST, true);
                    //previous dlist code april 13 2021 //curdListItemPosition +1
                    args.putInt(Constant.EXTRA_DLIST_ROW_POSITION,curdListItemPosition);

                    BottomSheetDropdown bottomSheetFragment = new BottomSheetDropdown(bottomSheetClickListener,false);
                    bottomSheetFragment.setArguments(args);
                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
            }
        }
    }

  /*  @Override
    public void evaluateSqlWithPayload(final int position, String onChange, final String value) {
        if (!onChange.isEmpty()) {
            if (dlistFieldValues != null) {
                String funcString = "";
                Pattern pattern = Pattern.compile("evaluatesql.*?\\);");
                Matcher matcher = pattern.matcher(onChange);
                while (matcher.find()) {
                    funcString = matcher.group(0);
                    break;
                }

                if (funcString.contains("evaluatesql")) {
                    String[] arr = funcString.split("evaluatesql");
                    String[] evalSql = arr[1].split(",");
                    final String fieldId = evalSql[0].replaceAll("['\\(\\)]", "");
                    //final String extension = evalSql[1].replaceAll("\'", "");

                    int dlistPos  = curdListItemPosition+1;

                    final String extension = "_"+dlistPos;

                    final String jcodeList = evalSql[2].replaceAll("['\\(\\)]", "");

                   // Log.e(DEBUG_TAG, "evaluateSql = FieldId = " + fieldId + "  Extension = " + extension + " JcodeList = " + jcodeList);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String evSqlURL = mFunctionHelper.evaluatesql(fieldId, extension, jcodeList, value, dlistFieldValues);
                            callAPI(evSqlURL, position,true);
                        }
                    });
                    thread.start();
                }

            }
        }
    } */

    @Override
    public void runFunctions(final int position, String function, final String value) {

        if (!function.isEmpty()) {
            String funcString = function;

            String[] jarr = funcString.split("\\);");
            for (int i = 0; i < jarr.length; i++) {

                 if(jarr[i].contains("clearfieldids")) {
                    clearFieldIds(jarr[i]);
                }else if (jarr[i].contains("evaluatesql")) {

                    String[] arr = jarr[i].split("evaluatesql");
                    String[] evalSql = arr[1].split(",");
                    final String fieldId = evalSql[0].replaceAll("['\\(\\)]", "");
                    //final String extension = evalSql[1].replaceAll("\'", "");
                    //previous dlist code
                    // int dlistPos  = curdListItemPosition+1;
                    int dlistPos  = curdListItemPosition;

                    final String extension = "_"+dlistPos;

                    final String jcodeList = evalSql[2].replaceAll("['\\(\\)]", "");

                    Log.e(DEBUG_TAG, "evaluateSql = FieldId = " + fieldId + "  Extension = " + extension + " JcodeList = " + jcodeList);
                    String evSqlURL = mFunctionHelper.evaluatesql(fieldId, extension, jcodeList, value, dlistFieldValues,false);
                    callAPI(evSqlURL, position,true);
                    //call api with this url
                }else if(jarr[i].contains("fn_dlist")) {

                    //"fn_dlist('1935','36933','Milestones','36933/','_0','1935');
                    String[] arr = jarr[i].split("fn_dlist");
                    String[] fndlistFunc = arr[1].replaceAll("\'","").split(",");
                    final String formid =  fndlistFunc[0].replaceAll("['\\(\\)]", "");
                    final String fieldid =  fndlistFunc[1];
                    final String matchingField = fndlistFunc[2];
                    final String matchingFieldIds = fndlistFunc[3];
                    String dlistid =  fndlistFunc[4];
                    final String dlistformid =  fndlistFunc[5];
                    //previous dlist code april 13 2021
                   // int dlistPos = curdListItemPosition+1;
                    int dlistPos = curdListItemPosition;
                    dlistid = "_"+dlistPos;

                    final String finalDlistid = dlistid;
                    String fndlistURL = mFunctionHelper.fn_dlist(formid, fieldid, matchingField, matchingFieldIds, finalDlistid,dlistformid,false);
                    callAPI(fndlistURL, position,true);

                }if(jarr[i].contains("evaluatefunction")) {
                    splitEvaluationFunction(jarr[i],value);
                } else if(jarr[i].contains("totaldlist")){
                    String[] arr = jarr[i].split("totaldlist");
                    String[] totaldlistFunc = arr[1].replaceAll("\'","").split(",");
                    final String id = totaldlistFunc[0].replaceAll("['\\(\\)]", "");
                    final String fieldid = totaldlistFunc[1];
                    final String dlistid = totaldlistFunc[2];
                    final String doround = totaldlistFunc[3];

                    Thread s = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mFunctionHelper.totaldlist(id,fieldid,dlistid,doround,false);
                        }
                    });
                    s.start();
                    s.setName("My Thread - "+ System.currentTimeMillis());

                }

               /* else if(jarr[i].contains("checkpattern")) {
                    int dlistPos  = curdListItemPosition+1;
                    boolean isReadOnly = dlistFieldValues.get(dlistPos).isReadOnly();
                    boolean hidden = dlistFieldValues.get(dlistPos).isHidden();
                    if(!value.isEmpty()){
                        checkPattern(jarr[i],value,position);
                    }
                } */
            }
        }

    }



    @Override
    protected void onStop() {
        updateDListInDb();
        super.onStop();
    }

    private void updateDListInDb() {
        int srNo = 0;
        if(curdListItemPosition == 0){
            srNo += 1;
        }else{
            srNo = curdListItemPosition;
        }

        if(dbManager != null){
            DListItem dListItem = new DListItem(dlistFieldValues);
            String dlistJson = gson.toJson(dListItem);
            dbManager.updateDListRowForm(FIELD_ID,srNo,dlistJson);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                KeyboardUtil.hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        if(v == btnPrevious) {
            btnNext.setEnabled(true);
            if (curdListItemPosition == 1) {
                btnPrevious.setEnabled(false);
            } else {

                if (curdListItemPosition == 1) {
                    btnPrevious.setEnabled(false);
                } else {
                    curdListItemPosition--;
                    btnPrevious.setEnabled(true);
                    resetRecyclerView();
                }
            }
        //}
        }

        if(v ==  btnNext){
                btnPrevious.setEnabled(true);
                dlistRowsCount = getTotalNoOfRows();
                if(curdListItemPosition == dlistRowsCount){
                    curdListItemPosition+= 1;

                    createNewDList();
                }else{
                    curdListItemPosition += 1;
                    btnNext.setEnabled(true);
                }
            resetRecyclerView();
        }
    }

    private void resetRecyclerView(){

        String dlistItemString = dbManager.fetchFormJsonBySrNo(FIELD_ID,curdListItemPosition);
        DListItem ditem = gson.fromJson(dlistItemString, DListItem.class);
        dlistFieldValues = ditem.getDlistArray();


        bindDlistFields(dlistFieldValues,mDlistButtonPosition,curdListItemPosition);
        mFunctionHelper.setFieldsList(FormFragment.fieldsList);
        mFunctionHelper.setAdditionalFieldDataList(FormFragment.additionalFieldDataList);
        mFunctionHelper.setDlistFieldValues(dlistFieldValues);
    }

    private void createNewDList(){
        if(mDlistButtonPosition != -1) {
            List<DList> dlistFields = new ArrayList<>();
            dlistFields = FormFragment.fieldsList.get(fieldPosition).getdListArray().get(mDlistButtonPosition).getdListsFields();

            int dlistValuesArraySize = getTotalNoOfRows();
            List<DList> newDlistField = new ArrayList<>();

            for (int i = 0; i < dlistFields.size(); i++) {
                DList dlist = dlistFields.get(i);
                String[] jarr = dlist.getId().split("_");

                DList object = new DList();
                object.setDropDownClick(dlist.getDropDownClick());
                object.setSearchRequired(dlist.getSearchRequired());
                object.setSaveRequired(dlist.getSaveRequired());
                object.setReadOnly(dlist.getReadOnly());
                object.setSrNo(dlist.getSrNo());
                object.setOptionsArrayList(dlist.getOptionsArrayList());
                object.setFieldName(dlist.getFieldName());
                object.setAddFunction(dlist.getAddFunction());
                object.setOnKeyDown(dlist.getOnKeyDown());
                object.setType(dlist.getType());
                object.setDefaultValue(dlist.getDefaultValue());

                if(dlistValuesArraySize == 0 ){
                    object.setName(jarr[0] + "_1");
                    object.setId(jarr[0] +"_1");
                    if(dlist.getFieldName().toLowerCase().matches("sr. no|sr|sr no")){
                        object.setValue("1");
                    }else{
                        String value = dlist.getValue();
                        //by default value is 0, coming from api
                        //replace 0 by ""
//                        if (value.equals("0")) {
//                            value = "";
//                        }
                        object.setValue(value);
                    }
                }else{
                    int pos = dlistValuesArraySize+1;

                    object.setName(jarr[0] +"_" +pos);
                    object.setId(jarr[0] +"_"+ pos);
                    if(dlist.getFieldName().toLowerCase().matches("sr. no|sr|sr no")){
                        object.setValue(String.valueOf(pos));
                    }else{
                        String value = dlist.getValue();
//                        if (value.equals("0")) {
//                            value = "";
//                        }
                        object.setValue(value);
                    }
                }
                object.setFieldType(dlist.getFieldType());
                newDlistField.add(object);
            }

            SharedPrefManager prefManager = new SharedPrefManager(DListFormActivity.this);
            String formId = prefManager.getFormId();
            DListItem dListItem = new DListItem(newDlistField);
            String dlistJson = gson.toJson(dListItem);
            Log.e(getClass().getSimpleName(), "Converting dlist to json ----->" +  dlistJson);
            dbManager.insertDListRow(Integer.parseInt(formId),title,FIELD_ID,"",dlistJson,curdListItemPosition);

            dlistRowsCount = getTotalNoOfRows();
            DListFieldHelper fieldHelper = new DListFieldHelper(DListFormActivity.this);
            fieldHelper.updateContentRows(FIELD_ID, dlistRowsCount);
        }
    }

    public void evaluateFunction(String fieldId, String functionlist,
                                 String extension, String jcodelist,
                                 String fieldValue
    )
    {
        int dlistArrayField = FormFragment.dlistArrayPosition;
        String url= "";
        String dlistspanids = "";
        List<Field> noOfDlist = new ArrayList<>(); // different dlist list
        List<DList> dlistfieldsList = new ArrayList<>();
        EncodeURIEngine uriEngine = new EncodeURIEngine();
        List<Field> fieldsList  = FormFragment.fieldsList;

        List<Field> additionalDataList = FormFragment.additionalFieldDataList;
        for(int i =0; i<additionalDataList.size(); i++){
            Field fObj = additionalDataList.get(i);
            if(fObj.getId().matches("dlistspanids")){
                dlistspanids = fObj.getValue();
                break;
            }
        }

        boolean issFieldFound = false;
        for(int k=0; k < dlistFieldValues.size(); k++){
            DList nObj = dlistFieldValues.get(k);
            int curnItemPos = curdListItemPosition;
            if (nObj.getId().matches("field"+fieldId+"_"+curnItemPos)) {
                fieldValue = nObj.getValue().trim();
                break;
            }
        }

        try {
            if ((!fieldValue.contains("<")) && (!fieldValue.contains(">"))) // (trim(fieldValue,"")!='') &&
            {
                while (functionlist.contains("&gt;") //&gt; this stands for the greater-than sign ( > )
                        || functionlist.contains("&lt;") //&lt; this stands for the less-than sign ( < )
                        || functionlist.contains("&amp;"))  //&amp; this stands for the ampersand sign ( & )
                {
                    functionlist = functionlist.replace("&gt;", ">");
                    functionlist = functionlist.replace("&lt;", "<");
                    functionlist = functionlist.replace("&amp;", "&");
                }

                String[] arr = functionlist.split(";");
                functionlist = "";
                for (int m = 0; m < arr.length; m++) {
                    String func = arr[m];
                  /*  if(extension.equals("")){
                        String[] arr1 = func.split("@@");
                        boolean arr1FieldFound = false;
                        for (int d=0; d<fieldsList.size(); d++) {
                            if(fieldsList.get(d).getId().matches("field"+arr1[0])){
                                arr1FieldFound = true;
                                break;
                            }
                        }

                        if(arr1FieldFound){
                            putfunctionvalues(func, extension, fieldValue);
                        }else{
                            //check field in some dlist
                            String[] darr=dlistspanids.split("/");
                            String dlistidin = "0";
                            String carrList = "";
                            boolean isindlist = false;

                            for(int p=0; p<darr.length; p++)
                            {
                                String dlistidlist = "";
                                //need to get few values from dlistarray

                                int dlistArrayPosition = FormFragment.dlistArrayPosition;
                                Field kObj = fieldsList.get(dlistArrayPosition);
                                if(!kObj.getDListItemList().isEmpty()){
                                    noOfDlist = kObj.getdListArray();

                                    for(int h=0; h<noOfDlist.size(); h++){
                                        dlistfieldsList = noOfDlist.get(h).getdListsFields();
                                        for(int a=0; a<dlistfieldsList.size(); a++){
                                            DList aObj = dlistfieldsList.get(a);

                                            if(aObj.getId().matches("fieldids"+darr[p])){
                                                dlistidlist = aObj.getValue();
                                                if( dlistidlist.indexOf(","+arr1[0]+",") > -1){
                                                    isindlist = true;
                                                    dlistidin = darr[p];
                                                    break;
                                                }
                                            }
                                        } // end of dlistfieldsList for loop
                                    } // end of noOfDlist for loop
                                } // end of getdListArray for loop
                                // end of  fieldsList for loop
                            } // end of p for loop

                            boolean isContentRowsFound = false;
                            for(int a=0; a<dlistfieldsList.size(); a++){
                                DList aObj = dlistfieldsList.get(a);
                                if(aObj.getId().toLowerCase().equals("contentrows"+dlistidin)){
                                    carrList = aObj.getValue();
                                    isContentRowsFound = true;
                                    break;
                                }
                            }
                            if(isContentRowsFound){
                                String[] carr = carrList.split(",");
                                for(int w =0; w<carr.length; w++){
                                    if(!carr[w].equals("")){
                                      //  previous dlist code april 13 2o21
                                     //   int curItemPosition = curdListItemPosition+1;
                                        int curItemPosition = curdListItemPosition;
                                        putfunctionvalues(func,"_"+curItemPosition,fieldValue);
                                        //  putfunctionvalues(func,"_"+carr[w],fieldValue,fieldsList,additionalDataList);
                                    }
                                }
                            }

                            if(!isindlist){
                                //it could be editpart field
                                if(func.contains("REQUIRED"))
                                    putfunctionvalues(func,extension,fieldValue);
                            }
                        }
                    } else */ if(extension.equals("_0")){
                        String[] arr1=func.split("@@");
                        String darr[]=dlistspanids.split("/");
                        String dlistidin = "0";
                        boolean isindlist = false;
                      /*  for(int p=0; p<darr.length; p++)
                        {
                            String dlistidlist = "";
                            Field kObj = fieldsList.get(dlistArrayField);
                            if(!kObj.getdListArray().isEmpty()){
                                noOfDlist = kObj.getdListArray();
                                for(int h=0; h<noOfDlist.size(); h++){
                                    dlistfieldsList = noOfDlist.get(h).getdListsFields();
                                    for(int a=0; a<dlistfieldsList.size(); a++){
                                        DList aObj = dlistfieldsList.get(a);
                                        if(aObj.getId().matches("fieldids"+darr[p])){
                                            dlistidlist = aObj.getValue();
                                            if( dlistidlist.indexOf(","+arr1[0]+",") > -1){
                                                isindlist = true;
                                                dlistidin = darr[p];
                                                break;
                                            }
                                        }

                                    } // end of dlistfieldsList for loop
                                } // end of noOfDlist for loop
                            } // end of getdListArray for loop
                        }

                        String carrList = "";
                        boolean isContentRowsFound = false;

                        Field kObj = fieldsList.get(dlistArrayField);
                        if(!kObj.getdListArray().isEmpty()){
                          //  noOfDlist = kObj.getdListArray(); // todo: comment this because we have initialised it at the top
                           first:
                            for(int h=0; h<noOfDlist.size(); h++){
                                dlistfieldsList = noOfDlist.get(h).getdListsFields();
                                for(int a=0; a<dlistfieldsList.size(); a++){
                                    DList aObj = dlistfieldsList.get(a);
                                    if(aObj.getId().toLowerCase().equals("contentrows"+dlistidin)){
                                        carrList = aObj.getValue();
                                        isContentRowsFound = true;
                                        break first;
                                    }
                                } // end of dlistfieldsList for loop
                            } // end of noOfDlist for loop
                        } */

                        //april 16 commented this for now as its looping through all the content rows
                        //even though we just want the value of the current row
                       /* if(isContentRowsFound){
                            String[] carr = carrList.split(",");
                            for(int w =1; w<carr.length; w++){
                                if(!carr[w].equals("")){
                                    int curItemPosition = curdListItemPosition;
                                    putfunctionvalues(func,"_"+curItemPosition,fieldValue);
                                }
                            }
                        } */
                        int curItemPosition = curdListItemPosition;
                        putfunctionvalues(func,"_"+curItemPosition,fieldValue);

                    }
                    else{
                        putfunctionvalues(func,extension,fieldValue);
                    }
                } //end of m for loop

                String jidlist = "";
                if (!jcodelist.equals("")) {
                    String[] jarr = jcodelist.split(";");
                    jcodelist = "";

                    for (int ja = 0; ja < jarr.length; ja++) {
                        String func = jarr[ja];
                        Log.e(DEBUG_TAG, "evaluate function jaar = " + jarr[ja]);
                        String[] jarr2 = func.split("@@");
                        String jid = jarr2[0];
                        if(jarr2.length == 1){
                            //added because it crashes when we get a values such as  36923@@
                            jarr2[0] = ""; ////changed to this when testing Nature of Category in Request Register
                          //  jarr2[1] = "";
                        }

                     //   String[] jarr3 = jarr2[1].split("/");
                        String[] jarr3 = jarr2[0].split("/"); //changed to this when testing Nature of Category in Request Register
                        String jvals = "";

                        for(int j3=0; j3<jarr3.length; j3++)
                        {
                            if(extension.equals("")){
                                for(int a=0; a<noOfDlist.size(); a++){
                                    List<DListItem> dListArray = noOfDlist.get(a).getDListItemList();
                                    for(int b=0; b<dListArray.size(); b++){
                                        List<DList> dListFieldsList = dListArray.get(b).getDlistArray();
                                        for(int c =0; c<dListFieldsList.size(); c++){
                                            DList cObj = dListFieldsList.get(c);
                                            if(cObj.getId().matches("field"+jarr3[j3]+extension)){
                                                if (!cObj.getValue().equals("")){
                                                    jvals = jvals + uriEngine.encodeURIComponent(cObj.getValue() + "@j@");
                                                }else{
                                                    String[] arr1 = func.split("@@");
                                                    func="";
                                                    //  window.setTimeout("clearField('field"+jarr3[j3]+extension+"');",2000);
                                                    jvals="";
                                                }
                                                break;
                                            }
                                        }//end of 'c' for loop
                                    }// end of 'b' foor loop
                                }// end of 'a' for loop
                            }else{
                                for(int d=0; d<fieldsList.size(); d++)
                                {
                                    Field dObj = fieldsList.get(d);
                                    if(dObj.getId().matches("field"+jarr3[j3])){
                                        if(!dObj.getValue().equals("")){
                                            jvals = jvals + uriEngine.encodeURIComponent(dObj.getValue() + "@j@");
                                        }else{
                                            String[] arr1 = func.split("@@");
                                            func = "";
                                            jvals = "";
                                        }
                                        break;
                                    }
                                }//end of d for loop
                            }//end of else condition
                        }//end of 'j3' for loop

                        if(!jvals.equals("")){
                            jcodelist = jcodelist + jvals +"@jj@";
                            jidlist = jidlist + func +";";
                        }//end of !jvals.equals("")
                    } // end of 'ja' for loop
                } //end of if !jcodelist.equals("")

                if(!jcodelist.equals("")){
                    //functionlist = functionlist.replace(/\+/g,"%2B");
                    jcodelist = jcodelist.replaceAll("\\+","%2B");
                    jcodelist = jcodelist.replaceAll("\\&","%26");
                    //alert("retrieve jidlist=="+jidlist+" jcodelist=="+jcodelist);
                    url = "getFunction.do?actn=evaluatefunction&functionlist=&extension="+extension+"&jcodelist="+jcodelist+"&jidlist="+jidlist+"&ask=COMMAND_NAME_1";
                    DlistFunctionHelper.retrieveString3(url,"","");
                }
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        updateDListInDb();
    }


    private void putfunctionvalues(String func, String extension,
                                       String fieldValue) {

        int position = -1;
        List<Field> fieldsList = FormFragment.fieldsList;
        List<Field> additionalFieldList = FormFragment.additionalFieldDataList;

        EvaluateEngine evaluateEngine = new EvaluateEngine();
        func = replacefunctionvalues(func, extension, fieldValue);
        String sum = "0";
        if (!func.equals("")) {
            func = func.replaceAll("[|]", "\"");
            func = func.replaceAll("[\r\n]", " ");

            String[] arr1 = func.split("@@");
            if (!arr1[1].contains("{x}") && !arr1[1].contains("{i}") &&
                    !arr1[1].contains("&nbsp")) {
                try {
                    sum = evaluateEngine.eval(arr1[1]);
                    Log.e("line 2710","eval function eval returned ->"+ sum + "for Field = " + arr1[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("There was an error", "on this page.\n\n");
                    Log.e("Error description: ", e.getMessage() + "\n\n");
                    sum = arr1.length > 2 ? arr1[2] : "";
                }
            } else {
                sum = "0";
            }

            if (sum.toLowerCase().equals("continue")) {
                return;
            } else if ((sum + "").indexOf("LABEL=") == 0) {
                return;
            }

          //  int dlistArrayPosition = FormFragment.dlistArrayPosition;
          //  List<Field> dlistArray = FormFragment.fieldsList.get(dlistArrayPosition).getdListArray();

            for (int i = 0; i < dlistFieldValues.size(); i++) {
                DList fObj = dlistFieldValues.get(i);
                if (fObj.getId().matches("field" + arr1[0] + extension)) {
                    position = i;
                    String oldValue = "";
                    oldValue = fObj.getValue();
                    if (fObj != null || ((sum+"").indexOf("REQUIRED") > -1))//it could be editpart field, then obj will be null
                    {
                        if (sum.toLowerCase().equals("readonly") || (sum + "").indexOf("READONLY") == 0) {
                            boolean doreadonly = true;
                            if (fObj.getOptionsArrayList() != null && !fObj.getOptionsArrayList().isEmpty()) {
                                String ovalue = fObj.getValue();
                             //   List<String> options = new ArrayList<>();
                             //   options.add(ovalue);
                            //    fObj.setDropDownList(options);
                            } else {
                                //   obj.disabled = doreadonly;//true;
                                fObj.setSaveRequired("read");
                             //   notifyAdapter(i);

                            }
                            notifyAdapterWithPayload(position, Constant.PAYLOAD_READ_EDIT);
                            return;
                        } else if (sum.toLowerCase().equals("editable") || (sum + "").indexOf("EDITABLE") == 0) {
                            boolean doreadonly = false;
                            if (!fObj.getDropDownList().isEmpty()) {

                            } else {
                                if(fObj.getSaveRequired().toLowerCase().equals("read")){
                                    fObj.setSaveRequired("false");
                                }else if(fObj.getSaveRequired().toLowerCase().equals("true")) {
                                    fObj.setSaveRequired("true");
                                }

                                // notifyAdapter(i);
                                notifyAdapterWithPayload(position, Constant.PAYLOAD_READ_EDIT);
                            }
                            return;
                        } else if (sum.toLowerCase().equals("required") || (sum + "").indexOf("REQUIRED") == 0) {
                            boolean domandatory = true;
                            if (extension.equals("")) {
                                String fieldarr1 = arr1[0];
                                String editpartid = (sum + "").substring(8);
                                if (!editpartid.equals("")) {
                                    for (int b = 0; b < fieldsList.size(); b++) {
                                        Field bObj = fieldsList.get(b);
                                        if (bObj.getId().matches("field" + editpartid)) {
                                            fieldarr1 = "e" + editpartid;
                                        }
                                    }
                                }
                                // if(!obj) return true;
                                String midlist = "";
                                for (int m = 0; m < additionalFieldList.size(); m++) {
                                    Field addFieldObj = additionalFieldList.get(m);
                                    if (addFieldObj.getName().matches("mandatory")) {
                                        midlist = addFieldObj.getValue();
                                        if (midlist.indexOf(fieldarr1 + "/") != 0 && midlist.indexOf("/" + fieldarr1 + "/") < 0) {
                                            midlist += fieldarr1 + "/";
                                        }
                                        addFieldObj.setValue(midlist);
                                        break;
                                    }
                                    return;
                                }
                            }//end of if no extenstion
                            else {
                                String dlistid = (sum + "").substring(8);
                                if (dlistid.equals("")) {
                                    // alert("Required DLIST Field ID in REQUIRED Function!");
                                } else {
                                    String midlist = "";
                                    for (int m = 0; m < additionalFieldList.size(); m++) {
                                        Field addFieldObj = additionalFieldList.get(m);
                                        if (addFieldObj.getName().matches("mandatory")) {
                                            midlist = addFieldObj.getValue();
                                            if (midlist.indexOf(dlistid + "_" + arr1[0] + "/") != 0 && midlist.indexOf("/" + dlistid + "_" + arr1[0] + "/") < 0) {
                                                if (extension != "" && midlist.indexOf(dlistid + "_" + arr1[0] + extension + "/") != 0 && midlist.indexOf("/" + dlistid + "_" + arr1[0] + extension + "/") < 0) {

                                                    midlist += dlistid + "_" + arr1[0] + extension + "/";//+extension
                                                } else {
                                                    midlist += dlistid + "_" + arr1[0] + "/";//+extension
                                                }
                                                addFieldObj.setValue(midlist);
                                                fObj.setSaveRequired("true");
                                                break;
                                            }
                                        }
                                    }
                                    notifyAdapterWithPayload(position, Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                                    return;
                                }
                            }
                        }//end of else if required
                        else if (sum.toLowerCase().equals("notrequired") || (sum + "").indexOf("NOTREQUIRED") == 0) {
                            boolean domendetory = false;
                            if (extension.equals("")) {
                                String fieldarr1 = arr1[0];
                                String editpartid = (sum + "").substring(11);
                                if (!editpartid.equals("")) {
                                    Field nObj = null;
                                    for (int n = 0; n < fieldsList.size(); n++) {
                                        nObj = fieldsList.get(n);
                                        if (nObj.getId().equals("field" + editpartid)) {
                                            ToastUtil.showToastMessage("Please select the required options.", DListFormActivity.this);
                                            //ToDo : adapter.notifyItemChanged();
                                            break;
                                        }
                                    }
                                    //if(nObj == null) return true;
                                    String midlist = "";
                                    for (int m = 0; m < additionalFieldList.size(); m++) {
                                        Field addFieldObj = additionalFieldList.get(m);
                                        if (addFieldObj.getName().matches("mandatory")) {
                                            midlist = addFieldObj.getValue();
                                            if (midlist.indexOf(fieldarr1 + "/") == 0)
                                                midlist = midlist.replace(fieldarr1 + "/", "");
                                            else if (midlist.indexOf("/" + fieldarr1 + "/") > -1)
                                                midlist = midlist.replace("/" + fieldarr1 + "/", "/");
                                        }
                                        addFieldObj.setValue(midlist);
                                        break;
                                    }

                                    notifyAdapterWithPayload(position,Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                                    return;
                                }
                            } else {
                                String dlistid = (sum + "").substring(11);
                                if (dlistid.equals("")) {

                                } else {
                                    String midlist = "";
                                    for (int m = 0; m < additionalFieldList.size(); m++) {
                                        Field addFieldObj = additionalFieldList.get(m);
                                        if (addFieldObj.getName().matches("mandatory")) {
                                            midlist = addFieldObj.getValue();
                                            if (midlist.indexOf(dlistid + "_" + arr1[0] + extension + "/") == 0)
                                                midlist = midlist.replace(dlistid + "_" + arr1[0] + extension + "/", "");
                                            else if (midlist.indexOf("/" + dlistid + "_" + arr1[0] + extension + "/") > -1)
                                                midlist = midlist.replace("/" + dlistid + "_" + arr1[0] + extension + "/", "/");
                                            else if (midlist.indexOf(dlistid + "_" + arr1[0] + "/") == 0)
                                                midlist = midlist.replace(dlistid + "_" + arr1[0] + "/", "");
                                            else if (midlist.indexOf("/" + dlistid + "_" + arr1[0] + "/") > -1)
                                                midlist = midlist.replace("/" + dlistid + "_" + arr1[0] + "/", "/");
                                            addFieldObj.setValue(midlist);
                                            fObj.setSaveRequired("false");
                                            break;
                                        }
                                    }
                                    notifyAdapterWithPayload(position,Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                                    return;
                                }
                            }
                            //return true;

                        }// end of not required
                        float CheckNum = NaN;
                        //changed arr[2] to arr[1] here was getting IndexOutOfBoundsException
                        //TODO : Commented it as of now becuase when its splits there isn't a arr1[2] in java
                        if((sum != null && !sum.isEmpty())  && (arr1[1] != null && !arr1[1].isEmpty())){
                            if(arr1[1].indexOf("0") > -1){
                                try{
                                    CheckNum = Float.parseFloat(sum);
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                    if(sum.isEmpty()){
                                        CheckNum = NaN;
                                    }

                                }
                            }
                        }
                        if(isNaN(CheckNum)){
                            if (fObj.getType().toLowerCase().equals("checkbox")){
                                fObj.setValue(sum);

                            }else{
                                fObj.setValue(sum);
                                if(fObj.getValue() != sum && !fObj.getOptionsArrayList().isEmpty()){
//                                    if(obj.size < 2){
//                                        if(sum!='null'){
//                                            obj.options[0]=new Option(sum,sum);
//                                            obj.value=obj.options[0].value;
//                                        }
//                                    }
                                }
                            }

                            notifyAdapterWithPayload(position,Constant.PAYLOAD_EVALUATE_FUNCTION);
                            break;
                          //  notifyAdapterWithPayload(position, Constant.PAYLOAD_EVALUATE_FUNCTION);
                        }else  if((sum+"").indexOf("/") > -1 || (sum+"").indexOf("-") > 0 || (sum+"").indexOf("_") > -1|| (sum+"").indexOf(":") > -1){
                            fObj.setValue(sum);
                           // notifyAdapter(i);
                            notifyAdapterWithPayload(position, Constant.PAYLOAD_EVALUATE_FUNCTION);
                            break;
                        } else if(arr1.length == 3){
                          //  fObj.setValue(Math.round(Math.round(Double.parseDouble(sum)*100)/100)+".00");//for solving round of problem for 251.4984 in javascript
                            //  formatDouble(arr1[0]+extension,"");
                            if (arr1[2].matches("0.00|0")){
                                fObj.setValue(formatNumber(sum,"0.00"));
                            }else if (arr1[2].equals("0.000")) {
                                fObj.setValue(formatNumber(sum,"0.000"));
                            }else if(arr1[2].equals("0.0000")){
                                fObj.setValue(formatNumber(sum,"0.0000"));
                            }else{
                                fObj.setValue(formatNumber(sum,"0.00"));
                            }
                         //  notifyAdapter(i);
                            notifyAdapterWithPayload(position,Constant.PAYLOAD_EVALUATE_FUNCTION);
                            break;
                        }else{
                            //sum = parseInt((sum+0.000000000001)*100);
                            double s = Math.round((Double.parseDouble(sum))*100);//4.9350000000000001
                            double d = (s/100);
                            sum = String.valueOf(d);
                            fObj.setValue(sum);
                            //   formatDouble(arr1[0]+extension);
                         //  notifyAdapter(i);
                            notifyAdapterWithPayload(position,Constant.PAYLOAD_EVALUATE_FUNCTION);
                            break;
                        }
                    }
                }
            }
        }

    //   notifyAdapter(position);
     //   notifyAdapterWithPayload(position, Constant.PAYLOAD_EVALUATE_FUNCTION);
        //adapter.notifyItemChanged(i);
    }

    private String formatNumber(String value , String format) {
        String mValue = value;
        try{

            if(!value.isEmpty()){
                double d = Double.parseDouble(value);
                mValue = new DecimalFormat(format).format(d);
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e("getValue = ","value = " + value);
            mValue = value;
        }
        return mValue;
    }

    private String replacefunctionvalues(String func, String extension,
                                         String fieldValue) {

        int i = 0, j = 0, sum = 0;
        String sub = "", varid = "", varReplace = "";
        boolean isdate = false;
        int h = 0;

        while(func.indexOf("${")> -1 &&  h < 100){
            h = h + 1;
            i = func.indexOf("${");
            sub = func.substring(i);
            j = sub.indexOf("}");
            varid = sub.substring(2, j);

            varReplace = "${" + sub.substring(2, j) + "}";

            if (varid.toLowerCase().equals("id")) varid = "id";

            if (varid.toLowerCase().equals("state")) {
                String vreplace = "";
                //   if( document.getElementById('statefield'))
                //       vreplace = document.getElementById('statefield').value; //not clear
                if (vreplace.equals("")) vreplace = "0";
                func = func.replace(varReplace, vreplace);
                h = 0;
            }//enf of if varid == state


            boolean isDListFieldFound = false;
            //Previous dlist code april 13 2021
          /*  List<DListItem> dlistArray = FormFragment.fieldsList.get(dlistArrayField).getdListArray().get(mDlistButtonPosition).getDListItemList();
            int dlistArrPosition = -1;
            int itemFieldPosition = -1;
            for(int n=0;n<dlistArray.size();n++) {

                List<DList> dlistFieldValues = dlistArray.get(n).getDlistArray();
                for(int k=0; k < dlistFieldValues.size(); k++){
                    DList nObj = dlistFieldValues.get(k);
                    //"field"+fieldid+extension
                    if (nObj.getId().matches("field"+varid+extension)) {
                        fieldValue = nObj.getValue().trim();
                        isDListFieldFound = true;
                        itemFieldPosition = k;
                        dlistArrPosition = n;
                        break;
                    }
                }
            }*/

            DList nObj= null;
            for (int k = 0; k < dlistFieldValues.size(); k++) {
                 nObj = dlistFieldValues.get(k);
                if (nObj.getId().matches("field" + varid + extension)) {
                    fieldValue = nObj.getValue().trim();
                    isDListFieldFound = true;
                    break;
                }
            }

            if(isDListFieldFound){
                //TODO :_ pass dlistArray in arguments
                //this is for dlist
                if(nObj.getType().equals("checkbox")){
                    func = func.replace(varReplace,fieldValue);
                }else{
                    String vreplace = fieldValue;
                    String datePat1 = "^(\\d{1,2})(-)(\\d{1,2})(-)(\\d{4})$";
                    String datePat2 = "^(\\d{4})(-)(\\d{1,2})(-)(\\d{1,2})$";
                    List<String> matchArray = new ArrayList();
                    Matcher matcher = Pattern.compile(datePat2).matcher(vreplace);
                    while ( matcher.find() ) {
                        // Do something with the matched text
                        matchArray.add(matcher.group(0));
                        matchArray.add(matcher.group(1));
                        matchArray.add(matcher.group(2));
                        matchArray.add(matcher.group(3));
                        matchArray.add(matcher.group(4));
                        matchArray.add(matcher.group(5));
                    }

                    if(!matchArray.isEmpty()){
                        String d1 = matchArray.get(5);
                        String m1=matchArray.get(3);
                        String y1 = matchArray.get(1);
                        vreplace=d1+"/"+m1+"/"+y1;
                    }

                    int vlength = vreplace.length();
                    if((vlength == 10 || vlength == 16) && vreplace.indexOf('/')==2 && vreplace.lastIndexOf('/')==5 && func.indexOf('|') < 0 ){
                        isdate = true;
                    }
                    if(vlength > 7 && vreplace.lastIndexOf(',')==(vlength-7) && vreplace.lastIndexOf('.')==(vlength-3)) vreplace = vreplace.replace(",","");
                    else if(vlength > 8 && vreplace.lastIndexOf(',')==(vlength-8) && vreplace.lastIndexOf('.')==(vlength-4)) vreplace = vreplace.replace(",", "");
                    else if(vlength > 15 && vreplace.lastIndexOf(',')==(vlength-15) && vreplace.lastIndexOf('.')==(vlength-11)) vreplace = vreplace.replace( ",", "" );
                    func = func.replace(varReplace,vreplace);
                }//end of else
                h = 0;
            }else {
                List<Field> fieldsList = FormFragment.fieldsList;
                for (int a = 0; a < fieldsList.size(); a++) {
                    Field aObj = fieldsList.get(a);
                    String vid = "field" + varid;
                    if (aObj.getId().equals(vid)) {

                        if (aObj.getFieldType().equals("checkbox") || aObj.getType().equals("checkbox")) {
                            func = func.replace(varReplace,aObj.getValue() );
                        } else {
                            String vreplace = aObj.getValue();
                            int vlength = vreplace.length();

                            if ((vlength == 10 || vlength == 16) &&
                                    vreplace.indexOf("/") == 2 &&
                                    vreplace.lastIndexOf("/") == 5
                                    && func.indexOf("|") < 0) {
                                isdate = true;
                            }

                            if (vlength > 7 && vreplace.lastIndexOf(',') == (vlength - 7)
                                    && vreplace.lastIndexOf('.') == (vlength - 3)) {

                                vreplace = vreplace.replace(",", "");

                            }
                            else if (vlength > 8 && vreplace.lastIndexOf(',') == (vlength - 8)
                                    && vreplace.lastIndexOf('.') == (vlength - 4)) {
                                vreplace = vreplace.replace(",", "");
                            }
                            func = func.replace(varReplace, vreplace);
                          //  Log.e("not checkbox","func ="+func);
                        }
                        h = 0;
                        break; // added break here april 14 2021
                    }else{
                        //not found in the form
                        String[] arr1=func.split("@@");
                       // func="";
                        continue;
                    }
                }
            }

            if(isdate)
            {
                evaluatedate(func,extension);
                return "";
            }

        }
        Log.e("replacefunctonvalues","returned func = "+func);
        return func;
    }

    private void  evaluatedate(String func,String extension)
    {
        int objPostion = -1;
        String[] arr1 = func.split("@@");
        String oldValue = "";
        boolean isFieldFound = false;

        for(int i =0; i <dlistFieldValues.size(); i++){
            DList fObj = dlistFieldValues.get(i);
            if(fObj.getId().matches("field"+arr1[0]+extension)){
                objPostion = i;
                oldValue = fObj.getValue();
                isFieldFound = true;
                break;
            }
        }

        if(isFieldFound){
            int sep = 1;
            if(arr1[1].indexOf("-")>-1) sep = -1;

            String[] varr;
            if(sep > 0){
                varr = arr1[1].split("\\+");
            }else{
                varr = arr1[1].split("-");
            }

            String vreplace = varr[0];
            String days = varr[1];
            int adjust = 0;

            //if(arr1[1].split(sep>0?"+":"-")[2]) adjust=arr1[1].split(sep>0?"+":"-")[2];//+""
//           String[] jaar = arr1[1x].split(sep>0?"+":"-");
//           if(jaar != null){
//               String [] jaar1 =arr1[1].split(sep>0?"+":"-");
//               adjust =  Integer.parseInt(jaar1[2]);
//           }

            if(days.length() ==0){
                //TODO = pending
                //  obj.value = arr1[2];
                //  return true;
            }

            int dt1  = Integer.parseInt(vreplace.substring(0,2),10);
            int mon1 = Integer.parseInt(vreplace.substring(3,5),10)-1;
            int yr1  = Integer.parseInt(vreplace.substring(6,10),10);
            int hr1 = 0;
            int min1 = 0;

            if(vreplace.length()>10){
                hr1  = Integer.parseInt(vreplace.substring(11,13),10);
                min1 = Integer.parseInt(vreplace.substring(14,16),10);
            }

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-yyyy");
//          //Date d = new Date(yr1, mon1, dt1,hr1,min1,0);
//            Date d;
//            try {
//                d = sdf.parse(vreplace);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            if( isNaN(dt1)){
                if(days.indexOf('/')>-1)dlistFieldValues.get(objPostion).setValue("0.00");
                else dlistFieldValues.get(objPostion).setValue("");
            }else if(days.indexOf('/')>-1){
                vreplace = days;
                //pending
            }else{

                String fmDate = DateUtil.formatDate(vreplace,Constant.dd_MM_yyyy,Constant.yyyy_MM_dd);
                String dateString =  DateUtil.setDate(fmDate,sep*Integer.parseInt(days));
                dlistFieldValues.get(objPostion).setValue(dateString);

                final int finalObjPostion = objPostion;
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.notifyItemChanged(finalObjPostion);
//                    }
//                });
            }
        }
    }

    @Override
    protected void onDestroy() {

        if(mFunctionHelper != null){
            mFunctionHelper.onDestroy();
        }
      //  deleteCache(DListFormActivity.this);
        super.onDestroy();

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            boolean result = deleteDir(dir);
            if(result){
                Log.e("cache"," clear success");
            }else{
                Log.e("cache","  clear failed");
            }

        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {

            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void loadCustomDateTimePicker(int position, String onChange) {
        displayCustomDateTimePicker(position);
    }

    private void displayCustomDateTimePicker(final int position) {
      CustomDateTimePicker  custom = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM, long _timeInMillies) {

//
//                            ((TextView) findViewById(R.id.lablel))
//                                    .setText(calendarSelected
//                                            .get(Calendar.DAY_OF_MONTH)
//                                            + "/" + (monthNumber+1) + "/" + year
//                                            + ", " + hour12 + ":" + min
//                                            + " " + AM_PM);

                        String strDayOfMonth = CustomDateTimePicker.pad(calendarSelected
                                .get(Calendar.DAY_OF_MONTH));
                       String strMonth = CustomDateTimePicker.pad((monthNumber+1));
                       String strHour24 = CustomDateTimePicker.pad(hour24);
                       String strMin = CustomDateTimePicker.pad(min);


                        String selectedDateTime = strDayOfMonth
                                + "/" + strMonth + "/" + year
                                + " " +strHour24 + ":" + strMin;


                        dlistFieldValues.get(position).setValue(selectedDateTime);

                        notifyAdapterWithPayload(position,Constant.PAYLOAD_TEXT);
                      //  notifyAdapter(position);
                        String addFunction = dlistFieldValues.get(position).getAddFunction();

                        if(!addFunction.isEmpty()){
                            onAddFunction(position, addFunction,
                                    selectedDateTime);

                        }
                         //long timeInMillies = _timeInMillies;
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

}
