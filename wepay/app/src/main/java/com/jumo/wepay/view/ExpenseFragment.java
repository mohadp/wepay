package com.jumo.wepay.view;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jumo.wepay.R;
import com.jumo.wepay.controller.ExpenseManager;
import com.jumo.wepay.model.Expense;
import com.jumo.wepay.provider.WepayContract;
import com.jumo.wepay.provider.dao.ExpenseCursor;
import com.jumo.wepay.provider.dao.GroupCursor;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@linkx OnFragmentInteractionListener}
 * interface.
 */
public class ExpenseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "ExpenseFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXTRA_GROUP_ID = "com.jumo.wepay.group_id";
    public static final String EXTRA_USER_ID = "com.jumo.wepay.user_id";

    //Loaders
    public static final int LOADER_EXPENSES = 0;

    //private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    //Fragment's attributes
    private String mUserName;
    private long mGroupId;

    public static ExpenseFragment newInstance(String userId, long groupId) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER_ID, userId);
        args.putLong(EXTRA_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExpenseFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        mUserName = getArguments().getString(EXTRA_USER_ID);
        mGroupId = getArguments().getLong(EXTRA_GROUP_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
		mListView.setAdapter(new ExpenseCursorAdapter(getActivity(),null));

        getLoaderManager().initLoader(LOADER_EXPENSES, null, this);

        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);
		
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = WepayContract.BASE_URI.buildUpon().appendPath(WepayContract.Expense.TABLE_NAME)
                .appendPath("user").appendPath(mUserName).appendPath("group").appendPath(Long.toString(mGroupId))
                .build();

        StringBuilder sortBy = new StringBuilder(WepayContract.Expense.CREATED_ON).append(" ASC");
        return new CursorLoader(getActivity(), uri, null, null, null, sortBy.toString());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(this.getActivity() == null || mListView == null) return;
        ((ExpenseCursorAdapter)mListView.getAdapter()).swapCursor(new ExpenseCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(mListView == null) return;
        ((ExpenseCursorAdapter)mListView.getAdapter()).swapCursor(null);
    }

    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }*/

}
