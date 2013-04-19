package utool.plugin.swiss.tieresolver;

import java.util.List;

import utool.plugin.swiss.SwissPlayer;

/**
 * Calculates a tie break based on the players' opponent score
 * @author Justin Kreier
 * @version 1/26/2013
 */
public class OpponentScoreTieResolver implements TieResolver{

	@Override
	public SwissPlayer resolveTie(SwissPlayer p1, SwissPlayer p2) {
		double p1Total = calculateOpponentScore(p1);
		double p2Total = calculateOpponentScore(p2);
		
		if (p1Total > p2Total){
			return p1;
		} else if (p2Total > p1Total){
			return p2;
		}
		
		return null;
	}

	
	/**
	 * The opponent score is found by calculating the total number of points 
	 * earned by the player's opponents.
	 * @param p The player to calculate the opponent score for
	 * @return The opponent score
	 */
	protected double calculateOpponentScore(SwissPlayer p){
		List<SwissPlayer> opponents = p.getOpponents();
		
		double total = 0.0;
		for (SwissPlayer o : opponents){
			total += o.calculateScore();
		}
		
		return total;
	}
}
