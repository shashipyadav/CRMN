package com.example.myapplication.user_interface.upcoming_meeting.controller;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.upcoming_meeting.model.MapModel;
import com.example.myapplication.user_interface.upcoming_meeting.view.UpcomingDetailActivity;

import java.util.List;
import java.util.Locale;

public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.ViewHolder>{
    private List<MapModel> listdata;
    private Activity activity;

    // RecyclerView recyclerView;
    public MapListAdapter(List<MapModel> listdata, Activity activity) {
        this.listdata = listdata;
        this.activity = activity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_map_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MapModel myListData = listdata.get(position);
        holder.tv_name.setText(myListData.getName());
        holder.tv_address.setText(myListData.getAddress()+"\n"+myListData.getDistict()+","+myListData.getState()+","+myListData.getCountry());
        holder.tv_delaer.setText(myListData.getDealer());
        holder.type.setText(myListData.getType());
        holder.tv_darection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", Double.parseDouble(myListData.getLatitude()),Double.parseDouble(myListData.getLongitude()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                activity.startActivity(intent);
            }
        });
        holder.ic_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = activity.getPackageManager();
                try {
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");

                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

                    waIntent.setPackage("com.whatsapp");
                    String broadcastMessage =myListData.getName()+"\n"+ myListData.getAddress()+"\n"+myListData.getDistict()+","+myListData.getState()+","+myListData.getCountry()+"\n"+myListData.getDealer()+"\n"+myListData.getType();
                    waIntent.putExtra(Intent.EXTRA_TEXT, broadcastMessage);
                    activity.startActivity(Intent.createChooser(waIntent, "Send Notification"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(activity, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, UpcomingDetailActivity.class);
                intent.putExtra("data", JsonUtil.objectToJson(myListData));
                activity.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public TextView tv_address;
        public TextView tv_delaer;
        public TextView type;
        public ImageView tv_darection;
        public ImageView ic_whatsapp;
        public LinearLayout ll_main;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_delaer = (TextView) itemView.findViewById(R.id.tv_delaer);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            type = (TextView) itemView.findViewById(R.id.type);
            tv_darection = (ImageView) itemView.findViewById(R.id.tv_darection);
            ic_whatsapp = (ImageView) itemView.findViewById(R.id.iv_wht);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);
        }
    }
    public void filterList(List<MapModel> filterdNames) {
        this.listdata = filterdNames;
        notifyDataSetChanged();
    }
    public List<MapModel> getListdata() {
        return listdata;
    }

}