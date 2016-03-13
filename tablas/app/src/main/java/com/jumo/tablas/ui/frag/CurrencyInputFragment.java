package com.jumo.tablas.ui.frag;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jumo.tablas.R;

/**
 * Created by Moha on 1/9/16.
 */
public class CurrencyInputFragment extends Fragment {
    public static final String EXTRA_EXPENSE_ID = "com.jumo.tablas.expense_id";
    public static final String EXTRA_USER_CURRENCY = "com.jumo.tablas.user_currency_id";

    private long mExpenseId;
    private String mUserCurrency; //determines whether this is for

    public static CurrencyInputFragment newInstance(long expenseId, String userCurrency){
        CurrencyInputFragment fragment = new CurrencyInputFragment();
        Bundle args = new Bundle();
        if(expenseId >= 0) {
            args.putLong(EXTRA_EXPENSE_ID, expenseId);
        }
        args.putString(EXTRA_USER_CURRENCY, userCurrency);
        fragment.setArguments(args);
        return fragment;
    }

    public CurrencyInputFragment(){ }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mExpenseId = getArguments().getLong(EXTRA_EXPENSE_ID, -1);
        mUserCurrency = getArguments().getString(EXTRA_USER_CURRENCY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_currency, container, false);
        return view;
    }

}
