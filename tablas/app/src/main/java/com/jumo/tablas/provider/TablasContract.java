package com.jumo.tablas.provider;

import android.net.Uri;

import com.jumo.tablas.provider.dao.Column;
import com.jumo.tablas.provider.dao.ColumnJoin;
import com.jumo.tablas.provider.dao.CompositeTable;
import com.jumo.tablas.provider.dao.Metric;
import com.jumo.tablas.provider.dao.Table;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Moha on 6/28/15.
 */
public final class TablasContract {

    private static final String TAG = "TablasContract";
    public static String AUTHORITY = "com.jumo.tablas.provider";
    public static String SCHEME = "content";
    public static final Uri BASE_URI = new Uri.Builder().scheme(TablasContract.SCHEME).authority(TablasContract.AUTHORITY).build();


    //Column definitions
    protected static final String USER_BALANCE = "user_balance";

    //Reusable strings to avoid using multiple instances of these strings;
    protected static final String DT_INTEGER = "integer";
    protected static final String DT_TEXT = "nvarchar(255)";
    protected static final String DT_DOUBLE = "double";
    protected static final String DT_BLOB = "blob";
    protected static final String DEF_PRIMARY_KEY = "primary key";


    private static final HashMap<String, Table> tableEntityMap = new HashMap<String, Table>();
    static{
        tableEntityMap.put(Group.TABLE_NAME, Group.getInstance());
        tableEntityMap.put(Expense.TABLE_NAME, Expense.getInstance());
        tableEntityMap.put(Member.TABLE_NAME, Member.getInstance());
        tableEntityMap.put(Payer.TABLE_NAME, Payer.getInstance());
    }

    public static Table getTable(String tableName){
        return tableEntityMap.get(tableName);
    }

    
    //All individual tables representing multiple entities.
    public final static class Group extends Table {
        //GroupCursor Table
        private static final String TABLE_NAME = "groups";
        
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String CREATED_ON = "created_on";
        public static final String GROUP_PICTURE = "group_picture";

        public static final String USER_BALANCE = TablasContract.USER_BALANCE; //Not saved in the databased, this represents a user' balance at the group level (or expense level) = the sum of payments.

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
        protected void defineColumnsAndForeignKeys(){
            mColumns.put(_ID, new Column(mTableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY, Column.TYPE_LONG));
            mColumns.put(NAME, new Column(mTableName, NAME, DT_TEXT, null, Column.TYPE_STRING));
            mColumns.put(CREATED_ON, new Column(mTableName, CREATED_ON, DT_INTEGER, null,  Column.TYPE_DATE));
            mColumns.put(GROUP_PICTURE, new Column(mTableName, GROUP_PICTURE, DT_BLOB, null, Column.TYPE_BYTES));
            mColumns.put(USER_BALANCE, new Column(mTableName, USER_BALANCE, DT_DOUBLE, null, Column.TYPE_DOUBLE, false));
        }
    }

    public final static class Expense extends Table {
        //ExpenseCursor getInstance
        private static final String TABLE_NAME = "expense";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS =  new LinkedHashMap<String, String[]>();

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


        public static final String _ID = "_id";
        public static final String GROUP_ID = "group_id";
        public static final String CREATED_ON = "created_on";
        public static final String MESSAGE = "message";
        public static final String AMOUNT = "amount";
		public static final String EXCHANGE_RATE = "exchange_rate";
        public static final String CURRENCY = "currency";
        public static final String CATEGORY_ID = "category_id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String IS_PAYMENT = "is_payment";
        public static final String GROUP_EXPENSE_ID = "group_expense_id";
        public static final String PERIODICITY = "periodicity";
        public static final String OFFSET = "offset";
        //public static final String RECURRENCE_ID = "recurrence_id";
        public static final String USER_BALANCE = TablasContract.USER_BALANCE; //Not saved in the databased, this represents a user' balance at the group level (or expense level) = the sum of payments.

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

        protected void defineColumnsAndForeignKeys(){
            mColumns.put(_ID, new Column(mTableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY, Column.TYPE_LONG));
            mColumns.put(GROUP_ID, new Column(mTableName, GROUP_ID, DT_INTEGER, "references groups(_id)", Column.TYPE_LONG));
            mColumns.put(CREATED_ON, new Column(mTableName, CREATED_ON, DT_INTEGER, null,  Column.TYPE_DATE));
            mColumns.put(MESSAGE, new Column(mTableName, MESSAGE, DT_TEXT, null,  Column.TYPE_STRING));
            mColumns.put(AMOUNT, new Column(mTableName, AMOUNT, DT_DOUBLE, null,  Column.TYPE_DOUBLE));
			mColumns.put(EXCHANGE_RATE, new Column(mTableName, EXCHANGE_RATE, DT_DOUBLE, null, Column.TYPE_DOUBLE));  //TODO: Add a clause for having 1 as default exchange rate (this means there is no conversion)
            mColumns.put(CURRENCY, new Column(mTableName, CURRENCY, DT_TEXT, null, Column.TYPE_STRING));
            mColumns.put(CATEGORY_ID, new Column(mTableName, CATEGORY_ID, DT_INTEGER, null, Column.TYPE_LONG));
            mColumns.put(LATITUDE, new Column(mTableName, LATITUDE, DT_DOUBLE, null, Column.TYPE_DOUBLE));
            mColumns.put(LONGITUDE, new Column(mTableName, LONGITUDE, DT_DOUBLE, null,  Column.TYPE_DOUBLE));
            mColumns.put(IS_PAYMENT, new Column(mTableName, IS_PAYMENT, DT_INTEGER, null, Column.TYPE_BOOL)); //boolean 0=false, 1=true
            mColumns.put(GROUP_EXPENSE_ID, new Column(mTableName, GROUP_EXPENSE_ID, DT_INTEGER, "references expense(_id)", Column.TYPE_LONG));
            mColumns.put(PERIODICITY, new Column(mTableName, PERIODICITY, DT_INTEGER, null,  Column.TYPE_INT));
            mColumns.put(OFFSET, new Column(mTableName, OFFSET, DT_INTEGER, null,  Column.TYPE_INT));
            mColumns.put(USER_BALANCE, new Column(mTableName, USER_BALANCE, DT_DOUBLE, null, Column.TYPE_DOUBLE, false));

            
            //Foreign keys only to other mTables (no recursive relationships included here).
            ColumnJoin join = new ColumnJoin(getColumn(GROUP_ID), Group.getInstance().getColumn(Group._ID));
            ArrayList<ColumnJoin> joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(Group.getInstance().getTableName(), joinExp);
        }
    }

    public static final class Member extends Table {
        //MemberCursor Table
        private static final String TABLE_NAME = "member";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();
            
        public static final String _ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String GROUP_ID = "group_id";
        public static final String IS_ADMIN = "is_admin";
        public static final String LEFT_GROUP = "left_group";

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

        protected void defineColumnsAndForeignKeys(){
            mColumns.put(_ID, new Column(mTableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY, Column.TYPE_LONG));
            mColumns.put(USER_ID, new Column(mTableName, USER_ID, DT_TEXT, null, Column.TYPE_STRING));
            mColumns.put(GROUP_ID, new Column(mTableName, GROUP_ID, DT_INTEGER, "references groups(_id)",  Column.TYPE_LONG));
            mColumns.put(IS_ADMIN, new Column(mTableName, IS_ADMIN, DT_INTEGER, null,  Column.TYPE_BOOL)); //boolean 0=false, 1=true
            mColumns.put(LEFT_GROUP, new Column(mTableName, LEFT_GROUP, DT_INTEGER, null,  Column.TYPE_BOOL)); //boolean 0=false, 1=true

            //Foreign keys only to other mTables (no recursive relationships included here).
            ArrayList<ColumnJoin> joinExp = new ArrayList<ColumnJoin>();
            ColumnJoin join = new ColumnJoin(getColumn(GROUP_ID), Group.getInstance().getColumn(Group._ID));
            joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(Group.getInstance().getTableName(), joinExp);


        }
    }

    public static final class Payer extends Table {

        //Types of roles for Payer
        public static final int OPTION_ROLE_PAID = 0;
        public static final int OPTION_ROLE_SHOULD_PAY = 1;

        //PayerCursor Table
        private static final String TABLE_NAME = "payer";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String MEMBER_ID = "member_id";
        public static final String EXPENSE_ID = "expense_id";
        public static final String ROLE = "role";
        public static final String PERCENTAGE = "percentage";

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

        protected void defineColumnsAndForeignKeys(){
            mColumns.put(_ID, new Column(mTableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY,  Column.TYPE_LONG));
            mColumns.put(MEMBER_ID, new Column(mTableName, MEMBER_ID, DT_INTEGER, "references member(_id)",  Column.TYPE_LONG));
            mColumns.put(EXPENSE_ID, new Column(mTableName, EXPENSE_ID, DT_INTEGER, "references expense(_id)",  Column.TYPE_LONG));
            mColumns.put(ROLE, new Column(mTableName, ROLE, DT_INTEGER, null,  Column.TYPE_INT));
            mColumns.put(PERCENTAGE, new Column(mTableName, PERCENTAGE, DT_DOUBLE, null,  Column.TYPE_DOUBLE));

            //Foreign keys
            ColumnJoin join = new ColumnJoin(getColumn(MEMBER_ID), Member.getInstance().getColumn(Member._ID));
            ArrayList<ColumnJoin> joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(Member.getInstance().getTableName(), joinExp);

            join = new ColumnJoin(getColumn(EXPENSE_ID), Expense.getInstance().getColumn(Expense._ID));
            joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(Expense.getInstance().getTableName(), joinExp);
        }
   
    }

    public static final class Compound{

        public static final class ExpenseBalance extends CompositeTable {

           //Tables added to this getInstance.
            public final static Table EXPENSE_TABLE = Expense.getInstance();
            public final static Table PAYER_TABLE = Payer.getInstance();
            public final static Table MEMBER_TABLE = Member.getInstance();

            //Singleton table
            private static CompositeTable mInstance;


            public static CompositeTable getInstance(){
                if(mInstance == null){
                    mInstance = new ExpenseBalance();
                }
                return mInstance;
            }

            //This is a composite table that includes all mColumns of the several mTables.
            private ExpenseBalance(){
                super();
            }

            protected void defineTables(){
                addTable(EXPENSE_TABLE);
                addTable(PAYER_TABLE);
                addTable(MEMBER_TABLE);
            }
        }

        public static final class GroupBalance extends CompositeTable {

            //Tables added to this getInstance.
            public final static Table EXPENSE_TABLE = Expense.getInstance();
            public final static Table PAYER_TABLE = Payer.getInstance();
            public final static Table MEMBER_TABLE = Member.getInstance();
            public final static Table GROUP_TABLE = Group.getInstance();

            //Singleton table
            private static CompositeTable mInstance;


            public static CompositeTable getInstance() {
                if (mInstance == null) {
                    mInstance = new GroupBalance();
                }
                return mInstance;
            }

            //This is a composite table that includes all mColumns of the several mTables.
            private GroupBalance() {
                super();
            }

            protected void defineTables() {
                addTable(EXPENSE_TABLE);
                addTable(PAYER_TABLE);
                addTable(MEMBER_TABLE);
                addTable(GROUP_TABLE);

            }
        }
    }

    protected static Metric getBalanceMetric(){
        Column expAmount = TablasContract.Expense.getInstance().getColumn(TablasContract.Expense.AMOUNT);
        Column expExchange = TablasContract.Expense.getInstance().getColumn(TablasContract.Expense.EXCHANGE_RATE);
        Column payPercent = TablasContract.Payer.getInstance().getColumn(TablasContract.Payer.PERCENTAGE);

        ArrayList<Column> columns = new ArrayList();
        columns.add(expAmount);
        columns.add(expExchange);
        columns.add(payPercent);

        StringBuffer expression = new StringBuffer("sum(");
        expression.append(expAmount.getFullName()).
                append(" * ").
                append(expExchange.getFullName()).
                append(" * ").
                append(payPercent.getFullName()).append(")");

        String alias = TablasContract.Expense.USER_BALANCE;

        return new Metric(columns, expression.toString());
    }


}
