package com.jumo.tablas.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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

/**
 * Created by Moha on 7/3/15.
 */
public class TablasProvider extends ContentProvider{
    private static final String TAG = "TablasProvider";

    //TODO: need to close database

    protected static final String PROVIDER_AUTHORITY = TablasContract.AUTHORITY;
    //private static final int USER_ID = 1;
    private static final int USERS = 18;
    private static final int USER_GROUPS = 2;
    private static final int GROUPS = 3;
    private static final int GROUP_ID = 4;
    //private static final int GROUP_MEMBER_USERS = 6;
    private static final int USER_GROUP_EXPENSES = 7;
    private static final int GROUP_EXPENSE_PAYERS = 8;
    private static final int GROUP_PAYERS = 17;
    private static final int MEMBER_ID = 9;
    private static final int MEMBERS = 19;
    private static final int MEMBER_USER = 10;
    private static final int EXPENSE_ID = 11;
    private static final int EXPENSES = 5;
    private static final int EXPENSE_USERS = 21;     //Max
    private static final int EXPENSE_PAYER_USERS = 12;
    //private static final int EXPENSE_LOCATION = 13;
    //private static final int EXPENSE_RECURRENCE = 14;
    private static final int PAYERS = 20;
    //private static final int RECURRENCE_ID = 15;
    //private static final int LOCATION_ID = 16;


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        //sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.User.getInstance().getTableName() + "/*", USER_ID);
        //sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.User.getInstance().getTableName(), USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName() + "/user/*", USER_GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName(), GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName() + "/#", GROUP_ID);
        //sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName() + "/#/users", GROUP_MEMBER_USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName() + "/#/expense/#/payers", GROUP_EXPENSE_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName() + "/#/payers", GROUP_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Member.getInstance().getTableName() + "/#", MEMBER_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Member.getInstance().getTableName(), MEMBERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Member.getInstance().getTableName() + "/#/users", MEMBER_USER);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Expense.getInstance().getTableName() + "/#", EXPENSE_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Expense.getInstance().getTableName() + "/#/users", EXPENSE_USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Expense.getInstance().getTableName() + "/user/*/group/#", USER_GROUP_EXPENSES);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Expense.getInstance().getTableName(), EXPENSES);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Payer.getInstance().getTableName() + "/expense/#", EXPENSE_PAYER_USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Payer.getInstance().getTableName(), PAYERS);
        //sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Location.getInstance().getTableName() + "/#", LOCATION_ID);
        //sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Location.getInstance().getTableName() + "/expense/#", EXPENSE_LOCATION);
        //sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Recurrence.getInstance().getTableName() + "/#", RECURRENCE_ID);
        //sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Recurrence.getInstance().getTableName() + "/recurrence/#", EXPENSE_RECURRENCE);

    }

    private TablasDatabaseHelper mDBHelper;

    public boolean onCreate(){
        mDBHelper = new TablasDatabaseHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

        Cursor cursorResult = null;

        //Additional preparation to query
        StringBuffer newSelection = (selection == null)? new StringBuffer() : new StringBuffer(selection);
        ArrayList<String> newSelectArgs = (selectionArgs == null)? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(selectionArgs));
        ArrayList<String> newProjection = (projection == null)? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(projection));
        String sqlQuery = null;
        String[] defaultProjection = null;
        CompositeTable compositeTable;
        SQLiteDatabase dbConnection = mDBHelper.getReadableDatabase();

        switch(sURIMatcher.match(uri)){
            //case USERS:
            //    cursorResult = dbConnection.query(TablasContract.User.getInstance().getTableName(),projection, selection /*filter*/,
            //            selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
            //    break;

            case USER_GROUPS: //This also adds a balance from the perspective of a user.
                compositeTable = TablasContract.Compound.GroupBalance.getInstance();

                defaultProjection = new String[] { TablasContract.Compound.GroupBalance.GROUP_ID, TablasContract.Compound.GroupBalance.GROUP_CREATED_ON,
                        TablasContract.Compound.GroupBalance.GROUP_NAME, TablasContract.Compound.GroupBalance.GROUP_PICTURE,
                        TablasContract.Compound.GroupBalance.USER_BALANCE };
                newProjection.addAll(Arrays.asList(defaultProjection));

                appendToFilter(newSelection, newSelectArgs, compositeTable.getColumn(TablasContract.Compound.GroupBalance.MEMBER_USER_ID), uri.getPathSegments().get(2)); // get the user ID from the path.
                sqlQuery = select(compositeTable, newProjection.toArray(new String[]{}), newSelection.toString(), sortOrder, false);

                cursorResult = dbConnection.rawQuery(sqlQuery, newSelectArgs.toArray(new String[]{}));
                break;

            case GROUPS:
                cursorResult = dbConnection.query(TablasContract.Group.getInstance().getTableName(),projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case MEMBERS:
                cursorResult = dbConnection.query(TablasContract.Member.getInstance().getTableName(), projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case EXPENSES:
                cursorResult = dbConnection.query(TablasContract.Expense.getInstance().getTableName(), projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case PAYERS:
                cursorResult = dbConnection.query(TablasContract.Payer.getInstance().getTableName(), projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case USER_GROUP_EXPENSES:
                compositeTable = TablasContract.Compound.ExpenseBalance.getInstance();

                defaultProjection = new String[]{ TablasContract.Compound.ExpenseBalance.EXPENSE_ID, TablasContract.Compound.ExpenseBalance.EXPENSE_AMOUNT,
                        TablasContract.Compound.ExpenseBalance.EXPENSE_CATEGORY_ID, TablasContract.Compound.ExpenseBalance.EXPENSE_CREATED_ON,
                        TablasContract.Compound.ExpenseBalance.EXPENSE_CURRENCY, TablasContract.Compound.ExpenseBalance.EXPENSE_EXCHANGE_RATE,
                        TablasContract.Compound.ExpenseBalance.EXPENSE_GROUP_EXPENSE_ID, TablasContract.Compound.ExpenseBalance.EXPENSE_IS_PAYMENT,
                        TablasContract.Compound.ExpenseBalance.EXPENSE_LATITUDE, TablasContract.Compound.ExpenseBalance.EXPENSE_LONGITUDE,
                        TablasContract.Compound.ExpenseBalance.EXPENSE_MESSAGE, TablasContract.Compound.ExpenseBalance.EXPENSE_OFFSET,
                        TablasContract.Compound.ExpenseBalance.EXPENSE_PERIODICITY, TablasContract.Compound.ExpenseBalance.EXPENSE_GROUP_ID,
                        TablasContract.Compound.ExpenseBalance.USER_BALANCE };
                newProjection.addAll(Arrays.asList(defaultProjection));

                appendToFilter(newSelection, newSelectArgs, compositeTable.getColumn(TablasContract.Compound.ExpenseBalance.MEMBER_USER_ID), uri.getPathSegments().get(2)); // get the user ID from the path.
                appendToFilter(newSelection, newSelectArgs, compositeTable.getColumn(TablasContract.Compound.ExpenseBalance.EXPENSE_GROUP_ID), uri.getPathSegments().get(4)); // // get the group ID from the path.
                sqlQuery = select(TablasContract.Compound.ExpenseBalance.getInstance(), newProjection.toArray(new String[]{}), newSelection.toString(), sortOrder, false);
                //Log.d(TAG, sqlQuery);
                cursorResult = dbConnection.rawQuery(sqlQuery, newSelectArgs.toArray(new String[]{}));
                break;

            case EXPENSE_USERS: //returns entities for both Payer and Member (so both can be read throught their respective entities
                compositeTable = TablasContract.Compound.ExpenseBalance.getInstance();

                defaultProjection = new String[] { TablasContract.Compound.ExpenseBalance.MEMBER_ID, TablasContract.Compound.ExpenseBalance.MEMBER_GROUP_ID,
                        TablasContract.Compound.ExpenseBalance.MEMBER_USER_ID, TablasContract.Compound.ExpenseBalance.MEMBER_IS_ADMIN,
                        TablasContract.Compound.ExpenseBalance.MEMBER_LEFT_GROUP};
                newProjection.addAll(Arrays.asList(defaultProjection));

                appendToFilter(newSelection, newSelectArgs, compositeTable.getColumn(TablasContract.Compound.ExpenseBalance.PAYER_EXPENSE_ID), uri.getPathSegments().get(1)); //get the expense ID
                sqlQuery = select(TablasContract.Compound.ExpenseBalance.getInstance(), newProjection.toArray(new String[]{}), newSelection.toString(), null, true);
                //Log.d(TAG, sqlQuery);
                cursorResult = dbConnection.rawQuery(sqlQuery, newSelectArgs.toArray(new String[]{}));
                break;

            default:
                dbConnection.close();
        }

        return cursorResult;
    }




    public String getType(Uri uri){
        StringBuilder str = new StringBuilder("vnd.android.cursor");
        switch(sURIMatcher.match(uri)){
            /*case USER_ID:
                return str.append(".item/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.User.getInstance().getTableName()).toString();
            case USERS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.User.getInstance().getTableName()).toString();*/
            case USER_GROUPS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.Group.getInstance().getTableName()).toString();
            case GROUPS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.Group.getInstance().getTableName()).toString();
            case MEMBERS:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.Member.getInstance().getTableName()).toString();
            case EXPENSES:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.Expense.getInstance().getTableName()).toString();
            case PAYERS:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.Payer.getInstance().getTableName()).toString();
            /*case GROUP_MEMBER_USERS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.User.getInstance().getTableName()).toString();*/
            case USER_GROUP_EXPENSES:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.Expense.getInstance().getTableName()).toString();
            case EXPENSE_USERS:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.Member.getInstance().getTableName()).toString();
        }

        return null;
    }



    public int delete(Uri uri, String selection, String[] selectionArgs){
        String table = tableToModify(uri);

        if(table == null)
            return 0;
        else
            return mDBHelper.getWritableDatabase().delete(table, selection, selectionArgs);
    }


    public Uri insert(Uri uri, ContentValues values){
        String table = tableToModify(uri);

        if(table == null)
            return null;
        else {
            return ContentUris.withAppendedId(uri, mDBHelper.getWritableDatabase().insert(table, null, values));
        }
    }


    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        String table = tableToModify(uri);

        if(table == null)
            return 0;
        else
            return mDBHelper.getWritableDatabase().update(table, values, selection, selectionArgs);
    }

    private String tableToModify(Uri uri){
        String table = null;

        switch(sURIMatcher.match(uri)){
            /*case USERS:
                table = TablasContract.User.getInstance().getTableName();
                break;
            */case GROUPS:
                table = TablasContract.Group.getInstance().getTableName();
                break;
            case MEMBERS:
                table = TablasContract.Member.getInstance().getTableName();
                break;
            case EXPENSES:
                table = TablasContract.Expense.getInstance().getTableName();
                break;
            case PAYERS:
                table = TablasContract.Payer.getInstance().getTableName();
                break;
        }
        return table;
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
     * Adds a new selection condition to the selection text in the form of "[...] AND col = ?" with
     * the new value added to the oritinalParams
     * @param originalSelection
     * @param originalParams
     * @param col
     * @param value
     */
    private void appendToFilter(StringBuffer originalSelection, ArrayList<String> originalParams, Column col, String value){
        if(originalParams.size() > 0){
            originalSelection.append(" AND ");
        }
        originalSelection.append(col.getFullName()).append(" = ?");
        originalParams.add(value);
    }

    private void appendToProjection(Collection<Column> columns, ArrayList<String> originalProjection){
        for(Column c : columns){
            if(!c.metric){
                originalProjection.add(c.getFullName());
            }
        }
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
            selectCol = col.getFullName();
            groupByCol = col.getFullName();
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
            return sb.append(treeNode.getTableName());
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
