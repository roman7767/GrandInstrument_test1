package com.example.grandinstrument;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grandinstrument.adapters.GoodsRecyclerViewCursorAdapter;
import com.example.grandinstrument.adapters.OrderRecyclerViewAdapter;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

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

        return mainView;
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

        switch (id) {
            case ORDERS_LOADER:
                CursorLoader cursorLoader = new CursorLoader(
                        Utils.mainContext,
                        DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER,
                        DataBaseContract.R_ORDER_HEADER.ORDER_HEADER_COLUMNS,
                        selection,
                        selectionArgs,
                        DataBaseContract.R_ORDER_HEADER.RH_KEY_ID + " DESC"
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