package com.example.grandinstrument.data_base_model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.example.grandinstrument.utils.DataBaseContract;
import com.example.grandinstrument.utils.Utils;

public class User {
    private String email;
    private String password;
    private String ID_android;
    private boolean loaded;
    private String _ID;

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getID_android() {
        return ID_android;
    }

    public void setID_android(String ID_android) {
        this.ID_android = ID_android;
    }


    public static User getUserByEmail(String email, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try{
             cursor = contentResolver.query(DataBaseContract.BASE_CONTENT_URI_LOGIN, DataBaseContract.R_LOGIN.LOGIN_COLUMNS,
                DataBaseContract.R_LOGIN.RL_EMAIL + "=?", new String[]{email}, null);}
        catch(Exception e){

        }

        User user=null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            user = new User();
            user.setEmail(email);
            user.setID_android(Utils.GIUD_DEVICE);
            user.setPassword(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_LOGIN.RL_PASSWORD)));
            if (cursor.getInt(cursor.getColumnIndex(DataBaseContract.R_LOGIN.RL_LOADED)) == 1){
                user.setLoaded(true);
            }else{
                user.setLoaded(false);
            }

            user.set_ID(cursor.getString(cursor.getColumnIndex(DataBaseContract.R_LOGIN.RL_KEY_ID)));

        }
        return user;

    }
}
