package com.example.grandinstrument.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.grandinstrument.CartActivity;
import com.example.grandinstrument.MainActivity;
import com.example.grandinstrument.OrderActivity;
import com.example.grandinstrument.R;
import com.example.grandinstrument.data_base_adapter.DataBaseHandler;
import com.example.grandinstrument.data_base_model.Client;
import com.example.grandinstrument.data_base_model.Goods;
import com.example.grandinstrument.data_base_model.TypeOfShipment;
import com.example.grandinstrument.data_base_model.User;
import com.example.grandinstrument.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.widget.Toast.makeText;


public class Utils {

    public static User curUser;
    public static String mainServer;
    public static String GIUD_DEVICE;
    public static boolean allow_load_from_url;
    public static Client curClient;
    public static boolean showPrice;
    public static ArrayList<LeftSideBarItem> leftSideBarItems;
    public static Context mainContext;
    public static Intent loginIntent;

    public static MutableLiveData<Integer> mCurCartQty;
    public static MutableLiveData<Double> mCurSumCart;
    public static String formatDate = "dd.MM.yyyy hh:mm:ss";

    public static String[] mStatuses;

    public static ArrayList<TypeOfShipment> shipmentList;




    public static void setShowPrice(Context context, boolean showPrice) {
        Utils.showPrice = showPrice;

        SharedPreferences sharedPreferences = context.getSharedPreferences("showPrice", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("showPrice", showPrice);

    }



    public static boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public static boolean setTextAvailable(Context context, TextView present_cb, int qty, boolean onlySign, String dateMillSec) {
        String textQty = "";


        long curDateMillSec = 0;
        try {
            curDateMillSec = Long.parseLong(dateMillSec);
        } catch (Exception e) {
            curDateMillSec = 0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curDateMillSec);

        Date currentTime = Calendar.getInstance().getTime();
        Date pastTime = cal.getTime();

        long diffInMs;
        diffInMs = currentTime.getTime() - pastTime.getTime();

        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        if (diffInSec > 60) {

            if (onlySign) {
                textQty = "";
            } else {
                textQty = "нет на складе";
            }
            present_cb.setText(textQty);
            present_cb.setBackground(null);
            present_cb.setTextColor(Resources.getSystem().getColor(android.R.color.white));
            return true;
        }

        if (qty <= 0) {
            if (onlySign) {
                textQty = "-";
            } else {
                textQty = "нет на складе";
            }
            present_cb.setBackground(context.getDrawable(R.drawable.circle_background_red));
            present_cb.setTextColor(Resources.getSystem().getColor(android.R.color.white));
        } else if (qty < 10) {
            if (onlySign) {
                textQty = "+";
            } else {
                textQty = "заканчивается";
            }
            present_cb.setTextColor(Resources.getSystem().getColor(android.R.color.black));
            present_cb.setBackground(context.getDrawable(R.drawable.circle_background_orange));
        } else {
            if (onlySign) {
                textQty = "+";
            } else {
                textQty = "в наличии";
            }
            present_cb.setTextColor(Resources.getSystem().getColor(android.R.color.black));
            present_cb.setBackground(context.getDrawable(R.drawable.circle_background_green));
        }

        present_cb.setText(textQty);

        return false;

    }

    private static void setImageFromURl(Context context, String url_image, ImageView goods_iv) {
        try {
            Glide
                    .with(context)
                    .load(url_image)
                    .into(goods_iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setImageGoods(Context context, String url_image, String id_1c,ImageView goods_iv) {

        goods_iv.setImageBitmap(null);
        if (url_image != null && !url_image.isEmpty()) {
            ContentResolver crP = context.getContentResolver();
            Cursor cursorP = crP.query(DataBaseContract.BASE_CONTENT_URI_GOODS_PICTURE,
                    DataBaseContract.R_PICTURE.PICTURE_COLUMNS,
                    DataBaseContract.R_PICTURE.RP_ID_1C + "=?",
                    new String[]{id_1c}, null);


            if (Utils.allow_load_from_url) {
                if (cursorP == null) {
                    setImageFromURl(context, url_image, goods_iv);
                } else {
                    cursorP.moveToFirst();
                    byte[] image = null;
                    try {
                        image = cursorP.getBlob(cursorP.getColumnIndexOrThrow(DataBaseContract.R_PICTURE.RP_IMAGE));
                    } catch (Exception e) {

                    }

                    if (image == null) {
                        setImageFromURl(context, url_image, goods_iv);
                    } else {
                        goods_iv.setImageBitmap(DbBitmapUtility.getImage(image));
                    }
                }

            } else {
                cursorP.moveToFirst();
                byte[] image = null;
                try {
                    image = cursorP.getBlob(cursorP.getColumnIndexOrThrow(DataBaseContract.R_PICTURE.RP_IMAGE));
                    goods_iv.setImageBitmap(DbBitmapUtility.getImage(image));
                } catch (Exception e) {

                }
            }


        }

    }

    public static void clearAllTables(Context context) {
        DataBaseHandler dbOH = new DataBaseHandler(context);
        dbOH.clearAllTable(dbOH);
    }

    public static void clearLoginTable(Context context) {
        DataBaseHandler dbOH = new DataBaseHandler(context);
        SQLiteDatabase db = dbOH.getWritableDatabase();
        dbOH.clearTable(db, DataBaseContract.LOGIN_TABLE_NAME);
    }

    private static JSONObject getObjectAvailable(String guid, String article, int qty) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject items = new JSONObject();
        JSONObject item = new JSONObject();
        item.put("guid", guid);
        item.put("qty", qty);
        item.put("article", article);


        items.put("item", item);
        obj.put("items", items);


        return obj;
    }

    public static JsonObjectRequest requestAvailable(Context context, String uid, String article) throws JSONException {
        String url;

        url = Utils.mainServer + "/hs/ExchangeOrders/v1/get_available";

        JSONObject requestObject = new JSONObject();
        requestObject = getObjectAvailable(uid, article, 0);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                boolean success;

                JSONArray jsonData;
                JSONArray jsonErrors;


                try {
                    success = (boolean) response.getBoolean("success");

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }


                if (!success) {
                    boolean disconnect_and_clear_base = false;
                    try {
                        disconnect_and_clear_base = response.getBoolean("disconnect_and_clear_base");
                        if (disconnect_and_clear_base) {

                            Utils.clearLoginTable(context);
                            Utils.curUser = null;
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        jsonErrors = response.getJSONArray("errors");
                        for (int i = 0; i < jsonErrors.length(); i++) {
                            String er = jsonErrors.getString(i);
                            Log.i("request", er);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                int a = 0;

                try {
                    jsonData = response.getJSONArray("items");
                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonObject = jsonData.getJSONObject(i);
                        int qty = jsonObject.getInt("qty");
                        boolean present = qty > 0 ? true : false;
                        String id_1c = jsonObject.getString("guid");

                        ContentResolver contentResolver = context.getContentResolver();
                        ContentValues contentValues = new ContentValues();


                        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_GOODS,
                                DataBaseContract.R_GOODS.GOODS_COLUMNS_FOR_LIST, DataBaseContract.R_GOODS.RG_ID_1C + "=?", new String[]{id_1c}, null);
                        if (cursor != null && cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            boolean curPresent = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_AVAILABLE)));
                            int curQty = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_QUANTITY));

                            long currentTime = Calendar.getInstance().getTimeInMillis();
                            contentValues.put(DataBaseContract.R_GOODS.RG_DATE_OF_RENOVATION, currentTime);
                            contentValues.put(DataBaseContract.R_GOODS.RG_AVAILABLE, present);
                            contentValues.put(DataBaseContract.R_GOODS.RG_QUANTITY, qty);
                            contentResolver.update(DataBaseContract.BASE_CONTENT_URI_GOODS, contentValues, DataBaseContract.R_GOODS.RG_ID_1C + "=?", new String[]{id_1c});

//                        if (curQty != qty ){
//                            contentResolver.update(DataBaseContract.BASE_CONTENT_URI_GOODS,contentValues,DataBaseContract.R_GOODS.RG_ID_1C+"=?",new String[]{id_1c});
//                        }
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("ID_android", Utils.curUser.getID_android());
                headers.put("log_android", Utils.curUser.getEmail());
                headers.put("pas_android", Utils.curUser.getPassword());
                headers.put("Accept", "application/json");
                return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return request;
    }

    public static JSONObject getJSONObject(ArrayList< HashMap<String,String>> data) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject items = new JSONObject();

        JSONArray ja = new JSONArray();
        for (int i = 0; i<data.size(); i++){
            HashMap<String,String> hashMap = data.get(i);

            JSONObject item = new JSONObject();
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                item.put(key, value);
            }

            ja.put(item);
        }

        obj.put("items", ja);
        obj.put("Product_Row_Quantity", data.size());

        return obj;
    }

    private static JSONObject getObjectPrice(String guid, String article, int qty) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("guid", guid);
        item.put("article", article);
        item.put("qty", 1);
        items.put(item);
        obj.put("items", items);
        obj.put("Product_Row_Quantity", 1);

        return obj;
    }

    public static JsonObjectRequest requestPrice(Context context, String uid, String article) throws JSONException {
        String url;

        url = Utils.mainServer + "/hs/GetPrices/v1/get_price";

        JSONObject requestObject = new JSONObject();
        requestObject = getObjectPrice(uid, article, 0);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                boolean success;

                JSONArray jsonData;
                JSONArray jsonErrors;


                try {
                    success = (boolean) response.getBoolean("success");

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }


                if (!success) {
                    boolean disconnect_and_clear_base = false;
                    try {
                        disconnect_and_clear_base = response.getBoolean("disconnect_and_clear_base");
                        if (disconnect_and_clear_base) {

                            Utils.clearLoginTable(context);
                            Utils.curUser = null;
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        jsonErrors = response.getJSONArray("errors");
                        for (int i = 0; i < jsonErrors.length(); i++) {
                            String er = jsonErrors.getString(i);
                            Log.i("request", er);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                int a = 0;

                try {
                    jsonData = response.getJSONArray("items");
                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonObject = jsonData.getJSONObject(i);
                        String id_1c = jsonObject.getString("guid");
                        double price = jsonObject.getDouble("price");
                        double rrc_price = jsonObject.getDouble("rrc_price");

                        ContentResolver contentResolver = context.getContentResolver();
                        ContentValues contentValues = new ContentValues();


                        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_GOODS,
                                DataBaseContract.R_GOODS.GOODS_COLUMNS_FOR_LIST, DataBaseContract.R_GOODS.RG_ID_1C + "=?", new String[]{id_1c}, null);
                        if (cursor != null && cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            long currentTime = Calendar.getInstance().getTimeInMillis();
                            contentValues.put(DataBaseContract.R_GOODS.RG_PRICE, price);
                            contentValues.put(DataBaseContract.R_GOODS.RG_RRC, rrc_price);

                            contentResolver.update(DataBaseContract.BASE_CONTENT_URI_GOODS, contentValues, DataBaseContract.R_GOODS.RG_ID_1C + "=?", new String[]{id_1c});
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("ID_android", Utils.curUser.getID_android());
                headers.put("log_android", Utils.curUser.getEmail());
                headers.put("pas_android", Utils.curUser.getPassword());
                headers.put("Customer_Code_1C", Utils.curClient.getId_1c());
                headers.put("APIkey", Utils.curClient.getApi_key());
                headers.put("Accept", "application/json");
                return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return request;
    }

    public static void load_Data(Context context, String uid, String article) throws JSONException {

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);


        JsonObjectRequest requestAvailable = requestAvailable(context, uid, article);
        // Schedule the request on the queue
        requestQueue.add(requestAvailable);

        if (Utils.curClient != null) {
            JsonObjectRequest requestPrice = requestPrice(context, uid, article);

            requestQueue.add(requestPrice);
        }

    }

    public static void clearPriceColumns(Context context) {

        ClearPriceColumns clearPriceColumns = new ClearPriceColumns(context);
        clearPriceColumns.execute();

    }

    public static void setCartChange(int value, String id_1c, double price) {

        ContentResolver contentResolver = mainContext.getContentResolver();
        ContentValues contentValues = new ContentValues();

        int curQty = 0;
        //Cursor cursorGood = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_GOODS,DataBaseContract.R_GOODS.GOODS_COLUMNS_FOR_LIST,DataBaseContract.R_GOODS.RG_ID_1C+"=?", new String[]{id_1c},null);
        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_CART,DataBaseContract.R_CART.CART_COLUMNS,DataBaseContract.R_CART.RC_GOOD_GUID_1C+"=?", new String[]{id_1c},null);
        if (cursor != null && cursor.getCount() !=0){
            cursor.moveToFirst();

            curQty = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.R_CART.RC_QTY));

            if (value < 0 && curQty==0){
                return;
            }

            curQty= curQty + value;

            contentValues.put(DataBaseContract.R_CART.RC_QTY,curQty);
            contentValues.put(DataBaseContract.R_CART.RC_PRICE,price);
            contentValues.put(DataBaseContract.R_CART.RC_TOTAL,price*curQty);
            contentResolver.update(DataBaseContract.BASE_CONTENT_URI_CART,contentValues,DataBaseContract.R_CART.RC_GOOD_GUID_1C+"=?", new String[]{id_1c});

        }else{
            if (value < 0){
                return;
            }else{
                curQty = 1;
                if (curClient != null){
                    contentValues.put(DataBaseContract.R_CART.RC_CLIENT_NAME,curClient.getName());
                    contentValues.put(DataBaseContract.R_CART.RC_CLIENT_ID_1C,curClient.getId_1c());
                    contentValues.put(DataBaseContract.R_CART.RC_CLIENT_GUID_1C,curClient.getGuid_1c());
                    contentValues.put(DataBaseContract.R_CART.RC_CLIENT_PHONE,curClient.getPhone());
                    contentValues.put(DataBaseContract.R_CART.RC_CLIENT_API_KEY,curClient.getApi_key());
                }else{

                }

                contentValues.put(DataBaseContract.R_CART.RC_GOOD_GUID_1C,id_1c);
                contentValues.put(DataBaseContract.R_CART.RC_PRICE,price);
                contentValues.put(DataBaseContract.R_CART.RC_QTY,curQty);
                contentValues.put(DataBaseContract.R_CART.RC_TOTAL,price*curQty);
                contentResolver.insert(DataBaseContract.BASE_CONTENT_URI_CART,contentValues);
            }
        }

        mCurCartQty.setValue(getQtyInCart());
        if (mCurSumCart != null){
            mCurSumCart.setValue(getSumCart());
        }


        //mainContext.getContentResolver().notifyChange(DataBaseContract.BASE_CONTENT_URI_GOODS, null);
    }

    public static Integer getQtyInCart() {

        String[] columns = new String[] { "sum(" + DataBaseContract.R_CART.RC_QTY + ")" };

        ContentResolver contentResolver = mainContext.getContentResolver();
        Cursor cursorTotal = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_CART,columns,null,null);
        cursorTotal.moveToFirst();
        int qty = 0;
        qty= (int) (qty + cursorTotal.getDouble(0));

        return  qty;
    }

    public static int getQtyInCartForGood(String guid_1c) {
        String[] columns = new String[] { "sum(" + DataBaseContract.R_CART.RC_QTY + ")" };

        ContentResolver contentResolver = mainContext.getContentResolver();
        Cursor cursorTotal = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_CART,columns,DataBaseContract.R_CART.RC_GOOD_GUID_1C + "=?",new String[]{guid_1c},null);
        cursorTotal.moveToFirst();
        int qty = 0;
        qty= (int) (qty + cursorTotal.getDouble(0));

        return  qty;

    }

    public static Goods getGoodFromDB(String guid_1c) {
        ContentResolver contentResolver = mainContext.getContentResolver();
        Cursor cursorG = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_GOODS,DataBaseContract.R_GOODS.GOODS_COLUMNS_FOR_LIST,DataBaseContract.R_GOODS.RG_ID_1C +"= ?",new String[]{guid_1c}, null);
        cursorG.moveToFirst();

        Goods goods = null;
        if (cursorG != null && cursorG.getCount() != 0){

            goods = new Goods();
            if (cursorG.getInt(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_GOOD_OF_WEEK)) == 0){
                goods.setGood_of_week(false);
            }else{
                goods.setGood_of_week(true);
            }
            goods.setArticle(cursorG.getString(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_ARTICLE)));
            goods.setBrand(cursorG.getString(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_BRAND)));
            goods.setId_1c(cursorG.getString(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_ID_1C)));
            goods.setUrl_of_image(cursorG.getString(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_URL_IMAGE)));
            goods.setDescription(cursorG.getString(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_DESCRIPTION)));
            goods.setDate_of_renovation(cursorG.getString(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_DATE_OF_RENOVATION)));
            goods.setPrice(cursorG.getFloat(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_PRICE)));
            goods.setRrc(cursorG.getFloat(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_RRC)));
            goods.setCategory(cursorG.getString(cursorG.getColumnIndex(DataBaseContract.R_GOODS.RG_CATEGORY)));

        }
        return goods;

    }

    public static boolean checkCartIsEmpty(String message) {
        if (mCurCartQty == null || mCurCartQty.getValue() == 0){
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Utils.mainContext);
        builder.setTitle("Внимание!!!")
                .setMessage(message);
        builder.setPositiveButton("Ок",null);
        builder.setCancelable(true);
        builder.create();
        builder.show();

        return false;

    }

    public static void setupClientFromCart() {
        ContentResolver contentResolver = mainContext.getContentResolver();
        String selection = DataBaseContract.R_CART.RC_QTY + " <> ?";
        String[] args = new String[]{"0"};
        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_CART,DataBaseContract.R_CART.CART_COLUMNS,selection,args,null);
        if (cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            if (cursor.getString(cursor.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_API_KEY)) != null){
                Utils.curClient = new Client();
                Utils.curClient.setApi_key(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_API_KEY)));
                Utils.curClient.setGuid_1c(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_GUID_1C)));
                Utils.curClient.setId_1c(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_ID_1C)));
                Utils.curClient.setName(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_NAME)));
                Utils.curClient.setPhone(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_PHONE)));
            }

        }else{
            clearTable(DataBaseContract.CART_TABLE_NAME);
        }

    }

    public static void clearTable(String tableName) {
        DataBaseHandler dbOH = new DataBaseHandler(mainContext.getApplicationContext());
        dbOH.clearTable(dbOH.getWritableDatabase(),tableName);
    }

    public static double getSumCart() {
        double sum = 0;
        ContentResolver contentResolver = mainContext.getContentResolver();
        Cursor cursorTotal = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_CART, DataBaseContract.R_CART.CART_COLUMNS,null,null);
        cursorTotal.moveToFirst();

        for (int i=0; i< cursorTotal.getCount();i++){
            cursorTotal.moveToPosition(i);
            double price = cursorTotal.getDouble(cursorTotal.getColumnIndex(DataBaseContract.R_CART.RC_PRICE));
            double qty   = cursorTotal.getDouble(cursorTotal.getColumnIndex(DataBaseContract.R_CART.RC_QTY));
            sum = sum + price*qty;
        }


        return Math.round(sum*100.0)/100.0;
    }

    public static void loadPriceForCart(Context context) {

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_CART,DataBaseContract.R_CART.CART_COLUMNS, null,null,null);

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

                Utils.LoadPrice loadPrice = new Utils.LoadPrice(context,data);
                loadPrice.execute();

            }
        }
    }

    private static void notifyClientSelected(Client client, Context context){

        if (context instanceof OrderActivity){
            ((OrderActivity) context).setClient(client);
        }

        if (context instanceof CartActivity){
            ((CartActivity) context).setClient(client);
        }

        if (context instanceof MainActivity){
            ((MainActivity) context).setClient(client);
        }

    }
    public static void selectingClient(ArrayList<Client> arrayChoiceOfClient, Context context) {

        if (arrayChoiceOfClient == null) {
            return;
        }

        if (arrayChoiceOfClient.size() == 0) {
            return;
        }

        if (arrayChoiceOfClient.size() == 1){
            notifyClientSelected(arrayChoiceOfClient.get(0), context);
            return;
        }

        ArrayList<String> arrayList = new ArrayList<>();
        for (Client item:arrayChoiceOfClient) {
            arrayList.add(item.getName()) ;
        }


        final CharSequence[] items = (CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Найденные клиенты:");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                notifyClientSelected(arrayChoiceOfClient.get(item), context);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private static  class LoadPrice extends AsyncTask<String, Void, Void> {
        private ProgressDialog mProgressDialog;

        private Context mContext;
        private String error = "";
        private ArrayList<HashMap<String,String>> data;
        private double total;



        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;


        public LoadPrice(Context context, ArrayList< HashMap<String,String>> data) {
            this.mContext = context;
            this.data  = data;
        }

        @Override
        protected Void doInBackground(String... codeClient) {

            error = "";
            String stringUrl = Utils.mainServer + "/hs/GetPrices/v1/get_price";
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
                connection.setRequestProperty("Customer_Code_1C", Utils.curClient.getId_1c());
                connection.setRequestProperty("APIkey",Utils.curClient.getApi_key());
                connection.setRequestProperty("Accept", "application/json");



                JSONObject requestObject = new JSONObject();
                try {
                    requestObject = Utils.getJSONObject(data);
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



            try {
                jsonData = jsonObject.getJSONArray("items");
                ContentResolver contentResolver = Utils.mainContext.getContentResolver();
                Cursor cursor_C = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_CART,DataBaseContract.R_CART.CART_COLUMNS,
                        null,null,null);


                ArrayList<ContentProviderOperation> list = new ArrayList<ContentProviderOperation>();

                total = 0;


                for (int j=0; j<cursor_C.getCount();j++){
                    boolean isPresent = false;
                    cursor_C.moveToPosition(j);
                    String id_1c_c = cursor_C.getString(cursor_C.getColumnIndex(DataBaseContract.R_CART.RC_GOOD_GUID_1C));

                    for (int i=0; i< jsonData.length();i++ ){
                        JSONObject jObject = jsonData.getJSONObject(i);

                        String id_1c = jObject.getString("guid");
                        double price = jObject.getDouble("price");

                        if (id_1c_c.equals(id_1c)){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DataBaseContract.R_CART.RC_PRICE, price);
                            contentValues.put(DataBaseContract.R_CART.RC_TOTAL, price*cursor_C.getInt(cursor_C.getColumnIndex(DataBaseContract.R_CART.RC_QTY)));
                            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_API_KEY, Utils.curClient.getApi_key());
                            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_ID_1C, Utils.curClient.getId_1c());
                            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_NAME, Utils.curClient.getName());
                            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_PHONE, Utils.curClient.getPhone());
                            list.add(ContentProviderOperation.
                                    newUpdate(DataBaseContract.BASE_CONTENT_URI_CART)
                                    .withSelection(DataBaseContract.R_CART.RC_GOOD_GUID_1C+ "=?", new String[]{id_1c})
                                    .withValues(contentValues)
                                    .build());
                            total = total + cursor_C.getInt(cursor_C.getColumnIndex(DataBaseContract.R_CART.RC_QTY))*price;
                            break;
                        }
                    }
                }

                try {
                    contentResolver.applyBatch(DataBaseContract.URI_AUTHORITY, list);

                } catch (RemoteException e) {
                    e.printStackTrace();
                    error = error + e.getMessage();
                    return null;
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                    error = error + e.getMessage();
                    return null;
                }



            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ProgressDialog.show(mContext, "Обновление цен в корзине", "Обновляем цены ...");
            mProgressDialog.setCanceledOnTouchOutside(true); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
            mProgressDialog.setIcon(R.drawable.ic_baseline_refresh_24);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dlg) {
                    Utils.LoadPrice.this.cancel(true);
                }
            });
        }



        @Override
        protected void onPostExecute(Void result) {
            if (this.isCancelled()) {
                result = null;
                return;
            }

            if (error != null && !error.isEmpty()){
                makeText(mContext,error, Toast.LENGTH_LONG).show();
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (mCurSumCart==null){
                mCurSumCart = new MutableLiveData<>();
            }

            mCurSumCart.setValue(total);
        }
    }

    public static boolean saveCart(TypeOfShipment shipment) {
        if (getQtyInCart() == 0){
            return false;
        }

        ContentResolver contentResolver = mainContext.getContentResolver();
        ContentValues contentV_CartRow = new ContentValues();
        ContentValues contentV_CartHeader = new ContentValues();

        Cursor cursorCart = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_CART, DataBaseContract.R_CART.CART_COLUMNS,
                DataBaseContract.R_CART.RC_QTY +" <> 0",null,null);

        int totalQty = 0;
        double totalSum = 0;
        String uuid = null;
        Client client = null;

        ArrayList<ContentProviderOperation> list = new
                ArrayList<ContentProviderOperation>();
        list.add(ContentProviderOperation.
                newDelete(DataBaseContract.BASE_CONTENT_URI_ROW_ORDER).withSelection(DataBaseContract.R_ORDER_ROW.R_UUID+ " = ?", new String[]{uuid}).build());



        cursorCart.moveToFirst();
        for (int i=0; i<cursorCart.getCount();i++){
            cursorCart.moveToPosition(i);

            if (uuid == null){
                uuid = cursorCart.getString(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_UUID));
                if (uuid==null || uuid.trim().isEmpty()){
                    uuid = UUID.randomUUID().toString();
                }
            }

            if (client == null){
                client = new Client();
                if (cursorCart.getString(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_API_KEY)) != null){

                    client.setPhone(cursorCart.getString(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_PHONE)));
                    client.setName(cursorCart.getString(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_NAME)));
                    client.setId_1c(cursorCart.getString(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_ID_1C)));
                    client.setGuid_1c(cursorCart.getString(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_GUID_1C)));
                    client.setApi_key(cursorCart.getString(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_CLIENT_API_KEY)));
                }

            }

            int curQty = cursorCart.getInt(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_QTY));
            double curPrice = cursorCart.getDouble(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_PRICE));
            double curTotal = curQty*curPrice;
            contentV_CartRow.put(DataBaseContract.R_ORDER_ROW.R_UUID,uuid);
            contentV_CartRow.put(DataBaseContract.R_ORDER_ROW.R_GOOD_GUID_1C,cursorCart.getString(cursorCart.getColumnIndex(DataBaseContract.R_CART.RC_GOOD_GUID_1C)));
            contentV_CartRow.put(DataBaseContract.R_ORDER_ROW.R_PRICE,curPrice);
            contentV_CartRow.put(DataBaseContract.R_ORDER_ROW.R_TOTAL,curTotal);
            contentV_CartRow.put(DataBaseContract.R_ORDER_ROW.R_QTY,curQty);


            list.add(ContentProviderOperation.
                    newInsert(DataBaseContract.BASE_CONTENT_URI_ROW_ORDER).
                    withValues(contentV_CartRow).build());

            totalQty = totalQty + curQty;
            totalSum = totalSum + curTotal;
            contentV_CartRow = new ContentValues();

        }


        Cursor cursorHeader = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER, DataBaseContract.R_ORDER_HEADER.ORDER_HEADER_COLUMNS,
                DataBaseContract.R_ORDER_HEADER.RH_UUID+" = ?",new String[]{uuid},null);



        long currentTime = Calendar.getInstance().getTimeInMillis();

        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_UUID,uuid);
        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_DATE,currentTime);
        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_STATUS,mStatuses[0]);
        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_NAME,client.getName());
        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_ID_1C,client.getId_1c());
        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_GUID_1C,client.getGuid_1c());
        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_PHONE,client.getPhone());
        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_API_KEY,client.getApi_key());

        if (shipment != null){
            contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT,shipment.getName());
            contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT_CODE,shipment.getCode_1c());
        }

        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_QTY,totalQty);
        contentV_CartHeader.put(DataBaseContract.R_ORDER_HEADER.RH_TOTAL,totalSum);





        if (cursorHeader == null || cursorHeader.getCount() == 0){
            list.add(ContentProviderOperation.
                    newInsert(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER).withValues(contentV_CartHeader).build());
        }else{

            list.add(ContentProviderOperation.
                    newUpdate(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER).withValues(contentV_CartHeader).withSelection(DataBaseContract.R_ORDER_HEADER.RH_UUID+ " = ?", new String[]{uuid}).build());
        }

        try {
            contentResolver.applyBatch(DataBaseContract.URI_AUTHORITY, list);
            clearTable(DataBaseContract.CART_TABLE_NAME);

        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public static void setImageStatus(ImageView status_iv, String string) {
        switch (string){
        case "Сохранен":
            status_iv.setImageResource(R.drawable.ic_baseline_save_order_24);
            break;
            case "Выгружен":
                status_iv.setImageResource(R.drawable.ic_baseline_send_24);
                break;
        }

    }

    public static void Fill_shipment_list() {
        Utils.shipmentList = new ArrayList<>();
        Utils.shipmentList.add(new TypeOfShipment());

        ContentResolver contentResolver = mainContext.getContentResolver();
        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_TYPE_OF_SHIPMENT,DataBaseContract.R_TYPE_OF_SHIPMENT.TYPE_OF_SHIPMENT_COLUMNS,null,null,DataBaseContract.R_TYPE_OF_SHIPMENT.R_NAME);

        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            int tAvailable = cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_TYPE_OF_SHIPMENT.R_AVAILABLE));
            if (tAvailable!=0){
                TypeOfShipment typeOfShipment = new TypeOfShipment();
                typeOfShipment.setCode_1c(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_TYPE_OF_SHIPMENT.R_CODE)));
                typeOfShipment.setName(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_TYPE_OF_SHIPMENT.R_NAME)));
                typeOfShipment.setCode_main_type(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_TYPE_OF_SHIPMENT.R_CODE_MAINE)));
                typeOfShipment.setAvailable(true);
                Utils.shipmentList.add(typeOfShipment);
            }
        }

    }

    public static void insertTypeOfShipment(ArrayList<TypeOfShipment> arrayList) {
        ContentResolver  contentResolver = mainContext.getContentResolver();


        for (int i = 0; i < arrayList.size(); i++){
            TypeOfShipment typeOfShipment = arrayList.get(i);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBaseContract.R_TYPE_OF_SHIPMENT.R_CODE,typeOfShipment.getCode_1c());
            contentValues.put(DataBaseContract.R_TYPE_OF_SHIPMENT.R_NAME,typeOfShipment.getName());
            contentValues.put(DataBaseContract.R_TYPE_OF_SHIPMENT.R_CODE_MAINE,typeOfShipment.getCode_main_type());
            contentValues.put(DataBaseContract.R_TYPE_OF_SHIPMENT.R_AVAILABLE,typeOfShipment.isAvailable());
            contentResolver.insert(DataBaseContract.BASE_CONTENT_URI_TYPE_OF_SHIPMENT,contentValues);
        }

        Fill_shipment_list();

    }

    public static void showAlert(Context context, String title, String message, String positive) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        if (positive != null){
            builder.setPositiveButton(positive,null);
        }else{
            builder.setPositiveButton("Ok",null);
        }


        builder.show();
    }

    public static void changeClientOnCart() {
        ContentResolver contentResolver = mainContext.getContentResolver();
        ContentValues contentValues = new ContentValues();
        if (Utils.curClient == null){
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_PHONE, "");
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_NAME, "");
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_ID_1C, "");
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_GUID_1C, "");
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_API_KEY, "");
        }else{
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_PHONE, curClient.getPhone());
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_NAME, curClient.getName());
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_ID_1C, curClient.getId_1c());
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_GUID_1C, curClient.getGuid_1c());
            contentValues.put(DataBaseContract.R_CART.RC_CLIENT_API_KEY, curClient.getApi_key());
        }

        contentResolver.update(DataBaseContract.BASE_CONTENT_URI_CART,contentValues,null,null);
    }


    private static class ClearPriceColumns extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mProgressDialog;

        private Context mContext;
        private String error = "";


        public ClearPriceColumns(Context context) {
            this.mContext = context;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            ContentResolver contentResolver = mContext.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBaseContract.R_GOODS.RG_PRICE, 0);

            contentResolver.update(DataBaseContract.BASE_CONTENT_URI_GOODS, contentValues, "price<>?", new String[]{"0"});

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ProgressDialog.show(mContext, "Ожидайте...", "Ожидайте...");
            mProgressDialog.setCanceledOnTouchOutside(true); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
        }


        @Override
        protected void onPostExecute(Void result) {
            if (this.isCancelled()) {
                result = null;
                return;
            }

            if (error != null && !error.isEmpty()) {
                makeText(mContext, error, Toast.LENGTH_LONG).show();
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            MainActivity mainActivity = (MainActivity) mContext;
            mainActivity.setTitleActivity();
        }
    }

    public static class DbBitmapUtility {

        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);

            byte[] tempByte = stream.toByteArray();

            while (tempByte.length > 500000) {
                Bitmap tbitmap = BitmapFactory.decodeByteArray(tempByte, 0, tempByte.length);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (tbitmap.getWidth() * 0.8), (int) (tbitmap.getHeight() * 0.8), true);
                ByteArrayOutputStream t_stream = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.PNG, 100, t_stream);
                tempByte = t_stream.toByteArray();
            }

            return tempByte;
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {


            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getDate(double milliSeconds) {
        return getDate((long) milliSeconds, Utils.formatDate);
    }
}



