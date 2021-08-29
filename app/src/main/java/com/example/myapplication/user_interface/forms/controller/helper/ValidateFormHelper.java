package com.example.myapplication.user_interface.forms.controller.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateFormHelper {

    private final String DEBUG_TAG = ValidateFormHelper.class.getSimpleName();
    private Context context;
    private boolean isVlist;
    private DatabaseManager dbManager;
    private List<Field> additionalFieldDataList = null;
    private List<Field> fieldsList = null;
    private int dlistArrayPosition = -1;

    public ValidateFormHelper(Context context, boolean isVlist){
        this.context = context;
        this.isVlist = isVlist;
        initDatabase();
    }

    private void initDatabase() {
        dbManager = new DatabaseManager(context);
        dbManager.open();
    }

    public boolean areSaveRequiredFieldsValidated(){
//        List<Field> fieldsList = null;
//        if(isVlist){
//            fieldsList = VlistFormActivity.vFieldsList;
//        }else{
//            fieldsList = FormFragment.fieldsList;
//        }


        boolean isValidated = true;
                for (int k = 0; k < getFieldsList().size(); k++) {
                    Field kobj = getFieldsList().get(k);

                        if (kobj.getdListArray().isEmpty()) {
                            //main Form
                            if (kobj.getSaveRequired().toLowerCase().equals("true")) {

                            if ((kobj.getValue().equals("0")) || (kobj.getValue().isEmpty())) {
                                if (!kobj.getType().matches("hidden")) {
                                    // ToastUtil.showToastMessage(kobj.getField_name() + "Can't be empty", getActivity());
                                    // isFieldHidden = true;
                                    // } else {
//                                    selectedItem = k;
                                    Log.e(DEBUG_TAG,"SaveRequired , Field Name = " + kobj.getFieldName());
                                 //   kobj.setShowErrorMessage(true);
                                    showErrorDialog(kobj.getFieldName());
                                 //   final int finalK = k;


//                                    if (context != null) {
//                                        ((Activity)context).runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                FormFragment.adapter.notifyItemChanged(finalK);
//                                            }
//                                        });
//                                    }
                                    isValidated = false;
                                    break;
                                }
                            } else {
                               // kobj.setShowErrorMessage(false);
                               // final int finalK1 = k;
//                                if (context != null) {
//                                    ((Activity)context).runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            FormFragment.adapter.notifyItemChanged(finalK1);
//                                        }
//                                    });
//                                }
                            }
                        } else {
                            //dlist Found
                            //ToDo =search for save required in DList pending
//                            dlistArray = kobj.getdListArray();
//                            for (int m = 0; m < dlistArray.size(); m++) { // will loop through the dlistbuttonArray
//                                Field dlist = dlistArray.get(m);
//                                if (fieldId.matches(dlist.getId())) {
//                                    //Display a toast saying this dlist can't be empty
//                                    // For.eg Configuration Parameter is Empty
//                                    ToastUtil.showToastMessage(dlist.getField_name() + "Can't be empty", getActivity());
//                                    selectedItem = m;
//                                    isFieldHidden = false;
//                                    break;
//                                }
//                            }
                        }
                    }
                }
                return isValidated;
            }

    public void showErrorDialog(String fieldName){
        DialogUtil.showAlertDialog((Activity) context, "",   fieldName+" can't be empty.", false, false);
    }

    public String getMandatoryName(int marr){


        String mandatoryNames = "";
        String mandatoryFieldName = "";

//        List<Field> additionalFieldDataList = null;
//        if(isVlist){
//            additionalFieldDataList = VlistFormActivity.vAdditionalFieldDataList;
//        }else{
//            additionalFieldDataList = FormFragment.additionalFieldDataList;
//        }


        if (getAdditionalFieldDataList() != null) {

            for(int i= 0 ; i < getAdditionalFieldDataList().size(); i++){
                Field fobj = getAdditionalFieldDataList().get(i);

                if(fobj.getId().equalsIgnoreCase("mandatorynames")) {
                    mandatoryNames = fobj.getValue();
                }
            }
        }

        String[] arr = mandatoryNames.split("@@");
        if(arr != null){
            for(int j=0; j < arr.length; j++){
                if(marr == j){
                    mandatoryFieldName = arr[j];
                }
            }
        }

        return mandatoryFieldName;
    }

    public boolean checkMandatory(String compulsory) {
  //      List<Field> fieldsList = null;

//        if(isVlist){
//            fieldsList = VlistFormActivity.vFieldsList;
//        }else{
//            fieldsList = FormFragment.fieldsList;
//        }

        //  showValidationProgress();

        final String[] result = {""};
        String mandatory = "";
        if(!compulsory.isEmpty()){
            mandatory = compulsory;
        }  else{
            mandatory = getMandatory();
        }

        //this condition is to just show the alert we get sometimes from dynamic buttons.
        //no further action should be taken
        if(mandatory.contains("alert")){
            showAlert(mandatory);
            return false;
        }

        int selectedItem = -1;
        boolean isFieldHidden = false;
        String[] arr = mandatory.split("/");

        List<Field> dlistArray = new ArrayList<>();
        first:
        for (int j = 0; j < arr.length; j++) {
            String fieldId = "field" + arr[j];
            //main Form validation
            if (!fieldId.contains("_")) {
                for (int k = 0; k < getFieldsList().size(); k++) {
                    Field kobj = getFieldsList().get(k);
                    if (fieldId.matches(kobj.getId())) {

                        if (kobj.getdListArray().isEmpty()) {
                            if(kobj.getDataType().toLowerCase().equals("file")){
                                String ext = getExtension(kobj.getValue());
                                //Checks if there is any extension after the last . in your input
                                if (ext.isEmpty()) {
                                    selectedItem = k;
                                    // dismissValidationProgress();
                                    showErrorDialog(kobj.getFieldName());
                                    isFieldHidden = false;
                                }
                            }else{
                                if (kobj.getValue().isEmpty()) {
                                    if (!kobj.getType().matches("hidden")) {
                                        // ToastUtil.showToastMessage(kobj.getField_name() + "Can't be empty", getActivity());
                                        // isFieldHidden = true;
                                        // } else {
                                        selectedItem = k;
                                        getFieldsList().get(k).setShowErrorMessage(true);
                                        //  dismissValidationProgress();
                                        showErrorDialog(kobj.getFieldName());
                                        final int finalK = k;

//                                        if (getActivity() != null) {
//                                            getActivity().runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    adapter.notifyItemChanged(finalK);
//                                                }
//                                            });
//                                        }
                                        isFieldHidden = false;
                                    }
                                } else {
                                    getFieldsList().get(k).setShowErrorMessage(false);
                                    final int finalK1 = k;
//                                    if (getActivity() != null) {
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                adapter.notifyItemChanged(finalK1);
//                                            }
//                                        });
//                                    }
                                }
                            }
                            break ;
                        } else {
                            //dlist Found
                            //ToDo = search for save required in DList pending
                            dlistArray = kobj.getdListArray();
                            for (int m = 0; m < dlistArray.size(); m++) { // will loop through the dlistbuttonArray
                                Field dlist = dlistArray.get(m);
                                if (fieldId.matches(dlist.getId())) {
                                    //Display a toast saying this dlist can't be empty
                                    // For.eg Configuration Parameter is Empty
                                    ToastUtil.showToastMessage(dlist.getField_name() + "Can't be empty", context);
                                    selectedItem = m;
                                    isFieldHidden = true;
                                    break;
                                }
                            }
                            Log.e("CHECKMANADATORY", "dlistEmpty");
                        }
                    }
                }
                if (selectedItem != -1) {
                    break;
                }
            } else {
                boolean r = validateDlist( arr[j],j);
                if(!r) {
                    result[0] = "false";
                    break ;
                }
            }
        }

        if (!isFieldHidden) {
            if (selectedItem != -1) {
                Log.e(DEBUG_TAG, "------ #line 715");
                Log.e("isValidated", "Position = " + selectedItem + " is Emtpy ------");
                //   scrollToFieldPostion(selectedItem);
                result[0] = "false";
            }
        }

        if (result[0].equals("false")) {
            Log.e("isValidated", String.valueOf(false));
            return false;
        }
        Log.e("isValidated", String.valueOf(true));
        return true;
    }

    private String getMandatory() {
//
//        List<Field> additionalFieldDataList = null;
//        if(isVlist){
//            additionalFieldDataList = VlistFormActivity.vAdditionalFieldDataList;
//        }else{
//            additionalFieldDataList = FormFragment.additionalFieldDataList;
//        }

        String mandatory = "";
        for (int i = 0; i < getAdditionalFieldDataList().size(); i++) {
            Field fObj = getAdditionalFieldDataList().get(i);
            if (fObj.getName().toLowerCase().matches("mandatory")) {
                mandatory = fObj.getValue();
                break;
            }
        }
        return mandatory;
    }

    private void showAlert(String mandatory){
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matchPattern = pattern.matcher(mandatory);

        while(matchPattern.find()) {
            mandatory = matchPattern.group(1);
        }
        DialogUtil.showAlertDialog((Activity) context, "",
                mandatory,
                false,
                false);
    }

    static String getExtension(String str){
        int begin = str.lastIndexOf(".");
        if(begin == -1)
            return "";
        String ext = str.substring(begin + 1);
        return ext;
    }

    public boolean validateDlist(String arr,int positionOfMandatoryField){
        boolean isValidated = true;
        //dlistfields validation
        String[] jaar = arr.split("_");
        String dlistID = jaar[0];
        String dlistField = jaar[1];
        //validate dlist form fields
        isValidated = validateDlistFormFields(dlistID,dlistField,positionOfMandatoryField);
        return isValidated;
    }

    public boolean validateDlistFormFields(String dlistField,
                                           String dlistChildField,
                                           int positionOfMandatoryField) {
        boolean isValidated = true;
        String[] arr4 = new String[0];
        boolean isContentRowFound = false;

        List<Field> dlistArray = getFieldsList().get(getDlistArrayPosition()).getdListArray();

//        if(isVlist) {
//            dlistArray = VlistFormActivity.vFieldsList.get(VlistFormActivity.vDlistArrayPosition).getdListArray();
//        }else{
//            dlistArray = FormFragment.fieldsList.get(FormFragment.dlistArrayPosition).getdListArray();
//        }

        //get the value in content rows for a partcular dlist
        for (int j = 0; j < dlistArray.size(); j++) {
            Field dlist = dlistArray.get(j); // sindle dlist Item
            if(dlist.getId().equals("field"+dlistField)){
                List<DList> dlistFields = dlist.getdListsFields();
                //loop through dlist title - zeroth row
                for (int k = 0; k < dlistFields.size(); k++) {
                    DList dobj = dlistFields.get(k);
                    if (dobj.getId().equals("contentrows" + dlistField.replace("field",""))) {
                        //    Log.e(DEBUG_TAG,"contentrows" + dlistField.replace("field","") + "value = " + dobj.getValue());
                        arr4 = dobj.getValue().split(",");
                        isContentRowFound= true;
                        break;
                    }
                }//end of for loop
            }
        }//end of for loop

        if(!isContentRowFound){
            return true;
        }

        //loop through the content array
        for (int l = 0; l < arr4.length; l++) {
            if (!arr4[l].equals("")) {
                isValidated =  checkIfDlistFormFieldEmpty(dlistField,dlistChildField,arr4[l],positionOfMandatoryField);
                if(!isValidated){
                    break;
                }
            }
        }
        return isValidated;
    }


    public boolean checkIfDlistFormFieldEmpty(String fieldId,
                                              String dlistFieldId,
                                              String extension,
                                              int positionOfMandatoryField) {

        boolean isValidated = true;

        List<Field> dlistButtonArray =  getFieldsList().get(getDlistArrayPosition()).getdListArray();

//        if(isVlist){
//            dlistButtonArray =  VlistFormActivity.vFieldsList.get(VlistFormActivity.vDlistArrayPosition).getdListArray();
//        }else{
//            dlistButtonArray =  FormFragment.fieldsList.get(FormFragment.dlistArrayPosition).getdListArray();
//        }



        first:
        for (int m = 0; m <dlistButtonArray.size(); m++) {
            Field dlist = dlistButtonArray.get(m);
            if(dlist.getId().equals("field"+fieldId)){

                List<String> dlistRowsJson =  dbManager.fetchDlistJson("field"+fieldId);
                for(int x=0; x<dlistRowsJson.size(); x++) {
                    try {
                        JSONObject jsonObject = new JSONObject(dlistRowsJson.get(x));
                        JSONArray jsonArray = jsonObject.getJSONArray("dlistArray");
                        for(int y =0; y<jsonArray.length(); y++){
                            JSONObject jObj = jsonArray.getJSONObject(y);
                            if (jObj.getString("mId").equals("field" + dlistFieldId + "_" + extension)) {

                                if(jObj.getString("mFieldType").toLowerCase().equals("file")){
                                    String ext = getExtension(jObj.getString("mValue"));
                                    //Checks if there is any extension after the last . in your input
                                    if (ext.isEmpty()) {
                                        //  dismissValidationProgress();
                                        showErrorDialog(jObj.getString("mFieldName"));
                                        isValidated = false;
                                        break first;
                                    }
                                }else{

                                    if(jObj.getString("mFieldType").toLowerCase().equals("double")){

                                        if(jObj.getString("mValue").isEmpty() ||
                                                jObj.getString("mValue").matches("0|0.0|0.00|0.000")){
                                            showErrorDialog(jObj.getString("mFieldName"));
                                            isValidated = false;
                                            break first;
                                        }
                                    }else{
                                        if(jObj.getString("mValue").isEmpty()) {
                                            //  dismissValidationProgress();
                                            showErrorDialog(jObj.getString("mFieldName"));
                                            isValidated = false;
                                            break first;
                                            //  return false;
                                        }
                                    }
                                }
                                Log.e(DEBUG_TAG, "getDlistFieldValue  " + "dlist field = " + jObj.getString("mId") + " value = " + jObj.getString("mValue"));
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(dlistRowsJson.size() == 0){

                    String mandatoryFieldName = getMandatoryName(positionOfMandatoryField);
                    //  dismissValidationProgress();
                    showErrorDialog(mandatoryFieldName);
                    isValidated = false;
                    break first;
                }
            }
        }
        return isValidated;
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
