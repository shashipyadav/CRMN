package com.example.myapplication.user_interface.pendingtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.R;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.user_interface.forms.controller.CommunicatorInterface;
import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.example.myapplication.Constant;

public class PendingTaskDetailsActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = PendingTaskDetailsActivity.class.getName();
    private String FORM_ID = "";
    private int RECORD_ID = 0;
    private String MODULE = "";
    private String  USERNAME = "";
    private String FORM_NAME = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_task_details);
        overridePendingTransition( R.anim.enter_from_right, R.anim.exit_to_left);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            FORM_ID = String.valueOf(bundle.getInt(Constant.EXTRA_FORM_ID));
            RECORD_ID = bundle.getInt(Constant.EXTRA_RECORD_ID);
            MODULE = bundle.getString(Constant.EXTRA_MODULE);
            USERNAME = bundle.getString(Constant.EXTRA_USER_NAME);
            FORM_NAME = bundle.getString(Constant.EXTRA_FORM_NAME);

            SharedPrefManager prefManager = new SharedPrefManager(this);
            String chartId = prefManager.getString(FORM_ID);

            Bundle args = new Bundle();
            args.putString(Constant.EXTRA_TITLE, FORM_NAME);
            args.putString(Constant.EXTRA_FORM_ID, FORM_ID);
            args.putString(Constant.EXTRA_CHART_ID, chartId);
            args.putString(Constant.EXTRA_RECORD_ID, String.valueOf(RECORD_ID));
            args.putString(Constant.EXTRA_ENTRY,"pending_task");

            Fragment fragment = new FormFragment(communicatorInterface);
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.pending_task_content_frame, fragment).commit();
        }
        setActionBar();
    }

    private void setActionBar(){
      /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

*/
       Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    CommunicatorInterface communicatorInterface = new CommunicatorInterface() {
        @Override
        public void respond() {
            onBackPressed();
        }
    };

}
