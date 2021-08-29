package com.example.myapplication;

import android.graphics.Color;

public class Constant {

    public static final String URL_SERVER_LIST          = "https://strategicerpcloud.com/strategicerp/getFunction.do?actn=getsqljsondatawebsite&sqlfieldid=38205&ids=mob/maname&valuestring=%1$s@j@StrategicERP";
    public static final String URL_GET_OTP              = "%1$slogin.do?actn=generateotpapp&mobileno=%2$s&cloudcode=%3$s";
    public static final String URL_VALIDATE_OTP         = "%1$slogin.do?actn=generateotpapp&mobileno=%2$s&otp=%3$s&gcmid=&iosid=&cloudcode=%4$s";
    public static final String URL_UPDATE_DEVICE_INFO   = "%1$spages/myess.jsp?action=registerfcmtokenforb2b&token=%2$s&cloudcode=%3$s&fcmtoken=%4$s&device=android&detail=%5$s";
    public static final String MENU_URL                 = "%1$sgetFormLink.do?actn=getChartLinkLeft&lastlocalstoragetime=%2$s&cloudcode=%3$s&token=%4$s&type=json";
    public static final String FORM_URL                 = "%1$sgetFunction.do?actn=getapphtmljs&chartid=%2$s&lastlocalstoragetime=%3$s&cloudcode=%4$s&token=%5$s&type=json";
    public static final String URL_DYNAMIC_BUTTONS      = "%1$sSaveFormField.do?actn=getDynamicButtons&reportid=%2$s&formid=%3$s&historyid=0&noresults=1&ask=COMMAND_NAME_1&cloudcode=%4$s&token=%5$s&random=285&crossDomain=true&AjaxRequestUniqueId=15948825591070&type=json&json=true";
    public static final String URL_CHART_ID             = "%1$spages/helper.jsp?mobileformlist=yes&cloudcode=%2$s&token=%3$s";
    public static final String URL_ATTACH_FILE          = "%1$sfilesaveupload.do?actn=upload&formid=%2$s&reportid=%3$s&token=%4$s&cloudcode=%5$s&type=json";
    public static final String URL_PRINT_REPORT         = "%1$ssendMailReport.do?actn=PrintData&idlist=@&fieldid=%2$s&fieldId=%3$s&fieldValue=&formid=%4$s&matchingFields=&reportid=%5$s&moduleconditions=&reportname=%6$s&cloudcode=%7$s&token=%8$s&type=json";
    public static final String URL_SINGLE_CHART_ID      = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=57358&ids=relationid&valuestring=%2$s@j@&token=%3$s&cloudcode=%4$s&type=json";

    //%1$s = server url,
    // %2$s = cloudcode,
    // %3$s = form id,
    // %4$s = file name
     public static final String IMAGE_URL                = "%1$suploads%2$s/Folder%3$s/%4$s";
     public static final String SERVER_IMAGE_URL         = "https://strategicerpcloud.com/strategicerp/%1$s";

    //Pending Task
    public static final String PENDING_TASKS_URL        = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=57245&cloudcode=%2$s&ids=recordsize/pagenumber&valuestring=100@j@%3$s&token=%4$s&type=json";
    public static final String PENDING_TASK_DETAIL_URL  = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=record&formid=%2$s&recordid=%3$s&cloudcode=%4$s&token=%5$s";
    public static final String URL_ACTION_TASK          = "%1$sSaveFormField.do?&actn=SaveDynamicStatePending&formid=%2$s&editids=state/stateownerid/&selectedids=%3$s&jvals=%4$s&modulename=Social Apps&ask=COMMAND_NAME_1&mobileform=yes&cloudcode=%5$s&token=%6$s";

    //Charts
    public static final String URL_LEAD_BY_SOURCE       = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=48010&chartcolumns=&ids=nameofcompany/projectname/startdate/enddate/&valuestring=%2$s&realtime=false&cloudcode=%3$s&token=%4$s";
    public static final String URL_LEAD_BY_STATUS       = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=48041&chartcolumns=&ids=nameofcompany/projectname/startdate/enddate/&valuestring=%2$s&realtime=false&cloudcode=%3$s&token=%4$s";
    public static final String URL_SALES_REV_USER_WISE  = "https://best-erp.com:9000/strategicerp/getFunction.do?actn=getsqljsondata&sqlfieldid=46879&chartcolumns=&ids=nameofcompany/projectname/startdate/enddate/&valuestring=@j@@j@2020-04-01@j@2021-03-31@j@&drilldown=Revenue%20Report-%20Month%20Wise&realtime=false&cloudcode=b2b&token=DNVEP9576C544LQG";
    public static final String URL_CHART_DATA           = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=%2$s&chartcolumns=&ids=nameofcompany/projectname/startdate/enddate/&valuestring=%3$s&realtime=false&cloudcode=%4$s&token=%5$s";

    //CHART FILTER
    public static final String URL_NAME_OF_COMPANY      = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=38675&cloudcode=%2$s&token=%3$s&type=json";
    public static final String URL_PROJECTS             = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=35767&cloudcode=%2$s&token=%3$s&type=json";

    //UPCOMING MEETINGS
    public static final String MAP_LIST_URL             = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=54435&idstring=self/&valuestring=%2$s@j@&cloudcode=%3$s&token=%4$s&type=json";
    public static final String MAP_FILTER_LIST_URL      = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=54973&ids=54972&valuestring=Map Filter@j@&cloudcode=%2$s&token=%3$s&type=json";
    public static final String PRODUCT_FILTER_LIST_URL  = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=55021&ids=54972/55183/55182&valuestring=%2$s&cloudcode=%3$s&token=%4$s&type=json";

    //Home
    public static final String HOME_URL                 = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=54850&ids=54972&valuestring=Home Page@j@&cloudcode=%2$s&token=%3$s&type=json";
    public static final String PRODUCT_DETAILS_URL      = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=55047&ids=54972/55053&valuestring=Product Detail@j@%2$s@j@&cloudcode=%3$s&token=%4$s&type=json";
    public static final String GOOGLE_DOC_VIEWER_URL    = "https://docs.google.com/gview?embedded=true&url=";

    //FETCH RECORD
    public static final String URL_FETCH_RECORD         = "%1$sSaveFormField.do?localrandom=739116&actn=getFieldValues&formid=%2$s&reportid=%3$s&ask=COMMAND_NAME_1&cloudcode=%4$s&token=%5$s&random=398&crossDomain=true&AjaxRequestUniqueId=160915923168052&type=json";
    public static final String URL_FETCH_USING_TAG      = "%1$sSaveFormField.do?localrandom=82083&actn=getUnique&uniquefieldtype=UNIQUE&uniquefieldid=%2$s&uniqueformid=%3$s&uniquevalue=%4$s&ask=COMMAND_NAME_1&cloudcode=%5$s&token=%6$s&random=1061&crossDomain=true&AjaxRequestUniqueId=16093084778388&type=json"; //request register  = 44133
    public static final String URL_FIELD_VIEW_VALUES   = "%1$sSaveFormField.do?localrandom=739116&actn=getFieldViewValues&formid=%2$s&reportid=%3$s&ask=COMMAND_NAME_1&cloudcode=%4$s&token=%5$s&random=398&crossDomain=true&AjaxRequestUniqueId=160915923168052&type=json&split=true";

    //MORE FILTER
    public static final String FILTER_MORE_DROPDOWN_URL = "%1$sgetFunction.do?actn=getsqljsondata&sqlfieldid=%2$s&cloudcode=%3$s&token=%4$s&type=json";

    public static final  int[] COLORS                       = new int[]{Color.BLACK, Color.RED};
    public static final  int[] ERROR_COLORS                 =  new int[]{Color.BLACK, Color.RED, Color.BLACK};

    //Permissions
    public static final int REQUEST_PERMISSIONS             = 201;
    public static final int PICK_FILE_REQUEST_CODE          = 202;

    //Volley
    public static int TIME_OUT                              = 5000;
    public static int NO_OF_TRIES                           = 2;

    //RecyclerView Item types
    public static int TYPE_EDIT_TEXT                        = 1;
    public static int TYPE_SPINNER                          = 2;
    public static int TYPE_EDIT_DATE                        = 3;
    public static int TYPE_EDIT_TEXT_AREA                   = 4;
    public static int TYPE_CHECKBOX                         = 5;
    public static int TYPE_SUMMARY                          = 6;
    public static int TYPE_DLIST                            = 7;
    public static int TYPE_ITEM_AUTO_COMPLETE               = 8;
    public static int TYPE_FILE                             = 10;

    //Menu Name for Database Query
    public static final String APP_MENU                     = "App_Menu";

    //date time to send in the url for the first time
    public static final String SERVER_DATE_TIME             = "1991-10-03%2011:20:00";

    //Extras for bundle
    public static final String EXTRA_MOBILE                 = "extra_mobile";
    public static final String EXTRA_DATA_URL               = "extra_data_url";
    public static final String EXTRA_TITLE                  = "extra_title";
    public static final String EXTRA_URL                    = "extra_url";
    public static final String EXTRA_MENU_ON_CLICK          = "menu_on_click";
    public static final String EXTRA_OBJECT                 = "extra_obj";
    public static final String EXTRA_FIELD_OBJECT           = "extra_field_obj";
    public static final String EXTRA_DLIST_OBJECT           = "extra_dlist_object";
    public static final String EXTRA_POSITION               = "extra_position";
    public static final String EXTRA_LIST_OF_DLIST          = "extra_list_of_dlist";
    public static final String EXTRA_FIELD_POSITION         = "extra_field_position";
    public static final String EXTRA_DLIST_BUTTON_POSITION  = "button_array_position";
    public static final String EXTRA_DLIST_ITEM_POSITION    = "dlist_item_position";
    public static final String EXTRA_DLIST_ROW_POSITION     = "list_row_position";
    public static final String EXTRA_MODE                   = "extra_mode";
    public static final String EXTRA_DLIST_ARRAY            = "extra_dlist_array";
    public static final String EXTRA_FILTER_STRING          = "extra_filter_string";
    public static final String dd_MM_yyyy                   = "dd/MM/yyyy";
    public static final String dd_MM_yyyy_HH_mm             = "dd/MM/yyyy HH:mm";
    public static final String EXTRA_FIELD_ID               = "extra_field_id";
    public static final String yyyy_MM_dd                   = "yyyy-MM-dd";
    public static final String yyyy_MM_dd_now               = "yyyy-MM-dd HH:mm";
    public static final String yyyy_MM_dd_HH_mm             = "yyyy-MM-dd'T'HH:mm";//yyyy-MM-dd'T'HH:mm
    public static final String EXTRA_CREATE_NEW             = "create_new";

    //BroadcastReceiver
    public static final String EXTRA_SHOW_DLIST             = "SHOW_DLIST";
    public static final String EXTRA_REFRESH_DLIST          = "REFRESH_DLIST";
    public static final String EXTRA_SHOW_SUMMARY           = "SHOW_CHART_SUMMARY";
    public static final String EXTRA_SUMMARY_JSON           = "summary_json";
    public static final String EXTRA_GET_DRILL_DOWN         = "GET_DRILL_DOWN";
    public static final String EXTRA_VALUE                  = "extra_value";
    public static final String EXTRA_KEY                    = "extra_key";
    public static final String EXTRA_DRILL_DOWN_TITLE       = "drillDownTitle";

    //PENDING TASK
    public static final String EXTRA_FORM_ID                = "form_id";
    public static final String EXTRA_FORM_NAME              = "form_name";
    public static final String EXTRA_RECORD_ID              = "record_id";
    public static final String EXTRA_CLICK                  = "click";
    public static final String EXTRA_MODULE                 = "module";
    public static final String EXTRA_USER_NAME              = "userName";
    public static final String EXTRA_VLIST                  = "vlist";
    public static final String EXTRA_ENTRY                  = "entry";
    public static final String EXTRA_DATA                   = "data";
    public static final String VLIST_FORM_ID                = "v_form_id";
    public static final String VLIST_RELATION_IDS           = "vlist_rel_ids";

    //CHART TYPE
    public static final int TYPE_PIE                        = 1;
    public static final int TYPE_DONUT                      = 2;
    public static final int TYPE_FUNNEL                     = 3;
    public static final int TYPE_BAR                        = 4;
    public static final int TYPE_HBAR                       = 5;
    public static final int TYPE_STACK_BAR                  = 6;
    public static final int TYPE_SPARKLINE                  = 7;
    public static final int TYPE_GUAGE                      = 8;
    public static final int TYPE_LINE                       = 9;
    public static final int TYPE_AREA                       = 10;
    public static final int TYPE_TABLE                      = 11;
    public static final String EXTRA_CHART_TYPE             = "extra_chart_type";
    public static final String EXTRA_CHART_ID               = "extra_chart_id";
    public static final int TYPE_HEADER                     = 12;
    public static final int TYPE_NORMAL                     = 13;
    public static final int TYPE_PRODUCT                    = 16;

    //PENDING TASK DETAILS TYPE
    public static final int TYPE_CALL                       = 14;
    public static final int TYPE_VLIST                      = 15;
    public static final String EXTRA_IMAGE_PATH             = "image_path";
    public static final String EXTRA_ITEM_ID                = "item_id";
    public static final String EXTRA_LIST                   = "extra_list";
    public static final String EXTRA_VIDEO                  = "extra_video";
    public static final String EXTRA_MORE                   = "extra_more";
    public static final String EXTRA_PARAMS                 = "extra_params";
    public static final String EXTRA_GRID                   = "gridLayoutManager";
    public static final String EXTRA_LINEAR                 = "linearLayoutManager";
    public static final String EXTRA_TYPE                   = "extra_type";
    public static final String EXTRA_MDCOMBO                = "extra_mdcombo";
    public static final String EXTRA_ONKEY_DOWN             = "extra_onKeydown";
    public static final String EXTRA_ONCLICK_RIGHT          = "extra_onClickRightButton";
    public static final String EXTRA_FIELD_NAME             = "extra_field_name";
    public static final String EXTRA_IS_DLIST               = "isDlist";
    public static final String NOTIFICATION_RECEIVED        = "isNotificationReceived";
    public static final String EXTRA_HTML                   = "html_code";

    public static final String PAYLOAD_HIDE_SHOW            = "payload_hide_show";
    public static final String PAYLOAD_TEXT_SQL             = "payload_text_on_change";
    public static final String PAYLOAD_TEXT                 = "payload_text";
    public static final String PAYLOAD_EVALUATE_FUNCTION    = "payload_evaluate_function";
    public static final String PAYLOAD_READ_EDIT            = "payload_read_edit";
    public static final String PAYLOAD_REQUIRED_NOTREQUIRED = "payload_required_notrequired";

}
