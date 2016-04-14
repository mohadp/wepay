package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;

/**
 * Created by Moha on 6/28/15.
 */
public class Payer extends BaseEntity {
    //private Entity entity;

    public Payer(Entity m){
        super(m);
    }

    public Payer(){
        super(new ActualEntity(TablasContract.Payer.getInstance()));
    }

    public long getId() {
        return getLong(TablasContract.Payer._ID);
    }

    public void setId(long id) {
        setField(TablasContract.Payer._ID, id);
    }

    public long getMemberId() {
        return getLong(TablasContract.Payer.PAYER_MEMBER_ID);
    }

    public void setMemberId(long memberId) {
        setField(TablasContract.Payer.PAYER_MEMBER_ID, memberId);
    }

    /**
     * Role represents whether this member in this group paid for the expense for an amount (OPTION_ROLE_PAID)
     * or if the this entry/member in this group should pay part of the expense.
     */
    public int getRole() {
        return getInt(TablasContract.Payer.PAYER_ROLE);
    }

    public void setRole(int role) {
        if((role == TablasContract.Payer.OPTION_ROLE_SHOULD_PAY && getPercentage() > 0) || (role == TablasContract.Payer.OPTION_ROLE_PAID && getPercentage() < 0)){
            //if percentage is already set, then
            //make percentage negative because payer should pay and this person should show as a debt (negative number) or
            //make percentage positive because payer actually paid, so he/she should receive compensation  (positive number).
            setPercentage(getPercentage() * -1);
        }
        setField(TablasContract.Payer.PAYER_ROLE, role);
    }

    public double getPercentage() {
        return getDouble(TablasContract.Payer.PAYER_PERCENTAGE);
    }

    public void setPercentage(double percentage) {
        if((getRole() == TablasContract.Payer.OPTION_ROLE_SHOULD_PAY && percentage > 0) || (getRole() == TablasContract.Payer.OPTION_ROLE_PAID && percentage < 0)) {
            setField(TablasContract.Payer.PAYER_PERCENTAGE, percentage * -1);
        }else{
            setField(TablasContract.Payer.PAYER_PERCENTAGE, percentage);
        }
    }

    public long getExpenseId() {
        return getLong(TablasContract.Payer.PAYER_EXPENSE_ID);
    }

    public void setExpenseId(long expenseId) {
        setField(TablasContract.Payer.PAYER_EXPENSE_ID, expenseId);
    }

    public boolean isManuallySet(){
        return getBoolean(TablasContract.Payer.PAYER_MANUALLY_SET);
    }

    public void setManuallySet(boolean userSet){
        setField(TablasContract.Payer.PAYER_MANUALLY_SET, userSet);
    }

}
