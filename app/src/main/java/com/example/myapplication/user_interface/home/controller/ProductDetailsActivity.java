package com.example.myapplication.user_interface.home.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.home.model.ProductItem;
import com.example.myapplication.user_interface.home.view.ProductDetailsImageSliderAdapter;
import com.example.myapplication.Constant;
import com.example.myapplication.util.ToastUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DEBUG_TAG = ProductDetailsActivity.class.getSimpleName();
    private List<String> mList = new ArrayList<>();
    private RelativeLayout rlWhatsapp ,rlWishList;
    private ViewPager viewPager;
    private ProductDetailsImageSliderAdapter adapter;
    private TextView btnMoreDetails, btnWishList;
    private TextView txtBrochure, txtTechnicalDoc;
    private ProductItem product;
    private ProgressDialog mWaiting;
    private LinearLayout llMoreDetail;
    private DatabaseManager dbManager;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewBasicInfo;
    private RecyclerView recyclerViewAddiInfo;
    private String mode = "";
    private Toolbar toolbar;
    private ImageView imageWishList;
    private LinearLayout llProductBottomLayout;
    private RelativeLayout rLWishListWhatsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        getArguments();
        init();
        setTabIndicator();
        setProductDetailsData();

    }

    private void getArguments() {
        Intent intent = getIntent();
        product = (ProductItem) intent.getSerializableExtra(Constant.EXTRA_OBJECT);
        mode = intent.getStringExtra(Constant.EXTRA_MODE);
    }

    private void init() {
        initViews();
        setImages();
        setBackImage();
        setWishListButton();
        checkMode();
    }
    private void setBackImage() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        ImageView  imageBack = toolbar.findViewById(R.id.img_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setWishListButton() {
        imageWishList = toolbar.findViewById(R.id.img_wishlist);
        imageWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this,
                        ProductActivity.class);
                intent.putExtra(Constant.EXTRA_MODE,"wishList");
                startActivity(intent);
            }
        });
    }

    private void setTabIndicator() {

        for(int n = 0; n < tabLayout.getTabCount(); n++) {
            View tab = ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(n);
            if(n == tabLayout.getTabCount()-1){
                int lastIndex = mList.size()-1;
                String imUrl = mList.get(lastIndex);
                String extension= imUrl.substring(imUrl.lastIndexOf(".") + 1);
                Log.e("EXTENSION", extension);

                if(extension.toLowerCase().matches("jpg|png")){
                    tab.setBackground(getResources().getDrawable(R.drawable.tab_selector));
                }else {
                    tab.setBackground(getResources().getDrawable(R.drawable.tab_selector_triangle));
                }
            }else{
                tab.setBackground(getResources().getDrawable(R.drawable.tab_selector));
            }
        }
    }


    private void checkMode() {
        switch (mode) {
            case "wishList" :
                wishlisthideShow();
                break;

            case "productList" :
                productsHideShow();
                break;

            default:
                break;

        }
    }
    private void wishlisthideShow() {
        imageWishList.setVisibility(View.GONE);
        llProductBottomLayout.setVisibility(View.GONE);
        rLWishListWhatsApp.setVisibility(View.VISIBLE);

    }

    private void productsHideShow() {
        imageWishList.setVisibility(View.VISIBLE);
        llProductBottomLayout.setVisibility(View.VISIBLE);
        rLWishListWhatsApp.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setButtonWishlist();
    }

    private void setBasicInfo() {
        recyclerViewBasicInfo.setLayoutManager(new LinearLayoutManager(this));
        ItemDetailsAdapter adapter = new ItemDetailsAdapter(this,
                product.getProductInfo().getItmBasicInfo());

        recyclerViewBasicInfo.setAdapter(adapter);
    }

    private void setAdditionalInfo() {
        recyclerViewAddiInfo.setLayoutManager(new LinearLayoutManager(this));
        ItemDetailsAdapter adapter = new ItemDetailsAdapter(this,
                product.getProductInfo().getItemAddInfo());
        recyclerViewAddiInfo.setAdapter(adapter);
    }

    private void initViews() {
        dbManager = new DatabaseManager(ProductDetailsActivity.this);
        dbManager.open();
        rlWhatsapp = findViewById(R.id.rl_whatsapp);
        rlWhatsapp.setOnClickListener(this);
        rlWishList = findViewById(R.id.rl_wishlist);
        btnWishList = findViewById(R.id.btn_wishList);
        setButtonWishlist();
        rlWishList.setOnClickListener(this);
        btnMoreDetails = findViewById(R.id.btn_more);
        btnMoreDetails.setOnClickListener(this);
        txtBrochure = findViewById(R.id.txt_doc_brochure);
        txtBrochure.setOnClickListener(this);
        txtTechnicalDoc = findViewById(R.id.txt_tech_doc);
        txtTechnicalDoc.setOnClickListener(this);
        llMoreDetail = findViewById(R.id.more_layout);
        recyclerViewBasicInfo = findViewById(R.id.recyclerview_basic);
        recyclerViewAddiInfo = findViewById(R.id.recyclerview_additional);
        llProductBottomLayout = findViewById(R.id.ll_products_bottom);
        rLWishListWhatsApp = findViewById(R.id.relative_whatsapp);
        rLWishListWhatsApp.setOnClickListener(this);
    }

    private void setButtonWishlist(){
        if(dbManager.checkIfProductExists(product.getItemID())){
            btnWishList.setText(R.string.saved_to_wishlist);
            btnWishList.setCompoundDrawablesWithIntrinsicBounds( ContextCompat.getDrawable(this,R.drawable.ic_wishlist_red), null,null, null);
        }else{
            btnWishList.setText(R.string.add_to_wishlist);
            btnWishList.setCompoundDrawablesWithIntrinsicBounds( ContextCompat.getDrawable(this,R.drawable.ic_wishlist_black), null,null, null);
        }
    }

    private void setImages(){
        if(product.getProductInfo() != null){
            mList = product.getProductInfo().getImageFullView();
        }
        viewPager = findViewById(R.id.viewpager);
        adapter = new ProductDetailsImageSliderAdapter( ProductDetailsActivity.this, mList,product.getItemName());
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == rlWhatsapp || v == rLWishListWhatsApp) {
            Bitmap bitmap = takeScreenshot();
            saveBitmap(bitmap);
            shareIt();
        }

        if(v == btnMoreDetails) {
            if(btnMoreDetails.getText().toString().toLowerCase().contains("more")){

                btnMoreDetails.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this,R.drawable.ic_arrow_up), null);
                btnMoreDetails.setText("Less details");
                recyclerViewAddiInfo.setVisibility(View.VISIBLE);
            }else {
                btnMoreDetails.setText("Click for more details");
                btnMoreDetails.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this,R.drawable.ic_arrow_down), null);
                recyclerViewAddiInfo.setVisibility(View.GONE);
            }
        }

        if(v == txtBrochure) {
            if(!product.getProductInfo().getDocumentBrochure().isEmpty()){
                Intent intent = new Intent(ProductDetailsActivity.this, PdfViewActivity.class);
                intent.putExtra(Constant.EXTRA_URL, product.getProductInfo().getDocumentBrochure());
                intent.putExtra(Constant.EXTRA_TITLE, getResources().getString(R.string.doc_brochure));
                startActivity(intent);
            }else{
                ToastUtil.showToastMessage("Document Brochure Not Available",this);
            }
        }

        if(v == txtTechnicalDoc){
            if(!product.getProductInfo().getTechnicalDocument().isEmpty()) {
                Intent intent = new Intent(ProductDetailsActivity.this, PdfViewActivity.class);
                intent.putExtra(Constant.EXTRA_URL, product.getProductInfo().getTechnicalDocument());
                intent.putExtra(Constant.EXTRA_TITLE, getResources().getString(R.string.doc_technical));
                startActivity(intent);
            }else{
                ToastUtil.showToastMessage("Technical Document Not Available",this);
            }
        }

        if(v == rlWishList){
            if(!dbManager.checkIfProductExists(product.getItemID())){
                Gson gson = new Gson();
                String jsonInString = gson.toJson(product);
                if(dbManager.insertWishListProduct(product.getItemID(), jsonInString)){
                    ToastUtil.showToastMessage("Added to Wishlist", ProductDetailsActivity.this);
                }else{
                    ToastUtil.showToastMessage("Error while adding to Wishlist!", ProductDetailsActivity.this);
                }
            }else{
                dbManager.deleteProductFromWishlist(product.getItemID());
                ToastUtil.showToastMessage("Removed from Wishlist", ProductDetailsActivity.this);
            }
            setButtonWishlist();
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

    private void shareIt() {
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

            if(product.getProductInfo() != null){
                shareIntent.putExtra(Intent.EXTRA_TEXT,"Name : "+product.getItemName() +"\nCode : "+product.getItemID() +"\nDescription : "+ product.getItemDesc()+"\n\n"+
                        "Please click on the link for more details \n" +
                        "https://www.simpolo.net/");
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_product)));
            }else{
                ToastUtil.showToastMessage("Product Not Available",this);
            }
        }
    }

    private void setProductDetailsData() {
        if(product.getProductInfo() != null) {

            String separator = ": ";
            TextView txtTitle = findViewById(R.id.title);
            txtTitle.setText(product.getItemName());
             setBasicInfo();
             setAdditionalInfo();

            TextView txtItemId = findViewById(R.id.txt_item_code);
            txtItemId.setText(separator +product.getItemID());

            TextView txtDesign = findViewById(R.id.txt_desc);
            txtDesign.setText(separator +product.getItemDesc());

        }
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

}
