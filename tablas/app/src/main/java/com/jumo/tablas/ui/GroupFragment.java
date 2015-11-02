package com.jumo.tablas.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
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
import com.jumo.tablas.account.AccountService;
import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.ui.adapters.GroupCursorAdapter;

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

        getLoaderManager().restartLoader(GROUPS_LOADER, null, this); //TODO: Try to make the loader to reuse Cursor (could not make it reuse it)
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //expenseManager = TablasManager.newInstance(this.getActivity());
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
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(new GroupCursorAdapter(getActivity(), null, mCache));
        mListView.setOnItemClickListener(new GroupListListener());
        mListView.setEmptyView(inflater.inflate(R.layout.list_empty, mListView, false));

        //testContactProviderQuery(AccountService.ACCOUNT_TYPE);
        //testRawContactSearch("Ju");

        return view;
    }


    private void testRawContactSearch(String test){
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);  //ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Context context = getActivity();

        String[] projection = new String[]{ContactsContract.RawContacts._ID //ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                , ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY //ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                , ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
                , ContactsContract.CommonDataKinds.Phone.LABEL};
                //, ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI};

        //TODO:  first, look for raw contacts with that name (which does not work now); then, get the same data as now.
        StringBuilder filter = (new StringBuilder())
                .append(ContactsContract.RawContacts.Entity.MIMETYPE).append(" = ? AND ")
                .append(ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET).append(" = ?");

        String[] filterVals = new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, AccountService.ACCOUNT_TYPE};
        String sortBy = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " ASC";

        Cursor c = context.getContentResolver().query(uri, projection, filter.toString(), filterVals, sortBy);

        logCursorContent(c);
        c.close();
    }

    /**
     * To test querying contacts
     */
    private void testContactProviderQuery(String accountType){
        Uri contactsUri = ContactsContract.RawContacts.CONTENT_URI;

        ContentResolver resolver = getActivity().getContentResolver();

        String[] projection = new String[]{
                ContactsContract.RawContacts.CONTACT_ID,
                ContactsContract.RawContacts._ID,
                ContactsContract.RawContacts.ACCOUNT_TYPE,
                ContactsContract.RawContacts.ACCOUNT_NAME,
                ContactsContract.RawContacts.DATA_SET,
                ContactsContract.RawContacts.SOURCE_ID,
                ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY
        };

        String filter = ContactsContract.RawContacts.ACCOUNT_TYPE + " like ? "; //OR " + ContactsContract.RawContacts.ACCOUNT_NAME + " like ?";
        String[] filterVals = new String[]{ accountType }; //AccountService.ACCOUNT_TYPE};

        Cursor cursor = resolver.query(contactsUri, projection, filter, filterVals, null);
        if(cursor != null) cursor.moveToFirst();

        Log.d(TAG, "Queried Contacts:");
        while(cursor != null && !cursor.isAfterLast()){
            long rawContactId = cursor.getLong(1);
            StringBuilder sb = new StringBuilder();
            sb.append("Contact [").append(rawContactId).append(", ").append(cursor.getString(3)).append(", ").append(cursor.getString(2)).append(", ").append(cursor.getString(6)).append("]:");
            Log.d(TAG, sb.toString());

            ContentResolver resolverDetail = getActivity().getContentResolver();
            Uri contactDetailUri = ContactsContract.Data.CONTENT_URI;
            //ContactsContract.Data.CONTENT_URI;
            //Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);

            //Log.d(TAG, contactDetailUri.toString());

            projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                    ContactsContract.Data.ACCOUNT_TYPE_AND_DATA_SET
            };

            //filter = ContactsContract.Contacts.Entity.RAW_CONTACT_ID + " = ?";
            //filterVals = new String[]{ String.valueOf(rawContactId) };

            filter = /*"(" + ContactsContract.Contacts.Entity.MIMETYPE + " = ?  " + /*AND " + ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " IS NOT NULL) */ /*" OR " + ContactsContract.Contacts.Entity.MIMETYPE  + " = ?) AND " + ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET + " = ? AND " + */ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = ?";
            filterVals = new String[]{ /*ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,  AccountService.ACCOUNT_TYPE, */String.valueOf(rawContactId)};

            String sortBy = ContactsContract.Contacts.Entity.RAW_CONTACT_ID + " ASC"; //, " + ContactsContract.Contacts.Entity.DISPLAY_NAME + " ASC ";

            //Retrieving all Data elements for every contact.
            Cursor cursorDetail = resolverDetail.query(contactDetailUri, projection, filter, filterVals, null);
            cursorDetail.moveToFirst();

            logCursorContent(cursorDetail);
            cursor.moveToNext();
        }
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
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //no need to check for GROUPS_LOADER
        Uri uri = TablasContract.BASE_URI.buildUpon().appendPath(TablasContract.User.getInstance().getTableName())
                .appendPath(mUserName).appendPath("groups")
                .build();

        TablasManager.newInstance(getActivity()).createSampleData();
        Log.d(TAG, "Loader callback: onCreateLoad()");

        return new CursorLoader(getActivity(), uri, null, null, null, null);
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
            i.putExtra(ExpenseFragment.EXTRA_GROUP_ID, group.getId());
            i.putExtra(ExpenseFragment.EXTRA_USER_ID, mUserName);
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
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(menu);

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
