package com.jumo.tablas.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.v13.app.FragmentPagerAdapter;

import com.jumo.tablas.R;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.ui.frag.CurrencyInputFragment;
import com.jumo.tablas.ui.frag.PayerInputFragment;
import com.jumo.tablas.ui.util.ExpenseCalculator;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Moha on 1/9/16.
 */
public class InputMethodPageAdapter extends FragmentPagerAdapter {
    private static final String TAG = "InputMethodPageAdapter";
    public static final int FRAGMENT_PAID = 0;
    public static final int FRAGMENT_SHOULD_PAY = 1;
    public static final int FRAGMENT_CURRENCY = 2;
    public static final int FRAGMENT_CATEGORY = 3; //Todo: Now implemented up to currency; category is not yet ready.

    private WeakReference<Context> mContextReference;

    private ExpenseCalculator mCalculator;
    private long mExpenseId;
    private long mGroupId;

    private HashMap<Integer, Fragment> mCurrentFragments;

    //private HashMap<Integer, ViewGroup> mViewGroups;
    //private HashMap<Integer, Object> mObjectsInViewGroup;


    public InputMethodPageAdapter(FragmentManager fm, Context context, long expenseId, long groupId){
        super(fm);
        mContextReference = new WeakReference<Context>(context);
        mCurrentFragments = new HashMap<Integer, Fragment>();
        mExpenseId = expenseId;
        mGroupId = groupId;

        //mViewGroups = new HashMap<>();
        //mObjectsInViewGroup = new HashMap<>();
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position){
            case FRAGMENT_PAID:
                fragment = PayerInputFragment.newInstance(mExpenseId, TablasContract.Payer.OPTION_ROLE_PAID);
                ((PayerInputFragment)fragment).setExpenseCalculator(mCalculator);
                break;
            case FRAGMENT_SHOULD_PAY:
                fragment = PayerInputFragment.newInstance(mExpenseId, TablasContract.Payer.OPTION_ROLE_SHOULD_PAY);
                ((PayerInputFragment)fragment).setExpenseCalculator(mCalculator);
                break;
            case FRAGMENT_CURRENCY:
                fragment = CurrencyInputFragment.newInstance(mExpenseId, "USD");
                break;
            case FRAGMENT_CATEGORY:
                break;
        }
        mCurrentFragments.put(position, fragment);
        return fragment;
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle (int position){
        Resources res = mContextReference.get().getResources();
        switch(position){
            case FRAGMENT_PAID:
                return res.getString(R.string.text_paid_tab);
            case FRAGMENT_SHOULD_PAY:
                return res.getString(R.string.text_payer_tab);
            case FRAGMENT_CURRENCY:
                return res.getString(R.string.text_curr_tab);
            case FRAGMENT_CATEGORY:
                return res.getString(R.string.text_category_tab);
        }
        return null;
    }

    public void setExpenseCalculator(ExpenseCalculator calculator){
        mCalculator = calculator;

        PayerInputFragment payerFragment = ((PayerInputFragment) mCurrentFragments.get(FRAGMENT_PAID));
        if(payerFragment != null){
            payerFragment.setExpenseCalculator(calculator);
        }
        payerFragment = ((PayerInputFragment) mCurrentFragments.get(FRAGMENT_SHOULD_PAY));
        if(payerFragment != null){
            payerFragment.setExpenseCalculator(calculator);
        }

    }



    /*@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try{
            super.destroyItem(container, position, object);
        }catch(Exception e){
            Log.d(TAG, e.toString());
        }
    }

    /*@Override
    public Parcelable saveState(){
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader){

    }*/

    /*public Object instantiateItem (ViewGroup container, int position){
        Object object = super.instantiateItem(container, position);
        mViewGroups.put(position, container);
        mObjectsInViewGroup.put(position, object);
        return object;
    }*/

    /*public void removeAllPages(){
        for(Integer i : mObjectsInViewGroup.keySet()){
            destroyItem(mViewGroups.get(i), i, mObjectsInViewGroup.get(i));
        }
    }*/
}
