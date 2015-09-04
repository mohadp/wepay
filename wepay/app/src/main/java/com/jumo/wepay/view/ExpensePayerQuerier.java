package com.jumo.wepay.view;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * Created by Moha on 8/29/15.
 */
public class ExpensePayerQuerier extends HandlerThread {
    private static final String TAG = "ExpensePayerQuerier";

    private final static int MSG_TYPE_EXPENSE_USERS = 0;

    //Handler that will process all the messages in the looper
    Handler processor;


    public ExpensePayerQuerier(){
        super(TAG);
    }

    /**
     * Here, initializing handler
     */
    @Override
    protected void onLooperPrepared(){

    }

    public void queueExpense(RoundImageViewRow payers, long expenseId){

    }

    private class UserImagesGetter extends Handler{

        /**
         * Process the message: get the set of users for the expense from ContentProvider.
         * Then for every user, get the user's image: verify if the image is in some cache. If not,
         * query the contacts provider for the image, add it to cache, and add it RoundImageViewRow
         *
         * @param msg message object
         */
        @Override
        public void handleMessage(Message msg){
            int messageId = msg.what;

            switch(msg.what){
                case MSG_TYPE_EXPENSE_USERS:
                    RoundImageViewRow imageRow = (RoundImageViewRow)msg.obj;
                    getExpenseUserImageRow(imageRow);
                    return;
            }
        }

        private void getExpenseUserImageRow(RoundImageViewRow imageRow){

        }
    }


}
