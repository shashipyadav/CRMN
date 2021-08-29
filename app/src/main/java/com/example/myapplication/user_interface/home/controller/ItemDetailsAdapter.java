package com.example.myapplication.user_interface.home.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemDetailsAdapter extends RecyclerView.Adapter<ItemDetailsAdapter.ViewHolder> {

    private Context mContext;
    private  List<LinkedHashMap<String,String>> mList;
    private LayoutInflater inflater;

    public ItemDetailsAdapter(Context mContext,  List<LinkedHashMap<String,String>> mList) {
        this.mContext = mContext;
        this.mList = mList;
        if(mContext != null){
            inflater = LayoutInflater.from(mContext);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{
            final  LinkedHashMap<String,String> item = mList.get(position);

            Set<String> keySet = item.keySet();
            String[] keyArray
                    = keySet.toArray(new String[keySet.size()]);
            String key = keyArray[0];
            String value = item.get(key);
//
//            String key =  entry.;
//            String value = entry.getValue();


            holder.txtKey.setText(key);
            holder.txtValue.setText(": " + value);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtKey, txtValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKey = itemView.findViewById(R.id.key);
            txtValue = itemView.findViewById(R.id.value);
        }
    }
}
