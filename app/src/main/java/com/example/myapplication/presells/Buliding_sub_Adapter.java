package com.example.myapplication.presells;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class Buliding_sub_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public List<RoomModel> buildingBottomStatusModels;
    private String floor;

    public Buliding_sub_Adapter(Activity activity, String floor, List<RoomModel> buildingBottomStatusModels) {
        this.activity = activity;
        this.buildingBottomStatusModels = buildingBottomStatusModels;
        this.floor = floor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_adapter, parent, false);

        return new VContentInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((VContentInfoHolder) holder).tv_floor_wing.setText(buildingBottomStatusModels.get(position).getAa());
        ((VContentInfoHolder) holder).tv_floor_number.setText(buildingBottomStatusModels.get(position).getUnitNo111());
        ((VContentInfoHolder) holder).tv_lef.setBackgroundColor(Color.parseColor(buildingBottomStatusModels.get(position).getColour()));
        ((VContentInfoHolder) holder).tv_reft.setBackgroundColor(Color.parseColor(buildingBottomStatusModels.get(position).getColour()));

        ((VContentInfoHolder) holder).ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendailog(buildingBottomStatusModels.get(position));
            }
        });





    }

    private void opendailog(RoomModel roomModel) {

        AlertDialog.Builder builder
                = new AlertDialog.Builder(activity);
        builder.setTitle("Unit Info");

        // set the custom layout
        final View customLayout
                = activity
                .getLayoutInflater()
                .inflate(
                        R.layout.dailog_builing_dailog,
                        null);
        builder.setView(customLayout);

        TextView tv_floor_number = customLayout.findViewById(R.id.tv_floor_number);
        TextView tv_Unit_No = customLayout.findViewById(R.id.tv_Unit_No);
        TextView tv_Unit_Type = customLayout.findViewById(R.id.tv_Unit_Type);
        TextView tv_Carpet_Area = customLayout.findViewById(R.id.tv_Carpet_Area);
        TextView tv_Direction = customLayout.findViewById(R.id.tv_Direction);
        TextView tv_Status = customLayout.findViewById(R.id.tv_Status);
        TextView tv_Sold_To = customLayout.findViewById(R.id.tv_Sold_To);
        TextView tv_project_Name_wing = customLayout.findViewById(R.id.tv_project_Name_wing);

        tv_floor_number.setText(floor);
        tv_Unit_No.setText(roomModel.getUnitNo111());
        tv_Unit_Type.setText(roomModel.getUnitType());
        tv_Carpet_Area.setText(roomModel.getCarpet().toString());
        tv_Direction.setText(roomModel.getDirection());
        tv_Status.setText(roomModel.getStatus());
        tv_Sold_To.setText(roomModel.getMemName());
        tv_project_Name_wing.setText(roomModel.getProject() +" "+ roomModel.getAa());
        // add a button
        builder
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which)
                            {
                                dialog.dismiss();
                                // send data from the
                                // AlertDialog to the Activity

                            }
                        });

        // create and show
        // the alert dialog
        AlertDialog dialog
                = builder.create();
        dialog.show();

    }

    @Override
    public int getItemCount() {
        return buildingBottomStatusModels.size();
    }


    private class VContentInfoHolder extends RecyclerView.ViewHolder {

        private TextView tv_floor_wing,tv_floor_number,tv_reft,tv_lef;
        private RecyclerView rv_bulding_palan;
        private LinearLayout ll_main;



        public VContentInfoHolder(View itemView) {
            super(itemView);

            tv_floor_wing = (TextView) itemView.findViewById(R.id.tv_floor_wing);
            tv_floor_number = (TextView) itemView.findViewById(R.id.tv_floor_number);
            tv_reft = (TextView) itemView.findViewById(R.id.tv_reft);
            tv_lef = (TextView) itemView.findViewById(R.id.tv_lef);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);



        }
    }


}
