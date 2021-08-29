package com.example.myapplication.user_interface.forms.model;


import android.content.Context;
import android.util.Log;

import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.user_interface.dlist.model.DListItem;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Field implements Serializable {

    private String watermark            = "";
    private String showfieldname        = "";
    private String webSaveRequired      = "";
    private String saveRequired         = "";
    private String jfunction            = "";
    private String onChange             = "";
    private String onKeyDown            = "";
    private String defaultValue         = "";
    private String type                 = "";
    private String sqlValue             = "";
    private String field_name           = "";
    private String relation             = "";
    private String states               = "";
    private String relationField        = "";
    private String onClickVList         = "";
    private String jIdList              = "";
    private String name                 = "";
    private String width                = "";
    private String searchRequired       = "";
    private String id                   = "";
    private String placeholder          = "";
    private String value                = "";
    private String fieldType            = "";
    private String onclicksummary       = "";
    private String newRecord            = "";
    private String options              = "";
    private String vlistRelationIds     = "";
    private String vlistDefaultIds      = "";
    private boolean mShowErrorMessage   = false;
    private boolean isClearTriggered    = false;

    private String errorMessage = "";
    private boolean mIsSelected          = false;
    private List<Field> dListArray          = new ArrayList<>();
    private List<DList> dListsFields        = new ArrayList<>(); //using this to display dlist header
   // private List<String> optionsArrayList   = new ArrayList<>();
    private List<OptionModel> optionsArrayList = new ArrayList<>();
    private String onclickrightbutton       = "";
    private String field_type = ""; // added this property as we have to make fields readonly if field_type = dlistsum1 or dlistsum
    private List<DListItem> mDListItemList = new ArrayList<>(); // this is the dlistfield values

    public Field(){

       // this("","","","","");
    }

    public Field(String showfieldname, String name, String id, String type, String value){
        this.showfieldname = showfieldname;
        this.name = name;
        this.id = id;
        this.type = type;
        this.value = value;

//        this("", showfieldname, "", "",
//                "", "","", type, "",
//                "","", "", "",
//               "", "", name,
//                "",id, "", value,
//                "","","",null,
//               null,"");
    }

    //field object for Main Form
    public Field(String watermark,
                 String showfieldname,
                 String saveRequired,
                 String jfunction,
                 String onChange,
                 String onKeyDown,
                 String defaultValue,
                 String type,
                 String sqlValue,
                 String field_name,
                 String relation,
                 String states,
                 String relationField,
                 String onClickVList,
                 String jIdList,
                 String name,
                 String searchRequired,
                 String id,
                 String placeholder,
                 String value,
                 String fieldType,
                 String onclicksummary,
                 String newRecord,
                 List<Field> dListArray,
                 List<OptionModel> mOptionsArrayList,
                 String onclickrightbutton,
                 String webSaveRequired,
                 String field_type) {

        this.watermark = watermark;
        this.showfieldname = showfieldname;
        this.saveRequired = saveRequired;
        this.jfunction = jfunction;
        this.onChange = onChange;
        this.onKeyDown = onKeyDown;
        this.defaultValue = defaultValue;
        this.type = type;
        this.sqlValue = sqlValue;
        this.field_name = field_name;
        this.relation = relation;
        this.states = states;
        this.relationField = relationField;
        this.onClickVList = onClickVList;
        this.jIdList = jIdList;
        this.name = name;
        this.searchRequired = searchRequired;
        this.id = id;
        this.placeholder = placeholder;
        this.value = value;
        this.fieldType = fieldType;
        this.onclicksummary = onclicksummary;
        this.newRecord = newRecord;
        this.dListArray = dListArray;
        this.optionsArrayList = mOptionsArrayList;
        this.onclickrightbutton = onclickrightbutton;
        this.webSaveRequired = webSaveRequired;
        this.field_type = field_type;

//        this( watermark, showfieldname,  saveRequired,  jfunction,
//                 onChange, onKeyDown,  defaultValue,  type,  sqlValue,
//                 field_name,  relation,  states,  relationField,
//                 onClickVList,  jIdList,  name,  "",
//                 searchRequired,  id,  placeholder,  value,
//                 fieldType, onclicksummary, newRecord, "",
//                dListArray,mOptionsArrayList,onclickrightbutton);
    }

    //for dlist fields
    public Field(String watermark,
                 String showfieldname,
                 String saveRequired,
                 String jfunction,
                 String onChange,
                 String onKeyDown,
                 String defaultValue,
                 String type,
                 String sqlValue,
                 String field_name,
                 String relation,
                 String states,
                 String relationField,
                 String onClickVList,
                 String jIdList,
                 String name,
                 String width,
                 String searchRequired,
                 String id,
                 String placeholder,
                 String value,
                 String fieldType,
                 String onclicksummary,
                 String newRecord,
                 String options,
                 List<DList> mdListsFields,
                 List<OptionModel> mOptionsArrayList,
                 String onclickrightbutton,
                 String webSaveRequired,
                 String field_type,
                 String vlistRelationIds,
                 String vlistDefaultIds) {

        this.watermark = watermark;
        this.showfieldname = showfieldname;
        this.saveRequired = saveRequired;
        this.jfunction = jfunction;
        this.onChange = onChange;
        this.onKeyDown = onKeyDown;
        this.defaultValue = defaultValue;
        this.type = type;
        this.sqlValue = sqlValue;
        this.field_name = field_name;
        this.relation = relation;
        this.states = states;
        this.relationField = relationField;
        this.onClickVList = onClickVList;
        this.jIdList = jIdList;
        this.name = name;
        this.width = width;
        this.searchRequired = searchRequired;
        this.id = id;
        this.placeholder = placeholder;
        this.value = value;
        this.fieldType = fieldType;
        this.onclicksummary = onclicksummary;
        this.newRecord = newRecord;
        this.dListsFields = mdListsFields;
        this.options = options;
        this.optionsArrayList = mOptionsArrayList;
        this.onclickrightbutton = onclickrightbutton;
        this.webSaveRequired = webSaveRequired;
        this.field_type = field_type;
        this.vlistRelationIds = vlistRelationIds;
        this.vlistDefaultIds = vlistDefaultIds;
    }

    public String getFieldName(){
        String fieldName = "";
        if(!getShowfieldname().equals("")) {
            fieldName = getShowfieldname();
        } else {
            fieldName = getField_name();

        }
        if(getDataType().toLowerCase().equals("tag")){
            fieldName = StringUtil.getTagTitle(fieldName);
        }
        return fieldName;
    }

    public String getDataType() {
        setHiddenOrReadOnly();

        String fieldType = "";
        if (isHidden()) {
            fieldType = getFieldType();
        } else {
            fieldType = getType();
        }
        return fieldType;
    }

    public String getShowfieldname() {
        return showfieldname;
    }

    public void setShowfieldname(String showfieldname) {
        this.showfieldname = showfieldname;
    }

    public String getSaveRequired() {
        return saveRequired;
    }

    public boolean isReadOnly() {

        if (getSaveRequired().equalsIgnoreCase("read")||
                getWebSaveRequired().equalsIgnoreCase("read")||
                 getField_type().toLowerCase().matches("dlistsum|dlistsum1")) {
            return true;
        }
        return false;
    }

    public boolean isHidden(){
        if (getType().toLowerCase().equals("hidden")) {
            return true;
        }
        return false;
    }

    public void setHiddenOrReadOnly() {
        //applicable only for view and vfunction
        if(getField_type().toLowerCase().matches("view|vfunction")) {
            if(!getType().toLowerCase().matches("hidden")){
                setSaveRequired("read");
            }
        }
    }
    public String getWebSaveRequired() {
        return webSaveRequired;
    }

    public void setSaveRequired(String saveRequired) {
        this.saveRequired = saveRequired;
    }

    public String getJfunction() {
        return jfunction;
    }

    public void setJfunction(String jfunction) {
        this.jfunction = jfunction;
    }

    public String getOnChange() {
        return onChange;
    }

    public void setOnChange(String onChange) {
        this.onChange = onChange;
    }

    public String getDefaultValue() {
        //added this as it was showing {self} for Responsible User
 //      return containsAny(defaultValue);

        return defaultValue;
    }

    public String getFinalValue(Context context){
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);

        if(!defaultValue.equals("")){
            String value = !getValue().isEmpty() ? getValue() : getDefaultValue();
            if(value.toLowerCase().matches("\\$\\{self\\}|\\$\\{username\\}|\\$\\{login\\}")){
                value = sharedPrefManager.getUserName();
                return value;
            }else if(defaultValue.matches("0.0|0|0.00")){
                return value;
            }else{
                return value;
            }
        }
        return "";
    }

    public String containsAny(String str)
    {
        //  boolean bResult=false; // will be set, if any of the words are found
        String[] words = {"EDFFPOPUP","SQLTRUE", "VLIST", "SCROLL", "NEXT","FUNCTIONDFF","DFFPOPUP","FUNCTION" ,"SQL","DFF","SQLFUNCTION"};

        List<String> list = Arrays.asList(words);
        for (String word: list ) {
            //  boolean bFound = str.matches(word);
            boolean bFound = str.contains(word);
            if (bFound) {
                //    bResult=bFound;
                str = str.replace(word, "");
                str = str.replaceAll("[\\$\\{\\}]","");
                str = str.replace("SQL",""); // todo : solve this sql remain after removing
                //   Log.e(DEBUG_TAG, "DEFAULT STRING = "+ str +" WORD =" +word);
                break;
            }
        }
        return str;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSqlValue() {
        return sqlValue;
    }

    public void setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getRelationField() {
        return relationField;
    }

    public void setRelationField(String relationField) {
        this.relationField = relationField;
    }

    public String getOnClickVList() {
        return onClickVList;
    }

    public void setOnClickVList(String onClickVList) {
        this.onClickVList = onClickVList;
    }

    public String getjIdList() {
        return jIdList;
    }

    public void setjIdList(String jIdList) {
        this.jIdList = jIdList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getSearchRequired() {
        return searchRequired;
    }

    public void setSearchRequired(String searchRequired) {
        this.searchRequired = searchRequired;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getValue() {
//        String mValue = value;
//        try{
//            if(getFieldType().toLowerCase().equals("double")){
//                if(!mValue.isEmpty()){
//                    double d = Double.parseDouble(mValue);
//                    mValue = new DecimalFormat("0.000").format(d);
//                }
//            }else{
//                mValue =  value;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.e("getValue = ","value = " + mValue);
//            mValue = value;
//        }
//        return mValue;

        if(getDataType().toLowerCase().matches("checkbox|boolean")){
            if(value.isEmpty()){
                value = "false";
            }
        }

        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getOnclicksummary() {
        return onclicksummary;
    }

    public void setOnclicksummary(String onclicksummary) {
        this.onclicksummary = onclicksummary;
    }

    public String getNewRecord() {
        return newRecord;
    }

    public void setNewRecord(String newRecord) {
        this.newRecord = newRecord;
    }

    public List<DList> getdListsFields() {
        return dListsFields;
    }

    public void setdListsFields(List<DList> dListsFields) {
        this.dListsFields = dListsFields;
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public List<Field> getdListArray() {
        return dListArray;
    }

    public void setdListArray(List<Field> dListArray) {
        this.dListArray = dListArray;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public List<OptionModel> getOptionsArrayList() {
        return optionsArrayList;
    }

    public void setOptionsArrayList(List<OptionModel> optionsArrayList) {
        this.optionsArrayList = optionsArrayList;
    }

    public String getOnKeyDown() {
        return onKeyDown;
    }

    public void setOnKeyDown(String onKeyDown) {
        this.onKeyDown = onKeyDown;
    }

    public String getOnclickrightbutton() {
        return onclickrightbutton;
    }

    public void setOnclickrightbutton(String onclickrightbutton) {
        this.onclickrightbutton = onclickrightbutton;
    }

    public boolean showErrorMessage() {
        return mShowErrorMessage;
    }

    public void setShowErrorMessage(boolean showErrorMessage) {
        this.mShowErrorMessage = showErrorMessage;
    }

    public boolean getIsSelected(){
        return mIsSelected;
    }

    public void setIsSelected(boolean isSelected){
        this.mIsSelected = isSelected;
    }

    public List<DListItem> getDListItemList() {
        return mDListItemList;
    }

    public void setDListItemList(List<DListItem> DListItemList) {
        mDListItemList = DListItemList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setWebSaveRequired(String webSaveRequired) {
        this.webSaveRequired = webSaveRequired;
    }

    public void setClearTriggered(boolean clearTriggered) {
        isClearTriggered = clearTriggered;
    }

    public boolean isClearTriggered() {
        return isClearTriggered;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public String getVlistRelationIds() {
        return vlistRelationIds;
    }

    public void setVlistRelationIds(String vlistRelationIds) {
        this.vlistRelationIds = vlistRelationIds;
    }

    public String getVlistDefaultIds() {
        return vlistDefaultIds;
    }

    public void setVlistDefaultIds(String vlistDefaultIds) {
        this.vlistDefaultIds = vlistDefaultIds;
    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Field field = (Field) o;
//        return id.equals(field.id);
////        return showfieldname.equals(field.showfieldname) &&
////                type.equals(field.type) &&
////                field_name.equals(field.field_name) &&
////                states.equals(field.states) &&
////                name.equals(field.name) &&
////                id.equals(field.id) &&
////                Objects.equals(value, field.value) &&
////                Objects.equals(fieldType, field.fieldType);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
}

