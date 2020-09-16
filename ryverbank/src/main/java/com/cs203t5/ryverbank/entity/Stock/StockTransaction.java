package com.cs203t5.ryverbank.entity;

public class StockTransaction {
    
    private int transactionID;
    private String acc_num;
    private String user_name;
    private String typeOfTransac;
    private double amount;
    private Stock stock;
    //private TimeStamp datetime
    private double time; //assign time as double for now

    public StockTransaction(int id, String acc_num, String username, String type, double amount, Stock stock, double time){
        this.transactionID = id;
        this.acc_num = acc_num;
        this.user_name = username;
        this.typeOfTransac = type;
        this.amount = amount;
        this.stock = stock;
        this.time = time;
    }

    public int getTransactionID(){
        return this.transactionID;
    }

    public String getAccNum(){
        return this.acc_num;
    }

    public String getUserName(){
        return this.user_name;
    }

    public String getTypeOfTransaction(){
        return this.typeOfTransac;
    }

    public double getAmount(){
        return this.amount;
    }

    public Stock getStock(){
        return this.stock;
    }

    public double getDateTime(){
        return this.time;
    }
}
