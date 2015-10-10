package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;

/**
 * Created by Moha on 6/26/15.
 */
public class Recurrence implements Entity {
    private Entity entity;

    public Recurrence(Entity m){
        entity = m;
    }

    public Recurrence(){
        entity = new ActualEntity(TablasContract.Recurrence.getInstance());
    }

    public long getId() {
        return getLong(TablasContract.Recurrence._ID);
    }

    public void setId(long id) {
        setField(TablasContract.Recurrence._ID, id);
    }

    public long getPeriodicity() {
        return getLong(TablasContract.Recurrence.PERIODICITY);
    }

    public void setPeriodicity(long periodicity) {
        setField(TablasContract.Recurrence.PERIODICITY, periodicity);
    }

    /**
     * When periodicity is
     *  PERIODICITY_DAILY, this variable has no significance;
     *  PERIODICITY_WEEKLY, offset = 1 means Monday, 2 is Tuesday, ... , 7 is Sunday.
     *  PERIODICITY_MONTHLY, offset = 1 means the first of the month, offset = OFFSET_LAST_OF_MONTH means last day of every month.
     **/
    public long getOffset() {
        return getLong(TablasContract.Recurrence.OFFSET);
    }

    public void setOffset(long offset) {
        setField(TablasContract.Recurrence.OFFSET, offset);
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
