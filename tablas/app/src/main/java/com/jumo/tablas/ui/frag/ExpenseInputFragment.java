package com.jumo.tablas.ui.frag;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.jumo.tablas.ui.adapters.InputMethodPageAdapter;
import com.jumo.tablas.ui.loaders.ExpenseLoader;
import com.jumo.tablas.ui.util.ExpenseCalculator;
import com.jumo.tablas.ui.util.OnKeyEventListener;
import com.jumo.tablas.ui.views.LinearLayoutResize;

/**
 * Created by Moha on 12/30/15.
 */
public class ExpenseInputFragment extends Fragment implements OnKeyEventListener, LinearLayoutResize.OnSizeChange, ExpenseLoader.OnExpenseCalculatorLoaded{
    private static final String TAG = "ExpenseInputFragment";

    //Extras for initializing this fragment (this one is optional)
    public static final String EXTRA_EXPENSE_ID = "com.jumo.tablas.expense_id";
    public static final String EXTRA_GROUP_ID = "com.jumo.tablas.group_id";

    //Loaders for this fragment
    private static final int LOADER_MEMBERS = 1;

    //Views within the layout to refer to
    private long mExpenseId;
    private long mGroupId;

    private LinearLayoutResize mExpenseEntryLayout;
    private ImageButton mCurrencyButton;
    private EditText mConversationEditText;
    private EditText mAmountEditText;
    private ViewPager mCustomKeyboard;
    //Other control variables
    private float mCustomKeyboardHeight;
    private boolean mShowCustomKeyboard = false;
    private CustomKeyboardHelper mCustomKeyboardHelper;
    private InputMethodPageAdapter mKeyboardPageAdapter;
    private ExpenseCalculator mExpenseCalculator;

    public static ExpenseInputFragment newInstance(long expenseId, long groupId){
        ExpenseInputFragment fragment = new ExpenseInputFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_EXPENSE_ID, expenseId);
        args.putLong(EXTRA_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseInputFragment(){  }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mExpenseId = getArguments().getLong(EXTRA_EXPENSE_ID, 0);
        mGroupId = getArguments().getLong(EXTRA_GROUP_ID, 0);
        Log.d(TAG, "ExpenseInputFragment created!, Arguments" +  this.toString() +": (ExpenseID: " + mExpenseId + ", GroupId: " + mGroupId + ")");

        /*if(savedInstanceState != null){
            mExpenseCalculator =
        }*/

        //Add loaders here: the asynch task that loads all members and payers for this expense.
        ExpenseLoader expenseLoader = new ExpenseLoader(getActivity());
        expenseLoader.addSubscriberToNotify(this);
        expenseLoader.execute(new Long(0), new Long(mGroupId));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        onExpenseCalculatorLoader(mExpenseCalculator);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_keyboard, container, false);

        //Set the references to the components in the fragment layout
        mExpenseEntryLayout = (LinearLayoutResize) view.findViewById(R.id.expense_entry);
        mCurrencyButton = (ImageButton) view.findViewById(R.id.button_currency);
        mConversationEditText = (EditText) view.findViewById(R.id.edit_message);
        mAmountEditText = (EditText) view.findViewById(R.id.edit_amount);
        mAmountEditText.addTextChangedListener(new AmountListener());

        //Set the custom keyboard pages
        mCustomKeyboard = (ViewPager) view.findViewById(R.id.input_method);
        mKeyboardPageAdapter = new InputMethodPageAdapter(getChildFragmentManager(), getActivity(), mExpenseId, mGroupId);
        mCustomKeyboard.setAdapter(mKeyboardPageAdapter);

        //Enable keyboard showing and hiding
        prepareCustomKeyboard();

        return view;
    }

    @Override
    public void onDetach(){
        super.onDetach();
    }

    /*@Override
    public void onDestroy(){
        super.onDestroy();
        mCustomKeyboard.setAdapter(null);
        mKeyboardPageAdapter = null;
    }*/

    private void prepareCustomKeyboard(){
        //first we update the height to be the dimension in the resource
        mCustomKeyboardHeight = getResources().getDimension(R.dimen.keyboard_height);

        View.OnClickListener mCustKeyboardToggleListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "ExpenseInputFragment Pressed Button: mCustomKeyboardToggleListener!");
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
        mCustomKeyboard.setVisibility(View.VISIBLE);
        mCustomKeyboard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mCustomKeyboardHeight));

        //Submit message on the UI thread to redraw the view tree because of these changes.
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                mCustomKeyboard.requestLayout();
            }
        });
    }


    private void dismissCustomKeyboard(){
        mShowCustomKeyboard = false;
        /*if(isCustomKeyboardShowing()) {
            mCustomKeyboard.dismiss();
        }*/
        mCustomKeyboard.setVisibility(View.GONE);
    }


    /**
     * This method determines if the system soft keyboard is showing or not depending on the top layout's size;
     * this method is accurate only if the activity is resized when the soft keyboard appears (when the app pans
     * or does nothing, this method does not return the correc
     * @return a boolean; true if system soft keyboard is showing; false if not.
     */
    private boolean isSystemKeyboardShowing(){
        //Log.d(TAG, "isSystemKeyboardShowing:mCustomKeybaordHelper = " + mCustomKeyboardHelper);

        return (mCustomKeyboardHelper == null)? false : mCustomKeyboardHelper.isSystemKeyboardShowing();
    }

    private boolean isCustomKeyboardShowing(){
        //Log.d(TAG, "isCustomKeyboardShowing() = " + (mCustomKeyboard.getVisibility() == View.VISIBLE));
        return (mCustomKeyboard.getVisibility() == View.VISIBLE);
    }


    public void setCallback(CustomKeyboardHelper fragmentInterface) {
        this.mCustomKeyboardHelper = fragmentInterface;
    }


    ///////////// Methods for OnExpenseCalculatorLoader interface to handle this fragment's key listener /////////////
    @Override
    public void onExpenseCalculatorLoader(ExpenseCalculator result) {
        mExpenseCalculator = result;
        mKeyboardPageAdapter.setExpenseCalculator(mExpenseCalculator);
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


    public interface CustomKeyboardHelper {
        public boolean isSystemKeyboardShowing();
    }

    protected class AmountListener implements TextWatcher {
        public AmountListener(){ }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {  }

        @Override
        public void afterTextChanged(Editable s) {
            double amount = 0;
            try{
                Log.d(TAG, "Amount for payer changed; updating payer amounts!");
                amount = Double.valueOf(s.toString());
                mKeyboardPageAdapter.updateExpenseAmount(amount);
            }catch(NumberFormatException e){
                Log.d(TAG, "Double Value could not be parsed", e);
            }
        }
    }

}
