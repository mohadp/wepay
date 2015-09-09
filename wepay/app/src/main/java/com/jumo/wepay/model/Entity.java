package com.jumo.wepay.model;

import com.jumo.wepay.provider.Table;

import java.util.Date;

/**
 * Created by Moha on 9/7/15.
 */
public interface Entity {

    public Table entity();

    public int getInt(String column);

    public long getLong(String column);

    public double getDouble(String column);

    public boolean getBoolean(String column);

    public String getText(String column);

    public Date getDate(String column);

    public byte[] getBytes(String column);

    public void setField(String column, Object val);

}
