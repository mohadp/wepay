package com.jumo.tablas.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.jumo.tablas.provider.dao.Column;
import com.jumo.tablas.provider.dao.ColumnJoin;
import com.jumo.tablas.provider.dao.CompositeTable;
import com.jumo.tablas.provider.dao.TreeNode;
import com.jumo.tablas.provider.dao.Metric;
import com.jumo.tablas.provider.db.TablasDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Moha on 7/3/15.
 */
public class TablasProvider extends ContentProvider{
    private static final String TAG = "TablasProvider";

    //TODO: need to close database

    protected static final String PROVIDER_AUTHORITY = TablasContract.AUTHORITY;

    private static final int GROUPS = 1;
    private static final int MEMBERS = 2;
    private static final int EXPENSES = 3;
    private static final int PAYERS = 4;
    private static final int COUNTRY = 5;
    private static final int CURRENCY = 6;
    private static final int EXCHANGE_RATE = 7;
    private static final int GROUP_BALANCE = 100;
    private static final int EXPENSE_BALANCE = 101;
    private static final int COUNTRY_CURRENCIES = 102;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName(), GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Member.getInstance().getTableName(), MEMBERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Expense.getInstance().getTableName(), EXPENSES);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Payer.getInstance().getTableName(), PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Country.getInstance().getTableName(), COUNTRY);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Currency.getInstance().getTableName(), CURRENCY);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.ExchangeRate.getInstance().getTableName(), EXCHANGE_RATE);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Compound.GroupBalance.getInstance().getTableName(), GROUP_BALANCE);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Compound.ExpenseBalance.getInstance().getTableName(), EXPENSE_BALANCE);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Compound.CountryCurrency.getInstance().getTableName(), COUNTRY_CURRENCIES);
    }

    private TablasDatabaseHelper mDBHelper;

    public boolean onCreate(){
        mDBHelper = new TablasDatabaseHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

        SQLiteDatabase dbConnection = mDBHelper.getReadableDatabase();
        Cursor cursorResult = null;
        List<String> uriPaths = uri.getPathSegments();

        int matcher = sURIMatcher.match(uri);
        if(matcher >= 0 && matcher < 100){
            cursorResult = dbConnection.query(uriPaths.get(0), projection, selection /*filter*/,
                    selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
        }else if(matcher >= 100){
            CompositeTable compositeTable = (CompositeTable)TablasContract.getTable(uriPaths.get(0));
            String sqlQuery = select(compositeTable, projection, selection, sortOrder, false);
            cursorResult = dbConnection.rawQuery(sqlQuery, selectionArgs);
        }
        //dbConnection.close();

        return cursorResult;
    }

    public String getType(Uri uri){
        List<String> uriPaths = uri.getPathSegments();
        StringBuilder str = new StringBuilder("vnd.android.cursor");
        String uriType = null;

        int matcher = sURIMatcher.match(uri);
        if(matcher >= 0){ //directory types for all URIs, for now.
            str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(uriPaths.get(0));
            uriType = str.toString();
        }

        return uriType;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs){
        String table = uri.getPathSegments().get(0);

        int matcher = sURIMatcher.match(uri);
        int deletedId = 0;

        if(matcher >= 0 && matcher < 100) {
            deletedId = mDBHelper.getWritableDatabase().delete(table, selection, selectionArgs);
        }
        return deletedId;
    }

    public Uri insert(Uri uri, ContentValues values){
        String table = uri.getPathSegments().get(0);

        int matcher = sURIMatcher.match(uri);
        Uri insertedUri = null;

        if(matcher >= 0 && matcher < 100){
            insertedUri = ContentUris.withAppendedId(uri, mDBHelper.getWritableDatabase().insert(table, null, values));
        }
        return insertedUri;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        String table = uri.getPathSegments().get(0);

        int matcher = sURIMatcher.match(uri);
        int affectedRows = 0;

        if(matcher >= 0 && matcher < 100) {
            affectedRows = mDBHelper.getWritableDatabase().update(table, values, selection, selectionArgs);
        }
        return affectedRows;
    }

    private String select(CompositeTable entity, String[] projection, String selection, String sortOrder, boolean distict){

        StringBuffer projectionStrBuffer = new StringBuffer();
        StringBuffer groupByStrBuffer = new StringBuffer();
        getProjectionGroupBySQL(entity, projection, projectionStrBuffer, groupByStrBuffer);

        StringBuffer from = getJoinTableSQL(entity);

        //Build the final query
        StringBuffer query = new StringBuffer();
        query.append("select ");
        if(distict) {
            query.append("distinct ");
        }
        query.append(projectionStrBuffer);
        query.append(" from ").append(from);

        if(selection != null){
            query.append(" where ").append(selection);
        }
        if(groupByStrBuffer.length() > 0){
            query.append(" group by ").append(groupByStrBuffer);
        }
        if(sortOrder != null){
            query.append(" order by ").append(sortOrder);
        }

        //Log.d(TAG, query.toString());
        return query.toString();
    }

    /**
     * Return the columns in the projection[]. The columns are identified with their full name, in the form of "table.column".
     * @param table
     * @param projection
     * @param select
     * @param groupBy
     */
    private void getProjectionGroupBySQL(CompositeTable table, String[] projection, StringBuffer select, StringBuffer groupBy){

        boolean selectOneAdded = false;
        boolean groupByOneAdded = false;

        Iterator<Column> it = (projection == null)? table.getColumns().iterator() : null;
        int index = 0;

        while((it != null && it.hasNext()) || (projection != null && index < projection.length)){
            Column col = (projection == null)? it.next() : table.getColumn(projection[index]);

            int selectLengthPrev = select.length();
            int groupByLengthPrev = groupBy.length();

            addToSelectGroupBy(table, col, selectOneAdded, select, groupByOneAdded, groupBy);

            //Add all subsequent columns with comas before; if new length is different (greater), then next time, we need to add comma.
            selectOneAdded = (selectOneAdded) ? true : (select.length() > selectLengthPrev);
            groupByOneAdded = (groupByOneAdded) ? true : (groupBy.length() > groupByLengthPrev);

            index++;
        }
    }

    /**
     * Auxiliary method that adds column names to the select and groupBy StringBuffers
     * @param table composite table
     * @param col column object
     * @param selectAddComma indicates whether to add a comma before attaching new column to projection
     * @param select stringbuffer containing projection columns
     * @param groupByAddComma indiciates whether to add a comma before attaching new column to groupBy
     * @param groupBy stringbuffer containing groupBy columns
     */
    private void addToSelectGroupBy(CompositeTable table, Column col,  boolean selectAddComma, StringBuffer select, boolean groupByAddComma, StringBuffer groupBy){
        String selectCol = null;
        String groupByCol = null;

        if(!col.metric) {
            //Supporting aliases (if alias is defined in column, add alias; else just the column).
            selectCol = col.getFullName() + ((col.alias == null)? "" : (" as " + col.alias));  //Todo: Instead of concat strings, I could use StringBuffer here
            groupByCol = (col.alias == null)? col.getFullName() : col.alias;
        }else{  //metric column
            Metric metric = table.getMetric(col.name);
            selectCol = metric.getColumnDefinition();
            groupByCol = (metric.isAggregation())? null : metric.getColumnName(); //metric may not be an aggregation metric; it may be just a scalar operation based on one or more columns
        }

        select.append((selectAddComma)? ", " : "");
        select.append(selectCol);
        if(groupByCol != null) {
            groupBy.append((groupByAddComma) ? ", " : "");
            groupBy.append(groupByCol);
        }
    }

    /**
     * They joining of multiple tables based on their foregin keys, according to the TablasContract tables's definitions.
     * @param compositeEntity
     * @return
     */
    private StringBuffer getJoinTableSQL(CompositeTable compositeEntity){
        TreeNode joinTree = compositeEntity.getTableJoinTree(null);
        return traverseTree(joinTree);
    }

    /**
     * Traverses the join tree node to build the "from" clause of the select.
     * @param treeNode the top/root node of the code
     * @return
     */
    private StringBuffer traverseTree(TreeNode treeNode){
        StringBuffer sb = new StringBuffer();
        if(treeNode.getLeft() == null && treeNode.getRight() == null && treeNode.getTableName() != null){
            sb.append(treeNode.getTableName());
            if(treeNode.getTable().isAlias()){
                sb.append(" as ").append(treeNode.getTable().getAlias());
            }
            return sb;
        }else if(treeNode.getLeft() != null && treeNode.getRight() != null && treeNode.getTableName() == null){
            sb.append(traverseTree(treeNode.getLeft())).append(getJoinString(treeNode.getJoinType())).append(traverseTree(treeNode.getRight())).append(" ");
            if(treeNode.getColumnJoins() != null) {
                sb.append("on (");
                Iterator<ColumnJoin> it = treeNode.getColumnJoins().iterator();
                while (it.hasNext()) {
                    ColumnJoin jc = it.next();
                    sb.append(jc.left.getFullName()).append(" ").append(jc.operator).append(" ").append(jc.right.getFullName());
                    if (it.hasNext()) {
                        sb.append(" AND ");
                    }
                }
                sb.append(")");
            }
        }
        return sb;
    }

    private String getJoinString(int joinType){
        switch(joinType){
            case TreeNode.INNER_JOIN:
                return " join ";
            case TreeNode.LEFT_OUTER_JOIN:
                return " left outer join ";
            case TreeNode.RIGHT_OUTER_JOIN: //Not supported by SQLite
                return " join ";
            case TreeNode.FULL_OUTER_JOIN:  //Not supported by SQLite
                return " join ";
        }
        return " join ";
    }

}
