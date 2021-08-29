package com.example.myapplication.user_interface.vlist;

import android.util.Log;

import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.user_interface.forms.model.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.example.myapplication.Constant.PAYLOAD_TEXT;

public class VlistRelationIdsHelper {

    private String vList;
    private String vlistRelationIds;
    private String vlistDefaultIds;
    private List<Field> vFieldsList;
    private int dlistArrayPosition;
    private List<Field> fieldsList;
    private FormRecylerAdapter adapter;

    public void setValueForRelationIds() {
        String[] idsArr = getIds(getVlistRelationIds());
        displayValuesOfRelationIds(idsArr);
        displayValuesOfDefaultIds();
    }

    private String[] getIds(String ids) {
        String[] arr = ids.split("/");
        return arr;
    }

    private void  displayValuesOfDefaultIds() {
        //vilstdefaultids

        try{
            if(!getVlistDefaultIds().isEmpty()){
                String[] idsArr = getVlistDefaultIds().split("@v@");
                if(getvFieldsList() != null){

                    for(int i=0; i< idsArr.length; i++){
                        for(int j=0; j<getvFieldsList().size(); j++){
                            Field fobj = getvFieldsList().get(j);
                            String[] vArr = idsArr[i].split("@i@");
                            String fId = "field"+vArr[0];
                            String value = vArr[1];

                            if(fobj.getId().equals(fId)){
                                fobj.setValue(value);
                                adapter.notifyAdapterWithPayLoad(j,PAYLOAD_TEXT);
                                break;
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void displayValuesOfRelationIds(String[] ids) {
        Log.e("IDS" ,ids.toString());
        try {
            List<String> valuesArr = getValuesFromMainForm();

            for(int i=0 ; i < ids.length; i++) {
                String relationId = "field"+ids[i];
                if(getvFieldsList()!= null){

                    for(int j=0;j<getvFieldsList().size(); j++ ) {
                        Field fObj = getvFieldsList().get(j);
                        if(fObj.getId().equals(relationId)) {
                            fObj.setValue(valuesArr.get(i));
                            getAdapter().notifyAdapterWithPayLoad(j,PAYLOAD_TEXT);
                            break;
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  List<String> getValuesFromMainForm() {
        String ids = getMainFormIdsFromVlist();
        String[] idsArr = ids.split("/");
        List<String> valuesArr = new ArrayList<>();

        for(int i=0; i < idsArr.length; i++) {
            String mainFieldId = "field"+idsArr[i];
            for(int j = 0; j< getFieldsList().size(); j++) {
                Field fObj = getFieldsList().get(j);
                if(fObj.getId().equals(mainFieldId)) {
                    valuesArr.add(fObj.getValue());
                    break;
                }
            }
        }
        return valuesArr;
    }

    private  String getMainFormIdsFromVlist() {
        String ids = "";
        List<String> arr = new ArrayList<>();
        try {
            if (!getvList().isEmpty()) {
                String regex = "&ids.*?\\&";

                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(getvList());
                if (matcher.find()) {
                    ids = matcher.group(0);
                }
                assert ids != null;
                ids = ids.replaceAll("[&ids=]", "");
                // Log.e("ids",ids);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    private String getvList() {
        return vList;
    }

    public void setvList(String vList) {
        this.vList = vList;
    }

    private String getVlistRelationIds() {
        return vlistRelationIds;
    }

    public void setVlistRelationIds(String vlistRelationIds) {
        this.vlistRelationIds = vlistRelationIds;
    }

    public int getDlistArrayPosition() {
        return dlistArrayPosition;
    }

    public void setDlistArrayPosition(int dlistArrayPosition) {
        this.dlistArrayPosition = dlistArrayPosition;
    }

    public List<Field> getvFieldsList() {
        return vFieldsList;
    }

    public void setvFieldsList(List<Field> vFieldsList) {
        this.vFieldsList = vFieldsList;
    }

    public List<Field> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<Field> fieldsList) {
        this.fieldsList = fieldsList;
    }

    public FormRecylerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FormRecylerAdapter adapter) {
        this.adapter = adapter;
    }

    public String getVlistDefaultIds() {
        return vlistDefaultIds;
    }

    public void setVlistDefaultIds(String vlistDefaultIds) {
        this.vlistDefaultIds = vlistDefaultIds;
    }

}
