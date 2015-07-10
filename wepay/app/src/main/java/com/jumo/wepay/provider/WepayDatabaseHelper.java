package com.jumo.wepay.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedHashMap;

/**
 * Created by Moha on 6/28/15.
 */
public class WepayDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wepay.sqlite";
    private static final int VERSION = 1;


    public WepayDatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(createTableSQL(WepayContract.User.TABLE_NAME, WepayContract.User.COL_DEFS));
        db.execSQL(createTableSQL(WepayContract.Group.TABLE_NAME, WepayContract.Group.COL_DEFS));
        db.execSQL(createTableSQL(WepayContract.Recurrence.TABLE_NAME, WepayContract.Recurrence.COL_DEFS));
        db.execSQL(createTableSQL(WepayContract.Location.TABLE_NAME, WepayContract.Location.COL_DEFS));
        db.execSQL(createTableSQL(WepayContract.Expense.TABLE_NAME, WepayContract.Expense.COL_DEFS));
        db.execSQL(createTableSQL(WepayContract.Member.TABLE_NAME, WepayContract.Member.COL_DEFS));
        db.execSQL(createTableSQL(WepayContract.Payer.TABLE_NAME, WepayContract.Payer.COL_DEFS));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //No need to upgrade now...
    }


    private String createTableSQL(String tableName, LinkedHashMap<String, String[]> columns){
        StringBuilder insertSQL = (new StringBuilder("create table ")).append(tableName).append(" ( ");

        int count = columns.size();
        for(String col_name : columns.keySet()){
            String[] col_def = columns.get(col_name);
            insertSQL.append(col_name).append(" ")                    //column name
                .append(col_def[WepayContract.COL_TYPE]).append(" ")    // column type
                .append((col_def[WepayContract.COL_SPEC] == null)? "" : col_def[WepayContract.COL_SPEC]) // column specifications (primary key, etc.)
                .append((count-- > 1) ? ", " : ""); //add comma at the end except for the last column.
        }

        insertSQL.append(")");
        return insertSQL.toString();
    }

}
