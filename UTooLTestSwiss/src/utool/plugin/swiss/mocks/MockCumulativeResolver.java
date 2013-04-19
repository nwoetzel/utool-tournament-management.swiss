package utool.plugin.swiss.mocks;

import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.tieresolver.CumulativeScoreTieResolver;

/**
 * Mock cumulative tie resolver with public access to protected methods
 * @author Justin Kreier
 * @version 2/3/2013
 */
public class MockCumulativeResolver extends CumulativeScoreTieResolver{

	@Override
	public double calculateCumulativeScore(SwissPlayer p){
		return super.calculateCumulativeScore(p);
	}
	
}
