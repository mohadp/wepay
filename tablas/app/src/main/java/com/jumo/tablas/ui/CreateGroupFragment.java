package com.jumo.tablas.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.loaders.SearchContactThreadHandler;

/**
 * Created by Moha on 10/27/15.
 */
public class CreateGroupFragment extends Fragment implements SearchContactThreadHandler.OnSearchCompleted, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private static final String TAG = "CreateGroupFragment";
    public static final String EXTRA_USER_ID = "user_id";

    private String mUserId;
    private LruCache<String, Bitmap> mCache; //cache for user profile images
    private SearchContactThreadHandler mContactSearcher;

    //This are view references in the view
    private SearchView mSearchView;
    private ListView mAddedContacts;


    public static CreateGroupFragment newInstance(String userId){
        CreateGroupFragment fragment = new CreateGroupFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mUserId = getArguments().getString(EXTRA_USER_ID);
        mContactSearcher = new SearchContactThreadHandler(getActivity(), new Handler(), this);
        mContactSearcher.start();
        mContactSearcher.getLooper();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        mSearchView = (SearchView)view.findViewById(R.id.search_contact);
        mAddedContacts = (ListView)view.findViewById(R.id.list_added_members);

        return view;
    }


    /**
     * This will set the adapter for the SearchView and the added members ListView
     */
    @Override
    public void handleSearchResults() {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //send query to the HandlerThread

        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        //clicking on a selection, we add this contact into the ListView
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return false;
    }

}
