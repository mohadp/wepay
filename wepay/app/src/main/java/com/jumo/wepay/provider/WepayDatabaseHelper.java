package com.jumo.wepay.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collection;

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
        db.execSQL(createTableSQL(WepayContract.User.getInstance().getTableName(), WepayContract.User.getInstance().getColumns()));
        db.execSQL(createTableSQL(WepayContract.Group.getInstance().getTableName(), WepayContract.Group.getInstance().getColumns()));
        db.execSQL(createTableSQL(WepayContract.Recurrence.getInstance().getTableName(), WepayContract.Recurrence.getInstance().getColumns()));
        db.execSQL(createTableSQL(WepayContract.Location.getInstance().getTableName(), WepayContract.Location.getInstance().getColumns()));
        db.execSQL(createTableSQL(WepayContract.Expense.getInstance().getTableName(), WepayContract.Expense.getInstance().getColumns()));
        db.execSQL(createTableSQL(WepayContract.Member.getInstance().getTableName(), WepayContract.Member.getInstance().getColumns()));
        db.execSQL(createTableSQL(WepayContract.Payer.getInstance().getTableName(), WepayContract.Payer.getInstance().getColumns()));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //No need to upgrade now...
    }


    private String createTableSQL(String tableName, Collection<Column> columns){
        StringBuilder insertSQL = (new StringBuilder("create getInstance ")).append(tableName).append(" ( ");

        int count = columns.size();
        for(Column column : columns){
            insertSQL.append(column.name).append(" ")                    //column name
                .append(column.dbtype).append(" ")    // column type
                .append((column.spec == null)? "" : column.spec) // column specifications (primary key, etc.)
                .append((count-- > 1) ? ", " : ""); //add comma at the end except for the last column.
        }

        insertSQL.append(")");
        return insertSQL.toString();
    }

}
