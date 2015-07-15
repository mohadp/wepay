package com.jumo.wepay.provider.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.wepay.provider.WepayContract;

/**
* Created by Moha on 7/15/15.
*/
public class PayerCursor extends CursorWrapper {

    public PayerCursor(Cursor c){
        super(c);
    }

    public com.jumo.wepay.model.Payer getPayer(){
        if(isBeforeFirst() || isAfterLast()){
            return null;
        }
        com.jumo.wepay.model.Payer payer = new com.jumo.wepay.model.Payer();
        int colIndex = -1;

        if((colIndex = getColumnIndex(WepayContract.Payer._ID)) >= 0) payer.setId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Payer.EXPENSE_ID)) >= 0) payer.setExpenseId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Payer.MEMBER_ID)) >= 0) payer.setMemberId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Payer.PERCENTAGE)) >= 0) payer.setPercentage(getDouble(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Payer.ROLE)) >= 0) payer.setRole(getInt(colIndex));
        return payer;
    }

    public String toString(){
        int pos = this.getPosition();
        StringBuilder toString =  new StringBuilder("PayerCursor: \n"); //Replace this

        this.moveToFirst();
        toString.append("  ").append(this.getPayer().toString()).append("\n"); //Replace this
        while(this.moveToNext()){
            toString.append("  ").append(this.getPayer().toString()).append("\n");  //Replace this
        }

        this.moveToPosition(pos);
        return toString.toString();
    }

}
