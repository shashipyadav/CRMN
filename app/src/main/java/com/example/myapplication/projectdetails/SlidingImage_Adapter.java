package com.example.myapplication.projectdetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Parsania Hardik on 23/04/2016.
 */
public class SlidingImage_Adapter extends PagerAdapter {


    private List<GalleryModel> imageModelArrayList;
    private LayoutInflater inflater;
    private Context context;
    private SharedPrefManager prefManager;


    public SlidingImage_Adapter(Context context, List<GalleryModel> imageModelArrayList, SharedPrefManager prefManager) {
        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
        inflater = LayoutInflater.from(context);
        this.prefManager=prefManager;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageModelArrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);

       // imageView.setImageResource(imageModelArrayList.get(position).getGalleryimage());



        final String imageUrl = prefManager.getClientServerUrl()+ "uploads" +prefManager.getCloudCode() + "/Folder1807/" + imageModelArrayList.get(position).getGalleryimage();

        Picasso.with(context)
                .load(imageUrl)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.progress_animation)

                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullImageViewActivity.class);
                intent.putExtra("imageUrl",imageUrl);
                intent.putExtra("decp",imageModelArrayList.get(position).getGallerydesc());

                context.startActivity(intent);
            }
        });

       /* Picasso.with(context).load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setBackground(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/


        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}