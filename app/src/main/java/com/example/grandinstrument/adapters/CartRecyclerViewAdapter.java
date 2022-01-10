package com.example.grandinstrument.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.grandinstrument.GoodsActivity;
import com.example.grandinstrument.R;
import com.example.grandinstrument.data_base_model.Goods;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

public class CartRecyclerViewAdapter extends RecyclerViewCursorAdapter<CartRecyclerViewAdapter.CartViewHolder> {

    private Context context;
    public CartRecyclerViewAdapter(Context context) {
        super(context);

        this.context = context;

        setupCursorAdapter(null, 0, R.layout.cart_item, false);
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
        public Button increment_btn;
        public Button decrease_btn;

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

            increment_btn = view.findViewById(R.id.increment_btn);
            increment_btn.setOnClickListener(this);

            decrease_btn = view.findViewById(R.id.decrease_btn);
            decrease_btn.setOnClickListener(this);

            goods_iv.setOnClickListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor) {


           Goods goods = Utils.getGoodFromDB(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_CART.RC_GOOD_GUID_1C)));


           if (goods != null){
               Utils.setImageGoods(context,goods.getUrl_of_image(),goods.getId_1c(),goods_iv);

               article_tv.setText(goods.getArticle());
               description_tv.setText(goods.getDescription());
               brand_tv.setText(goods.getBrand());
               categories_tv.setText(goods.getCategory());
               price_tv.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataBaseContract.R_CART.RC_PRICE))));
               total_tv.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataBaseContract.R_CART.RC_TOTAL))));
               quantity_tv.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_CART.RC_QTY))));
               if (goods.isGood_of_week()){
                   iv_good_of_week.setVisibility(View.VISIBLE);
               }else{
                   iv_good_of_week.setVisibility(View.GONE);
               }
           }
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Cursor cursor =  mCursorAdapter.getCursor();
            if (cursor==null){
                return;
            }

            String id_1c;
            cursor.moveToPosition(position);
            try {
                id_1c = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_CART.RC_GOOD_GUID_1C));
            }catch(Exception exception){
                Log.d("error GI",exception.getMessage());
                return;
            }

            if (v == goods_iv){

                TextView tv = v.findViewById(R.id.article_tv);
                Intent intent = new Intent(context, GoodsActivity.class);
                intent.putExtra("id_1c", id_1c);
                Utils.mainContext.startActivity(intent);

            }

            if (v == increment_btn || v == decrease_btn){

                Utils.setCartChange(v == decrease_btn?-1:1,id_1c, cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseContract.R_CART.RC_PRICE)), false);

                try{
                    TextView quantity_tv = ((ViewGroup) v.getParent()).findViewById(R.id.quantity_tv);
                    quantity_tv.setText(String.valueOf(Utils.getQtyInCartForGood(id_1c)));
                }catch (Exception e){}

            }


        }
    }

}
