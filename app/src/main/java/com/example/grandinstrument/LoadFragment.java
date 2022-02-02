package com.example.grandinstrument;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.grandinstrument.data_base_adapter.DataBaseHandler;
import com.example.grandinstrument.data_base_model.Goods;
import com.example.grandinstrument.data_base_model.TypeOfShipment;
import com.example.grandinstrument.ui.login.LoginActivity;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadFragment extends Fragment {


    private RequestQueue requestQueue;
    private boolean nextGoods = false;

    private int number_of_packet;
    private DataBaseHandler dbOH = new DataBaseHandler(Utils.mainContext);

    private boolean eraseGoods = true;
    private boolean finishLoading = true;
    private ProgressBar progressBar;
    private Button btLoadsGoods;
    private Button btLoadPicture;

    private Button btLoadBrands;
    private Button btUpdate;


    private ImageView tools_iv;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoadFragment newInstance(String param1, String param2) {
        LoadFragment fragment = new LoadFragment();
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


    public void setEnableButtonLoadGoods() {
        if (!finishLoading) {
            btLoadsGoods.setAlpha(0.5f);
            btLoadsGoods.setEnabled(false);
        } else {
            btLoadsGoods.setAlpha(1f);
            btLoadsGoods.setEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_load, container, false);


        tools_iv = mainView.findViewById(R.id.tools_iv);
        tools_iv.setVisibility(View.INVISIBLE);

        requestQueue = Volley.newRequestQueue(Utils.mainContext);

        if (progressBar == null) {
            progressBar = mainView.findViewById(R.id.progressBar);
        }

        btLoadsGoods = mainView.findViewById(R.id.btLoadsGoods);
        btLoadsGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btLoadGoods(v);
            }
        });

        btLoadPicture = mainView.findViewById(R.id.btLoadPicture);
        btLoadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btLoadPicturesOnclick(v);
            }
        });
        setEnableButtonLoadGoods();


        Button btClearOrders = mainView.findViewById(R.id.btClearOrders);
        btClearOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.clearTable(DataBaseContract.ORDER_HEAD_TABLE_NAME);
                Utils.clearTable(DataBaseContract.ORDER_ROW_TABLE_NAME);
            }
        });

        Button btLoad_type_of_shipment = mainView.findViewById(R.id.btLoad_type_of_shipment);
        btLoad_type_of_shipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTypeOfShipment();
            }
        });

        btLoadBrands = mainView.findViewById(R.id.btLoadBrands);
        btLoadBrands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.loadBrandsTable();
            }
        });

        Button btUpdate = mainView.findViewById(R.id.btUpdate);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Utils.mainContext);
                builder.setTitle("Обновление.");
                builder.setMessage("Обновить приложение?");
                builder.setNegativeButton("Нет", null);
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateApp();
                    }
                });
                builder.show();

            }
        });

        return mainView;
    }

    private void updateApp() {

        UpdateApp updateApp = new UpdateApp();
        updateApp.execute();


    }

    private class UpdateApp extends AsyncTask{
        private ProgressDialog mProgressDialog;
        private Context mContext;
        private String mErrors;

        public UpdateApp() {

            mContext = Utils.mainContext;

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(Utils.refUpdateAPK);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                output = new FileOutputStream(Utils.pathToFileApp+"/GI");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
//                    total += count;
//                    // publishing the progress....
//                    if (fileLength > 0) // only if total length is known
//                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                mErrors = e.toString();
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(mContext, "Загрузка файла...", "Ожидайте...");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
            mProgressDialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (mErrors !=null && !mErrors.equals("")){
                Utils.showAlert(mContext,"Ошибка!!!", mErrors,"");
            }else{
                updateAppFromFile();
            }


        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }
    }

    private void updateAppFromFile() {

        File dir = new File(Utils.pathToFileApp);
        if(dir.exists()){
            File from = new File(dir,"GI");
            File to = new File(dir,"GI.apk");
            if(from.exists())
                from.renameTo(to);
        }

//        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//        intent.setDataAndType(Uri.fromFile(new File(dir,"GI.apk")), "application/vnd.android.package");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        Utils.mainContext.startActivity(intent);

        Uri uri = FileProvider.getUriForFile(Utils.mainContext, BuildConfig.APPLICATION_ID + ".provider",new File(Utils.pathToFileApp+"/GI.apk"));
        Intent promptInstall = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive");
        promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Utils.mainContext.startActivity(promptInstall);

    }

    private void loadTypeOfShipment() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        load_type_of_shipment_async();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Utils.mainContext);
        builder.setMessage("Загрузить способы доставки?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();


    }

    private void load_type_of_shipment_async() {

        LoadFragment.Load_type_of_shipment_async load_type_of_shipment_async = new LoadFragment.Load_type_of_shipment_async();
        load_type_of_shipment_async.execute();
    }

    class Load_type_of_shipment_async extends AsyncTask {
        private String error = "";

        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;

        public Load_type_of_shipment_async() {}

        @Override
        protected Object doInBackground(Object[] objects) {


            Utils.clearTable(DataBaseContract.TYPE_OF_SHIPMENT_TABLE_NAME);

            error = "";
            String stringUrl = Utils.mainServer +"/hs/ExchangeOrders/v1/get_type_of_shipment";
            String result = null;
            String inputLine;
            HttpURLConnection connection = null;
            boolean success;

            JSONObject jsonObject = null;
            JSONArray jsonData = null;
            JSONArray jsonErrors = null;

            //Create a connection
            URL myUrl = null;
            try {
                myUrl = new URL(stringUrl);
                connection =(HttpURLConnection) myUrl.openConnection();

                connection.setRequestProperty("ID_android",Utils.GIUD_DEVICE);
                connection.setRequestProperty("log_android",Utils.curUser.getEmail());
                connection.setRequestProperty("pas_android",Utils.curUser.getPassword());
                connection.setRequestProperty("Accept", "application/json");

                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.flush();
                wr.close();
                connection.connect();

            } catch (IOException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }

            try {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }


            try {
                jsonObject = new JSONObject(result);

            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }


            try {
                success = (boolean) jsonObject.getBoolean("success");

            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }


            if (!success) {
                try {
                    jsonErrors = jsonObject.getJSONArray("errors");
                    for (int i = 0; i < jsonErrors.length(); i++) {
                        String er = jsonErrors.getString(i);
                        Log.i("request", er);
                        error = error +"\n"+er;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = e.getMessage();
                    return null;
                }
            }

            ArrayList<TypeOfShipment> arrayList = new ArrayList<>();
            try {
                jsonData = jsonObject.getJSONArray("items");
                for (int i=0; i< jsonData.length();i++ ){
                    JSONObject jObject = jsonData.getJSONObject(i);
                    TypeOfShipment typeOfShipment = new TypeOfShipment();
                    typeOfShipment.setCode_1c(jObject.getString("code"));
                    typeOfShipment.setName(jObject.getString("description"));
                    typeOfShipment.setCode_main_type(jObject.getString("code_main_type"));
                    typeOfShipment.setAvailable(jObject.getBoolean("available"));

                    arrayList.add(typeOfShipment);
                }



            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }

            try {
                Utils.insertTypeOfShipment(arrayList);
            } catch (Exception e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(Utils.mainContext, "Старт загрузки способов отправки", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (error.equals("")){
                Toast.makeText(Utils.mainContext, "Способы отправки загружены", Toast.LENGTH_SHORT).show();
            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(Utils.mainContext);
                builder.setMessage(error);
                builder.setTitle("Ошибка загрузки способов отправки.");
                builder.setCancelable(true);
                builder.setNegativeButton("OK",null);
                builder.show();

            }

            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


    }


    private void load_Goods(){
        finishLoading = false;
        setEnableButtonLoadGoods();

        String url;
        if (! nextGoods){
            url = Utils.mainServer +"/hs/GetCatalog/v1/get_catalog";
            nextGoods = true;
            Toast.makeText(Utils.mainContext,"Начало загрузки номенклатуры...", Toast.LENGTH_LONG).show();
        } else{
            url = Utils.mainServer +"/hs/GetCatalog/v1/get_catalog_next";
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                boolean success;
                boolean end_of_catalog;

                JSONArray jsonData;
                JSONArray jsonErrors;

                ArrayList<Goods> goodsArrayList = new ArrayList<>();



                try {
                    success = (boolean) response.getBoolean("success");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Utils.mainContext,"Ошибка получения Товаров", Toast.LENGTH_LONG).show();
                    tools_iv.setVisibility(View.INVISIBLE);
                    return;
                }



                if (!success){
                    boolean disconnect_and_clear_base = false;
                    try {
                        disconnect_and_clear_base = response.getBoolean("disconnect_and_clear_base");
                        if (disconnect_and_clear_base){
                            Utils.clearAllTables(Utils.mainContext);
                            Utils.curUser = null;
                            requestQueue.stop();

                            Intent intent = new Intent(Utils.mainContext, LoginActivity.class);
                            Utils.mainContext.startActivity(intent);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    try {
                        jsonErrors = response.getJSONArray("errors");
                        for (int i=0; i< jsonErrors.length();i++ ){
                            String er = jsonErrors.getString(i);
                            Log.i("request", er);
                            Toast.makeText(Utils.mainContext,er, Toast.LENGTH_LONG).show();
                        }
                        tools_iv.setVisibility(View.INVISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Utils.mainContext,"Ошибка получения Товаров", Toast.LENGTH_LONG).show();
                        tools_iv.setVisibility(View.INVISIBLE);
                        return;
                    }
                }

                try {
                    end_of_catalog = (boolean) response.getBoolean("end_of_catalog");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Utils.mainContext,"Ошибка получения Товаров", Toast.LENGTH_LONG).show();
                    tools_iv.setVisibility(View.INVISIBLE);
                    return;
                }

                number_of_packet++;

                try {
                    jsonData = response.getJSONArray("items");
                    for (int i=0; i< jsonData.length();i++ ){
                        JSONObject jsonObject = jsonData.getJSONObject(i);
                        Goods goods = new Goods();
                        goods.setArticle(jsonObject.getString("Product"));
                        goods.setId_1c(jsonObject.getString("GUID"));
                        goods.setDescription(jsonObject.getString("Name"));
                        goods.setBrand(jsonObject.getString("Brand"));
                        goods.setUrl_of_image(jsonObject.getString("url_image"));
                        goods.setAvailable(false);
                        goods.setRrc(jsonObject.getLong("rrc"));
                        goods.setGood_of_week(jsonObject.getBoolean("good_of_week"));
                        goods.setPrice(0);
                        goods.setIn_box(jsonObject.getInt("box"));
                        goods.setIn_package(jsonObject.getInt("package"));

                        goodsArrayList.add(goods);
                    }
                    Toast.makeText(Utils.mainContext,"Получен "+number_of_packet+" пакет номенклатуры.", Toast.LENGTH_LONG).show();
                    if (!end_of_catalog){

                        Toast.makeText(Utils.mainContext,"Запись в базу...", Toast.LENGTH_LONG).show();
                        writeGoodsToBase(goodsArrayList);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Utils.mainContext,"Ошибка получения Товаров", Toast.LENGTH_LONG).show();
                    tools_iv.setVisibility(View.INVISIBLE);
                }

                if (end_of_catalog){
                    Toast.makeText(Utils.mainContext,"Товары получены, запись в базу...", Toast.LENGTH_LONG).show();
                    finishLoading = true;
                    writeGoodsToBase(goodsArrayList);
                    tools_iv.setVisibility(View.INVISIBLE);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Utils.mainContext,"Ошибка получения Товаров", Toast.LENGTH_LONG).show();
                tools_iv.setVisibility(View.INVISIBLE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("ID_android", Utils.curUser.getID_android());
                headers.put("log_android", Utils.curUser.getEmail());
                headers.put("pas_android", Utils.curUser.getPassword());

                headers.put("Accept", "application/json");
                return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Schedule the request on the queue
        requestQueue.add(request);

    }

    private void writeGoodsToBase(ArrayList<Goods> goodsArrayList) {


        if (eraseGoods){
            dbOH.clearTable(dbOH.getWritableDatabase(), DataBaseContract.GOODS_TABLE_NAME);
            dbOH.clearTable(dbOH.getWritableDatabase(),DataBaseContract.BRANDS_TABLE_NAME);
            eraseGoods = false;
        }

        WriteGoodsToBaseAsync writeGoodsToBaseAsync = new WriteGoodsToBaseAsync(goodsArrayList, finishLoading);
        writeGoodsToBaseAsync.execute();

        if (finishLoading){
            Toast.makeText(Utils.mainContext,"Товары успешно загружены.", Toast.LENGTH_LONG).show();
            setEnableButtonLoadGoods();
            eraseGoods = true;
            finishLoading = false;
            tools_iv.setVisibility(View.INVISIBLE);


        }
    }

    private class WriteGoodsToBaseAsync extends AsyncTask{

        private ArrayList<Goods>goodsArrayList;
        private boolean finishLoading;
        public WriteGoodsToBaseAsync(ArrayList<Goods>goodsArrayList, boolean finishLoading) {
            this.goodsArrayList = goodsArrayList;
            this.finishLoading = finishLoading;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            for (int i=0; i< goodsArrayList.size();i++){
                ContentValues contentValues = contentValuesGoods(goodsArrayList.get(i));

                insertGoods(contentValues);

                contentValues = contentValuesPicture(goodsArrayList.get(i));
                insertPicture(contentValues);

            }

            if (finishLoading){
                eraseGoods = true;
                finishLoading = false;
                Utils.loadBrandsTable();

            }else{
                load_Goods();
            }
            return null;
        }


    }

    private void insertPicture(ContentValues contentValues) {
        if (contentValues == null){
            return;
        }

        String selection = DataBaseContract.R_PICTURE.RP_ID_1C+"=?";
        String[] selectionArgs = new String[]{contentValues.getAsString(DataBaseContract.R_PICTURE.RP_ID_1C)};

        ContentResolver cr=Utils.mainContext.getContentResolver();
        Cursor c = cr.query(DataBaseContract.BASE_CONTENT_URI_GOODS_PICTURE, DataBaseContract.R_PICTURE.PICTURE_COLUMNS,selection,selectionArgs,null);
        if (c.getCount() > 0){
            cr.update(DataBaseContract.BASE_CONTENT_URI_GOODS_PICTURE,contentValues,selection,selectionArgs);
        }else{
            cr.insert(DataBaseContract.BASE_CONTENT_URI_GOODS_PICTURE,contentValues);
        }

    }

    private ContentValues contentValuesPicture(Goods g) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseContract.R_PICTURE.RP_ID_1C,g.getId_1c());
        contentValues.put(DataBaseContract.R_GOODS.RG_URL_IMAGE,g.getUrl_of_image());
        return contentValues;
    }

    private ContentValues contentValuesGoods(Goods g){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseContract.R_GOODS.RG_ID_1C,g.getId_1c());
        contentValues.put(DataBaseContract.R_GOODS.RG_ARTICLE,g.getArticle());
        contentValues.put(DataBaseContract.R_GOODS.RG_GOOD_OF_WEEK,g.getDescription());
        contentValues.put(DataBaseContract.R_GOODS.RG_DESCRIPTION,g.getDescription());
        contentValues.put(DataBaseContract.R_GOODS.RG_BRAND,g.getBrand());
        contentValues.put(DataBaseContract.R_GOODS.RG_URL_IMAGE,g.getUrl_of_image());
        contentValues.put(DataBaseContract.R_GOODS.RG_RRC,g.getRrc());
        contentValues.put(DataBaseContract.R_GOODS.RG_PRICE,g.getPrice());
        contentValues.put(DataBaseContract.R_GOODS.RG_AVAILABLE,g.isAvailable());
        contentValues.put(DataBaseContract.R_GOODS.RG_GOOD_OF_WEEK,g.isGood_of_week());
        contentValues.put(DataBaseContract.R_GOODS.RG_BOX,g.getIn_box());
        contentValues.put(DataBaseContract.R_GOODS.RG_PACKAGE,g.getIn_package());

        return contentValues;
    }

    private void insertGoods(ContentValues c) {

        ContentResolver contentResolver = Utils.mainContext.getContentResolver();
        Uri uri =contentResolver.insert(DataBaseContract.BASE_CONTENT_URI_GOODS,c);

        if (uri==null){
            Toast.makeText(Utils.mainContext,"Ошибка записи в базу номенклатуры.", Toast.LENGTH_LONG).show();
        }

    }


    public void btLoadGoods(View view) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        tools_iv.setVisibility(View.VISIBLE);
                        Glide.with(Utils.mainContext).asGif().load(R.drawable.tool).into(tools_iv);


                        nextGoods= false;
                        number_of_packet = 0;
                        load_Goods();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Utils.mainContext);
        builder.setMessage("Загрузить номенклатуру?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();





    }

    private void setVisibilityTool(int gone) {
        tools_iv.setVisibility(gone);
    }

    public void btClearGoods(View view) {
        int i=0;
        i++;
        //DataBaseHandler dbOH = new DataBaseHandler(this);
        Toast.makeText(Utils.mainContext,"Очистка номенклатуры...", Toast.LENGTH_LONG).show();
        dbOH.clearTable(dbOH.getWritableDatabase(), DataBaseContract.GOODS_TABLE_NAME);
    }

    public void btLoadPicturesOnclick(View view) {



        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        load_picture_async();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Utils.mainContext);
        builder.setMessage("Загрузить изображения?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();

    }

    private void load_picture_async() {

        progressBar.setVisibility(View.VISIBLE);

        LoadFragment.Load_picture_async load_picture_async = new LoadFragment.Load_picture_async(progressBar);
        load_picture_async.execute();


    }

    class Load_picture_async extends AsyncTask {

        private ProgressBar mProgressBar;

        public Load_picture_async(ProgressBar target) {
            mProgressBar = target;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            ContentResolver cr = Utils.mainContext.getContentResolver();
            Cursor cursor = cr.query(DataBaseContract.BASE_CONTENT_URI_GOODS_PICTURE,null,null,null);

            mProgressBar.setMin(0);
            mProgressBar.setMax(cursor.getCount());

            //cursor.moveToFirst();

            int progress =0;

            while (cursor.moveToNext()){

                byte[] image = null;
                try {
                    image = cursor.getBlob(cursor.getColumnIndexOrThrow(DataBaseContract.R_PICTURE.RP_IMAGE));
                }catch (Exception e){

                }


                if (image == null) {
                    String url_image = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.R_PICTURE.RP_URL_IMAGE));

                    try {
                        URL url = new URL(url_image);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);

                        if (bitmap != null){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DataBaseContract.R_PICTURE.RP_IMAGE, Utils.DbBitmapUtility.getBytes(bitmap));

                            cr.update(DataBaseContract.BASE_CONTENT_URI_GOODS_PICTURE,contentValues, DataBaseContract.R_PICTURE.RP_KEY_ID +"=?",new String[]{cursor.getString(cursor.getColumnIndex(DataBaseContract.R_PICTURE.RP_KEY_ID))});
                        }

                    } catch (IOException e) {
                        // Log exception
                    }
                }

                publishProgress(progress);
                progress++;

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(Utils.mainContext, "Старт загрузки изображений", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(Utils.mainContext, "Изображения загружены", Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

            mProgressBar.setProgress(((Integer)values[0]));
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}