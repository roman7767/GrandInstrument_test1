package com.example.grandinstrument.data_base_model;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.grandinstrument.OrderActivity;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.Toast.makeText;

public class OrderHeader {
    private String id;
    private String uuid;
    private double order_date;
    private String delivery_date;
    private String order_status;
    private String order_number_1c;
    private Client client;
    private String type_of_shipment;
    private String type_of_shipment_code;
    private int qty;

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    private double total;
    private String error;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static boolean SaveOrderToBase(OrderHeader orderHeader, ContentResolver contentResolver, String errors) {

        ContentValues content_H= new ContentValues();

        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_UUID, orderHeader.getUuid());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_DATE, orderHeader.getOrder_date());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_DELIVERY_DATE, orderHeader.getDelivery_date());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_STATUS, orderHeader.getOrder_status());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_ORDER_NUMBER_1c, orderHeader.getOrder_number_1c());

        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_NAME, orderHeader.getClient().getName());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_ID_1C, orderHeader.getClient().getId_1c());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_GUID_1C, orderHeader.getClient().getGuid_1c());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_PHONE, orderHeader.getClient().getPhone());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_API_KEY, orderHeader.getClient().getApi_key());



        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT, orderHeader.getType_of_shipment());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT_CODE, orderHeader.getType_of_shipment_code());
        content_H.put(DataBaseContract.R_ORDER_HEADER.RH_COMMENT, orderHeader.getComment());

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
            curOrderHeader.setDelivery_date(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_DELIVERY_DATE)));
            curOrderHeader.setOrder_status(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_STATUS)));
            curOrderHeader.setOrder_number_1c(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_ORDER_NUMBER_1c)));
            curOrderHeader.setType_of_shipment(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT)));
            curOrderHeader.setType_of_shipment_code(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT_CODE)));
            curOrderHeader.setComment(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_COMMENT)));
            curOrderHeader.setQty(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_QTY)));
            curOrderHeader.setTotal(cursor.getDouble(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TOTAL)));
            curOrderHeader.setId(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_KEY_ID)));
            curOrderHeader.setDelivery_date(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_DELIVERY_DATE)));


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
        return "Заказ №"+id+"  от "+ Utils.getDate(order_date)  +". Статус:  "+order_status+".";
    }


     public boolean saveOrderTo_1c(Context context){
         ContentResolver contentResolver = context.getContentResolver();
         Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_ROW_ORDER,DataBaseContract.R_ORDER_ROW.ORDER_ROW_COLUMNS, DataBaseContract.R_ORDER_ROW.R_UUID + "=?",
                 new String[]{uuid},null);

         if (cursor !=null && cursor.getCount() >0){
             HashMap<String,String> hashMap;
             ArrayList< HashMap<String,String>> data = new ArrayList<>();

             for (int i=0; i<cursor.getCount(); i++){
                 cursor.moveToPosition(i);
                 hashMap = new HashMap<>();
                 hashMap.put("guid",cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_GOOD_GUID_1C)));
                 hashMap.put("qty",cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_QTY)));
                 data.add(hashMap);
             }

             if (data.size() > 0){

                 SaveOrderTo_1c saveOrderTo_1c = new SaveOrderTo_1c(context,data, error);
                 saveOrderTo_1c.execute();

             }
         }

        deleteUploadedOrders(context);
        return false;


     }

    private void deleteUploadedOrders(Context context) {
        Toast.makeText(context,"Начало удаления переданных заказов",Toast.LENGTH_LONG).show();
        String error = "";
        DeleteUploadedOrdersNext deleteUploadedOrdersNext = new DeleteUploadedOrdersNext(Utils.mainContext,error);
        deleteUploadedOrdersNext.execute();

    }

    private class DeleteUploadedOrdersNext extends AsyncTask<String, Void, Void> {
        private ProgressDialog mProgressDialog;

        private Context mContext;
        private String error = "";

        public DeleteUploadedOrdersNext(Context context, String error) {
            this.mContext = context;
            this.error  = error;
        }

        @Override
        protected Void doInBackground(String... params) {

            ContentResolver contentResolver = mContext.getContentResolver();

            ArrayList<ContentProviderOperation> list = new
                    ArrayList<ContentProviderOperation>();

            Cursor cursorH = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER,DataBaseContract.R_ORDER_HEADER.ORDER_HEADER_COLUMNS,
                    DataBaseContract.R_ORDER_HEADER.RH_STATUS +"=?", new String[]{DataBaseContract.STATUS_UPLOADED},null );

            for (int i=0; i<cursorH.getCount(); i++){
                cursorH.moveToPosition(i);
                String uuid = cursorH.getString(cursorH.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_UUID));

                list.add(ContentProviderOperation.
                     newDelete(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER).withSelection(DataBaseContract.R_ORDER_HEADER.RH_UUID+ " = ?", new String[]{uuid}).build());

                list.add(ContentProviderOperation.
                        newDelete(DataBaseContract.BASE_CONTENT_URI_ROW_ORDER).withSelection(DataBaseContract.R_ORDER_ROW.R_UUID+ " = ?", new String[]{uuid}).build());

            }

            try {
                contentResolver.applyBatch(DataBaseContract.URI_AUTHORITY, list);
                Utils.curClient = null;
                Utils.curOrder = null;
                contentResolver.notifyChange(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER,null);

            } catch (RemoteException e) {
                e.printStackTrace();
                error = error + "\n" + e.getMessage();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
                error = error + "\n" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ProgressDialog.show(mContext, "Удаление заказов", "Удаляем отправленные заказы...");
            mProgressDialog.setCanceledOnTouchOutside(true); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (error.isEmpty()){
                Toast.makeText(mContext,"Переданные заказы удалены.",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(mContext,"Ошибка удаления заказов." + error,Toast.LENGTH_LONG).show();
            }


        }
    }

    public void loadOrderToCart(Context context) {

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_ROW_ORDER,DataBaseContract.R_ORDER_ROW.ORDER_ROW_COLUMNS,
                DataBaseContract.R_ORDER_ROW.R_UUID +"=?", new String[]{getUuid()},null);

        if (cursor.getCount()!=0){
            for (int i=0;i<cursor.getCount();i++){
                cursor.moveToPosition(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(DataBaseContract.R_CART.RC_UUID_ORDER, getUuid());
                contentValues.put(DataBaseContract.R_CART.RC_DELIVERY_DATE, getDelivery_date());
                contentValues.put(DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT_CODE, getType_of_shipment_code());
                contentValues.put(DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT, getType_of_shipment());
                contentValues.put(DataBaseContract.R_CART.RC_CLIENT_API_KEY,getClient().getApi_key());
                contentValues.put(DataBaseContract.R_CART.RC_CLIENT_GUID_1C, getClient().getGuid_1c());
                contentValues.put(DataBaseContract.R_CART.RC_CLIENT_NAME, getClient().getName());
                contentValues.put(DataBaseContract.R_CART.RC_CLIENT_PHONE, getClient().getPhone());
                contentValues.put(DataBaseContract.R_CART.RC_CLIENT_ID_1C, getClient().getId_1c());

                contentValues.put(DataBaseContract.R_CART.RC_GOOD_GUID_1C, cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_GOOD_GUID_1C)));

                contentValues.put(DataBaseContract.R_CART.RC_PRICE, cursor.getLong(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_PRICE)));
                contentValues.put(DataBaseContract.R_CART.RC_QTY, cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_QTY)));
                contentValues.put(DataBaseContract.R_CART.RC_TOTAL, cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_QTY))*cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_PRICE)));
                contentResolver.insert(DataBaseContract.BASE_CONTENT_URI_CART,contentValues);


            }

            Utils.curOrder = this;

            Utils.mCurCartQty.setValue(Utils.getQtyInCart());
            if (Utils.mCurSumCart != null){
                Utils.mCurSumCart.setValue(Utils.getSumCart());
            }
            if (getClient().getApi_key()!=null){
                Utils.curClient = getClient();
            }
        }


    }

    private class SaveOrderTo_1c extends AsyncTask<String, Void, Void> {
        private ProgressDialog mProgressDialog;

        private Context mContext;
        private String error = "";
        private ArrayList< HashMap<String,String>> data;



        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;


        public SaveOrderTo_1c(Context context, ArrayList< HashMap<String,String>> data, String error) {
            this.mContext = context;
            this.data  = data;
            this.error  = error;
        }

        @Override
        protected Void doInBackground(String... codeClient) {

            error = "";
            String stringUrl = Utils.mainServer + "/hs/ExchangeOrders/v1/send_order_draft";
            String result = null;
            String inputLine;
            HttpURLConnection connection = null;
            boolean success;

            JSONObject jsonObject = null;
            JSONArray jsonData = null;
            JSONArray jsonErrors = null;

            //Create a connection
            URL myUrl = null;
            try {
                myUrl = new URL(stringUrl);
                connection =(HttpURLConnection) myUrl.openConnection();
                connection.setRequestProperty("ID_android",Utils.GIUD_DEVICE);
                connection.setRequestProperty("log_android",Utils.curUser.getEmail());
                connection.setRequestProperty("pas_android",Utils.curUser.getPassword());
                connection.setRequestProperty("Customer_Code_1C", getClient().getId_1c());
                connection.setRequestProperty("APIkey", getClient().getApi_key());
                connection.setRequestProperty("Accept", "application/json");



                JSONObject requestObject = new JSONObject();
                try {
                    requestObject = Utils.getJSONObject(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = e.getMessage();
                    return null;
                }

                try {
                    requestObject.put("UID",getUuid());
                    requestObject.put("Number",getId());
                    //requestObject.put("Comment", URLEncoder.encode(getComment(), "UTF-16"));
                    requestObject.put("Comment", URLEncoder.encode(getComment().replace(' ','~'),"UTF-8"));
                    requestObject.put("dispatch_method",getType_of_shipment_code());
                    requestObject.put("dateOfDelivery",getDelivery_date());
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = e.getMessage();
                    return null;
                }


                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(requestObject.toString());
                wr.flush();
                wr.close();




                connection.connect();

            } catch (IOException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }

            try {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }


            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }


            try {
                success = (boolean) jsonObject.getBoolean("success");

            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }


            if (!success) {
                try {
                    jsonErrors = jsonObject.getJSONArray("errors");
                    for (int i = 0; i < jsonErrors.length(); i++) {
                        String er = jsonErrors.getString(i);
                        Log.i("request", er);
                        error = error +"\n"+er;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = e.getMessage();
                    return null;
                }
            }

            if (success){
                try {
                    String Number_1c = jsonObject.getString("Number_1c");
                    setOrder_number_1c(Number_1c);
                    setOrder_status(DataBaseContract.STATUS_UPLOADED);
                    OrderHeader.SaveOrderToBase(OrderHeader.this,Utils.mainContext.getContentResolver(),error);


                } catch (JSONException e) {
                    e.printStackTrace();
                    error = e.getMessage();
                    return null;
                }

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ProgressDialog.show(mContext, "Выгрузка заказа", "Выгружаем заказ...");
            mProgressDialog.setCanceledOnTouchOutside(true); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
        }



        @Override
        protected void onPostExecute(Void result) {
            if (this.isCancelled()) {
                result = null;
                return;
            }

            if (error != null && !error.isEmpty()){
                Utils.showAlert(Utils.mainContext,"Ошибка выгрузки заказа", error,"Ok");
                //makeText(mContext,error, Toast.LENGTH_LONG).show();
            }else{
                Utils.showAlert(Utils.mainContext,"Выгрузка заказа", OrderHeader.this.toString() + "\n Выгружен успешно.","Ok");

//
//                OrderActivity orderActivity = (OrderActivity)mContext;
//                orderActivity.fillHeaderOnForm();

            }

//            if (mProgressDialog != null) {
//                mProgressDialog.dismiss();
//            }

        }
    }

}
