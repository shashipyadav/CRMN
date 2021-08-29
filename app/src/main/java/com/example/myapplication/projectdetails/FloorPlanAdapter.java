package com.example.myapplication.projectdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
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

public class FloorPlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public List<FloorPlanModel> floorPlanModelList;
    private SharedPrefManager prefManager;

    public FloorPlanAdapter(Activity activity, List<FloorPlanModel> floorPlanModelList, SharedPrefManager prefManager) {
        this.activity = activity;
        this.floorPlanModelList = floorPlanModelList;
        this.prefManager = prefManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_floorplan, parent, false);

        return new VContentInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((VContentInfoHolder) holder).title.setText(floorPlanModelList.get(position).getFloorplandesc());



        final String imageUrl = prefManager.getClientServerUrl()+ "uploads" +prefManager.getCloudCode() + "/Folder1806/" + floorPlanModelList.get(position).getFloorplanimage();
        Log.d("imageUrl",imageUrl);
        Picasso.with(activity)
                .load(imageUrl)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.progress_animation)

                .into(((VContentInfoHolder) holder).iv_image);

        ((VContentInfoHolder) holder).iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FullImageViewActivity.class);
                intent.putExtra("imageUrl",imageUrl);
                intent.putExtra("decp",floorPlanModelList.get(position).getFloorplandesc());

                activity.startActivity(intent);
            }
        });
    ((VContentInfoHolder) holder).title.setText(floorPlanModelList.get(position).getFloorplandesc());

    }

    @Override
    public int getItemCount() {
        return floorPlanModelList.size();
    }


    private class VContentInfoHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private LinearLayout ll_main;
        private ImageView iv_image;


        public VContentInfoHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);


           // Typeface custom_font1 = Typeface.createFromAsset(activity.getAssets(), "Titillium-Regular.otf");
           // ((TextView) itemView.findViewById(R.id.title)).setTypeface(custom_font1);






        }
    }


}
