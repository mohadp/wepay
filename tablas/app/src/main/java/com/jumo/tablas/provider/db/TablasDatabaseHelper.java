package com.jumo.tablas.provider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jumo.tablas.provider.dao.Column;
import com.jumo.tablas.provider.TablasContract;

import java.util.Collection;

/**
 * Created by Moha on 6/28/15.
 */
public class TablasDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wepay.sqlite";
    private static final int VERSION = 1;


    public TablasDatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL(createTableSQL(TablasContract.Group.getInstance().getTableName(), TablasContract.Group.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.Expense.getInstance().getTableName(), TablasContract.Expense.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.Member.getInstance().getTableName(), TablasContract.Member.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.Payer.getInstance().getTableName(), TablasContract.Payer.getInstance().getColumns()));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //No need to upgrade now...
    }


    private String createTableSQL(String tableName, Collection<Column> columns){
        StringBuilder insertSQL = (new StringBuilder("create table ")).append(tableName).append(" ( ");

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
