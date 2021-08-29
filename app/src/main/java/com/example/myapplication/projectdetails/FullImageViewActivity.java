package com.example.myapplication.projectdetails;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.etiennelawlor.imagegallery.library.ui.TouchImageView;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

public class FullImageViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_sreen);
        TouchImageView imageView = (TouchImageView) findViewById(R.id.iv_full_screen);
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        String decp = intent.getStringExtra("decp");

        Log.d("imageUrl", imageUrl);
        Picasso.with(FullImageViewActivity.this)
                .load(imageUrl)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.progress_animation)
                .into(imageView);

        TextView tv_dec = (TextView) findViewById(R.id.tv_dec);
        tv_dec.setText(decp);

      /*  Typeface custom_font1 = Typeface.createFromAsset(com.itaakash.android.nativecustomerapp.activity.FullImageViewActivity.this.getAssets(), "Titillium-Regular.otf");
        tv_dec.setTypeface(custom_font1);*/
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
