package com.example.grandinstrument.data_base_model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.grandinstrument.CartActivity;
import com.example.grandinstrument.MainActivity;
import com.example.grandinstrument.OrderActivity;
import com.example.grandinstrument.R;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

import static android.widget.Toast.makeText;

public class Client {
    private String id_1c;
    private String guid_1c;
    private String name;
    private String phone;
    private String api_key;

    public static void startLoadClient(Context context, Client client,String curCode) {
        LoadClient loadClient = new LoadClient(context,client);
        loadClient.execute(curCode);
    }

    private static class LoadClient extends AsyncTask<String, Void, Void> {
        private ProgressDialog mProgressDialog;

        private Context mContext;
        private String error = "";
        private Client client;
        private ArrayList<Client> arrayChoiceOfClient;


        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;


        public LoadClient(Context context, Client client) {
            this.mContext = context;
            this.client = client;
        }

        @Override
        protected Void doInBackground(String... codeClient) {

            error = "";
            // String stringUrl = Utils.mainServer +"/hs/GetClient/v1/get_client_by_code";
            // String stringUrl = Utils.mainServer +"/hs/GetCatalog/v1/get_user";
            String stringUrl = Utils.mainServer + mContext.getString(R.string.adress_get_client_by_code);
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


                String jsonInputString = "{\"code\":\"" + URLEncoder.encode(codeClient[0].replace(' ','~'),"UTF-8")+"\"}";

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

                arrayChoiceOfClient =new ArrayList<>();

                for (int i=0; i< jsonData.length();i++ ){
                    JSONObject jObject = jsonData.getJSONObject(i);
                    Client curClient =  new Client();
                    curClient.setName(jObject.getString("name"));
                    curClient.setId_1c(jObject.getString("code"));
                    curClient.setGuid_1c(jObject.getString("guid"));
                    curClient.setApi_key(jObject.getString("api_key"));
                    arrayChoiceOfClient.add(curClient);
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

            mProgressDialog = ProgressDialog.show(mContext, "Ищем клиента", "Ищем клиента ...");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dlg) {
                    LoadClient.this.cancel(true);
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

            if (arrayChoiceOfClient != null){
                    if (mContext instanceof OrderActivity){
                        OrderActivity orderActivity = (OrderActivity)mContext;
                        orderActivity.choiceClient(arrayChoiceOfClient);
                    }

                    if (mContext instanceof CartActivity){
                        CartActivity cartActivity = (CartActivity)mContext;
                        cartActivity.choiceClient(arrayChoiceOfClient);
                    }

                    if (mContext instanceof MainActivity){
                        MainActivity mainActivity = (MainActivity)mContext;
                        mainActivity.choiceClient(arrayChoiceOfClient);
                    }
            }

        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        if (guid_1c==null && client.guid_1c == null) return true;
        if (guid_1c==null) return false;
        return guid_1c.equals(client.guid_1c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid_1c);
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId_1c() {
        return id_1c;
    }

    public void setId_1c(String id_1c) {
        this.id_1c = id_1c;
    }

    public String getGuid_1c() {
        return guid_1c;
    }

    public void setGuid_1c(String guid_1c) {
        this.guid_1c = guid_1c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
