package com.example.myapplication.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefManager {
    // Shared preference file name
    private static final String PREF_NAME = "Simbiosis";
    private static final String KEY_CLIENT_SERVER_URL = "client_server_url";
    private static final String KEY_CLOUD_CODE = "cloud_code";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_IS_USER_LOGGED_IN = "isUserLoggedIn";
    private static final String KEY_PROJECT_NAME = "projectName";
    private static final String KEY_MOBILE_NO = "mobileNumber";
    private static final String KEY_QUICK_LINKS = "quickLinks";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_Login = "user_logine";
    private static final String KEY_DASHBOARD_MENU = "dashboard_menu";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_FORM_ID = "form_id";
    private static final String KEY_VLIST_FORM_ID = "v_form_id";
    private static final String KEY_FIELD_POSITIONS = "field_positions";
    private static final String KEY_V_FIELD_POSITIONS = "vlist_field_position";
    private static final String KEY_SERVER_IMAGE_URL = "serverImageUrl";
    //Shared Preference
    private SharedPreferences pref;
    //Editor for Shared Preferences
    private SharedPreferences.Editor editor;
    //Context
    private Context mContext;
    //SharedPreference mode
    private int PRIVATE_MODE = 0;

    public SharedPrefManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setServerDetails(String clientServerUrl,
                                 String cloudCode,
                                 String serverImageUrl) {

        editor.putString(KEY_CLIENT_SERVER_URL, clientServerUrl);
        editor.putString(KEY_CLOUD_CODE, cloudCode);
        editor.putString(KEY_SERVER_IMAGE_URL,serverImageUrl );
        editor.commit();
    }

    public String getServerImageUrl() {
        return pref.getString(KEY_SERVER_IMAGE_URL, "");
    }

    public void saveUserDetails(boolean isLoggedIn,
                                String token,
                                String mobileNo,
                                String userName, String userID,String loginname) {
        editor.putBoolean(KEY_IS_USER_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.putString(KEY_MOBILE_NO, mobileNo);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ID, userID);
        editor.putString(KEY_USER_Login, loginname);
        editor.commit();
    }

    public String getFormId() {
        return pref.getString(KEY_FORM_ID, "");
    }

    public void setFormId(String formId) {
        editor.putString(KEY_FORM_ID, formId);
        editor.commit();
    }

    public String getVFormId() {
        return pref.getString(KEY_VLIST_FORM_ID, "");
    }

    public void setVFormId(String formId) {
        editor.putString(KEY_VLIST_FORM_ID, formId);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }

    public String getClientServerUrl() {
        return pref.getString(KEY_CLIENT_SERVER_URL, "");
    }

    public String getCloudCode() {
        return pref.getString(KEY_CLOUD_CODE, "");
    }

    public String getAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, "");
    }
    public String getKeyUserId() {
        return pref.getString(KEY_USER_ID, "");
    } public String getKeyUserLogin() {
        return pref.getString(KEY_USER_Login, "");
    }
    public String getKeyUsername() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public void setAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.commit();
    }

    public String getMobileNo() {
        return pref.getString(KEY_MOBILE_NO, "");
    }

    public String getQuickLinks() {
        return pref.getString(KEY_QUICK_LINKS, "");
    }

    public void setQuickLinks(String quickLinks) {
        editor.putString(KEY_QUICK_LINKS, quickLinks);
        editor.commit();
    }

    public boolean getIsUserLoggedIn() {
        return pref.getBoolean(KEY_IS_USER_LOGGED_IN, false);
    }

    public String getDashboardMenu() {
        return pref.getString(KEY_DASHBOARD_MENU, "");
    }

    public void setDashboardMenu(String dashboardMenu) {
        editor.putString(KEY_DASHBOARD_MENU, dashboardMenu);
        editor.commit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public String getFcmToken() {
        return pref.getString(KEY_FCM_TOKEN, "");
    }

    public void setFcmToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.commit();
    }

    public void setFieldPositionsToNotify(String positions) {
        Log.e("SHAREDPREF", "FIELD POSITIONS == > " + positions);
        editor.putString(KEY_FIELD_POSITIONS, positions);
        editor.commit();
    }

    public void setVFieldPositionsToNotify(String positions) {
        Log.e("SHAREDPREF", "VFIELD POSITIONS == > " + positions);
        editor.putString(KEY_V_FIELD_POSITIONS, positions);
        editor.commit();
    }

    public String getFieldPositions() {
        Log.e("SHAREDPREF", "getFieldPositions == > " + pref.getString(KEY_FIELD_POSITIONS, ""));
        return pref.getString(KEY_FIELD_POSITIONS, "");
    }

    public String geVFieldPositions() {
        Log.e("SHAREDPREF", "getVFieldPositions == > " + pref.getString(KEY_FIELD_POSITIONS, ""));
        return pref.getString(KEY_V_FIELD_POSITIONS, "");
    }

//    public HashMap<String, String> getServerDetails() {
//            HashMap<String, String> profile = new HashMap<>();
//            profile.put("name", pref.getString(KEY_NAME, null));
//            profile.put("email", pref.getString(KEY_EMAIL, null));
//            profile.put("mobile", pref.getString(KEY_MOBILE, null));
//            return profile;
//    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}

