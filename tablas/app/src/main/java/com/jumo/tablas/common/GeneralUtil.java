package com.jumo.tablas.common;

import android.database.Cursor;

/**
 * Created by Moha on 3/6/16.
 */
public class GeneralUtil {

    public static String cursorToString(Cursor cursor){
        int pos = cursor.getPosition();
        StringBuilder sb =  new StringBuilder("****** Cursor results:\n");

        cursor.moveToFirst();
        sb.append(rowToString(cursor)).append("\n");

        while(cursor.moveToNext()){
            sb.append(rowToString(cursor)).append("\n");
        }
        cursor.moveToPosition(pos);

        return sb.toString();
    }

    public static String rowToString(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < cursor.getColumnCount(); i++){
            sb.append(cursor.getColumnName(i)).append("  ").append(cursor.getString(i)).append("; ");
        }
        return sb.toString();
    }
}
