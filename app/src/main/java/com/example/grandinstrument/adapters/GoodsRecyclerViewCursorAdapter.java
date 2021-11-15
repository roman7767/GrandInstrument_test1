package com.example.grandinstrument.adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
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
import androidx.exifinterface.media.ExifInterface;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.example.grandinstrument.GoodsActivity;
import com.example.grandinstrument.R;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

import org.json.JSONException;

public class GoodsRecyclerViewCursorAdapter extends RecyclerViewCursorAdapter<GoodsRecyclerViewCursorAdapter.GoodsViewHolder> {


    private Context context;
    private RequestQueue requestQueue;


    public GoodsRecyclerViewCursorAdapter(Context context) {
        super(context);
        this.context = context;

        setupCursorAdapter(null, 0, R.layout.goods_list_item, false);
        requestQueue = Volley.newRequestQueue(Utils.mainContext);
    }

    @Override
    public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodsViewHolder(mCursorAdapter.newView(Utils.mainContext, mCursorAdapter.getCursor(), parent));
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        // Move cursor to this position
        mCursorAdapter.getCursor().moveToPosition(position);

        // Set the ViewHolder
        setViewHolder(holder);

        // Bind this view
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }


    public class GoodsViewHolder extends RecyclerViewCursorViewHolder implements View.OnClickListener {
        public ImageView goods_iv;
        public TextView article_tv;
        public TextView description_tv;
        public TextView brand_tv;
        public TextView categories_tv;
        public TextView rrc_tv;
        public TextView price_tv;
        public TextView present_cb;
        public TextView quantity_tv;
        public TextView id_1c_tv;
        public ImageView iv_good_of_week;
        public Button increment_btn;
        public Button decrease_btn;


        public GoodsViewHolder(View view) {
            super(view);
            goods_iv = view.findViewById(R.id.goods_iv);
            article_tv = view.findViewById(R.id.article_tv);
            description_tv = view.findViewById(R.id.description_tv);
            brand_tv = view.findViewById(R.id.brand_tv);
            categories_tv = view.findViewById(R.id.categories_tv);
            rrc_tv = view.findViewById(R.id.rrc_tv);
            price_tv = view.findViewById(R.id.price_tv);
            present_cb = view.findViewById(R.id.present_cb);
            quantity_tv = view.findViewById(R.id.quantity_tv);
            id_1c_tv = view.findViewById(R.id.id_1c_tv);
            iv_good_of_week = view.findViewById(R.id.iv_good_of_week);
            increment_btn = view.findViewById(R.id.increment_btn);
            increment_btn.setOnClickListener(this);

            decrease_btn = view.findViewById(R.id.decrease_btn);
            decrease_btn.setOnClickListener(this);

            goods_iv.setOnClickListener(this);

        }



        @Override
        public void bindCursor(Cursor cursor) {

            Utils.setImageGoods(Utils.mainContext,cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_URL_IMAGE)),cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_ID_1C)),goods_iv);


            article_tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_ARTICLE)));
            description_tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_DESCRIPTION)));
            brand_tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_BRAND)));
            categories_tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_CATEGORY)));
            rrc_tv.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_RRC))));
            if (Utils.curClient == null || !Utils.showPrice){
                price_tv.setText("");
            }else{
                price_tv.setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_PRICE))));
            }



            boolean needGetData = Utils.setTextAvailable(context,present_cb,cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_QUANTITY)),true,cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_DATE_OF_RENOVATION)));

            quantity_tv.setText(String.valueOf(Utils.getQtyInCartForGood(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_ID_1C)))));
            if (cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_GOOD_OF_WEEK)) == 1){
                iv_good_of_week.setVisibility(View.VISIBLE);
            }else{
                iv_good_of_week.setVisibility(View.GONE);
            }


            if (needGetData){
                try {
                    Utils.load_Data(context, cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_ID_1C)),cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_ARTICLE)));
                } catch (JSONException e) {
                    e.printStackTrace();
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
                id_1c = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_ID_1C));
            }catch(Exception exception){
                Log.d("error GI",exception.getMessage());
                return;
            }

            if (v == goods_iv){

                TextView tv = v.findViewById(R.id.article_tv);



                Intent intent = new Intent(context, GoodsActivity.class);
                intent.putExtra("id_1c", id_1c);
                intent.putExtra("goods_iv", cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_URL_IMAGE)));

                Utils.mainContext.startActivity(intent);

            }

            if (v == increment_btn || v == decrease_btn){
                if (Utils.curClient == null){
//                    AlertDialog.Builder builder = new AlertDialog.Builder(Utils.mainContext);
//                    builder.setTitle("Внимание!!!")
//                            .setMessage("Для добавления товаров необходимо выбрать клиента.");
//                    builder.setPositiveButton("Ок",null);
//                    builder.setCancelable(true);
//                    builder.create();
//                    builder.show();
//
//                    return;
                    Utils.setCartChange(v == decrease_btn?-1:1,id_1c, cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_RRC)));
                }else{
                    Utils.setCartChange(v == decrease_btn?-1:1,id_1c, cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_PRICE)));
                }

               try{
                   TextView quantity_tv = ((ViewGroup) v.getParent()).findViewById(R.id.quantity_tv);
                   quantity_tv.setText(String.valueOf(Utils.getQtyInCartForGood(id_1c)));
               }catch (Exception e){}

            }

        }
    }



}
