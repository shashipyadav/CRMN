package com.example.myapplication.projectdetails;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;

import java.util.List;

public class MemberLedgerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public List<MemberLedgerModel> serverPojoList;

    public MemberLedgerAdapter(Activity activity, List<MemberLedgerModel> serverPojoList) {
        this.activity = activity;
        this.serverPojoList = serverPojoList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_ledger, parent, false);
        return new VContentInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((VContentInfoHolder) holder).tv_v_no.setText(serverPojoList.get(position).getVOUCHER_NO());

        
        ((VContentInfoHolder) holder).tv_date.setText(serverPojoList.get(position).getDATE());
        ((VContentInfoHolder) holder).tv_RECEIPT_NO.setText(serverPojoList.get(position).getRECEIPT_NO());
        ((VContentInfoHolder) holder).tv_MEMBER_CODE.setText(serverPojoList.get(position).getMEMBER_CODE());
        ((VContentInfoHolder) holder).tv_UNIT_CODE.setText(serverPojoList.get(position).getUNIT_CODE());
        ((VContentInfoHolder) holder).tv_MEMBER_NAME.setText(serverPojoList.get(position).getMEMBER_NAME());
        ((VContentInfoHolder) holder).tv_VOUCHER_TYPE.setText(serverPojoList.get(position).getVOUCHER_TYPE());
        ((VContentInfoHolder) holder).tv_PARTICULARS.setText(serverPojoList.get(position).getPARTICULARS());
        ((VContentInfoHolder) holder).tv_BANK_NAME.setText(serverPojoList.get(position).getBANK_NAME());
        ((VContentInfoHolder) holder).tv_CHEQUE_NO.setText(serverPojoList.get(position).getCHEQUE_NO());
        ((VContentInfoHolder) holder).tv_CHEQUE_DT.setText(serverPojoList.get(position).getCHEQUE_DT());
        ((VContentInfoHolder) holder).tv_DUE_AMT.setText(serverPojoList.get(position).getDUE_AMT());
        ((VContentInfoHolder) holder).tv_RECEIVED_AMT.setText(serverPojoList.get(position).getRECEIVED_AMT());




    }

    @Override
    public int getItemCount() {
        return serverPojoList.size();
    }


    private class VContentInfoHolder extends RecyclerView.ViewHolder {

        private TextView tv_v_no;
        private TextView tv_date;
        private TextView tv_RECEIPT_NO;
        private TextView tv_MEMBER_CODE;
        private TextView tv_UNIT_CODE;
        private TextView tv_MEMBER_NAME;
        private TextView tv_VOUCHER_TYPE;
        private TextView tv_PARTICULARS;
        private TextView tv_BANK_NAME;
        private TextView tv_CHEQUE_NO;
        private TextView tv_CHEQUE_DT;
        private TextView tv_DUE_AMT;
        private TextView tv_RECEIVED_AMT;



        public VContentInfoHolder(View itemView) {
            super(itemView);

            tv_v_no = (TextView) itemView.findViewById(R.id.tv_v_no);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_RECEIPT_NO = (TextView) itemView.findViewById(R.id.tv_RECEIPT_NO);
            tv_MEMBER_CODE = (TextView) itemView.findViewById(R.id.tv_MEMBER_CODE);
            tv_UNIT_CODE = (TextView) itemView.findViewById(R.id.tv_UNIT_CODE);
            tv_MEMBER_NAME = (TextView) itemView.findViewById(R.id.tv_MEMBER_NAME);
            tv_VOUCHER_TYPE = (TextView) itemView.findViewById(R.id.tv_VOUCHER_TYPE);
            tv_PARTICULARS = (TextView) itemView.findViewById(R.id.tv_PARTICULARS);
            tv_BANK_NAME = (TextView) itemView.findViewById(R.id.tv_BANK_NAME);
            tv_CHEQUE_NO = (TextView) itemView.findViewById(R.id.tv_CHEQUE_NO);
            tv_CHEQUE_DT = (TextView) itemView.findViewById(R.id.tv_CHEQUE_DT);
            tv_DUE_AMT = (TextView) itemView.findViewById(R.id.tv_DUE_AMT);
            tv_RECEIVED_AMT = (TextView) itemView.findViewById(R.id.tv_RECEIVED_AMT);


           // Typeface custom_font1 = Typeface.createFromAsset(activity.getAssets(), "Titillium-Regular.otf");
           // ((TextView) itemView.findViewById(R.id.tv_client_name)).setTypeface(custom_font1);

         }
    }


}
