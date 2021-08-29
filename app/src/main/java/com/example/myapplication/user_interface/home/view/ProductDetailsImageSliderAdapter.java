package com.example.myapplication.user_interface.home.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.home.model.ImageModel;
import com.example.myapplication.user_interface.home.model.Product;
import com.example.myapplication.util.ToastUtil;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductDetailsImageSliderAdapter extends PagerAdapter{
    private Context mContext;
    private List<String> mList;
    private LayoutInflater inflater;
    public View currentImageView;
    public String itemName;


    public ProductDetailsImageSliderAdapter(Context context, List<String> list,String itemName){
        mContext = context;
        this.mList = list;
        if(context != null){
            inflater = LayoutInflater.from(context);
        }
        this.itemName = itemName;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        if(position == mList.size() -1 ){

            String imgUrl = mList.get(position);
            String extension= imgUrl.substring(imgUrl.lastIndexOf(".") + 1);
            if(extension.toLowerCase().matches("jpg|png")){
                final String imageUri = mList.get(position);
                view = inflater.inflate(R.layout.item_product_detail_image, container, false);
                ImageView   imageView = view.findViewById(R.id.product_image);

                Picasso.with(mContext).load(imageUri)
               .placeholder(R.drawable.progress_animation)
               .error(R.drawable.default_product_img)
                        .into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, FullScreenImageActivity.class);
                        intent.putExtra(Constant.EXTRA_IMAGE_PATH ,imageUri);
                        intent.putExtra(Constant.EXTRA_ITEM_ID,itemName);
                        mContext.startActivity(intent);
                    }
                });


            }else {
                String videoUrl = mList.get(position);
                view = inflater.inflate(R.layout.item_product_detail_video, container, false);
                WebView webview = view.findViewById(R.id.videoview);
                webview.setWebViewClient(new WebViewClient());
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webview.getSettings().setPluginState(WebSettings.PluginState.ON);
                webview.getSettings().setMediaPlaybackRequiresUserGesture(true);
                webview.setWebChromeClient(new WebChromeClient());
                //https://www.youtube.com/watch?v=iVoMMdl26F0
                String videoID = getYouTubeId(videoUrl);
                webview.loadUrl("http://www.youtube.com/embed/" + videoID + "?autoplay=1&vq=small");
            }
        }else{
            final String imageUri = mList.get(position);
            view = inflater.inflate(R.layout.item_product_detail_image, container, false);
            ImageView   imageView = view.findViewById(R.id.product_image);
            Picasso.with(mContext).load(imageUri)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.default_product_img)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FullScreenImageActivity.class);
                    intent.putExtra(Constant.EXTRA_IMAGE_PATH ,imageUri);
                    intent.putExtra(Constant.EXTRA_ITEM_ID,itemName);
                    mContext.startActivity(intent);
                }
            });


        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull  View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull  Object object) {
        container.removeView((View) container);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull  Object object) {
        currentImageView = (View)object;
    }

    private String getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }
}






