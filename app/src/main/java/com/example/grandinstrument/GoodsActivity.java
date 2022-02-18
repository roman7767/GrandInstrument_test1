package com.example.grandinstrument;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

public class GoodsActivity extends AppCompatActivity {

    private ImageView goods_iv;
    private TextView article_tv;
    private TextView brand_tv;
    private TextView description_tv;
    private TextView rrc_tv;
    private TextView opt_tv;
    private TextView price_tv;
    private TextView present_cb;
    private String id_1c;
    private Cursor cursor;
    private ContentResolver contentResolver;
    private Uri uri;
    private ImageView iv_good_of_week;
    private TextView tvInBox;
    private TextView tvInPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.grand_logo_48_48);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Товар");


        goods_iv = findViewById(R.id.goods_iv);
        article_tv = findViewById(R.id.article_tv);
        description_tv = findViewById(R.id.description_tv);
        brand_tv = findViewById(R.id.brand_tv);
       // categories_tv = findViewById(R.id.categories_tv);
        rrc_tv = findViewById(R.id.rrc_tv);
        opt_tv = findViewById(R.id.opt_tv);
        price_tv = findViewById(R.id.price_tv);
        present_cb = findViewById(R.id.present_cb);
        iv_good_of_week = findViewById(R.id.iv_good_of_week);
        uri = DataBaseContract.BASE_CONTENT_URI_GOODS;
        tvInBox = findViewById(R.id.tvInBox);
        tvInPackage = findViewById(R.id.tvInPackage);

        Intent intent = getIntent();
        if (intent != null){

            id_1c = intent.getStringExtra("id_1c");
            if (! id_1c.trim().isEmpty()){

                contentResolver = getContentResolver();

                cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_GOODS,
                        DataBaseContract.R_GOODS.GOODS_COLUMNS_FOR_LIST, DataBaseContract.R_GOODS.RG_ID_1C+"=?",new String[]{id_1c},null);


                if (cursor!=null){
                    cursor.moveToFirst();

                    Utils.setImageGoods(this,cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_URL_IMAGE)),cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_ID_1C)),goods_iv);

                    article_tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_ARTICLE)));
                    description_tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_DESCRIPTION)));
                    brand_tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_BRAND)));
                    //categories_tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_CATEGORY)));
                    if (!Utils.showPriceRRC){
                        rrc_tv.setText("");
                    }else{
                        rrc_tv.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_RRC))));
                    }

                    if (Utils.curClient == null || !Utils.showPrice){
                        price_tv.setText("");
                    }else{
                        price_tv.setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_PRICE))));
                    }
                    Utils.setTextAvailable( this,present_cb,cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_QUANTITY)),false, cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_DATE_OF_RENOVATION)));

                    if (cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.R_GOODS.RG_GOOD_OF_WEEK)) == 1){
                        iv_good_of_week.setVisibility(View.VISIBLE);
                    }else{
                        iv_good_of_week.setVisibility(View.GONE);
                    }
                    tvInBox.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_GOODS.RG_BOX))));
                    tvInPackage.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_GOODS.RG_PACKAGE))));
                }
            }


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
}