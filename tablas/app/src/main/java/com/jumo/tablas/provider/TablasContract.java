package com.jumo.tablas.provider;

import android.net.Uri;

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
    }

    public static Table getTable(String tableName){
        return tableEntityMap.get(tableName);
    }

    
    //All individual tables representing multiple entities.
    public final static class Group extends Table implements GroupColumns{
        //GroupCursor Table
        public static final String TABLE_NAME = "groups";
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
    }

    public final static class Expense extends Table implements ExpenseColumns{
        //ExpenseCursor getInstance
        public static final String TABLE_NAME = "expense";
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
            addColumn(EXPENSE_GROUP_ID, new Column(getTableName(), EXPENSE_GROUP_ID, Column.DB_TYPE_INTEGER, "references groups(_id)", Column.INTERNAL_TYPE_LONG));
            addColumn(EXPENSE_CREATED_ON, new Column(getTableName(), EXPENSE_CREATED_ON, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_DATE));
            addColumn(EXPENSE_MESSAGE, new Column(getTableName(), EXPENSE_MESSAGE, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));
            addColumn(EXPENSE_AMOUNT, new Column(getTableName(), EXPENSE_AMOUNT, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));
			addColumn(EXPENSE_EXCHANGE_RATE, new Column(getTableName(), EXPENSE_EXCHANGE_RATE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));  //TODO: Add a clause for having 1 as default exchange rate (this means there is no conversion)
            addColumn(EXPENSE_CURRENCY, new Column(getTableName(), EXPENSE_CURRENCY, Column.DB_TYPE_TEXT, null, Column.INTERNAL_TYPE_STRING));
            addColumn(EXPENSE_CATEGORY_ID, new Column(getTableName(), EXPENSE_CATEGORY_ID, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_LONG));
            addColumn(EXPENSE_LATITUDE, new Column(getTableName(), EXPENSE_LATITUDE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));
            addColumn(EXPENSE_LONGITUDE, new Column(getTableName(), EXPENSE_LONGITUDE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));
            addColumn(EXPENSE_IS_PAYMENT, new Column(getTableName(), EXPENSE_IS_PAYMENT, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true
            addColumn(EXPENSE_GROUP_EXPENSE_ID, new Column(getTableName(), EXPENSE_GROUP_EXPENSE_ID, Column.DB_TYPE_INTEGER, "references expense(_id)", Column.INTERNAL_TYPE_LONG));
            addColumn(EXPENSE_PERIODICITY, new Column(getTableName(), EXPENSE_PERIODICITY, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT));
            addColumn(EXPENSE_OFFSET, new Column(getTableName(), EXPENSE_OFFSET, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT));

            
            //Foreign keys only to other mTables (no recursive relationships included here).
            ColumnJoin join = new ColumnJoin(getColumn(EXPENSE_GROUP_ID), Group.getInstance().getColumn(Group._ID));
            LinkedHashSet<ColumnJoin> joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(Group.getInstance().getTableName(), joinExp);
        }
    }

    public static final class Member extends Table implements MemberColumns{
        //MemberCursor Table
        public static final String TABLE_NAME = "member";
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
            addColumn(MEMBER_GROUP_ID, new Column(getTableName(), MEMBER_GROUP_ID, Column.DB_TYPE_INTEGER, "references groups(_id)", Column.INTERNAL_TYPE_LONG));
            addColumn(MEMBER_IS_ADMIN, new Column(getTableName(), MEMBER_IS_ADMIN, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true
            addColumn(MEMBER_LEFT_GROUP, new Column(getTableName(), MEMBER_LEFT_GROUP, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true
            addColumn(MEMBER_IS_CURR_USR, new Column(getTableName(), MEMBER_IS_CURR_USR, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_BOOL)); //boolean 0=false, 1=true

            //Foreign keys only to other mTables (no recursive relationships included here).

            ColumnJoin join = new ColumnJoin(getColumn(MEMBER_GROUP_ID), Group.getInstance().getColumn(Group._ID));
            LinkedHashSet<ColumnJoin> joinExp = new LinkedHashSet<ColumnJoin>();
            joinExp.add(join);
            addForeignTable(Group.getInstance().getTableName(), joinExp);


        }
    }

    public static final class Payer extends Table implements PayerColumns {

        //Types of roles for Payer
        public static final int OPTION_ROLE_PAID = 0;
        public static final int OPTION_ROLE_SHOULD_PAY = 1;

        //PayerCursor Table
        public static final String TABLE_NAME = "payer";
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
            addColumn(PAYER_MEMBER_ID, new Column(getTableName(), PAYER_MEMBER_ID, Column.DB_TYPE_INTEGER, "references member(_id)", Column.INTERNAL_TYPE_LONG));
            addColumn(PAYER_EXPENSE_ID, new Column(getTableName(), PAYER_EXPENSE_ID, Column.DB_TYPE_INTEGER, "references expense(_id)", Column.INTERNAL_TYPE_LONG));
            addColumn(PAYER_ROLE, new Column(getTableName(), PAYER_ROLE, Column.DB_TYPE_INTEGER, null, Column.INTERNAL_TYPE_INT));
            addColumn(PAYER_PERCENTAGE, new Column(getTableName(), PAYER_PERCENTAGE, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE));

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
   
    }

    public static final class Compound{

        public static final class ExpenseBalance extends CompositeTable implements ExpenseColumns, PayerColumns, MemberColumns {
            public static final String TABLE_NAME = "ExpenseBalance";

           //Tables added to this getInstance.
            private final static Table EXPENSE_TABLE = Expense.getInstance();
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
            public static final String USER_BALANCE = "user_balance";

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

            private ExpenseBalance(String tableName){
                super(tableName);
            }


            @Override
            protected void addMetrics(){
                addMetric(TablasContract.getUserBalanceMetric(USER_BALANCE));
            }

            @Override
            protected void addTablesToCompositeTable(){
                addTable(EXPENSE_TABLE);
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
                return tree;
            }
        }

        public static final class GroupBalance extends CompositeTable implements GroupColumns, ExpenseColumns, PayerColumns, MemberColumns {
            public static final String TABLE_NAME = "GroupBalance";

            //Tables added to this getInstance.
            private final static Table GROUP_TABLE = Group.getInstance();
            private final static Table EXPENSE_TABLE = Expense.getInstance();
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
                    mInstance = new GroupBalance();
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
                addTable(PAYER_TABLE);
                addTable(EXPENSE_TABLE);
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

                TreeNode.findFirstCommonParent(tree.findTableNode(MEMBER_TABLE.getTableName()), tree.findTableNode(PAYER_TABLE.getTableName()))
                        .setJoinType(TreeNode.LEFT_OUTER_JOIN);
                return tree;
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
    }

    /**
     * Set of columns for the Expense table.
     */
    public interface ExpenseColumns{
        public static final String EXPENSE_GROUP_ID = "group_id";
        public static final String EXPENSE_CREATED_ON = "exp_created_on";
        public static final String EXPENSE_MESSAGE = "message";
        public static final String EXPENSE_AMOUNT = "amount";
        public static final String EXPENSE_EXCHANGE_RATE = "exchange_rate";
        public static final String EXPENSE_CURRENCY = "currency";
        public static final String EXPENSE_CATEGORY_ID = "category_id";
        public static final String EXPENSE_LATITUDE = "latitude";
        public static final String EXPENSE_LONGITUDE = "longitude";
        public static final String EXPENSE_IS_PAYMENT = "is_payment";
        public static final String EXPENSE_GROUP_EXPENSE_ID = "group_exp_id";
        public static final String EXPENSE_PERIODICITY = "periodicity";
        public static final String EXPENSE_OFFSET = "offset";
    }

    /**
     * Set of columns for the Member table.
     */
    public interface MemberColumns{
        public static final String MEMBER_USER_ID = "user_id";
        public static final String MEMBER_GROUP_ID = "group_id";
        public static final String MEMBER_IS_ADMIN = "is_admin";
        public static final String MEMBER_LEFT_GROUP = "left_group";
        public static final String MEMBER_IS_CURR_USR = "is_curr_usr";
    }

    /**
     * Set of columns for the Payer table.
     */
    public interface PayerColumns{
        public static final String PAYER_MEMBER_ID = "member_id";
        public static final String PAYER_EXPENSE_ID = "expense_id";
        public static final String PAYER_ROLE = "pay_role";
        public static final String PAYER_PERCENTAGE = "percentage";
    }

    protected static Metric getUserBalanceMetric(String name){
        Column expAmount = TablasContract.Expense.getInstance().getColumn(TablasContract.Expense.EXPENSE_AMOUNT);
        Column expExchange = TablasContract.Expense.getInstance().getColumn(TablasContract.Expense.EXPENSE_EXCHANGE_RATE);
        Column payPercent = TablasContract.Payer.getInstance().getColumn(TablasContract.Payer.PAYER_PERCENTAGE);

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

        Column metricCol = new Column(null, name, Column.DB_TYPE_DOUBLE, null, Column.INTERNAL_TYPE_DOUBLE, Column.IS_METRIC);

        return new Metric(columns, expression.toString(), Metric.IS_AGGREGATION, metricCol);
    }

    protected static Metric getIsCurrentUserMetric(String name){

        return null;
    }

    protected static Metric getNumberOfPayingPayers(String name){

        return null;
    }



}
