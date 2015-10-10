package com.jumo.tablas.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.jumo.tablas.R;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.EntityCursor;
import com.jumo.tablas.ui.adapters.ExpenseCursorAdapter;
import com.jumo.tablas.ui.util.OnKeyEventListener;
import com.jumo.tablas.ui.loaders.ExpenseUserThreadHandler;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@linkx OnFragmentInteractionListener}
 * interface.
 */
public class ExpenseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnKeyEventListener{

    private static final String TAG = "ExpenseFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXTRA_GROUP_ID = "com.jumo.wepay.group_id";
    public static final String EXTRA_USER_ID = "com.jumo.wepay.user_id";
    private static final int KEYBOARD_THRESHOLD = 300;
    private static final int UNSET_PREVIOUS_HEIGHT = -1283013084;

    //Loaders
    public static final int LOADER_EXPENSES = 0;

    //private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;
    private LinearLayout mConversationLayout;
    private LinearLayout mChatControlsLayout;
    private PopupWindow mCustomKeyboard;
    private ImageButton mCurrencyButton;
    private EditText mConversationEditText;
    private EditText mAmountEditText;


    private FrameLayout mCustomKeyboardSpacer;
    private View mPopupView;

    //Other control variables
    private float mCustomKeyboardHeight;
    //private float mPreviousHeight;



    //Fragment's attributes
    private String mUserName;
    private long mGroupId;
    private LruCache<String, Bitmap> mCache;
    //private LruCache<Long, ImageViewRow> mCacheImageRow;
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
        mCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes
            }
        };

        mPayerLoader = new ExpenseUserThreadHandler(getActivity(), mCache, new Handler());
        mPayerLoader.start();
        mPayerLoader.getLooper();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);


        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
		mListView.setAdapter(new ExpenseCursorAdapter(getActivity(), null, mCache, mPayerLoader));

        //Set the references to the components in the fragment layout
        mConversationLayout = (LinearLayout) view.findViewById(R.id.view_conversations);
        mChatControlsLayout = (LinearLayout) view.findViewById(R.id.layout_chat_controls);
        mCustomKeyboardSpacer = (FrameLayout) view.findViewById(R.id.inputMethod);
        mCurrencyButton = (ImageButton) view.findViewById(R.id.button_currency);
        mConversationEditText = (EditText) view.findViewById(R.id.edit_message);
        mAmountEditText = (EditText) view.findViewById(R.id.edit_amount);

        //Setting up the custom input
        mPopupView = inflater.inflate(R.layout.popup_input_methods, container, false);

        //popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mCustomKeyboard = new PopupWindow(mPopupView);

        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);
        prepareCustomKeyboard();

        return view;
    }

    public void prepareCustomKeyboard(){
        //first we update the height to be the dimension in the resource
        mCustomKeyboardHeight = getResources().getDimension(R.dimen.keyboard_height);
        //mPreviousHeight = UNSET_PREVIOUS_HEIGHT;

        mCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomKeyboard.isShowing()){
                    dismissCustomKeyboard();
                }else {
                    showCustomKeyboard();
                    mCustomKeyboard.update();
                }
            }
        });

        View.OnClickListener clickDismissKeyboard = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mCustomKeyboard.isShowing()) {
                dismissCustomKeyboard();
            }
            }
        };
        View.OnFocusChangeListener focusDismissKeyboard = new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if(mCustomKeyboard.isShowing()) {
                dismissCustomKeyboard();
            }
            }
        };

        mConversationEditText.setOnClickListener(clickDismissKeyboard);
        mConversationEditText.setOnFocusChangeListener(focusDismissKeyboard);

        mAmountEditText.setOnClickListener(clickDismissKeyboard);
        mAmountEditText.setOnFocusChangeListener(focusDismissKeyboard);
        //keepUpdatingCustomKeyboardHeight();
    }

    private void showCustomKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mConversationLayout.getWindowToken(), 0);

        mCustomKeyboardSpacer.setVisibility(View.VISIBLE);
        mCustomKeyboardSpacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) mCustomKeyboardHeight));
        int spacerHeight = mCustomKeyboardSpacer.getHeight();
        int spacerKeyboardHeight = (spacerHeight == 0)? mCustomKeyboardSpacer.getLayoutParams().height : spacerHeight;

        mCustomKeyboard.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mCustomKeyboard.setHeight(spacerKeyboardHeight);
        mCustomKeyboard.setAnimationStyle(0);
        mCustomKeyboard.showAsDropDown(mChatControlsLayout, 0, 0);
    }

    private void dismissCustomKeyboard(){
        mCustomKeyboard.dismiss();
        mCustomKeyboardSpacer.setVisibility(View.GONE);
    }

    /*private void keepUpdatingCustomKeyboardHeight(){
        ViewTreeObserver.OnGlobalLayoutListener softKeyboardAndOrientation = new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                Rect visibleFrame = getVisibleDisplayFrame(); //Get the current conversation layout's height.
                int globalHeight = mConversationLayout.getRootView().getHeight(); //Get the current total Height

                if (mPreviousHeight == UNSET_PREVIOUS_HEIGHT) { //only set mPreviousHeight to current height; we have not shown anything.
                    mPreviousHeight = visibleFrame.height();
                } else {
                    int tentativeKeyboardHeight = (int) mPreviousHeight - visibleFrame.height();
                    //if previousHeight is greater than current one,
                    // that means keyboard is showing, and the difference is the keyboard height
                    if (tentativeKeyboardHeight > KEYBOARD_THRESHOLD) {
                        mCustomKeyboardHeight = (tentativeKeyboardHeight > mCustomKeyboardHeight)? tentativeKeyboardHeight : mCustomKeyboardHeight;
                        Log.d(TAG, "New keyboard size: " + mCustomKeyboardHeight);
                    }

                    mPreviousHeight = visibleFrame.height();
                }
            }
        };

        mConversationLayout.getViewTreeObserver().addOnGlobalLayoutListener(softKeyboardAndOrientation);
    }*/

    /*private Rect getVisibleDisplayFrame(){
        Rect rect = new Rect();
        mConversationLayout.getWindowVisibleDisplayFrame(rect);
        return rect;
    }*/

    @Override
    public boolean onKeyPress(int keyEvent, KeyEvent event){
        if(keyEvent == KeyEvent.KEYCODE_BACK){
            if(mCustomKeyboard.isShowing()){
                dismissCustomKeyboard();
                return true;
            }
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");

        Uri uri = TablasContract.BASE_URI.buildUpon().appendPath(TablasContract.Expense.getInstance().getTableName())
                .appendPath("user").appendPath(mUserName).appendPath("group").appendPath(Long.toString(mGroupId))
                .build();

        StringBuilder sortBy = new StringBuilder(TablasContract.Expense.CREATED_ON).append(" ASC");
        return new CursorLoader(getActivity(), uri, null, null, null, sortBy.toString());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
        if(this.getActivity() == null || mListView == null) return;
        ((ExpenseCursorAdapter)mListView.getAdapter()).changeCursor(new EntityCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(mListView == null) return;
        ((ExpenseCursorAdapter)mListView.getAdapter()).changeCursor(null);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //public void onFragmentInteraction(String id);
        public void onBackButtonPress();
    }

}
