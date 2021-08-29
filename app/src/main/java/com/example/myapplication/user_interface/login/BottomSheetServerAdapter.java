package com.example.myapplication.user_interface.login;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dashboard.model.Drilldown;
import com.example.myapplication.user_interface.forms.controller.BottomSheetDropdownAdapter;
import com.example.myapplication.user_interface.forms.model.OptionModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class BottomSheetServerAdapter extends
        RecyclerView.Adapter<BottomSheetServerAdapter.ViewHolder>  implements Filterable {

    private Context mContext;
    private List<Server> mServerList;
    private List<Server> mFilterList;
    private ServerInterface mListener;
    private ValueFilter valueFilter;
    private SharedPrefManager prefManager;

    public interface ServerInterface {
        public void onItemSelected(String value);
    }

    public BottomSheetServerAdapter(Context context,
                                    List<Server> serverList,
                                    ServerInterface listener) {
        this.mContext = context;
        this.mServerList = serverList;
        this.mListener = listener;
        this.mFilterList = serverList;
        prefManager = new SharedPrefManager(mContext);

    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_sheet_server, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Server server = mServerList.get(position);

        holder.txtServerUrl.setText(server.getMobileClientName());
        final String serverImageUrl = String.format(Constant.SERVER_IMAGE_URL,server.getClientLogoForLoginScreen());
        Picasso.with(mContext).load(serverImageUrl).
                placeholder(R.drawable.default_server_icon)
                .fit().into(holder.imageServerIcon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prefManager.setServerDetails(server.getClientServerURL(),
                        server.getClientServerCloudCode(),
                        serverImageUrl);


                String selectedValue = server.getMobileClientName();
                mListener.onItemSelected(selectedValue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mServerList.size();
    }
     static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtServerUrl;
        ImageView imageServerIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtServerUrl = itemView.findViewById(R.id.txt_server_name);
            imageServerIcon = itemView.findViewById(R.id.server_icon);
        }
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(constraint !=null && constraint.length() > 0){
                List<Server> filterList = new ArrayList<>();
                for (int i = 0; i < mFilterList.size(); i++) {

                    if (mFilterList.get(i).getMobileClientName().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        filterList.add(mFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mFilterList.size();
                results.values = mFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mServerList = (List<Server>) results.values;
            notifyDataSetChanged();
        }
    }
}
