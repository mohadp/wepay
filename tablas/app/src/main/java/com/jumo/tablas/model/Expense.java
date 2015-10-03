package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.Table;

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
        entity = new ActualEntity(TablasContract.Expense.getInstance());
        setExchangeRate(1);
    }

	public double getExchangeRate(){
		return getDouble(TablasContract.Expense.EXCHANGE_RATE);
	}
	
	public void setExchangeRate(double rate){
        setField(TablasContract.Expense.EXCHANGE_RATE, rate);
	}

    public long getId() {
        return getLong(TablasContract.Expense._ID);
    }

    public void setId(long id) {
        setField(TablasContract.Expense._ID, id);
    }

    public Date getCreatedOn() {
        return getDate(TablasContract.Expense.CREATED_ON);
    }

    public void setCreatedOn(Date createdOn) {
        setField(TablasContract.Expense.CREATED_ON, createdOn);
    }

    public String getMessage() {
        return getText(TablasContract.Expense.MESSAGE);
    }

    public void setMessage(String message) {
        setField(TablasContract.Expense.MESSAGE, message);
    }

    public double getAmount() {
        return getDouble(TablasContract.Expense.AMOUNT);
    }

    public void setAmount(double amount) {
        setField(TablasContract.Expense.AMOUNT, amount);
    }

    public String getCurrencyId() {
        return getText(TablasContract.Expense.CURRENCY);
    }

    public void setCurrencyId(String currencyId) {
        setField(TablasContract.Expense.CURRENCY, currencyId);
    }

    public long getLocationId() {
        return getLong(TablasContract.Expense.LOCATION_ID);
    }

    public void setLocationId(long locationId) {
        setField(TablasContract.Expense.LOCATION_ID, locationId);
    }

    public long getCategoryId() {
        return getLong(TablasContract.Expense.CATEGORY_ID);
    }

    public void setCategoryId(long categoryId) {
        setField(TablasContract.Expense.CATEGORY_ID, categoryId);
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
        return getLong(TablasContract.Expense.RECURRENCE_ID);
    }

    public void setRecurrenceId(long recurrenceId) {
        setField(TablasContract.Expense.RECURRENCE_ID, recurrenceId);
    }

    public long getGroupExpenseId() {
        return getLong(TablasContract.Expense.GROUP_EXPENSE_ID);
    }

    public void setGroupExpenseId(long groupExpenseId) {
        setField(TablasContract.Expense.GROUP_EXPENSE_ID, groupExpenseId);
    }

    public boolean isPayment() {
        return getBoolean(TablasContract.Expense.IS_PAYMENT);
    }

    public void setPayment(boolean isPayment) {
        setField(TablasContract.Expense.IS_PAYMENT, isPayment);
    }

    public long getGroupId() {
        return getLong(TablasContract.Expense.GROUP_ID);
    }

    public void setGroupId(long groupId) {
        setField(TablasContract.Expense.GROUP_ID, groupId);
    }

    public double getUserBalance() {
        return getDouble(TablasContract.Expense.USER_BALANCE);
    }

    public void setUserBalance(double userBalance) {
        setField(TablasContract.Expense.USER_BALANCE, userBalance);
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
