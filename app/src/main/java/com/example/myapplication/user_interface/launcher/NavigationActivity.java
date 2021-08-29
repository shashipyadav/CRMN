package com.example.myapplication.user_interface.launcher;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.fragment.ProjectFragment;
import com.example.myapplication.helper.PackageManagerHelper;
import com.example.myapplication.presells.PresellFragment;
import com.example.myapplication.user_interface.dashboard.view.DashboardFragment;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.forms.controller.CommunicatorInterface;
import com.example.myapplication.user_interface.forms.controller.helper.FormHelper;
import com.example.myapplication.user_interface.forms.view.WebViewFragment;
import com.example.myapplication.user_interface.home.controller.ProductActivity;
import com.example.myapplication.user_interface.login.LoginActivity;
import com.example.myapplication.user_interface.upcoming_meeting.view.UpcomingMeetingFragment;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.menu.controller.MenuHelper;
import com.example.myapplication.user_interface.quicklink.QuickLink;
import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.menu.controller.DrawerAdapter;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.menu.view.MenuFragment;
import com.example.myapplication.user_interface.menu.controller.OnMenuClickListener;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.home.controller.HomeFragment;
import com.example.myapplication.user_interface.pendingtask.PendingTaskFragment;
import com.example.myapplication.user_interface.quicklink.QuickLinksFragment;
import com.example.myapplication.user_interface.menu.model.DrawerItem;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.customviews.SimpleDividerItemDecoration;
import com.example.myapplication.util.VolleyErrorUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity
        implements MenuFragment.MenuListener,
        QuickLinksFragment.QuickLinkClickInterface {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private final String DEBUG_TAG = NavigationActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private ProgressDialog progressDialog;
    private RecyclerView rvMenu;
    private DrawerAdapter recyclerAdapter;
    private RelativeLayout logoView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private Animation animShow, animHide;
    private DatabaseManager dbManager;
    private SharedPrefManager mPrefManager;
    private FormHelper formHelper;
    private ArrayList<DrawerItem> menuList = new ArrayList<>();
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  throw new RuntimeException("Test Crash"); // Force a crash

        if(!checkPermission()) {
            requestPermission();
        }

        setContentView(R.layout.activity_navigation);
        SharedPrefManager prefManager = new SharedPrefManager(this);
        prefManager.getFcmToken();
        boolean isNotificationReceived = false;
        Bundle bundle =  getIntent().getExtras();
        if(bundle != null){
            isNotificationReceived = bundle.getBoolean(Constant.NOTIFICATION_RECEIVED);
        }

        //added for performance
        getWindow().setBackgroundDrawable(null);
        initViews();
     //   displayServerImage();

        initFrameLayout();
        if(isNotificationReceived){
            loadPendingTaskFragment();
        }else{
            loadHomeFragment();
        }

        callMenuAPI();
        setDrawerLayout();
        setBottomNavigationView();
        setRightNavigationView();
        initAnimation();
       // dbManager.deleteForm(100052);
        // dbManager.removeAll();

    }

    private void displayServerImage(){
        imageView = findViewById(R.id.server_image);
        String imageUrl = mPrefManager.getServerImageUrl();

        Picasso.with(this).load(imageUrl).placeholder(R.drawable.default_server_icon).into(imageView);

        NavigationView navigationView = (NavigationView) findViewById(R.id.right_nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView imgServerIcon = (ImageView) headerView.findViewById(R.id.server_imageView);
        Picasso.with(this).load(imageUrl).placeholder(R.drawable.default_server_icon).into(imgServerIcon);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean locationAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (readAccepted && writeAccepted && locationAccepted) {

                        //  Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    } else {
                       // Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE )) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
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
        new AlertDialog.Builder(NavigationActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() > 0)
//        {
//            getFragmentManager().popBackStackImmediate();
//        }
//        else
//        {
//            super.onBackPressed();
//        }
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e(DEBUG_TAG,"onNewIntent called");
        super.onNewIntent(intent);
        boolean isNotificationReceived = false;
        Bundle bundle =  getIntent().getExtras();
        if(bundle != null){
            isNotificationReceived = bundle.getBoolean(Constant.NOTIFICATION_RECEIVED);
        }

        if(isNotificationReceived){
            loadPendingTaskFragment();
        }else{
            loadHomeFragment();
        }
    }


    private void initViews(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPrefManager = new SharedPrefManager(this);
        formHelper = new FormHelper();
        logoView = findViewById(R.id.menu_title);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProjectFragment();
              //  Fragment fragment = new HomeFragment();
                replaceFragment(fragment);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
    }

    private void setDrawerLayout() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void initAnimation() {
        animShow = AnimationUtils.loadAnimation(this, R.anim.enter_from_right);
        animHide = AnimationUtils.loadAnimation(this, R.anim.exit_to_right);
    }

    private void initFrameLayout() {
        frameLayout = findViewById(R.id.menu_content_frame);
    }

    private void setBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuItemId = menuItem.getItemId();
                Fragment fragment = null;
                switch (menuItemId) {

                    case R.id.b_nav_home:
                        fragment = new ProjectFragment();
                        //fragment = new HomeFragment();
                        break;
                    case R.id.b_nav_dashboard:
                        fragment = new DashboardFragment();
                        break;

                    case R.id.b_nav_pending_task:
                        fragment = new PendingTaskFragment();
                        break;

                    case R.id.b_nav_quicklink:
                        fragment = new QuickLinksFragment();
                        break;

                    case R.id.b_nav_more:
                        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                            drawer.closeDrawer(Gravity.RIGHT);
                        } else {
                            drawer.openDrawer(Gravity.RIGHT);
                        }
                        break;

                    default:
                        break;
                }

                if (fragment != null) {
                    replaceFragment(fragment);
                }
                return true;
            }
        });
    }

    private void setRightNavigationView(){
        NavigationView rightNavigationView = findViewById(R.id.right_nav_view);
        View hView =  rightNavigationView.getHeaderView(0);

        TextView navUser = (TextView)hView.findViewById(R.id.nav_name);
        StringBuilder sb=new StringBuilder("Welcome ");
        sb.append(mPrefManager.getUserName());
        navUser.setText(sb);

        TextView navMobile = hView.findViewById(R.id.nav_mobile);
        navMobile.setText(mPrefManager.getMobileNo());

        rightNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuItemId = menuItem.getItemId();
                switch (menuItemId) {
                    case R.id.nav_wishlist:
                        Intent intent = new Intent(NavigationActivity.this,
                                ProductActivity.class);
                        intent.putExtra(Constant.EXTRA_URL,"");
                        intent.putExtra(Constant.EXTRA_MODE,"wishList");
                        intent.putExtra(Constant.EXTRA_TITLE, "Wishlist");
                        startActivity(intent);
                        break;

                    case R.id.nav_logout:
                       logoutConfirmationDialog();
                        break;
                }
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
                return true;
            }
        });

//        if(drawerItem.getText().toLowerCase().equals("logout")){
//        }else{
//            Fragment fragment = FormFragment.newInstance(drawerItem.getOnClick(), drawerItem.getText());
//            replaceFragment(fragment);
//        }

    }

    private void setRecyclerView() {
        rvMenu = (RecyclerView) findViewById(R.id.left_drawer);
        rvMenu.setHasFixedSize(false);
        SimpleDividerItemDecoration itemDecoration = new SimpleDividerItemDecoration(this);
        rvMenu.addItemDecoration(itemDecoration);
        rvMenu.setLayoutManager(new LinearLayoutManager(NavigationActivity.this));
        recyclerAdapter = new DrawerAdapter(NavigationActivity.this, menuList, listener);
        rvMenu.setAdapter(recyclerAdapter);
    }

    private void loadHomeFragment() {
        Fragment fragment = new ProjectFragment();
        //Fragment fragment = new HomeFragment();
        replaceFragment(fragment);
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
//        fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }
    private void loadPendingTaskFragment() {
        Fragment fragment = new PendingTaskFragment();
        replaceFragment(fragment);
    }

    public void replaceMenuFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        //fragmentTransaction.replace(R.id.menu_content_frame, fragment).addToBackStack(fragment.toString()).commit();
        fragmentTransaction.replace(R.id.menu_content_frame, fragment).commit();
    }

    public void replaceFragment(Fragment fragment) {
        try{
        if(getSupportFragmentManager().findFragmentById(R.id.content_frame) != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commitAllowingStateLoss();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
//                .addToBackStack(fragment.toString())
                .commit();

//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
//        fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(null).commit();
      //  fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(fragment.toString()).commit();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initDatabase() {
        dbManager = new DatabaseManager(NavigationActivity.this);
        dbManager.open();
    }

    private void callMenuAPI(){
        initDatabase();
        boolean formAvailable = dbManager.checkIfFormExists(Constant.APP_MENU);
        if (NetworkUtil.isNetworkOnline(NavigationActivity.this)) {
            String dateTime = "";
            if(!formAvailable){
                dateTime = Constant.SERVER_DATE_TIME;
            }else{
               dateTime = DateUtil.getCurrentDate("yyyy-MM-dd hh:mm:ss");
            }

           String menuURL = String.format(Constant.MENU_URL,
                    mPrefManager.getClientServerUrl(),
                    dateTime, mPrefManager.getCloudCode(), mPrefManager.getAuthToken());
            menuAPI(menuURL);
        } else {
            if(formAvailable){
                loadAppMenu();
            }else {
                DialogUtil.showAlertDialog(NavigationActivity.this,
                        "Network Error", "Oops! Please check your network connection", false, false);
            }
        }
    }

    private void menuAPI(String menuURL) {

        Log.e("MENU_URL",menuURL);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET,
                menuURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("MENU_API","RESPONSE = "+ response);
                        if(!response.isEmpty() && !response.toLowerCase().matches("nochange|invalid token!")){
                            boolean formAvailable = dbManager.checkIfFormExists(Constant.APP_MENU);
                            if(formAvailable){

                                dbManager.updateForm(0,response);
                            }else{
                                dbManager.insertForm(0, Constant.APP_MENU,response);
                            }
                        }
                            loadAppMenu();
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       VolleyErrorUtil.showVolleyError(NavigationActivity.this,error);
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                        loadAppMenu();
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
        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT, Constant.NO_OF_TRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void loadAppMenu(){
        String response = dbManager.getFormJson(0);
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("PrintMenu");
            MenuHelper menuHelper = new MenuHelper(this);
            menuList = menuHelper.setMenu(jsonArray);
            setRecyclerView();

        }catch (JSONException e)
        {
         if(progressDialog != null){
             progressDialog.dismiss();
         }
            e.printStackTrace();
        }
    }

    @Override
    public void menuItemClicked(DrawerItem drawerItem) {
        String chartId = "";
        if(!drawerItem.getOnClick().equals("")){
            chartId = formHelper.getChartId(drawerItem.getOnClick());
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constant.EXTRA_TITLE, drawerItem.getTitle());
        bundle.putString(Constant.EXTRA_CHART_ID, chartId);

        Fragment fragment = null;
        if(drawerItem.getTitle().toLowerCase().equals("company policy")){
            //we don't send chartid for company policy as its a static pdf
            fragment = new WebViewFragment();
            bundle.putString(Constant.EXTRA_CHART_ID, "");
        }else if(drawerItem.getTitle().toLowerCase().equals("branch locator")){
            fragment = new UpcomingMeetingFragment();
        }else{
            if(drawerItem.getChartType().toLowerCase().matches("html")){
                if(drawerItem.getTitle().equalsIgnoreCase("GraphicalSales")){
                    //we don't send chartid for company policy as its a static pdf
                    fragment = new PresellFragment();
                }else
                {
                    fragment = new WebViewFragment();

                }
            }else {
                fragment = new FormFragment(communicatorInterface);
            }
        }

        if(fragment != null){
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //  fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);

            //to remove from backstack
            fragmentTransaction.replace(R.id.content_frame, fragment).commit(); //addToBackStack(fragment.toString())
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private final OnMenuClickListener listener = new OnMenuClickListener() {
        @Override
        public void onMenuClicked(int position, DrawerItem drawerItem, int visibility) {
            String chartId = "";
            if(!drawerItem.getOnClick().equals("")){
               chartId = formHelper.getChartId(drawerItem.getOnClick());
            }

            if (!drawerItem.getListHeader().isEmpty()) {
                if (visibility == View.GONE) {
                    frameLayout.startAnimation(animHide);
                    frameLayout.setVisibility(View.GONE);
                } else {
                    frameLayout.setVisibility(View.VISIBLE);
                    frameLayout.startAnimation(animShow);
                }
                Fragment fragment = MenuFragment.newInstance(drawerItem.getListHeader(),
                        drawerItem.getListDataChild());
                replaceMenuFragment(fragment);
            } else {

                Bundle bundle = new Bundle();
                bundle.putString(Constant.EXTRA_TITLE, drawerItem.getTitle());
                bundle.putString(Constant.EXTRA_CHART_ID, chartId);

                //First Level Menu
                Fragment fragment = null;
                if(drawerItem.getTitle().toLowerCase().equals("ess")){
                    PackageManagerHelper pmhelper = new PackageManagerHelper(NavigationActivity.this);
                    pmhelper.directUserToESSApp();
                }else if(drawerItem.getTitle().toLowerCase().equals("company policy")){
                    ////we don't send chartid for company policy as its a static pdf
                    fragment = new WebViewFragment();
                    bundle.putString(Constant.EXTRA_CHART_ID, "");
                }else if(drawerItem.getTitle().toLowerCase().equals("branch locator")){
                    fragment = new UpcomingMeetingFragment();
                }else{
                    if(drawerItem.getChartType().toLowerCase().matches("html")){
                        fragment = new WebViewFragment();
                    }else {
                        fragment = new FormFragment(communicatorInterface);
                    }
                }

                if(fragment!= null){
                    fragment.setArguments(bundle);
                    replaceFragment(fragment);
                }
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        }
    };


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
              //  KeyboardUtil.hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onQuickLinkClicked(QuickLink obj) {

        Bundle bundle = new Bundle();
        bundle.putString(Constant.EXTRA_TITLE, obj.getTitle());
        bundle.putString(Constant.EXTRA_CHART_ID, obj.getChartId());

        Fragment fragment = new FormFragment(communicatorInterface);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(fragment.toString()).commit();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    private void logoutConfirmationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you wish to proceed?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        SharedPrefManager prefManager = new SharedPrefManager(NavigationActivity.this);
                        prefManager.clearSession();
                        dbManager.removeAll();

                        Intent intent = new Intent(NavigationActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    CommunicatorInterface communicatorInterface = new CommunicatorInterface() {
        @Override
        public void respond() {
            loadHomeFragment();
        }
    };

}
