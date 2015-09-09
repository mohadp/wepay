package com.jumo.wepay.view;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jumo.wepay.R;
import com.jumo.wepay.controller.ExpenseManager;
import android.widget.*;
import android.os.*;

import com.jumo.wepay.model.Group;
import com.jumo.wepay.provider.WepayContract;
import com.jumo.wepay.provider.dao.*;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@links OnFragmentInteractionListener}
 * interface.
 */
public class GroupFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "GroupFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_USER = "com.jumo.wepay.user_id";

    //Loaders
    private static final int GROUPS_LOADER = 0;

    //private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
	private String mUserName;

    public static GroupFragment newInstance(String userId) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER, userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroupFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //expenseManager = ExpenseManager.newInstance(this.getActivity());
		
        mUserName = getArguments().getString(EXTRA_USER);
        
		//Moving all this to the AsyncTask, all on the OnCreateView
		//mAdapter = new GroupCursorAdapter(this.getActivity(), expenseManager.getUserGroups(mUserName));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(new GroupCursorAdapter(getActivity(),null));
        mListView.setOnItemClickListener(new GroupListListener());

        getLoaderManager().initLoader(GROUPS_LOADER, null, this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //no need to check for GROUPS_LOADER
        Uri uri = WepayContract.BASE_URI.buildUpon().appendPath(WepayContract.User.TABLE_NAME)
                .appendPath(mUserName).appendPath("groups")
                .build();

        ExpenseManager.newInstance(getActivity()).createSampleData();

        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
        if(this.getActivity() == null || mListView == null) return;

        ((GroupCursorAdapter)mListView.getAdapter()).changeCursor(new GroupCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoadReset");
        if(mListView == null) return;

        ((GroupCursorAdapter)mListView.getAdapter()).changeCursor(null);
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
     * List item listener to respond to list items.
     */
    private class GroupListListener implements AbsListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GroupCursorAdapter groups = (GroupCursorAdapter) parent.getAdapter();
            Group group = groups.getItem(position);

            //TODO: I will call an interface method for upper class to either start an activity or just update a fragment

            Intent i = new Intent(GroupFragment.this.getActivity(), ExpenseActivity.class);
            i.putExtra(ExpenseFragment.EXTRA_GROUP_ID, group.getId());
            i.putExtra(ExpenseFragment.EXTRA_USER_ID, mUserName);
            startActivity(i);
        }
    }

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
