package utool.plugin.swiss.roundgenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import utool.plugin.swiss.Match;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.SwissTournament;

/**
 * This class is responsible for generating a round such that no two
 * players will play against each other more times than any other set of players
 * @author Justin Kreier
 * @version 1/31/2013
 */
public class LeastPlayedRoundGenerator extends RoundGenerator{

	@Override
	public Round generateRound(List<SwissPlayer> orderedPlayers, SwissTournament tournament) {
		Round ret = new Round(tournament.getRounds().size(), tournament);
		ArrayList<Match> matches = new ArrayList<Match>();

		int count = 0;
		ArrayList<SwissPlayer> copiedPlayers = new ArrayList<SwissPlayer>(orderedPlayers);

		while (copiedPlayers.size() > 0){

			ArrayList<PlayerStore> playerStores = new ArrayList<PlayerStore>();
			//for each player, create a list of players who have played against the player the least
			for (int i = 0; i < copiedPlayers.size(); i++){
				SwissPlayer p = copiedPlayers.get(i);
				ArrayList<SwissPlayer> potential = new ArrayList<SwissPlayer>();
				for (int j = 0; j < copiedPlayers.size(); j++){
					SwissPlayer o = copiedPlayers.get(j);

					if (!p.equals(o) && p.countPlayedAgainstPlayer(o) <= count){
						potential.add(o);
					}
				}

				//store the list with that player
				if (potential.size() > 0){
					playerStores.add(new PlayerStore(p, potential));
				}
			}

			//for each player in playerStore
			while (playerStores.size() > 0){
				PlayerStore store = playerStores.remove(0);
				SwissPlayer p = store.p;

				while(store.potentialMatches.size() > 0){
					//get the top player
					SwissPlayer potential = store.potentialMatches.remove(0);

					boolean safe = true;
					//check if that player exists in any other list
					for (int j = 0; j < playerStores.size(); j++){
							//if another list has it, do additional checking
							if (playerStores.get(j).potentialMatches.indexOf(potential) != -1){
								if (playerStores.get(j).potentialMatches.size() == 1){
									//if the size is 1, removing it would be bad
									//we will instead just break away and do nothing with this one
									safe = false;
									break;
								}
								
								if (playerStores.get(j).potentialMatches.size() == 2){
									if (playerStores.get(j).potentialMatches.indexOf(p) != -1){
										//if size is 2, and the other one was p, removing both would be bad
										//instead just break away and do nothing
										safe = false;
										break;
									}
								}
							}
					}

					if (safe){
						//remove the players from each list
						for (int j = 0; j < playerStores.size(); j++){
							playerStores.get(j).potentialMatches.remove(potential);
							playerStores.get(j).potentialMatches.remove(p);
						}

						if (p.countPlayedAgainstPlayer(potential) != 0){
							System.out.println("Yoyoyoyoyo");
						}
						
						//create the match for this pairing
						Match m = new Match(p, potential, ret);
						matches.add(m);
						
						//remove the players from copied players
						copiedPlayers.remove(p);
						copiedPlayers.remove(potential);
						
						//remove the player store holding o
						for (int j = 0; j < playerStores.size(); j++){
							if (playerStores.get(j).p.equals(potential)){
								playerStores.remove(j);
							}
						}

						//break away from the for while to go to the next player
						break;
					}
				}
			}

			count++;
		}
		
		ret.setMatches(matches);
		return ret;
	}

	/**
	 * Storage class to link potential matches with a player
	 * @author Justin Kreier
	 * @version 2/2/2013
	 */
	private class PlayerStore{

		/**
		 * The player being stored
		 */
		final SwissPlayer p;
		
		/**
		 * The potential matches being stored
		 */
		List<SwissPlayer> potentialMatches;

		/**
		 * Constructor, sorts the potential matches afterwards
		 * @param p The player
		 * @param potentialMatches The potential matches
		 */
		public PlayerStore(final SwissPlayer p, List<SwissPlayer> potentialMatches){
			this.p = p;

			//sort the matches
			Collections.sort(potentialMatches, new Comparator<SwissPlayer>(){

				@Override
				public int compare(SwissPlayer lhs, SwissPlayer rhs) {
					double pscore = p.getScore();
					double lhscore = lhs.getScore();
					double rhscore = rhs.getScore();

					double lhdiff = Math.abs(pscore-lhscore);
					double rhdiff = Math.abs(pscore-rhscore);

					if (lhdiff < rhdiff){
						return -1;
					} else if (lhdiff > rhdiff){
						return 1;
					} else {
						return 0;
					}
				}

			});
			
			List<SwissPlayer> realSorted = new ArrayList<SwissPlayer>();
			
			List<List<SwissPlayer>> groups = groupPlayers(potentialMatches);
			for (int i = 0; i < groups.size(); i++){
				List<SwissPlayer> group = groups.get(i);
				for (int j = group.size()/2; j < group.size(); j++){
					realSorted.add(group.get(j));
				}
				
				for (int j = 0; j < group.size()/2; j++){
					realSorted.add(group.get(j));
				}
			}
			
			this.potentialMatches = realSorted;
		}
		
		/**
		 * Groups the players by their scores
		 * @param potentialMatches The list of potential matches
		 * @return A list of grouped players
		 */
		private List<List<SwissPlayer>> groupPlayers(List<SwissPlayer> potentialMatches){
			//proceed with an even count of players
			//group players by their total score
			List<List<SwissPlayer>> groupedPlayers = new ArrayList<List<SwissPlayer>>();

			//while there are still players to sort
			while(potentialMatches.size() > 0){
				ArrayList<SwissPlayer> group = new ArrayList<SwissPlayer>();
				double score = potentialMatches.get(0).getScore();
				for (int i = 0; i < potentialMatches.size(); i++){
					if (potentialMatches.get(0).getScore() == score){
						group.add(potentialMatches.remove(i));
						i--;
					} else {
						break;//we can stop because we know its sorted
					}
				}
				groupedPlayers.add(group);
			}
			
			return groupedPlayers;
		}
		
		
	}

}
