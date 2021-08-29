package com.example.myapplication.user_interface.pendingtask;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.dlist.view.TabDlistActivity;
import com.example.myapplication.user_interface.summary.SummaryActivity;
import com.example.myapplication.user_interface.forms.controller.helper.FileHelper;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.customviews.ClearableAutoCompleteTextView;
import com.example.myapplication.Constant;
import com.example.myapplication.util.SpannableStringBuilderUtil;
import com.example.myapplication.util.ToastUtil;
import java.util.List;

public class PendingTaskDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Field> fieldList;
    private FileHelper fileHelper;

    public PendingTaskDetailsAdapter(Context context, List<Field> fieldList) {
        this.mContext = context;
        this.fieldList = fieldList;
        fileHelper = new FileHelper((AppCompatActivity) context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Constant.TYPE_CALL) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_call, parent, false);
            return new PhoneViewHolder(view);

        }  else if (viewType == Constant.TYPE_EDIT_TEXT_AREA) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_edit_textarea,
                    parent, false);
            return new EditTextAreaViewHolder(view);

        }else if (viewType == Constant.TYPE_CHECKBOX) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_checkbox,
                    parent, false);
            return new CheckboxViewHolder(view);

        } else if (viewType == Constant.TYPE_DLIST) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_dlist,
                    parent, false);
            return new DlistViewHolder(view);
        } else if (viewType == Constant.TYPE_FILE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_file,
                    parent, false);
            return new FileViewHolder(view);
        }  else if (viewType == Constant.TYPE_SUMMARY) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_summary,
                    parent, false);
            return new SummaryViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_uneditable_text, parent, false);
            return new EditTextViewHolder(view);
        }


        /*
        else if (viewType == Constant.TYPE_VLIST) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_form_vlist, parent, false);
            return new VlistViewHolder(view);

        }*/
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final Field fieldObj = fieldList.get(position);
        final String fieldName = fieldObj.getFieldName();
        final String fieldValue = fieldObj.getValue();


        if (getItemViewType(position) == Constant.TYPE_CALL) {
            final PhoneViewHolder mHolder = (PhoneViewHolder) holder;
            if (fieldValue.equals("")) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            mHolder.txtView.setText(fieldName);
            final String mobileNo = fieldValue.replace("CALL@@:", "");
            mHolder.editText.setText(mobileNo);

            mHolder.btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("tel:" + mobileNo);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
                    try {
                        mContext.startActivity(callIntent);
                    } catch (ActivityNotFoundException activityNotFoundException) {
                        // TODO: place code to handle users that have no call application installed, otherwise the app crashes
                        ToastUtil.showToastMessage("No application installed", mContext);
                    }
                }
            });

        } else if (getItemViewType(position) == Constant.TYPE_DLIST) {
            final DlistViewHolder mHolder = (DlistViewHolder) holder;
            mHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TabDlistActivity.class);
                    intent.putExtra(Constant.EXTRA_ENTRY,"pendingTask");
                    mContext.startActivity(intent);
                }
            });
        }
        else if (getItemViewType(position) == Constant.TYPE_SUMMARY) {
            SummaryViewHolder mHolder = (SummaryViewHolder) holder;
            if (fieldObj.getType().equalsIgnoreCase("hidden")) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            mHolder.btnSummary.setText(fieldName);

            mHolder.btnSummary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.EXTRA_OBJECT, fieldObj);
                    bundle.putString(Constant.EXTRA_ENTRY, "form");
                    bundle.putString(Constant.EXTRA_DATA, JsonUtil.objectToJson(fieldObj));
                    Intent intent = new Intent(mContext, SummaryActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

        }else if (getItemViewType(position) == Constant.TYPE_EDIT_TEXT_AREA) {
           EditTextAreaViewHolder mHolder = (EditTextAreaViewHolder) holder;
            if (fieldObj.showErrorMessage()) {
                boolean isInvalid = false;
                if(fieldObj.getErrorMessage().toLowerCase().equals("enter a valid")){
                    isInvalid = true;
                }
                SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getErrorTextList(fieldName,isInvalid), Constant.ERROR_COLORS);
                mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                mHolder.txtView.setBackgroundColor(mContext.getResources().getColor(R.color.orange_light));
                //mHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.red_light));
            } else {
                mHolder.txtView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                if (fieldObj.getSaveRequired().toLowerCase().matches("true")) {
                    SpannableStringBuilder builder = SpannableStringBuilderUtil.appendString(getTextList(fieldName), Constant.COLORS);
                    mHolder.txtView.setText(builder, TextView.BufferType.SPANNABLE);
                } else {
                    mHolder.txtView.setText(fieldName);
                }
            }

            mHolder.etTxtArea.setText(fieldObj.getValue());
            if (fieldObj.getType().equalsIgnoreCase("hidden")) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

        } else if (getItemViewType(position) == Constant.TYPE_CHECKBOX) {
            CheckboxViewHolder mHolder = (CheckboxViewHolder) holder;

            mHolder.checkBox.setText(fieldName);
            if (fieldObj.getType().equalsIgnoreCase("hidden")) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            mHolder.checkBox.setText(fieldName);

        } else if (getItemViewType(position) == Constant.TYPE_FILE){
            FileViewHolder mHolder = (FileViewHolder) holder;

            //check if hidden field
            if (fieldObj.getType().equalsIgnoreCase("hidden")) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            if (fieldObj.isReadOnly()) {
                /// disable button for readonly
            }
            mHolder.txtView.setText(fieldName);
            String fileName = fileHelper.getAttachedFileName(fieldObj.getValue());
            mHolder.txtFileName.setText(fileName);

        }else {

            final EditTextViewHolder mHolder = (EditTextViewHolder) holder;
            mHolder.txtView.setText(fieldName);
            mHolder.editText.setText(fieldValue);
        }




        /*
        * else if (getItemViewType(position) == Constant.TYPE_VLIST) {

            final VlistViewHolder mHolder = (VlistViewHolder) holder;
            if (fieldValue.equals("")) {
                mHolder.itemView.setVisibility(View.GONE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                mHolder.itemView.setVisibility(View.VISIBLE);
                mHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            mHolder.txtView.setText(fieldName);

            mHolder.btnShowVList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SummaryActivity.class);
                    intent.putExtra(Constant.EXTRA_ENTRY, "PendingTask");
                    intent.putExtra(Constant.EXTRA_TITLE, fieldName);
                    intent.putExtra(Constant.EXTRA_VLIST, fieldValue);
                    mContext.startActivity(intent);
                }
            });

        }
        * */
    }
    private String[] getTextList(String fieldName) {
        return new String[]{fieldName, " *"};
    }

    private String[] getErrorTextList(String fieldName, boolean isInvalid) {
        if(isInvalid){
            return new String[]{fieldName, " * ","[INVALID DATA]"};
        }else{
            return new String[]{fieldName, " * ", "[REQUIRED]"};
        }
    }

    @Override
    public int getItemViewType(int position) {
        String fieldType = fieldList.get(position).getDataType();

        if (fieldType.equalsIgnoreCase("TEXTAREA")) {
            return Constant.TYPE_EDIT_TEXT_AREA;
        } else if (fieldType.equalsIgnoreCase("checkbox")) {
            return Constant.TYPE_CHECKBOX;
        } else if (fieldType.toLowerCase().matches("summary|vlist")){
            return Constant.TYPE_SUMMARY;
        } else if (fieldType.equalsIgnoreCase("DLIST")) {
            return Constant.TYPE_DLIST;
        }  else if (fieldType.equalsIgnoreCase("file")) {
            return Constant.TYPE_FILE;
        }if (fieldType.toLowerCase().equals("call")) {
            return Constant.TYPE_CALL;
        } else {
            return Constant.TYPE_EDIT_TEXT;
        }


//        if (fieldType.toLowerCase().equals("call")) {
//            return Constant.TYPE_CALL;
//        } else if (fieldType.toLowerCase().equals("dlist")) {
//            return Constant.TYPE_VLIST;
//        } else if (fieldType.toLowerCase().matches("vlist|summary")) {
//            return Constant.TYPE_VLIST;
//        } else {
//            return Constant.TYPE_EDIT_TEXT;
//        }
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    public class EditTextViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;
        EditText editText;

        public EditTextViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
            editText = itemView.findViewById(R.id.edit_text);
        }
    }

    class VlistViewHolder extends RecyclerView.ViewHolder {

        Button btnShowVList;
        TextView txtView;

        public VlistViewHolder(View v) {
            super(v);
            txtView = itemView.findViewById(R.id.text_view);
            btnShowVList = v.findViewById(R.id.btn_show_vlist);

        }
    }

    class PhoneViewHolder extends RecyclerView.ViewHolder {

        ImageButton btnCall;
        TextView txtView;
        EditText editText;


        public PhoneViewHolder(View v) {
            super(v);
            txtView = itemView.findViewById(R.id.text_view);
            btnCall = v.findViewById(R.id.btn_call);
            editText = itemView.findViewById(R.id.edit_text);
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

    class FileViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;
        Button btnChooseFile;
        TextView txtFileName;

        public FileViewHolder(View v) {
            super(v);
            txtView = itemView.findViewById(R.id.text_view);
            btnChooseFile = itemView.findViewById(R.id.btn_choose_file);
            txtFileName = itemView.findViewById(R.id.txt_file_name);
        }
    }

    class EditTextAreaViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;
        EditText etTxtArea;


        public EditTextAreaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.text_view);
            etTxtArea = itemView.findViewById(R.id.edit_text_area);
            etTxtArea.setFocusable(false);
            etTxtArea.setCursorVisible(false);
        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {

        TextView txtView;
        EditText etDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);

            txtView = itemView.findViewById(R.id.text_view);
            etDate = itemView.findViewById(R.id.edit_text_date);


        }
    }

    class CheckboxViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;

        public CheckboxViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    class TextAutoCompleteViewHolder extends RecyclerView.ViewHolder  {
        TextView txtView;
        ClearableAutoCompleteTextView autoCompleteTextView;
        ImageButton autoButton;

        public TextAutoCompleteViewHolder(@NonNull View itemView) {
            super(itemView);
            autoCompleteTextView = itemView.findViewById(R.id.autocomplete);
            autoCompleteTextView.setThreshold(1);
            txtView = itemView.findViewById(R.id.text_view);
            autoButton = itemView.findViewById(R.id.auto_button);


        }


    }

}
