package com.aguadeoro.utils;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Query {
    private final JSONParser parser;
    private final List<NameValuePair> params;
    private JSONObject json;
    private String mQuery;
    private final String method;
    private final ArrayList<Map<String, String>> res;

    public Query(String mQuery) {
        this.parser = new JSONParser();
        this.params = new ArrayList<NameValuePair>();
        this.res = new ArrayList<Map<String, String>>();
        this.mQuery = mQuery;
        this.method = "POST";
    }

    public String getmQuery() {
        return mQuery;
    }

    public void setmQuery(String mQuery) {
        this.mQuery = mQuery;
    }

    public boolean execute() {
        res.clear();
        params.add(new BasicNameValuePair("query", mQuery));
        params.add(new BasicNameValuePair("destination", "aguadeoro19"));
        String server = Utils.getStringSetting("server_address");

        try {
            Log.d("server adress ", server);
            json = parser.makeHttpRequest(server, method, params);
            Log.d("returned json is", "" + json);
            int success = json.getInt("success");
            if (success == 1) {
                if (mQuery.startsWith("select") || mQuery.startsWith("SELECT")) {
                    Log.d("aaa", json.toString());
                    JSONArray users = json.getJSONArray("result");
                    // looping through All Objects
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject obj = users.getJSONObject(i);
                        int id = obj.length();
                        JSONArray v = obj.names();
                        Map<String, String> strRes = new HashMap<String, String>();
                        for (int j = 0; j < id; j++) {
                            strRes.put(v.getString(j), obj.getString(v.getString(j)));
                        }
                        res.add(strRes);
                    }
                } else {
                    Log.d("bbb", "not a select " + mQuery);
                }
            } else {
                Log.e("OOPS", "Error " + json.getString("message"));
                Log.e("OOPS", "Error " + json.getString("query"));
                return false;
            }
        } catch (Exception e) {
            Log.d("ERROR", "could not connect");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<Map<String, String>> getRes() {
        return res;
    }

}