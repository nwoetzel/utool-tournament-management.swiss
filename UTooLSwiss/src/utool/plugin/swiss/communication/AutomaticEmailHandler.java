package utool.plugin.swiss.communication;

import java.util.ArrayList;
import java.util.List;

import utool.plugin.activity.AbstractTournament;
import utool.plugin.activity.TournamentContainer;
import utool.plugin.email.GMailSender;
import utool.plugin.swiss.SwissOptionsTabActivity;
import utool.plugin.swiss.SwissTournament;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Manager for the automatic Email Handling
 * Sends emails from default email of outgoingEmailAddress
 * @author waltzm
 * @version 1/14/2013
 */
public class AutomaticEmailHandler 
{
	/**
	 * Outgoing email address to use to send the messages
	 */
	private static final String OUTGOING_EMAIL_ADDRESS = "msoetablet@gmail.com";
	
	/**
	 * Outgoing email password to use
	 */
	private static final String OUTGOING_EMAIL_PASSWORD = "msoetablet";
	
	/**
	 * Holds the list of active subscribers to the tournament
	 */
	private ArrayList<String> subscribers;

	/**
	 * Holds the list of non-active subscribers in the tournament
	 */
	private ArrayList<String> possible_subscribers;

	/**
	 * Tournament id
	 */
	private long tid;
	
	/**
	 * Log tag to be used in this class
	 */
	private static final String LOG_TAG = "AutomaticEmailHandler";

	/**
	 * Constructor for AutomaticEmailHandler. Links it to a tournamemt
	 * @param tournamentId id of the tournament
	 */
	public AutomaticEmailHandler(long tournamentId)
	{
		//initialize variables
		this.tid = tournamentId;
		subscribers = new ArrayList<String>();
		possible_subscribers = new ArrayList<String>();
	}

	/**
	 * Updates the subscriber of the tournament state
	 * @param address of the subscriber to update
	 */
	public void updateSubscriber(final String address)
	{
		//send notification to subscriber of setup
		Log.d(LOG_TAG,"Updateing "+address);
		new RetreiveFeedTask().execute(address);
	}

	/**
	 * Getter for list of subscribers
	 * @return cloned list of subscribers
	 */
	public ArrayList<String> getSubscribers()
	{
		//return clone
		ArrayList<String> subscriberst = new ArrayList<String>();
		for(int i=0;i<subscribers.size();i++)
		{
			subscriberst.add(subscribers.get(i));
		}
		return subscriberst;
	}

	/**
	 * Getter for the list of possible subscribers
	 * @return cloned list of subscribers
	 */
	public ArrayList<String> getPossibleSubscribers()
	{
		//return clone
		ArrayList<String> possible_subscriberst = new ArrayList<String>();
		for(int i=0;i<possible_subscribers.size();i++)
		{
			possible_subscriberst.add(possible_subscribers.get(i));
		}
		return possible_subscriberst;
	}

	/**
	 * Setter for subscribers. If a new subscriber is in the list an initial
	 * email will be sent to them with the current state of the tournament
	 * Note: passed in list is cloned before getting set
	 * @param subs list of subscribers to add
	 */
	public void setSubscribers(List<String> subs)
	{
		Log.d(LOG_TAG,"subs: "+subs.toString());
		Log.d(LOG_TAG,"subscr: "+subscribers.toString());

		//go through calling update on new additions
		for(int i=0;i<subs.size();i++)
		{
			boolean isIn = false;
			for(int j=0;j<subscribers.size();j++)
			{
				if(subs.get(i).equals(subscribers.get(j)))
				{
					isIn = true;
					break;
				}
			}
			if(!isIn)
			{
				this.updateSubscriber(subs.get(i));
			}
		}

		//reset to passed in
		subscribers = new ArrayList<String>();
		for(int i=0;i<subs.size();i++)
		{
			subscribers.add(subs.get(i));
		}

	}
	
	/**
	 * Setter for the possible subscribers
	 * @param subs the possible subscribers to add
	 */
	public void setPossibleSubscribers(List<String> subs)
	{
		possible_subscribers = new ArrayList<String>();
		for(int i=0;i<subs.size();i++)
		{
			possible_subscribers.add(subs.get(i));
		}
	}

	/**
	 * Sends out an email to all subscribers with current tournament state. This
	 * should be called every new round, and every time something matchup related
	 * is changed.
	 */
	public void sendOutNotifications()
	{

		//send new email to each subscriber
		for(int i=0;i<this.subscribers.size();i++)
		{
			//create  duplicate final to call task on
			final String clone = subscribers.get(i);
			
			new RetreiveFeedTask().execute(clone);
		}
	}
	
	/**
	 * AsyncTask for actually sending emails.
	 * Sends from outgoingEmailAddress
	 * @author waltzm
	 * @version 1/14/2013
	 */
	class RetreiveFeedTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {

			try {   
				GMailSender sender = new GMailSender(OUTGOING_EMAIL_ADDRESS, OUTGOING_EMAIL_PASSWORD);
				AbstractTournament t = TournamentContainer.getInstance(tid);
				String subject = t.getTournamentName()+": Tournament Matches up to Round "+(((SwissTournament)t).getRounds().size());
				String body = SwissOptionsTabActivity.getTournamentData((SwissTournament) TournamentContainer.getInstance(tid));

				sender.sendMail(subject, body, OUTGOING_EMAIL_ADDRESS, urls[0]);   
				Log.d(LOG_TAG, "sent");
			} catch (Exception e) {   
				Log.e(LOG_TAG, "Error:"+e.getMessage());
			} 
			return null;
		}

		protected void onPostExecute(String feed) {
		}
	}

}
