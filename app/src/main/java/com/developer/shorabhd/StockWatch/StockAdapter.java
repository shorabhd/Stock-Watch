package com.developer.shorabhd.StockWatch;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shorabhd on 3/5/17.
 */

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {
    private static final String TAG = "StockAdapter";
    private List<Stock> mStockList;
    private MainActivity mainAct;

    public StockAdapter(List<Stock> mStockList, MainActivity ma) {
        this.mStockList = mStockList;
        mainAct = ma;
    }

    public StockAdapter(ArrayList<Stock> mStockList) {
        this.mStockList = mStockList;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new StockViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = mStockList.get(position);
        holder.mSymbol.setText(stock.getSymbol());
        holder.mCompanyName.setText(stock.getCompany());
        holder.mPrice.setText(Double.toString(stock.getPrice()));
        holder.mChangedPerc.setText("(" + Double.toString(stock.getChangedPerc()) + "%)");
        if(stock.getPriceChange()>=0) {
            holder.mSymbol.setTextColor(Color.GREEN);
            holder.mCompanyName.setTextColor(Color.GREEN);
            holder.mPrice.setTextColor(Color.GREEN);
            holder.mPriceChange.setText("\u25B2"+Double.toString(stock.getPriceChange()));
            holder.mPriceChange.setTextColor(Color.GREEN);
            holder.mChangedPerc.setTextColor(Color.GREEN);
        }
        else{
            holder.mSymbol.setTextColor(Color.RED);
            holder.mCompanyName.setTextColor(Color.RED);
            holder.mPrice.setTextColor(Color.RED);
            holder.mPriceChange.setText("\u25BC"+(Double.toString((-1)*stock.getPriceChange())));
            holder.mPriceChange.setTextColor(Color.RED);
            holder.mChangedPerc.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }
}
