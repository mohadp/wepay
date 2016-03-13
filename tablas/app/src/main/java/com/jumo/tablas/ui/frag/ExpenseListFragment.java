package com.jumo.tablas.ui.frag;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jumo.tablas.R;
import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.provider.dao.EntityCursor;
import com.jumo.tablas.ui.adapters.ExpenseCursorAdapter;
import com.jumo.tablas.ui.loaders.ExpenseUserThreadHandler;
import com.jumo.tablas.ui.views.LinearLayoutResize;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@linkx OnFragmentInteractionListener}
 * interface.
 */
public class ExpenseListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ExpenseListFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXTRA_GROUP_ID = "com.jumo.tablas.group_id";
    public static final String EXTRA_USER_ID = "com.jumo.tablas.user_id";
    //Loaders
    public static final int LOADER_EXPENSES = 0;
    public static final int LOADER_MEMBERS = 1;
    /**
     * The fragment's ListView/GridView.
     */
    private RecyclerView mRecyclerView;
    private LinearLayoutResize mConversationLayout;
    private int mMaxConversationHeight; // height for whenever there is no system keyboard
    //Fragment's attributes
    private String mUserName;
    private long mGroupId;
    private ExpenseUserThreadHandler mPayerLoader;
    private LinearLayoutResize.OnSizeChange mSizeListener;


    public static ExpenseListFragment newInstance(String userId, long groupId) {
        ExpenseListFragment fragment = new ExpenseListFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER_ID, userId);
        args.putLong(EXTRA_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(LOADER_EXPENSES, null, this);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExpenseListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mUserName = getArguments().getString(EXTRA_USER_ID);
        mGroupId = getArguments().getLong(EXTRA_GROUP_ID);
        mPayerLoader = new ExpenseUserThreadHandler(getActivity(), new Handler());
        mPayerLoader.start();
        mPayerLoader.getLooper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        // Set the adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_messages);
		mRecyclerView.setAdapter(new ExpenseCursorAdapter(getActivity(), null, mPayerLoader));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set the references to the components in the fragment layout
        mConversationLayout = (LinearLayoutResize) view.findViewById(R.id.view_conversations);

        //Record size of the conversation layout, useful to identify when the system keyboard is on.
        LinearLayoutResize.OnSizeChange onSizeChange = new LinearLayoutResize.OnSizeChange() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                //Log.d(TAG, "Old sizes: w: " + w + ", h: " + h + ", " + ", oldw: " + oldw + ", oldh: " + oldh);
                //Log.d(TAG, "MaxConversationHeight: " + mMaxConversationHeight);

                if(oldh == 0 || mMaxConversationHeight < h){ //always keep the largest drawing rectangle to know when the system keyboard is showing or not.
                    mMaxConversationHeight = h;
                }
                if(mSizeListener != null){
                    mSizeListener.onSizeChanged(w,h,oldw, oldh);
                }
            }
        };
        mConversationLayout.setOnSizeChange(onSizeChange);

        return view;
    }

    /**
     * This method determines if the system soft keyboard is showing or not depending on the top layout's size;
     * this method is accurate only if the activity is resized when the soft keyboard appears (when the app pans
     * or does nothing, this method does not return the correc
     * @return a boolean; true if system soft keyboard is showing; false if not.
     */
    public boolean isSystemKeyboardShowing(){
        Rect rect = new Rect();
        mConversationLayout.getDrawingRect(rect);
        Log.d(TAG, "DrawingRect: " + rect.height() + "    mMaxConversationHeight: " + mMaxConversationHeight);
        if(rect.height() < mMaxConversationHeight){
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mPayerLoader.clearQueue();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mPayerLoader.quit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.default_menu, menu);
    }

    ///////////// Methods for LoaderCallbacks interface to handle this fragment's loaders /////////////

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");

        if(id == LOADER_EXPENSES){
            return TablasManager.getInstance(getActivity()).getExpensesWithBalanceLoader(mGroupId, "USD"); //TODO: Update the USD parameter to the user's currency preference
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
        int id = loader.getId();

        if(id == LOADER_EXPENSES) {
            if (this.getActivity() == null || mRecyclerView == null) {
                return;
            }
            ((ExpenseCursorAdapter) mRecyclerView.getAdapter()).changeCursor(new EntityCursor(data));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();

        if(id == LOADER_EXPENSES) {
            if (mRecyclerView == null)
                return;
            ((ExpenseCursorAdapter) mRecyclerView.getAdapter()).changeCursor(null);
        }
    }

    public void setSizeListener(LinearLayoutResize.OnSizeChange sizeListener) {
        mSizeListener = sizeListener;
    }

}
