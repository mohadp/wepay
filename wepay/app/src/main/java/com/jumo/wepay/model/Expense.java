package com.jumo.wepay.model;

import com.jumo.wepay.provider.Table;
import com.jumo.wepay.provider.WepayContract;

import java.util.Date;

/**
 * Created by Moha on 6/20/15. Decorator class on ActualEntity, which allows to get fields for particular Table/table
 */
public class Expense implements Entity {
    private Entity entity;

	public Expense(Entity m){
        entity = m;
        setExchangeRate(1);
	}

    public Expense(){
        entity = new ActualEntity(WepayContract.Expense.getInstance());
        setExchangeRate(1);
    }

	public double getExchangeRate(){
		return getDouble(WepayContract.Expense.EXCHANGE_RATE);
	}
	
	public void setExchangeRate(double rate){
        setField(WepayContract.Expense.EXCHANGE_RATE, rate);
	}

    public long getId() {
        return getLong(WepayContract.Expense._ID);
    }

    public void setId(long id) {
        setField(WepayContract.Expense._ID, id);
    }

    public Date getCreatedOn() {
        return getDate(WepayContract.Expense.CREATED_ON);
    }

    public void setCreatedOn(Date createdOn) {
        setField(WepayContract.Expense.CREATED_ON, createdOn);
    }

    public String getMessage() {
        return getText(WepayContract.Expense.MESSAGE);
    }

    public void setMessage(String message) {
        setField(WepayContract.Expense.MESSAGE, message);
    }

    public double getAmount() {
        return getDouble(WepayContract.Expense.AMOUNT);
    }

    public void setAmount(double amount) {
        setField(WepayContract.Expense.AMOUNT, amount);
    }

    public String getCurrencyId() {
        return getText(WepayContract.Expense.CURRENCY);
    }

    public void setCurrencyId(String currencyId) {
        setField(WepayContract.Expense.CURRENCY, currencyId);
    }

    public long getLocationId() {
        return getLong(WepayContract.Expense.LOCATION_ID);
    }

    public void setLocationId(long locationId) {
        setField(WepayContract.Expense.LOCATION_ID, locationId);
    }

    public long getCategoryId() {
        return getLong(WepayContract.Expense.CATEGORY_ID);
    }

    public void setCategoryId(long categoryId) {
        setField(WepayContract.Expense.CATEGORY_ID, categoryId);
    }

    /**
     * If recurrenceId is unset (value is 0), this is a normal expense; else
     * the expense is a group-level expense, which is not considered in the group balances
     * Only instances of this group-level expense can be counted for the group balances; instances
     * of a group-level expense can be identified by the groupExpenseId, which value is the ID of the
     * group-level expense.
     * @return
     */
    public long getRecurrenceId() {
        return getLong(WepayContract.Expense.RECURRENCE_ID);
    }

    public void setRecurrenceId(long recurrenceId) {
        setField(WepayContract.Expense.RECURRENCE_ID, recurrenceId);
    }

    public long getGroupExpenseId() {
        return getLong(WepayContract.Expense.GROUP_EXPENSE_ID);
    }

    public void setGroupExpenseId(long groupExpenseId) {
        setField(WepayContract.Expense.GROUP_EXPENSE_ID, groupExpenseId);
    }

    public boolean isPayment() {
        return getBoolean(WepayContract.Expense.IS_PAYMENT);
    }

    public void setPayment(boolean isPayment) {
        setField(WepayContract.Expense.IS_PAYMENT, isPayment);
    }

    public long getGroupId() {
        return getLong(WepayContract.Expense.GROUP_ID);
    }

    public void setGroupId(long groupId) {
        setField(WepayContract.Expense.GROUP_ID, groupId);
    }

    public double getUserBalance() {
        return getDouble(WepayContract.Expense.USER_BALANCE);
    }

    public void setUserBalance(double userBalance) {
        setField(WepayContract.Expense.USER_BALANCE, userBalance);
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
