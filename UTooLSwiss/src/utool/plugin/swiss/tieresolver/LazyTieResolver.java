package utool.plugin.swiss.tieresolver;

import utool.plugin.swiss.SwissPlayer;

/**
 * This tie resolver simply returns the second parameter
 * @author Justin Kreier
 * @version 1/26/2013
 */
public class LazyTieResolver implements TieResolver{

	@Override
	public SwissPlayer resolveTie(SwissPlayer p1, SwissPlayer p2) {
		return p2;
	}

}
