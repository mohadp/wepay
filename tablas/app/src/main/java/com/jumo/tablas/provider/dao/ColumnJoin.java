package com.jumo.tablas.provider.dao;

/**
 * Created by Moha on 9/7/15.
 */
public class ColumnJoin {
    public Column left;
    public Column right;
    public String operator;

    public ColumnJoin(Column first, Column second) {
        left = first;
        right = second;
        operator = "=";
    }

    public boolean containsTable(String table){
        if(left.table.equals(table))
            return true;
        if(right.table.equals(table))
            return true;

        return false;
    }
}
