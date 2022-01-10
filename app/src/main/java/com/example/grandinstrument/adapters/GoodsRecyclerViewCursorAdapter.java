package com.example.grandinstrument.adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
        private ImageView goods_iv;
        private TextView article_tv;
        private TextView description_tv;
        private TextView brand_tv;
        private TextView categories_tv;
        private TextView rrc_tv;
        private TextView price_tv;
        private TextView present_cb;
        private TextView quantity_tv;
        private TextView id_1c_tv;
        private ImageView iv_good_of_week;
        private Button increment_btn;
        private Button decrease_btn;
        private TextView tvInBox;
        private TextView tvInPackage;
        private RelativeLayout rlBox;
        private RelativeLayout rlPackage;


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
            quantity_tv.setClickable(true);
            quantity_tv.setOnClickListener(this);
            iv_good_of_week = view.findViewById(R.id.iv_good_of_week);
            increment_btn = view.findViewById(R.id.increment_btn);
            increment_btn.setOnClickListener(this);

            decrease_btn = view.findViewById(R.id.decrease_btn);
            decrease_btn.setOnClickListener(this);

            goods_iv.setOnClickListener(this);

            tvInBox = view.findViewById(R.id.tvInBox);
            tvInPackage = view.findViewById(R.id.tvInPackage);

            rlBox = view.findViewById(R.id.rlBox);
            rlPackage = view.findViewById(R.id.rlPackage);

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

            int inBox = cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_GOODS.RG_BOX));
            if (inBox==0){
                rlBox.setVisibility(View.GONE);
            }else{
                if (inBox > 99){
                    tvInBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                }else{
                    tvInBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                }
                tvInBox.setText(String.valueOf(inBox));
            }
            int inPackage = cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_GOODS.RG_PACKAGE));
            if (inPackage==0){
                rlPackage.setVisibility(View.GONE);
            }else{
                if (inPackage > 99){
                    tvInPackage.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                }else{
                    tvInPackage.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                }
                tvInPackage.setText(String.valueOf(inPackage));
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

            if (v == quantity_tv) {
                inputQty(v,id_1c,cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_RRC)), quantity_tv.getText().toString());
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
                    Utils.setCartChange(v == decrease_btn?-1:1,id_1c, cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_RRC)), false);
                }else{
                    Utils.setCartChange(v == decrease_btn?-1:1,id_1c, cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_PRICE)), false);
                }

               try{
                   TextView quantity_tv = ((ViewGroup) v.getParent()).findViewById(R.id.quantity_tv);
                   quantity_tv.setText(String.valueOf(Utils.getQtyInCartForGood(id_1c)));
               }catch (Exception e){}

            }

        }
    }

    private void inputQty(View v, String id_1c, double price, String curQty) {

        final EditText etQty = new EditText(Utils.mainContext);
        etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        etQty.setHint("Введите количество");
        etQty.setText(curQty=="0"?"":curQty);
        etQty.setFocusable(true);
        etQty.setFocusableInTouchMode(true);
        etQty.requestFocus();



        final android.app.AlertDialog dialog;
        dialog = new android.app.AlertDialog.Builder(Utils.mainContext)
                .setTitle("")
                .setMessage("")
                .setView(etQty)
                .setPositiveButton("ОК", null)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();

        etQty.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            int curQty = 0;
                            try {
                                curQty = Integer.parseInt(etQty.getText().toString());
                            } catch (Exception e){
                                curQty = 0;
                            }

                            if (curQty >0) {
                                ((TextView) v).setText(String.valueOf(curQty));
                                Utils.setCartChange(curQty,id_1c,price, true);
                            }
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
                int curQty = 0;
                try {
                    curQty = Integer.parseInt(etQty.getText().toString());
                } catch (Exception e){
                    curQty = 0;
                }
                ((TextView) v).setText(String.valueOf(curQty));
                Utils.setCartChange(curQty,id_1c,price, true);
                dialog.dismiss();
            }
        });



    }


}
