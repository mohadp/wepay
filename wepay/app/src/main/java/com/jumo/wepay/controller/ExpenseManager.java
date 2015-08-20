package com.jumo.wepay.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.jumo.wepay.model.Categories;
import com.jumo.wepay.model.Expense;
import com.jumo.wepay.model.Group;
import com.jumo.wepay.model.Member;
import com.jumo.wepay.model.User;
import com.jumo.wepay.model.Payer;
import com.jumo.wepay.provider.dao.EntityDao;
import com.jumo.wepay.provider.WepayContract;
import com.jumo.wepay.provider.dao.GroupCursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import android.util.Log;
import android.widget.*;
import android.database.*;
import com.jumo.wepay.provider.dao.*;

/**
 * Created by Moha on 7/10/15.
 */
public class ExpenseManager {

    private static final String TAG = "ExpenseManager";


    private Context mContext;
    private Uri baseUri;
    private static ExpenseManager expenseManager;

    private ExpenseManager(Context context){
        mContext = context;
        baseUri = new Uri.Builder().scheme(WepayContract.SCHEME).authority(WepayContract.AUTHORITY).build();
        //String uriString = baseUri.toString();
        //Log.d(TAG, baseUri.toString());
        //createSampleData();

    }

    public static ExpenseManager newInstance(Context context){
        if(expenseManager == null){
            expenseManager = new ExpenseManager(context);
        }
        return expenseManager;
    }

    public GroupCursor getUserGroups(String userId){
        ContentResolver content = mContext.getContentResolver();
        Uri uri = baseUri.buildUpon().appendPath(WepayContract.User.TABLE_NAME)
                .appendPath(userId).appendPath("groups")
                .build();

        return new GroupCursor(content.query(uri, null, null, null, null));
    }
	
	public ExpenseCursor getUserGroupExpenses(String userId, long groupId){
		ContentResolver content = mContext.getContentResolver();
		Uri uri = baseUri.buildUpon().appendPath(WepayContract.Expense.TABLE_NAME)
				.appendPath("user").appendPath(userId).appendPath("group").appendPath(Long.toString(groupId))
				.build();
		
		StringBuilder sortBy = new StringBuilder(WepayContract.Expense.CREATED_ON).append(" ASC");
		
		return new ExpenseCursor(content.query(uri, null, null, null, sortBy.toString()));
	}

    public void createSampleData(){
        ContentResolver content = mContext.getContentResolver();

        Uri seeGroups = baseUri.buildUpon().appendPath(WepayContract.Group.TABLE_NAME).build();
        Cursor groupsCursor = content.query(seeGroups, null, null, null, null);
		

        //boolean afterLast = groupsCursor.isAfterLast();
        //boolean beforeFirst = groupsCursor.isBeforeFirst();


        if(groupsCursor == null || groupsCursor.getCount() == 0){
        	deleteAllData();
		
		//Toast.makeText(mContext, "Data Deleted", Toast.LENGTH_SHORT);
		
        	ArrayList<User> newUsers = createSampleUsers();
        	ArrayList<Group> newGroups = createSampleGroups();
        	HashMap<Long, ArrayList<Member>> newGroupMembers = createSampleMembers(newUsers, newGroups);
        	HashMap<Long, ArrayList<Expense>> newGroupExpenses = createSampleExpenses(newGroupMembers);
        	createSamplePayers(newGroupMembers, newGroupExpenses);
        }

        /*EntityDao.UserCursor userCursor = new EntityDao.UserCursor(content.query(baseUri.buildUpon().appendPath(WepayContract.User.TABLE_NAME).build() ,null, null, null, null));
        Log.d(TAG, userCursor.toString());

        EntityDao.GroupCursor groupCursor = new EntityDao.GroupCursor(content.query(baseUri.buildUpon().appendPath(WepayContract.Group.TABLE_NAME).build() ,null, null, null, null));
        Log.d(TAG, groupCursor.toString());

        EntityDao.MemberCursor memberCursor = new EntityDao.MemberCursor(content.query(baseUri.buildUpon().appendPath(WepayContract.Member.TABLE_NAME).build() ,null, null, null, null));
        Log.d(TAG, memberCursor.toString());

        EntityDao.ExpenseCursor expenseCursor = new EntityDao.ExpenseCursor(content.query(baseUri.buildUpon().appendPath(WepayContract.Expense.TABLE_NAME).build() ,null, null, null, null));
        Log.d(TAG, expenseCursor.toString());

        PayerCursor payerCursor = new PayerCursor(content.query(baseUri.buildUpon().appendPath(WepayContract.Payer.TABLE_NAME).build() ,null, null, null, null));
        Log.d(TAG, payerCursor.toString());
        */

    }


    public void synchronizeUserData(String userId){

    }

    public void deleteAllData(){
        ContentResolver content = mContext.getContentResolver();
        content.delete(baseUri.buildUpon().appendPath(WepayContract.Payer.TABLE_NAME).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(WepayContract.Expense.TABLE_NAME).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(WepayContract.Member.TABLE_NAME).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(WepayContract.Group.TABLE_NAME).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(WepayContract.User.TABLE_NAME).build(), null, null);
    }

    public ArrayList<User> createSampleUsers(){
        //Insert users, 10 users
        ArrayList<User> newUsers = new ArrayList<User>();

        String[] names = {"Luis", "Moha", "Julieta", "María", "Benjamín", "Pedro"};
        String[] lastNames = { "Carrillo", "Vishar", "Pineda", "Wilson", "Potter", "Schwartz" };
        Uri usersTable = baseUri.buildUpon().appendPath(WepayContract.User.TABLE_NAME).build();

        int offset = 0;

        for(int i = 0; i < 8; i++) {

            User user = new User();
            int nameIndex = i % (names.length);
            int lNameIndex = (i + offset) % (lastNames.length);

            user.setId((names[nameIndex].charAt(0) + lastNames[lNameIndex]).toLowerCase() + "@gmail.com");
            user.setName(names[nameIndex] + " " + lastNames[lNameIndex]);
            user.setPhone("+1 703656620" + i);


            //insert user
            mContext.getContentResolver().insert(usersTable, EntityDao.toContentValues(user));
            newUsers.add(user);

            //we want to create name-lastName with an offset of phased alignment between the arrays to create new names with diferent combinations of names and last names.
            if((i+1)%lastNames.length == 0) offset++;
        }
        return newUsers;
    }

    public ArrayList<Group> createSampleGroups(){
        ArrayList<Group> newGroups = new ArrayList<Group>();
        Uri groupTable = baseUri.buildUpon().appendPath(WepayContract.Group.TABLE_NAME).build();

        long time = new Date().getTime();


        for(int i = 0; i < 5; i++){
            Group group = new Group();
            group.setId(time + i);
            group.setCreatedOn(new Date());
            group.setName("GroupCursor " + i);

            mContext.getContentResolver().insert(groupTable, EntityDao.toContentValues(group));
            newGroups.add(group);
        }

        return newGroups;
    }

    public HashMap<Long, ArrayList<Member>> createSampleMembers(ArrayList<User> users, ArrayList<Group> groups){
        HashMap<Long, ArrayList<Member>> groupMembers = new HashMap();
        Uri memberTable = baseUri.buildUpon().appendPath(WepayContract.Member.TABLE_NAME).build();

        for(Group group : groups){

            int noMembers = 5; //(int)(Math.round(Math.random()*100)) % 5 + 1; //Groups have 5 or less members... creating 5 members
            int startAddingFromPos = (int)(Math.round(Math.random()*100)) % users.size(); //A random position within the user list. Start adding users from a starting index

            ArrayList<Member> members = new ArrayList<Member>();

            for(int i=0; i < noMembers; i++ ){
                Member member = new Member();
                member.setId(group.getId()*10+i); //Id of member is the group ID *10 plus number of member
                member.setGroupId(group.getId()); //GroupCursor id of the current group.
                member.setAdmin(i==0); // Only the first added user is the admin now
                member.setLeftGroup(false);
                //Add user id
                int addUserAt = (startAddingFromPos + i) % users.size(); //make sure the index falls within the user's list
                member.setUserId(users.get(addUserAt).getId());

                //Insert member into the database.
                mContext.getContentResolver().insert(memberTable, EntityDao.toContentValues(member));
                members.add(member);
            }

            groupMembers.put(group.getId(), members);
        }

        return groupMembers;
    }

    public HashMap<Long, ArrayList<Expense>> createSampleExpenses(HashMap<Long, ArrayList<Member>> groupMembers){

        HashMap<Long, ArrayList<Expense>> groupExpenses = new HashMap();
        Uri expenseTable = baseUri.buildUpon().appendPath(WepayContract.Expense.TABLE_NAME).build();

        Set<Long> groups = groupMembers.keySet();

        int count = 0;
        for(Long groupId : groups){

            ArrayList<Expense> expenses = new ArrayList();

            //get number of expenses: how many?
            int noExpenses = (int)(Math.round(Math.random()*100)) % 15 + 1; //Groups will have 15 or less members
            //Decide the amount

            for(int i = 0 ; i < noExpenses; i++){
                Expense expense = new Expense();
                expense.setId(groupId*100 + i); //Id is the group id, plus the for index.
                expense.setGroupId(groupId);
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
                mContext.getContentResolver().insert(expenseTable, EntityDao.toContentValues(expense));
                expenses.add(expense);
            }

            groupExpenses.put(groupId, expenses);
        }
        return groupExpenses;
    }

    public HashMap<Long, ArrayList<Payer>> createSamplePayers(HashMap<Long, ArrayList<Member>> groupMembers, HashMap<Long, ArrayList<Expense>> groupExpenses){
        HashMap<Long, ArrayList<Payer>> expensePayers = new HashMap();
        Uri payerTable = baseUri.buildUpon().appendPath(WepayContract.Payer.TABLE_NAME).build();

        Set<Long> groupIds = groupExpenses.keySet();

        //For each group, and each expense, insert payers.
        for(long groupId : groupIds) {
            //each group one set of members accross all expenses.
            ArrayList<Member> members = groupMembers.get(groupId);
            if(members != null) { // in case there are members for the current group retrieved from the groupExpenses collection.
                int currentExpense = 0;
                for (Expense expense : groupExpenses.get(groupId)) {
                    //every expenses will be split among all members equally. Only the first or first two members will have paid the full expense.
                    int noMembers = members.size();
                    double percentagePerPerson = 1d / noMembers;
                    int currentMember = 0;
                    ArrayList<Payer> payers = new ArrayList();

                    for (Member member : members) {

                        //Add payer as ROLE_SHOULD_PAY
                        Payer payerShouldPay = new Payer();
                        payerShouldPay.setId(expense.getId()*100 + currentMember*2);
                        payerShouldPay.setMemberId(member.getId());
                        payerShouldPay.setExpenseId(expense.getId());
                        payerShouldPay.setRole(Payer.ROLE_SHOULD_PAY);
                        payerShouldPay.setPercentage(percentagePerPerson);


                        //Add payer as ROLE_PAID
                        //Make only the first member to pay all; though, for even expenses, make the first two members pay all
                        Payer payerPaid = null;
                        //double percentagePayer = 0;
                        //boolean addAsPayerToo = false; // true for first

                        /*
                        if(expense.getId() % 2 == 0 && currentMember <= 1){
                            addAsPayerToo = true;
                            percentagePayer = 0.5;
                        } else if(currentMember <= 0){
                            addAsPayerToo = true;
                            percentagePayer = 1;
                        }*/

                        if(currentMember <= 0 /*addAsPayerToo*/) {
                            payerPaid = new Payer();
                            payerPaid.setId(expense.getId() * 100 + currentMember * 2 + 1);
                            payerPaid.setMemberId(member.getId());
                            payerPaid.setExpenseId(expense.getId());
                            payerPaid.setRole(Payer.ROLE_PAID);
                            payerPaid.setPercentage(1);
                        }

                        //Insert Payers.
                        mContext.getContentResolver().insert(payerTable, EntityDao.toContentValues(payerShouldPay));
                        payers.add(payerShouldPay);

                        if(payerPaid != null) {
                            mContext.getContentResolver().insert(payerTable, EntityDao.toContentValues(payerPaid));
                            payers.add(payerPaid);
                        }
                        currentMember++;
                    }

                    expensePayers.put(expense.getId(), payers);

                    currentExpense++;
                }
            }
        }
        return expensePayers;
    }

}
