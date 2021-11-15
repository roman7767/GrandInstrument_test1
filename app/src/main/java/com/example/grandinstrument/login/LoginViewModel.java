package com.example.grandinstrument.login;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.grandinstrument.R;
import com.example.grandinstrument.data_base_model.User;
import com.example.grandinstrument.data_login.LoginRepository;
import com.example.grandinstrument.data_login.Result;
import com.example.grandinstrument.data_login.model.LoggedInUser;
import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private Context context;
    private RequestQueue requestQueue;


    LoginViewModel(LoginRepository loginRepository, Context context) {
        this.loginRepository = loginRepository;
        this.context = context;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) throws IOException {
        // can be launched in a separate asynchronous job
        requestQueue = Volley.newRequestQueue(context);
        Utils.curUser = User.getUserByEmail(username, context);
        if (Utils.curUser == null || !Utils.curUser.isLoaded()){
            if (Utils.curUser == null){
                Utils.curUser = new User();
                Utils.curUser.setPassword(password);
                Utils.curUser.setID_android(Utils.GIUD_DEVICE);
                Utils.curUser.setEmail(username);
            }


            String url = Utils.mainServer +"/hs/GetCatalog/v1/get_user";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    boolean success;
                    JSONArray jsonData;
                    JSONArray jsonErrors;

                    try {
                        success = (boolean) response.getBoolean("success");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.curUser = null;
                        Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
                        setAvailableLoginView(true);
                        return;
                    }

                    if (!success){
                        try {

                            boolean disconnect_and_clear_base = false;
                            try {
                                disconnect_and_clear_base = response.getBoolean("disconnect_and_clear_base");
                                if (disconnect_and_clear_base){
                                   Utils.clearLoginTable(context);
                                   Utils.curUser = null;
                                   requestQueue.stop();
                                   setAvailableLoginView(true);
                                   Toast.makeText(context,"Ваша учетная запись заблокирована администратором! Свяжитесь со своим супервайзером для активации доступа в приложение.", Toast.LENGTH_LONG).show();
                                   return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setAvailableLoginView(true);
                            }

                            jsonErrors = response.getJSONArray("errors");
                            for (int i=0; i< jsonErrors.length();i++ ){
                                String er = jsonErrors.getString(i);
                                Toast.makeText(context,er, Toast.LENGTH_LONG).show();
                                Log.i("request", er);
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
                            setAvailableLoginView(true);
                            return;
                        }
                    }

                    if (success){
                        Utils.curUser.setLoaded(true);
                        writeUserToBase();

                        Result<LoggedInUser> result = loginRepository.login(Utils.curUser.getEmail(), Utils.curUser.getPassword());

                        if (result instanceof Result.Success) {
                            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
                        } else {
                            loginResult.setValue(new LoginResult(R.string.login_failed));
                        }
                    }
                    setAvailableLoginView(true);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,error.getMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    setAvailableLoginView(true);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    // on below line we are passing our key
                    // and value pair to our parameters.
                    headers.put("ID_android", Utils.GIUD_DEVICE);
                    headers.put("log_android",username);
                    headers.put("pas_android",password);
                    headers.put("Accept", "application/json");
                    return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
                }


            };


            request.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Schedule the request on the queue
            requestQueue.add(request);


        }else{
            setAvailableLoginView(true);

            Result<LoggedInUser> result = loginRepository.login(Utils.curUser.getEmail(), Utils.curUser.getPassword());

            if (result instanceof Result.Success) {
                LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
            } else {
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        }


    }

    private void setAvailableLoginView(boolean b) {
        LoginActivity loginActivity = (LoginActivity) context;
        loginActivity.setAvailableLoginView(true);
    }

    private void writeUserToBase() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseContract.R_LOGIN.RL_EMAIL, Utils.curUser.getEmail());
        contentValues.put(DataBaseContract.R_LOGIN.RL_LOADED, Utils.curUser.isLoaded());
        contentValues.put(DataBaseContract.R_LOGIN.RL_PASSWORD, Utils.curUser.getPassword());

        ContentResolver contentResolver = context.getContentResolver();

        if (Utils.curUser.get_ID() == null){
            contentResolver.insert(DataBaseContract.BASE_CONTENT_URI_LOGIN, contentValues);
        }else{
        contentResolver.update(DataBaseContract.BASE_CONTENT_URI_LOGIN, contentValues, DataBaseContract.R_LOGIN.RL_EMAIL+"=?",
                new String[]{Utils.curUser.getEmail()});}


    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}