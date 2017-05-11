package com.developer.shorabhd.StockWatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by shorabhd on 3/7/17.
 */

public class StockDatabaseHandler extends SQLiteOpenHelper{

    private static final String TAG = "StockDatabaseHandler";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private static final String SQL_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ( "
                                                        +SYMBOL+" TEXT NOT NULL UNIQUE, "
                                                        +COMPANY+" TEXT NOT NULL)";
    private SQLiteDatabase database;

    public StockDatabaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Mking New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ".");
    }

    public void addStock(Stock stock) {
        Log.d(TAG, "addStock: Adding " + stock.getSymbol());
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompany());
        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: " + key);

    }

    public void deleteStock(String symbol) {
        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);
        int cnt = database.delete(TABLE_NAME,SYMBOL+" = ?", new String[]{symbol});
        Log.d(TAG, "deleteStock: " + cnt);
    }

    public ArrayList<Stock> loadStocks() {
        Log.d(TAG, " loadStocks: Load all symbol-company entries from DB");
        ArrayList<Stock> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME, // The table to query
                new String[]{ SYMBOL, COMPANY }, // The columns to return
                null, // The columns for the WHERE clause, null means “*”
                null, // The values for the WHERE clause, null means “*”
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order
        if (cursor != null) { // Only proceed if cursor is not null
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0); // 1st returned column
                String company = cursor.getString(1); // 2nd returned column
                stocks.add(new Stock(symbol, company));
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE LOADING STOCK DATA FROM DB");
        return stocks;
    }

    public void dumpLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                Log.d(TAG, "dumpLog: " +
                        String.format("%s %-18s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", COMPANY + ":", company));
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "dumpLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
}

