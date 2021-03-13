package com.example.tgtgnotify.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


// https://stackoverflow.com/questions/28172496/android-volley-how-to-isolate-requests-in-another-class
public class TgtAPI {

    private String login_url = "https://apptoogoodtogo.com/api/auth/v1/loginByEmail";
    private String request_url = "https://apptoogoodtogo.com/api/item/v7/";
    private String refresh_token_url = "https://apptoogoodtogo.com/api/auth/v1/token/refresh";

    private RequestQueue requestQueue;


    public TgtAPI(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }


    // Log in using e-mail address and password and retrieve access_token.
    public void login(String email, String password, apiCallback callback) {
        Log.d("TGTG", "API call: login");
        String postUrl = this.login_url;
        Map<String, String> headers = this.getHeaders("");

        // Request data
        JSONObject postData = new JSONObject();
        try {
            postData.put("device_type", "ANDROID");
            postData.put("email", email);
            postData.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    Log.d("TGTG", "Response'" + response.toString()  + "'");
                    callback.onSuccess(response);
                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                return headers;
            }
        };

        // Add post request to queue.

        requestQueue.add(jsonObjectRequest);
    }



    // Request a new access token using the refresh token
    public void refresh_token(String refresh_token, apiCallback callback) {
        Log.d("TGTG", "API call: refresh_token");
        String postUrl = this.refresh_token_url;
        Map<String, String> headers = this.getHeaders("");


        // Request data
        JSONObject postData = new JSONObject();
        try {
            postData.put("refresh_token", refresh_token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                Log.d("TGTG", "Response'" + response.toString()  + "'");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                return headers;
            }
        };

        // Add post request to queue.
        requestQueue.add(jsonObjectRequest);
    }


    // Request data
    public void request_data(String user_id, String accesToken, apiCallback callback) {
        Log.d("TGTG", "API call: request_data");

        String postUrl = this.request_url;
        Map<String, String> headers = this.getHeaders(accesToken);


        // Request data
        JSONObject postData = new JSONObject();
        try {
            postData.put("user_id", user_id);
            postData.put("origin", new JSONObject().put("latitude", 0.0).put("longitude",0.0));
            postData.put("radius", 21);
            postData.put("page_size", 20);
            postData.put("page", 1);
            postData.put("discover", false);
            postData.put("favorites_only", true);
            postData.put("item_categories", JSONObject.NULL);
            postData.put("diet_categories", JSONObject.NULL);
            postData.put("pickup_earliest", JSONObject.NULL);
            postData.put("pickup_latest", JSONObject.NULL);
            postData.put("search_phrase", JSONObject.NULL);
            postData.put("with_stock_only", false);
            postData.put("hidden_only", false);
            postData.put("we_care_only", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                Log.d("TGTG", "Response'" + response.toString()  + "'");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                return headers;
            }
        };

        // Add post request to queue.
        requestQueue.add(jsonObjectRequest);
    }

    public  Map<String, String> getHeaders(String access_token) {
            Map<String, String> headers = new HashMap<>();
            headers.put("user-agent", System.getProperty( "http.agent" ));
            headers.put("accept-language", "en-UK");
            if (!access_token.isEmpty()){
                headers.put("authorization", "Bearer " + access_token);
            }
            return headers;
    }


}