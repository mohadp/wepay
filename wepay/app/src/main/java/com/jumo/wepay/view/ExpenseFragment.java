package com.jumo.wepay.view;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jumo.wepay.R;
import com.jumo.wepay.controller.ExpenseManager;
import com.jumo.wepay.model.Expense;
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
public class ExpenseFragment extends Fragment {

    private static final String TAG = "ExpenseFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXTRA_GROUP_ID = "com.jumo.wepay.group_id";
    public static final String EXTRA_USER_ID = "com.jumo.wepay.user_id";

    private ExpenseManager expenseManager;



    //private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ExpenseCursorAdapter mAdapter;
	private ExpenseCursor mExpenses;

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
        expenseManager = ExpenseManager.newInstance(this.getActivity());
		
        mUserName = getArguments().getString(EXTRA_USER_ID);
        mGroupId = getArguments().getLong(EXTRA_GROUP_ID);
        
		//Moving all this to the AsyncTask, all on the OnCreateView
		//mAdapter = new GroupCursorAdapter(this.getActivity(), expenseManager.getUserGroups(mUserName));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
		//mListView.setEmptyView(inflater.inflate(R.layout.list_empty, container, false));

		//Calling the adapter setup in the AsynchTaskl
        //mListView.setAdapter(mAdapter);
		new ExpenseLoaderTask().execute();

        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);
		
        return view;
    }
	
	protected void setupAdapter(){
		if(this.getActivity() == null || mListView == null) return;
		
		if(mExpenses != null){
			mAdapter = new ExpenseCursorAdapter(this.getActivity(), mExpenses);
		}else{
			mAdapter = null;
		}
		mListView.setAdapter(mAdapter);
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
	
	private class ExpenseLoaderTask extends AsyncTask<Void, Void,ExpenseCursor>{

		@Override
		protected ExpenseCursor doInBackground(Void... p1){
			//All the database work is done here
			//expenseManager.createSampleData();
			return expenseManager.getUserGroupExpenses(mUserName, mGroupId);
		}

		//Updating the UI on this method, which is executed in the main thread.
		@Override
		protected void onPostExecute(ExpenseCursor expenses){
			mExpenses = expenses;
			setupAdapter();
		}
	}
}
