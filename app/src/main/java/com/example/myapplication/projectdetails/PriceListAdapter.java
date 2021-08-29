package com.example.myapplication.projectdetails;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PriceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public List<PriceListModel> amenitiesModelList;
    private SharedPreferences sharedPref;
    private long downloadID;
    private SharedPrefManager prefManager;

    public PriceListAdapter(Activity activity, List<PriceListModel> amenitiesModelList, SharedPrefManager prefManager) {
        this.activity = activity;
        this.amenitiesModelList = amenitiesModelList;
        //activity.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        this.prefManager =prefManager;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pricelist, parent, false);

        return new VContentInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((VContentInfoHolder) holder).title.setText(amenitiesModelList.get(position).getPricedesc());
        sharedPref = activity.getSharedPreferences("customerpp", Context.MODE_PRIVATE);

        String someFilepath = amenitiesModelList.get(position).getPriceimage();
        String extension = someFilepath.substring(someFilepath.lastIndexOf("."));
        final String imageUrl = prefManager.getClientServerUrl()+ "uploads" +prefManager.getCloudCode() + "/Folder3565/" + amenitiesModelList.get(position).getPriceimage();

        if(!extension.equalsIgnoreCase(".pdf")) {


            Picasso.with(activity)
                    .load(imageUrl)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.drawable.progress_animation)

                    .into(((VContentInfoHolder) holder).iv_image);
        }

        ((VContentInfoHolder) holder).iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity,"Downloading Started",Toast.LENGTH_LONG).show();

                String someFilepath = amenitiesModelList.get(position).getPriceimage();
                String extension = someFilepath.substring(someFilepath.lastIndexOf("."));
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
                request.setDescription("Some descrition");
                request.setTitle(amenitiesModelList.get(position).getPricedesc()+extension);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, amenitiesModelList.get(position).getPricedesc()+extension);

                DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
               //  downloadID = manager.enqueue(request);// enqueue puts the download request in the queue.

            }
        });

    }

    @Override
    public int getItemCount() {
        return amenitiesModelList.size();
    }


    private class VContentInfoHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private LinearLayout ll_main;
        private ImageView iv_image;


        public VContentInfoHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);


           // Typeface custom_font1 = Typeface.createFromAsset(activity.getAssets(), "Titillium-Regular.otf");
           // ((TextView) itemView.findViewById(R.id.title)).setTypeface(custom_font1);






        }
    }




}
