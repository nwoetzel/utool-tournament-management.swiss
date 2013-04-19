package utool.plugin.swiss.roundgenerator;

import java.util.ArrayList;
import java.util.List;

import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.SwissTournament;

/**
 * This interface defines a round generator for a Swiss System tournament
 * @author Justin Kreier
 * @version 1/31/2013
 */
public abstract class RoundGenerator {

	/**
	 * Generates a round from the list of swiss players
	 * @param orderedPlayers The list of swiss players
	 * @param tournament Reference to the tournament the round is being generated for
	 * @return The next round
	 */
	public abstract Round generateRound(List<SwissPlayer> orderedPlayers, SwissTournament tournament);
	
	
	/**
	 * Determines which player should receive the bye from a set of players
	 * @param orderedPlayers The list of ordered players
	 * @return The player to receive the bye
	 */
	protected SwissPlayer findByePlayer(List<SwissPlayer> orderedPlayers){
		SwissPlayer ret = null;

		//find players that have received least number of byes
		int leastByes = getLeastByes(orderedPlayers);
		ArrayList<SwissPlayer> byeCandidates = new ArrayList<SwissPlayer>();
		for (int i = 0; i < orderedPlayers.size(); i++){
			if (orderedPlayers.get(i).countByes() == leastByes){
				byeCandidates.add(orderedPlayers.get(i));
			}
		}

		//find the player among bye candidates with the lowest score
		double lowestScore = getLowestScore(byeCandidates);
		for (int i = byeCandidates.size()-1; i >= 0; i--){
			if (byeCandidates.get(i).getScore() == lowestScore){
				ret = byeCandidates.get(i);
				break;
			}
		}

		return ret;
	}
	
	
	/**
	 * Gets the least number of byes received by players
	 * @param players The list of players
	 * @return The least number of byes received
	 */
	protected int getLeastByes(List<SwissPlayer> players){
		int leastByes = Integer.MAX_VALUE;
		for (int i = 0; i < players.size(); i++){
			if (players.get(i).countByes() < leastByes){
				leastByes = players.get(i).countByes();
			}
		}
		return leastByes;
	}
	
	
	/**
	 * Gets the lowest score from a list of players
	 * @param players The list of players
	 * @return The lowest score
	 */
	protected double getLowestScore(List<SwissPlayer> players){
		double lowestScore = Double.MAX_VALUE;
		for (int i = 0; i < players.size(); i++){
			if (players.get(i).getScore() < lowestScore){
				lowestScore = players.get(i).getScore();
			}
		}
		return lowestScore;
	}
}
