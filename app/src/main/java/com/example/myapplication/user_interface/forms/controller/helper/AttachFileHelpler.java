package com.example.myapplication.user_interface.forms.controller.helper;

import android.app.Activity;
import android.app.ProgressDialog;

import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.Constant;
import com.example.myapplication.util.DialogUtil;
import com.example.myapplication.util.MultipartUtility;
import com.example.myapplication.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AttachFileHelpler {
    private static final String DEBUG_TAG = AttachFileHelpler.class.getSimpleName();
    private ProgressDialog mWaiting;
    private SharedPrefManager prefManager;
    private String recordId;
    private String formId;
    private String fileName;
    private Activity activitiy;
    private boolean isAttachFileClicked = false;


    public AttachFileHelpler(Activity activity){
        this.activitiy = activity;
        prefManager = new SharedPrefManager(activity);
    }

    public void uploadFile(){
        if(getRecordId().equals("0")){
            DialogUtil.showAlertDialog(activitiy, "",   "You must select a record before uploading attachment", false, false);
        }else{
            callAttachFileAPI();
        }

    }

    public void callAttachFileAPI(){
        showProgressDialog();
        String url = String.format(Constant.URL_ATTACH_FILE,
                prefManager.getClientServerUrl(),
                getFormId(),
                getRecordId(),
                prefManager.getAuthToken(),
                prefManager.getCloudCode());

       // Log.e(DEBUG_TAG, "URL = "+ url);
        final String charset = "UTF-8";

        try {
            MultipartUtility multipart = new MultipartUtility(url, charset);
            File file = new File(getFileName());
            multipart.addFilePart("theFile", file);
            multipart.addFormField("movement", "Inward");
            multipart.addFormField("bUpload", "submit");

            List<String> response = multipart.finish();
           // Log.e("SERVER REPLIED: ", response.toString());
            final String result = response.toString();
          //  Log.e("RESPONSE", "Attach File Helper" + result);

            if(result.contains("File Sucessfully Uploaded")){

              showMessage("File Successfully Uploaded");
            }else{
                showMessage("Please try again!");
            }
            hideProgressDialog();

        }catch (IOException ex) {
            System.err.println(ex);
            hideProgressDialog();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showMessage(final String message){
        activitiy.runOnUiThread(new Runnable() {
            public void run() {
                ToastUtil.showToastMessage(message,activitiy);
            }
        });
    }

    private void showProgressDialog() {
        activitiy.runOnUiThread(new Runnable() {
            public void run() {
                mWaiting = ProgressDialog.show(activitiy, "",
                        "Uploading Attachment ...", false);
            }
        });
    }

    private void hideProgressDialog() {
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

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isAttachFileClicked() {
        return isAttachFileClicked;
    }

    public void setAttachFileClicked(boolean attachFileClicked) {
        isAttachFileClicked = attachFileClicked;
    }
}
