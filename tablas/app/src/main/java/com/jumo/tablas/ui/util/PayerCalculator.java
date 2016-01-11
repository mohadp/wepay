package com.jumo.tablas.ui.util;

import com.jumo.tablas.model.Expense;
import com.jumo.tablas.model.Payer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Moha on 1/3/16.
 */
public class PayerCalculator {
    private Expense mExpense;

    private LinkedHashMap<Long, Double> mPayers; //Contains list of people who should pay.
    private ArrayList<Long> mPayersManuallySet; //Contains whether the amounts for thee users were manually set; always the most recent modified is in position 0; the least recently modified is at the last position.
    private HashMap<Long, Payer> mPayerEntities;
    //private WeakReference<Context> mContextReferece;

    public PayerCalculator(){
        mExpense = new Expense();
        mPayers = new LinkedHashMap<Long, Double>();
        mPayersManuallySet = new ArrayList<Long>();
        mPayerEntities = new HashMap<Long, Payer>();
        //mContextReferece = new WeakReference<Context>(context);
    }

    public PayerCalculator(Expense expense){
        mExpense = expense;
        mPayers = new LinkedHashMap<Long, Double>();
        mPayersManuallySet = new ArrayList<Long>();
    }


    public void makeAllPayEvenly(){
        mPayersManuallySet.clear();
        rebalanceAmounts();
    }

    public void addPayer(long memberId){
        if(!mPayers.containsKey(memberId)) {
            mPayers.put(memberId, 0d);
            removeFromManuallySet(memberId);
            rebalanceAmounts(); //sets values correctly
        }
    }

    public void addPayer(long memberId, double amount){
        mPayers.put(memberId, amount);
        //In manually set amounts, move  to the front of the list to try to keep this as set, since
        // Rebalance code makes the last members as set automatically, when the manually set amounts are above total expense.
        moveFirstManuallySet(memberId);
        rebalanceAmounts();
    }

    public void addPayer(long memberId, Payer payer){
        mPayers.put(memberId, (mExpense.getAmount() * payer.getPercentage()));
        mPayerEntities.put(memberId, payer);

        if(payer.isManuallySet()){
            moveFirstManuallySet(memberId);
        }else{
            removeFromManuallySet(memberId);
        }
        rebalanceAmounts();
    }

    public double getAmountForMember(long memberId){
        return mPayers.get(memberId);
    }

    private void moveFirstManuallySet(long memberId){
        int indexOfMember = mPayersManuallySet.indexOf(memberId);

        //Remove if previously existed in position other than 0.
        if(indexOfMember > 0) {
            mPayersManuallySet.remove(indexOfMember);
        }
        //Only previous position was not 0, add
        if(indexOfMember != 0){
            mPayersManuallySet.add(0, memberId);
        }
    }

    private void removeFromManuallySet(long memberId){
        int indexOfMember = mPayersManuallySet.indexOf(memberId);
        if(indexOfMember >= 0) {
            mPayersManuallySet.remove(indexOfMember);
        }
    }


    public void rebalanceAmounts(){
        //First see if the manually set amounts are the same as the total amount, or larger;
        //The first user-set-amount member that completes the totality of the expense amount
        // or surpasses the expense amount becomes now an automatically-amount-set member (removing from the manually set collection).
        // All the subsequent user-set-amount members also become automatically-amount-set members.
        double manuallySetAmount = 0d;
        boolean totalReached = false;
        Set<Long> toMakeAutomaticallySet = new HashSet<Long>();

        Iterator<Long> it = mPayersManuallySet.iterator();
        while(it.hasNext()){
            long currentMemberId = it.next();
            manuallySetAmount += (totalReached)? 0d : mPayers.get(currentMemberId);
            if(totalReached || manuallySetAmount >= mExpense.getAmount()){
                if(!totalReached){
                    totalReached = true; //from this point on, we start marking this and all other members as "automatically set"
                    manuallySetAmount -= mPayers.get(currentMemberId); //we record the total amount specified manually by users (excluding the current member which will be set to "automatically set"
                }
                toMakeAutomaticallySet.add(currentMemberId);
            }
        }
        mPayersManuallySet.removeAll(toMakeAutomaticallySet);

        //If all members are manually set, and if they do not add up to the full amount, make the least recently updated manual member "automatically set"
        if(mPayersManuallySet.size() == mPayers.size() && mPayersManuallySet.size() != 0 && manuallySetAmount < mExpense.getAmount()){
            //Check when all payers have manually set amounts, but all of them do not add up to the expense total amount
            //In this case, make the last manuallySet member automatic to compensate.
            int indexLastManually = mPayersManuallySet.size()-1;
            long memberLastManually = mPayersManuallySet.get(indexLastManually);
            mPayersManuallySet.remove(indexLastManually);
            manuallySetAmount -=  mPayers.get(memberLastManually);
        }

        //Set the automatically set amounts to members (the ones that users have not specified or which become "invalid" as explained
        //in the comments above.
        int numAutomaticallySet = mPayers.size() - mPayersManuallySet.size();
        double amountAutoSet = mExpense.getAmount() - manuallySetAmount;
        double amountAutoPerPerson = (numAutomaticallySet > 0)? (amountAutoSet / numAutomaticallySet) : 0;
        for(Long l : mPayers.keySet()) {
            if (!mPayersManuallySet.contains(l)) {
                mPayers.put(l, amountAutoPerPerson);
            }
        }
    }

    public ArrayList<Payer> exportPayers(int payerRole){
        ArrayList<Payer> payers = new ArrayList<Payer>();
        for(Long l : mPayers.keySet()){
            Payer payer = mPayerEntities.get(l);
            payer = (payer == null)? new Payer() : payer;
            payer.setMemberId(l.longValue());
            payer.setRole(payerRole);
            payer.setManuallySet(mPayersManuallySet.contains(l));
            payer.setExpenseId(mExpense.getId());
            payer.setPercentage(mPayers.get(l)/mExpense.getAmount());
        }
        return payers;
    }


}
