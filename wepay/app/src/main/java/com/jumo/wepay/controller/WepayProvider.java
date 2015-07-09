package com.jumo.wepay.controller;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Moha on 7/3/15.
 */
public class WepayProvider extends ContentProvider{

    private static final int JOIN_GROUP_MEMBER = 1;
    private static final int JOIN_GROUP_MEMBER_USER = 2;
    private static final int JOIN_GROUP_MEMBER_USER_EXPENSE = 3;
    private static final int JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER = 4;


    private static final String PROVIDER_AUTHORITY = "com.jumo.wepay.provider";
    private static final int USER_ID = 1;
    private static final int USERS = 18;        //Max
    private static final int USER_GROUPS = 2;
    private static final int GROUPS = 3;
    private static final int GROUP_ID = 4;
    private static final int GROUP_MEMBERS = 5;
    private static final int GROUP_MEMBER_USERS = 6;
    private static final int USER_GROUP_EXPENSES = 7;
    private static final int GROUP_EXPENSE_PAYERS = 8;
    private static final int GROUP_PAYERS = 17;
    private static final int MEMBER_ID = 9;
    private static final int MEMBER_USER = 10;
    private static final int EXPENSE_ID = 11;
    private static final int EXPENSE_PAYERS = 12;
    private static final int EXPENSE_LOCATION = 13;
    private static final int EXPENSE_RECURRENCE = 14;
    private static final int RECURRENCE_ID = 15;
    private static final int LOCATION_ID = 16;


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {   //TODO: possibly add a GROUP_USERS
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.TABLE_NAME + "/*", USER_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.TABLE_NAME, USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.User.TABLE_NAME + "/*/group", USER_GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.TABLE_NAME, GROUPS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.TABLE_NAME + "/#", GROUP_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.TABLE_NAME + "/#/member", GROUP_MEMBERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.TABLE_NAME + "/#/user", GROUP_MEMBER_USERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.TABLE_NAME + "/*/#/expense", USER_GROUP_EXPENSES);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.TABLE_NAME + "/#/expense/#/payer", GROUP_EXPENSE_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Group.TABLE_NAME + "/#/payer", GROUP_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Member.TABLE_NAME + "/#", MEMBER_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Member.TABLE_NAME + "/#/user", MEMBER_USER);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.TABLE_NAME + "/#", EXPENSE_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.TABLE_NAME + "/#/payer", EXPENSE_PAYERS);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.TABLE_NAME + "/#/location", EXPENSE_LOCATION);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Expense.TABLE_NAME + "/#/recurrence", EXPENSE_RECURRENCE);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Location.TABLE_NAME + "/#", LOCATION_ID);
        sURIMatcher.addURI(PROVIDER_AUTHORITY, WepayContract.Recurrence.TABLE_NAME + "/#", RECURRENCE_ID);
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

        String userId, groupId;

        Cursor wrapped = null;

        switch(sURIMatcher.match(uri)){
            case USERS:
                wrapped = mDBHelper.getReadableDatabase().query(WepayContract.User.TABLE_NAME,projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                 return new Cursors.UserCursor(wrapped);

            case USER_GROUPS: //This also adds a balance from the perspective of a user.
                userId = uri.getPathSegments().get(1); // get the user ID from the path.
                wrapped = mDBHelper.getReadableDatabase().rawQuery(selectUserGroups(projection, selection, sortOrder, userId), selectionArgs);
                return new Cursors.GroupCursor(wrapped);

            case GROUPS:
                wrapped = mDBHelper.getReadableDatabase().query(WepayContract.Group.TABLE_NAME,projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                return new Cursors.GroupCursor(wrapped);

            case GROUP_MEMBERS:
                wrapped = mDBHelper.getReadableDatabase().query(WepayContract.Member.TABLE_NAME, projection, selection /*filter*/,
                        selectionArgs /*filter values*/, null /*group by*/, sortOrder /* order by*/, null /* having*/);
                return new Cursors.MemberCursor(wrapped);

            case GROUP_MEMBER_USERS:
                groupId = uri.getPathSegments().get(1); // get the group ID from the path.
                wrapped = mDBHelper.getReadableDatabase().rawQuery(selectGroupUsers(projection, selection, sortOrder, groupId), selectionArgs);
                return new Cursors.UserCursor(wrapped);

            case USER_GROUP_EXPENSES:
                userId = uri.getPathSegments().get(1); // get the user ID from the path.
                groupId = uri.getPathSegments().get(2); // get the group ID from the path.
                wrapped = mDBHelper.getReadableDatabase().rawQuery(selectUserGroupExpenses(projection, selection, sortOrder, userId, groupId), selectionArgs);
                return new Cursors.ExpenseCursor(wrapped);
        }
        return null;
    }




    public int delete(Uri uri, String selection, String[] selectionArgs){
        return 0;
    }

    public String getType(Uri uri){
        StringBuilder str = new StringBuilder("vnd.android.cursor");
        switch(sURIMatcher.match(uri)){
            case USERS:
                return str.append(".item/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.User.TABLE_NAME).toString();

            case USER_GROUPS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Group.TABLE_NAME).toString();
            case GROUPS:
                return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Group.TABLE_NAME).toString();
            case GROUP_MEMBERS:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Member.TABLE_NAME).toString();
            case GROUP_MEMBER_USERS:
                //return str.append(".dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.User.TABLE_NAME).toString();
            case USER_GROUP_EXPENSES:
                return str.append("dir/").append("vnd.").append(PROVIDER_AUTHORITY).append(".").append(WepayContract.Expense.TABLE_NAME).toString();

        }

        return null;
    }

    public Uri insert(Uri uri, ContentValues values){

        String table = null;

        switch(sURIMatcher.match(uri)){
            case USERS:
                table = uri.getLastPathSegment();
                break;
            case GROUPS:
                table = uri.getLastPathSegment();
                break;
            case GROUP_MEMBERS:
                table = uri.getLastPathSegment();
                values.remove(WepayContract.Member.GROUP_ID);  // make sure we add member to the group as specified in URI path
                String groupId = uri.getPathSegments().get(1); // get the group ID from the path.
                values.put(WepayContract.Member.GROUP_ID, Long.parseLong(groupId)); // make sure to use the group ID in the path for the member
                break;
        }
        if(table == null)
            return null;

        return ContentUris.withAppendedId(uri, mDBHelper.getWritableDatabase().insert(table, null, values));
    }


    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        return 0;
    }

    /**
     * Gets the groups for a particular user.
     * @param projection
     * @param filter
     * @param sortOrder
     * @return
     */
    private String selectUserGroups(String[] projection, String filter, String sortOrder, String userId){
        //// SELECT ////
        StringBuffer sb = new StringBuffer("select ");
        String[] newProjection;

        //add table prefix to projection columns
        if(projection != null) {
            newProjection = projection;
        }else{
            newProjection = WepayContract.Group.COL_DEFS.keySet().toArray(new String[0]);
        }
        sb.append(SQLGenerator.addTablePrefixToProjection(WepayContract.Group.TABLE_NAME, newProjection));
        sb.append(", ").append(addBalanceSum());

        //// FROM ////
        //build the join path; from group, join to expense, and then to payer.
        sb.append(" from ").append(joinGroupMemberUserExpensePayer(JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER));

        //// WHERE ////
        sb.append(" where ");
        if(filter != null ){
            sb.append(SQLGenerator.addTablePrefixToString(WepayContract.Group.TABLE_NAME, WepayContract.Group.COL_DEFS.keySet(), filter)).append(" and ");
        }
        //Add the condition on User Name
        sb.append("(").append(WepayContract.User.TABLE_NAME).append(".").append(WepayContract.User._ID);
        sb.append(" = ").append(userId).append(")");


        //// GROUP BY ////
        sb.append(" group by ");
        sb.append(SQLGenerator.addTablePrefixToGroupBy(WepayContract.Group.TABLE_NAME, newProjection));

        //// SORT BY ////
        if(sortOrder != null) {
            sb.append(" sort by ");
            sb.append(SQLGenerator.addTablePrefixToString(WepayContract.Group.TABLE_NAME, WepayContract.Group.COL_DEFS.keySet(), sortOrder));
        }

        return sb.toString();
    }

    /**
     * Gets the expenses for a particular group; the expenses include a balance for the user.
     * @param projection
     * @param filter
     * @param sortOrder
     * @return
     */
    private String selectUserGroupExpenses(String[] projection, String filter, String sortOrder, String userId, String groupId){
        //// SELECT ////
        StringBuffer sb = new StringBuffer("select ");
        String[] newProjection;

        //add table prefix to projection columns
        if(projection != null) {
            newProjection = projection;
        }else{
            newProjection = WepayContract.Group.COL_DEFS.keySet().toArray(new String[0]);
        }
        sb.append(SQLGenerator.addTablePrefixToProjection(WepayContract.Expense.TABLE_NAME, newProjection));
        sb.append(", ").append(addBalanceSum());

        //// FROM ////
        //build the join path; from group, join to expense, and then to payer.
        sb.append(" from ").append(joinGroupMemberUserExpensePayer(JOIN_GROUP_MEMBER_USER_EXPENSE_PAYER));

        //// WHERE ////
        sb.append(" where ");
        if(filter != null ){
            sb.append(SQLGenerator.addTablePrefixToString(WepayContract.Expense.TABLE_NAME, WepayContract.Expense.COL_DEFS.keySet(), filter)).append(" and ");
        }
        //Add the condition on GroupID
        sb.append("(").append(WepayContract.User.TABLE_NAME).append(".").append(WepayContract.User._ID);
        sb.append(" = ").append(userId).append(") and ");
        sb.append("(").append(WepayContract.Expense.TABLE_NAME).append(".").append(WepayContract.Expense.GROUP_ID);
        sb.append(" = ").append(groupId).append(")");


        //// GROUP BY ////
        sb.append(" group by ");
        sb.append(SQLGenerator.addTablePrefixToGroupBy(WepayContract.Expense.TABLE_NAME, newProjection));

        //// SORT BY ////
        if(sortOrder != null) {
            sb.append(" sort by ");
            sb.append(SQLGenerator.addTablePrefixToString(WepayContract.Expense.TABLE_NAME, WepayContract.Expense.COL_DEFS.keySet(), sortOrder));
        }

        return sb.toString();
    }

    private String selectGroupUsers(String[] projection, String filter, String sortOrder, String groupId) {
        //// SELECT ////
        StringBuffer sb = new StringBuffer("select ");
        String[] newProjection;

        //add table prefix to projection columns
        if (projection != null) {
            newProjection = projection;
        } else {
            newProjection = WepayContract.User.COL_DEFS.keySet().toArray(new String[0]);
        }
        sb.append(SQLGenerator.addTablePrefixToProjection(WepayContract.User.TABLE_NAME, newProjection));

        //// FROM ////
        //build the join path; from group, join to expense, and then to payer.
        sb.append(" from ").append(joinGroupMemberUserExpensePayer(JOIN_GROUP_MEMBER_USER));

        //// WHERE ////
        sb.append(" where ");
        if (filter != null) {
            sb.append(SQLGenerator.addTablePrefixToString(WepayContract.User.TABLE_NAME, WepayContract.User.COL_DEFS.keySet(), filter)).append(" and ");
        }
        //Add the condition on GroupID
        sb.append("(").append(WepayContract.Group.TABLE_NAME).append(".").append(WepayContract.Group._ID);
        sb.append(" = ").append(groupId).append(")");


        //// SORT BY ////
        if (sortOrder != null) {
            sb.append(" sort by ");
            sb.append(SQLGenerator.addTablePrefixToString(WepayContract.User.TABLE_NAME, WepayContract.User.COL_DEFS.keySet(), sortOrder));
        }

        return sb.toString();
    }


    private StringBuffer addBalanceSum(){
        StringBuffer sb = new StringBuffer("sum(");
        sb.append(WepayContract.Expense.TABLE_NAME).append(".").append(WepayContract.Expense.AMOUNT).
                append(" * ").
                append(WepayContract.Payer.TABLE_NAME).append(".").append(WepayContract.Payer.PERCENTAGE).
                append(") as ").append(WepayContract.Expense.USER_BALANCE);
        return sb;
    }

    /**
     * Returns a join string for the tables to join. If 1 is entered, then join only Group and Member; 2 joins Group, Member and User.
     * 3 joins up to Expense, and 4 up to Payer.
     * @param whichToJoin
     * @return
     */
    private StringBuffer joinGroupMemberUserExpensePayer(int whichToJoin){
        StringBuffer sb = null;

        if(whichToJoin >= 1) {
            //Join with Member
            StringBuffer join1 = SQLGenerator.joinTables(sb, WepayContract.Group.TABLE_NAME, new String[]{WepayContract.Group._ID},
                    WepayContract.Member.TABLE_NAME, new String[]{WepayContract.Member.GROUP_ID});
            sb = join1;
        }if(whichToJoin >= 2) {
            //Join with user
            StringBuffer join2 = SQLGenerator.joinTables(sb, WepayContract.Member.TABLE_NAME, new String[]{WepayContract.Member.USER_ID},
                    WepayContract.User.TABLE_NAME, new String[]{WepayContract.User._ID});
            sb = join2;
        }if(whichToJoin >= 3) {
            //Join Group and Expense
            StringBuffer join3 = SQLGenerator.joinTables(sb, WepayContract.Group.TABLE_NAME, new String[]{WepayContract.Group._ID},
                    WepayContract.Expense.TABLE_NAME, new String[]{WepayContract.Expense.GROUP_ID});
            sb = join3;
        }if(whichToJoin >= 4) {
            //Join the above with Payer.
            StringBuffer join4 = SQLGenerator.joinTables(sb, WepayContract.Expense.TABLE_NAME, new String[]{WepayContract.Expense._ID},
                    WepayContract.Payer.TABLE_NAME, new String[]{WepayContract.Payer.EXPENSE_ID});
            sb = join4;
        }

        return sb;
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
        protected static StringBuffer joinTables(StringBuffer prevJoinTree, String table1, String[] colsTable1, String table2, String[] colsTable2) {
            StringBuffer sb = new StringBuffer();
            if (prevJoinTree == null) {
                sb.append(table1);
            } else {
                sb.append(prevJoinTree);
            }

            sb.append(" join ").append(table2).append(" on (");

            int size = colsTable1.length;

            for (int i = 0; i < size; i++) {
                sb.append(table1).append(".").append(colsTable1[i]);
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


        /*
         * Adds table name as prefix to all columns in the filter expression, and also fills in the ? in the filter expression.
         * For example, if a condition is included as "_ID = ?" with table=group and filterValues = {1},
         * this would return "group._id = 1"
         * @param table
         * @param filter
         * @param filterValues
         * @return A StringBuffer with the fitlering expression
         */
        /*private StringBuffer addTablePrefixToFilter(String table, Set<String> allCols, String filter, String[] filterValues) {
            StringBuffer regularExp = generateRegexForColumns(allCols);
            regularExp.append("|?");

            StringBuffer sb = new StringBuffer();

            //replace all columns with table.columns; replace all '?'s with filter values
            int valPos = 0;
            Pattern pattern = Pattern.compile(regularExp.toString());
            Matcher matcher = pattern.matcher(filter.toLowerCase());

            while (matcher.find()) {
                String finding = matcher.group();
                if (finding.equals("?") && valPos < filterValues.length) {
                    matcher.appendReplacement(sb, filterValues[valPos++]);
                } else {
                    matcher.appendReplacement(sb, table + "." + finding);
                }
            }
            matcher.appendTail(sb);
            return sb;
        }*/
    }

}
