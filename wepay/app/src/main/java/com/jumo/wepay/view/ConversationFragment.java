package com.jumo.wepay.view;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.jumo.wepay.*;
import com.jumo.wepay.controller.*;

public class ConversationFragment extends Fragment{
	
	private static final String TAG = "ConversationFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_USER = "com.jumo.wepay.user_id";
	private static final String EXTRA_GROUP = "com.jumo.wepay.group_id";

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
    private ListAdapter mAdapter;

    public static GroupFragment newInstance(String userId, long groupId) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER, userId);
		args.putLong(EXTRA_GROUP, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConversationFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expenseManager = ExpenseManager.newInstance(this.getActivity());

        String userName = getArguments().getString(EXTRA_USER);
		long groupId = getArguments().getLong(EXTRA_GROUP);
        //TODO: add a cursor adapter for expenses...
		//mAdapter = new GroupCursorAdapter(this.getActivity(), expenseManager.getUserGroups(userName));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);

        return view;
    }
}
