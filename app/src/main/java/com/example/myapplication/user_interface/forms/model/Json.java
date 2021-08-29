package com.example.myapplication.user_interface.forms.model;

import com.example.myapplication.user_interface.forms.model.Field;

import java.util.ArrayList;
import java.util.List;

public class Json {

    private String enctype;

    private String dataTitle;

    private String formMethod;

    private String formId;

    private String id;

    private String formSave;

    private List<Field> fields = new ArrayList<Field>();

    public Json(String enctype, String dataTitle, String formMethod, String formId,
                String id, String formSave, List<Field> fields) {
        this.enctype = enctype;
        this.dataTitle = dataTitle;
        this.formMethod = formMethod;
        this.formId = formId;
        this.id = id;
        this.formSave = formSave;
        this.fields = fields;
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

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
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

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
