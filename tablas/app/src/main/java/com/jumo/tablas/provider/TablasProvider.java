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
import com.jumo.tablas.provider.dao.JoinTreeNode;
import com.jumo.tablas.provider.dao.Metric;
import com.jumo.tablas.provider.dao.Table;
import com.jumo.tablas.provider.db.TablasDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Moha on 7/3/15.
 */
public class TablasProvider extends ContentProvider{
    private static final String TAG = "TablasProvider";

    //TODO: need to close database

    private static final int JOIN_GROUP_MEMBER = 1;
    private static final int JOIN_GROUP_MEMBER_USER = 2;
    private static final int JOIN_GROUP_MEMBER_USER_EXPENSE = 3;
    private static final int JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER = 4;
    private static final boolean ADD_BALANCE = true;
    private static final boolean NO_BALANCE = false;


    protected static final String PROVIDER_AUTHORITY = TablasContract.AUTHORITY;
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
    private static final int EXPENSE_USERS = 21;     //Max
    private static final int EXPENSE_PAYER_USERS = 12;
    private static final int EXPENSE_LOCATION = 13;
    private static final int EXPENSE_RECURRENCE = 14;
    private static final int PAYERS = 20;
    private static final int RECURRENCE_ID = 15;
    private static final int LOCATION_ID = 16;


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {   //TODO: possibly add a GROUP_USERS
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.User.getInstance().getTableName() + "/*", USER_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.User.getInstance().getTableName(), USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.User.getInstance().getTableName() + "/*/groups", USER_GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName(), GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName() + "/#", GROUP_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Group.getInstance().getTableName() + "/#/users", GROUP_MEMBER_USERS);
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
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Location.getInstance().getTableName() + "/#", LOCATION_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Location.getInstance().getTableName() + "/expense/#", EXPENSE_LOCATION);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Recurrence.getInstance().getTableName() + "/#", RECURRENCE_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, TablasContract.Recurrence.getInstance().getTableName() + "/recurrence/#", EXPENSE_RECURRENCE);

    }

    private TablasDatabaseHelper mDBHelper;

    public boolean onCreate(){
        mDBHelper = new TablasDatabaseHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri,
        String[] projection,
        String selection,
        String[] selectionArgs,
        String sortOrder){

        Cursor cursorResult = null;

        //Additional preparation to query
        StringBuffer newSelection = (selection == null)? new StringBuffer() : new StringBuffer(selection);
        ArrayList<String> newSelectArgs = (selectionArgs == null)? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(selectionArgs));
        ArrayList<String> newProjection = (projection == null)? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(projection));
        ArrayList<Metric> metrics = new ArrayList<Metric>();
        Metric metric = null;
        String sqlQuery = null;
        SQLiteDatabase dbConnection = mDBHelper.getReadableDatabase();

        switch(sURIMatcher.match(uri)){
            case USERS:
                cursorResult = dbConnection.query(TablasContract.User.getInstance().getTableName(),projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case USER_GROUPS: //This also adds a balance from the perspective of a user.
                appendToProjection(TablasContract.UserGroupBalance.GROUP_TABLE.getColumns(), newProjection);
                appendToFilter(newSelection, newSelectArgs, TablasContract.UserGroupBalance.MEMBER_TABLE.getColumn(TablasContract.Member.USER_ID), uri.getPathSegments().get(1)); // get the user ID from the path.

                //cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(TablasContract.Group.getInstance().getTableName(), TablasContract.Group.getInstance().getColumnNames(), projection, true, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, null), selectionArgs);
                metric = TablasContract.getBalanceMetric();
                metric.setColumn(TablasContract.Group.getInstance().getColumn(TablasContract.Group.USER_BALANCE));
                metrics.add(metric);

                sqlQuery = select(TablasContract.UserGroupBalance.getInstance(), metrics,
                        newProjection.toArray(new String[]{}), newSelection.toString(), sortOrder, false);
                //Log.d(TAG, sqlQuery);
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
                appendToProjection(TablasContract.UserExpenseBalance.EXPENSE_TABLE.getColumns(), newProjection);
                appendToFilter(newSelection, newSelectArgs,
                        TablasContract.UserExpenseBalance.USER_TABLE.getColumn(TablasContract.User._ID),
                        uri.getPathSegments().get(2)); // get the user ID from the path.
                appendToFilter(newSelection, newSelectArgs,
                        TablasContract.UserExpenseBalance.EXPENSE_TABLE.getColumn(TablasContract.Expense.GROUP_ID),
                        uri.getPathSegments().get(4)); // // get the group ID from the path.
                metric = TablasContract.getBalanceMetric();
                metric.setColumn(TablasContract.UserExpenseBalance.EXPENSE_TABLE.getColumn(TablasContract.Expense.USER_BALANCE));
                metrics.add(metric);
                sqlQuery = select(TablasContract.UserExpenseBalance.getInstance(), metrics,
                        newProjection.toArray(new String[]{}), newSelection.toString(), sortOrder, false);
                //Log.d(TAG, sqlQuery);
                cursorResult = dbConnection.rawQuery(sqlQuery, newSelectArgs.toArray(new String[]{}));
                break;

            case EXPENSE_USERS: //returns entities for both Payer and Member (so both can be read throught their respective entities
                appendToProjection(TablasContract.UserExpenseBalance.USER_TABLE.getColumns(), newProjection);
                //appendToProjection(TablasContract.UserExpenseBalance.PAYER_TABLE.getColumns(), newProjection);
                appendToFilter(newSelection, newSelectArgs,
                        TablasContract.UserExpenseBalance.PAYER_TABLE.getColumn(TablasContract.Payer.EXPENSE_ID),
                        uri.getPathSegments().get(1)); //get the expense ID
                sqlQuery = select(TablasContract.UserExpenseBalance.getInstance(), null,
                        newProjection.toArray(new String[]{}), newSelection.toString(), null, true);

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
            case USER_ID:
                return str.append(".item/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.User.getInstance().getTableName()).toString();
            case USERS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.User.getInstance().getTableName()).toString();
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
            case GROUP_MEMBER_USERS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.User.getInstance().getTableName()).toString();
            case USER_GROUP_EXPENSES:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(TablasContract.Expense.getInstance().getTableName()).toString();
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
                table = TablasContract.User.getInstance().getTableName();
                break;
            case GROUPS:
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


    private String select(CompositeTable entity, ArrayList<Metric> metrics,
                          String[] projection, String selection, String sortOrder, boolean distict){

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
        query.append("select ");
        if(distict) {
            query.append("distinct ");
        }

        query.append(columns);
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
     * They joining of multiple tables based on their foregin keys, according to the TablasContract tables's definitions.
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
