package com.example.myapplication.user_interface.dashboard.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dashboard.model.Dashboard;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener,
        FilterBottomSheetFragment.FilterBottomSheetClickListener {

    private static final String DEBUG_TAG = ChartActivity.class.getSimpleName();
    private String title = "";
    private String chartId = "";
    private FloatingActionButton fabMenu, fabSummary,fabShare;
    private Group fabGroup;
    private boolean mShowingBack = false;
    private SharedPrefManager mPrefManager;
    private String formUrl = "";
    boolean isOut = false;
    private Dashboard dashboardObj;
    String jsonMyObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        overridePendingTransition( R.anim.enter_from_right, R.anim.exit_to_left);
        getArguments();
        initView();
        formUrl = getDashboardMenuUrl();

        if (savedInstanceState == null) {
            Log.e(DEBUG_TAG, "savedInstanceState == null");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, ChartFrontFragment.newInstance(jsonMyObject,formUrl,title))
                    .commit();
        }
    }

    private void getArguments(){
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            jsonMyObject = bundle.getString(Constant.EXTRA_OBJECT);
            dashboardObj = new Gson().fromJson(jsonMyObject,Dashboard.class);
            chartId = dashboardObj.getChartId();
            title = bundle.getString(Constant.EXTRA_TITLE);
        }
    }

    private void initView(){
        mPrefManager = new SharedPrefManager(this);
        setActionBar();
        initFab();
    }

    private String getDashboardMenuUrl(){
        DatabaseManager  mDbManager = new DatabaseManager(ChartActivity.this);
        mDbManager.open();
        boolean formAvailable = mDbManager.checkIfFormExists(Integer.parseInt(chartId));
        Log.e(DEBUG_TAG, "formAvailable = "+ formAvailable);

            String dateTime = "";
            if(!formAvailable){
                dateTime = Constant.SERVER_DATE_TIME;
            }else{
                //1991-10-03%2011:20:00
                dateTime = DateUtil.getCurrentDate("yyyy-MM-dd hh:mm:ss");
            }

        return String.format(Constant.FORM_URL,
                mPrefManager.getClientServerUrl(),
                chartId,dateTime, mPrefManager.getCloudCode(), mPrefManager.getAuthToken());
    }

    private void flipCard() {
        if (mShowingBack) {
            mShowingBack = false;
            getSupportFragmentManager().popBackStack();
            fabSummary.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_summary_white));
            return;
        }

        // Flip to the back.
        mShowingBack = true;
        fabSummary.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_back));
        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        getSupportFragmentManager()
                .beginTransaction()
                // Replace the default fragment animations with animator resources representing
                // rotations when switching to the back of the card, as well as animator
                // resources representing rotations when flipping back to the front (e.g. when
                // the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                // Replace any fragments currently in the container view with a fragment
                // representing the next page (indicated by the just-incremented currentPage
                // variable).
                .replace(R.id.container, ChartSummaryBackFragment.newInstance(chartId,title,formUrl))
                // Add this transaction to the back stack, allowing users to press Back
                // to get to the front of the card.
                .addToBackStack(null)
                // Commit the transaction.
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fabMenu.getVisibility() == View.GONE){
            fabMenu.show();
        }
    }

    private void setActionBar(){
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initFab(){
        fabGroup = findViewById(R.id.group);
        fabMenu = findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(this);
        fabShare = findViewById(R.id.fabShare);
        fabShare.setOnClickListener(this);
        fabSummary = findViewById(R.id.fabSummary);
        fabSummary.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public void onClick(View v) {
        if(v == fabMenu){
            if (isOut) {
              fabGroup.setVisibility(View.GONE);
            } else {
                fabGroup.setVisibility(View.VISIBLE);
            }
            isOut = !isOut;
        }

        if(v == fabSummary){
            flipCard();
        }


        if(v == fabShare){
            fabGroup.setVisibility(View.GONE);
            fabMenu.hide();
            Bitmap bitmap = takeScreenshot();
            saveBitmap(bitmap);
            shareIt();
        }
    }

    private Bitmap takeScreenshot() {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.root_layout);
        Bitmap bitmap = null;
        if (coordinatorLayout != null) {
             bitmap = Bitmap.createBitmap(coordinatorLayout.getWidth(),
                    coordinatorLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas b = new Canvas(bitmap);
            coordinatorLayout.draw(b);
        }
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap){
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareIt(){
        File imagePath = new File(getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", newFile);

        Log.e("CONTENT URI",String.valueOf(contentUri));
        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    public void sendFilterValues(String title, String value) {
        if (NetworkUtil.isNetworkOnline(ChartActivity.this)) {
//                pieChart.clear();
//                pieChart.setNoDataText("Loading");
//                pieChart.invalidate();
//                callChartDataAPI(value);
        } else {
            DialogUtil.showAlertDialog(ChartActivity.this, "No Internet Connection!",
                    "Please check your internet connection and try again", false, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
