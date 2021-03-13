package com.example.tgtgnotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.tgtgnotify.api.TgtAPI;
import com.example.tgtgnotify.api.apiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainRepository {

    private static volatile MainRepository instance;
    private TgtAPI api;
    private SharedPreferences sharedPref;
    private MutableLiveData<List<Item>> items;

    // private constructor : singleton access
    private MainRepository(TgtAPI api, SharedPreferences sharedPref) {
        this.api = api;
        this.sharedPref = sharedPref;
        this.items = new MutableLiveData<>();
    }

    public static MainRepository getInstance(Context context) {
        if (instance == null) {
            TgtAPI api = new TgtAPI(context);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            instance = new MainRepository(api, sharedPref);
        }
        return instance;
    }

    public LiveData<List<Item>> getItems(){
        return this.items;
    }


    public void refreshData() {
        Log.d("TGTG", "get_data() repository");
        apiCallback callback = new apiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    items.setValue(extractItemList(result));
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

        Log.d("TGTG", "MainRepository got user_id " + user_id);
        Log.d("TGTG", "MainRepository got access_token" + access_token);

        api.request_data(user_id, access_token, callback);
    }


    private String getStoredValue(String keyLabel) {
        // Return null if value cannot be found.
        return sharedPref.getString(keyLabel, null);
    }


    // Converts the API 'data' response to a list of Item objects.
    private @Nullable List<Item> extractItemList(JSONObject apiResponse) throws JSONException {
        List<Item> itemList = new ArrayList<>();


        JSONArray items = apiResponse.getJSONArray("items");
        Log.d("TGTG", "Items: " + items.toString());

        for (int i = 0, size = items.length(); i < size; i++)
        {
            JSONObject item = items.getJSONObject(i);
            Log.d("TGTG", "Item: " + item.toString());

            String id = item.getJSONObject("item").getString("item_id");
            String storeName = item.getJSONObject("store").getString("store_name");
            String displayName  = item.getString("display_name");

            int price = item.getJSONObject("item").getJSONObject("price_including_taxes").getInt("minor_units");
            int itemsAvailable = item.getInt("items_available");

            Item newItem = new Item(id, displayName, storeName, price, itemsAvailable);
            itemList.add(newItem);
        }

        return itemList;
    }


    public void logout(){
        // Clear all stored data
        this.sharedPref.edit().clear().apply();
    }


    // Store item id in sharedpreferences.
    public void store_item_id(String id){
        Log.d("TGTG", "Adding item " + id);
        // Check if list of items is already available
        Set<String> selected_ids;

        if (sharedPref.contains("selected_ids")) {
            selected_ids = sharedPref.getStringSet("selected_ids", null);
            Log.d("TGTG", "Before adding " + selected_ids.toString());
            if (selected_ids.contains(id)) {
                return;
            } // id already stored.
        }
        else{selected_ids = new HashSet<>();}
        selected_ids.add(id);
        Log.d("TGTG", "Before adding " + selected_ids.toString());

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet("selected_ids", selected_ids);
        editor.apply();
    }

    public void remove_item_id(String id){
        Log.d("TGTG", "Removing item " + id);
        if (sharedPref.contains("selected_ids")) {
            Set<String> selected_ids = sharedPref.getStringSet("selected_ids", null);
            Log.d("TGTG", "Before removing " + selected_ids.toString());
            selected_ids.remove(id);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putStringSet("selected_ids", selected_ids);
            editor.apply();
        }
    }

    public Set<String> getStoredItemIds(){
        Set<String> selected_ids = sharedPref.getStringSet("selected_ids", null);
        return selected_ids;
    }

}
