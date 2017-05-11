package com.developer.shorabhd.StockWatch;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout swiper;
    public static List<Stock> mStockList = new ArrayList<Stock>();
    private StockDatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mAdapter = new StockAdapter(mStockList,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    doRefresh();
            }
        });
        databaseHandler = new StockDatabaseHandler(this);
        databaseHandler.dumpLog();
        List<Stock> stocks = databaseHandler.loadStocks();
        if(stocks.size() > 0){
            String sym = "", comp="";
            for(int i=0;i<stocks.size();i++){
                sym = sym + stocks.get(i).getSymbol() + ",";
                comp = comp + stocks.get(i).getCompany() + ",";
            }
            new DataAsync(MainActivity.this).execute(sym,comp);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseHandler.close();
    }

    private void doRefresh() {
        if(isNetworkAvailable()) {
            String sym = "", comp="";
            for(int i=0;i<mStockList.size();i++) {
                sym = sym + mStockList.get(i).getSymbol() + ",";
                comp = comp + mStockList.get(i).getCompany() + ",";
            }
            new DataAsync(MainActivity.this).execute(sym,comp);
            mStockList.clear();
            swiper.setRefreshing(false);
            Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.setMessage("Network Unavailable");
            builder.setTitle("No Internet Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        int pos = mRecyclerView.getChildPosition(v);
        Stock s = mStockList.get(pos);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.marketwatch.com/investing/stock/"+s.getSymbol())));
    }

    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
        final int pos = mRecyclerView.getChildPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseHandler.deleteStock(mStockList.get(pos).getSymbol());
                mStockList.remove(pos);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage("Delete Stock " + mStockList.get(pos).getSymbol() + "?");
        builder.setTitle("Delete Stock");
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add:
                if(isNetworkAvailable()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);
                    builder.setView(et);
                    //builder.setIcon(R.drawable.icon1);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (isNetworkAvailable())
                                Log.d("1.", "Add");
                            new StockAsync(MainActivity.this).execute(et.getText().toString());
                        }
                    });
                    builder.setNegativeButton("NO WAY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builder.setMessage("Please enter Company SYMBOL:");
                    builder.setTitle("Stock");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setMessage("Network Unavailable");
                    builder.setTitle("No Internet Connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void processNewStock(String symbol, String company) {
        for(int i=0;i<mStockList.size();i++) {
            if (mStockList.get(i).compareTo(new Stock(symbol,company)) == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Duplicate Stock");
                builder.setMessage("Stock Symbol " + symbol + " is already displayed");
                //builder.setIcon(R.drawable.ic_duplicate);
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
        }
        if (isNetworkAvailable()) {
            databaseHandler.addStock(new Stock(symbol,company));
            databaseHandler.dumpLog();
            new DataAsync(MainActivity.this).execute(symbol,company);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.setMessage("Network Unavailable");
            builder.setTitle("No Internet Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void addNewStock(Stock stock) {
        Log.d("4.","Update Stock");
        mStockList.add(stock);
        Collections.sort(mStockList, new Comparator<Stock>() {
            @Override
            public int compare(Stock lhs, Stock rhs) {
                return lhs.getSymbol().compareTo(rhs.getSymbol());
            }
        });
        mAdapter.notifyDataSetChanged();
    }

}
