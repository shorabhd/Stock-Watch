package com.developer.shorabhd.StockWatch;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shorabhd on 3/9/17.
 */

public class DataAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = "DataAsyncLoaderTask";
    private MainActivity mainActivity;
    private List<Stock> wData = new ArrayList<Stock>();
    private String company;
    private final String URL = "http://finance.google.com/finance/info";

    public DataAsync(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... params) {
        company = params[1];
        Log.d(TAG,company);
        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("client", "ig");
        buildURL.appendQueryParameter("q", params[0]);
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
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString().substring(4));

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return "Exception";
        }
        return sb.toString().substring(4);
    }

    @Override
    protected void onPostExecute(String s) {
        String[] companies = company.split(",");
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
            try {
                JSONArray stock = new JSONArray(s);
                for (int i = 0; i < stock.length(); i++) {
                    JSONObject jStock = (JSONObject) stock.get(i);
                    mainActivity.addNewStock(new Stock(jStock.getString("t"), companies[i], jStock.getDouble("l"),
                            jStock.getDouble("c"), jStock.getDouble("cp")));
                }
                Log.d(TAG, "onPostExecute: ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}