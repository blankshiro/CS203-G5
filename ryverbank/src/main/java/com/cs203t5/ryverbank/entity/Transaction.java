package com.cs203t5.ryverbank.entity;

public class Transaction {

    private long transactionID;
    private double amount;
    private String transactionType;

    public Transaction(long id, double amt, String tType){
        this.transactionID = id;
        this.amount = amt;
        this.transactionType = tType;
    }
    public long getID (){
        return transactionID;
    }

    public double getAmonut(){
        return amount;
    }

    public String getTransactionType(){
        return transactionType;
    }

}
