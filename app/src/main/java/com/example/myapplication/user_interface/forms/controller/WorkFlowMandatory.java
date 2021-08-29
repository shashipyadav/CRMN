package com.example.myapplication.user_interface.forms.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.forms.model.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class WorkFlowMandatory {

    Context context;
    FormRecylerAdapter adapter;
    List<Field> fieldList;

    public WorkFlowMandatory(Context mContext,
                             List<Field> mFieldList,
                             FormRecylerAdapter mAdapter){
        this.context = mContext;
        this.fieldList = mFieldList;
        this.adapter = mAdapter;
    }

    public void showMandatoryFields( String mandatory, int dlistArrayPosition){
        try{

            List<String> mandatoryFields = getMandatoryFields(mandatory);
            showFields(mandatoryFields);
         //   showFieldsInDlist(dlistArrayPosition);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private  List<String> getMandatoryFields(String mandatory){
        String delimiter = "/";
        List<String> mandatoryFields = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(mandatory,delimiter);

        while (st.hasMoreTokens()) {
            mandatoryFields.add(st.nextToken());
        }
        return mandatoryFields;
    }

    private void showFields(List<String> mandatoryFields){

        for(int i =0; i<mandatoryFields.size(); i++) {
            String mandatoryField = "field"+mandatoryFields.get(i);

            for(int j=0; j < fieldList.size(); j++) {
                Field fObj = fieldList.get(j);
                if(mandatoryField.contains("_")){
                    //if there is '_' in the given mandatory field then it means its a dlist field

                }else{
                    if(fObj.getId().equals(mandatoryField)){
                        if( fObj.getType().equals("hidden")){
                            fObj.setType(fObj.getFieldType());
                            //to make it required
                            fObj.setSaveRequired("true");
                            notifyAdapterWithPayLoad(j,Constant.PAYLOAD_HIDE_SHOW);
                            notifyAdapterWithPayLoad(j, Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                        }else{
                            fObj.setSaveRequired("true");
                            notifyAdapterWithPayLoad(j, Constant.PAYLOAD_REQUIRED_NOTREQUIRED);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void showFieldsInDlist(int dlistArrayPosition, String mandatory){

        String[] arr = mandatory.split("_");
        String dlistField = "field" +arr[0];

        if(dlistArrayPosition != -1){
            Field fieldDlist = fieldList.get(dlistArrayPosition);
            List<Field> dlistFieldArray = fieldDlist.getdListArray();
            for(int i=0; i < dlistFieldArray.size(); i++){
                Field dlistF = dlistFieldArray.get(i);
                if(dlistF.getId().equals(dlistField)){

                    //here we get the fields related to a certain dlist form
                }
            }
        }


    }

    private void notifyAdapter(final int position) {
        if(context!= null){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            if(adapter != null){
                                adapter.notifyItemChanged(position);
                            }
                        }
                    };
                    handler.post(r);
                }
            });
        }
    }


    private void notifyAdapterWithPayLoad(final int position, final String payload) {
        if(context!= null){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            if(adapter != null){
                                adapter.notifyItemChanged(position,payload);
                            }
                        }
                    };
                    handler.post(r);
                }
            });
        }
    }

}
