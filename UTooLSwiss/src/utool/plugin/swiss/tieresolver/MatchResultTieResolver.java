package utool.plugin.swiss.tieresolver;

import java.util.List;

import utool.plugin.swiss.Match;
import utool.plugin.swiss.MatchResult;
import utool.plugin.swiss.SwissPlayer;

/**
 * This tie resolver determines a winner by checking if the two players have played
 * against one another. The winner of their match wins the tie, otherwise Null is returned
 * @author Justin Kreier
 * @version 1/26/2013
 */
public class MatchResultTieResolver implements TieResolver{

	@Override
	public SwissPlayer resolveTie(SwissPlayer p1, SwissPlayer p2) {
		List<Match> matches = p1.getMatchesPlayed();
		
		int playerOneWins = 0;
		int playerTwoWins = 0;
		
		for (Match m : matches){
			if (m.getPlayerOne().equals(p1) && m.getPlayerTwo().equals(p2)){
				if (m.getMatchResult().equals(MatchResult.PLAYER_ONE)){
					playerOneWins++;
				} else if (m.getMatchResult().equals(MatchResult.PLAYER_TWO)){
					playerTwoWins++;
				}
			}
			else if (m.getPlayerTwo().equals(p1) && m.getPlayerOne().equals(p2)){
				if (m.getMatchResult().equals(MatchResult.PLAYER_ONE)){
					playerTwoWins++;
				} else if (m.getMatchResult().equals(MatchResult.PLAYER_TWO)){
					playerOneWins++;
				}
			}
				
		}
		
		if (playerOneWins > playerTwoWins){
			return p1;
		} else if (playerTwoWins > playerOneWins){
			return p2;
		}
		
		return null;
	}

}
