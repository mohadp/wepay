package com.jumo.wepay.model;

/**
 * Created by Moha on 6/28/15.
 */
public class Location {

    private long id;
    private String name;
    private double latitude;
    private double longitude;

    public String toString(){
        StringBuilder toString = new StringBuilder("Location: {");
        toString.append(id).append(", ")
                .append(name).append(", ")
                .append(latitude).append(", ")
                .append(longitude).append("}");
        return toString.toString();
    }

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
}
