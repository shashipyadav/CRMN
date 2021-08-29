package com.example.myapplication.user_interface.dlist.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseManager;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.user_interface.dlist.controller.DListRecyclerAdapter;
import com.example.myapplication.user_interface.dlist.controller.DListFieldHelper;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.model.DlistRowItem;
import com.example.myapplication.user_interface.forms.controller.helper.FetchRecordHelper;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.model.Form;
import com.example.myapplication.user_interface.forms.model.Json;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.Constant;
import com.example.myapplication.util.PixelUtil;
import com.example.myapplication.util.ToastUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DlistFragment extends Fragment implements View.OnClickListener,
        DListRecyclerAdapter.CheckBoxListener {

    private final String DEBUG_TAG = DlistFragment.class.getSimpleName();
    private LinearLayout mLinearLayoutHeader;
    public  List<DlistRowItem> dlistFieldValues;
    private RecyclerView recyclerviewForm;
    private DListRecyclerAdapter adapter;
    private FloatingActionButton fabAdd;
    private LinearLayout llNoItemsView;
    private ActionMode actionMode;
    private Button btnAddDlist;
    private DatabaseManager dbManager;
    private SharedPrefManager prefManager;
    private int dlistButtonPosition = -1;
    private int dlistItemPosition =  -1;
    public static String title = "";
    private int fieldPosition = -1;
    private String fieldId = "";
    private   Gson gson;

    public DlistFragment() {
        // Required empty public constructor
    }

    public static DlistFragment newInstance(int dlistButtonPosition, String fieldId) {

        DlistFragment fragment = new DlistFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.EXTRA_DLIST_BUTTON_POSITION, dlistButtonPosition);
        bundle.putString(Constant.EXTRA_FIELD_ID, fieldId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDestroy() {
        dlistFieldValues = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dlist, container, false);
        if (getArguments() != null) {
            fieldId = getArguments().getString(Constant.EXTRA_FIELD_ID);
            dlistButtonPosition = getArguments().getInt(Constant.EXTRA_DLIST_BUTTON_POSITION,-1);
            Log.i(DEBUG_TAG,"Displaying rows for fieldId ----> " + fieldId);
        }
        fieldPosition = FormFragment.dlistArrayPosition;
        initViews(root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(View root)
    {
        initDatabase();
        gson = new Gson();
        fabAdd = root.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);
        mLinearLayoutHeader = root.findViewById(R.id.linearlayout_header);
        btnAddDlist = root.findViewById(R.id.button_add_dlist);
        btnAddDlist.setOnClickListener(this);
        llNoItemsView = root.findViewById(R.id.no_items_view);
        recyclerviewForm = root.findViewById(R.id.recylerview_form);
        recyclerviewForm.setLayoutManager(new LinearLayoutManager(getActivity()));
        prefManager = new SharedPrefManager(getActivity());
    }

    private void initDlistHeader() {
        try {
            if (mLinearLayoutHeader != null) {
                mLinearLayoutHeader.removeAllViews();
            }
            if (dlistButtonPosition != -1) {
                String fieldId = FormFragment.fieldsList.get(fieldPosition)
                        .getdListArray().get(dlistButtonPosition).getId();

                if (dlistButtonPosition != -1) {
                    FetchRecordHelper fh = new FetchRecordHelper(getActivity(),
                            null);
                    fh.setFieldsList(FormFragment.fieldsList);
                    fh.setDlistArrayPosition(FormFragment.dlistArrayPosition);
                    fh.setAdditionalFieldDataList(FormFragment.additionalFieldDataList);

                    List<DList> dlistFields = fh.getDListFieldObjectArray(fieldId,false);

                    if (dlistFields != null) {
                        for (int i = 0; i < dlistFields.size(); i++) {
                            DList dobj = dlistFields.get(i);

                            String fieldName = dobj.getFieldName();
                            //to support all versions use
                            Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.montserrat_bold);
                            TextView tv = new TextView(getActivity());
                            //    if(!fieldName.isEmpty()){
                            if (!dobj.isHidden()) {
                                tv.setText(fieldName);
                                tv.setPadding(20, 20, 20, 20);
                                tv.setTextSize(18);
                                tv.setTypeface(typeface);
                                tv.setWidth(PixelUtil.convertDpToPixel(getActivity(), 150f));
                                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                                tv.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                tv.setGravity(Gravity.CENTER);
                                tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT));
                                mLinearLayoutHeader.addView(tv);
                            }
                        }
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadDListRows()
    {
        try {
            if (dlistButtonPosition != -1) {
                dlistFieldValues = new ArrayList<>();
                List<String> dlistRowsJson = dbManager.fetchDlistJson(fieldId);
                for (int i = 0; i < dlistRowsJson.size(); i++) {
                    try {
                        JSONObject jsonObject = new JSONObject(dlistRowsJson.get(i));
                        DlistRowItem ditem = new DlistRowItem(jsonObject.getJSONArray("dlistArray"),jsonObject.getBoolean("mIsSelected"));
                        dlistFieldValues.add(ditem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!dlistFieldValues.isEmpty()) {
                    llNoItemsView.setVisibility(View.GONE);
                    adapter = new DListRecyclerAdapter(getActivity(),
                            dlistFieldValues,
                            fieldId,// e.g  Configuration Parameter
                            dlistButtonPosition,  //dlistButtonArrayPosition is the position of the dlistButtonArray in the main FieldList
                            dlistItemPosition,  //dlistItemPosition is the position of a dlistItem inside the dlistButtonArray
                            this,
                            false);
                    recyclerviewForm.setAdapter(adapter);
                } else {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    llNoItemsView.setVisibility(View.VISIBLE);
                }
            } else {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                llNoItemsView.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        dbManager = new DatabaseManager(getActivity());
        dbManager.open();
    }

    @Override
    public void onResume() {
        super.onResume();
        initDlistHeader();
        loadDListRows();
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public String checkDeleteCheckInField() {
        String result = "";
        if(fieldPosition != -1) {
            Field field = FormFragment.fieldsList.get(fieldPosition);
            for(int i=0; i < field.getdListArray().size(); i++){

                Field dlist = field.getdListArray().get(i);
                if(dlist.getId().equals(fieldId)){
                    if(dlist.getDefaultValue().toLowerCase().contains("deletecheck")){
                        result = dlist.getDefaultValue();
                       break;
                    }
                }
            }
        }
        return result;
    }

    public boolean doStopDelete(int srNo) {


        try{
            String deleteCheckField = checkDeleteCheckInField();
            if(!deleteCheckField.isEmpty()) {

                String arr[] = deleteCheckField.toLowerCase().split("deletecheck");
                deleteCheckField = arr[1];

                if(dbManager != null) {
                    String json =   dbManager.fetchFormJsonBySrNo(fieldId,srNo);
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("dlistArray");
                    for(int i=0; i <jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String fieldId = jsonObject.getString("mId");

                        if(fieldId.contains(deleteCheckField)) {
                            if(jsonObject.getString("mValue").equals("1")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if(v == fabAdd){
            int lastInsertedPosition = createNewDList();
         //   Log.e( DEBUG_TAG ,"lastInsertedPosition = " + lastInsertedPosition);

            Bundle bundle = new Bundle();
            Intent intent = new Intent(getActivity(), DListFormActivity.class);
            bundle.putString(Constant.EXTRA_FIELD_ID,fieldId);
            bundle.putSerializable(Constant.EXTRA_TITLE, title); // Field object which has the dlist object
            bundle.putInt(Constant.EXTRA_DLIST_BUTTON_POSITION, dlistButtonPosition); // position of dlist button array list in initial field list array
            bundle.putInt(Constant.EXTRA_DLIST_ROW_POSITION,lastInsertedPosition); // position of dlistfield row in list array
            bundle.putString(Constant.EXTRA_MODE,Constant.EXTRA_CREATE_NEW);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private int getTotalNoOfRows(){
        return dbManager.getDlistRowsCount(fieldId);
    }

    private int createNewDList(){
            List<DList> dlistFields = FormFragment.fieldsList.get(fieldPosition).getdListArray().get(dlistButtonPosition).getdListsFields();
            int dlistValuesArraySize = getTotalNoOfRows();

            Log.e(DEBUG_TAG, "createNewDList1 line# 526  dlistValuesArraySize = " + dlistValuesArraySize);
            List<DList> newDlistField = new ArrayList<>();
            for (int i = 0; i < dlistFields.size(); i++) {
                DList dlist = dlistFields.get(i);
                String[] jarr = dlist.getId().split("_");

                DList object = new DList();
                object.setDropDownClick(dlist.getDropDownClick());
                object.setSearchRequired(dlist.getSearchRequired());
                object.setSaveRequired(dlist.getSaveRequired());
                object.setReadOnly(dlist.getReadOnly());
                object.setSrNo(dlist.getSrNo());
                object.setOptionsArrayList(dlist.getOptionsArrayList());
                object.setFieldName(dlist.getFieldName());
                object.setAddFunction(dlist.getAddFunction());
                object.setOnKeyDown(dlist.getOnKeyDown());
                object.setType(dlist.getType());
                object.setDefaultValue(dlist.getDefaultValue());

                if(dlistValuesArraySize == 0 ){
                    object.setName(jarr[0] + "_1");
                    object.setId(jarr[0] +"_1");
                    if(dlist.getFieldName().toLowerCase().matches("sr. no|sr|sr no")){
                        object.setValue("1");
                    }else{
                      /*  if(dlist.getFieldName().toLowerCase().equals("per unit qty")){
                            object.setValue("1");
                        }else{

                            object.setValue(dlist.getValue());
                        } */

                        String value = dlist.getValue();

//                        if(value.equals("0")){
//                            value = "";
//                        }

                        object.setValue(value);
                    }
                }else{
                    int pos = dlistValuesArraySize+1;
                    object.setName(jarr[0] +"_" +pos);
                    object.setId(jarr[0] +"_"+ pos);
                    if(dlist.getFieldName().toLowerCase().matches("sr. no|sr|sr no")){
                        object.setValue(String.valueOf(pos));
                    }else{
                     /*   if(dlist.getFieldName().toLowerCase().equals("per unit qty")){
                            object.setValue("1");
                        }else{

                            object.setValue(dlist.getValue());
                        } */
                        String value = dlist.getValue();
//                        if(value.equals("0")){
//                            value = "";
//                        }
                        object.setValue(value);
                    }
                }
                object.setFieldType(dlist.getFieldType());
                newDlistField.add(object);
            }

        SharedPrefManager prefManager = new SharedPrefManager(getActivity());
        String formId = prefManager.getFormId();
        DListItem dListItem = new DListItem(newDlistField);
        String dlistJson = gson.toJson(dListItem);

        int previousTotal = getTotalNoOfRows();
        dbManager.insertDListRow(Integer.parseInt(formId),title,fieldId,"",dlistJson,previousTotal+1);
        int newTotalRowCount =  getTotalNoOfRows();

        DListFieldHelper fieldHelper = new DListFieldHelper(getActivity());
        fieldHelper.updateContentRows(fieldId, newTotalRowCount);

        return newTotalRowCount;
    }

    private void  updateContentRows() {
        int dlistRowsCount = getTotalNoOfRows();
        DListFieldHelper fieldHelper = new DListFieldHelper(getActivity());
        fieldHelper.updateContentRows(fieldId, dlistRowsCount);
    }

    @Override
    public void onCheckBoxClicked(int position) {
        Log.e(DEBUG_TAG, "checkbox clicked position = " + position);

        int srNo = position + 1;
        if (doStopDelete(srNo)) {

            if(dlistFieldValues != null) {
                if (dlistFieldValues.get(position).isSelected()) {
                    dlistFieldValues.get(position).setSelected(false);
                }
                adapter.notifyDataSetChanged();
            }

            ToastUtil.showToastMessage("Delete Not Allowed", getActivity());
            adapter.toggleSelection(position);
        }else{
            if(dlistFieldValues != null) {
                if (dlistFieldValues.get(position).isSelected()) {
                    dlistFieldValues.get(position).setSelected(false);
                } else {
                    dlistFieldValues.get(position).setSelected(true);
                }
                adapter.notifyDataSetChanged();
                if (actionMode == null) {
                    ActionModeCallback actionModeCallback = new ActionModeCallback();
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
                }
                toggleSelection(position);
            }
        }
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
            actionMode = null;
        } else {
            String title = getString(
                    R.string.selected_count,
                    adapter.getSelectedItemCount());
            actionMode.setTitle(title);
            actionMode.invalidate();
        }
    }

    class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Delete Entries")
                            .setMessage("Are you sure your wish to remove the entries?")
                      //    .setIcon(R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            List<Integer> selectedItemPositions = adapter.getSelectedItems();
                                            int currPos;
                                            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                                                currPos = selectedItemPositions.get(i);
                                                adapter.removeData(currPos);
                                                int srNo = currPos + 1;

                                                removeItemFromDatabase(srNo);
                                            }
                                            actionMode.finish();
                                            Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton(android.R.string.no,
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    actionMode.finish();
                                    adapter.clearSelection();
                                }
                            }).show();
                    return true;
                default:
                    return false;
            }
        }



        public void removeItemFromDatabase(int position) {
            try {
                if(dbManager != null){
                    dbManager.deleteDlistRowWithSrNo(fieldId,position);
                    updateContentRows();
                    updateSrNoInDList();
                    loadDListRows();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;
        }
    }

    @Override
    public void onDestroyView() {
        Log.e(DEBUG_TAG, "onDestroyView called");
        mLinearLayoutHeader = null;
        adapter = null;
        actionMode= null;
        dlistFieldValues = null;
        super.onDestroyView();
    }

    private void updateSrNoInDList(){
        List<DListItem> dlistFieldArrayList = new ArrayList<>();
        List<String> dlistRowsJson = dbManager.fetchDlistJson(fieldId);
        for (int i = 0; i < dlistRowsJson.size(); i++) {
            try {
                DListItem ditem = gson.fromJson(dlistRowsJson.get(i), DListItem.class);
                dlistFieldArrayList.add(ditem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        dbManager.deleteDList();

        for( int i = 0; i <dlistFieldArrayList.size(); i++) {
            DListItem item = dlistFieldArrayList.get(i);
            List<DList> dlistArray = item.getDlistArray();
            int pos = i+1;
            for(int j =0 ;j<dlistArray.size(); j++){
                DList dObj = dlistArray.get(j);

                String[] jarr = dObj.getId().split("_");
                dObj.setName(jarr[0] +"_" +pos);
                dObj.setId(jarr[0] +"_"+ pos);
                if(dObj.getFieldName().toLowerCase().matches("sr. no|sr|sr no")){
                    dObj.setValue(String.valueOf(pos));
                }
            }
            DListItem dListItem = new DListItem(dlistArray);
            String dlistJson = gson.toJson(dListItem);
            dbManager.insertDListRow(Integer.parseInt(prefManager.getFormId()),title,fieldId,"",dlistJson,pos);
        }
    }



}