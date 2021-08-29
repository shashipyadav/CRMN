package com.example.myapplication.user_interface.vlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dlist.controller.DListRecyclerAdapter;
import com.example.myapplication.user_interface.forms.controller.helper.FileHelper;
import com.example.myapplication.user_interface.forms.view.OpenUrlActivity;
import com.example.myapplication.user_interface.summary.SummaryActivity;
import com.example.myapplication.util.ExtensionUtil;
import com.example.myapplication.util.PixelUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class VlistAdapter extends RecyclerView.Adapter<VlistAdapter.ViewHolder>{

    private Context context;
    Typeface typeface;
    List<List<Vlist>> list;
    private SharedPrefManager prefManager;

    public VlistAdapter(Context mContext, List<List<Vlist>> list) {
        this.context = mContext;
        this.list = list;
        typeface = ResourcesCompat.getFont(context, R.font.montserrat);
        prefManager = new SharedPrefManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final List<Vlist> dListsFields = list.get(position);
        holder.bind(dListsFields);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String recordId =  getRecordId(dListsFields);
                Bundle bundle = new Bundle();
                bundle.putString(Constant.VLIST_FORM_ID, prefManager.getVFormId());
                bundle.putString(Constant.EXTRA_MODE,"update"); // new = new Form or update = form which already exists
                bundle.putString(Constant.EXTRA_RECORD_ID,recordId);
                Intent intent = new Intent(context,VlistFormActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private String getRecordId( List<Vlist> dListsFields) {

        String recordId = "";
        for(int i=0; i<dListsFields.size(); i++) {

            if(dListsFields.get(i).columnField.toLowerCase().equals("record_id")) {
                recordId =  dListsFields.get(i).getFieldValue();
                break;
            }
        }

        return recordId;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mLinearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.linearLayout);
        }

        public void bind(List<Vlist> response) {
            Typeface typefaceNormal =  ResourcesCompat.getFont(context, R.font.montserrat);
            if(mLinearLayout != null){
                mLinearLayout.removeAllViews();
            }
            try {
                TextView tv = null;
                for (int i = 0; i < response.size(); i++) {
                    Vlist vObj = response.get(i);
                    String value = vObj.getFieldValue();
                    String title = vObj.getColumnTitle();


                    if (!title.matches("^-.*|Record ID")) {

                        tv = new TextView(context);
                        tv.setText(value);
                        tv.setPadding(20, 20, 20, 20);
                        tv.setBackground(context.getResources().getDrawable(R.drawable.cell_shape_light_bg));
                        tv.setTextSize(16);
                        tv.setWidth(PixelUtil.convertDpToPixel(context, 150f));
                        tv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        tv.setGravity(Gravity.CENTER);
                     //   tv.setTextIsSelectable(true);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv.setTypeface(typefaceNormal);

                        if (value.startsWith("http") || value.startsWith("https")) {
                            tv.setText(Html.fromHtml("<font color=#000000>  <u>" + value + "</u>  </font>"));
                            final String finalValue1 = value;
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, OpenUrlActivity.class);
                                    intent.putExtra(Constant.EXTRA_URL, finalValue1);
                                    context.startActivity(intent);
                                }
                            });

                        } else {
                            if (!value.matches("^(.+)@(.+)$")) {
                                String extension = ExtensionUtil.getExtension(value);
                                if (!extension.isEmpty()) {
                                    tv.setText(Html.fromHtml("<font color=#000000>  <u>" + value + "</u>  </font>"));

                                    final String finalValue = value;
                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FileHelper fileHelper = new FileHelper((AppCompatActivity) context);
                                            fileHelper.viewFile(finalValue, true);
                                        }
                                    });
                                }
                            }
                        }
                        mLinearLayout.addView(tv);
                        tv = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

