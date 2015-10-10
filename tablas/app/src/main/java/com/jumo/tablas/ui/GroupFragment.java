package com.jumo.tablas.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.adapters.GroupCursorAdapter;
import com.jumo.tablas.util.ExpenseManager;
import android.widget.*;

import com.jumo.tablas.model.Group;
import com.jumo.tablas.provider.TablasContract;
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
    private LruCache<String, Bitmap> mCache;
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

        getLoaderManager().restartLoader(GROUPS_LOADER, null, this); //TODO: Try to make the loader to reuse Cursor (could not make it reuse it)

        /*Log.d(TAG, "onActivityCreated()");
        if(mCursor != null){
            Log.d(TAG, "Cursor is closed? " + mCursor.isClosed());
        }*/

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //expenseManager = ExpenseManager.newInstance(this.getActivity());
        setHasOptionsMenu(true);
        setRetainInstance(true);

        /*Log.d(TAG, "onCreate()");
        if(mCursor != null){
            Log.d(TAG, "Cursor is closed? " + mCursor.isClosed());
        }*/


        mUserName = getArguments().getString(EXTRA_USER);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;  // Use 1/8th of the available memory for this memory cache.
        mCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*Log.d(TAG, "onCreateView()");
        if(mCursor != null){
            Log.d(TAG, "Cursor is closed? " + mCursor.isClosed());
        }*/
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(new GroupCursorAdapter(getActivity(), null, mCache));
        mListView.setOnItemClickListener(new GroupListListener());
        mListView.setEmptyView(inflater.inflate(R.layout.list_empty, mListView, false));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*Log.d(TAG, "onResume()");
        if(mCursor != null){
            Log.d(TAG, "Cursor is closed? " + mCursor.isClosed());
        }*/
        //Log.d(TAG, "mUserName: " + mUserName);

        /*Loader loader = getLoaderManager().getLoader(GROUPS_LOADER);
        if(loader != null && loader.isStarted()){
            loader.forceLoad();
            Log.d(TAG, "onResume(): Forcing Loader Load");
        }*/
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*Log.d(TAG, "onAttach()");
        if(mCursor != null){
            Log.d(TAG, "Cursor is closed? " + mCursor.isClosed());
        }*/
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        /*Log.d(TAG, "onDetach()");
        if(mCursor != null){
            Log.d(TAG, "Cursor is closed? " + mCursor.isClosed());
        }*/
        //mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //no need to check for GROUPS_LOADER
        Uri uri = TablasContract.BASE_URI.buildUpon().appendPath(TablasContract.User.getInstance().getTableName())
                .appendPath(mUserName).appendPath("groups")
                .build();

        ExpenseManager.newInstance(getActivity()).createSampleData();
        Log.d(TAG, "Loader callback: onCreateLoad()");

        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
        if(this.getActivity() == null || mListView == null) return;

        /*if(mCursor != null){
            Log.d(TAG, "Cursor is closed? " + mCursor.isClosed());
        }*/

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
            i.putExtra(ExpenseFragment.EXTRA_GROUP_ID, group.getId());
            i.putExtra(ExpenseFragment.EXTRA_USER_ID, mUserName);
            startActivity(i);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.default_menu, menu);
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
