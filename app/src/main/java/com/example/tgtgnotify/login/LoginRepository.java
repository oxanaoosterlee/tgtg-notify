package com.example.tgtgnotify.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.tgtgnotify.Item;
import com.example.tgtgnotify.api.TgtAPI;
import com.example.tgtgnotify.api.apiCallback;

import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoginRepository {

    private static volatile LoginRepository instance;
    private TgtAPI api;
    private SharedPreferences sharedPref;
    private MutableLiveData<LoggedInUser> loggedInUser;

    // private constructor : singleton access
    private LoginRepository(TgtAPI api, SharedPreferences sharedPref) {
        this.api = api;
        this.sharedPref = sharedPref;
        this.loggedInUser = new MutableLiveData<>();
    }

    public static LoginRepository getInstance(Context context) {
        if (instance == null) {
            TgtAPI api = new TgtAPI(context);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            instance = new LoginRepository(api, sharedPref);
        }
        return instance;
    }

    public LiveData<LoggedInUser> getLoggedInUser(){return this.loggedInUser;}

    public void login(@Nullable String email, @Nullable String password) {
        Log.d("TGTG", "LoginRepository::login()");

        // Check if we need to log in
        if (storedUserAvailable()) {
            if (is_token_expired()) {
                refresh_token();
            }
            else{
                Log.d("TGTG", "Token is still valid. Getting stored user");
                LoggedInUser new_login = getStoredUser();
                new_login.setLoggedIn(true);
                this.loggedInUser.setValue(new_login);
            }

        }
        else{
            if(!email.isEmpty() && !password.isEmpty()) {
                new_login(email, password);
            }
        }
    }

    public void new_login(String email, String password){
        Log.d("TGTG", "New login.");
        apiCallback callback = new apiCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    // Extract correct data
                    String result_access_token = result.getString("access_token");
                    String result_refresh_token = result.getString("refresh_token");
                    String result_user_id = result.getJSONObject("startup_data").getJSONObject("user").getString("user_id");
                    String result_name = "JOE";

                    // Time
                    Date now = Calendar.getInstance().getTime();
                    String token_creation_time = now.toString();


                    storeString("access_token", result_access_token);
                    storeString("refresh_token", result_refresh_token);
                    storeString("user_id", result_user_id);
                    storeString("email", email);
                    storeString("display_name", "JOE");
                    storeString("token_creation_time", token_creation_time);


                    Log.d("TGTG", "Login request returned: " + result.toString());
                    Log.d("TGTG", "Loginrepository::login() token creation time: " + token_creation_time);
                    LoggedInUser new_user = new LoggedInUser(result_user_id, result_name);
                    new_user.setLoggedIn(true);
                    loggedInUser.setValue(new_user);

                } catch (Exception e) {
                    Log.d("TGTG", "Cannot read JSONObject due to error " + e.toString());
                }
            }

            public void onError(VolleyError error) {
                Log.d("TGTG", "Cannot login due to error: " + error.toString());
            }
        };

        api.login(email, password, callback);
    }


    public MutableLiveData<List<Item>> get_data() {
        Log.d("TGTG", "get_data() repository");

        MutableLiveData<List<Item>> items = new MutableLiveData<>();

        apiCallback callback = new apiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Log.d("TGTG", "Got result: " + result.toString());
                } catch (Exception e) {
                    Log.d("TGTG", "Cannot read JSONObject");
                }
            }

            public void onError(VolleyError error) {
                Log.d("TGTG", "Cannot get data due to error: " + error.toString());
            }
        };


        String user_id = getStoredValue("user_id");
        String access_token = getStoredValue("access_token");
        api.request_data(user_id, access_token, callback);
        return items;
    }


    public boolean is_token_expired(){
        Log.d("TGTG", "Repository::is_token_expired()");

        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.US);

        Date token_creation_time = simpledateformat.parse(getStoredValue("token_creation_time"),  new ParsePosition(0));
        Log.d("TGTG", "Token creation time: " + token_creation_time.toString());

        Date now = Calendar.getInstance().getTime();
        Log.d("TGTG", "now: " + now.toString());

        long diff = now.getTime() - token_creation_time.getTime();
        int hours = (int) (diff / 1000 / 3600);
        Log.d("TGTG", "Diff hours: " + String.valueOf(hours));

        // Expired after 4 hours
        return hours >= 0;
    }


    public void refresh_token() {
        Log.d("TGTG", "refresh token");
        apiCallback callback = new apiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String result_refresh_token = result.getString("refresh_token");
                    String result_access_token = result.getString("access_token");

                    Date now = Calendar.getInstance().getTime();
                    String expiry = now.toString();


                    storeString("refresh_token", result_refresh_token);
                    storeString("access_token", result_access_token);
                    storeString("expiry", expiry);


                    Log.d("TGTG", "Expiry: " + expiry);

                    LoggedInUser stored_user = getStoredUser();
                    stored_user.setLoggedIn(true);
                    loggedInUser.setValue(stored_user);

                } catch (Exception e) {
                    Log.d("TGTG", "Cannot read JSONObject");
                }
            }

            public void onError(VolleyError error) {
                Log.d("TGTG", "Cannot get data due to error: " + error.toString());
            }
        };
        api.refresh_token(getStoredValue("refresh_token"), callback);
    }

    // Store string in sharedPreferences
    private void storeString(String keyLabel, String keyValue) {
        SharedPreferences.Editor editor = this.sharedPref.edit();
        editor.putString(keyLabel, keyValue);
        editor.apply();
    }


    private String getStoredValue(String keyLabel) {
        // Return null if value cannot be found.
        return sharedPref.getString(keyLabel, null);
    }

    private LoggedInUser getStoredUser() {
        LoggedInUser new_loggedInUser = (new LoggedInUser(
                getStoredValue("user_id"),
                getStoredValue("display_name")
        ));

        return new_loggedInUser;
    }

    public boolean storedUserAvailable() {
        String id = getStoredValue("user_id");

//        Log.d("TGTG", "LoginRepository::storeduserAvailable() - Stored user with id: " + id + " and token creation time " + token_creation);

        if (id == null) {
            Log.d("TGTG", "Stored user is not available");
            return false;
        } else {
            Log.d("TGTG", "Stored user is available");
            return true;
        }
    }

}