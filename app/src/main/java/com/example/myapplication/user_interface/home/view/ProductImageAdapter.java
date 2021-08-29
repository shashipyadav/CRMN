package com.example.myapplication.user_interface.home.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.home.controller.ProductDetailsActivity;
import com.example.myapplication.user_interface.home.controller.WishlistProductDetailsActivity;
import com.example.myapplication.user_interface.home.model.ProductItem;
import com.example.myapplication.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ViewHolder> {

    private Context mContext;
    private List<ProductItem> mList;
    private LayoutInflater inflater;
    private boolean mIsWishlist;
    private DeleteProductInterface mlistener;

    public interface DeleteProductInterface {
        void onProductRemoved(int position);
    }

    public ProductImageAdapter(Context context, List<ProductItem> list,
                               boolean isWishlist,
                               DeleteProductInterface listener){
        mContext = context;
        this.mList = list;
        if(context != null){
            inflater = LayoutInflater.from(context);
        }
        this.mIsWishlist = isWishlist;
        this.mlistener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ProductItem productObj =mList.get(position);
        //holder.txtItemCode.setText(productObj.getItemID());

        if(productObj.getProductInfo() != null) {
            holder.txtTitle.setText(productObj.getItemName());

          //  holder.txtTitle.setText(productObj.getProductInfo().getSkuName());
          //  holder.txtDesc.setText(productObj.getProductInfo().getItemDesc());

            final String imageUri = productObj.getProductInfo().getImageThumbnail();
            Log.e("ProductImageAdapter",imageUri);
            if(!imageUri.isEmpty()){
                Picasso.with(mContext).load(imageUri).fit()
               .placeholder(R.drawable.progress_animation)
               .error(R.drawable.default_product_img)
                        .into(holder.imgProduct);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailsActivity.class);

                if(mIsWishlist){
                    intent.putExtra(Constant.EXTRA_MODE, "wishList");
                }else{
                    intent.putExtra(Constant.EXTRA_MODE, "productList");
                }
                intent.putExtra(Constant.EXTRA_OBJECT ,productObj);
                mContext.startActivity(intent);
            }
        });
        if(mIsWishlist){
            holder.imageDelete.setVisibility(View.VISIBLE);
            holder.imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   mlistener.onProductRemoved(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        if(mList != null){
            return mList.size();
        }else{
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTitle, txtDesc, txtItemCode;
        ImageView imgProduct,imageDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title);
         //   txtDesc = itemView.findViewById(R.id.description);
         //   txtItemCode = itemView.findViewById(R.id.txt_item_code);
            imgProduct = itemView.findViewById(R.id.product_image);
            imageDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
