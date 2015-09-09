package com.jumo.wepay.model;

import android.util.Log;

import com.jumo.wepay.provider.Table;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Moha on 9/7/15.
 */
public class ActualEntity implements Entity {
    private HashMap<String, Object> attributes;
    private Table table;
    private static final String TAG = "ActualEntity";

    public ActualEntity(Table e){
        table = e;
    }

    @Override
    public Table table(){
        return table;
    }

    @Override
    public int getInt(String column){
        int val = 0;
        try {
            val =((Integer) attributes.get(column)).intValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an integer value:" + e.toString());
        }

        return val;
    }

    @Override
    public long getLong(String column){
        long val = 0;
        try {
            val =((Integer) attributes.get(column)).intValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an long value:" + e.toString());
        }

        return val;
    }

    @Override
    public double getDouble(String column){
        double val = 0;
        try{
            val = ((Double)attributes.get(column)).doubleValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an double value:" + e.toString());
        }
        return val;
    }

    @Override
    public boolean getBoolean(String column){
        boolean val = false;
        try{
            val = ((Boolean)attributes.get(column)).booleanValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an boolean value:" + e.toString());
        }
        return val;
    }

    @Override
    public String getText(String column){
        String val = null;
        try{
            val =  (String)attributes.get(column);
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an String value:" + e.toString());
        }
        return val;
    }

    @Override
    public Date getDate(String column){
        Date val = null;
        try{
            val = (Date)attributes.get(column);
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an Date value:" + e.toString());
        }
        return val;
    }

    @Override
    public byte[] getBytes(String column){
        byte[] val = null;
        try{
            val = (byte[])attributes.get(column);
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an byte[] value:" + e.toString());
        }
        return val;
    }

    @Override
    public void setField(String column, Object val){
        attributes.put(column, val);
    }
}
