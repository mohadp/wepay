package com.jumo.wepay.provider;

/**
 * Created by Moha on 9/7/15.
 */
public class Column {
    public String name;
    public String dbtype;
    public String spec;
    public String table;
    public int datatype;
    public boolean persisted;

    public static final int TYPE_INT = 0;
    public static final int TYPE_LONG = 1;
    public static final int TYPE_BOOL = 2;
    public static final int TYPE_STRING = 3;
    public static final int TYPE_DATE = 4;
    public static final int TYPE_BYTES = 5;
    public static final int TYPE_DOUBLE = 6;



    private Column(String dbType, String definition) {
        dbtype = dbType;
        spec = definition;
        persisted = true;
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

    public Column(String tableName, String colName, String dbType, String definition, int internalDataType, boolean isPersisted){
        this(tableName, colName, dbType, definition, internalDataType);
        persisted = isPersisted;
    }

    public String getFullName() {
        StringBuffer sb = (new StringBuffer(table)).append(".").append(name);
        return sb.toString();
    }
}
