package com.jumo.wepay.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Set;
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
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.table().tableName + "/*", USER_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.table().tableName, USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.table().tableName + "/*/groups", USER_GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.table().tableName, GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.table().tableName + "/#", GROUP_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.table().tableName + "/#/users", GROUP_MEMBER_USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.table().tableName + "/#/expense/#/payers", GROUP_EXPENSE_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.table().tableName + "/#/payers", GROUP_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Member.table().tableName + "/#", MEMBER_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Member.table().tableName, MEMBERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Member.table().tableName + "/#/users", MEMBER_USER);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.table().tableName + "/#", EXPENSE_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.table().tableName + "/user/*/group/#", USER_GROUP_EXPENSES);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.table().tableName, EXPENSES);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Payer.table().tableName + "/expense/#", EXPENSE_PAYER_USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Payer.table().tableName, PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Location.table().tableName + "/#", LOCATION_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Location.table().tableName + "/expense/#", EXPENSE_LOCATION);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Recurrence.table().tableName + "/#", RECURRENCE_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Recurrence.table().tableName + "/recurrence/#", EXPENSE_RECURRENCE);

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

        switch(sURIMatcher.match(uri)){
            case USER_ID:
                userId = uri.getPathSegments().get(1); // get the user ID from the path.
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.User.table().tableName, WepayContract.User.table().columns.keySet(), projection, ADD_BALANCE, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, null), selectionArgs);
                break;

            case USERS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.User.table().tableName,projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case USER_GROUPS: //This also adds a balance from the perspective of a user.
                userId = uri.getPathSegments().get(1); // get the user ID from the path.
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.Group.table().tableName, WepayContract.Group.table().columns.keySet(), projection, true, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, null), selectionArgs);
                break;

            case GROUPS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.Group.table().tableName,projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case MEMBERS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.Member.table().tableName, projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case EXPENSES:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.Expense.table().tableName, projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case PAYERS:
                cursorResult = mDBHelper.getReadableDatabase().query(WepayContract.Payer.table().tableName, projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                break;

            case GROUP_MEMBER_USERS:
                groupId = uri.getPathSegments().get(1); // get the group ID from the path.
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.User.table().tableName, WepayContract.User.table().columns.keySet(), projection, NO_BALANCE, JOIN_GROUP_MEMBER_USER, selection, sortOrder, null, groupId), selectionArgs);
                break;

            case USER_GROUP_EXPENSES:
                userId = uri.getPathSegments().get(2); // get the user ID from the path.
                groupId = uri.getPathSegments().get(4); // get the group ID from the path.
                //String q = selectEntity(WepayContract.Expense.table().tableName, WepayContract.Expense.table().columns.keySet(), projection, ADD_BALANCE, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, groupId);
                //Log.d(TAG, q);
                cursorResult = mDBHelper.getReadableDatabase().rawQuery(selectEntity(WepayContract.Expense.table().tableName, WepayContract.Expense.table().columns.keySet(), projection, ADD_BALANCE, JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER, selection, sortOrder, userId, groupId), selectionArgs);
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
                return str.append(".item/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.User.table().tableName).toString();
            case USERS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.User.table().tableName).toString();
            case USER_GROUPS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Group.table().tableName).toString();
            case GROUPS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Group.table().tableName).toString();
            case MEMBERS:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Member.table().tableName).toString();
            case EXPENSES:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Expense.table().tableName).toString();
            case PAYERS:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Payer.table().tableName).toString();
            case GROUP_MEMBER_USERS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.User.table().tableName).toString();
            case USER_GROUP_EXPENSES:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Expense.table().tableName).toString();
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
                table = WepayContract.User.table().tableName;
                break;
            case GROUPS:
                table = WepayContract.Group.table().tableName;
                break;
            case MEMBERS:
                table = WepayContract.Member.table().tableName;
                break;
            case EXPENSES:
                table = WepayContract.Expense.table().tableName;
                break;
            case PAYERS:
                table = WepayContract.Payer.table().tableName;
                break;
        }
        return table;
    }


    private String select(WepayContract.CompositeEntity entity, ArrayList<Metric> metrics){ //include the rest of parameters.


        return null;
    }


    /**
     *
     * @param table is the table for the entity being retrieved
     * @param tableCols is the set of columns of the table for which the entity is being retrieved
     * @param projection determines the set of columns to select from a particular entity table
     * @param addBalance determines whether to include balance grouped by whether column defined in the projection
     * @param filter filter specification
     * @param sortOrder sort specification
     * @param tableJoins is an integer representing the tables to be joined; use the static members JOIN_*
     * @param userIdFilter represents the user ID to be included in the where clause
     * @param groupIdFilter represents the group ID to be included in the where clause
     * @return a full SQL select query with all joins and filters based on passed on parameters
     */
    private String selectEntity(String table, Set<String> tableCols, String[] projection, boolean addBalance, int tableJoins, String filter, String sortOrder,  String userIdFilter, String groupIdFilter){
        //// SELECT ////
        StringBuffer sb = new StringBuffer("select ");
        String[] newProjection;

        //add table prefix to projection columns
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
            sb.append("(").append(WepayContract.User.table().tableName).append(".").append(WepayContract.User._ID);
            sb.append(" = '").append(userIdFilter).append("')");
            if(groupIdFilter != null) sb.append(" and ");
        }
        //Add the condition on GroupCursor Id
        if(groupIdFilter != null){
            sb.append("(").append(WepayContract.Group.table().tableName).append(".").append(WepayContract.Group._ID);
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
        sb.append(WepayContract.Expense.table().tableName).append(".").append(WepayContract.Expense.AMOUNT).
                append(" * ").
				append(WepayContract.Expense.table().tableName).append(".").append(WepayContract.Expense.EXCHANGE_RATE).
				append(" * ").
                append(WepayContract.Payer.table().tableName).append(".").append(WepayContract.Payer.PERCENTAGE).
                append(") as ").append(WepayContract.USER_BALANCE);
        return sb;
    }

    /**
     * Returns a join string for the tables to join. If 1 is entered, then join only GroupCursor and MemberCursor; 2 joins GroupCursor, MemberCursor and UserCursor.
     * 3 joins up to ExpenseCursor, and 4 up to PayerCursor.
     * @param whichToJoin
     * @return
     */
    private StringBuffer joinGroupMemberUserExpensePayer(int whichToJoin){
        StringBuffer sb = null;

        if(whichToJoin >= 1) {
            //Join GroupCursor with MemberCursor
            StringBuffer join1 = SQLGenerator.joinTables(sb, new String[] {WepayContract.Group.table().tableName}, new String[]{WepayContract.Group._ID},
                    WepayContract.Member.table().tableName, new String[]{WepayContract.Member.GROUP_ID});
            sb = join1;
        }if(whichToJoin >= 2) {
            //Join with user
            StringBuffer join2 = SQLGenerator.joinTables(sb, new String[] {WepayContract.Member.table().tableName}, new String[]{WepayContract.Member.USER_ID},
                    WepayContract.User.table().tableName, new String[]{WepayContract.User._ID});
            sb = join2;
        }if(whichToJoin >= 3) {
            //Join ExpenseCursor
            StringBuffer join3 = SQLGenerator.joinTables(sb, new String[] {WepayContract.Group.table().tableName}, new String[]{WepayContract.Group._ID},
                    WepayContract.Expense.table().tableName, new String[]{WepayContract.Expense.GROUP_ID});
            sb = join3;
        }if(whichToJoin >= 4) {
            //Join the above with PayerCursor.
            StringBuffer join4 = SQLGenerator.joinTables(sb, new String[] {WepayContract.Expense.table().tableName, WepayContract.Member.table().tableName}, new String[]{WepayContract.Expense._ID, WepayContract.Member._ID},
                    WepayContract.Payer.table().tableName, new String[]{WepayContract.Payer.EXPENSE_ID, WepayContract.Payer.MEMBER_ID});
            sb = join4;
        }

        return sb;
    }

    private Metric getBalanceMetric(){
        WepayContract.Column expAmount = WepayContract.Expense.table().columns.get(WepayContract.Expense.AMOUNT);
        WepayContract.Column expExchange = WepayContract.Expense.table().columns.get(WepayContract.Expense.EXCHANGE_RATE);
        WepayContract.Column payPercent = WepayContract.Payer.table().columns.get(WepayContract.Payer.PERCENTAGE);

        ArrayList<WepayContract.Column> columns = new ArrayList();
        columns.add(expAmount);
        columns.add(expExchange);
        columns.add(payPercent);

        StringBuffer expression = new StringBuffer("sum(");
        expression.append(expAmount.getFullName()).
                append(" * ").
                append(expExchange.getFullName()).
                append(" * ").
                append(payPercent.getFullName()).append(")");

        String alias = WepayContract.Expense.USER_BALANCE;

        return new Metric(columns, alias, expression.toString());
    }

    private static class Metric{
        private ArrayList<WepayContract.Column> mColumns;
        private String mExpression;
        private String mAlias;

        public Metric(ArrayList<WepayContract.Column> cols, String exp, String alias){
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

        public void addDependentColumn(WepayContract.Column col){
            mColumns.add(col);
        }

        public void removeDependentColumn(WepayContract.Column col){
            mColumns.remove(col);
        }

        public String getColumnDefinition(){
            return mExpression + " as " + mAlias;
        }

        public ArrayList<WepayContract.Entity> getDependentEntities(){
            if(mColumns == null) return null;

            ArrayList<WepayContract.Entity> tables = new ArrayList<WepayContract.Entity>();

            for(WepayContract.Column c : mColumns){
                tables.add(WepayContract.getEntity(c.table));
            }
            return tables;
        }
    }


    private static class SQLGenerator{
        /**
         * If the prevJoinTree argument is null, we are starting a new "join tree". If prevJoinTree is not null, we are
         * assuming that table1 is already in the prevJoinTree; in this case, we use table1 to add prefixes to the columns
         * in the "prefJoinTree join table2 on (table1.colN = table2.colM)" part of the SQL.
         * Joining is done with "=" comparisons between columns (e.g. table1.colN = table2.colM).
         * Plus, if there are multiple join columns, all conditions in the join for each column are and'ed (e.g. (table1.colN1 = table2.colM1 AND table1.colN2 = table2.colM2)
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
         * Adds a table name as prefix for all column names appearing in the sortBy string. For example,
         * if the sort by clause is "ID ASC" for table group, this would gnerate "group.ID ASC".
         * @param table
         * @param tableCols
         * @param sortBy
         * @return
         */
        protected static StringBuffer addTablePrefixToString(String table, Set<String> tableCols, String sortBy){
            StringBuffer regularExp = generateRegexForColumns(tableCols);
            StringBuffer sb = new StringBuffer();

            //replace all columns with table.columns
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
        protected static StringBuffer generateRegexForColumns(Set<String> cols){
            StringBuffer regularExp = new StringBuffer();

            int count = cols.size();
            //Building the regular expression like "col1|col2|col3|?", finding columns names and question marks
            for (String col : cols) {
                regularExp.append(col);

                if(count-- > 1) regularExp.append("|");
            }
            return regularExp;
        }

        /**
         * Add table name as prefix to every column in projection, and concatenates in a comma-separated string.
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
         * Adds table name as prefix to every column in the "projection" array
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
