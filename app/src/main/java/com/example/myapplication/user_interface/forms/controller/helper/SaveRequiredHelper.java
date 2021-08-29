package com.example.myapplication.user_interface.forms.controller.helper;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.dlist.controller.DlistFieldRecyclerAdapter;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.util.SpannableStringBuilderUtil;

public class SaveRequiredHelper {

    private Context context;

    public SaveRequiredHelper(Context context) {
        this.context = context;
    }

    public void dlistDateSetRequired(String saveRequired, String fieldName, DlistFieldRecyclerAdapter.DateViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

    public void dlistEditTextSetRequired(String saveRequired, String fieldName, DlistFieldRecyclerAdapter.EditTextViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

    public void dlistEditTextAreaSetRequired(String saveRequired, String fieldName,
                                        DlistFieldRecyclerAdapter.EditTextAreaViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

    public void dlistAutocompleteTextSetRequired(String saveRequired, String fieldName, DlistFieldRecyclerAdapter.TextAutoCompleteViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

    public void dlistSpinnerSetRequired(String saveRequired, String fieldName, DlistFieldRecyclerAdapter.SpinnerViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

    public void dlistFileSetRequired(String saveRequired, String fieldName, DlistFieldRecyclerAdapter.FileViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

    public void editTextSetRequired(String saveRequired, String fieldName, FormRecylerAdapter.EditTextViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }


    public void editDateSetRequired(String saveRequired, String fieldName, FormRecylerAdapter.DateViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

    public void spinnerSetRequired(String saveRequired, String fieldName, FormRecylerAdapter.SpinnerViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

    public void autocompleteTextSetRequired(String saveRequired, String fieldName, FormRecylerAdapter.TextAutoCompleteViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }


    public void editTextAreaSetRequired(String saveRequired, String fieldName,
                                        FormRecylerAdapter.EditTextAreaViewHolder mHolder){
        if (saveRequired.toLowerCase().matches("true")) {
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getTextList(fieldName), Constant.COLORS);
            mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            mHolder.txtView.setText(fieldName);
        }
    }

}
