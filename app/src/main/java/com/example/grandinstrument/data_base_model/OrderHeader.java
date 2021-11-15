package com.example.grandinstrument.data_base_model;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.BaseColumns;

import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

import java.util.ArrayList;

public class OrderHeader {
    private String id;
    private String uuid;
    private double order_date;
    private String order_status;
    private String order_number_1c;
    private Client client;
    private String type_of_shipment;
    private String type_of_shipment_code;
    private int qty;
    private double total;

    public static boolean SaveOrderToBase(OrderHeader orderHeader, ContentResolver contentResolver, String errors) {

        ContentValues content_H= new ContentValues();

        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_UUID, orderHeader.getUuid());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_DATE, orderHeader.getOrder_date());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_STATUS, orderHeader.getOrder_status());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_ORDER_NUMBER_1c, orderHeader.getOrder_number_1c());

        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_NAME, orderHeader.getClient().getName());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_ID_1C, orderHeader.getClient().getId_1c());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_GUID_1C, orderHeader.getClient().getGuid_1c());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_PHONE, orderHeader.getClient().getPhone());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_API_KEY, orderHeader.getClient().getApi_key());



        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT, orderHeader.getType_of_shipment());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT_CODE, orderHeader.getType_of_shipment_code());

        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_TOTAL, orderHeader.getTotal());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_QTY, orderHeader.getQty());



        ArrayList<ContentProviderOperation> list = new
                ArrayList<ContentProviderOperation>();
        list.add(ContentProviderOperation.
                newUpdate(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER)
                .withSelection(DataBaseContract.R_ORDER_HEADER.RH_UUID+ " = ?", new String[]{orderHeader.getUuid()})
                .withValues(content_H)
                .build());

        try {
            contentResolver.applyBatch(DataBaseContract.URI_AUTHORITY, list);

        } catch (RemoteException e) {
            e.printStackTrace();
            errors = errors + e.getMessage();
            return false;
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            errors = errors + e.getMessage();
            return false;
        }

        return true;

    }

    public static OrderHeader loadOrderHeader(String curUUID) {
        ContentResolver contentResolver= Utils.mainContext.getContentResolver();
        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER,DataBaseContract.R_ORDER_HEADER.ORDER_HEADER_COLUMNS, DataBaseContract.R_ORDER_HEADER.RH_UUID+"=? ", new String[]{curUUID},null);

        OrderHeader curOrderHeader = null;

        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            curOrderHeader = new OrderHeader();
            curOrderHeader.setUuid(curUUID);

            Client client = new Client();

            client.setApi_key(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_API_KEY)));
            client.setGuid_1c(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_GUID_1C)));
            client.setName(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_NAME)));
            client.setId_1c(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_ID_1C)));
            client.setPhone(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_PHONE)));

            curOrderHeader.setClient(client);
            curOrderHeader.setOrder_date(cursor.getLong(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_DATE)));
            curOrderHeader.setOrder_status(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_STATUS)));
            curOrderHeader.setOrder_number_1c(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_ORDER_NUMBER_1c)));
            curOrderHeader.setType_of_shipment(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT)));
            curOrderHeader.setType_of_shipment_code(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT_CODE)));
            curOrderHeader.setQty(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_QTY)));
            curOrderHeader.setTotal(cursor.getDouble(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TOTAL)));
            curOrderHeader.setId(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_KEY_ID)));


        }
        return curOrderHeader;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public String getType_of_shipment_code() {
        return type_of_shipment_code;
    }

    public void setType_of_shipment_code(String type_of_shipment_code) {
        this.type_of_shipment_code = type_of_shipment_code;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public OrderHeader() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getOrder_date() {
        return order_date;
    }

    public void setOrder_date(double order_date) {
        this.order_date = order_date;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_number_1c() {
        return order_number_1c;
    }

    public void setOrder_number_1c(String order_number_1c) {
        this.order_number_1c = order_number_1c;
    }

    public String getType_of_shipment() {
        return type_of_shipment;
    }

    public void setType_of_shipment(String type_of_shipment) {
        this.type_of_shipment = type_of_shipment;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Заказ №"+id+"  от "+ Utils.getDate(order_date)  +" : "+order_status;
    }


}
