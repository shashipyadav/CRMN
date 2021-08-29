package com.example.myapplication.projectdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AmenitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public List<AmenitiesModel> amenitiesModelList;
    private SharedPrefManager prefManager;

    public AmenitiesAdapter(Activity activity, List<AmenitiesModel> amenitiesModelList, SharedPrefManager prefManager) {
        this.activity = activity;
        this.amenitiesModelList = amenitiesModelList;
        this.prefManager = prefManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amenities, parent, false);

        return new VContentInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((VContentInfoHolder) holder).title.setText(amenitiesModelList.get(position).getAmenitiesdesc());



        final String imageUrl = prefManager.getClientServerUrl()+ "uploads" +prefManager.getCloudCode() + "/Folder1808/" + amenitiesModelList.get(position).getAmenitiesimage();

        Picasso.with(activity)
                .load(imageUrl)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.progress_animation)

                .into(((VContentInfoHolder) holder).iv_image);

        ((VContentInfoHolder) holder).title.setText(amenitiesModelList.get(position).getAmenitiesdesc());
        ((VContentInfoHolder) holder).iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FullImageViewActivity.class);
                intent.putExtra("imageUrl",imageUrl);
                intent.putExtra("decp",amenitiesModelList.get(position).getAmenitiesdesc());
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return amenitiesModelList.size();
    }


    private class VContentInfoHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private LinearLayout ll_main;
        private ImageView iv_image;


        public VContentInfoHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);


           // Typeface custom_font1 = Typeface.createFromAsset(activity.getAssets(), "Titillium-Regular.otf");
            //((TextView) itemView.findViewById(R.id.title)).setTypeface(custom_font1);






        }
    }


}
