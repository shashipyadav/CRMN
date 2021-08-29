package com.example.myapplication.user_interface.quicklink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

import java.util.List;

public class QuickLinkAdapter extends RecyclerView.Adapter<QuickLinkAdapter.ViewHolder> {

    private Context mContext;
    private List<QuickLink> mQuickLinkList;
    private QuickLinkInterface mListener;

    public QuickLinkAdapter(Context context, List<QuickLink> quickLinkList,
                            QuickLinkInterface listener) {
        mContext = context;
        mQuickLinkList = quickLinkList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_quick_link_black, parent, false);
        QuickLinkAdapter.ViewHolder viewHolder = new QuickLinkAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final QuickLink quickLinkItem = mQuickLinkList.get(position);

        holder.txtQuickLink.setText(quickLinkItem.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.quickLinkClicked(quickLinkItem);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mQuickLinkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtQuickLink;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuickLink = itemView.findViewById(R.id.txt_quick_lnk);
        }
    }
}
