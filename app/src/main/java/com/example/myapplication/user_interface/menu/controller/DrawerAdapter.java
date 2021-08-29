package com.example.myapplication.user_interface.menu.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.menu.model.DrawerItem;

import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerItemViewHolder>{

    private Context mContext;
    private List<DrawerItem> menuList;
    private OnMenuClickListener listener;
    private int lastPositionClicked = -1;

    public DrawerAdapter(Context context, List<DrawerItem> itemList,
                         OnMenuClickListener listener){
        this.mContext = context;
        this.menuList = itemList;
        this.listener = listener;
    }

    @Override
    public DrawerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item,parent,false);
        DrawerItemViewHolder dvh=new DrawerItemViewHolder(v);
        return dvh;
    }

    @Override
    public void onBindViewHolder(final DrawerItemViewHolder holder, final int position) {
        holder.bindView(position);
    }

    private Drawable getIcon(String name){
        Resources resources = mContext.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                mContext.getPackageName());
        return resources.getDrawable(resourceId);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class DrawerItemViewHolder extends RecyclerView.ViewHolder{
        TextView itemTitle;
        ImageView imgNext;
        AppCompatImageView imageIcon;

        public DrawerItemViewHolder(View itemView) {
            super(itemView);
            itemTitle=itemView.findViewById(R.id.menu_item_title);
            imgNext = itemView.findViewById(R.id.img_next);
            imageIcon = itemView.findViewById(R.id.menu_item_icon);
        }

        public void bindView(final int position) {
            // bindView() method to implement actions
            final DrawerItem currentItem= menuList.get(position);

            itemTitle.setText(currentItem.getTitle());
            if(currentItem.getListHeader().isEmpty()){
                imgNext.setVisibility(View.GONE);
            }else{
                imgNext.setVisibility(View.VISIBLE);
            }

            String iconName = currentItem.getTitle().toLowerCase();
            String mIconName = iconName.replaceAll("[\\s;\\/:*?\"<>|&']","");
            final int drawableResourceId = mContext.getResources().getIdentifier(mIconName, "drawable", mContext.getPackageName());

            //holder.imageIcon.setPadding(10,10,10,10);
            if(drawableResourceId == 0){
                imageIcon.setImageResource(R.drawable.ic_other);
            }else{
                imageIcon.setImageResource(drawableResourceId);
            }

            if(lastPositionClicked==position) {
             //   imageIcon.setSelected(true);
                itemTitle.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.highlight));
             //   imageIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.highlight));
                imageIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.highlight));
            }else {
               // imageIcon.setSelected(false);
                itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.white));
                itemTitle.setTextColor(ContextCompat.getColor(mContext,R.color.black));
                imageIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.gray));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int visibility = View.VISIBLE;
                    if(lastPositionClicked == position){
                        visibility = View.GONE;
                        lastPositionClicked = -1;
                    }else{
                        lastPositionClicked = position;
                    }
                    listener.onMenuClicked(position,currentItem,visibility);
                    notifyDataSetChanged();
                }
            });
        }

    }
}
