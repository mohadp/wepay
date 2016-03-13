package com.jumo.tablas.provider.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class to represent eventually singleton database entities/mTables in a database (singleton part implemented by child classes). Contains set of mColumns, their spec and their foreign keys.
 */
public abstract class Table {
    private LinkedHashMap<String, Column> mColumns;
    private LinkedHashMap<String, LinkedHashSet<ColumnJoin>> mForeignKeys;
    private String mTableName;
    private String mAlias = null;


    /**
     * Generic column that all tables have as ID column.
     */
    public static final String _ID = "_id";


    protected Table(){
        mColumns = new LinkedHashMap<String, Column>();
        mForeignKeys = new LinkedHashMap<String, LinkedHashSet<ColumnJoin>>();
        defineTable();
    }

    protected Table(String table){
        mColumns = new LinkedHashMap<String, Column>();
        mForeignKeys = new LinkedHashMap<String, LinkedHashSet<ColumnJoin>>();
        mTableName = table;
        defineTable();
    }

    /**
     * Add information on mColumns and foreign keys, or any other necessary settings for the table. The table constructor will
     * call this method to properly construct the table.
     * @return
     */
    protected abstract void defineTable();

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
    public LinkedHashMap<String, LinkedHashSet<ColumnJoin>> getForeignKeys() {
        return mForeignKeys;
    }

    public LinkedHashSet<ColumnJoin> getColumnJoinsToTable(String tableName){
        return (mForeignKeys == null)? null : mForeignKeys.get(tableName);
    }

    public Set<String> getForeignTables(){
        return (mForeignKeys == null)? null : mForeignKeys.keySet();
    }

    public LinkedHashMap<String, Column> getColumnMap() {
        return mColumns;
    }

    public void addColumn(String colId, Column col){
        mColumns.put(colId, col);
    }

    public void addColumns(Map<String, Column> colMaps){
        mColumns.putAll(colMaps);
    }

    public void addForeignTable(String foreignTableName, LinkedHashSet<ColumnJoin> joinDefinition){
        mForeignKeys.put(foreignTableName, joinDefinition);
    }

    public boolean isAlias() {
        return (mAlias != null);
    }

    public String getAlias() {
        return mAlias;
    }

    public void setAlias(String mAliasName) {
        this.mAlias = mAliasName;
    }
}
