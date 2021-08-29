package com.example.myapplication.user_interface.upcoming_meeting.controller;

import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListMainModel;
import com.example.myapplication.user_interface.upcoming_meeting.model.FilterListModel;

import java.util.ArrayList;
import java.util.List;

public class MultipleTypeImageAdaptern extends RecyclerView.Adapter<MultipleTypeImageAdaptern.ViewHolder> {

    private Activity mContext;
    private List<FilterListMainModel> mList;
    private LayoutInflater inflater;
    private int curentPost = -1;
    private RecyclerView filterValuesRV;
    public int selectedPostion = 0;
    public FilterValuesAdapter adapter;
    EditText et_filter;

    public  MultipleTypeImageAdaptern(Activity context, List<FilterListMainModel> list, RecyclerView filterValuesRV,EditText et_filter){
        mContext = context;
        this.mList = list;
        this .filterValuesRV =filterValuesRV;
        if(context != null){
            inflater = LayoutInflater.from(context);
        }
        this.et_filter = et_filter;
        et_filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub

                //   ((MultipleTypeImageAdaptern) multipleTypeImageAdaptern).adapter.getFilter().filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()> 2){
                    // filter your list from your input
                    filter(s.toString());
                    //you can use runnable postDelayed like 500 ms to delay search text
                }

            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_main_recyclern, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filterValuesRV.setAdapter(new FilterValuesAdapter(mContext, position,mList));

                 adapter = new FilterValuesAdapter( mContext,selectedPostion, mList.get(selectedPostion).getFilterList(),holder.tv_title);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
                filterValuesRV.setLayoutManager(layoutManager);
                filterValuesRV.setAdapter(adapter);
                selectedPostion = position;

                notifyDataSetChanged();
            }
        });

        // filterValuesRV.setAdapter(new FilterValuesAdapter(mContext, selectedPostion,mList));

        adapter = new FilterValuesAdapter( mContext,selectedPostion, mList.get(selectedPostion).getFilterList(),holder.tv_title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        filterValuesRV.setLayoutManager(layoutManager);
        filterValuesRV.setAdapter(adapter);

        holder.container.setBackgroundColor(selectedPostion == position ? Color.BLACK : Color.WHITE);
        if(selectedPostion ==position) {
            holder.tv_title.setTextColor(Color.WHITE);
        }else {
            holder.tv_title.setTextColor(Color.BLACK);
        }

        holder.tv_view.setVisibility(View.GONE);
        if(mList.get(position).isSelectedmain()){
            holder.tv_view.setVisibility(View.VISIBLE);

        }else {
            holder.tv_view.setVisibility(View.GONE);

        }
        holder.tv_title.setText(mList.get(position).getValue());
    }


    private void filter(String text) {
      //  Toast.makeText(mContext,"Tes",Toast.LENGTH_LONG).show();
        List<FilterListModel> filterdNames = new ArrayList<>();
        //

        //looping through existing elements
        for (FilterListModel s : mList.get(selectedPostion).getFilterList()) {
            //if the existing elements contains the search input
            if (s.getTitle().toLowerCase().contains(text.toLowerCase()) ) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }
        //adapter.filterList(filterdNames);
        adapter.filterList(mList,selectedPostion,filterdNames);


    }


    @Override
    public int getItemCount() {
        if (mList != null){
            return mList.size();
        }else{
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title;
        private TextView tv_view;
        View container;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView;

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_view = itemView.findViewById(R.id.tv_view);
        }
    }

    public List<FilterListMainModel> getlistMainModelList()
    {
        return mList;
    }



}

