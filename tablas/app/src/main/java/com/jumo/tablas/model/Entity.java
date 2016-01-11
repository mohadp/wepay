package com.jumo.tablas.model;

import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 9/7/15.
 */
public abstract class Entity {

    public abstract Table table();

    public abstract Object get(String column);

    public abstract Iterator<String> getFieldNameIterator();

    public abstract int getInt(String column);

    public abstract long getLong(String column);

    public abstract double getDouble(String column);

    public abstract boolean getBoolean(String column);

    public abstract String getText(String column);

    public abstract Date getDate(String column);

    public abstract byte[] getBytes(String column);

    public abstract void setField(String column, Object val);

}
