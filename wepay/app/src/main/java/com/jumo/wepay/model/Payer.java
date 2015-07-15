package com.jumo.wepay.model;

/**
 * Created by Moha on 6/28/15.
 */
public class Payer {

    public static final int ROLE_PAID = 0;
    public static final int ROLE_SHOULD_PAY = 1;

    private long id;
    private long memberId;           //Foreign key

    private int role;
    private double percentage;

    //Ancestor
    private long expenseId;          //Foreign key

    public String toString(){
        StringBuilder toString = new StringBuilder("Payer: {");
        toString.append(id).append(", ")
                .append(memberId).append(", ")
                .append(role).append(", ")
                .append(percentage).append(", ")
                .append(expenseId).append("}");
        return toString.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    /**
     * Role represents whether this member in this group paid for the expense for an amount (ROLE_PAID)
     * or if the this entry/member in this group should pay part of the expense.
     */
    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        if((role == ROLE_SHOULD_PAY && percentage > 0) || (role == ROLE_PAID && percentage < 0)){
            //if percentage is already set, then
            //make percentage negative because payer should pay and this person should show as a debt (negative number) or
            //make percentage positive because payer actually paid, so he/she should receive compensation  (positive number).
            percentage = percentage * -1;
        }
        this.role = role;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        if((role == ROLE_SHOULD_PAY && percentage > 0) || (role == ROLE_PAID && percentage < 0)) {
            this.percentage = percentage * -1;
        }else{
            this.percentage = percentage;
        }

    }

    public long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(long expenseId) {
        this.expenseId = expenseId;
    }
}
