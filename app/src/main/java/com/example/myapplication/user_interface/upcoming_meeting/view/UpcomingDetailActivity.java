package com.example.myapplication.user_interface.upcoming_meeting.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.user_interface.upcoming_meeting.model.MapModel;

public class UpcomingDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_deatils);
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
      String json=  intent.getStringExtra("data");
        MapModel mapModel = (MapModel) JsonUtil.jsonToObject(json, MapModel.class);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView tv_contact_pers = findViewById(R.id.tv_contact_pers);
        TextView tv_company_name = findViewById(R.id.tv_company_name);
        TextView tv_mobile = findViewById(R.id.tv_mobile);
        TextView tv_email = findViewById(R.id.tv_email);
        TextView tv_busi_class = findViewById(R.id.tv_busi_class);
        TextView tv_busi_type = findViewById(R.id.tv_busi_type);
        TextView tv_address = findViewById(R.id.tv_address);

        tv_contact_pers.setText(mapModel.getContact_person());
        tv_company_name.setText(mapModel.getName());
        tv_mobile.setText(mapModel.getPhone());
        tv_email.setText(mapModel.getEmail());
        tv_busi_class.setText(mapModel.getType());
        tv_busi_type.setText(mapModel.getDealer());
        tv_address.setText(mapModel.getAddress()+"\n"+mapModel.getDistict()+","+mapModel.getState()+","+mapModel.getCountry());

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
