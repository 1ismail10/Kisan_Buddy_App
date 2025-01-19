package com.example.kisanbuddy;

import com.google.gson.annotations.SerializedName;

public class Mandi {
    @SerializedName("State")
    private String state;

    @SerializedName("District")
    private String district;

    @SerializedName("Market")
    private String name;

    @SerializedName("CropName")
    private String cropName;

    @SerializedName("Price")
    private double price;

    private double profit; // This field is computed later, so no annotation needed.

    public Mandi(String state, String district, String name, String cropName, double price, double profit) {
        this.state = state;
        this.district = district;
        this.name = name;
        this.cropName = cropName;
        this.price = price;
        this.profit = profit;
    }

    public String getState() {
        return state;
    }

    public String getDistrict() {
        return district;
    }

    public String getName() {
        return name;
    }

    public String getCropName() {
        return cropName;
    }

    public double getPrice() {
        return price;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }
}
