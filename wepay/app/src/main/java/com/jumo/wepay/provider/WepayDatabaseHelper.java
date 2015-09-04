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
        db.execSQL(createTableSQL(WepayContract.User.table().tableName, WepayContract.User.table().columns));
        db.execSQL(createTableSQL(WepayContract.Group.table().tableName, WepayContract.Group.table().columns));
        db.execSQL(createTableSQL(WepayContract.Recurrence.table().tableName, WepayContract.Recurrence.table().columns));
        db.execSQL(createTableSQL(WepayContract.Location.table().tableName, WepayContract.Location.table().columns));
        db.execSQL(createTableSQL(WepayContract.Expense.table().tableName, WepayContract.Expense.table().columns));
        db.execSQL(createTableSQL(WepayContract.Member.table().tableName, WepayContract.Member.table().columns));
        db.execSQL(createTableSQL(WepayContract.Payer.table().tableName, WepayContract.Payer.table().columns));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //No need to upgrade now...
    }


    private String createTableSQL(String tableName, LinkedHashMap<String, WepayContract.Column> columns){
        StringBuilder insertSQL = (new StringBuilder("create table ")).append(tableName).append(" ( ");

        int count = columns.size();
        for(WepayContract.Column column : columns.values()){
            insertSQL.append(column.name).append(" ")                    //column name
                .append(column.datatype).append(" ")    // column type
                .append((column.spec == null)? "" : column.spec) // column specifications (primary key, etc.)
                .append((count-- > 1) ? ", " : ""); //add comma at the end except for the last column.
        }

        insertSQL.append(")");
        return insertSQL.toString();
    }

}
