package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 6/28/15.
 */
public class Payer implements Entity {
    private Entity entity;

    public Payer(Entity m){
        entity = m;
    }

    public Payer(){
        entity = new ActualEntity(TablasContract.Payer.getInstance());
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

    public boolean isUserSet(){
        return getBoolean(TablasContract.Payer.PAYER_USER_SET);
    }

    public void setUserSet(boolean userSet){
        setField(TablasContract.Payer.PAYER_USER_SET, userSet);
    }

    @Override
    public Table table(){
        return entity.table();
    }

    @Override
    public Object get(String column){
        return entity.get(column);
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
    public Iterator<String> getFieldNameIterator(){
        return entity.getFieldNameIterator();
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
