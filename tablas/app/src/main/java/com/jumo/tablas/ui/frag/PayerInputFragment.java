package com.jumo.tablas.ui.frag;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.loaders.ExpenseLoader;
import com.jumo.tablas.ui.util.BitmapCache;
import com.jumo.tablas.ui.util.BitmapLoader;
import com.jumo.tablas.ui.util.ExpenseCalculator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Moha on 1/9/16.
 */
public class PayerInputFragment extends Fragment implements AdapterView.OnItemSelectedListener, ExpenseLoader.OnExpenseCalculatorLoaded {
    private static final String TAG = "PayerInputFragment";
    public static final String EXTRA_EXPENSE_ID = "com.jumo.tablas.expense_id";
    public static final String EXTRA_PAYER_MODE = "com.jumo.tablas.payer_mode";


    private long mExpenseId;
    private int mPayerMode; //determines whether this is for
    private ExpenseCalculator mExpenseCalculator;
    private MembersSimpleAdapter mMemberAdapter;
    private ArrayList<HashMap<String, String>> mAddedList;
    private LinkedHashMap<Long, HashMap<String, String>> mAddedListById;
    private Spinner mMemberSpinner;
    private ImageButton mAddPayerButton;


    public static PayerInputFragment newInstance(long expenseId, int payerMode){
        PayerInputFragment fragment = new PayerInputFragment();
        Bundle args = new Bundle();
        if(expenseId > 0) {
            args.putLong(EXTRA_EXPENSE_ID, expenseId);
        }
        args.putInt(EXTRA_PAYER_MODE, payerMode);
        fragment.setArguments(args);
        return fragment;
    }



    public PayerInputFragment(){
        mAddedList = new ArrayList<HashMap<String, String>>();
        mAddedListById = new LinkedHashMap<Long, HashMap<String, String>>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        //setRetainInstance(true);
        mExpenseId = getArguments().getLong(EXTRA_EXPENSE_ID, -1);
        mPayerMode = getArguments().getInt(EXTRA_PAYER_MODE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_payer, container, false);

        mMemberSpinner = (Spinner)view.findViewById(R.id.spinner_members);
        String[] fromCols = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
        };
        int[] toViewIds = new int[]{
                R.id.list_contact_name,
                R.id.list_contact_image
        };
        mMemberAdapter = new MembersSimpleAdapter(getActivity(), mAddedList, R.layout.list_item_contact_simple, fromCols, toViewIds);
        mMemberSpinner.setAdapter(mMemberAdapter);

        mAddPayerButton = (ImageButton) view.findViewById(R.id.button_add_payer);
        mAddPayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button pressed");
            }
        });

        return view;
    }

    private void updateMemberAdapterList(){
        if(mExpenseCalculator == null){
            return;
        }

        Collection<ExpenseCalculator.Person> people = mExpenseCalculator.getPeople();
        for(ExpenseCalculator.Person p : people){
            long memberId = p.member.getId();
            HashMap<String, String> row = mAddedListById.get(memberId);
            if(mExpenseCalculator.getAmountForMember(memberId, mPayerMode) == 0){
                if(row == null) {
                    row = new HashMap<String, String>();
                    row.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, p.displayName);
                    row.put(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, p.photoUri);
                    mAddedList.add(row);
                    mAddedListById.put(memberId, row);
                }
            }else{
                mAddedList.remove(row);
                mAddedListById.remove(memberId);
            }
        }
        if(mMemberAdapter != null){
            mMemberAdapter.notifyDataSetChanged();
        }
    }

    public ExpenseCalculator getExpenseCalculator() {
        return mExpenseCalculator;
    }

    public void setExpenseCalculator(ExpenseCalculator expenseCalculator) {
        mExpenseCalculator = expenseCalculator;
        updateMemberAdapterList();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {    }

    @Override
    public void onExpenseCalculatorLoader(ExpenseCalculator result) {
        setExpenseCalculator(result);
    }

    private class MembersSimpleAdapter extends SimpleAdapter implements SpinnerAdapter{

        ViewBinder binder = new ViewBinder(){
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view.getId() == R.id.list_contact_image){
                    BitmapLoader.ImageRetrieval imgRetrieval = new BitmapLoader.ImageRetrieval(
                            BitmapLoader.ImageRetrieval.CONTENT_URI,
                            (String)data,
                            ContactsContract.Contacts.Photo.PHOTO);

                    BitmapLoader.asyncSetBitmapInImageView(imgRetrieval, (ImageView) view, getActivity(), BitmapCache.getInstance());
                    return true;
                }
                return false;
            }
        };

        public MembersSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to){
            super(context, data, resource, from, to);
            setViewBinder(binder);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View view = super.getView(position, convertView, parent);
            return view;
        }



    }
}
