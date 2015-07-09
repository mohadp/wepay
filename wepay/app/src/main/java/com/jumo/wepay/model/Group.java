package com.jumo.wepay.model;

import java.util.Date;

/**
 * Created by Moha on 6/20/15.
 */
public class Group {
    private long id;
    private String name;
    private Date createdOn;
    private byte[] groupPicture;
    private double userBalance; //Not persisted in the database; used for contain balances for the group for a user (owes or has paid more and for how much)

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public byte[] getGroupPicture() {
        return groupPicture;
    }

    public void setGroupPicture(byte[] groupPicture) {
        this.groupPicture = groupPicture;
    }

    public double getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(double userBalance) {
        this.userBalance = userBalance;
    }
}
