package com.jumo.wepay.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Moha on 7/3/15.
 */
public class WepayProvider extends ContentProvider{
    private static final String TAG = "WepayProvider";

    //TODO: need to close database

    private static final int JOIN_GROUP_MEMBER = 1;
    private static final int JOIN_GROUP_MEMBER_USER = 2;
    private static final int JOIN_GROUP_MEMBER_USER_EXPENSE = 3;
    private static final int JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER = 4;
    private static final boolean ADD_BALANCE = true;
    private static final boolean NO_BALANCE = false;


    protected static final String PROVIDER_AUTHORITY = "com.jumo.wepay.provider";
    private static final int USER_ID = 1;
    private static final int USERS = 18;
    private static final int USER_GROUPS = 2;
    private static final int GROUPS = 3;
    private static final int GROUP_ID = 4;
    private static final int GROUP_MEMBER_USERS = 6;
    private static final int USER_GROUP_EXPENSES = 7;
    private static final int GROUP_EXPENSE_PAYERS = 8;
    private static final int GROUP_PAYERS = 17;
    private static final int MEMBER_ID = 9;
    private static final int MEMBERS = 19;
    private static final int MEMBER_USER = 10;
    private static final int EXPENSE_ID = 11;
    private static final int EXPENSES = 5;
    private static final int EXPENSE_PAYER_USERS = 12;
    private static final int EXPENSE_LOCATION = 13;
    private static final int EXPENSE_RECURRENCE = 14;
    private static final int PAYERS = 20;       //Max
    private static final int RECURRENCE_ID = 15;
    private static final int LOCATION_ID = 16;


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {   //TODO: possibly add a GROUP_USERS
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.getInstance().getTableName() + "/*", USER_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.getInstance().getTableName(), USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.getInstance().getTableName() + "/*/groups", USER_GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.getInstance().getTableName(), GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.getInstance().getTableName() + "/#", GROUP_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.getInstance().getTableName() + "/#/users", GROUP_MEMBER_USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.getInstance().getTableName() + "/#/expense/#/payers", GROUP_EXPENSE_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.getInstance().getTableName() + "/#/payers", GROUP_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Member.getInstance().getTableName() + "/#", MEMBER_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Member.getInstance().getTableName(), MEMBERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Member.getInstance().getTableName() + "/#/users", MEMBER_USER);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.getInstance().getTableName() + "/#", EXPENSE_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.getInstance().getTableName() + "/user/*/group/#", USER_GROUP_EXPENSES);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.getInstance().getTableName(), EXPENSES);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Payer.getInstance().getTableName() + "/expense/#", EXPENSE_PAYER_USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Payer.getInstance().getTableName(), PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Location.getInstance().getTableName() + "/#", LOCATION_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Location.getInstance().getTableName() + "/expense/#", EXPENSE_LOCATION);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Recurrence.getInstance().getTableName() + "/#", RECURRENCE_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Recurrence.getInstance().getTableName() + "/recurrence/#", EXPENSE_RECURRENCE);

    }

    private WepayDatabaseHelper mDBHelper;

    public boolean onCreate(){
        mDBHelper = new WepayDatabaseHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri,
        String[] projection,
        String selection,
        String[] selectionArgs,
        String sortOrder){

        String userId, groupId, expenseId;
        Cursor cursorResult = null;

        //Additional preparation to query
        StringBuffer newSelection = (selection == null)? new StringBuffer() : new StringBuffer(selection);
        ArrayList<String> newSelectArgs = (selectionArgs == null)? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(selectionArgs));
        ArrayList<String> newProjection = (projection == null)? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(projection));
        ArrayList<Metric> metrics = new ArrayList<Metric>();
        Metric metric = null;

        switch(sURIMatcher.match(uri)){
            case USER_ID:
                userId = uri.getPathSegments().get(1); // get the user ID from the path.
                //cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.User.getInstance().getTableName(), WepayContract.User.getInstance().getColumnNames(), projection, ADD_BALANCE, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, null), selectionArgs);
                break;

            case USERS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.User.getInstance().getTableName(),projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case USER_GROUPS: //This also adds a balance from the perspective of a user.
                appendToProjection(WepayContract.UserGroupBalance.GROUP_TABLE.getColumns(), newProjection);
                appendToFilter(newSelection, newSelectArgs, WepayContract.User.getInstance().getColumn(WepayContract.User._ID), uri.getPathSegments().get(1)); // get the user ID from the path.

                //cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.Group.getInstance().getTableName(), WepayContract.Group.getInstance().getColumnNames(), projection, true, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, null), selectionArgs);
                metric = WepayContract.getBalanceMetric();
                metric.setColumn(WepayContract.Group.getInstance().getColumn(WepayContract.Group.USER_BALANCE));
                metrics.add(metric);
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(select(WepayContract.UserGroupBalance.getInstance(), metrics, newProjection.toArray(new String[]{}), newSelection.toString(), sortOrder), newSelectArgs.toArray(new String[]{}));
                break;

            case GROUPS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.Group.getInstance().getTableName(),projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case MEMBERS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.Member.getInstance().getTableName(), projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case EXPENSES:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.Expense.getInstance().getTableName(), projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case PAYERS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.Payer.getInstance().getTableName(), projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case GROUP_MEMBER_USERS:
                groupId = uri.getPathSegments().get(1); // get the group ID from the path.
                //cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.User.getInstance().getTableName(), WepayContract.User.getInstance().getColumnNames(), projection, NO_BALANCE, JOIN_GROUP_MEMBER_USER, selection, sortOrder, null, groupId), selectionArgs);
                break;

            case USER_GROUP_EXPENSES:
                appendToProjection(WepayContract.UserExpenseBalance.EXPENSE_TABLE.getColumns(), newProjection);
                appendToFilter(newSelection, newSelectArgs, WepayContract.UserExpenseBalance.USER_TABLE.getColumn(WepayContract.User._ID), uri.getPathSegments().get(2)); // get the user ID from the path.
                appendToFilter(newSelection, newSelectArgs, WepayContract.UserExpenseBalance.EXPENSE_TABLE.getColumn(WepayContract.Expense.GROUP_ID), uri.getPathSegments().get(4)); // // get the group ID from the path.
                metric = WepayContract.getBalanceMetric();
                metric.setColumn(WepayContract.UserExpenseBalance.EXPENSE_TABLE.getColumn(WepayContract.Expense.USER_BALANCE));
                metrics.add(metric);

                //cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.Expense.getInstance().getTableName(), WepayContract.Expense.getInstance().getColumnNames(), projection, ADD_BALANCE, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, groupId), selectionArgs);
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(select(WepayContract.UserExpenseBalance.getInstance(), metrics, newProjection.toArray(new String[]{}), newSelection.toString(), sortOrder), newSelectArgs.toArray(new String[]{}));
                break;

            case EXPENSE_PAYER_USERS:
                expenseId = uri.getPathSegments().get(2);
                //cursorResult = mDBHelper.getReadableDatabase().
                break;
        }
        return cursorResult;
    }



    public String getType(Uri uri){
        StringBuilder str = new StringBuilder("vnd.android.cursor");
        switch(sURIMatcher.match(uri)){
            case USER_ID:
                return str.append(".item/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.User.getInstance().getTableName()).toString();
            case USERS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.User.getInstance().getTableName()).toString();
            case USER_GROUPS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Group.getInstance().getTableName()).toString();
            case GROUPS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Group.getInstance().getTableName()).toString();
            case MEMBERS:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Member.getInstance().getTableName()).toString();
            case EXPENSES:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Expense.getInstance().getTableName()).toString();
            case PAYERS:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Payer.getInstance().getTableName()).toString();
            case GROUP_MEMBER_USERS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.User.getInstance().getTableName()).toString();
            case USER_GROUP_EXPENSES:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Expense.getInstance().getTableName()).toString();
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
            case USERS:
                table = WepayContract.User.getInstance().getTableName();
                break;
            case GROUPS:
                table = WepayContract.Group.getInstance().getTableName();
                break;
            case MEMBERS:
                table = WepayContract.Member.getInstance().getTableName();
                break;
            case EXPENSES:
                table = WepayContract.Expense.getInstance().getTableName();
                break;
            case PAYERS:
                table = WepayContract.Payer.getInstance().getTableName();
                break;
        }
        return table;
    }


    private String select(CompositeTable entity, ArrayList<Metric> metrics,
                          String[] projection, String selection, String sortOrder){

        StringBuffer columns = getProjectionSQL(entity, projection);
        StringBuffer metricCols = null;
        HashSet<Table> metricTables = null;

        //Get set of tables needed for metric calculation, and form the metric expressions
        if(metrics != null) {
            metricCols = new StringBuffer();
            metricTables = new HashSet<Table>();
            Iterator<Metric> it = metrics.iterator();
            while (it.hasNext()) {
                Metric m = it.next();
                metricTables.addAll(m.getDependentEntities());
                metricCols.append(m.getColumnDefinition());
                if (it.hasNext()) {
                    metricCols.append(", ");
                }
            }
        }

        StringBuffer from = getJoinTableSQL(entity, metricTables);

        //Build the final query
        StringBuffer query = new StringBuffer();
        query.append("select ").append(columns);
        if(metricCols != null) {
            query.append(", ").append(metricCols);
        }
        query.append(" from ").append(from);

        if(selection != null){
            query.append(" where ").append(selection);
        }
        if(metrics != null && metrics.size() > 0){
            query.append(" group by ").append(columns);
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
            if(c.persisted){
                originalProjection.add(c.getFullName());
            }
        }
    }


    /**
     * Return a StringBuffer with columns to include in a select statement. If the projection[] array is empty,
     * then return the full set of columns present in the entity. Else, just return the columns in the
     * projection[]. The columns are identified with their full name, in the form of "table.column".
     * @param entity
     * @param projection
     * @return
     */
    private StringBuffer getProjectionSQL(CompositeTable entity, String[] projection){
        StringBuffer select = new StringBuffer();
        if(projection == null){
            Iterator<Column> it = entity.getColumns().iterator();
            while(it.hasNext()){
                Column col = it.next();
                if(col.persisted) {
                    select.append(col.getFullName());
                    if(it.hasNext()){
                        select.append(", ");
                    }
                }

            }
        }else{
            int maxIndex = projection.length - 1;
            for(int i = 0; i < projection.length; i++){
                select.append(projection[i]);
                if(i < maxIndex) {
                    select.append(", ");
                }
            }
        }
        return select;
    }

    /**
     * They joining of multiple tables based on their foregin keys, according to the WepayContract tables's definitions.
     * @param compositeEntity
     * @return
     */
    private StringBuffer getJoinTableSQL(CompositeTable compositeEntity, HashSet<Table> additionalTables){
        JoinTreeNode joinTree = compositeEntity.getTableJoinTree(additionalTables);
        return traverseTree(joinTree);
    }

    private StringBuffer traverseTree(JoinTreeNode treeNode){
        StringBuffer sb = new StringBuffer();
        if(treeNode.getLeft() == null && treeNode.getRight() == null && treeNode.getTableName() != null){
            return sb.append(treeNode.getTableName());
        }else if(treeNode.getLeft() != null && treeNode.getRight() != null && treeNode.getTableName() == null){
            sb.append(traverseTree(treeNode.getLeft())).append(" join ").append(traverseTree(treeNode.getRight()));
            sb.append(" on (");

            Iterator<ColumnJoin> it = treeNode.getColumnJoins().iterator();
            while(it.hasNext()){
                ColumnJoin jc = it.next();
                sb.append(jc.left.getFullName()).append(" ").append(jc.operator).append(" ").append(jc.right.getFullName());
                if(it.hasNext()){
                    sb.append(" AND ");
                }
            }
            sb.append(")");
        }
        return sb;
    }

}
