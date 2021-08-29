package com.example.myapplication.user_interface.pendingtask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.forms.controller.OnBottomReachedListener;

import java.util.ArrayList;
import java.util.List;

public class PendingTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private List<Task> pendingTaskList;
    ValueFilter valueFilter;
    List<Task> mFilterList;
    private SparseBooleanArray selectedItems;
    private OnLoadMoreListener onLoadMoreListener;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;



    public PendingTaskAdapter(Context context,
                              RecyclerView recyclerView,
                              final List<Task> pendingTaskList/*,
                              PendingTaskItemClickListener mListener*/) {
        if (pendingTaskList == null) {
            throw new IllegalArgumentException("list must not be null");
        }
        this.context            = context;
        this.pendingTaskList    = pendingTaskList;
        this.mFilterList        = pendingTaskList;
        this.selectedItems      = new SparseBooleanArray();
       // this.listener           = mListener;


        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);


                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            Log.e("TOTALITEM", String.valueOf(totalItemCount));
                            Log.e("LASTVISIBLEITEM == ", String.valueOf(lastVisibleItem));
                            Log.e("visibleThreshold == ", String.valueOf(visibleThreshold));

                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoading() {
        loading = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
//        View view = inflater.inflate(R.layout.item_pending_task, parent, false);
//        // Return a new holder instance
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;

        if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.item_pending_task, parent, false);
            return  new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view =inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Task task = pendingTaskList.get(position);
        if (holder instanceof ViewHolder) {
            ViewHolder vHolder = (ViewHolder) holder;

            if(task.getNameOfCompany().isEmpty() && task.getProjectName().isEmpty()){
                vHolder.txtCompanyProject.setVisibility(View.GONE);
            }else{
                vHolder.txtCompanyProject.setVisibility(View.VISIBLE);
            }

            if(task.getTaskDetails().isEmpty()){
                vHolder.txtTask.setVisibility(View.GONE);
            }else{
                vHolder.txtTask.setVisibility(View.VISIBLE);
            }

            vHolder.txtModule.setText(task.getModuleName());
            vHolder.txtForm.setText(task.getFormName());
            vHolder.txtAssignedBy.setText(task.getUserName());
            vHolder.txtDateTime.setText(task.getShowDate());
            vHolder.txtCompanyProject.setText(task.getNameOfCompany()+" | "+task.getProjectName());
            vHolder.txtTask.setText(task.getTaskDetails());
            vHolder.btnState.setText(task.getStateName());

            if(task.isSelected()){
                vHolder.txtChar.setVisibility(View.GONE);
                vHolder.imgSelected.setVisibility(View.VISIBLE);
            }else{
                vHolder.txtChar.setVisibility(View.VISIBLE);
                vHolder.imgSelected.setVisibility(View.GONE);
            }

            String userName = task.getUserName();
            if(!userName.isEmpty()){
                vHolder.txtChar.setText(userName.substring(0, 1));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent = new Intent(context, PendingTaskDetailsActivity.class);
                    intent.putExtra(Constant.EXTRA_FORM_ID, task.getFormId());
                    intent.putExtra(Constant.EXTRA_RECORD_ID,task.getRecordId());
                    intent.putExtra(Constant.EXTRA_MODULE, task.getModuleName());
                    intent.putExtra(Constant.EXTRA_USER_NAME,task.getUserName());
                    intent.putExtra(Constant.EXTRA_FORM_NAME,task.getFormName());
                    context.startActivity(intent);
                }
            });

            vHolder.txtChar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return pendingTaskList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount()
    {
        return pendingTaskList == null ? 0 : pendingTaskList.size();
    }


    // Search
    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtModule, txtForm, txtAssignedBy,txtDateTime,txtCompanyProject, txtTask,txtChar;
        Button btnState,btnMore;
        ImageView imgSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtModule           = itemView.findViewById(R.id.txt_module);
            txtForm             = itemView.findViewById(R.id.txt_form);
            txtAssignedBy       = itemView.findViewById(R.id.txt_assigned_by);
            txtDateTime         = itemView.findViewById(R.id.txt_date_time);
            txtCompanyProject   = itemView.findViewById(R.id.txt_company_project);
            txtTask             = itemView.findViewById(R.id.txt_task);
            txtChar             = itemView.findViewById(R.id.txt_char);
            btnState            = itemView.findViewById(R.id.btn_state);
            imgSelected         = itemView.findViewById(R.id.img_selected);
        }
    }


    class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar           = itemView.findViewById(R.id.progressbar);
        }
    }

    private class ValueFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if(constraint !=null && constraint.length() > 0){
                List<Task> filterList = new ArrayList<>();
                for (int i = 0; i < mFilterList.size(); i++) {

                    Task obj =mFilterList.get(i);
                    if(obj != null) {
                        if ((mFilterList.get(i).getModuleName().toUpperCase())
                                .contains(constraint.toString().toUpperCase()) ||

                                mFilterList.get(i).getFormName().toUpperCase()
                                        .contains(constraint.toString().toUpperCase()) ||

                                mFilterList.get(i).getUserName().toUpperCase()
                                        .contains(constraint.toString().toUpperCase()) ||

                                mFilterList.get(i).getNameOfCompany().toUpperCase()
                                        .contains(constraint.toString().toUpperCase()) ||

                                mFilterList.get(i).getProjectName().toUpperCase()
                                        .contains(constraint.toString().toUpperCase()) ||

                                mFilterList.get(i).getStateName().toUpperCase()
                                        .contains(constraint.toString().toUpperCase()) ||

                                mFilterList.get(i).getShowDate().toUpperCase()
                                        .contains(constraint.toString().toUpperCase()) ||

                                mFilterList.get(i).getTaskDetails().toUpperCase()
                                        .contains(constraint.toString().toUpperCase()))

                        {
                            filterList.add(mFilterList.get(i));
                        }
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
            pendingTaskList = (List<Task>) results.values;
            notifyDataSetChanged();
        }
    }

}


