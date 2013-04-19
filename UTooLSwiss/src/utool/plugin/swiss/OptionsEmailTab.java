package utool.plugin.swiss;

import java.util.ArrayList;
import java.util.StringTokenizer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import utool.plugin.activity.AbstractPluginCommonActivity;
import utool.plugin.activity.TournamentContainer;
import utool.plugin.swiss.TournamentActivity.HelpDialog;
import utool.plugin.swiss.communication.AutomaticEmailHandler;

/**
 * Activity for handling the setting up of subscriber emails.
 * @author waltzm
 * @version 1/14/2013
 */
public class OptionsEmailTab extends AbstractPluginCommonActivity
{

	/**
	 * Log tag to be used in this class
	 */
	private static final String LOG_TAG = "Swiss Email Options";

	/**
	 * Holds the arrayAdapter
	 */
	private AdvancedOptionsAdapter ad;

	/**
	 * Holds first help text
	 */
	private static final String HELP_TEXT_1 = "This tab allows you to subscribe email address to recieve tournament updates. Emails will be sent to all selected email addresses at every round progression detailing the new matchups.";

	/**
	 * Shared preferences key for getting if the screen has been visited before
	 */
	private static final String FIRST_TIME_KEY = "utool.plugin.swiss.OptionsEmailTab";
	
	/**
	 * String used to save the email addresses permanently
	 */
	private static final String SHARED_PREF_EMAIL_ADDRESSES = "email_addresses";

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.swiss_options_email_tab);

		//setup adapter
		AutomaticEmailHandler a = ((SwissTournament)TournamentContainer.getInstance(getTournamentId())).getAutomaticEmailHandler();
		ArrayList<String> emails = a.getSubscribers();
		int size = emails.size();
		emails.addAll(a.getPossibleSubscribers());

		ListView l = (ListView)findViewById(R.id.email_subscribers);
		ad=new AdvancedOptionsAdapter(this, R.id.email_subscribers, emails);
		l.setAdapter(ad);

		//load email addresses from preferences and add to list if unique
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String em= prefs.getString(SHARED_PREF_EMAIL_ADDRESSES, ""); 
		StringTokenizer e = new StringTokenizer(em, ",");
		while(e.hasMoreTokens())
		{
			addPossibleSubscriber(emails, e.nextToken());
		}

		ArrayList<Boolean> ton = new ArrayList<Boolean>();
		for(int i=0;i<emails.size();i++)
		{
			if(i<size)
			{
				ton.add(true);
			}
			else
			{
				ton.add(false);
			}
		}
		ad.turnedOn= ton ;
		ad.notifyDataSetChanged();

		//setup add button
		ImageButton plus = (ImageButton)findViewById(R.id.email_plus);
		plus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) 
			{
				EditText ea = (EditText)findViewById(R.id.email_address);

				//add typed in email to list
				ad.add(ea.getText().toString());
				ad.notifyDataSetChanged();
				reloadUI();

			}

		});



		// use a default value to true (is first time)
		Boolean firstTime= prefs.getBoolean(FIRST_TIME_KEY, true); 
		if(firstTime)
		{
			this.setupHelpPopups();
			//setup preferences to remember help has been played
			prefs.edit().putBoolean(FIRST_TIME_KEY, false).commit();
		}


		reloadUI();

	}

	@Override
	public void onPause()
	{
		super.onPause();

		//save settings to tournament's email object and exit
		AutomaticEmailHandler a = ((SwissTournament)TournamentContainer.getInstance(getTournamentId())).getAutomaticEmailHandler();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> psubs = new ArrayList<String>();
		ArrayList<String> emails = ad.addresses;
		ArrayList<Boolean> on = ad.turnedOn;
		for(int i=0;i<emails.size();i++)
		{
			if(on.get(i))
			{
				//add to subscriber since checked
				subs.add(emails.get(i));
			}
			else
			{
				//add to possible subscriber since unchecked
				psubs.add(emails.get(i));
			}
		}

		//Log.e(logtag, subs);
		a.setSubscribers(subs);
		a.setPossibleSubscribers(psubs);

		String ems = "";
		for(int i=0;i<subs.size();i++)
		{
			ems+=subs.get(i)+",";
		}
		for(int i=0;i<psubs.size();i++)
		{
			ems+=psubs.get(i)+",";
		}

		//save list to preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit().putString(SHARED_PREF_EMAIL_ADDRESSES, ems).commit();
	}



	/**
	 * Re-registers listview for the context menu
	 */
	private void reloadUI()
	{
		ListView l = (ListView)findViewById(R.id.email_subscribers);
		l.setOnCreateContextMenuListener(this);
		registerForContextMenu(l);
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_email_options_context_menu, menu);
		Log.d(LOG_TAG,"Inflating Menu");
	}


	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.context_delete:
			ad.addresses.remove(info.position);
			ad.turnedOn.remove(info.position);
			ad.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Adds nextToken to list if not already in emails
	 * @param emails list of addresses
	 * @param nextToken email to add if unique
	 * @return true if added, false if not added
	 */
	public boolean addPossibleSubscriber(ArrayList<String> emails, String nextToken) 
	{
		for(int i=0;i<emails.size();i++)
		{
			if(emails.get(i).equals(nextToken))
			{
				return false;
			}
		}

		//not in list
		emails.add(nextToken);
		return true;
	}



	/**
	 * This class is responsible for setting up the list of players to display in the list view
	 * @author waltzm
	 * @version 12/11/2012
	 */
	private class AdvancedOptionsAdapter extends ArrayAdapter<String>{

		/**
		 * Holds the list of players
		 */
		private ArrayList<String> addresses;

		/**
		 * Holds whether addresses are subscribed
		 */
		private ArrayList<Boolean> turnedOn;

		/**
		 * Simple constructor to hide the annoying stuff
		 * @param context the application context
		 * @param textViewResourceId the list id
		 * @param addresses list of addresses
		 */
		public AdvancedOptionsAdapter(Context context, int textViewResourceId, ArrayList<String> addresses)
		{
			super(context, textViewResourceId, addresses);
			this.addresses = addresses;
			turnedOn = new ArrayList<Boolean>();
			for(int i=0;i<addresses.size();i++)
			{
				turnedOn.add(false);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.swiss_options_email_row, parent, false);

			//add address
			TextView adr = (TextView)row.findViewById(R.id.adv_address);
			adr.setText(addresses.get(position));

			CheckBox box = (CheckBox)row.findViewById(R.id.checkBox1);
			box.setOnCheckedChangeListener(new OnCheckChangedListener_AdvancedOptions(position));
			if(turnedOn.get(position))
			{

				box.setChecked(true);
			}
			else
			{
				box.setChecked(false);
			}



			row.invalidate();
			return row;
		}

		@Override
		public void add(String item)
		{
			addresses.add(item);
			turnedOn.add(true);
		}

		/**
		 * Turns the email address at the position on or off
		 * @param position the position of the address
		 * @param state on or off
		 */
		public void setTurnedOn(int position, boolean state)
		{
			turnedOn.set(position,state);
		}

	}


	/**
	 * Custom listener to update the player based on if the check box is checked
	 * If checked the player is set to Moderator, otherwise it is set to Participant
	 * @author waltzm
	 *
	 */
	private class OnCheckChangedListener_AdvancedOptions implements OnCheckedChangeListener
	{
		/**
		 * Holds the position
		 */
		private int position;

		/**
		 * Constructor that accepts the position for the checkbox.
		 * @param position the position
		 */
		public OnCheckChangedListener_AdvancedOptions(int position)
		{
			this.position = position;
		}

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			ad.setTurnedOn(position, isChecked);
		}

	}


	/**
	 * Sets up the popup help bubbles to cycle through
	 */
	private void setupHelpPopups() 
	{
		DialogFragment warning;
		
		warning = new HelpDialog(HELP_TEXT_1 );
		warning.show(getSupportFragmentManager(), "Help Dialog");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.help:
			this.setupHelpPopups();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
