package com.example.myapplication.user_interface.dlist.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.user_interface.dlist.model.DlistRowItem;
import com.example.myapplication.user_interface.dlist.view.DListFormActivity;
import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.vdlist.VlistDlistFormActivity;
import com.example.myapplication.util.PixelUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DListRecyclerAdapter extends
        RecyclerView.Adapter<DListRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<DlistRowItem> list;
    private int mDlistButtonArrayPosition = -1;
    private int mDlistItemPosition = -1;
    private String mFieldId;
    private CheckBoxListener listener;
    private SparseBooleanArray selectedItems;
    private Typeface typeface;
    private boolean isVlist;

    public interface CheckBoxListener {
        void onCheckBoxClicked(int position);
    }

    public DListRecyclerAdapter(Context context,
                                List<DlistRowItem> list,
                                String fieldId,
                                int dlistButtonArrayPosition,
                                int dlistItemPosition,
                                CheckBoxListener listener,
                                boolean isVlist) {
        this.context = context;
        this.list = list;
        this.mFieldId = fieldId;
        this.mDlistButtonArrayPosition = dlistButtonArrayPosition;
        this.mDlistItemPosition = dlistItemPosition;
        this.selectedItems = new SparseBooleanArray();
        this.listener = listener;
        typeface = ResourcesCompat.getFont(context, R.font.montserrat);
        this.isVlist = isVlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dlist_field, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
      //  final List<DList> dListsFields = list.get(position).getDlistArray();
        final JSONArray dListsFields = list.get(position).getDlistArray();
        holder.bind(dListsFields);

        holder.mCheckBox.setChecked(list.get(position).isSelected());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent intent;
                if(isVlist){
                    intent = new Intent(context, VlistDlistFormActivity.class);
                }else{
                     intent = new Intent(context, DListFormActivity.class);
                }

                // Field object which has the dlist object
                bundle.putSerializable(Constant.EXTRA_FIELD_ID, mFieldId);

                // position of dlist button array list in initial field list array
                bundle.putInt(Constant.EXTRA_DLIST_BUTTON_POSITION, mDlistButtonArrayPosition);
                bundle.putInt(Constant.EXTRA_DLIST_ITEM_POSITION, mDlistItemPosition);

                // position of dlistfield row in list array
                int dlistRowPosition = position;
                dlistRowPosition++;
                bundle.putInt(Constant.EXTRA_DLIST_ROW_POSITION, dlistRowPosition);
                bundle.putString(Constant.EXTRA_MODE, "");
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        //in some cases, it will prevent unwanted situations
        holder.mCheckBox.setOnCheckedChangeListener(null);

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCheckBoxClicked(position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelection() {
        selectedItems.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                list.get(i).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLinearLayout;
        CheckBox mCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.linearLayout);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }

        //List<DList> dListsFields
        public void bind(JSONArray dListsFields) {

            try {
                if(mLinearLayout!= null){
                    mLinearLayout.removeAllViews();
                }

                for (int i = 0; i < dListsFields.length(); i++) {
                    JSONObject jsonObject = dListsFields.getJSONObject(i);
                    String value = jsonObject.getString("mValue");
                    String type = jsonObject.getString("mType");
                    String fieldType = jsonObject.getString("mFieldType");
                    String searchReq = jsonObject.getString("mSearchRequired");

                    boolean isHidden = false;
                    if (type.toLowerCase().equals("hidden")) {
                        isHidden = true;
                    }
                    if (fieldType.toLowerCase().equals("hidden")) {
                        isHidden = true;
                    }
                    if (searchReq.toLowerCase().equals("false")) {
                        isHidden = true;
                    }

                    if(fieldType.toLowerCase().matches("checkbox|boolean")) {
                        if(value.equals("0")){
                            value = "";
                        }
                    }

                    TextView tv = new TextView(context);
                    if (!isHidden) {
                        tv.setText(value);
                        tv.setPadding(20, 20, 20, 20);
                        tv.setTextSize(16);
                        tv.setTypeface(typeface);
                        tv.setWidth(PixelUtil.convertDpToPixel(context, 150f));
                        tv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        tv.setBackground(context.getResources().getDrawable(R.drawable.cell_shape_light_bg));
                        tv.setGravity(Gravity.CENTER);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        mLinearLayout.addView(tv);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        }
    }

