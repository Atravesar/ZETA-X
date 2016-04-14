package com.example.npquy.entity;

import java.io.Serializable;

/**
 * Created by ITACHI on 3/24/2016.
 */
public class ElectronicPayment implements Serializable{
    String nonce;
    Integer CustID;
    Double Amount;

    public ElectronicPayment() {
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Integer getCustID() {
        return CustID;
    }

    public void setCustID(Integer custID) {
        CustID = custID;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }
}
