package com.example.myapplication.user_interface.dlist.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
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
import com.example.myapplication.helper.DefaultValueFormatter2;
import com.example.myapplication.user_interface.dlist.model.DList;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.CustomDateTimePickerInterface;
import com.example.myapplication.user_interface.forms.controller.FormRecylerAdapter;
import com.example.myapplication.user_interface.forms.controller.helper.FileHelper;
import com.example.myapplication.user_interface.forms.controller.helper.ReadEditableHelper;
import com.example.myapplication.user_interface.forms.controller.helper.SaveRequiredHelper;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.user_interface.forms.controller.helper.SpinnerHelper;
import com.example.myapplication.user_interface.forms.model.OptionModel;
import com.example.myapplication.customviews.ClearableAutoCompleteTextView;
import com.example.myapplication.Constant;
import com.example.myapplication.util.ExtensionUtil;
import com.example.myapplication.util.KeyboardUtil;
import com.example.myapplication.util.SpannableStringBuilderUtil;

import java.util.ArrayList;
import java.util.List;

public class DlistFieldRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DList> fieldList;
    private DlistFieldInterface mListener;
    private int mDlistButtonArrayPosition = -1;
    private int mListObjPosition = -1;
    private int fieldPostion;
    private FileHelper fileHelper;
    private boolean isCallFunction = true;
 // private OnBottomReachedListener onBottomReachedListener;
    private boolean isUserAction = false;
    private CustomDateTimePickerInterface customDateTimePickerListener;
    private ReadEditableHelper readEditableHelper;
    private SaveRequiredHelper saveRequiredHelper;
    private SharedPrefManager mPrefManager;
    private DefaultValueFormatter2 dfValueFormatter;
    public interface OnBottomReachedListener {
        void onBottomReached(int position);
    }

//    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
//        this.onBottomReachedListener = onBottomReachedListener;
//    }

    public void setCustomDateTimePickerListener(CustomDateTimePickerInterface customDateTimePickerListener){
        this.customDateTimePickerListener = customDateTimePickerListener;
    }

    public DlistFieldRecyclerAdapter(Context context, List<DList> fields,
                                     DlistFieldInterface listener,
                                     int dlistButtonArrayPosition,
                                    /* int dlistItemPosition,*/
                                     int listObjPosition) {
        this.context = context;
        this.fieldList = fields;
        this.mListener = listener;
        this.mDlistButtonArrayPosition = dlistButtonArrayPosition;
        //this.mDlistItemPosition = dlistItemPosition;
        this.mListObjPosition = listObjPosition;
        fieldPostion = FormFragment.dlistArrayPosition;
        fileHelper = new FileHelper((AppCompatActivity) this.context);
        readEditableHelper = new ReadEditableHelper(context);
        saveRequiredHelper = new SaveRequiredHelper(context);
        mPrefManager = new SharedPrefManager(context);
        dfValueFormatter = new DefaultValueFormatter2(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View view;
        if (viewType == Constant.TYPE_SPINNER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_spinner,
                    parent, false);
            return new SpinnerViewHolder(view);

        } else if (viewType == Constant.TYPE_EDIT_DATE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_date,
                    parent, false);
            return new DateViewHolder(view);

        } else if (viewType == Constant.TYPE_EDIT_TEXT_AREA) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_edit_textarea,
                    parent, false);
            return new EditTextAreaViewHolder(view);

        } else if (viewType == Constant.TYPE_CHECKBOX) { // for email layout
            view = LayoutInflater.from(context).inflate(R.layout.item_form_checkbox,
                    parent, false);
            return new CheckboxViewHolder(view);

        }else if (viewType == Constant.TYPE_ITEM_AUTO_COMPLETE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_autocomplete,
                    parent, false);
            return new TextAutoCompleteViewHolder(view);

        } /* else if (viewType == TYPE_SUMMARY) {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_summary,
                    parent, false);
            return new SummaryViewHolder(view);
        } else if (viewType == TYPE_DLIST) {
            view = LayoutInflater.from(context).inflate(R.layout.item_dlist,
                    parent, false);
            return new DlistViewHolder(view);
        } else if(viewType == Constant.TYPE_MULTI_SELECT_SPINNER){
            view = LayoutInflater.from(context).inflate(R.layout.item_multiselect_spinner,
                    parent, false);
            return new MultiSelectSpinnerViewHolder(view);
        }*/

        else if (viewType == Constant.TYPE_FILE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_file,
                    parent, false);
            return new FileViewHolder(view);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.item_form_edit_text, parent, false);
            return new EditTextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 final int position,
                                 @NonNull List<Object> payloads) {
        final DList fieldObj = fieldList.get(position);
          if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
              for (Object payload : payloads) {
                  if (payload.equals(Constant.PAYLOAD_EVALUATE_FUNCTION)) {
                      if (getItemViewType(position) == Constant.TYPE_EDIT_DATE) {
                          DateViewHolder mHolder = (DateViewHolder) holder;
                          mHolder.etDate.setText(fieldObj.getValue());
                          runAddFunctionInBackground(position,fieldObj.getAddFunction(),fieldObj.getValue());

                      }else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                          SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
                          if (!fieldObj.getValue().isEmpty()) {
                              mHolder.spinner.setSelection(SpinnerHelper.getIndex(fieldObj.getOptionsArrayList(), fieldObj.getValue()), true);
                          }
                          runAddFunctionInBackground(position,fieldObj.getAddFunction(),fieldObj.getValue());

                      }else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                          TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                          mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                          runAddFunctionInBackground(position,fieldObj.getAddFunction(),fieldObj.getValue());
                      }else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                          EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                          mHolder.etTxtArea.setText(fieldObj.getValue());
                          runAddFunctionInBackground(position,fieldObj.getAddFunction(),fieldObj.getValue());
                      }else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                          final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;
                          if (!fieldObj.getValue().isEmpty()) {
                              mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
                          }
                          runAddFunctionInBackground(position,fieldObj.getAddFunction(),fieldObj.getValue());

                      } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                          super.onBindViewHolder(holder, position, payloads);
                      }else if(getItemViewType(position) == Constant.TYPE_EDIT_TEXT){
                          EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                          mHolder.editText.setText(fieldObj.getValue());
                          runAddFunctionInBackground(position,fieldObj.getAddFunction(),fieldObj.getValue());
                      }

                  }else if (payload.equals(Constant.PAYLOAD_TEXT)) {
                      if (getItemViewType(position) == Constant.TYPE_EDIT_DATE)
                      {
                          DateViewHolder mHolder = (DateViewHolder) holder;
                          mHolder.etDate.setText(fieldObj.getValue());

                      }else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                          super.onBindViewHolder(holder, position, payloads);
                      }else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                          TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                          mHolder.autoCompleteTextView.setText(fieldObj.getValue());

                      }else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                          EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                          mHolder.etTxtArea.setText(fieldObj.getValue());

                      }else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                          final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;
                          if (!fieldObj.getValue().isEmpty()) {
                              mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
                          }
                      } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                          super.onBindViewHolder(holder, position, payloads);
                      }else if(getItemViewType(position) == Constant.TYPE_EDIT_TEXT){
                          EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                          mHolder.editText.setText(fieldObj.getValue());
                      }

                  }else if(payload.equals(Constant.PAYLOAD_READ_EDIT)) {
                      if (getItemViewType(position) == Constant.TYPE_EDIT_DATE)
                      {
                          DateViewHolder mHolder = (DateViewHolder) holder;
                          mHolder.etDate.setText(fieldObj.getValue());
                          readEditableHelper.dlistReadableEditableEditText(fieldObj.isReadOnly(), mHolder);

                      }else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                          super.onBindViewHolder(holder, position, payloads);
                      }else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                          TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                          mHolder.autoCompleteTextView.setReadOnly(fieldObj.isReadOnly());
                          mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                          readEditableHelper.dlistReadEditAutoCompleteTextView(fieldObj.isReadOnly(), mHolder);

                      }else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                          EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                          readEditableHelper.dlistReadEditTextArea(fieldObj.isReadOnly(), mHolder);
                      }else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                          final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;
                          if (!fieldObj.getValue().isEmpty()) {
                              mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
                          }
                          readEditableHelper.dlistReadEditCheckBox(fieldObj.isReadOnly(), mHolder);
                      } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                          super.onBindViewHolder(holder, position, payloads);
                      }else if(getItemViewType(position) == Constant.TYPE_EDIT_TEXT){
                          EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                          mHolder.editText.setText(fieldObj.getValue());
                          readEditableHelper.dlistReadEditTextField(fieldObj.isReadOnly(),mHolder);
                      }
                  }else if (payload.equals(Constant.PAYLOAD_REQUIRED_NOTREQUIRED)) {
                      if (getItemViewType(position) == Constant.TYPE_EDIT_DATE)
                      {
                          DateViewHolder mHolder = (DateViewHolder) holder;
                          mHolder.etDate.setText(fieldObj.getValue());
                          saveRequiredHelper.dlistDateSetRequired(fieldObj.getSaveRequired(),
                                  fieldObj.getFieldName(),
                                  mHolder);
                      }else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
                          SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
                          saveRequiredHelper.dlistSpinnerSetRequired(fieldObj.getSaveRequired(),
                                  fieldObj.getFieldName(),
                                  mHolder);
                      }else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE) {
                          TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
                          mHolder.autoCompleteTextView.setText(fieldObj.getValue());
                          saveRequiredHelper.dlistAutocompleteTextSetRequired(fieldObj.getSaveRequired(),
                                  fieldObj.getFieldName(),
                                  mHolder);
                      }else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
                          EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
                          mHolder.etTxtArea.setText(fieldObj.getValue());
                          saveRequiredHelper.dlistEditTextAreaSetRequired(fieldObj.getSaveRequired(),
                                  fieldObj.getFieldName(),
                                  mHolder);
                      }else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
                          final CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;
                          if (!fieldObj.getValue().isEmpty()) {
                              mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
                          }
                      } else if (getItemViewType(position) == Constant.TYPE_FILE) {
                          super.onBindViewHolder(holder, position, payloads);
                      }else if(getItemViewType(position) == Constant.TYPE_EDIT_TEXT){
                          EditTextViewHolder mHolder = (EditTextViewHolder) holder;
                          saveRequiredHelper.dlistEditTextSetRequired(fieldObj.getSaveRequired(),
                                  fieldObj.getFieldName(),
                                  mHolder);
                      }
                  }
              }
          }

    }

    private void runInBackground(final int position, final String addFunction, final String value) {
        mListener.runFunctions(position,addFunction,value);

    }

    private void runAddFunctionInBackground(final int position, final String addFunction, final String value) {
        mListener.onAddFunction(position,addFunction,value);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final DList fieldObj = fieldList.get(position);
        final String fieldName  = fieldObj.getFieldName();
        String finalValue = fieldObj.getDefaultValue();

        if (getItemViewType(position) == Constant.TYPE_EDIT_DATE)
        {
            DateViewHolder mHolder = (DateViewHolder) holder;
             if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                    SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                    mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                } else {
                    mHolder.txtView.setText(fieldName);
                }

             readEditableHelper.dlistReadableEditableEditText(fieldObj.isReadOnly(),mHolder);

//            if (fieldObj.isReadOnly()) {
//                mHolder.etDate.setFocusable(false);
//                mHolder.etDate.setCursorVisible(false);
//                mHolder.etDate.setTextColor(context.getResources().getColor(R.color.white));
//                mHolder.etDate.setBackgroundColor(context.getResources().getColor(R.color.read_only));
//            }

            if (fieldObj.isHidden()) {
                // hide the view
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            dfValueFormatter.setDefaultValue(fieldObj.getDefaultValue());
            dfValueFormatter.setFieldValue(fieldObj.getValue());


            String dtsr = dfValueFormatter.formatDateDefaultValue();
            if(!dtsr.isEmpty()){
                mListener.onValueChanged(position,dtsr,false);
            }

            //TODO : change format of the date when we fetch it from api
            mHolder.etDate.setText(dtsr);

            mHolder.etDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fieldObj.getFieldType().toLowerCase().matches("datetime|datetime-local")){
                        customDateTimePickerListener.loadCustomDateTimePicker(position,"");
                    }else{
                        mListener.loadDatePicker(position);
                    }
                }
            });

        }else if (getItemViewType(position) == Constant.TYPE_SPINNER) {
           SpinnerViewHolder mHolder = (SpinnerViewHolder) holder;
            if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
            } else {
                mHolder.txtView.setText(fieldName);
            }

            if (fieldObj.isHidden()) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            List<OptionModel> optionsArrayList = new ArrayList<>();
            optionsArrayList = fieldObj.getOptionsArrayList();

            if (optionsArrayList != null && !optionsArrayList.isEmpty()) {
                ArrayAdapter<OptionModel> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, optionsArrayList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                mHolder.spinner.setAdapter(adapter);
            } else {
             //   mListener.loadSpinner(position, fieldObj.getOnclickrightbutton());
            }


            final List<OptionModel> finalOptionsArrayList = optionsArrayList;
            mHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    if(!parent.getSelectedItem().toString().toLowerCase().equals("< select >") ||
                            !parent.getSelectedItem().toString().toLowerCase().equals("select") ||
                            !parent.getSelectedItem().toString().toLowerCase().equals("") ){

                        if(isUserAction) {
                            mListener.onValueChanged(position, finalOptionsArrayList.get(pos).getId(),true);

                            runAddFunctionInBackground(position,fieldObj.getAddFunction(),"");
                            isUserAction = false;
                       }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            if(!fieldObj.getValue().isEmpty()) {
              //  Log.e(DEBUG_TAG, "TYPE_SPINNER = FIELD = " + fieldObj.getField_name()+ " - "+ fieldObj.getValue());
                mHolder.spinner.setSelection(SpinnerHelper.getIndex(fieldObj.getOptionsArrayList(),fieldObj.getValue()),true);
            }

        }   else if (getItemViewType(position) == Constant.TYPE_ITEM_AUTO_COMPLETE)
        {



            final TextAutoCompleteViewHolder mHolder = (TextAutoCompleteViewHolder) holder;
            if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
            } else {
                mHolder.txtView.setText(fieldName);
            }
          //  mHolder.autoCompleteTextView.setText(fieldObj.getValue());

            if (fieldObj.isHidden()) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }


            dfValueFormatter.setDefaultValue(fieldObj.getDefaultValue());
            dfValueFormatter.setFieldValue(fieldObj.getValue());

            String fValue = dfValueFormatter.getFormattedValue();
            mListener.onValueChanged(position,fValue,false);
            mHolder.autoCompleteTextView.setText(fValue);
            //// COMMENTED - APRIL 12 2O21
//            if(FormFragment.RECORD_ID_REF.equals("0")){
//            if(!fieldObj.getValue().isEmpty()){
//                    runAddFunctionInBackground(position,fieldObj.getAddFunction(),fieldObj.getValue());
//                }
//            }

            if(fieldObj.isReadOnly()){
                mHolder.relativeLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.autocomplete_bg_black));
                mHolder.autoCompleteTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
                mHolder.autoCompleteTextView.setEnabled(false);
                mHolder.autoButton.setEnabled(false);

            }else{
                mHolder.relativeLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.bottom_lne_bg));
                mHolder.autoCompleteTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
                mHolder.autoCompleteTextView.setEnabled(true);
                mHolder.autoButton.setEnabled(true);
            }

            mHolder.autoCompleteTextView.setReadOnly(fieldObj.isReadOnly());

            mHolder.autoCompleteTextView.setOnClearListener(new ClearableAutoCompleteTextView.OnClearListener() {
                @Override
                public void onClear() {
                    mListener.onValueChanged(position, "",true);

                }
            });
            mHolder.autoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHolder.autoCompleteTextView.requestFocus();
                    mListener.onClickRightButton(position,fieldObj.getDropDownClick(),
                            fieldObj.getFieldType());
                }
            });

        }else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
            EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
            if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                    SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                    mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                } else {
                    mHolder.txtView.setText(fieldName);
                }

            if (fieldObj.isHidden()) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            if (fieldObj.isReadOnly()) {
                mHolder.etTxtArea.setFocusable(false);
                mHolder.etTxtArea.setCursorVisible(false);
                mHolder.etTxtArea.setTextColor(context.getResources().getColor(R.color.white));
                mHolder.etTxtArea.setBackgroundColor(context.getResources().getColor(R.color.read_only));
            }

            dfValueFormatter.setDefaultValue(fieldObj.getDefaultValue());
            dfValueFormatter.setFieldValue(fieldObj.getValue());

            String fValue = dfValueFormatter.getFormattedValue();
            mListener.onValueChanged(position,fValue,false);
            mHolder.etTxtArea.setText(fValue);




        } else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
          CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;

            mHolder.checkBox.setText(fieldName);
            if (fieldObj.isHidden()) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            if(fieldObj.isReadOnly()){
                mHolder.checkBox.setEnabled(false);
                mHolder.checkBox.setTextColor(Color.GRAY);
                CompoundButtonCompat.setButtonTintList(mHolder.checkBox, ColorStateList.valueOf(Color.GRAY));
            }else{
                mHolder.checkBox.setEnabled(true);
                mHolder.checkBox.setTextColor(Color.BLACK);
                CompoundButtonCompat.setButtonTintList(mHolder.checkBox, ColorStateList.valueOf(Color.BLACK));
            }

            if(!fieldObj.getValue().isEmpty()) {
                mHolder.checkBox.setChecked(Boolean.parseBoolean(fieldObj.getValue()));
            }

            mHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String value = String.valueOf(isChecked);
//                    Log.e("==CHECKBOX", "isChecked =" + value);
                    mListener.onValueChanged(position, value,true);
                    runAddFunctionInBackground(position,fieldObj.getAddFunction(),"");
                }
            });

            runAddFunctionInBackground(position,fieldObj.getAddFunction(),"");

        }

        else if (getItemViewType(position) == Constant.TYPE_FILE){
            FileViewHolder mHolder = (FileViewHolder) holder;
            if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
            } else {
                mHolder.txtView.setText(fieldName);
            }

            //check if hidden field
            if (fieldObj.getType().equalsIgnoreCase("hidden")) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                if(fieldObj.getStates().contains("s111s")){

                }
            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

//            if (fieldObj.isReadOnly()) {
//                /// disable button for readonly
//            }
            mHolder.bindFileTypeView(fieldObj,position);

        }else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT){
            final EditTextViewHolder mHolder = (EditTextViewHolder) holder;

            if (fieldObj.isHidden()) {
                // hide the view
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            if (!fieldObj.isHidden()) {

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
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            fieldObj.setErrorMessage("");
//                            notifyItemChanged(position);
//                        }
//                    },2000);
                } else {
                    if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                        SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                        mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                        mHolder.txtView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    } else {
                        mHolder.txtView.setText(fieldName);

                    }
                }
                readEditableHelper.dlistReadEditTextField(fieldObj.isReadOnly(),mHolder);
                dfValueFormatter.setDefaultValue(fieldObj.getDefaultValue());
                dfValueFormatter.setFieldValue(fieldObj.getValue());

                String fValue = dfValueFormatter.getFormattedValue();
                mListener.onValueChanged(position,fValue,false);
                mHolder.editText.setText(fValue);
            }

                // april 15 commented this for now
                //Credit Debit Note - Cndn rate was not showing values hence uncommented this
            if(FormFragment.RECORD_ID_REF.equals("0")) {
                String formId = mPrefManager.getFormId();
                //We dont want to run the function in few forms hence the condition
                if(!formId.matches("1931|2443|3520")){
                    runAddFunctionInBackground(position,fieldObj.getAddFunction(),"");
                }
            }

        }

//        if (position == fieldList.size() - 1){
//            onBottomReachedListener.onBottomReached(position);
//        }
    }

    private String[] getTextList(String fieldName) {
        return new String[]{fieldName, " *"};
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String fieldType = fieldList.get(position).getFieldType();

        if (fieldType != null && fieldType.toLowerCase().matches("double|long")) {
            return Constant.TYPE_EDIT_TEXT;

        } else if (fieldType != null && fieldType.toLowerCase().matches("date|datetime|datetime-local")) {
            return Constant.TYPE_EDIT_DATE;

        } else if (fieldType.toLowerCase().matches("tag|adcombo|dcombo|mdcombo")) {
            return Constant.TYPE_ITEM_AUTO_COMPLETE;

        } else if (fieldType.toLowerCase().matches("textarea")) {
            return Constant.TYPE_EDIT_TEXT_AREA;

        } else if (fieldType.toLowerCase().matches("checkbox|boolean")) {
            return Constant.TYPE_CHECKBOX;

        } else if (fieldType.toLowerCase().matches("tabs|combo|select")) {
            return Constant.TYPE_SPINNER;
        } else if (fieldType.equalsIgnoreCase("file")) {
            return Constant.TYPE_FILE;
        } else {
            return Constant.TYPE_EDIT_TEXT;
        }
    }

    //setting true of false
    // the functions are getting called many times hence maintaining a boolean flag
    //setting the flag to false, when we don't want to call the functions
    public void setFlag(boolean callFunction){
        isCallFunction = callFunction;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class EditTextViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentlayout;
        public TextView txtView;
        public EditText editText;
        long delay = 2500; // 2.5 seconds after user stops typing
        long last_text_edit = 0;
        Handler handler = new Handler();

//        private Runnable input_finish_checker = new Runnable() {
//            public void run() {
//                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
//                    if(getAdapterPosition() !=-1){
//
//                        //Apr 09commented this for now as its slow
//                      /*  mListener.onValueChanged(getAdapterPosition(), editText.getText().toString());
//                        mListener.onAddFunction(getAdapterPosition(),
//                                fieldList.get(getAdapterPosition()).getAddFunction(),
//                                editText.getText().toString()); */
//                    }
//                }
//            }
//        };

        public EditTextViewHolder(@NonNull View itemView) {
            super(itemView);
            parentlayout = itemView.findViewById(R.id.parent);
            txtView = itemView.findViewById(R.id.text_view);
            editText = itemView.findViewById(R.id.edit_text);

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_DONE:
                            Log.e("EDITTEXT", "IME_ACTION_DONE");
                            mListener.onValueChanged(getAdapterPosition(), editText.getText().toString(),true);
                            runAddFunctionInBackground(getAdapterPosition(),fieldList.get(getAdapterPosition()).getAddFunction(),editText.getText().toString());
//                            mListener.onAddFunction(getAdapterPosition(),
//                                    fieldList.get(getAdapterPosition()).getAddFunction(),
//                                    editText.getText().toString());
                            KeyboardUtil.hideKeyboard((Activity) context);
                            return true;

                        case EditorInfo.IME_ACTION_NEXT:
                            Log.e("EDITTEXT", "IME_ACTION_NEXT");
//                            mListener.onValueChanged(getAdapterPosition(), editText.getText().toString());
//                            mListener.onAddFunction(getAdapterPosition(),
//                                    fieldList.get(getAdapterPosition()).getAddFunction(),
//                                    editText.getText().toString());
                            return true;

                        case EditorInfo.IME_ACTION_PREVIOUS:
                            Log.e("EDITTEXT", "IME_ACTION_PREVIOUS");
//                            mListener.onValueChanged(getAdapterPosition(), editText.getText().toString());
//                            mListener.onAddFunction(getAdapterPosition(),
//                                    fieldList.get(getAdapterPosition()).getAddFunction(),
//                                    editText.getText().toString());
                            return true;
                    }
                    return false;
                }
            });




//            editText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                    if(s.length() > 0){
////                        String addFunction = fieldList.get(getAdapterPosition()).getAddFunction();
////                        if(addFunction.contains("checkpattern")){
////                            FormFragment.fieldsList.get(fieldPostion).
////                                    getdListArray().get(mDlistButtonArrayPosition)
////                                    .getDListItemList()
////                                    .get(mListObjPosition)
////                                    .getDlistArray()
////                                    .get(getAdapterPosition())
////                                    .setValue(s.toString());
////
////                            mListener.onAddFunction(getAdapterPosition(),
////                                    fieldList.get(getAdapterPosition()).getAddFunction(),
////                                    s.toString());
////                        }
//
//                            last_text_edit = System.currentTimeMillis();
//                            handler.postDelayed(input_finish_checker, delay);
//
//                    }
//                }
//            });
        }
    }

    public class SpinnerViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        Spinner spinner;

        public SpinnerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
            spinner = itemView.findViewById(R.id.spinner);

            spinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
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

        }
    }

    public class CheckboxViewHolder extends RecyclerView.ViewHolder {
       public CheckBox checkBox;
        public CheckboxViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);


            checkBox.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
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
//            if(getAdapterPosition() != -1){
//                mListener.onAddFunction(getAdapterPosition(),
//                        fieldList.get(getAdapterPosition()).getAddFunction(),"");
//            }
        }
    }

    public class EditTextAreaViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        public EditText etTxtArea;

        public EditTextAreaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
            etTxtArea = itemView.findViewById(R.id.edit_text_area);

//            if(getAdapterPosition() != -1){
//                mListener.onAddFunction(getAdapterPosition(),
//                        fieldList.get(getAdapterPosition()).getAddFunction(),"");
//            }
        }
    }

    class SummaryViewHolder extends RecyclerView.ViewHolder {
        Button btnSummary;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            btnSummary = itemView.findViewById(R.id.button_summary);
        }
    }

    public class TextAutoCompleteViewHolder extends RecyclerView.ViewHolder
            implements ClearableAutoCompleteTextView.OnClearListener {
        public TextView txtView;
       public ClearableAutoCompleteTextView autoCompleteTextView;
       public ImageButton autoButton;
       public RelativeLayout relativeLayout;

        public TextAutoCompleteViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rel_layout);
            autoCompleteTextView = itemView.findViewById(R.id.autocomplete);
            autoCompleteTextView.setThreshold(1);
            autoCompleteTextView.setOnClearListener(this);
            txtView = itemView.findViewById(R.id.text_view);
            autoButton = itemView.findViewById(R.id.auto_button);

            autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_DONE:
                            Log.e("autoCompleteTextView", "DLIST IME_ACTION_DONE");
                                if (getAdapterPosition() != -1) {
                                    mListener.onValueChanged(getAdapterPosition(), autoCompleteTextView.getText().toString(),false);
                                }
                            KeyboardUtil.hideKeyboard((Activity) context);
                            return true;
                        case EditorInfo.IME_ACTION_NEXT:
                            if (getAdapterPosition() != -1) {
                                mListener.onValueChanged(getAdapterPosition(), autoCompleteTextView.getText().toString(),false);

                            }
                            KeyboardUtil.hideKeyboard((Activity) context);
                            return true;

                        case EditorInfo.IME_ACTION_PREVIOUS:
                            if (getAdapterPosition() != -1) {
                                mListener.onValueChanged(getAdapterPosition(), autoCompleteTextView.getText().toString(),false);
                            }
                            KeyboardUtil.hideKeyboard((Activity) context);
                            return true;
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClear() {
            mListener.onValueChanged(getAdapterPosition(), "",true);
        }
    }

   public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtView;
        Button btnChooseFile;
        public TextView txtFileName;
        DList fieldObj;
        int position;
        View view;

        public FileViewHolder(View v) {
            super(v);
            txtView = itemView.findViewById(R.id.text_view);
            btnChooseFile = itemView.findViewById(R.id.btn_choose_file);
            txtFileName = itemView.findViewById(R.id.txt_file_name);
            btnChooseFile.setOnClickListener(this);
            txtFileName.setOnClickListener(this);

        }

        private void bindFileTypeView(DList dObj, final int position) {
            this.fieldObj = dObj;
            this.position = position;

            String fileName = fileHelper.getAttachedFileName(fieldObj.getValue());
            setupHyperlink(fileName);

        }

        private void setupHyperlink(String fileName) {
            String extension = ExtensionUtil.getExtension(fileName);
            if(!extension.isEmpty()){
                txtFileName.setText(Html.fromHtml("<font color=#000000>  <u>" + fileName + "</u>  </font>"));
                //txtFileName.setMovementMethod(LinkMovementMethod.getInstance());
            }else{
                txtFileName.setText(fileName);
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_choose_file){
                mListener.pickFile(position);
            }
            else if (v.getId() == R.id.txt_file_name){
               // FileHelper fileHelper = new FileHelper((AppCompatActivity) context);
                SharedPrefManager prefManager = new SharedPrefManager(context);
                fileHelper.viewFile(fieldObj.getValue(),false);

            }
        }
    }
}
