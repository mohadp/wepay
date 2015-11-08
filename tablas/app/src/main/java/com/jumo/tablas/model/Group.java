package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 6/20/15.
 */
public class Group implements Entity {
    private Entity entity;

    public Group(Entity m){
        entity = m;
    }

    public Group(){
        entity = new ActualEntity(TablasContract.Group.getInstance());
    }

    public long getId() {
        return getLong(TablasContract.Group._ID);
    }

    public void setId(long id) {
        setField(TablasContract.Group._ID, id);
    }

    public String getName() {
        return getText(TablasContract.Group.NAME);
    }

    public void setName(String name) {
        setField(TablasContract.Group.NAME, name);
    }

    public Date getCreatedOn() {
        return getDate(TablasContract.Group.CREATED_ON);
    }

    public void setCreatedOn(Date createdOn) {
        setField(TablasContract.Group.CREATED_ON, createdOn);
    }

    public byte[] getGroupPicture() {
        return getBytes(TablasContract.Group.GROUP_PICTURE);
    }

    public void setGroupPicture(byte[] groupPicture) {
        setField(TablasContract.Group.GROUP_PICTURE, groupPicture);
    }

    public double getUserBalance() {
        return getDouble(TablasContract.Group.USER_BALANCE);
    }

    public void setUserBalance(double userBalance) {
        setField(TablasContract.Group.USER_BALANCE, userBalance);
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
    public Iterator<String> getFieldNameIterator(){
        return entity.getFieldNameIterator();
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
