package com.example.myapplication.user_interface.upcoming_meeting.controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.upcoming_meeting.model.MapModel;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder>{
    private List<MapModel> listdata;
    private Activity activity;
    private String filterData;

    // RecyclerView recyclerView;
    public FilterAdapter(List<MapModel> listdata, Activity activity,String filterData) {
        this.listdata = listdata;
        this.activity = activity;
        this.filterData = filterData;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_filter, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MapModel myListData = listdata.get(position);
        holder.tv_name.setText(myListData.getFilter());
      /*  if(filterData.equalsIgnoreCase("country")){
            holder.tv_name.setText(myListData.getCountry());

        }else if(filterData.equalsIgnoreCase("state")){
            holder.tv_name.setText(myListData.getState());

        }else if(filterData.equalsIgnoreCase("distict")){
            holder.tv_name.setText(myListData.getDistict());

        }else if(filterData.equalsIgnoreCase("busi_type")){
            holder.tv_name.setText(myListData.getDealer());

        }else if(filterData.equalsIgnoreCase("busi_class")){
            holder.tv_name.setText(myListData.getType());

        }*/

        holder.chkSelected.setChecked(myListData.isSelected());

        holder.chkSelected.setTag(listdata.get(position));


        holder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                MapModel contact = (MapModel) cb.getTag();

                contact.setSelected(cb.isChecked());
                listdata.get(position).setSelected(cb.isChecked());

              /*  Toast.makeText(
                        v.getContext(),
                        "Clicked on Checkbox: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG).show();*/
            }
        });

    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox chkSelected;
        public TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            chkSelected = (CheckBox) itemView
                    .findViewById(R.id.chkSelected);
        }
    }
    public List<MapModel> getListdata() {
        return listdata;
    }
}
