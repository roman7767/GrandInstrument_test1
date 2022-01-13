package com.example.grandinstrument;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.app.DatePickerDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private EditText etDateOfDelivery;
    private DatePickerDialog datePickerDialog;

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
        if (Utils.curOrder !=null){
            setTitle("Корзина для "+Utils.curOrder.toString());
        }

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

        TypeOfShipment typeOfShipment = (TypeOfShipment) Utils.getValueFromCart(DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT);
        if (typeOfShipment != null){
            sTypeOfShipment.setSelection(TypeOfShipment.getIndexByCode(typeOfShipment.getCode_1c()));
        }
        sTypeOfShipment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TypeOfShipment typeOfShipment = Utils.shipmentList.get(position);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT,typeOfShipment.getName());
                contentValues.put(DataBaseContract.R_CART.RC_TYPE_OF_SHIPMENT_CODE,typeOfShipment.getCode_1c());

                Utils.setValueInCart(contentValues);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

                DecimalFormat precision = new DecimalFormat("0.00");
                etSum.setText(precision.format(value));
            }
        });

        ibSelectClient = findViewById(R.id.ibSelectClient);
        ibSelectClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectClient();
            }
        });

        etDateOfDelivery = (EditText) findViewById(R.id.etDateOfDelivery);

        etDateOfDelivery.setText(String.valueOf(Utils.getValueFromCart(DataBaseContract.R_CART.RC_DELIVERY_DATE)));
        // perform click event on edit text
        etDateOfDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(CartActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                String textDate = Utils.fix_date(String.valueOf(dayOfMonth)) + "." + Utils.fix_date(String.valueOf((monthOfYear + 1)))+ "." + year;
                                etDateOfDelivery.setText(textDate);

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DataBaseContract.R_CART.RC_DELIVERY_DATE,textDate);
                                Utils.setValueInCart(contentValues);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
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
        //txtCodeClient.setInputType(InputType.TYPE_CLASS_TEXT);
        txtCodeClient.setSingleLine();
        txtCodeClient.requestFocus();


        final android.app.AlertDialog dialog;
        dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Код/наименование клиента")
                .setMessage("Введите код/наименование клиента")
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
                            dialog.dismiss();
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
        

        Utils.loadPriceForCart(this);

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

    public void btSaveOnClick(View view) throws OperationApplicationException {

        TypeOfShipment shipment = (TypeOfShipment) sTypeOfShipment.getSelectedItem();

        if (Utils.saveCart(shipment)){
            finish();
        }


    }

    public void btDeleteOnClick(View view) {
        Utils.clearTable(DataBaseContract.CART_TABLE_NAME);
        Utils.curOrder = null;
        finish();
    }
    public void setClient(Client client){
        Utils.curClient = client;
        changeClient();
    }

    public void choiceClient(ArrayList<Client> arrayChoiceOfClient) {
        Utils.selectingClient(arrayChoiceOfClient, this);
    }
}