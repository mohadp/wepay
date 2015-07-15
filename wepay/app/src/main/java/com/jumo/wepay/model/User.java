package com.jumo.wepay.model;

/**
 * Created by Moha on 6/26/15.
 */
public class User {

    private String id;
    private String name;
    private String phone;
    private double userBalance;

    public String toString(){
        StringBuilder toString = new StringBuilder("User: {");
        toString.append(id).append(", ")
                .append(name).append(", ")
                .append(phone).append(", ")
                .append(userBalance).append("}");
        return toString.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(double userBalance) {
        this.userBalance = userBalance;
    }
}
