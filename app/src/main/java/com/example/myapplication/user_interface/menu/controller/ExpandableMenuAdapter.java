package com.example.myapplication.user_interface.menu.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.List;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.menu.model.DrawerItem;

public class ExpandableMenuAdapter extends BaseExpandableListAdapter {

    Context context;
    private List<DrawerItem> listDataGroup;
    private HashMap<DrawerItem, List<DrawerItem>> listDataChild;
    private LayoutInflater inflater;
    private ExpandableListView expandableListView;
    int lastExpandedPosition = -1;
    private ExpandableItemClickListener expListener;
    private int lastChildPositionClicked = -1;
    private int lastPositionClicked = -1;

    public ExpandableMenuAdapter(Context context, List<DrawerItem> listDataGroup,
                                       HashMap<DrawerItem, List<DrawerItem>> listChildData,
                                 ExpandableListView expListView, ExpandableItemClickListener listener) {
        this.context = context;
        this.listDataGroup = listDataGroup;
        this.listDataChild = listChildData;
        inflater = LayoutInflater.from(context);
        this.expandableListView = expListView;
        this.expListener = listener;
    }

    @Override
    public int getGroupCount() {
        return this.listDataGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (listDataChild.get(listDataGroup.get(groupPosition)) != null) {
            return listDataChild.get(listDataGroup.get(groupPosition)).size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
            return this.listDataChild.get(this.listDataGroup.get(groupPosition)).get(childPosition);
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View view,
                             final ViewGroup parent) {

        final DrawerItem pObject = (DrawerItem) getGroup(groupPosition);

        try {
            if (view == null) {
                view = inflater.inflate(R.layout.item_parent, null);
            }

            LinearLayout root = view.findViewById(R.id.parent_view);
            TextView txtTitle = view.findViewById(R.id.txt_item);
            ImageView imgIndicator = view.findViewById(R.id.image_indicator);

            txtTitle.setText(pObject.getTitle());


        if (getChildrenCount(groupPosition) == 0) {
            imgIndicator.setVisibility(View.GONE);
        } else {
            imgIndicator.setVisibility(View.VISIBLE);
            //this code sets the correct image when the group is collapsed and expanded.
            int imageResourceId = isExpanded ? R.drawable.ic_minus
                    : R.drawable.ic_plus;
            imgIndicator.setImageResource(imageResourceId);
        }

            if(lastPositionClicked==groupPosition) {
                txtTitle.setTextColor(ContextCompat.getColor(context,R.color.white));
                root.setBackgroundColor(ContextCompat.getColor(context,R.color.highlight));
            }else {
                txtTitle.setTextColor(ContextCompat.getColor(context,R.color.black));
                root.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent));
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastPositionClicked = groupPosition;
                    if (getChildrenCount(groupPosition) == 0) {
                        expListener.onExpandableItemClick(pObject);
                    }else{
                        if(isExpanded) ((ExpandableListView) parent).collapseGroup(groupPosition);
                        else ((ExpandableListView) parent).expandGroup(groupPosition, true);
                    }
                    notifyDataSetChanged();
                }
            });


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                            && groupPosition != lastExpandedPosition) {
                        expandableListView.collapseGroup(lastExpandedPosition);
                        expandableListView.setSelectedGroup(groupPosition);
                }
                    lastExpandedPosition = groupPosition;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View view, ViewGroup parent) {

        final DrawerItem childObj = (DrawerItem) getChild(groupPosition, childPosition);
        if (view == null) {
            view = inflater.inflate(R.layout.item_child, null);
        }

        LinearLayout root = view.findViewById(R.id.child_root);
        TextView txtChild =  view.findViewById(R.id.txt_child_item);
        txtChild.setText(childObj.getTitle());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expListener.onExpandableItemClick(childObj);
            }
        });

//        if(lastChildPositionClicked==childPosition) {
//            txtChild.setTextColor(ContextCompat.getColor(context,R.color.white));
//            root.setBackgroundColor(ContextCompat.getColor(context,R.color.highlight));
//        }else {
//            txtChild.setTextColor(ContextCompat.getColor(context,R.color.black));
//            root.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
//        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
