package com.jumo.wepay.provider.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.wepay.provider.WepayContract;

import java.util.Date;

/**
* Created by Moha on 7/15/15.
*/
public class ExpenseCursor extends CursorWrapper {

    public ExpenseCursor(Cursor c){
        super(c);
    }

    public com.jumo.wepay.model.Expense getExpense(){
        if(isBeforeFirst() || isAfterLast()){
            return null;
        }
        com.jumo.wepay.model.Expense expense = new com.jumo.wepay.model.Expense();
        int colIndex = -1;

        if((colIndex = getColumnIndex(WepayContract.Expense._ID)) >= 0) expense.setId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.GROUP_ID)) >= 0)expense.setGroupId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.CATEGORY_ID)) >= 0)expense.setCategoryId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.LOCATION_ID)) >= 0)expense.setLocationId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.RECURRENCE_ID)) >= 0)expense.setRecurrenceId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.GROUP_EXPENSE_ID)) >= 0)expense.setGroupExpenseId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.CURRENCY)) >= 0)expense.setCurrencyId(getString(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.CREATED_ON)) >= 0)expense.setCreatedOn(new Date(getLong(colIndex)));
        if((colIndex = getColumnIndex(WepayContract.Expense.MESSAGE)) >= 0)expense.setMessage(getString(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.AMOUNT)) >= 0)expense.setAmount(getDouble(colIndex));
		if((colIndex = getColumnIndex(WepayContract.Expense.EXCHANGE_RATE)) >= 0) expense.setExchangeRate(getDouble(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Expense.IS_PAYMENT)) >= 0)expense.setPayment(getInt(colIndex) != 0);
        if((colIndex = getColumnIndex(WepayContract.Group.USER_BALANCE)) >= 0) expense.setUserBalance(getDouble(colIndex));

        return expense;
    }

    public String toString(){
        int pos = this.getPosition();
        StringBuilder toString =  new StringBuilder("ExpenseCursor: \n"); //Replace this

        this.moveToFirst();
        toString.append("  ").append(this.getExpense().toString()).append("\n"); //Replace this
        while(this.moveToNext()){
            toString.append("  ").append(this.getExpense().toString()).append("\n");  //Replace this
        }

        this.moveToPosition(pos);
        return toString.toString();
    }

}
