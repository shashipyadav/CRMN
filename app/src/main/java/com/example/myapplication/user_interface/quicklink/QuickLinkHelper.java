package com.example.myapplication.user_interface.quicklink;

import android.content.Context;

import com.example.myapplication.helper.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuickLinkHelper {
    private Context context;
    private String title;
    private String chartId;
    private SharedPrefManager mPrefManager;

    public QuickLinkHelper(Context context){
        this.context = context;
        mPrefManager = new SharedPrefManager(this.context);
    }

    public boolean checkIfSavedInQuickLinks() {
        boolean isAlreadySaved = false;
        List<QuickLink> quickLinkList = null;
        Gson gson = new Gson();
        String jsonText = mPrefManager.getQuickLinks();
        Type type = new TypeToken<List<QuickLink>>() {}.getType();
        quickLinkList = gson.fromJson(jsonText, type);

        if (quickLinkList != null) {
            for (int i = 0; i < quickLinkList.size(); i++) {
                QuickLink link = quickLinkList.get(i);
                if (link.getTitle().equals(getTitle())) {
                    //Log.e("QUICK_LINK","TITLE = "+ link.getTitle() + "onMenuClick = " +link.getMenuOnClick());
                    isAlreadySaved = true;
                }
            }
        }
        return isAlreadySaved;
    }

    public boolean saveQuickLink() {
        boolean isAlreadySaved = false;
        List<QuickLink> quickLinkList = null;
        Gson gson = new Gson();
        String jsonText = mPrefManager.getQuickLinks();
        Type type = new TypeToken<List<QuickLink>>() {
        }.getType();
        quickLinkList = gson.fromJson(jsonText, type);

        if (quickLinkList == null) {
            quickLinkList = new ArrayList<>();
            //save quicklink
            //Todo : change parameter from menuOnClick to chartID
              quickLinkList.add(new QuickLink(getChartId(), getTitle()));
            String jsonT = gson.toJson(quickLinkList);
            mPrefManager.setQuickLinks(jsonT);
        } else {
            //check if already saved
            //if not save it
            //boolean isAlreadySaved = false;
            int position = -1;
            for (int i = 0; i < quickLinkList.size(); i++) {
                QuickLink link = quickLinkList.get(i);
                if (link.getTitle().equals(getTitle())) {
                    //    Log.e("QUICK_LINK","TITLE = "+ link.getTitle() + "onMenuClick = " +link.getMenuOnClick());
                    isAlreadySaved = true;
                    position = i;
                }
            }

            if (!isAlreadySaved) {
                //save quicklink
                quickLinkList.add(new QuickLink(getChartId(), getTitle()));
                String jsonT = gson.toJson(quickLinkList);
                mPrefManager.setQuickLinks(jsonT);
            } else {
                if (position != -1) {
                    quickLinkList.remove(position);
                    String jsonT = gson.toJson(quickLinkList);
                    mPrefManager.setQuickLinks(jsonT);
                }
            }
        }
        return isAlreadySaved;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChartId() {
        return chartId;
    }

    public void setChartId(String chartId) {
        this.chartId = chartId;
    }
}
