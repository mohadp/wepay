package com.jumo.tablas.common;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.jumo.tablas.model.Categories;
import com.jumo.tablas.model.Expense;
import com.jumo.tablas.model.Group;
import com.jumo.tablas.model.Member;
import com.jumo.tablas.model.Payer;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.EntityCursor;

import java.util.ArrayList;
import java.util.Date;

import android.database.*;
import android.util.Log;

/**
 * Created by Moha on 7/10/15.
 */
public class TablasManager {

    private static final String TAG = "TablasManager";

    private static final String CURRENT_USER = "+17036566202";

    private Context mContext;
    private Uri baseUri;
    private static TablasManager sampleDataUtil;

    private String[] mUsers = {"+17036566202", "+17037173160", "+17036566203",
            "+19192188457", "+5215534007789", "+5215540840084", "+5215513725485",
            "+12026790071", "+16504557014"};


    private TablasManager(Context context){
        mContext = context;
        baseUri = new Uri.Builder().scheme(TablasContract.SCHEME).authority(TablasContract.AUTHORITY).build();
        //String uriString = baseUri.toString();
        //Log.d(TAG, baseUri.toString());
        //createSampleData();

    }

    public static TablasManager newInstance(Context context){
        if(sampleDataUtil == null){
            sampleDataUtil = new TablasManager(context);
        }
        return sampleDataUtil;
    }

    public void createSampleData(){
        ContentResolver content = mContext.getContentResolver();

        Uri seeGroups = baseUri.buildUpon().appendPath(TablasContract.Group.getInstance().getTableName()).build();
        Cursor groupsCursor = content.query(seeGroups, null, null, null, null);

        Log.d(TAG, "Group Cursor size: " + groupsCursor.getCount());

        if(groupsCursor == null || groupsCursor.getCount() == 0){
        	deleteAllData();
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            createGroupsMembersExpensesPayers(ops);

            try{
                content.applyBatch(TablasContract.AUTHORITY, ops);
            }catch(Exception e){
                Log.e(TAG, "Could not create sample data because of exception", e);
            }
        }
        groupsCursor.close();

        EntityCursor groupCursor = new EntityCursor(content.query(seeGroups, null, null, null, null));
        String groupStr = groupCursor.toString(TablasContract.Group.getInstance());

        EntityCursor memberCursor = new EntityCursor(content.query(baseUri.buildUpon().appendPath(TablasContract.Member.getInstance().getTableName()).build() ,null, null, null, null));
        String memberStr = memberCursor.toString(TablasContract.Member.getInstance());

        EntityCursor expenseCursor = new EntityCursor(content.query(baseUri.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName()).build() ,null, null, null, null));
        String expenseStr = expenseCursor.toString(TablasContract.Expense.getInstance());

        EntityCursor payerCursor = new EntityCursor(content.query(baseUri.buildUpon().appendPath(TablasContract.Payer.getInstance().getTableName()).build() ,null, null, null, null));
        String payerStr = payerCursor.toString(TablasContract.Payer.getInstance());


        groupCursor.close();
        memberCursor.close();
        expenseCursor.close();
        payerCursor.close();


    }

    public void createGroupsMembersExpensesPayers(ArrayList<ContentProviderOperation> ops){
        int newGroupIndex = ops.size();
        Uri groupTable = baseUri.buildUpon().appendPath(TablasContract.Group.getInstance().getTableName()).build();

        for(int i = 0; i < 10; i++){
            Group group = new Group();
            //group.setId(time + i);
            group.setCreatedOn(new Date());
            group.setName("Group " + i);

            ContentProviderOperation op = ContentProviderOperation.newInsert(groupTable)
                    .withValue(TablasContract.Group.GROUP_CREATED_ON, group.getCreatedOn().getTime())
                    .withValue(TablasContract.Group.GROUP_NAME, group.getName())
                    .build();
            ops.add(op);
            createMembersExpensesPayers(ops, ops.size() - 1);
        }
    }

    public void createMembersExpensesPayers(ArrayList<ContentProviderOperation> ops, int groupOpIndex){
        Uri memberTable = baseUri.buildUpon().appendPath(TablasContract.Member.getInstance().getTableName()).build();

        int noMembers = 2 + ((int)(Math.round(Math.random()*100)) % 4); //Groups have 5 or less members... creating 5 members
        int startAddingFromPos = (int)(Math.round(Math.random()*100)) % mUsers.length; //A random position within the user list. Start adding users from a starting index

        int firstMember = ops.size(); //index of the first member for this group
        for(int i=0; i < noMembers; i++ ){
            Member member = new Member();
            //member.setGroupId(group.getId()); //GroupCursor id of the current group.
            member.setAdmin(i == 0); // Only the first added user is the admin now
            member.setLeftGroup(false);
            //Add user id
            int addUserAt = (startAddingFromPos + i) % mUsers.length; //make sure the index falls within the user's list
            member.setUserId(mUsers[addUserAt]);

            //Insert member into the database.
            ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(memberTable)
                    .withValueBackReference(TablasContract.Member.MEMBER_GROUP_ID, groupOpIndex)
                    .withValue(TablasContract.Member.MEMBER_IS_ADMIN, member.isAdmin() ? 1 : 0)
                    .withValue(TablasContract.Member.MEMBER_LEFT_GROUP, member.hasLeftGroup() ? 1 : 0)
                    .withValue(TablasContract.Member.MEMBER_USER_ID, member.getUserId())
                    .withValue(TablasContract.Member.MEMBER_IS_CURR_USR, member.isCurrentUser()? 1 : 0);
            ops.add(op.build());
        }
        int lastMember = ops.size() - 1; //index of the last member for this group.
        createExpenses(ops, groupOpIndex, firstMember, lastMember);
    }


    public void createExpenses(ArrayList<ContentProviderOperation> ops, int groupOpIndex, int firstMember, int lastMember){
        //get number of expenses: how many?
        Uri expenseTable = baseUri.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName()).build();
        int noExpenses = (int)(Math.round(Math.random()*100)) % 15 + 5; //Groups will have 15 or less members
        //Decide the amount

        for(int i = 0 ; i < noExpenses; i++){
            Expense expense = new Expense();
            //expense.setId(groupId*100 + i); //Id is the group id, plus the for index.
            //expense.setGroupId(groupId); //THIS
            expense.setPayment(false);
            expense.setAmount(Math.random()*100); // amount is any number from 0 to 100.
            expense.setExchangeRate(1);
            expense.setMessage("Message " + i);
            expense.setCreatedOn(new Date());
            expense.setCurrencyId("USD");
            //include category
            int categoryId = ((int)Math.round(Math.random() * 100)) % Categories.getInstance().size();
            expense.setCategoryId(categoryId);

            //Insert member into the database.
            //Insert member into the database.
            ContentProviderOperation op = ContentProviderOperation.newInsert(expenseTable)
                    .withValueBackReference(TablasContract.Expense.EXPENSE_GROUP_ID, groupOpIndex)
                    .withValue(TablasContract.Expense.EXPENSE_IS_PAYMENT, expense.isPayment()? 1 : 0)
                    .withValue(TablasContract.Expense.EXPENSE_AMOUNT, expense.getAmount())
                    .withValue(TablasContract.Expense.EXPENSE_EXCHANGE_RATE, expense.getExchangeRate())
                    .withValue(TablasContract.Expense.EXPENSE_MESSAGE, expense.getMessage())
                    .withValue(TablasContract.Expense.EXPENSE_CREATED_ON, expense.getCreatedOn().getTime())
                    .withValue(TablasContract.Expense.EXPENSE_CURRENCY, expense.getCurrencyId())
                    .withValue(TablasContract.Expense.EXPENSE_CATEGORY_ID, expense.getCategoryId())
                    .build();
            ops.add(op);
            createPayers(ops, ops.size() - 1, firstMember, lastMember);

        }
    }

    public void createPayers(ArrayList<ContentProviderOperation> ops, int expenseOpIndex, int firstMember, int lastMember){
        Uri payerTable = baseUri.buildUpon().appendPath(TablasContract.Payer.getInstance().getTableName()).build();
        int noMembers = lastMember - firstMember + 1;
        double percentagePerPerson = 1d / noMembers;
        int currentMember = 0;

        //figure out who the payer is for this expense; will make this random, any of the members
        int payer = ((int)Math.round(Math.random()*100)) % noMembers;

        for (int i = firstMember; i <= lastMember; i++) {

            //Add payer as OPTION_ROLE_SHOULD_PAY
            Payer payerShouldPay = new Payer();
            payerShouldPay.setRole(TablasContract.Payer.OPTION_ROLE_SHOULD_PAY);
            payerShouldPay.setPercentage(percentagePerPerson);

            ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(payerTable)
                    .withValueBackReference(TablasContract.Payer.PAYER_MEMBER_ID, i)
                    .withValueBackReference(TablasContract.Payer.PAYER_EXPENSE_ID, expenseOpIndex)
                    .withValue(TablasContract.Payer.PAYER_ROLE, payerShouldPay.getRole())
                    .withValue(TablasContract.Payer.PAYER_PERCENTAGE, payerShouldPay.getPercentage());

            if(currentMember != payer && i == lastMember){
                op.withYieldAllowed(true);
            }
            ops.add(op.build());

            //Make only a member to pay all
            Payer payerPaid = null;
            if(currentMember == payer /*addAsPayerToo*/) {
                payerPaid = new Payer();
                payerPaid.setRole(TablasContract.Payer.OPTION_ROLE_PAID);
                payerPaid.setPercentage(1);

                ContentProviderOperation.Builder op2 = ContentProviderOperation.newInsert(payerTable)
                        .withValueBackReference(TablasContract.Payer.PAYER_MEMBER_ID, i)
                        .withValueBackReference(TablasContract.Payer.PAYER_EXPENSE_ID, expenseOpIndex)
                        .withValue(TablasContract.Payer.PAYER_ROLE, payerPaid.getRole())
                        .withValue(TablasContract.Payer.PAYER_PERCENTAGE, payerPaid.getPercentage());

                if(i == lastMember){
                    op2.withYieldAllowed(true);
                }
                ops.add(op2.build());
            }
            currentMember++;
        }
    }

    public void deleteAllData(){
        ContentResolver content = mContext.getContentResolver();
        content.delete(baseUri.buildUpon().appendPath(TablasContract.Payer.getInstance().getTableName()).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName()).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(TablasContract.Member.getInstance().getTableName()).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(TablasContract.Group.getInstance().getTableName()).build(), null, null);
    }

}
