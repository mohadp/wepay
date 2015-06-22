package com.jumo.wepay.wepay;

import android.location.Location;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Moha on 6/20/15.
 */
public class Expense {
    private UUID group;
    private String user;
    private Date timestamp;
    private double latitude;
    private double longitude;
    private String paidCurrency;
    private int category;
    private HashMap<String, Double> whoPaid;
    private HashMap<String, Double> paidFor;



    public Expense(){    }


    public UUID getGroup() {
        return group;
    }

    public void setGroup(UUID group) {
        this.group = group;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPaidCurrency() {
        return paidCurrency;
    }

    public void setPaidCurrency(String paidCurrency) {
        this.paidCurrency = paidCurrency;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public HashMap<String, Double> getWhoPaid() {
        return whoPaid;
    }

    public void setWhoPaid(HashMap<String, Double> whoPaid) {
        this.whoPaid = whoPaid;
    }

    public HashMap<String, Double> getPaidFor() {
        return paidFor;
    }

    public void setPaidFor(HashMap<String, Double> paidFor) {
        this.paidFor = paidFor;
    }
}
