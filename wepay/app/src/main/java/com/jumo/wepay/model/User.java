package com.jumo.wepay.model;

/**
 * Created by Moha on 6/26/15.
 */
public class User {

    private String id;
    private String name;
    private long phone;

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

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }
}
