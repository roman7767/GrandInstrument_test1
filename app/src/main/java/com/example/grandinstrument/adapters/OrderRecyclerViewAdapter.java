package com.example.grandinstrument.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.example.grandinstrument.OrderActivity;
import com.example.grandinstrument.R;
import com.example.grandinstrument.SelectOrderModel;
import com.example.grandinstrument.data_base_model.Client;
import com.example.grandinstrument.data_base_model.OrderHeader;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

import java.util.List;

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

        private LinearLayout ll_orders;
        private ImageView status_iv;
        private TextView tv_date;
        private TextView tv_number;
        private TextView tv_Number1c;
        private TextView tv_NameClient;
        private TextView tv_total;
        private TextView tv_totalQty;
        private TextView tv_type_of_shipment;
        private TextView tv_codeClient;
        private ImageButton ibCheck;


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
            tv_codeClient = view.findViewById(R.id.tv_codeClient);

            ibCheck = view.findViewById(R.id.ibCheck);
            ibCheck.setOnClickListener(this);
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
            tv_codeClient.setText(Utils.deletePreviousZero(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_CLIENT_ID_1C))));
            tv_total.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TOTAL))));
            tv_totalQty.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_QTY))));
            tv_type_of_shipment.setText(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_ORDER_HEADER.RH_TYPE_OF_SHIPMENT)));

            SelectOrderModel selectOrderModel = new SelectOrderModel(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_ORDER_HEADER.RH_UUID)));


            int curSelected = Utils.mSelectedList.indexOf(selectOrderModel);
            if (curSelected==-1){
                ibCheck.setImageResource(R.drawable.uncheck_box_50);
            }else{
                selectOrderModel = Utils.mSelectedList.get(curSelected);
                if (selectOrderModel.isSelected()){
                    ibCheck.setImageResource(R.drawable.checked_box_50);
                }else{
                    ibCheck.setImageResource(R.drawable.uncheck_box_50);
                }

            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == R.id.ibCheck){
                selectOrder(position);
                return;
            }


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

    private void selectOrder(int position) {
        Cursor cursor =  mCursorAdapter.getCursor();
        if (cursor==null){
            return;
        }

        cursor.moveToPosition(position);
        SelectOrderModel selectOrderModel = new SelectOrderModel(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_ORDER_HEADER.RH_UUID)));


        int curSelected = Utils.mSelectedList.indexOf(selectOrderModel);
        OrderHeader orderHeader = OrderHeader.loadOrderHeader(selectOrderModel.getUuid());

        if (curSelected==-1){

                selectOrderModel.setSelected(true);
                Utils.mSelectedList.add(selectOrderModel);


        }else{
            selectOrderModel = Utils.mSelectedList.get(curSelected);
            selectOrderModel.setSelected(!selectOrderModel.isSelected());
        }

        Utils.isCheckedOrder.setValue(true);
        mContext.getContentResolver().notifyChange(DataBaseContract.BASE_CONTENT_URI_HEAD_ORDER,null);

    }

}
