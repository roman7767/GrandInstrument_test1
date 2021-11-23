package com.example.grandinstrument;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grandinstrument.adapters.CartRecyclerViewAdapter;
import com.example.grandinstrument.data_base_model.Client;
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

public class CartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView cart_rv;
    public CartRecyclerViewAdapter adapter;
    private LinearLayoutManager manager;
    private TextView etClient;
    private TextView etSum;
    private Spinner sTypeOfShipment;
    private ArrayAdapter shipmentAdapter;
    private ImageButton ibSelectClient;

    private static final int CARD_LOADER = 0;
    private static final int CARD_LOADER_WITH_SELECTION = 1;

    private static String selection = "";
    private static String[] selectionArgs = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.grand_logo_48_48);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Корзина");

        cart_rv = findViewById(R.id.order_rv);
        adapter = new CartRecyclerViewAdapter(this);
        manager = new LinearLayoutManager(this);
        cart_rv.setAdapter(adapter);
        cart_rv.setLayoutManager(manager);
        cart_rv.hasFixedSize();

        etClient = findViewById(R.id.etClient);
        if (Utils.curClient != null){
            etClient.setText(Utils.curClient.getName());
        }

        sTypeOfShipment = findViewById(R.id.sTypeOfShipment);
        shipmentAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,Utils.shipmentList);
        shipmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sTypeOfShipment.setAdapter(shipmentAdapter);


        etSum = findViewById(R.id.etSum);

        selection = DataBaseContract.R_CART.RC_QTY +" <> ?";
        selectionArgs = new String[]{"0"};

        if (Utils.mCurSumCart == null){
            Utils.mCurSumCart = new MutableLiveData<>();
        }
        Utils.mCurSumCart.setValue(Utils.getSumCart());

        Utils.mCurSumCart.observe(this,new Observer<Double>() {
            @Override
            public void onChanged(Double value) {
                etSum.setText(String.valueOf(value));
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

    private void startLoadClient(String curCode, Dialog dialog, EditText txtCodeClient){
        if (curCode.length() < 4){
            txtCodeClient.setError("Длина кода должна быть не меньше 4 символов.");
        }else{
            dialog.dismiss();
            //String codeClient = txtCodeClient.getText().toString();

            Client.startLoadClient(CartActivity.this,Utils.curClient,curCode);

        }
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

    public void changeClient() {
        etClient.setText(Utils.curClient.getName());

        loadPriceForCart();

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Внимание!!!")
//                .setMessage("Изменился клиент. Пересчитать цены?");
//        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                loadPriceForOrder();
//            }
//        });
//        builder.setNegativeButton("Нет", null);
//        builder.setCancelable(true);
//        builder.create();
//        builder.show();


    }

    private class LoadPrice extends AsyncTask<String, Void, Void> {
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

            mProgressDialog = ProgressDialog.show(mContext, "Обновление цен", "Обновляем цены ...");
            mProgressDialog.setCanceledOnTouchOutside(true); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
            mProgressDialog.setIcon(R.drawable.ic_baseline_refresh_24);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dlg) {
                    CartActivity.LoadPrice.this.cancel(true);
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

            Utils.mCurSumCart.setValue(total);
        }
    }

    private void loadPriceForCart() {

        ContentResolver contentResolver = getContentResolver();
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

                CartActivity.LoadPrice loadPrice = new CartActivity.LoadPrice(this,data);
                loadPrice.execute();

            }
        }
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
        initLoader(CARD_LOADER);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case CARD_LOADER:
                CursorLoader cursorLoader = new CursorLoader(
                        Utils.mainContext,
                        DataBaseContract.BASE_CONTENT_URI_CART,
                        DataBaseContract.R_CART.CART_COLUMNS,
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
            case CARD_LOADER :
            case CARD_LOADER_WITH_SELECTION:
                adapter.swapCursor(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch(loader.getId()) {
            case CARD_LOADER:
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

    public void btSaveOnClick(View view) {

        TypeOfShipment shipment = (TypeOfShipment) sTypeOfShipment.getSelectedItem();

        if (Utils.saveCart(shipment)){
            finish();
        }


    }

    public void btDeleteOnClick(View view) {
        Utils.clearTable(DataBaseContract.CART_TABLE_NAME);
        finish();
    }
}