package com.jumo.wepay.wepay;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Moha on 6/20/15.
 */
public class Group {
    private UUID groupId;
    private String groupName;
    private Date timestamp;
    private ArrayList<String> admins;
    private String iconName;

    private ArrayList<String> members;
    private HashMap<String, ArrayList<RecipientAmount>> transfers;
    private ArrayList<Expense> messages;

    class RecipientAmount{
        private String user;
        private double amount;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }

}
