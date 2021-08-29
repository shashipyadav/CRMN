package com.example.myapplication.user_interface.home.view;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.home.model.HomeModel;
import com.example.myapplication.Constant;
import com.example.myapplication.customviews.CustomItemDecoration;
import com.example.myapplication.customviews.LinePagerIndicatorDecoration;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MultipleTypeImageAdapter extends RecyclerView.Adapter<MultipleTypeImageAdapter.ViewHolder> {

    private Context mContext;
    private List<HomeModel> mList;
    private LayoutInflater inflater;

    public MultipleTypeImageAdapter(Context context, List<HomeModel> list){
        mContext = context;
        this.mList = list;
        if(context != null){
            inflater = LayoutInflater.from(context);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_main_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
     // holder.tv_rv.setHasFixedSize(true);
        final DemoMultipleTypeImageAdapter adapter;
        String sectionName = mList.get(position).getProductList().get(0).getSection_Name();
        int elongated  = mList.get(position).getProductList().get(0).getElongdated();

        if(position == 0 ) {
            adapter  = new DemoMultipleTypeImageAdapter( mContext,mList.get(position).getProductList(),Constant.EXTRA_LINEAR);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
            holder.tv_rv.setLayoutManager(layoutManager);
            holder.tv_title.setVisibility(View.GONE);
            holder.tv_rv.addItemDecoration(new LinePagerIndicatorDecoration());

            final int speedScroll = 3000;
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                int count = 0;
                boolean flag = true;

                @Override
                public void run() {
                    if(adapter.getItemCount() > 1){
                        if (count < adapter.getItemCount()) {
                            if (count == adapter.getItemCount() - 1) {
                                flag = false;
                            } else if (count == 0) {
                                flag = true;
                            }

                            if (flag) count++;
                            else count--;

                            holder.tv_rv.smoothScrollToPosition(count);
                            handler.postDelayed(this, speedScroll);
                        }
                    }

                }
            };

            handler.postDelayed(runnable, speedScroll);
        }else{

            int gridCode = mList.get(position).getProductList().get(0).getGridCode();
            holder.tv_rv.addItemDecoration(new CustomItemDecoration());
            if(elongated == 0){
                adapter = new DemoMultipleTypeImageAdapter( mContext,mList.get(position).getProductList(), Constant.EXTRA_GRID);

                GridLayoutManager manager = new GridLayoutManager(mContext, gridCode, GridLayoutManager.HORIZONTAL, false);
                holder.tv_rv.setLayoutManager(manager);

            }else{
                adapter  = new DemoMultipleTypeImageAdapter( mContext,mList.get(position).getProductList(),Constant.EXTRA_LINEAR);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
                holder.tv_rv.setLayoutManager(layoutManager);
            }
            holder.tv_title.setVisibility(View.VISIBLE);
        }
        holder.tv_rv.setAdapter(adapter);
        holder.tv_title.setText(sectionName);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ViewPager viewPager;
        private TextView tv_title;
        private TabLayout tabLayout;
        private RecyclerView tv_rv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_rv = itemView.findViewById(R.id.tv_rv);
            tv_rv.setHasFixedSize(true);
            tv_rv.setItemViewCacheSize(20);
            tv_rv.setDrawingCacheEnabled(true);

        }
    }


}
