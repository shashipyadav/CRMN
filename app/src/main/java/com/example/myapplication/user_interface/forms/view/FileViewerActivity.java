package com.example.myapplication.user_interface.forms.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.Constant;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FileViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer);

        try {
            Bundle bundle = getIntent().getExtras();
            String fileUrl = "";
            String fileName = "";
            if (bundle != null) {
                fileName = bundle.getString(Constant.EXTRA_TITLE);
                fileUrl = bundle.getString(Constant.EXTRA_URL);
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(fileName);

            Log.e("FileViewerActivity", fileUrl);
            final ImageView imageView = findViewById(R.id.image);
            if (!fileUrl.isEmpty()) {

                Picasso.with(this).load(fileUrl).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            } else {
                //display toast
            }
        }catch (Exception e){
            e.printStackTrace();
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
