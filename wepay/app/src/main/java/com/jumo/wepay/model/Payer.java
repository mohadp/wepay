package com.jumo.wepay.model;

import com.jumo.wepay.provider.Table;
import com.jumo.wepay.provider.WepayContract;

import java.util.Date;

/**
 * Created by Moha on 6/28/15.
 */
public class Payer implements Entity {
    private Entity entity;

    public Payer(Entity m){
        entity = m;
    }

    public Payer(){
        entity = new ActualEntity(WepayContract.Payer.getInstance());
    }

    public long getId() {
        return getLong(WepayContract.Payer._ID);
    }

    public void setId(long id) {
        setField(WepayContract.Payer._ID, id);
    }

    public long getMemberId() {
        return getLong(WepayContract.Payer.MEMBER_ID);
    }

    public void setMemberId(long memberId) {
        setField(WepayContract.Payer.MEMBER_ID, memberId);
    }

    /**
     * Role represents whether this member in this group paid for the expense for an amount (OPTION_ROLE_PAID)
     * or if the this entry/member in this group should pay part of the expense.
     */
    public int getRole() {
        return getInt(WepayContract.Payer.ROLE);
    }

    public void setRole(int role) {
        if((role == WepayContract.Payer.OPTION_ROLE_SHOULD_PAY && getPercentage() > 0) || (role == WepayContract.Payer.OPTION_ROLE_PAID && getPercentage() < 0)){
            //if percentage is already set, then
            //make percentage negative because payer should pay and this person should show as a debt (negative number) or
            //make percentage positive because payer actually paid, so he/she should receive compensation  (positive number).
            setPercentage(getPercentage() * -1);
        }
        setField(WepayContract.Payer.ROLE, role);
    }

    public double getPercentage() {
        return getDouble(WepayContract.Payer.PERCENTAGE);
    }

    public void setPercentage(double percentage) {
        if((getRole() == WepayContract.Payer.OPTION_ROLE_SHOULD_PAY && percentage > 0) || (getRole() == WepayContract.Payer.OPTION_ROLE_PAID && percentage < 0)) {
            setField(WepayContract.Payer.PERCENTAGE, percentage * -1);
        }else{
            setField(WepayContract.Payer.PERCENTAGE, percentage);
        }
    }

    public long getExpenseId() {
        return getLong(WepayContract.Payer.EXPENSE_ID);
    }

    public void setExpenseId(long expenseId) {
        setField(WepayContract.Payer.EXPENSE_ID, expenseId);
    }

    @Override
    public Table table(){
        return entity.table();
    }

    @Override
    public int getInt(String column) {
        return entity.getInt(column);
    }

    @Override
    public long getLong(String column) {
        return entity.getLong(column);
    }

    @Override
    public double getDouble(String column) {
        return entity.getDouble(column);
    }

    @Override
    public boolean getBoolean(String column) {
        return entity.getBoolean(column);
    }

    @Override
    public String getText(String column) {
        return entity.getText(column);
    }

    @Override
    public Date getDate(String column) {
        return entity.getDate(column);
    }

    @Override
    public byte[] getBytes(String column) {
        return entity.getBytes(column);
    }

    @Override
    public void setField(String column, Object val) {
        entity.setField(column, val);
    }

    @Override
    public String toString(){
        return entity.toString();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity m){
        entity = m;
    }
}
