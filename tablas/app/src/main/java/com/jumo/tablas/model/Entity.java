package com.jumo.tablas.model;

import com.jumo.tablas.provider.Table;

import java.util.Date;

/**
 * Created by Moha on 9/7/15.
 */
public interface Entity {

    public Table table();

    public Object get(String column);

    public int getInt(String column);

    public long getLong(String column);

    public double getDouble(String column);

    public boolean getBoolean(String column);

    public String getText(String column);

    public Date getDate(String column);

    public byte[] getBytes(String column);

    public void setField(String column, Object val);

}
