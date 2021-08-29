package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.model.ProjectModel;
import com.example.myapplication.projectdetails.GallaryActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class ProjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    //

    private Activity activity;
    public List<ProjectModel> projectModelList;
    private SharedPrefManager prefManager;

    public ProjectAdapter(Activity activity, List<ProjectModel> projectModelList,SharedPrefManager prefManager) {
        this.activity = activity;
        this.projectModelList = projectModelList;
        this.prefManager =prefManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_details, parent, false);

        return new VContentInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((VContentInfoHolder) holder).tv_project_Name.setText(projectModelList.get(position).getPROJECT());
        ((VContentInfoHolder) holder).tv_rera_no.setText(projectModelList.get(position).getRERA());
        ((VContentInfoHolder) holder).tv_loaction.setText(projectModelList.get(position).getLOCATION());
        ((VContentInfoHolder) holder).tv_status.setText(projectModelList.get(position).getSTATUS());
        ((VContentInfoHolder) holder).tv_categery.setText(projectModelList.get(position).getCATEGORY());


        //https:4.strategicerpcloud.com/strategicerp/uploadskundan/Folder1809/1809_13_34574_V1_nibm-final file(2016)a.jpg


        //sharedPref = activity.getSharedPreferences("customerpp", Context.MODE_PRIVATE);


        final String imageUrl = prefManager.getClientServerUrl() + "uploads" +  prefManager.getCloudCode() + "/Folder1809/" + projectModelList.get(position).getIMAGE();
        Log.d("imageUrl", imageUrl);
        Picasso.with(activity)
                .load(imageUrl)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.progress_animation)

                .into(((VContentInfoHolder) holder).iv_project_image);

     /*   Picasso.with(activity).load(imageUrl)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.logo_c)

                .into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ((VContentInfoHolder) holder).iv_project_image.setBackground(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*///9821716127 shreekant joshi


        ((VContentInfoHolder) holder).ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(activity, GallaryActivity.class);
                intent.putExtra("projectName", projectModelList.get(position).getPROJECT());
                intent.putExtra("projectCode", projectModelList.get(position).getPROJECT_CODE());
                activity.startActivity(intent);





            }
        });


        ((VContentInfoHolder) holder).iv_wht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  String image_url = "https://play.google.com/store/apps/details?id=com.itaakash.android.nativecustomerapp";
                //https://4.strategicerpcloud.com/strategicerp/uploadskundan/Folder1809/1809_13_34574_V1_nibm-final file(2016)a.jpg
                String image_url = imageUrl.replaceAll(" ", "%20");



              /*  Intent shareIntent = new Intent();
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setAction(Intent.ACTION_SEND);
                //without the below line intent will show error
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, image_url);
                // Target whatsapp:
                shareIntent.setPackage("com.whatsapp");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                try {
                    activity.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity,
                            "Whatsapp have not been installed.",
                            Toast.LENGTH_SHORT).show();
                }*/
               /* Uri imageUri = Uri.parse("android.resource://" + activity.getPackageName()
                        + "/drawable/" + "youtube");*/
              /*  Uri imageUri = Uri.parse(image_url);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);*/
               // shareIntent.setType("*/*");
               /* shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(Intent.createChooser(shareIntent, "send"));*/

                int sharableImage = R.drawable.about_us_icon;

                URL url = null;
                Bitmap bitmap = null;
                try {
                    url = new URL(image_url);
                     bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  Bitmap bitmap= BitmapFactory.decodeResource(activity.getResources(), sharableImage);
              String s =  getUUID().replaceAll("-","-");
                String path = activity.getExternalCacheDir()+"/sharable_image.jpg";
               // String path = activity.getExternalCacheDir()+"/"+getUUID()+"/sharable_image.jpg";
                java.io.OutputStream out;
                File file = new File(path);

                if(!file.exists()) {
                    try {
                        out = new java.io.FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                if(file.exists()) {
                    try {
                        out = new java.io.FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                path = file.getPath();
///storage/emulated/0/Android/data/com.itaakash.android.nativepartnerapp/cache/sharable_image.jpg
                //Uri bmpUri = Uri.parse("file://" + path);
                Uri bmpUri = FileProvider.getUriForFile(
                        activity,
                        "com.strategicerp.crm", //(use your app signature + ".provider" )
                        file);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_TEXT, projectModelList.get(position).getPROJECT());
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/*");
                shareIntent.setPackage("com.whatsapp");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                activity.startActivity(Intent.createChooser(shareIntent,"Share with"));
            }
        });


    }

    @Override
    public int getItemCount() {
        return projectModelList.size();
    }
    public String getUUID() {
        UUID uuid = UUID.randomUUID();
        return String.valueOf(uuid);
    }

    private class VContentInfoHolder extends RecyclerView.ViewHolder {

        private TextView tv_project_Name, tv_rera_no, tv_loaction, tv_categery, tv_status;
        private LinearLayout ll_main;
        private ImageView iv_project_image,iv_wht;


        public VContentInfoHolder(View itemView) {
            super(itemView);

            tv_project_Name = (TextView) itemView.findViewById(R.id.tv_project_Name);
            tv_rera_no = (TextView) itemView.findViewById(R.id.tv_rera_no);
            tv_loaction = (TextView) itemView.findViewById(R.id.tv_loaction);
            tv_categery = (TextView) itemView.findViewById(R.id.tv_categery);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            iv_project_image = (ImageView) itemView.findViewById(R.id.iv_project_image);
            iv_wht = (ImageView) itemView.findViewById(R.id.iv_wht);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);


          /*  Typeface custom_font1 = Typeface.createFromAsset(activity.getAssets(), "Titillium-Regular.otf");
            ((TextView) itemView.findViewById(R.id.tv_project_Name)).setTypeface(custom_font1);
            ((TextView) itemView.findViewById(R.id.tv_rera_no)).setTypeface(custom_font1);
            ((TextView) itemView.findViewById(R.id.tv_loaction)).setTypeface(custom_font1);
            ((TextView) itemView.findViewById(R.id.tv_categery)).setTypeface(custom_font1);
            ((TextView) itemView.findViewById(R.id.tv_status)).setTypeface(custom_font1);


            Typeface custom_font = Typeface.createFromAsset(activity.getAssets(), "titillium-web.bold.ttf");
            ((TextView) itemView.findViewById(R.id.tvrerano)).setTypeface(custom_font);
            ((TextView) itemView.findViewById(R.id.tvLocation)).setTypeface(custom_font);
            ((TextView) itemView.findViewById(R.id.tvCategery)).setTypeface(custom_font);*/


        }
    }

    public void filterList(List<ProjectModel> filterdNames) {
        this.projectModelList = filterdNames;
        notifyDataSetChanged();
    }
}
