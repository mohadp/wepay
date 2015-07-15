package com.jumo.wepay.provider.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.wepay.provider.WepayContract;

import java.util.Date;

/**
* Created by Moha on 7/15/15.
*/
public class GroupCursor extends CursorWrapper {

    public GroupCursor(Cursor c) {
        super(c);
    }

    public com.jumo.wepay.model.Group getGroup() {
        if (isBeforeFirst() || isAfterLast())
            return null;
        com.jumo.wepay.model.Group group = new com.jumo.wepay.model.Group();
        int colIndex = -1;

        if ((colIndex = getColumnIndex(WepayContract.Group._ID)) >= 0)
            group.setId(getLong(colIndex));
        if ((colIndex = getColumnIndex(WepayContract.Group.NAME)) >= 0)
            group.setName(getString(colIndex));
        if ((colIndex = getColumnIndex(WepayContract.Group.CREATED_ON)) >= 0)
            group.setCreatedOn(new Date(getLong(colIndex)));
        if ((colIndex = getColumnIndex(WepayContract.Group.GROUP_PICTURE)) >= 0)
            group.setGroupPicture(getBlob(colIndex));
        if ((colIndex = getColumnIndex(WepayContract.Group.USER_BALANCE)) >= 0)
            group.setUserBalance(getDouble(colIndex));

        return group;

    }


    public String toString(){
        int pos = this.getPosition();
        StringBuilder toString =  new StringBuilder("GroupCursor: \n"); //Replace this

        this.moveToFirst();
        toString.append("  ").append(this.getGroup().toString()).append("\n"); //Replace this
        while(this.moveToNext()){
            toString.append("  ").append(this.getGroup().toString()).append("\n");  //Replace this
        }

        this.moveToPosition(pos);
        return toString.toString();
    }

}
