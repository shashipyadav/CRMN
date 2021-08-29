package com.example.myapplication.user_interface.forms.controller.helper;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.dlist.controller.DListRecyclerAdapter;
import com.example.myapplication.user_interface.dlist.controller.DlistFieldRecyclerAdapter;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.view.FormFragment;

public class ReadEditableHelper {

    private Context context;

    public ReadEditableHelper(Context context) {
        this.context = context;
    }

    public  void readableEditableEditText(boolean isReadOnly,
                                          FormRecylerAdapter.EditTextViewHolder mHolder) {
        if(isReadOnly){
            mHolder.editText.setFocusable(false);
            mHolder.editText.setCursorVisible(false);
            mHolder.editText.setFocusableInTouchMode(false);
            mHolder.editText.setTextColor(context.getResources().getColor(R.color.white));
            mHolder.editText.setBackgroundColor(context.getResources().getColor(R.color.read_only));
        }else{
            mHolder.editText.setFocusable(true);
            mHolder.editText.setCursorVisible(true);
            mHolder.editText.setFocusableInTouchMode(true);
            mHolder.editText.setTextColor(context.getResources().getColor(R.color.black));
            mHolder.editText.setBackgroundResource(R.drawable.edit_text_border);
        }
    }

    public void readEditAutoCompleteTextView(boolean isReadOnly,FormRecylerAdapter.TextAutoCompleteViewHolder mHolder){

        if(isReadOnly) {
            mHolder.relativeLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.autocomplete_bg_black));
            mHolder.autoCompleteTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
            mHolder.autoCompleteTextView.setEnabled(false);
            mHolder.autoButton.setEnabled(false);

        }else {
            mHolder.relativeLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.bottom_lne_bg));
            mHolder.autoCompleteTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            mHolder.autoCompleteTextView.setEnabled(true);
            mHolder.autoButton.setEnabled(true);
        }
    }

    public void readEditDate(boolean isReadOnly, FormRecylerAdapter.DateViewHolder mHolder) {

        if (isReadOnly) {
            mHolder.etDate.setFocusable(false);
            mHolder.etDate.setCursorVisible(false);
            mHolder.etDate.setTextColor(context.getResources().getColor(R.color.white));
            mHolder.etDate.setBackgroundColor(context.getResources().getColor(R.color.read_only));
            mHolder.etDate.getCompoundDrawablesRelative()[2].setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        } else{
            //  mHolder.etDate.setTextColor(context.getResources().getColor(R.color.black));
            mHolder.etDate.getCompoundDrawablesRelative()[2].setColorFilter(ContextCompat.getColor(context, R.color.icon_grey), PorterDuff.Mode.SRC_IN);
        }
    }

    public void readEditTextArea(boolean isReadOnly, FormRecylerAdapter.EditTextAreaViewHolder mHolder) {
        if(isReadOnly){
            mHolder.etTxtArea.setFocusable(false);
            mHolder.etTxtArea.setCursorVisible(false);
            mHolder.etTxtArea.setFocusableInTouchMode(false);
            mHolder.etTxtArea.setTextColor(context.getResources().getColor(R.color.white));
            mHolder.etTxtArea.setBackgroundColor(context.getResources().getColor(R.color.read_only));
        }else{
            mHolder.etTxtArea.setFocusable(true);
            mHolder.etTxtArea.setCursorVisible(true);
            mHolder.etTxtArea.setFocusableInTouchMode(true);
            mHolder.etTxtArea.setTextColor(context.getResources().getColor(R.color.black));
            mHolder.etTxtArea.setBackgroundResource(R.drawable.edit_text_border);
        }
    }

    public void readEditCheckBox(boolean isReadOnly, FormRecylerAdapter.CheckboxViewHolder mHolder) {

        if(isReadOnly){
            mHolder.checkBox.setEnabled(false);
            mHolder.checkBox.setTextColor(Color.GRAY);
            CompoundButtonCompat.setButtonTintList(mHolder.checkBox, ColorStateList.valueOf(Color.GRAY));
        }else{
            mHolder.checkBox.setEnabled(true);
            mHolder.checkBox.setTextColor(Color.BLACK);
            CompoundButtonCompat.setButtonTintList(mHolder.checkBox, ColorStateList.valueOf(Color.BLACK));
        }
    }

    public void dlistReadEditTextField(boolean isReadOnly, DlistFieldRecyclerAdapter.EditTextViewHolder mHolder) {
        if (isReadOnly) {
            mHolder.editText.setFocusable(false);
            mHolder.editText.setCursorVisible(false);
            mHolder.editText.setTextColor(context.getResources().getColor(R.color.white));
            mHolder.editText.setBackgroundColor(context.getResources().getColor(R.color.read_only));
        }else{
            mHolder.editText.setFocusable(true);
            mHolder.editText.setCursorVisible(true);
            mHolder.editText.setFocusableInTouchMode(true);
            mHolder.editText.setTextColor(context.getResources().getColor(R.color.black));
            mHolder.editText.setBackgroundResource(R.drawable.edit_text_border);
        }

    }

    public void dlistReadEditCheckBox(boolean isReadOnly, DlistFieldRecyclerAdapter.CheckboxViewHolder mHolder) {

        if(isReadOnly){
            mHolder.checkBox.setEnabled(false);
            mHolder.checkBox.setTextColor(Color.GRAY);
            CompoundButtonCompat.setButtonTintList(mHolder.checkBox, ColorStateList.valueOf(Color.GRAY));
        }else{
            mHolder.checkBox.setEnabled(true);
            mHolder.checkBox.setTextColor(Color.BLACK);
            CompoundButtonCompat.setButtonTintList(mHolder.checkBox, ColorStateList.valueOf(Color.BLACK));
        }
    }

    public void dlistReadEditTextArea(boolean isReadOnly, DlistFieldRecyclerAdapter.EditTextAreaViewHolder mHolder) {
        if(isReadOnly){
            mHolder.etTxtArea.setFocusable(false);
            mHolder.etTxtArea.setCursorVisible(false);
            mHolder.etTxtArea.setFocusableInTouchMode(false);
            mHolder.etTxtArea.setTextColor(context.getResources().getColor(R.color.white));
            mHolder.etTxtArea.setBackgroundColor(context.getResources().getColor(R.color.read_only));
        }else{
            mHolder.etTxtArea.setFocusable(true);
            mHolder.etTxtArea.setCursorVisible(true);
            mHolder.etTxtArea.setFocusableInTouchMode(true);
            mHolder.etTxtArea.setTextColor(context.getResources().getColor(R.color.black));
            mHolder.etTxtArea.setBackgroundResource(R.drawable.edit_text_border);
        }
    }

    public void dlistReadEditAutoCompleteTextView(boolean isReadOnly,DlistFieldRecyclerAdapter.TextAutoCompleteViewHolder mHolder){

        if(isReadOnly) {
            mHolder.relativeLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.autocomplete_bg_black));
            mHolder.autoCompleteTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
            mHolder.autoCompleteTextView.setEnabled(false);
            mHolder.autoButton.setEnabled(false);

        }else {
            mHolder.relativeLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.bottom_lne_bg));
            mHolder.autoCompleteTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            mHolder.autoCompleteTextView.setEnabled(true);
            mHolder.autoButton.setEnabled(true);
        }
    }



    public  void dlistReadableEditableEditText(boolean isReadOnly,
                                          DlistFieldRecyclerAdapter.DateViewHolder mHolder) {
        if(isReadOnly){
            mHolder.etDate.setFocusable(false);
            mHolder.etDate.setCursorVisible(false);
            mHolder.etDate.setFocusableInTouchMode(false);
            mHolder.etDate.setTextColor(context.getResources().getColor(R.color.white));
            mHolder.etDate.setBackgroundColor(context.getResources().getColor(R.color.read_only));
        }else{

            mHolder.etDate.setTextColor(context.getResources().getColor(R.color.black));
            mHolder.etDate.setBackgroundResource(R.drawable.edit_text_border);
        }
    }
}
