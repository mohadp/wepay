package com.jumo.wepay.model;

import java.util.Date;

/**
 * Created by Moha on 6/20/15.
 */
public class Expense {

    private long id;
    private Date createdOn;
    private String message;
    private double amount;
    private String currencyId;                        //Foreign key (optional)
    private long locationId;                           //Foreign key
    private long categoryId;                           //Foreign key

    private long recurrenceId;                         //Foreign key
    private long groupExpenseId;                       //Foreign key
    private boolean isPayment;
    private double userBalance; //Not persisted in the database; used for contain balances for the group for a user (owes or has paid more and for how much)


    //Ancestor fields
    private long groupId;                            //Foreign key

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /** If recurrenceId is unset (value is 0), this is a normal expense; else
     * the expense is a group-level expense, which is not considered in the group balances
     * Only instances of this group-level expense can be counted for the group balances; instances
     * of a group-level expense can be identified by the groupExpenseId, which value is the ID of the
     * group-level expense.
     */
    public long getRecurrenceId() {
        return recurrenceId;
    }

    public void setRecurrenceId(long recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    public long getGroupExpenseId() {
        return groupExpenseId;
    }

    public void setGroupExpenseId(long groupExpenseId) {
        this.groupExpenseId = groupExpenseId;
    }

    public boolean isPayment() {
        return isPayment;
    }

    public void setPayment(boolean isPayment) {
        this.isPayment = isPayment;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public double getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(double userBalance) {
        this.userBalance = userBalance;
    }
}
