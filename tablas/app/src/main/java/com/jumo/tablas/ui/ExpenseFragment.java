package com.jumo.tablas.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jumo.tablas.R;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.EntityCursor;
import com.jumo.tablas.ui.adapters.ExpenseCursorAdapter;
import com.jumo.tablas.ui.util.CacheManager;
import com.jumo.tablas.ui.util.OnKeyEventListener;
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
public class ExpenseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnKeyEventListener, CacheManager<Object, Bitmap> {

    private static final String TAG = "ExpenseFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXTRA_GROUP_ID = "com.jumo.wepay.group_id";
    public static final String EXTRA_USER_ID = "com.jumo.wepay.user_id";

    //Loaders
    public static final int LOADER_EXPENSES = 0;
    public static final int LOADER_MEMBERS = 1;

    //private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private RecyclerView mRecyclerView;
    private LinearLayoutResize mConversationLayout;
    private ImageButton mCurrencyButton;
    private EditText mConversationEditText;
    private EditText mAmountEditText;

    private FrameLayout mCustomKeyboardSpacer;
    private View mPopupView;
    private int mMaxConversationHeight; // height for whenever there is no system keyboard

    //Other control variables
    private float mCustomKeyboardHeight;
    private boolean mShowCustomKeyboard = false;

    //Fragment's attributes
    private String mUserName;
    private long mGroupId;
    private LruCache<Object, Bitmap> mCache;
    private ExpenseUserThreadHandler mPayerLoader;


    public static ExpenseFragment newInstance(String userId, long groupId) {
        ExpenseFragment fragment = new ExpenseFragment();
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
    public ExpenseFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mUserName = getArguments().getString(EXTRA_USER_ID);
        mGroupId = getArguments().getLong(EXTRA_GROUP_ID);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;  // Use 1/8th of the available memory for this memory cache.
        mCache = new LruCache<Object, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Object key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes
            }
        };

        mPayerLoader = new ExpenseUserThreadHandler(getActivity(), mCache, new Handler());
        mPayerLoader.start();
        mPayerLoader.getLooper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        // Set the adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_messages);
		mRecyclerView.setAdapter(new ExpenseCursorAdapter(getActivity(), null, this, mPayerLoader));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set the references to the components in the fragment layout
        mConversationLayout = (LinearLayoutResize) view.findViewById(R.id.view_conversations);
        mCustomKeyboardSpacer = (FrameLayout) view.findViewById(R.id.inputMethod);
        mCurrencyButton = (ImageButton) view.findViewById(R.id.button_currency);
        mConversationEditText = (EditText) view.findViewById(R.id.edit_message);
        mAmountEditText = (EditText) view.findViewById(R.id.edit_amount);

        prepareCustomKeyboard();
        return view;
    }

    public void prepareCustomKeyboard(){
        //first we update the height to be the dimension in the resource
        mCustomKeyboardHeight = getResources().getDimension(R.dimen.keyboard_height);

        View.OnClickListener mCustKeyboardToggleListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCustomKeyboardShowing()) {
                    dismissCustomKeyboard();
                } else {
                    callCustomKeyboard();
                }
            }
        };

        View.OnClickListener clickDismissCustomKeyboard = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissCustomKeyboard();
            }
        };
        View.OnFocusChangeListener focusDismissKeyboard = new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                dismissCustomKeyboard();
            }
        };

        LinearLayoutResize.OnSizeChange onSizeChange = new LinearLayoutResize.OnSizeChange() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                if(oldh == 0){
                    mMaxConversationHeight = h;
                }
                if(hasCustomKeyboardBeenCalled()){
                    showCustomKeyboard();
                }
            }
        };

        mCurrencyButton.setOnClickListener(mCustKeyboardToggleListener);
        mConversationEditText.setOnClickListener(clickDismissCustomKeyboard);
        mConversationEditText.setOnFocusChangeListener(focusDismissKeyboard);
        mAmountEditText.setOnClickListener(clickDismissCustomKeyboard);
        mAmountEditText.setOnFocusChangeListener(focusDismissKeyboard);
        mConversationLayout.setOnSizeChange(onSizeChange);
        //mCustomKeyboard.setOnDismissListener(dismissListener);
    }

    private void hideSystemKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mConversationLayout.getWindowToken(), 0);
    }

    private void callCustomKeyboard(){
        if (!isSystemKeyboardShowing()) {
            showCustomKeyboard();
        } else {
            mShowCustomKeyboard = true;
            hideSystemKeyboard(); //There is a listener on this layout resize; we will show the
        }
    }

    private boolean hasCustomKeyboardBeenCalled(){
        return mShowCustomKeyboard;
    }

    private void showCustomKeyboard(){
        mCustomKeyboardSpacer.setVisibility(View.VISIBLE);
        mCustomKeyboardSpacer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mCustomKeyboardHeight));

        //Submit message on the UI thread to redraw the view tree because of these changes.
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mCustomKeyboardSpacer.requestLayout();
            }
        });
    }

    /**
     * This method determines if the system soft keyboard is showing or not depending on the top layout's size;
     * this method is accurate only if the activity is resized when the soft keyboard appears (when the app pans
     * or does nothing, this method does not return the correc
     * @return a boolean; true if system soft keyboard is showing; false if not.
     */
    private boolean isSystemKeyboardShowing(){
        Rect rect = new Rect();
        mConversationLayout.getDrawingRect(rect);
        if(rect.height() < mMaxConversationHeight){
            return true;
        }
        return false;
    }


    private boolean isCustomKeyboardShowing(){
        return (mCustomKeyboardSpacer.getVisibility() == View.VISIBLE);
    }

    private void dismissCustomKeyboard(){
        mShowCustomKeyboard = false;
        /*if(isCustomKeyboardShowing()) {
            mCustomKeyboard.dismiss();
        }*/
        mCustomKeyboardSpacer.setVisibility(View.GONE);
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


    ///////////// Methods for OnKeyEventListener interface to handle this fragment's key listener /////////////

    @Override
    public boolean onKeyPress(int keyEvent, KeyEvent event){
        if(keyEvent == KeyEvent.KEYCODE_BACK){
            if(isCustomKeyboardShowing()){
                dismissCustomKeyboard();
                return true;
            }
        }
        return false;
    }

    ///////////// Methods for LoaderCallbacks interface to handle this fragment's loaders /////////////

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");

        if(id == LOADER_EXPENSES){
            Uri uri = TablasContract.BASE_URI.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName())
                    .appendPath("user").appendPath(mUserName).appendPath("group").appendPath(Long.toString(mGroupId))
                    .build();

            StringBuilder sortBy = new StringBuilder(TablasContract.Expense.EXPENSE_CREATED_ON).append(" ASC");
            return new CursorLoader(getActivity(), uri, null, null, null, sortBy.toString());
        }else if(id == LOADER_MEMBERS){
            return null;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
        int id = loader.getId();

        if(id == LOADER_EXPENSES) {
            if (this.getActivity() == null || mRecyclerView == null)
                return;
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


    ///////////// Methods for CacheManager interface to handle this fragment's cache /////////////

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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *
    public interface OnFragmentInteractionListener {
        //public void onFragmentInteraction(String id);
        public void onBackButtonPress();
    }*/

}
