package com.example.myapplication.user_interface.dashboard.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.dashboard.model.Drilldown;

import java.util.List;

public class DrilldownAdapter extends  RecyclerView.Adapter<DrilldownAdapter.ViewHolder> {

    private Context mContext;
    private List<Drilldown> mDrilldownList;

    public DrilldownAdapter(Context context, List<Drilldown> drilldownList) {
        mContext = context;
        mDrilldownList = drilldownList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mContext).inflate(R.layout.item_drill_down, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Drilldown drilldown = mDrilldownList.get(position);

        holder.mTextViewLeadNo.setText(drilldown.getLeadNo());
        holder.mTextViewOrgName.setText(drilldown.getOrganisationName());
        holder.mTextViewProduct.setText(drilldown.getProduct());
        holder.mTextViewLeadEst.setText(drilldown.getLeadEstimate());
        holder.mTextViewLoc.setText(drilldown.getLocation());
        holder.mTextViewLeadBy.setText(drilldown.getLeadBy());
        holder.mTextViewResPerson.setText(drilldown.getResponsibleUser());


    }

    @Override
    public int getItemCount() {
        return mDrilldownList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewLeadNo, mTextViewOrgName, mTextViewProduct, mTextViewLeadEst, mTextViewLoc, mTextViewLeadBy, mTextViewResPerson;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewLeadNo = itemView.findViewById(R.id.lead_no);
            mTextViewOrgName = itemView.findViewById(R.id.org_name);
            mTextViewProduct = itemView.findViewById(R.id.product);
            mTextViewLeadEst = itemView.findViewById(R.id.lead_est);
            mTextViewLoc = itemView.findViewById(R.id.location);
            mTextViewLeadBy = itemView.findViewById(R.id.lead_by);
            mTextViewResPerson = itemView.findViewById(R.id.resp_person);


        }
    }
}
