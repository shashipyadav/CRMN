package com.example.myapplication.presells;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ProjectAdapter;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.network.VolleyResponseListener;
import com.example.myapplication.network.VolleyUtils;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.function.CheckHideShowHelper.DEBUG_TAG;


public class BuldingAtivity extends AppCompatActivity {
    private SharedPrefManager prefManager;
    private RecyclerView rv_buldig_title,rv_availbti;
    private List<RootModel> rootModelList = new ArrayList<RootModel>();
    private String projectName;
    private String subprojectName;
    private List<BuildingBottomStatusModel> buildingBottomStatusModelList = new ArrayList<BuildingBottomStatusModel>();;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulding_activity);

        prefManager = new SharedPrefManager(BuldingAtivity.this);
        rv_buldig_title = findViewById(R.id.rv_buldig_title);
        rv_availbti= findViewById(R.id.rv_availbti);

        Intent i = getIntent();
        projectName = i.getStringExtra("projectName");
        subprojectName = i.getStringExtra("subprojectName");
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
                    getSupportActionBar().setTitle("GraphicalSales");


        }
        toolbar.setTitleTextColor(Color.WHITE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });
        getList();

        BuildingBottomStatusModel buildingBottomStatusModel1 = new BuildingBottomStatusModel();
        buildingBottomStatusModel1.setColor("#3fb618");
        buildingBottomStatusModel1.setStatus("Available");

        BuildingBottomStatusModel buildingBottomStatusModel2 = new BuildingBottomStatusModel();
        buildingBottomStatusModel2.setColor("#ff0039");
        buildingBottomStatusModel2.setStatus("Booked");

        BuildingBottomStatusModel buildingBottomStatusModel3 = new BuildingBottomStatusModel();
        buildingBottomStatusModel3.setColor("#d5d80d");
        buildingBottomStatusModel3.setStatus("On Hold");

        BuildingBottomStatusModel buildingBottomStatusModel4 = new BuildingBottomStatusModel();
        buildingBottomStatusModel4.setColor("#9954bb");
        buildingBottomStatusModel4.setStatus(" Not for Sale / Refuge");


        BuildingBottomStatusModel buildingBottomStatusModel5 = new BuildingBottomStatusModel();
        buildingBottomStatusModel5.setColor("#ff00a8");
        buildingBottomStatusModel5.setStatus("Rehab Allocated ");




        BuildingBottomStatusModel buildingBottomStatusModel6 = new BuildingBottomStatusModel();
        buildingBottomStatusModel6.setColor("#0000ff");
        buildingBottomStatusModel6.setStatus("Land Owner ");

        buildingBottomStatusModelList.add(buildingBottomStatusModel1);
        buildingBottomStatusModelList.add(buildingBottomStatusModel2);
        buildingBottomStatusModelList.add(buildingBottomStatusModel3);
        buildingBottomStatusModelList.add(buildingBottomStatusModel4);
        buildingBottomStatusModelList.add(buildingBottomStatusModel5);
        buildingBottomStatusModelList.add(buildingBottomStatusModel6);


        Avilabiltiy_status_Adapter projectAdapter = new Avilabiltiy_status_Adapter(BuldingAtivity.this, buildingBottomStatusModelList);
        rv_availbti.setAdapter(projectAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BuldingAtivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_availbti.setLayoutManager(linearLayoutManager);
    }

    private void getList() {
        if (NetworkUtil.isNetworkOnline(BuldingAtivity.this)) {
            ProgressDialog mWaiting = ProgressDialog.show(BuldingAtivity.this, "", "Loading ...", false);

            /* String URL_GET = String.format(Constant.URL_SINGLE_CHART_ID,
                    prefManager.getClientServerUrl(),
                    VFORM_ID,
                    prefManager.getAuthToken(),
                    prefManager.getCloudCode());*/
            String URL_GET = prefManager.getClientServerUrl()+"pages/graphicalsaleschartnew.jsp?action=listjson&list=boxes&project="+projectName+"&subproject=%27"+subprojectName+"%27&viewonly=true&userid="+ prefManager.getKeyUserId()+"&fullusername="+prefManager.getUserName()+"&username="+prefManager.getKeyUserLogin()+"&cloudcode="+prefManager.getCloudCode()+"&token=" + prefManager.getAuthToken();

            Log.e(DEBUG_TAG, "callGetChartIdApi url = " + URL_GET);

            VolleyUtils.GET_METHOD(BuldingAtivity.this, URL_GET, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    System.out.println("Error" + message);

                    mWaiting.dismiss();
                }

                @Override
                public void onResponse(Object response) {
                    Log.e("SUCCESS", (String) response);
                    mWaiting.dismiss();

                    try {
                        JSONArray jsonArray = new JSONArray((String) response);
                        // "ID":"349","PROJECTNAME":"Aakash Asters"

                        for (int y = 0; y < jsonArray.length(); y++) {
                            RootModel rootModel = new RootModel();

                            JSONObject jObj = jsonArray.getJSONObject(y);
                            if (jObj.has("floor")) {
                                rootModel.setFloor(jObj.getString("floor"));
                            }
                            if (jObj.has("rooms")) {
                                List<RoomModel> rooms = new ArrayList<RoomModel>();
                                JSONArray jsonArray1 = new JSONArray((String) jObj.getString("rooms"));

                                for (int yj = 0; yj < jsonArray1.length(); yj++) {
                                    RoomModel roomModel = new RoomModel();
                                    JSONObject jObj1 = jsonArray1.getJSONObject(yj);

                                    if (jObj1.has("aa")) {
                                        roomModel.setAa(jObj1.getString("aa"));
                                    }
                                    if (jObj1.has("mem_code")) {
                                        roomModel.setMemCode(jObj1.getString("mem_code"));
                                    }
                                    if (jObj1.has("amt")) {
                                        roomModel.setAmt(jObj1.getString("amt"));
                                    }
                                    if (jObj1.has("project")) {
                                        roomModel.setProject(jObj1.getString("project"));
                                    }
                                    if (jObj1.has("unit_type")) {
                                        roomModel.setUnitType(jObj1.getString("unit_type"));
                                    }
                                    if (jObj1.has("room")) {
                                        roomModel.setRoom(jObj1.getString("room"));
                                    }if (jObj1.has("holdby")) {
                                        roomModel.setHoldby(jObj1.getString("holdby"));
                                    }
                                    if (jObj1.has("unit_no111")) {
                                        roomModel.setUnitNo111(jObj1.getString("unit_no111"));
                                    }
                                    if (jObj1.has("sale")) {
                                        roomModel.setSale(Integer.valueOf(jObj1.getString("sale")));
                                    }
                                    if (jObj1.has("mem_name")) {
                                        roomModel.setMemName(jObj1.getString("mem_name"));
                                    }
                                    if (jObj1.has("carpet")) {
                                        roomModel.setCarpet(Integer.valueOf(jObj1.getString("carpet")));
                                    }
                                    if (jObj1.has("status")) {
                                        roomModel.setStatus(jObj1.getString("status"));
                                    }if (jObj1.has("direction")) {
                                        roomModel.setDirection(jObj1.getString("direction"));
                                    }if (jObj1.has("colour")) {
                                        roomModel.setColour(jObj1.getString("colour"));
                                    }
                                    rooms.add(roomModel);;;
                                }
                                rootModel.setRooms(rooms);
                            }
                            rootModelList.add(rootModel);

                        }
                        Buliding_Adapter_Adapter projectAdapter = new Buliding_Adapter_Adapter(BuldingAtivity.this,rootModelList);
                        rv_buldig_title.setAdapter(projectAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BuldingAtivity.this);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_buldig_title.setLayoutManager(linearLayoutManager);

                    } catch (Exception e) {
                        e.printStackTrace();


                    }
                }
            });
        } else {
            ToastUtil.showToastMessage("Please check your internet connection and try again", BuldingAtivity.this);
        }
    }

}