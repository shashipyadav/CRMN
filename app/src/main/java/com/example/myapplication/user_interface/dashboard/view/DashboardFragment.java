package com.example.myapplication.user_interface.dashboard.view;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.dashboard.model.Dashboard;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.menu.model.DrawerItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private static final String DEBUG_TAG = DashboardFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private List<DrawerItem> dashboardMenuList = new ArrayList<>();
    private DatabaseManager dbManager;
    private List<Dashboard> mdashboardList = new ArrayList<>();
    private ChartTitleAdapter mChartAdapter;
    private ProgressDialog mWaiting;
    private TextView txtNoDataMsg;
    private LinearLayout llNoItemsView;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_dashboard, container,
                false);
        Log.e(DEBUG_TAG, "onCreateView called");
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(DEBUG_TAG, "onViewCreated called");
        initView(view);
        getDashboardMenu();
    }

    private void setTitle(){
        getActivity().setTitle(getResources().getString(R.string.dashboard));
    }

    private void showProgressDialog(){
        mWaiting = ProgressDialog.show(getActivity(), "",
                "Loading...", false);
    }

    private void hideProgressDialog(){
        if (mWaiting != null) {
            mWaiting.dismiss();
        }
    }

    private void getDashboardMenu(){
        mdashboardList.clear();
        showProgressDialog();
        Log.e(DEBUG_TAG, "getDashboardMenu called");
        SharedPrefManager mPrefManager = new SharedPrefManager(getActivity());
        Gson gson = new Gson();
        String jsonText = mPrefManager.getDashboardMenu();
        Type type = new TypeToken<List<DrawerItem>>(){}.getType();
        dashboardMenuList = gson.fromJson(jsonText, type);


        if(dashboardMenuList != null){
            mdashboardList = new ArrayList<>(dashboardMenuList.size());
            for(int i = 0; i< dashboardMenuList.size(); i++){
                DrawerItem drawerItem = dashboardMenuList.get(i);
                Log.e(DEBUG_TAG, "Dashboard Menu drilldownValue = "+ drawerItem.getDrillDown());
                String chartId =  getIdFromMenuOnClick(drawerItem.getOnClick());
                String drillDown = drawerItem.getDrillDown();

                if(!drillDown.equals("")){
                    String drillDownId =  getIdFromMenuOnClick(dashboardMenuList.get(i+1).getOnClick());
                    mdashboardList.add(new Dashboard(drawerItem.getTitle(), chartId, drawerItem.getOnClick(),drawerItem.getDrillDown(),drawerItem.getShowName(),drawerItem.getChartType(),drawerItem.getId(),drillDownId));
                    dashboardMenuList.remove(i+1);
                }else{
                    mdashboardList.add(new Dashboard(drawerItem.getTitle(), chartId, drawerItem.getOnClick(),drawerItem.getDrillDown(),drawerItem.getShowName(),drawerItem.getChartType(),drawerItem.getId(),""));
                }
            }
        }
        callAdapter();
        hideProgressDialog();
    }

    private String getIdFromMenuOnClick(String onclick) {
        String[] m = onclick.split("getChartLeft");
        String n = m[1].replaceAll("[\\(\\)]", "");
        String[] j = n.split(",");
        Log.e(DEBUG_TAG, "CHART ID=" + j[0].replace("\'", ""));
        return j[0].replace("\'", "");
    }

    private void initView(View root){
        Log.e(DEBUG_TAG, "initView called");
        setTitle();
        llNoItemsView = root.findViewById(R.id.no_items_view);
        txtNoDataMsg =root.findViewById(R.id.txt_msg);
        dbManager = new DatabaseManager(getActivity());
        dbManager.open();
        setRecyclerView(root);
    }

    private void setRecyclerView(View root){
        mRecyclerView = root.findViewById(R.id.recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(false);
    }

    private void callAdapter() {
        if (mdashboardList.isEmpty()) {
            llNoItemsView.setVisibility(View.VISIBLE);
        } else {
            llNoItemsView.setVisibility(View.GONE);
            mChartAdapter = new ChartTitleAdapter(getActivity(), mdashboardList);
            mRecyclerView.setAdapter(mChartAdapter);
        }
        hideProgressDialog();
        Log.e(DEBUG_TAG, "callAdapter mChartList Size = " + mdashboardList.size());
    }
}
