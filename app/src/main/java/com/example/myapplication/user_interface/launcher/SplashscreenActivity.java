package com.example.myapplication.user_interface.launcher;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.login.LoginActivity;
import com.squareup.picasso.Picasso;

public class SplashscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final SharedPrefManager prefManager = new SharedPrefManager(SplashscreenActivity.this);
        ImageView img = findViewById(R.id.splash_img);

        try {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean isUserLoggedIn = prefManager.getIsUserLoggedIn();
                    Intent intent = null;
                    if (isUserLoggedIn) {
                        intent = new Intent(SplashscreenActivity.this,
                                NavigationActivity.class);
                    } else {
                        intent = new Intent(SplashscreenActivity.this,
                                LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
