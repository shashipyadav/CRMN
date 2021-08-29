package com.example.myapplication.presells;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class Buliding_Adapter_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public List<RootModel> buildingBottomStatusModels;

    public Buliding_Adapter_Adapter(Activity activity, List<RootModel> buildingBottomStatusModels) {
        this.activity = activity;
        this.buildingBottomStatusModels = buildingBottomStatusModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bulding_view_adater, parent, false);

        return new VContentInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((VContentInfoHolder) holder).tv_floor_name.setText("Floor  "+buildingBottomStatusModels.get(position).getFloor());

        Buliding_sub_Adapter projectAdapter = new Buliding_sub_Adapter(activity,buildingBottomStatusModels.get(position).getFloor(),buildingBottomStatusModels.get(position).getRooms());
        ((VContentInfoHolder) holder).rv_bulding_palan.setAdapter(projectAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ((VContentInfoHolder) holder).rv_bulding_palan.setLayoutManager(linearLayoutManager);


    }

    @Override
    public int getItemCount() {
        return buildingBottomStatusModels.size();
    }


    private class VContentInfoHolder extends RecyclerView.ViewHolder {

        private TextView tv_floor_name;
        private RecyclerView rv_bulding_palan;



        public VContentInfoHolder(View itemView) {
            super(itemView);

            tv_floor_name = (TextView) itemView.findViewById(R.id.tv_floor_name);
            rv_bulding_palan = (RecyclerView) itemView.findViewById(R.id.rv_bulding_palan);



        }
    }


}
