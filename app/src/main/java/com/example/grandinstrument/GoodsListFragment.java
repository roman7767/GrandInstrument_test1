package com.example.grandinstrument;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grandinstrument.adapters.GoodsRecyclerViewCursorAdapter;
import com.example.grandinstrument.data_base_model.Client;
import com.example.grandinstrument.utils.DataBaseContract;
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
import java.util.List;

import static android.widget.Toast.makeText;

public class GoodsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;

    private RecyclerView recyclerView;
    public GoodsRecyclerViewCursorAdapter adapter;
    private LinearLayoutManager manager;
    private static final int GOODS_LOADER = 0;
    private static final int GOODS_LOADER_WITH_SELECTION = 1;

    private static String selection = "";
    private static String[] selectionArgs = new String[]{};
    private static String sort;
    private static SearchView searchView;
    private String titleActivity;
    private EditText etSeek;
    private EditText etSeekBrand;

    private EditText etPriceTo;
    private ImageButton ibClearSeek;
    private MenuItem itemMenuShowPrice;
    private TextView tvTitle;
    private View mainView;
    private Button btAddAllDiscountGoods;


    public void setTitleActivity(){
        if (Utils.curClient == null || Utils.curClient.getName().isEmpty()){
            titleActivity = "Товары";
        }else{
            titleActivity = "Товары" + "  "+"Клиент: "+ Utils.curClient.getName();
        }

        ((MainActivity)mContext).setTitle(titleActivity);
        tvTitle.setText(titleActivity);
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


    public GoodsListFragment() {
        mContext = Utils.mainContext;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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

        setDefaultSearchString();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.activity_goods_list, container, false);

        recyclerView = mainView.findViewById(R.id.goods_rv);
        etSeek =  mainView.findViewById(R.id.etSeek);


        etSeekBrand = mainView.findViewById(R.id.etSeekBrand);
        etPriceTo = mainView.findViewById(R.id.etPriceTo);

        adapter = new GoodsRecyclerViewCursorAdapter(mContext);
        manager = new LinearLayoutManager(mContext);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();

        etSeek.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    processingSeek();

                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        etSeek.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                processingSeek();
                etSeek.requestFocus();
            }
        });


        etSeekBrand.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    processingSeek();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        etSeekBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ///if (etSeekBrand.getText().toString().length()>2){
                    processingSeek();
                    etSeekBrand.requestFocus();
                //}

            }
        });



        etPriceTo.setInputType(InputType.TYPE_CLASS_NUMBER);
        etPriceTo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    processingSeek();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        ibClearSeek = mainView.findViewById(R.id.ibClearSeek);
        ibClearSeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_ibClearSeek(v);
            }
        });

        ImageButton ibSeek = mainView.findViewById(R.id.ibSeek);
        ibSeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processingSeek();
            }
        });

        btAddAllDiscountGoods = mainView.findViewById(R.id.btAddAllDiscountGoods);
        btAddAllDiscountGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addAllDiscountGoods(Utils.mainContext);
            }
        });
        return mainView;
    }

    private void setDefaultSearchString(){
        selection = DataBaseContract.R_GOODS.RG_GOOD_OF_WEEK + "=?";
        selectionArgs = new String[]{"1"};
        sort = "rrc";
    }

    private void processingSeek() {

        selection = "";
        selectionArgs = null;

        List<String> aArg = new ArrayList<>();


        String queryGoods = etSeek.getText().toString().trim();
        String queryBrand = etSeekBrand.getText().toString().trim();

        int queryPriceTo = 0;
        if (!etPriceTo.getText().toString().isEmpty()){
            queryPriceTo =  Integer.parseInt(etPriceTo.getText().toString().trim());
        }


        if (queryGoods.isEmpty() && queryBrand.isEmpty()  && queryPriceTo==0){
            setDefaultSearchString();
            restartLoader(GOODS_LOADER);
            return;
        }

        if (!queryGoods.isEmpty())  {
            queryGoods.replaceAll("  ", " ");
            String curWord = "";
            String c;


            for (int i = 0; i < queryGoods.length(); i++) {
                c = queryGoods.substring(i, i + 1);
                if (i == queryGoods.length() - 1 || c.equals(" ")) {
                    if (i == queryGoods.length() - 1) {
                        curWord = curWord + c;
                    }

                    if (!curWord.isEmpty()) {
                        selection = selection + (selection.isEmpty() ? "" : " and ") + "(" + DataBaseContract.R_GOODS.RG_ARTICLE + " like ? or " + DataBaseContract.R_GOODS.RG_DESCRIPTION + " like ? )";
                        aArg.add("%" + curWord + "%");
                        aArg.add("%" + curWord + "%");
                        curWord = "";
                    }
                } else if (!c.isEmpty()) {
                    curWord = curWord + c;
                }
            }

        }


        if (!queryBrand.isEmpty())  {
            queryBrand.replaceAll("  ", " ");
            String curWord = "";
            String c;

            boolean isBrandSelection = false;
            for (int i = 0; i < queryBrand.length(); i++) {
                c = queryBrand.substring(i, i + 1);
                if (i == queryBrand.length() - 1 || c.equals(" ")) {
                    if (i == queryBrand.length() - 1) {
                        curWord = curWord + c;
                    }

                    if (!curWord.isEmpty()) {
                        if (isBrandSelection){
                            selection = selection + (selection.isEmpty() ? "" : " or ") + "(" + DataBaseContract.R_GOODS.RG_BRAND + " like ? )";

                        }else{
                            isBrandSelection = true;
                            selection = selection + (selection.isEmpty() ? "" : " and ") + "(" + DataBaseContract.R_GOODS.RG_BRAND + " like ? )";
                        }

                        aArg.add("%" + curWord + "%");
                        curWord = "";
                    }
                } else if (!c.isEmpty()) {
                    curWord = curWord + c;
                }
            }
        }

        if (queryPriceTo != 0)  {

            selection = selection + (selection.isEmpty() ? " (" : " and (")+ DataBaseContract.R_GOODS.RG_RRC +" <= ?)";
            aArg.add(String.valueOf(queryPriceTo));

        }

        selectionArgs = new String[aArg.size()];
        for (int i = 0; i < aArg.size(); i++) {
            selectionArgs[i] = aArg.get(i);
        }


        recyclerView.requestFocus();
        restartLoader(GOODS_LOADER);
    }


    private void startLoadClient(String curCode, Dialog dialog, EditText txtCodeClient){
        if (curCode.length() < 4){
            txtCodeClient.setError("Длина кода должна быть не меньше 4 символов.");
        }else{
            dialog.dismiss();
            String codeClient = txtCodeClient.getText().toString();
            GoodsListFragment.LoadClient loadClient = new GoodsListFragment.LoadClient(mContext);
            loadClient.execute(codeClient);

        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        return true;
    }

    public void onClick_ibClearSeek(View view) {
        etSeek.setText(null);
        etSeekBrand.setText(null);
        etPriceTo.setText(null);
        processingSeek();

    }


    private class LoadClient extends AsyncTask<String, Void, Void> {
        private ProgressDialog mProgressDialog;

        private Context mContext;
        private String error = "";

        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;


        public LoadClient(Context context) {
            this.mContext = context;

        }

        @Override
        protected Void doInBackground(String... codeClient) {

            error = "";
            // String stringUrl = Utils.mainServer +"/hs/GetClient/v1/get_client_by_code";
            // String stringUrl = Utils.mainServer +"/hs/GetCatalog/v1/get_user";
            String stringUrl = Utils.mainServer +"/hs/GetCatalog/v1/get_client_by_code";
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

                connection.setRequestProperty("ID_android", Utils.GIUD_DEVICE);
                connection.setRequestProperty("log_android", Utils.curUser.getEmail());
                connection.setRequestProperty("pas_android", Utils.curUser.getPassword());
                connection.setRequestProperty("Accept", "application/json");


                String jsonInputString = "{\"code\":\"" +codeClient[0]+"\"}";

                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(jsonInputString);
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

            try {
                jsonData = jsonObject.getJSONArray("items");
                for (int i=0; i< jsonData.length();i++ ){
                    JSONObject jObject = jsonData.getJSONObject(i);
                    if (Utils.curClient == null){
                        Utils.curClient = new Client();
                    }
                    Utils.curClient.setName(jObject.getString("name"));
                    Utils.curClient.setId_1c(jObject.getString("code"));
                    Utils.curClient.setGuid_1c(jObject.getString("guid"));
                    Utils.curClient.setApi_key(jObject.getString("api_key"));
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return null;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ProgressDialog.show(mContext, "Ищем контрагента", "Ищем контрагента ...");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dlg) {
                    GoodsListFragment.LoadClient.this.cancel(true);
                }
            });
        }



        @Override
        protected void onPostExecute(Void result) {
            if (this.isCancelled()) {
                result = null;
                return;
            }

            if (error != null && !error.isEmpty()){
                makeText(mContext,error, Toast.LENGTH_LONG).show();
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            setTitleActivity();

            mContext.getContentResolver().notifyChange(DataBaseContract.BASE_CONTENT_URI_GOODS, null);


        }
    }


    public void initLoader(int id_loader){
        LoaderManager.getInstance(this).initLoader(id_loader, null, this);
    }

    public void restartLoader(int id_loader){
        LoaderManager.getInstance(this).restartLoader(id_loader, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoader(GOODS_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case GOODS_LOADER:
                CursorLoader cursorLoader = new CursorLoader(
                        Utils.mainContext,
                        DataBaseContract.BASE_CONTENT_URI_GOODS,
                        DataBaseContract.R_GOODS.GOODS_COLUMNS_FOR_LIST,
                        selection,
                        selectionArgs,
                        sort
                );
                return cursorLoader;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case GOODS_LOADER :
            case GOODS_LOADER_WITH_SELECTION:
                adapter.swapCursor(data);

                if (!etSeek.getText().toString().isEmpty() && recyclerView.getAdapter().getItemCount() == 0){
                    new AlertDialog.Builder(Utils.mainContext)
                            .setTitle("")
                            .setMessage("По заданным параметрам товары не найдены")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(null, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch(loader.getId()) {
            case GOODS_LOADER:
                adapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

}
