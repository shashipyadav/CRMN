package com.example.myapplication.user_interface.dashboard.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.dashboard.model.FilterMoreModel;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class FilterMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<FilterMoreModel> filterMoreModelList;
    FilterMoreInterface mListener;
    ArrayAdapter<String> adapter;



    public FilterMoreAdapter(Context context, List<FilterMoreModel> mList, FilterMoreInterface listener){
        this.mContext = context;
        this.filterMoreModelList = mList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Constant.TYPE_EDIT_DATE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_date, parent, false);
            return new DateViewHolder(view);

        }else if(viewType == Constant.TYPE_SPINNER){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_spinner, parent, false);
            return new SpinnerViewHolder(view);

        }else{
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_edit_text, parent, false);
            return new EditTextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, final int position) {
        final FilterMoreModel field = filterMoreModelList.get(position);

        if (getItemViewType(position) ==  Constant.TYPE_EDIT_DATE) {
            final DateViewHolder mHolder = (DateViewHolder) holder;
            mHolder.txtView.setText(field.getFieldName());
            String dateString = field.getValue();
            dateString = DateUtil.formatDate(dateString, Constant.yyyy_MM_dd, Constant.dd_MM_yyyy);
            mHolder.etDate.setText(dateString);

            mHolder.etDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.dateClickListener(position);
                }
            });

        }else if(getItemViewType(position) ==  Constant.TYPE_SPINNER){

            final SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
            mHolder.txtView.setText(field.getFieldName());
            List<String> dropdownList = new ArrayList<String>();
            dropdownList = field.getDropdownList();

            if(!dropdownList.isEmpty()){
               adapter  = new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_spinner_item, dropdownList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                mHolder.spinner.setAdapter(adapter);

            }else{
               mListener.loadSpinnerData(position);
            }

            final List<String> finalDropdownList = dropdownList;
            mHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    mListener.onValueChanged(position, finalDropdownList.get(pos));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else {
            final EditTextViewHolder mHolder = (EditTextViewHolder) holder;
            mHolder.txtView.setText(field.getFieldName());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filterMoreModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String fieldType = filterMoreModelList.get(position).getDataType();

        if (fieldType.toLowerCase().equals("date")) {
            return Constant.TYPE_EDIT_DATE;
        }else if(fieldType.toLowerCase().contains("sql")){
            return Constant.TYPE_SPINNER;
        }else{
            return Constant.TYPE_EDIT_TEXT;
        }
    }
    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;
        EditText etDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
            etDate = itemView.findViewById(R.id.edit_text_date);
        }
    }

   public  class SpinnerViewHolder extends RecyclerView.ViewHolder{
        TextView txtView;
        Spinner spinner;
        public SpinnerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
            spinner = itemView.findViewById(R.id.spinner);
//            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                    Log.e(getClass().getSimpleName(), "parent.getSelectedITem()" + parent.getSelectedItem());
//                    mListener.onValueChanged(getAdapterPosition(), parent.getSelectedItem().toString());
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });

        }
    }

    public class EditTextViewHolder extends RecyclerView.ViewHolder{
        TextView txtView;

        public EditTextViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
        }
    }
}


