package com.jumo.tablas.provider;

import java.util.ArrayList;

/**
 * Created by Moha on 9/7/15.
 */
class Metric {
    private ArrayList<Column> mDependedColumns;
    private String mExpression;
    private Column mColumn;

    public Metric(ArrayList<Column> cols, String exp) {
        mExpression = exp;
        mDependedColumns = cols;
    }

    public Metric(ArrayList<Column> cols, String exp, Column col){
        this(cols, exp);
        mColumn = col;
    }

    public String getColumnName() {
        return (mColumn == null)? "unNamedMetric" : mColumn.name;
    }

    public String getExpression() {
        return mExpression;
    }

    public void setExpression(String mExpression) {
        this.mExpression = mExpression;
    }

    public void addDependentColumn(Column col) {
        mDependedColumns.add(col);
    }

    public void removeDependentColumn(Column col) {
        mDependedColumns.remove(col);
    }

    public String getColumnDefinition() {
        StringBuilder sb = new StringBuilder();
        sb.append(mExpression).append(" as ")
                .append("\"").append(getColumnName()).append("\"");

        return sb.toString();
    }

    public ArrayList<Table> getDependentEntities() {
        if (mDependedColumns == null) return null;

        ArrayList<Table> tables = new ArrayList<Table>();

        for (Column c : mDependedColumns) {
            tables.add(WepayContract.getTable(c.table));
        }
        return tables;
    }

    public Column getColumn() {
        return mColumn;
    }

    public void setColumn(Column mColumn) {
        this.mColumn = mColumn;
    }
}
