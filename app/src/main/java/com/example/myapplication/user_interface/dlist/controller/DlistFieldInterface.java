package com.example.myapplication.user_interface.dlist.controller;

public interface DlistFieldInterface {
    public void pickFile(int position);
    public void loadDatePicker(int position);
    public void onValueChanged(int position, String value,boolean doUpdateRecyclerView);
    public void onAddFunction(int position, String function, String value);
    public void onClickRightButton(int position, String dropDownClickFunc,String fieldType);
 //   public void evaluateSqlWithPayload(int position,String onChange, String value);
    //this is called only after evaluateFunction is called
    public void runFunctions(int position, String function, String value);
}
