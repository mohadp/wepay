package com.jumo.tablas.common;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.jumo.tablas.model.Categories;
import com.jumo.tablas.model.Expense;
import com.jumo.tablas.model.Group;
import com.jumo.tablas.model.Member;
import com.jumo.tablas.model.User;
import com.jumo.tablas.model.Payer;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.EntityCursor;
import com.jumo.tablas.provider.dao.EntityWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import android.database.*;
import android.util.Log;

/**
 * Created by Moha on 7/10/15.
 */
public class TablasManager {

    private static final String TAG = "TablasManager";


    private Context mContext;
    private Uri baseUri;
    private static TablasManager sampleDataUtil;

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

    /*
    // I will not use this
    public GroupCursor getUserGroups(String userId){
        ContentResolver content = mContext.getContentResolver();
        Uri uri = baseUri.buildUpon().appendPath(TablasContract.User.getInstance().getTableName())
                .appendPath(userId).appendPath("groups")
                .build();

        return new GroupCursor(content.query(uri, null, null, null, null));
    }*/

    /*
	public ExpenseCursor getUserGroupExpenses(String userId, long groupId){
		ContentResolver content = mContext.getContentResolver();
		Uri uri = baseUri.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName())
				.appendPath("user").appendPath(userId).appendPath("group").appendPath(Long.toString(groupId))
				.build();
		
		StringBuilder sortBy = new StringBuilder(TablasContract.Expense.CREATED_ON).append(" ASC");
		
		return new ExpenseCursor(content.query(uri, null, null, null, sortBy.toString()));
	}*/

    public void createSampleData(){
        ContentResolver content = mContext.getContentResolver();

        Uri seeGroups = baseUri.buildUpon().appendPath(TablasContract.Group.getInstance().getTableName()).build();
        Cursor groupsCursor = content.query(seeGroups, null, null, null, null);
		

        //boolean afterLast = groupsCursor.isAfterLast();
        //boolean beforeFirst = groupsCursor.isBeforeFirst();

        EntityCursor entCursor = new EntityCursor(groupsCursor);

        Log.d(TAG, "Group Cursor size: " + entCursor.getCount());
        if(entCursor.getCount() > 0)
            Log.d(TAG, "All Groups: "+ entCursor.toString(TablasContract.Group.getInstance()));


        if(groupsCursor == null || groupsCursor.getCount() == 0){
        	deleteAllData();
		
		//Toast.makeText(mContext, "Data Deleted", Toast.LENGTH_SHORT);
		
        	ArrayList<User> newUsers = createSampleUsers();
        	ArrayList<Group> newGroups = createSampleGroups();
        	HashMap<Long, ArrayList<Member>> newGroupMembers = createSampleMembers(newUsers, newGroups);
        	HashMap<Long, ArrayList<Expense>> newGroupExpenses = createSampleExpenses(newGroupMembers);
        	createSamplePayers(newGroupMembers, newGroupExpenses);
        }

        /*EntityWriter.UserCursor userCursor = new EntityWriter.UserCursor(content.query(baseUri.buildUpon().appendPath(TablasContract.User.getInstance().getTableName()).build() ,null, null, null, null));
        Log.d(TAG, userCursor.toString());

        EntityWriter.GroupCursor groupCursor = new EntityWriter.GroupCursor(content.query(baseUri.buildUpon().appendPath(TablasContract.Group.getInstance().getTableName()).build() ,null, null, null, null));
        Log.d(TAG, groupCursor.toString());

        EntityWriter.MemberCursor memberCursor = new EntityWriter.MemberCursor(content.query(baseUri.buildUpon().appendPath(TablasContract.Member.getInstance().getTableName()).build() ,null, null, null, null));
        Log.d(TAG, memberCursor.toString());

        EntityWriter.ExpenseCursor expenseCursor = new EntityWriter.ExpenseCursor(content.query(baseUri.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName()).build() ,null, null, null, null));
        Log.d(TAG, expenseCursor.toString());

        PayerCursor payerCursor = new PayerCursor(content.query(baseUri.buildUpon().appendPath(TablasContract.Payer.getInstance().getTableName()).build() ,null, null, null, null));
        Log.d(TAG, payerCursor.toString());
        */

    }


    public void synchronizeUserData(String userId){

    }

    public void deleteAllData(){
        ContentResolver content = mContext.getContentResolver();
        content.delete(baseUri.buildUpon().appendPath(TablasContract.Payer.getInstance().getTableName()).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName()).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(TablasContract.Member.getInstance().getTableName()).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(TablasContract.Group.getInstance().getTableName()).build(), null, null);
        content.delete(baseUri.buildUpon().appendPath(TablasContract.User.getInstance().getTableName()).build(), null, null);
    }

    public ArrayList<User> createSampleUsers(){
        //Insert users, 10 users
        ArrayList<User> newUsers = new ArrayList<User>();

        //String[] names = {"Luis", "Moha", "Julieta", "María", "Benjamín", "Pedro"};
        //String[] lastNames = { "Carrillo", "Vishar", "Pineda", "Wilson", "Potter", "Schwartz" };
        String[] fullNames = {"Moha", "Luis Carrillo", "Julieta Vishar", "Vijay Anand", "Ismael Diakite", "Cindy Diakite", "Octavio Soto", "Valentina Xavier", "Sean Darling-Hammond"};
        String[] emails = {"mohadp@gmail.com", "edgardo.carrillo@gmail.com", "julieta.villar.athie@gmail.com", "vijayanand180@gmail.com", "ismaeld@gmail.com", "cindydiakite@gmail.com", "g.octavio.soto@gmail.com", "valentina.xavier@gmail.com", "sean.darling.hammond@gmail.com"};
        String[] tels = {"+17036566202", "+17037173160", "+17036566203", "+19192188457", "+5215534007789", "+5215540840084", "+5215513725485", "+12026790071", "+16504557014"};
        Uri usersTable = baseUri.buildUpon().appendPath(TablasContract.User.getInstance().getTableName()).build();

        //int offset = 0;

        //for(int i = 0; i < 8; i++) {
        for(int i = 0; i < emails.length; i++){

            User user = new User();
            //int nameIndex = i % (names.length);
            //int lNameIndex = (i + offset) % (lastNames.length);

            //user.setId((names[nameIndex].charAt(0) + lastNames[lNameIndex]).toLowerCase() + "@gmail.com");
            user.setId(tels[i]);
            //user.setName(names[nameIndex] + " " + lastNames[lNameIndex]);
            user.setName(fullNames[i]);
            user.setEmail(emails[i]);

            //insert user
            mContext.getContentResolver().insert(usersTable, EntityWriter.toContentValues(user));
            newUsers.add(user);

            //we want to create name-lastName with an offset of phased alignment between the arrays to create new names with diferent combinations of names and last names.
            //if((i+1)%lastNames.length == 0) offset++;
        }
        return newUsers;
    }

    public ArrayList<Group> createSampleGroups(){
        ArrayList<Group> newGroups = new ArrayList<Group>();
        Uri groupTable = baseUri.buildUpon().appendPath(TablasContract.Group.getInstance().getTableName()).build();

        long time = new Date().getTime();


        for(int i = 0; i < 10; i++){
            Group group = new Group();
            group.setId(time + i);
            group.setCreatedOn(new Date());
            group.setName("Group " + i);

            mContext.getContentResolver().insert(groupTable, EntityWriter.toContentValues(group));
            newGroups.add(group);
        }

        return newGroups;
    }

    public HashMap<Long, ArrayList<Member>> createSampleMembers(ArrayList<User> users, ArrayList<Group> groups){
        HashMap<Long, ArrayList<Member>> groupMembers = new HashMap();
        Uri memberTable = baseUri.buildUpon().appendPath(TablasContract.Member.getInstance().getTableName()).build();

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
                mContext.getContentResolver().insert(memberTable, EntityWriter.toContentValues(member));
                members.add(member);
            }

            groupMembers.put(group.getId(), members);
        }

        return groupMembers;
    }

    public HashMap<Long, ArrayList<Expense>> createSampleExpenses(HashMap<Long, ArrayList<Member>> groupMembers){

        HashMap<Long, ArrayList<Expense>> groupExpenses = new HashMap();
        Uri expenseTable = baseUri.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName()).build();

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
                mContext.getContentResolver().insert(expenseTable, EntityWriter.toContentValues(expense));
                expenses.add(expense);
            }

            groupExpenses.put(groupId, expenses);
        }
        return groupExpenses;
    }

    public HashMap<Long, ArrayList<Payer>> createSamplePayers(HashMap<Long, ArrayList<Member>> groupMembers, HashMap<Long, ArrayList<Expense>> groupExpenses){
        HashMap<Long, ArrayList<Payer>> expensePayers = new HashMap();
        Uri payerTable = baseUri.buildUpon().appendPath(TablasContract.Payer.getInstance().getTableName()).build();

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

                    //figure out who the payer is for this expense; will make this random, any of the members
                    int payer = ((int)Math.round(Math.random()*100)) % members.size();

                    for (Member member : members) {

                        //Add payer as OPTION_ROLE_SHOULD_PAY
                        Payer payerShouldPay = new Payer();
                        payerShouldPay.setId(expense.getId()*100 + currentMember*2);
                        payerShouldPay.setMemberId(member.getId());
                        payerShouldPay.setExpenseId(expense.getId());
                        payerShouldPay.setRole(TablasContract.Payer.OPTION_ROLE_SHOULD_PAY);
                        payerShouldPay.setPercentage(percentagePerPerson);


                        //Add payer as OPTION_ROLE_PAID
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




                        if(currentMember == payer /*addAsPayerToo*/) {
                            payerPaid = new Payer();
                            payerPaid.setId(expense.getId() * 100 + currentMember * 2 + 1);
                            payerPaid.setMemberId(member.getId());
                            payerPaid.setExpenseId(expense.getId());
                            payerPaid.setRole(TablasContract.Payer.OPTION_ROLE_PAID);
                            payerPaid.setPercentage(1);
                        }

                        //Insert Payers.
                        mContext.getContentResolver().insert(payerTable, EntityWriter.toContentValues(payerShouldPay));
                        payers.add(payerShouldPay);

                        if(payerPaid != null) {
                            mContext.getContentResolver().insert(payerTable, EntityWriter.toContentValues(payerPaid));
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