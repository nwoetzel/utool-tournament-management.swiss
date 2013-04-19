package utool.plugin.swiss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import utool.plugin.Player;
import utool.plugin.activity.AbstractTournament;
import utool.plugin.observer.Observable;
import utool.plugin.observer.Observer;
import utool.plugin.swiss.communication.AutomaticEmailHandler;
import utool.plugin.swiss.communication.IncomingCommandHandler;

/**
 * This class is responsible for maintaining the state of an entire Swiss System Tournament.
 * @author Justin Kreier
 * @version 1/21/2013
 */
public class SwissTournament extends AbstractTournament{

	/**
	 * The list of rounds
	 */
	protected List<Round> rounds;

	/**
	 * Observable implementation for SwissTournament
	 */
	protected Observable<SwissTournament> observable;

	/**
	 * Holds a reference to the tournament's email handler
	 */
	protected volatile AutomaticEmailHandler aeh;

	/**
	 * Holds the swiss configuration object
	 */
	protected SwissConfiguration conf;

	/**
	 * holds the logger tag
	 */
	private static final String LOG_TAG = "SwissTournaemnt";

	/**
	 * Instantiates a new SwissTournament
	 * @param tournamentId The tournament id
	 * @param playerList The list of players
	 * @param tournamentName The tournament name
	 * @param profileId The profile id
	 * @param o The object observing this tournament
	 * @param c The application context
	 */
	public SwissTournament(long tournamentId, ArrayList<Player> playerList, String tournamentName, UUID profileId, Observer o, Context c) {
		super(tournamentId, playerListToSwissPlayers(playerList), 
				(String) nullChecker(tournamentName, 
						new NullPointerException("Tournament name cannot be null")),
						(UUID)nullChecker(profileId,
								new NullPointerException("Profile id cannot be null")));


		rounds = new ArrayList<Round>();

		observable = new Observable<SwissTournament>(this);
		observable.registerObserver(o);

		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
		conf = new SwissConfiguration(getPlayers(),p);
	}

	/**
	 * Checks if an object is null, and throws the exception if it is
	 * @param objectToCheck The object to check
	 * @param e The exception to throw
	 * @return The object if it is not null
	 */
	private static Object nullChecker(Object objectToCheck, RuntimeException e){
		if(objectToCheck == null){
			throw e;
		} else {
			return objectToCheck;
		}
	}

	/**
	 * Converts the player list into a list of Swiss players (performing necessary initialization 
	 * and preventing type cast exceptions later on)
	 * @param playerList The player list to convert
	 * @return The list of SwissPlayers
	 */
	public static ArrayList<Player> playerListToSwissPlayers(ArrayList<Player> playerList){
		ArrayList<Player> ret = new ArrayList<Player>();
		for (Player p : playerList){
			ret.add(new SwissPlayer(p));
		}
		return ret;
	}


	/**
	 * Generates a new round based on current round information
	 * @return The newly generated round
	 */
	public Round generateNextRound(){
		Round ret;
		if (rounds.size() == 0){
			//randomize the list of players
			ArrayList<Player> tempPlayers = new ArrayList<Player>(this.getPlayers());
			ArrayList<SwissPlayer> randomizedPlayers = new ArrayList<SwissPlayer>();

			Random r = new Random();
			while (tempPlayers.size() > 0){
				randomizedPlayers.add((SwissPlayer)tempPlayers.remove(r.nextInt(tempPlayers.size())));
			}

			//round size is zero, so we're generating the first round
			ret = generateRound(randomizedPlayers);
		} else{
			//generating any other round
			List<SwissPlayer> orderedPlayers = getStandingsArray();
			ret = generateRound(orderedPlayers);
		}

		rounds.add(ret);
		observable.notifyChanged();

		//update email handler
		this.getAutomaticEmailHandler().sendOutNotifications();
		
		return ret;
	}

	/**
	 * Generates a round using the appropriate round generator from a list of players whose
	 * scores have been calculated
	 * @param orderedPlayers The list of players to generate a round for
	 * @return the new round
	 */
	public Round generateRound(List<SwissPlayer> orderedPlayers)
	{
		Round r = conf.getRoundGenerator().generateRound(orderedPlayers, this);
		return r;
	}

	/**
	 * Returns the list of all rounds in the tournament
	 * @return The list of all rounds in the tournament
	 */
	public List<Round> getRounds(){
		return rounds;
	}

	/**
	 * Gets the player list as a list of swiss players
	 * @return The player list as a list of swiss players
	 */
	public List<SwissPlayer> getSwissPlayers(){
		List<Player> p = this.getPlayers();
		List<SwissPlayer> player = new ArrayList<SwissPlayer>();
		for (int i = 0; i < p.size(); i++){
			player.add((SwissPlayer)p.get(i));
		}
		return player;
	}

	/**
	 * Returns the list of players ordered by their calculated score
	 * @return The list of players ordered by their score 
	 */
	public List<SwissPlayer> getStandingsArray(){
		ArrayList<SwissPlayer> players = new ArrayList<SwissPlayer>();

		for (int i = 0; i < getPlayers().size(); i++){
			SwissPlayer player = (SwissPlayer)getPlayers().get(i);
			player.calculateScore();
			players.add(player);
		}

		//sort the players
		Collections.sort(players, new Comparator<SwissPlayer>(){

			public int compare(SwissPlayer p1, SwissPlayer p2) {
				if (p1.getScore() > p2.getScore()){
					return -1;
				} else if (p1.getScore() < p2.getScore()){
					return 1;
				} else {

					SwissPlayer tieWinner = SwissPlayer.resolveTie(p1, p2, conf.getTieResolver());

					if (tieWinner == null){
						return 0;
					} else if (tieWinner.equals(p1)){
						return -1;
					} else {
						return 1;
					}
				}
			}

		});

		SwissPlayer lastPlayer = null;
		int rank = 0;
		for (int i = 0; i < players.size(); i++){
			SwissPlayer player = players.get(i);

			
			//if last player > player, rank = i+1
			if (lastPlayer != null && lastPlayer.getScore() > player.getScore()){
				rank = i+1;
				player.setRank(rank);
			}
			//if there is a last player, and when we compare the two players they are considered tied, then set their rank to be the same
			else if (lastPlayer != null && SwissPlayer.resolveTie(player, lastPlayer, conf.getTieResolver()) == null){
				player.setRank(rank);
			} else {
				rank = i+1;
				player.setRank(rank);
			}

			lastPlayer = player;
		}

		return players;
	}

	/**
	 * Notifies the observable that it has been changed
	 */
	public void notifyChanged(){
		observable.notifyChanged();
	}

	/**
	 * Get the tournament's email handler 
	 * @return Instance of an email handler
	 */
	public AutomaticEmailHandler getAutomaticEmailHandler(){
		if (aeh == null){
			synchronized (this) {
				if (aeh == null){
					aeh = new AutomaticEmailHandler(tournamentId);
				}
			}
		}
		return aeh;
	}

	/**
	 * Get the tournament's SwissConfiguration 
	 * @return Instance of an SwissConfiguration
	 */
	public SwissConfiguration getSwissConfiguration(){
		return conf;
	}

	/**
	 * Clears a tournament's information
	 */
	public void clearTournament(){
		rounds.clear();
		List<SwissPlayer> swiss = this.getSwissPlayers();
		for (Player p : swiss){
			SwissPlayer s = (SwissPlayer)p;
			s.getMatchesPlayed().clear();
		}
		notifyChanged();
	}

	/**
	 * Switches s1 with s2 in the given round
	 * @param p1Match the match id of s1
	 * @param isP1First if S1 is first player in match
	 * @param p2Match the match id of s2
	 * @param isP2First if s2 is the first player in match
	 * @param round the round of the tournament (0 is round 1)
	 */
	public void switchPlayers(int p1Match, boolean isP1First, int p2Match, boolean isP2First, int round) 
	{
		List<Round> rounds = getRounds();
		if(rounds.size()>0)
		{
			List<Match> matches = rounds.get(round).getMatches();

			//Find p1 and p2
			SwissPlayer p1=null;
			SwissPlayer p2=null;

			if(isP1First)
			{
				p1 = matches.get(p1Match).getPlayerOne();
			}
			else
			{
				p1 = matches.get(p1Match).getPlayerTwo();
			}

			if(isP2First)
			{
				p2 = matches.get(p2Match).getPlayerOne();
			}
			else
			{
				p2 = matches.get(p2Match).getPlayerTwo();
			}

			//remove unplayed match from p1 and p2
			Log.d(LOG_TAG,"P1 Matches prior: "+p1.getMatchesPlayed().toString());
			Log.d(LOG_TAG,"P2 Matches prior: "+p2.getMatchesPlayed().toString());
			if(p1.getMatchesPlayed().size()>0)
			{
				p1.getMatchesPlayed().remove(p1.getMatchesPlayed().size()-1);
			}
			if(p2.getMatchesPlayed().size()>0)
			{
				p2.getMatchesPlayed().remove(p2.getMatchesPlayed().size()-1);
			}

			Log.d(LOG_TAG,"P1 Matches after: "+p1.getMatchesPlayed().toString());
			Log.d(LOG_TAG,"P2 Matches after: "+p2.getMatchesPlayed().toString());
			//switch p1 and p2
			//get p1 opponent
			SwissPlayer temp;
			if(isP1First)
			{
				//remove if both are BYE's
				temp = matches.get(p1Match).getPlayerTwo();
				if(p2.equals(SwissPlayer.BYE)&&temp.equals(SwissPlayer.BYE))
				{
					matches.remove(p1Match);
				}
				else
				{
					matches.set(p1Match, new Match(p2, temp, this.rounds.get(round)));
				}
			}
			else
			{
				//remove if both are BYE's
				temp = matches.get(p1Match).getPlayerOne();
				if(p2.equals(SwissPlayer.BYE)&&temp.equals(SwissPlayer.BYE))
				{
					matches.remove(p1Match);
				}
				else
				{		
					matches.set(p1Match, new Match(temp, p2, this.rounds.get(round)));
				}
			}
			//get p2 opponent
			SwissPlayer temp2;
			if(isP2First)
			{
				temp2 = matches.get(p2Match).getPlayerTwo();
				//remove if both are byes
				if(p1.equals(SwissPlayer.BYE)&&temp2.equals(SwissPlayer.BYE))
				{
					matches.remove(p2Match);
				}
				else
				{
					matches.set(p2Match, new Match(p1, temp2, this.rounds.get(round)));
				}
			}
			else
			{
				temp2 = matches.get(p2Match).getPlayerOne();
				//remove if both are byes
				if(p1.equals(SwissPlayer.BYE)&&temp2.equals(SwissPlayer.BYE))
				{
					matches.remove(p2Match);
				}
				else
				{
					matches.set(p2Match, new Match(temp2, p1, this.rounds.get(round)));
				}
			}

			//notify of change
			this.notifyChanged();		
			//notify connected players of the change
			IncomingCommandHandler inc = new IncomingCommandHandler(this);
			inc.handleReceiveError(TournamentActivity.RESEND_ERROR_CODE, "", "", "");
		}	
	}
}
