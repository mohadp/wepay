package com.jumo.tablas.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.frag.ExpenseInputFragment;
import com.jumo.tablas.ui.frag.ExpensesFragment;
import com.jumo.tablas.ui.util.OnKeyEventListener;
import com.jumo.tablas.ui.views.LinearLayoutResize;

/**
 * Created by Moha on 7/3/15.
 */
public class ExpenseActivity extends AppCompatActivity implements ExpenseInputFragment.Callback, LinearLayoutResize.OnSizeChange {

    private ExpensesFragment mExpensesFragment;
    private ExpenseInputFragment mExpenseEditFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //setDefaultPreferences();

        //Include
        FragmentManager fm = getFragmentManager();
        mExpensesFragment = (ExpensesFragment) fm.findFragmentById(R.id.view_conversation);
        mExpenseEditFragment = (ExpenseInputFragment) fm.findFragmentById(R.id.custom_keyboard);

        FragmentTransaction fragTransaction = fm.beginTransaction();
        boolean transOperations = false;

        if(mExpenseEditFragment == null){
            mExpenseEditFragment = createEditExpenseFragment();
            mExpenseEditFragment.setCallback(this);
            fragTransaction.add(R.id.custom_keyboard, mExpenseEditFragment);
            transOperations = true;
        }

        if(mExpensesFragment == null){
            mExpensesFragment = createExpenseFragment();
            mExpensesFragment.setSizeListener(this);
            fragTransaction.add(R.id.view_conversation, mExpensesFragment);
            transOperations = true;
        }

        if(transOperations) {
            fragTransaction.commit();
        }
    }

    protected ExpensesFragment createExpenseFragment(){
        String userName = getIntent().getStringExtra(ExpensesFragment.EXTRA_USER_ID);
        long groupId = getIntent().getLongExtra(ExpensesFragment.EXTRA_GROUP_ID, 0);
        return ExpensesFragment.newInstance(userName, groupId);
    }

    protected ExpenseInputFragment createEditExpenseFragment(){
        long expenseId = getIntent().getLongExtra(ExpenseInputFragment.EXTRA_EXPENSE_ID, 0);
        return ExpenseInputFragment.newInstance(expenseId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(mExpenseEditFragment != null){
            OnKeyEventListener listener = (OnKeyEventListener) mExpenseEditFragment;
            return (listener.onKeyPress(keyCode, event))? true : super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean isSystemKeyboardShowing() {
        return (mExpensesFragment != null)? mExpensesFragment.isSystemKeyboardShowing() : false;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(mExpenseEditFragment != null){
            mExpenseEditFragment.onSizeChanged(w, h, oldw, oldh);
        }
    }

}
