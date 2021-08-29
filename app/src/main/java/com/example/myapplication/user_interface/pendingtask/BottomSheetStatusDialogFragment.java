package com.example.myapplication.user_interface.pendingtask;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.Constant;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetStatusDialogFragment extends BottomSheetDialogFragment  implements View.OnClickListener{

    private TextView txtTitle;
    private Button btnOk;
    private Button btnCancel;
    private StatusBottomSheetClickListener mListener;
    private String title = "";
    private String onClick = "";
    private SharedPrefManager mPrefManager;

        public BottomSheetStatusDialogFragment() {
        // Required empty public constructor
    }

    public BottomSheetStatusDialogFragment(StatusBottomSheetClickListener listener) {
        mListener = listener;
    }

    public interface StatusBottomSheetClickListener{
        void changeStatus(String title,String value);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            title = args.getString(Constant.EXTRA_TITLE);
            onClick = args.getString(Constant.EXTRA_CLICK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_bottom_sheet_status_dialog, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        mPrefManager = new SharedPrefManager(getActivity());
        txtTitle = root.findViewById(R.id.text_title);
        txtTitle.setText(title);
        btnOk = root.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(this);
        btnCancel = root.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnOk){
            mListener.changeStatus(title,onClick);
            dismiss();
        }
        if(v == btnCancel){
            dismiss();
        }
    }
}
