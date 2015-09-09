package com.jumo.wepay.provider;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Moha on 6/28/15.
 */
public final class WepayContract {

    private static final String TAG = "WepayContract";
    public static String AUTHORITY = WepayProvider.PROVIDER_AUTHORITY;
    public static String SCHEME = "content";
    public static final Uri BASE_URI = new Uri.Builder().scheme(WepayContract.SCHEME).authority(WepayContract.AUTHORITY).build();


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
        tableEntityMap.put(User.TABLE_NAME, User.getInstance());
        tableEntityMap.put(Recurrence.TABLE_NAME, Recurrence.getInstance());
        tableEntityMap.put(Payer.TABLE_NAME, Payer.getInstance());
        tableEntityMap.put(Location.TABLE_NAME, Location.getInstance());
    }

    public static Table getTable(String tableName){
        return tableEntityMap.get(tableName);
    }

    
    //All individual tables representing multiple entities.
    public final static class Group extends Table {
        //GroupCursor Table
        private static final String TABLE_NAME = "groups";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();
        
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String CREATED_ON = "created_on";
        public static final String GROUP_PICTURE = "group_picture";
        public static final String USER_BALANCE = WepayContract.USER_BALANCE; //Not saved in the databased, this represents a user' balance at the group level (or expense level) = the sum of payments.

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

        public static final String _ID = "_id";
        public static final String GROUP_ID = "group_id";
        public static final String CREATED_ON = "created_on";
        public static final String MESSAGE = "message";
        public static final String AMOUNT = "amount";
		public static final String EXCHANGE_RATE = "exchange_rate";
        public static final String CURRENCY = "currency";
        public static final String LOCATION_ID = "location_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String RECURRENCE_ID = "recurrence_id";
        public static final String GROUP_EXPENSE_ID = "group_expense_id";
        public static final String IS_PAYMENT = "is_payment";
        public static final String USER_BALANCE = WepayContract.USER_BALANCE; //Not saved in the databased, this represents a user' balance at the group level (or expense level) = the sum of payments.

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
            mColumns.put(LOCATION_ID, new Column(mTableName, LOCATION_ID, DT_INTEGER, "references location(_id)", Column.TYPE_LONG));
            mColumns.put(CATEGORY_ID, new Column(mTableName, CATEGORY_ID, DT_INTEGER, null, Column.TYPE_LONG));
            mColumns.put(RECURRENCE_ID, new Column(mTableName, RECURRENCE_ID, DT_INTEGER, "references recurrence(_id)", Column.TYPE_LONG));
            mColumns.put(GROUP_EXPENSE_ID, new Column(mTableName, GROUP_EXPENSE_ID, DT_INTEGER, "references expense(_id)", Column.TYPE_LONG));
            mColumns.put(IS_PAYMENT, new Column(mTableName, IS_PAYMENT, DT_INTEGER, null, Column.TYPE_BOOL)); //boolean 0=false, 1=true
            mColumns.put(USER_BALANCE, new Column(mTableName, USER_BALANCE, DT_DOUBLE, null, Column.TYPE_DOUBLE, false));
            
            //Foreign keys only to other mTables (no recursive relationships included here).
            ColumnJoin join = new ColumnJoin(getColumn(GROUP_ID), Group.getInstance().getColumn(Group._ID));
            ArrayList<ColumnJoin> joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(Group.getInstance().getTableName(), joinExp);

            join = new ColumnJoin(getColumn(LOCATION_ID), Location.getInstance().getColumn(Location._ID));
            joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(Location.getInstance().getTableName(), joinExp);

            join = new ColumnJoin(getColumn(RECURRENCE_ID), Recurrence.getInstance().getColumn(Recurrence._ID));
            joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(Recurrence.getInstance().getTableName(),joinExp);

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
            mColumns.put(USER_ID, new Column(mTableName, USER_ID, DT_INTEGER, "references user(_id)", Column.TYPE_LONG));
            mColumns.put(GROUP_ID, new Column(mTableName, GROUP_ID, DT_INTEGER, "references groups(_id)",  Column.TYPE_LONG));
            mColumns.put(IS_ADMIN, new Column(mTableName, IS_ADMIN, DT_INTEGER, null,  Column.TYPE_BOOL)); //boolean 0=false, 1=true
            mColumns.put(LEFT_GROUP, new Column(mTableName, LEFT_GROUP, DT_INTEGER, null,  Column.TYPE_BOOL)); //boolean 0=false, 1=true

            //Foreign keys only to other mTables (no recursive relationships included here).
            ColumnJoin join = new ColumnJoin(getColumn(USER_ID), User.getInstance().getColumn(User._ID));
            ArrayList<ColumnJoin> joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(User.getInstance().getTableName(), joinExp);

            join = new ColumnJoin(getColumn(GROUP_ID), Group.getInstance().getColumn(Group._ID));
            joinExp = new ArrayList<ColumnJoin>();
            joinExp.add(join);
            mForeignKeys.put(Group.getInstance().getTableName(), joinExp);


        }
    }
    
    public static final class User extends Table {

        //UserCursor Table
        private static final String TABLE_NAME = "user";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String USER_BALANCE = WepayContract.USER_BALANCE;

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new User();
            }
            return mInstance;
        }

        private User(){
            super(TABLE_NAME);
        }

        protected void defineColumnsAndForeignKeys(){
            mColumns.put(_ID, new Column(mTableName, _ID, DT_TEXT, DEF_PRIMARY_KEY, Column.TYPE_LONG));
            mColumns.put(NAME, new Column(mTableName, NAME, DT_TEXT, null, Column.TYPE_STRING));
            mColumns.put(PHONE, new Column(mTableName, PHONE, DT_TEXT, null, Column.TYPE_STRING));
        }

    }
    
    public static final class Recurrence extends Table {


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

        //Recurrence Table
        private static final String TABLE_NAME = "recurrence";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String PERIODICITY = "periodicity";
        public static final String OFFSET = "offset";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new Recurrence();
            }
            return mInstance;
        }

        private Recurrence(){
            super(TABLE_NAME);
        }

        protected void defineColumnsAndForeignKeys(){
            mColumns.put(_ID, new Column(mTableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY, Column.TYPE_LONG));
            mColumns.put(PERIODICITY, new Column(mTableName, PERIODICITY, DT_INTEGER, null,  Column.TYPE_INT));
            mColumns.put(OFFSET, new Column(mTableName, OFFSET, DT_INTEGER, null,  Column.TYPE_INT));
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

    public static final class Location extends Table {

        //Location Table
        private static final String TABLE_NAME = "location";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";

        //Singleton table
        private static Table mInstance;


        public static Table getInstance(){
            if(mInstance == null){
                mInstance = new Location();
            }
            return mInstance;
        }
        
        private Location(){
            super(TABLE_NAME);
        }

        @Override
        protected void defineColumnsAndForeignKeys(){
            mColumns.put(_ID, new Column(mTableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY, Column.TYPE_LONG));
            mColumns.put(NAME, new Column(mTableName, NAME, DT_TEXT, null,  Column.TYPE_STRING));
            mColumns.put(LATITUDE, new Column(mTableName, LATITUDE, DT_DOUBLE, null,  Column.TYPE_DOUBLE));
            mColumns.put(LONGITUDE, new Column(mTableName, LONGITUDE, DT_DOUBLE, null,  Column.TYPE_DOUBLE));
        }
    }
    
    public static final class UserExpenseBalance extends CompositeTable {

       //Tables added to this getInstance.
        public final static Table EXPENSE_TABLE = Expense.getInstance();
        public final static Table PAYER_TABLE = Payer.getInstance();
        public final static Table USER_TABLE = User.getInstance();
        public final static Table MEMBER_TABLE = Member.getInstance();

        //Singleton table
        private static CompositeTable mInstance;


        public static CompositeTable getInstance(){
            if(mInstance == null){
                mInstance = new UserExpenseBalance();
            }
            return mInstance;
        }

        //This is a composite table that includes all mColumns of the several mTables.
        private UserExpenseBalance(){
            super();
        }

        protected void defineTables(){
            addTable(EXPENSE_TABLE);
            addTable(PAYER_TABLE);
            addTable(USER_TABLE);
            addTable(MEMBER_TABLE);
        }
    }

    public static final class UserGroupBalance extends CompositeTable {

        //Tables added to this getInstance.
        public final static Table EXPENSE_TABLE = Expense.getInstance();
        public final static Table PAYER_TABLE = Payer.getInstance();
        public final static Table USER_TABLE = User.getInstance();
        public final static Table MEMBER_TABLE = Member.getInstance();
        public final static Table GROUP_TABLE = Group.getInstance();

        //Singleton table
        private static CompositeTable mInstance;


        public static CompositeTable getInstance() {
            if (mInstance == null) {
                mInstance = new UserGroupBalance();
            }
            return mInstance;
        }

        //This is a composite table that includes all mColumns of the several mTables.
        private UserGroupBalance() {
            super();
        }

        protected void defineTables() {
            addTable(EXPENSE_TABLE);
            addTable(PAYER_TABLE);
            addTable(USER_TABLE);
            addTable(MEMBER_TABLE);
            addTable(GROUP_TABLE);

        }
    }

    protected static Metric getBalanceMetric(){
        Column expAmount = WepayContract.Expense.getInstance().getColumn(WepayContract.Expense.AMOUNT);
        Column expExchange = WepayContract.Expense.getInstance().getColumn(WepayContract.Expense.EXCHANGE_RATE);
        Column payPercent = WepayContract.Payer.getInstance().getColumn(WepayContract.Payer.PERCENTAGE);

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

        String alias = WepayContract.Expense.USER_BALANCE;

        return new Metric(columns, expression.toString());
    }


}
