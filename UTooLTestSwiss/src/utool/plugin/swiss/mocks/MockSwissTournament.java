package utool.plugin.swiss.mocks;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;


import utool.plugin.Player;
import utool.plugin.observer.Observable;
import utool.plugin.observer.Observer;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissTournament;

/**
 * A testable swiss tournament with access to protected methods
 * @author Justin Kreier
 * @version 1/25/2013
 */
public class MockSwissTournament extends SwissTournament{

	/**
	 * Required constructor
	 * @param tournamentId The tournament id
	 * @param playerList The player list
	 * @param tournamentName The tournament name
	 * @param profileId The profile id
	 * @param c The application context
	 */
	public MockSwissTournament(long tournamentId, ArrayList<Player> playerList, String tournamentName, UUID profileId, Context c) {
		super(tournamentId, playerList, tournamentName, profileId, new Observer(){

			@Override
			public void updateObserver(Object observedObject) {
				//do nothing
				
			}
			
		}, c);
	}
	
	@Override
	public Round generateNextRound(){
		return super.generateNextRound();
	}
	
	/**
	 * Public access to the observable object
	 * @return The observable object
	 */
	public Observable<SwissTournament> getObservable(){
		return super.observable;
	}

	/**
	 * Method to add a round to rounds without calling generate
	 * @param r The round to add
	 */
	public void addRound(Round r){
		super.rounds.add(r);
	}
}
