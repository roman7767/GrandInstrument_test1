package com.example.grandinstrument.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.example.grandinstrument.R;
import com.example.grandinstrument.data_base_model.Goods;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

public class OrderRowRecyclerViewAdapter extends RecyclerViewCursorAdapter<OrderRowRecyclerViewAdapter.CartViewHolder> {

    private Context context;
    public OrderRowRecyclerViewAdapter(Context context) {
        super(context);

        this.context = context;

        setupCursorAdapter(null, 0, R.layout.order_row_item, false);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(mCursorAdapter.newView(context, mCursorAdapter.getCursor(), parent));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Move cursor to this position
        mCursorAdapter.getCursor().moveToPosition(position);

        // Set the ViewHolder
        setViewHolder(holder);

        // Bind this view
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }

    public class CartViewHolder extends RecyclerViewCursorViewHolder implements View.OnClickListener{

        public ImageView goods_iv;
        public TextView article_tv;
        public TextView description_tv;
        public TextView brand_tv;
        public TextView categories_tv;
        public TextView total_tv;
        public TextView price_tv;
        public TextView present_cb;
        public TextView quantity_tv;
        public TextView id_1c_tv;
        public ImageView iv_good_of_week;

        public CartViewHolder(View view) {
            super(view);

            goods_iv = view.findViewById(R.id.goods_iv);
            article_tv = view.findViewById(R.id.article_tv);
            description_tv = view.findViewById(R.id.description_tv);
            brand_tv = view.findViewById(R.id.brand_tv);
            categories_tv = view.findViewById(R.id.categories_tv);
            total_tv = view.findViewById(R.id.total_tv);
            price_tv = view.findViewById(R.id.price_tv);
            present_cb = view.findViewById(R.id.present_cb);
            quantity_tv = view.findViewById(R.id.quantity_tv);
            iv_good_of_week = view.findViewById(R.id.iv_good_of_week);

            goods_iv.setOnClickListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor) {


           Goods goods = Utils.getGoodFromDB(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_ORDER_ROW.R_GOOD_GUID_1C)));


           if (goods != null){
               Utils.setImageGoods(context,goods.getUrl_of_image(),goods.getId_1c(),goods_iv);

               article_tv.setText(goods.getArticle());
               description_tv.setText(goods.getDescription());
               brand_tv.setText(goods.getBrand());
               categories_tv.setText(goods.getCategory());
               price_tv.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_PRICE))));
               total_tv.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_TOTAL))));
               quantity_tv.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_ORDER_ROW.R_QTY))));
               if (goods.isGood_of_week()){
                   iv_good_of_week.setVisibility(View.VISIBLE);
               }else{
                   iv_good_of_week.setVisibility(View.GONE);
               }
           }
        }

        @Override
        public void onClick(View v) {

//            int position = getAdapterPosition();
//            Cursor cursor =  mCursorAdapter.getCursor();
//            if (cursor==null){
//                return;
//            }
//
//            String id_1c;
//            cursor.moveToPosition(position);
//            try {
//                id_1c = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_CART.RC_GOOD_GUID_1C));
//            }catch(Exception exception){
//                Log.d("error GI",exception.getMessage());
//                return;
//            }

        }
    }

}
