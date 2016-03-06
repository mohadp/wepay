package com.jumo.tablas.provider.dao;

import com.jumo.tablas.provider.TablasContract;

import java.util.ArrayList;

/**
 * Created by Moha on 9/7/15.
 */
public class Metric {
    /**
     * Marks whether the metric needs a group-by clause or not; aggregation = true means the metric aggregatis multiple rows.
     */
    public static final boolean IS_AGGREGATION = true;

    private ArrayList<Column> mDependedColumns;
    private String mExpression;
    final private Column mColumn; //we do not want to change the column related to this metric
    private boolean mAggregation;



    /*public Metric(ArrayList<Column> cols, String exp) {
        mExpression = exp;
        mDependedColumns = cols;
    }*/

    /**
     * Instantiates a metric object.
     * @param cols the set of columns that this metric depends on
     * @param exp the string that represents the metric's expression.
     * @param isAggregation indicates whether metric is an aggregation metric (true) or not (false).
     * @param col the column in a table that will represent this metric; the column's metric property will be set to true.
     */
    public Metric(ArrayList<Column> cols, String exp, boolean isAggregation, Column col){
        mExpression = exp;
        mDependedColumns = cols;
        mColumn = col;
        mAggregation = isAggregation;

        //make sure the column is a metric column
        col.metric = true;

    }

    public String getColumnName() {
        return (mColumn == null)? null : mColumn.name;
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

    public ArrayList<Table> getDependentTables() {
        if (mDependedColumns == null) return null;

        ArrayList<Table> tables = new ArrayList<Table>();

        for (Column c : mDependedColumns) {
            tables.add(TablasContract.getTable(c.table));
        }
        return tables;
    }

    public Column getColumn() {
        return mColumn;
    }


    public void setName(String colName){
        if(mColumn != null){
            mColumn.name = colName;
        }
    }

    public boolean isAggregation() {
        return mAggregation;
    }

    public void setAggregation(boolean aggregation) {
        this.mAggregation = aggregation;
    }
}
