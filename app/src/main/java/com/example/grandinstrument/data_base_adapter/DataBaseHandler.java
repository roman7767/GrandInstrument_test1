package com.example.grandinstrument.data_base_adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {
    private ArrayList<String> listOfTable;

    public DataBaseHandler( Context context) {
        super(context, DataBaseContract.DATA_BASE_NAME, null, DataBaseContract.DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if(listOfTable == null){
            listOfTable = new ArrayList<>();
            listOfTable.add(DataBaseContract.GOODS_TABLE_NAME);
            listOfTable.add(DataBaseContract.GOODS_PICTURE_TABLE_NAME);
            listOfTable.add(DataBaseContract.LOGIN_TABLE_NAME);
            listOfTable.add(DataBaseContract.CART_TABLE_NAME);
            listOfTable.add(DataBaseContract.ORDER_HEAD_TABLE_NAME);
            listOfTable.add(DataBaseContract.ORDER_ROW_TABLE_NAME);
            listOfTable.add(DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME);
        }

        for (int i = 0; i<listOfTable.size(); i++){
            String s = listOfTable.get(i);
            if (s == DataBaseContract.GOODS_TABLE_NAME){
                createTableGoods(db);
            }
            if (s == DataBaseContract.GOODS_PICTURE_TABLE_NAME){
                createTablePicture_Goods(db);
            }
            if (s == DataBaseContract.LOGIN_TABLE_NAME){
                createTableLOGIN(db);
            }

            if (s == DataBaseContract.CART_TABLE_NAME){
                createTableCart(db);
            }

            if (s == DataBaseContract.ORDER_HEAD_TABLE_NAME){
                createTableOrderHeader(db);
            }

            if (s == DataBaseContract.ORDER_ROW_TABLE_NAME){
                createTableOrderRow(db);
            }
            if (s == DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME){
                createTableTypeOfShipment(db);
            }
        }
    }

    protected boolean createTableTypeOfShipment(SQLiteDatabase db) {
        try {
            String CREATE_TYPE_OF_SHIPMENT = "create table "+ DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME+
                    "("+ DataBaseContract.R_TYPE_OF_SHIPMENT.R_KEY_ID+" integer primary key, "+
                    DataBaseContract.R_TYPE_OF_SHIPMENT.R_CODE+" text, "+
                    DataBaseContract.R_TYPE_OF_SHIPMENT.R_NAME+" text, "+
                    DataBaseContract.R_TYPE_OF_SHIPMENT.R_CODE_MAINE+" text, "+
                    DataBaseContract.R_TYPE_OF_SHIPMENT.R_AVAILABLE+" boolean)";

            db.execSQL(CREATE_TYPE_OF_SHIPMENT);
            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }
    }

    protected boolean createTableLOGIN(SQLiteDatabase db) {

        try {
            String CREATE_GOODS_TABLE = "create table "+ DataBaseContract.LOGIN_TABLE_NAME+
                    "("+ DataBaseContract.R_LOGIN.RL_KEY_ID+" integer primary key, "+
                    DataBaseContract.R_LOGIN.RL_EMAIL+" text, "+
                    DataBaseContract.R_LOGIN.RL_PASSWORD+" text, "+
                    DataBaseContract.R_LOGIN.RL_LOADED+" boolean)";

            db.execSQL(CREATE_GOODS_TABLE);
            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }
    }

    boolean tableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
                new String[] {"table", tableName}
        );
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion > oldVersion){
            if (listOfTable == null){
                listOfTable = new ArrayList<>();
            }else{
                listOfTable.clear();
            }


            if (!tableExists(db, DataBaseContract.GOODS_TABLE_NAME)){
                listOfTable.add(DataBaseContract.GOODS_TABLE_NAME);
            }else{
                checkRowInTable(db,DataBaseContract.GOODS_TABLE_NAME,DataBaseContract.R_GOODS.RG_BOX,"real");
                checkRowInTable(db,DataBaseContract.GOODS_TABLE_NAME,DataBaseContract.R_GOODS.RG_PACKAGE,"real");
            }

            if (!tableExists(db, DataBaseContract.CART_TABLE_NAME)){
                listOfTable.add(DataBaseContract.CART_TABLE_NAME);
            }else{
                checkRowInTable(db,DataBaseContract.CART_TABLE_NAME,DataBaseContract.R_CART.RC_UUID_ORDER,"text");
                checkRowInTable(db,DataBaseContract.CART_TABLE_NAME,DataBaseContract.R_CART.RC_DELIVERY_DATE,"text");
                checkRowInTable(db,DataBaseContract.CART_TABLE_NAME,DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT,"text");
                checkRowInTable(db,DataBaseContract.CART_TABLE_NAME,DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT_CODE,"text");
            }

            if (!tableExists(db, DataBaseContract.GOODS_PICTURE_TABLE_NAME)){
                listOfTable.add(DataBaseContract.GOODS_PICTURE_TABLE_NAME);
            }

            if (!tableExists(db, DataBaseContract.LOGIN_TABLE_NAME)){
                listOfTable.add(DataBaseContract.LOGIN_TABLE_NAME);
            }

            if (!tableExists(db, DataBaseContract.ORDER_HEAD_TABLE_NAME)){
                listOfTable.add(DataBaseContract.ORDER_HEAD_TABLE_NAME);
            }else{
                checkRowInTable(db,DataBaseContract.ORDER_HEAD_TABLE_NAME,DataBaseContract.R_ORDER_HEADER.RH_DELIVERY_DATE,"text");
            }

            if (!tableExists(db, DataBaseContract.ORDER_ROW_TABLE_NAME)){
                listOfTable.add(DataBaseContract.ORDER_ROW_TABLE_NAME);
            }

            if (!tableExists(db, DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME)){
                listOfTable.add(DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME);
            }


            onCreate(db);
        }

    }

    private void checkRowInTable(SQLiteDatabase db, String tableName, String columnName, String type) {
        String query = "SELECT * FROM "+tableName+" LIMIT 1";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.getColumnIndex(columnName) < 0){
            db.execSQL("ALTER TABLE "+tableName+" ADD COLUMN "+columnName+" "+type);
        }
    }


    protected boolean createTableGoods(SQLiteDatabase db){
        int i;
        try {
            String CREATE_GOODS_TABLE = "create table "+ DataBaseContract.GOODS_TABLE_NAME+
                    "("+ DataBaseContract.R_GOODS.RG_KEY_ID+" integer primary key, "+
                    DataBaseContract.R_GOODS.RG_ID_1C+" text, "+
                    DataBaseContract.R_GOODS.RG_CODE_1C+" text, "+
                    DataBaseContract.R_GOODS.RG_ARTICLE+" text, "+
                    DataBaseContract.R_GOODS.RG_GOOD_OF_WEEK+" boolen, "+
                    DataBaseContract.R_GOODS.RG_DESCRIPTION+" text, "+
                    DataBaseContract.R_GOODS.RG_BRAND+" text, "+
                    DataBaseContract.R_GOODS.RG_CATEGORY+" text, "+
                    DataBaseContract.R_GOODS.RG_AVAILABLE+" boolean, "+
                    DataBaseContract.R_GOODS.RG_RRC+" real, "+
                    DataBaseContract.R_GOODS.RG_PRICE+" real, "+
                    DataBaseContract.R_GOODS.RG_QUANTITY+" real, "+
                    DataBaseContract.R_GOODS.RG_DATE_OF_RENOVATION+" text,"+
                    DataBaseContract.R_GOODS.RG_URL_IMAGE+" text,"+
                    DataBaseContract.R_GOODS.RG_BOX+" real, "+
                    DataBaseContract.R_GOODS.RG_PACKAGE+" real)";

            db.execSQL(CREATE_GOODS_TABLE);
            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }
    }

    protected boolean createTablePicture_Goods(SQLiteDatabase db){
        int i;
        try {
            String CREATE_PICTURE_GOODS_TABLE = "create table "+ DataBaseContract.GOODS_PICTURE_TABLE_NAME+
                    "("+ DataBaseContract.R_PICTURE.RP_KEY_ID+" integer primary key, "+
                    DataBaseContract.R_PICTURE.RP_ID_1C+" text, "+
                    DataBaseContract.R_PICTURE.RP_URL_IMAGE+" text, "+
                    DataBaseContract.R_PICTURE.RP_IMAGE+" blob)";

            db.execSQL(CREATE_PICTURE_GOODS_TABLE);
            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }
    }

    protected boolean createTableCart(SQLiteDatabase db){
        int i;
        try {
            String CREATE_CART_TABLE = "create table "+ DataBaseContract.CART_TABLE_NAME+
                    "("+ DataBaseContract.R_CART.RC_KEY_ID+" integer primary key, "+
                    DataBaseContract.R_CART.RC_UUID+" text, "+
                    DataBaseContract.R_CART.RC_UUID_ORDER+" text, "+
                    DataBaseContract.R_CART.RC_DELIVERY_DATE+" text, "+
                    DataBaseContract.R_CART.RC_CLIENT_NAME+" text, "+
                    DataBaseContract.R_CART.RC_CLIENT_ID_1C+" text, "+
                    DataBaseContract.R_CART.RC_CLIENT_GUID_1C+" text," +
                    DataBaseContract.R_CART.RC_CLIENT_PHONE+" text, " +
                    DataBaseContract.R_CART.RC_CLIENT_API_KEY+" text, " +
                    DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT+" text, " +
                    DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT_CODE+" text, " +
                    DataBaseContract.R_CART.RC_GOOD_GUID_1C+" text, " +
                    DataBaseContract.R_CART.RC_QTY+" real, " +
                    DataBaseContract.R_CART.RC_PRICE+" real, " +
                    DataBaseContract.R_CART.RC_TOTAL+" real" +
                    ")";

            db.execSQL(CREATE_CART_TABLE);
            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }
    }

    protected boolean createTableOrderHeader(SQLiteDatabase db){
        int i;
        try {
            String CREATE_ORDER_HEAD_TABLE = "create table "+ DataBaseContract.ORDER_HEAD_TABLE_NAME+
                    "("+ DataBaseContract.R_ORDER_HEADER.RH_KEY_ID+" integer primary key, "+
                    DataBaseContract.R_ORDER_HEADER.RH_UUID+" text, "+
                    DataBaseContract.R_ORDER_HEADER.RH_DATE+" text, "+
                    DataBaseContract.R_ORDER_HEADER.RH_DELIVERY_DATE+" text, "+
                    DataBaseContract.R_ORDER_HEADER.RH_STATUS+" text, "+
                    DataBaseContract.R_ORDER_HEADER.RH_ORDER_NUMBER_1c+" text," +
                    DataBaseContract.R_ORDER_HEADER.RH_CLIENT_NAME+" text, " +
                    DataBaseContract.R_ORDER_HEADER.RH_CLIENT_ID_1C+" text, " +
                    DataBaseContract.R_ORDER_HEADER.RH_CLIENT_GUID_1C+" text, " +
                    DataBaseContract.R_ORDER_HEADER.RH_CLIENT_PHONE+" text, " +
                    DataBaseContract.R_ORDER_HEADER.RH_CLIENT_API_KEY+" text, " +
                    DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT+" text, " +
                    DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT_CODE+" text, " +
                    DataBaseContract.R_ORDER_HEADER.RH_COMMENT+" text, " +
                    DataBaseContract.R_ORDER_HEADER.RH_QTY+" real, " +
                    DataBaseContract.R_ORDER_HEADER.RH_TOTAL+" real " +
                    ")";

            db.execSQL(CREATE_ORDER_HEAD_TABLE);
            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }
    }

    protected boolean createTableOrderRow(SQLiteDatabase db){
        int i;
        try {
            String CREATE_ORDER_ROW_TABLE = "create table "+ DataBaseContract.ORDER_ROW_TABLE_NAME+
                    "("+ DataBaseContract.R_ORDER_ROW.R_KEY_ID+" integer primary key, "+
                    DataBaseContract.R_ORDER_ROW.R_UUID+" text, "+
                    DataBaseContract.R_ORDER_ROW.R_GOOD_GUID_1C+" text, "+

                    DataBaseContract.R_ORDER_ROW.R_PRICE+" real, " +
                    DataBaseContract.R_ORDER_ROW.R_QTY+" real, " +
                    DataBaseContract.R_ORDER_ROW.R_TOTAL+" real " +
                    ")";

            db.execSQL(CREATE_ORDER_ROW_TABLE);
            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearTable(SQLiteDatabase db, String nameTable){
        try {
            db.execSQL("DROP TABLE IF EXISTS " + nameTable);
            if (nameTable == DataBaseContract.GOODS_TABLE_NAME){
                createTableGoods(db);
            }

            if (nameTable == DataBaseContract.GOODS_PICTURE_TABLE_NAME){
                createTablePicture_Goods(db);
            }

            if (nameTable == DataBaseContract.LOGIN_TABLE_NAME){
                createTableLOGIN(db);
            }

            if (nameTable == DataBaseContract.CART_TABLE_NAME){
                createTableCart(db);
                Utils.mainContext.getContentResolver().notifyChange(DataBaseContract.BASE_CONTENT_URI_CART, null);
                Utils.mCurCartQty.setValue(0);
            }

            if (nameTable == DataBaseContract.ORDER_HEAD_TABLE_NAME){
                createTableOrderHeader(db);
            }

            if (nameTable == DataBaseContract.ORDER_ROW_TABLE_NAME){
                createTableOrderRow(db);
            }

            if (nameTable == DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME){
                createTableTypeOfShipment(db);
            }

            return true;
        }catch(Error e){
            e.printStackTrace();
            return false;
        }

    }

    public void clearAllTable(DataBaseHandler dbOH) {
        SQLiteDatabase db = dbOH.getWritableDatabase();
        for (int i=0; i< DataBaseContract.ALL_TABLES.all_tables().size();i++){
            String tableName = DataBaseContract.ALL_TABLES.all_tables().get(i);

            clearTable(db,tableName);
        }

    }
}
