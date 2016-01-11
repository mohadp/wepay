package com.jumo.tablas.ui.frag;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.jumo.tablas.R;
import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.model.Group;
import com.jumo.tablas.model.Member;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.ui.adapters.ContactSearchAdapter;
import com.jumo.tablas.ui.util.BitmapLoader;
import com.jumo.tablas.ui.util.CacheManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Moha on 10/27/15.
 */
public class CreateGroupFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, CacheManager<Object, Bitmap>{

    private static final String TAG = "CreateGroupFragment";
    public static final String EXTRA_USER_ID = "user_id";

    private String mUserId;
    private LruCache<Object, Bitmap> mCache; //cache for user profile images

    //This are view references in the view
    private SearchView mSearchView;
    private EditText mEditGroupName;
    private ListView mAddedContacts;
    private SelectedContactAdapter mAddedAdapter;
    private ArrayList<HashMap<String, String>> mAddedList;
    private HashMap<String, HashMap<String, String>> mAddedListById;


    private FilterQueryProvider mFilterQueryProvider = new FilterQueryProvider() {
        public Cursor runQuery(CharSequence constraint) {
            // assuming you have your custom DBHelper instance
            // ready to execute the DB request
            String displayName = "";
            if(constraint != null){
                displayName = constraint.toString();
            }
            return TablasManager.getInstance(getActivity()).searchForContactsByName(displayName);
        }
    };

    private View.OnClickListener mRemoveContactBtnListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            String phone = (String)v.getTag();
            mAddedList.remove(mAddedListById.get(phone));
            mAddedListById.remove(phone);
            mAddedAdapter.notifyDataSetChanged();
        }
    };

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

        initializeImageCache();
        mUserId = getArguments().getString(EXTRA_USER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        mSearchView = (SearchView)view.findViewById(R.id.search_contact);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSuggestionsAdapter(new ContactSearchAdapter(getActivity(), null, this));

        mAddedContacts = (ListView)view.findViewById(R.id.list_added_members);
        mAddedList = new ArrayList<HashMap<String, String>>();
        mAddedListById = new HashMap<String, HashMap<String, String>>();
        String[] fromCols = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
        };
        int[] toViewIds = new int[]{
                R.id.list_contact_name,
                R.id.img_contact,
                R.id.btn_remove_contact
        };

        mAddedAdapter = new SelectedContactAdapter(getActivity(), mAddedList, R.layout.list_item_contact, fromCols, toViewIds);
        mAddedContacts.setAdapter(mAddedAdapter);

        mEditGroupName = (EditText)view.findViewById(R.id.edit_group_name);

        return view;
    }

    /**
     * Initialize the image cache for the contacts being searched
     */
    private void initializeImageCache(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;  // Use 1/8th of the available memory for this memory cache.
        mCache = new LruCache<Object, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Object key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes
            }
        };
    }

    private void createGroupAndMembers(){
        //TODO: add a toaster indicating that if there are no people, then there must be one person in the group.
        //TODO: Create a centrals static method to generate ContentProviderOperations.Builder for entities.
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        Group group = new Group();
        group.setCreatedOn(new Date());
        group.setName(mEditGroupName.getText().toString());
        ops.add(getGroupContentProviderOp(group));

        Member member = new Member();
        member.setAdmin(true);
        member.setLeftGroup(false);
        member.setUserId(mUserId);
        member.setCurrentUser(true);
        ops.add(getMemberContentProviderOp(member));

        for(String userId : mAddedListById.keySet()){
            member = new Member();
            member.setAdmin(false);
            member.setLeftGroup(false);
            member.setUserId(userId);
            member.setCurrentUser(false);
            ops.add(getMemberContentProviderOp(member));
        }

        try{
            ContentProviderResult[] result = getActivity().getContentResolver().applyBatch(TablasContract.AUTHORITY, ops);
            for(ContentProviderResult cpr : result){
                Log.d(TAG, cpr.toString());
            }
        }catch(OperationApplicationException e){
            Log.d(TAG, "Creating Group & Members: ApplyBatch of operations failed: ", e);
        }catch(RemoteException e){
            Log.d(TAG, "Creating Group & Members: ApplyBatch of operations failed", e);
        }
    }

    private ContentProviderOperation getGroupContentProviderOp(Group group){
        Uri groupTable = TablasContract.BASE_URI.buildUpon().appendPath(TablasContract.Group.getInstance().getTableName()).build();
        ContentProviderOperation op = ContentProviderOperation.newInsert(groupTable)
                .withValue(TablasContract.Group.GROUP_NAME, group.getName())
                .withValue(TablasContract.Group.GROUP_CREATED_ON, group.getCreatedOn().getTime())
                .build();
        return op;
    }

    private ContentProviderOperation getMemberContentProviderOp(Member member){
        Uri memberTable = TablasContract.BASE_URI.buildUpon().appendPath(TablasContract.Member.getInstance().getTableName()).build();
        ContentProviderOperation op = ContentProviderOperation.newInsert(memberTable)
                .withValueBackReference(TablasContract.Member.MEMBER_GROUP_ID, 0)
                .withValue(TablasContract.Member.MEMBER_USER_ID, member.getUserId())
                .withValue(TablasContract.Member.MEMBER_IS_ADMIN, member.isAdmin() ? 1 : 0)
                .withValue(TablasContract.Member.MEMBER_LEFT_GROUP, member.hasLeftGroup()? 1 : 0)
                .build();
        return op;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_group_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        switch(menu.getItemId()){
            case R.id.menu_item_save_group:
                createGroupAndMembers();
                getActivity().setResult(Activity.RESULT_OK, new Intent());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(menu);

        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText == null || newText.equals(""))
            return false;

        CursorAdapter adapter = mSearchView.getSuggestionsAdapter();

        final Cursor oldCursor = adapter.getCursor();
        adapter.setFilterQueryProvider(mFilterQueryProvider);
        adapter.getFilter().filter(newText, new Filter.FilterListener() {
            public void onFilterComplete(int count) {
                if (oldCursor != null && !oldCursor.isClosed()) {
                    oldCursor.close();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        //clicking on a selection, we add this contact into the ListView
        ContactSearchAdapter adapter = (ContactSearchAdapter)mSearchView.getSuggestionsAdapter();
        HashMap<String, String> contactInfo = adapter.get(position);

        String phone = contactInfo.get(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
        if(mAddedListById.get(phone) == null) {
            mAddedListById.put(contactInfo.get(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER), contactInfo);
            mAddedList.add(contactInfo);
            mAddedAdapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public void addToCache(Object key, Bitmap bitmap) {
        if(mCache == null)
            return;

        synchronized (mCache) {
            if (mCache.get(key) == bitmap) {
                return;
            } else {
                mCache.put(key, bitmap);
            }
        }
    }

    @Override
    public Bitmap retrieveFromCache(Object key) {
        if(mCache == null)
            return null;

        return mCache.get(key);
    }

    class SelectedContactAdapter extends SimpleAdapter {
        ViewBinder binder = new ViewBinder(){
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view.getId() == R.id.btn_remove_contact){
                    //here, add the phone number of the contact to the button's tag, so that if a user presses the button,
                    //we know which contact to remove from the adapter, based on the phone number
                    view.setTag(data);
                    return true;
                }
                else if(view.getId() == R.id.img_contact){
                    BitmapLoader.ImageRetrieval imgRetrieval = new BitmapLoader.ImageRetrieval(
                            BitmapLoader.ImageRetrieval.CONTENT_URI,
                            (String)data,
                            ContactsContract.Contacts.Photo.PHOTO);

                    BitmapLoader.asyncSetBitmapInImageView(imgRetrieval, (ImageView)view, getActivity(), CreateGroupFragment.this);
                    return true;
                }
                return false;
            }
        };

        public SelectedContactAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to){
            super(context, data, resource, from, to);
            setViewBinder(binder);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View view = super.getView(position, convertView, parent);

            ImageButton imgButton = (ImageButton)view.findViewById(R.id.btn_remove_contact);
            imgButton.setOnClickListener(mRemoveContactBtnListener);

            return view;
        }

    }

}
