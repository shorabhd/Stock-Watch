package com.developer.shorabhd.StockWatch;

/**
 * Created by shorabhd on 3/5/17.
 */

public class Stock implements Comparable<Stock>{

    public Stock(String Symbol){
        this.Symbol = Symbol;
    }

    String Symbol;
    String Company;

    public Stock(String symbol,String company, double price, double price_change, double price_change_perc) {
        this.Symbol = symbol;
        this.Company = company;
        this.Price = price;
        this.PriceChange = price_change;
        this.ChangedPerc = price_change_perc;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String Symbol) {
        this.Symbol = Symbol;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String Company) {
        this.Company = Company;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double Price) {
        this.Price = Price;
    }

    public double getPriceChange() {
        return PriceChange;
    }

    public void setPriceChange(double PriceChange) {
        this.PriceChange = PriceChange;
    }

    public double getChangedPerc() {
        return ChangedPerc;
    }

    public void setChangedPerc(double ChangedPerc) {
        this.ChangedPerc = ChangedPerc;
    }

    double Price;
    double PriceChange;
    double ChangedPerc;

    private static int ctr = 1;

    public Stock() {
        this.Symbol = "Stock " + ctr;
        this.Company = "Company " + ctr;
        this.Price = 0.0;
        this.PriceChange = 0.0;
        this.ChangedPerc = 0.0;
        ctr++;
    }

    public Stock(String Symbol,String Company) {
        this.Symbol = Symbol;
        this.Company = Company;
        this.Price = 0.0;
        this.PriceChange = 0.0;
        this.ChangedPerc = 0.0;
    }


    @Override
    public int compareTo(Stock stock) {
        return Symbol.compareTo(stock.getSymbol().toString());
    }
}
