package com.jumo.tablas.provider.dao;

/**
 * Created by Moha on 9/7/15.
 */
public class Column {


    public String name;
    public String dbtype;
    public String spec;
    public String table;
    public int datatype;
    public boolean metric;

    public static final int INTERNAL_TYPE_INT = 0;
    public static final int INTERNAL_TYPE_LONG = 1;
    public static final int INTERNAL_TYPE_BOOL = 2;
    public static final int INTERNAL_TYPE_STRING = 3;
    public static final int INTERNAL_TYPE_DATE = 4;
    public static final int INTERNAL_TYPE_BYTES = 5;
    public static final int INTERNAL_TYPE_DOUBLE = 6;

    //Reusable strings to avoid using multiple instances of these strings;
    public static final String DB_TYPE_INTEGER = "integer";
    public static final String DB_TYPE_TEXT = "nvarchar(255)";
    public static final String DB_TYPE_DOUBLE = "double";
    public static final String DB_TYPE_BLOB = "blob";

    public static final String DEF_PRIMARY_KEY = "primary key";

    public static final boolean IS_METRIC = true;

    private Column(String dbType, String definition) {
        dbtype = dbType;
        spec = definition;
        metric = false;
    }

    private Column(String colName, String dbType, String definition) {
        this(dbType, definition);
        name = colName;
    }

    public Column(String tableName, String colName, String dbType, String definition) {
        this(colName, dbType, definition);
        table = tableName;
    }

    public Column(String tableName, String colName, String dbType, String definition, int internalDataType){
        this(tableName, colName, dbType, definition);
        datatype = internalDataType;
    }

    public Column(String tableName, String colName, String dbType, String definition, int internalDataType, boolean isAggregation){
        this(tableName, colName, dbType, definition, internalDataType);
        metric = isAggregation;
    }

    public String getFullName() {

        StringBuffer sb = new StringBuffer();
        if(table != null) {
            sb.append(table).append(".");
        }
        sb.append(name);
        return sb.toString();
    }
}
