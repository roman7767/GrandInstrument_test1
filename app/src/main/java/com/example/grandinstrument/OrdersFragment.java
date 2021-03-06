package com.example.grandinstrument;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grandinstrument.adapters.GoodsRecyclerViewCursorAdapter;
import com.example.grandinstrument.adapters.OrderRecyclerViewAdapter;
import com.example.grandinstrument.data_base_model.OrderHeader;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    public OrderRecyclerViewAdapter adapter;
    private LinearLayoutManager manager;
    private View mainView;
    private Context mContext;

    private static final int ORDERS_LOADER = 11;
    private static final int ORDERS_LOADER_WITH_SELECTION = 22;

    private static String selection = null;
    private static String[] selectionArgs = null;

    private LinearLayout llChosenOrders;
    private TextView tvChosenOrders;
    private Button btSaveTo1cChosenOrders;
    private Button btUnSelect;
    private int rbByOrderNumber;
    private int rbByClientName;
    private RadioGroup rgSorting;
    private ImageButton ibDirectionOfSorting;
    private ImageButton ibDeleteOrder;

    public OrdersFragment() {
        mContext = Utils.mainContext;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView = mainView.findViewById(R.id.orders_rv);
        adapter = new OrderRecyclerViewAdapter(mContext);
        manager = new LinearLayoutManager(mContext);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();

        llChosenOrders = mainView.findViewById(R.id.llChosenOrders);
        tvChosenOrders = mainView.findViewById(R.id.tvChosenOrders);
        btSaveTo1cChosenOrders = mainView.findViewById(R.id.btSaveTo1cChosenOrders);
        btSaveTo1cChosenOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrdersTo1c();
            }
        });
        btUnSelect = mainView.findViewById(R.id.btUnSelect);
        btUnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelected();
            }
        });

        Utils.isCheckedOrder.observe((LifecycleOwner) Utils.mainContext,new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                showLayoutSelectedOrder();
            }
        });


        ibDirectionOfSorting = mainView.findViewById(R.id.ibDirectionOfSorting);
        ibDirectionOfSorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.directionSortingOrders = !Utils.directionSortingOrders;
                setSortingOrders();
            }
        });
        rgSorting = mainView.findViewById(R.id.rgSorting);
        rgSorting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==rbByOrderNumber){
                    Utils.typeOfSortingOrders=1;
                }else{
                    Utils.typeOfSortingOrders=2;
                }
                setSortingOrders();
            }
        });
        rbByOrderNumber = R.id.rbByOrderNumber;
        rbByClientName = R.id.rbByClientName;
        setSortingOrders();

        ibDeleteOrder = mainView.findViewById(R.id.ibDeleteOrder);
        ibDeleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedOrder();
            }
        });
        return mainView;
    }

    private void deleteSelectedOrder() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setTitle("???????????????? ??????????????..");
        builder.setMessage("?????????????? "+SelectOrderModel.getQtySelected()+" ??????????(????)");
        builder.setCancelable(true);
        builder.setNegativeButton("??????", null);
        builder.setPositiveButton("????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSelectedOrderNext();
            }
        });

        builder.show();


        if (Utils.mSelectedList == null){
            return;
        }

    }

    private void deleteSelectedOrderNext() {
        ArrayList<String> orderForDelete = new ArrayList<>();

        for (int i=0; i<Utils.mSelectedList.size(); i++){


            SelectOrderModel selectOrderModel = Utils.mSelectedList.get(i);
            String uuid = selectOrderModel.getUuid();

            orderForDelete.add(uuid);
        }

        for (int i = 0; i<orderForDelete.size();i++){
            OrderHeader orderHeader = OrderHeader.loadOrderHeader(orderForDelete.get(i));
            orderHeader.deleteOrder();
        }
        clearSelected();
    }

    private void setSortingOrders() {
        if (Utils.typeOfSortingOrders ==0){
            Utils.typeOfSortingOrders = 1;//???? ???????????? ????????????
        }

        if (Utils.typeOfSortingOrders == 1){
            rgSorting.check(rbByOrderNumber);
        }else{
            rgSorting.check(rbByClientName);
        }
        if (Utils.directionSortingOrders){
            ibDirectionOfSorting.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
        }else{
            ibDirectionOfSorting.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
        }

        LoaderManager.getInstance(this).restartLoader(ORDERS_LOADER, null, this);

    }

    private void clearSelected() {
        Utils.mSelectedList.clear();
        getContext().getContentResolver().notifyChange(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER,null);
        Utils.isCheckedOrder.setValue(false);
    }

    private void saveOrdersTo1c() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setTitle("???????????????? ??????????????..");
        builder.setMessage("???????????????? "+SelectOrderModel.getQtySelected()+" ??????????(????)");
        builder.setCancelable(true);
        builder.setNegativeButton("??????", null);
        builder.setPositiveButton("????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveOrdersTo1cNext();
            }
        });

        builder.show();

    }

    private void saveOrdersTo1cNext() {

        ContentResolver contentResolver = mContext.getContentResolver();

        int qtyUploaded = 0;
        Utils.mMessage = "";

        for (int i=0; i<Utils.mSelectedList.size(); i++){
            SelectOrderModel selectOrderModel = Utils.mSelectedList.get(i);

            if (selectOrderModel.isSelected()){
                OrderHeader orderHeader = OrderHeader.loadOrderHeader(selectOrderModel.getUuid());
                if (orderHeader.verifyOrder(Utils.mainContext)){
                    String errors = "";
                    orderHeader.saveOrderTo_1c(mContext,i==Utils.mSelectedList.size()-1?false:true);
                    if (!errors.equals("")){
                        Toast.makeText(mContext,errors,Toast.LENGTH_LONG).show();
                    }else{
                        qtyUploaded++;
                    }
                }
            }
        }
        clearSelected();

    }

    private void showLayoutSelectedOrder() {

        if (Utils.mSelectedList.size() > 0){
            int selQty = SelectOrderModel.getQtySelected();
            if (selQty==0){
                llChosenOrders.setVisibility(View.GONE);
            }else{
                llChosenOrders.setVisibility(View.VISIBLE);
                tvChosenOrders.setText("?????????????? "+selQty+" ??????????(????): ");
            }
        }else{
            llChosenOrders.setVisibility(View.GONE);
        }

    }

    public void initLoader(int id_loader){
        LoaderManager.getInstance(this).initLoader(id_loader, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoader(ORDERS_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sorting;
        if (Utils.typeOfSortingOrders==1){
            sorting = DataBaseContract.R_ORDER_HEADER.RH_KEY_ID;
        }else{
            sorting = DataBaseContract.R_ORDER_HEADER.RH_CLIENT_NAME;
        }
        if (!Utils.directionSortingOrders){
            sorting = sorting + " DESC";
        }


        switch (id) {
            case ORDERS_LOADER:
                CursorLoader cursorLoader = new CursorLoader(
                        Utils.mainContext,
                        DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER,
                        DataBaseContract.R_ORDER_HEADER.ORDER_HEADER_COLUMNS,
                        selection,
                        selectionArgs,
                        sorting
                );
                return cursorLoader;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case ORDERS_LOADER :
            case ORDERS_LOADER_WITH_SELECTION:
                adapter.swapCursor(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch(loader.getId()) {
            case ORDERS_LOADER:
                adapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

}