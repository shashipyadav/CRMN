package com.example.myapplication.presells;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.model.ProjectModel;
import com.example.myapplication.projectdetails.GallaryActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class Avilabiltiy_status_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public List<BuildingBottomStatusModel> buildingBottomStatusModels;

    public Avilabiltiy_status_Adapter(Activity activity, List<BuildingBottomStatusModel> buildingBottomStatusModels) {
        this.activity = activity;
        this.buildingBottomStatusModels = buildingBottomStatusModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_availabity_adater, parent, false);

        return new VContentInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((VContentInfoHolder) holder).tv_bg_color.setBackgroundColor(Color.parseColor(buildingBottomStatusModels.get(position).getColor()));
        ((VContentInfoHolder) holder).tv_status.setText(buildingBottomStatusModels.get(position).getStatus());




    }

    @Override
    public int getItemCount() {
        return buildingBottomStatusModels.size();
    }


    private class VContentInfoHolder extends RecyclerView.ViewHolder {

        private TextView tv_bg_color, tv_status;



        public VContentInfoHolder(View itemView) {
            super(itemView);

            tv_bg_color = (TextView) itemView.findViewById(R.id.tv_bg_color);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);



        }
    }


}
