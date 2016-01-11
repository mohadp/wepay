package com.jumo.tablas.ui.frag;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.adapters.GroupCursorAdapter;

/**
 * Created by Moha on 1/9/16.
 */
public class PayerFragment extends Fragment {
    public static final String EXTRA_EXPENSE_ID = "com.jumo.tablas.expense_id";
    public static final String EXTRA_PAYER_MODE = "com.jumo.tablas.expense_id";

    private long mExpenseId;
    private long mPayerMode; //determines whether this is for

    public static PayerFragment newInstance(long expenseId, int payerMode){
        PayerFragment fragment = new PayerFragment();
        Bundle args = new Bundle();
        if(expenseId >= 0) {
            args.putLong(EXTRA_EXPENSE_ID, expenseId);
        }
        args.putInt(EXTRA_PAYER_MODE, payerMode);
        fragment.setArguments(args);
        return fragment;
    }

    public PayerFragment(){ }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //expenseManager = TablasManager.getInstance(this.getActivity());
        //setHasOptionsMenu(true);
        //setRetainInstance(true);

        mExpenseId = getArguments().getLong(EXTRA_EXPENSE_ID, -1);
        mPayerMode = getArguments().getInt(EXTRA_PAYER_MODE);


        /*final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;  // Use 1/8th of the available memory for this memory cache.
        mCache = new LruCache<Object, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Object key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes
            }
        };*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cust_input_payer, container, false);

        /* Set the adapter
        mListView = (ListView) view.findViewById(R.id.list_groups);
        mListView.setAdapter(new GroupCursorAdapter(getActivity(), null, this));
        mListView.setOnItemClickListener(new GroupListListener());
        mListView.setEmptyView(inflater.inflate(R.layout.list_empty, mListView, false));*/

        return view;
    }





}
