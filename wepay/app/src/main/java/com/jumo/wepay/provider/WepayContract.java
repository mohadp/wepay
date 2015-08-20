package com.jumo.wepay.provider;

import java.util.LinkedHashMap;

/**
 * Created by Moha on 6/28/15.
 */
public final class WepayContract {

    private static final String TAG = "WepayContract";
    public static String AUTHORITY = WepayProvider.PROVIDER_AUTHORITY;
    public static String SCHEME = "content";

    //Column definitions
    protected static final int COL_TYPE = 0;
    protected static final int COL_SPEC = 1;
    protected static final String USER_BALANCE = "user_balance";

    //Tables and columns


    public final static class Group{
        //GroupCursor Table
        public static final String TABLE_NAME = "groups";
        protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();
        
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String CREATED_ON = "created_on";
        public static final String GROUP_PICTURE = "group_picture";
        public static final String USER_BALANCE = WepayContract.USER_BALANCE; //Not saved in the databased, this represents a user' balance at the group level (or expense level) = the sum of payments.

        static{
            COL_DEFS.put(_ID, new String[]{"integer", "primary key"});
            COL_DEFS.put(NAME, new String[]{"nvarchar(100)", null});
            COL_DEFS.put(CREATED_ON, new String[]{"integer", null});
            COL_DEFS.put(GROUP_PICTURE, new String[]{"blob", null});
        }
    }


    public final static class Expense{
        //ExpenseCursor table
        public static final String TABLE_NAME = "expense";
        protected static final LinkedHashMap<String, String[]> COL_DEFS =  new LinkedHashMap<String, String[]>();

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
        
        static {
            COL_DEFS.put(_ID, new String[]{"integer", "primary key"});
            COL_DEFS.put(GROUP_ID, new String[]{"integer", "references groups(_id)"});
            COL_DEFS.put(CREATED_ON, new String[]{"integer", null});
            COL_DEFS.put(MESSAGE, new String[]{"nvarchar(255)", null});
            COL_DEFS.put(AMOUNT, new String[]{"double", null});
			COL_DEFS.put(EXCHANGE_RATE, new String[]{"double", null});  //TODO: Add a clause for having 1 as default exchange rate (this means there is no conversion)
            COL_DEFS.put(CURRENCY, new String[]{"nvarchar(5)", null});
            COL_DEFS.put(LOCATION_ID, new String[]{"integer", "references location(_id)"});
            COL_DEFS.put(CATEGORY_ID, new String[]{"integer", null});
            COL_DEFS.put(RECURRENCE_ID, new String[]{"integer", "references recurrence(_id)"});
            COL_DEFS.put(GROUP_EXPENSE_ID, new String[]{"integer", "references expense(_id)"});
            COL_DEFS.put(IS_PAYMENT, new String[]{"integer", null}); //boolean 0=false, 1=true
        }
    }

    public static final class Member{
        //MemberCursor Table
        public static final String TABLE_NAME = "member";
        protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();        
            
        public static final String _ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String GROUP_ID = "group_id";
        public static final String IS_ADMIN = "is_admin";
        public static final String LEFT_GROUP = "left_group";

        static {
            COL_DEFS.put(_ID, new String[]{"integer", "primary key"});
            COL_DEFS.put(USER_ID, new String[]{"integer", "references user(_id)"});
            COL_DEFS.put(GROUP_ID, new String[]{"integer", "references groups(_id)"});
            COL_DEFS.put(IS_ADMIN, new String[]{"integer", null}); //boolean 0=false, 1=true
            COL_DEFS.put(LEFT_GROUP, new String[]{"integer", null}); //boolean 0=false, 1=true
        }
    }
    
    public static final class User{

        //UserCursor Table
        public static final String TABLE_NAME = "user";
        protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String USER_BALANCE = WepayContract.USER_BALANCE;

        static {
            COL_DEFS.put(_ID, new String[]{"nvarchar(255)", "primary key"});
            COL_DEFS.put(NAME, new String[]{"nvarchar(255)", null});
            COL_DEFS.put(PHONE, new String[]{"nvarchar(255)", null});
        }

    }
    
    public static final class Recurrence{

        //Recurrence Table   
        public static final String TABLE_NAME = "recurrence";
        protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String PERIODICITY = "periodicity";
        public static final String OFFSET = "offset";
        
        static{
            COL_DEFS.put(_ID, new String[]{"integer", "primary key"});
            COL_DEFS.put(PERIODICITY, new String[]{"integer", null});
            COL_DEFS.put(OFFSET, new String[]{"integer", null});
        }
    }

    public static final class Payer{

        //PayerCursor Table
        public static final String TABLE_NAME = "payer";
        protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String MEMBER_ID = "member_id";
        public static final String EXPENSE_ID = "expense_id";
        public static final String ROLE = "role";
        public static final String PERCENTAGE = "percentage";

        static {
            COL_DEFS.put(_ID, new String[]{"integer", "primary key"});
            COL_DEFS.put(MEMBER_ID, new String[]{"integer", "references member(_id)"});
            COL_DEFS.put(EXPENSE_ID, new String[]{"integer", "references expense(_id)"});
            COL_DEFS.put(ROLE, new String[]{"integer", null});
            COL_DEFS.put(PERCENTAGE, new String[]{"double", null});
        }
   
    }

    public static final class Location{


        //Location Table
        public static final String TABLE_NAME = "location";
        protected static final LinkedHashMap<String, String[]> COL_DEFS = new LinkedHashMap<String, String[]>();

        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";

        static{
            COL_DEFS.put(_ID, new String[]{"integer", "primary key"});
            COL_DEFS.put(NAME, new String[]{"nvarchar(255)", null});
            COL_DEFS.put(LATITUDE, new String[]{"real", null});
            COL_DEFS.put(LONGITUDE, new String[]{"real", null});
        }
    }
}
