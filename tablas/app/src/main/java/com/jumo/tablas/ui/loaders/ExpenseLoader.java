package com.jumo.tablas.ui.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.model.Entity;
import com.jumo.tablas.model.Expense;
import com.jumo.tablas.model.Member;
import com.jumo.tablas.model.Payer;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.EntityCursor;
import com.jumo.tablas.ui.util.ExpenseCalculator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Moha on 1/9/16.
 */
public class ExpenseLoader extends AsyncTask<Long, Void, ExpenseCalculator> {
    private static final String TAG = "ExpenseLoader";
    private ArrayList<OnExpenseCalculatorLoaded> mOnLoadedSubscribers;
    private WeakReference<Context> mContextReference;


    public ExpenseLoader(Context context){
        mOnLoadedSubscribers = new ArrayList<OnExpenseCalculatorLoaded>();
        mContextReference = new WeakReference<Context>(context);
    }

    /**
     * Retrieve members and corresponding payers (if existent) for a given expense in a group.
     * @param ids first parameter is an expenseId, and the second is a groupId.
     * @return and ExpenseCalculator object loaded with all membes and contact information, which will handle all calculation logic.
     */
    @Override
    public ExpenseCalculator doInBackground(Long... ids) {
        long expenseId = ids[0];
        long groupId = ids[1];

        TablasManager tablasManager = TablasManager.getInstance(mContextReference.get());

        //Retrieve expense (if any present)
        ExpenseCalculator calculator = loadExpense(tablasManager, expenseId);
        //Retrieve members and, if any, existent payers
        loadMembersAndPayers(tablasManager, calculator, expenseId, groupId);

        return calculator;
    }

    private ExpenseCalculator loadExpense(TablasManager tablasManager, long expenseId){
        Expense expense = null;
        EntityCursor expenseCursor = new EntityCursor(tablasManager.getExpense(expenseId));
        if(expenseCursor == null || expenseCursor.isClosed() || expenseCursor.getCount() == 0){
            expense = new Expense();
        }else{
            expenseCursor.moveToFirst();
            expense = new Expense(expenseCursor.getEntity(TablasContract.Expense.getInstance()));
        }
        expenseCursor.close();
        ExpenseCalculator calculator = new ExpenseCalculator(expense);
        return calculator;
    }

    private void loadMembersAndPayers(TablasManager tablasManager, ExpenseCalculator calculator, long expenseId, long groupId){
        boolean isNewExpense = calculator.getExpense().getId() <= 0; //If loaded expense id is 0 or less, no expense found; we are creating new expense.
        //Get members
        Cursor memberCursor = isNewExpense? tablasManager.getGroupMembers(groupId) : tablasManager.getExpensePayersAndMembers(expenseId);
        EntityCursor entityCursor = new EntityCursor(memberCursor);
        HashMap<Long, ExpenseCalculator.Person> people = new HashMap<Long, ExpenseCalculator.Person>();

        while(entityCursor.moveToNext()){
            Entity memberOrPayer = entityCursor.getEntity(isNewExpense? TablasContract.Member.getInstance() : TablasContract.Compound.GroupBalance.getInstance());
            Member member = new Member(memberOrPayer);

            ExpenseCalculator.Person person = people.get(member.getId());
            person = (person == null)? new ExpenseCalculator.Person(member) : person;

            //Get contact information for current member
            loadContactInfoIntoPerson(tablasManager, person);

            if(!isNewExpense) {
                Payer payer = new Payer(memberOrPayer);
                //Payers should be null if creating a new expense.
                Payer hasPaid = (payer.getId() > 0 && payer.getRole() == TablasContract.Payer.OPTION_ROLE_PAID) ? payer : null;
                Payer shouldPay = (payer.getId() > 0 && payer.getRole() == TablasContract.Payer.OPTION_ROLE_SHOULD_PAY) ? payer : null;

                calculator.addPerson(person, hasPaid, shouldPay);
            }else{
                calculator.addPerson(person);
            }
        }


        entityCursor.close();
    }

    private void loadContactInfoIntoPerson(TablasManager tablasManager, ExpenseCalculator.Person person){
        String userId = person.member.getUserId();

        Cursor contactCursor = tablasManager.getContactsByUserId(userId);
        //Assumption is that all contacts are in the phone.
        if(contactCursor == null || contactCursor.isClosed() || contactCursor.getCount() == 0){
            return;
        }

        contactCursor.moveToFirst();
        String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String url = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

        person.setContactInfo(name, url);
        contactCursor.close();
    }

    @Override
    protected void onPostExecute(ExpenseCalculator result) {
        //showDialog("Downloaded " + result + " bytes");
        for(OnExpenseCalculatorLoaded s : mOnLoadedSubscribers){
            s.onExpenseCalculatorLoader(result);
        }
    }

    public void addSubscriberToNotify(OnExpenseCalculatorLoaded subscriber){
        mOnLoadedSubscribers.add(subscriber);
    }

    public interface OnExpenseCalculatorLoaded{
        public void onExpenseCalculatorLoader(ExpenseCalculator result);
    }
}
