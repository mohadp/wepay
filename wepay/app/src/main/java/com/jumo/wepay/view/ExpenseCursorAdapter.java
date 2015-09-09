package com.jumo.wepay.view;

import android.widget.CursorAdapter;

import com.jumo.wepay.provider.WepayContract;
import android.content.Context;
import android.view.*;
import com.jumo.wepay.model.*;
import android.widget.*;

import com.jumo.wepay.R;
import com.jumo.wepay.provider.dao.*;

class ExpenseCursorAdapter extends CursorAdapter{
	//private ExpenseCursor mExpenseCursor;
	
	//To get balances on a per-user basis for a particular group
	private String mUserName;
	private long groupId;

    public ExpenseCursorAdapter(Context context, EntityCursor cursor) {
        super(context, cursor, 0);
        //mExpenseCursor = cursor;
	}

    //Create view for each item
    @Override
    public View newView(Context context, android.database.Cursor cursor, ViewGroup parent) {
        // use a layout inflater to get a row view
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.list_item_message, parent, false);

		return view;
    }

    //fill the view with data from the cursor.
    @Override
    public void bindView(View view, Context context, android.database.Cursor cursor) {
        EntityCursor expenseCursor = (EntityCursor)getCursor();

        if(expenseCursor == null) return;

        // get the run for the current row
        Expense expense = new Expense(expenseCursor.getEntity(WepayContract.Expense.getInstance()));

        // set up the start date text view
        ((ImageView)view.findViewById(R.id.list_message_image)).setImageResource(R.drawable.ic_launcher); //TODO: will load the image of the person who createrd this expense, prob. through another background process
		((ImageView)view.findViewById(R.id.list_message_category)).setImageResource(R.drawable.ic_launcher);  //TODO: will load image of category once I have the category images
		
		int rand = (int) Math.round(Math.random()*100) % 5 + 1;
		int[] resources = new int[rand];
		
		for(int i = 0; i < rand; i++){
			resources[i] = R.drawable.ic_launcher;
		}

        RoundImageViewRow imgPayers = (RoundImageViewRow)view.findViewById(R.id.list_message_payers_images);
        imgPayers.removeAllRoundImageViews();
		imgPayers.addRoundImageViews(resources); //TODO: will set this in a separate thread (probably a Handler/Looper/Message, to query each expense for payers and set images.
		
		((TextView)view.findViewById(R.id.list_message_desc)).setText(expense.getMessage());
		((TextView)view.findViewById(R.id.list_message_balance)).setText(String.format("%1$.2f",expense.getUserBalance()));
		((TextView)view.findViewById(R.id.list_message_total)).setText(String.format("%1$.2f",expense.getAmount()));
		((TextView)view.findViewById(R.id.list_message_date)).setText(expense.getCreatedOn().toLocaleString());
		((TextView)view.findViewById(R.id.list_message_location)).setText("Washington, DC, USA");


    }
	
}
