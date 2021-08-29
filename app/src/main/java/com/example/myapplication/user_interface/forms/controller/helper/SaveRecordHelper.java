package com.example.myapplication.user_interface.forms.controller.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dynamicbutton.DynamicButtonHelper;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.controller.ResponseInterface;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.example.myapplication.util.ExtensionUtil;
import com.example.myapplication.util.MultipartUtility;
import com.example.myapplication.util.ToastUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveRecordHelper {
    private static final String DEBUG_TAG = SaveRecordHelper.class.getSimpleName();
    private Activity activitiy;
    private ProgressDialog mWaiting;
    private String formSave;
    private String recordId;
    private String formId;
    private SharedPrefManager prefManager;
    private ResponseInterface listener;
    private boolean mChangeState;
    private String nextState;
    private boolean editRecord = false;
    private boolean isCallFunction = true;
    private String buttonTitle;
    private String formTitle;
    private DatabaseManager dbManager;
    private boolean isVlist;
    private List<Field> additionalFieldDataList = null;
    private List<Field> fieldsList = null;
    private int dlistArrayPosition = -1;

    public SaveRecordHelper(Activity activity,ResponseInterface listener){
        this.activitiy = activity;
        this.listener = listener;
        prefManager = new SharedPrefManager(activity);
        dbManager = new DatabaseManager(activity);
        dbManager.open();
    }


    //  needs formId and formSave for the api to be called successfully
    public void callSaveFormRecordAPI() {
        showDialog();
        String formSave = getFormSave();
        String[] jarr = formSave.split("saveeditpart");
        String[] arr = jarr[1].replaceAll("['\\(\\)]", "").split(",");
        String formid = arr[0];
        setFormId(formid);
        String editids = arr[1];
        final String msg = arr[2];

        String URL = prefManager.getClientServerUrl() + "/SaveFormField.do?actn=SaveData&mobileform=yes&" +
                "id=" + formid + "&editids=" + editids + "&" +
                "valuestring=null&globalvar=0&cloudcode=" + prefManager.getCloudCode() + "&token=" + prefManager.getAuthToken();

        Log.e("CALLSAVEDLIST API url ", URL);
        final String charset = "UTF-8";

        try {
            MultipartUtility multipart = new MultipartUtility(URL, charset);
//            List<Field> additionalFieldDataList = null;
//            List<Field> fieldsList = null;
//            int dlistArrayPosition = -1;
//            if(isVlist){
//                additionalFieldDataList = VlistFormActivity.vAdditionalFieldDataList;
//                fieldsList = VlistFormActivity.vFieldsList;
//                dlistArrayPosition = VlistFormActivity.vDlistArrayPosition;
//            }else {
//                additionalFieldDataList =  FormFragment.additionalFieldDataList;
//                fieldsList = FormFragment.fieldsList;
//                dlistArrayPosition = FormFragment.dlistArrayPosition;
//            }



            //additional info like mandatory and such
            for (int j = 0; j < getAdditionalFieldDataList().size(); j++) {
                Field fieldobj = getAdditionalFieldDataList().get(j);
                if (!fieldobj.getId().matches("loadonchangeids|menudivresults|menudivform|dlistspanids|checkhideshowids|stateids|formsavechecknames|formsavecheck")) {
                    if (fieldobj.getId().toLowerCase().equals("mandatorynames")) {
                        fieldobj.setValue("");
                    }

                    if(isEditRecord()){
                        if((fieldobj.getId().toLowerCase().equals("idselected")) ||
                                fieldobj.getId().toLowerCase().equals("idhidden") ||
                                fieldobj.getId().toLowerCase().equals("fieldid")){

                            if(isEditRecord()){
                                fieldobj.setValue(getRecordId());
                            }
                        }
                    }
                    multipart.addFormField(fieldobj.getId(), fieldobj.getValue());
                    Log.e("", fieldobj.getId() + ":" + fieldobj.getValue());
                }
            }
            multipart.addFormField("showformid", "");
            multipart.addFormField("api", "REST");
            Log.e("", "showformid" + ":" + "");

            //main form fields
            for (int i = 0; i < getFieldsList().size(); i++) {
                Field fieldobj = getFieldsList().get(i);
                //we dont want to send the dlist item field id in the main form
                if (fieldobj.getdListArray().isEmpty()) {
                    if(fieldobj.getDataType().equalsIgnoreCase("file")){
                        if(!fieldobj.getValue().equals("")  || !fieldobj.getValue().equals("0")){
                            String ext = ExtensionUtil.getExtension(fieldobj.getValue());
                            if (!ext.isEmpty()){
                                File file = new File(fieldobj.getValue());

                                if(file.exists()) {
                                    multipart.addFilePart(fieldobj.getId(),file);
                                }
                            }
                        }
                    }else if (fieldobj.getDataType().toLowerCase().matches("checkbox|boolean")){

                        String value = fieldobj.getValue().equals("true") ? "on" : "";
                        multipart.addFormField(fieldobj.getId(), value);
                        Log.e("", fieldobj.getId() + ":" + value);

                    }else{
                        multipart.addFormField(fieldobj.getId(), fieldobj.getValue());
                        Log.e("", fieldobj.getId() + ":" + fieldobj.getValue());
                    }
                }
            }

            if (dlistArrayPosition != -1) {
                //Dlist
                Field fobj = getFieldsList().get(dlistArrayPosition);
                if (!fobj.getdListArray().isEmpty()) {
                    List<Field> dlistArray = fobj.getdListArray();
                    for (int j = 0; j < dlistArray.size(); j++) { // will loop through the dlistbuttonArray

                        Field dlist = dlistArray.get(j); // sindle dlist Item
                        String DLIST_FIELD_ID = dlist.getId();
                        String dlistfieldid = dlist.getId().replace("field", "fieldid");
                        List<DList> dlistFields = dlist.getdListsFields();
                        String contentRows = "";
                        String noOfRows = "";

                        //loop through dlist titles - zeroth row
                        for (int k = 0; k < dlistFields.size(); k++) {
                            DList dobj = dlistFields.get(k);

                            if (k == 0) {
                                multipart.addFormField(dlistfieldid + "_0", "0");
                                Log.e("", dlistfieldid + "_0" + ":" + "0");
                            }

                            if (dobj.getId().contains("contentrows")) {
                                contentRows = dobj.getId();
                            }
                            if (dobj.getId().contains("noofrows")) {
                                noOfRows = dobj.getId();
                            }
                            if (!dobj.getId().matches("(contentrows([0-9]{5,}))|(noofrows([0-9]{5,}))|searchids36906|stateids36906|scrollwidth36906|islonglist36906")) {

                                multipart.addFormField(dobj.getId(), dobj.getValue());
                                Log.e("", dobj.getId() + ":" + dobj.getValue());
                            }
                        }

                        //PREVIOUS DLIST CODE APRIL 13 2O21loop through dlistfieldvalues
                      //  List<DListItem> dlistFieldValues = dlist.getDListItemList(); // get the dlistFieldValues Array


                        List<String> dlistRowsJson =  dbManager.fetchDlistJson(DLIST_FIELD_ID);
//--------------------------------------------------------------------------------------------------------------------------
                        String listids = ",";
                        for(int x =0; x< dlistRowsJson.size(); x++){
                            int index = x + 1;
                            try{
                                JSONObject jsonObject = new JSONObject(dlistRowsJson.get(x));
                                JSONArray jsonArray = jsonObject.getJSONArray("dlistArray");
                                for(int y =0; y<jsonArray.length(); y++){
                                    JSONObject jObj = jsonArray.getJSONObject(y);
                                    String regex = "(contentrows([0-9]{5,})(_[0-9]))|(deletedrows([0-9]{5,})(_[0-9]))|(noofrows([0-9]{5,})(_[0-9]))|(searchids([0-9]{5,})(_[0-9]))|(stateids([0-9]{5,})(_[0-9]))|(scrollwidth([0-9]{5,})(_[0-9]))|(islonglist([0-9]{5,})(_[0-9]))";
                                    String regex1 = "(contentrows([0-9]{5,}))|(deletedrows([0-9]{5,}))|(noofrows([0-9]{5,}))|(searchids([0-9]{5,}))|(stateids([0-9]{5,}))|(scrollwidth([0-9]{5,}))|(islonglist([0-9]{5,}))";

                                    if (!jObj.getString("mId").matches(regex)) {
                                        if (!jObj.getString("mId").matches(regex1)) {
                                            if(jObj.getString("mFieldType").equalsIgnoreCase("file")){
                                                if(!jObj.getString("mValue").equals("")){
                                                    if (!jObj.getString("mValue").equals("0")){
                                                        File file = new File(jObj.getString("mValue"));
                                                        if(file.exists()){
                                                            multipart.addFilePart(jObj.getString("mId"),file);
                                                        }
                                                    }
                                                }
                                            }else if (jObj.getString("mFieldType").toLowerCase().matches("checkbox|boolean")) {
                                                String value = jObj.getString("mValue").equals("true") ? "on" : "off";
                                                multipart.addFormField(jObj.getString("mId"),value);
                                                Log.e("", jObj.getString("mId") + ":" + value);
                                            }else{
                                                multipart.addFormField(jObj.getString("mId"), jObj.getString("mValue"));
                                                Log.e("", jObj.getString("mId") + ":" + jObj.getString("mValue"));
                                            }
                                        }
                                    }

                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            listids += "" + index + ","; // contentrows
                        }

                        multipart.addFormField(contentRows, listids);
                        multipart.addFormField(noOfRows, String.valueOf(dlistRowsJson.size()));
                        Log.e("", contentRows + ":" + listids);
                        Log.e("", noOfRows + ":" + String.valueOf(dlistRowsJson.size()));
                    }
                }
            }
            multipart.addFormField("rows", "50");
            Log.e("", "rows" + ":" + "50");

            List<String> response = multipart.finish();
            Log.e("SERVER REPLIED: ", Arrays.asList(response).toString());
            final String result = response.toString();

            Log.e("RESPONSE", "saveEditPart" + result);
            if(isVlist){
                VlistFormActivity.VRECORD_ID_REF = "0";
            }else{
                FormFragment.RECORD_ID_REF = "0";
            }

            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(result);
            if (m.find()) {
                System.out.println(m.group(0));
                setRecordId(m.group(0));
            }

            activitiy.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToastMessage(msg, activitiy);
                    dismissDialog();

//                    if(dbManager != null){
//                        dbManager.deleteDList();
//                    }


                    if(changeState()){
                        DynamicButtonHelper dynamicButtonHelper = new DynamicButtonHelper(activitiy, listener);
                        dynamicButtonHelper.setFormId(getFormId());
                        dynamicButtonHelper.setRecordId(getRecordId());
                        dynamicButtonHelper.setButtonTitle(getButtonTitle());
                        dynamicButtonHelper.setInputInTagField(false);
                        dynamicButtonHelper.setFormTitle(formTitle);
                        dynamicButtonHelper.callActionAPI(getNextState());
                    }else{
                        if(listener != null){
                            listener.onSuccessResponse(getRecordId(),false);
                            //previous
                           // listener.onChangeState(getRecordId());
                        }
                    }
                }
            });

//            if(!changeState()){
//                if(listener != null){
//                    listener.onSuccessResponse(getRecordId(),changeState(),false);
//                }
//            }
        } catch (IOException ex) {
            System.err.println(ex);
            dismissDialog();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showDialog(){
        activitiy.runOnUiThread(new Runnable() {
            public void run() {
                mWaiting = ProgressDialog.show(activitiy, "",
                        "Saving Details ...", false);
            }
        });
    }

    private void dismissDialog() {
        activitiy.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWaiting != null) {
                    mWaiting.dismiss();
                }
            }
        });
    }

    public void setFormId(String formId){
        this.formId = formId;
    }

    private String getFormId(){
        return formId;
    }

    public void setFormSave(String formSave){
        this.formSave = formSave;
    }

    private String getFormSave(){
        return formSave;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
        Log.e(DEBUG_TAG,"saved Record Id = " + this.recordId);
    }

    public String getRecordId(){
        if(recordId == null){
            return "0";
        }else{
            return recordId;
        }
    }

    public boolean changeState() {
        return mChangeState;
    }

    public void setChangeState(boolean changeState) {
        this.mChangeState = changeState;
    }

    public String getNextState() {
        return nextState;
    }

    public void setNextState(String nextState) {
        this.nextState = nextState;
    }

    public boolean isEditRecord() {
        return editRecord;
    }

    public void setEditRecord(boolean editRecord) {
        this.editRecord = editRecord;
    }

    public void setButtonTitle(String value){
        buttonTitle = value;
    }

    private String getButtonTitle(){
        return buttonTitle;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public void setFlag(boolean callFunction){
        isCallFunction = callFunction;
    }
    private boolean getFlag(){
        return isCallFunction;
    }

    public boolean isVlist() {
        return isVlist;
    }

    public void setVlist(boolean vlist) {
        isVlist = vlist;
    }

    public List<Field> getAdditionalFieldDataList() {
        return additionalFieldDataList;
    }

    public void setAdditionalFieldDataList(List<Field> additionalFieldDataList) {
        this.additionalFieldDataList = additionalFieldDataList;
    }

    public List<Field> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<Field> fieldsList) {
        this.fieldsList = fieldsList;
    }

    public int getDlistArrayPosition() {
        return dlistArrayPosition;
    }

    public void setDlistArrayPosition(int dlistArrayPosition) {
        this.dlistArrayPosition = dlistArrayPosition;
    }
}
