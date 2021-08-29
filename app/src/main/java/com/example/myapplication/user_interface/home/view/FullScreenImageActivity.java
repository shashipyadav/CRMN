package com.example.myapplication.user_interface.home.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {

    String imageUrl = "";
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        getArguments();
        setActionBar();
        displayImage();
    }

    private void getArguments() {
        if(getIntent().getExtras() != null){
            imageUrl = getIntent().getExtras().getString(Constant.EXTRA_IMAGE_PATH);
            title = getIntent().getExtras().getString(Constant.EXTRA_ITEM_ID);
        }
    }

    private void setActionBar(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
    }



    private void displayImage() {
        ImageView imageView = findViewById(R.id.product_image);
        Picasso.with(this).load(imageUrl)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.default_product_img)
                .into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}