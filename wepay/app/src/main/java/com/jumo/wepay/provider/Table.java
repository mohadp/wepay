package com.jumo.wepay.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Abstract class to represent eventually singleton database entities/mTables in a database (singleton part implemented by child classes). Contains set of mColumns, their spec and their foreign keys.
 */
public abstract class Table {
    protected LinkedHashMap<String, Column> mColumns;
    protected LinkedHashMap<String, ArrayList<ColumnJoin>> mForeignKeys;
    protected String mTableName;


    protected Table(){
        mColumns = new LinkedHashMap<String, Column>();
        mForeignKeys = new LinkedHashMap<String, ArrayList<ColumnJoin>>();
        defineColumnsAndForeignKeys();
    }

    protected Table(String table){
        mColumns = new LinkedHashMap<String, Column>();
        mForeignKeys = new LinkedHashMap<String, ArrayList<ColumnJoin>>();
        mTableName = table;
        defineColumnsAndForeignKeys();
    }

    /**
     * Add information on mColumns and foreign keys. Call first Table's constructor, and then
     * add column and foreign key definitions.
     * @return
     */
    protected abstract void defineColumnsAndForeignKeys();

    public String getTableName(){
        return mTableName;
    }

    /**
     * Key in the hash map is the column name. The value contains a Column object with all its information.
     */
    public Collection<Column> getColumns() {
        return mColumns.values();
    }

    public Collection<String> getColumnNames(){
        ArrayList<String> colNames = new ArrayList<String>();
        for(Column c : mColumns.values()){
            colNames.add(c.getFullName());
        }
        return colNames;
    }

    public Column getColumn(String colName){
        return mColumns.get(colName);
    }

    /**
     * Returns the full column name, in the form of "table.column"
     * @param colName the column name
     * @return the column name in the form of "table.column"
     */
    public String getFullColumnName(String colName){
        return mColumns.get(colName).getFullName();
    }

    /**
     * For every local column in the current getInstance, relate the column in the foreign getInstance.
     */
    public LinkedHashMap<String, ArrayList<ColumnJoin>> getForeignKeys() {
        return mForeignKeys;
    }
}
