package com.example.myapplication.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class PackageManagerHelper {

    private Context context;

    public PackageManagerHelper(Context context){
        this.context = context;
    }

    public void directUserToESSApp(){
        String packageName = "com.itaakash.ess";
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        boolean isInstalled = isPackageInstalled(packageName,packageManager);
        if(isInstalled){
            Intent intent = context.getApplicationContext().getPackageManager().
                    getLaunchIntentForPackage(packageName);
            if(intent != null) {
                context.startActivity(intent);
            }
        }else{

            Uri mUri = Uri.parse("https://play.google.com/store/apps/details?id=" + packageName);
            Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
            context.startActivity(mIntent );
        }

    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
