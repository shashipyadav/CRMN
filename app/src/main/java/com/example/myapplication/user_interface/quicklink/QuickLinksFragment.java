package com.example.myapplication.user_interface.quicklink;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuickLinksFragment extends Fragment implements QuickLinkInterface{

    private LinearLayout llNoItemsView;
    private TextView txtNoItemsText;
    private ImageView noItemsImg;
    private RecyclerView recyclerView;
    private QuickLinkAdapter mAdapter;
    private List<QuickLink> quickLinkList;
    private QuickLinkClickInterface mQuickLinkClickListener;

    public interface QuickLinkClickInterface{
        public void onQuickLinkClicked(QuickLink obj);
    }

    public QuickLinksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_quick_links, container, false);
        initView(root);
        setTitle();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mQuickLinkClickListener = (QuickLinksFragment.QuickLinkClickInterface) context;
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getSavedQuickLinks();
        setRecyclerView(view);
    }

    private void setTitle(){
        getActivity().setTitle(getResources().getString(R.string.quick_link));
    }

    private void initView(View root){
        quickLinkList = new ArrayList<>();
        llNoItemsView = root.findViewById(R.id.no_items_view);
        txtNoItemsText = root.findViewById(R.id.txt_msg);
        txtNoItemsText.setText("No Quick Links");
        noItemsImg = root.findViewById(R.id.no_items_img);
        noItemsImg.setImageResource(R.drawable.ic_list);
    }

    private void getSavedQuickLinks(){
        try{
        SharedPrefManager mPrefManager = new SharedPrefManager(getActivity());
        Gson gson = new Gson();
        String jsonText = mPrefManager.getQuickLinks();
        Type type = new TypeToken<List<QuickLink>>(){}.getType();
        quickLinkList = gson.fromJson(jsonText, type);

        if(quickLinkList != null){

            for(int i=0; i<quickLinkList.size(); i++){
                QuickLink quickLink = quickLinkList.get(i);
                Log.e("Saved", "QuickLink = "+ quickLink.getChartId() + quickLink.getTitle());
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setRecyclerView(View root) {
        mAdapter = new QuickLinkAdapter(getActivity(), quickLinkList,this);
        recyclerView = root.findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
//                DividerItemDecoration.VERTICAL));


        if(quickLinkList != null){
            if(quickLinkList.isEmpty()){
                llNoItemsView.setVisibility(View.VISIBLE);
            }else{
                recyclerView.setAdapter(mAdapter);
            }
        }else{
            llNoItemsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void quickLinkClicked(QuickLink obj) {
        mQuickLinkClickListener.onQuickLinkClicked(obj);
    }
}
