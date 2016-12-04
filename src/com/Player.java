/**
 * 
 */
package com;

/**
 * @author as
 *
 */

//change for test
public class Player {
	private String name;
	private double score;
	private double position;
	
	package com.mis4800.group0.checkbook;

	import java.util.ArrayList;
	import java.util.Date;
	import java.util.List;

	import com.mis4800.group0.checkbook.db.CheckbookDatabase;
	import com.mis4800.group0.checkbook.model.CheckBook;
	import com.mis4800.group0.checkbook.model.Transaction;

	import android.app.Activity;
	import android.os.Bundle;
	import android.view.Menu;
	import android.view.MenuItem;
	import android.view.View;
	import android.view.View.OnClickListener;
	import android.widget.AdapterView;
	import android.widget.AdapterView.OnItemClickListener;
	import android.widget.ArrayAdapter;
	import android.widget.Button;
	import android.widget.EditText;
	import android.widget.ListView;
	import android.widget.TextView;

	public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {

		Button btnAdd, btnDelete;
		TextView tvBalance;
		ListView lstTransactions;
		EditText txtAmount, txtDate, txtPayee, txtMemo;
		
		// The Adapter is an adapter for Transactions, not Double
		CheckbookRowAdapter adapter;
		// Create an array of transactions
		// With Objects, we don't need an array or arraylist, but just a checkbook.
		CheckBook mycheckbook;
		CheckbookDatabase mydatabase;
			
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main); // connect controller to the view
			// Connect the controller to the Model by initializing a Checkbook
			mycheckbook = new CheckBook("WPCU", "Jit");
			
			// Create the database
			mydatabase = new CheckbookDatabase(this);
			
			// We need to hook up with the rest of the EditText controls here
			tvBalance = (TextView) findViewById(R.id.tvBalance);
			txtAmount = (EditText) findViewById(R.id.txtAmount);
			txtDate = (EditText) findViewById(R.id.txtDate);
			txtPayee = (EditText) findViewById(R.id.txtTo);
			txtMemo = (EditText) findViewById(R.id.txtDescription);
			
			btnAdd = (Button) findViewById(R.id.btnAdd);
			btnDelete = (Button) findViewById(R.id.btnDelete);
			btnAdd.setOnClickListener(this);
			btnDelete.setOnClickListener(this);
			
			lstTransactions = (ListView) findViewById(R.id.lstTransactions);
			
			// The adapter needs to be updated to be an adapter of transactions
			adapter = new CheckbookRowAdapter(this, 
					R.layout.checkbookrow, 
					mycheckbook.getTransactions());    // Also we need to connect the adapter to the transactions from the checkbook
			lstTransactions.setAdapter(adapter);
			lstTransactions.setOnItemClickListener(this);
		}
		
		/* (non-Javadoc)
		 * @see android.app.Activity#onPause()
		 */
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			mydatabase.save(mycheckbook);
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onResume()
		 */
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			mydatabase.retrieve(mycheckbook);
			tvBalance.setText("Current balance is: " + mycheckbook.balance());
		}

		private int tappedPosition = -1;
			
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			int id = item.getItemId();
			if (id == R.id.action_settings) {
				return true;
			} else if (id == R.id.sortDate) {
				mycheckbook.sort(0);
				adapter.notifyDataSetChanged();
				return true;
			} else if (id == R.id.isortAmount) {
				mycheckbook.sort(1);
				adapter.notifyDataSetChanged();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.btnAdd:
				double amount = Double.parseDouble(txtAmount.getText().toString());
				
				// First we need to create an instance of the transaction object from stuff the user has put in
				// Since we only have the amount, lets put some default values for now.
				// new Date() returns today's date.
				Transaction newtransaction = new Transaction(new Date(), "blank", amount, "memo");
				// Check the text on the button - if Add, perform the Add function
				if (btnAdd.getText().equals("Add")) {
					// Add the amount to the array
					// With the objects, we don't add a double to the arraylist, 
					// but we add a transaction to the checkbook, exactly what you would expect
					mycheckbook.addTransaction(newtransaction);
				} else {  // Otherwise perform the update function
					if(tappedPosition < 0) return;
					// Update the transaction using the saved positionToUpdate
					// With objects, you call the updateTransaction method.
					mycheckbook.updateTransaction(tappedPosition, newtransaction);
					// Turn back the text of the button
					btnAdd.setText("Add");
				}
				break;
			case R.id.btnDelete:
				if(tappedPosition < 0) return;
				// Same thing with deleteTransaction 
				mycheckbook.deleteTransaction(tappedPosition);
				break;
			}
			// Tell the adapter that the dataset has changed
			adapter.notifyDataSetChanged();
			// Reset the positionToUpdate back
			tappedPosition = -1;
			// Update the balance
			 // We don't need our calculateBalance any more - just call the balance method from CheckBook
			tvBalance.setText("Current balance is: " + mycheckbook.balance()); 
		}

		@Override
		public void onItemClick(AdapterView<?> listview, View itemview, int itemposition, long itemId) {
			// Copy the data from the current position to the EditText
			// Lets get the transaction from the current position from the list of transactions in the checkbook.
			// Another option would have been to create a getTransaction(position) method in the CheckBook class
			Transaction curt = mycheckbook.getTransactions().get(itemposition);
			
			// Using getFormattedAmount would cause a problem parsing the $ symbol out, so lets stay with Doubles
			// to put in the Amount field.
			txtAmount.setText(curt.getAmount().toString());
			// Change the button so the user knows we are updating
			btnAdd.setText("Update");
			// Save the itemposition so update can work
			tappedPosition = itemposition;
		}
	}

}







