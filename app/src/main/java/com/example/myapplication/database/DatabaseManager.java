package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.example.myapplication.user_interface.home.model.Product;
import com.example.myapplication.user_interface.home.model.ProductItem;
import com.example.myapplication.Constant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager {

    private Context context;
    private SQLiteDatabase database;

    public DatabaseManager(Context c) {
        context = c;
    }

    public DatabaseManager open() throws SQLException {
        database = DatabaseHelper.getInstance(context).getWritableDatabase();
        return this;
    }

//    public void close() {
//        databaseHelper.close();
//    }

    public void insertForm(int chartId, String formName,String formJson) {


        Log.e(getClass().getSimpleName(), "insert");
        SQLiteStatement stmt = database.compileStatement("INSERT INTO " + DatabaseHelper.TABLE_FORM + "(" + DatabaseHelper.COL_FORM_TABLE_ID + "," +DatabaseHelper.COL_FORM_NAME+","+ DatabaseHelper.COL_FORM_JSON + ")" + " VALUES (?,?,?)");
        stmt.bindLong(1, chartId);
        stmt.bindString(2, formName);
        stmt.bindString(3, formJson);

        long rowId = stmt.executeInsert();
        Log.e(getClass().getSimpleName(), "InsertedRow Id =" + rowId);
    }

    public void insertFormTime(int chartId, String time) {
        Log.e(getClass().getSimpleName(), "inserttime");
        SQLiteStatement stmt = database.compileStatement("INSERT INTO " + DatabaseHelper.TABLE_FORM_TIME + "(" + DatabaseHelper.COL_CHART_ID + "," + DatabaseHelper.COL_TIME + ")" + " VALUES (?,?)");
        stmt.bindLong(1, chartId);
        stmt.bindString(2, time);
        long rowId = stmt.executeInsert();
    }


    public void insertDListRow(long formId,
                               String formTitle,
                               String dlistFieldId,
                               String dlistTitle,
                               String dlistJson,
                               int srNo) {

        try{
            Log.e(getClass().getSimpleName(), "insertDListForm");
            SQLiteStatement stmt = database.compileStatement("INSERT INTO " + DatabaseHelper.TABLE_DLIST + "(" + DatabaseHelper.COL_FORM_TABLE_ID + "," + DatabaseHelper.COL_FORM_NAME + ","+ DatabaseHelper.COL_DLIST_ID + "," + DatabaseHelper.COL_DLIST_TITLE + "," + DatabaseHelper.COL_DLIST_JSON +","+ DatabaseHelper.COL_SR_NO + ")" + " VALUES (?,?,?,?,?,?)");
            stmt.bindLong(1, formId);
            stmt.bindString(2, formTitle);
            stmt.bindString(3, dlistFieldId);
            stmt.bindString(4, dlistTitle);
            stmt.bindString(5, dlistJson);
            stmt.bindLong(6,srNo);
            long rowId = stmt.executeInsert();
            if(rowId > 0){
                Log.e(getClass().getSimpleName(),"insertedDListRow Id =" + rowId);
            }else{
                Log.e(getClass().getSimpleName(),"insertedDListRow failed");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public String getFormJson(int tableId) {
        String query = "";
        if(tableId != 0){
            query = "SELECT  * FROM " + DatabaseHelper.TABLE_FORM + " WHERE " + DatabaseHelper.COL_FORM_TABLE_ID + " = " + tableId;
        }else{
            //this is to get the appmenu
            query = "SELECT  * FROM " + DatabaseHelper.TABLE_FORM + " WHERE " + DatabaseHelper.COL_FORM_NAME + " = '" + Constant.APP_MENU + "'";
        }

        String formJSON = "";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                formJSON = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FORM_JSON));
            }
        }
        return formJSON;
    }


    public  List<String> fetchDlistJson(String fieldId) {
        List<String> list = new ArrayList<>();
        String formJSON = "";
        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_DLIST + " WHERE " + DatabaseHelper.COL_DLIST_ID + " = '" + fieldId.trim() + "'";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    formJSON = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DLIST_JSON));
                    list.add(formJSON);
                }while (cursor.moveToNext());
            }
        }
        return list;
    }

    public int getDlistRowsCount(String fieldId) {
        String query = "SELECT  COUNT(*) FROM " + DatabaseHelper.TABLE_DLIST + " WHERE " + DatabaseHelper.COL_DLIST_ID + " = '" + fieldId.trim() + "'";
        Cursor cursor = database.rawQuery(query, null);
        int count = 0;
        if(cursor!= null)
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
        cursor.close();
        return count;
    }

    // fetch dlist form using fieldId and srNo
    public String fetchFormJsonBySrNo(String fieldId, int srNo) {
        String formJSON = "";

        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_DLIST + " WHERE " + DatabaseHelper.COL_DLIST_ID + " = '" + fieldId.trim() + "' AND " + DatabaseHelper.COL_SR_NO  + " = " + srNo;

       Cursor cursor = null;
        try{
            cursor = database.rawQuery(query, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    formJSON = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DLIST_JSON));
                }
            }

        }catch (SQLiteException e){
             e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return formJSON;
    }

    public boolean updateDListRowForm(String fieldId, int srNo,String formJson) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DLIST_JSON, formJson);

        try {
            // String[] args = new String[]{fieldId,String.valueOf(srNo)};
            long id = database.update(DatabaseHelper.TABLE_DLIST, values, DatabaseHelper.COL_DLIST_ID + " = '" + fieldId.trim() + "' AND " + DatabaseHelper.COL_SR_NO  + " = '" + srNo + "'", null);
            if(id != 0 ){
                Log.e("updateForm()","form update sucess");
                return true;
            }else{
                return  false;
            }
        } catch (SQLiteException e) {
            Log.e("updateForm()","form update failed");
            return false;

        }
    }

    public void deleteDlistRowWithSrNo(String fieldId, int srNo) {
        Log.e(getClass().getSimpleName(), "delete dlist");
        try {
            int noOfRowsEffected =  database.delete(DatabaseHelper.TABLE_DLIST,DatabaseHelper.COL_DLIST_ID + " = '" + fieldId.trim() + "' AND " + DatabaseHelper.COL_SR_NO  + " = '" + srNo + "'",null);
            if(noOfRowsEffected > 0){
                Log.e("deleteDlistRowWithSrNo" , srNo + " deleted successfully");
            }else{
                Log.e("deleteDlistRowWithSrNo" , srNo + " deleted failed");
            }

        } catch (SQLiteConstraintException sqle) {
            String err = sqle.getMessage();
            Log.e("DBHelper", err);
        }
    }

    public void deleteDListWithFieldId(String fieldId) {
        Log.e(getClass().getSimpleName(), "delete dlist");
        try {
            int noOfRowsEffected =  database.delete(DatabaseHelper.TABLE_DLIST,DatabaseHelper.COL_DLIST_ID + " = '" + fieldId.trim() + "'",null);
            if(noOfRowsEffected > 0){
                Log.e("deleteDListWithFieldId" , fieldId + " deleted successfully");
            }else{
                Log.e("deleteDListWithFieldId" , fieldId + " deleted failed");
            }

        } catch (SQLiteConstraintException sqle) {
            String err = sqle.getMessage();
            Log.e("DBHelper", err);
        }
    }

    public void deleteForm(int tableId) {
        Log.e(getClass().getSimpleName(), "deleteTable");
        try {
            database.execSQL("delete from " + DatabaseHelper.TABLE_FORM + " WHERE " + DatabaseHelper.COL_FORM_TABLE_ID + "=" + tableId);
        } catch (SQLiteConstraintException sqle) {
            String err = sqle.getMessage();
            Log.e("DBHelper", err);
        }
    }


    // DELETE Form
    public void deleteDList() {
        Log.e(getClass().getSimpleName(), "delete dlist");
        try {
            database.delete(DatabaseHelper.TABLE_DLIST,null,null);
        } catch (SQLiteConstraintException sqle) {
            String err = sqle.getMessage();
            Log.e("DBHelper", err);
        }
    }

    public boolean checkIfFormExists(int formId) {
        Log.e(getClass().getSimpleName(), "checkIfFormExists");
        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_FORM + " WHERE " + DatabaseHelper.COL_FORM_TABLE_ID + " = " + formId;

        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean checkIfFormExists(String title){
        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_FORM + " WHERE " + DatabaseHelper.COL_FORM_NAME + " = '" + title + "'";

        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean isFirstTimeLoadingForm(int chartId) {
        Log.e(getClass().getSimpleName(), "isFirstTimeLoadingForm");
        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_FORM_TIME + " WHERE " + DatabaseHelper.COL_CHART_ID + " = " + chartId;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        return true;
    }
    /**
     * Update Form item by chartId
     *
     * @param chartId
     * @return
     */
    public boolean updateForm(int chartId,String formJson) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_FORM_JSON, formJson);

        try {
            String[] args = new String[]{String.valueOf(chartId)};
            database.update(DatabaseHelper.TABLE_FORM, values, DatabaseHelper.COL_FORM_TABLE_ID + "=?", args);
            Log.e("updateForm()","form update success");
            return true;
        } catch (SQLiteException e) {
            Log.e("updateForm()","form update failed");
            return false;

        }
    }

    public void deleteMenuRecord(int chartId) {
        try {
            String[] args = new String[]{String.valueOf(chartId)};
            database.delete(DatabaseHelper.TABLE_FORM, DatabaseHelper.COL_FORM_TABLE_ID + "=?", args);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }


    public boolean insertWishListProduct(String itemId, String json) {

        Log.e(getClass().getSimpleName(), "insertWishListProduct");
        SQLiteStatement stmt = database.compileStatement("INSERT INTO " + DatabaseHelper.TABLE_WISH_LIST + "(" + DatabaseHelper.COL_ITEM_ID +"," +DatabaseHelper.COL_JSON+")" + " VALUES (?,?)");
        stmt.bindString(1, itemId);
        stmt.bindString(2, json);


//        String imageFullVew = null;
//        if (android.os.Build.VERSION.SDK_IxNT >= android.os.Build.VERSION_CODES.O) {
//            imageFullVew = String.join(",", product.getProductInfo().getImageFullView());
//        }else{
//            String SEPARATOR = ",";
//            StringBuilder stringBuilder = new StringBuilder();
//            for(String image :  product.getProductInfo().getImageFullView()){
//                stringBuilder.append(image);
//                stringBuilder.append(SEPARATOR);
//            }
//            String imagesString = stringBuilder.toString();
//            //Remove last comma
//            imageFullVew = imagesString.substring(0, imagesString.length() - SEPARATOR.length());
//        }
//        stmt.bindString(17,imageFullVew );

        long rowId = stmt.executeInsert();
        System.out.println("InsertedRow Id =" + rowId);
        if(rowId == -1) {
            return false;
        }else{
            return true;
        }
    }

    public List<String> getWishlistProducts() {
        List<String> wishlist = new ArrayList<>();
     //   List<ProductItem> wishlist = new ArrayList<>();
        Cursor cursor = null;
        try {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_WISH_LIST;
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_JSON));
                    wishlist.add(json);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return wishlist;
    }

    public void removeAll(){
        try{
            database.delete(DatabaseHelper.TABLE_FORM, null, null);
  //          database.delete(DatabaseHelper.TABLE_WISHLIST,null,null);
            database.delete(DatabaseHelper.TABLE_DLIST,null,null);
            database.delete(DatabaseHelper.TABLE_WISH_LIST,null,null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Check if a product exists in wishlist table
     * @param itemID
     * @return
     */
    public boolean checkIfProductExists(String itemID) {
        Cursor cursor = null;
        boolean result = true;

        try {
            Log.e(getClass().getSimpleName(), "checkIfProductExists");
            String query = "SELECT  * FROM " + DatabaseHelper.TABLE_WISH_LIST + " WHERE " + DatabaseHelper.COL_ITEM_ID + " = '" + itemID + "'";

            cursor = database.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();

                result= false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * Delete a particular product from wishlist
     * @param itemID = product itemId
     * @return
     */
    public boolean deleteProductFromWishlist(String itemID)
    {
        return database.delete(DatabaseHelper.TABLE_WISH_LIST, DatabaseHelper.COL_ITEM_ID + "=?", new String[]{itemID}) > 0;
    }
}
