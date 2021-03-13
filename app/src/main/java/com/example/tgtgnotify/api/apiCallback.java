package com.example.tgtgnotify.api;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface apiCallback {
    void onSuccess(JSONObject result);
    void onError(VolleyError error);
};