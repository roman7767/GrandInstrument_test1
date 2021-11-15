package com.example.grandinstrument.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.example.grandinstrument.OrderActivity;
import com.example.grandinstrument.R;
import com.example.grandinstrument.data_base_model.Client;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

public class OrderRecyclerViewAdapter extends RecyclerViewCursorAdapter<OrderRecyclerViewAdapter.OrderViewHolder> {

    private Context context;
    public OrderRecyclerViewAdapter(Context context) {
        super(context);

        this.context = context;

        setupCursorAdapter(null, 0, R.layout.order_item, false);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(mCursorAdapter.newView(context, mCursorAdapter.getCursor(), parent));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        // Move cursor to this position
        mCursorAdapter.getCursor().moveToPosition(position);

        // Set the ViewHolder
        setViewHolder(holder);

        // Bind this view
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }

    public class OrderViewHolder extends RecyclerViewCursorViewHolder implements View.OnClickListener{

        public LinearLayout ll_orders;
        public ImageView status_iv;
        public TextView tv_date;
        public TextView tv_number;
        public TextView tv_Number1c;
        public TextView tv_NameClient;
        public TextView tv_total;
        public TextView tv_totalQty;
        public TextView tv_type_of_shipment;

        public OrderViewHolder(View view) {
            super(view);

            ll_orders = view.findViewById(R.id.ll_orders);
            ll_orders.setOnClickListener(this);


            status_iv = view.findViewById(R.id.status_iv);
            tv_date = view.findViewById(R.id.tv_date);
            tv_number = view.findViewById(R.id.tv_number);
            tv_Number1c = view.findViewById(R.id.tv_Number1c);
            tv_NameClient = view.findViewById(R.id.tv_NameClient);
            tv_total = view.findViewById(R.id.tv_total);
            tv_totalQty = view.findViewById(R.id.tv_totalQty);
            tv_type_of_shipment = view.findViewById(R.id.tv_type_of_shipment);
        }

        @Override
        public void bindCursor(Cursor cursor) {

            Utils.setImageStatus(status_iv, cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_STATUS)));
            String date = "";
            long milliSec = cursor.getLong(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_DATE));

            if (milliSec != 0){
                date = Utils.getDate(milliSec, Utils.formatDate);
            }
            tv_date.setText(date);
            tv_number.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_KEY_ID))));
            tv_Number1c.setText(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_ORDER_NUMBER_1c)));
            tv_NameClient.setText(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_NAME)));
            tv_total.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TOTAL))));
            tv_totalQty.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_QTY))));
            tv_type_of_shipment.setText(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT)));
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Cursor cursor =  mCursorAdapter.getCursor();
            if (cursor==null){
                return;
            }

            cursor.moveToPosition(position);

            Intent intent = new Intent(Utils.mainContext, OrderActivity.class);

            intent.putExtra(DataBaseContract.R_ORDER_HEADER.RH_KEY_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_KEY_ID))));
            intent.putExtra(DataBaseContract.R_ORDER_HEADER.RH_UUID, cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_UUID)));


            Utils.mainContext.startActivity(intent);
        }
    }

}
