package com.jumo.tablas.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.util.OnKeyEventListener;

/**
 * Created by Moha on 7/3/15.
 */
public class ExpenseActivity extends AppCompatActivity {

    private ExpensesFragment expensesFragment;
    private ExpenseEditFragment expenseEditFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //setDefaultPreferences();

        //Include
        FragmentManager fm = getFragmentManager();
        Fragment fragmentExpenses = fm.findFragmentById(R.id.view_conversation);
        Fragment fragmentEditExpenses = fm.findFragmentById(R.id.custom_keyboard);

        FragmentTransaction fragTransaction = fm.beginTransaction();
        boolean transOperations = false;

        if(fragmentEditExpenses == null){
            fragmentEditExpenses = createEditExpenseFragment();
            fragTransaction.add(R.id.custom_keyboard, fragmentEditExpenses);
            transOperations = true;
        }

        if(fragmentExpenses == null){
            fragmentExpenses = createExpenseFragment();
            fragTransaction.add(R.id.view_conversation, fragmentExpenses);
            transOperations = true;
        }

        if(transOperations) {
            fragTransaction.commit();
        }
    }

    protected Fragment createExpenseFragment(){
        String userName = getIntent().getStringExtra(ExpensesFragment.EXTRA_USER_ID);
        long groupId = getIntent().getLongExtra(ExpensesFragment.EXTRA_GROUP_ID, 0);
        return ExpensesFragment.newInstance(userName, groupId);
    }

    protected Fragment createEditExpenseFragment(){
        long expenseId = getIntent().getLongExtra(ExpenseEditFragment.EXTRA_EXPENSE_ID, 0);
        return ExpenseEditFragment.newInstance(expenseId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.custom_keyboard);

        if(fragment != null && fragment instanceof OnKeyEventListener){
            OnKeyEventListener listener = (OnKeyEventListener) fragment;
            return (listener.onKeyPress(keyCode, event))? true : super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    //protected void setDefaultPreferences(){ return; }

}
