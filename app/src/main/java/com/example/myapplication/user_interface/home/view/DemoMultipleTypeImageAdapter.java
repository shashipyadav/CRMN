package com.example.myapplication.user_interface.home.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.home.controller.OpenWebPageActivity;
import com.example.myapplication.user_interface.home.controller.ProductActivity;
import com.example.myapplication.user_interface.home.model.HomeSectionModel;
import com.example.myapplication.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DemoMultipleTypeImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<HomeSectionModel> mList;
    private LayoutInflater inflater;
    private String mlayoutManagerType;


    public DemoMultipleTypeImageAdapter(Context context, List<HomeSectionModel> list, String layoutManagerType){
        mContext = context;
        this.mList = list;
        if(context != null){
            inflater = LayoutInflater.from(context);
        }
        this.mlayoutManagerType = layoutManagerType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Constant.TYPE_HEADER) {
            view = inflater.inflate(R.layout.item_image_pager, parent, false);
            return new PagerViewHolder(view);
        }else {
            view = inflater.inflate(R.layout.item_home_image, parent, false);
            return new ImageViewHolder(view);
        }

        /*else{
            view = inflater.inflate(R.layout.item_product_grid, parent, false);
            return new ProductViewHolder(view);
        }
         */
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

         if (getItemViewType(position) == Constant.TYPE_HEADER)
        {
            final  PagerViewHolder  mHolder = (PagerViewHolder) holder;
            String imageUri = mList.get(position).getImage_Path();

            Picasso.with(mContext)
                    .load(imageUri).fit()
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.default_product_img)
                    .into(mHolder.imageView);

        }else /*if(getItemViewType(position) == Constant.TYPE_NORMAL){ */{
             final  ImageViewHolder  mHolder = (ImageViewHolder) holder;
             String imageUri = mList.get(position).getImage_Path();
             final String sectionName = mList.get(position).getSection_Name();

             if(mlayoutManagerType.equals(Constant.EXTRA_GRID)){
                //home_image_height
              //   mHolder.cardView.getLayoutParams().height = mContext.getResources().getDimensionPixelSize(R.dimen.home_image_height);
                 mHolder.cardView.getLayoutParams().width = getImageWidth(40);
                mHolder.cardView.getLayoutParams().height =  getImageWidth(100);
             }else{
                 mHolder.cardView.getLayoutParams().width = getImageWidth(40);
             }

             Picasso.with(mContext).load(imageUri)
                     .fit()
                     .placeholder(R.drawable.progress_animation)
                     .error(R.drawable.default_product_img)
                     .into(mHolder.imageView);

             mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     final String apiUrl = mList.get(position).getAPI_ID();
                     if(sectionName.toLowerCase().matches("schemes and discounts|news")){

                         if(apiUrl.isEmpty()){
                             Toast.makeText(mContext, mContext.getString(R.string.website_link_na), Toast.LENGTH_SHORT).show();
                         }else{
                             Log.e("SCHEMES AND D" , apiUrl);
                             Intent intent = new Intent(mContext, OpenWebPageActivity.class);
                             intent.putExtra(Constant.EXTRA_URL,apiUrl);
                             intent.putExtra(Constant.EXTRA_TITLE, sectionName);
                             mContext.startActivity(intent);
                         }
                     }else{
                         Intent intent = new Intent(mContext, ProductActivity.class);
                         intent.putExtra(Constant.EXTRA_URL,apiUrl);
                         intent.putExtra(Constant.EXTRA_TITLE, sectionName);
                         intent.putExtra(Constant.EXTRA_MODE, "productList");
                         mContext.startActivity(intent);
                     }
                 }
             });
         }
    }

    public int getImageWidth (int difference){

        return (getWidth() / 2) - difference;
    }

    public int getWidth () {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        double seq = Double.parseDouble(mList.get(position).getSeq());

        if(seq >=2){
//            if(mList.get(position).getSection_Name().toLowerCase().equals("wall tiles")){
//                return Constant.TYPE_PRODUCT;
//            }else{
                return Constant.TYPE_NORMAL;
 //           }
        }else{
            return Constant.TYPE_HEADER;
        }
    }

    public class PagerViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private CardView cardView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardview);

        }
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView1;
        private ImageView imageView2;
        private ImageView imageView3;
        private ImageView imageView4;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
            imageView4 = itemView.findViewById(R.id.imageView4);
        }
    }
}
