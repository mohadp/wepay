package com.jumo.wepay.provider.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.wepay.provider.WepayContract;

/**
* Created by Moha on 7/15/15.
*/
public class UserCursor extends CursorWrapper {

    public UserCursor(Cursor c){
        super(c);
    }

    public com.jumo.wepay.model.User getUser(){
        if(isBeforeFirst() || isAfterLast()){
            return null;
        }
        com.jumo.wepay.model.User user = new com.jumo.wepay.model.User();
        int colIndex = -1;

        if((colIndex = getColumnIndex(WepayContract.User._ID)) >= 0) user.setId(getString(colIndex));
        if((colIndex = getColumnIndex(WepayContract.User.NAME)) >= 0)user.setName(getString(colIndex));
        if((colIndex = getColumnIndex(WepayContract.User.PHONE)) >= 0)user.setPhone(getString(colIndex));
        if((colIndex = getColumnIndex(WepayContract.User.USER_BALANCE)) >= 0) user.setUserBalance(getDouble(colIndex));
        return user;
    }

    public String toString(){
        int pos = this.getPosition();
        StringBuilder toString =  new StringBuilder("UserCursor: \n"); //Replace this

        this.moveToFirst();
        toString.append("  ").append(this.getUser().toString()).append("\n"); //Replace this
        while(this.moveToNext()){
            toString.append("  ").append(this.getUser().toString()).append("\n");  //Replace this
        }

        this.moveToPosition(pos);
        return toString.toString();
    }

}
