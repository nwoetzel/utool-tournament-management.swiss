package utool.plugin.swiss.roundgenerator;

import java.util.ArrayList;
import java.util.List;

import utool.plugin.swiss.Match;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.SwissTournament;

/**
 * This round generator is responible for creating a round using
 * standard Swiss System rules
 * @author Justin Kreier
 * @version 1/31/2013
 */
public class StandardRoundGenerator extends RoundGenerator{

	@Override
	public Round generateRound(List<SwissPlayer> orderedPlayers, SwissTournament tournament) {
		List<SwissPlayer> copiedPlayers = new ArrayList<SwissPlayer>(orderedPlayers);
		Round ret = new Round(tournament.getRounds().size(), tournament);
		List<Match> matches = new ArrayList<Match>();
		SwissPlayer byePlayer = null;
		if (orderedPlayers.size()%2 != 0){
			byePlayer = findByePlayer(orderedPlayers);
			copiedPlayers.remove(byePlayer);
		}

		//proceed with an even count of players
		//group players by their total score
		List<List<SwissPlayer>> groupedPlayers = new ArrayList<List<SwissPlayer>>();

		//while there are still players to sort
		while(copiedPlayers.size() > 0){
			ArrayList<SwissPlayer> group = new ArrayList<SwissPlayer>();
			double score = copiedPlayers.get(0).getScore();
			for (int i = 0; i < copiedPlayers.size(); i++){
				if (copiedPlayers.get(0).getScore() == score){
					group.add(copiedPlayers.remove(i));
					i--;
				} else {
					break;//we can stop because we know its sorted
				}
			}
			groupedPlayers.add(group);
		}

		//for each group, set up new matches
		for (int i = 0; i < groupedPlayers.size(); i++){
			List<SwissPlayer> group = groupedPlayers.get(i);

			//if the group is odd, pull from the round below it
			if (group.size()%2 != 0){
				group.add(groupedPlayers.get(i+1).remove(0));
			}
			matches.addAll(findClosestMatches(group, ret));
			
		}

		//add bye match if necessary
		if(byePlayer != null){
			Match m = new Match(byePlayer, SwissPlayer.BYE, ret);
			matches.add(m);
		}

		ret.setMatches(matches);
		return ret;
	}
	
	
	/**
	 * Finds the optimal matches of a group, meaning that the least number
	 * of players in the group have played against one another previously
	 * 
	 * This means it is the closest score algorithm
	 * 
	 * Warning: Only works for even sized groups matches
	 * 
	 * @param group The group to find the optimal matches for
	 * @param r The round we're finding matches for
	 * @return The list of optimal matches
	 */
	private List<Match> findClosestMatches(List<SwissPlayer> group, Round r){
		ArrayList<Match> matches = new ArrayList<Match>();

		//create auxiliary list
		ArrayList<SwissPlayer> aux = new ArrayList<SwissPlayer>();

		for (int i = group.size()/2; i < group.size(); i++){
			aux.add(group.get(i));
		}
		for (int i = 0; i < group.size()/2; i++){
			aux.add(group.get(i));
		}

		//cross check the list with the number of times played
		int count = 0;
		while (group.size() > 0){
			int size = group.size();
			for (int j = 0; j < size; j++){
				SwissPlayer p = group.get(0);
				for (int i = 0; i < aux.size(); i++)	{
					SwissPlayer o = aux.get(i);
					if (!p.equals(o) && p.countPlayedAgainstPlayer(o) <= count){
						Match m = new Match(p, o, r);
						matches.add(m);

						//remove matched players from both lists
						group.remove(p);
						group.remove(o);
						aux.remove(p);
						aux.remove(o);
						size = size-2;
						j--;
						break;
					}
				}
			}
			//if we got here with the group size not being zero, then everyone
			//has played against each other at least once. Now we increase it
			//to find people who have played against each other the least.
			count++;
		}

		return matches;
	}

}
