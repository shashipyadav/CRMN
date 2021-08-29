package com.example.myapplication.presells;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ProjectAdapter;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.model.ProjectModel;
import com.example.myapplication.network.VolleyResponseListener;
import com.example.myapplication.network.VolleyUtils;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.function.CheckHideShowHelper.DEBUG_TAG;

public class PresellFragment extends Fragment {
    private SharedPrefManager prefManager;


    private Spinner spacer_project;
    private Spinner spacer_sub_project;
    private List<String> projectStringList =  new ArrayList<String>();
    private List<String> subprojectStringList =  new ArrayList<String>();
    private String selectedProject;
    private String selectedsubProject;
    private RecyclerView rv_availbti,rv_buldig;
    private List<BuildingBottomStatusModel> buildingBottomStatusModelList = new ArrayList<BuildingBottomStatusModel>();;;
    private List<RootModel> rootModelList = new ArrayList<RootModel>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view = inflater.inflate(R.layout.fragment_presele_chart, container, false);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
           String title = bundle.getString(Constant.EXTRA_TITLE);
            if (getActivity() != null) {
                getActivity().setTitle(title);
            }


        }


        spacer_project = view.findViewById(R.id.spacer_project);
        spacer_sub_project = view.findViewById(R.id.spacer_sub_project);
        rv_availbti= view.findViewById(R.id.rv_availbti);
        rv_buldig= view.findViewById(R.id.rv_buldig);
        prefManager = new SharedPrefManager(getActivity());
        spacer_project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedProject = projectStringList.get(position);
                getSubPrject(selectedProject);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spacer_sub_project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedsubProject = subprojectStringList.get(position);
               // getList(selectedProject,selectedsubProject);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent =   new Intent(getActivity(),BuldingAtivity.class);
        intent.putExtra("projectName",selectedProject);
        intent.putExtra("subprojectName",selectedsubProject);
        startActivity(intent);
    }
});

        setList();

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


        Avilabiltiy_status_Adapter projectAdapter = new Avilabiltiy_status_Adapter(getActivity(), buildingBottomStatusModelList);
        rv_availbti.setAdapter(projectAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_availbti.setLayoutManager(linearLayoutManager);
        return view;
    }



    private void getSubPrject(String selectedProject)   {
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            subprojectStringList.clear();
            ProgressDialog  mWaiting = ProgressDialog.show(getActivity(), "", "Loading ...", false);


            //https://16.strategicerpcloud.com/strategicerp/pages/graphicalsaleschartnew.jsp?action=filter&filter=project&userid=406&fullusername=Mukesh%20Kulal&username=mukesh&check=realestatemedium&token=null
           // String URL_GET= prefManager.getClientServerUrl()+"/pages/sqls/customerappsqls.jsp?action=projectlist&token="+  prefManager.getAuthToken()+"&cloudcode="+ prefManager.getCloudCode();
            String URL_GET = prefManager.getClientServerUrl()+"pages/graphicalsaleschartnew.jsp?action=filter&filter=subproject&projectname="+selectedProject+"&userid="+prefManager.getKeyUserId()+"&fullusername="+prefManager.getUserName()+"&username="+prefManager.getKeyUserLogin()+"&check="+prefManager.getCloudCode()+"&token="+ prefManager.getAuthToken();
            https://16.strategicerpcloud.com/strategicerp/pages/graphicalsaleschartnew.jsp?action=filter&filter=subproject&projectname=Embassy%20Habitat&userid=406&fullusername=Mukesh%20Kulal&username=mukesh&check=realestatemedium&token=null
           /* String URL_GET = String.format(Constant.URL_SINGLE_CHART_ID,
                    prefManager.getClientServerUrl(),
                    VFORM_ID,
                    prefManager.getAuthToken(),
                    prefManager.getCloudCode());*/

            Log.e(DEBUG_TAG, "callGetChartIdApi url = " + URL_GET);

            VolleyUtils.GET_METHOD(getActivity(), URL_GET, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    System.out.println("Error" + message);
                    mWaiting.dismiss();


                }

                @Override
                public void onResponse(Object response) {
                    Log.e("SUCCESS" , (String) response);
                    mWaiting.dismiss();

                    try {
                        JSONArray jsonArray = new JSONArray((String) response);
                       // "ID":"349","PROJECTNAME":"Aakash Asters"

                        for(int y =0; y<jsonArray.length(); y++) {

                            JSONObject jObj = jsonArray.getJSONObject(y);
                            if(jObj.has("SUBPROJECTNAME")){
                                subprojectStringList.add(jObj.getString("SUBPROJECTNAME"));
                            }
                        }
                        ArrayAdapter mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, subprojectStringList) {

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView item = (TextView) super.getView(position, convertView, parent);

                                //  item.setTypeface(custom_font);

                                // item.setTextColor(Color.parseColor("#909090"));
                                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);


                                return item;
                            }
                        };

                        spacer_sub_project.setAdapter(mAdapter);


                    }catch (Exception e ){
                        e.printStackTrace();


                    }
                }
            });
        }else{
            ToastUtil.showToastMessage("Please check your internet connection and try again",getActivity());
        }
    }
    private void setList() {
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            ProgressDialog  mWaiting = ProgressDialog.show(getActivity(), "", "Loading ...", false);

            //https://16.strategicerpcloud.com/strategicerp/pages/graphicalsaleschartnew.jsp?action=filter&filter=project&userid=406&fullusername=Mukesh%20Kulal&username=mukesh&check=realestatemedium&token=null
           // String URL_GET= prefManager.getClientServerUrl()+"/pages/sqls/customerappsqls.jsp?action=projectlist&token="+  prefManager.getAuthToken()+"&cloudcode="+ prefManager.getCloudCode();
            String URL_GET = prefManager.getClientServerUrl()+"pages/graphicalsaleschartnew.jsp?action=filter&filter=project&userid="+ prefManager.getKeyUserId()+"&fullusername="+prefManager.getUserName()+"&username="+prefManager.getKeyUserLogin()+"&check="+prefManager.getCloudCode()+"&token="+ prefManager.getAuthToken();

           /* String URL_GET = String.format(Constant.URL_SINGLE_CHART_ID,
                    prefManager.getClientServerUrl(),
                    VFORM_ID,
                    prefManager.getAuthToken(),
                    prefManager.getCloudCode());*/

            Log.e(DEBUG_TAG, "callGetChartIdApi url = " + URL_GET);

            VolleyUtils.GET_METHOD(getActivity(), URL_GET, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    System.out.println("Error" + message);
                    mWaiting.dismiss();

                }

                @Override
                public void onResponse(Object response) {
                    Log.e("SUCCESS" , (String) response);
                    mWaiting.dismiss();

                    try {
                        JSONArray jsonArray = new JSONArray((String) response);
                       // "ID":"349","PROJECTNAME":"Aakash Asters"

                        for(int y =0; y<jsonArray.length(); y++) {

                            ProjectModel projectModel = new ProjectModel();
                            JSONObject jObj = jsonArray.getJSONObject(y);
                            if(jObj.has("PROJECTNAME")){
                                projectStringList.add(jObj.getString("PROJECTNAME"));
                            }
                        }
                        ArrayAdapter mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, projectStringList) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView item = (TextView) super.getView(position, convertView, parent);

                                //  item.setTypeface(custom_font);

                                // item.setTextColor(Color.parseColor("#909090"));
                                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);


                                return item;
                            }
                        };

                        spacer_project.setAdapter(mAdapter);


                    }catch (Exception e ){
                        e.printStackTrace();


                    }
                }
            });
        }else{
            ToastUtil.showToastMessage("Please check your internet connection and try again",getActivity());
        }
    }
    private void getList(String projectName, String subprojectName) {
        if (NetworkUtil.isNetworkOnline(getActivity())) {
            ProgressDialog mWaiting = ProgressDialog.show(getActivity(), "", "Loading ...", false);

            /* String URL_GET = String.format(Constant.URL_SINGLE_CHART_ID,
                    prefManager.getClientServerUrl(),
                    VFORM_ID,
                    prefManager.getAuthToken(),
                    prefManager.getCloudCode());*/
            String URL_GET = prefManager.getClientServerUrl()+"pages/graphicalsaleschartnew.jsp?action=listjson&list=boxes&project="+projectName+"&subproject=%27"+subprojectName+"%27&viewonly=true&userid="+ prefManager.getKeyUserId()+"&fullusername="+prefManager.getUserName()+"&username="+prefManager.getKeyUserLogin()+"&cloudcode="+prefManager.getCloudCode()+"&token=" + prefManager.getAuthToken();

            Log.e(DEBUG_TAG, "callGetChartIdApi url = " + URL_GET);

            VolleyUtils.GET_METHOD(getActivity(), URL_GET, new VolleyResponseListener() {
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
                        Buliding_Adapter_Adapter projectAdapter = new Buliding_Adapter_Adapter(getActivity(),rootModelList);
                        rv_buldig.setAdapter(projectAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_buldig.setLayoutManager(linearLayoutManager);

                    } catch (Exception e) {
                        e.printStackTrace();


                    }
                }
            });
        } else {
            ToastUtil.showToastMessage("Please check your internet connection and try again", getActivity());
        }
    }


}
