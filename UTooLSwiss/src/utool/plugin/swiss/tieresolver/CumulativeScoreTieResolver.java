package utool.plugin.swiss.tieresolver;

import java.util.ArrayList;
import java.util.List;

import utool.plugin.swiss.Match;
import utool.plugin.swiss.SwissPlayer;

/**
 * Calculates a tiebreak based on cumulative score
 * @author Justin Kreier
 * @version 1/26/2013
 */
public class CumulativeScoreTieResolver implements TieResolver{

	@Override
	public SwissPlayer resolveTie(SwissPlayer p1, SwissPlayer p2) {

		//resolve by cumulative
		double p1Cumulative = calculateCumulativeScore(p1);
		double p2Cumulative = calculateCumulativeScore(p2);
		if (p1Cumulative > p2Cumulative){
			return p1;
		} else if (p2Cumulative > p1Cumulative){
			return p2;
		}

		return null;
	}

	/**
	 * The cumulative score is calculated by summing the running score for 
	 * each round.
	 * @param p The player to calculate the score for
	 * @return The player's score
	 */
	protected double calculateCumulativeScore(SwissPlayer p){
		ArrayList<Double> scores = new ArrayList<Double>();
		double cumulative = 0;
		List<Match> matches = p.getMatchesPlayed();
		for (Match m : matches){
			double score = m.getScore(p);
			cumulative += score;
			scores.add(cumulative);
		}

		double totalScore = 0;
		for (Double d : scores){
			totalScore += d;
		}

		return totalScore;
	}

}
