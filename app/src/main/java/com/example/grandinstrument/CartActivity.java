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

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.grandinstrument.adapters.CartRecyclerViewAdapter;
import com.example.grandinstrument.data_base_model.TypeOfShipment;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

public class CartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView cart_rv;
    public CartRecyclerViewAdapter adapter;
    private LinearLayoutManager manager;
    private TextView etClient;
    private TextView etSum;
    private Spinner sTypeOfShipment;
    private ArrayAdapter shipmentAdapter;




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