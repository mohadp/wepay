package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 6/20/15. Decorator class on ActualEntity, which allows to get fields for particular Table/table
 */
public class BaseEntity extends Entity {
    private Entity entity;

	public BaseEntity(Entity m){
        entity = m;
	}

    /*public BaseEntity(){
        entity = new ActualEntity(TablasContract.Expense.getInstance());
    }*/

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
