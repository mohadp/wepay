package com.jumo.tablas.provider.dao;

import android.content.ContentValues;


import com.jumo.tablas.model.Entity;
import com.jumo.tablas.model.Expense;
import com.jumo.tablas.model.Group;
import com.jumo.tablas.model.Member;
import com.jumo.tablas.model.Payer;
import com.jumo.tablas.model.User;
import com.jumo.tablas.provider.TablasContract;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 7/4/15.
 */
public class EntityWriter {

    //List of toContentValues functions that convert objects into ContentValues.
    //TODO: This can be simplified by just iterating over the Entity's fields to get a ContentValues object.

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

    /*public static ContentValues toContentValues(Group g){
        ContentValues cv = new ContentValues();

        if(g.getId() != 0) cv.put(TablasContract.Group._ID, g.getId());
        if(g.getName() != null) cv.put(TablasContract.Group.NAME, g.getName());
        if(g.getCreatedOn() != null) cv.put(TablasContract.Group.CREATED_ON, g.getCreatedOn().getTime());
        if(g.getGroupPicture() != null) cv.put(TablasContract.Group.GROUP_PICTURE, g.getGroupPicture());

        if(cv.size() == 0) return null;

        return cv;
    }

    public static ContentValues toContentValues(Member m){
        ContentValues cv = new ContentValues();

        if(m.getId() != 0) cv.put(TablasContract.Member._ID, m.getId());
        if(m.getGroupId() != 0) cv.put(TablasContract.Member.GROUP_ID, m.getGroupId());
        if(m.getUserId() != null) cv.put(TablasContract.Member.USER_ID, m.getUserId());
        cv.put(TablasContract.Member.IS_ADMIN, m.isAdmin()? 1 : 0 );
        cv.put(TablasContract.Member.LEFT_GROUP, m.hasLeftGroup()? 1 : 0);

        if(cv.size() == 0) return null;

        return cv;
    }

    public static ContentValues toContentValues(Payer p){
        ContentValues cv = new ContentValues();

        if(p.getId() != 0) cv.put(TablasContract.Payer._ID, p.getId());
        if(p.getExpenseId() != 0) cv.put(TablasContract.Payer.EXPENSE_ID, p.getExpenseId());
        if(p.getMemberId() != 0) cv.put(TablasContract.Payer.MEMBER_ID, p.getMemberId());
        cv.put(TablasContract.Payer.PERCENTAGE, p.getPercentage());
        cv.put(TablasContract.Payer.ROLE, p.getRole());

        if(cv.size() == 0) return null;

        return cv;
    }

    public static ContentValues toContentValues(User u){
        ContentValues cv = new ContentValues();


        if(u.getId() != null) cv.put(TablasContract.User._ID, u.getId());
        if(u.getName() != null) cv.put(TablasContract.User.NAME, u.getName());
        if(u.getEmail() != null) cv.put(TablasContract.User.EMAIL, u.getEmail());

        if(cv.size() == 0) return null;

        return cv;
    }


    public static ContentValues toContentValues(Expense e){
        ContentValues cv = new ContentValues();

        if(e.getId() != 0) cv.put(TablasContract.Expense._ID, e.getId());
        if(e.getGroupId() != 0) cv.put(TablasContract.Expense.GROUP_ID, e.getGroupId());
        if(e.getCategoryId() != 0) cv.put(TablasContract.Expense.CATEGORY_ID, e.getCategoryId());
        if(e.getLocationId() != 0) cv.put(TablasContract.Expense.LOCATION_ID, e.getLocationId());
        if(e.getRecurrenceId() != 0) cv.put(TablasContract.Expense.RECURRENCE_ID, e.getRecurrenceId());
        if(e.getGroupExpenseId() != 0) cv.put(TablasContract.Expense.GROUP_EXPENSE_ID, e.getGroupExpenseId());
        if(e.getCurrencyId() != null) cv.put(TablasContract.Expense.CURRENCY, e.getCurrencyId());
        if(e.getCreatedOn() != null) cv.put(TablasContract.Expense.CREATED_ON, e.getCreatedOn().getTime());
        if(e.getMessage() != null) cv.put(TablasContract.Expense.MESSAGE, e.getMessage());
        cv.put(TablasContract.Expense.AMOUNT, e.getAmount());
		cv.put(TablasContract.Expense.EXCHANGE_RATE, e.getExchangeRate());
        cv.put(TablasContract.Expense.IS_PAYMENT, e.isPayment()? 1 : 0);

        if(cv.size() == 0) return null;

        return cv;
    }*/
}
