package com.example.myapplication.user_interface.dashboard.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.dashboard.model.Dashboard;
import com.example.myapplication.Constant;
import com.google.gson.Gson;

import java.util.List;

public class ChartTitleAdapter extends RecyclerView.Adapter<ChartTitleAdapter.ViewHolder> {

    private Context mContext;
    private List<Dashboard> mChartList;

    public ChartTitleAdapter(Context context, List<Dashboard> chartList) {
        mContext = context;
        mChartList = chartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chart_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Dashboard dashObj = mChartList.get(position);
        String title = dashObj.getTitle();

        try{
            if(!title.isEmpty()){
               title = title.split("-->")[1];
            }

            holder.txtTitle.setText(title);
        }catch (Exception e){
            e.printStackTrace();
        }

        final String finalTitle = title;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChartActivity.class);
                intent.putExtra(Constant.EXTRA_OBJECT,new Gson().toJson(dashObj));
//                intent.putExtra(Constant.EXTRA_CHART_ID, dashObj.getChartId());
                intent.putExtra(Constant.EXTRA_TITLE, finalTitle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title);
        }
    }
}
