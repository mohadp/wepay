package com.jumo.tablas.model;

import android.util.Log;

import com.jumo.tablas.provider.dao.Column;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Moha on 9/7/15.
 */
public class ActualEntity implements Entity {
    private HashMap<String, Object> attributes;
    private Table table;
    private static final String TAG = "ActualEntity";

    public ActualEntity(Table e){
        attributes = new HashMap<String, Object>();
        table = e;
    }

    @Override
    public Table table(){
        return table;
    }

    @Override
    public Object get(String column){
        return attributes.get(column);
    }

    @Override
    public int getInt(String column){
        int val = 0;
        try {
            Object field = attributes.get(column);
            val = (field == null)? 0 : ((Integer)field).intValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an integer value:" + e.toString());
        }

        return val;
    }

    @Override
    public long getLong(String column){
        long val = 0;
        try {
            Object field = attributes.get(column);
            val = (field == null)? 0 :((Long)field).longValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an long value:" + e.toString());
        }

        return val;
    }

    @Override
    public double getDouble(String column){
        double val = 0;
        try{
            Object field = attributes.get(column);
            val = (field == null)? 0 : ((Double)field).doubleValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an double value:" + e.toString());
        }
        return val;
    }

    @Override
    public boolean getBoolean(String column){
        boolean val = false;
        try{
            Object field = attributes.get(column);
            val = (field == null)? false : ((Boolean)field).booleanValue();
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

    @Override
    public Iterator<String> getFieldNameIterator(){
        return attributes.keySet().iterator();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder((table.getTableName() == null)? "<NoName>" : table.getTableName());
        sb.append(":[\n");

        for(Column c : table.getColumns()){
            sb.append("\t").append(c.name)
                    .append(":").append((get(c.name) == null)? "null": get(c.name).toString())
                    .append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }
}
