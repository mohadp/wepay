package com.jumo.tablas.ui.frag;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.support.v4.view.ViewPager;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.util.OnKeyEventListener;
import com.jumo.tablas.ui.views.LinearLayoutResize;

/**
 * Created by Moha on 12/30/15.
 */
public class ExpenseEditFragment extends Fragment implements OnKeyEventListener, LinearLayoutResize.OnSizeChange, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ExpenseEditFragment";

    //Extras for initializing this fragment (this one is optional)
    public static final String EXTRA_EXPENSE_ID = "com.jumo.tablas.expense_id";

    //Loaders for this fragment
    private static final int LOADER_MEMBERS = 1;

    //Tabs within Tabhost for custom keyboard
    public static final String TAB_CURR = "currency";
    public static final String TAB_PAID = "paid";
    public static final String TAB_PAYERS = "payers";

    //Views within the layout to refer to
    private LinearLayoutResize mExpenseEntryLayout;
    private ImageButton mCurrencyButton;
    private EditText mConversationEditText;
    private EditText mAmountEditText;
    private ViewPager mCustomKeyboardSpacer;
    //Other control variables
    private float mCustomKeyboardHeight;
    private boolean mShowCustomKeyboard = false;
    private long mExpenseId;
    private Callback mCallback;

    public static ExpenseEditFragment newInstance(long expenseId){
        ExpenseEditFragment fragment = new ExpenseEditFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_EXPENSE_ID, expenseId);
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseEditFragment(){  }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //Add loaders here
        //getLoaderManager().restartLoader(LOADER_EXPENSES, null, this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Long expenseId = getArguments().getLong(EXTRA_EXPENSE_ID);
        mExpenseId = (expenseId == null)? -1 : expenseId.longValue();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_keyboard, container, false);

        //Set the references to the components in the fragment layout
        mExpenseEntryLayout = (LinearLayoutResize) view.findViewById(R.id.expense_entry);
        mCustomKeyboardSpacer = (ViewPager) view.findViewById(R.id.input_method);
        mCurrencyButton = (ImageButton) view.findViewById(R.id.button_currency);
        mConversationEditText = (EditText) view.findViewById(R.id.edit_message);
        mAmountEditText = (EditText) view.findViewById(R.id.edit_amount);


        /*Resources res = getActivity().getResources();
        TabHost mTabHost = (TabHost)view.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec(TAB_PAYERS).setIndicator(res.getString(R.string.text_payer_tab)).setContent(R.id.tab_payers));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_PAID).setIndicator(res.getString(R.string.text_paid_tab)).setContent(R.id.tab_paid));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_CURR).setIndicator(res.getString(R.string.text_curr_tab)).setContent(R.id.tab_currencies));*/

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
                if(hasFocus) {
                    dismissCustomKeyboard();
                }
            }
        };

        mCurrencyButton.setOnClickListener(mCustKeyboardToggleListener);
        mConversationEditText.setOnClickListener(clickDismissCustomKeyboard);
        mConversationEditText.setOnFocusChangeListener(focusDismissKeyboard);
        mAmountEditText.setOnClickListener(clickDismissCustomKeyboard);
        mAmountEditText.setOnFocusChangeListener(focusDismissKeyboard);
        //mExpenseEntryLayout.setOnSizeChange(onSizeChange);
        //mCustomKeyboard.setOnDismissListener(dismissListener);
    }


    private void hideSystemKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mExpenseEntryLayout.getWindowToken(), 0);
    }

    private void callCustomKeyboard(){
        if (!isSystemKeyboardShowing()) {
            showCustomKeyboard();
        } else {
            mShowCustomKeyboard = true;
            hideSystemKeyboard(); //There is a listener on the LinearLayoutResize (the root layout); if the size of the layout is changed, and the custom keyboard is requested, we will show the keyboard.
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


    private void dismissCustomKeyboard(){
        mShowCustomKeyboard = false;
        /*if(isCustomKeyboardShowing()) {
            mCustomKeyboard.dismiss();
        }*/
        mCustomKeyboardSpacer.setVisibility(View.GONE);
    }


    /**
     * This method determines if the system soft keyboard is showing or not depending on the top layout's size;
     * this method is accurate only if the activity is resized when the soft keyboard appears (when the app pans
     * or does nothing, this method does not return the correc
     * @return a boolean; true if system soft keyboard is showing; false if not.
     */
    private boolean isSystemKeyboardShowing(){
        return (mCallback == null)? false : mCallback.isSystemKeyboardShowing();
    }

    private boolean isCustomKeyboardShowing(){
        return (mCustomKeyboardSpacer.getVisibility() == View.VISIBLE);
    }


    public void setCallback(Callback fragmentInterface) {
        this.mCallback = fragmentInterface;
    }

    ///////////// Methods for LoaderCallbacks interface to handle this fragment's loaders /////////////

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");

        if(id == LOADER_MEMBERS){
            return null;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
        int id = loader.getId();

        if(id == LOADER_MEMBERS) {
            if (this.getActivity() == null)
                return;
            // Update the data in the corresponsing adapters for payers and paying people.
            // ((ExpenseCursorAdapter) mRecyclerView.getAdapter()).changeCursor(new EntityCursor(data));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();

        if(id == LOADER_MEMBERS) {
            //((ExpenseCursorAdapter) mRecyclerView.getAdapter()).changeCursor(null);
        }
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

    ///////////// Methods for LinearLayoutResize.OnSizeChange interface to handle this fragment's key listener /////////////

    /**
     * Show custom keyboard only after the system keyboard has been hidden (once the size of the layout changes)
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(hasCustomKeyboardBeenCalled()){
            showCustomKeyboard();
        }
    }


    public interface Callback {
        public boolean isSystemKeyboardShowing();
    }

}
