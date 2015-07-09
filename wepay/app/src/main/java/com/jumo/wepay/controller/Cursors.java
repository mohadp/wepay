package com.jumo.wepay.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.wepay.model.Expense;
import com.jumo.wepay.model.Group;
import com.jumo.wepay.model.Member;
import com.jumo.wepay.model.Payer;
import com.jumo.wepay.model.User;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Moha on 7/4/15.
 */
public class Cursors {

    public static class GroupCursor extends CursorWrapper {

        public GroupCursor(Cursor c) {
            super(c);
        }

        public Group getGroup() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            Group group = new Group();
            int colIndex = -1;

            if((colIndex = getColumnIndex(WepayContract.Group._ID)) >= 0) group.setId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Group.NAME)) >= 0) group.setName(getString(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Group.CREATED_ON)) >= 0) group.setCreatedOn(new Date(getLong(colIndex)));
            if((colIndex = getColumnIndex(WepayContract.Group.GROUP_PICTURE)) >= 0) group.setGroupPicture(getBlob(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Group.USER_BALANCE)) >= 0) group.setUserBalance(getDouble(colIndex));

            return group;

        }

        public static ContentValues toContentValue(Group g){
            ContentValues cv = new ContentValues();

            if(g.getId() != 0) cv.put(WepayContract.Group._ID, g.getId());
            if(g.getName() != null) cv.put(WepayContract.Group.NAME, g.getName());
            if(g.getCreatedOn() != null) cv.put(WepayContract.Group.CREATED_ON, g.getCreatedOn().getTime());
            if(g.getGroupPicture() != null) cv.put(WepayContract.Group.GROUP_PICTURE, g.getGroupPicture());

            if(cv.size() == 0) return null;

            return cv;
        }
    }

    public static class MemberCursor extends CursorWrapper {

        public MemberCursor(Cursor c){
            super(c);
        }

        public Member getMember() {
            if(isBeforeFirst() || isAfterLast())
                return null;
            Member member = new Member();
            int colIndex = -1;

            if((colIndex = getColumnIndex(WepayContract.Member.GROUP_ID)) >= 0) member.setGroupId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Member._ID)) >= 0) member.setId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Member.IS_ADMIN)) >= 0) member.setAdmin(getInt(colIndex) != 0);
            if((colIndex = getColumnIndex(WepayContract.Member.LEFT_GROUP)) >= 0) member.setLeftGroup(getInt(colIndex) != 0);
            if((colIndex = getColumnIndex(WepayContract.Member.USER_ID)) >= 0) member.setUserId(getString(colIndex));

            return member;
        }

        public static ContentValues toContentValue(Member m){
            ContentValues cv = new ContentValues();

            if(m.getId() != 0) cv.put(WepayContract.Member._ID, m.getId());
            if(m.getGroupId() != 0) cv.put(WepayContract.Member.GROUP_ID, m.getGroupId());
            if(m.getUserId() != null) cv.put(WepayContract.Member.USER_ID, m.getUserId());
            cv.put(WepayContract.Member.IS_ADMIN, m.isAdmin()? 1 : 0 );
            cv.put(WepayContract.Member.LEFT_GROUP, m.hasLeftGroup()? 1 : 0);

            if(cv.size() == 0) return null;

            return cv;
        }
    }

    public static class PayerCursor extends CursorWrapper{

        public PayerCursor(Cursor c){
            super(c);
        }

        public Payer getPayer(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }
            Payer payer = new Payer();
            int colIndex = -1;

            if((colIndex = getColumnIndex(WepayContract.Payer._ID)) >= 0) payer.setId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Payer.EXPENSE_ID)) >= 0) payer.setExpenseId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Payer.MEMBER_ID)) >= 0) payer.setMemberId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Payer.PERCENTAGE)) >= 0) payer.setPercentage(getDouble(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Payer.ROLE)) >= 0) payer.setRole(getInt(colIndex));
            return payer;
        }

        public static ContentValues toContentValues(Payer p){
            ContentValues cv = new ContentValues();

            if(p.getId() != 0) cv.put(WepayContract.Payer._ID, p.getId());
            if(p.getExpenseId() != 0) cv.put(WepayContract.Payer.EXPENSE_ID, p.getExpenseId());
            if(p.getMemberId() != 0) cv.put(WepayContract.Payer.MEMBER_ID, p.getMemberId());
            cv.put(WepayContract.Payer.PERCENTAGE, p.getPercentage());
            cv.put(WepayContract.Payer.ROLE, p.getRole());

            if(cv.size() == 0) return null;

            return cv;
        }
    }

    public static class UserCursor extends CursorWrapper{

        public UserCursor(Cursor c){
            super(c);
        }

        public User getUser(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }
            User user = new User();
            int colIndex = -1;

            if((colIndex = getColumnIndex(WepayContract.User._ID)) >= 0) user.setId(getString(colIndex));
            if((colIndex = getColumnIndex(WepayContract.User.NAME)) >= 0)user.setName(getString(colIndex));
            if((colIndex = getColumnIndex(WepayContract.User.PHONE)) >= 0)user.setPhone(getLong(colIndex));
            return user;
        }

        public static ContentValues toContentValues(User u){
            ContentValues cv = new ContentValues();

            if(u.getId() != null) cv.put(WepayContract.User._ID, u.getId());
            if(u.getName() != null) cv.put(WepayContract.User.NAME, u.getName());
            cv.put(WepayContract.User.NAME, u.getPhone());

            if(cv.size() == 0) return null;

            return cv;
        }
    }

    public static class ExpenseCursor extends CursorWrapper{

        public ExpenseCursor(Cursor c){
            super(c);
        }

        public Expense getExpense(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }
            Expense expense = new Expense();
            int colIndex = -1;

            if((colIndex = getColumnIndex(WepayContract.Expense._ID)) >= 0) expense.setId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.GROUP_ID)) >= 0)expense.setGroupId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.CATEGORY_ID)) >= 0)expense.setCategoryId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.LOCATION_ID)) >= 0)expense.setLocationId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.RECURRENCE_ID)) >= 0)expense.setRecurrenceId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.GROUP_EXPENSE_ID)) >= 0)expense.setGroupExpenseId(getLong(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.CURRENCY)) >= 0)expense.setCurrencyId(getString(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.CREATED_ON)) >= 0)expense.setCreatedOn(new Date(getLong(colIndex)));
            if((colIndex = getColumnIndex(WepayContract.Expense.MESSAGE)) >= 0)expense.setMessage(getString(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.AMOUNT)) >= 0)expense.setAmount(getDouble(colIndex));
            if((colIndex = getColumnIndex(WepayContract.Expense.IS_PAYMENT)) >= 0)expense.setPayment(getInt(colIndex) != 0);
            if((colIndex = getColumnIndex(WepayContract.Group.USER_BALANCE)) >= 0) expense.setUserBalance(getDouble(colIndex));

            return expense;
        }

        public static ContentValues toContentValues(Expense e){
            ContentValues cv = new ContentValues();

            if(e.getId() != 0) cv.put(WepayContract.Expense._ID, e.getId());
            if(e.getGroupId() != 0) cv.put(WepayContract.Expense.GROUP_ID, e.getGroupId());
            if(e.getCategoryId() != 0) cv.put(WepayContract.Expense.CATEGORY_ID, e.getCategoryId());
            if(e.getLocationId() != 0) cv.put(WepayContract.Expense.LOCATION_ID, e.getLocationId());
            if(e.getRecurrenceId() != 0) cv.put(WepayContract.Expense.RECURRENCE_ID, e.getRecurrenceId());
            if(e.getGroupExpenseId() != 0) cv.put(WepayContract.Expense.GROUP_EXPENSE_ID, e.getGroupExpenseId());
            if(e.getCurrencyId() != null) cv.put(WepayContract.Expense.CURRENCY, e.getCurrencyId());
            if(e.getCreatedOn() != null) cv.put(WepayContract.Expense.CREATED_ON, e.getCreatedOn().getTime());
            if(e.getMessage() != null) cv.put(WepayContract.Expense.MESSAGE, e.getMessage());
            cv.put(WepayContract.Expense.AMOUNT, e.getAmount());
            cv.put(WepayContract.Expense.IS_PAYMENT, e.isPayment()? 1 : 0);

            if(cv.size() == 0) return null;

            return cv;
        }
    }

/*    public static <T> ContentValues toContentValues(T entity){
        ContentValues cv = new ContentValues();
        Field[] fields = entity.getClass().getDeclaredFields();

        for(Field field : fields){
            String columnName = null;
            if(field.getName() == "id") {
                columnName = "_id";
            }else{
                columnName = toColumnNameFormat(field.getName());
            }

            Type type = field.getType();
            if(type.equals(int.class)){

            }else if(type.equals(long.class)){

            }else if(type.equals(boolean.class)){

            }else if(type.equals(double.class)){

            }else if(type.equals(float.class)){

            }else if(type.equals(String.class)){

            }

                //cv.put("_id", )
        }


        return null;
    }


    private static String toColumnNameFormat(String colName){
        Pattern pattern = Pattern.compile("\\p{javaUpperCase}");
        Matcher matcher = pattern.matcher(colName);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            String replacement = "_"+ matcher.group(0).toLowerCase();
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
*/

}
