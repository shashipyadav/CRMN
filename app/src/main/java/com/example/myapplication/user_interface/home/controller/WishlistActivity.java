package com.example.myapplication.user_interface.home.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.home.model.Product;
import com.example.myapplication.user_interface.home.model.ProductItem;
import com.example.myapplication.user_interface.home.view.ProductImageAdapter;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WishlistActivity extends AppCompatActivity implements ProductImageAdapter.DeleteProductInterface {

    private RecyclerView recyclerview;
    private DatabaseManager dbManager ;
    private ArrayList<ProductItem> wishList = new ArrayList<>();
    private ProductImageAdapter adapter_wishlist;
    private LinearLayout llNoItemsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        init();

//        TextView textView=findViewById(R.id.txturl);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Name="Swapnil";
//                Code="SSH";
//                if (checkPermission()) {
//                    new Downloading().execute(url);
//                }
//            }
//        });
    }

    private void init() {
        setTitle();
        initViews();
        initDatabase();
        setUpRecyclerView();
    }

    private  void initViews(){
        llNoItemsView = findViewById(R.id.no_items_view);
        ImageView noItemsImg = findViewById(R.id.no_items_img);
        noItemsImg.setImageResource(R.drawable.ic_grid);
        TextView txtNoItems = findViewById(R.id.txt_msg);
        txtNoItems.setText("Wishlist Empty");
    }

    private void initDatabase() {
        dbManager = new DatabaseManager(this);
        dbManager.open();
    }

    private void setUpRecyclerView() {
        recyclerview = findViewById(R.id.recyclerview_product);
        recyclerview.setHasFixedSize(true);
        setLayoutManager();
        setRecyclerViewAdapter();
    }

    private void setLayoutManager() {
        int numberOfColumns = 2;
        GridLayoutManager llm = new GridLayoutManager(
                getApplicationContext(),
                numberOfColumns);

        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerview.setLayoutManager(llm);
    }

    private void setRecyclerViewAdapter() {
        List<String> wishListJson = new ArrayList<>();
        wishListJson.addAll(dbManager.getWishlistProducts());
        Gson gson = new Gson();
        ProductItem product = null;
        for(int i = 0 ; i < wishListJson.size(); i++) {

            String json = wishListJson.get(i);
            product = gson.fromJson(json, ProductItem.class);
            wishList.add(product);
        }

        adapter_wishlist = new ProductImageAdapter(
                this,
                wishList,
                true,
                this);
        recyclerview.setAdapter(adapter_wishlist);
        checkIfListIsEmpty();
    }

    private void checkIfListIsEmpty () {
        if(wishList.isEmpty()){
            llNoItemsView.setVisibility(View.VISIBLE);
        }else{
            llNoItemsView.setVisibility(View.GONE);
            adapter_wishlist.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTitle(){
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Wishlist");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onProductRemoved(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WishlistActivity.this);
        builder.setTitle("Remove from Wishlist");
        builder.setMessage("Are you sure you wish to proceed?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       String itemId =  wishList.get(position).getItemID();
                        DatabaseManager databaseManager = new DatabaseManager(WishlistActivity.this);
                        databaseManager.deleteProductFromWishlist(itemId);
                        wishList.remove(position);
                        adapter_wishlist.notifyDataSetChanged();
                        if(wishList.isEmpty()){
                            llNoItemsView.setVisibility(View.VISIBLE);
                        }else{
                            llNoItemsView.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
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

//    public class Downloading extends AsyncTask<String, Integer, String> {
//
//        @Override
//        public void onPreExecute() {
//            super .onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... url) {
//            File mydir = new File(Environment.getExternalStorageDirectory() + "/wishlist");
//            if (!mydir.exists()) {
//                mydir.mkdirs();
//            }
//
//            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//            Uri downloadUri = Uri.parse(url[0]);
//            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
//            String date = dateFormat.format(new Date());
//
//            request.setAllowedNetworkTypes(
//                    DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//                    .setAllowedOverRoaming(false)
//                    .setTitle("Downloading")
//                    .setDestinationInExternalPublicDir("/wishlist", date + ".jpg");
//
//            manager.enqueue(request);
//            return mydir.getAbsolutePath() + File.separator + date + ".jpg";
//        }
//
//        @Override
//        public void onPostExecute(String s) {
//            super .onPostExecute(s);
//            if(dbHelper.insertWishListProduct(s,
//                    Name,Code)){
//                Toast.makeText(getApplicationContext(), "done",
//                        Toast.LENGTH_SHORT).show();
//            } else{
//                Toast.makeText(getApplicationContext(), "not done",
//                        Toast.LENGTH_SHORT).show();
//            }
//            Toast.makeText(getApplicationContext(), "image added successfully", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    //runtime storage permission
//    public boolean checkPermission() {
//        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        int WRITE_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
//            return false;
//        }
//        if((WRITE_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE);
//            return false;
//        }
//        return true;
//    }
//
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode==PERMISSION_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            //do somethings
//        }
//    }
}