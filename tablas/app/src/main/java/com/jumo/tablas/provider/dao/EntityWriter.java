package com.jumo.tablas.provider.dao;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.net.Uri;


import com.jumo.tablas.model.Entity;
import com.jumo.tablas.model.Expense;
import com.jumo.tablas.model.Group;
import com.jumo.tablas.model.Member;
import com.jumo.tablas.model.Payer;
import com.jumo.tablas.provider.TablasContract;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 7/4/15.
 */
public class EntityWriter {

    //List of toContentValues functions that convert objects into ContentValues.

    public static ContentValues toContentValues(Entity entity){
        ContentValues cv = new ContentValues();

        Iterator<String> fieldNameIterator = entity.getFieldNameIterator();

        while(fieldNameIterator.hasNext()){
            String fieldName = fieldNameIterator.next();
            Object value = entity.get(fieldName);
            if(value != null) {
                putValueBasedOnType(cv, fieldName, value);
            }
        }

        if(cv.size() == 0) return null;

        return cv;
    }


    private static void putValueBasedOnType(ContentValues cv, String fieldName, Object value){
        if(value instanceof Long){
            cv.put(fieldName, (Long)value);
        }else if(value instanceof Integer){
            cv.put(fieldName, (Integer)value);
        }else if(value instanceof String){
            cv.put(fieldName, (String)value);
        }else if(value instanceof Double){
            cv.put(fieldName, (Double)value);
        }else if(value instanceof Date){
            cv.put(fieldName, new Long(((Date)value).getTime()));
        }else if(value instanceof Boolean){
            cv.put(fieldName, ((Boolean)value).booleanValue()? 1 : 0);
        }else if(value instanceof byte[]){
            cv.put(fieldName, (byte[])value);
        }else if(value instanceof Float){
            cv.put(fieldName, (Float)value);
        }
    }

}
