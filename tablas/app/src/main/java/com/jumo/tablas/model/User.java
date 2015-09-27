package com.jumo.tablas.model;

import com.jumo.tablas.provider.Table;
import com.jumo.tablas.provider.WepayContract;

import java.util.Date;

/**
 * Created by Moha on 6/26/15.
 */
public class User implements Entity {
    private Entity entity;

    public User(Entity m){
        entity = m;
    }

    public User(){
        entity = new ActualEntity(WepayContract.User.getInstance());
    }

    public String getId() {
        return getText(WepayContract.User._ID);
    }

    public void setId(String id) {
        setField(WepayContract.User._ID, id);
    }

    public String getName() {
        return getText(WepayContract.User.NAME);
    }

    public void setName(String name) {
        setField(WepayContract.User.NAME, name);
    }

    public String getPhone() {
        return getText(WepayContract.User.PHONE);
    }

    public void setPhone(String phone) {
        setField(WepayContract.User.PHONE, phone);
    }

    public double getUserBalance() {
        return getDouble(WepayContract.User.USER_BALANCE);
    }

    public void setUserBalance(double userBalance) {
        setField(WepayContract.User.USER_BALANCE, userBalance);
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
