package com.jumo.tablas.ui.frag;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.loaders.ExpenseLoader;
import com.jumo.tablas.ui.util.BitmapCache;
import com.jumo.tablas.ui.util.BitmapLoader;
import com.jumo.tablas.ui.util.ExpenseCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Moha on 1/9/16.
 */
public class PayerInputFragment extends Fragment implements ExpenseLoader.OnExpenseCalculatorLoaded{
    private static final String TAG = "PayerInputFragment";
    public static final String EXTRA_EXPENSE_ID = "com.jumo.tablas.expense_id";
    public static final String EXTRA_PAYER_MODE = "com.jumo.tablas.payer_mode";


    private long mExpenseId;
    private int mPayerMode; //determines whether this is for
    private ExpenseCalculator mExpenseCalculator;

    //private Spinner mMemberSpinner;
    //private ImageButton mAddPayerButton;
    private RecyclerView mAddedPayersView;

    private ArrayList<HashMap<String, String>> mNonPayerMembers;
    private ArrayList<Long> mNonPayerMemberIds;
    private ArrayList<Long> mAddedPayers;
    private HashSet<Long> mPayersBeingEdited;
    //private MemberSimpleAdapter mMemberAdapter;
    private PayersRecycleAdapter mPayerAdapter;
    MemberSimpleAdapter mMemberAdapter;

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
        mNonPayerMembers = new ArrayList<HashMap<String, String>>();
        mNonPayerMemberIds = new ArrayList<Long>();
        mAddedPayers = new ArrayList<Long>();
        mPayersBeingEdited = new HashSet<Long>();
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

        mAddedPayersView = (RecyclerView) view.findViewById(R.id.list_payer);
        mPayerAdapter = new PayersRecycleAdapter();
        mAddedPayersView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAddedPayersView.setAdapter(mPayerAdapter);


        //Adapter for the people not added as payers.
        String[] fromCols = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
        };
        int[] toViewIds = new int[]{
                R.id.list_contact_name,
                R.id.list_contact_image
        };
        mMemberAdapter = new MemberSimpleAdapter(getActivity(), mNonPayerMembers, R.layout.list_item_contact_simple, fromCols, toViewIds);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateAdapterLists();
    }

    public void updatePayerAmounts(){
        updateAdapterLists();
    }

    private void updateAdapterLists(){
        if(mExpenseCalculator == null){
            return;
        }
        Collection<ExpenseCalculator.Person> people = mExpenseCalculator.getPeople();
        for(ExpenseCalculator.Person p : people){
            long memberId = p.member.getId();
            if(!mExpenseCalculator.isPayer(memberId, mPayerMode)){ //Member pays/has paid nothing; it should not be added as a payer.
                if(!mNonPayerMemberIds.contains(memberId)) {    //If already as a non payer, no need to re-add member again.
                    addToAvailableNonpayers(memberId, p);
                }
                int index = mAddedPayers.indexOf(memberId);
                if(index >= 0){
                    removePayerAtPosition(index);
                }
            }else{ //Here, we want to add as a payer
                //Remove the member from available non-payers
                int index = mNonPayerMemberIds.indexOf(memberId);
                if(index >= 0) {
                    removeFromAvailableNonpayers(index);
                }
                //Add member to payers, if not present already
                index = mAddedPayers.indexOf(memberId);
                if(index < 0){
                    addPayer(memberId);
                }else{
                    refreshPayerAtPosition(index, memberId);
                }
            }
        }
    }

    private void addPayer(long memberId){
        mAddedPayers.add(memberId);
        mAddedPayersView.getAdapter().notifyItemInserted(mAddedPayers.size()-1); //inserted last position.
    }

    private void removePayerAtPosition(int index){
        mAddedPayers.remove(index); //Remove from the payers collection, so member does not show as a payer.
        mAddedPayersView.getAdapter().notifyItemRemoved(index);
    }

    private void refreshPayerAtPosition(int index, long memberId){
        //Only update the item if its payer amount is NOT being edited.
        if(!mPayersBeingEdited.contains(memberId)) {
            mAddedPayersView.getAdapter().notifyItemChanged(index);
        }
    }


    private void addToAvailableNonpayers(long memberId, ExpenseCalculator.Person p){
        HashMap<String, String> row = new HashMap<String, String>();
        row.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, p.displayName);
        row.put(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, p.photoUri);
        mNonPayerMembers.add(row);
        mNonPayerMemberIds.add(memberId);
    }

    private void removeFromAvailableNonpayers(int index){
        mNonPayerMembers.remove(index);
        mNonPayerMemberIds.remove(index);
    }

    public ExpenseCalculator getExpenseCalculator() {
        return mExpenseCalculator;
    }

    public void setExpenseCalculator(ExpenseCalculator expenseCalculator) {
        Log.d(TAG, "ExpenseCalculator updated/set!");
        mExpenseCalculator = expenseCalculator;
        updateAdapterLists();
    }


    @Override
    public void onExpenseCalculatorLoader(ExpenseCalculator result) {
        setExpenseCalculator(result);
    }

    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Adapter classes: Non Payers //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    private class MemberSimpleAdapter extends SimpleAdapter implements SpinnerAdapter{

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

        public MemberSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to){
            super(context, data, resource, from, to);
            setViewBinder(binder);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View view = super.getView(position, convertView, parent);
            return view;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Adapter classes:  Payers /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    public class PayersRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_PAYER = 0;
        private static final int TYPE_MEMBERS = 1;

        public PayersRecycleAdapter(){
            super();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            RecyclerView.ViewHolder viewHolder = null;

            if(viewType == TYPE_PAYER) {
                View view = inflater.inflate(R.layout.list_item_payer, parent, false);
                viewHolder = new PayerViewHolder(view);

            }else if(viewType == TYPE_MEMBERS){
                View view = inflater.inflate(R.layout.list_item_add_payer, parent, false);
                viewHolder = new MemberViewHolder(view);
            }
            return viewHolder;
        }

        @Override
        public int getItemViewType (int position){
            if(position < mAddedPayers.size()){
                return TYPE_PAYER;
            }else{
                return TYPE_MEMBERS;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof PayerViewHolder) {
                bindPayerHolder(holder, position);
            }else if(holder instanceof MemberViewHolder){
                bindMembersHolder(holder, position);
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder){
            if(holder instanceof PayerViewHolder){
                PayerViewHolder payerHolder = (PayerViewHolder)holder;
                payerHolder.removePayerListener.setMemberId(0);
                payerHolder.payerAmountWatcher.setMemberId(0);
                payerHolder.payerAmount.setTag(new Long(0));
            }
        }


        @Override
        public int getItemCount() {
            return mAddedPayers.size() + 1;
        }


        private void bindPayerHolder(RecyclerView.ViewHolder holder, int position){
            PayerViewHolder payerHolder = (PayerViewHolder)holder;
            long memberId = mAddedPayers.get(position);
            ExpenseCalculator.Person person = mExpenseCalculator.getPerson(memberId);

            payerHolder.contactName.setText(person.displayName);
            payerHolder.updatePayerAmount(mExpenseCalculator.getAmountForMember(memberId, mPayerMode));
            payerHolder.payerAmount.setTag(memberId);
            payerHolder.payerAmountWatcher.setMemberId(memberId);
            payerHolder.removePayerListener.setMemberId(memberId);
            //payerHolder.payerAmount.setTag(memberId);

            BitmapLoader.ImageRetrieval imgRetrieval = new BitmapLoader.ImageRetrieval(
                    BitmapLoader.ImageRetrieval.CONTENT_URI,
                    person.photoUri,
                    ContactsContract.CommonDataKinds.Photo.PHOTO
            );
            BitmapLoader.asyncSetBitmapInImageView(imgRetrieval, payerHolder.image, getActivity(), BitmapCache.getInstance());
        }

        private void bindMembersHolder(RecyclerView.ViewHolder holder, int position){
            MemberViewHolder membersHolder = (MemberViewHolder)holder;
        }


        public class PayerViewHolder extends RecyclerView.ViewHolder{
            protected ImageView image;
            protected ImageButton removeButton;
            protected TextView contactName;
            protected EditText payerAmount;
            protected AmountListener payerAmountWatcher;
            protected RemovePayerListener removePayerListener;

            public PayerViewHolder(View itemView){
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.img_contact);
                contactName = (TextView) itemView.findViewById(R.id.text_name);
                payerAmount = (EditText) itemView.findViewById(R.id.edit_amount);
                payerAmountWatcher = new AmountListener();
                payerAmount.addTextChangedListener(payerAmountWatcher);
                payerAmount.setOnFocusChangeListener(new PayerAmountFocused());
                removeButton = (ImageButton) itemView.findViewById(R.id.btn_remove_contact);
                removePayerListener = new RemovePayerListener();
                removeButton.setOnClickListener(removePayerListener);
            }

            public void updatePayerAmount(double amount){
                payerAmountWatcher.setIgnoreUpdates(true);
                payerAmount.setText(String.valueOf(amount));
                payerAmountWatcher.setIgnoreUpdates(false);
            }
        }

        public class MemberViewHolder extends RecyclerView.ViewHolder{
            //protected Spinner memberList;
            ListPopupWindow mAddPayerPopup;

            public MemberViewHolder(View itemView){
                super(itemView);
                mAddPayerPopup = new ListPopupWindow(getActivity());
                //memberList = (Spinner)itemView.findViewById(R.id.spinner_members);
                mAddPayerPopup.setAnchorView(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAddPayerPopup.show();
                    }
                });
                mAddPayerPopup.setAdapter(mMemberAdapter);
                mAddPayerPopup.setOnItemClickListener(new AddMemberListenerClick(mAddPayerPopup));
            }
        }
    }
    private class AddMemberListenerClick implements AdapterView.OnItemClickListener{
        private ListPopupWindow mPopupWindow;

        public AddMemberListenerClick(ListPopupWindow popup){
            mPopupWindow = popup;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Member added as payer selected from Dropdown!!");
            long memberId = mNonPayerMemberIds.get(position);
            mExpenseCalculator.addPayer(memberId, mPayerMode);
            //int payersSize = mAddedPayers.size();
            updateAdapterLists();
            /*if(payersSize < mAddedPayers.size()) {
                mPayerAdapter.notifyItemInserted(mAddedPayers.size() - 1);
            }*/
            mPopupWindow.dismiss();
            ((MemberSimpleAdapter)parent.getAdapter()).notifyDataSetChanged();
        }
    }

    protected class PayerAmountFocused implements View.OnFocusChangeListener{

        /**
         * Will keep track of which payers are being edited at any particular moment (generally, only one)
         * @param v
         * @param hasFocus
         */
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            long memberId = (v.getTag() == null)? 0 : ((Long)v.getTag()).longValue();

            Log.d(TAG, "OnFocusChange: " + v.toString() + ", focus = " + hasFocus);

            if(hasFocus) {
                mPayersBeingEdited.add(memberId);
            }else{
                mPayersBeingEdited.remove(memberId);
                int index = mAddedPayers.indexOf(memberId);
                mAddedPayersView.getAdapter().notifyItemChanged(index);
            }
        }
    }

    protected class RemovePayerListener implements View.OnClickListener{
        private long mMemberId;

        @Override
        public void onClick(View v) {
            //Remove an added member
            Log.d(TAG, "Removing payer!");
            long memberId = mMemberId;
            int indexOfPayer = mAddedPayers.indexOf(memberId);
            mExpenseCalculator.removePayer(memberId, mPayerMode);
            updateAdapterLists();
            //mPayerAdapter.notifyItemRemoved(indexOfPayer);
        }

        public long getMemberId() {
            return mMemberId;
        }

        public void setMemberId(long memberId) {
            this.mMemberId = memberId;
        }
    }


    protected class AmountListener implements TextWatcher{

        private long mMemberId = 0;
        private boolean mIgnoreUpdates = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {  }

        @Override
        public void afterTextChanged(Editable s) {
            Long memberId = mMemberId;
            if(s == null || memberId == null || memberId.longValue() <= 0 || mIgnoreUpdates){
                return;
            }
            double amount = 0;
            try{
                Log.d(TAG, "Amount for payer changed; updating payer amounts!");
                amount = Double.valueOf(s.toString());
                mExpenseCalculator.addPayer(memberId, amount, mPayerMode);
                updateAdapterLists();

            }catch(NumberFormatException e){
                Log.d(TAG, "Double Value could not be parsed", e);
            }
        }

        public long getMemberId() {
            return mMemberId;
        }

        public void setMemberId(long memberId) {
            this.mMemberId = memberId;
        }

        public void setIgnoreUpdates(boolean ignore){
            mIgnoreUpdates = ignore;
        }
    }


}
