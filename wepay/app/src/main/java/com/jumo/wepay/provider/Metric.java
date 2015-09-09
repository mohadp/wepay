package com.jumo.wepay.provider;

import java.util.ArrayList;

/**
 * Created by Moha on 9/7/15.
 */
class Metric {
    private ArrayList<Column> mColumns;
    private String mExpression;
    private String mAlias;

    public Metric(ArrayList<Column> cols, String exp, String alias) {
        mExpression = exp;
        mColumns = cols;
        mAlias = alias;
    }

    public String getColumnName() {
        return mAlias;
    }

    public void setColumnName(String alias) {
        this.mAlias = alias;
    }

    public String getExpression() {
        return mExpression;
    }

    public void setExpression(String mExpression) {
        this.mExpression = mExpression;
    }

    public void addDependentColumn(Column col) {
        mColumns.add(col);
    }

    public void removeDependentColumn(Column col) {
        mColumns.remove(col);
    }

    public String getColumnDefinition() {
        return mExpression + " as " + mAlias;
    }

    public ArrayList<Entity> getDependentEntities() {
        if (mColumns == null) return null;

        ArrayList<Entity> tables = new ArrayList<Entity>();

        for (Column c : mColumns) {
            tables.add(WepayContract.getEntity(c.table));
        }
        return tables;
    }
}
