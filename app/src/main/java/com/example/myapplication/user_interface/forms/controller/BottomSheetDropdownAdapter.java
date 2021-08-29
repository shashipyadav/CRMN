package com.example.myapplication.user_interface.forms.controller;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.model.OptionModel;
import com.example.myapplication.user_interface.pendingtask.PendingTaskAdapter;
import com.example.myapplication.user_interface.pendingtask.Task;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetDropdownAdapter  extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public List<OptionModel> dropdownlist;
    public Context context;
    public LayoutInflater inflater;
    private String fieldType;
    private BottomSheetDropdownClickListener listener;
    private List<OptionModel>  selected;
    private ValueFilter valueFilter;
    private List<OptionModel> mFilterList;
    private SparseBooleanArray selectedItems;
    private OnMdcomboItemSelectedListener mdcomboItemSelectedListener;
    
    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public interface BottomSheetDropdownClickListener{
        public void onItemSelected(String value);
    }

    public interface OnMdcomboItemSelectedListener {
        public void disableDropDownItems(int position);
    }

    public void setMdcomboItemSelectedListener(OnMdcomboItemSelectedListener mdcomboItemSelectedListener) {
        this.mdcomboItemSelectedListener = mdcomboItemSelectedListener;
    }

    public BottomSheetDropdownAdapter(Context context,
                                      List<OptionModel> dropdownlist,
                                      String fieldType,
                                      BottomSheetDropdownClickListener listener) {

        this.context        = context;
        this.dropdownlist   = dropdownlist;
        this.inflater       = LayoutInflater.from(context);
        this.listener       = listener;
        this.fieldType      = fieldType;
        this.selected       = new ArrayList<>();
        this.mFilterList    = dropdownlist;
        this.selectedItems  = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if(fieldType.toLowerCase().equalsIgnoreCase("mdcombo")){
             v = inflater.inflate(R.layout.item_bottom_sheet_dropdown_multiselect, parent, false);
            return new MultiSelectViewHolder(v);
        }else{
             v = inflater.inflate(R.layout.item_bottom_sheet_dropdown, parent, false);
            return new SingleSelectViewHolder(v);
        }
    }

    public void onSelected(String value){
        if(dropdownlist != null){
                for(int i = 0 ; i < dropdownlist.size(); i ++){
                    OptionModel obj = dropdownlist.get(i);

                    if(value.equals("%")){
                        if(i != 0){
                            obj.setEnabled(false);
                        }else{
                            obj.setEnabled(true);
                        }
                    }else{
                        if(i != 0){
                            obj.setEnabled(true);
                        }else{
                            obj.setEnabled(false);
                        }
                    }
                }
                notifyDataSetChanged();
            }

        }

    public void onDeselected() {

        String selected = getSelected().toString();
        if (dropdownlist != null) {

            for (int i = 0; i < dropdownlist.size(); i++) {
                OptionModel obj = dropdownlist.get(i);
                if(selected.equals("[]")){
                    obj.setEnabled(true);
                }else if(selected.equals("%")){
                    if(i != 0 ){
                        obj.setEnabled(false);
                    }else{
                        obj.setEnabled(true);
                    }
                }else{
                    if(i != 0 ){
                        obj.setEnabled(true);
                    }else{
                        obj.setEnabled(false);
                    }
                }
            }

            notifyDataSetChanged();
        }

        }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final OptionModel item = dropdownlist.get(position);

        if(fieldType.toLowerCase().equalsIgnoreCase("mdcombo")){
            MultiSelectViewHolder mHolder = (MultiSelectViewHolder) holder;
            mHolder.textValue.setText(item.getValue());

            mHolder.checkBox.setEnabled(item.isEnabled());
            if(item.isEnabled()){
                mHolder.textValue.setTextColor(Color.BLACK);
                CompoundButtonCompat.setButtonTintList(mHolder.checkBox, ColorStateList.valueOf(Color.BLACK));
            }else{
                mHolder.textValue.setTextColor(Color.GRAY);
                CompoundButtonCompat.setButtonTintList(mHolder.checkBox, ColorStateList.valueOf(Color.GRAY));
            }


            //in some cases, it will prevent unwanted situations
            mHolder.checkBox.setOnCheckedChangeListener(null);
            //if true, your checkbox will be selected, else unselected
            mHolder.checkBox.setChecked(item.isSelected());

            mHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (selected.contains(item)) {
                            selected.remove(item);
                            item.setSelected(false);
                        } else {
                            selected.add(item);
                            item.setSelected(true);
                        }

                        mdcomboItemSelectedListener.disableDropDownItems(position);
                    }
            });
        }else{
            SingleSelectViewHolder mHolder = (SingleSelectViewHolder) holder;

            mHolder.textValue.setText(item.getValue());

            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedValue = item.getValue();
                    if (selectedValue.contains(" --- ")) {
                        String[] arr = selectedValue.split(" --- ");
                        selectedValue = arr[0].trim();
                    }
                    listener.onItemSelected(selectedValue);
                }
            });
        }
    }

    public List<OptionModel> getSelected() {
        return selected;
    }

    @Override
    public int getItemCount() {
        return dropdownlist.size();
    }

    public class SingleSelectViewHolder extends RecyclerView.ViewHolder{

        TextView textValue;

        public SingleSelectViewHolder(@NonNull View itemView) {
            super(itemView);
            textValue = itemView.findViewById(R.id.txt_value);
        }
    }

    public class MultiSelectViewHolder extends RecyclerView.ViewHolder{

        TextView textValue;
        AppCompatCheckBox checkBox;

        public MultiSelectViewHolder(@NonNull View itemView) {
            super(itemView);
            textValue = itemView.findViewById(R.id.txt_value);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    private class ValueFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if(constraint !=null && constraint.length() > 0){
                List<OptionModel> filterList = new ArrayList<>();
                for (int i = 0; i < mFilterList.size(); i++) {

                    if (mFilterList.get(i).getValue().toUpperCase()
                                    .contains(constraint.toString().toUpperCase()))

                    {
                        filterList.add(mFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mFilterList.size();
                results.values = mFilterList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dropdownlist = (List<OptionModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
