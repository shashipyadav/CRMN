package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

  private static DatabaseHelper sInstance;

    /**
     * Database name
     */
  private static final String DATABASE_NAME = "simbiosis_b2b.db";
//  private static final String DATABASE_NAME = "b2b.db";

    /**
     * Database version
     */
    private static final int DB_VERSION = 1;

    /**
     * Table Names
     */
    public static final String TABLE_FORM_TIME = "B2bFormTime";
    public static final String TABLE_FORM = "Form";
    public static final String TABLE_DLIST = "DList";
    public static final String TABLE_DFF = "DFF";
    public static final String TABLE_WISHLIST = "Wishlist";
    public static final String TABLE_WISH_LIST = "Wish_list";// new db for wishlist

    /**
     * Column Names
     */
    public static final String COL_ID = "Id";
    public static final String COL_CHART_ID = "chartId";
    public static final String COL_TIME = "colTime";
    public static final String COL_FORM_NAME = "colFormName";
    public static final String COL_FORM_TABLE_ID = "FormTableId";
    public static final String COL_FORM_JSON = "FormJson";
    public static final String COL_DLIST_ID = "colDlistTableId";
    public static final String COL_DLIST_TITLE = "colDlistTitle";
    public static final String COL_DLIST_JSON = "colDlistJson";
    public static final String COL_SR_NO = "colSrNo";

    //Wishlist
    public static final String COL_SIZE = "size";
    public static final String COL_DESIGN = "design";
    public static final String COL_WIDTH = "width";
    public static final String COL_HEIGHT = "height";
    public static final String COL_LENGTH = "length";
    public static final String COL_HS_CODE = "hsCode";
    public static final String COL_SKU_CODE = "skuCode";
    public static final String COL_FT2_PER_BOX = "ft2PerBox";
    public static final String COL_PCS_PER_BOX = "pcsPerBox";
    public static final String COL_GROSS_WEIGHT = "grossWeight";
    public static final String COL_SUR_CODE = "surfaceCode";
    public static final String COL_CAT_CODE = "categoryCode";
    public static final String COL_VERTICAL_NAME = "verticalName";
    public static final String COL_IMAGEURL = "imagePath";
    public static final String COL_SKU_NAME = "skuName";
    public static final String COL_ITEM_ID = "itemId";
    public static final String COL_FULL_VIEW_IMAGE = "fullViewImages";
    public static final String COL_JSON = "colJson";

    /**
     * Create TABLE_FORM_TIME
     */
    private static final String CREATE_TABLE_TIME = "CREATE TABLE IF NOT EXISTS " +  TABLE_FORM_TIME + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CHART_ID + " INTEGER, "
            + COL_TIME + " TEXT);";

    /**
     * Create TABLE_FORM
     */
    private static final String CREATE_TABLE_FORM = "CREATE TABLE IF NOT EXISTS " + TABLE_FORM + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FORM_TABLE_ID + " INTEGER, "
            + COL_FORM_NAME + " TEXT, "
            + COL_FORM_JSON + " TEXT);";

    /**
     * Create TABLE_DLIST
     */
    private static final String CREATE_TABLE_DLIST = "CREATE TABLE IF NOT EXISTS " + TABLE_DLIST + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FORM_TABLE_ID + " INTEGER, "
            + COL_FORM_NAME + " TEXT, "
            + COL_DLIST_ID + " TEXT, "
            + COL_DLIST_TITLE + " TEXT, "
            + COL_DLIST_JSON + " TEXT, "
            + COL_SR_NO + " INTEGER);";

    /**
     * Create TABLE_DFF
     */
    private static final String CREATE_TABLE_DFF = "CREATE TABLE IF NOT EXISTS " + TABLE_DFF + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FORM_TABLE_ID + " INTEGER, "
            + COL_FORM_JSON + " TEXT);";

    /**
     *
     * Create TABLE_WISHLIST
     */
    public static final String CREATE_TABLE_WISHLIST =
            "CREATE TABLE " + TABLE_WISHLIST + "("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_SIZE + " TEXT,"
                    + COL_DESIGN + " TEXT,"
                    + COL_WIDTH + " TEXT,"
                    + COL_HEIGHT + " TEXT,"
                    + COL_LENGTH + " TEXT,"
                    + COL_HS_CODE + " TEXT,"
                    + COL_SKU_CODE + " TEXT,"
                    + COL_FT2_PER_BOX + " TEXT,"
                    + COL_PCS_PER_BOX + " TEXT,"
                    + COL_GROSS_WEIGHT + " TEXT,"
                    + COL_SUR_CODE + " TEXT,"
                    + COL_CAT_CODE + " TEXT,"
                    + COL_VERTICAL_NAME + " TEXT,"
                    + COL_IMAGEURL + " TEXT,"
                    + COL_SKU_NAME + " TEXT,"
                    + COL_ITEM_ID + " TEXT, "
                    + COL_FULL_VIEW_IMAGE + " TEXT"
                    + ")";

 //create new wishlist
 public static final String CREATE_TABLE_WISH_LIST =   "CREATE TABLE " + TABLE_WISH_LIST
         + "("
         + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
         + COL_ITEM_ID + " TEXT, "
         + COL_JSON + " TEXT"
         + ")";

  public static DatabaseHelper getInstance(Context context) {
    // Use the application context, which will ensure that you
    // don't accidentally leak an Activity's context.
    // See this article for more information: http://bit.ly/6LRzfx
    if (sInstance == null) {
      sInstance = new DatabaseHelper(context.getApplicationContext());
    }
    return sInstance;
  }

  public DatabaseHelper(@Nullable Context context) {
    super(context, DATABASE_NAME, null, DB_VERSION);
  }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FORM);
      //db.execSQL(CREATE_TABLE_WISHLIST);
        db.execSQL(CREATE_TABLE_DLIST);
      db.execSQL(CREATE_TABLE_WISH_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISH_LIST);
        onCreate(db);
    }
}
