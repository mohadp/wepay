package com.jumo.tablas.provider.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jumo.tablas.R;
import com.jumo.tablas.provider.dao.Column;
import com.jumo.tablas.provider.TablasContract;

import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * Created by Moha on 6/28/15.
 */
public class TablasDatabaseHelper extends SQLiteOpenHelper {

    WeakReference<Context> contextReference;
    private static final String DB_NAME = "tablas.sqlite";
    private static final int VERSION = 1;


    public TablasDatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
        contextReference = new WeakReference<Context>(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL(createTableSQL(TablasContract.Group.getInstance().getTableName(), TablasContract.Group.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.Currency.getInstance().getTableName(), TablasContract.Currency.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.Country.getInstance().getTableName(), TablasContract.Country.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.ExchangeRate.getInstance().getTableName(), TablasContract.ExchangeRate.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.Expense.getInstance().getTableName(), TablasContract.Expense.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.Member.getInstance().getTableName(), TablasContract.Member.getInstance().getColumns()));
        db.execSQL(createTableSQL(TablasContract.Payer.getInstance().getTableName(), TablasContract.Payer.getInstance().getColumns()));

        insertCountries(db);
        insertCurrencies(db);
    }

    private void insertCountries(SQLiteDatabase db){
        Resources resources = contextReference.get().getResources();
        TypedArray countryIds = resources.obtainTypedArray(R.array.country_ids);
        TypedArray countryNames = resources.obtainTypedArray(R.array.country_name);
        TypedArray countryCodes = resources.obtainTypedArray(R.array.country_code);
        TypedArray countryCurrs = resources.obtainTypedArray(R.array.country_curr);

        for(int i = 0; i < countryIds.length(); i++){
            ContentValues values = new ContentValues();
            values.put(TablasContract.Country._ID, countryIds.getString(i));
            values.put(TablasContract.Country.COUNTRY_NAME, countryNames.getString(i));
            values.put(TablasContract.Country.COUNTRY_CODE, countryCodes.getString(i));
            values.put(TablasContract.Country.COUNTRY_CURR_ID, countryCurrs.getString(i));
            db.insert(TablasContract.Country.TABLE_NAME, null, values);
        }
    }

    private void insertCurrencies(SQLiteDatabase db){
        Resources resources = contextReference.get().getResources();
        TypedArray currencyIds = resources.obtainTypedArray(R.array.currency_ids);
        TypedArray currencyNames = resources.obtainTypedArray(R.array.currency_names);
        TypedArray currencySymbols = resources.obtainTypedArray(R.array.currency_symbols);


        for(int i = 0; i < currencyIds.length(); i++){
            ContentValues values = new ContentValues();
            values.put(TablasContract.Currency._ID, currencyIds.getString(i));
            values.put(TablasContract.Currency.CURRENCY_NAME, currencyNames.getString(i));
            values.put(TablasContract.Currency.CURRENCY_SYMBOL, currencySymbols.getString(i));
            db.insert(TablasContract.Currency.TABLE_NAME, null, values);
        }

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
