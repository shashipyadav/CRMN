package com.example.myapplication.user_interface.upcoming_meeting.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListMainModel;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListModel;

import java.util.List;

public class FilterValuesAdapter extends RecyclerView.Adapter<FilterValuesAdapter.MyViewHolder>  {

    private Context context;
    private Integer filterIndex;
    private List<FilterListModel> list;
    private SharedPreferences sharedpreferences;
    private TextView tv_title;
    //NewFilter mfilter;


    //  public FilterValuesAdapter(Context context, Integer filterIndex, List<FilterListMainModel> list, TextView tv_title) {
    public FilterValuesAdapter(Context context, Integer filterIndex, List<FilterListModel> list, TextView tv_title) {
        this.context = context;
        this.filterIndex = filterIndex;
        this.list = list;
        this.tv_title = tv_title;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_value_item, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(view);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {

        // final FilterListMainModel tmpFilter = list.get(filterIndex);
//        Log.e("title", list.get(position).getTitle());
        try {
            //myViewHolder.value.setText(tmpFilter.getFilterList().get(position).getTitle());
            myViewHolder.value.setText(list.get(position).getTitle());
        } catch (Exception ex) {
            myViewHolder.value.setText("NA");

            ex.printStackTrace();
        }

        //myViewHolder.value.setChecked(tmpFilter.getFilterList().get(position).isSelected());
        myViewHolder.value.setChecked(list.get(position).isSelected());

        // myViewHolder.value.setTag(tmpFilter.getFilterList().get(position));
        myViewHolder.value.setTag(list.get(position));


        myViewHolder.value.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                FilterListModel contact = (FilterListModel) cb.getTag();

                contact.setSelected(cb.isChecked());
                //tmpFilter.getFilterList().get(position).setSelected(cb.isChecked());

                notifyDataSetChanged();


            }
        });




    }

    @Override
    public int getItemCount() {
        //return list.get(filterIndex).getFilterList().size();
        return list.size();
    }




    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View container;
        CheckBox value;

        public MyViewHolder(View view) {
            super(view);
            container = view;
            value = view.findViewById(R.id.value);
        }
    }

    /*public List<FilterListModel> getListdata() {
        return list.get(filterIndex).getFilterList();
}*/

    public void filterList(List<FilterListMainModel> filterdNames, int postion, List<FilterListModel> seletfilterdNamesss) {

       /*filterdNames.get(postion).setFilterList(seletfilterdNamesss);
        this.list = filterdNames;
        this.filterIndex = postion;
        notifyDataSetChanged();*/

        this.list = seletfilterdNamesss;
        //this.filterIndex = postion;
        notifyDataSetChanged();
    }


}

