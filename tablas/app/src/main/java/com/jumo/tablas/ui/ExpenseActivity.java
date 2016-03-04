package com.jumo.tablas.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.frag.ExpenseInputFragment;
import com.jumo.tablas.ui.frag.ExpenseListFragment;
import com.jumo.tablas.ui.util.OnKeyEventListener;
import com.jumo.tablas.ui.views.LinearLayoutResize;

/**
 * Created by Moha on 7/3/15.
 */
public class ExpenseActivity extends AppCompatActivity implements ExpenseInputFragment.CustomKeyboardHelper, LinearLayoutResize.OnSizeChange {

    private static final String TAG = "ExpenseActivity";
    private final static String SAVE_EXPENSE_FRAG = "e";
    private final static String SAVE_EXPENSE_INPUT_FRAG = "ei";

    private ExpenseListFragment mExpenseListFragment;
    private ExpenseInputFragment mExpenseEditFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //setDefaultPreferences();
        FragmentManager fm = getFragmentManager();

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mExpenseListFragment = (ExpenseListFragment)fm.getFragment(savedInstanceState, SAVE_EXPENSE_FRAG);
            mExpenseEditFragment = (ExpenseInputFragment)fm.getFragment(savedInstanceState, SAVE_EXPENSE_INPUT_FRAG);
            //Make sure to remove the previous mExpenseEditFragment:
            //getFragmentManager().beginTransaction().remove(mExpenseEditFragment).commit();
        }else {
            FragmentTransaction fragTransaction = fm.beginTransaction();
            boolean atLeastOneTransaction = false;
            //Create the fragments
            mExpenseListFragment = (ExpenseListFragment) fm.findFragmentById(R.id.view_conversation);
            if(mExpenseListFragment == null){
                mExpenseListFragment = createExpenseFragment();
                fragTransaction.add(R.id.view_conversation, mExpenseListFragment);
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getFragmentManager().putFragment(outState, SAVE_EXPENSE_FRAG, mExpenseListFragment);
        getFragmentManager().putFragment(outState, SAVE_EXPENSE_INPUT_FRAG, mExpenseEditFragment);
    }


    @Override
    protected void onStart(){
        super.onStart();
        mExpenseListFragment.setSizeListener(this);
        mExpenseEditFragment.setCallback(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mExpenseListFragment.setSizeListener(null);
        mExpenseEditFragment.setCallback(null);
    }

    protected ExpenseListFragment createExpenseFragment(){
        String userName = getIntent().getStringExtra(ExpenseListFragment.EXTRA_USER_ID);
        long groupId = getIntent().getLongExtra(ExpenseListFragment.EXTRA_GROUP_ID, 0);
        return ExpenseListFragment.newInstance(userName, groupId);
    }

    protected ExpenseInputFragment createEditExpenseFragment(){
        long expenseId = getIntent().getLongExtra(ExpenseInputFragment.EXTRA_EXPENSE_ID, 0);
        long groupId = getIntent().getLongExtra(ExpenseListFragment.EXTRA_GROUP_ID, 0);
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
        return (mExpenseListFragment != null)? mExpenseListFragment.isSystemKeyboardShowing() : false;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(mExpenseEditFragment != null){
            mExpenseEditFragment.onSizeChanged(w, h, oldw, oldh);
        }
    }

}
