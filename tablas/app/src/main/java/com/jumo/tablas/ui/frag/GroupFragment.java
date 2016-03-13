package com.jumo.tablas.ui.frag;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jumo.tablas.R;
import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.ui.CreateGroupActivity;
import com.jumo.tablas.ui.ExpenseActivity;
import com.jumo.tablas.ui.adapters.GroupCursorAdapter;

import android.widget.*;

import com.jumo.tablas.model.Group;
import com.jumo.tablas.provider.dao.*;

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

    //results
    private static final int REQUEST_NEW_GROUP = 0;

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
    private LruCache<Object, Bitmap> mCache;
    //private Cursor mCursor;

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
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Loader loader = getLoaderManager().getLoader(GROUPS_LOADER);

        getLoaderManager().restartLoader(GROUPS_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //expenseManager = TablasManager.getInstance(this.getActivity());
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mUserName = getArguments().getString(EXTRA_USER);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;  // Use 1/8th of the available memory for this memory cache.
        mCache = new LruCache<Object, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Object key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(R.id.list_groups);
        mListView.setAdapter(new GroupCursorAdapter(getActivity(), null));
        mListView.setOnItemClickListener(new GroupListListener());
        mListView.setEmptyView(inflater.inflate(R.layout.list_empty, mListView, false));

        return view;
    }

    private void logCursorContent(Cursor cursor){
        while(cursor != null && !cursor.isAfterLast()){
            StringBuilder sbDetail = new StringBuilder("\t{");

            for(int i = 0; i < cursor.getColumnCount(); i++){
                sbDetail.append(cursor.getColumnName(i)).append(": ").append(cursor.getString(i));
                if(i < cursor.getColumnCount() - 1) {
                    sbDetail.append("; ");
                }
            }
            sbDetail.append("}\n");
            Log.d(TAG, sbDetail.toString());
            cursor.moveToNext();
        }
        cursor.moveToNext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_NEW_GROUP:
                getLoaderManager().restartLoader(GROUPS_LOADER, null, this);
                break;
            default:
                return;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        TablasManager.getInstance(getActivity()).createSampleData();
        Log.d(TAG, "Loader callback: onCreateLoad()");

        return TablasManager.getInstance(getActivity()).getGroupsWithBalanceLoader(mUserName);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
        if(this.getActivity() == null || mListView == null) return;

        //mCursor = data;
        ((GroupCursorAdapter)mListView.getAdapter()).changeCursor(new EntityCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoadReset()");
        if(mListView == null) return;

        ((GroupCursorAdapter)mListView.getAdapter()).changeCursor(null);
    }


    /**
     * List item onImagesLoaded to respond to list items.
     */
    private class GroupListListener implements AbsListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GroupCursorAdapter groups = (GroupCursorAdapter) parent.getAdapter();
            Group group = groups.getItem(position);

            //TODO: I will call an interface method for upper class to either start an activity or just update a fragment

            Intent i = new Intent(GroupFragment.this.getActivity(), ExpenseActivity.class);
            i.putExtra(ExpenseListFragment.EXTRA_GROUP_ID, group.getId());
            i.putExtra(ExpenseListFragment.EXTRA_USER_ID, mUserName);
            startActivity(i);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.group_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        switch(menu.getItemId()){
            case R.id.menu_item_new_group:
                Intent i = new Intent(getActivity(), CreateGroupActivity.class);
                startActivityForResult(i, REQUEST_NEW_GROUP);
                return true;
            default:
                return super.onOptionsItemSelected(menu);

        }
    }
}
