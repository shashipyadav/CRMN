package com.example.myapplication.user_interface.vdlist;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.myapplication.Constant;
import com.example.myapplication.R;
import com.example.myapplication.user_interface.dlist.view.TabDlistActivity;
import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.vlist.VlistFormActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TabVDlistActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = TabVDlistActivity.class.getSimpleName();
    List<String> tabTitles = new ArrayList<>();
    String entryFrom = "";
    List<String> fieldIds = new ArrayList<>();
    List<Integer> dlistButtonPositions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_dlist);
        setActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            entryFrom = bundle.getString(Constant.EXTRA_ENTRY);
        }

        //  FunctionHelper functionHelper = new FunctionHelper(this);
        //  functionHelper.onLoadChangeIds("");

        tabTitles = new ArrayList<>();
        fieldIds = new ArrayList<>();
        dlistButtonPositions = new ArrayList<>();
        displayDlistFieldNames();
    }

    private void displayDlistFieldNames() {

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        try {
            int dlistArrPosition = VlistFormActivity.vDlistArrayPosition;
            List<Field> dlistArray = VlistFormActivity.vFieldsList.get(dlistArrPosition)
                    .getdListArray();
            for (int k = 0; k < dlistArray.size(); k++) {
                Field dlistField = dlistArray.get(k);

                if(!dlistField.isHidden()) {
                    fieldIds.add(dlistField.getId());
                    tabTitles.add(dlistField.getFieldName());
                    dlistButtonPositions.add(k);
                    tabLayout.addTab(tabLayout.newTab().setText(dlistField.getFieldName()));
                }
            }

            DlistPagerAdapter adapter = new DlistPagerAdapter(getSupportFragmentManager(),
                    tabLayout.getTabCount(), tabTitles,fieldIds,dlistButtonPositions);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(1);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            // If your tab layout has more than 3 tabs then
            // tab will scroll other wise they will take whole width of the screen
//        if (tabLayout.getTabCount() == 3) {
//            tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        } else {
//            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setActionBar(){
        getSupportActionBar().setTitle("Tabs");
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

    public static class DlistPagerAdapter extends FragmentStatePagerAdapter {

        int mNumOfTabs;
        List<String> mTabTitles;
        List<String> mFieldIds;
        List<Integer> dlistButtonPositions;

        public DlistPagerAdapter(FragmentManager fm,
                                 int NumOfTabs, List<String> tabTitles,
                                 List<String> fieldIds,
                                 List<Integer> dlistButtonPositions) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
            this.mTabTitles = tabTitles;
            this.mFieldIds = fieldIds;
            this.dlistButtonPositions = dlistButtonPositions;
        }

        @Override
        public Fragment getItem(int position) {

            return  VDlistFragment.newInstance(dlistButtonPositions.get(position),mFieldIds.get(position));
        }

        @Override
        public int getCount() {
            return mTabTitles.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles.get(position);
        }
    }
}

