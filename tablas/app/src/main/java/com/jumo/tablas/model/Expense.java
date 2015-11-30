package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.Iterator;

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
		return getDouble(TablasContract.Expense.EXPENSE_EXCHANGE_RATE);
	}
	
	public void setExchangeRate(double rate){
        setField(TablasContract.Expense.EXPENSE_EXCHANGE_RATE, rate);
	}

    public long getId() {
        return getLong(TablasContract.Expense._ID);
    }

    public void setId(long id) {
        setField(TablasContract.Expense._ID, id);
    }

    public Date getCreatedOn() {
        return getDate(TablasContract.Expense.EXPENSE_CREATED_ON);
    }

    public void setCreatedOn(Date createdOn) {
        setField(TablasContract.Expense.EXPENSE_CREATED_ON, createdOn);
    }

    public String getMessage() {
        return getText(TablasContract.Expense.EXPENSE_MESSAGE);
    }

    public void setMessage(String message) {
        setField(TablasContract.Expense.EXPENSE_MESSAGE, message);
    }

    public double getAmount() {
        return getDouble(TablasContract.Expense.EXPENSE_AMOUNT);
    }

    public void setAmount(double amount) {
        setField(TablasContract.Expense.EXPENSE_AMOUNT, amount);
    }

    public String getCurrencyId() {
        return getText(TablasContract.Expense.EXPENSE_CURRENCY);
    }

    public void setCurrencyId(String currencyId) {
        setField(TablasContract.Expense.EXPENSE_CURRENCY, currencyId);
    }

    /*public long getLocationId() {
        return getLong(TablasContract.Expense.LOCATION_ID);
    }

    public void setLocationId(long locationId) {
        setField(TablasContract.Expense.LOCATION_ID, locationId);
    }*/

    public long getCategoryId() {
        return getLong(TablasContract.Expense.EXPENSE_CATEGORY_ID);
    }

    public void setCategoryId(long categoryId) {
        setField(TablasContract.Expense.EXPENSE_CATEGORY_ID, categoryId);
    }

    public boolean isPayment() {
        return getBoolean(TablasContract.Expense.EXPENSE_IS_PAYMENT);
    }

    public void setPayment(boolean isPayment) {
        setField(TablasContract.Expense.EXPENSE_IS_PAYMENT, isPayment);
    }

    public double getLatitude() {
        return getDouble(TablasContract.Expense.EXPENSE_LATITUDE);
    }

    public void setLatitude(double latitude) {
        setField(TablasContract.Expense.EXPENSE_LATITUDE, latitude);
    }

    public double getLongitude() {
        return getDouble(TablasContract.Expense.EXPENSE_LONGITUDE);
    }

    public void setLongitude(double longitude) {
        setField(TablasContract.Expense.EXPENSE_LONGITUDE, longitude);
    }



    public long getGroupExpenseId() {
        return getLong(TablasContract.Expense.EXPENSE_GROUP_EXPENSE_ID);
    }

    public void setGroupExpenseId(long groupExpenseId) {
        setField(TablasContract.Expense.EXPENSE_GROUP_EXPENSE_ID, groupExpenseId);
    }

    /**
     * If recurrenceId is unset (value is 0), this is a normal expense; else
     * the expense is a group-level expense, which is not considered in the group balances
     * Only instances of this group-level expense can be counted for the group balances; instances
     * of a group-level expense can be identified by the groupExpenseId, which value is the ID of the
     * group-level expense.
     * @return
     */
    /*public long getRecurrenceId() {
        return getLong(TablasContract.Expense.RECURRENCE_ID);
    }

    public void setRecurrenceId(long recurrenceId) {
        setField(TablasContract.Expense.RECURRENCE_ID, recurrenceId);
    }*/

    public long getPeriodicity() {
        return getLong(TablasContract.Expense.EXPENSE_PERIODICITY);
    }

    public void setPeriodicity(long periodicity) {
        setField(TablasContract.Expense.EXPENSE_PERIODICITY, periodicity);
    }

    /**
     * When periodicity is
     *  PERIODICITY_DAILY, this variable has no significance;
     *  PERIODICITY_WEEKLY, offset = 1 means Monday, 2 is Tuesday, ... , 7 is Sunday.
     *  PERIODICITY_MONTHLY, offset = 1 means the first of the month, offset = OFFSET_LAST_OF_MONTH means last day of every month.
     **/
    public long getOffset() {
        return getLong(TablasContract.Expense.EXPENSE_OFFSET);
    }

    public void setOffset(long offset) {
        setField(TablasContract.Expense.EXPENSE_OFFSET, offset);
    }


    public long getGroupId() {
        return getLong(TablasContract.Expense.EXPENSE_GROUP_ID);
    }

    public void setGroupId(long groupId) {
        setField(TablasContract.Expense.EXPENSE_GROUP_ID, groupId);
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
