package com.example.grandinstrument;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grandinstrument.adapters.OrderRowRecyclerViewAdapter;
import com.example.grandinstrument.data_base_model.Client;
import com.example.grandinstrument.data_base_model.OrderHeader;
import com.example.grandinstrument.data_base_model.TypeOfShipment;
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
import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.Toast.makeText;

public class OrderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView order_rv;
    public OrderRowRecyclerViewAdapter adapter;
    private LinearLayoutManager manager;
    private TextView etClient;
    private TextView tv_date;
    private TextView tv_number;
    private TextView tv_Number1c;
    private TextView etSum;
    private Spinner sTypeOfShipment;
    private String uuid;
    private ArrayAdapter shipmentAdapter;
    private ImageButton ibSelectClient;

    private boolean modified;

    private static final int ORDER_LOADER = 33;
    private static final int ORDER_LOADER_WITH_SELECTION = 44;

    private static String selection = "";
    private static String[] selectionArgs = new String[]{};
    private OrderHeader orderHeader;


    private void fillHeaderOnForm() {
        tv_date.setText(Utils.getDate(orderHeader.getOrder_date()));
        tv_number.setText(orderHeader.getId());
        tv_Number1c.setText(orderHeader.getOrder_number_1c());
        etClient.setText(orderHeader.getClient().getName());

        if (orderHeader.getType_of_shipment_code() != null){
            sTypeOfShipment.setSelection(TypeOfShipment.getIndexByCode(orderHeader.getType_of_shipment_code()));
        }
        etSum.setText(String.valueOf(orderHeader.getTotal()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.grand_logo_48_48);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Заказ");

        order_rv = findViewById(R.id.order_row_rv);
        adapter = new OrderRowRecyclerViewAdapter(this);
        manager = new LinearLayoutManager(this);
        order_rv.setAdapter(adapter);
        order_rv.setLayoutManager(manager);
        order_rv.hasFixedSize();

        Intent intent = getIntent();
        uuid = intent.getStringExtra(DataBaseContract.R_ORDER_HEADER.RH_UUID);

        orderHeader = OrderHeader.loadOrderHeader(uuid);

        sTypeOfShipment = findViewById(R.id.sTypeOfShipment);
        shipmentAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,Utils.shipmentList);
        shipmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sTypeOfShipment.setAdapter(shipmentAdapter);

        tv_date = findViewById(R.id.tv_date);
        tv_number = findViewById(R.id.tv_number);
        tv_Number1c = findViewById(R.id.tv_Number1c);
        etClient = findViewById(R.id.etClient);
        etSum = findViewById(R.id.etSum);

        fillHeaderOnForm();

        selection = DataBaseContract.R_ORDER_ROW.R_UUID +" = ?";
        selectionArgs = new String[]{uuid};

        Button bt_save = findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrder();
            }
        });

        Button bt_save_1c = findViewById(R.id.bt_save_1c);
        bt_save_1c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                saveOrderTo_1c();
            }
        });

        Button bt_reloadPrice = findViewById(R.id.bt_reloadPrice);
        bt_reloadPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPriceForOrder();
            }
        });

        ibSelectClient = findViewById(R.id.ibSelectClient);
        ibSelectClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectClient();
            }
        });
    }

    private void SelectClient() {

        final EditText txtCodeClient = new EditText(this);
        txtCodeClient.setHint("");
        txtCodeClient.setInputType(InputType.TYPE_CLASS_NUMBER);


        final android.app.AlertDialog dialog;
        dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Код клиента")
                .setMessage("Введите код клиента")
                .setView(txtCodeClient)
                .setPositiveButton("ОК", null)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();


        txtCodeClient.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            String curCode = txtCodeClient.getText().toString().trim().replaceAll("\n","");
                            startLoadClient(curCode,dialog,txtCodeClient);
                            return true;

                        default:
                            break;
                    }
                }return false;
            }
        });

        dialog.show();

        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String curCode = txtCodeClient.getText().toString().trim().replaceAll("\n","");
                startLoadClient(curCode,dialog,txtCodeClient);
            }
        });

    }

    private void startLoadClient(String curCode, Dialog dialog, EditText txtCodeClient){
        if (curCode.length() < 4){
            txtCodeClient.setError("Длина кода должна быть не меньше 4 символов.");
        }else{
            dialog.dismiss();
            String codeClient = txtCodeClient.getText().toString();
            OrderActivity.LoadClient loadClient = new OrderActivity.LoadClient(this);
            loadClient.execute(codeClient);
        }
    }

    private class LoadClient extends AsyncTask<String, Void, Void> {
        private ProgressDialog mProgressDialog;

        private Context mContext;
        private String error = "";
        private Client curClient;


        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;


        public LoadClient(Context context) {
            this.mContext = context;


        }

        @Override
        protected Void doInBackground(String... codeClient) {

            error = "";
            // String stringUrl = Utils.mainServer +"/hs/GetClient/v1/get_client_by_code";
            // String stringUrl = Utils.mainServer +"/hs/GetCatalog/v1/get_user";
            String stringUrl = Utils.mainServer + getString(R.string.adress_get_client_by_code);
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
                connection.setRequestProperty("Accept", "application/json");


                String jsonInputString = "{\"code\":\"" +codeClient[0]+"\"}";

                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(jsonInputString);
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
                for (int i=0; i< jsonData.length();i++ ){
                    JSONObject jObject = jsonData.getJSONObject(i);
                    curClient =  new Client();
                    curClient.setName(jObject.getString("name"));
                    curClient.setId_1c(jObject.getString("code"));
                    curClient.setGuid_1c(jObject.getString("guid"));
                    curClient.setApi_key(jObject.getString("api_key"));
                    break;
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

            mProgressDialog = ProgressDialog.show(mContext, "Ищем контрагента", "Ищем контрагента ...");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dlg) {
                    OrderActivity.LoadClient.this.cancel(true);
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

            if (curClient != null){

                if (!orderHeader.getClient().equals(curClient)){
                    etClient.setText(curClient.getName());

                    orderHeader.setClient(curClient);
                    changeClient();
                }
            }

        }
    }

    private void changeClient() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Внимание!!!")
                .setMessage("Изменился клиент. Пересчитать цены?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadPriceForOrder();
            }
        });
        builder.setNegativeButton("Нет", null);
        builder.setCancelable(true);
        builder.create();
        builder.show();


    }

    private class LoadPrice extends AsyncTask<String, Void, Void> {
        private ProgressDialog mProgressDialog;

        private Context mContext;
        private String error = "";
        private ArrayList< HashMap<String,String>> data;



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
                connection.setRequestProperty("Customer_Code_1C", orderHeader.getClient().getId_1c());
                connection.setRequestProperty("APIkey", orderHeader.getClient().getApi_key());
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
                Cursor cursor_R = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_ROW_ORDER,DataBaseContract.R_ORDER_ROW.ORDER_ROW_COLUMNS,
                        DataBaseContract.R_ORDER_ROW.R_UUID+"=?",new String[]{uuid},null);


                ArrayList<ContentProviderOperation> list = new ArrayList<ContentProviderOperation>();

                double total = 0;


                for (int j=0; j<cursor_R.getCount();j++){
                    boolean isPresent = false;
                    cursor_R.moveToPosition(j);
                    String id_1c_c = cursor_R.getString(cursor_R.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_GOOD_GUID_1C));

                    for (int i=0; i< jsonData.length();i++ ){
                        JSONObject jObject = jsonData.getJSONObject(i);

                        String id_1c = jObject.getString("guid");
                        double price = jObject.getDouble("price");

                        if (id_1c_c.equals(id_1c)){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DataBaseContract.R_ORDER_ROW.R_PRICE, price);
                            contentValues.put(DataBaseContract.R_ORDER_ROW.R_TOTAL, price*cursor_R.getInt(cursor_R.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_QTY)));
                            list.add(ContentProviderOperation.
                                    newUpdate(DataBaseContract.BASE_CONTENT_URI_ROW_ORDER)
                                    .withSelection(DataBaseContract.R_ORDER_ROW.R_UUID+ "=? and "+DataBaseContract.R_ORDER_ROW.R_GOOD_GUID_1C+ "=? ", new String[]{orderHeader.getUuid(),id_1c})
                                    .withValues(contentValues)
                                    .build());
                            total = total + cursor_R.getInt(cursor_R.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_QTY))*price;
                            isPresent = true;
                            break;
                        }
                    }

                    if (!isPresent){
                        total = total + cursor_R.getInt(cursor_R.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_QTY))*cursor_R.getInt(cursor_R.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_PRICE));
                    }
                }

                ContentValues contentValues_H = new ContentValues();
                contentValues_H.put(DataBaseContract.R_ORDER_HEADER.RH_TOTAL,total);
                list.add(ContentProviderOperation.
                        newUpdate(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER)
                        .withSelection(DataBaseContract.R_ORDER_HEADER.RH_UUID+ "=? ", new String[]{orderHeader.getUuid()})
                        .withValues(contentValues_H)
                        .build());

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

                orderHeader.setTotal(total);

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

            mProgressDialog = ProgressDialog.show(mContext, "Обновление цен", "Обновляем цены ...");
            mProgressDialog.setCanceledOnTouchOutside(true); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dlg) {
                    OrderActivity.LoadPrice.this.cancel(true);
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

            fillHeaderOnForm();
            saveOrder();


        }
    }

    private void loadPriceForOrder() {

        if (orderHeader.getClient() == null){
            Utils.showAlert(this, "Внимание!","Не выбран контрагент.", "Ок");
            return;
        }
        if (orderHeader.getClient().getApi_key()==null || orderHeader.getClient().getApi_key().isEmpty()){
            Utils.showAlert(this,"Внимание!","Не выбран контрагент.", "Ок");
            return;
        }

        ContentResolver contentResolver = getContentResolver();
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

                OrderActivity.LoadPrice loadPrice = new OrderActivity.LoadPrice(this,data);
                loadPrice.execute();

            }
         }
    }



    private void saveOrderTo_1c() {
        setTypeOfShipmentToHeader();

        if (orderHeader.getClient() == null){
            Utils.showAlert(this, "Ошибка сохранения заказа.","Не выбран клиент",null);
            return;
        }

        if (orderHeader.getType_of_shipment_code() == null){
            Utils.showAlert(this, "Ошибка сохранения заказа.","Не выбран способ отправки",null);
            return;
        }

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_ROW_ORDER, DataBaseContract.R_ORDER_ROW.ORDER_ROW_COLUMNS,DataBaseContract.R_ORDER_ROW.R_UUID + "=?",new String[]{uuid},null);

        if (cursor.getCount() == 0){
            Utils.showAlert(this, "Ошибка сохранения заказа.","Заказ не содержит товаров.",null);
            return;
        }

        saveOrder();

        orderHeader.saveOrderTo_1c(this);


    }

    private void saveOrder() {

        setTypeOfShipmentToHeader();


        String errors = "";
        Boolean success = OrderHeader.SaveOrderToBase(orderHeader, getContentResolver(), errors);
        if (!success){
            Utils.showAlert(this, "Ошибка сохранения заказа.","Ошибка сохранения заказа.\n"+errors,null);

        }else{
            //finish();
        }

    }

    private void setTypeOfShipmentToHeader() {
        int item = sTypeOfShipment.getSelectedItemPosition();
        TypeOfShipment typeOfShipment = (TypeOfShipment) shipmentAdapter.getItem(item);


        orderHeader.setType_of_shipment_code(typeOfShipment.getCode_1c());
        orderHeader.setType_of_shipment(typeOfShipment.getName());
    }

    public void initLoader(int id_loader){
        LoaderManager.getInstance(this).initLoader(id_loader, null, this);
    }

    public void restartLoader(int id_loader){
        LoaderManager.getInstance(this).restartLoader(id_loader, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoader(ORDER_LOADER);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ORDER_LOADER:
                CursorLoader cursorLoader = new CursorLoader(
                        Utils.mainContext,
                        DataBaseContract.BASE_CONTENT_URI_ROW_ORDER,
                        DataBaseContract.R_ORDER_ROW.ORDER_ROW_COLUMNS,
                        selection,
                        selectionArgs,
                        null
                );
                return cursorLoader;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case ORDER_LOADER :
            case ORDER_LOADER_WITH_SELECTION:
                adapter.swapCursor(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch(loader.getId()) {
            case ORDER_LOADER:
                adapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home){
            finish();
        }
        return true;
    }
}