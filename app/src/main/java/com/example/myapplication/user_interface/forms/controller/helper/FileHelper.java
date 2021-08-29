package com.example.myapplication.user_interface.forms.controller.helper;
import android.content.Intent;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.user_interface.forms.view.FileViewerActivity;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.home.controller.PdfViewActivity;
import com.example.myapplication.Constant;
import com.example.myapplication.util.ExtensionUtil;

public class FileHelper {
    private AppCompatActivity activity;
    private boolean isSummary = false;

    public FileHelper(AppCompatActivity activity){
        this.activity = activity;
    }

    public void chooseFile(){
        String[] mimeTypes = {"image/*",
                        "application/pdf",
                        "application/msword",
                        "application/vnd.ms-powerpoint",
                        "application/vnd.ms-excel",
                        "text/plain"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        activity.startActivityForResult(Intent.createChooser(intent,"ChooseFile"), Constant.PICK_FILE_REQUEST_CODE);
    }

    public String getAttachedFileName(String fullPath){
        if(fullPath.equals("0")){
            fullPath = "";
        }
        if(!fullPath.equals("")){
            int index = fullPath.lastIndexOf("/");
            return fullPath.substring(index + 1);
        }else{
            return "";
        }
    }

    public void viewFile(String fileName, boolean isSummary) {
        this.isSummary = isSummary;
        String extension = ExtensionUtil.getExtension( fileName);
        if(!extension.isEmpty()){
            if(extension.toLowerCase().matches("png|jpg|jpeg")){
                openImageViewer(fileName);
            }else{
                openDocumentViewer( fileName);
            }
        }
    }

    public void openDocumentViewer(String fileName){

        String fileUrl = getFileUrl(fileName);
        Intent intent = new Intent(activity, PdfViewActivity.class);
        intent.putExtra(Constant.EXTRA_TITLE, fileName);
        intent.putExtra(Constant.EXTRA_URL, fileUrl );
        activity.startActivity(intent);
    }

    public void openImageViewer( String imageName){
        String imageUrl = getFileUrl(imageName);
        Intent intent = new Intent(activity, FileViewerActivity.class);
        intent.putExtra(Constant.EXTRA_TITLE, imageName);
        intent.putExtra(Constant.EXTRA_URL,imageUrl);
        activity.startActivity(intent);
    }

    private String getFileUrl(String fileName) {

        String folderId = "";
        String[] arr = fileName.split("_",2);
        folderId = arr[0];
        fileName = arr[1];

        if(!isSummary) {
            fileName = folderId+"_"+fileName;
        }

        SharedPrefManager prefManager = new SharedPrefManager(activity);
        String fileUrl = String.format(Constant.IMAGE_URL,
                prefManager.getClientServerUrl(),
                prefManager.getCloudCode(),
                folderId,
                fileName);

       return fileUrl;
    }

}
