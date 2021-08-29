package com.example.myapplication.user_interface.home.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.home.model.ProductItem;
import com.example.myapplication.user_interface.home.view.ProductImageAdapter;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListMainModel;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.NetworkUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.myapplication.Constant.EXTRA_FILTER_STRING;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String DEBUG_TAG = ProductActivity.class.getSimpleName();
    private List<ProductItem> productList;
    private ProgressDialog mWaiting;
    private SwipeRefreshLayout pullToRefresh;
    private RecyclerView recyclerView;
    private String title = "";
    private ProductImageAdapter adapter;
    private LinearLayout llFilterSortView;
    private DatabaseManager dbManager ;
    private Button btnFilter;
    private String apiUrl = "";
    private String itemCategory = "";
    private String itemGroup = "";
    private String mode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getArguments();
        initViews();
        checkMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mode.equals("wishList")) {
            callAdapter(getProductListFromDb());
        }
    }

    private void getArguments () {
        if(getIntent().getExtras() != null){
            title = getIntent().getExtras().getString(Constant.EXTRA_TITLE);
            apiUrl = getIntent().getExtras().getString(Constant.EXTRA_URL);
            mode = getIntent().getExtras().getString(Constant.EXTRA_MODE);
        }
    }

    private void initViews() {
        setActionBar();
        pullToRefresh = findViewById(R.id.pullToRefresh);
        llFilterSortView = findViewById(R.id.ll_sort_filter_view);
        recyclerView = findViewById(R.id.recyclerview_product);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        btnFilter = findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(this);
    }

    private void checkMode() {
        switch (mode) {
            case "wishList" :
                initDatabase();
                hideViews();
                callAdapter(getProductListFromDb());
                break;

            case "productList" :
                showViews();
                setPullToRefresh();
                callProductsAPI();
                break;

            default:
                break;
        }
    }

    private void initDatabase() {
        dbManager = new DatabaseManager(this);
        dbManager.open();
    }

    private void setPullToRefresh() {
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callProductsAPI();
                pullToRefresh.setRefreshing(false);
            }
        });
    }


    private void hideViews(){
        //filter button
        llFilterSortView.setVisibility(View.GONE);
        //swiperefreshlayout
        pullToRefresh.setEnabled(false);
    }

    private void showViews(){
        //filter button
        llFilterSortView.setVisibility(View.VISIBLE);
        //swiperefreshlayout
        pullToRefresh.setEnabled(true);
    }

    private void callProductsAPI(){
        if (NetworkUtil.isNetworkOnline(this)) {
            callProductListAPI();
        }else{
            DialogUtil.showAlertDialog(this,
                    "No Internet Connection!",
                    "Please check your internet connection and try again",
                    false,
                    false);
        }
    }

    private void setActionBar() {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void callProductListAPI(){

        showProgressDialog();
        Log.e(DEBUG_TAG, "PRoduct list URL = " + apiUrl);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                          Log.e(DEBUG_TAG, "callProductListAPI Response=" + response);
                        try {
                            productList = new ArrayList<>();
                            ProductItem productItem = null;
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                productItem =  new Gson().fromJson(jsonObject.toString(), ProductItem.class);
                                productItem.convertStringToObject(productItem.getItemDetails());
                                productItem.addVideoLinkToList();

                                if(!productItem.getItemDetails().equals("[]") ){
                                    productList.add(productItem);
                                }
                                itemGroup = productItem.getItmGroup();
                                itemCategory =   productItem.getItmCategory();
                            }
                             callAdapter(productList);
                             hideProgressDialog();
                        } catch (Exception e) {
                            callAdapter(productList);
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callAdapter(productList);
              //  Toast.makeText(ProductActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        });
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT,
                Constant.NO_OF_TRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void showProgressDialog() {
        mWaiting = ProgressDialog.show(this, "",
                "Loading...", false);
    }

    private void hideProgressDialog() {
        if (mWaiting != null) {
            mWaiting.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnFilter) {

            String filterValueString = itemCategory + "@j@" + itemGroup;
            List<FilterListMainModel> filterList =   ProductFilterHelper.getProductFilterList(productList);

            Intent intent = new Intent(ProductActivity.this, ProductFilterActivity.class);
            intent.putExtra("filterData", "busi_class");
            intent.putExtra(EXTRA_FILTER_STRING, filterValueString);
            startActivityForResult(intent, 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {

            try {
                String dataFilter = data.getStringExtra("filterData");
                List<ProductItem> productList2 = new ArrayList<>();
                List<String> productList1 = (List<String>) JsonUtil.jsonArrayToListObject(dataFilter, String.class);

                for (int j = 0; j < productList.size(); j++) {

                    boolean isExist = false;
                    for (int i = 0; i < productList1.size(); i++) {
                        String datafi = productList1.get(i);

                        if ( productList.get(j).getProductInfo() != null){
                            List<LinkedHashMap<String,String>> basicHasMap = productList.get(j).getProductInfo().getItmBasicInfo();
                            for(int n=0; n < basicHasMap.size(); n++){
                                LinkedHashMap<String,String> hm = basicHasMap.get(n);
                                Iterator<Map.Entry<String, String>> basicIterator = hm.entrySet().iterator();
                                while (basicIterator.hasNext()) {
                                    Map.Entry mapElement = (Map.Entry)basicIterator.next();

                                    if(datafi.equalsIgnoreCase((String)mapElement.getValue())){
                                        System.out.println(mapElement.getKey());
                                        isExist = true;
                                        break;
                                    }
                                }
                            }

                            if(!isExist){
                                List<LinkedHashMap<String,String>> addHashMap = productList.get(j).getProductInfo().getItemAddInfo();
                                for(int m=0; m < addHashMap.size(); m++){
                                    LinkedHashMap<String,String> hm = addHashMap.get(m);
                                    Iterator<Map.Entry<String, String>> addIterator = hm.entrySet().iterator();

                                    while (addIterator.hasNext()) {
                                        Map.Entry mapElement = (Map.Entry)addIterator.next();

                                        if(datafi.equalsIgnoreCase((String)mapElement.getValue())){
                                            System.out.println(mapElement.getKey());
                                            isExist = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (isExist) {
                        productList2.add(productList.get(j));
                    }
                }

              //  ToastUtil.showToastMessage("No Items found after applying Filter",ProductActivity.this);
                callAdapter(productList2);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void callAdapter(List<ProductItem> products) {

        boolean isWishList = mode.equals("wishList");

        LinearLayout llNoItemsView = findViewById(R.id.no_items_view);
        if((products == null || products.isEmpty())  ){
         //llFilterSortView.setVisibility(View.GONE);
           llNoItemsView.setVisibility(View.VISIBLE);
            ImageView noItemsImg = findViewById(R.id.no_items_img);
            noItemsImg.setImageResource(R.drawable.ic_grid);
            TextView txtNoItems = findViewById(R.id.txt_msg);
            txtNoItems.setText("No Products Found");
            adapter = new ProductImageAdapter(ProductActivity.this,
                    products,
                    isWishList,
                    null);
            recyclerView.setAdapter(adapter);

        }else{
          //llFilterSortView.setVisibility(View.VISIBLE);
            llNoItemsView.setVisibility(View.GONE);
            adapter = new ProductImageAdapter(ProductActivity.this, products,
                    isWishList,null);
            recyclerView.setAdapter(adapter);
        }
    }

    private  List<ProductItem>  getProductListFromDb() {
        List<ProductItem> productItems = new ArrayList<>();
        List<String> wishListJson = new ArrayList<>();
        wishListJson.addAll(dbManager.getWishlistProducts());
        Gson gson = new Gson();
        ProductItem product = null;
        for(int i = 0 ; i < wishListJson.size(); i++) {

            String json = wishListJson.get(i);
            product = gson.fromJson(json, ProductItem.class);
            productItems.add(product);
        }
        return productItems;
    }


}
