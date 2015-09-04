package com.jumo.wepay.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Moha on 6/28/15.
 */
public final class WepayContract {

    private static final String TAG = "WepayContract";
    public static String AUTHORITY = WepayProvider.PROVIDER_AUTHORITY;
    public static String SCHEME = "content";

    //Column definitions
    protected static final String USER_BALANCE = "user_balance";

    //Reusable strings to avoid using multiple instances of these strings;
    protected static final String DT_INTEGER = "integer";
    protected static final String DT_TEXT = "nvarchar(255)";
    protected static final String DT_DOUBLE = "double";
    protected static final String DT_BLOB = "blob";
    protected static final String DEF_PRIMARY_KEY = "primary key";


    private static final HashMap<String, Entity> tableEntityMap = new HashMap<String, Entity>();
    static{
        tableEntityMap.put(Group.TABLE_NAME, Group.table());
        tableEntityMap.put(Expense.TABLE_NAME, Expense.table());
        tableEntityMap.put(Member.TABLE_NAME, Member.table());
        tableEntityMap.put(User.TABLE_NAME, User.table());
        tableEntityMap.put(Recurrence.TABLE_NAME, Recurrence.table());
        tableEntityMap.put(Payer.TABLE_NAME, Payer.table());
        tableEntityMap.put(Location.TABLE_NAME, Location.table());
    }

    public static Entity getEntity(String tableName){
        return tableEntityMap.get(tableName);
    }

    
    //Entities
    public final static class Group extends Entity{
        //GroupCursor Table
        private static final String TABLE_NAME = "groups";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();
        
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String CREATED_ON = "created_on";
        public static final String GROUP_PICTURE = "group_picture";
        public static final String USER_BALANCE = WepayContract.USER_BALANCE; //Not saved in the databased, this represents a user' balance at the group level (or expense level) = the sum of payments.

        //Singleton entity
        private static Entity mInstance;


        public static Entity table(){
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
            columns.put(_ID, new Column(tableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY));
            columns.put(NAME, new Column(tableName, NAME, DT_TEXT, null));
            columns.put(CREATED_ON, new Column(tableName, CREATED_ON, DT_INTEGER, null));
            columns.put(GROUP_PICTURE, new Column(tableName, GROUP_PICTURE, DT_BLOB, null));
        }
    }

    public final static class Expense extends Entity{
        //ExpenseCursor table
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

        //Singleton entity
        private static Entity mInstance;


        public static Entity table(){
            if(mInstance == null){
                mInstance = new Expense();
            }
            return mInstance;
        }

        private Expense(){
            super(TABLE_NAME);
        }

        protected void defineColumnsAndForeignKeys(){
            columns.put(_ID, new Column(tableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY));
            columns.put(GROUP_ID, new Column(tableName, GROUP_ID, DT_INTEGER, "references groups(_id)"));
            columns.put(CREATED_ON, new Column(tableName, CREATED_ON, DT_INTEGER, null));
            columns.put(MESSAGE, new Column(tableName, MESSAGE, DT_TEXT, null));
            columns.put(AMOUNT, new Column(tableName, AMOUNT, DT_DOUBLE, null));
			columns.put(EXCHANGE_RATE, new Column(tableName, EXCHANGE_RATE, DT_DOUBLE, null));  //TODO: Add a clause for having 1 as default exchange rate (this means there is no conversion)
            columns.put(CURRENCY, new Column(tableName, CURRENCY, DT_TEXT, null));
            columns.put(LOCATION_ID, new Column(tableName,LOCATION_ID, DT_INTEGER, "references location(_id)"));
            columns.put(CATEGORY_ID, new Column(tableName, CATEGORY_ID, DT_INTEGER, null));
            columns.put(RECURRENCE_ID, new Column(tableName, RECURRENCE_ID, DT_INTEGER, "references recurrence(_id)"));
            columns.put(GROUP_EXPENSE_ID, new Column(tableName, GROUP_EXPENSE_ID, DT_INTEGER, "references expense(_id)"));
            columns.put(IS_PAYMENT, new Column(tableName, IS_PAYMENT, DT_INTEGER, null)); //boolean 0=false, 1=true
            
            //Foreign keys only to other tables (no recursive relationships included here).
            foreignKeys.put(GROUP_ID, Group.table().columns.get(Group._ID));
            foreignKeys.put(LOCATION_ID, Location.table().columns.get(Location._ID));
            foreignKeys.put(RECURRENCE_ID, Recurrence.table().columns.get(Recurrence._ID));

        }
    }

    public static final class Member extends Entity{
        //MemberCursor Table
        private static final String TABLE_NAME = "member";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();
            
        public static final String _ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String GROUP_ID = "group_id";
        public static final String IS_ADMIN = "is_admin";
        public static final String LEFT_GROUP = "left_group";

        //Singleton entity
        private static Entity mInstance;


        public static Entity table(){
            if(mInstance == null){
                mInstance = new Member();
            }
            return mInstance;
        }

        private Member(){
            super(TABLE_NAME);
        }

        protected void defineColumnsAndForeignKeys(){
            columns.put(_ID, new Column(tableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY));
            columns.put(USER_ID, new Column(tableName, USER_ID, DT_INTEGER, "references user(_id)"));
            columns.put(GROUP_ID, new Column(tableName, GROUP_ID, DT_INTEGER, "references groups(_id)"));
            columns.put(IS_ADMIN, new Column(tableName, IS_ADMIN, DT_INTEGER, null)); //boolean 0=false, 1=true
            columns.put(LEFT_GROUP, new Column(tableName, LEFT_GROUP, DT_INTEGER, null)); //boolean 0=false, 1=true

            //Foreign keys only to other tables (no recursive relationships included here).
            foreignKeys.put(USER_ID, User.table().columns.get(User._ID));
            foreignKeys.put(GROUP_ID, Group.table().columns.get(Group._ID));


        }
    }
    
    public static final class User extends Entity{

        //UserCursor Table
        private static final String TABLE_NAME = "user";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String USER_BALANCE = WepayContract.USER_BALANCE;

        //Singleton entity
        private static Entity mInstance;


        public static Entity table(){
            if(mInstance == null){
                mInstance = new User();
            }
            return mInstance;
        }

        private User(){
            super(TABLE_NAME);
        }

        protected void defineColumnsAndForeignKeys(){
            columns.put(_ID, new Column(tableName, _ID, DT_TEXT, DEF_PRIMARY_KEY));
            columns.put(NAME, new Column(tableName, NAME, DT_TEXT, null));
            columns.put(PHONE, new Column(tableName, PHONE, DT_TEXT, null));
        }

    }
    
    public static final class Recurrence extends Entity{

        //Recurrence Table   
        private static final String TABLE_NAME = "recurrence";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String PERIODICITY = "periodicity";
        public static final String OFFSET = "offset";

        //Singleton entity
        private static Entity mInstance;


        public static Entity table(){
            if(mInstance == null){
                mInstance = new Recurrence();
            }
            return mInstance;
        }

        private Recurrence(){
            super(TABLE_NAME);
        }

        protected void defineColumnsAndForeignKeys(){
            columns.put(_ID, new Column(tableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY));
            columns.put(PERIODICITY, new Column(tableName, PERIODICITY, DT_INTEGER, null));
            columns.put(OFFSET, new Column(tableName, OFFSET, DT_INTEGER, null));
        }
    }

    public static final class Payer extends Entity{

        //PayerCursor Table
        private static final String TABLE_NAME = "payer";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String MEMBER_ID = "member_id";
        public static final String EXPENSE_ID = "expense_id";
        public static final String ROLE = "role";
        public static final String PERCENTAGE = "percentage";

        //Singleton entity
        private static Entity mInstance;


        public static Entity table(){
            if(mInstance == null){
                mInstance = new Payer();
            }
            return mInstance;
        }

        private Payer(){
            super(TABLE_NAME);
        }

        protected void defineColumnsAndForeignKeys(){
            columns.put(_ID, new Column(tableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY));
            columns.put(MEMBER_ID, new Column(tableName, MEMBER_ID, DT_INTEGER, "references member(_id)"));
            columns.put(EXPENSE_ID, new Column(tableName, EXPENSE_ID, DT_INTEGER, "references expense(_id)"));
            columns.put(ROLE, new Column(tableName, ROLE, DT_INTEGER, null));
            columns.put(PERCENTAGE, new Column(tableName, PERCENTAGE, DT_DOUBLE, null));

            //Foreign keys
            foreignKeys.put(MEMBER_ID, Member.table().columns.get(Member._ID));
            foreignKeys.put(EXPENSE_ID, Expense.table().columns.get(Expense._ID));
        }
   
    }

    public static final class Location extends Entity{

        //Location Table
        private static final String TABLE_NAME = "location";
        //protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";

        //Singleton entity
        private static Entity mInstance;


        public static Entity table(){
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
            columns.put(_ID, new Column(tableName, _ID, DT_INTEGER, DEF_PRIMARY_KEY));
            columns.put(NAME, new Column(tableName, NAME, DT_TEXT, null));
            columns.put(LATITUDE, new Column(tableName, LATITUDE, DT_DOUBLE, null));
            columns.put(LONGITUDE, new Column(tableName, LONGITUDE, DT_DOUBLE, null));
        }
    }
    
    public static final class ExpensePayerUsers extends CompositeEntity{

       //Tables added to this table.

        public final static Entity EXPENSE_TABLE = Expense.table();
        public final static Entity PAYER_TABLE = Payer.table();
        public final static Entity USER_TABLE = User.table();
        public final static Entity MEMBER_TABLE = Member.table();

        //Singleton entity
        private static Entity mInstance;


        public static Entity table(){
            if(mInstance == null){
                mInstance = new ExpensePayerUsers();
            }
            return mInstance;
        }

        //This is a composite entity that includes all columns of the several tables.
        private ExpensePayerUsers(){
            super();
        }

        protected void defineTables(){
            tables.add(EXPENSE_TABLE);
            tables.add(PAYER_TABLE);
            tables.add(USER_TABLE);
            tables.add(MEMBER_TABLE);
        }
    }

    public static final class ExpensePayerGroupUsers extends CompositeEntity {

        //Tables added to this table.
        public final static Entity EXPENSE_TABLE = Expense.table();
        public final static Entity PAYER_TABLE = Payer.table();
        public final static Entity USER_TABLE = User.table();
        public final static Entity MEMBER_TABLE = Member.table();
        public final static Entity GROUP_TABLE = Group.table();

        //Singleton entity
        private static Entity mInstance;


        public static Entity table() {
            if (mInstance == null) {
                mInstance = new ExpensePayerUsers();
            }
            return mInstance;
        }

        //This is a composite entity that includes all columns of the several tables.
        private ExpensePayerGroupUsers() {
            super();
        }

        protected void defineTables() {
            tables.add(EXPENSE_TABLE);
            tables.add(PAYER_TABLE);
            tables.add(USER_TABLE);
            tables.add(MEMBER_TABLE);
            tables.add(GROUP_TABLE);
        }
    }

    protected abstract static class CompositeEntity extends Entity{

        public ArrayList<Entity> tables;

        private CompositeEntity(){
            super();
            tables = new ArrayList<Entity>();
            defineTables();
        }

        /**
         * Determine the tables that conform the CompositeEntity by adding references to the tables in the "tables" attribute.
         * Having all tables in one collection will allow the querying layer to be able to join the tables correctly
         */
        protected abstract void defineTables();

        protected void defineColumnsAndForeignKeys(){ return; }

        public Column getColumn(String table, String col){
            Entity tableEntity = WepayContract.getEntity(table);
            if(tableEntity == null)
                return null;

            return tableEntity.getColumn(col);
        }

        @Override
        public Column getColumn(String fullColName){
            String[] colfullName = fullColName.split("\\.");

            if(colfullName.length != 2)
                return null;

            Entity table =  WepayContract.getEntity(colfullName[0]);
            return table.getColumn(colfullName[1]);
        }
    }


    /**
     * Abstract class to represent eventually singleton database entities/tables in a database (singleton part implemented by child classes). Contains set of columns, their spec and their foreign keys.
     */
    protected abstract static class Entity{
        /**
         * Key in the hash map is the column name. The value contains a Column object with all its information.
         */
        public LinkedHashMap<String, Column> columns;
        /**
         * For every local column in the current table, relate the column in the foreign table. 
         */
        public LinkedHashMap<String, Column> foreignKeys;
        public String tableName;


        private Entity(){
            columns = new LinkedHashMap<String, Column>();
            foreignKeys = new LinkedHashMap<String, Column>();
            defineColumnsAndForeignKeys();
        }

        private Entity(String table){
            columns = new LinkedHashMap<String, Column>();
            foreignKeys = new LinkedHashMap<String, Column>();
            tableName = table;
            defineColumnsAndForeignKeys();
        }

        public Column getColumn(String colName){
            return columns.get(colName);
        }

        /**
         * Add information on columns and foreign keys. Call first Entity's constructor, and then
         * add column and foreign key definitions.
         * @return
         */
        protected abstract void defineColumnsAndForeignKeys();

        public String getTableName(){
            return tableName;
        }

    }

    protected static class Column{
        public String name;
        public String datatype;
        public String spec;
        public String table;

        private Column(String dt, String def){
            datatype = dt;
            spec = def;
        }

        private Column(String n, String dt, String def){
            this(dt, def);
            name = n;
        }

        public Column(String t, String n, String dt, String def){
            this(n, dt, def);
            table = t;
        }

        public String getFullName(){
            StringBuffer sb = (new StringBuffer(table)).append(".").append(name);
            return sb.toString();
        }


    }

}
