package com.jumo.wepay.provider.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.wepay.provider.WepayContract;

/**
* Created by Moha on 7/15/15.
*/
public class MemberCursor extends CursorWrapper {

    public MemberCursor(Cursor c){
        super(c);
    }

    public com.jumo.wepay.model.Member getMember() {
        if(isBeforeFirst() || isAfterLast())
            return null;
        com.jumo.wepay.model.Member member = new com.jumo.wepay.model.Member();
        int colIndex = -1;

        if((colIndex = getColumnIndex(WepayContract.Member.GROUP_ID)) >= 0) member.setGroupId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Member._ID)) >= 0) member.setId(getLong(colIndex));
        if((colIndex = getColumnIndex(WepayContract.Member.IS_ADMIN)) >= 0) member.setAdmin(getInt(colIndex) != 0);
        if((colIndex = getColumnIndex(WepayContract.Member.LEFT_GROUP)) >= 0) member.setLeftGroup(getInt(colIndex) != 0);
        if((colIndex = getColumnIndex(WepayContract.Member.USER_ID)) >= 0) member.setUserId(getString(colIndex));

        return member;
    }

    public String toString(){
        int pos = this.getPosition();
        StringBuilder toString =  new StringBuilder("MemberCursor: \n"); //Replace this

        this.moveToFirst();
        toString.append("  ").append(this.getMember().toString()).append("\n"); //Replace this
        while(this.moveToNext()){
            toString.append("  ").append(this.getMember().toString()).append("\n");  //Replace this
        }

        this.moveToPosition(pos);
        return toString.toString();
    }

}
