package com.example.myapplication.user_interface.forms.model;

public class OptionModel {
    private String id;
    private String value;
    private boolean isSelected;
    private boolean enable = true;

    public OptionModel(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.replaceAll("\\\\t$","");
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEnabled() {
        return enable;
    }

    public void setEnabled(boolean enabled) {
        enable = enabled;
    }
}
