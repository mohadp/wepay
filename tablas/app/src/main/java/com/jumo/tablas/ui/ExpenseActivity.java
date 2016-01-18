package com.jumo.tablas.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    private static final String TAG = "ExpenseActivity";
    private final static String SAVE_EXPENSE_FRAG = "e";
    private final static String SAVE_EXPENSE_INPUT_FRAG = "ei";

    private ExpensesFragment mExpensesFragment;
    private ExpenseInputFragment mExpenseEditFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //setDefaultPreferences();
        FragmentManager fm = getFragmentManager();

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mExpensesFragment = (ExpensesFragment)fm.getFragment(savedInstanceState, SAVE_EXPENSE_FRAG);
            mExpenseEditFragment = (ExpenseInputFragment)fm.getFragment(savedInstanceState, SAVE_EXPENSE_INPUT_FRAG);
            //Make sure to remove the previous mExpenseEditFragment:
            //getFragmentManager().beginTransaction().remove(mExpenseEditFragment).commit();
        }else {
            FragmentTransaction fragTransaction = fm.beginTransaction();
            boolean atLeastOneTransaction = false;
            //Create the fragments
            mExpensesFragment = (ExpensesFragment) fm.findFragmentById(R.id.view_conversation);
            if(mExpensesFragment == null){
                mExpensesFragment = createExpenseFragment();
                fragTransaction.add(R.id.view_conversation, mExpensesFragment);
                atLeastOneTransaction = true;
            }

            mExpenseEditFragment = (ExpenseInputFragment) fm.findFragmentById(R.id.custom_keyboard);
            if(mExpenseEditFragment == null) {
                Log.d(TAG, "ExpenseEditFragment being created again!");
                mExpenseEditFragment = createEditExpenseFragment();
                fragTransaction.add(R.id.custom_keyboard, mExpenseEditFragment);
                atLeastOneTransaction = true;
            }

            if(atLeastOneTransaction){
                fragTransaction.commit();
            }
        }
        //Because of bug, I cannot "setRetainInstance(true)" this fragment; need to recreate every time: https://code.google.com/p/android/issues/detail?id=42601#c10

        mExpenseEditFragment.setCallback(this);
        mExpensesFragment.setSizeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        mExpensesFragment.setSizeListener(null);
        mExpenseEditFragment.setCallback(null);
        //Save ony one of them
        getFragmentManager().putFragment(outState, SAVE_EXPENSE_FRAG, mExpensesFragment);
        getFragmentManager().putFragment(outState, SAVE_EXPENSE_INPUT_FRAG, mExpenseEditFragment);
    }

    protected ExpensesFragment createExpenseFragment(){
        String userName = getIntent().getStringExtra(ExpensesFragment.EXTRA_USER_ID);
        long groupId = getIntent().getLongExtra(ExpensesFragment.EXTRA_GROUP_ID, 0);
        return ExpensesFragment.newInstance(userName, groupId);
    }

    protected ExpenseInputFragment createEditExpenseFragment(){
        long expenseId = getIntent().getLongExtra(ExpenseInputFragment.EXTRA_EXPENSE_ID, 0);
        long groupId = getIntent().getLongExtra(ExpensesFragment.EXTRA_GROUP_ID, 0);
        return ExpenseInputFragment.newInstance(expenseId, groupId);
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
