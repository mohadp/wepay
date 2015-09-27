package com.jumo.tablas.provider;

/**
 * Created by Moha on 9/7/15.
 */
class ColumnJoin {
    protected Column left;
    protected Column right;
    protected String operator;

    public ColumnJoin(Column first, Column second) {
        left = first;
        right = second;
        operator = "=";
    }
}
