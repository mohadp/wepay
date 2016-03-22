package com.jumo.tablas.provider;

import android.net.Uri;

import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.provider.dao.Column;
import com.jumo.tablas.provider.dao.ColumnJoin;
import com.jumo.tablas.provider.dao.CompositeTable;
import com.jumo.tablas.provider.dao.TreeNode;
import com.jumo.tablas.provider.dao.Metric;
import com.jumo.tablas.provider.dao.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Moha on 6/28/15.
 */
public final class TablasContract {

    private static final String TAG = "TablasContract";
    public static String AUTHORITY = "com.jumo.tablas.provider";
    public static String SCHEME = "content";
    public static final Uri BASE_URI = new Uri.Builder().scheme(TablasContract.SCHEME).authority(TablasContract.AUTHORITY).build();


    private static final HashMap<String, Table> tableEntityMap = new HashMap<String, Table>();
    static{
        tableEntityMap.put(Group.TABLE_NAME, Group.getInstance());
        tableEntityMap.put(Expense.TABLE_NAME, Expense.getInstance());
        tableEntityMap.put(Member.TABLE_NAME, Member.getInstance());
        tableEntityMap.put(Payer.TABLE_NAME, Payer.getInstance());
        tableEntityMap.put(Currency.TABLE_NAME, Currency.getInstance());
        tableEntityMap.put(Country.TABLE_NAME, Country.getInstance());
        tableEntityMap.put(ExchangeRate.TABLE_NAME, ExchangeRate.getInstance());
        tableEntityMap.put(CurrencyFromAlias.TABLE_ALIAS, CurrencyFromAlias.getInstance());
        tableEntityMap.put(CurrencyToAlias.TABLE_ALIAS, CurrencyToAlias.getInstance());
        tableEntityMap.put(Compound.ExpenseBalance.TABLE_NAME, Compound.ExpenseBalance.getInstance());
        tableEntityMap.put(Compound.GroupBalance.TABLE_NAME, Compound.GroupBalance.getInstance());
        tableEntityMap.put(Compound.CountryCurrency.TABLE_NAME, Compound.CountryCurrency.getInstance());
    }

    public static Table getTable(String tableName){
        return tableEntityMap.get(tableName);
    }

    
    //All individual tables representing multiple entities.
    public final static class Group extends Table implements GroupColumns{
        //GroupCursor Table
        public static final String TABLE_NAME = "groups";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);

        public static final String _ID = "_id";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new Group();
            }
            return mInstance;
        }

        private Group(){
            super(TABLE_NAME);
        }

        @Override
        protected void defineTable(){
            addColumn(_ID, new Column(getTableName(), _ID, Column.DB_TYPE_INTEGER, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_LONG));
            addColumn(GROUP_NAME, new Column(getTableName(), GROUP_NAME, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));
            addColumn(GROUP_CREATED_ON, new Column(getTableName(), GROUP_CREATED_ON, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_DATE));
            addColumn(GROUP_PICTURE, new Column(getTableName(), GROUP_PICTURE, Column.DB_TYPE_BLOB, null, Column.INTERNAL_TYPE_BYTES));
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }
    }

    public final static class Expense extends Table implements ExpenseColumns{
        //ExpenseCursor getInstance
        public static final String TABLE_NAME = "expense";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);

        public static final String _ID = "_id";


        /**
         * When periodicity is
         *  PERIODICITY_DAILY, this variable has no significance;
         *  PERIODICITY_WEEKLY, offset = 1 means Monday, 2 is Tuesday, ... , 7 is Sunday.
         *  PERIODICITY_MONTHLY, offset = 1 means the first of the month, offset = OFFSET_LAST_OF_MONTH means last day of every month.
         **/
        public static final int OPTION_PERIOD_DAILY = 0;
        public static final int OPTION_PERIOD_WEEKLY = 1;
        public static final int OPTION_PERIOD_MONTHLY = 2;
        public static final int OPTION_OFFSET_LAST_DAY_OF_MONTH = -1;


        //Singleton table
        private static Table mInstance;

        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new Expense();
            }
            return mInstance;
        }

        private Expense(){
            super(TABLE_NAME);
        }

        protected void defineTable(){
            addColumn(_ID, new Column(getTableName(), _ID, Column.DB_TYPE_INTEGER, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_LONG));
            addColumn(EXPENSE_GROUP_ID, new Column(getTableName(), EXPENSE_GROUP_ID, Column.DB_TYPE_INTEGER, Group.getExternalReference(Group._ID), Column.INTERNAL_TYPE_LONG));
            addColumn(EXPENSE_CREATED_ON, new Column(getTableName(), EXPENSE_CREATED_ON, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_DATE));
            addColumn(EXPENSE_MESSAGE, new Column(getTableName(), EXPENSE_MESSAGE, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));
            addColumn(EXPENSE_AMOUNT, new Column(getTableName(), EXPENSE_AMOUNT, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));
			//addColumn(EXPENSE_EXCHANGE_RATE, new Column(getTableName(), EXPENSE_EXCHANGE_RATE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));  //TODO: this should be in a group-level table with exchange rates
            addColumn(EXPENSE_CURRENCY_ID, new Column(getTableName(), EXPENSE_CURRENCY_ID, Column.DB_TYPE_TEXT, ExchangeRate.getExternalReference(ExchangeRate.EXCHANGE_CURR_FR), Column.INTERNAL_TYPE_STRING));
            addColumn(EXPENSE_CATEGORY_ID, new Column(getTableName(), EXPENSE_CATEGORY_ID, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_LONG));
            addColumn(EXPENSE_LATITUDE, new Column(getTableName(), EXPENSE_LATITUDE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));
            addColumn(EXPENSE_LONGITUDE, new Column(getTableName(), EXPENSE_LONGITUDE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));
            addColumn(EXPENSE_IS_PAYMENT, new Column(getTableName(), EXPENSE_IS_PAYMENT, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true
            addColumn(EXPENSE_GROUP_EXPENSE_ID, new Column(getTableName(), EXPENSE_GROUP_EXPENSE_ID, Column.DB_TYPE_INTEGER, Expense.getExternalReference(Expense._ID), Column.INTERNAL_TYPE_LONG));
            addColumn(EXPENSE_PERIODICITY, new Column(getTableName(), EXPENSE_PERIODICITY, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT));
            addColumn(EXPENSE_OFFSET, new Column(getTableName(), EXPENSE_OFFSET, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT));

            
            //Foreign keys only to other mTables (no recursive relationships included here).
            ColumnJoin join = new ColumnJoin(getColumn(EXPENSE_GROUP_ID), Group.getInstance().getColumn(Group._ID));
            LinkedHashSet<ColumnJoin> joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(Group.getInstance().getTableName(), joinExp);

            //Joining with ExpenseRate (two columns as foreign key)
            joinExp = new LinkedHashSet<ColumnJoin>();
            join = new ColumnJoin(getColumn(EXPENSE_CURRENCY_ID), ExchangeRate.getInstance().getColumn(ExchangeRate.EXCHANGE_CURR_FR));
            joinExp.add(join);
            join = new ColumnJoin(getColumn(EXPENSE_GROUP_ID), ExchangeRate.getInstance().getColumn(ExchangeRate.EXCHANGE_GROUP_ID));
            joinExp.add(join);
            addForeignTable(ExchangeRate.getInstance().getTableName(), joinExp);
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }
    }

    public static final class Member extends Table implements MemberColumns{
        //MemberCursor Table
        public static final String TABLE_NAME = "member";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();
            
        public static final String _ID = "_id";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new Member();
            }
            return mInstance;
        }

        private Member(){
            super(TABLE_NAME);
        }

        protected void defineTable(){
            addColumn(_ID, new Column(getTableName(), _ID, Column.DB_TYPE_INTEGER, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_LONG));
            addColumn(MEMBER_USER_ID, new Column(getTableName(), MEMBER_USER_ID, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));
            addColumn(MEMBER_GROUP_ID, new Column(getTableName(), MEMBER_GROUP_ID, Column.DB_TYPE_INTEGER, Group.getExternalReference(Group._ID), Column.INTERNAL_TYPE_LONG));
            addColumn(MEMBER_IS_ADMIN, new Column(getTableName(), MEMBER_IS_ADMIN, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true
            addColumn(MEMBER_LEFT_GROUP, new Column(getTableName(), MEMBER_LEFT_GROUP, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true
            addColumn(MEMBER_IS_CURR_USR, new Column(getTableName(), MEMBER_IS_CURR_USR, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true

            //Foreign keys only to other mTables (no recursive relationships included here).

            ColumnJoin join = new ColumnJoin(getColumn(MEMBER_GROUP_ID), Group.getInstance().getColumn(Group._ID));
            LinkedHashSet<ColumnJoin> joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(Group.getInstance().getTableName(), joinExp);
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }
    }

    public static final class Payer extends Table implements PayerColumns {

        //Types of roles for Payer
        public static final int OPTION_ROLE_PAID = 0;
        public static final int OPTION_ROLE_SHOULD_PAY = 1;

        //PayerCursor Table
        public static final String TABLE_NAME = "payer";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new Payer();
            }
            return mInstance;
        }

        private Payer(){
            super(TABLE_NAME);
        }

        protected void defineTable(){
            addColumn(_ID, new Column(getTableName(), _ID, Column.DB_TYPE_INTEGER, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_LONG));
            addColumn(PAYER_MEMBER_ID, new Column(getTableName(), PAYER_MEMBER_ID, Column.DB_TYPE_INTEGER, Member.getExternalReference(Member._ID), Column.INTERNAL_TYPE_LONG));
            addColumn(PAYER_EXPENSE_ID, new Column(getTableName(), PAYER_EXPENSE_ID, Column.DB_TYPE_INTEGER, Expense.getExternalReference(Expense._ID), Column.INTERNAL_TYPE_LONG));
            addColumn(PAYER_ROLE, new Column(getTableName(), PAYER_ROLE, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT));
            addColumn(PAYER_PERCENTAGE, new Column(getTableName(), PAYER_PERCENTAGE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));
            addColumn(PAYER_MANUALLY_SET, new Column(getTableName(), PAYER_MANUALLY_SET, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true

            //Foreign keys
            ColumnJoin join = new ColumnJoin(getColumn(PAYER_MEMBER_ID), Member.getInstance().getColumn(Member._ID));
            LinkedHashSet<ColumnJoin> joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(Member.getInstance().getTableName(), joinExp);

            join = new ColumnJoin(getColumn(PAYER_EXPENSE_ID), Expense.getInstance().getColumn(Expense._ID));
            joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(Expense.getInstance().getTableName(), joinExp);
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }
    }

    public static final class Currency extends Table implements CurrencyColumns {

        //Currency Table
        public static final String TABLE_NAME = "currency";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);

        public static final String _ID = "_id";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new Currency();
            }
            return mInstance;
        }

        private Currency(){
            super(TABLE_NAME);
        }

        protected void defineTable(){
            addColumn(_ID, new Column(getTableName(), _ID, Column.DB_TYPE_TEXT, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_STRING));
            addColumn(CURRENCY_NAME, new Column(getTableName(), CURRENCY_NAME, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));
            addColumn(CURRENCY_SYMBOL, new Column(getTableName(), CURRENCY_SYMBOL, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }
    }

    public static final class Country extends Table implements CountryColumns {

        //Currency Table
        public static final String TABLE_NAME = "country";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);

        public static final String _ID = "_id";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new Country();
            }
            return mInstance;
        }

        private Country(){
            super(TABLE_NAME);
        }

        protected void defineTable(){
            addColumn(_ID, new Column(getTableName(), _ID, Column.DB_TYPE_TEXT, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_STRING));
            addColumn(COUNTRY_NAME, new Column(getTableName(), COUNTRY_NAME, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));
            addColumn(COUNTRY_CODE, new Column(getTableName(), COUNTRY_CODE, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT));
            addColumn(COUNTRY_CURR_ID, new Column(getTableName(), COUNTRY_CURR_ID, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));

            //Foreign keys
            ColumnJoin join = new ColumnJoin(getColumn(COUNTRY_CURR_ID), Currency.getInstance().getColumn(Currency._ID));
            LinkedHashSet<ColumnJoin> joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(Currency.getInstance().getTableName(), joinExp);
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }
    }

    /**
     * Alias table from Currency table
     */
    public static final class CurrencyToAlias extends Table implements CurrencyColumns, CurrencyToColumns {

        //Currency Table
        public static final String TABLE_NAME = "currency";
        public static final String TABLE_ALIAS = "currency_to";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_ALIAS);

        public static final String _ID = "_id";
        public static final String _ID_TO = "_id_to";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new CurrencyToAlias();
            }
            return mInstance;
        }

        private CurrencyToAlias(){
            super(TABLE_NAME);
            setAlias(TABLE_ALIAS);
        }

        protected void defineTable(){
            addColumn(_ID_TO, new Column(getAlias(), _ID, Column.DB_TYPE_TEXT, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_STRING, _ID_TO));
            addColumn(CURRENCY_TO_NAME, new Column(getAlias(), CURRENCY_NAME, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING, CURRENCY_TO_NAME));
            addColumn(CURRENCY_TO_SYMBOL, new Column(getAlias(), CURRENCY_SYMBOL, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING, CURRENCY_TO_SYMBOL));
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }

    }

    /**
     * Alias table from Currency table
     */
    public static final class CurrencyFromAlias extends Table implements CurrencyColumns, CurrencyFromColumns {

        //Currency Table
        public static final String TABLE_NAME = "currency";
        public static final String TABLE_ALIAS = "currency_from";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_ALIAS);

        public static final String _ID = "_id";
        public static final String _ID_FROM = "_id_from";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new CurrencyFromAlias();
            }
            return mInstance;
        }

        private CurrencyFromAlias(){
            super(TABLE_NAME);
            setAlias(TABLE_ALIAS);
        }

        protected void defineTable(){
            addColumn(_ID_FROM, new Column(getAlias(), _ID, Column.DB_TYPE_TEXT, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_STRING, _ID_FROM));
            addColumn(CURRENCY_FROM_NAME, new Column(getAlias(), CURRENCY_NAME, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING, CURRENCY_FROM_NAME));
            addColumn(CURRENCY_FROM_SYMBOL, new Column(getAlias(), CURRENCY_SYMBOL, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING, CURRENCY_FROM_SYMBOL));
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }

    }

    public static final class ExchangeRate extends Table implements ExchangeRateColumns{
        //Currency Table
        public static final String TABLE_NAME = "exchange_rate";
        public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);
        //public static final String _ID = "_id";

        //Singleton table
        private static Table mInstance;

        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new ExchangeRate();
            }
            return mInstance;
        }

        private ExchangeRate(){
            super(TABLE_NAME);
        }

        protected void defineTable(){
            //addColumn(_ID, new Column(getTableName(), _ID, Column.DB_TYPE_TEXT, Column.DEF_PRIMARY_KEY, Column.INTERNAL_TYPE_STRING));
            addColumn(EXCHANGE_GROUP_ID, new Column(getTableName(), EXCHANGE_GROUP_ID, Column.DB_TYPE_INTEGER, Group.getExternalReference(Group._ID), Column.INTERNAL_TYPE_LONG));
            addColumn(EXCHANGE_CURR_FR, new Column(getTableName(), EXCHANGE_CURR_FR, Column.DB_TYPE_TEXT, Currency.getExternalReference(Currency._ID), Column.INTERNAL_TYPE_STRING));
            addColumn(EXCHANGE_CURR_TO, new Column(getTableName(), EXCHANGE_CURR_TO, Column.DB_TYPE_TEXT, Currency.getExternalReference(Currency._ID), Column.INTERNAL_TYPE_STRING));
            addColumn(EXCHANGE_RATE, new Column(getTableName(), EXCHANGE_RATE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));

            //Foreign keys only to other mTables (no recursive relationships included here).
            ColumnJoin join = new ColumnJoin(getColumn(EXCHANGE_GROUP_ID), Group.getInstance().getColumn(Group._ID));
            LinkedHashSet<ColumnJoin> joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(Group.getInstance().getTableName(), joinExp);

            join = new ColumnJoin(getColumn(EXCHANGE_CURR_FR), CurrencyFromAlias.getInstance().getColumn(CurrencyFromAlias._ID_FROM));
            joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(CurrencyFromAlias.getInstance().getTableName(), joinExp);

            join = new ColumnJoin(getColumn(EXCHANGE_CURR_TO), CurrencyToAlias.getInstance().getColumn(CurrencyToAlias._ID_TO));
            joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(CurrencyToAlias.getInstance().getTableName(), joinExp);
        }

        static protected String getExternalReference(String colName){
            StringBuffer sb = new StringBuffer();
            sb.append("references ").append(TABLE_NAME).append("(").append(colName).append(")");
            return sb.toString();
        }
    }

    public static final class Compound{

        public static final class ExpenseBalance extends CompositeTable implements ExpenseColumns, PayerColumns, MemberColumns, ExchangeRateColumns {
            public static final String TABLE_NAME = "ExpenseBalance";
            public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);

           //Tables added to this getInstance.
            private final static Table EXPENSE_TABLE = Expense.getInstance();
            private final static Table EXCHANGE_TABLE = ExchangeRate.getInstance();
            private final static Table PAYER_TABLE = Payer.getInstance();
            private final static Table MEMBER_TABLE = Member.getInstance();

            //Additional columns
            /**
             * These "*_ORIGINAL" columns  represent the original "_id" columns of each table, saved as "table_name._id"; these
             * columns are here so that people can ask for entities at a higher level different from this composite table's _id (without retrieving this _id)
             * by still having an "_id" column (necessary for the cursor adapter, which needs an _id column).
             */
            public static final String EXPENSE_ID = EXPENSE_TABLE.getColumn(Expense._ID).getFullName();
            public static final String MEMBER_ID = MEMBER_TABLE.getColumn(Member._ID).getFullName();
            public static final String _ID = "_id";
            public static final String USER_BALANCE = "user_balance";       //Metric column for balance of every expense.
            public static final String CURR_USER_PAID = "curr_user_paid";   //Metric column to identify if current user paid for part or all of the expense
            public static final String CURR_USER_SHOULD_PAY = "curr_user_should_pay";   //Metric column to identify if current user should pay for part or all of the expense
            public static final String USERS_WHO_SHOULD_PAY = "users_who_should_pay";         //Metric column that has the set of users among which the expense should be split
            public static final String USERS_WHO_PAID = "users_who_paid";   //Metric column that has the set of users who paid for the expense


            //Singleton table
            private static CompositeTable mInstance;


            public static CompositeTable getInstance(){
                if(mInstance == null){
                    mInstance = new ExpenseBalance(TABLE_NAME);
                }
                return mInstance;
            }

            //This is a composite table that includes all mColumns of the several mTables.
            private ExpenseBalance(){
                super();
            }

            private ExpenseBalance(String tableName){
                super(tableName);
            }


            @Override
            protected void addMetrics(){
                addMetric(TablasContract.getUserBalanceMetric(USER_BALANCE));
                addMetric(TablasContract.getHasCurrUserPaidMetric(CURR_USER_PAID));
                addMetric(TablasContract.getUsersWhoShouldPayMetric(USERS_WHO_SHOULD_PAY));
                addMetric(TablasContract.getCurrUserShouldPayMetric(CURR_USER_SHOULD_PAY));
                addMetric(TablasContract.getUsersWhoPaidMetric(USERS_WHO_PAID));
            }

            @Override
            protected void addTablesToCompositeTable(){
                addTable(EXPENSE_TABLE);
                addTable(EXCHANGE_TABLE);
                addTable(PAYER_TABLE);
                addTable(MEMBER_TABLE);
            }

            @Override
            protected Column getIdColumn(){
                return PAYER_TABLE.getColumn(Payer._ID);
            }



            @Override
            public TreeNode getTableJoinTree(Set<Table> additionalTables){
                //Set left outer joins between Expense and Payers table
                TreeNode tree = super.getTableJoinTree(additionalTables);
                TreeNode.findFirstCommonParent(tree.findTableNode(EXPENSE_TABLE.getTableName()), tree.findTableNode(PAYER_TABLE.getTableName()))
                        .setJoinType(TreeNode.LEFT_OUTER_JOIN);
                TreeNode.findFirstCommonParent(tree.findTableNode(EXPENSE_TABLE.getTableName()), tree.findTableNode(EXCHANGE_TABLE.getTableName()))
                        .setJoinType(TreeNode.LEFT_OUTER_JOIN);
                return tree;
            }
        }

        public static final class GroupBalance extends CompositeTable implements GroupColumns, ExpenseColumns, PayerColumns, MemberColumns, ExchangeRateColumns {
            public static final String TABLE_NAME = "GroupBalance";
            public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);

            //Tables added to this getInstance.
            private final static Table GROUP_TABLE = Group.getInstance();
            private final static Table EXPENSE_TABLE = Expense.getInstance();
            private final static Table EXCHANGE_TABLE = ExchangeRate.getInstance();
            private final static Table PAYER_TABLE = Payer.getInstance();
            private final static Table MEMBER_TABLE = Member.getInstance();

            //Additional columns
            /**
             * These "*_ORIGINAL" columns  represent the original "_id" columns of each table, saved as "table_name._id"; these
             * columns are here so that people can ask for entities at a higher level different from this composite table's _id (without retrieving this _id)
             * by still having an "_id" column (necessary for the cursor adapter, which needs an _id column).
             */
            public static final String EXPENSE_ID = EXPENSE_TABLE.getColumn(Expense._ID).getFullName();
            public static final String MEMBER_ID = MEMBER_TABLE.getColumn(Member._ID).getFullName();
            public static final String GROUP_ID = GROUP_TABLE.getColumn(Group._ID).getFullName();
            public static final String _ID = "_id";
            public static final String USER_BALANCE = "user_balance";

            //Singleton table
            private static CompositeTable mInstance;


            public static CompositeTable getInstance() {
                if (mInstance == null) {
                    mInstance = new GroupBalance(TABLE_NAME);
                }
                return mInstance;
            }

            //This is a composite table that includes all mColumns of the several mTables.
            private GroupBalance() {
                super();
            }

            private GroupBalance(String tableName) {
                super(tableName);
            }

            @Override
            protected void addMetrics(){
                addMetric(TablasContract.getUserBalanceMetric(USER_BALANCE));
            }

            @Override
            protected void addTablesToCompositeTable() {
                addTable(GROUP_TABLE);
                addTable(MEMBER_TABLE);
                addTable(EXPENSE_TABLE);
                addTable(PAYER_TABLE);
                addTable(EXCHANGE_TABLE);
            }

            @Override
            protected Column getIdColumn(){
                return PAYER_TABLE.getColumn(Payer._ID);
            }

            /**
             * Changes the join type for the Group table, so tha we show all the groups even if there are no expenses.
             * @param additionalTables this may be null or an arraylist of tables; null will add no additional tables to the ones that conform the composite table.
             * @return
             */
            @Override
            public TreeNode getTableJoinTree(Set<Table> additionalTables){
                //Set Group to left outer join.
                TreeNode tree = super.getTableJoinTree(additionalTables);

                TreeNode.findFirstCommonParent(tree.findTableNode(GROUP_TABLE.getTableName()), tree.findTableNode(EXPENSE_TABLE.getTableName()))
                        .setJoinType(TreeNode.LEFT_OUTER_JOIN);

                TreeNode.findFirstCommonParent(tree.findTableNode(GROUP_TABLE.getTableName()), tree.findTableNode(EXCHANGE_TABLE.getTableName()))
                        .setJoinType(TreeNode.LEFT_OUTER_JOIN);

                TreeNode.findFirstCommonParent(tree.findTableNode(MEMBER_TABLE.getTableName()), tree.findTableNode(PAYER_TABLE.getTableName()))
                        .setJoinType(TreeNode.LEFT_OUTER_JOIN);

                TreeNode.findFirstCommonParent(tree.findTableNode(EXPENSE_TABLE.getTableName()), tree.findTableNode(EXCHANGE_TABLE.getTableName()))
                        .setJoinType(TreeNode.LEFT_OUTER_JOIN);

                return tree;
            }
        }

        public static final class CountryCurrency extends CompositeTable implements CurrencyColumns, CountryColumns {
            public static final String TABLE_NAME = "CountryCurrency";
            public static final Uri TABLE_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);

            //Tables added to this getInstance.
            private final static Table CURRENCY_TABLE = Currency.getInstance();
            private final static Table COUNTRY_TABLE = Country.getInstance();

            //Additional columns
            /**
             * These "*_ORIGINAL" columns  represent the original "_id" columns of each table, saved as "table_name._id"; these
             * columns are here so that people can ask for entities at a higher level different from this composite table's _id (without retrieving this _id)
             * by still having an "_id" column (necessary for the cursor adapter, which needs an _id column).
             */
            public static final String _ID = "_id";

            //Singleton table
            private static CompositeTable mInstance;


            public static CompositeTable getInstance() {
                if (mInstance == null) {
                    mInstance = new CountryCurrency(TABLE_NAME);
                }
                return mInstance;
            }

            //This is a composite table that includes all mColumns of the several mTables.
            private CountryCurrency() {
                super();
            }

            private CountryCurrency(String tableName) {
                super(tableName);
            }

            @Override
            protected void addMetrics(){
                //addMetric(TablasContract.getUserBalanceMetric(USER_BALANCE));
            }

            @Override
            protected void addTablesToCompositeTable() {
                addTable(CURRENCY_TABLE);
                addTable(COUNTRY_TABLE);
            }

            @Override
            protected Column getIdColumn(){
                return COUNTRY_TABLE.getColumn(Country._ID);
            }
        }
    }

    /**
     * Set of columns for the Group table.
     */
    public interface GroupColumns{
        public static final String GROUP_NAME = "group_name";
        public static final String GROUP_CREATED_ON = "group_created_on";
        public static final String GROUP_PICTURE = "group_picture";
        public static final String GROUP_CURRENCY = "group_currency";
    }

    /**
     * Set of columns for the Expense table.
     */
    public interface ExpenseColumns{
        public static final String EXPENSE_GROUP_ID = "expense_group_id";
        public static final String EXPENSE_CREATED_ON = "expense_created_on";
        public static final String EXPENSE_MESSAGE = "message";
        public static final String EXPENSE_AMOUNT = "amount";
        //public static final String EXPENSE_EXCHANGE_RATE = "exchange_rate"; //TODO: This will be removed, and instead, we are creating a new table that has all the exchange rates for the group
        public static final String EXPENSE_CURRENCY_ID = "currency";
        public static final String EXPENSE_CATEGORY_ID = "expense_category_id";
        public static final String EXPENSE_LATITUDE = "latitude";
        public static final String EXPENSE_LONGITUDE = "longitude";
        public static final String EXPENSE_IS_PAYMENT = "is_payment";
        public static final String EXPENSE_GROUP_EXPENSE_ID = "group_expense_id";
        public static final String EXPENSE_PERIODICITY = "periodicity";
        public static final String EXPENSE_OFFSET = "offset";
    }

    /**
     * Set of columns for the Member table.
     */
    public interface MemberColumns{
        public static final String MEMBER_USER_ID = "member_user_id";
        public static final String MEMBER_GROUP_ID = "member_group_id";
        public static final String MEMBER_IS_ADMIN = "is_admin";
        public static final String MEMBER_LEFT_GROUP = "left_group";
        public static final String MEMBER_IS_CURR_USR = "is_curr_usr";
    }

    /**
     * Set of columns for the Payer table.
     */
    public interface PayerColumns{
        public static final String PAYER_MEMBER_ID = "payer_member_id";
        public static final String PAYER_EXPENSE_ID = "payer_expense_id";
        public static final String PAYER_ROLE = "pay_role";
        public static final String PAYER_PERCENTAGE = "percentage";
        public static final String PAYER_MANUALLY_SET = "manually_set"; //whether the amount is automatically or manually set.
    }

    /**
     * SEt of columns for the Exchange Rate table.
     */
    public interface ExchangeRateColumns{
        public static final String EXCHANGE_GROUP_ID = "exchange_group_id";
        public static final String EXCHANGE_CURR_FR = "exchange_curr_fr";
        public static final String EXCHANGE_CURR_TO = "exchange_curr_to";
        public static final String EXCHANGE_RATE = "exchange_rate";
    }

    /**
     * Set of columns for the Currency table.
     */
    public interface CurrencyColumns{
        public static final String CURRENCY_NAME = "curr_name";
        public static final String CURRENCY_SYMBOL = "curr_symbol";
    }

    /**
     * Set of columns for the Country table.
     */
    public interface CountryColumns{
        public static final String COUNTRY_NAME = "country_name";
        public static final String COUNTRY_CODE = "country_code";
        public static final String COUNTRY_CURR_ID = "country_currency_id";
    }

    /**
     * Set of column names used as alias to the Currency table columns, representing the source currency
     */
    public interface CurrencyFromColumns{
        public static final String CURRENCY_FROM_NAME = "curr_fr_name";
        public static final String CURRENCY_FROM_SYMBOL = "curr_fr_symbol";
    }

    /**
     * Set of column names used as aliases to the Currency table columns, representing the target currency
     */
    public interface CurrencyToColumns{
        public static final String CURRENCY_TO_NAME = "curr_to_name";
        public static final String CURRENCY_TO_SYMBOL = "curr_to_symbol";
    }

    /**
     * Metric that represents the expression to calculate the balance of the current user' (as returned by
     * TablasManager.getCurrentUser()) for an expense or group.
     * @param name
     * @return
     */
    protected static Metric getUserBalanceMetric(String name){
        Column expAmount = TablasContract.Expense.getInstance().getColumn(TablasContract.Expense.EXPENSE_AMOUNT);
        Column expExchange = TablasContract.ExchangeRate.getInstance().getColumn(ExchangeRate.EXCHANGE_RATE);  //TablasContract.Expense.getInstance().getColumn(TablasContract.Expense.EXPENSE_EXCHANGE_RATE);
        Column payPercent = TablasContract.Payer.getInstance().getColumn(TablasContract.Payer.PAYER_PERCENTAGE);
        Column userIdCol = TablasContract.Member.getInstance().getColumn(TablasContract.Member.MEMBER_USER_ID);

        ArrayList<Column> columns = new ArrayList();
        columns.add(expAmount);
        columns.add(expExchange);
        columns.add(payPercent);
        columns.add(userIdCol);

        String userId = TablasManager.getCurrentUser();

        StringBuffer expression = new StringBuffer("sum(case when (");
        expression.append(userIdCol.getFullName()).append(" = '").append(TablasManager.getCurrentUser()).append("') then (")
                .append(expAmount.getFullName())
                .append(" * coalesce(")
                .append(expExchange.getFullName())
                .append(", 1) * ")
                .append(payPercent.getFullName())
                .append(")")
                .append(" else 0 end")
                .append(")");

        Column metricCol = new Column(null, name, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE, Column.IS_METRIC);

        return new Metric(columns, expression.toString(), Metric.IS_AGGREGATION, metricCol);
    }

    /**
     * Metric that represents whether the "current user" paid for part or the full amount of the expense.
     * MAX(CASE WHEN (PAYER_ROLE = [paid]) AND MEMBER.USER_ID = '[userID]') THEN 1 ELSE 0 END)
     * The user id is retrieved from the application's context (preferences, for example), retrieved from the TablasManager class.
     * @param name represents the column name that will represent this metric
     * @return true or false, as an integer 1 or 0, respectively.
     */
    protected static Metric getHasCurrUserPaidMetric(String name){
        //Aggregation metric
        Column memberUserId = TablasContract.Member.getInstance().getColumn(Member.MEMBER_USER_ID);
        Column payerRole = TablasContract.Payer.getInstance().getColumn(Payer.PAYER_ROLE);

        ArrayList<Column> cols = new ArrayList<Column>();
        cols.add(memberUserId);
        cols.add(payerRole);

        StringBuffer expression = new StringBuffer("max(case when (");
        expression.append(payerRole.getFullName()).append(" = ").append(Payer.OPTION_ROLE_PAID)
                .append(" AND ")
                .append(memberUserId.getFullName()).append(" = '").append(TablasManager.getCurrentUser()).append("') then 1 else 0 end)");

        Column metricCol = new Column(null, name, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT, Column.IS_METRIC);

        return new Metric(cols, expression.toString(), Metric.IS_AGGREGATION, metricCol);
    }


    /**
     * Metric that represents whether the "current user" paid for part or the full amount of the expense.
     * MAX(CASE WHEN (PAYER_ROLE = [paid]) AND MEMBER.USER_ID = '[userID]') THEN 1 ELSE 0 END)
     * The user id is retrieved from the application's context (preferences, for example), retrieved from the TablasManager class.
     * @param name represents the column name that will represent this metric
     * @return true or false, as an integer 1 or 0, respectively.
     */
    protected static Metric getCurrUserShouldPayMetric(String name){
        //Aggregation metric
        Column memberUserId = TablasContract.Member.getInstance().getColumn(Member.MEMBER_USER_ID);
        Column payerRole = TablasContract.Payer.getInstance().getColumn(Payer.PAYER_ROLE);

        ArrayList<Column> cols = new ArrayList<Column>();
        cols.add(memberUserId);
        cols.add(payerRole);

        StringBuffer expression = new StringBuffer("max(case when (");
        expression.append(payerRole.getFullName()).append(" = ").append(Payer.OPTION_ROLE_SHOULD_PAY)
                .append(" AND ")
                .append(memberUserId.getFullName()).append(" = '").append(TablasManager.getCurrentUser()).append("') then 1 else 0 end)");

        Column metricCol = new Column(null, name, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT, Column.IS_METRIC);

        return new Metric(cols, expression.toString(), Metric.IS_AGGREGATION, metricCol);
    }

    /**
     * Metric that represents the different users that should pay for an expense.
     * GROUP_CONCAT(CASE WHEN (PAYER_ROLE =  [shouldPay]) THEN MEMBER.USER_ID ELSE NULL)
     * @param name represents the column name that will represent this metric
     * @return a string concatenating the different user IDs that conform the users that should pay for an expense, separating each user ID with a comma.
     */
    protected static Metric getUsersWhoShouldPayMetric(String name){
        //Aggregation metric
        Column payerRole = TablasContract.Payer.getInstance().getColumn(Payer.PAYER_ROLE);
        Column memberUserId = TablasContract.Member.getInstance().getColumn(Member.MEMBER_USER_ID);

        ArrayList<Column> cols = new ArrayList<Column>();
        cols.add(payerRole);

        StringBuffer expression = new StringBuffer("group_concat(case when (");
        expression.append(payerRole.getFullName()).append(" = ").append(Payer.OPTION_ROLE_SHOULD_PAY)
                .append(") then ").append(memberUserId.getFullName()).append(" else null end)");

        Column metricCol = new Column(null, name, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING, Column.IS_METRIC);

        return new Metric(cols, expression.toString(), Metric.IS_AGGREGATION, metricCol);
    }

    /**
     * Metric that represents the different users that should pay for an expense.
     * GROUP_CONCAT(CASE WHEN (PAYER_ROLE =  [shouldPay]) THEN MEMBER.USER_ID ELSE NULL)
     * @param name represents the column name that will represent this metric
     * @return a string concatenating the different user IDs that conform the users that should pay for an expense, separating each user ID with a comma.
     */
    protected static Metric getUsersWhoPaidMetric(String name){
        //Aggregation metric
        Column payerRole = TablasContract.Payer.getInstance().getColumn(Payer.PAYER_ROLE);
        Column memberUserId = TablasContract.Member.getInstance().getColumn(Member.MEMBER_USER_ID);

        ArrayList<Column> cols = new ArrayList<Column>();
        cols.add(payerRole);

        StringBuffer expression = new StringBuffer("group_concat(case when (");
        expression.append(payerRole.getFullName()).append(" = ").append(Payer.OPTION_ROLE_PAID)
                .append(") then ").append(memberUserId.getFullName()).append(" else null end)");

        Column metricCol = new Column(null, name, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING, Column.IS_METRIC);

        return new Metric(cols, expression.toString(), Metric.IS_AGGREGATION, metricCol);
    }

}
