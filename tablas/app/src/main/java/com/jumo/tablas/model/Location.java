package com.jumo.tablas.model;

import com.jumo.tablas.provider.Table;
import com.jumo.tablas.provider.WepayContract;

import java.util.Date;

/**
 * Created by Moha on 6/28/15.
 */
public class Location implements Entity {
    private Entity entity;

    public Location(Entity m){
        entity = m;
    }

    public Location(){
        entity = new ActualEntity(WepayContract.Location.getInstance());
    }

    public long getId() {
        return getLong(WepayContract.Location._ID);
    }

    public void setId(long id) {
        setField(WepayContract.Location._ID, id);
    }

    public String getName() {
        return getText(WepayContract.Location.NAME);
    }

    public void setName(String name) {
        setField(WepayContract.Location.NAME, name);
    }

    public double getLatitude() {
        return getDouble(WepayContract.Location.LATITUDE);
    }

    public void setLatitude(double latitude) {
        setField(WepayContract.Location.LATITUDE, latitude);
    }

    public double getLongitude() {
        return getDouble(WepayContract.Location.LONGITUDE);
    }

    public void setLongitude(double longitude) {
        setField(WepayContract.Location.LONGITUDE, longitude);
    }

    @Override
    public Table table(){
        return entity.table();
    }

    @Override
    public Object get(String column){
        return entity.get(column);
    }

    @Override
    public int getInt(String column) {
        return entity.getInt(column);
    }

    @Override
    public long getLong(String column) {
        return entity.getLong(column);
    }

    @Override
    public double getDouble(String column) {
        return entity.getDouble(column);
    }

    @Override
    public boolean getBoolean(String column) {
        return entity.getBoolean(column);
    }

    @Override
    public String getText(String column) {
        return entity.getText(column);
    }

    @Override
    public Date getDate(String column) {
        return entity.getDate(column);
    }

    @Override
    public byte[] getBytes(String column) {
        return entity.getBytes(column);
    }

    @Override
    public void setField(String column, Object val) {
        entity.setField(column, val);
    }

    @Override
    public String toString(){
        return entity.toString();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity m){
        entity = m;
    }
}
