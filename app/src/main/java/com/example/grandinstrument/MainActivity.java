package com.example.grandinstrument;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.grandinstrument.data_base_model.Client;
import com.example.grandinstrument.data_base_model.TypeOfShipment;
import com.example.grandinstrument.data_base_model.User;
import com.example.grandinstrument.ui.login.LoginActivity;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.LeftSideBarItem;
import com.example.grandinstrument.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity  {

    private ViewPager viewPager;
    private ImageView ivCollapseBar;
    private TextView tv_CollapseBar;
    private TextView tvSettingsBar;
    private int curMenuXml=0;
    private MenuItem itemMenuShowPrice;
    private ConstraintLayout leftSideBar;
    private TextView tvCartQty;
    private ImageView iv_ClientBar;
    private TextView tv_NameClient;
    private LinearLayout ll_show_client;
    String titleActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.grand_logo_48_48);
        Utils.mainContext = this;


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        Utils.allow_load_from_url = prefs.getBoolean("allow_load_from_url",false);

        Utils.mainServer = prefs.getString("server","");
        if (Utils.GIUD_DEVICE == null) {
            Utils.GIUD_DEVICE = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        }

        SharedPreferences.OnSharedPreferenceChangeListener prefListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences curPrefs,
                                                          String key) {
                        if (key.equals("server")) {
                            Utils.mainServer = curPrefs.getString("server","");
                        }
                    }
                };

//        //*********************
//        Utils.curUser = User.getUserByEmail("e@e.com", this);
//        //*********************

        if (Utils.mStatuses == null){
            Utils.mStatuses = getResources().getStringArray( R.array.statuses_of_order);
        }
        if (Utils.loginIntent==null){
            Utils.loginIntent = new Intent(Utils.mainContext,LoginActivity.class);
        }
        if (Utils.mCurCartQty == null){
            Utils.mCurCartQty = new MutableLiveData<>();
            Utils.mCurCartQty.setValue(Utils.getQtyInCart());
        };

        if (Utils.shipmentList == null){

            Utils.Fill_shipment_list();

        };

        Utils.mCurCartQty.observe(this,new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (tvCartQty != null){
                    int curValue = Utils.mCurCartQty.getValue();
                    if (curValue <= 0){
                        tvCartQty.setVisibility(View.GONE);
                    }else{
                        tvCartQty.setVisibility(View.VISIBLE);
                        tvCartQty.setText(String.valueOf(curValue));
                    }

                }
            }

        });


        MyAdapter adapterPage = new MyAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapterPage);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i=0; i< Utils.leftSideBarItems.size();i++){
                    LeftSideBarItem leftSideBarItem = Utils.leftSideBarItems.get(i);
                    if (i==position){
                        leftSideBarItem.getLinearLayout().setBackgroundColor(getResources().getColor(R.color.dark_grand_color,getTheme()));
                        setMenu(leftSideBarItem.getText());
                    }else{
                        leftSideBarItem.getLinearLayout().setBackgroundColor(0);
                    }

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Utils.leftSideBarItems = new ArrayList<>();
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvHomeBar),findViewById(R.id.ivHomeBar),0,"Старт",R.drawable.ic_baseline_home_24 ,findViewById(R.id.ll_home_bar), new Home()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvGoodsBar),findViewById(R.id.ivGoodsBar),1,"Товары",R.drawable.ic_baseline_goods_24 ,findViewById(R.id.ll_goods_bar),new GoodsListFragment()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvOrdersBar),findViewById(R.id.iv_OrdersBar),2,"Заказы",R.drawable.ic_baseline_goods_24 ,findViewById(R.id.ll_orders),new OrdersFragment()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvClientsBar),findViewById(R.id.iv_ClientsBar),3,"Клиенты",R.drawable.ic_baseline_client_24 ,findViewById(R.id.ll_clients),new ClientFragment()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvWarehouseBar),findViewById(R.id.iv_WarehouseBar),4,"Склады",R.drawable.ic_baseline_storage_24 ,findViewById(R.id.ll_WarehouseBar),new WarehouseFragment()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvPaymentsBar),findViewById(R.id.iv_PaymentsBar),5,"Платежи",R.drawable.ic_baseline_payments_24 ,findViewById(R.id.ll_PaymentsBar),new PaymentsFragment()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvTasksBar),findViewById(R.id.iv_TasksBar),6,"Задачи",R.drawable.ic_baseline_task_24 ,findViewById(R.id.ll_TasksBar),new TasksFragment()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvProfileBar),findViewById(R.id.iv_ProfileBar),7,"Профиль",R.drawable.ic_baseline_profile_24 ,findViewById(R.id.ll_ProfileBar),new ProfileFragment()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvLoadBar),findViewById(R.id.iv_LoadBar),8,"Загрузки",R.drawable.ic_baseline_goods_24 ,findViewById(R.id.ll_Load),new LoadFragment()));
        Utils.leftSideBarItems.add(new LeftSideBarItem(findViewById(R.id.tvSaleBar),findViewById(R.id.iv_SaleBar),9,"Акции",R.drawable.ic_baseline_sale_24 ,findViewById(R.id.ll_SaleBar),new SaleFragment()));

        ivCollapseBar = findViewById(R.id.ivCollapseBar);
        tv_CollapseBar = findViewById(R.id.tv_CollapseBar);
        tvSettingsBar = findViewById(R.id.tvSettingsBar);
        leftSideBar = findViewById(R.id.leftSideBar);


    }

    @Override
    protected void onResume() {
        super.onResume();


        if (Utils.curUser == null){
            startActivity(Utils.loginIntent );
        }

        if (Utils.mCurCartQty != null && Utils.curClient == null){
            Utils.setupClientFromCart();
        }

        setTitleActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void btHomeBar(View view) {
        viewPager.setCurrentItem(0);
    }

    public void btGoodsListBar(View view) {
        viewPager.setCurrentItem(1);

    }

    public void btOrderBar(View view) {
        viewPager.setCurrentItem(2);
    }

    public void btClientsBar(View view) {
        viewPager.setCurrentItem(3);
    }

    public void btWarehouseBar(View view) {
        viewPager.setCurrentItem(4);
    }

    public void btPaymentsBar(View view) {
        viewPager.setCurrentItem(5);
    }

    public void btTasksBar(View view) {
        viewPager.setCurrentItem(6);
    }

    public void btProfileBar(View view) {
        viewPager.setCurrentItem(7);
    }

    public void btLoadBar(View view) {
        viewPager.setCurrentItem(8);
    }

    public void btSaleBar(View view) {
        viewPager.setCurrentItem(9);
    }


    public void bt1OnClickCollapse(View view) {
        for (int i =0; i<Utils.leftSideBarItems.size();i++){
            View curView = Utils.leftSideBarItems.get(i).getTv_Text();
            if (curView.getVisibility() == View.VISIBLE){
               curView.setVisibility(View.GONE);
            }else{
                curView.setVisibility(View.VISIBLE);
            }
        }

        if (tv_CollapseBar.getVisibility() == View.VISIBLE){
            tvSettingsBar.setVisibility(View.GONE);

            tv_CollapseBar.setVisibility(View.GONE);
            ivCollapseBar.setImageResource(R.drawable.ic_baseline_arrow_forward_24);
        }else{
            tvSettingsBar.setVisibility(View.VISIBLE);

            tv_CollapseBar.setVisibility(View.VISIBLE);
            ivCollapseBar.setImageResource(R.drawable.ic_baseline_arrow_back_24);
        }
    }

    public void setTitleActivity(){
        titleActivity = "Grandinstrument";
        if (Utils.curClient == null || Utils.curClient.getName().isEmpty()){
            titleActivity = "Grandinstrument";
        }else{
            //titleActivity = "Grandinstrument" + "  "+"Клиент: "+Utils.curClient.getName();

        }
        setVisibleClientInToolBar();
        setTitle(titleActivity);

    }

    public void btSettingsBar(View view) {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }



    private static class MyAdapter extends FragmentPagerAdapter {


        MyAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 10;
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            LeftSideBarItem leftSideBarItem;
            try {
                leftSideBarItem = Utils.leftSideBarItems.get(position);
            }
            catch(Exception e){
                return   new Home();
            }

            fragment = leftSideBarItem.getFragment();

            return fragment;
        }
    }

    public void setMenu(String page) {
        switch (page){
            case "Товары":
                setMenuGoods();
                return;
            default:
                setDefaultMenu();
        }

    }

    private void setMenuGoods() {
        curMenuXml = R.menu.menu_goods_list;
        invalidateOptionsMenu();

    }

    private void setDefaultMenu() {

        curMenuXml = 0;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        itemMenuShowPrice = null;
        if (curMenuXml == 0){
            menuInflater.inflate(R.menu.menu,menu);
        }else if (curMenuXml == R.menu.menu_goods_list){
            menuInflater.inflate(curMenuXml,menu);
            itemMenuShowPrice = menu.findItem(R.id.btShowPrice);
            setShowPriceTitle();
        }

        MenuItem item = menu.findItem(R.id.iCart);
        MenuItemCompat.setActionView(item, R.layout.cart_menu_item);
        RelativeLayout rlCart = (RelativeLayout)   MenuItemCompat.getActionView(item);
        tvCartQty = (TextView) rlCart.findViewById(R.id.tv_CartQty);
        tvCartQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Utils.mainContext,CartActivity.class);
                startActivity(intent);
            }
        });

        MenuItem itemClient= menu.findItem(R.id.iClient);
        MenuItemCompat.setActionView(itemClient, R.layout.show_client_menu_item);
        ll_show_client = (LinearLayout)   MenuItemCompat.getActionView(itemClient);
        tv_NameClient = (TextView) ll_show_client.findViewById(R.id.tv_NameClient);
        iv_ClientBar = (ImageView) ll_show_client.findViewById(R.id.iv_ClientBar);
        iv_ClientBar.setImageResource(0);
        tv_NameClient.setText("");

        setVisibleClientInToolBar();


        if (Utils.mCurCartQty != null){
            Utils.mCurCartQty.setValue(Utils.getQtyInCart());
        }

        if (Utils.mCurCartQty.getValue()==0){
            tvCartQty.setVisibility(View.GONE);
        }
        return true;
    }

    private void setVisibleClientInToolBar() {
        if (ll_show_client != null) {
            if (Utils.curClient == null||Utils.curClient.getApi_key()==null||Utils.curClient.getApi_key().isEmpty()) {
                iv_ClientBar.setImageResource(0);
                tv_NameClient.setText("");

            } else {
                iv_ClientBar.setImageResource(R.drawable.ic_baseline_select_client_24);
                iv_ClientBar.setColorFilter(getResources().getColor(R.color.purple_500));
                tv_NameClient.setText(Utils.curClient.getName());

            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btSettings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.btlogout){
            Utils.curUser = null;
            startActivity(Utils.loginIntent);
        }

        if (id == R.id.btChoseClient){

//            if (! Utils.checkCartIsEmpty("Перед выбором клиента необходимо сохранить или удалить товары из корзины.")){
//                return false;
//            }

            final EditText txtCodeClient = new EditText(this);
            txtCodeClient.setHint("");
            //txtCodeClient.setInputType(InputType.TYPE_CLASS_NUMBER);


            final AlertDialog dialog;
            dialog = new AlertDialog.Builder(this)
                    .setTitle("Код/наименование клиента")
                    .setMessage("Введите код/наименование клиента")
                    .setView(txtCodeClient)
                    .setPositiveButton("ОК", null)
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .create();


            txtCodeClient.setOnKeyListener(new View.OnKeyListener(){
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_ENTER:
                                String curCode = txtCodeClient.getText().toString().trim().replaceAll("\n","");
                                startLoadClient(curCode,dialog,txtCodeClient);
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
                    String curCode = txtCodeClient.getText().toString().trim().replaceAll("\n","");
                    startLoadClient(curCode,dialog,txtCodeClient);
                }
            });

        }

        if (id==R.id.btClearClient){

//            if (! Utils.checkCartIsEmpty("Перед выбором клиента необходимо сохранить или удалить товары из корзины.")){
//                return false;
//            }

            Utils.curClient = null;
            setTitleActivity();

            Utils.clearPriceColumns(this);
            Utils.changeClientOnCart();
        }

        if (id==android.R.id.home){
            finish();
        }

        if (id == R.id.btShowPrice){
            Utils.setShowPrice(this,!Utils.showPrice);
            getContentResolver().notifyChange(DataBaseContract.BASE_CONTENT_URI_GOODS, null);
            setShowPriceTitle();
        }

        return true;

    }

    private void setShowPriceTitle(){

        if (itemMenuShowPrice != null){
            if (Utils.showPrice){
                itemMenuShowPrice.setTitle("Скрыть цену");
            }else{
                itemMenuShowPrice.setTitle("Показать цену");
            }
        }
    }

    public void setClient(Client client){
        Utils.curClient = client;
        Utils.changeClientOnCart();
        Utils.loadPriceForCart(MainActivity.this);
        setTitleActivity();

        getContentResolver().notifyChange(DataBaseContract.BASE_CONTENT_URI_GOODS, null);
    }

    public void choiceClient(ArrayList<Client> arrayChoiceOfClient) {
        Utils.selectingClient(arrayChoiceOfClient, MainActivity.this);
    }

    private void startLoadClient(String curCode, Dialog dialog, EditText txtCodeClient){
        if (curCode.length() < 4){
            txtCodeClient.setError("Длина кода/наименоания  должна быть не меньше 4 символов.");
        }else{
            dialog.dismiss();
            //String codeClient = txtCodeClient.getText().toString();

            Client.startLoadClient(MainActivity.this,null,curCode);

        }
    }

}

