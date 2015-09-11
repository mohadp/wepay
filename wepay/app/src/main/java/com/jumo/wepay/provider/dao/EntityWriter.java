package com.jumo.wepay.provider.dao;

import android.content.ContentValues;
import android.database.CursorWrapper;
import android.database.Cursor;


import com.jumo.wepay.model.Expense;
import com.jumo.wepay.model.Group;
import com.jumo.wepay.model.Member;
import com.jumo.wepay.model.Payer;
import com.jumo.wepay.model.User;
import com.jumo.wepay.provider.WepayContract;

import java.util.Date;

/**
 * Created by Moha on 7/4/15.
 */
public class EntityWriter {

    //List of toContentValues functions that convert objects into ContentValues.
    //TODO: This can be simplified by just iterating over the Entity's fields to get a ContentValues object.

    public static ContentValues toContentValues(Group g){
        ContentValues cv = new ContentValues();

        if(g.getId() != 0) cv.put(WepayContract.Group._ID, g.getId());
        if(g.getName() != null) cv.put(WepayContract.Group.NAME, g.getName());
        if(g.getCreatedOn() != null) cv.put(WepayContract.Group.CREATED_ON, g.getCreatedOn().getTime());
        if(g.getGroupPicture() != null) cv.put(WepayContract.Group.GROUP_PICTURE, g.getGroupPicture());

        if(cv.size() == 0) return null;

        return cv;
    }

    public static ContentValues toContentValues(Member m){
        ContentValues cv = new ContentValues();

        if(m.getId() != 0) cv.put(WepayContract.Member._ID, m.getId());
        if(m.getGroupId() != 0) cv.put(WepayContract.Member.GROUP_ID, m.getGroupId());
        if(m.getUserId() != null) cv.put(WepayContract.Member.USER_ID, m.getUserId());
        cv.put(WepayContract.Member.IS_ADMIN, m.isAdmin()? 1 : 0 );
        cv.put(WepayContract.Member.LEFT_GROUP, m.hasLeftGroup()? 1 : 0);

        if(cv.size() == 0) return null;

        return cv;
    }

    public static ContentValues toContentValues(Payer p){
        ContentValues cv = new ContentValues();

        if(p.getId() != 0) cv.put(WepayContract.Payer._ID, p.getId());
        if(p.getExpenseId() != 0) cv.put(WepayContract.Payer.EXPENSE_ID, p.getExpenseId());
        if(p.getMemberId() != 0) cv.put(WepayContract.Payer.MEMBER_ID, p.getMemberId());
        cv.put(WepayContract.Payer.PERCENTAGE, p.getPercentage());
        cv.put(WepayContract.Payer.ROLE, p.getRole());

        if(cv.size() == 0) return null;

        return cv;
    }

    public static ContentValues toContentValues(User u){
        ContentValues cv = new ContentValues();


        if(u.getId() != null) cv.put(WepayContract.User._ID, u.getId());
        if(u.getName() != null) cv.put(WepayContract.User.NAME, u.getName());
        cv.put(WepayContract.User.NAME, u.getPhone());

        if(cv.size() == 0) return null;

        return cv;
    }


    public static ContentValues toContentValues(Expense e){
        ContentValues cv = new ContentValues();

        if(e.getId() != 0) cv.put(WepayContract.Expense._ID, e.getId());
        if(e.getGroupId() != 0) cv.put(WepayContract.Expense.GROUP_ID, e.getGroupId());
        if(e.getCategoryId() != 0) cv.put(WepayContract.Expense.CATEGORY_ID, e.getCategoryId());
        if(e.getLocationId() != 0) cv.put(WepayContract.Expense.LOCATION_ID, e.getLocationId());
        if(e.getRecurrenceId() != 0) cv.put(WepayContract.Expense.RECURRENCE_ID, e.getRecurrenceId());
        if(e.getGroupExpenseId() != 0) cv.put(WepayContract.Expense.GROUP_EXPENSE_ID, e.getGroupExpenseId());
        if(e.getCurrencyId() != null) cv.put(WepayContract.Expense.CURRENCY, e.getCurrencyId());
        if(e.getCreatedOn() != null) cv.put(WepayContract.Expense.CREATED_ON, e.getCreatedOn().getTime());
        if(e.getMessage() != null) cv.put(WepayContract.Expense.MESSAGE, e.getMessage());
        cv.put(WepayContract.Expense.AMOUNT, e.getAmount());
		cv.put(WepayContract.Expense.EXCHANGE_RATE, e.getExchangeRate());
        cv.put(WepayContract.Expense.IS_PAYMENT, e.isPayment()? 1 : 0);

        if(cv.size() == 0) return null;

        return cv;
    }


}