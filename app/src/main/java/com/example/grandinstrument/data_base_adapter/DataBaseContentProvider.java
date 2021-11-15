package com.example.grandinstrument.data_base_adapter;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

public class DataBaseContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private DataBaseHandler dbOH;

    static {
        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.GOODS_TABLE_NAME, DataBaseContract.URI_CODE_ALL_TABLE);
        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.GOODS_TABLE_NAME+"/#", DataBaseContract.URI_CODE_ONE_ROW);

        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.GOODS_PICTURE_TABLE_NAME, DataBaseContract.URI_CODE_ALL_TABLE);
        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.GOODS_PICTURE_TABLE_NAME+"/#", DataBaseContract.URI_CODE_ONE_ROW);

        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.LOGIN_TABLE_NAME, DataBaseContract.URI_CODE_ALL_TABLE);
        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.LOGIN_TABLE_NAME+"/#", DataBaseContract.URI_CODE_ONE_ROW);

        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.CART_TABLE_NAME, DataBaseContract.URI_CODE_ALL_TABLE);
        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.CART_TABLE_NAME+"/#", DataBaseContract.URI_CODE_ONE_ROW);

        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.ORDER_HEAD_TABLE_NAME, DataBaseContract.URI_CODE_ALL_TABLE);
        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.ORDER_HEAD_TABLE_NAME+"/#", DataBaseContract.URI_CODE_ONE_ROW);

        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.ORDER_ROW_TABLE_NAME, DataBaseContract.URI_CODE_ALL_TABLE);
        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.ORDER_ROW_TABLE_NAME+"/#", DataBaseContract.URI_CODE_ONE_ROW);

        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME, DataBaseContract.URI_CODE_ALL_TABLE);
        uriMatcher.addURI(DataBaseContract.URI_AUTHORITY, DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME+"/#", DataBaseContract.URI_CODE_ONE_ROW);
    }

    @Override
    public boolean onCreate() {
        dbOH = new DataBaseHandler(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = uri.getPath().replace('/',' ').trim();
        SQLiteDatabase db = dbOH.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match){
            case DataBaseContract.URI_CODE_ALL_TABLE:
                cursor = db.query(tableName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case DataBaseContract.URI_CODE_ONE_ROW:
                selection = DataBaseContract.R_GOODS.RG_KEY_ID +"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(tableName,projection,selection,selectionArgs,null,null,null);
                break;
            default:
                throw new IllegalArgumentException("Incorrect URI" + String.valueOf(uri));

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = uri.getPath().replace('/',' ').trim();
        SQLiteDatabase db = dbOH.getWritableDatabase();
        int match = uriMatcher.match(uri);
        long id;

        switch (match){
            case DataBaseContract.URI_CODE_ALL_TABLE:
                id = db.insert(tableName,null,values);
                if (id == -1){
                    Log.e("insert_row","Unsuccessfully insert " + String.valueOf(uri));
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri,
                        null);
                return ContentUris.withAppendedId(uri,id);
            default:
                throw new IllegalArgumentException("Incorrect URI" + String.valueOf(uri));

        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        String tableName = uri.getPath().replace('/',' ').trim();
        SQLiteDatabase db = dbOH.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match){
            case DataBaseContract.URI_CODE_ALL_TABLE:

                int i = db.update(tableName,values,selection,selectionArgs);
                if (i>0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return i;
            case DataBaseContract.URI_CODE_ONE_ROW:
                selection = DataBaseContract.R_GOODS.RG_KEY_ID +"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.update(tableName,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Incorrect URI" + String.valueOf(uri));

        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    public boolean clearTable(String nameTable){

        SQLiteDatabase db = dbOH.getWritableDatabase();

        try {
            db.execSQL("DROP TABLE IF EXISTS " + nameTable);
            if (nameTable == DataBaseContract.GOODS_TABLE_NAME){
                dbOH.createTableGoods(db);
            }

            if (nameTable == DataBaseContract.GOODS_PICTURE_TABLE_NAME){
                dbOH.createTablePicture_Goods(db);
            }

            if (nameTable == DataBaseContract.LOGIN_TABLE_NAME){
                dbOH.createTableLOGIN(db);
            }

            if (nameTable == DataBaseContract.CART_TABLE_NAME){
                dbOH.createTableCart(db);
                getContext().getContentResolver().notifyChange(DataBaseContract.BASE_CONTENT_URI_CART, null);
                Utils.mCurCartQty.setValue(0);

            }

            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }

    }
}
