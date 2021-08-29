package com.example.myapplication.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DownloadReportUtil {

    private static DownloadReportUtil mInstance;
    private Context mCtx;

    private DownloadReportUtil(Context context)
    {
        mCtx = context;
    }

    public static synchronized DownloadReportUtil getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DownloadReportUtil(context);
        }
        return mInstance;
    }

    public void downloadReport(String printUrl,String title ){
        Toast.makeText(mCtx, title + " Download Started", Toast.LENGTH_SHORT).show();
        Log.e("PRINT URL",printUrl);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(printUrl));
        request.setDescription("Some Description");
        request.setTitle(title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

        DownloadManager manager = (DownloadManager) mCtx.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
