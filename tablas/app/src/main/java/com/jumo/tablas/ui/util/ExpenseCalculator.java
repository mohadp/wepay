package com.jumo.tablas.ui.util;

import android.util.Log;

import com.jumo.tablas.model.Expense;
import com.jumo.tablas.model.Member;
import com.jumo.tablas.model.Payer;
import com.jumo.tablas.provider.TablasContract;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Moha on 1/10/16.
 */
public class ExpenseCalculator {
    private static final String TAG = "ExpenseCalculator";
    private Expense mExpense;
    private PayerCalculator mShouldPay;
    private PayerCalculator mHasPaid;
    private HashMap<Long, Person> mPeople; //Contains all the objects to be inserted

    public ExpenseCalculator(){
        mExpense = new Expense();
        mShouldPay = new PayerCalculator(mExpense);
        mHasPaid = new PayerCalculator(mExpense);
        mPeople = new HashMap<Long, Person>();
    }

    public ExpenseCalculator(Expense expense){
        mExpense = expense;
        mShouldPay = new PayerCalculator(mExpense);
        mHasPaid = new PayerCalculator(mExpense);
        mPeople = new HashMap<Long, Person>();
    }

    /**
     * Adds a person to the ExpenseCalculator. If Payer objects are not null, these payers are accounted for when calculating amounts.
     * @param person Person object that has member and contact information.
     * @param paid Payer entity contains information on what the Person's member paid for current expense. This can be null, and is useful when start editing an existent expense with payers.
     * @param shouldPay Payer entity contains information on what the Person's member should paid current expense. This can be null.  This can be null, and is useful when start editing an existent expense with payers.
     */
    public void addPerson(Person person, Payer paid, Payer shouldPay){
        addPerson(person);
        if(paid != null){
            addPayer(person.member.getId(), paid);
        }
        if(shouldPay != null){
            addPayer(person.member.getId(), shouldPay);
        }
    }

    public void addPerson(Person person){
        mPeople.put(person.member.getId(), person);
    }

    //Todo: Add a removePayer method...

    public void updateExpenseAmount(double amount){
        mExpense.setAmount(amount);
        mShouldPay.rebalanceAmounts();
        mHasPaid.rebalanceAmounts();
    }

    public boolean addShouldPay(long memberId){
        return addGenericPayer(memberId, mShouldPay);
    }

    public boolean addShouldPay(long memberId, double amount){
        return addGenericPayer(memberId, amount, mShouldPay);
    }

    public boolean addHasPaid(long memberId){
        return addGenericPayer(memberId, mHasPaid);
    }

    public boolean addHasPaid(long memberId, double amount){
        return addGenericPayer(memberId, amount, mHasPaid);
    }

    public boolean addPayer(long memberId, int payerMode){
        if(payerMode == TablasContract.Payer.OPTION_ROLE_PAID){
            return addHasPaid(memberId);
        }else if(payerMode == TablasContract.Payer.OPTION_ROLE_SHOULD_PAY){
            return addShouldPay(memberId);
        }
        return false;
    }

    public boolean addPayer(long memberId, double amount, int payerMode){
        if(payerMode == TablasContract.Payer.OPTION_ROLE_PAID){
            return addHasPaid(memberId, amount);
        }else if(payerMode == TablasContract.Payer.OPTION_ROLE_SHOULD_PAY){
            return addShouldPay(memberId, amount);
        }
        return false;
    }

    public double getAmountForMember(long memberId, int payerMode){
        if(payerMode == TablasContract.Payer.OPTION_ROLE_PAID){
            return mHasPaid.getAmountForMember(memberId);
        }else if(payerMode == TablasContract.Payer.OPTION_ROLE_SHOULD_PAY){
            return mShouldPay.getAmountForMember(memberId);
        }
        return 0;
    }

    private boolean addGenericPayer(long memberId, PayerCalculator calculator){
        if(!mPeople.containsKey(memberId)){
            Log.d(TAG, "Member is not in mPeople collection: " + memberId);
            return false;
        }
        calculator.addPayer(memberId);
        return true;
    }

    private boolean addGenericPayer(long memberId, double amount, PayerCalculator calculator){
        if(!mPeople.containsKey(memberId)){
            Log.d(TAG, "Member is not in mPeople collection: " + memberId);
            return false;
        }
        calculator.addPayer(memberId, amount);
        return true;
    }

    public boolean addPayer(long memberId, Payer payer){
        if(!mPeople.containsKey(memberId)){
            Log.d(TAG, "Member is not in mPeople collection: " + memberId);
            return false;
        }
        PayerCalculator calculator = (payer.getRole() == TablasContract.Payer.OPTION_ROLE_PAID)? mHasPaid : mShouldPay;
        calculator.addPayer(memberId, payer);
        return true;
    }

    public Collection<Person> getPeople(){
        return (mPeople == null)? null : mPeople.values();
    }


    public static class Person{
        public String displayName;
        public String photoUri;
        public Member member;

        public Person(Member mem, String name, String uriPhoto){
            member = mem;
            displayName = name;
            photoUri = uriPhoto;
        }

        public Person(Member mem){
            member = mem;
        }

        public void setContactInfo(String name, String uriPhoto){
            displayName = name;
            photoUri = uriPhoto;
        }

        public String getUserId(){
            return member.getUserId();
        }
    }

    public Expense getExpense(){
        return mExpense;
    }

}
