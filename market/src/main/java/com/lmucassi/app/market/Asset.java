package com.lmucassi.app.market;

import lombok.Getter;

@Getter
public class Asset {
    private String name;
    private double totalAmount;
    private double price;

    public Asset(String name, double totalAmount, double price) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.price =  price;
    }

    public boolean buyAsset(double quantity){
        this.totalAmount -= quantity;
        if (this.totalAmount <= 0){
            this.totalAmount += quantity;
            return false;
        }
            return true;
    }

    public boolean sellAsset(double quant, double price){
        double originalPrice = this.price * quant;
        double sellPrice = price * quant;
        if (originalPrice == sellPrice || originalPrice > sellPrice){
            this.totalAmount = this.totalAmount + quant;
            return true;
        }
        return false;
    }
}
