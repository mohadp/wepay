package com.jumo.wepay.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.jumo.wepay.provider.dao.EntityCursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.User.getInstance().getTableName(), WepayContract.User.getInstance().getColumnNames(), projection, ADD_BALANCE, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, null), selectionArgs);
                break;

            case USERS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.User.getInstance().getTableName(),projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case USER_GROUPS: //This also adds a balance from the perspective of a user.
                appendToProjection(WepayContract.Group.getInstance().getColumns(), newProjection);
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
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.User.getInstance().getTableName(), WepayContract.User.getInstance().getColumnNames(), projection, NO_BALANCE, JOIN_GROUP_MEMBER_USER, selection, sortOrder, null, groupId), selectionArgs);
                break;

            case USER_GROUP_EXPENSES:
                appendToProjection(WepayContract.Expense.getInstance().getColumns(), newProjection);
                appendToFilter(newSelection, newSelectArgs, WepayContract.User.getInstance().getColumn(WepayContract.User._ID), uri.getPathSegments().get(2)); // get the user ID from the path.
                appendToFilter(newSelection, newSelectArgs, WepayContract.Group.getInstance().getColumn(WepayContract.Group._ID), uri.getPathSegments().get(4)); // // get the group ID from the path.
                metric = WepayContract.getBalanceMetric();
                metric.setColumn(WepayContract.Expense.getInstance().getColumn(WepayContract.Expense.USER_BALANCE));
                metrics.add(metric);

                //cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.Expense.getInstance().getTableName(), WepayContract.Expense.getInstance().getColumnNames(), projection, ADD_BALANCE, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, groupId), selectionArgs);
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(select(WepayContract.UserExpenseBalance.getInstance(), metrics, projection, newSelection.toString(), sortOrder), newSelectArgs.toArray(new String[]{}));
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


        Log.d(TAG, query.toString());
        return query.toString();
    }


    /**
     *
     * @param table is the getInstance for the table being retrieved
     * @param tableCols is the set of mColumns of the getInstance for which the table is being retrieved
     * @param projection determines the set of mColumns to select from a particular table getInstance
     * @param addBalance determines whether to include balance grouped by whether column defined in the projection
     * @param filter filter specification
     * @param sortOrder sort specification
     * @param tableJoins is an integer representing the mTables to be joined; use the static members JOIN_*
     * @param userIdFilter represents the user ID to be included in the where clause
     * @param groupIdFilter represents the group ID to be included in the where clause
     * @return a full SQL select query with all joins and filters based on passed on parameters
     */
    private String selectEntity(String table, Collection<String> tableCols, String[] projection, boolean addBalance, int tableJoins, String filter, String sortOrder,  String userIdFilter, String groupIdFilter){
        //// SELECT ////
        StringBuffer sb = new StringBuffer("select ");
        String[] newProjection;

        //add getInstance prefix to projection mColumns
        if(projection != null) {
            newProjection = projection;
        }else{
            newProjection = tableCols.toArray(new String[0]);
        }
        sb.append(SQLGenerator.addTablePrefixToProjection(table, newProjection));
        if(addBalance) sb.append(", ").append(addBalanceSum());

        //// FROM ////
        //build the join path; from group, join to expense, and then to payer.
        sb.append(" from ").append(joinGroupMemberUserExpensePayer(tableJoins));

        //// WHERE ////
        sb.append(" where ");
        if(filter != null ){
            sb.append(SQLGenerator.addTablePrefixToString(table, tableCols, filter.toLowerCase()));

            if(userIdFilter != null || groupIdFilter != null) sb.append(" and ");
        }

        //Add the condition on UserCursor Name
        if(userIdFilter != null){
            sb.append("(").append(WepayContract.User.getInstance().getTableName()).append(".").append(WepayContract.User._ID);
            sb.append(" = '").append(userIdFilter).append("')");
            if(groupIdFilter != null) sb.append(" and ");
        }
        //Add the condition on GroupCursor Id
        if(groupIdFilter != null){
            sb.append("(").append(WepayContract.Group.getInstance().getTableName()).append(".").append(WepayContract.Group._ID);
            sb.append(" = ").append(groupIdFilter).append(")");
        }

        //// GROUP BY ////
        if(addBalance){
            sb.append(" group by ");
            sb.append(SQLGenerator.addTablePrefixToGroupBy(table, newProjection));
        }

        //// SORT BY ////
        if(sortOrder != null) {
            sb.append(" order by ");
            sb.append(SQLGenerator.addTablePrefixToString(table, tableCols, sortOrder.toLowerCase()));
        }

        return sb.toString();
    }


    private StringBuffer addBalanceSum(){
        StringBuffer sb = new StringBuffer("sum(");
        sb.append(WepayContract.Expense.getInstance().getTableName()).append(".").append(WepayContract.Expense.AMOUNT).
                append(" * ").
				append(WepayContract.Expense.getInstance().getTableName()).append(".").append(WepayContract.Expense.EXCHANGE_RATE).
				append(" * ").
                append(WepayContract.Payer.getInstance().getTableName()).append(".").append(WepayContract.Payer.PERCENTAGE).
                append(") as ").append(WepayContract.USER_BALANCE);
        return sb;
    }

    /**
     * Returns a join string for the mTables to join. If 1 is entered, then join only GroupCursor and MemberCursor; 2 joins GroupCursor, MemberCursor and UserCursor.
     * 3 joins up to ExpenseCursor, and 4 up to PayerCursor.
     * @param whichToJoin
     * @return
     */
    private StringBuffer joinGroupMemberUserExpensePayer(int whichToJoin){
        StringBuffer sb = null;

        if(whichToJoin >= 1) {
            //Join GroupCursor with MemberCursor
            StringBuffer join1 = SQLGenerator.joinTables(sb, new String[] {WepayContract.Group.getInstance().getTableName()}, new String[]{WepayContract.Group._ID},
                    WepayContract.Member.getInstance().getTableName(), new String[]{WepayContract.Member.GROUP_ID});
            sb = join1;
        }if(whichToJoin >= 2) {
            //Join with user
            StringBuffer join2 = SQLGenerator.joinTables(sb, new String[] {WepayContract.Member.getInstance().getTableName()}, new String[]{WepayContract.Member.USER_ID},
                    WepayContract.User.getInstance().getTableName(), new String[]{WepayContract.User._ID});
            sb = join2;
        }if(whichToJoin >= 3) {
            //Join ExpenseCursor
            StringBuffer join3 = SQLGenerator.joinTables(sb, new String[] {WepayContract.Group.getInstance().getTableName()}, new String[]{WepayContract.Group._ID},
                    WepayContract.Expense.getInstance().getTableName(), new String[]{WepayContract.Expense.GROUP_ID});
            sb = join3;
        }if(whichToJoin >= 4) {
            //Join the above with PayerCursor.
            StringBuffer join4 = SQLGenerator.joinTables(sb, new String[] {WepayContract.Expense.getInstance().getTableName(), WepayContract.Member.getInstance().getTableName()}, new String[]{WepayContract.Expense._ID, WepayContract.Member._ID},
                    WepayContract.Payer.getInstance().getTableName(), new String[]{WepayContract.Payer.EXPENSE_ID, WepayContract.Payer.MEMBER_ID});
            sb = join4;
        }

        return sb;
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
        if(treeNode.getLeft() == null && treeNode.getRight() == null && treeNode.getTable() != null){
            return sb.append(treeNode.getTable());
        }else if(treeNode.getLeft() != null && treeNode.getRight() != null && treeNode.getTable() == null){
            sb.append(traverseTree(treeNode.getLeft())).append(" join ").append(traverseTree(treeNode.getRight()));
            sb.append(" on (");

            Iterator<ColumnJoin> it = treeNode.getColumnJoins().iterator();
            while(it.hasNext()){
                ColumnJoin jc = it.next();
                sb.append(jc.left.getFullName()).append(jc.operator).append(jc.right.getFullName());
                if(it.hasNext()){
                    sb.append(" AND ");
                }
            }
            sb.append(")");
        }
        return sb;
    }

    private static class SQLGenerator{
        /**
         * If the prevJoinTree argument is null, we are starting a new "join tree". If prevJoinTree is not null, we are
         * assuming that table1 is already in the prevJoinTree; in this case, we use table1 to add prefixes to the mColumns
         * in the "prefJoinTree join table2 on (table1.colN = table2.colM)" part of the SQL.
         * Joining is done with "=" comparisons between mColumns (e.g. table1.colN = table2.colM).
         * Plus, if there are multiple join mColumns, all conditions in the join for each column are and'ed (e.g. (table1.colN1 = table2.colM1 AND table1.colN2 = table2.colM2)
         *
         * @param prevJoinTree
         * @param table1
         * @param colsTable1
         * @param table2
         * @param colsTable2
         * @return
         */
        protected static StringBuffer joinTables(StringBuffer prevJoinTree, String[] table1, String[] colsTable1, String table2, String[] colsTable2) {
            StringBuffer sb = new StringBuffer();
            if (prevJoinTree == null) {
                sb.append(table1[0]);
            } else {
                sb.append(prevJoinTree);
            }

            sb.append(" join ").append(table2).append(" on (");

            int size = colsTable1.length;

            for (int i = 0; i < size; i++) {
                sb.append(table1[i]).append(".").append(colsTable1[i]);
                sb.append(" = ");
                sb.append(table2).append(".").append(colsTable2[i]);

                if (i < size - 1) { sb.append(" AND "); }
            }
            sb.append(")");

            return sb;
        }

        /**
         * Adds a getInstance name as prefix for all column names appearing in the sortBy string. For example,
         * if the sort by clause is "ID ASC" for getInstance group, this would gnerate "group.ID ASC".
         * @param table
         * @param tableCols
         * @param sortBy
         * @return
         */
        protected static StringBuffer addTablePrefixToString(String table, Collection<String> tableCols, String sortBy){
            StringBuffer regularExp = generateRegexForColumns(tableCols);
            StringBuffer sb = new StringBuffer();

            //replace all mColumns with getInstance.mColumns
            Pattern pattern = Pattern.compile(regularExp.toString());
            Matcher matcher = pattern.matcher(sortBy.toLowerCase());

            while (matcher.find()) {
                String finding = matcher.group();
                matcher.appendReplacement(sb, table + "." + finding);
            }
            matcher.appendTail(sb);

            return sb;
        }

        /**
         * Generates a regular expression "col1|col2|col3" from a set of cols.
         *
         * @param cols
         * @return
         */
        protected static StringBuffer generateRegexForColumns(Collection<String> cols){
            StringBuffer regularExp = new StringBuffer();

            int count = cols.size();
            //Building the regular expression like "col1|col2|col3|?", finding mColumns names and question marks
            for (String col : cols) {
                regularExp.append(col);

                if(count-- > 1) regularExp.append("|");
            }
            return regularExp;
        }

        /**
         * Add getInstance name as prefix to every column in projection, and concatenates in a comma-separated string.
         * @param table
         * @param projection
         * @return
         */
        protected static StringBuffer addTablePrefixToGroupBy(String table, String[] projection){
            StringBuffer groupBy = new StringBuffer();

            int count = projection.length;

            for(String col : projection){
                groupBy.append(table).append(".").append(col);
                if(count-- > 1) groupBy.append(", ");
            }
            return groupBy;
        }

        /**
         * Adds getInstance name as prefix to every column in the "projection" array
         * @param table
         * @param projection
         * @return
         */
        protected static StringBuffer addTablePrefixToProjection(String table, String[] projection){
            StringBuffer sb = new StringBuffer();
            int count = projection.length;

            for(String item : projection){
                sb.append(table).append(".").append(item);
                if(count-- > 1) sb.append(", ");
            }
            return sb;
        }
    }

}
