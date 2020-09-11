package com.cs203t5.ryverbank.entity;

public class Stock {

    private int stockID;
    private String stockName;
    private String companyName;
    private double currentPrice;
    private double rate;
    private double diff;

    public Stock(int id, String name, String company, double currentPrice, double rate, double diff){
        this.stockID = id;
        this.stockName = name;
        this.companyName = company;
        this.currentPrice = currentPrice;
        this.rate = rate;
        this.diff = diff;
    }

    public int getStockID(){
        return this.stockID;
    }

    public String getStockName(){
        return this.stockName;
    }

    public String getCompanyName(){
        return this.companyName;
    }

    public double getCurrentPrice(){
        return this.currentPrice;
    }

    public double getRate(){
        return this.rate;
    }

    public double getDiff(){
        return this.diff;
    }

}
