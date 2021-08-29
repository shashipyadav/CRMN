package com.example.myapplication.user_interface.home.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.home.model.ProductItem;
import com.example.myapplication.user_interface.home.view.ProductDetailsImageSliderAdapter;
import com.example.myapplication.Constant;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WishlistProductDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private String itemId = "";
    private List<String> mList = new ArrayList<>();
    private RelativeLayout rlWhatsapp;
    private ViewPager viewPager;
    private ProductDetailsImageSliderAdapter adapter;
    private TextView btnMoreDetails;
    private TextView txtTitle;
    private TextView txtBrochure, txtTechnicalDoc;
    private ProductItem product;
    private LinearLayout llMoreDetail;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_product_details);

        Intent intent = getIntent();
        product = (ProductItem) intent.getSerializableExtra(Constant.EXTRA_OBJECT);

        init();
    }

    private void init() {
        initViews();
        setBackImage();
        setImages();
        setTabIndicator();
        setProductDetailsData();
    }

    private void initViews(){
        rlWhatsapp = findViewById(R.id.rl_whatsapp);
        rlWhatsapp.setOnClickListener(this);

        btnMoreDetails = findViewById(R.id.btn_more);
        btnMoreDetails.setOnClickListener(this);

        txtBrochure = findViewById(R.id.txt_doc_brochure);
        txtBrochure.setOnClickListener(this);

        txtTechnicalDoc = findViewById(R.id.txt_tech_doc);
        txtTechnicalDoc.setOnClickListener(this);

        llMoreDetail = findViewById(R.id.more_layout);
    }

    private void setBackImage() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ImageView imageBack = toolbar.findViewById(R.id.img_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setImages(){
        if(product.getProductInfo() != null){
            mList = product.getProductInfo().getImageFullView();
        }

        // dummyImages();
        viewPager = findViewById(R.id.viewpager);
        adapter = new ProductDetailsImageSliderAdapter( WishlistProductDetailsActivity.this, mList,product.getItemID());
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void setTabIndicator() {
        for(int n = 0; n < tabLayout.getTabCount(); n++) {
            View tab = ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(n);

            if(n == tabLayout.getTabCount()-1) {
                int lastIndex = mList.size()-1;
                String imUrl = mList.get(lastIndex);
                String extension= imUrl.substring(imUrl.lastIndexOf(".") + 1);
                Log.e("EXTENSION", extension);

                if(extension.toLowerCase().matches("jpg|png")) {
                    tab.setBackground(getResources().getDrawable(R.drawable.tab_selector));
                }else {
                    tab.setBackground(getResources().getDrawable(R.drawable.tab_selector_triangle));
                }
            }else{
                tab.setBackground(getResources().getDrawable(R.drawable.tab_selector));
            }
        }
    }

    private void setProductDetailsData() {

        txtTitle = findViewById(R.id.title);
        txtTitle.setText(product.getItemID());
//        txtItemId = findViewById(R.id.txt_item_id);
//        txtItemId.setText(product.getProductInfo().getItemId());

    }

    @Override
    public void onClick(View v) {
        if(v == txtBrochure){
            Intent intent = new Intent(WishlistProductDetailsActivity.this, PdfViewActivity.class);
            intent.putExtra(Constant.EXTRA_URL, product.getProductInfo().getDocumentBrochure());
            intent.putExtra(Constant.EXTRA_TITLE, getResources().getString(R.string.doc_brochure));
            startActivity(intent);
        }

        if(v == txtTechnicalDoc){
            Intent intent = new Intent(WishlistProductDetailsActivity.this, PdfViewActivity.class);
            intent.putExtra(Constant.EXTRA_URL, product.getProductInfo().getTechnicalDocument());
            intent.putExtra(Constant.EXTRA_TITLE, getResources().getString(R.string.doc_technical));
            startActivity(intent);
        }
        if(v == btnMoreDetails){
            if(btnMoreDetails.getText().toString().toLowerCase().contains("more")){

                btnMoreDetails.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this,R.drawable.ic_arrow_up), null);

                btnMoreDetails.setText("Less Details");
                llMoreDetail.setVisibility(View.VISIBLE);
            }else{
                btnMoreDetails.setText("Click For More Details");
                btnMoreDetails.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this,R.drawable.ic_arrow_down), null);
                llMoreDetail.setVisibility(View.GONE);
            }
        }
        if(v == rlWhatsapp){
            Bitmap bitmap = takeScreenshot();
            saveBitmap(bitmap);
            shareIt();
        }
    }


    private Bitmap takeScreenshot() {
        Bitmap bitmap = null;
        try {
            if (viewPager != null) {
                bitmap = Bitmap.createBitmap(adapter.currentImageView.getWidth(),
                        adapter.currentImageView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas b = new Canvas(bitmap);
                adapter.currentImageView.draw(b);
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap){
        if(bitmap !=null) {
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
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this product");
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_product)));
        }
    }


}