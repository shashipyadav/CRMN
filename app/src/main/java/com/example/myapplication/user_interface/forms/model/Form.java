package com.example.myapplication.user_interface.forms.model;

public class Form {

    private String enctype;
    private String dataTitle;
    private String formMethod;
    private String formID;
    private String id;
    private String formSave;
    private String formSaveCheck;
    private String formSaveCheckNames;

    public Form(){ }

    public Form(String enctype,
                String dataTitle,
                String formMethod,
                String formID,
                String id,
                String formSave,
                String formSaveCheck,
                String formSaveCheckNames)
    {
        this.enctype = enctype;
        this.dataTitle = dataTitle;
        this.formMethod = formMethod;
        this.formID = formID;
        this.id= id;
        this.formSave = formSave;
        this.formSaveCheck = formSaveCheck;
        this.formSaveCheckNames = formSaveCheckNames;
    }

    /*
    *  String formId = jsonObject.getString("form_id");
                String fieldName = jsonObject.getString("field_name");
                boolean hasList = jsonObject.getBoolean("haslist");
                String relation = jsonObject.getString("relation");
                String relationList = jsonObject.getString("relationlist");
                String searchRequired = jsonObject.getString("search_required");
                String saveRequired = jsonObject.getString("save_required");
                String fieldType = jsonObject.getString("field_type");
                String relationField = jsonObject.getString("relationfield");
                String relationId = jsonObject.getString("relationid");
                String default_Value = jsonObject.getString("default_value");
                int parentId = jsonObject.getInt("parent_id");
                String functionValue = jsonObject.getString("function_value");
                String fieldFunction = jsonObject.getString("fieldfunction");
                String fieldCalculated = jsonObject.getString("fieldcalculated");
                String defaultValue = jsonObject.getString("defaultvalue");
                String width = jsonObject.getString("width");
                String states = jsonObject.getString("states");
                String fieldSql = jsonObject.getString("fieldsql");
                String sqlValue = jsonObject.getString("sql_value");
                String alias = jsonObject.getString("alias");
                String showFieldName = jsonObject.getString("showfieldname");
                String formatPattern = jsonObject.getString("format_pattern");
    * */

    private int formId;
    private int fieldId;
    private String fieldName;
    private String hasList;
    private String relation;
    private String relationList;
    private String searchRequired;
    private String saveRequired;
    private String fieldType;
    private String relationField;
    private String relationId;
    private String default_Value;
    private int parentId;
    private String functionValue;
    private String fieldFunction;
    private String fieldCalculated;
    private String defaultValue;
    private int width;
    private String states;
    private String fieldSql;
    private String sqlValue;
    private String alias;
    private String showFieldName;
    private String formatPattern;
    private String formName;
    private String columnName;
    private String showName;
    private String dataType;
    private String extraParam;
    private int numBlocks;


    public Form(int formId,int fieldId, String fieldName,String hasList,String relation,String relationList,String searchRequired,
                String saveRequired,String fieldType,String relationField,String relationId,
                String default_Value,int parentId,String functionValue,String fieldFunction,
                String fieldCalculated,String defaultValue,int width,String states,
                String fieldSql,String sqlValue,String alias,String showFieldName,
                String formatPattern, int numBlocks){

        this.formId = formId;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.hasList = hasList;
        this.relation= relation;
        this.relationList = relationList;
        this.searchRequired = searchRequired;
        this.saveRequired = saveRequired;
        this.fieldType = fieldType;
        this.relationField = relationField;
        this.relationId = relationId;
        this.default_Value = default_Value;
        this.parentId = parentId;
        this.functionValue = functionValue;
        this.fieldFunction = fieldFunction;
        this.fieldCalculated = fieldCalculated;
        this.defaultValue = defaultValue;
        this.width = width;
        this.states = states;
        this.fieldSql = fieldSql;
        this.sqlValue = sqlValue;
        this.alias = alias;
        this.showFieldName = showFieldName;
        this.formatPattern = formatPattern;
        this.numBlocks = numBlocks;
    }

    public Form(int formId, String formName, String columnName,
                String showName, String dataType,
                String extraParam, String defaultValue) {

        this.formId = formId;
        this.formName = formName;
        this.columnName = columnName;
        this.showName = showName;
        this.dataType = dataType;
        this.extraParam = extraParam;
        this.defaultValue = defaultValue;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getExtraParam() {
        return extraParam;
    }

    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getHasList() {
        return hasList;
    }

    public void setHasList(String hasList) {
        this.hasList = hasList;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRelationList() {
        return relationList;
    }

    public void setRelationList(String relationList) {
        this.relationList = relationList;
    }

    public String getSearchRequired() {
        return searchRequired;
    }

    public void setSearchRequired(String searchRequired) {
        this.searchRequired = searchRequired;
    }

    public String getSaveRequired() {
        return saveRequired;
    }

    public void setSaveRequired(String saveRequired) {
        this.saveRequired = saveRequired;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getRelationField() {
        return relationField;
    }

    public void setRelationField(String relationField) {
        this.relationField = relationField;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getDefault_Value() {
        return default_Value;
    }

    public void setDefault_Value(String default_Value) {
        this.default_Value = default_Value;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getFunctionValue() {
        return functionValue;
    }

    public void setFunctionValue(String functionValue) {
        this.functionValue = functionValue;
    }

    public String getFieldFunction() {
        return fieldFunction;
    }

    public void setFieldFunction(String fieldFunction) {
        this.fieldFunction = fieldFunction;
    }

    public String getFieldCalculated() {
        return fieldCalculated;
    }

    public void setFieldCalculated(String fieldCalculated) {
        this.fieldCalculated = fieldCalculated;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getFieldSql() {
        return fieldSql;
    }

    public void setFieldSql(String fieldSql) {
        this.fieldSql = fieldSql;
    }

    public String getSqlValue() {
        return sqlValue;
    }

    public void setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getShowFieldName() {
        return showFieldName;
    }

    public void setShowFieldName(String showFieldName) {
        this.showFieldName = showFieldName;
    }

    public String getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    public int getNumBlocks() {
        return numBlocks;
    }

    public void setNumBlocks(int numBlocks) {
        this.numBlocks = numBlocks;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public String getEnctype() {
        return enctype;
    }

    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public String getFormMethod() {
        return formMethod;
    }

    public void setFormMethod(String formMethod) {
        this.formMethod = formMethod;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormSave() {
        return formSave;
    }

    public void setFormSave(String formSave) {
        this.formSave = formSave;
    }


    public String getFormSaveCheck() {
        return formSaveCheck;
    }

    public void setFormSaveCheck(String formSaveCheck) {
        this.formSaveCheck = formSaveCheck;
    }

    public String getFormSaveCheckNames() {
        return formSaveCheckNames;
    }

    public void setFormSaveCheckNames(String formSaveCheckNames) {
        this.formSaveCheckNames = formSaveCheckNames;
    }
}
