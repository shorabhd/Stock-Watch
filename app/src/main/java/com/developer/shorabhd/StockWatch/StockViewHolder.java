package com.developer.shorabhd.StockWatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shorabhd on 3/9/17.
 */

public class StockViewHolder extends RecyclerView.ViewHolder {
    public TextView mSymbol;
    public TextView mCompanyName;
    public TextView mPrice;
    public TextView mPriceChange;
    public TextView mChangedPerc;

    public StockViewHolder(View view) {
        super(view);
        mSymbol = (TextView) view.findViewById(R.id.symbol);
        mCompanyName = (TextView) view.findViewById(R.id.company_name);
        mPrice = (TextView) view.findViewById(R.id.price);
        mPriceChange = (TextView) view.findViewById(R.id.price_change);
        mChangedPerc = (TextView) view.findViewById(R.id.price_change_perc);
    }
}
