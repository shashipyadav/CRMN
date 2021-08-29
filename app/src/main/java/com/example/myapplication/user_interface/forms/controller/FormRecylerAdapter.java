package com.example.myapplication.user_interface.forms.controller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.dlist.view.TabDlistActivity;
import com.example.myapplication.helper.DefaultValueFormatter;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.helper.FieldHelper;
import com.example.myapplication.user_interface.forms.controller.helper.FileHelper;
import com.example.myapplication.user_interface.forms.controller.helper.ReadEditableHelper;
import com.example.myapplication.user_interface.forms.controller.helper.SaveRequiredHelper;
import com.example.myapplication.user_interface.summary.SummaryActivity;
import com.example.myapplication.function.FunctionHelper;
import com.example.myapplication.user_interface.forms.controller.helper.FormHelper;
import com.example.myapplication.user_interface.forms.controller.helper.SpinnerHelper;
import com.example.myapplication.user_interface.forms.model.OptionModel;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.customviews.ClearableAutoCompleteTextView;
import com.example.myapplication.Constant;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.vdlist.TabVDlistActivity;
import com.example.myapplication.user_interface.vlist.VlistActivity;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.example.myapplication.util.DateUtil;
import com.example.myapplication.util.ExtensionUtil;
import com.example.myapplication.util.KeyboardUtil;
import com.example.myapplication.util.SpannableStringBuilderUtil;
import com.example.myapplication.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class FormRecylerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String DEBUG_TAG = FormRecylerAdapter.class.getSimpleName();
    FunctionHelper funcHelper;
    private Context context;
    private List<Field> fieldList;
    private FormFieldInterface listener;
    private SharedPrefManager mPrefManager;
    private FileHelper fileHelper;
    private String entryFrom;
    private boolean isUserAction = false; // using this for spinner
    private boolean isCallFunction = true;
    private OnBottomReachedListener onBottomReachedListener;
    private String recordId = "0";
    private String formId = "";
    private boolean autoUserInteraction = false;
    private LocationInterface locationListener;
    private ReadEditableHelper readEditableHelper;
    private CustomDateTimePickerInterface customDateTimePickerListener;
    private SaveRequiredHelper saveRequiredHelper;
    private boolean isVlist;

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        if(onBottomReachedListener != null) {
            this.onBottomReachedListener = onBottomReachedListener;
        }
    }

    public void setCustomDateTimePickerListener(CustomDateTimePickerInterface customDateTimePickerListener) {
        this.customDateTimePickerListener = customDateTimePickerListener;
    }

    public void setLocationListener(LocationInterface locationListener) {
        this.locationListener = locationListener;
    }

    public FormRecylerAdapter(Context mContext,
                              List<Field> fields,
                              FormFieldInterface mListener,
                              String entryFrom,
                              boolean isVlist) {
        this.context = mContext;
        this.fieldList = fields;
        this.listener = mListener;
        this.entryFrom = entryFrom;
        funcHelper = new FunctionHelper(context);
        fileHelper = new FileHelper((AppCompatActivity) context);
        mPrefManager = new SharedPrefManager(context);
        readEditableHelper = new ReadEditableHelper(context);
        saveRequiredHelper = new SaveRequiredHelper(context);
        this.isVlist = isVlist;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    //setting true of false
    // the functions are getting called many times hence maintaining a boolean flag
    //setting the flag to false, when we don't want to call the functions
    public void setFlag(boolean flag) {
        isCallFunction = flag;
    }

    public void notifyAdapter(final int position) {
        if (context != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            notifyItemChanged(position);
                        }
                    };
                    handler.post(r);
                }
            });
        }
    }

    public void notifyAdapterWithPayLoad(final int position, final String payload) {
        if (context != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            notifyItemChanged(position,payload);
                        }
                    };
                    handler.post(r);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View view;
        if (viewType == Constant.TYPE_EDIT_TEXT) { // for call layout
            view = LayoutInflater.from(context).inflate(R.layout.item_form_edit_text, parent, false);
            return new EditTextViewHolder(view);

        } else if (viewType == Constant.TYPE_SPINNER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_spinner,
                    parent, false);
            return new SpinnerViewHolder(view);

        } else if (viewType == Constant.TYPE_ITEM_AUTO_COMPLETE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_autocomplete,
                    parent, false);
            return new TextAutoCompleteViewHolder(view);

        } else if (viewType == Constant.TYPE_EDIT_DATE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_date,
                    parent, false);
            return new DateViewHolder(view);

        } else if (viewType == Constant.TYPE_EDIT_TEXT_AREA) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_edit_textarea,
                    parent, false);
            return new EditTextAreaViewHolder(view);

        } else if (viewType == Constant.TYPE_CHECKBOX) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_checkbox,
                    parent, false);
            return new CheckboxViewHolder(view);

        } else if (viewType == Constant.TYPE_SUMMARY) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_summary,
                    parent, false);
            return new SummaryViewHolder(view);
        } else if (viewType == Constant.TYPE_DLIST) {
            view = LayoutInflater.from(context).inflate(R.layout.item_dlist,
                    parent, false);
            return new DlistViewHolder(view);
        } else if (viewType == Constant.TYPE_FILE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_file,
                    parent, false);
            return new FileViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_edit_text, parent, false);
            return new EditTextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position, @NonNull List<Object> payloads) {
        final Field fieldObj = fieldList.get(position);

        try {

            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {

                for (Object payload : payloads) {

                    if (payload.equals(Constant.PAYLOAD_HIDE_SHOW)) {
                        if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT) {
                            final EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                            final SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                            final TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                            FileViewHolder mHolder = (FileViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_DATE) {
                            final DateViewHolder mHolder = (DateViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                            EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                            final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_SUMMARY) {
                            SummaryViewHolder mHolder = (SummaryViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_DLIST) {
                            final DlistViewHolder mHolder = (DlistViewHolder) holder;
                            hideShowView(fieldObj.isHidden(), mHolder);
                        }
                    } else if (payload.equals(Constant.PAYLOAD_TEXT_SQL)) {

                        if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT) {
                            final EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                            mHolder.editText.setText(fieldObj.getValue());
                            listener.evaluateSqlWithPayload(position, fieldObj.getOnChange());
                            saveRequiredHelper.editTextSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                            final SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
                            if (!fieldObj.getValue().isEmpty()) {
                                mHolder.spinner.setSelection(SpinnerHelper.getIndex(fieldObj.getOptionsArrayList(), fieldObj.getValue()), true);
                            }
                            saveRequiredHelper.spinnerSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);
                            listener.evaluateSqlWithPayload(position, fieldObj.getOnChange());

                        } else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                            final TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                            mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                            mHolder.autoCompleteTextView.setReadOnly(fieldObj.isReadOnly());
                            readEditableHelper.readEditAutoCompleteTextView(fieldObj.isReadOnly(), mHolder);
                            saveRequiredHelper.autocompleteTextSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);
                            listener.evaluateSqlWithPayload(position, fieldObj.getOnChange());

                        } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_DATE) {
                            final DateViewHolder mHolder = (DateViewHolder) holder;
                            mHolder.etDate.setText(fieldObj.getValue());
                            readEditableHelper.readEditDate(fieldObj.isReadOnly(), mHolder);
                            listener.evaluateSqlWithPayload(position, fieldObj.getOnChange());
                            saveRequiredHelper.editDateSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                            EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                            mHolder.etTxtArea.setText(fieldObj.getValue());
                            readEditableHelper.readEditTextArea(fieldObj.isReadOnly(), mHolder);
                            listener.evaluateSqlWithPayload(position, fieldObj.getOnChange());
                            saveRequiredHelper.editTextAreaSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                            final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;
                            if (!fieldObj.getValue().isEmpty()) {
                                mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
                            }
                            readEditableHelper.readEditCheckBox(fieldObj.isReadOnly(), mHolder);
                            callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldObj.getDataType());
                            listener.evaluateSqlWithPayload(position, fieldObj.getOnChange());
                        } else if (getItemViewType(position) == Constant.TYPE_SUMMARY) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_DLIST) {
                            super.onBindViewHolder(holder, position, payloads);
                        }
                    } else if (payload.equals(Constant.PAYLOAD_TEXT)) {

                        if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT) {
                            final EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                            mHolder.editText.setText(fieldObj.getValue());

                        } else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                            final SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
                            if (!fieldObj.getValue().isEmpty()) {
                                mHolder.spinner.setSelection(SpinnerHelper.getIndex(fieldObj.getOptionsArrayList(), fieldObj.getValue()), true);
                            }
                        } else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                            final TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                            mHolder.autoCompleteTextView.setText(fieldObj.getValue());


                        } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_DATE) {
                            final DateViewHolder mHolder = (DateViewHolder) holder;
                            mHolder.etDate.setText(fieldObj.getValue());

                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                            EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                            mHolder.etTxtArea.setText(fieldObj.getValue());


                        } else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                            final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;
                            if (!fieldObj.getValue().isEmpty()) {
                                mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
                            }

                        } else if (getItemViewType(position) == Constant.TYPE_SUMMARY) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_DLIST) {
                            super.onBindViewHolder(holder, position, payloads);
                        }
                    } else if (payload.equals(Constant.PAYLOAD_READ_EDIT)) {

                        if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT) {
                            final EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                            mHolder.editText.setText(fieldObj.getValue());
                            readEditableHelper.readableEditableEditText(fieldObj.isReadOnly(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                            final SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
                            if (!fieldObj.getValue().isEmpty()) {
                                mHolder.spinner.setSelection(SpinnerHelper.getIndex(fieldObj.getOptionsArrayList(), fieldObj.getValue()), true);
                            }
                        } else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                            final TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                            mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                            readEditableHelper.readEditAutoCompleteTextView(fieldObj.isReadOnly(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_DATE) {
                            final DateViewHolder mHolder = (DateViewHolder) holder;
                            mHolder.etDate.setText(fieldObj.getValue());
                            readEditableHelper.readEditDate(fieldObj.isReadOnly(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                            EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                            mHolder.etTxtArea.setText(fieldObj.getValue());
                            readEditableHelper.readEditTextArea(fieldObj.isReadOnly(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                            final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;
                            if (!fieldObj.getValue().isEmpty()) {
                                mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
                            }
                            readEditableHelper.readEditCheckBox(fieldObj.isReadOnly(), mHolder);
                        } else if (getItemViewType(position) == Constant.TYPE_SUMMARY) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_DLIST) {
                            super.onBindViewHolder(holder, position, payloads);
                        }

                    } else if (payload.equals(Constant.PAYLOAD_REQUIRED_NOTREQUIRED)) {
                        if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT) {
                            final EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                            mHolder.editText.setText(fieldObj.getValue());
                            saveRequiredHelper.editTextSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                            final SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
                            saveRequiredHelper.spinnerSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                            final TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                            mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                            saveRequiredHelper.autocompleteTextSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_DATE) {
                            final DateViewHolder mHolder = (DateViewHolder) holder;
                            mHolder.etDate.setText(fieldObj.getValue());
                            saveRequiredHelper.editDateSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                            EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                            mHolder.etTxtArea.setText(fieldObj.getValue());
                            saveRequiredHelper.editTextAreaSetRequired(fieldObj.getSaveRequired(), fieldObj.getFieldName(), mHolder);

                        } else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_SUMMARY) {
                            super.onBindViewHolder(holder, position, payloads);
                        } else if (getItemViewType(position) == Constant.TYPE_DLIST) {
                            super.onBindViewHolder(holder, position, payloads);
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideShowView(boolean isHidden, RecyclerView.ViewHolder holder) {

        if (isHidden) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                 final int position) {

        try {

            final Field fieldObj = fieldList.get(position);
            final String fieldName = fieldObj.getFieldName();
            final String fieldType = fieldObj.getDataType();
            String defaultValue = fieldObj.getDefaultValue();
            String formatedDefaultValue = "";
            if (!defaultValue.isEmpty()) {
                formatedDefaultValue = DefaultValueFormatter.containsAny(defaultValue);
            }

            if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT) {
                final EditTextViewHolder mHolder = (EditTextViewHolder) holder;


                String fieldValue = "";
                if (recordId.equals("0")) {
                    if (fieldObj.getValue().equals("")) {
                        if (formatedDefaultValue.toLowerCase().matches("\\$\\{self\\}|\\$\\{username\\}|\\$\\{login\\}|\\$username|\\$self")) {
                            fieldValue = mPrefManager.getUserName();
                        } else {
                            if (formatedDefaultValue.matches("0.0|0|0.00|EMAIL|CAPS|auto|AUTO|LOCATION|LOCATION\\$location")) {
                                if (formatedDefaultValue.matches("LOCATION|LOCATION\\$location")) {
                                    locationListener.displayLocation(position, "address");
                                } else if (formatedDefaultValue.matches("\\$location")) {
                                    locationListener.displayLocation(position, "location");
                                } else {
                                    fieldValue = fieldObj.getValue();
                                }
                            } else {
                                FieldHelper fieldHelper = new FieldHelper(context);
                                fieldValue = fieldHelper.getValue(formatedDefaultValue);
                            }
                        }
                    } else {
                        FieldHelper fieldHelper = new FieldHelper(context);
                        fieldValue = fieldHelper.getValue(fieldObj.getValue());

                        if (formatedDefaultValue.toLowerCase().matches("\\$\\{self\\}|\\$\\{username\\}|\\$\\{login\\}|\\$username|\\$self")) {
                            fieldValue = mPrefManager.getUserName();
                        } else {
                            if (fieldValue.matches("LOCATION|LOCATION\\$location")) {
                                locationListener.displayLocation(position, "address");
                            } else if (fieldValue.matches("\\$location")) {
                                locationListener.displayLocation(position, "location");
                            } else {
                                fieldValue = fieldObj.getValue();
                            }
                        }
                    }
                } else {
                    fieldValue = fieldObj.getValue();
                }

                mHolder.editText.setText(fieldValue);
                listener.onValueChanged(position, fieldValue, fieldObj.getOnChange());
                readEditableHelper.readableEditableEditText(fieldObj.isReadOnly(), mHolder);
                hideShowView(fieldObj.isHidden(), mHolder);
                if (fieldObj.isHidden()) {
                    if (isCallFunction) {
                        listener.checkHideShow(fieldObj.getOnChange());
                        listener.onChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldType);
                    }
                }

                if (fieldObj.showErrorMessage()) {
                    boolean isInvalid = false;
                    if (fieldObj.getErrorMessage().toLowerCase().contains("enter a valid")) {
                        isInvalid = true;
                    }
                    SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                            SpannableStringBuilderUtil.getErrorTextList(fieldName, isInvalid),
                            Constant.ERROR_COLORS);
                    mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
                    // mHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.red_light));
                } else {
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                        SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                        mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    } else {
                        mHolder.txtView.setText(fieldName);
                    }
                }

                if (fieldObj.getFieldType().toLowerCase().matches("createdby")) {
                    mHolder.editText.setFocusable(false);
                    mHolder.editText.setCursorVisible(false);
                    mHolder.editText.setFocusableInTouchMode(false);
                    mHolder.editText.setTextColor(context.getResources().getColor(R.color.white));
                    mHolder.editText.setBackgroundColor(context.getResources().getColor(R.color.read_only));
                    fieldObj.setSaveRequired("read");
                    mHolder.editText.setText(mPrefManager.getUserName());
                    listener.onValueChanged(position, mPrefManager.getUserName(), fieldObj.getOnChange());
                } else {
                    readEditableHelper.readableEditableEditText(fieldObj.isReadOnly(), mHolder);
                }

                if (fieldObj.isReadOnly() && !fieldObj.isHidden()) {
                    if (isCallFunction) {
                        listener.checkHideShow(fieldObj.getOnChange());
                        callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldType);

                    }
                }
                if (defaultValue.toLowerCase().contains("email") || fieldObj.getField_type().toLowerCase().equals("email")) {
                    mHolder.buttonAction.setVisibility(View.VISIBLE);
                    mHolder.buttonAction.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_email));
                } else if (defaultValue.toLowerCase().contains("caps")) {
                    mHolder.buttonAction.setVisibility(View.GONE);
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.AllCaps();
                    mHolder.editText.setFilters(filterArray);
                } else if (defaultValue.toLowerCase().equals("call") || fieldObj.getField_type().toLowerCase().equals("call")) {
                    mHolder.buttonAction.setVisibility(View.VISIBLE);
                    mHolder.buttonAction.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_phone));
                }else {
                    mHolder.buttonAction.setVisibility(View.GONE);
                }
                mHolder.bindEditText(fieldObj, position);

            } else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                final SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;

                if (fieldObj.showErrorMessage()) {
                    boolean isInvalid = false;
                    if (fieldObj.getErrorMessage().toLowerCase().equals("enter a valid")) {
                        isInvalid = true;
                    }
                    SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                            SpannableStringBuilderUtil.getErrorTextList(fieldName, isInvalid),
                            Constant.ERROR_COLORS);
                    mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
                    // mHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.red_light));
                } else {
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                        SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                        mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    } else {
                        mHolder.txtView.setText(fieldName);
                    }
                }
                hideShowView(fieldObj.isHidden(), mHolder);
                if (fieldObj.isHidden()) {
                    listener.checkHideShow(fieldObj.getOnChange());
                    callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldType);

                }

                List<OptionModel> optionsArrayList = new ArrayList<>();
                optionsArrayList = fieldObj.getOptionsArrayList();

                if (!optionsArrayList.isEmpty()) {
                    ArrayAdapter<OptionModel> adapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionsArrayList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    mHolder.spinner.setAdapter(adapter);
                } else {
                    listener.loadSpinner(position, fieldObj.getOnclickrightbutton());
                }

                final List<OptionModel> finalOptionsArrayList = optionsArrayList;
                mHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        Log.e(getClass().getSimpleName(), "parent.getSelectedITem()" + parent.getSelectedItem().toString());
                        if (!parent.getSelectedItem().toString().toLowerCase().equals("< select >") ||
                                !parent.getSelectedItem().toString().toLowerCase().equals("select") ||
                                !parent.getSelectedItem().toString().toLowerCase().equals("")) {

                            if (isUserAction) {
                                //  fieldList.get(position).setValue(finalOptionsArrayList.get(pos).getId());
                                listener.onValueChanged(position, finalOptionsArrayList.get(pos).getId(), "");
                                listener.checkHideShow(fieldObj.getOnChange());
                                callOnChange(position, fieldObj.getOnChange(), finalOptionsArrayList.get(pos).getId(), fieldType);

                                //  listener.onChange(position, fieldObj.getOnChange(), finalOptionsArrayList.get(pos).getId(), fieldType);
                                listener.onKeyDown(position, fieldObj);
                                isUserAction = false;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                String value = !fieldObj.getValue().isEmpty() ? fieldObj.getValue() : formatedDefaultValue;
                FieldHelper fieldHelper = new FieldHelper(context);
                value = fieldHelper.getValue(value);
                listener.onValueChanged(position, value, fieldObj.getOnChange());

                if (!value.isEmpty()) {
                    mHolder.spinner.setSelection(SpinnerHelper.getIndex(fieldObj.getOptionsArrayList(), value), true);
                    if (fieldObj.getOnChange().contains("checkhideshow")) {
                        listener.checkHideShow(fieldObj.getOnChange());
                    }
                }
            } else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                final TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                if (fieldType.toLowerCase().equals("tag")) {
                    mHolder.autoCompleteTextView.setHint(fieldObj.getWatermark());
                    //set text allcaps only for datatype = tag
//                InputFilter[] filterArray = new InputFilter[1];
//                filterArray[0] = new InputFilter.AllCaps();
                    // mHolder.autoCompleteTextView.setFilters(filterArray);
                    if (!recordId.equals("0")) {
                        if (isCallFunction) {
                            if (!fieldObj.getValue().isEmpty()) {
                                callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldObj.getDataType());
                                listener.checkHideShow(fieldObj.getOnChange());
                            }
                        }
                    }
                }

                mHolder.autoCompleteTextView.setReadOnly(fieldObj.isReadOnly());

                if (fieldObj.showErrorMessage()) {
                    boolean isInvalid = false;
                    if (fieldObj.getErrorMessage().toLowerCase().equals("enter a valid")) {
                        isInvalid = true;
                    }
                    SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                            SpannableStringBuilderUtil.getErrorTextList(fieldName, isInvalid),
                            Constant.ERROR_COLORS);
                    mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
                    // mHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.red_light));
                } else {
                    if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                        SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                        mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    } else {
                        mHolder.txtView.setText(fieldName);
                    }
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                }
                mHolder.autoCompleteTextView.setText(fieldObj.getValue());

                if (fieldObj.isClearTriggered()) {
                    mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                } else {
                    //Added this check as default value was showing even though value wasn't empty
                    if (recordId.equals("0")) {
                        if (!fieldObj.getValue().equals("")) {

                            FieldHelper fieldHelper = new FieldHelper(context);
                            String value = fieldHelper.getValue(fieldObj.getValue());
                            if (!value.isEmpty()) {
                                mHolder.autoCompleteTextView.setText(value);
                            } else {
                                mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                            }

                            listener.onValueChanged(position, value, fieldObj.getOnChange());
                            if (isCallFunction) {
                                callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldObj.getDataType());
                                listener.checkHideShow(fieldObj.getOnChange());
                            }

                        } else {
                            if (!formatedDefaultValue.equals("")) {
                                String value = !fieldObj.getValue().isEmpty() ? fieldObj.getValue() : formatedDefaultValue;
                                if (value.toLowerCase().
                                        matches("\\$\\{self\\}|self|\\$\\{username\\}|\\$\\{login\\}")) {
                                    value = mPrefManager.getUserName();
                                } else if (value.toLowerCase().equals("auto")) {
                                    value = fieldObj.getValue();
                                }
                                mHolder.autoCompleteTextView.setText(value);
                                listener.onValueChanged(position, value, fieldObj.getOnChange());
                                if (isCallFunction) {
                                    callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldObj.getDataType());
                                    listener.checkHideShow(fieldObj.getOnChange());
                                }
                            }
                        }
                    } else {
                        mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                    }
                }

                hideShowView(fieldObj.isHidden(), mHolder);
                if (fieldObj.isHidden()) {
                    if (isCallFunction) {
                        callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldType);
                        listener.checkHideShow(fieldObj.getOnChange());
                    }

                }
                readEditableHelper.readEditAutoCompleteTextView(fieldObj.isReadOnly(), mHolder);

                mHolder.autoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHolder.autoCompleteTextView.requestFocus();
                        listener.onClickRightButton(position, fieldObj.getOnKeyDown(),
                                fieldObj.getOnclickrightbutton(), fieldType, fieldName);
                    }
                });

                mHolder.autoCompleteTextView.setOnClearListener(
                        new ClearableAutoCompleteTextView.OnClearListener() {
                            @Override
                            public void onClear() {
                                fieldObj.setClearTriggered(true);
                                mHolder.txtView.setFocusable(true);
                                mHolder.txtView.setFocusableInTouchMode(true);
                                mHolder.txtView.requestFocus();
                                listener.onValueChanged(position, "", "");
                                notifyItemChanged(position);
                            }
                        });

            } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                FileViewHolder mHolder = (FileViewHolder) holder;
                hideShowView(fieldObj.isHidden(), mHolder);
                mHolder.bindFileTypeView(fieldObj, position);

            } else if (getItemViewType(position) == Constant.TYPE_EDIT_DATE) {
                final DateViewHolder mHolder = (DateViewHolder) holder;
                //check if error
                if (fieldObj.showErrorMessage()) {
                    boolean isInvalid = false;
                    if (fieldObj.getErrorMessage().toLowerCase().equals("enter a valid")) {
                        isInvalid = true;
                    }
                    SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                            SpannableStringBuilderUtil.getErrorTextList(fieldName, isInvalid),
                            Constant.ERROR_COLORS);
                    mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
                    //  mHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.red_light));
                } else {
                    if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                        SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                        mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    } else {
                        mHolder.txtView.setText(fieldName);
                    }
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                }

                hideShowView(fieldObj.isHidden(), mHolder);
                if (fieldObj.isHidden()) {
                    //call function if needed
                }

                if (fieldObj.getFieldType().toLowerCase().matches("createdat")) {
                    //if fieldtype = createdat, it should be always be readonly
                    //show the date in the field
                    mHolder.etDate.setFocusable(false);
                    mHolder.etDate.setCursorVisible(false);
                    fieldObj.setSaveRequired("read");
                    mHolder.etDate.setTextColor(context.getResources().getColor(R.color.white));
                    mHolder.etDate.setBackgroundColor(context.getResources().getColor(R.color.read_only));
                    mHolder.etDate.getCompoundDrawablesRelative()[2].setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
                } else {
                    readEditableHelper.readEditDate(fieldObj.isReadOnly(), mHolder);
                }

                //TODO : change format of the date when we fetch it from api
                String dateString = fieldObj.getValue();
                if (!dateString.isEmpty()) {
                    if (defaultValue.matches("\\$\\{now\\}|field\\$\\{now\\}")) {
                        dateString = DateUtil.formatDate(dateString, Constant.yyyy_MM_dd_now, Constant.dd_MM_yyyy_HH_mm);
                    } else if (defaultValue.matches("\\$\\{today\\}|field\\$\\{today\\}")) {
                        dateString = DateUtil.formatDate(dateString, Constant.yyyy_MM_dd, Constant.dd_MM_yyyy);
                    } else {
                        dateString = DateUtil.formatDate(dateString, Constant.yyyy_MM_dd, Constant.dd_MM_yyyy);
                    }

                    mHolder.etDate.setText(dateString);
                    listener.onValueChanged(position, dateString, "");
                } else {
                    //if value is empty, set default value
                    if (!defaultValue.equals("")) {

                        Log.e(DEBUG_TAG, "defaultValue = " + formatedDefaultValue);

                        String dtStr = DateUtil.getFormatedDate(formatedDefaultValue);
                        mHolder.etDate.setText(dtStr);
                        listener.onValueChanged(position, dtStr, "");
                        Log.e("LINE 274", dtStr);
                    }
                }

                if (isCallFunction) {
                    callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldType);
                }
                listener.checkHideShow(fieldObj.getOnChange());

                readEditableHelper.readEditDate(fieldObj.isReadOnly(), mHolder);

                mHolder.etDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!fieldObj.isReadOnly()) {

                            if (fieldObj.getDataType().toLowerCase().matches("datetime|datetime-local")) {
                                customDateTimePickerListener.loadCustomDateTimePicker(position, fieldObj.getOnChange());
                            } else {
                                listener.loadDatePicker(position, fieldObj.getOnChange());
                            }
                            mHolder.txtView.setFocusable(true);
                            mHolder.txtView.setFocusableInTouchMode(true);
                            mHolder.txtView.requestFocus();
                        }
                    }
                });

            } else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                if (fieldObj.showErrorMessage()) {
                    boolean isInvalid = false;
                    if (fieldObj.getErrorMessage().toLowerCase().equals("enter a valid")) {
                        isInvalid = true;
                    }
                    SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                            SpannableStringBuilderUtil.getErrorTextList(fieldName, isInvalid), Constant.ERROR_COLORS);
                    mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
                    //mHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.red_light));
                } else {
                    mHolder.txtView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                        SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                        mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                    } else {
                        mHolder.txtView.setText(fieldName);
                    }
                }

                mHolder.etTxtArea.setText(fieldObj.getValue());
                hideShowView(fieldObj.isHidden(), mHolder);
                if (fieldObj.isHidden()) {
                /* if(fieldObj.getStates().contains("s111s") || isCallFunction){
                    callOnChange(position,fieldObj.getOnChange(),fieldObj.getValue(),fieldType);
                } */

                }

                readEditableHelper.readEditTextArea(fieldObj.isReadOnly(), mHolder);

            } else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;

                mHolder.checkBox.setText(fieldName);
                hideShowView(fieldObj.isHidden(), mHolder);
                if (fieldObj.isHidden()) {
                /*if(fieldObj.getStates().contains("s111s") || isCallFunction){
                    callOnChange(position,fieldObj.getOnChange(),fieldObj.getValue(),fieldType);
                } */
                }

                readEditableHelper.readEditCheckBox(fieldObj.isReadOnly(), mHolder);

                mHolder.checkBox.setText(fieldName);
                if (!fieldObj.getValue().isEmpty()) {
                    mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
                }

                mHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String value = String.valueOf(isChecked);
                        Log.e("==CHECKBOX", "isChecked =" + value);

                        listener.onValueChanged(position, value, "");
                        callOnChange(position, fieldObj.getOnChange(), value, fieldType);
                        listener.checkHideShow(fieldObj.getOnChange());
                    }
                });

                callOnChange(position, fieldObj.getOnChange(), fieldObj.getValue(), fieldType);
                listener.checkHideShow(fieldObj.getOnChange());


            } else if (getItemViewType(position) == Constant.TYPE_SUMMARY) {
                SummaryViewHolder mHolder = (SummaryViewHolder) holder;
                hideShowView(fieldObj.isHidden(), mHolder);
                if (fieldObj.isHidden()) {
                /*if(fieldObj.getStates().contains("s111s") || isCallFunction){
                    callOnChange(position,fieldObj.getOnChange(),fieldObj.getValue(),fieldType);
                } */
                }
                mHolder.btnSummary.setText(fieldName);
                mHolder.btnSummary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constant.EXTRA_OBJECT, fieldObj);
                        bundle.putString(Constant.EXTRA_ENTRY, "form");
                        bundle.putString(Constant.EXTRA_DATA, JsonUtil.objectToJson(fieldObj));

                        if (fieldObj.getDataType().toLowerCase().matches("vlist")) {
                            mPrefManager.setVFormId(fieldObj.getRelation().trim());
                            intent = new Intent(context, VlistActivity.class);
                        } else {
                            intent = new Intent(context, SummaryActivity.class);
                        }
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });

            } else if (getItemViewType(position) == Constant.TYPE_DLIST) {
                final DlistViewHolder mHolder = (DlistViewHolder) holder;

                hideShowView(fieldObj.isHidden(), mHolder);

                if (fieldObj.isHidden()) {
                /* if(fieldObj.getStates().contains("s111s") || isCallFunction){
                    callOnChange(position,fieldObj.getOnChange(),fieldObj.getValue(),fieldType);
                } */
                }

                mHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        if (isVlist) {
                            intent = new Intent(context.getApplicationContext(), TabVDlistActivity.class);
                        } else {
                            intent = new Intent(context.getApplicationContext(), TabDlistActivity.class);
                        }
                        context.startActivity(intent);
                    }
                });
            }

            if (position == fieldList.size() - 1) {
                onBottomReachedListener.onBottomReached(position);
            }
        }catch ( Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    private void callOnChange(final int position, final String onChange, final String value, final String fieldType) {
        listener.onChange(position, onChange, value,
                fieldType);
    }

    private String[] getTextList(String fieldName) {
        return new String[]{fieldName, " *"};
    }

    @Override
    public int getItemViewType(int position) {
        String fieldType = fieldList.get(position).getDataType();

        if (fieldType.toLowerCase().matches("date|datetime|datetime-local|createdat")) {
            return Constant.TYPE_EDIT_DATE;

        } else if (
                fieldType.equalsIgnoreCase("TABS") ||
                        fieldType.equalsIgnoreCase("COMBO") ||
                        fieldType.equalsIgnoreCase("select")) {
            return Constant.TYPE_SPINNER;

        } else if (fieldType.equalsIgnoreCase("TAG") ||
                fieldType.equalsIgnoreCase("ADCOMBO") ||
                fieldType.equalsIgnoreCase("DCOMBO") ||
                fieldType.equalsIgnoreCase("MDCOMBO")) {
            return Constant.TYPE_ITEM_AUTO_COMPLETE;

        } else if (fieldType.equalsIgnoreCase("TEXTAREA")) {
            return Constant.TYPE_EDIT_TEXT_AREA;
        } else if (fieldType.toLowerCase().matches("checkbox|boolean")) {
            return Constant.TYPE_CHECKBOX;
        } else if (fieldType.toLowerCase().matches("summary|vlist")) {
            return Constant.TYPE_SUMMARY;
        } else if (fieldType.equalsIgnoreCase("DLIST")) {
            return Constant.TYPE_DLIST;
        } else if (fieldType.equalsIgnoreCase("file")) {
            return Constant.TYPE_FILE;
        } else if (fieldType.equalsIgnoreCase("DOUBLE")) {
            return Constant.TYPE_EDIT_TEXT;
        } else {
            return Constant.TYPE_EDIT_TEXT;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class EditTextViewHolder extends RecyclerView.ViewHolder {

        LinearLayout parentlayout;
        public TextView txtView;
        public EditText editText;
        ImageButton buttonAction;
        // Instance of a Custom edit text listener

        public EditTextViewHolder(@NonNull View itemView) {
            super(itemView);
            parentlayout = itemView.findViewById(R.id.parent);
            txtView = itemView.findViewById(R.id.text_view);
            editText = itemView.findViewById(R.id.edit_text);
            buttonAction = itemView.findViewById(R.id.button_action);

            buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (!hasFocus) {
                            if (getAdapterPosition() != -1) {

                                String fieldType = fieldList.get(getAdapterPosition()).getDataType();
                                listener.onValueChanged(getAdapterPosition(), editText.getText().toString(), "");
                                String onChange = fieldList.get(getAdapterPosition()).getOnChange();
                                if (!editText.getText().toString().isEmpty()) {
                                    Log.e("edittextonFocus", "********************* called");

                                    listener.checkHideShow(onChange);
                                    callOnChange(getAdapterPosition(), onChange, editText.getText().toString(), fieldType);
                                }

                                // listener.onChange(, , , );
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_DONE:
                            Log.e("EDITTEXT", "IME_ACTION_DONE");
                            listener.onValueChanged(getAdapterPosition(), editText.getText().toString(), fieldList.get(getAdapterPosition()).getOnChange());
                            KeyboardUtil.hideKeyboard((Activity) context);
                            return true;
                        case EditorInfo.IME_ACTION_NEXT:
                            Log.e("EDITTEXT", "IME_ACTION_NEXT");
                            listener.onValueChanged(getAdapterPosition(), editText.getText().toString(), fieldList.get(getAdapterPosition()).getOnChange());
                            return true;

                        case EditorInfo.IME_ACTION_PREVIOUS:
                            Log.e("EDITTEXT", "IME_ACTION_PREVIOUS");
                            listener.onValueChanged(getAdapterPosition(), editText.getText().toString(), fieldList.get(getAdapterPosition()).getOnChange());
                            return true;
                    }
                    return false;
                }
            });

        }


        public void bindEditText(final Field fieldObj, int position) {

            buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fieldObj.getDefaultValue().toLowerCase().contains("email") || fieldObj.getField_type().toLowerCase().equals("email")) {
                        performEmailAction(fieldObj.getValue());
                    } else if (fieldObj.getDefaultValue().toLowerCase().contains("call") || fieldObj.getField_type().toLowerCase().equals("call")) {
                        performCallAction(fieldObj.getValue());
                    }
                }
            });
        }

        private void performCallAction(String mobileNo) {
            if (!mobileNo.isEmpty()) {
                Uri uri = Uri.parse("tel:" + mobileNo);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
                try {
                    context.startActivity(callIntent);
                } catch (ActivityNotFoundException activityNotFoundException) {

                    ToastUtil.showToastMessage("No application installed", context);
                }
            } else {
                ToastUtil.showToastMessage("Mobile No not found", context);
            }
        }

        private void performEmailAction(String recipient) {
            if (!recipient.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});

                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException activityNotFoundException) {
                    ToastUtil.showToastMessage("No application installed", context);
                }
            } else {
                ToastUtil.showToastMessage("Email address not found", context);
            }
        }
    }

    public class SpinnerViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        Spinner spinner;
        Field fieldObj;
        List<OptionModel> optionsArrayList;

        public SpinnerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
            spinner = itemView.findViewById(R.id.spinner);

            if (getAdapterPosition() != -1) {
                fieldObj = fieldList.get(getAdapterPosition());
                optionsArrayList = fieldObj.getOptionsArrayList();

            }

            spinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        spinner.setFocusable(true);
                        spinner.setFocusableInTouchMode(true);
                        spinner.requestFocus();
                        spinner.setFocusableInTouchMode(false);
                        spinner.clearFocus();
                        isUserAction = true;
                    }
                    return false;
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Log.e(getClass().getSimpleName(), "parent.getSelectedITem()" + parent.getSelectedItem().toString());
                    if (!parent.getSelectedItem().toString().toLowerCase().equals("< select >") ||
                            !parent.getSelectedItem().toString().toLowerCase().equals("select") ||
                            !parent.getSelectedItem().toString().toLowerCase().equals("")) {

                        if (isUserAction) {
                            //  fieldList.get(position).setValue(finalOptionsArrayList.get(pos).getId());
                            listener.onValueChanged(getAdapterPosition(), optionsArrayList.get(pos).getId(), "");
                            listener.checkHideShow(fieldObj.getOnChange());
                            callOnChange(getAdapterPosition(), fieldObj.getOnChange(), optionsArrayList.get(pos).getId(), fieldObj.getDataType());

                            //  listener.onChange(getAdapterPosition(), fieldObj.getOnChange(), optionsArrayList.get(pos).getId(), fieldObj.getDataType());
                            listener.onKeyDown(getAdapterPosition(), fieldObj);
                            isUserAction = false;
                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    public class TextAutoCompleteViewHolder extends RecyclerView.ViewHolder {
        public  TextView txtView;
        public ClearableAutoCompleteTextView autoCompleteTextView;
        public ImageButton autoButton;
        public RelativeLayout relativeLayout;

        public TextAutoCompleteViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rel_layout);
            autoCompleteTextView = itemView.findViewById(R.id.autocomplete);
            autoCompleteTextView.setThreshold(1);
            txtView = itemView.findViewById(R.id.text_view);
            autoButton = itemView.findViewById(R.id.auto_button);

//            autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (entryFrom == null) {
//                        if (!hasFocus) {
//                            if (getAdapterPosition() != -1) {
//                                String fieldType = fieldList.get(getAdapterPosition()).getDataType();
//                                String fieldId = fieldList.get(getAdapterPosition()).getId();
//                                if (fieldType.equalsIgnoreCase("TAG")) {
//                                    String value = autoCompleteTextView.getText().toString();
//                                    if (!value.equals("")) {
//                                        listener.onValueChanged(getAdapterPosition(), autoCompleteTextView.getText().toString(), "");
//                                        listener.fetchRecord(getAdapterPosition(), autoCompleteTextView.getText().toString(), fieldId);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            });

            autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_DONE:
                            Log.e("autoCompleteTextView", "IME_ACTION_DONE");
                            if (entryFrom == null) {
                                    if (getAdapterPosition() != -1) {
                                        String fieldType = fieldList.get(getAdapterPosition()).getDataType();
                                        String fieldId = fieldList.get(getAdapterPosition()).getId();
                                        if (fieldType.equalsIgnoreCase("TAG")) {
                                            String value = autoCompleteTextView.getText().toString();
                                            if (!value.equals("")) {
                                                listener.onValueChanged(getAdapterPosition(), autoCompleteTextView.getText().toString(), "");
                                                listener.fetchRecord(getAdapterPosition(), autoCompleteTextView.getText().toString(), fieldId);
                                            }
                                        }
                                    }
                            }
                            KeyboardUtil.hideKeyboard((Activity) context);
                            return true;
                        case EditorInfo.IME_ACTION_NEXT:
                            if (entryFrom == null) {
                                if (getAdapterPosition() != -1) {
                                    String fieldType = fieldList.get(getAdapterPosition()).getDataType();
                                    String fieldId = fieldList.get(getAdapterPosition()).getId();
                                    if (fieldType.equalsIgnoreCase("TAG")) {
                                        String value = autoCompleteTextView.getText().toString();
                                        if (!value.equals("")) {
                                            listener.onValueChanged(getAdapterPosition(), autoCompleteTextView.getText().toString(), "");
                                            listener.fetchRecord(getAdapterPosition(), autoCompleteTextView.getText().toString(), fieldId);
                                        }
                                    }
                                }
                            }
                            KeyboardUtil.hideKeyboard((Activity) context);
                            return true;

                        case EditorInfo.IME_ACTION_PREVIOUS:
                            if (entryFrom == null) {
                                if (getAdapterPosition() != -1) {
                                    String fieldType = fieldList.get(getAdapterPosition()).getDataType();
                                    String fieldId = fieldList.get(getAdapterPosition()).getId();
                                    if (fieldType.equalsIgnoreCase("TAG")) {
                                        String value = autoCompleteTextView.getText().toString();
                                        if (!value.equals("")) {
                                            listener.onValueChanged(getAdapterPosition(), autoCompleteTextView.getText().toString(), "");
                                            listener.fetchRecord(getAdapterPosition(), autoCompleteTextView.getText().toString(), fieldId);
                                        }
                                    }
                                }
                            }
                            KeyboardUtil.hideKeyboard((Activity) context);
                            return true;
                    }
                    return false;
                }
            });




            autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        listener.onValueChanged(getAdapterPosition(), autoCompleteTextView.getText().toString(), "");
                    }
                }
            });
        }
    }


    public class CheckboxViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;

        public CheckboxViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        checkBox.setFocusable(true);
                        checkBox.setFocusableInTouchMode(true);
                        checkBox.requestFocus();
                        checkBox.setFocusableInTouchMode(false);
                        checkBox.clearFocus();
                    }
                    return false;
                }
            });
        }
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {

        public TextView txtView;
        public EditText etDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);

            txtView = itemView.findViewById(R.id.text_view);
            etDate = itemView.findViewById(R.id.edit_text_date);

            etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (!hasFocus) {
                            try {
                                String fieldType = fieldList.get(getAdapterPosition()).getDataType();
                                String formatedDate = DateUtil.formatDateTo_yyyyMMdd(etDate.getText().toString());

                                listener.checkHideShow(fieldList.get(getAdapterPosition()).getOnChange());
                                callOnChange(getAdapterPosition(), fieldList.get(getAdapterPosition()).getOnChange(), formatedDate, fieldType);

                                //  listener.onChange(getAdapterPosition(), fieldList.get(getAdapterPosition()).getOnChange(), formatedDate, fieldType);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

//            etDate.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    try{
//                        if(!fieldList.get(getAdapterPosition()).getOnChange().isEmpty()){
//                            String fieldType = fieldList.get(getAdapterPosition()).getDataType();
//                            String formatedDate = DateUtil.formatDateTo_yyyyMMdd(s.toString());
//                            listener.onChange(getAdapterPosition(), fieldList.get(getAdapterPosition()).getOnChange(), formatedDate, fieldType);
//                        }
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            });
        }
    }

    public class EditTextAreaViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        public EditText etTxtArea;
        long delay = 2000; // 1 seconds after user stops typing
        long last_text_edit = 0;
        Handler handler = new Handler();

        private Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    if (getAdapterPosition() != -1) {
                        listener.onValueChanged(getAdapterPosition(), etTxtArea.getText().toString(), "");
                        String fieldType = fieldList.get(getAdapterPosition()).getDataType();
                        if (isCallFunction) {
                            listener.checkHideShow(fieldList.get(getAdapterPosition()).getOnChange());
                            callOnChange(getAdapterPosition(), fieldList.get(getAdapterPosition()).getOnChange(), etTxtArea.getText().toString(), fieldType);
                        }
                    }
                }
            }
        };

        public EditTextAreaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
            etTxtArea = itemView.findViewById(R.id.edit_text_area);

            etTxtArea.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        last_text_edit = System.currentTimeMillis();
                        handler.postDelayed(input_finish_checker, delay);
                    }
                }
            });

            etTxtArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (getAdapterPosition() != -1) {
                        try {
                            if (!hasFocus) {
                                listener.onValueChanged(getAdapterPosition(), etTxtArea.getText().toString(), "");
                                String fieldType = fieldList.get(getAdapterPosition()).getDataType();
                                listener.checkHideShow(fieldList.get(getAdapterPosition()).getOnChange());
                                callOnChange(getAdapterPosition(), fieldList.get(getAdapterPosition()).getOnChange(), etTxtArea.getText().toString(), fieldType);

                                //   listener.onChange(getAdapterPosition(), fieldList.get(getAdapterPosition()).getOnChange(), etTxtArea.getText().toString(), fieldType);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
    }

    static class SummaryViewHolder extends RecyclerView.ViewHolder {

        Button btnSummary;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            btnSummary = itemView.findViewById(R.id.button_summary);
        }
    }

    class DlistViewHolder extends RecyclerView.ViewHolder {
        Button btnMore;
        RelativeLayout mRelativeLayout;

        public DlistViewHolder(View v) {
            super(v);
            mRelativeLayout = v.findViewById(R.id.rel_button);
            btnMore = v.findViewById(R.id.btn_show_more);
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtView;
        Button btnChooseFile;
        TextView txtFileName;
        Field fieldObj;
        int position;
        View view;

        public FileViewHolder(View v) {
            super(v);
            view = v;

            txtView = itemView.findViewById(R.id.text_view);
            btnChooseFile = itemView.findViewById(R.id.btn_choose_file);
            txtFileName = itemView.findViewById(R.id.txt_file_name);
            btnChooseFile.setOnClickListener(this);
            txtFileName.setOnClickListener(this);

        }

        private void bindFileTypeView(Field fieldObj, final int position) {
            this.fieldObj = fieldObj;
            this.position = position;

            displayErrorMessage(txtView, fieldObj);

            if (fieldObj.isReadOnly()) {
                /// disable button for readonly
            }

            final String fileName = fileHelper.getAttachedFileName(fieldObj.getValue());
            setupHyperlink(fileName);
        }

        private void setupHyperlink(String fileName) {
            String extension = ExtensionUtil.getExtension(fileName);
            if (!extension.isEmpty()) {
                txtFileName.setText(Html.fromHtml("<font color=#000000>  <u>" + fileName + "</u>  </font>"));
                //txtFileName.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                txtFileName.setText(fileName);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_choose_file) {
                listener.pickFile(position);
            } else if (v.getId() == R.id.txt_file_name) {
                FileHelper fileHelper = new FileHelper((AppCompatActivity) context);
                fileHelper.viewFile(fieldObj.getValue(), false);

            }
        }
    }

    private void displayErrorMessage(TextView txtView, Field fieldObj) {
        String fieldName = fieldObj.getFieldName();
        if (fieldObj.showErrorMessage()) {
            boolean isInvalid = false;
            if (fieldObj.getErrorMessage().toLowerCase().equals("enter a valid")) {
                isInvalid = true;
            }
            SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(
                    SpannableStringBuilderUtil.getErrorTextList(fieldName, isInvalid),
                    Constant.ERROR_COLORS);
            txtView.setText(builder, TextView.BufferType.SPANNABLE);
            txtView.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
            //  mHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.red_light));
        } else {
            if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                txtView.setText(builder, TextView.BufferType.SPANNABLE);
            } else {
                txtView.setText(fieldName);
            }
            txtView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
    }
}


