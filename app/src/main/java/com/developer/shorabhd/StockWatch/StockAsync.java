package com.developer.shorabhd.StockWatch;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shorabhd on 3/9/17.
 */

public class StockAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = "StockAsyncLoaderTask";
    private MainActivity mainActivity;
    private List<Stock> wData = new ArrayList<Stock>();
    private final String URL = "https://stocksearchapi.com/api/";
    private final String API_KEY = "05e12c3ccaa86fcfd32c156916772e74a0085d9d";
    String[] sArray;
    public StockAsync(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... params) {

        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("api_key", API_KEY);
        buildURL.appendQueryParameter("search_text", params[0]);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d(TAG,line);
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        }
        catch (Exception e) {
            return "Exception";
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            if (s.equals("Exception")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setMessage("No Stock Found");
                builder.setTitle("Invalid Symbol");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Log.d(TAG,s);
                JSONArray stock = new JSONArray(s);
                if (stock.length() == 1)
                    for (int i = 0; i < stock.length(); i++) {
                        JSONObject jStock = (JSONObject) stock.get(i);
                        mainActivity.processNewStock(jStock.getString("company_symbol"), jStock.getString("company_name"));
                    }
                else {
                    sArray = new String[stock.length()];
                    for (int i = 0; i < stock.length(); i++)
                        sArray[i] = ((JSONObject) stock.get(i)).getString("company_symbol") + " : " + ((JSONObject) stock.get(i)).getString("company_name");
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle("Make a selection");
                    builder.setItems(sArray, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (mainActivity.isNetworkAvailable())
                                mainActivity.processNewStock(sArray[which].split(" : ")[0], sArray[which].split(" : ")[1]);
                        }
                    });
                    builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                Log.d(TAG, "onPostExecute: ");
            }

            } catch(Exception e){
                e.printStackTrace();
            }

    }
}
