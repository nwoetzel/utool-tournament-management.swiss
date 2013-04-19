package utool.plugin.swiss.mocks;

import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.tieresolver.OpponentScoreTieResolver;

/**
 * Mock opponent score resolver with public access to protected methods
 * @author Justin Kreier
 * @version 2/3/2013
 */
public class MockOpponentScoreResolver extends OpponentScoreTieResolver{
	
	
	@Override
	public double calculateOpponentScore(SwissPlayer p){
		return super.calculateOpponentScore(p);
	}

}
